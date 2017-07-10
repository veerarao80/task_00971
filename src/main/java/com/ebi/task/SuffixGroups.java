package com.ebi.task;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@Component
public class SuffixGroups {

	private ArrayList<String> suffixGroups = new ArrayList<String>();

	public ArrayList<String> getSuffixGroups() {
		return suffixGroups;
	}

	public void setSuffixGroups(ArrayList<String> suffixGroups) {
		this.suffixGroups = suffixGroups;
	}


}
