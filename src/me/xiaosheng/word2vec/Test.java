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
			vec.loadGoogleModel("data/wiki_chinese_word2vec(Google).model");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(vec.wordSimilarity("狗", "猫"));
		System.out.println(vec.wordSimilarity("计算机", "电脑"));
		System.out.println(vec.wordSimilarity("计算机", "人"));
		
		String s1 = "苏州 有 多条 公路 正在 施工 造成 局部 地区 汽车 行驶 非常 缓慢";
		String s2 = "苏州 最近 有 多条 公路 在 施工 导致 部分 地区 交通 拥堵 汽车 难以 通行";
		String s3 = "苏州 是 一座 美丽 的 城市 四季 分明 雨量 充沛";
		System.out.println(vec.sentenceSimilairy(s1, s1));
		System.out.println(vec.sentenceSimilairy(s1, s2));
		System.out.println(vec.sentenceSimilairy(s1, s3));
//		try {
//			Word2Vec.trainJavaModel("data/train.txt", "data/test.model");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
