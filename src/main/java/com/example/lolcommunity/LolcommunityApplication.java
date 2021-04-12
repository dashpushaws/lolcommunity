package com.example.lolcommunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LolcommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(LolcommunityApplication.class, args);
	}

}
