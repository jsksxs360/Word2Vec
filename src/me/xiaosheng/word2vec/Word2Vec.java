package me.xiaosheng.word2vec;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ansj.vec.Learn;
import com.ansj.vec.Word2VEC;

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
		float dist = 0;
		for (int i = 0; i < word1Vec.length; i++) {
			dist += word1Vec[i] * word2Vec[i];
		}
		return dist;
	}
	/**
	 * 计算句子相似度
	 * @param sentence1
	 * @param sentence2
	 * @return
	 */
	public float sentenceSimilairy(String sentence1, String sentence2) {
		if (loadModel == false) {
			return -1;
		}
		List<String> sentence1Words = Arrays.asList(sentence1.split(" "));
		List<String> sentence2Words = Arrays.asList(sentence2.split(" "));
		if (sentence1Words.size() == 0 || sentence2Words.size() == 0) {
			return -1;
		}
		Set<String> wordSet = new HashSet<String>();
		for (String word : sentence1Words) {
			wordSet.add(word);
		}
		for (String word : sentence2Words) {
			wordSet.add(word);
		}
		List<String> allWordList = new LinkedList<String>(wordSet);
		float[] vector1 = new float[allWordList.size()];
		for (int i = 0; i < vector1.length; i++) {
			String center = allWordList.get(i);
			if (sentence1Words.contains(center)) {
				vector1[i] = 1;
			} else {
				float score = 0;
				for (int j = 0; j < sentence1Words.size(); j++) {
					float tmp = wordSimilarity(center, sentence1Words.get(j));
					if (tmp > score) {
						score = tmp;
					}
				}
				vector1[i] = score;
			}
		}
		float[] vector2 = new float[allWordList.size()];
		for (int i = 0; i < vector2.length; i++) {
			String center = allWordList.get(i);
			if (sentence2Words.contains(center)) {
				vector2[i] = 1;
			} else {
				float score = 0;
				for (int j = 0; j < sentence2Words.size(); j++) {
					float tmp = wordSimilarity(center, sentence2Words.get(j));
					if (tmp > score) {
						score = tmp;
					}
				}
				vector2[i] = score;
			}
		}
		float dist = 0;
		for (int i = 0; i < vector1.length; i++) {
			dist += vector1[i] * vector2[i];
		}
		float vec1module = 0;
		float vec2module = 0;
		for (int i = 0; i < vector1.length; i++) {
			vec1module += vector1[i] * vector1[i];
			vec2module += vector2[i] * vector2[i];
		}
		vec1module = (float) Math.sqrt(vec1module);
		vec2module = (float) Math.sqrt(vec2module);
		return dist / (vec1module * vec2module);
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
}
