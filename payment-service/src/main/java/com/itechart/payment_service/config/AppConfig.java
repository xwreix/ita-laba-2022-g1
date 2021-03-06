package com.itechart.payment_service.config;

import com.itechart.payment_service.util.ElectronicPaymentSystem;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig
{
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate()
    {
        return new RestTemplate();
    }

    @Bean
    public ElectronicPaymentSystem electronicPaymentSystem() { return new ElectronicPaymentSystem(); }
}
