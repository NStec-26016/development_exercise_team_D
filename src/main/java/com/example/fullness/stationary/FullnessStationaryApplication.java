package com.example.fullness.stationary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		SecurityAutoConfiguration.class // 👈これを入れることで、デフォルトのロック機能が完全にオフになります
})
public class FullnessStationaryApplication {

	public static void main(String[] args) {
		SpringApplication.run(FullnessStationaryApplication.class, args);
	}

}
