package com.ufu.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.springframework.stereotype.Component;

import com.ufu.dup.to.Posts;
import com.ufu.dup.to.Question;
import com.ufu.dup.to.SimScore;

@Component
public class DupPredictorComposer {
	private static double alpha = 0.8; //title-title
	private static double beta = 0.51; //body-body
	private static double gama = 0.01; //topics
	private static double delta = 0.37; //tag-tag
	
	public static Integer vectorSizeTag;
	
	public static Boolean useLDA;
	public static Integer numTopics; 
	
	public static double calculateSimilarity(Question questaoA, Question questaoB) throws Exception {
	//public static double calculateSimilarity(Posts questaoA, Posts questaoB) throws Exception {
		double simScore = 0d;
		double titleSim = CosineSimilarity.getCosineSimilarity(questaoA.getTitle(), questaoB.getTitle());
		double descSim  = CosineSimilarity.getCosineSimilarity(questaoA.getBody(), questaoB.getBody());
	
		double topicSim = 0d;
		if(useLDA){
			topicSim = CosineSimilarity.cosineSimilarity(transformFromStringToVectorOfDoublesTopics(questaoA.getTopicVectors()), transformFromStringToVectorOfDoublesTopics(questaoB.getTopicVectors())); 
		}
		
		generateWVForTags(questaoA,questaoB);
		double tagSim = CosineSimilarity.cosineSimilarity(transformFromStringToVectorOfDoubles(questaoA.getTagVectors(),vectorSizeTag), transformFromStringToVectorOfDoubles(questaoB.getTagVectors(),vectorSizeTag));
		simScore = alpha*titleSim + beta*descSim + gama*topicSim + delta*tagSim;
		return simScore;
		
	}
	
	public static SimScore calculateSimScore(Question questaoA, Question questaoB) throws Exception {
	//public static SimScore calculateSimScore(Posts questaoA, Posts questaoB) throws Exception {
		double titleSim = CosineSimilarity.getCosineSimilarity(questaoA.getTitle(), questaoB.getTitle());
		double descSim  = CosineSimilarity.getCosineSimilarity(questaoA.getBody(), questaoB.getBody());	
		double topicSim = 0d;
		
		if(useLDA){
			topicSim = CosineSimilarity.cosineSimilarity(transformFromStringToVectorOfDoublesTopics(questaoA.getTopicVectors()), transformFromStringToVectorOfDoublesTopics(questaoB.getTopicVectors())); 
		}
		
		generateWVForTags(questaoA,questaoB);
		double tagSim = CosineSimilarity.cosineSimilarity(transformFromStringToVectorOfDoubles(questaoA.getTagVectors(),vectorSizeTag), transformFromStringToVectorOfDoubles(questaoB.getTagVectors(),vectorSizeTag));
		SimScore simScore = new SimScore(titleSim,descSim,topicSim,tagSim);
		return simScore;
		
	}
	
	public static double calculaSimilaridade(SimScore simScore) throws Exception {
		double score = alpha*simScore.getTitleSim() + beta*simScore.getDesSim() + gama*simScore.getTopicSim() + delta*simScore.getTagSim();
		return score;
	}
	
	public static double[] transformFromStringToVectorOfDoublesTopics(String vectors)  {
		double[] doubleVector = new double[numTopics];
		StringTokenizer defaultTokenizer = new StringTokenizer(vectors);
		// System.out.println("Total number of tokens found : " +
		// defaultTokenizer.countTokens());
		int pos = 0;
		while (defaultTokenizer.hasMoreTokens()) {
			doubleVector[pos] = Double.valueOf(defaultTokenizer.nextToken());
			pos++;
		}
		defaultTokenizer = null;
		return doubleVector;
	}

	public static double[] transformFromStringToVectorOfDoubles(String vectors, Integer vecSize) {
		double[] doubleVector = new double[vecSize];
		StringTokenizer defaultTokenizer = new StringTokenizer(vectors);
		// System.out.println("Total number of tokens found : " +
		// defaultTokenizer.countTokens());
		while (defaultTokenizer.hasMoreTokens()) {
			String parts[] = defaultTokenizer.nextToken().split(":");
			try {
				doubleVector[Integer.valueOf(parts[0])] = Double.valueOf(parts[1]);
			} catch (Exception e) {
				System.out.println(e);
			}
			
		}
		
		return doubleVector;

	}


	
	public static void generateWVForTags(Question questaoA, Question questaoB) {
	//public static void generateWVForTags(Posts questaoA, Posts questaoB) {
		List<String> listA = new ArrayList<String>();
		List<String> listB = new ArrayList<String>();
		//String bowFileContent = "";
		
		Set<String> bowSet = new LinkedHashSet<String>();
		
		StringTokenizer defaultTokenizer = new StringTokenizer(questaoA.getTags());
		while (defaultTokenizer.hasMoreTokens()) {
			String token = defaultTokenizer.nextToken();
			listA.add(token);
			bowSet.add(token);
		}
		
		defaultTokenizer = new StringTokenizer(questaoB.getTags());
		while (defaultTokenizer.hasMoreTokens()) {
			String token = defaultTokenizer.nextToken();
			listB.add(token);
			bowSet.add(token);
		}
		
		String bowArray[] = bowSet.toArray(new String[bowSet.size()]);
		vectorSizeTag = bowSet.size();
		
		String strVectorA = "";
		String strVectorB = "";
		
		for(int i=0; i<bowArray.length; i++){
			if(listA.contains(bowArray[i])){
				strVectorA+=i+":"+1/(double)(listA.size()) +" ";
			}
			if(listB.contains(bowArray[i])){
				strVectorB+=i+":"+1/(double)(listB.size()) +" ";
			}
			//bowFileContent+=(bowArray[i]+"\n");
		}
		
		//System.out.println(bowFileContent);
		questaoA.setTagVectors(strVectorA);
		questaoB.setTagVectors(strVectorB);
		
		defaultTokenizer = null;
		bowSet = null;
		bowArray = null;
		listA = null;
		listB = null;
	}

	public static void setScores(double alpha, double beta, double gama,double delta) {
		DupPredictorComposer.alpha = alpha;
		DupPredictorComposer.beta= beta;
		DupPredictorComposer.gama = gama;
		DupPredictorComposer.delta = delta;

	}


	public static double getAlpha() {
		return alpha;
	}



	public static void setAlpha(double alpha) {
		DupPredictorComposer.alpha = alpha;
	}



	public static double getBeta() {
		return beta;
	}



	public static void setBeta(double beta) {
		DupPredictorComposer.beta = beta;
	}



	public static double getDelta() {
		return delta;
	}



	public static void setDelta(double delta) {
		DupPredictorComposer.delta = delta;
	}

	public static double getGama() {
		return gama;
	}

	public static void setGama(double gama) {
		DupPredictorComposer.gama = gama;
	}

	public static Boolean getUseLDA() {
		return useLDA;
	}

	public static void setUseLDA(Boolean useLDA) {
		DupPredictorComposer.useLDA = useLDA;
	}

	public static Integer getVectorSizeTag() {
		return vectorSizeTag;
	}

	public static void setVectorSizeTag(Integer vectorSizeTag) {
		DupPredictorComposer.vectorSizeTag = vectorSizeTag;
	}

	public static Integer getNumTopics() {
		return numTopics;
	}

	public static void setNumTopics(Integer numTopics) {
		DupPredictorComposer.numTopics = numTopics;
	}

	
	
}
