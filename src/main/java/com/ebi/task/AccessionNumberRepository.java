package com.ebi.task;

import org.springframework.data.repository.CrudRepository;

public interface AccessionNumberRepository extends CrudRepository<AccessionNumberRange, Long> {

	/*
	 * @RestResource(exported = false, path = "/accessionNumberRanges") public
	 * <S extends AccessionNumberRange> S save(S entity);
	 */

}
