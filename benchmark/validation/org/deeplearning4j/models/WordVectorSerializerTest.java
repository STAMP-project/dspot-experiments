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
package org.deeplearning4j.models;


import com.google.common.primitives.Doubles;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.val;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.VectorsConfiguration;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.sentenceiterator.UimaSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author jeffreytang
 * @author raver119@gmail.com
 */
public class WordVectorSerializerTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    private File textFile;

    private File binaryFile;

    private File textFile2;

    private File fastTextRaw;

    private File fastTextZip;

    private File fastTextGzip;

    String pathToWriteto;

    private Logger logger = LoggerFactory.getLogger(WordVectorSerializerTest.class);

    @Test
    public void testLoaderText() throws IOException {
        WordVectors vec = WordVectorSerializer.readWord2VecModel(textFile);
        Assert.assertEquals(vec.vocab().numWords(), 30);
        Assert.assertTrue(vec.vocab().hasToken("Morgan_Freeman"));
        Assert.assertTrue(vec.vocab().hasToken("JA_Montalbano"));
    }

    @Test
    public void testLoaderStream() throws IOException {
        WordVectors vec = WordVectorSerializer.readWord2VecModel(textFile);
        Assert.assertEquals(vec.vocab().numWords(), 30);
        Assert.assertTrue(vec.vocab().hasToken("Morgan_Freeman"));
        Assert.assertTrue(vec.vocab().hasToken("JA_Montalbano"));
    }

    @Test
    public void testLoaderBinary() throws IOException {
        WordVectors vec = WordVectorSerializer.readWord2VecModel(binaryFile);
        Assert.assertEquals(vec.vocab().numWords(), 30);
        Assert.assertTrue(vec.vocab().hasToken("Morgan_Freeman"));
        Assert.assertTrue(vec.vocab().hasToken("JA_Montalbano"));
        double[] wordVector1 = vec.getWordVector("Morgan_Freeman");
        double[] wordVector2 = vec.getWordVector("JA_Montalbano");
        Assert.assertTrue(((wordVector1.length) == 300));
        Assert.assertTrue(((wordVector2.length) == 300));
        Assert.assertEquals(Doubles.asList(wordVector1).get(0), 0.044423, 0.001);
        Assert.assertEquals(Doubles.asList(wordVector2).get(0), 0.051964, 0.001);
    }

    @Test
    public void testIndexPersistence() throws Exception {
        File inputFile = new ClassPathResource("/big/raw_sentences.txt").getFile();
        SentenceIterator iter = UimaSentenceIterator.createWithPath(inputFile.getAbsolutePath());
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec = new Word2Vec.Builder().minWordFrequency(5).iterations(1).epochs(1).layerSize(100).stopWords(new ArrayList<String>()).useAdaGrad(false).negativeSample(5).seed(42).windowSize(5).iterate(iter).tokenizerFactory(t).build();
        vec.fit();
        VocabCache orig = vec.getVocab();
        File tempFile = File.createTempFile("temp", "w2v");
        tempFile.deleteOnExit();
        WordVectorSerializer.writeWordVectors(vec, tempFile);
        WordVectors vec2 = WordVectorSerializer.loadTxtVectors(tempFile);
        VocabCache rest = vec2.vocab();
        Assert.assertEquals(orig.totalNumberOfDocs(), rest.totalNumberOfDocs());
        for (VocabWord word : vec.getVocab().vocabWords()) {
            INDArray array1 = vec.getWordVectorMatrix(word.getLabel());
            INDArray array2 = vec2.getWordVectorMatrix(word.getLabel());
            Assert.assertEquals(array1, array2);
        }
    }

    @Test
    public void testFullModelSerialization() throws Exception {
        File inputFile = new ClassPathResource("/big/raw_sentences.txt").getFile();
        SentenceIterator iter = UimaSentenceIterator.createWithPath(inputFile.getAbsolutePath());
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        InMemoryLookupCache cache = new InMemoryLookupCache(false);
        WeightLookupTable table = new InMemoryLookupTable.Builder().vectorLength(100).useAdaGrad(false).negative(5.0).cache(cache).lr(0.025F).build();
        Word2Vec vec = // .workers(6)
        new Word2Vec.Builder().minWordFrequency(5).iterations(1).epochs(1).layerSize(100).lookupTable(table).stopWords(new ArrayList<String>()).useAdaGrad(false).negativeSample(5).vocabCache(cache).seed(42).windowSize(5).iterate(iter).tokenizerFactory(t).build();
        Assert.assertEquals(new ArrayList<String>(), vec.getStopWords());
        vec.fit();
        // logger.info("Original word 0: " + cache.wordFor(cache.wordAtIndex(0)));
        // logger.info("Closest Words:");
        Collection<String> lst = vec.wordsNearest("day", 10);
        System.out.println(lst);
        WordVectorSerializer.writeFullModel(vec, "tempModel.txt");
        File modelFile = new File("tempModel.txt");
        modelFile.deleteOnExit();
        Assert.assertTrue(modelFile.exists());
        Assert.assertTrue(((modelFile.length()) > 0));
        Word2Vec vec2 = WordVectorSerializer.loadFullModel("tempModel.txt");
        Assert.assertNotEquals(null, vec2);
        Assert.assertEquals(vec.getConfiguration(), vec2.getConfiguration());
        // logger.info("Source ExpTable: " + ArrayUtils.toString(((InMemoryLookupTable) table).getExpTable()));
        // logger.info("Dest  ExpTable: " + ArrayUtils.toString(((InMemoryLookupTable)  vec2.getLookupTable()).getExpTable()));
        Assert.assertTrue(ArrayUtils.isEquals(getExpTable(), getExpTable()));
        InMemoryLookupTable restoredTable = ((InMemoryLookupTable) (vec2.lookupTable()));
        /* logger.info("Restored word 1: " + restoredTable.getVocab().wordFor(restoredTable.getVocab().wordAtIndex(1)));
        logger.info("Restored word 'it': " + restoredTable.getVocab().wordFor("it"));
        logger.info("Original word 1: " + cache.wordFor(cache.wordAtIndex(1)));
        logger.info("Original word 'i': " + cache.wordFor("i"));
        logger.info("Original word 0: " + cache.wordFor(cache.wordAtIndex(0)));
        logger.info("Restored word 0: " + restoredTable.getVocab().wordFor(restoredTable.getVocab().wordAtIndex(0)));
         */
        Assert.assertEquals(cache.wordAtIndex(1), restoredTable.getVocab().wordAtIndex(1));
        Assert.assertEquals(cache.wordAtIndex(7), restoredTable.getVocab().wordAtIndex(7));
        Assert.assertEquals(cache.wordAtIndex(15), restoredTable.getVocab().wordAtIndex(15));
        /* these tests needed only to make sure INDArray equality is working properly */
        double[] array1 = new double[]{ 0.323232325, 0.65756575, 0.12315, 0.12312315, 0.1232135, 0.12312315, 0.4343423425, 0.15 };
        double[] array2 = new double[]{ 0.423232325, 0.25756575, 0.12375, 0.12311315, 0.1232035, 0.12318315, 0.4343493425, 0.25 };
        Assert.assertNotEquals(Nd4j.create(array1), Nd4j.create(array2));
        Assert.assertEquals(Nd4j.create(array1), Nd4j.create(array1));
        INDArray rSyn0_1 = restoredTable.getSyn0().slice(1);
        INDArray oSyn0_1 = getSyn0().slice(1);
        // logger.info("Restored syn0: " + rSyn0_1);
        // logger.info("Original syn0: " + oSyn0_1);
        Assert.assertEquals(oSyn0_1, rSyn0_1);
        // just checking $^###! syn0/syn1 order
        int cnt = 0;
        for (VocabWord word : cache.vocabWords()) {
            INDArray rSyn0 = restoredTable.getSyn0().slice(word.getIndex());
            INDArray oSyn0 = getSyn0().slice(word.getIndex());
            Assert.assertEquals(rSyn0, oSyn0);
            Assert.assertEquals(1.0, arraysSimilarity(rSyn0, oSyn0), 0.001);
            INDArray rSyn1 = restoredTable.getSyn1().slice(word.getIndex());
            INDArray oSyn1 = getSyn1().slice(word.getIndex());
            Assert.assertEquals(rSyn1, oSyn1);
            if ((arraysSimilarity(rSyn1, oSyn1)) < 0.98) {
                // logger.info("Restored syn1: " + rSyn1);
                // logger.info("Original  syn1: " + oSyn1);
            }
            // we exclude word 222 since it has syn1 full of zeroes
            if (cnt != 222)
                Assert.assertEquals(1.0, arraysSimilarity(rSyn1, oSyn1), 0.001);

            if ((getSyn1Neg()) != null) {
                INDArray rSyn1Neg = restoredTable.getSyn1Neg().slice(word.getIndex());
                INDArray oSyn1Neg = getSyn1Neg().slice(word.getIndex());
                Assert.assertEquals(rSyn1Neg, oSyn1Neg);
                // assertEquals(1.0, arraysSimilarity(rSyn1Neg, oSyn1Neg), 0.001);
            }
            Assert.assertEquals(word.getHistoricalGradient(), restoredTable.getVocab().wordFor(word.getWord()).getHistoricalGradient());
            cnt++;
        }
        // at this moment we can assume that whole model is transferred, and we can call fit over new model
        // iter.reset();
        iter = UimaSentenceIterator.createWithPath(inputFile.getAbsolutePath());
        vec2.setTokenizerFactory(t);
        vec2.setSentenceIterator(iter);
        vec2.fit();
        INDArray day1 = vec.getWordVectorMatrix("day");
        INDArray day2 = vec2.getWordVectorMatrix("day");
        INDArray night1 = vec.getWordVectorMatrix("night");
        INDArray night2 = vec2.getWordVectorMatrix("night");
        double simD = arraysSimilarity(day1, day2);
        double simN = arraysSimilarity(night1, night2);
        logger.info(("Vec1 day: " + day1));
        logger.info(("Vec2 day: " + day2));
        logger.info(("Vec1 night: " + night1));
        logger.info(("Vec2 night: " + night2));
        logger.info(("Day/day cross-model similarity: " + simD));
        logger.info(("Night/night cross-model similarity: " + simN));
        logger.info(("Vec1 day/night similiraty: " + (vec.similarity("day", "night"))));
        logger.info(("Vec2 day/night similiraty: " + (vec2.similarity("day", "night"))));
        // check if cross-model values are not the same
        Assert.assertNotEquals(1.0, simD, 0.001);
        Assert.assertNotEquals(1.0, simN, 0.001);
        // check if cross-model values are still close to each other
        Assert.assertTrue((simD > 0.7));
        Assert.assertTrue((simN > 0.7));
        modelFile.delete();
    }

    @Test
    public void testOutputStream() throws Exception {
        File file = File.createTempFile("tmp_ser", "ssa");
        file.deleteOnExit();
        File inputFile = new ClassPathResource("/big/raw_sentences.txt").getFile();
        SentenceIterator iter = new BasicLineIterator(inputFile);
        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        InMemoryLookupCache cache = new InMemoryLookupCache(false);
        WeightLookupTable table = new InMemoryLookupTable.Builder().vectorLength(100).useAdaGrad(false).negative(5.0).cache(cache).lr(0.025F).build();
        Word2Vec vec = // .workers(6)
        new Word2Vec.Builder().minWordFrequency(5).iterations(1).epochs(1).layerSize(100).lookupTable(table).stopWords(new ArrayList<String>()).useAdaGrad(false).negativeSample(5).vocabCache(cache).seed(42).windowSize(5).iterate(iter).tokenizerFactory(t).build();
        Assert.assertEquals(new ArrayList<String>(), vec.getStopWords());
        vec.fit();
        INDArray day1 = vec.getWordVectorMatrix("day");
        WordVectorSerializer.writeWordVectors(vec, new FileOutputStream(file));
        WordVectors vec2 = WordVectorSerializer.loadTxtVectors(file);
        INDArray day2 = vec2.getWordVectorMatrix("day");
        Assert.assertEquals(day1, day2);
        File tempFile = File.createTempFile("tetsts", "Fdfs");
        tempFile.deleteOnExit();
        WordVectorSerializer.writeWord2VecModel(vec, tempFile);
        Word2Vec vec3 = WordVectorSerializer.readWord2VecModel(tempFile);
    }

    @Test
    public void testParaVecSerialization1() throws Exception {
        VectorsConfiguration configuration = new VectorsConfiguration();
        configuration.setIterations(14123);
        configuration.setLayersSize(156);
        INDArray syn0 = Nd4j.rand(100, configuration.getLayersSize());
        INDArray syn1 = Nd4j.rand(100, configuration.getLayersSize());
        AbstractCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        for (int i = 0; i < 100; i++) {
            VocabWord word = new VocabWord(((float) (i)), ("word_" + i));
            List<Integer> points = new ArrayList<>();
            List<Byte> codes = new ArrayList<>();
            int num = RandomUtils.nextInt(1, 20);
            for (int x = 0; x < num; x++) {
                points.add(RandomUtils.nextInt(1, 100000));
                codes.add(RandomUtils.nextBytes(10)[0]);
            }
            if ((org.deeplearning4j.models.embeddings.learning.impl.elements.RandomUtils.nextInt(10)) < 3) {
                word.markAsLabel(true);
            }
            word.setIndex(i);
            word.setPoints(points);
            word.setCodes(codes);
            cache.addToken(word);
            cache.addWordToIndex(i, word.getLabel());
        }
        InMemoryLookupTable<VocabWord> lookupTable = ((InMemoryLookupTable<VocabWord>) (new InMemoryLookupTable.Builder<VocabWord>().vectorLength(configuration.getLayersSize()).cache(cache).build()));
        lookupTable.setSyn0(syn0);
        lookupTable.setSyn1(syn1);
        ParagraphVectors originalVectors = new ParagraphVectors.Builder(configuration).vocabCache(cache).lookupTable(lookupTable).build();
        File tempFile = File.createTempFile("paravec", "tests");
        tempFile.deleteOnExit();
        WordVectorSerializer.writeParagraphVectors(originalVectors, tempFile);
        ParagraphVectors restoredVectors = WordVectorSerializer.readParagraphVectors(tempFile);
        InMemoryLookupTable<VocabWord> restoredLookupTable = ((InMemoryLookupTable<VocabWord>) (restoredVectors.getLookupTable()));
        AbstractCache<VocabWord> restoredVocab = ((AbstractCache<VocabWord>) (restoredVectors.getVocab()));
        Assert.assertEquals(restoredLookupTable.getSyn0(), lookupTable.getSyn0());
        Assert.assertEquals(restoredLookupTable.getSyn1(), lookupTable.getSyn1());
        for (int i = 0; i < (cache.numWords()); i++) {
            Assert.assertEquals(cache.elementAtIndex(i).isLabel(), restoredVocab.elementAtIndex(i).isLabel());
            Assert.assertEquals(cache.wordAtIndex(i), restoredVocab.wordAtIndex(i));
            Assert.assertEquals(cache.elementAtIndex(i).getElementFrequency(), restoredVocab.elementAtIndex(i).getElementFrequency(), 0.1F);
            List<Integer> originalPoints = cache.elementAtIndex(i).getPoints();
            List<Integer> restoredPoints = restoredVocab.elementAtIndex(i).getPoints();
            Assert.assertEquals(originalPoints.size(), restoredPoints.size());
            for (int x = 0; x < (originalPoints.size()); x++) {
                Assert.assertEquals(originalPoints.get(x), restoredPoints.get(x));
            }
            List<Byte> originalCodes = cache.elementAtIndex(i).getCodes();
            List<Byte> restoredCodes = restoredVocab.elementAtIndex(i).getCodes();
            Assert.assertEquals(originalCodes.size(), restoredCodes.size());
            for (int x = 0; x < (originalCodes.size()); x++) {
                Assert.assertEquals(originalCodes.get(x), restoredCodes.get(x));
            }
        }
    }

    /**
     * This method tests binary file loading as static model
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testStaticLoaderBinary() throws Exception {
        logger.info("Executor name: {}", Nd4j.getExecutioner().getClass().getSimpleName());
        WordVectors vectorsLive = WordVectorSerializer.readWord2VecModel(binaryFile);
        WordVectors vectorsStatic = WordVectorSerializer.loadStaticModel(binaryFile);
        INDArray arrayLive = vectorsLive.getWordVectorMatrix("Morgan_Freeman");
        INDArray arrayStatic = vectorsStatic.getWordVectorMatrix("Morgan_Freeman");
        Assert.assertNotEquals(null, arrayLive);
        Assert.assertEquals(arrayLive, arrayStatic);
    }

    @Test
    public void testStaticLoaderFromStream() throws Exception {
        logger.info("Executor name: {}", Nd4j.getExecutioner().getClass().getSimpleName());
        WordVectors vectorsLive = WordVectorSerializer.readWord2VecModel(binaryFile);
        WordVectors vectorsStatic = WordVectorSerializer.loadStaticModel(new FileInputStream(binaryFile));
        INDArray arrayLive = vectorsLive.getWordVectorMatrix("Morgan_Freeman");
        INDArray arrayStatic = vectorsStatic.getWordVectorMatrix("Morgan_Freeman");
        Assert.assertNotEquals(null, arrayLive);
        Assert.assertEquals(arrayLive, arrayStatic);
    }

    /**
     * This method tests CSV file loading as static model
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testStaticLoaderText() throws Exception {
        logger.info("Executor name: {}", Nd4j.getExecutioner().getClass().getSimpleName());
        WordVectors vectorsLive = WordVectorSerializer.loadTxtVectors(textFile);
        WordVectors vectorsStatic = WordVectorSerializer.loadStaticModel(textFile);
        INDArray arrayLive = vectorsLive.getWordVectorMatrix("Morgan_Freeman");
        INDArray arrayStatic = vectorsStatic.getWordVectorMatrix("Morgan_Freeman");
        Assert.assertNotEquals(null, arrayLive);
        Assert.assertEquals(arrayLive, arrayStatic);
    }

    /**
     * This method tests ZIP file loading as static model
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testStaticLoaderArchive() throws Exception {
        logger.info("Executor name: {}", Nd4j.getExecutioner().getClass().getSimpleName());
        File w2v = new ClassPathResource("word2vec.dl4j/file.w2v").getFile();
        WordVectors vectorsLive = WordVectorSerializer.readWord2Vec(w2v);
        WordVectors vectorsStatic = WordVectorSerializer.loadStaticModel(w2v);
        INDArray arrayLive = vectorsLive.getWordVectorMatrix("night");
        INDArray arrayStatic = vectorsStatic.getWordVectorMatrix("night");
        Assert.assertNotEquals(null, arrayLive);
        Assert.assertEquals(arrayLive, arrayStatic);
    }

    @Test
    public void testUnifiedLoaderArchive1() throws Exception {
        logger.info("Executor name: {}", Nd4j.getExecutioner().getClass().getSimpleName());
        File w2v = new ClassPathResource("word2vec.dl4j/file.w2v").getFile();
        WordVectors vectorsLive = WordVectorSerializer.readWord2Vec(w2v);
        WordVectors vectorsUnified = WordVectorSerializer.readWord2VecModel(w2v, false);
        INDArray arrayLive = vectorsLive.getWordVectorMatrix("night");
        INDArray arrayStatic = vectorsUnified.getWordVectorMatrix("night");
        Assert.assertNotEquals(null, arrayLive);
        Assert.assertEquals(arrayLive, arrayStatic);
        Assert.assertEquals(null, getSyn1());
        Assert.assertEquals(null, getSyn1Neg());
    }

    @Test
    public void testUnifiedLoaderArchive2() throws Exception {
        logger.info("Executor name: {}", Nd4j.getExecutioner().getClass().getSimpleName());
        File w2v = new ClassPathResource("word2vec.dl4j/file.w2v").getFile();
        WordVectors vectorsLive = WordVectorSerializer.readWord2Vec(w2v);
        WordVectors vectorsUnified = WordVectorSerializer.readWord2VecModel(w2v, true);
        INDArray arrayLive = vectorsLive.getWordVectorMatrix("night");
        INDArray arrayStatic = vectorsUnified.getWordVectorMatrix("night");
        Assert.assertNotEquals(null, arrayLive);
        Assert.assertEquals(arrayLive, arrayStatic);
        Assert.assertNotEquals(null, getSyn1());
    }

    /**
     * This method tests CSV file loading via unified loader
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUnifiedLoaderText() throws Exception {
        logger.info("Executor name: {}", Nd4j.getExecutioner().getClass().getSimpleName());
        WordVectors vectorsLive = WordVectorSerializer.loadTxtVectors(textFile);
        WordVectors vectorsUnified = WordVectorSerializer.readWord2VecModel(textFile, true);
        INDArray arrayLive = vectorsLive.getWordVectorMatrix("Morgan_Freeman");
        INDArray arrayStatic = vectorsUnified.getWordVectorMatrix("Morgan_Freeman");
        Assert.assertNotEquals(null, arrayLive);
        Assert.assertEquals(arrayLive, arrayStatic);
        // we're trying EXTENDED model, but file doesn't have syn1/huffman info, so it should be silently degraded to simplified model
        Assert.assertEquals(null, getSyn1());
    }

    /**
     * This method tests binary file loading via unified loader
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUnifiedLoaderBinary() throws Exception {
        logger.info("Executor name: {}", Nd4j.getExecutioner().getClass().getSimpleName());
        WordVectors vectorsLive = WordVectorSerializer.readWord2VecModel(binaryFile);
        WordVectors vectorsStatic = WordVectorSerializer.readWord2VecModel(binaryFile, false);
        INDArray arrayLive = vectorsLive.getWordVectorMatrix("Morgan_Freeman");
        INDArray arrayStatic = vectorsStatic.getWordVectorMatrix("Morgan_Freeman");
        Assert.assertNotEquals(null, arrayLive);
        Assert.assertEquals(arrayLive, arrayStatic);
    }

    @Test
    public void testVocabPeristence() throws Exception {
        val vocabA = new AbstractCache.Builder<VocabWord>().build();
        vocabA.addToken(new VocabWord(3.0, "alpha"));
        vocabA.addWordToIndex(1, "alpha");
        vocabA.addToken(new VocabWord(4.0, "beta"));
        vocabA.addWordToIndex(0, "beta");
        val tmpFile = File.createTempFile("sdsds", "sfdsfdsgsdf");
        tmpFile.deleteOnExit();
        vocabA.setTotalWordOccurences(200);
        vocabA.incrementTotalDocCount(100);
        Assert.assertEquals(100, vocabA.totalNumberOfDocs());
        Assert.assertEquals(200, vocabA.totalWordOccurrences());
        WordVectorSerializer.writeVocabCache(vocabA, tmpFile);
        val vocabB = WordVectorSerializer.readVocabCache(tmpFile);
        Assert.assertEquals(vocabA.wordAtIndex(0), vocabB.wordAtIndex(0));
        Assert.assertEquals(vocabA.wordAtIndex(1), vocabB.wordAtIndex(1));
        Assert.assertEquals(vocabA.numWords(), vocabB.numWords());
        Assert.assertEquals(vocabA.totalNumberOfDocs(), vocabB.totalNumberOfDocs());
        Assert.assertEquals(vocabA.totalWordOccurrences(), vocabB.totalWordOccurrences());
    }

    @Test
    public void testMalformedLabels1() throws Exception {
        List<String> words = new ArrayList<>();
        words.add("test A");
        words.add("test B");
        words.add("test\nC");
        words.add("test`D");
        words.add("test_E");
        words.add("test 5");
        AbstractCache<VocabWord> vocabCache = new AbstractCache();
        int cnt = 0;
        for (String word : words) {
            vocabCache.addToken(new VocabWord(1.0, word));
            vocabCache.addWordToIndex(cnt, word);
            cnt++;
        }
        vocabCache.elementAtIndex(1).markAsLabel(true);
        InMemoryLookupTable<VocabWord> lookupTable = new InMemoryLookupTable(vocabCache, 10, false, 0.01, Nd4j.getRandom(), 0.0);
        lookupTable.resetWeights(true);
        Assert.assertNotEquals(null, lookupTable.getSyn0());
        Assert.assertNotEquals(null, lookupTable.getSyn1());
        Assert.assertNotEquals(null, lookupTable.getExpTable());
        Assert.assertEquals(null, lookupTable.getSyn1Neg());
        ParagraphVectors vec = new ParagraphVectors.Builder().lookupTable(lookupTable).vocabCache(vocabCache).build();
        File tempFile = File.createTempFile("temp", "w2v");
        tempFile.deleteOnExit();
        WordVectorSerializer.writeParagraphVectors(vec, tempFile);
        ParagraphVectors restoredVec = WordVectorSerializer.readParagraphVectors(tempFile);
        for (String word : words) {
            Assert.assertEquals(true, restoredVec.hasWord(word));
        }
        Assert.assertTrue(restoredVec.getVocab().elementAtIndex(1).isLabel());
    }

    @Test
    public void testB64_1() throws Exception {
        String wordA = "night";
        String wordB = "night day";
        String encA = WordVectorSerializer.encodeB64(wordA);
        String encB = WordVectorSerializer.encodeB64(wordB);
        Assert.assertEquals(wordA, WordVectorSerializer.decodeB64(encA));
        Assert.assertEquals(wordB, WordVectorSerializer.decodeB64(encB));
        Assert.assertEquals(wordA, WordVectorSerializer.decodeB64(wordA));
        Assert.assertEquals(wordB, WordVectorSerializer.decodeB64(wordB));
    }

    @Test
    public void testFastText() {
        File[] files = new File[]{ fastTextRaw, fastTextZip, fastTextGzip };
        for (File file : files) {
            try {
                Word2Vec word2Vec = WordVectorSerializer.readAsCsv(file);
                Assert.assertEquals(99, word2Vec.getVocab().numWords());
            } catch (Exception e) {
                Assert.fail(((("Failure for input file " + (file.getAbsolutePath())) + " ") + (e.getMessage())));
            }
        }
    }
}

