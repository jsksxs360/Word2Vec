package me.xiaosheng.util;

import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.recognition.impl.FilterRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;

public class Segment {

    /**
     * 分词
     * @param sentence 待分词的句子
     * @return 分词结果
     */
    public static List<Term> Seg(String sentence) {
        FilterRecognition filter = new FilterRecognition();
        //过滤标点符号
        filter.insertStopWord(",", " ", ".", "，", "。", ":", "：", "'", "‘", "’", "　", "“", "”", "《", "》", "[", "]", "-");
        return ToAnalysis.parse(sentence).recognition(filter).getTerms();
    }
    /**
     * 获取词语列表
     * @param sentence 待分词的句子
     * @return 分词后的词语列表
     */
    public static List<String> getWords(String sentence) {
        List<Term> termList = Seg(sentence);
        List<String> wordList = new ArrayList<String>();
        for (Term wordTerm : termList) {
            wordList.add(wordTerm.getName());
        }
        return wordList;
    }
    /**
     * 获取词性列表
     * @param sentence 待分词的句子
     * @return 分词后的词性列表
     */
    public static List<String> getPOS(String sentence) {
        List<Term> termList = Seg(sentence);
        List<String> natureList = new ArrayList<String>();
        for (Term wordTerm : termList) {
            natureList.add(wordTerm.getNatureStr());
        }
        return natureList;
    }
    /**
     * 获取词性权值数组
     * @param posList 词性列表
     * @return 词性列表对应的权值数组
     */
    public static float[] getPOSWeightArray(List<String> posList) {
        float[] weightVector = new float[posList.size()];
        for (int i = 0; i < weightVector.length; i++) {
            String POS = posList.get(i);
            switch(POS.charAt(0)) {
            case 'n':
            case 'v':weightVector[i] = 1;break;
            default:weightVector[i] = (float) 0.8;break;
            }
        }
        return weightVector;
    }
}
