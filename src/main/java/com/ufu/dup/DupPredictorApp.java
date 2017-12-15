package com.ufu.dup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ufu.dup.repository.ExperimentRepository;
import com.ufu.dup.repository.RecallRateRepository;
import com.ufu.dup.service.DupPredictorService;
import com.ufu.dup.to.Experiment;
import com.ufu.dup.to.PostLink;
import com.ufu.dup.to.Posts;
import com.ufu.dup.to.Question;
import com.ufu.dup.to.RecallRate;
import com.ufu.dup.to.TopicVector;
import com.ufu.util.DupPredictorComposer;
import com.ufu.util.DupPredictorUtils;

@Component
public class DupPredictorApp {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static Map<Integer, Question> allPostsQuestions;
	private List<Question> closedDuplicatedNonMasters;
	private List<Question> toTest;
	private List<PostLink> postsLinks;
	private DecimalFormat decimalFormat;
	
	private Map<Integer, Double> scoresMap;
	private Map<Integer, String> topicVectors;
	private Set<TopicVector> vectorsSet;
	
	protected static SimpleDateFormat dateFormat;
	
	private String PATH_MALLET;
	private String PATH_MALLET_TOPIC;
	private String TOPIC_DISTRIBUTION_FILE_NAME = "topics_duppredictor.txt";
	

	// Arquivo main.properties pode sobrescrever essas variáveis, bastando estar na mesma pasta onde será executado o jar em producao
	@Value("${closedDuplicatedNonMastersLimit}")
	public Integer closedDuplicatedNonMastersLimit; 
	
	@Value("${allQuestionsByFiltersLimit}")
	public Integer allQuestionsByFiltersLimit; // testes
	
	@Value("${calculateRecallRates}")
	public Boolean calculateRecallRates;
	
	@Value("${observation}")
	public String observation;
	
	@Value("${lote}")
	public Integer lote;
	
	@Value("${mallet.dir}")
	public String malletDir;  
	
	
	@Value("${estimateWeights}")
	public Boolean estimateWeights;
	
	@Value("${spring.datasource.url}")
	public String database;
	
	@Value("${maxCreationDate}")
	public String maxCreationDate;
	
	@Value("${tagFilter}")
	public String tagFilter;  //null for all
	
	@Value("${maxResultSize}")
	public  Integer maxResultSize;   //null para máximo possível
	
	@Value("${trainingPercentOfQuestions}")
	public  Integer trainingPercentOfQuestions;   
	
	@Value("${testingPercentOfQuestions}")
	public  Integer testingPercentOfQuestions;  //null para todas 

	@Value("${spring.datasource.url}")
	public String dataSource;  //null for all
	
	
	@Value("${numTopics}")
	public Integer numTopics; 
	
	@Value("${useLDA}")
	public Boolean useLDA;   
	
	@Value("${runMalletCommands}")
	public Boolean runMalletCommands;   //automatically run mallet commands
	
	@Value("${loadVectorsToDB}")
	public Boolean loadVectorsToDB;   //automatically run mallet commands
	
		
	@Value("${buildMalletTopicFiles}")
	public Boolean buildMalletTopicFiles;   
		
	@Autowired
	private DupPredictorUtils dupPredictorUtils;
	
	private long initTime;
	private long generalInitTime;
	private boolean vectorsLoadedToQuestions;

	private long initTimeQuestion;
	private long initTimeLoop; //time to each experiment
	private long endTimeQuestion;
	private long endTimeLoop;
	
		
	@Autowired
	private DupPredictorService dupPredictorService;
	
	@Autowired
	protected ExperimentRepository experimentRepository;
	
	@Autowired
	protected RecallRateRepository recallRateRepository;


	@PostConstruct
	public void init() throws Exception {
		generalInitTime =  System.currentTimeMillis();
		logger.info("Initializing DupPredictor...");
		dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		this.PATH_MALLET = malletDir;
		this.PATH_MALLET_TOPIC = PATH_MALLET + "/topics/";
		decimalFormat = new DecimalFormat("##.##");
		decimalFormat.setRoundingMode(RoundingMode.DOWN);
		vectorsLoadedToQuestions = false;
		
		dupPredictorUtils.initializeConfigs();
		
		logger.info("Setted variables:"
				+"\n database: "+database
				+"\n lote: "+lote
				+"\n malletDir: "+malletDir
				+"\n\n useLDA: "+useLDA
				+"\n numTopicos: "+numTopics
				+"\n buildMalletTopicFiles: "+buildMalletTopicFiles
				+"\n runMalletCommands: "+runMalletCommands
				+"\n loadVectorsToDB: "+loadVectorsToDB
				+"\n\n estimateWeights="+estimateWeights
				+"\n trainingPercentOfQuestions="+trainingPercentOfQuestions
				+"\n\n calculateRecallRates: "+calculateRecallRates
				+"\n observation: "	+observation
				+"\n closedDuplicatedNonMastersLimit: "+closedDuplicatedNonMastersLimit
				+"\n allQuestionsByFiltersLimit: "+allQuestionsByFiltersLimit
				+"\n maxResultSize: "+maxResultSize
				+"\n testingPercentOfQuestions: "+testingPercentOfQuestions
				+"\n maxCreationDate: "+maxCreationDate
				+"\n tagFilter: "+tagFilter);
		
		DupPredictorComposer.setUseLDA(useLDA);
		DupPredictorComposer.setNumTopics(numTopics);
		
		
		if(useLDA){
			if(buildMalletTopicFiles){
				allPostsQuestions  = dupPredictorService.getQuestionsByFilters(tagFilter,allQuestionsByFiltersLimit, maxCreationDate);
				generateMalletFiles();
				
			}
			if(runMalletCommands){
				runMalletCommands();
			}
			
			if(loadVectorsToDB){
				if(allPostsQuestions==null){
					allPostsQuestions  = dupPredictorService.getQuestionsByFilters(tagFilter,allQuestionsByFiltersLimit, maxCreationDate);
				}
				vectorsSet = buildTopicVectors();
				dupPredictorService.deleteAllTopicVectors();
				dupPredictorService.saveTopicVectors(vectorsSet);
			}
			
		}
		
		if(estimateWeights || calculateRecallRates) {
			dupPredictorUtils.generateBuckets();
			postsLinks = dupPredictorUtils.getAllPostLinks();
			
		}
		
		
		if(estimateWeights){
			initTime = System.currentTimeMillis();
				
			Integer intNumberOfTestedQuestions = null;
			if(closedDuplicatedNonMastersLimit!=null){
				float factorOfTestedQuestions = trainingPercentOfQuestions/(float)100;
				float numberOfTestedQuestions = closedDuplicatedNonMastersLimit * factorOfTestedQuestions;
				intNumberOfTestedQuestions = Float.valueOf(numberOfTestedQuestions).intValue();
			}		
			
			logger.info("Estimating weights, limiting to the first "+intNumberOfTestedQuestions+ " questions...");
			
			closedDuplicatedNonMasters = dupPredictorService.findClosedDuplicatedNonMasters(tagFilter,intNumberOfTestedQuestions, maxCreationDate);
			if(allPostsQuestions==null){
				allPostsQuestions  = dupPredictorService.getQuestionsByFilters(tagFilter,allQuestionsByFiltersLimit, maxCreationDate);
			}
			removeNonExistingMasters();
			
			if(useLDA){
				loadTopicVectorsToMap();
				loadVectorsToQuestions();
			}
			
			dupPredictorUtils.generateBuckets();
			estimateWeights();
			
			logger.info("End of estimating weights......");
			dupPredictorUtils.reportElapsedTime(initTime,"estimateWeights()");
		}
		
		if(calculateRecallRates){
			logger.info("Calculating recall-rates...");
			initTime = System.currentTimeMillis();
			List<Question> trainingSet =  new ArrayList<>();
			
			if(closedDuplicatedNonMastersLimit!=null){
				float factorOfTestedQuestions = trainingPercentOfQuestions/(float)100;
				float numberOfTestedQuestions = closedDuplicatedNonMastersLimit * factorOfTestedQuestions;
				Integer intNumberOfTestedQuestions = Float.valueOf(numberOfTestedQuestions).intValue();
				trainingSet = dupPredictorService.findClosedDuplicatedNonMasters(tagFilter,intNumberOfTestedQuestions, maxCreationDate);
			}			 
			
			List<Question> testSet = dupPredictorService.findClosedDuplicatedNonMasters(tagFilter, closedDuplicatedNonMastersLimit, maxCreationDate);
			testSet.removeAll(trainingSet);
			closedDuplicatedNonMasters = new ArrayList<>(testSet);
			
			testSet = null;
			trainingSet = null;
			
			if(allPostsQuestions==null){
				allPostsQuestions  = dupPredictorService.getQuestionsByFilters(tagFilter,allQuestionsByFiltersLimit, maxCreationDate);
			}
			removeNonExistingMasters();
			
			if(useLDA){
				loadTopicVectorsToMap();
				vectorsSet = null; //releasing resources
				loadVectorsToQuestions();
			}
			
			calculateRecallRates();
			dupPredictorUtils.reportElapsedTime(initTime,"calculateRecallRates()");
			logger.info("End of calculateRecallRates...");
		}
					
		
		dupPredictorUtils.reportElapsedTime(generalInitTime,"DupPredictor end");
		logger.info("End of experiment...");
		

	}



	private void removeNonExistingMasters() {	
		logger.info("Verifying masters which does not exists in posts table...");
		//List<Posts> excludingNonMasters = new ArrayList<>();
		List<Question> excludingNonMasters = new ArrayList<>();
		//for(Posts nonMaster: closedDuplicatedNonMasters) {
		for(Question nonMaster: closedDuplicatedNonMasters) {
			Set<Integer> mastersIds = getMastersIdsOfNonMaster(postsLinks,nonMaster); 
			for(Integer masterId: mastersIds) {
				if(allPostsQuestions.get(masterId) == null) {
					logger.info("Master does not exist in allPostsQuestions: "+masterId+ " - removing...");
					excludingNonMasters.add(nonMaster);
				}
			}
			mastersIds = null;
		}
		logger.info("Number of excluded questions because master does not exist in allPostsQuestions: "+excludingNonMasters.size());
		int sizeBefore = closedDuplicatedNonMasters.size();
		closedDuplicatedNonMasters.removeAll(excludingNonMasters);
		excludingNonMasters = null;
		logger.info("Size of closedDuplicatedNonMasters before: "+sizeBefore+ "\nSize after cleaning: "+closedDuplicatedNonMasters.size());
		
	}

	private Set<Integer> getMastersIdsOfNonMaster(List<PostLink> postsLinks,Question nonMaster) {
	//private Set<Integer> getMastersIdsOfNonMaster(List<PostLink> postsLinks,Posts nonMaster) {
		Set<Integer> mastersIds = new HashSet<>();
		for(PostLink postLink: postsLinks) {
			if(postLink.getPostId().equals(nonMaster.getId())) {
				mastersIds.add(postLink.getRelatedPostId());
			}
		}
	
		return mastersIds;
	}
	





	private void loadVectorsToQuestions() {
		if(!vectorsLoadedToQuestions){
			initTime = System.currentTimeMillis();
			logger.info("loading vectors to questions...");
			vectorsLoadedToQuestions = true;
			
			for(Question nonMaster: closedDuplicatedNonMasters){
			//for(Posts nonMaster: closedDuplicatedNonMasters){
				nonMaster.setTopicVectors(topicVectors.get(nonMaster.getId()));
			}
			for (Map.Entry<Integer, Question> entry : allPostsQuestions.entrySet()) {
				entry.getValue().setTopicVectors(topicVectors.get(entry.getKey()));
			}
			
		}
		
		logger.info("loading vectors to questions done !");
		dupPredictorUtils.reportElapsedTime(initTime,"loadVectorsToQuestions()");
	}







	private void loadTopicVectorsToMap() {
		if(vectorsSet==null){
			logger.info("loading vectorsSet from db...");
			vectorsSet = dupPredictorService.getTopicVectors();
			logger.info("loading vectorsSet from db done ! "+vectorsSet.size()+ " questions and vectors");
		}
		if(topicVectors==null){
			topicVectors = new LinkedHashMap();
			logger.info("loading vectorsSet to vectorsMap ...");
			for(TopicVector topicVector: vectorsSet){
				topicVectors.put(topicVector.getPostid(), topicVector.getVectors());
			}
			logger.info("loading vectorsSet to vectorsMap done!");
		}
				
	}







	private void generateMalletFiles() throws IOException {
		FileUtils.deleteDirectory(new File(PATH_MALLET_TOPIC));
		File dir4 = new File(PATH_MALLET_TOPIC);
		dir4.mkdirs();
		
		
		int count = 1;
		int total = allPostsQuestions.size();
		Integer id;
		Question question;
		String fileName;
		FileWriter writer;
		
		for (Map.Entry<Integer, Question> pair : allPostsQuestions.entrySet()) {
			id = pair.getKey();
			question = pair.getValue();
			
			fileName = PATH_MALLET_TOPIC + "questionId-" + question.getId() + "-topic.txt";
			writer = new FileWriter(new File(fileName));
			writer.write(question.getTitle() + " " + question.getBody());
			writer.flush();
			writer.close();
			writer = null;
			
			if (count % 250000 == 0) {
				logger.info("Files generated for question : " + count + " of " + total);
			}
			
			count++;
		}		
		
	}


	

	private void runMalletCommands() throws IOException, InterruptedException {

		String[] malletImportDirCommands = { "bin/mallet", "import-dir", "--input", "topics", "--output", "topic.mallet", "--keep-sequence" };
		String[] malletTrainCommands = { "bin/mallet", "train-topics", "--input", "topic.mallet", "--num-topics", numTopics.toString(), "--output-state", "topic-state.gz", "--output-topic-keys", "topics_keys.txt", "--output-doc-topics", TOPIC_DISTRIBUTION_FILE_NAME };
		ProcessBuilder processBuilder1 = new ProcessBuilder(malletImportDirCommands);
		processBuilder1.directory(new File(PATH_MALLET));

		ProcessBuilder processBuilder2 = new ProcessBuilder(malletTrainCommands);
		processBuilder2.directory(new File(PATH_MALLET));

		Process p, p2 = null;
		logger.info("Executing mallet: building model....");
		p = processBuilder1.start();
		p.waitFor();

		logger.info("Executing mallet: training topics....");
		p2 = processBuilder2.start();
		p2.waitFor();

	}

		
	public Set<TopicVector> buildTopicVectors() throws Exception {
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		Set<TopicVector> topicVectors = new LinkedHashSet();
		
		try {
			logger.info("Building vectors to all questions - reading files and saving vectors in strings");

			File file = new File(PATH_MALLET +"/"+ TOPIC_DISTRIBUTION_FILE_NAME);
			// int count;
			Integer id = null;
			String line;
			String vectors = "";
			Question question;
						
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			String parts[] = null;
			while ((line = bufferedReader.readLine()) != null) {
				Pattern pattern = Pattern.compile("questionId-(.*?)-");
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					try {
						id = new Integer(matcher.group(1));
						parts = line.split("-topic.txt\t");
						parts[1] = parts[1].replace("\t", " ");
						
						if (parts.length == 2) {
							vectors = parts[1];
						} else {
							logger.warn("Warning... Empty vector for id: " + id);
						}
								
					} catch (Exception e) {
						logger.error("Error :Id: " + id + ": " + vectors + " --\n" + "\n" + e.getMessage() + "\n");
						e.printStackTrace();
						throw e;
					}

					question = allPostsQuestions.get(id);
					if (question != null) {
						//question.setTopicVectors(vectors);
						topicVectors.add(new TopicVector(id,vectors));
					} else {
						logger.info("Question not found in memory ! ...: " + line+ " -id: "+id);
					}
				} else {
					throw new Exception("Id not found....: " + line+ " -id: "+id);
				}

			}

			reduceVectors(topicVectors);

		} catch (Exception e) {
			logger.error("Erro em geraVetoresTodasQuestoes: " + e.getMessage() + " --- \n" + e.getStackTrace());
			e.printStackTrace();
			throw e;
		} finally {
			bufferedReader.close();
			fileReader.close();
		}
		
		return topicVectors;

	}


	public static void reduceVectors(Set<TopicVector> topicVectors) {
		for(TopicVector topicVector: topicVectors) {
			String vectors = topicVector.getVectors();
			String reducedVectors = "";
			
			StringTokenizer defaultTokenizer = new StringTokenizer(vectors);
			while (defaultTokenizer.hasMoreTokens()) {
				String token = defaultTokenizer.nextToken();
				BigDecimal d = new BigDecimal(token);
				token = d.toPlainString();
				int length = 7;
				if(token.length()<7){
					length = token.length();
				}
				reducedVectors+= token.substring(0,length)+ " ";
				d = null;
				token = null;
			}
			topicVector.setVectors(reducedVectors);
			
		}
		
		
	}








	static <K, V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K, V> map) {

		List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());

		Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> e1, Entry<K, V> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		return sortedEntries;
	}

	
	
	private RecallRate calculateRecallRates() {
		//BufferedWriter bw = null;
		
		try {
			//Posts questaoA = null;
			Question questaoB = null;
			Integer questaoBId = null;
			int questionCount=0;
			int numTest=0;
			String duration, timeMessage;
			double cos = 0d;
			int listSize= 0;
			int consideredCropNumber = 0;
			
			initTimeLoop = System.currentTimeMillis();
						
			int hitsMaxSumCosines=0, hits10000SumCosines=0,hits1000SumCosines=0,hits500SumCosines=0, hits100SumCosines=0, hits50SumCosines=0, hits20SumCosines = 0, hits10SumCosines = 0, hits5SumCosines=0,hits1SumCosines=0 ,hitOrderSumCosines = 0;
			double recallRateMaxSumCosines = 0d, recallRate10000SumCosines = 0d, recallRate1000SumCosines = 0d, recallRate500SumCosines = 0d,
					   recallRate100SumCosines = 0d, recallRate50SumCosines = 0d, recallRate20SumCosines = 0d, recallRate10SumCosines = 0d,
					   recallRate5SumCosines = 0d, recallRate1SumCosines = 0d;
			
			
			float divNumber = 100/(float)testingPercentOfQuestions;
			
			//toTest = new ArrayList<Posts>();
			toTest = new ArrayList<Question>();
			
			for(int i=0; i< closedDuplicatedNonMasters.size();i++){
				if(i%divNumber==0){
					toTest.add(closedDuplicatedNonMasters.get(i));
				}
			}
			
			Integer totalDuplicatedForTest = toTest.size();
			
			
			if(!estimateWeights){
				logger.info("Initializing recall rates calculation... \nPercent of questions to test: "+testingPercentOfQuestions+"\nNumber of questions to test:"+totalDuplicatedForTest+"\nTotal of closed non masters: " +toTest.size()+"\nallQuestions size:"+allPostsQuestions.size());
			}			
			
			for (Question nonMaster : toTest) {
				initTimeQuestion = System.currentTimeMillis();
				questionCount++;
				
				if(!estimateWeights){
					logger.info("Lote: "+lote+" - Testing question: "+questionCount+ " of "+totalDuplicatedForTest + " - Questão id: "+nonMaster.getId());
				}
				
				scoresMap = new HashMap<Integer, Double>();
				
				// Compara essa questão com todas as outras, gerando para cada par, um score.
				// Armazena esse score para essa iteração
				for (Map.Entry<Integer, Question> entry : allPostsQuestions.entrySet()) {
					try {
						//questaoB = dupPredictorService.findPostById(questaoBId);
						questaoB = entry.getValue();
						questaoBId = questaoB.getId();
												
						if(!nonMaster.getId().equals(questaoBId)){  //não compara ela com ela mesma
							cos	= DupPredictorComposer.calculateSimilarity(nonMaster, questaoB);
							scoresMap.put(questaoBId, cos);
							
							/*if(numTest%500000==0){
								logger.info("Similaridades calculadas para "+ numTest + " questões de "+allPostsQuestions.size());
							}*/
						}
					} catch (Exception e) {
						//logger.error(e.getMessage());
						logger.error("Iteração i:"+questionCount+" -j:"+numTest+" - Questão A: "+nonMaster.getId()+ "- Question B: "+questaoB.getId());
						throw e;
					}
					numTest++;

				}
				numTest=0;								
				// order as questões por score desc
				List<Entry<Integer, Double>> descSortedEntries = entriesSortedByValues(scoresMap);

				// gera sub lista com primeiros recallK elementos
				List<Entry<Integer, Double>> subList20 = null;
				try {
					
					listSize = descSortedEntries.size(); 
					consideredCropNumber = maxResultSize;
					if(consideredCropNumber>listSize){
						consideredCropNumber = listSize;
					}
					subList20 = new ArrayList<Entry<Integer, Double>>(descSortedEntries.subList(0, consideredCropNumber));
									
				} catch (Exception e) {
					logger.error("Erro ao gerar sublista.Iteracao i: "+questionCount+" - Tamanho da ordenada: "+descSortedEntries.size()+" -Tamanho da original: "+scoresMap.size());
					throw e;
				}
				
				// verifica se as primeiras recallK contém a questão duplicada da questão questaoAId. Se sim, hit++
				//hitOrder = verificaHitK(nonMaster.getId(), subList20, bw);
				hitOrderSumCosines = verificaHitKemMemoria(nonMaster.getId(), subList20);
				subList20 = null;
				descSortedEntries = null;
				
				if(hitOrderSumCosines>-1){
					if(hitOrderSumCosines<=10000){
						hits10000SumCosines++;
					}
					if(hitOrderSumCosines<=1000){
						hits1000SumCosines++;
					}
					if(hitOrderSumCosines<=500){
						hits500SumCosines++;
					}
					if(hitOrderSumCosines<=100){
						hits100SumCosines++;
					}
					if(hitOrderSumCosines<=50){
						hits50SumCosines++;
					}
					if(hitOrderSumCosines<=20){
						hits20SumCosines++;
					}
					if(hitOrderSumCosines<=10){
						hits10SumCosines++;
					}
					if(hitOrderSumCosines<=5){
						hits5SumCosines++;
					}
					if(hitOrderSumCosines==1){
						hits1SumCosines++;
					}
				}
				
				recallRateMaxSumCosines = hitsMaxSumCosines*100 / (double)questionCount;
				//recallRate100000SumCosines = hits100000SumCosines*100 / (double)questionCount;
				recallRate10000SumCosines = hits10000SumCosines*100 / (double)questionCount;
				recallRate1000SumCosines = hits1000SumCosines*100 / (double)questionCount;
				recallRate500SumCosines = hits500SumCosines*100 / (double)questionCount;
				recallRate100SumCosines = hits100SumCosines*100 / (double)questionCount;
				recallRate50SumCosines = hits50SumCosines*100 / (double)questionCount;
				recallRate20SumCosines = hits20SumCosines*100 / (double)questionCount;
				recallRate10SumCosines = hits10SumCosines*100 / (double)questionCount;
				recallRate5SumCosines = hits5SumCosines*100 / (double)questionCount;
				recallRate1SumCosines = hits1SumCosines*100 / (double)questionCount;
				
				String recallRateMsgSumCosines = "\nResults for Sum of Cosines: "+ questionCount+ " of  "+totalDuplicatedForTest
					//	+ "\nHits 100000:  "+hits100000SumCosines + " - Recall rate 10000:  "+ df.format(recallRate100000SumCosines)
						+ "\nHits 10000:  "+hits10000SumCosines + " - Recall rate 10000:  "+ decimalFormat.format(recallRate10000SumCosines)
						+ "\nHits 1000:  "+hits1000SumCosines + " - Recall rate 1000:  "+ decimalFormat.format(recallRate1000SumCosines)
						+ "\nHits 100:  "+hits100SumCosines + " - Recall rate 100:  "+ decimalFormat.format(recallRate100SumCosines)
						+ "\nHits 50:  "+hits50SumCosines + " - Recall rate 50:  "+ decimalFormat.format(recallRate50SumCosines)
						+ "\nHits 20:  "+hits20SumCosines + " - Recall rate 20:  "+ decimalFormat.format(recallRate20SumCosines)
						+ "\nHits 10  "+hits10SumCosines + " - Recall rate 10:  "+ decimalFormat.format(recallRate10SumCosines)
						+ "\nHits 5:  "+hits5SumCosines + " - Recall rate 5:  "+ decimalFormat.format(recallRate5SumCosines)
						+ "\nHits 1  "+hits1SumCosines + " - Recall rate 1:  "+ decimalFormat.format(recallRate1SumCosines);
						
				endTimeQuestion = System.currentTimeMillis();
				duration = DurationFormatUtils.formatDuration(endTimeQuestion-initTimeQuestion, "HH:mm:ss,SSS");
				timeMessage = "\nTime to calculate recall-rates for question "+questionCount+ ": "+duration;
							
				if(!estimateWeights){
					logger.info(timeMessage+recallRateMsgSumCosines);
				}else{
					logger.info(timeMessage);
				}
				
				recallRateMsgSumCosines = null;
				scoresMap = null;				
				
			}
			
			endTimeLoop = System.currentTimeMillis();
			duration = DurationFormatUtils.formatDuration(endTimeLoop-initTimeLoop, "HH:mm:ss,SSS");
			if(!estimateWeights){
				logger.info("Lote: "+lote+" - Tempo para cálculo dos recall-rates para experimento "+0+ ": "+duration);
			}
			
			
			//Grava resultados
			Experiment experiment = new Experiment();
			experiment.setDate(dateFormat.format(new Timestamp(Calendar.getInstance().getTimeInMillis())));
			experiment.setNumberOfTestedQuestions(totalDuplicatedForTest);
			experiment.setTtWeight(DupPredictorComposer.getAlpha());
			experiment.setBbWeight(DupPredictorComposer.getBeta());
			experiment.setTopicTopicWeight(DupPredictorComposer.getGama());
			experiment.setTagTagWeight(DupPredictorComposer.getDelta());
			experiment.setObservacao(observation);
			experiment.setLote(lote);
			experiment.setBase(DupPredictorUtils.getDataBase(database));
			experiment.setDuration(duration);
			experiment.setTag(tagFilter==null?"all":tagFilter);
			experiment.setMaxresultsize(maxResultSize);
			experiment.setApp("DupPredictor");
			experiment.setEstimateWeights(estimateWeights);
			experimentRepository.save(experiment);
						
			
			RecallRate recallRate = new RecallRate();
			recallRate.setExperimentId(experiment.getId());
			recallRate.setOrigem("Sum of Cosines");
			recallRate.setHits50000(hitsMaxSumCosines);
			recallRate.setHits10000(hits10000SumCosines);
			recallRate.setHits1000(hits1000SumCosines);
			recallRate.setHits100(hits100SumCosines);
			recallRate.setHits50(hits50SumCosines);
			recallRate.setHits20(hits20SumCosines);
			recallRate.setHits10(hits10SumCosines);
			recallRate.setHits5(hits5SumCosines);
			recallRate.setHits1(hits1SumCosines);
			recallRate.setRecallrate_50000(new Double(decimalFormat.format(recallRateMaxSumCosines)));
			recallRate.setRecallrate_10000(new Double(decimalFormat.format(recallRate10000SumCosines)));
			recallRate.setRecallrate_1000(new Double(decimalFormat.format(recallRate1000SumCosines)));
			recallRate.setRecallrate_100(new Double(decimalFormat.format(recallRate100SumCosines)));
			recallRate.setRecallrate_50(new Double(decimalFormat.format(recallRate50SumCosines)));
			recallRate.setRecallrate_20(new Double(decimalFormat.format(recallRate20SumCosines)));
			recallRate.setRecallrate_10(new Double(decimalFormat.format(recallRate10SumCosines)));
			recallRate.setRecallrate_5(new Double(decimalFormat.format(recallRate5SumCosines)));
			recallRate.setRecallrate_1(new Double(decimalFormat.format(recallRate1SumCosines)));
			
			recallRateRepository.save(recallRate);
			toTest = null;
			return recallRate;

		} catch (Exception ex) {
			logger.error("Erro ao gerar recall rate");
			ex.printStackTrace();
		}
		return null;

	}

	
	
	private <K, V extends Comparable<? super V>> Integer verificaHitKemMemoria(Integer nonMasterId, List<Entry<K, V>> subList) throws IOException {
		int questionCount = 0;
		for (Entry<K, V> entry : subList) {
			questionCount++;
			Integer testingId = (Integer)entry.getKey();
			
			Set<Integer> nonMasters = dupPredictorUtils.getBucketDuplicatiosMap().get(testingId);
			if(nonMasters!=null && !nonMasters.isEmpty()){
				for(Integer nonMasterTestId: nonMasters){
					 
					if(nonMasterTestId.equals(nonMasterId)){
						String found = "\nEncontrada duplicada(s) - dup id: " + nonMasterId + " - Master: "+testingId+ " - posição: "+questionCount;
						if(!estimateWeights){
							logger.info(found);
						}
						return questionCount;
					}					
			}
		 }
			nonMasters = null;
		}

		return -1;
	}
	

	
	public void estimateWeights() throws Exception {
		int iter = 100;
		double alpha;
		double beta;
		double gamma;
		double delta;
		
		initTime = System.currentTimeMillis();
		
		for(int count=0; count<iter; count++){
			initTime = System.currentTimeMillis();
			
			boolean validSequence = false;
			
			do {
				
				alpha = DupPredictorUtils.redondear(Math.random(),2); //only 2 decimal places
				beta = DupPredictorUtils.redondear(Math.random(),2); 
				gamma = DupPredictorUtils.redondear(Math.random(),2);
				delta = DupPredictorUtils.redondear(Math.random(),2);
				
				validSequence = ( (alpha > beta) && (beta > delta) && (delta > gamma)); 
				
			} while (!validSequence);
			
			/*alpha = 0.8;
			beta = 0.51;
			gamma = 0.01;
			delta  = 0.37;*/
			DupPredictorComposer.setScores(alpha,beta, gamma,delta);
			//DupPredictorComposer.setScores(alpha,beta, gamma,delta);
			logger.info("Calculating recall-rates for iter: "+count+ " of "+iter+ "  with weights: "+alpha + " - "+ beta + " - "+gamma+ " - "+delta);
			
			
			calculateRecallRates();
			
			logger.info("\n\n\nEnd of iter "+count);
			dupPredictorUtils.reportElapsedTime(initTime,"estimateWeights iter: "+count);
			
		}
		
		
	}




}
