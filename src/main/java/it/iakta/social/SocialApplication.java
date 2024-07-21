package it.iakta.social;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableAutoConfiguration
@EntityScan(basePackages = {"it.iakta.social.entity"})
public class SocialApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialApplication.class, args);
	}

	/**
	 * TODO:
	 * 
	 * 1. Create controller, service, entity, dto and repository packages for the following entities:
	 * 	- List of users followed by a user
	 *  - List of posts
	 *  	. A post has a content, a creation date and a list of comments
	 *  
	 */
	
	
}
