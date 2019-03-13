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
package org.deeplearning4j.bagofwords.vectorizer;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.DefaultTokenizer;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.util.SerializationUtils;


/**
 *
 *
 * @author Adam Gibson
 */
@Slf4j
public class TfidfVectorizerTest {
    @Rule
    public final TemporaryFolder testDir = new TemporaryFolder();

    @Test(timeout = 60000L)
    public void testTfIdfVectorizer() throws Exception {
        val rootDir = testDir.newFolder();
        ClassPathResource resource = new ClassPathResource("tripledir");
        resource.copyDirectory(rootDir);
        Assert.assertTrue(rootDir.isDirectory());
        LabelAwareSentenceIterator iter = new org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareFileSentenceIterator(rootDir);
        TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        TfidfVectorizer vectorizer = // .labels(labels)
        // .cleanup(true)
        new TfidfVectorizer.Builder().setMinWordFrequency(1).setStopWords(new ArrayList<String>()).setTokenizerFactory(tokenizerFactory).setIterator(iter).allowParallelTokenization(false).build();
        vectorizer.fit();
        VocabWord word = vectorizer.getVocabCache().wordFor("file.");
        Assume.assumeNotNull(word);
        Assert.assertEquals(word, vectorizer.getVocabCache().tokenFor("file."));
        Assert.assertEquals(3, vectorizer.getVocabCache().totalNumberOfDocs());
        Assert.assertEquals(3, word.getSequencesCount());
        Assert.assertEquals(3, word.getElementFrequency(), 0.1);
        VocabWord word1 = vectorizer.getVocabCache().wordFor("1");
        Assert.assertEquals(1, word1.getSequencesCount());
        Assert.assertEquals(1, word1.getElementFrequency(), 0.1);
        log.info(("Labels used: " + (vectorizer.getLabelsSource().getLabels())));
        Assert.assertEquals(3, vectorizer.getLabelsSource().getNumberOfLabelsUsed());
        Assert.assertEquals(3, vectorizer.getVocabCache().totalNumberOfDocs());
        Assert.assertEquals(11, vectorizer.numWordsEncountered());
        INDArray vector = vectorizer.transform("This is 3 file.");
        log.info(("TF-IDF vector: " + (Arrays.toString(vector.data().asDouble()))));
        VocabCache<VocabWord> vocabCache = vectorizer.getVocabCache();
        Assert.assertEquals(0.04402, vector.getDouble(vocabCache.tokenFor("This").getIndex()), 0.001);
        Assert.assertEquals(0.04402, vector.getDouble(vocabCache.tokenFor("is").getIndex()), 0.001);
        Assert.assertEquals(0.119, vector.getDouble(vocabCache.tokenFor("3").getIndex()), 0.001);
        Assert.assertEquals(0, vector.getDouble(vocabCache.tokenFor("file.").getIndex()), 0.001);
        DataSet dataSet = vectorizer.vectorize("This is 3 file.", "label3");
        // assertEquals(0.0, dataSet.getLabels().getDouble(0), 0.1);
        // assertEquals(0.0, dataSet.getLabels().getDouble(1), 0.1);
        // assertEquals(1.0, dataSet.getLabels().getDouble(2), 0.1);
        int cnt = 0;
        for (int i = 0; i < 3; i++) {
            if ((dataSet.getLabels().getDouble(i)) > 0.1)
                cnt++;

        }
        Assert.assertEquals(1, cnt);
        File tempFile = testDir.newFile("somefile.bin");
        tempFile.delete();
        SerializationUtils.saveObject(vectorizer, tempFile);
        TfidfVectorizer vectorizer2 = SerializationUtils.readObject(tempFile);
        vectorizer2.setTokenizerFactory(tokenizerFactory);
        dataSet = vectorizer2.vectorize("This is 3 file.", "label2");
        Assert.assertEquals(vector, dataSet.getFeatures());
    }

    @Test(timeout = 10000L)
    public void testParallelFlag1() throws Exception {
        val vectorizer = new TfidfVectorizer.Builder().allowParallelTokenization(false).build();
        Assert.assertFalse(vectorizer.isParallel);
    }

    @Test(expected = ND4JIllegalStateException.class, timeout = 20000L)
    public void testParallelFlag2() throws Exception {
        val collection = new ArrayList<String>();
        collection.add("First string");
        collection.add("Second string");
        collection.add("Third string");
        collection.add("");
        collection.add("Fifth string");
        // collection.add("caboom");
        val vectorizer = new TfidfVectorizer.Builder().allowParallelTokenization(false).setIterator(new org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator(collection)).setTokenizerFactory(new TfidfVectorizerTest.ExplodingTokenizerFactory(8, (-1))).build();
        vectorizer.buildVocab();
        log.info("Fitting vectorizer...");
        vectorizer.fit();
    }

    @Test(expected = ND4JIllegalStateException.class, timeout = 20000L)
    public void testParallelFlag3() throws Exception {
        val collection = new ArrayList<String>();
        collection.add("First string");
        collection.add("Second string");
        collection.add("Third string");
        collection.add("");
        collection.add("Fifth string");
        collection.add("Long long long string");
        collection.add("Sixth string");
        val vectorizer = new TfidfVectorizer.Builder().allowParallelTokenization(false).setIterator(new org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator(collection)).setTokenizerFactory(new TfidfVectorizerTest.ExplodingTokenizerFactory((-1), 4)).build();
        vectorizer.buildVocab();
        log.info("Fitting vectorizer...");
        vectorizer.fit();
    }

    protected class ExplodingTokenizerFactory extends DefaultTokenizerFactory {
        protected int triggerSentence;

        protected int triggerWord;

        protected AtomicLong cnt = new AtomicLong(0);

        protected ExplodingTokenizerFactory(int triggerSentence, int triggerWord) {
            this.triggerSentence = triggerSentence;
            this.triggerWord = triggerWord;
        }

        @Override
        public Tokenizer create(String toTokenize) {
            if (((triggerSentence) >= 0) && ((cnt.incrementAndGet()) >= (triggerSentence)))
                throw new ND4JIllegalStateException("TokenizerFactory exploded");

            val tkn = new TfidfVectorizerTest.ExplodingTokenizer(toTokenize, triggerWord);
            return tkn;
        }
    }

    protected class ExplodingTokenizer extends DefaultTokenizer {
        protected int triggerWord;

        public ExplodingTokenizer(String string, int triggerWord) {
            super(string);
            this.triggerWord = triggerWord;
            if ((this.triggerWord) >= 0)
                if ((countTokens()) >= triggerWord)
                    throw new ND4JIllegalStateException("Tokenizer exploded");


        }
    }
}

