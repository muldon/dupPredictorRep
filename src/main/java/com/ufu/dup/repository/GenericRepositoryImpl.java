package com.ufu.dup.repository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ufu.dup.to.PostLink;
import com.ufu.dup.to.Posts;
import com.ufu.dup.to.Question;
import com.ufu.util.DupPredictorUtils;

@Repository
public class GenericRepositoryImpl implements GenericRepository {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	private EntityManager em;



	@Override
	public List<Posts> findAllQuestions() {
		Query q = em.createNativeQuery("select * from posts p where p.posttypeid =1 order by id desc", Posts.class);
		return (List<Posts>) q.getResultList();
	}

	

	@Override
	public Map<Integer, Question> getQuestionsByFilters(String tagFilter, Integer limit, String maxCreationDate) {
		int considered = 0;
		int disconsidered = 0;			
		logger.info("getQuestionsByFilters: "+limit);
		HashMap<Integer, Question> map=new LinkedHashMap<Integer, Question>();
		
		String sql = " select * from posts  p " 
				+ " WHERE   p.posttypeId = 1 "
				+ " and p.title is not null "
				+ " and p.body is not null ";
		
		if(!StringUtils.isBlank(maxCreationDate)) {
			sql+= " and p.creationdate < '"+maxCreationDate+"'";
		}
		
		sql += DupPredictorUtils.getQueryComplementByTag(tagFilter);
		
		sql += " order by p.id ";
		
		if(limit!=null){
			sql+= " limit "+limit;
		}
		
		Query q = em.createNativeQuery(sql, Question.class);
		
		List<Question> posts = q.getResultList();
				
		logger.info("Posts in getQuestionsByFilters: "+posts.size());
		for(Question post:posts){
			map.put(post.getId(), post);
			//DupPredictorUtils.setBlanks(post);
		}
		//logger.info("Valid posts: "+considered);
		//logger.info("Invalid posts (title or body is blank): "+disconsidered);
		posts= null;
		return map;
	}
	
	
	
	

	

	@Override
	public List<PostLink> getAllPostLinks() {
		
		Query q = em.createNativeQuery("select * from postlinks p where linktypeid = 3", PostLink.class);
		return (List<PostLink>) q.getResultList();
	}

	@Override
	public List<Question> findClosedDuplicatedNonMasters(String tagFilter, Integer limit, String maxCreationDate) {
	//public List<Posts> findClosedDuplicatedNonMasters(String tagFilter, Integer limit, String maxCreationDate) {
		String sql = " select * from posts  p " 
				+ " WHERE   p.posttypeId = 1 "
				+ " and p.closeddate is not null"
				+ " and p.title is not null "
				+ " and p.body is not null ";
		
		
		if(!StringUtils.isBlank(maxCreationDate)) {
			sql+= " and p.creationdate < '"+maxCreationDate+"'";
		}
		
		sql += DupPredictorUtils.getQueryComplementByTag(tagFilter);
		
		sql +=  " and p.id in "
				+ " ( select distinct(pl.postid)"
				+ " from postlinks pl where pl.linktypeid = 3 ) ";
		
		sql += " order by p.creationdate ";
		
		if(limit!=null){
			sql+= " limit "+limit;
		}
		
		Query q = em.createNativeQuery(sql, Question.class);
		
		List<Question> posts = q.getResultList();
				
		logger.info("Posts in findClosedDuplicatedNonMasters: "+posts.size());
		/*for(Posts post:posts){
			DupPredictorUtils.setBlanks(post);
		}*/
		
		
		return posts;
	}
	
	
}
