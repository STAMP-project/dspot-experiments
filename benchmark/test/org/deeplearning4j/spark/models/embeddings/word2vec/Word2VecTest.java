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
package org.deeplearning4j.spark.models.embeddings.word2vec;


import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * This test is for LEGACY w2v implementation
 *
 * @author jeffreytang
 */
@Ignore
public class Word2VecTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testConcepts() throws Exception {
        // These are all default values for word2vec
        SparkConf sparkConf = new SparkConf().setMaster("local[8]").setAppName("sparktest");
        // Set SparkContext
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        // Path of data part-00000
        String dataPath = new ClassPathResource("raw_sentences.txt").getFile().getAbsolutePath();
        // dataPath = "/ext/Temp/part-00000";
        // String dataPath = new ClassPathResource("spark_word2vec_test.txt").getFile().getAbsolutePath();
        // Read in data
        JavaRDD<String> corpus = sc.textFile(dataPath);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec word2Vec = // .setTokenizer("org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory")
        // .setTokenPreprocessor("org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor")
        // .setRemoveStop(false)
        new Word2Vec.Builder().setNGrams(1).tokenizerFactory(t).seed(42L).negative(10).useAdaGrad(false).layerSize(150).windowSize(5).learningRate(0.025).minLearningRate(1.0E-4).iterations(1).batchSize(100).minWordFrequency(5).stopWords(Arrays.asList("three")).useUnknown(true).build();
        word2Vec.train(corpus);
        // word2Vec.setModelUtils(new FlatModelUtils());
        System.out.println(("UNK: " + (word2Vec.getWordVectorMatrix("UNK"))));
        InMemoryLookupTable<VocabWord> table = ((InMemoryLookupTable<VocabWord>) (word2Vec.lookupTable()));
        double sim = word2Vec.similarity("day", "night");
        System.out.println(("day/night similarity: " + sim));
        /* System.out.println("Hornjo: " + word2Vec.getWordVectorMatrix("hornjoserbsce"));
        System.out.println("carro: " + word2Vec.getWordVectorMatrix("carro"));

        Collection<String> portu = word2Vec.wordsNearest("carro", 10);
        printWords("carro", portu, word2Vec);

        portu = word2Vec.wordsNearest("davi", 10);
        printWords("davi", portu, word2Vec);

        System.out.println("---------------------------------------");
         */
        Collection<String> words = word2Vec.wordsNearest("day", 10);
        Word2VecTest.printWords("day", words, word2Vec);
        Assert.assertTrue(words.contains("night"));
        Assert.assertTrue(words.contains("week"));
        Assert.assertTrue(words.contains("year"));
        sim = word2Vec.similarity("two", "four");
        System.out.println(("two/four similarity: " + sim));
        words = word2Vec.wordsNearest("two", 10);
        Word2VecTest.printWords("two", words, word2Vec);
        // three should be absent due to stopWords
        Assert.assertFalse(words.contains("three"));
        Assert.assertTrue(words.contains("five"));
        Assert.assertTrue(words.contains("four"));
        sc.stop();
        // test serialization
        File tempFile = testDir.newFile((("temp" + (System.currentTimeMillis())) + ".tmp"));
        int idx1 = word2Vec.vocab().wordFor("day").getIndex();
        INDArray array1 = word2Vec.getWordVectorMatrix("day").dup();
        VocabWord word1 = word2Vec.vocab().elementAtIndex(0);
        WordVectorSerializer.writeWordVectors(word2Vec.getLookupTable(), tempFile);
        WordVectors vectors = WordVectorSerializer.loadTxtVectors(tempFile);
        VocabWord word2 = ((VocabCache<VocabWord>) (vectors.vocab())).elementAtIndex(0);
        VocabWord wordIT = ((VocabCache<VocabWord>) (vectors.vocab())).wordFor("it");
        int idx2 = vectors.vocab().wordFor("day").getIndex();
        INDArray array2 = vectors.getWordVectorMatrix("day").dup();
        System.out.println(("word 'i': " + word2));
        System.out.println(("word 'it': " + wordIT));
        Assert.assertEquals(idx1, idx2);
        Assert.assertEquals(word1, word2);
        Assert.assertEquals(array1, array2);
    }
}

