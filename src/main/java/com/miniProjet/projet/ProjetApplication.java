package com.miniProjet.projet;

import com.github.javafaker.Faker;
import com.miniProjet.projet.models.Role;
import com.miniProjet.projet.models.User;
import com.miniProjet.projet.service.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Date;

@SpringBootApplication
@OpenAPIDefinition
public class ProjetApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetApplication.class, args);


	}

	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService userService){
		return args ->{
			userService.saveRole(new Role(null, "admin"));
			userService.saveRole(new Role(null, "user"));

			Date date = new Date();
			Faker f = new Faker();

			userService.saveUser( new User(null,"Kimberley", "McCullough", f.date().birthday(), "North Aguedachester", "Andorra", "com.github.javafaker.Avatar@50e20022", "Kreiger-Kling", "Director",  "380.941.4454 x47758", "gill", "gill@gill.com", "1234",new ArrayList<>()));
			userService.saveUser(new User(null, "Myrtice", "Beer", f.date().birthday(), "Bartonville", "Montenegro", "com.github.javafaker.Avatar@65099ca1", "Klocko-Quitzon", "Architect", "312.939.6875", "kratos", "kratos@kratos.com", "1234", new ArrayList<>()));

			userService.assignRoleToUser("kratos","admin");
			userService.assignRoleToUser("gill","user");




		};
	}



}
