# Word2Vec

[Word2VEC_java](https://github.com/NLPchina/Word2VEC_java) 是[孙健](http://www.nlpcn.org/)（ansj）编写的一个谷歌 word2vec 的 java 实现版本，同时支持读取由谷歌 c 语言版 [word2vec](https://github.com/svn2github/word2vec) 训练产生的模型。Word2Vec 则是对其的进一步包装，同时实现了常用的词语相似度和句子相似度计算。

## 如何使用

```java
Word2Vec vec = new Word2Vec();
try {
	vec.loadGoogleModel("data/review.model");
} catch (IOException e) {
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
}
```

输出结果：

```java
0.7751967 //"狗"和"猫"的相似度
0.40951487 //"计算机"和"硬盘"的相似度
-0.13773794 //"计算机"和"人"的相似度

1.0
0.873214
0.74950576
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

### 3. 维基百科中文语料库(已分好词)

- [`wiki_chinese_preprocessed.simplied.txt`](https://pan.baidu.com/s/1dFgIbTZ)（1GB）

## 参考

- [word2vec使用指导](http://blog.csdn.net/zhoubl668/article/details/24314769)