package com.prashant.queuectl.config;


import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.jline.utils.AttributedString;

import org.springframework.shell.jline.PromptProvider;


@Configuration
public class AppConfig {


    @Bean
    public ModelMapper getModelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PromptProvider myPromptProvider() {
        return () -> new AttributedString("queuectl:> ");
    }

}
