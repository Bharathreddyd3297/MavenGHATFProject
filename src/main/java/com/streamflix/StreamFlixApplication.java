package com.streamflix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the StreamFlix application.
 * Running this class starts an embedded web server on the port defined
 * in application.properties (default: 8080).
 */
@SpringBootApplication
public class StreamFlixApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamFlixApplication.class, args);
    }

}
