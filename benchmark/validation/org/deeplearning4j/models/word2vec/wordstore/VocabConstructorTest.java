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
package org.deeplearning4j.models.word2vec.wordstore;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.val;
import org.deeplearning4j.models.sequencevectors.interfaces.SequenceIterator;
import org.deeplearning4j.models.sequencevectors.iterators.AbstractSequenceIterator;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.deeplearning4j.models.sequencevectors.transformers.impl.SentenceTransformer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.documentiterator.FileLabelAwareIterator;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class VocabConstructorTest {
    protected static final Logger log = LoggerFactory.getLogger(VocabConstructorTest.class);

    TokenizerFactory t = new DefaultTokenizerFactory();

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testVocab() throws Exception {
        File inputFile = new ClassPathResource("big/raw_sentences.txt").getFile();
        SentenceIterator iter = new BasicLineIterator(inputFile);
        Set<String> set = new HashSet<>();
        int lines = 0;
        int cnt = 0;
        while (iter.hasNext()) {
            Tokenizer tok = t.create(iter.nextSentence());
            for (String token : tok.getTokens()) {
                if (((token == null) || (token.isEmpty())) || (token.trim().isEmpty()))
                    continue;

                cnt++;
                if (!(set.contains(token)))
                    set.add(token);

            }
            lines++;
        } 
        VocabConstructorTest.log.info((((((("Total number of tokens: [" + cnt) + "], lines: [") + lines) + "], set size: [") + (set.size())) + "]"));
        VocabConstructorTest.log.info(("Set:\n" + set));
    }

    @Test
    public void testBuildJointVocabulary1() throws Exception {
        File inputFile = new ClassPathResource("big/raw_sentences.txt").getFile();
        SentenceIterator iter = new BasicLineIterator(inputFile);
        VocabCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        SentenceTransformer transformer = new SentenceTransformer.Builder().iterator(iter).tokenizerFactory(t).build();
        /* And we pack that transformer into AbstractSequenceIterator */
        AbstractSequenceIterator<VocabWord> sequenceIterator = new AbstractSequenceIterator.Builder<>(transformer).build();
        VocabConstructor<VocabWord> constructor = new VocabConstructor.Builder<VocabWord>().addSource(sequenceIterator, 0).useAdaGrad(false).setTargetVocabCache(cache).build();
        constructor.buildJointVocabulary(true, false);
        Assert.assertEquals(244, cache.numWords());
        Assert.assertEquals(0, cache.totalWordOccurrences());
    }

    @Test
    public void testBuildJointVocabulary2() throws Exception {
        File inputFile = new ClassPathResource("big/raw_sentences.txt").getFile();
        SentenceIterator iter = new BasicLineIterator(inputFile);
        VocabCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        SentenceTransformer transformer = new SentenceTransformer.Builder().iterator(iter).tokenizerFactory(t).build();
        AbstractSequenceIterator<VocabWord> sequenceIterator = new AbstractSequenceIterator.Builder<>(transformer).build();
        VocabConstructor<VocabWord> constructor = new VocabConstructor.Builder<VocabWord>().addSource(sequenceIterator, 5).useAdaGrad(false).setTargetVocabCache(cache).build();
        constructor.buildJointVocabulary(false, true);
        // assertFalse(cache.hasToken("including"));
        Assert.assertEquals(242, cache.numWords());
        Assert.assertEquals("i", cache.wordAtIndex(1));
        Assert.assertEquals("it", cache.wordAtIndex(0));
        Assert.assertEquals(634303, cache.totalWordOccurrences());
    }

    @Test
    public void testCounter1() throws Exception {
        VocabCache<VocabWord> vocabCache = new AbstractCache.Builder<VocabWord>().build();
        final List<VocabWord> words = new ArrayList<>();
        words.add(new VocabWord(1, "word"));
        words.add(new VocabWord(2, "test"));
        words.add(new VocabWord(1, "here"));
        Iterable<Sequence<VocabWord>> iterable = new Iterable<Sequence<VocabWord>>() {
            @Override
            public Iterator<Sequence<VocabWord>> iterator() {
                return new Iterator<Sequence<VocabWord>>() {
                    private AtomicBoolean switcher = new AtomicBoolean(true);

                    @Override
                    public boolean hasNext() {
                        return switcher.getAndSet(false);
                    }

                    @Override
                    public Sequence<VocabWord> next() {
                        Sequence<VocabWord> sequence = new Sequence(words);
                        return sequence;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        SequenceIterator<VocabWord> sequenceIterator = new AbstractSequenceIterator.Builder<>(iterable).build();
        VocabConstructor<VocabWord> constructor = new VocabConstructor.Builder<VocabWord>().addSource(sequenceIterator, 0).useAdaGrad(false).setTargetVocabCache(vocabCache).build();
        constructor.buildJointVocabulary(false, true);
        Assert.assertEquals(3, vocabCache.numWords());
        Assert.assertEquals(1, vocabCache.wordFrequency("test"));
    }

    @Test
    public void testCounter2() throws Exception {
        VocabCache<VocabWord> vocabCache = new AbstractCache.Builder<VocabWord>().build();
        final List<VocabWord> words = new ArrayList<>();
        words.add(new VocabWord(1, "word"));
        words.add(new VocabWord(0, "test"));
        words.add(new VocabWord(1, "here"));
        Iterable<Sequence<VocabWord>> iterable = new Iterable<Sequence<VocabWord>>() {
            @Override
            public Iterator<Sequence<VocabWord>> iterator() {
                return new Iterator<Sequence<VocabWord>>() {
                    private AtomicBoolean switcher = new AtomicBoolean(true);

                    @Override
                    public boolean hasNext() {
                        return switcher.getAndSet(false);
                    }

                    @Override
                    public Sequence<VocabWord> next() {
                        Sequence<VocabWord> sequence = new Sequence(words);
                        return sequence;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
        SequenceIterator<VocabWord> sequenceIterator = new AbstractSequenceIterator.Builder<>(iterable).build();
        VocabConstructor<VocabWord> constructor = new VocabConstructor.Builder<VocabWord>().addSource(sequenceIterator, 0).useAdaGrad(false).setTargetVocabCache(vocabCache).build();
        constructor.buildJointVocabulary(false, true);
        Assert.assertEquals(3, vocabCache.numWords());
        Assert.assertEquals(1, vocabCache.wordFrequency("test"));
    }

    /**
     * Here we test basic vocab transfer, done WITHOUT labels
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMergedVocab1() throws Exception {
        AbstractCache<VocabWord> cacheSource = new AbstractCache.Builder<VocabWord>().build();
        AbstractCache<VocabWord> cacheTarget = new AbstractCache.Builder<VocabWord>().build();
        ClassPathResource resource = new ClassPathResource("big/raw_sentences.txt");
        BasicLineIterator underlyingIterator = new BasicLineIterator(resource.getFile());
        SentenceTransformer transformer = new SentenceTransformer.Builder().iterator(underlyingIterator).tokenizerFactory(t).build();
        AbstractSequenceIterator<VocabWord> sequenceIterator = new AbstractSequenceIterator.Builder<>(transformer).build();
        VocabConstructor<VocabWord> vocabConstructor = new VocabConstructor.Builder<VocabWord>().addSource(sequenceIterator, 1).setTargetVocabCache(cacheSource).build();
        vocabConstructor.buildJointVocabulary(false, true);
        int sourceSize = cacheSource.numWords();
        VocabConstructorTest.log.info(("Source Vocab size: " + sourceSize));
        VocabConstructor<VocabWord> vocabTransfer = new VocabConstructor.Builder<VocabWord>().addSource(sequenceIterator, 1).setTargetVocabCache(cacheTarget).build();
        vocabTransfer.buildMergedVocabulary(cacheSource, false);
        Assert.assertEquals(sourceSize, cacheTarget.numWords());
    }

    @Test
    public void testMergedVocabWithLabels1() throws Exception {
        AbstractCache<VocabWord> cacheSource = new AbstractCache.Builder<VocabWord>().build();
        AbstractCache<VocabWord> cacheTarget = new AbstractCache.Builder<VocabWord>().build();
        ClassPathResource resource = new ClassPathResource("big/raw_sentences.txt");
        BasicLineIterator underlyingIterator = new BasicLineIterator(resource.getFile());
        SentenceTransformer transformer = new SentenceTransformer.Builder().iterator(underlyingIterator).tokenizerFactory(t).build();
        AbstractSequenceIterator<VocabWord> sequenceIterator = new AbstractSequenceIterator.Builder<>(transformer).build();
        VocabConstructor<VocabWord> vocabConstructor = new VocabConstructor.Builder<VocabWord>().addSource(sequenceIterator, 1).setTargetVocabCache(cacheSource).build();
        vocabConstructor.buildJointVocabulary(false, true);
        int sourceSize = cacheSource.numWords();
        VocabConstructorTest.log.info(("Source Vocab size: " + sourceSize));
        val dir = testDir.newFolder();
        new ClassPathResource("/paravec/labeled").copyDirectory(dir);
        FileLabelAwareIterator labelAwareIterator = new FileLabelAwareIterator.Builder().addSourceFolder(dir).build();
        transformer = new SentenceTransformer.Builder().iterator(labelAwareIterator).tokenizerFactory(t).build();
        sequenceIterator = new AbstractSequenceIterator.Builder<>(transformer).build();
        VocabConstructor<VocabWord> vocabTransfer = new VocabConstructor.Builder<VocabWord>().addSource(sequenceIterator, 1).setTargetVocabCache(cacheTarget).build();
        vocabTransfer.buildMergedVocabulary(cacheSource, true);
        // those +3 go for 3 additional entries in target VocabCache: labels
        Assert.assertEquals((sourceSize + 3), cacheTarget.numWords());
        // now we check index equality for transferred elements
        Assert.assertEquals(cacheSource.wordAtIndex(17), cacheTarget.wordAtIndex(17));
        Assert.assertEquals(cacheSource.wordAtIndex(45), cacheTarget.wordAtIndex(45));
        Assert.assertEquals(cacheSource.wordAtIndex(89), cacheTarget.wordAtIndex(89));
        // we check that newly added labels have indexes beyond the VocabCache index space
        // please note, we need >= since the indexes are zero-based, and sourceSize is not
        Assert.assertTrue(((cacheTarget.indexOf("Zfinance")) > (sourceSize - 1)));
        Assert.assertTrue(((cacheTarget.indexOf("Zscience")) > (sourceSize - 1)));
        Assert.assertTrue(((cacheTarget.indexOf("Zhealth")) > (sourceSize - 1)));
    }

    @Test
    public void testTransfer_1() {
        val vocab = new AbstractCache<VocabWord>();
        vocab.addToken(new VocabWord(1.0, "alpha"));
        vocab.addWordToIndex(0, "alpha");
        vocab.addToken(new VocabWord(2.0, "beta"));
        vocab.addWordToIndex(5, "beta");
        vocab.addToken(new VocabWord(3.0, "gamma"));
        vocab.addWordToIndex(10, "gamma");
        val constructor = new VocabConstructor.Builder<VocabWord>().build();
        val result = constructor.transferVocabulary(vocab, true);
        Assert.assertEquals(3, result.numWords());
        Assert.assertEquals("gamma", result.wordAtIndex(0));
        Assert.assertEquals("beta", result.wordAtIndex(1));
        Assert.assertEquals("alpha", result.wordAtIndex(2));
    }

    @Test
    public void testTransfer_2() {
        val vocab = new AbstractCache<VocabWord>();
        vocab.addToken(new VocabWord(1.0, "alpha"));
        vocab.addWordToIndex(0, "alpha");
        vocab.addToken(new VocabWord(2.0, "beta"));
        vocab.addWordToIndex(5, "beta");
        vocab.addToken(new VocabWord(3.0, "gamma"));
        vocab.addWordToIndex(10, "gamma");
        val constructor = new VocabConstructor.Builder<VocabWord>().build();
        val result = constructor.transferVocabulary(vocab, false);
        Assert.assertEquals(3, result.numWords());
        Assert.assertEquals("gamma", result.wordAtIndex(10));
        Assert.assertEquals("beta", result.wordAtIndex(5));
        Assert.assertEquals("alpha", result.wordAtIndex(0));
    }

    @Test
    public void testTransfer_3() {
        val vocab = new AbstractCache<VocabWord>();
        vocab.addToken(new VocabWord(1.0, "alpha"));
        vocab.addWordToIndex(0, "alpha");
        vocab.addToken(new VocabWord(2.0, "beta"));
        vocab.addWordToIndex(5, "beta");
        vocab.addToken(new VocabWord(3.0, "gamma"));
        vocab.addWordToIndex(10, "gamma");
        val vocabIntersect = new AbstractCache<VocabWord>();
        vocabIntersect.addToken(new VocabWord(4.0, "alpha"));
        vocabIntersect.addWordToIndex(0, "alpha");
        vocab.addToken(new VocabWord(2.0, "delta"));
        vocab.addWordToIndex(15, "delta");
        val constructor = new VocabConstructor.Builder<VocabWord>().setTargetVocabCache(vocab).setLockFactor(false).build();
        val result = constructor.transferVocabulary(vocabIntersect, true);
        Assert.assertEquals(4, result.numWords());
        Assert.assertEquals("alpha", result.wordAtIndex(0));
        Assert.assertEquals(5.0, result.wordFrequency("alpha"), 1.0E-5);
        Assert.assertEquals("beta", result.wordAtIndex(5));
        Assert.assertEquals("gamma", result.wordAtIndex(10));
        Assert.assertEquals("delta", result.wordAtIndex(15));
    }

    // 5s timeout
    @Test(timeout = 5000)
    public void testParallelTokenizationDisabled_Completes() throws Exception {
        File inputFile = new ClassPathResource("big/raw_sentences.txt").getFile();
        SentenceIterator iter = new BasicLineIterator(inputFile);
        SentenceTransformer transformer = new SentenceTransformer.Builder().iterator(iter).tokenizerFactory(t).build();
        AbstractSequenceIterator<VocabWord> sequenceIterator = new AbstractSequenceIterator.Builder<>(transformer).build();
        VocabConstructor<VocabWord> constructor = new VocabConstructor.Builder<VocabWord>().addSource(sequenceIterator, 5).allowParallelTokenization(false).build();
        constructor.buildJointVocabulary(false, true);
    }
}

