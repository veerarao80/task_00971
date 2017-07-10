package com.ebi.task;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Component
@NoArgsConstructor
@Data
class AccessionNumberRange {

	private @Id @GeneratedValue Long id;

	@JsonIgnore
	private TreeSet<String> suffixes = new TreeSet<>();

	private String prefix;

	private TreeSet<String> suffixGroups;

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


	public TreeSet<String> generateSuffixGroups() {
		suffixGroups = suffixes	.stream()
								.collect(new AccessionNumberRangeCollector(prefix));
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

	private class AccessionNumberRangeCollector implements Collector<String, TreeSet<String>, TreeSet<String>> {

		private String prefix = "";

		private String previousString = "";

		private String rangeStartString = "";

		private int elementCount = 0;

		boolean rangeBool = false;

		AccessionNumberRangeCollector(String prefix) {
			this.prefix = prefix;
		}

		@Override
		public Supplier<TreeSet<String>> supplier() {
			return TreeSet<String>::new;
		}

		@Override
		public BiConsumer<TreeSet<String>, String> accumulator() {
			previousString = "";
			rangeStartString = "";
			elementCount = 0;
			return (sortedSet, str) -> {
				elementCount++;
				if (elementCount == 1) {
					if (suffixes.size() == 1) {
						sortedSet.add(prefix + str);
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
						if (elementCount == suffixes.size()) {
							sortedSet.add(prefix + rangeStartString + "-" + prefix + previousString);
						}
					} else if (rangeBool) {
						sortedSet.add(prefix + rangeStartString + "-" + prefix + previousString);
						rangeBool = false;
						previousString = str;
						if (elementCount == suffixes.size()) {
							sortedSet.add(prefix + str);
						}
					} else {
						sortedSet.add(prefix + previousString);
						previousString = str;
					}
				}
			};

		}

		@Override
		public BinaryOperator<TreeSet<String>> combiner() {
			return (first, second) -> {
				first.addAll(second);
				return first;
			};
		}

		@Override
		public Function<TreeSet<String>, TreeSet<String>> finisher() {
			return (sortedSet) -> {
				System.out.println(sortedSet);
				return sortedSet;
			};
		}

		@Override
		public Set<Characteristics> characteristics() {
			return EnumSet.of(Characteristics.UNORDERED);
		}

	}

	@Override
	public String toString() {
		return "AccessionNumberRange [suffixes=" + suffixes + ", suffixGroups=" + suffixGroups + "]";
	}
}
