package com.prashant.queuectl.entity;

import com.prashant.queuectl.entity.enums.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity(name = "jobs")
@EntityListeners(AuditingEntityListener.class)
@Data
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Job state is required")
    private State state;

    @NotBlank(message = "Command is required")
    private String command;

    @Column(nullable = false)
    private Integer attempts = 0;

    @Column(nullable = false)
    private Integer maxRetries = 3;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime nextAttemptTime;
}
