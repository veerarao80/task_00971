package com.ebi.task;

import java.util.Arrays;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessionNumberLoader {

	private final AccessionNumberRepository accessionNumberRepository;

	public ConcurrentSkipListMap<AccessionNumber, AccessionNumberRange> accessionNumberRanges = new ConcurrentSkipListMap<>();

	@Autowired
	public AccessionNumberLoader(AccessionNumberRepository accessionNumberRepository) {
		this.accessionNumberRepository = accessionNumberRepository;
	}

	public void updateAccessionNumbers(String accessionNumbers) {
		Arrays.asList(accessionNumbers.split(",")).stream().forEach((value -> {
			AccessionNumber accNumber = AccessionNumber.constructAccessionNumber(value);
			if (accNumber == null) {
				return;
			}
			AccessionNumberRange accNumberRange = accessionNumberRanges.putIfAbsent(accNumber,
					new AccessionNumberRange(accNumber));
			if (accNumberRange != null)
				accNumberRange.addSuffix(accNumber.suffix);
		}));
		accessionNumberRanges.values().stream().forEach(value -> value.generateSuffixGroups());
		StreamSupport.stream(accessionNumberRepository.save(accessionNumberRanges.values()).spliterator(), false);
	}

	@PostConstruct
	public void init() {
		String accessionNumbers = "A00000, A0001, ERR000111, ERR000112, ERR000113, ERR000115, ERR000116, ERR100114, ERR200000001, ERR200000002, ERR200000003, DRR2110012, SRR211001, ABCDEFG1";
		updateAccessionNumbers(accessionNumbers);
	}

	public AccessionNumberRange updateAccessionNumber(AccessionNumber accNumber) {
		AccessionNumberRange accNumberRange = accessionNumberRanges.get(accNumber);
		if (accNumberRange == null) {
			accessionNumberRanges.put(accNumber, new AccessionNumberRange(accNumber));
			accNumberRange = accessionNumberRanges.get(accNumber);
			accNumberRange.addSuffix(accNumber.suffix);
		}
		accNumberRange.addSuffix(accNumber.getSuffix());
		accNumberRange.generateSuffixGroups();
		accessionNumberRepository.save(accNumberRange);
		return accNumberRange;
	}

	public AccessionNumberRange deleteAccessionNumber(AccessionNumber accNumber) {
		AccessionNumberRange accNumberRange = accessionNumberRanges.get(accNumber);
		if (accNumberRange == null) {
			return null;
		}
		accNumberRange.removeSuffix(accNumber.getSuffix());
		int suffixCount = accNumberRange.getSuffixes().size();
		accNumberRange.generateSuffixGroups();
		if (suffixCount >= 1)
			accessionNumberRepository.save(accNumberRange);
		else {
			accessionNumberRanges.remove(accNumber);
			accessionNumberRepository.delete(accNumberRange.getId());
			accessionNumberRepository.exists(accNumberRange.getId());
			accessionNumberRepository.findOne(accNumberRange.getId());
		}
		return accNumberRange;
	}

	public void updateAccessionNumber(String accString) {
		AccessionNumber accNumber = AccessionNumber.constructAccessionNumber(accString);
		updateAccessionNumber(accNumber);
	}

	public ConcurrentSkipListMap<AccessionNumber, AccessionNumberRange> getAccessionNumberRanges() {
		return accessionNumberRanges;
	}
}
