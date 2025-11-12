package com.toursandtravel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.toursandtravel.repository")
public class ToursandtravelApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToursandtravelApplication.class, args);
	}

}
