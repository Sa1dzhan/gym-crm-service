package com.gymcrm.config;

import com.gymcrm.util.Authentication;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String authToken = Authentication.getCurrentAuthToken();
            if (authToken != null) {
                requestTemplate.header("Authorization", authToken);
            }

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String transactionId = attributes.getRequest().getHeader("transactionId");
            if (transactionId != null) {
                requestTemplate.header("transactionId", transactionId);
            }
        };
    }
}
