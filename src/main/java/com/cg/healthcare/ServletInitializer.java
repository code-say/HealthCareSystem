package com.cg.healthcare;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	
	/*
	 * SpringBootServletInitializer is an interface to
	 *  run SpringApplication from a traditional WAR deployment. 
	 * It binds Servlet,
	 *  Filter and ServletContextInitializer 
	 *  beans from the application context to the server.
	 * 
	 */
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(HealthCareSystemApplication.class);
	}
}
