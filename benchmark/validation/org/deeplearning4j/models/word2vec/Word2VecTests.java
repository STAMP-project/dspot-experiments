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
package org.deeplearning4j.models.word2vec;


import com.google.common.primitives.Doubles;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.val;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.VectorsConfiguration;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.UimaSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author jeffreytang
 */
// 
public class Word2VecTests {
    private static final Logger log = LoggerFactory.getLogger(Word2VecTests.class);

    private File inputFile;

    private File inputFile2;

    private String pathToWriteto;

    private WordVectors googleModel;

    @Test
    public void testGoogleModelLoaded() throws Exception {
        Assert.assertEquals(googleModel.vocab().numWords(), 30);
        Assert.assertTrue(googleModel.hasWord("Morgan_Freeman"));
        double[] wordVector = googleModel.getWordVector("Morgan_Freeman");
        Assert.assertTrue(((wordVector.length) == 300));
        Assert.assertEquals(Doubles.asList(wordVector).get(0), 0.044423, 0.001);
    }

    @Test
    public void testSimilarity() throws Exception {
        testGoogleModelLoaded();
        Assert.assertEquals(googleModel.similarity("Benkovic", "Boeremag_trialists"), 0.1204, 0.01);
        Assert.assertEquals(googleModel.similarity("Benkovic", "Gopie"), 0.335, 0.01);
        Assert.assertEquals(googleModel.similarity("Benkovic", "Youku.com"), 0.0116, 0.01);
    }

    @Test
    public void testWordsNearest() throws Exception {
        testGoogleModelLoaded();
        List<Object> lst = Arrays.asList(googleModel.wordsNearest("Benkovic", 10).toArray());
        Assert.assertTrue(lst.contains("Gopie"));
        Assert.assertTrue(lst.contains("JIM_HOOK_Senior"));
        /* assertEquals(lst.get(0), "Gopie");
        assertEquals(lst.get(1), "JIM_HOOK_Senior");
         */
    }

    @Test
    public void testUIMAIterator() throws Exception {
        SentenceIterator iter = UimaSentenceIterator.createWithPath(inputFile.getAbsolutePath());
        Assert.assertEquals(iter.nextSentence(), "No ,  he says now .");
    }

    @Test
    public void testWord2VecCBOW() throws Exception {
        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec = layerSize(150).seed(42).sampling(0).negativeSample(0).useHierarchicSoftmax(true).windowSize(5).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils<VocabWord>()).useAdaGrad(false).iterate(iter).workers(4).tokenizerFactory(t).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW<VocabWord>()).build();
        vec.fit();
        Collection<String> lst = vec.wordsNearest("day", 10);
        Word2VecTests.log.info(Arrays.toString(lst.toArray()));
        // assertEquals(10, lst.size());
        double sim = vec.similarity("day", "night");
        Word2VecTests.log.info(("Day/night similarity: " + sim));
        Assert.assertTrue(lst.contains("week"));
        Assert.assertTrue(lst.contains("night"));
        Assert.assertTrue(lst.contains("year"));
        Assert.assertTrue((sim > 0.65F));
    }

    @Test
    public void testWord2VecMultiEpoch() throws Exception {
        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec = layerSize(150).seed(42).sampling(0).negativeSample(0).useHierarchicSoftmax(true).windowSize(5).epochs(3).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils<VocabWord>()).useAdaGrad(false).iterate(iter).workers(8).tokenizerFactory(t).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW<VocabWord>()).build();
        vec.fit();
        Collection<String> lst = vec.wordsNearest("day", 10);
        Word2VecTests.log.info(Arrays.toString(lst.toArray()));
        // assertEquals(10, lst.size());
        double sim = vec.similarity("day", "night");
        Word2VecTests.log.info(("Day/night similarity: " + sim));
        Assert.assertTrue(lst.contains("week"));
        Assert.assertTrue(lst.contains("night"));
        Assert.assertTrue(lst.contains("year"));
    }

    @Test
    public void reproducibleResults_ForMultipleRuns() throws Exception {
        val shakespear = new ClassPathResource("big/rnj.txt");
        val basic = new ClassPathResource("big/rnj.txt");
        SentenceIterator iter = new BasicLineIterator(inputFile);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec1 = layerSize(100).stopWords(new ArrayList<String>()).seed(42).learningRate(0.025).minLearningRate(0.001).sampling(0).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram<VocabWord>()).epochs(1).windowSize(5).allowParallelTokenization(true).workers(1).useHierarchicSoftmax(true).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils<VocabWord>()).iterate(iter).tokenizerFactory(t).build();
        Word2Vec vec2 = layerSize(100).stopWords(new ArrayList<String>()).seed(42).learningRate(0.025).minLearningRate(0.001).sampling(0).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram<VocabWord>()).epochs(1).windowSize(5).allowParallelTokenization(true).workers(1).useHierarchicSoftmax(true).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils<VocabWord>()).iterate(iter).tokenizerFactory(t).build();
        vec1.fit();
        iter.reset();
        vec2.fit();
        INDArray syn0_from_vec1 = ((InMemoryLookupTable<VocabWord>) (vec1.getLookupTable())).getSyn0();
        INDArray syn0_from_vec2 = ((InMemoryLookupTable<VocabWord>) (vec2.getLookupTable())).getSyn0();
        Assert.assertEquals(syn0_from_vec1, syn0_from_vec2);
        Word2VecTests.log.info("Day/night similarity: {}", vec1.similarity("day", "night"));
        val result = vec1.wordsNearest("day", 10);
        Word2VecTests.printWords("day", result, vec1);
    }

    @Test
    public void testRunWord2Vec() throws Exception {
        // Strip white space before and after for each line
        /* val shakespear = new ClassPathResource("big/rnj.txt");
        SentenceIterator iter = new BasicLineIterator(shakespear.getFile());
         */
        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec = // .negativeSample(10)
        layerSize(100).stopWords(new ArrayList<String>()).seed(42).learningRate(0.025).minLearningRate(0.001).sampling(0).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram<VocabWord>()).epochs(1).windowSize(5).allowParallelTokenization(true).workers(6).usePreciseMode(true).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils<VocabWord>()).iterate(iter).tokenizerFactory(t).build();
        Assert.assertEquals(new ArrayList<String>(), vec.getStopWords());
        vec.fit();
        File tempFile = File.createTempFile("temp", "temp");
        tempFile.deleteOnExit();
        WordVectorSerializer.writeFullModel(vec, tempFile.getAbsolutePath());
        Collection<String> lst = vec.wordsNearest("day", 10);
        // log.info(Arrays.toString(lst.toArray()));
        Word2VecTests.printWords("day", lst, vec);
        Assert.assertEquals(10, lst.size());
        double sim = vec.similarity("day", "night");
        Word2VecTests.log.info(("Day/night similarity: " + sim));
        Assert.assertTrue((sim < 1.0));
        Assert.assertTrue((sim > 0.4));
        Assert.assertTrue(lst.contains("week"));
        Assert.assertTrue(lst.contains("night"));
        Assert.assertTrue(lst.contains("year"));
        Assert.assertFalse(lst.contains(null));
        lst = vec.wordsNearest("day", 10);
        // log.info(Arrays.toString(lst.toArray()));
        Word2VecTests.printWords("day", lst, vec);
        Assert.assertTrue(lst.contains("week"));
        Assert.assertTrue(lst.contains("night"));
        Assert.assertTrue(lst.contains("year"));
        new File("cache.ser").delete();
        ArrayList<String> labels = new ArrayList<>();
        labels.add("day");
        labels.add("night");
        labels.add("week");
        INDArray matrix = vec.getWordVectors(labels);
        Assert.assertEquals(matrix.getRow(0), vec.getWordVectorMatrix("day"));
        Assert.assertEquals(matrix.getRow(1), vec.getWordVectorMatrix("night"));
        Assert.assertEquals(matrix.getRow(2), vec.getWordVectorMatrix("week"));
        WordVectorSerializer.writeWordVectors(vec, pathToWriteto);
    }

    /**
     * Adding test for cosine similarity, to track changes in Transforms.cosineSim()
     */
    @Test
    public void testCosineSim() {
        double[] array1 = new double[]{ 1.01, 0.91, 0.81, 0.71 };
        double[] array2 = new double[]{ 1.01, 0.91, 0.81, 0.71 };
        double[] array3 = new double[]{ 1.0, 0.9, 0.8, 0.7 };
        double sim12 = Transforms.cosineSim(Nd4j.create(array1), Nd4j.create(array2));
        double sim23 = Transforms.cosineSim(Nd4j.create(array2), Nd4j.create(array3));
        Word2VecTests.log.info(("Arrays 1/2 cosineSim: " + sim12));
        Word2VecTests.log.info(("Arrays 2/3 cosineSim: " + sim23));
        Word2VecTests.log.info(("Arrays 1/2 dot: " + (Nd4j.getBlasWrapper().dot(Nd4j.create(array1), Nd4j.create(array2)))));
        Word2VecTests.log.info(("Arrays 2/3 dot: " + (Nd4j.getBlasWrapper().dot(Nd4j.create(array2), Nd4j.create(array3)))));
        Assert.assertEquals(1.0, sim12, 0.01);
        Assert.assertEquals(0.99, sim23, 0.01);
    }

    @Test
    public void testLoadingWordVectors() throws Exception {
        File modelFile = new File(pathToWriteto);
        if (!(modelFile.exists())) {
            testRunWord2Vec();
        }
        WordVectors wordVectors = WordVectorSerializer.loadTxtVectors(modelFile);
        Collection<String> lst = wordVectors.wordsNearest("day", 10);
        System.out.println(Arrays.toString(lst.toArray()));
    }

    @Test
    public void testW2VnegativeOnRestore() throws Exception {
        // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec = layerSize(100).stopWords(new ArrayList<String>()).seed(42).learningRate(0.025).minLearningRate(0.001).sampling(0).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram<VocabWord>()).negativeSample(10).epochs(1).windowSize(5).useHierarchicSoftmax(false).allowParallelTokenization(true).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.FlatModelUtils<VocabWord>()).iterate(iter).tokenizerFactory(t).build();
        Assert.assertEquals(false, vec.getConfiguration().isUseHierarchicSoftmax());
        Word2VecTests.log.info("Fit 1");
        vec.fit();
        File tmpFile = File.createTempFile("temp", "file");
        tmpFile.deleteOnExit();
        WordVectorSerializer.writeWord2VecModel(vec, tmpFile);
        iter.reset();
        Word2Vec restoredVec = WordVectorSerializer.readWord2VecModel(tmpFile, true);
        restoredVec.setTokenizerFactory(t);
        restoredVec.setSentenceIterator(iter);
        Assert.assertEquals(false, restoredVec.getConfiguration().isUseHierarchicSoftmax());
        Assert.assertTrue(((restoredVec.getModelUtils()) instanceof org.deeplearning4j.models.embeddings.reader.impl.FlatModelUtils));
        Assert.assertTrue(restoredVec.getConfiguration().isAllowParallelTokenization());
        Word2VecTests.log.info("Fit 2");
        restoredVec.fit();
        iter.reset();
        restoredVec = WordVectorSerializer.readWord2VecModel(tmpFile, false);
        restoredVec.setTokenizerFactory(t);
        restoredVec.setSentenceIterator(iter);
        Assert.assertEquals(false, restoredVec.getConfiguration().isUseHierarchicSoftmax());
        Assert.assertTrue(((restoredVec.getModelUtils()) instanceof org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils));
        Word2VecTests.log.info("Fit 3");
        restoredVec.fit();
    }

    @Test
    public void testUnknown1() throws Exception {
        // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec = layerSize(100).stopWords(new ArrayList<String>()).seed(42).learningRate(0.025).minLearningRate(0.001).sampling(0).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW<VocabWord>()).epochs(1).windowSize(5).useHierarchicSoftmax(true).allowParallelTokenization(true).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.FlatModelUtils<VocabWord>()).iterate(iter).tokenizerFactory(t).build();
        vec.fit();
        Assert.assertTrue(vec.hasWord("PEWPEW"));
        Assert.assertTrue(vec.getVocab().containsWord("PEWPEW"));
        INDArray unk = vec.getWordVectorMatrix("PEWPEW");
        Assert.assertNotEquals(null, unk);
        File tempFile = File.createTempFile("temp", "file");
        tempFile.deleteOnExit();
        WordVectorSerializer.writeWord2VecModel(vec, tempFile);
        Word2VecTests.log.info("Original configuration: {}", vec.getConfiguration());
        Word2Vec restored = WordVectorSerializer.readWord2VecModel(tempFile);
        Assert.assertTrue(restored.hasWord("PEWPEW"));
        Assert.assertTrue(restored.getVocab().containsWord("PEWPEW"));
        INDArray unk_restored = restored.getWordVectorMatrix("PEWPEW");
        Assert.assertEquals(unk, unk_restored);
        // now we're getting some junk word
        INDArray random = vec.getWordVectorMatrix("hhsd7d7sdnnmxc_SDsda");
        INDArray randomRestored = restored.getWordVectorMatrix("hhsd7d7sdnnmxc_SDsda");
        Word2VecTests.log.info("Restored configuration: {}", restored.getConfiguration());
        Assert.assertEquals(unk, random);
        Assert.assertEquals(unk, randomRestored);
    }

    @Test
    public void orderIsCorrect_WhenParallelized() throws Exception {
        // Strip white space before and after for each line
        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec = // .negativeSample(10)
        layerSize(100).stopWords(new ArrayList<String>()).seed(42).learningRate(0.025).minLearningRate(0.001).sampling(0).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram<VocabWord>()).epochs(1).windowSize(5).allowParallelTokenization(true).workers(1).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils<VocabWord>()).iterate(iter).tokenizerFactory(t).build();
        vec.fit();
        System.out.println(vec.getVocab().numWords());
        val words = vec.getVocab().words();
        for (val word : words) {
            System.out.println(word);
        }
    }

    @Test
    public void testJSONSerialization() {
        Word2Vec word2Vec = new Word2Vec.Builder().layerSize(1000).limitVocabularySize(1000).elementsLearningAlgorithm(org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW.class.getCanonicalName()).allowParallelTokenization(true).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.FlatModelUtils<VocabWord>()).usePreciseMode(true).batchSize(1024).windowSize(23).minWordFrequency(24).iterations(54).seed(45).learningRate(0.08).epochs(45).stopWords(Collections.singletonList("NOT")).sampling(44).workers(45).negativeSample(56).useAdaGrad(true).useHierarchicSoftmax(false).minLearningRate(0.002).resetModel(true).useUnknown(true).enableScavenger(true).usePreciseWeightInit(true).build();
        AbstractCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        val words = new VocabWord[3];
        words[0] = new VocabWord(1.0, "word");
        words[1] = new VocabWord(2.0, "test");
        words[2] = new VocabWord(3.0, "tester");
        for (int i = 0; i < (words.length); ++i) {
            cache.addToken(words[i]);
            cache.addWordToIndex(i, words[i].getLabel());
        }
        word2Vec.setVocab(cache);
        String json = null;
        Word2Vec unserialized = null;
        try {
            json = word2Vec.toJson();
            Word2VecTests.log.info("{}", json.toString());
            unserialized = Word2Vec.fromJson(json);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertEquals(cache.totalWordOccurrences(), getVocab().totalWordOccurrences());
        Assert.assertEquals(cache.totalNumberOfDocs(), getVocab().totalNumberOfDocs());
        for (int i = 0; i < (words.length); ++i) {
            val cached = cache.wordAtIndex(i);
            val restored = getVocab().wordAtIndex(i);
            Assert.assertNotNull(cached);
            Assert.assertEquals(cached, restored);
        }
    }

    @Test
    public void testWord2VecConfigurationConsistency() {
        VectorsConfiguration configuration = new VectorsConfiguration();
        Assert.assertEquals(configuration.getLayersSize(), 200);
        Assert.assertEquals(configuration.getLayersSize(), 200);
        assert (configuration.getElementsLearningAlgorithm()) == null;
        Assert.assertEquals(configuration.isAllowParallelTokenization(), false);
        Assert.assertEquals(configuration.isPreciseMode(), false);
        Assert.assertEquals(configuration.getBatchSize(), 512);
        assert (configuration.getModelUtils()) == null;
        Assert.assertTrue((!(configuration.isPreciseMode())));
        Assert.assertEquals(configuration.getBatchSize(), 512);
        Assert.assertEquals(configuration.getWindow(), 5);
        Assert.assertEquals(configuration.getMinWordFrequency(), 5);
        Assert.assertEquals(configuration.getIterations(), 1);
        Assert.assertEquals(configuration.getSeed(), 0);
        Assert.assertEquals(configuration.getLearningRate(), 0.025, 1.0E-5F);
        Assert.assertEquals(configuration.getEpochs(), 1);
        Assert.assertTrue(configuration.getStopList().isEmpty());
        Assert.assertEquals(configuration.getSampling(), 0.0, 1.0E-5F);
        Assert.assertEquals(configuration.getNegative(), 0, 1.0E-5F);
        Assert.assertTrue((!(configuration.isUseAdaGrad())));
        Assert.assertTrue(configuration.isUseHierarchicSoftmax());
        Assert.assertEquals(configuration.getMinLearningRate(), 1.0E-4, 1.0E-5F);
        Assert.assertTrue((!(configuration.isUseUnknown())));
        Word2Vec word2Vec = layerSize(1000).limitVocabularySize(1000).elementsLearningAlgorithm(org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW.class.getCanonicalName()).allowParallelTokenization(true).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.FlatModelUtils<VocabWord>()).usePreciseMode(true).batchSize(1024).windowSize(23).minWordFrequency(24).iterations(54).seed(45).learningRate(0.08).epochs(45).stopWords(Collections.singletonList("NOT")).sampling(44).workers(45).negativeSample(56).useAdaGrad(true).useHierarchicSoftmax(false).minLearningRate(0.002).resetModel(true).useUnknown(true).enableScavenger(true).usePreciseWeightInit(true).build();
        Assert.assertEquals(word2Vec.getConfiguration().getLayersSize(), word2Vec.getLayerSize());
        Assert.assertEquals(word2Vec.getConfiguration().getLayersSize(), 1000);
        Assert.assertEquals(word2Vec.getConfiguration().getElementsLearningAlgorithm(), org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW.class.getCanonicalName());
        Assert.assertEquals(word2Vec.getConfiguration().isAllowParallelTokenization(), true);
        Assert.assertEquals(word2Vec.getConfiguration().isPreciseMode(), true);
        Assert.assertEquals(word2Vec.getConfiguration().getBatchSize(), 1024);
        String modelUtilsName = word2Vec.getConfiguration().getModelUtils();
        Assert.assertEquals(modelUtilsName, org.deeplearning4j.models.embeddings.reader.impl.FlatModelUtils.class.getCanonicalName());
        Assert.assertTrue(word2Vec.getConfiguration().isPreciseMode());
        Assert.assertEquals(word2Vec.getConfiguration().getBatchSize(), 1024);
        Assert.assertEquals(word2Vec.getConfiguration().getWindow(), 23);
        Assert.assertEquals(word2Vec.getConfiguration().getMinWordFrequency(), 24);
        Assert.assertEquals(word2Vec.getConfiguration().getIterations(), 54);
        Assert.assertEquals(word2Vec.getConfiguration().getSeed(), 45);
        Assert.assertEquals(word2Vec.getConfiguration().getLearningRate(), 0.08, 1.0E-5F);
        Assert.assertEquals(word2Vec.getConfiguration().getEpochs(), 45);
        Assert.assertEquals(word2Vec.getConfiguration().getStopList().size(), 1);
        Assert.assertEquals(configuration.getSampling(), 44.0, 1.0E-5F);
        Assert.assertEquals(configuration.getNegative(), 56.0, 1.0E-5F);
        Assert.assertTrue(configuration.isUseAdaGrad());
        Assert.assertTrue((!(configuration.isUseHierarchicSoftmax())));
        Assert.assertEquals(configuration.getMinLearningRate(), 0.002, 1.0E-5F);
        Assert.assertTrue(configuration.isUseUnknown());
    }

    @Test
    public void testWordVectorsPartiallyAbsentLabels() throws Exception {
        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec = layerSize(100).stopWords(new ArrayList<String>()).seed(42).learningRate(0.025).minLearningRate(0.001).sampling(0).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW<VocabWord>()).epochs(1).windowSize(5).useHierarchicSoftmax(true).allowParallelTokenization(true).useUnknown(false).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.FlatModelUtils<VocabWord>()).iterate(iter).tokenizerFactory(t).build();
        vec.fit();
        ArrayList<String> labels = new ArrayList<>();
        labels.add("fewfew");
        labels.add("day");
        labels.add("night");
        labels.add("week");
        INDArray matrix = vec.getWordVectors(labels);
        Assert.assertEquals(3, matrix.rows());
        Assert.assertEquals(matrix.getRow(0), vec.getWordVectorMatrix("day"));
        Assert.assertEquals(matrix.getRow(1), vec.getWordVectorMatrix("night"));
        Assert.assertEquals(matrix.getRow(2), vec.getWordVectorMatrix("week"));
    }

    @Test
    public void testWordVectorsAbsentLabels() throws Exception {
        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec = layerSize(100).stopWords(new ArrayList<String>()).seed(42).learningRate(0.025).minLearningRate(0.001).sampling(0).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW<VocabWord>()).epochs(1).windowSize(5).useHierarchicSoftmax(true).allowParallelTokenization(true).useUnknown(false).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.FlatModelUtils<VocabWord>()).iterate(iter).tokenizerFactory(t).build();
        vec.fit();
        ArrayList<String> labels = new ArrayList<>();
        labels.add("fewfew");
        INDArray matrix = vec.getWordVectors(labels);
        Assert.assertTrue(matrix.isEmpty());
    }

    @Test
    public void testWordVectorsAbsentLabels_WithUnknown() throws Exception {
        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec = // .negativeSample(10)
        layerSize(100).stopWords(new ArrayList<String>()).seed(42).learningRate(0.025).minLearningRate(0.001).sampling(0).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram<VocabWord>()).epochs(1).windowSize(5).allowParallelTokenization(true).workers(4).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils<VocabWord>()).iterate(iter).tokenizerFactory(t).useUnknown(true).unknownElement(new VocabWord(1, "UNKOWN")).build();
        vec.fit();
        ArrayList<String> labels = new ArrayList<>();
        labels.add("bus");
        labels.add("car");
        INDArray matrix = vec.getWordVectors(labels);
        for (int i = 0; i < (labels.size()); ++i)
            Assert.assertEquals(matrix.getRow(i), vec.getWordVectorMatrix("UNKNOWN"));

    }

    @Test
    public void weightsNotUpdated_WhenLocked() throws Exception {
        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());
        Word2Vec vec1 = layerSize(100).stopWords(new ArrayList<String>()).seed(42).learningRate(0.025).minLearningRate(0.001).sampling(0).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram<VocabWord>()).epochs(1).windowSize(5).allowParallelTokenization(true).workers(1).iterate(iter).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils<VocabWord>()).build();
        vec1.fit();
        iter = new BasicLineIterator(inputFile2.getAbsolutePath());
        Word2Vec vec2 = layerSize(100).stopWords(new ArrayList<String>()).seed(32).learningRate(0.021).minLearningRate(0.001).sampling(0).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram<VocabWord>()).epochs(1).windowSize(5).allowParallelTokenization(true).workers(1).iterate(iter).intersectModel(vec1, true).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils<VocabWord>()).build();
        vec2.fit();
        Assert.assertEquals(vec1.getWordVectorMatrix("put"), vec2.getWordVectorMatrix("put"));
        Assert.assertEquals(vec1.getWordVectorMatrix("part"), vec2.getWordVectorMatrix("part"));
        Assert.assertEquals(vec1.getWordVectorMatrix("made"), vec2.getWordVectorMatrix("made"));
        Assert.assertEquals(vec1.getWordVectorMatrix("money"), vec2.getWordVectorMatrix("money"));
    }

    @Test
    public void weightsNotUpdated_WhenLocked_CBOW() throws Exception {
        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());
        Word2Vec vec1 = layerSize(100).stopWords(new ArrayList<String>()).seed(42).learningRate(0.025).minLearningRate(0.001).sampling(0).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW<VocabWord>()).epochs(1).windowSize(5).allowParallelTokenization(true).workers(1).iterate(iter).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils<VocabWord>()).build();
        vec1.fit();
        iter = new BasicLineIterator(inputFile2.getAbsolutePath());
        Word2Vec vec2 = layerSize(100).stopWords(new ArrayList<String>()).seed(32).learningRate(0.021).minLearningRate(0.001).sampling(0).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW<VocabWord>()).epochs(1).windowSize(5).allowParallelTokenization(true).workers(1).iterate(iter).intersectModel(vec1, true).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils<VocabWord>()).build();
        vec2.fit();
        Assert.assertEquals(vec1.getWordVectorMatrix("put"), vec2.getWordVectorMatrix("put"));
        Assert.assertEquals(vec1.getWordVectorMatrix("part"), vec2.getWordVectorMatrix("part"));
        Assert.assertEquals(vec1.getWordVectorMatrix("made"), vec2.getWordVectorMatrix("made"));
        Assert.assertEquals(vec1.getWordVectorMatrix("money"), vec2.getWordVectorMatrix("money"));
    }
}

