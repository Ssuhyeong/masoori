package com.fintech.masoori;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(servers = {@Server(url = "http://localhost:8080", description = "Local Server URL"),
	@Server(url = "${server.ec2-url}", description = "EC2 Server URL")})
@EnableJpaAuditing
@SpringBootApplication
public class MasooriApplication {

	public static void main(String[] args) {
		SpringApplication.run(MasooriApplication.class, args);
	}

}
