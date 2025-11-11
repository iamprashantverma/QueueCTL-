package com.prashant.queuectl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class QueueCtlApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueueCtlApplication.class, args);
		System.out.println("Application is Running");
	}

}
