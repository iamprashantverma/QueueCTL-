package com.prashant.queuectl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for QueueCTL - A CLI-based background job queue system.
 * 
 * @author Prashant
 * @version 1.0
 */
@Slf4j
@SpringBootApplication
@EnableJpaAuditing
public class QueueCtlApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueueCtlApplication.class, args);
        log.info("QueueCTL Application started successfully");
    }
}
