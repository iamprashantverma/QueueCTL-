package com.prashant.queuectl.cli;

import com.prashant.queuectl.config.AppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@Slf4j
@ShellComponent
@RequiredArgsConstructor
public class ConfigShellCommand {

    private final AppConfig appConfig;

    @ShellMethod(key = "config set", value = "Update configuration settings")
    public String setConfig(
            @ShellOption(help = "Configuration key (max-retries, backoff)") String key,
            @ShellOption(help = "Configuration value") int value) {
        
        log.info("Setting config: {} = {}", key, value);
        
        switch (key.toLowerCase()) {
            case "max-retries":
                appConfig.setMaxRetries(value);
                return String.format("maxRetries set to %d", value);
            case "backoff":
                appConfig.setBackoffSeconds(value);
                return String.format("backoffSeconds set to %d", value);
            default:
                return "Unknown config key: " + key + ". Available keys: max-retries, backoff";
        }
    }

    @ShellMethod(key = "config get", value = "Show current configuration")
    public String getConfig(@ShellOption(defaultValue = "all") String key) {
        switch (key.toLowerCase()) {
            case "max-retries":
                return String.format("maxRetries = %d", appConfig.getMaxRetries());
            case "backoff":
                return String.format("backoffSeconds = %d", appConfig.getBackoffSeconds());
            case "all":
            default:
                return String.format("maxRetries = %d, backoffSeconds = %d",
                        appConfig.getMaxRetries(), appConfig.getBackoffSeconds());
        }
    }
}
