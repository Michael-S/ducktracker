package io.firebreathingduck.ducktracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import io.firebreathingduck.ducktracker.persist.DTPersister;
import io.firebreathingduck.ducktracker.persist.PostgresDTPersister;


@SpringBootApplication
@RestController
public class WebApplication {

	@Autowired
	JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		DTPersister persister = new PostgresDTPersister(jdbcTemplate);
		String ducks = persister.getAllDucks().toString();
		String ponds = persister.getAllPonds().toString();
		String duckTravels = persister.getAllDuckTravel().toString();

		return String.format("Hello %s! The ducks are %s, the ponds are %s, and the travel is %s.",
			name, ducks, ponds, duckTravels);
	}

}
