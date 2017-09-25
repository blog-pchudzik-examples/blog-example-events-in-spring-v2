package com.pchudzik.blog.example.springevents.more;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringeventsMoreApplication {
	public static void main(String[] args) {
		final ConfigurableApplicationContext ctx = SpringApplication.run(SpringeventsMoreApplication.class, args);
	}
}
