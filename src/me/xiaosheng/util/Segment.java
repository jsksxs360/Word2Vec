package me.xiaosheng.util;

import java.util.LinkedList;
import java.util.List;

import com.hankcs.hanlp.*;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;

public class Segment {

	private List<Term> termList;
	/**
	 * 分词器构造函数
	 * @param sentence 要分词的句子
	 * @param enableStopWordFilter 是否过滤停用词
	 */
	public Segment(String sentence, boolean enableStopWordFilter) {
		termList = HanLP.segment(sentence);
		if (enableStopWordFilter) {
			CoreStopWordDictionary.apply(termList);
		}
	}
	/**
	 * 获取词语列表
	 * @return
	 */
	public List<String> getWords() {
		List<String> wordList = new LinkedList<String>();
		for (Term wordTerm : termList) {
			wordList.add(wordTerm.word);
		}
		return wordList;
	}
	/**
	 * 获取词性列表
	 * @return
	 */
	public List<String> getPOS() {
		List<String> POSList = new LinkedList<String>();
		for (Term wordTerm : termList) {
			POSList.add(wordTerm.nature.toString());
		}
		return POSList;
	}
	
	public float[] getPOSWeightVector() {
		float[] weightVector = new float[termList.size()];
		for (int i = 0; i < weightVector.length; i++) {
			String POS = termList.get(i).nature.toString();
			if (POS.charAt(0) == 'n') {
				weightVector[i] = 1;
			} else if (POS.charAt(0) == 'v') {
				weightVector[i] = 1;
			} else {
				weightVector[i] = (float) 0.8;
			}
		}
		return weightVector;
	}
	@Override
	public String toString() {
		return termList.toString();
	}
}
