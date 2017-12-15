package com.ufu.dup.repository;

import org.springframework.data.repository.CrudRepository;

import com.ufu.dup.to.Experiment;

public interface ExperimentRepository extends CrudRepository<Experiment, Integer> {

	Experiment findByTtWeightAndBbWeightAndTagTagWeightAndTag(double d, double e, double f, String tagFilter);
    
	

	
}
