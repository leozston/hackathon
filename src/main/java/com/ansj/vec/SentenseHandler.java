package com.ansj.vec;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.ansj.vec.Learn;
import com.ansj.vec.model.MatchResultBean;
import com.ansj.vec.preprocess.ReduceFile;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huaban.analysis.jieba.JiebaSegmenter;

/**
 * Created by liweipeng on 2017/4/7.
 */
public class SentenseHandler {
    /**
     * 获取整个句子的词向量
     * */
    public static float[] getSentenceVerctor(String sentense, Map<String, float[]> wordVectorMap, List<String> extraDicWhenQuery){
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<String> sentenseLineWords = segmenter.sentenceProcess(sentense);
        List<String> sentenseUnStopsWords = Lists.newArrayList();
        for (String s : sentenseLineWords) {
            if (!ReduceFile.stop_words.contains(s) && !extraDicWhenQuery.contains(s)) {
                sentenseUnStopsWords.add(s);
            }
        }
        float[] senVerctor = new float[Learn.layerSize];
        for(String word : sentenseUnStopsWords){
            if (wordVectorMap.containsKey(word)){
                float[] wordVector = wordVectorMap.get(word);
                for (int i = 0; i < Learn.layerSize; i++){
                    senVerctor[i] += wordVector[i];
                }
            }
        }
        return senVerctor;
    }

    /**
     * 获取句子中每个词的词向量
     * */
    public static float[][] getSentenceDicVector(String sentense, Map<String, float[]> wordVectorMap, List<String> extraDicWhenQuery){
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<String> sentenseLineWords = segmenter.sentenceProcess(sentense);
        List<String> sentenseUnStopsWords = Lists.newArrayList();
        for (String s : sentenseLineWords) {
            if (!ReduceFile.stop_words.contains(s) && !extraDicWhenQuery.contains(s)) {
//            if (!ReduceFile.stop_words.contains(s)) {
                sentenseUnStopsWords.add(s);
            }
        }
        int sentLength = 0;
        for (String s : sentenseUnStopsWords){
            if (wordVectorMap.containsKey(s)) {
                sentLength++;
            }
        }
        float[][] sentDicVector = new float[sentLength][Learn.layerSize];
        int index = 0;
        for(String word : sentenseUnStopsWords){
            if (wordVectorMap.containsKey(word)){
                float[] wordVector = wordVectorMap.get(word);
                sentDicVector[index] = wordVector;
                index++;
            }
        }
        return sentDicVector;
    }

    /**
     * 获取句子相似的词向量组，这个词向量组是通过句子中的每个词取其最相似的词
     * */
    public static Map<String, Float> getSentenceSimilarVector(String sentence, Map<String, float[]> wordVectorMap, int length, List<String> extraDicWhenQuery) {
        Map<String, Float> resultMap = Maps.newHashMap();
        List<MatchResultBean> resultBeanList = Lists.newArrayList();
        float[][] sentenceDicVector = getSentenceDicVector(sentence, wordVectorMap, extraDicWhenQuery);
        for (int i = 0; i <sentenceDicVector.length; i++) {
            List<MatchResultBean> tmpBeans = Lists.newArrayList();
            for (Map.Entry<String, float[]> entry : wordVectorMap.entrySet()) {
                tmpBeans.add(MatchResultBean.of(entry.getKey(), getSimilar(sentenceDicVector[i], entry.getValue())));
            }
            Collections.sort(tmpBeans);
            resultBeanList.addAll(tmpBeans.subList(0, length));
        }

        for (MatchResultBean m : resultBeanList) {
            if (!resultMap.containsKey(m.getMatchedName())) {
                resultMap.put(m.getMatchedName(), m.getScore());
            } else {
                resultMap.put(m.getMatchedName(), m.getScore() + resultMap.get(m.getMatchedName()));
            }
        }
        return resultMap;
    }

    /**
     * 获取两个词向量组的相似度
     * */
    public static float getSentenceSimilar(Map<String, Float> first, Map<String, Float> second) {
        float sum = 0;
        float firstLength = 0;
        float secondLength = 0;
        for (Map.Entry<String, Float> entry : first.entrySet()) {
            if (second.containsKey(entry.getKey())) {
                sum += entry.getValue() * second.get(entry.getKey());
            }
            firstLength += entry.getValue() * entry.getValue();
        }

        for (Map.Entry<String, Float> entry : second.entrySet()) {
            secondLength += entry.getValue() * entry.getValue();
        }

        if (firstLength == 0 || secondLength == 0) {
            return 0;
        }
        return (float) (sum / Math.sqrt(firstLength * secondLength));
    }


    /**
     * 两个向量的相似度
     * */
    public static float getSimilar(float[] first, float[] second) {
        if (first.length != second.length) {
            return 0;
        }
        float sum = 0;
        float firstLength = 0;
        float secondLength = 0;
        for (int i = 0; i < first.length; i++) {
            sum += first[i] * second[i];
            firstLength += first[i] * first[i];
            secondLength += second[i] * second[i];
        }
        if (firstLength == 0 || secondLength == 0) {
            return 0;
        }
        return (float)(sum / (Math.sqrt(firstLength * secondLength)));
    }
}
