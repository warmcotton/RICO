package com.sws.rico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RicoApplication {
	public static void main(String[] args) {
		SpringApplication.run(RicoApplication.class, args);
	}

}
