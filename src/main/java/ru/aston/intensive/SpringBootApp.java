package ru.aston.intensive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.aston.intensive.controller", "ru.aston.intensive.service", "ru.aston.intensive.exception",
    "ru.aston.intensive.mapper"})
public class SpringBootApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApp.class, args);
    }

}
