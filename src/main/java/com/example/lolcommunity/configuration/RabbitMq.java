package com.example.lolcommunity.configuration;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMq {
	@Bean // MessageConverter Bean 추가
	public MessageConverter rabbitMessageConverter() {
		return new Jackson2JsonMessageConverter();// Java Object <- JSON
	}
}
