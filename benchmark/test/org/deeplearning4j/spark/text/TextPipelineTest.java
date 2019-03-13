/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.deeplearning4j.spark.text;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.deeplearning4j.models.word2vec.Huffman;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.spark.models.embeddings.word2vec.FirstIterationFunction;
import org.deeplearning4j.spark.models.embeddings.word2vec.FirstIterationFunctionAdapter;
import org.deeplearning4j.spark.models.embeddings.word2vec.MapToPairFunction;
import org.deeplearning4j.spark.models.embeddings.word2vec.Word2Vec;
import org.deeplearning4j.spark.text.functions.CountCumSum;
import org.deeplearning4j.spark.text.functions.TextPipeline;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.primitives.Counter;
import org.nd4j.linalg.primitives.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;


/**
 *
 *
 * @author Jeffrey Tang
 */
public class TextPipelineTest extends BaseSparkTest {
    private List<String> sentenceList;

    private SparkConf conf;

    private Word2Vec word2vec;

    private Word2Vec word2vecNoStop;

    private static final Logger log = LoggerFactory.getLogger(TextPipeline.class);

    @Test
    public void testTokenizer() throws Exception {
        JavaSparkContext sc = getContext();
        JavaRDD<String> corpusRDD = getCorpusRDD(sc);
        Broadcast<Map<String, Object>> broadcastTokenizerVarMap = sc.broadcast(word2vec.getTokenizerVarMap());
        TextPipeline pipeline = new TextPipeline(corpusRDD, broadcastTokenizerVarMap);
        JavaRDD<List<String>> tokenizedRDD = pipeline.tokenize();
        Assert.assertEquals(2, tokenizedRDD.count());
        Assert.assertEquals(Arrays.asList("this", "is", "a", "strange", "strange", "world"), tokenizedRDD.first());
        sc.stop();
    }

    @Test
    public void testWordFreqAccIdentifyStopWords() throws Exception {
        JavaSparkContext sc = getContext();
        JavaRDD<String> corpusRDD = getCorpusRDD(sc);
        Broadcast<Map<String, Object>> broadcastTokenizerVarMap = sc.broadcast(word2vec.getTokenizerVarMap());
        TextPipeline pipeline = new TextPipeline(corpusRDD, broadcastTokenizerVarMap);
        JavaRDD<List<String>> tokenizedRDD = pipeline.tokenize();
        JavaRDD<Pair<List<String>, AtomicLong>> sentenceWordsCountRDD = pipeline.updateAndReturnAccumulatorVal(tokenizedRDD);
        Counter<String> wordFreqCounter = pipeline.getWordFreqAcc().value();
        Assert.assertEquals(wordFreqCounter.getCount("STOP"), 4, 0);
        Assert.assertEquals(wordFreqCounter.getCount("strange"), 2, 0);
        Assert.assertEquals(wordFreqCounter.getCount("flowers"), 1, 0);
        Assert.assertEquals(wordFreqCounter.getCount("world"), 1, 0);
        Assert.assertEquals(wordFreqCounter.getCount("red"), 1, 0);
        List<Pair<List<String>, AtomicLong>> ret = sentenceWordsCountRDD.collect();
        Assert.assertEquals(ret.get(0).getFirst(), Arrays.asList("this", "is", "a", "strange", "strange", "world"));
        Assert.assertEquals(ret.get(1).getFirst(), Arrays.asList("flowers", "are", "red"));
        Assert.assertEquals(ret.get(0).getSecond().get(), 6);
        Assert.assertEquals(ret.get(1).getSecond().get(), 3);
        sc.stop();
    }

    @Test
    public void testWordFreqAccNotIdentifyingStopWords() throws Exception {
        JavaSparkContext sc = getContext();
        // word2vec.setRemoveStop(false);
        JavaRDD<String> corpusRDD = getCorpusRDD(sc);
        Broadcast<Map<String, Object>> broadcastTokenizerVarMap = sc.broadcast(word2vecNoStop.getTokenizerVarMap());
        TextPipeline pipeline = new TextPipeline(corpusRDD, broadcastTokenizerVarMap);
        JavaRDD<List<String>> tokenizedRDD = pipeline.tokenize();
        pipeline.updateAndReturnAccumulatorVal(tokenizedRDD);
        Counter<String> wordFreqCounter = pipeline.getWordFreqAcc().value();
        Assert.assertEquals(wordFreqCounter.getCount("is"), 1, 0);
        Assert.assertEquals(wordFreqCounter.getCount("this"), 1, 0);
        Assert.assertEquals(wordFreqCounter.getCount("are"), 1, 0);
        Assert.assertEquals(wordFreqCounter.getCount("a"), 1, 0);
        Assert.assertEquals(wordFreqCounter.getCount("strange"), 2, 0);
        Assert.assertEquals(wordFreqCounter.getCount("flowers"), 1, 0);
        Assert.assertEquals(wordFreqCounter.getCount("world"), 1, 0);
        Assert.assertEquals(wordFreqCounter.getCount("red"), 1, 0);
        sc.stop();
    }

    @Test
    public void testWordFreqAccIdentifyingStopWords() throws Exception {
        JavaSparkContext sc = getContext();
        // word2vec.setRemoveStop(false);
        JavaRDD<String> corpusRDD = getCorpusRDD(sc);
        Broadcast<Map<String, Object>> broadcastTokenizerVarMap = sc.broadcast(word2vec.getTokenizerVarMap());
        TextPipeline pipeline = new TextPipeline(corpusRDD, broadcastTokenizerVarMap);
        JavaRDD<List<String>> tokenizedRDD = pipeline.tokenize();
        pipeline.updateAndReturnAccumulatorVal(tokenizedRDD);
        Counter<String> wordFreqCounter = pipeline.getWordFreqAcc().value();
        Assert.assertEquals(wordFreqCounter.getCount("is"), 0, 0);
        Assert.assertEquals(wordFreqCounter.getCount("this"), 0, 0);
        Assert.assertEquals(wordFreqCounter.getCount("are"), 0, 0);
        Assert.assertEquals(wordFreqCounter.getCount("a"), 0, 0);
        Assert.assertEquals(wordFreqCounter.getCount("STOP"), 4, 0);
        Assert.assertEquals(wordFreqCounter.getCount("strange"), 2, 0);
        Assert.assertEquals(wordFreqCounter.getCount("flowers"), 1, 0);
        Assert.assertEquals(wordFreqCounter.getCount("world"), 1, 0);
        Assert.assertEquals(wordFreqCounter.getCount("red"), 1, 0);
        sc.stop();
    }

    @Test
    public void testFilterMinWordAddVocab() throws Exception {
        JavaSparkContext sc = getContext();
        JavaRDD<String> corpusRDD = getCorpusRDD(sc);
        Broadcast<Map<String, Object>> broadcastTokenizerVarMap = sc.broadcast(word2vec.getTokenizerVarMap());
        TextPipeline pipeline = new TextPipeline(corpusRDD, broadcastTokenizerVarMap);
        JavaRDD<List<String>> tokenizedRDD = pipeline.tokenize();
        pipeline.updateAndReturnAccumulatorVal(tokenizedRDD);
        Counter<String> wordFreqCounter = pipeline.getWordFreqAcc().value();
        pipeline.filterMinWordAddVocab(wordFreqCounter);
        VocabCache<VocabWord> vocabCache = pipeline.getVocabCache();
        Assert.assertTrue((vocabCache != null));
        VocabWord redVocab = vocabCache.tokenFor("red");
        VocabWord flowerVocab = vocabCache.tokenFor("flowers");
        VocabWord worldVocab = vocabCache.tokenFor("world");
        VocabWord strangeVocab = vocabCache.tokenFor("strange");
        Assert.assertEquals(redVocab.getWord(), "red");
        Assert.assertEquals(redVocab.getElementFrequency(), 1, 0);
        Assert.assertEquals(flowerVocab.getWord(), "flowers");
        Assert.assertEquals(flowerVocab.getElementFrequency(), 1, 0);
        Assert.assertEquals(worldVocab.getWord(), "world");
        Assert.assertEquals(worldVocab.getElementFrequency(), 1, 0);
        Assert.assertEquals(strangeVocab.getWord(), "strange");
        Assert.assertEquals(strangeVocab.getElementFrequency(), 2, 0);
        sc.stop();
    }

    @Test
    public void testBuildVocabCache() throws Exception {
        JavaSparkContext sc = getContext();
        JavaRDD<String> corpusRDD = getCorpusRDD(sc);
        Broadcast<Map<String, Object>> broadcastTokenizerVarMap = sc.broadcast(word2vec.getTokenizerVarMap());
        TextPipeline pipeline = new TextPipeline(corpusRDD, broadcastTokenizerVarMap);
        pipeline.buildVocabCache();
        VocabCache<VocabWord> vocabCache = pipeline.getVocabCache();
        Assert.assertTrue((vocabCache != null));
        TextPipelineTest.log.info(("VocabWords: " + (vocabCache.words())));
        Assert.assertEquals(5, vocabCache.numWords());
        VocabWord redVocab = vocabCache.tokenFor("red");
        VocabWord flowerVocab = vocabCache.tokenFor("flowers");
        VocabWord worldVocab = vocabCache.tokenFor("world");
        VocabWord strangeVocab = vocabCache.tokenFor("strange");
        TextPipelineTest.log.info(("Red word: " + redVocab));
        TextPipelineTest.log.info(("Flower word: " + flowerVocab));
        TextPipelineTest.log.info(("World word: " + worldVocab));
        TextPipelineTest.log.info(("Strange word: " + strangeVocab));
        Assert.assertEquals(redVocab.getWord(), "red");
        Assert.assertEquals(redVocab.getElementFrequency(), 1, 0);
        Assert.assertEquals(flowerVocab.getWord(), "flowers");
        Assert.assertEquals(flowerVocab.getElementFrequency(), 1, 0);
        Assert.assertEquals(worldVocab.getWord(), "world");
        Assert.assertEquals(worldVocab.getElementFrequency(), 1, 0);
        Assert.assertEquals(strangeVocab.getWord(), "strange");
        Assert.assertEquals(strangeVocab.getElementFrequency(), 2, 0);
        sc.stop();
    }

    @Test
    public void testBuildVocabWordListRDD() throws Exception {
        JavaSparkContext sc = getContext();
        JavaRDD<String> corpusRDD = getCorpusRDD(sc);
        Broadcast<Map<String, Object>> broadcastTokenizerVarMap = sc.broadcast(word2vec.getTokenizerVarMap());
        TextPipeline pipeline = new TextPipeline(corpusRDD, broadcastTokenizerVarMap);
        pipeline.buildVocabCache();
        pipeline.buildVocabWordListRDD();
        JavaRDD<AtomicLong> sentenceCountRDD = pipeline.getSentenceCountRDD();
        JavaRDD<List<VocabWord>> vocabWordListRDD = pipeline.getVocabWordListRDD();
        List<List<VocabWord>> vocabWordList = vocabWordListRDD.collect();
        List<VocabWord> firstSentenceVocabList = vocabWordList.get(0);
        List<VocabWord> secondSentenceVocabList = vocabWordList.get(1);
        System.out.println(Arrays.deepToString(firstSentenceVocabList.toArray()));
        List<String> firstSentenceTokenList = new ArrayList<>();
        List<String> secondSentenceTokenList = new ArrayList<>();
        for (VocabWord v : firstSentenceVocabList) {
            if (v != null) {
                firstSentenceTokenList.add(v.getWord());
            }
        }
        for (VocabWord v : secondSentenceVocabList) {
            if (v != null) {
                secondSentenceTokenList.add(v.getWord());
            }
        }
        Assert.assertEquals(pipeline.getTotalWordCount(), 9, 0);
        Assert.assertEquals(sentenceCountRDD.collect().get(0).get(), 6);
        Assert.assertEquals(sentenceCountRDD.collect().get(1).get(), 3);
        Assert.assertTrue(firstSentenceTokenList.containsAll(Arrays.asList("strange", "strange", "world")));
        Assert.assertTrue(secondSentenceTokenList.containsAll(Arrays.asList("flowers", "red")));
        sc.stop();
    }

    @Test
    public void testHuffman() throws Exception {
        JavaSparkContext sc = getContext();
        JavaRDD<String> corpusRDD = getCorpusRDD(sc);
        Broadcast<Map<String, Object>> broadcastTokenizerVarMap = sc.broadcast(word2vec.getTokenizerVarMap());
        TextPipeline pipeline = new TextPipeline(corpusRDD, broadcastTokenizerVarMap);
        pipeline.buildVocabCache();
        VocabCache<VocabWord> vocabCache = pipeline.getVocabCache();
        Huffman huffman = new Huffman(vocabCache.vocabWords());
        huffman.build();
        huffman.applyIndexes(vocabCache);
        Collection<VocabWord> vocabWords = vocabCache.vocabWords();
        System.out.println("Huffman Test:");
        for (VocabWord vocabWord : vocabWords) {
            System.out.println(("Word: " + vocabWord));
            System.out.println(vocabWord.getCodes());
            System.out.println(vocabWord.getPoints());
        }
        sc.stop();
    }

    @Test
    public void testCountCumSum() throws Exception {
        JavaSparkContext sc = getContext();
        JavaRDD<String> corpusRDD = getCorpusRDD(sc);
        Broadcast<Map<String, Object>> broadcastTokenizerVarMap = sc.broadcast(word2vec.getTokenizerVarMap());
        TextPipeline pipeline = new TextPipeline(corpusRDD, broadcastTokenizerVarMap);
        pipeline.buildVocabCache();
        pipeline.buildVocabWordListRDD();
        JavaRDD<AtomicLong> sentenceCountRDD = pipeline.getSentenceCountRDD();
        CountCumSum countCumSum = new CountCumSum(sentenceCountRDD);
        JavaRDD<Long> sentenceCountCumSumRDD = countCumSum.buildCumSum();
        List<Long> sentenceCountCumSumList = sentenceCountCumSumRDD.collect();
        Assert.assertTrue(((sentenceCountCumSumList.get(0)) == 6L));
        Assert.assertTrue(((sentenceCountCumSumList.get(1)) == 9L));
        sc.stop();
    }

    /**
     * This test checked generations retrieved using stopWords
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testZipFunction1() throws Exception {
        JavaSparkContext sc = getContext();
        JavaRDD<String> corpusRDD = getCorpusRDD(sc);
        // word2vec.setRemoveStop(false);
        Broadcast<Map<String, Object>> broadcastTokenizerVarMap = sc.broadcast(word2vec.getTokenizerVarMap());
        TextPipeline pipeline = new TextPipeline(corpusRDD, broadcastTokenizerVarMap);
        pipeline.buildVocabCache();
        pipeline.buildVocabWordListRDD();
        JavaRDD<AtomicLong> sentenceCountRDD = pipeline.getSentenceCountRDD();
        JavaRDD<List<VocabWord>> vocabWordListRDD = pipeline.getVocabWordListRDD();
        CountCumSum countCumSum = new CountCumSum(sentenceCountRDD);
        JavaRDD<Long> sentenceCountCumSumRDD = countCumSum.buildCumSum();
        JavaPairRDD<List<VocabWord>, Long> vocabWordListSentenceCumSumRDD = vocabWordListRDD.zip(sentenceCountCumSumRDD);
        List<Tuple2<List<VocabWord>, Long>> lst = vocabWordListSentenceCumSumRDD.collect();
        List<VocabWord> vocabWordsList1 = lst.get(0)._1();
        Long cumSumSize1 = lst.get(0)._2();
        Assert.assertEquals(3, vocabWordsList1.size());
        Assert.assertEquals(vocabWordsList1.get(0).getWord(), "strange");
        Assert.assertEquals(vocabWordsList1.get(1).getWord(), "strange");
        Assert.assertEquals(vocabWordsList1.get(2).getWord(), "world");
        Assert.assertEquals(cumSumSize1, 6L, 0);
        List<VocabWord> vocabWordsList2 = lst.get(1)._1();
        Long cumSumSize2 = lst.get(1)._2();
        Assert.assertEquals(2, vocabWordsList2.size());
        Assert.assertEquals(vocabWordsList2.get(0).getWord(), "flowers");
        Assert.assertEquals(vocabWordsList2.get(1).getWord(), "red");
        Assert.assertEquals(cumSumSize2, 9L, 0);
        sc.stop();
    }

    @Test
    public void testZipFunction2() throws Exception {
        JavaSparkContext sc = getContext();
        JavaRDD<String> corpusRDD = getCorpusRDD(sc);
        // word2vec.setRemoveStop(false);
        Broadcast<Map<String, Object>> broadcastTokenizerVarMap = sc.broadcast(word2vecNoStop.getTokenizerVarMap());
        TextPipeline pipeline = new TextPipeline(corpusRDD, broadcastTokenizerVarMap);
        pipeline.buildVocabCache();
        pipeline.buildVocabWordListRDD();
        JavaRDD<AtomicLong> sentenceCountRDD = pipeline.getSentenceCountRDD();
        JavaRDD<List<VocabWord>> vocabWordListRDD = pipeline.getVocabWordListRDD();
        CountCumSum countCumSum = new CountCumSum(sentenceCountRDD);
        JavaRDD<Long> sentenceCountCumSumRDD = countCumSum.buildCumSum();
        JavaPairRDD<List<VocabWord>, Long> vocabWordListSentenceCumSumRDD = vocabWordListRDD.zip(sentenceCountCumSumRDD);
        List<Tuple2<List<VocabWord>, Long>> lst = vocabWordListSentenceCumSumRDD.collect();
        List<VocabWord> vocabWordsList1 = lst.get(0)._1();
        Long cumSumSize1 = lst.get(0)._2();
        Assert.assertEquals(6, vocabWordsList1.size());
        Assert.assertEquals(vocabWordsList1.get(0).getWord(), "this");
        Assert.assertEquals(vocabWordsList1.get(1).getWord(), "is");
        Assert.assertEquals(vocabWordsList1.get(2).getWord(), "a");
        Assert.assertEquals(vocabWordsList1.get(3).getWord(), "strange");
        Assert.assertEquals(vocabWordsList1.get(4).getWord(), "strange");
        Assert.assertEquals(vocabWordsList1.get(5).getWord(), "world");
        Assert.assertEquals(cumSumSize1, 6L, 0);
        List<VocabWord> vocabWordsList2 = lst.get(1)._1();
        Long cumSumSize2 = lst.get(1)._2();
        Assert.assertEquals(vocabWordsList2.size(), 3);
        Assert.assertEquals(vocabWordsList2.get(0).getWord(), "flowers");
        Assert.assertEquals(vocabWordsList2.get(1).getWord(), "are");
        Assert.assertEquals(vocabWordsList2.get(2).getWord(), "red");
        Assert.assertEquals(cumSumSize2, 9L, 0);
        sc.stop();
    }

    @Test
    public void testFirstIteration() throws Exception {
        JavaSparkContext sc = getContext();
        JavaRDD<String> corpusRDD = getCorpusRDD(sc);
        // word2vec.setRemoveStop(false);
        Broadcast<Map<String, Object>> broadcastTokenizerVarMap = sc.broadcast(word2vec.getTokenizerVarMap());
        TextPipeline pipeline = new TextPipeline(corpusRDD, broadcastTokenizerVarMap);
        pipeline.buildVocabCache();
        pipeline.buildVocabWordListRDD();
        VocabCache<VocabWord> vocabCache = pipeline.getVocabCache();
        /* Huffman huffman = new Huffman(vocabCache.vocabWords());
        huffman.build();
        huffman.applyIndexes(vocabCache);
         */
        VocabWord token = vocabCache.tokenFor("strange");
        VocabWord word = vocabCache.wordFor("strange");
        TextPipelineTest.log.info(("Strange token: " + token));
        TextPipelineTest.log.info(("Strange word: " + word));
        // Get total word count and put into word2vec variable map
        Map<String, Object> word2vecVarMap = word2vec.getWord2vecVarMap();
        word2vecVarMap.put("totalWordCount", pipeline.getTotalWordCount());
        double[] expTable = word2vec.getExpTable();
        JavaRDD<AtomicLong> sentenceCountRDD = pipeline.getSentenceCountRDD();
        JavaRDD<List<VocabWord>> vocabWordListRDD = pipeline.getVocabWordListRDD();
        CountCumSum countCumSum = new CountCumSum(sentenceCountRDD);
        JavaRDD<Long> sentenceCountCumSumRDD = countCumSum.buildCumSum();
        JavaPairRDD<List<VocabWord>, Long> vocabWordListSentenceCumSumRDD = vocabWordListRDD.zip(sentenceCountCumSumRDD);
        Broadcast<Map<String, Object>> word2vecVarMapBroadcast = sc.broadcast(word2vecVarMap);
        Broadcast<double[]> expTableBroadcast = sc.broadcast(expTable);
        Iterator<Tuple2<List<VocabWord>, Long>> iterator = vocabWordListSentenceCumSumRDD.collect().iterator();
        FirstIterationFunctionAdapter firstIterationFunction = new FirstIterationFunctionAdapter(word2vecVarMapBroadcast, expTableBroadcast, pipeline.getBroadCastVocabCache());
        Iterable<Map.Entry<VocabWord, INDArray>> ret = firstIterationFunction.call(iterator);
        Assert.assertTrue(ret.iterator().hasNext());
    }

    @Test
    public void testSyn0AfterFirstIteration() throws Exception {
        JavaSparkContext sc = getContext();
        JavaRDD<String> corpusRDD = getCorpusRDD(sc);
        // word2vec.setRemoveStop(false);
        Broadcast<Map<String, Object>> broadcastTokenizerVarMap = sc.broadcast(word2vec.getTokenizerVarMap());
        TextPipeline pipeline = new TextPipeline(corpusRDD, broadcastTokenizerVarMap);
        pipeline.buildVocabCache();
        pipeline.buildVocabWordListRDD();
        VocabCache<VocabWord> vocabCache = pipeline.getVocabCache();
        Huffman huffman = new Huffman(vocabCache.vocabWords());
        huffman.build();
        // Get total word count and put into word2vec variable map
        Map<String, Object> word2vecVarMap = word2vec.getWord2vecVarMap();
        word2vecVarMap.put("totalWordCount", pipeline.getTotalWordCount());
        double[] expTable = word2vec.getExpTable();
        JavaRDD<AtomicLong> sentenceCountRDD = pipeline.getSentenceCountRDD();
        JavaRDD<List<VocabWord>> vocabWordListRDD = pipeline.getVocabWordListRDD();
        CountCumSum countCumSum = new CountCumSum(sentenceCountRDD);
        JavaRDD<Long> sentenceCountCumSumRDD = countCumSum.buildCumSum();
        JavaPairRDD<List<VocabWord>, Long> vocabWordListSentenceCumSumRDD = vocabWordListRDD.zip(sentenceCountCumSumRDD);
        Broadcast<Map<String, Object>> word2vecVarMapBroadcast = sc.broadcast(word2vecVarMap);
        Broadcast<double[]> expTableBroadcast = sc.broadcast(expTable);
        FirstIterationFunction firstIterationFunction = new FirstIterationFunction(word2vecVarMapBroadcast, expTableBroadcast, pipeline.getBroadCastVocabCache());
        JavaRDD<Pair<VocabWord, INDArray>> pointSyn0Vec = vocabWordListSentenceCumSumRDD.mapPartitions(firstIterationFunction).map(new MapToPairFunction());
    }
}

