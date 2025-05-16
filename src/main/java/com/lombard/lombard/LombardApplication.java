package com.lombard.lombard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = "com.lombard.lombard")
public class LombardApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(LombardApplication.class, args);
    }
}