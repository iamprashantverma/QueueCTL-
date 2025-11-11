package com.prashant.queuectl.config;

import lombok.Getter;
import lombok.Setter;
import org.jline.utils.AttributedString;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.jline.PromptProvider;

@Getter
@Setter
@Configuration
public class AppConfig {

    private int maxRetries = 3;
    private int backoffSeconds = 2;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PromptProvider promptProvider() {
        return () -> new AttributedString("queuectl:> ");
    }
}
