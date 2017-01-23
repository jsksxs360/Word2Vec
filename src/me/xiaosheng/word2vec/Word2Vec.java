package me.xiaosheng.word2vec;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.ansj.vec.Learn;
import com.ansj.vec.Word2VEC;
import com.ansj.vec.domain.WordEntry;

public class Word2Vec {

	private Word2VEC vec;
	private boolean loadModel; //是否已经加载模型
	
	public Word2Vec() {
		vec = new Word2VEC();
		loadModel = false;
	}
	/**
	 * 加载Google版Word2Vec模型(C语言训练)
	 * @param modelPath 模型文件路径
	 * @throws IOException
	 */
	public void loadGoogleModel(String modelPath) throws IOException {
		vec.loadGoogleModel(modelPath);
		loadModel = true;
	}
	/**
	 * 加载Java版Word2Vec模型(java语言训练)
	 * @param modelPath 模型文件路径
	 * @throws IOException
	 */
	public void loadJavaModel(String modelPath) throws IOException {
		vec.loadJavaModel(modelPath);
		loadModel = false;
	}
	/**
	 * 训练Java版Word2Vec模型
	 * @param trainFilePath 训练文件路径
	 * @param modelFilePath 模型文件路径
	 * @throws IOException
	 */
	public static void trainJavaModel(String trainFilePath, String modelFilePath) throws IOException {
		Learn learn = new Learn();
	    long start = System.currentTimeMillis();
	    learn.learnFile(new File(trainFilePath));
	    System.out.println("use time " + (System.currentTimeMillis() - start));
	    learn.saveModel(new File(modelFilePath));
	}
	/**
	 * 获得词向量
	 * @param word
	 * @return
	 */
	public float[] getWordVector(String word) {
		if (loadModel == false) {
			return null;
		}
		return vec.getWordVector(word);
	}
	/**
	 * 计算向量内积
	 * @param vec1
	 * @param vec2
	 * @return
	 */
	private float calDist(float[] vec1, float[] vec2) {
		float dist = 0;
		for (int i = 0; i < vec1.length; i++) {
			dist += vec1[i] * vec2[i];
		}
		return dist;
	}
	/**
	 * 计算词相似度
	 * @param word1
	 * @param word2
	 * @return
	 */
	public float wordSimilarity(String word1, String word2) {
		if (loadModel == false) {
			return -1;
		}
		float[] word1Vec = getWordVector(word1);
		float[] word2Vec = getWordVector(word2);
		if(word1Vec == null || word2Vec == null) {
			return -1;
		}
		return calDist(word1Vec, word2Vec);
	}
	/**
	 * 获取相似词语
	 * @param word
	 * @param maxReturnNum
	 * @return
	 */
	public Set<WordEntry> getSimilarWords(String word, int maxReturnNum) {
		if (loadModel == false)
			return null;
		float[] center = getWordVector(word);
		if (center == null) {
			return Collections.emptySet();
		}
		int resultSize = vec.getWords() < maxReturnNum ? vec.getWords() : maxReturnNum;
		TreeSet<WordEntry> result = new TreeSet<WordEntry>();
		double min = Double.MIN_VALUE;
		for (Map.Entry<String, float[]> entry : vec.getWordMap().entrySet()) {
			float[] vector = entry.getValue();
			float dist = calDist(center, vector);
			if (result.size() <= resultSize) {
				result.add(new WordEntry(entry.getKey(), dist));
				min = result.last().score;
			} else {
				if (dist > min) {
					result.add(new WordEntry(entry.getKey(), dist));
					result.pollLast();
					min = result.last().score;
				}
			}
		}
		result.pollFirst();
		return result;
	}
	/**
	 * 计算词语与词语列表中所有词语的最大相似度
	 * (最小返回0)
	 * @param centerWord 词语
	 * @param wordList 词语列表
	 * @return
	 */
	private float calMaxSimilarity(String centerWord, List<String> wordList) {
		float max = 0; //最小返回0
		if (wordList.contains(centerWord)) {
			return 1;
		} else {
			for (String word : wordList) {
				float temp = wordSimilarity(centerWord, word);
				if (temp > max) {
					max = temp;
				}
			}
		}
		return max;
	}
//	/**
//	 * 计算句子相似度
//	 * @param sentence1Words 句子1词语列表
//	 * @param sentence2Words 句子2词语列表
//	 * @return
//	 */
//	public float sentenceSimilarity(List<String> sentence1Words, List<String> sentence2Words) {
//		if (loadModel == false) {
//			return -1;
//		}
//		if (sentence1Words.isEmpty() || sentence2Words.isEmpty()) {
//			return -1;
//		}
//		Set<String> wordSet = new HashSet<String>();
//		for (String word : sentence1Words) {
//			wordSet.add(word);
//		}
//		for (String word : sentence2Words) {
//			wordSet.add(word);
//		}
//		List<String> allWordList = new LinkedList<String>(wordSet);
//		float[] vector1 = new float[allWordList.size()];
//		for (int i = 0; i < vector1.length; i++) {
//			String center = allWordList.get(i);
//			vector1[i] = calMaxSimilarity(center, sentence1Words);
//		}
//		float[] vector2 = new float[allWordList.size()];
//		for (int i = 0; i < vector2.length; i++) {
//			String center = allWordList.get(i);
//			vector2[i] = calMaxSimilarity(center, sentence2Words);
//		}
//		float dist = 0;
//		for (int i = 0; i < vector1.length; i++) {
//			dist += vector1[i] * vector2[i];
//		}
//		float vec1module = 0;
//		float vec2module = 0;
//		for (int i = 0; i < vector1.length; i++) {
//			vec1module += vector1[i] * vector1[i];
//			vec2module += vector2[i] * vector2[i];
//		}
//		//return dist / (float) Math.sqrt(vec1module * vec2module);
//		return dist / ((float) Math.sqrt(vec1module) * (float) Math.sqrt(vec2module));
//	}
	/**
	 * 计算句子相似度
	 * 所有词语权值设为1
	 * @param sentence1Words 句子1词语列表
	 * @param sentence2Words 句子2词语列表
	 * @return
	 * @throws Exception 词语列表和权值向量长度不同
	 */
	public float sentenceSimilarity(List<String> sentence1Words, List<String> sentence2Words) throws Exception {
		if (loadModel == false) {
			return -1;
		}
		if (sentence1Words.isEmpty() || sentence2Words.isEmpty()) {
			return -1;
		}
		float[] vector1 = new float[sentence1Words.size()];
		float[] vector2 = new float[sentence2Words.size()];
		for (int i = 0; i < vector1.length; i++) {
			vector1[i] = calMaxSimilarity(sentence1Words.get(i), sentence2Words);
		}
		for (int i = 0; i < vector2.length; i++) {
			vector2[i] = calMaxSimilarity(sentence2Words.get(i), sentence1Words);
		}
		float sum1 = 0;
		for (int i = 0; i < vector1.length; i++) {
			sum1 += vector1[i];
		}
		float sum2 = 0;
		for (int i = 0; i < vector2.length; i++) {
			sum2 += vector2[i];
		}
		return (sum1 + sum2) / (sentence1Words.size() + sentence2Words.size());
	}
	/**
	 * 计算句子相似度(带权值)
	 * 每一个词语都有一个对应的权值
	 * @param sentence1Words 句子1词语列表
	 * @param sentence2Words 句子2词语列表
	 * @param weightVector1 句子1权值向量
	 * @param weightVector2 句子2权值向量
	 * @return
	 * @throws Exception 词语列表和权值向量长度不同
	 */
	public float sentenceSimilarity(List<String> sentence1Words, List<String> sentence2Words, float[] weightVector1, float[] weightVector2) throws Exception {
		if (loadModel == false) {
			return -1;
		}
		if (sentence1Words.isEmpty() || sentence2Words.isEmpty()) {
			return -1;
		}
		if (sentence1Words.size() != weightVector1.length || sentence2Words.size() != weightVector2.length) {
			throw new Exception("length of word list and weight vector is different");
		}
		float[] vector1 = new float[sentence1Words.size()];
		float[] vector2 = new float[sentence2Words.size()];
		for (int i = 0; i < vector1.length; i++) {
			vector1[i] = calMaxSimilarity(sentence1Words.get(i), sentence2Words);
		}
		for (int i = 0; i < vector2.length; i++) {
			vector2[i] = calMaxSimilarity(sentence2Words.get(i), sentence1Words);
		}
		float sum1 = 0;
		for (int i = 0; i < vector1.length; i++) {
			sum1 += vector1[i] * weightVector1[i];
		}
		float sum2 = 0;
		for (int i = 0; i < vector2.length; i++) {
			sum2 += vector2[i] * weightVector2[i];
		}
		float divide1 = 0;
		for (int i = 0; i < weightVector1.length; i++) {
			divide1 += weightVector1[i];
		}
		float divide2 = 0;
		for (int j = 0; j < weightVector2.length; j++) {
			divide2 += weightVector2[j];
		}
		return (sum1 + sum2) / (divide1 + divide2);
	}
}
