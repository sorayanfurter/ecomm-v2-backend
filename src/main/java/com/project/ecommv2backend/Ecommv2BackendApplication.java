package com.project.ecommv2backend;

import com.project.ecommv2backend.model.LocalUser;
import com.project.ecommv2backend.repository.LocalUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Ecommv2BackendApplication{

	public static void main(String[] args) {
		SpringApplication.run(Ecommv2BackendApplication.class, args);
	}


}
