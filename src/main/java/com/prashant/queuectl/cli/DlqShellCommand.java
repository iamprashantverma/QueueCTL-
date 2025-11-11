
package com.prashant.queuectl.cli;

import com.prashant.queuectl.dto.JobResponseDTO;
import com.prashant.queuectl.entity.enums.State;
import com.prashant.queuectl.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@Slf4j
@ShellComponent
@RequiredArgsConstructor
public class DlqShellCommand {

    private final JobService jobService;

    @ShellMethod(key = "dlq list", value = "View jobs in Dead Letter Queue")
    public List<JobResponseDTO> listDlqJobs() {
        return jobService.jobsByState(State.DEAD);
    }

    @ShellMethod(key = "dlq retry", value = "Retry a job from Dead Letter Queue")
    public JobResponseDTO retryDlqJob(@ShellOption(help = "Job ID to retry") Long jobId) {
        return jobService.retryFromDlq(jobId);
    }
}
