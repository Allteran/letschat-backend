package io.allteran.letschatbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude={MongoAutoConfiguration.class})
public class LetschatBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LetschatBackendApplication.class, args);
	}

}
