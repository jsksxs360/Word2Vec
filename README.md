# Word2Vec

[Word2VEC_java](https://github.com/NLPchina/Word2VEC_java) 是[孙健](http://www.nlpcn.org/)（ansj）编写的一个谷歌 word2vec 的 java 实现版本，同时支持读取由谷歌 c 语言版 [word2vec](https://github.com/svn2github/word2vec) 训练产生的模型。Word2Vec 则是对其的进一步包装，同时实现了常用的词语相似度和句子相似度计算。

## 下载

- [Word2Vec.jar](https://github.com/jsksxs360/Word2Vec/releases/)
- [维基百科中文语料(测试使用)](#3-维基百科中文语料已分好词)

## 如何使用

### 1. 新建 Word2Vec 对象，加载模型:

Word2Vec 同时支持加载谷歌模型和 Java 模型，分别使用 `loadGoogleModel()` 和 `loadJavaModel()` 函数读取。关于如何训练模型，请见[**训练 word2vec 模型**](#训练-word2vec-模型)。

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

Word2Vec 目前提供了两种计算句子相似度的方法，`sentenceSimilarity()` 和 `easySentenceSimilarity()`。它们的输入都是分好词的句子，即句子的词语列表。其中 `easySentenceSimilarity()` 还支持对词语赋予不同的权值(默认权值相同为1)。

为了方便测试，Word2Vec 对 [HanLP](https://github.com/hankcs/HanLP) 便携版进行了包装，提供了一个简易的分词工具类 `Segment`，用来获取分词后的词语列表和词性列表。由于 HanLP 便携版分词能力有限，在实际使用中，建议使用自己的分词工具(比如 [HanLP标准版](https://github.com/hankcs/HanLP)、[斯坦福NLP](http://stanfordnlp.github.io/CoreNLP/)、[哈工大语言技术平台](https://github.com/HIT-SCIR/ltp)、[中科院分词系统](http://ictclas.nlpir.org/) 等)。

```java
String s1 = "苏州有多条公路正在施工，造成局部地区汽车行驶非常缓慢。";
String s2 = "苏州最近有多条公路在施工，导致部分地区交通拥堵，汽车难以通行。";
String s3 = "苏州是一座美丽的城市，四季分明，雨量充沛。";
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
```

#### 输出结果：

```java
//标准句子相似度
1.0
0.88231444
0.6493894

//简易句子相似度
1.0
0.7413265
0.41655433

//简易句子相似度(名词、动词权值设为1，其他设为0.8)
1.0
0.74916583
0.41593283
```

**注意：**加载不同的 word2vec 模型，计算相似度的结果也不同。

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

- [`wiki_chinese_preprocessed.simplied.txt`](https://pan.baidu.com/s/1dFgIbTZ)（1GB）

对应训练出的 Google 版模型：

- [`wiki_chinese_word2vec(Google).model`](https://pan.baidu.com/s/1kUD0jzh)(516.4MB)

## 参考

- [word2vec使用指导](http://blog.csdn.net/zhoubl668/article/details/24314769)
- [word2vec原理推导与代码分析](http://www.hankcs.com/nlp/word2vec.html)