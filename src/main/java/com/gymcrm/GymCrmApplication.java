package com.gymcrm;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.NumberFormat;

@Slf4j
@SpringBootApplication
public class GymCrmApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymCrmApplication.class, args);

        Runtime runtime = Runtime.getRuntime();

        final NumberFormat format = NumberFormat.getInstance();
        final long maxMemory = runtime.maxMemory();
        final long allocatedMemory = runtime.totalMemory();
        final long freeMemory = runtime.freeMemory();
        final long mb = 1024 * 1024;
        final String mega = " MB";

        log.info("========================== Memory Info ==========================");
        log.info("Free memory: " + format.format(freeMemory / mb) + mega);
        log.info("Allocated memory: " + format.format(allocatedMemory / mb) + mega);
        log.info("Max memory: " + format.format(maxMemory / mb) + mega);
        log.info("Total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / mb) + mega);
    }
}

