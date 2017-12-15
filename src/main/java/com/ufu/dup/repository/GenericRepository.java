package com.ufu.dup.repository;

import java.util.List;
import java.util.Map;

import com.ufu.dup.to.PostLink;
import com.ufu.dup.to.Posts;
import com.ufu.dup.to.Question;


public interface GenericRepository {
	
	
	public List<Posts> findAllQuestions();

	public List<PostLink> getAllPostLinks();

	Map<Integer, Question> getQuestionsByFilters(String tagFilter,Integer limit, String maxCreationDate);
	
	//public List<Posts> findClosedDuplicatedNonMasters(String tagFilter, Integer postsLimit, String maxCreationDate);
	public List<Question> findClosedDuplicatedNonMasters(String tagFilter, Integer postsLimit, String maxCreationDate);
	

	
    
}
