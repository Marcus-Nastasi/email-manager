package com.system.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * The e-mail manager application.
 *
 * @author Marcus Rolemnerg
 * @version 1.0.1
 * @since 2025
 */
@SpringBootApplication
@EnableCaching
public class EmailApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailApplication.class, args);
	}
}
