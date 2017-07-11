package com.ebi.task;

import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Component
class AccessionNumberRange {

	private @Id @GeneratedValue Long id;

	@JsonIgnore
	private TreeSet<String> suffixes = new TreeSet<>();

	private String prefix;

	private TreeSet<String> suffixGroups = new TreeSet<>();

	AccessionNumberRange() {

	}

	AccessionNumberRange(AccessionNumber pp) {
		prefix = pp.prefix;
		addSuffix(pp.suffix);
	}

	public void populateSuffixGroups() {
		suffixGroups = generateSuffixGroups();
	}

	public void addSuffix(String suffix) {
		suffixes.add(suffix);
	}

	public void removeSuffix(String suffix) {
		suffixes.remove(suffix);
	}

	private String previousString = "";

	private String rangeStartString = "";

	boolean rangeBool = false;

	public TreeSet<String> generateSuffixGroups() {
		previousString = "";
		rangeStartString = "";
		rangeBool = false;
		String[] suffixesArray = suffixes.toArray(new String[suffixes.size()]);
		suffixGroups.clear();
		for (int elementCount = 0; elementCount < suffixes.size(); elementCount++) {
			String str = suffixesArray[elementCount];
			if (elementCount == 0) {
				if (suffixes.size() == 1) {
					suffixGroups.add(prefix + str);
				}
				previousString = str;
			} else {
				Integer numValueInteger = Integer.parseInt(str);
				int lastValue = Integer.parseInt(previousString);
				if (numValueInteger == lastValue + 1) {
					if (rangeBool == false) {
						rangeBool = true;
						rangeStartString = previousString;
					}
					previousString = str;
					if (elementCount == suffixes.size() - 1) {
						suffixGroups.add(prefix + rangeStartString + "-" + prefix + previousString);
					}
				} else if (rangeBool) {
					suffixGroups.add(prefix + rangeStartString + "-" + prefix + previousString);
					rangeBool = false;
					previousString = str;
					if (elementCount == suffixes.size() - 1) {
						suffixGroups.add(prefix + str);
					}
				} else {
					suffixGroups.add(prefix + previousString);
					previousString = str;
					if (elementCount == suffixes.size() - 1) {
						suffixGroups.add(prefix + str);
					}
				}
			}
		}
		System.out.println(suffixGroups);
		return suffixGroups;
	}

	public TreeSet<String> getSuffixes() {
		return suffixes;
	}

	public void setSuffixes(TreeSet<String> suffixes) {
		this.suffixes = suffixes;
	}

	public TreeSet<String> getSuffixGroups() {
		return suffixGroups;
	}

	public void setSuffixGroups(TreeSet<String> suffixGroups) {
		this.suffixGroups = suffixGroups;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "AccessionNumberRange [suffixes=" + suffixes + ", suffixGroups=" + suffixGroups + "]";
	}
}
