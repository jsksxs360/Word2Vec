from gensim.models import KeyedVectors

class Word2Vec():
	def __init__(self, modelPath, kind='bin'):
		"""
		创建Word2Vec对象
		
		modelPath: 模型路径
		kind: 模型类型
			bin: 二进制文件
			txt: 文本文件
		return: 无
		"""
		
		if kind != 'bin':
			kind = False
		else:
			kind = True
		print('loading word2vector model...')
		self.model = KeyedVectors.load_word2vec_format(modelPath, binary=kind, unicode_errors='ignore')
	
	def get_word_vector(self, word):
		"""
		获得词向量
		
		word: 词语
		return: 词向量
		"""
		
		if word in self.model:
			return self.model[word]
		return None
		
	def word_similarity(self, word1, word2):
		"""
		计算词语相似度
		
		word1: 词语1
		word2: 词语2
		return: 词语1与词语2的相似度
		"""
		
		if word1 not in self.model or word2 not in self.model:
			return 0
		return self.model.similarity(word1, word2)
		
	def get_similar_Words(self, word, maxReturnNum):
		"""
		获得语义相似的词语
		
		word: 词语
		maxReturnNum: 最大返回词语数量
		return: 词语及相似度 [(word, simi)...]
		"""
		
		if word not in self.model:
			return None
		return self.model.similar_by_word(word, topn=maxReturnNum)
	
	def __cal_max_similarity(self, centerWord, wordList):
		"""
		计算词语与词语列表中词语的最大相似度
		
		centerWord: 词语
		wordList: 词语列表
		return: 词语与词语列表中词语的最大相似度
		"""
		
		maxSimi = -1
		if centerWord in wordList:
			return 1
		else:
			for word in wordList:
				temp = self.word_similarity(centerWord, word)
				if temp == 0: continue
				if temp > maxSimi: maxSimi = temp
		if maxSimi == -1: return 0
		return maxSimi
		
	def sentence_similarity(self, sentence1Words, sentence2Words):
		"""
		计算句子相似度
		
		sentence1Words: 句子1词语列表
		sentence2Words: 句子2词语列表
		return: 两个句子的相似度
		"""
		
		if len(sentence1Words) == 0 or len(sentence2Words) == 0:
			return 0
		vector1 = [self.__cal_max_similarity(word, sentence2Words) for word in sentence1Words]
		vector2 = [self.__cal_max_similarity(word, sentence1Words) for word in sentence2Words]
		return (sum(vector1) + sum(vector2)) / (len(vector1) + len(vector2))
	
	def sentence_weight_similarity(self, sentence1Words, sentence2Words, weightVector1, weightVector2):
		"""
		计算句子相似度(带权值)
		每一个词语都有一个对应的权值
		
		sentence1Words: 句子1词语列表
		sentence2Words: 句子2词语列表
		weightVector1: 句子1权值向量
		weightVector2: 句子2权值向量
		return: 两个句子的相似度
		"""
		
		if len(sentence1Words) == 0 or len(sentence2Words) == 0:
			return 0
		if len(sentence1Words) != len(weightVector1) or len(sentence2Words) != len(weightVector2):
			raise Exception('length of word list and weight vector is different')
		vector1 = [self.__cal_max_similarity(word, sentence2Words) * weight for word, weight in zip(sentence1Words, weightVector1)]
		vector2 = [self.__cal_max_similarity(word, sentence1Words) * weight for word, weight in zip(sentence2Words, weightVector2)]
		return (sum(vector1) + sum(vector2)) / (sum(weightVector1) + sum(weightVector2))