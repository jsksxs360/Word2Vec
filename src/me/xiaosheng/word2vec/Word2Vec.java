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
        loadModel = true;
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
     * 向量求和
     * @param sum 和向量
     * @param vec 添加向量
     */
    private void calSum(float[] sum, float[] vec) {
        for (int i = 0; i < sum.length; i++) {
            sum[i] += vec[i];
        }
    }
    /**
     * 计算词相似度
     * @param word1
     * @param word2
     * @return
     */
    public float wordSimilarity(String word1, String word2) {
        if (loadModel == false) {
            return 0;
        }
        float[] word1Vec = getWordVector(word1);
        float[] word2Vec = getWordVector(word2);
        if(word1Vec == null || word2Vec == null) {
            return 0;
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
        float max = -1;
        if (wordList.contains(centerWord)) {
            return 1;
        } else {
            for (String word : wordList) {
                float temp = wordSimilarity(centerWord, word);
                if (temp == 0) continue;
                if (temp > max) {
                    max = temp;
                }
            }
        }
        if (max == -1) return 0;
        return max;
    }
    /**
     * 快速计算句子相似度
     * @param sentence1Words 句子1词语列表
     * @param sentence2Words 句子2词语列表
     * @return 两个句子的相似度
     */
    public float fastSentenceSimilarity(List<String> sentence1Words, List<String> sentence2Words) {
        if (loadModel == false) {
            return 0;
        }
        if (sentence1Words.isEmpty() || sentence2Words.isEmpty()) {
            return 0;
        }
        float[] sen1vector = new float[vec.getSize()];
        float[] sen2vector = new float[vec.getSize()];
        double len1 = 0;
        double len2 = 0;
        for (int i = 0; i < sentence1Words.size(); i++) {
            float[] tmp = getWordVector(sentence1Words.get(i));
            if (tmp != null) calSum(sen1vector, tmp);
        }
        for (int i = 0; i < sentence2Words.size(); i++) {
            float[] tmp = getWordVector(sentence2Words.get(i));
            if (tmp != null) calSum(sen2vector, tmp);
        }
        for (int i = 0; i < vec.getSize(); i++) {
            len1 += sen1vector[i] * sen1vector[i];
            len2 += sen2vector[i] * sen2vector[i];
        }
        return (float) (calDist(sen1vector, sen2vector) / Math.sqrt(len1 * len2));
    }
    /**
     * 计算句子相似度
     * 所有词语权值设为1
     * @param sentence1Words 句子1词语列表
     * @param sentence2Words 句子2词语列表
     * @return 两个句子的相似度
     */
    public float sentenceSimilarity(List<String> sentence1Words, List<String> sentence2Words) {
        if (loadModel == false) {
            return 0;
        }
        if (sentence1Words.isEmpty() || sentence2Words.isEmpty()) {
            return 0;
        }
        float sum1 = 0;
        float sum2 = 0;
        int count1 = 0;
        int count2 = 0;
        for (int i = 0; i < sentence1Words.size(); i++) {
            if (getWordVector(sentence1Words.get(i)) != null) {
                count1++;
                sum1 += calMaxSimilarity(sentence1Words.get(i), sentence2Words);
            }
        }
        for (int i = 0; i < sentence2Words.size(); i++) {
            if (getWordVector(sentence2Words.get(i)) != null) {
                count2++;
                sum2 += calMaxSimilarity(sentence2Words.get(i), sentence1Words);
            }
        }
        return (sum1 + sum2) / (count1 + count2);
    }
    /**
     * 计算句子相似度(带权值)
     * 每一个词语都有一个对应的权值
     * @param sentence1Words 句子1词语列表
     * @param sentence2Words 句子2词语列表
     * @param weightVector1 句子1权值向量
     * @param weightVector2 句子2权值向量
     * @return 两个句子的相似度
     * @throws Exception 词语列表和权值向量长度不同
     */
    public float sentenceSimilarity(List<String> sentence1Words, List<String> sentence2Words, float[] weightVector1, float[] weightVector2) throws Exception {
        if (loadModel == false) {
            return 0;
        }
        if (sentence1Words.isEmpty() || sentence2Words.isEmpty()) {
            return 0;
        }
        if (sentence1Words.size() != weightVector1.length || sentence2Words.size() != weightVector2.length) {
            throw new Exception("length of word list and weight vector is different");
        }
        float sum1 = 0;
        float sum2 = 0;
        float divide1 = 0;
        float divide2 = 0;
        for (int i = 0; i < sentence1Words.size(); i++) {
            if (getWordVector(sentence1Words.get(i)) != null) {
                float wordMaxSimi = calMaxSimilarity(sentence1Words.get(i), sentence2Words);
                sum1 += wordMaxSimi * weightVector1[i];
                divide1 += weightVector1[i];
            }
        }
        for (int i = 0; i < sentence2Words.size(); i++) {
            if (getWordVector(sentence2Words.get(i)) != null) {
                float wordMaxSimi = calMaxSimilarity(sentence2Words.get(i), sentence1Words);
                sum2 += wordMaxSimi * weightVector2[i];
                divide2 += weightVector2[i];
            }
        }
        return (sum1 + sum2) / (divide1 + divide2);
    }
}
