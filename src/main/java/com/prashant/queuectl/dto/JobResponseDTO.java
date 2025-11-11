package com.prashant.queuectl.dto;

import com.prashant.queuectl.entity.enums.State;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobResponseDTO {

    private Long id;
    private State state;
    private Integer attempts;
    private Integer maxRetries;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
