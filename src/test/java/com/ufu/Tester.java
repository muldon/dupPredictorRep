package com.ufu;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.ufu.dup.DupPredictorApp;
import com.ufu.dup.to.Posts;
import com.ufu.dup.to.TopicVector;
import com.ufu.util.CosineSimilarity;
import com.ufu.util.DupPredictorComposer;
import com.ufu.util.DupPredictorUtils;

public class Tester {
	public static final String ONLY_WORDS_EXPRESSION = "(?<!\\S)\\p{Alpha}+(?!\\S)";
	public static final Pattern ONLY_WORDS_PATTERN = Pattern.compile(ONLY_WORDS_EXPRESSION, Pattern.DOTALL);
	
	
	
	public static void main(String[] args) throws Exception {
		
		//Tester t = new Tester();
		//ResearchStringUtils.getSimilarityScore("Java comparison with == of two strings is false?", "How do I compare strings in Java?", "vsm");
		
		String string1 = "Java comparison with == of two strings is false? meetings [duplicate] + - && || ! ( ) { } [ ] ^ \" <good> <code> ~ * ? : \\";
		String string2 = "How do I compare strings in Java?";
		
		String cut = string2.replaceAll("\\[duplicate\\]", "");		
		System.out.println(string2);
		//System.out.println(ResearchStringUtils.removeStopWordsAndPerformSteeming(string1));
		//System.out.println(ResearchStringUtils.removeStopWordsAndPerformSteeming(string2,"vsm"));
		
		//String PATH = "/home/rodrigo/Dropbox/Doutorado/Projeto/wvtool-1.1/examples/data/";
		//String[] fileNames = {PATH+"Text1/text1.txt",PATH+"Text2/text2.txt"};
		
		//MyPorterStemmer.performStemming(fileNames);
		//MyPorterStemmer2 stemmer = new MyPorterStemmer2();
		//System.out.println(stemmer.stem("Java comparison with == of two strings is meetings false?"));
		
		//String mydata = "question-42749347-title.txt; 0:0.5 1:0.5 2:0.5 3:0.5";
		/*String mydata = "0	file:/home/rodrigo/Programs/mallet-2.0.8/topics/questionId-42751116-topic.txt	0.007886435331230283	0.0015772870662460567	0.0015772870662460567	0.8406940063091483	0.017350157728706624	0.0015772870662460567	0.02050473186119874	0.061514195583596214	0.026813880126182965	0.02050473186119874";
		Pattern pattern = Pattern.compile("questionId-(.*?)-");
		Matcher matcher = pattern.matcher(mydata);
		if (matcher.find())
		{
		    System.out.println(matcher.group(1));
		    String parts[] = mydata.split("-topic.txt\t");
		    System.out.println(parts[0]);
		    System.out.println(parts[1]);
		}*/
		/*StringTokenizer defaultTokenizer = new StringTokenizer(mydata);
		System.out.println("Total number of tokens found : " + defaultTokenizer.countTokens());
		while (defaultTokenizer.hasMoreTokens())
		{
		    System.out.println(defaultTokenizer.nextToken());
		}
		System.out.println("Total number of tokens found : " + defaultTokenizer.countTokens());*/
		
		Map<Integer, Posts> allQuestions = new HashMap<Integer, Posts>();
		
		Posts post1 = new Posts(1);
		post1.setTitle("teste 1");
		//post1.setTitleVectors(new double[]{1,2,3});
		
		Posts post2 = new Posts(2);
		post2.setTitle("teste 2");
		//post2.setTitleVectors(new double[]{1,2,3});
		
		allQuestions.put(1, post1);
		allQuestions.put(2, post2);
		
		Map<Integer, Posts> allQuestionsExceptThis = new HashMap<Integer, Posts>(allQuestions);
		allQuestionsExceptThis.remove(1);
		//allQuestions = null;
		//questions.get(1).setTitle("teste 111111111");
		/*List<Integer> integersA = new ArrayList<Integer>();
		integersA.add(1);integersA.add(1);integersA.add(1);
		
		DupPredictorApp.setNonMastersDuplicatedPostsIds(integersA);
		integersA = null;
		System.out.println(DupPredictorApp.getNonMastersDuplicatedPostsIds());*/
		
		/*for (Map.Entry<Integer, Posts> pair : allQuestionsExceptThis.entrySet()) {
			System.out.println(pair.getValue());
		}
		for (Map.Entry<Integer, Posts> pair : allQuestions.entrySet()) {
			System.out.println(pair.getValue());
		} */
		
		/*Integer hits = 2;
		Integer ntotal = 1000;
		double recall = hits / (double)ntotal;
		System.out.println(recall);*/
		/*int vecSize = 1505;
		double[] vector2 = new double[vecSize];
		
		String vectors2 = "40:0.13367934679956575 41:0.03819409908559022 42:0.03819409908559022 43:0.07638819817118044 44:0.01909704954279511 45:0.07638819817118044 46:0.01909704954279511 47:0.01909704954279511";
		
		
		StringTokenizer defaultTokenizer = new StringTokenizer(vectors2);
		System.out.println("Total number of tokens found : " + defaultTokenizer.countTokens());
		while (defaultTokenizer.hasMoreTokens())
		{
			String parts[] = defaultTokenizer.nextToken().split(":");
			vector2[Integer.valueOf(parts[0])] = Double.valueOf(parts[1]);
		}
		System.out.println();*/
		
		
		/*double[] vectorDouble = new double[10];
		
		String vectors2 = "0.0027624309392265192	0.17403314917127072	0.20165745856353592	0.0027624309392265192	0.48342541436464087	0.0027624309392265192	0.0027624309392265192	0.058011049723756904	0.058011049723756904	0.013812154696132596";
		vectors2 = "";
		StringTokenizer defaultTokenizer = new StringTokenizer(vectors2);
		System.out.println("Total number of tokens found : " + defaultTokenizer.countTokens());
		int pos = 0;
		while (defaultTokenizer.hasMoreTokens())
		{
			vectorDouble[pos] = Double.valueOf(defaultTokenizer.nextToken());
			pos++;
		}
		System.out.println();
		
		boolean bol = ((2000000 % 1000000) == 0);
		System.out.println(bol);*/
		
		//WVToolExample wvtool = new WVToolExample();
		//wvtool.runWVTool("./data/duppredictor/tags/", "tags_wordlist", "wv_tag","tag");
		
		/*StopWatch stopWatch = new StopWatch();
		stopWatch.start("parte 1");
		WVToolExample wvtool = new WVToolExample();
		wvtool.runWVTool("./data/duppredictor/tags/", "tags_wordlist", "wv_tag","tag");
		
		stopWatch.stop();
	    System.out.println(stopWatch.getTotalTimeMillis());
	    
	    stopWatch.start("parte 2");
	    Thread.sleep(1000);
	    stopWatch.stop();
	    System.out.println(stopWatch.prettyPrint());*/
		
		

		
		//int vectorSize = DupPredictorApp.countLines(DupPredictorApp.PATH + "/tags_bow");
		//System.out.println(vectorSize);
		DupPredictorApp dup = new DupPredictorApp();
		//dup.generateTagsWV(DupPredictorApp.PATH_TAGS,DupPredictorApp.QUESTION_TAG + "s_bow", "wv_" + DupPredictorApp.QUESTION_TAG);
		Posts questaoA = new Posts(1);
		Posts questaoB = new Posts(2);
		//dup.generateWVForTags("ruby c c++ java", "c java", questaoA, questaoB);
		System.out.println(questaoA);
		System.out.println(questaoB);
		
		DupPredictorUtils utils = new DupPredictorUtils();
		utils.initializeConfigs();
		
		String test = utils.tokenizeStopStem("a the test dupe here why what doing do playing play");
		System.out.println(test);
		
		String tag = "<c#><code-generation><j#><visualj#>";
		String tagsA =tag.replaceAll("<","");
		tagsA = tagsA.replaceAll(">"," ");
		System.out.println(tagsA);
		
		DupPredictorComposer composer = new DupPredictorComposer();
		questaoA.setTags(tagsA); 
		questaoB.setTags("");
		
		//composer.generateWVForTags(questaoA, questaoB);
		//double tagSim = CosineSimilarity.cosineSimilarity(composer.transformFromStringToVectorOfDoubles(questaoA.getTagVectors(),composer.vectorSizeTag), composer.transformFromStringToVectorOfDoubles(questaoB.getTagVectors(),composer.vectorSizeTag));
		//System.out.println(tagSim);
		
		//System.out.println(StringUtils.isBlank("    "));
		
		String somentePalavras = DupPredictorUtils.getOnlyWords(" 8 ");
		System.out.println(StringUtils.isBlank(somentePalavras));
		
		System.out.println(somentePalavras);
		
		System.out.println(DupPredictorUtils.redondear(Math.random(),2));
		
		String base = DupPredictorUtils.getDataBase("jdbc:postgresql://localhost:5432/stackoverflow2014duppredictor2");
		System.out.println(base);
		
		Map<Integer, Posts> allPostsQuestions = new HashMap<>();
		Posts p = new Posts();
		p.setId(123);
		
		allPostsQuestions.put(1,p);
		
		Posts same = allPostsQuestions.get(1);
		same.setId(456);
		
		System.out.println(allPostsQuestions);
		
		
		
		Set<TopicVector> topicVectors = new HashSet<>();
		topicVectors.add(new TopicVector(1, "0.0017857142857142859 0.0817857142857142859 0.0417857142857142859 0.0517857142857142859 0.0017857142857142859 0.0017857142857142859 0.0017857142857142859 0.0017857142857142859 0.0017857142857142859 0.0017857142857142859 0.0017857142857142859 0.001785714285"));
		topicVectors.add(new TopicVector(2, "2.2831050228310504E-4 2.442831050228310504E-4 2.892831050228310504E-4 2.362831050228310504E-4 2.2831050228310504E-4 2.2831050228310504E-4 2.2831050228310504E-4 2.2831050228310504E-4 2.2831050228310504E-4 2.2831050228310504E-4 2.2831050228310504E-4 2.283105022831"));
		DupPredictorApp.reduceVectors(topicVectors);
		
		for(TopicVector topicVector: topicVectors) {
			System.out.println(topicVector);
		}
		
		
		String number1 = "2.2831050228310504E-4";
		
		BigDecimal d = new BigDecimal(number1);
		System.out.println(d.toPlainString());
		
		
		
		double topicSim = 0d;
		String vecA = "";
		String vecB = "";
		//topicSim = CosineSimilarity.cosineSimilarity(DupPredictorComposer.transformFromStringToVectorOfDoublesTopics(vecA), DupPredictorComposer.transformFromStringToVectorOfDoublesTopics(vecB)); 
		
		boolean validSequence = false;
		double parai[] = new double[4];
		double random;
		do {
			for(int i=0; i<4;i++){
				parai[i] = DupPredictorUtils.redondear(Math.random(),2); //only 2 decimal places
			}
			
			validSequence = ( (parai[0] >= parai[1]) && (parai[1] >= parai[2]) && (parai[2] >= parai[3])); 
			
		} while (!validSequence);
		
		System.out.println(parai[0] + " - "+ parai[1] + " - "+parai[2]+ " - "+parai[3]);
	}
	
	

		
	
}
