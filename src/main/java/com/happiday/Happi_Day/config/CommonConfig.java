package com.happiday.Happi_Day.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {
    @Value("${frontendUrl}")
    private String frontendUrl;

    public String getFrontendUrl() {
        return frontendUrl;
    }
}
