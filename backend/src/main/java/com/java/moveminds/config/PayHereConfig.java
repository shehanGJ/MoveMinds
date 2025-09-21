package com.java.moveminds.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payhere")
@Data
public class PayHereConfig {
    
    private String merchantId;
    private String merchantSecret;
    private String sandboxUrl;
    private String liveUrl;
    private String returnUrl;
    private String cancelUrl;
    private String notifyUrl;
    private boolean sandboxMode = true;
    
    public String getPaymentUrl() {
        return sandboxMode ? sandboxUrl : liveUrl;
    }
}
