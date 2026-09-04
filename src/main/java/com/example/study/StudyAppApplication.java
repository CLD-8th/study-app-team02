package com.example.study;

import com.example.study.auth.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class StudyAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyAppApplication.class, args);
    }
}
