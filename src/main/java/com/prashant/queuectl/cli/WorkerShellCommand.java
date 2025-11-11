package com.prashant.queuectl.cli;

import com.prashant.queuectl.service.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@Slf4j
@ShellComponent
@RequiredArgsConstructor
public class WorkerShellCommand {

    private final JobWorker jobWorker;

    @ShellMethod(key = "worker start", value = "Start N background workers")
    public String startWorkers(@ShellOption int count) {
        log.info("Starting {} worker(s)", count);
        
        for (int i = 0; i < count; i++) {
            jobWorker.processJobs();
        }
        
        return String.format("%d worker(s) started successfully", count);
    }

    @ShellMethod(key = "worker stop", value = "Stop running workers gracefully")
    public String stopWorkers() {
        log.info("Initiating graceful shutdown of all workers");
        jobWorker.shutdown();
        return "Stopping all workers gracefully...";
    }
}
