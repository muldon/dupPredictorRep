package com.ufu;


import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;

import com.ufu.dup.to.Question;
import com.ufu.util.CosineSimilarity;
import com.ufu.util.DupPredictorComposer;

@SuppressWarnings("javadoc")
public class AccuracyTest {

	
	@Test
	public void testSimilarity() throws Exception {
		double cosim = 0;
		String s1 = "can anyon explain mona 3333";
		String s2 = "can anyon explain monad 3333";
		

		String tag1 = "objective-c ios iphone";
		String tag2 = "objective-c ios ddd"; 
		
		/*System.out.println("Componente 1");
		cosim = CosineDocumentSimilarity.getCosineSimilarity(s1, s2);
		System.out.println(cosim);*/
		
		System.out.println("Componente 2");
		cosim = CosineSimilarity.getCosineSimilarity(s1,s2);
		System.out.println(cosim);
		
		Question questaoA = new Question();
		Question questaoB = new Question();
		questaoA.setTags(s1);
		questaoB.setTags(s2);
		DupPredictorComposer.generateWVForTags(questaoA,questaoB);
		double tagSim = CosineSimilarity.cosineSimilarity(DupPredictorComposer.transformFromStringToVectorOfDoubles(questaoA.getTagVectors(),DupPredictorComposer.vectorSizeTag), DupPredictorComposer.transformFromStringToVectorOfDoubles(questaoB.getTagVectors(),DupPredictorComposer.vectorSizeTag));
		System.out.println(tagSim);		
		
		
		
	}
	
}
