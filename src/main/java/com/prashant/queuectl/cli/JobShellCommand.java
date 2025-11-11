package com.prashant.queuectl.cli;

import com.prashant.queuectl.dto.JobRequestDTO;
import com.prashant.queuectl.dto.JobResponseDTO;
import com.prashant.queuectl.entity.enums.State;
import com.prashant.queuectl.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class JobShellCommand {

    private final JobService jobService;


    private JobRequestDTO createJobRequestDto(String command, int maxRetries) {
        return JobRequestDTO.builder()
                .command(command)
                .state(State.PENDING)
                .maxRetries(maxRetries)
                .build();
    }

    @ShellMethod(key = "enqueue", value = "Add a new job")
    public JobResponseDTO enqueueJob(@ShellOption(help = "Command to execute") String command, @ShellOption(defaultValue = "3", help = "Max retries") int maxRetries) {

        JobRequestDTO jobRequestDTO = createJobRequestDto(command,maxRetries);
        return  jobService.enqueueJob(jobRequestDTO);
    }

    @ShellMethod(key = "status", value = "Get jobs by state")
    public List<JobResponseDTO> jobByState(@ShellOption(help = "State of jobs to fetch") State state) {
        return jobService.jobsByState(state);
    }


}
