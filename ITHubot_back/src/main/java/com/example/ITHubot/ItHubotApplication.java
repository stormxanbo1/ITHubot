package com.example.ITHubot;

import com.example.ITHubot.Dal.DataAccessLayer;
import com.example.ITHubot.Models.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class ItHubotApplication {
	public static ApplicationContext context;
	public static User currentUser = null;
	public static void main(String[] args) {
		SpringApplication.run(ItHubotApplication.class, args);
	}

}
