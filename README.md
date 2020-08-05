# Word2Vec

[Word2VEC_java](https://github.com/NLPchina/Word2VEC_java) 是[孙健](http://www.nlpcn.org/)（ansj）编写的一个谷歌 word2vec 的 java 实现版本，同时支持读取由谷歌 c 语言版 [word2vec](https://github.com/svn2github/word2vec) 训练产生的模型。Word2Vec 则是对其的进一步包装，同时实现了常用的词语相似度和句子相似度计算。

## 下载

- [Word2Vec.jar](https://github.com/jsksxs360/Word2Vec/releases/)
- [维基百科中文语料(测试使用)](#3-维基百科中文语料已分好词)

## 如何使用

### 1. 新建 Word2Vec 对象，加载模型:

Word2Vec 同时支持加载谷歌模型和 Java 模型，分别使用 `loadGoogleModel()` 和 `loadJavaModel()` 函数读取。关于如何训练模型，请见[训练 word2vec 模型](#训练-word2vec-模型)。

```java
Word2Vec vec = new Word2Vec();
try {
	vec.loadGoogleModel("data/wiki_chinese_word2vec(Google).model");
} catch (IOException e) {
	e.printStackTrace();
}
```

### 2. 计算词语的语义相似度:

Word2Vec 提供了简单的词语相似度计算方法，可以直接调用 `wordSimilarity()` 函数计算两个词语的相似度，也可以通过 `getSimilarWords()` 函数获取与指定词语相似的词语。

```java
//计算词语相似度
System.out.println("狗|猫: " + vec.wordSimilarity("狗", "猫"));
System.out.println("计算机|电脑: " + vec.wordSimilarity("计算机", "电脑"));
System.out.println("计算机|人: " + vec.wordSimilarity("计算机", "人"));

//获取相似的词语
Set<WordEntry> similarWords = vec.getSimilarWords("漂亮", 10);
for(WordEntry word : similarWords) {
	System.out.println(word.name + " : " + word.score);
}
```

#### 输出结果：

```java
狗|猫: 0.71021223
计算机|电脑: 0.64130974
计算机|人: 0.060623944

//与"漂亮"语义相似的前10个词语:
可爱 : 0.7255844
时髦 : 0.68324685
脸蛋 : 0.6748609
打扮 : 0.6430359
乖巧 : 0.6370341
迷人 : 0.63440853
甜美 : 0.6340918
温柔 : 0.63337386
爽朗 : 0.63315964
聪明 : 0.6319053
```

### 3. 计算句子的语义相似度:

Word2Vec 还提供了计算句子相似度的方法 `fastSentenceSimilarity()` 和 `sentenceSimilarity()`，输入是两个分好词的句子(即两个词语列表)，还支持自定义每个词语在相似度计算中的权值(默认所有词语权值为1)。

为了方便测试，Word2Vec 对 [Ansj中文分词](https://github.com/NLPchina/ansj_seg) 进行了包装，提供了一个简易的分词工具类 `Segment`，用来获取分词后的词语列表和词性列表。在实际使用中，也可以使用自己的分词工具(比如 [Ansj中文分词](https://github.com/NLPchina/ansj_seg)、[斯坦福NLP](http://stanfordnlp.github.io/CoreNLP/)、[哈工大语言技术平台](https://github.com/HIT-SCIR/ltp)、[中科院分词系统](http://ictclas.nlpir.org/)、[HanLP](https://github.com/hankcs/HanLP) 等)。

```java
String s1 = "苏州有多条公路正在施工，造成局部地区汽车行驶非常缓慢。";
String s2 = "苏州最近有多条公路在施工，导致部分地区交通拥堵，汽车难以通行。";
String s3 = "苏州是一座美丽的城市，四季分明，雨量充沛。";

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
System.out.println("s1|s1: " + vec.sentenceSimilarity(wordList1, wordList1));
System.out.println("s1|s2: " + vec.sentenceSimilarity(wordList1, wordList2));
System.out.println("s1|s3: " + vec.sentenceSimilarity(wordList1, wordList3));

//句子相似度(名词、动词权值设为1，其他设为0.8)
float[] weightArray1 = Segment.getPOSWeightArray(Segment.getPOS(s1));
float[] weightArray2 = Segment.getPOSWeightArray(Segment.getPOS(s2));
float[] weightArray3 = Segment.getPOSWeightArray(Segment.getPOS(s3));
System.out.println("s1|s1: " + vec.sentenceSimilarity(wordList1, wordList1, weightArray1, weightArray1));
System.out.println("s1|s2: " + vec.sentenceSimilarity(wordList1, wordList2, weightArray1, weightArray2));
System.out.println("s1|s3: " + vec.sentenceSimilarity(wordList1, wordList3, weightArray1, weightArray3));
```

#### 输出结果：

```java
//快速句子相似度
s1|s1: 1.0000006
s1|s2: 0.9144124
s1|s3: 0.6289892

//句子相似度:
s1|s1: 1.0
s1|s2: 0.7888574
s1|s3: 0.4520114

//句子相似度(名词、动词权值设为1，其他设为0.8):
s1|s1: 1.0
s1|s2: 0.7922064
s1|s3: 0.45209178
```

**注意：** 加载不同的 word2vec 模型，计算相似度的结果也不同。

## 训练 word2vec 模型

### 1. 训练 Java 版模型

[Word2VEC_java](https://github.com/NLPchina/Word2VEC_java) 实现了 java 下的 word2vec 模型训练，Word2Vec 对其封装，也提供了相应的调用接口：

```java
void trainJavaModel(String trainFilePath, String modelFilePath)
```
- trainFilePath: 训练文件路径
- modelFilePath: 模型文件路径

**trainJavaModel** 为静态函数，可以直接调用：

```
try {
	Word2Vec.trainJavaModel("data/train.txt", "data/test.model");
} catch (IOException e) {
	e.printStackTrace();
}
```

### 2. 训练 Google 版模型

Google 实现的 C 语言版的 word2vec 是目前公认的准确率最高的 word2vec 版本，因而训练更推荐直接使用 google 的原版。ansj 实现的 java 版也支持训练模型，但是准确率未和 Google 版做过比较。

下面提供两个 GitHub 上克隆的 Google word2vec 项目：

- [word2vec:](https://github.com/svn2github/word2vec) Google 原版的克隆
- [word2vec:](https://github.com/dav/word2vec) 在 Google 原版上稍作修改，可以在 MacOS 上编译。

下载后使用 `make` 命令编译，之后使用编译出的 **word2vec** 来训练模型：

```bash
./word2vec -train $TEXT_DATA -output $VECTOR_DATA -cbow 0 -size 200 -window 5 -negative 0 -hs 1 -sample 1e-3 -threads 12 -binary 1
```
简单说明一下：  
`TEXT_DATA` 为训练文本文件路径，词之间使用空格分隔；`VECTOR_DATA` 为输出的模型文件；不使用 cbow 模型，默认为 Skip-Gram 模型；每个单词的向量维度是 200；训练的窗口大小为 5；不使用 NEG 方法，使用 HS 方法；`-sampe` 指的是采样的阈值，如果一个词语在训练样本中出现的频率越大，那么就越会被采样；`-binary` 为 1 指的是结果二进制存储，为 0 是普通存储。

### 3. 维基百科中文语料(已分好词)

详情请见 [**Word2Vector 模型**](https://github.com/jsksxs360/AHANLP/blob/master/github/w2v.md)，可以下载训练好的 Google 版模型及对应的训练语料。

如果想对最新的维基百科中文语料进行处理，可以参考[《维基百科中文语料库词向量的训练》](http://xiaosheng.run/2017/05/26/article63/)。

### 4. Python 版本 

Word2Vec 也提供了 Python 的实现版本（需要安装 [gensim](https://radimrehurek.com/gensim/) 依赖包），只需下载  [Word2Vec.py](https://github.com/jsksxs360/Word2Vec/tree/master/python/Word2Vec.py)，然后 `from Word2Vec import Word2Vec` 引入项目即可：

```python
from Word2Vec import Word2Vec

w2v = Word2Vec('/Users/jim/Desktop/wiki_chinese_word2vec.model', kind='bin')

print('狗|猫: ', w2v.word_similarity('狗', '猫'))
print('计算机|电脑: ', w2v.word_similarity('计算机', '电脑'))
print('计算机|人: ', w2v.word_similarity('计算机', '人'))
```

详细示例可以参见 [test.py](https://github.com/jsksxs360/Word2Vec/tree/master/python/test.py)。

## 参考

- [word2vec使用指导](http://blog.csdn.net/zhoubl668/article/details/24314769)
- [word2vec原理推导与代码分析](http://www.hankcs.com/nlp/word2vec.html)
