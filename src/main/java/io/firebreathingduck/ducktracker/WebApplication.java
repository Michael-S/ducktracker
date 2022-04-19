package io.firebreathingduck.ducktracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import io.firebreathingduck.ducktracker.domain.FieldNames;
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

	private String getAsJson(Object o) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(o);
		} catch (IOException ioe) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/api/ducks")
	public String getDucks() {
		List<Map<String, Object>> ducks = new PostgresDTPersister(jdbcTemplate).getAllDucks();
		return getAsJson(ducks);
	}

	@GetMapping("/api/ducks/{id}")
	public String getDuck(@PathVariable("id") Integer id) {
		Map<String, Object> duck = null;
		if (id != null) {
			duck = new PostgresDTPersister(jdbcTemplate).getDuck(id);
		}
		if (duck == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return getAsJson(duck);
	}

	@PostMapping("/api/ducks/create")
	public ModelAndView createDuck(@RequestParam(value = "name") String duckName, 
		@RequestParam(value = "tagged") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tagged) {
		Map<String, Object> createdDuck = Map.of(FieldNames.DUCK_NAME, duckName, 
			FieldNames.DUCK_TAGGED, tagged);
		createdDuck = new PostgresDTPersister(jdbcTemplate).saveDuck(createdDuck);
		String redirectPattern = "redirect:/api/ducks/" + createdDuck.get(FieldNames.DUCK_ID);
		return new ModelAndView(redirectPattern);
	}

	@GetMapping("/api/ponds")
	public String getPonds() {
		List<Map<String, Object>> ponds = new PostgresDTPersister(jdbcTemplate).getAllPonds();
		return getAsJson(ponds);
	}

	@GetMapping("/api/ponds/{id}")
	public String getPond(@PathVariable("id") Integer id) {
		Map<String, Object> pond = null;
		if (id != null) {
			pond = new PostgresDTPersister(jdbcTemplate).getPond(id);
		}
		if (pond == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return getAsJson(pond);
	}

	@PostMapping("/api/ponds/create")
	public ModelAndView createPond(@RequestParam(value = "name") String pondName, 
		@RequestParam(value = "location") String pondLocation) {
		Map<String, Object> createdPond = Map.of(FieldNames.POND_NAME, pondName, 
			FieldNames.POND_LOCATION, pondLocation);
		createdPond = new PostgresDTPersister(jdbcTemplate).savePond(createdPond);
		String redirectPattern = "redirect:/api/ponds/" + createdPond.get(FieldNames.POND_ID);
		return new ModelAndView(redirectPattern);
	}

	@GetMapping("/api/ducktravel")
	public String getDuckTravel() {
		List<Map<String, Object>> duckTravels = new PostgresDTPersister(jdbcTemplate).getAllDuckTravel();
		return getAsJson(duckTravels);
	}

	@GetMapping("/api/ducktravel/duck/{id}")
	public String getDuckTravelByDuck(@PathVariable("id") Integer id) {
		List<Map<String, Object>> duckTravels = null;
		if (id != null) {
			duckTravels = new PostgresDTPersister(jdbcTemplate).getDuckTravelByDuck(id);
		}
		if (duckTravels == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return getAsJson(duckTravels);
	}

	@GetMapping("/api/ducktravel/pond/{id}")
	public String getDuckTravelByPond(@PathVariable("id") Integer id) {
		List<Map<String, Object>> duckTravels = null;
		if (id != null) {
			duckTravels = new PostgresDTPersister(jdbcTemplate).getDuckTravelByPond(id);
		}
		if (duckTravels == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return getAsJson(duckTravels);
	}

	@PostMapping("/api/ducktravel/create")
	public ModelAndView createDuckTravel(@RequestParam(value = "duckId") Integer duckId, 
		@RequestParam(value = "pondId") Integer pondId, @RequestParam(value = "arrival")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date arrival,
		@RequestParam(value = "departure", required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date departure) {
		Map<String, Object> createdDuckTravel = null;
		if (departure != null) {
			createdDuckTravel = Map.of(FieldNames.DUCK_TRAVEL_DUCK_ID, duckId,
				FieldNames.DUCK_TRAVEL_POND_ID, pondId, FieldNames.DUCK_TRAVEL_ARRIVAL, arrival,
				FieldNames.DUCK_TRAVEL_DEPARTURE, departure);
		} else {
			createdDuckTravel = Map.of(FieldNames.DUCK_TRAVEL_DUCK_ID, duckId,
				FieldNames.DUCK_TRAVEL_POND_ID, pondId, FieldNames.DUCK_TRAVEL_ARRIVAL, arrival);
		}
		new PostgresDTPersister(jdbcTemplate).saveDuckTravel(createdDuckTravel);
		String redirectPattern = "redirect:/api/ducktravel/duck/" + duckId;
		return new ModelAndView(redirectPattern);
	}

}
