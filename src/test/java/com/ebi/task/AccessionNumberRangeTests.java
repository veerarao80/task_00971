package com.ebi.task;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

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
	}

	public static void main(String[] args) {
		AccessionNumberRangeTests a = new AccessionNumberRangeTests();
		// ArrayList<String[]> t = a.tests();
		// System.out.println(t);
	}

	@Test
	public void tests() {
		Character ch = 'A';
		ArrayList<String[]> array = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			String ch1[] = serialNumbers(5, i, ch, i);
			array.add(ch1);
			System.out.println(String.join(",", ch1));
			accessionNumberLoader.update(String.join(",", ch1));
			accessServiceUsingRestTemplate();
			for (String acc : ch1) {
				AccessionNumber accNumber = AccessionNumber.constructAccessionNumber(acc);
				accessionNumberLoader.deleteAccessionNumber(accNumber);
			}
		}
		// return array;
	}

	public void accessServiceUsingRestTemplate() {
		URI uri = URI.create(String.format(SERVICE_URI + "accessionGroups", port));
		ResponseEntity<String[]> output = restTemplate.getForEntity(uri, String[].class);
		String[] finalOutput = output.getBody();
		Arrays.asList(finalOutput).stream().forEach(n -> System.out.println(n));
	}

	public String[] serialNumbers(int count, int skip, Character prefix, int iter) {
		String[] ch = new String[count];
		boolean skipped = false;
		for (int i = 0; i < count; i++) {
			if (iter != 0 && (i == skip || skipped == true)) {
				skipped = true;
				ch[i] = prefix + "" + (i + 1);
			} else
				ch[i] = prefix + "" + i;
		}
		return ch;
	}

}
