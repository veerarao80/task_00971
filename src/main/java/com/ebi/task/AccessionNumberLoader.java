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

	@Autowired
	public AccessionNumberRange accessionNumberRange;

	public ConcurrentSkipListMap<AccessionNumber, AccessionNumberRange> accessionNumberRanges = new ConcurrentSkipListMap<>();

	@Autowired
	public AccessionNumberLoader(AccessionNumberRepository accessionNumberRepository) {
		this.accessionNumberRepository = accessionNumberRepository;
	}

	@PostConstruct
	public void init() {
		String accessionNumbers = "A00000, A0001, ERR000111, ERR000112, ERR000113, ERR000115, ERR000116, ERR100114, ERR200000001, ERR200000002, ERR200000003, DRR2110012, SRR211001, ABCDEFG1";
		Arrays	.asList(accessionNumbers.split(","))
				.stream()
				.forEach((value ->
		{
					AccessionNumber accNumber = AccessionNumber.constructAccessionNumber(value);
					AccessionNumberRange accNumberRange = accessionNumberRanges.putIfAbsent(accNumber,
							new AccessionNumberRange(accNumber));
					if (accNumberRange != null)
						accNumberRange.addSuffix(accNumber.suffix);
				}));
		accessionNumberRanges	.values()
								.stream()
								.forEach(value -> value.generateSuffixGroups());
		StreamSupport.stream(accessionNumberRepository	.save(accessionNumberRanges.values())
														.spliterator(),
				false);
	}

	public AccessionNumberRange updateAccessionNumber(AccessionNumber accNumber) {
		AccessionNumberRange accNumberRange = accessionNumberRanges.get(accNumber);
		if (accessionNumberRange == null) {
			accessionNumberRanges.put(accNumber, new AccessionNumberRange(accNumber));
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
		int suffixCount = accNumberRange.getSuffixes()
										.size();
		accNumberRange.generateSuffixGroups();
		if (suffixCount > 1)
			accessionNumberRepository.save(accNumberRange);
		else {
			accessionNumberRanges.remove(accNumber);
			accessionNumberRepository.delete(accNumberRange);
		}
		return accNumberRange;
	}

	public void updateAccessionNumber(String accString) {
		AccessionNumber accNumber = AccessionNumber.constructAccessionNumber(accString);
		AccessionNumberRange accNumberRange = accessionNumberRanges.get(accNumber);
		accNumberRange.addSuffix(accNumber.getSuffix());
		accNumberRange.generateSuffixGroups();
		accessionNumberRepository.save(accNumberRange);
	}
}
