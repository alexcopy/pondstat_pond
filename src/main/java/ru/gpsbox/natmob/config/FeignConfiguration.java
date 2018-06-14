package ru.gpsbox.natmob.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "ru.gpsbox.natmob")
public class FeignConfiguration {

}
