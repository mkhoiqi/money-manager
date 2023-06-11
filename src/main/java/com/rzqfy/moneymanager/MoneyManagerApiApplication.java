package com.rzqfy.moneymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class MoneyManagerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneyManagerApiApplication.class, args);
	}

}
