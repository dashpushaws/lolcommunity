package com.example.lolcommunity.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
	// CORS(cross origin resource sharing)을 설정
	// 서로 다른 도메인:포트 간의 통신 허용
	// AJAX(Async Javascript and XML) 통신 허용 8080(spring)과 8081(vue)
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// 모든경로 모든 메소드(GET, POST...)
		// (/로시작하는 모든경로)
		registry.addMapping("/**").allowedMethods("*");
	}

}
