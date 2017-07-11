package com.ebi.task;

import static org.junit.Assert.assertArrayEquals;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AccessionNumberRangeTests {

	@Autowired
	public AccessionNumberRange accessionNumberRange;

	@Autowired
	public AccessionNumberLoader accessionNumberLoader;

	@SpringBootApplication
	static class Config {

		@Bean
		public RestTemplate restTemplate() {
			return new RestTemplate();
		}
	}

	@LocalServerPort
	int port;

	private static final String SERVICE_URI = "http://localhost:%s/";

	@Autowired
	RestTemplate restTemplate;

	@Before
	public void setUp() {
		// accessionNumberLoader.init();
		deleteAccessionNumbers();
	}

	public void deleteAccessionNumbers() {
		accessionNumberLoader.getAccessionNumberRanges().clear();
	}

	@Test
	public void tests() {
		HashMap<String, String> map = new HashMap<>();
		map.put("A0,A1,A2,A3,A4,A5", "A0-A5");
		map.put("A0,A2,A3,A4,A5,A6", "A0,A2-A6");
		map.put("A0,A1,A3,A4,A5,A6", "A0-A1,A3-A6");
		map.put("A0,A1,A2,A4,A5,A6", "A0-A2,A4-A6");
		map.put("A0,A1,A2,A3,A4,A6", "A0-A4,A6");
		map.put("A0,A1,A3,A4,A5,A7", "A0-A1,A3-A5,A7");
		//map.put("0A", "");
		//map.put("0A,A0", "");
		for (String key : map.keySet()) {
			String[] output = accessServiceUsingRestTemplate();
			accessionNumberLoader.updateAccessionNumbers(key);
			AccessionNumber anr = AccessionNumber.constructAccessionNumber(key);
			if (anr == null)
				continue;
			TreeSet<String> ts = accessionNumberLoader.getAccessionNumberRanges().get(anr).getSuffixGroups();
			output = ts.toArray(new String[ts.size()]);
			String value = map.get(key);
			String[] values = value.split(",");
			assertArrayEquals(values, output);
			deleteAccessionNumbers();
		}

	}

	public String[] accessServiceUsingRestTemplate() {
		URI uri = URI.create(String.format(SERVICE_URI + "accessionGroups", "8080"));
		ResponseEntity<String[]> output = restTemplate.getForEntity(uri, String[].class);
		String[] finalOutput = output.getBody();
		Arrays.asList(finalOutput).stream().forEach(n -> System.out.println(n));
		return finalOutput;
	}

}
