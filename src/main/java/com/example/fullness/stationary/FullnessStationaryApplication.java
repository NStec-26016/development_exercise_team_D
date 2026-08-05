package com.example.fullness.stationary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication()
public class FullnessStationaryApplication {

	public static void main(String[] args) {
		SpringApplication.run(FullnessStationaryApplication.class, args);
	}

}
