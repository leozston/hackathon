package com.ansj.vec;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.ansj.vec.domain.WordEntry;
import com.ansj.vec.model.MatchResultBean;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Word2VEC {
	private static int mostSimilarLength = 100;

	public static void main(String[] args) throws IOException {

		Word2VEC word2VEC = new Word2VEC();
//		word2VEC.loadJavaModelSelf("/Users/liweipeng/myProject/hackathon/vectordata//wordvector.txt");
		word2VEC.loadJavaModelSelf("/Users/lvlonglong/hacker2017/wordvector_loupan.txt");
		System.out.println("词的数量:" + word2VEC.wordMap.size());
		//获取词典
		List<String> dictionary = Lists.newArrayList();
		for (Entry<String, float[]> entry : word2VEC.wordMap.entrySet()) {
			dictionary.add(entry.getKey());
		}

		BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));

		List<String> groupInfoDictionary = Lists.newArrayList();
		groupInfoDictionary.add("价格");
		groupInfoDictionary.add("户型");
		groupInfoDictionary.add("周边");
		groupInfoDictionary.add("位置");
		groupInfoDictionary.add("简介");


		Map<String, float[]> groupDicVector = Maps.newHashMap();
		for (String s : groupInfoDictionary) {
			groupDicVector.put(s, SentenseHandler.getSentenceVerctor(s, word2VEC.wordMap));
		}


//		while (true)  {
//			System.out.print("请输入一个字符串(-1结束)：");
//			String sentence = strin.readLine();
//			if (!sentence.equals("-1")){
//				List<MatchResultBean> matchResultBeanList = Lists.newArrayList();
//				float[] sentenceVector = SentenseHandler.getSentenceVerctor(sentence, word2VEC.wordMap);
////				System.out.println("相近词：" + word2VEC.distance(sentence));
//				for (Entry<String, float[]> entry : groupDicVector.entrySet()) {
//					matchResultBeanList.add(MatchResultBean.of(entry.getKey(), SentenseHandler.getSimilar(sentenceVector, entry.getValue())));
//				}
//				Collections.sort(matchResultBeanList);
//
//				for (MatchResultBean m : matchResultBeanList) {
//					System.out.println(m);
//				}
//			}else{
//				break;
//			}
//		}

		while (true)  {
			System.out.print("请输入一个字符串(-1结束)：");
			String sentence = strin.readLine();
			if (!sentence.equals("-1")){
				List<MatchResultBean> matchResultBeanList = Lists.newArrayList();
				//获取输入语句的词向量
				Map<String, Float> inputVectorMap = SentenseHandler.getSentenceSimilarVector(sentence, word2VEC.wordMap, mostSimilarLength);
				for (Entry<String, float[]> entry : groupDicVector.entrySet()) {
					Map<String, Float> currentPropertyVectorMap = SentenseHandler.getSentenceSimilarVector(entry.getKey(), word2VEC.wordMap, mostSimilarLength);
					matchResultBeanList.add(MatchResultBean.of(entry.getKey(), SentenseHandler.getSentenceSimilar(inputVectorMap, currentPropertyVectorMap)));
				}
				Collections.sort(matchResultBeanList);

				for (MatchResultBean m : matchResultBeanList) {
					System.out.println(m);
				}
			}else{
				break;
			}
		}
	}

	private HashMap<String, float[]> wordMap = new HashMap<String, float[]>();

	private int words;
	private int size;
	private int topNSize = 10;




	/**
	 * 加载模型
	 * 
	 * @param path
	 *            模型的路径
	 * @throws IOException
	 */
	public void loadGoogleModel(String path) throws IOException {
		DataInputStream dis = null;
		BufferedInputStream bis = null;
		double len = 0;
		float vector = 0;
		try {
			bis = new BufferedInputStream(new FileInputStream(path));
			dis = new DataInputStream(bis);
			// //读取词数
			words = Integer.parseInt(readString(dis));
			// //大小
			size = Integer.parseInt(readString(dis));
			String word;
			float[] vectors = null;
			for (int i = 0; i < words; i++) {
				word = readString(dis);
				vectors = new float[size];
				len = 0;
				for (int j = 0; j < size; j++) {
					vector = readFloat(dis);
					len += vector * vector;
					vectors[j] = (float) vector;
				}
				len = Math.sqrt(len);

				for (int j = 0; j < size; j++) {
					vectors[j] /= len;
				}

				wordMap.put(word, vectors);
				dis.read();
			}
		} finally {
			bis.close();
			dis.close();
		}
	}

	/**
	 * 加载模型
	 * 
	 * @param path
	 *            模型的路径
	 * @throws IOException
	 */
	public void loadJavaModel(String path) throws IOException {
		try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)))) {
			words = dis.readInt();
			size = dis.readInt();

			float vector = 0;

			String key = null;
			float[] value = null;
			for (int i = 0; i < words; i++) {
				double len = 0;
				key = dis.readUTF();
				value = new float[size];
				for (int j = 0; j < size; j++) {
					vector = dis.readFloat();
					len += vector * vector;
					value[j] = vector;
				}

				len = Math.sqrt(len);

				for (int j = 0; j < size; j++) {
					value[j] /= len;
				}
				wordMap.put(key, value);
			}

		}
	}

	private void loadJavaModelSelf(String path) {
		FileReader reader = null;
		try {
			reader = new FileReader(path);
		} catch (FileNotFoundException e) {
			System.out.println("***********文件路径错误*********");
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(reader);
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				List<String> currentLineUnStopsWords = Lists.newArrayList();
				String[] list = line.split(" ");
				if (list.length != 2) {
					continue;
				}
				String scoreString = list[1];
				String[] scoreList = scoreString.split(",");
				float[] score = new float[Learn.layerSize];
				for (int i = 0; i < scoreList.length; i++) {
					score[i] = Float.parseFloat(scoreList[i]);
				}
				wordMap.put(list[0], score);
			}
		} catch (IOException e) {
			System.out.println("***********读取文件失败************");
			e.printStackTrace();
		}
		try {
			br.close();
			reader.close();
		} catch (IOException e) {
			System.out.println("***********关闭读文件流失败************");
			e.printStackTrace();
		}
	}

	private static final int MAX_SIZE = 50;

	/**
	 * 近义词
	 * 
	 * @return
	 */
	public TreeSet<WordEntry> analogy(String word0, String word1, String word2) {
		float[] wv0 = getWordVector(word0);
		float[] wv1 = getWordVector(word1);
		float[] wv2 = getWordVector(word2);

		if (wv1 == null || wv2 == null || wv0 == null) {
			return null;
		}
		float[] wordVector = new float[size];
		for (int i = 0; i < size; i++) {
			wordVector[i] = wv1[i] - wv0[i] + wv2[i];
		}
		float[] tempVector;
		String name;
		List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
		for (Entry<String, float[]> entry : wordMap.entrySet()) {
			name = entry.getKey();
			if (name.equals(word0) || name.equals(word1) || name.equals(word2)) {
				continue;
			}
			float dist = 0;
			tempVector = entry.getValue();
			for (int i = 0; i < wordVector.length; i++) {
				dist += wordVector[i] * tempVector[i];
			}
			insertTopN(name, dist, wordEntrys);
		}
		return new TreeSet<WordEntry>(wordEntrys);
	}

	private void insertTopN(String name, float score, List<WordEntry> wordsEntrys) {
		// TODO Auto-generated method stub
		if (wordsEntrys.size() < topNSize) {
			wordsEntrys.add(new WordEntry(name, score));
			return;
		}
		float min = Float.MAX_VALUE;
		int minOffe = 0;
		for (int i = 0; i < topNSize; i++) {
			WordEntry wordEntry = wordsEntrys.get(i);
			if (min > wordEntry.score) {
				min = wordEntry.score;
				minOffe = i;
			}
		}

		if (score > min) {
			wordsEntrys.set(minOffe, new WordEntry(name, score));
		}

	}

	public Set<WordEntry> distance(String queryWord) {

		float[] center = wordMap.get(queryWord);
		if (center == null) {
			return Collections.emptySet();
		}

		int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
		TreeSet<WordEntry> result = new TreeSet<WordEntry>();

		double min = Float.MIN_VALUE;
		for (Map.Entry<String, float[]> entry : wordMap.entrySet()) {
			float[] vector = entry.getValue();
			float dist = 0;
			for (int i = 0; i < vector.length; i++) {
				dist += center[i] * vector[i];
			}

			if (dist > min) {
				result.add(new WordEntry(entry.getKey(), dist));
				if (resultSize < result.size()) {
					result.pollLast();
				}
				min = result.last().score;
			}
		}
		result.pollFirst();

		return result;
	}

	public Set<WordEntry> distance(List<String> words) {

		float[] center = null;
		for (String word : words) {
			center = sum(center, wordMap.get(word));
		}

		if (center == null) {
			return Collections.emptySet();
		}

		int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
		TreeSet<WordEntry> result = new TreeSet<WordEntry>();

		double min = Float.MIN_VALUE;
		for (Map.Entry<String, float[]> entry : wordMap.entrySet()) {
			float[] vector = entry.getValue();
			float dist = 0;
			for (int i = 0; i < vector.length; i++) {
				dist += center[i] * vector[i];
			}

			if (dist > min) {
				result.add(new WordEntry(entry.getKey(), dist));
				if (resultSize < result.size()) {
					result.pollLast();
				}
				min = result.last().score;
			}
		}
		result.pollFirst();

		return result;
	}

	private float[] sum(float[] center, float[] fs) {
		// TODO Auto-generated method stub

		if (center == null && fs == null) {
			return null;
		}

		if (fs == null) {
			return center;
		}

		if (center == null) {
			return fs;
		}

		for (int i = 0; i < fs.length; i++) {
			center[i] += fs[i];
		}

		return center;
	}

	/**
	 * 得到词向量
	 * 
	 * @param word
	 * @return
	 */
	public float[] getWordVector(String word) {
		return wordMap.get(word);
	}

	public static float readFloat(InputStream is) throws IOException {
		byte[] bytes = new byte[4];
		is.read(bytes);
		return getFloat(bytes);
	}

	/**
	 * 读取一个float
	 * 
	 * @param b
	 * @return
	 */
	public static float getFloat(byte[] b) {
		int accum = 0;
		accum = accum | (b[0] & 0xff) << 0;
		accum = accum | (b[1] & 0xff) << 8;
		accum = accum | (b[2] & 0xff) << 16;
		accum = accum | (b[3] & 0xff) << 24;
		return Float.intBitsToFloat(accum);
	}

	/**
	 * 读取一个字符串
	 * 
	 * @param dis
	 * @return
	 * @throws IOException
	 */
	private static String readString(DataInputStream dis) throws IOException {
		// TODO Auto-generated method stub
		byte[] bytes = new byte[MAX_SIZE];
		byte b = dis.readByte();
		int i = -1;
		StringBuilder sb = new StringBuilder();
		while (b != 32 && b != 10) {
			i++;
			bytes[i] = b;
			b = dis.readByte();
			if (i == 49) {
				sb.append(new String(bytes));
				i = -1;
				bytes = new byte[MAX_SIZE];
			}
		}
		sb.append(new String(bytes, 0, i + 1));
		return sb.toString();
	}

	public int getTopNSize() {
		return topNSize;
	}

	public void setTopNSize(int topNSize) {
		this.topNSize = topNSize;
	}

	public HashMap<String, float[]> getWordMap() {
		return wordMap;
	}

	public int getWords() {
		return words;
	}

	public int getSize() {
		return size;
	}

	public void setWordMap(HashMap<String, float[]> wordMap) {
		this.wordMap = wordMap;
	}
}
