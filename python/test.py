#coding:utf-8

from Word2Vec import Word2Vec
import jieba

w2v = Word2Vec('/Users/jim/Desktop/wiki_chinese_word2vec.model', kind='bin')

print('狗|猫: ', w2v.word_similarity('狗', '猫'))
print('计算机|电脑: ', w2v.word_similarity('计算机', '电脑'))
print('计算机|人: ', w2v.word_similarity('计算机', '人'))

print('\n与"漂亮"语义相似的前10个词语:')
similarWords = w2v.get_similar_Words('漂亮', 10)
for word, simi in similarWords:
	print(word, ':', simi)
	
s1 = '苏州有多条公路正在施工，造成局部地区汽车行驶非常缓慢。'
s2 = '苏州最近有多条公路在施工，导致部分地区交通拥堵，汽车难以通行。'
s3 = '苏州是一座美丽的城市，四季分明，雨量充沛。'
wordList1 = list(jieba.cut(s1))
wordList2 = list(jieba.cut(s2))
wordList3 = list(jieba.cut(s3))

print("s1|s1: " + str(w2v.sentence_similarity(wordList1, wordList1)))
print("s1|s2: " + str(w2v.sentence_similarity(wordList1, wordList2)))
print("s1|s3: " + str(w2v.sentence_similarity(wordList1, wordList3)))