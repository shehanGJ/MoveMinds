package com.java.moveminds.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PayHereConfig.class)
public class PayHereConfiguration {
}
