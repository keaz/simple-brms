package com.kzone.brms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "brms.git")
public class GitConfigs {

    private String url;
    private String username;
    private String token;
    private String dir;

}
