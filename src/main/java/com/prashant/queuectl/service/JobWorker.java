package com.prashant.queuectl.service;

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
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobWorker {

    private final JobRepository jobRepository;
    private final JobService jobService;

    private volatile boolean running = true;

    @Async("jobExecutor")
    public void processJobs() {
        log.info("Worker started: {}", Thread.currentThread().getName());

        while (running) {
            try {
                processPendingJobs();
            } catch (Exception e) {
                log.error("Unexpected exception in worker thread", e);
            }

            try {
                Thread.sleep(1000); // polling interval between job batches
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
                log.info("Worker interrupted, shutting down...");
            }
        }
    }

    @Transactional
    public void processPendingJobs() {
        List<Job> pendingJobs = jobRepository.findPendingJobsForUpdate(State.PENDING);

        for (Job job : pendingJobs) {
            try {
                executeJob(job);
            } catch (Exception e) {
                log.error("Failed to execute job {}: ", job.getId(), e);
            }
        }
    }

    private void executeJob(Job job) {
        job.setState(State.PROCESSING);
        jobService.saveJob(job);

        log.info("Executing job {}: {}", job.getId(), job.getCommand());

        try {
            ProcessBuilder pb = new ProcessBuilder(job.getCommand().split(" "));
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                reader.lines().forEach(log::info);
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                job.setState(State.COMPLETED);
                log.info("Job {} completed successfully", job.getId());
            } else {
                log.warn("Job {} failed with exit code {}", job.getId(), exitCode);
                handleFailure(job);
            }

        } catch (Exception e) {
            log.error("Job {} execution failed", job.getId(), e);
            handleFailure(job);
        } finally {
            jobService.saveJob(job);
        }
    }


    private void handleFailure(Job job) {
        job.setAttempts(job.getAttempts() + 1);

        if (job.getAttempts() > job.getMaxRetries()) {
            job.setState(State.DEAD);
            log.warn("Job {} moved to Dead Letter Queue", job.getId());
        } else {
            job.setState(State.FAILED);
            long delay = (long) Math.pow(2, job.getAttempts());
            log.info("Job {} will retry after {} seconds", job.getId(), delay);

            try {
                Thread.sleep(delay * 1000);
                job.setState(State.PENDING);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
                log.info("Worker interrupted during retry sleep...");
            }
        }
    }

    public void shutdown() {
        running = false;
        log.info("Shutting down JobWorker gracefully...");
    }
}
