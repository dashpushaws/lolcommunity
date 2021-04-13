package com.example.lolcommunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//@Scheduled 사용위해 @Configuration @EnableScheduling 선언돼있어야
@SpringBootApplication // @Configuration를 포함하고 있음
@EnableScheduling
public class LolcommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(LolcommunityApplication.class, args);
	}

}
