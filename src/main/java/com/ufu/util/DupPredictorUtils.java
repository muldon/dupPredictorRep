package com.ufu.util;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ufu.dup.repository.GenericRepository;
import com.ufu.dup.to.PostLink;
import com.ufu.dup.to.Posts;

@Component
public class DupPredictorUtils {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static CharArraySet stopWords;
	private AttributeFactory factory;
	private static StandardTokenizer standardTokenizer;
	Boolean configsInitialized = false;
	private static long endTime;
	
	private static Map<Integer, Set<Integer>> bucketDuplicatiosMap;
	private Set<Integer> allDuplicatedQuestionsIds;
	private static List<PostLink> allPostLinks;
	
	@Autowired
	protected GenericRepository genericRepository;
	
	
	public static final String CODE_REGEX_EXPRESSION = "(?sm)<code>(.*?)</code>";
	public static final Pattern CODE_PATTERN = Pattern.compile(CODE_REGEX_EXPRESSION, Pattern.DOTALL); 
	
	public static final String BLOCKQUOTE_EXPRESSION = "(?sm)<blockquote>(.*?)</blockquote>";
	public static final Pattern BLOCKQUOTE_PATTERN = Pattern.compile(BLOCKQUOTE_EXPRESSION, Pattern.DOTALL);
	
	//public static final String LINK_EXPRESSION_OUT = "(?sm)<a href=(.*?) rel=\"nofollow\">";
	public static final String LINK_EXPRESSION_OUT = "(?sm)<a href=(.*?)</a>";
	public static final Pattern LINK_PATTERN = Pattern.compile(LINK_EXPRESSION_OUT, Pattern.DOTALL);
	
	public static final String ONLY_WORDS_EXPRESSION = "(?<!\\S)\\p{Alpha}+(?!\\S)";
	public static final Pattern ONLY_WORDS_PATTERN = Pattern.compile(ONLY_WORDS_EXPRESSION, Pattern.DOTALL);
	
	public static final String NOT_ONLY_WORDS_EXPRESSION = "(?<!\\S)(?!\\p{Alpha}+(?!\\S))\\S+";
	public static final Pattern NOT_ONLY_WORDS_PATTERN = Pattern.compile(NOT_ONLY_WORDS_EXPRESSION, Pattern.DOTALL);
	
	
	
	private static String htmlTags[] = {"<p>","</p>","<pre>","</pre>","<blockquote>","</blockquote>", /*"<a href=\"","\">",*/
			"</a>","<img src=","alt=","<ol>","</ol>","<li>","</li>","<ul>",
			"</ul>","<br>","</br>","<h1>","</h1>","<h2>","</h2>","<strong>","</strong>",
			"<code>","</code>","<em>","</em>","<hr>"};
	
	
	public void initializeConfigs() throws Exception {
		if(!configsInitialized){
			configsInitialized = true;
				
			factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;
			standardTokenizer = new StandardTokenizer(factory);
			stopWords = EnglishAnalyzer.getDefaultStopSet();
			
			standardTokenizer.close();
		}
				
	}
	
	
	
	public String tokenizeStopStem(String input) throws Exception {
		if (StringUtils.isBlank(input)) {
			return "";
		}
		standardTokenizer.setReader(new StringReader(input));
		TokenStream stream = new StopFilter(new LowerCaseFilter(new PorterStemFilter(standardTokenizer)), stopWords);

		CharTermAttribute charTermAttribute = standardTokenizer.addAttribute(CharTermAttribute.class);
		stream.reset();

		StringBuilder sb = new StringBuilder();
		while (stream.incrementToken()) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(charTermAttribute.toString());
		}

		stream.end();
		stream.close();
		
		
		return sb.toString();
	}
	
	
	public void reportElapsedTime(long initTime, String processName) {
		
		endTime = System.currentTimeMillis();
		String duration = DurationFormatUtils.formatDuration(endTime-initTime, "HH:mm:ss,SSS");
		logger.info("Elapsed time: "+duration+ " of the execution of  "+processName);
		
	}
	
	
	public void getPostsLinks() {
		if(allPostLinks==null){
			logger.info("Fetching PostLinks... ");
			allPostLinks = genericRepository.getAllPostLinks();
			logger.info("PostLinks retrieved: " + allPostLinks.size());
		}
		

	}
	
	
	//@PostConstruct
	public void generateBuckets() {
		if(bucketDuplicatiosMap==null){
			bucketDuplicatiosMap = new HashMap<Integer, Set<Integer>>();	
			allDuplicatedQuestionsIds = new HashSet<Integer>();
			getPostsLinks();
			//Pode haver mais de uma duplicada por questao.. Bucket structure
			logger.info("Building buckets");
					
			for(PostLink postLink:allPostLinks){
				allDuplicatedQuestionsIds.add(postLink.getId());
				allDuplicatedQuestionsIds.add(postLink.getRelatedPostId());
				
				Set<Integer> duplicatedOfMaster = bucketDuplicatiosMap.get(postLink.getRelatedPostId());  
				if(duplicatedOfMaster==null){
					duplicatedOfMaster = new HashSet<Integer>();
					bucketDuplicatiosMap.put(postLink.getRelatedPostId(),duplicatedOfMaster);
				}
				duplicatedOfMaster.add(postLink.getPostId());
			}
			
			logger.info("Buckets gerados...");
		}
	}
	
	
	private static String substitituiSimbolosHTML(String finalContent) {
		finalContent = finalContent.replaceAll("&amp;","&");
		finalContent = finalContent.replaceAll("&lt;", "<");
		finalContent = finalContent.replaceAll("&gt;", ">");
		finalContent = finalContent.replaceAll("&quot;", "\"");
		finalContent = finalContent.replaceAll("&apos;", "\'"); 
		
		return finalContent;
	}
	
	
	public static List<String> getCodeValues(Pattern patter,String str) {
	    final List<String> tagValues = new ArrayList<String>();
	    final Matcher matcher = patter.matcher(str);
	    while (matcher.find()) {
	        tagValues.add(matcher.group(1));
	    }
	    return tagValues;
	}
	

	
	public String[] separaSomentePalavrasNaoSomentePalavras(String content, String parte) throws Exception {
		String[] finalContent = new String[4];
		
		String lower = content.toLowerCase()+ " ";
			
		//volta simbolos de marcacao HTML para estado original 
		String simbolosOriginais = substitituiSimbolosHTML(lower);		
		
		String textoSemCodigosLinksEBlackquotes = simbolosOriginais.replaceAll(LINK_EXPRESSION_OUT, " ");
		
		//blockquotes
		String blockquoteContent = "";
		List<String> blockquotes = getCodeValues(BLOCKQUOTE_PATTERN, textoSemCodigosLinksEBlackquotes);
		for(String blockquote: blockquotes){
			blockquoteContent+= blockquote+ " ";
		}
		textoSemCodigosLinksEBlackquotes = textoSemCodigosLinksEBlackquotes.replaceAll(BLOCKQUOTE_EXPRESSION, " ");
		
		//codes
		String codeContent = "";
		List<String> codes = getCodeValues(CODE_PATTERN, textoSemCodigosLinksEBlackquotes);
		for(String code: codes){
			codeContent+= code+ " ";
		}
		
		codeContent = codeContent.replaceAll("\n", " ");
		codeContent = retiraHtmlTags(codeContent);
					
		
		//String codeToBeJoinedInBody = retiraSimbolosCodeParaBody(codeContent);
				
		//String bloquesAndLinks = blockquoteContent+ " "+linksContent;
		String bloquesAndLinks = blockquoteContent;
		bloquesAndLinks = bloquesAndLinks.replaceAll("\n", " ");
		bloquesAndLinks = retiraHtmlTags(bloquesAndLinks);
		bloquesAndLinks += " "+codeContent;
		
		
		textoSemCodigosLinksEBlackquotes = textoSemCodigosLinksEBlackquotes.replaceAll(CODE_REGEX_EXPRESSION, " ");
		textoSemCodigosLinksEBlackquotes = retiraHtmlTags(textoSemCodigosLinksEBlackquotes);
		
		//trata antes de pegar somenta as palavras
		
		String separated = retiraSimbolosImportantesNaoCodigo(textoSemCodigosLinksEBlackquotes,parte);
		
		
		String somentePalavras = getOnlyWords(separated);
		/*List<String> palavras = getWords(ONLY_WORDS_PATTERN, separated);
		for(String word: palavras){
			somentePalavras+= word+ " ";
		}*/
		
		
		String naoSomentePalavras = "";
		List<String> naoPalavras = getWords(NOT_ONLY_WORDS_PATTERN, separated);
		for(String word: naoPalavras){
			naoSomentePalavras+= word+ " ";
		}
		naoSomentePalavras+= bloquesAndLinks;
		
		naoSomentePalavras = retiraSimbolosEspeciais(naoSomentePalavras);
		
		String stoppedStemmed = tokenizeStopStem(somentePalavras.trim());
		
		finalContent[0] = stoppedStemmed; //+ " "+somentePalavrasCodeClean;
		finalContent[1] = naoSomentePalavras.trim();
		finalContent[2] = codeContent.trim();
		
		return finalContent;
		

	}
	
	

	public static String getOnlyWords(String separated) {
		String somentePalavras="";
		List<String> palavras = getWords(ONLY_WORDS_PATTERN, separated);
		for(String word: palavras){
			somentePalavras+= word+ " ";
		}
		return somentePalavras;
		
	}



	public String retiraSimbolosImportantesNaoCodigo(String finalContent, String parte) {
		if(parte.equals("title")){
			finalContent = finalContent.replaceAll("\\?", " ");
		}else {
			finalContent = finalContent.replaceAll("\\:", " ");
		}
		return finalContent;
	}
	
	
	
	/**
	 * Retira tags de marcação do body ou title  
	 */
	public static String retiraHtmlTags(String content) {
		if(content==null || content.trim().equals("")){
			return "";
		}
		//boolean startedHtml = false;
		for(String tag: htmlTags){
			content= content.replaceAll(tag," ");
		}
		//content = content.replaceAll("<a href=", " ");
		//content = content.replaceAll("</a>", " ");

		return content;
		
	}
	
	
	
	public static List<String> getWords(Pattern patter,String str) {
	    final List<String> tagValues = new ArrayList<String>();
	    final Matcher matcher = patter.matcher(str);
	    while (matcher.find()) {
	        tagValues.add(matcher.group(0));
	    }
	    return tagValues;
	}
	
	
	
	
	
	public static String retiraSimbolosEspeciais(String finalContent) {
		
		//nao precisam de espaco
		finalContent = finalContent.replaceAll("\\+"," ");
		finalContent = finalContent.replaceAll("\\^", " ");
		finalContent = finalContent.replaceAll(":", " ");
		finalContent = finalContent.replaceAll(";", " ");
		finalContent = finalContent.replaceAll("-", " ");
		finalContent = finalContent.replaceAll("\\+", " ");
		finalContent = finalContent.replaceAll("&", " ");
		finalContent = finalContent.replaceAll("\\*", " ");
		finalContent = finalContent.replaceAll("\\~", " ");
		finalContent = finalContent.replaceAll("\\\\", " ");
		finalContent = finalContent.replaceAll("/", " ");
		//finalContent = finalContent.replaceAll("\\'", "simbaspassimpl");
		finalContent = finalContent.replaceAll("\\`", " ");
		finalContent = finalContent.replaceAll("\"", " ");
		finalContent = finalContent.replaceAll("\\(", " ");
		finalContent = finalContent.replaceAll("\\)", " ");
		finalContent = finalContent.replaceAll("\\[", " ");
		finalContent = finalContent.replaceAll("\\]", " ");
		finalContent = finalContent.replaceAll("\\{", " ");
		finalContent = finalContent.replaceAll("\\}", " ");
		finalContent = finalContent.replaceAll("\\?", " ");
		finalContent = finalContent.replaceAll("\\|", " ");
		finalContent = finalContent.replaceAll("\\%", " ");
		finalContent = finalContent.replaceAll("\\$", " ");
		finalContent = finalContent.replaceAll("\\@", " ");
		finalContent = finalContent.replaceAll("\\<", " ");
		finalContent = finalContent.replaceAll("\\>", " ");
		finalContent = finalContent.replaceAll("\\#", " ");
		finalContent = finalContent.replaceAll("\\=", " ");
		finalContent = finalContent.replaceAll("\\.", " ");
		finalContent = finalContent.replaceAll("\\,", " ");
		finalContent = finalContent.replaceAll("\\_", " ");
		finalContent = finalContent.replaceAll("\\!", " ");
		finalContent = finalContent.replaceAll("\\'", " ");		
		
		finalContent = finalContent.replaceAll("\n", " ");
		
		return finalContent;
	}
	
	
	


	public static Map<Integer, Set<Integer>> getBucketDuplicatiosMap() {
		return bucketDuplicatiosMap;
	}


	public static void setBucketDuplicatiosMap(Map<Integer, Set<Integer>> bucketDuplicatiosMap) {
		DupPredictorUtils.bucketDuplicatiosMap = bucketDuplicatiosMap;
	}



	public static void setBlanks(Posts post) {
		post.setAcceptedAnswerId(null);
		post.setAnswerCount(null);
		post.setClosedDate(null);
		post.setCommentCount(null);
		post.setCommunityOwnedDate(null);
		post.setFavoriteCount(null);
		post.setLastActivityDate(null);
		post.setLastEditorDisplayName(null);
		post.setLastEditorDisplayName(null);
		post.setLastEditorUserId(null);
		post.setOwnerUserId(null);
		post.setPostTypeId(null);
		post.setViewCount(null);
		post.setParentId(null);
		post.setCreationDate(null);
		post.setTagsSyn(null); //used only in dupe
		post.setCode(null); //used only in dupe
		
	}



	public static String getQueryComplementByTag(String tagFilter) {
		String query="";
		if(tagFilter!=null && !"".equals(tagFilter)) {
			if(tagFilter.equals("java")) {
				query += " and tags like '%java%' and tags not like '%javascript%' "; 
			}else {
				query += " and tags like '%"+tagFilter+"%'";
			}
		}
		return query;
		
	}

	 public static double redondear(double pNumero, int pCantidadDecimales) {
		    // the function is call with the values Redondear(625.3f, 2)
		    BigDecimal value = new BigDecimal(pNumero);
		    value = value.setScale(pCantidadDecimales, RoundingMode.HALF_EVEN); // here the value is correct (625.30)
		    return value.doubleValue(); // but here the values is 625.3
		}



	public static String getDataBase(String fullPath) {
		String dataBaseName[] = fullPath.split("5432/?");
		return dataBaseName[1];
	}



	public static List<PostLink> getAllPostLinks() {
		return allPostLinks;
	}



	public static void setAllPostLinks(List<PostLink> allPostLinks) {
		DupPredictorUtils.allPostLinks = allPostLinks;
	}



	
	
		
	
}
