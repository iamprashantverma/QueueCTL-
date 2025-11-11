package com.prashant.queuectl.service;

import com.prashant.queuectl.config.AppConfig;
import com.prashant.queuectl.entity.Job;
import com.prashant.queuectl.entity.enums.State;
import com.prashant.queuectl.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobWorker {

    private static final long POLLING_INTERVAL_MS = 1000;
    
    private final JobRepository jobRepository;
    private final AppConfig appConfig;
    private final Set<String> activeWorkers = ConcurrentHashMap.newKeySet();
    
    private volatile boolean running = true;

    @Async("jobExecutor")
    public void processJobs() {
        String workerName = Thread.currentThread().getName();

        if (!activeWorkers.add(workerName)) {
            log.warn("Worker {} already running, skipping start", workerName);
            return;
        }

        log.info("Worker started: {}", workerName);

        try {
            while (running) {
                try {
                    processPendingJobs();
                    Thread.sleep(POLLING_INTERVAL_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    running = false;
                    log.info("Worker {} interrupted, shutting down", workerName);
                } catch (Exception e) {
                    log.error("Unexpected error in worker {}", workerName, e);
                }
            }
        } finally {
            activeWorkers.remove(workerName);
            log.info("Worker {} stopped", workerName);
        }
    }

    @Transactional
    public void processPendingJobs() {
        List<Job> pendingJobs = jobRepository.findPendingJobsForUpdate(State.PENDING);

        for (Job job : pendingJobs) {
            if (shouldSkipJob(job)) {
                continue;
            }

            try {
                executeJob(job);
            } catch (Exception e) {
                log.error("Failed to execute job {}", job.getId(), e);
                handleFailure(job);
            }
        }
    }

    private boolean shouldSkipJob(Job job) {
        return job.getNextAttemptTime() != null && 
               job.getNextAttemptTime().isAfter(LocalDateTime.now());
    }

    @Transactional
    private void executeJob(Job job) {
        updateJobState(job, State.PROCESSING);
        log.info("Executing job {}: {}", job.getId(), job.getCommand());

        try {
            int exitCode = runCommand(job.getCommand());
            
            if (exitCode == 0) {
                updateJobState(job, State.COMPLETED);
                log.info("Job {} completed successfully", job.getId());
            } else {
                log.warn("Job {} failed with exit code {}", job.getId(), exitCode);
                handleFailure(job);
            }
        } catch (Exception e) {
            log.error("Job {} execution failed", job.getId(), e);
            handleFailure(job);
        }
    }

    private int runCommand(String command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command.split(" "));
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(line -> log.debug("Command output: {}", line));
        }

        return process.waitFor();
    }

    @Transactional
    private void handleFailure(Job job) {
        job.setAttempts(job.getAttempts() + 1);

        if (job.getAttempts() >= job.getMaxRetries()) {
            updateJobState(job, State.DEAD);
            log.warn("Job {} moved to Dead Letter Queue after {} attempts", job.getId(), job.getAttempts());
        } else {
            scheduleRetry(job);
        }
    }

    private void scheduleRetry(Job job) {
        long delay = calculateBackoffDelay(job.getAttempts());
        job.setNextAttemptTime(LocalDateTime.now().plusSeconds(delay));
        updateJobState(job, State.PENDING);
        log.info("Job {} scheduled for retry in {} seconds", job.getId(), delay);
    }

    private long calculateBackoffDelay(int attempts) {
        return (long) Math.pow(appConfig.getBackoffSeconds(), attempts);
    }

    private void updateJobState(Job job, State state) {
        job.setState(state);
        jobRepository.save(job);
    }

    public void shutdown() {
        running = false;
        log.info("Initiating JobWorker shutdown");
    }

    public boolean allWorkersStopped() {
        return activeWorkers.isEmpty();
    }

    public int getActiveWorkerCount() {
        return activeWorkers.size();
    }
}
