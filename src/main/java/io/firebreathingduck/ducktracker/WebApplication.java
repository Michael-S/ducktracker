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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
		@RequestParam(value = "tagged", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date tagged) {
		List<String> errors = new ArrayList<>();
		if (duckName == null || duckName.length() < 3 || duckName.length() > 200) {
			errors.add("Duck name must be present and between 3 and 200 characters in length.");
		}
		if (tagged == null) {
			errors.add("The date the duck was tagged must be present and in numeric yyyy-mm-dd format.");
		}
		if (!errors.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.stream().collect(Collectors.joining("\n")));
		}

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
		List<String> errors = new ArrayList<>();
		if (pondName == null || pondName.length() < 3 || pondName.length() > 200) {
			errors.add("Pond name must be present and between 3 and 200 characters in length.");
		}
		if (pondLocation == null || pondLocation.length() < 5 || pondLocation.length() > 500) {
			errors.add("Pond location must be present and between 5 and 500 characters in length.");
		}
		if (!errors.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.stream().collect(Collectors.joining("\n")));
		}
		Map<String, Object> createdPond = Map.of(FieldNames.POND_NAME, pondName,
			FieldNames.POND_LOCATION, pondLocation);
		createdPond = new PostgresDTPersister(jdbcTemplate).savePond(createdPond);
		String redirectPattern = "redirect:/api/ponds/" + createdPond.get(FieldNames.POND_ID);
		return new ModelAndView(redirectPattern);
	}

	@GetMapping("/api/ducktravels")
	public String getDuckTravel() {
		List<Map<String, Object>> duckTravels = new PostgresDTPersister(jdbcTemplate).getAllDuckTravelViews();
		return getAsJson(duckTravels);
	}

	@GetMapping("/api/ducktravels/duck/{id}")
	public String getDuckTravelByDuck(@PathVariable("id") Integer id) {
		List<Map<String, Object>> duckTravels = null;
		if (id != null) {
			duckTravels = new PostgresDTPersister(jdbcTemplate).getDuckTravelViewByDuck(id);
		}
		if (duckTravels == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return getAsJson(duckTravels);
	}

	@GetMapping("/api/ducktravels/pond/{id}")
	public String getDuckTravelByPond(@PathVariable("id") Integer id) {
		List<Map<String, Object>> duckTravels = null;
		if (id != null) {
			duckTravels = new PostgresDTPersister(jdbcTemplate).getDuckTravelViewByPond(id);
		}
		if (duckTravels == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return getAsJson(duckTravels);
	}

	@PostMapping("/api/ducktravels/create")
	public ModelAndView createDuckTravel(@RequestParam(value = "duck_id") Integer duckId,
		@RequestParam(value = "pond_id") Integer pondId, @RequestParam(value = "arrival", required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date arrival,
		@RequestParam(value = "departure", required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date departure) {
		List<String> errors = new ArrayList<>();
		DTPersister dtPersister = new PostgresDTPersister(jdbcTemplate);
		Map<String, Object> duckData = dtPersister.getDuck(duckId);
		if (duckData == null || duckData.isEmpty()) {
			errors.add("A valid duck_id must be supplied, no duck for duck_id " + duckId + " was found.");
		}
		Map<String, Object> pondData = dtPersister.getPond(pondId);
		if (pondData == null || pondData.isEmpty()) {
			errors.add("A valid pond_id must be supplied, no pond for pond_id " + pondId + " was found.");
		}		
		if (arrival == null) {
			errors.add("An arrival date for the duck to reach the pond must be provided.");
		} else if (arrival != null
			&& duckData != null
			&& duckData.get(FieldNames.DUCK_TAGGED) != null
			&& ((Date)duckData.get(FieldNames.DUCK_TAGGED)).after(arrival)) {
			errors.add("The arrival date for the duck at a pond has to be after the date the duck was tagged.");
		}
		if (!errors.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.stream().collect(Collectors.joining("\n")));
		}			

		Map<String, Object> createdDuckTravel = null;
		if (departure != null) {
			createdDuckTravel = Map.of(FieldNames.DUCK_TRAVEL_DUCK_ID, duckId,
				FieldNames.DUCK_TRAVEL_POND_ID, pondId, FieldNames.DUCK_TRAVEL_ARRIVAL, arrival,
				FieldNames.DUCK_TRAVEL_DEPARTURE, departure);
		} else {
			createdDuckTravel = Map.of(FieldNames.DUCK_TRAVEL_DUCK_ID, duckId,
				FieldNames.DUCK_TRAVEL_POND_ID, pondId, FieldNames.DUCK_TRAVEL_ARRIVAL, arrival);
		}
		dtPersister.saveDuckTravel(createdDuckTravel);
		String redirectPattern = "redirect:/api/ducktravels/duck/" + duckId;
		return new ModelAndView(redirectPattern);
	}

}
