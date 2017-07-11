package com.ebi.task;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.nio.charset.Charset;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@WebAppConfiguration
public class EbiApplicationIntegrationTests {

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

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Autowired
	private MockMvc mockMvc;

	@Before
	public void setUp() {
		accessionNumberLoader.init();
	}

	// @Test
	public void update() {
		String accessionNumbers = "A00001,A0002";
		Arrays.asList(accessionNumbers.split(",")).stream().forEach((value -> {
			accessionNumberLoader.updateAccessionNumber(value);
		}));
	}

	// @Test
	public void accessServiceUsingRestTemplate() {
		// update();
		URI uri = URI.create(String.format(SERVICE_URI + "accessionGroups", port));
		ResponseEntity<String[]> output = restTemplate.getForEntity(uri, String[].class);
		String[] finalOutput = output.getBody();
		Arrays.asList(finalOutput).stream().forEach(n -> System.out.println(n));
	}

	@Test
	public void postAccNumber() {
		URI uri = URI.create(String.format(SERVICE_URI + "accessionNumberRanges/", port));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		AccessionNumber an = AccessionNumber.constructAccessionNumber("A00002");
		HttpEntity<AccessionNumber> entity = new HttpEntity<>(an, headers);
		/*
		 * RequestEntity<Void> request = RequestEntity .get(uri)
		 * .accept(MediaTypes.HAL_JSON) .build();
		 */
		Object response = restTemplate.exchange(uri, HttpMethod.POST, entity, Object.class);
		System.out.println(response);
		accessServiceUsingRestTemplate();
	}

	@Test
	public void mockTests() {
		
		/*mockMvc.perform(get("/accessionNumberRanges" + this.bookmarkList.get(0).getId())).andExpect(status().isOk())
				.andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.id", is(this.bookmarkList.get(0).getId().intValue())))
				.andExpect(jsonPath("$.uri", is("http://bookmark.com/1/" + userName)))
				.andExpect(jsonPath("$.description", is("A description")));*/
	}

}
