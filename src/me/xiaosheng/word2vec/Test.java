package me.xiaosheng.word2vec;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Word2Vec vec = new Word2Vec();
		try {
			vec.loadGoogleModel("data/review.model");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(vec.wordSimilarity("狗", "猫"));
		System.out.println(vec.wordSimilarity("计算机", "硬盘"));
		System.out.println(vec.wordSimilarity("计算机", "人"));
		
		String s1 = "张家港 最近 汽车 行驶 非常 缓慢 因为 有 多条 公路 正在 施工";
		String s2 = "张家港 最近 交通 拥堵 部分 道路 难以 通行";
		String s3 = "张家港 水 污染 很 严重";
		System.out.println(vec.sentenceSimilairy(s1, s1));
		System.out.println(vec.sentenceSimilairy(s1, s2));
		System.out.println(vec.sentenceSimilairy(s1, s3));
		try {
			Word2Vec.trainJavaModel("data/train.txt", "data/test.model");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
