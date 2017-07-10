package com.ebi.task;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class AccessionController {

	private final AccessionNumberRepository accessionNumberRepository;

	@Autowired
	private AccessionNumberLoader accessionNumberLoader;

	public AccessionController(AccessionNumberRepository accessionNumberRepository) {
		this.accessionNumberRepository = accessionNumberRepository;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/accessionGroups")
	public Collection<String> getAccessionNumberGroups() {
		TreeSet<String> output = new TreeSet<>();
		for (AccessionNumberRange anr : accessionNumberRepository.findAll()) {
			output.addAll(anr.getSuffixGroups());
		}
		return output;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/accessionNumberRanges")
	public Iterable<AccessionNumberRange> getAccessionNumbers() {
		return accessionNumberRepository.findAll();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/accessionNumberRanges/{id}")
	public AccessionNumberRange getAccessionNumberRanges(@PathVariable Long id) {
		AccessionNumberRange anr = accessionNumberRepository.findOne(id);
		return anr;
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/accessionNumberRanges")
	Collection<ResponseEntity<?>> delete(@RequestBody SuffixGroups suffixGroups) {
		ArrayList<ResponseEntity<?>> a = new ArrayList<>();
		ArrayList<String> groups = suffixGroups.getSuffixGroups();
		for (String item : groups) {
			AccessionNumber accNumber = AccessionNumber.constructAccessionNumber(item);
			AccessionNumberRange anr = accessionNumberLoader.deleteAccessionNumber(accNumber);
			if (anr != null)
				a.add(ResponseEntity.noContent().build());
			else
				a.add(ResponseEntity.notFound().build());
		}
		return a;
	}

	@RequestMapping(method = RequestMethod.POST, path = "/accessionNumberRanges")
	Collection<ResponseEntity<?>> add(@RequestBody SuffixGroups suffixGroups) {
		ArrayList<ResponseEntity<?>> a = new ArrayList<>();
		ArrayList<String> groups = suffixGroups.getSuffixGroups();
		for (String item : groups) {
			AccessionNumber accNumber = AccessionNumber.constructAccessionNumber(item);
			if (accNumber == null) {
				a.add(ResponseEntity.unprocessableEntity().build());
				continue;
			}
			AccessionNumberRange anr = accessionNumberLoader.updateAccessionNumber(accNumber);
			URI anrUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/accessionNumberRanges/{id}")
					.buildAndExpand(anr.getId()).toUri();
			a.add(ResponseEntity.created(anrUri).build());
		}
		return a;
	}
}
