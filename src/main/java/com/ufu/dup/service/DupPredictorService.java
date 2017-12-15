package com.ufu.dup.service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufu.dup.repository.GenericRepository;
import com.ufu.dup.repository.PostsRepository;
import com.ufu.dup.repository.TopicVectorRepository;
import com.ufu.dup.to.PostLink;
import com.ufu.dup.to.Posts;
import com.ufu.dup.to.Question;
import com.ufu.dup.to.TopicVector;
import com.ufu.util.DupPredictorUtils;


@Service
@Transactional
public class DupPredictorService {
	@Autowired
	protected PostsRepository postsRepository;
	
	@Autowired
	protected TopicVectorRepository topicVectorRepository;
	
	
	@Autowired
	protected GenericRepository genericRepository;
	
	
	@Autowired
	private DupPredictorUtils dupPredictorUtils;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	
	
	protected Timestamp getCurrentDate(){
		Timestamp ts_now = new Timestamp(Calendar.getInstance().getTimeInMillis());
		return ts_now;
	}


	
	@Transactional(readOnly = true)
	public Map<Integer, Question> getQuestionsByFilters(String tagFilter,Integer limit, String maxCreationDate) {
		return genericRepository.getQuestionsByFilters(tagFilter,limit,maxCreationDate);
	}
	
	

	@Transactional(readOnly = true)
	public Posts findPostById(Integer id) {
		return postsRepository.findOne(id);
	}

	

	@Transactional(readOnly = true)
	public List<PostLink> getAllPostLinks() {
		return genericRepository.getAllPostLinks();
	}

	/*public Map<Integer, Posts> stemStop() throws Exception {
		logger.info("preProcess stemStop...");
		logger.info("Retrieving all posts question... ");
		Map<Integer, Posts> allQuestions  = genericRepository.getQuestionsByFilters(null);
	
		logger.info("all questions:  "+allQuestions.size());
	
		for (Map.Entry<Integer, Posts> postPair : allQuestions.entrySet()) {
			Posts question = postPair.getValue();
			
			
			String title = question.getTitle();
			String body = question.getBody();
			
			String tags = question.getTags().replaceAll("<","");
			tags = tags.replaceAll(">"," ");
			question.setTags(tags);
			
			
			String[] titleContent = dupPredictorUtils.separaSomentePalavrasNaoSomentePalavras(title,question,"title");
			//[0] = stoppedStemmed;
			//[1] = naoSomentePalavras;
			//[2] = codeContent;
			question.setTitle(titleContent[0] + " "+ titleContent[1]);
			
			String[] bodyContent = dupPredictorUtils.separaSomentePalavrasNaoSomentePalavras(body,question,"body");
			question.setBody(bodyContent[0] + " "+ bodyContent[1]);
			question.setCode(bodyContent[2]);
			question.setErrormessages(bodyContent[3]);
			//String[] tagsContent = dupeUtils.separaSomentePalavrasNaoSomentePalavrasTags(question.getTags(),question,"tag");
			//question.setTags(dupeUtils.separaTags(question.getTags()));
			
			if(retiraTodosSimbolosTitleBodyTags){ //precisa retirar todos os símbolos
				tags = dupeUtils.retiraSimbolosEspeciais(tags);
				question.setTags(tags);
			}
			
			question.setTitle(dupPredictorUtils.tokenizeStopStem(question.getTitle()));
			question.setBody(dupPredictorUtils.tokenizeStopStem(question.getBody()));
						
			
					
		}
		
		return allQuestions;
	}*/
	
	
	
	
	@Transactional(readOnly = true)
	public List<Question> findClosedDuplicatedNonMasters(String tagFilter, Integer postsLimit, String maxCreationDate) throws Exception {
	//public List<Posts> findClosedDuplicatedNonMasters(String tagFilter, Integer postsLimit, String maxCreationDate) throws Exception {
				
		logger.info("fetching closed duplicated non master questions... ");
		List<Question> closedDuplicatedNonMasters = genericRepository.findClosedDuplicatedNonMasters(tagFilter,postsLimit, maxCreationDate);
		logger.info("closed duplicated non master questions: "+closedDuplicatedNonMasters.size());
			
		return closedDuplicatedNonMasters;
	}

	


	public void saveTopicVectors(Set<TopicVector> topicVectors) {
		logger.info("saving topic vectors to database...");
		for(TopicVector topicVector: topicVectors){
			topicVectorRepository.save(topicVector);
		}
		logger.info("saving topic vectors to database done !");
		
	}



	public Set<TopicVector> getTopicVectors() {
		Set<TopicVector> topicSet = new HashSet((List<TopicVector>)topicVectorRepository.findAll());
		return topicSet;
		
	}



	public void deleteAllTopicVectors() {
		logger.info("deleting all topic vectors ...");
		topicVectorRepository.deleteAll();
		logger.info("deleting all topic vectors done !");
	}



	public TopicVector findTopicVectorById(Integer postId) {
		return topicVectorRepository.findOne(postId);
	}

	
	


	
	
}
