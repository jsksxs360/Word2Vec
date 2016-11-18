package me.xiaosheng.word2vec;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ansj.vec.domain.WordEntry;

import me.xiaosheng.util.Segment;

public class Test {

	public static void main(String[] args) throws Exception {
		Word2Vec vec = new Word2Vec();
		try {
			vec.loadGoogleModel("data/wiki_chinese_word2vec(Google).model");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//计算词语相似度
		System.out.println("-----词语相似度-----");
		System.out.println("狗|猫: " + vec.wordSimilarity("狗", "猫"));
		System.out.println("计算机|电脑: " + vec.wordSimilarity("计算机", "电脑"));
		System.out.println("计算机|人: " + vec.wordSimilarity("计算机", "人"));
		//获取相似的词语
		Set<WordEntry> similarWords = vec.getSimilarWords("漂亮", 10);
		System.out.println("与 [ 漂亮 ] 语义相似的词语:");
		for(WordEntry word : similarWords) {
			System.out.println(word.name + " : " + word.score);
		}
		//计算句子相似度
		System.out.println("-----句子相似度-----");
		String s1 = "苏州有多条公路正在施工，造成局部地区汽车行驶非常缓慢。";
		String s2 = "苏州最近有多条公路在施工，导致部分地区交通拥堵，汽车难以通行。";
		String s3 = "苏州是一座美丽的城市，四季分明，雨量充沛。";
		System.out.println("s1: " + s1);
		System.out.println("s2: " + s2);
		System.out.println("s3: " + s3);
		//分词，去停用词
		Segment seg1 = new Segment(s1, true);
		Segment seg2 = new Segment(s2, true);
		Segment seg3 = new Segment(s3, true);
		//标准句子相似度
		System.out.println(vec.sentenceSimilarity(seg1.getWords(), seg1.getWords()));
		System.out.println(vec.sentenceSimilarity(seg1.getWords(), seg2.getWords()));
		System.out.println(vec.sentenceSimilarity(seg1.getWords(), seg3.getWords()));
		//简易句子相似度
		System.out.println(vec.easySentenceSimilarity(seg1.getWords(), seg1.getWords()));
		System.out.println(vec.easySentenceSimilarity(seg1.getWords(), seg2.getWords()));
		System.out.println(vec.easySentenceSimilarity(seg1.getWords(), seg3.getWords()));
		//简易句子相似度(名词、动词权值设为1，其他设为0.8)
		System.out.println(vec.easySentenceSimilarity(seg1.getWords(), seg1.getWords(), seg1.getPOSWeightVector(), seg1.getPOSWeightVector()));
		System.out.println(vec.easySentenceSimilarity(seg1.getWords(), seg2.getWords(), seg1.getPOSWeightVector(), seg2.getPOSWeightVector()));
		System.out.println(vec.easySentenceSimilarity(seg1.getWords(), seg3.getWords(), seg1.getPOSWeightVector(), seg3.getPOSWeightVector()));
//		try {
//			Word2Vec.trainJavaModel("data/train.txt", "data/test.model");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
