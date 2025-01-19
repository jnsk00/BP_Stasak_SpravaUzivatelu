package org.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class SpravaUzivateluApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpravaUzivateluApplication.class, args);
    }
}


