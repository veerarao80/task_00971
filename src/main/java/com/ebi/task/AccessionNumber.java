package com.ebi.task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
class AccessionNumber implements Comparable<AccessionNumber> {

	public String prefix;

	public int suffixLength;

	public String suffix;

	public String getSuffix() {
		return suffix;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + suffixLength;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccessionNumber other = (AccessionNumber) obj;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (suffixLength != other.suffixLength)
			return false;
		return true;
	}

	@Override
	public int compareTo(AccessionNumber p) {
		return prefix.compareTo(p.prefix) == 0 ? (new Integer(suffixLength)).compareTo(new Integer(p.suffixLength))
				: prefix.compareTo(p.prefix);
	}

	public static AccessionNumber constructAccessionNumber(String value) {
		Pattern pattern = Pattern.compile("[A-Z]+\\d+");
		Matcher matcher = pattern.matcher(value.trim());
		if (!matcher.matches()) {
			//errors.add(value);
		}
		char[] chars = value.toCharArray();
		String prefix = "";
		String suffix = "";
		for (char ch : chars) {
			if (Character.isDigit(ch)) {
				suffix += ch;
			} else if (Character.isAlphabetic(ch)) {
				prefix += ch;
			}
		}
		suffix = suffix.trim();
		AccessionNumber accNumber = new AccessionNumber();
		accNumber.prefix = prefix;
		accNumber.suffixLength = suffix.length();
		accNumber.suffix = suffix;
		return accNumber;
	}

}
