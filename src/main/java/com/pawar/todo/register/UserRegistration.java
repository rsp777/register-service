package com.pawar.todo.register;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//@EnableDiscoveryClient
public class UserRegistration {

	public static void main(String[] args) {
		SpringApplication.run(UserRegistration.class, args);
	}

}
