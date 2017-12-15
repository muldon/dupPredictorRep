package com.ufu;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ufu.dup.repository.ExperimentRepository;
import com.ufu.dup.repository.RecallRateRepository;
import com.ufu.dup.service.DupPredictorService;
import com.ufu.dup.to.Experiment;
import com.ufu.dup.to.PostLink;
import com.ufu.dup.to.Posts;
import com.ufu.dup.to.RecallRate;
import com.ufu.dup.to.TopicVector;
import com.ufu.util.CosineSimilarity;
import com.ufu.util.DupPredictorComposer;
import com.ufu.util.DupPredictorUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class DupePredictorApplicationTests {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
		
	@Autowired
	private DupPredictorService dupPredictorService;
	
	@Autowired
	protected ExperimentRepository experimentRepository;
	
	@Autowired
	protected RecallRateRepository recallRateRepository;
	
	@Autowired
	private DupPredictorUtils dupPredictorUtils;
	
	private List<Posts> closedDuplicatedNonMasters;
	
	private static Map<Integer, Posts> allPostsQuestions;
	
	protected static SimpleDateFormat dateFormat;
	
	{
		dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	}
	/*@Test
	public void contextLoads() {
	}*/
	
	//@Test
	/*public void runExperiment() throws Exception{
		DupPredictorApp app = new DupPredictorApp();
		app.runExperiment();
	}*/

	
	
	
		
	 
	
	//@Test
	public void testCossineSimilarity(){
		/*double[] vec1 = transformFromStringToVectorOfDoubles("0:0.8427009716003842 1:0.12038585308576916 2:0.12038585308576916 3:0.12038585308576916 4:0.12038585308576916 5:0.12038585308576916 6:0.12038585308576916 7:0.12038585308576916 8:0.12038585308576916 9:0.12038585308576916 10:0.12038585308576916 11:0.12038585308576916 12:0.2407717061715383 13:0.12038585308576916 14:0.12038585308576916 15:0.12038585308576916 16:0.12038585308576916 17:0.12038585308576916",18);
		double[] vec2 = transformFromStringToVectorOfDoubles("0:0.8427009716003842 1:0.12038585308576916 2:0.12038585308576916 3:0.12038585308576916 4:0.12038585308576916 5:0.12038585308576916 6:0.12038585308576916 7:0.12038585308576916 8:0.12038585308576916 9:0.12038585308576916 10:0.12038585308576916 11:0.12038585308576916 12:0.2407717061715383 13:0.12038585308576916 14:0.12038585308576916 15:0.12038585308576916 16:0.12038585308576916 17:0.12038585308576916",18);
		
		double cosSim = CosineSimilarity.cosineSimilarity(vec1, vec2);
		System.out.println(cosSim);*/
		
		
		
		
		//Map<Integer, Posts> questions = DupPredictorApp.allPosts;
		//List<Posts> questions = dupPredictorService.getQuestionsByFiltersOrderByIdDesc(tagName,limit);
		
		/*for (Map.Entry<Integer, Posts> pair : questions.entrySet()) {
			logger.info(pair.getValue().toString());
		}*/
		/*Posts question1 = questions.get(995918);
		Posts question2 = questions.get(513832);
*/
		//logger.info("Similaridade: "+DupPredictorComposer.calculaSimilaridade(question1,question2));

		
	}
	
	//@Test
	public void getQuestionsByFilters() throws Exception{
		
		
		try {
			String tagName = "<java>";
			Integer limit = 1000;
			
			//Map<Integer, Posts> questions = dupPredictorService.getQuestionsByFiltersOrderByIdDescTeste(DupPredictorApp.tagFilter,DupPredictorApp.POSTS_LIMIT);
			
			/*for (Map.Entry<Integer, Posts> pair : questions.entrySet()) {
				if(pair.getValue().get)
				logger.info(pair.getValue().toString());
			}*/
		} catch (Exception e) {
			logger.error("Errorrr "+e.getMessage());
		}
		
	}
	
	
	//@Test
	public void testExperimentInsert() {
		
		Experiment experiment = new Experiment();
		experiment.setDate(dateFormat.format(new Timestamp(Calendar.getInstance().getTimeInMillis())));
		experiment.setNumberOfTestedQuestions(10);
		experiment.setTtWeight(DupPredictorComposer.getAlpha());
		experiment.setBbWeight(DupPredictorComposer.getBeta());
		experiment.setTagTagWeight(DupPredictorComposer.getDelta());
		experiment.setObservacao("testtttt");
		experiment.setLote(1);
		experiment.setBase("test");
		experiment.setDuration("testtt");
		experimentRepository.save(experiment);
					
		
		RecallRate recallRate = new RecallRate();
		recallRate.setExperimentId(experiment.getId());
		recallRate.setOrigem("Sum of Cosines");
		recallRate.setHits50000(10);
				
		recallRateRepository.save(recallRate);

	}
	
	
	
	//@Test
	public void testTopicVectors() throws Exception {
		/*Posts postA = dupPredictorService.findPostById(70714);
		Posts postB = dupPredictorService.findPostById(355934);
		*/
		TopicVector topicA = dupPredictorService.findTopicVectorById(73713);
		TopicVector topicB = dupPredictorService.findTopicVectorById(48935);
		DupPredictorComposer.setNumTopics(100);
		double topicSim = CosineSimilarity.cosineSimilarity(DupPredictorComposer.transformFromStringToVectorOfDoublesTopics(topicA.getVectors()), DupPredictorComposer.transformFromStringToVectorOfDoublesTopics(topicB.getVectors())); 
		System.out.println(topicSim);
	}	
	
	
	
	//@Test
	/*public void verifyIfMastersAreInPosts() throws Exception {
		
		dupPredictorUtils.generateBuckets();
		
		List<PostLink> postsLinks = dupPredictorUtils.getAllPostLinks();
		
		allPostsQuestions  = dupPredictorService.getQuestionsByFilters(null,null, "2011-10-01");
		
		closedDuplicatedNonMasters = dupPredictorService.findClosedDuplicatedNonMasters(null,1528, "2011-10-01");
				 
		
		for(Posts nonMaster: closedDuplicatedNonMasters) {
			
			List<Integer> mastersIds = getMastersIdsOfNonMaster(postsLinks,nonMaster); 
			
			for(Integer masterId: mastersIds) {
				if(allPostsQuestions.get(masterId) == null) {
					System.out.println("Master does not exist in allPostsQuestions: "+masterId);
				}
			}
			
		}
		
		

	}*/

	private List<Integer> getMastersIdsOfNonMaster(List<PostLink> postsLinks,Posts nonMaster) {
		List<Integer> mastersIds = new ArrayList<>();
		for(PostLink postLink: postsLinks) {
			if(postLink.getPostId().equals(nonMaster.getId())) {
				mastersIds.add(postLink.getRelatedPostId());
			}
		}
	
		return mastersIds;
	}
	
	

}
