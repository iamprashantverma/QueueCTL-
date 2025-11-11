package com.prashant.queuectl.dto;

import com.prashant.queuectl.entity.enums.State;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobRequestDTO {

    @NotNull(message = "State is required")
    private State state;
    @NotNull(message = "Please enter the command")
    private String command;
    private Integer maxRetries ;
}
