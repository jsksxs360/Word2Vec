package me.xiaosheng.word2vec;

import java.io.IOException;
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
        System.out.println("与 [漂亮] 语义相似的词语:");
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
        //分词，获取词语列表
        List<String> wordList1 = Segment.getWords(s1);
        List<String> wordList2 = Segment.getWords(s2);
        List<String> wordList3 = Segment.getWords(s3);
        //快速句子相似度
        System.out.println("快速句子相似度:");
        System.out.println("s1|s1: " + vec.fastSentenceSimilarity(wordList1, wordList1));
        System.out.println("s1|s2: " + vec.fastSentenceSimilarity(wordList1, wordList2));
        System.out.println("s1|s3: " + vec.fastSentenceSimilarity(wordList1, wordList3));
        //句子相似度(所有词语权值设为1)
        System.out.println("句子相似度:");
        System.out.println("s1|s1: " + vec.sentenceSimilarity(wordList1, wordList1));
        System.out.println("s1|s2: " + vec.sentenceSimilarity(wordList1, wordList2));
        System.out.println("s1|s3: " + vec.sentenceSimilarity(wordList1, wordList3));
        //句子相似度(名词、动词权值设为1，其他设为0.8)
        System.out.println("句子相似度(名词、动词权值设为1，其他设为0.8):");
        float[] weightArray1 = Segment.getPOSWeightArray(Segment.getPOS(s1));
        float[] weightArray2 = Segment.getPOSWeightArray(Segment.getPOS(s2));
        float[] weightArray3 = Segment.getPOSWeightArray(Segment.getPOS(s3));
        System.out.println("s1|s1: " + vec.sentenceSimilarity(wordList1, wordList1, weightArray1, weightArray1));
        System.out.println("s1|s2: " + vec.sentenceSimilarity(wordList1, wordList2, weightArray1, weightArray2));
        System.out.println("s1|s3: " + vec.sentenceSimilarity(wordList1, wordList3, weightArray1, weightArray3));
//        try {
//            Word2Vec.trainJavaModel("data/train.txt", "data/test.model");
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }
}
