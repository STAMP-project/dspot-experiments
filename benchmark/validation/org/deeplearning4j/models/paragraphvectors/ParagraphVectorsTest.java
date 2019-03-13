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
package org.deeplearning4j.models.paragraphvectors;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.sequencevectors.sequence.Sequence;
import org.deeplearning4j.models.sequencevectors.transformers.impl.SentenceTransformer;
import org.deeplearning4j.models.sequencevectors.transformers.impl.iterables.ParallelTransformerIterator;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.documentiterator.FileLabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.documentiterator.LabelsSource;
import org.deeplearning4j.text.sentenceiterator.AggregatingSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.io.CollectionUtils;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.linalg.util.SerializationUtils;


/**
 * Created by agibsonccc on 12/3/14.
 */
@Slf4j
public class ParagraphVectorsTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    /* @Test
    public void testWord2VecRunThroughVectors() throws Exception {
    ClassPathResource resource = new ClassPathResource("/big/raw_sentences.txt");
    File file = resource.getFile().getParentFile();
    LabelAwareSentenceIterator iter = LabelAwareUimaSentenceIterator.createWithPath(file.getAbsolutePath());


    TokenizerFactory t = new UimaTokenizerFactory();


    ParagraphVectors vec = new ParagraphVectors.Builder()
    .minWordFrequency(1).iterations(5).labels(Arrays.asList("label1", "deeple"))
    .layerSize(100)
    .stopWords(new ArrayList<String>())
    .windowSize(5).iterate(iter).tokenizerFactory(t).build();

    assertEquals(new ArrayList<String>(), vec.getStopWords());


    vec.fit();
    double sim = vec.similarity("day","night");
    log.info("day/night similarity: " + sim);
    new File("cache.ser").delete();

    }
     */
    /**
     * This test checks, how vocab is built using SentenceIterator provided, without labels.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testParagraphVectorsVocabBuilding1() throws Exception {
        ClassPathResource resource = new ClassPathResource("/big/raw_sentences.txt");
        File file = resource.getFile();// .getParentFile();

        SentenceIterator iter = new BasicLineIterator(file);// UimaSentenceIterator.createWithPath(file.getAbsolutePath());

        int numberOfLines = 0;
        while (iter.hasNext()) {
            iter.nextSentence();
            numberOfLines++;
        } 
        iter.reset();
        InMemoryLookupCache cache = new InMemoryLookupCache(false);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        // LabelsSource source = new LabelsSource("DOC_");
        ParagraphVectors vec = // .labelsGenerator(source)
        new ParagraphVectors.Builder().minWordFrequency(1).iterations(5).layerSize(100).windowSize(5).iterate(iter).vocabCache(cache).tokenizerFactory(t).build();
        vec.buildVocab();
        LabelsSource source = vec.getLabelsSource();
        // VocabCache cache = vec.getVocab();
        log.info(("Number of lines in corpus: " + numberOfLines));
        Assert.assertEquals(numberOfLines, source.getLabels().size());
        Assert.assertEquals(97162, source.getLabels().size());
        Assert.assertNotEquals(null, cache);
        Assert.assertEquals(97406, cache.numWords());
        // proper number of words for minWordsFrequency = 1 is 244
        Assert.assertEquals(244, ((cache.numWords()) - (source.getLabels().size())));
    }

    /**
     * This test doesn't really cares about actual results. We only care about equality between live model & restored models
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testParagraphVectorsModelling1() throws Exception {
        ClassPathResource resource = new ClassPathResource("/big/raw_sentences.txt");
        File file = resource.getFile();
        SentenceIterator iter = new BasicLineIterator(file);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        LabelsSource source = new LabelsSource("DOC_");
        ParagraphVectors vec = new ParagraphVectors.Builder().minWordFrequency(1).iterations(5).seed(119).epochs(1).layerSize(150).learningRate(0.025).labelsSource(source).windowSize(5).sequenceLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.sequence.DM<VocabWord>()).iterate(iter).trainWordVectors(true).usePreciseWeightInit(true).batchSize(8192).tokenizerFactory(t).workers(4).sampling(0).build();
        vec.fit();
        VocabCache<VocabWord> cache = vec.getVocab();
        File fullFile = File.createTempFile("paravec", "tests");
        fullFile.deleteOnExit();
        INDArray originalSyn1_17 = getSyn1().getRow(17).dup();
        WordVectorSerializer.writeParagraphVectors(vec, fullFile);
        int cnt1 = cache.wordFrequency("day");
        int cnt2 = cache.wordFrequency("me");
        Assert.assertNotEquals(1, cnt1);
        Assert.assertNotEquals(1, cnt2);
        Assert.assertNotEquals(cnt1, cnt2);
        Assert.assertEquals(97406, cache.numWords());
        Assert.assertTrue(vec.hasWord("DOC_16392"));
        Assert.assertTrue(vec.hasWord("DOC_3720"));
        List<String> result = new java.util.ArrayList(vec.nearestLabels(vec.getWordVectorMatrix("DOC_16392"), 10));
        System.out.println(("nearest labels: " + result));
        for (String label : result) {
            System.out.println(((label + "/DOC_16392: ") + (vec.similarity(label, "DOC_16392"))));
        }
        Assert.assertTrue(result.contains("DOC_16392"));
        // assertTrue(result.contains("DOC_21383"));
        /* We have few lines that contain pretty close words invloved.
        These sentences should be pretty close to each other in vector space
         */
        // line 3721: This is my way .
        // line 6348: This is my case .
        // line 9836: This is my house .
        // line 12493: This is my world .
        // line 16393: This is my work .
        // this is special sentence, that has nothing common with previous sentences
        // line 9853: We now have one .
        double similarityD = vec.similarity("day", "night");
        log.info(("day/night similarity: " + similarityD));
        if (similarityD < 0.0) {
            log.info(("Day: " + (Arrays.toString(vec.getWordVectorMatrix("day").dup().data().asDouble()))));
            log.info(("Night: " + (Arrays.toString(vec.getWordVectorMatrix("night").dup().data().asDouble()))));
        }
        List<String> labelsOriginal = vec.labelsSource.getLabels();
        double similarityW = vec.similarity("way", "work");
        log.info(("way/work similarity: " + similarityW));
        double similarityH = vec.similarity("house", "world");
        log.info(("house/world similarity: " + similarityH));
        double similarityC = vec.similarity("case", "way");
        log.info(("case/way similarity: " + similarityC));
        double similarity1 = vec.similarity("DOC_9835", "DOC_12492");
        log.info(("9835/12492 similarity: " + similarity1));
        // assertTrue(similarity1 > 0.7d);
        double similarity2 = vec.similarity("DOC_3720", "DOC_16392");
        log.info(("3720/16392 similarity: " + similarity2));
        // assertTrue(similarity2 > 0.7d);
        double similarity3 = vec.similarity("DOC_6347", "DOC_3720");
        log.info(("6347/3720 similarity: " + similarity3));
        // assertTrue(similarity2 > 0.7d);
        // likelihood in this case should be significantly lower
        double similarityX = vec.similarity("DOC_3720", "DOC_9852");
        log.info(("3720/9852 similarity: " + similarityX));
        Assert.assertTrue((similarityX < 0.5));
        File tempFile = File.createTempFile("paravec", "ser");
        tempFile.deleteOnExit();
        INDArray day = vec.getWordVectorMatrix("day").dup();
        /* Testing txt serialization */
        File tempFile2 = File.createTempFile("paravec", "ser");
        tempFile2.deleteOnExit();
        WordVectorSerializer.writeWordVectors(vec, tempFile2);
        ParagraphVectors vec3 = WordVectorSerializer.readParagraphVectorsFromText(tempFile2);
        INDArray day3 = vec3.getWordVectorMatrix("day").dup();
        List<String> labelsRestored = vec3.labelsSource.getLabels();
        Assert.assertEquals(day, day3);
        Assert.assertEquals(labelsOriginal.size(), labelsRestored.size());
        /* Testing binary serialization */
        SerializationUtils.saveObject(vec, tempFile);
        ParagraphVectors vec2 = ((ParagraphVectors) (SerializationUtils.readObject(tempFile)));
        INDArray day2 = vec2.getWordVectorMatrix("day").dup();
        List<String> labelsBinary = vec2.labelsSource.getLabels();
        Assert.assertEquals(day, day2);
        tempFile.delete();
        Assert.assertEquals(labelsOriginal.size(), labelsBinary.size());
        INDArray original = vec.getWordVectorMatrix("DOC_16392").dup();
        INDArray originalPreserved = original.dup();
        INDArray inferredA1 = vec.inferVector("This is my work .");
        INDArray inferredB1 = vec.inferVector("This is my work .");
        double cosAO1 = Transforms.cosineSim(inferredA1.dup(), original.dup());
        double cosAB1 = Transforms.cosineSim(inferredA1.dup(), inferredB1.dup());
        log.info("Cos O/A: {}", cosAO1);
        log.info("Cos A/B: {}", cosAB1);
        log.info("Inferred: {}", inferredA1);
        // assertTrue(cosAO1 > 0.45);
        Assert.assertTrue((cosAB1 > 0.95));
        // assertArrayEquals(inferredA.data().asDouble(), inferredB.data().asDouble(), 0.01);
        ParagraphVectors restoredVectors = WordVectorSerializer.readParagraphVectors(fullFile);
        restoredVectors.setTokenizerFactory(t);
        INDArray restoredSyn1_17 = getSyn1().getRow(17).dup();
        Assert.assertEquals(originalSyn1_17, restoredSyn1_17);
        INDArray originalRestored = vec.getWordVectorMatrix("DOC_16392").dup();
        Assert.assertEquals(originalPreserved, originalRestored);
        INDArray inferredA2 = restoredVectors.inferVector("This is my work .");
        INDArray inferredB2 = restoredVectors.inferVector("This is my work .");
        INDArray inferredC2 = restoredVectors.inferVector("world way case .");
        double cosAO2 = Transforms.cosineSim(inferredA2.dup(), original.dup());
        double cosAB2 = Transforms.cosineSim(inferredA2.dup(), inferredB2.dup());
        double cosAAX = Transforms.cosineSim(inferredA1.dup(), inferredA2.dup());
        double cosAC2 = Transforms.cosineSim(inferredC2.dup(), inferredA2.dup());
        log.info("Cos A2/B2: {}", cosAB2);
        log.info("Cos A1/A2: {}", cosAAX);
        log.info("Cos O/A2: {}", cosAO2);
        log.info("Cos C2/A2: {}", cosAC2);
        log.info("Vector: {}", Arrays.toString(inferredA1.data().asFloat()));
        log.info("cosAO2: {}", cosAO2);
        // assertTrue(cosAO2 > 0.45);
        Assert.assertTrue((cosAB2 > 0.95));
        Assert.assertTrue((cosAAX > 0.95));
    }

    @Test
    public void testParagraphVectorsDM() throws Exception {
        ClassPathResource resource = new ClassPathResource("/big/raw_sentences.txt");
        File file = resource.getFile();
        SentenceIterator iter = new BasicLineIterator(file);
        AbstractCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        LabelsSource source = new LabelsSource("DOC_");
        ParagraphVectors vec = new ParagraphVectors.Builder().minWordFrequency(1).iterations(2).seed(119).epochs(3).layerSize(100).learningRate(0.025).labelsSource(source).windowSize(5).iterate(iter).trainWordVectors(true).vocabCache(cache).tokenizerFactory(t).negativeSample(0).useHierarchicSoftmax(true).sampling(0).workers(1).usePreciseWeightInit(true).sequenceLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.sequence.DM<VocabWord>()).build();
        vec.fit();
        int cnt1 = cache.wordFrequency("day");
        int cnt2 = cache.wordFrequency("me");
        Assert.assertNotEquals(1, cnt1);
        Assert.assertNotEquals(1, cnt2);
        Assert.assertNotEquals(cnt1, cnt2);
        double simDN = vec.similarity("day", "night");
        log.info("day/night similariry: {}", simDN);
        double similarity1 = vec.similarity("DOC_9835", "DOC_12492");
        log.info(("9835/12492 similarity: " + similarity1));
        // assertTrue(similarity1 > 0.2d);
        double similarity2 = vec.similarity("DOC_3720", "DOC_16392");
        log.info(("3720/16392 similarity: " + similarity2));
        // assertTrue(similarity2 > 0.2d);
        double similarity3 = vec.similarity("DOC_6347", "DOC_3720");
        log.info(("6347/3720 similarity: " + similarity3));
        // assertTrue(similarity3 > 0.6d);
        double similarityX = vec.similarity("DOC_3720", "DOC_9852");
        log.info(("3720/9852 similarity: " + similarityX));
        Assert.assertTrue((similarityX < 0.5));
        // testing DM inference now
        INDArray original = vec.getWordVectorMatrix("DOC_16392").dup();
        INDArray inferredA1 = vec.inferVector("This is my work");
        INDArray inferredB1 = vec.inferVector("This is my work .");
        double cosAO1 = Transforms.cosineSim(inferredA1.dup(), original.dup());
        double cosAB1 = Transforms.cosineSim(inferredA1.dup(), inferredB1.dup());
        log.info("Cos O/A: {}", cosAO1);
        log.info("Cos A/B: {}", cosAB1);
    }

    @Test
    public void testParagraphVectorsDBOW() throws Exception {
        ClassPathResource resource = new ClassPathResource("/big/raw_sentences.txt");
        File file = resource.getFile();
        SentenceIterator iter = new BasicLineIterator(file);
        AbstractCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        LabelsSource source = new LabelsSource("DOC_");
        ParagraphVectors vec = new ParagraphVectors.Builder().minWordFrequency(1).iterations(5).seed(119).epochs(1).layerSize(100).learningRate(0.025).labelsSource(source).windowSize(5).iterate(iter).trainWordVectors(true).vocabCache(cache).tokenizerFactory(t).negativeSample(0).allowParallelTokenization(true).useHierarchicSoftmax(true).sampling(0).workers(4).usePreciseWeightInit(true).sequenceLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.sequence.DBOW<VocabWord>()).build();
        vec.fit();
        Assert.assertFalse(((InMemoryLookupTable<VocabWord>) (vec.getLookupTable())).getSyn0().isAttached());
        Assert.assertFalse(((InMemoryLookupTable<VocabWord>) (vec.getLookupTable())).getSyn1().isAttached());
        int cnt1 = cache.wordFrequency("day");
        int cnt2 = cache.wordFrequency("me");
        Assert.assertNotEquals(1, cnt1);
        Assert.assertNotEquals(1, cnt2);
        Assert.assertNotEquals(cnt1, cnt2);
        double simDN = vec.similarity("day", "night");
        log.info("day/night similariry: {}", simDN);
        double similarity1 = vec.similarity("DOC_9835", "DOC_12492");
        log.info(("9835/12492 similarity: " + similarity1));
        // assertTrue(similarity1 > 0.2d);
        double similarity2 = vec.similarity("DOC_3720", "DOC_16392");
        log.info(("3720/16392 similarity: " + similarity2));
        // assertTrue(similarity2 > 0.2d);
        double similarity3 = vec.similarity("DOC_6347", "DOC_3720");
        log.info(("6347/3720 similarity: " + similarity3));
        // assertTrue(similarity3 > 0.6d);
        double similarityX = vec.similarity("DOC_3720", "DOC_9852");
        log.info(("3720/9852 similarity: " + similarityX));
        Assert.assertTrue((similarityX < 0.5));
        // testing DM inference now
        INDArray original = vec.getWordVectorMatrix("DOC_16392").dup();
        INDArray inferredA1 = vec.inferVector("This is my work");
        INDArray inferredB1 = vec.inferVector("This is my work .");
        INDArray inferredC1 = vec.inferVector("This is my day");
        INDArray inferredD1 = vec.inferVector("This is my night");
        log.info("A: {}", Arrays.toString(inferredA1.data().asFloat()));
        log.info("C: {}", Arrays.toString(inferredC1.data().asFloat()));
        Assert.assertNotEquals(inferredA1, inferredC1);
        double cosAO1 = Transforms.cosineSim(inferredA1.dup(), original.dup());
        double cosAB1 = Transforms.cosineSim(inferredA1.dup(), inferredB1.dup());
        double cosAC1 = Transforms.cosineSim(inferredA1.dup(), inferredC1.dup());
        double cosCD1 = Transforms.cosineSim(inferredD1.dup(), inferredC1.dup());
        log.info("Cos O/A: {}", cosAO1);
        log.info("Cos A/B: {}", cosAB1);
        log.info("Cos A/C: {}", cosAC1);
        log.info("Cos C/D: {}", cosCD1);
    }

    @Test
    public void testParagraphVectorsWithWordVectorsModelling1() throws Exception {
        ClassPathResource resource = new ClassPathResource("/big/raw_sentences.txt");
        File file = resource.getFile();
        SentenceIterator iter = new BasicLineIterator(file);
        // InMemoryLookupCache cache = new InMemoryLookupCache(false);
        AbstractCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        LabelsSource source = new LabelsSource("DOC_");
        ParagraphVectors vec = new ParagraphVectors.Builder().minWordFrequency(1).iterations(3).epochs(1).layerSize(100).learningRate(0.025).labelsSource(source).windowSize(5).iterate(iter).trainWordVectors(true).vocabCache(cache).tokenizerFactory(t).sampling(0).build();
        vec.fit();
        int cnt1 = cache.wordFrequency("day");
        int cnt2 = cache.wordFrequency("me");
        Assert.assertNotEquals(1, cnt1);
        Assert.assertNotEquals(1, cnt2);
        Assert.assertNotEquals(cnt1, cnt2);
        /* We have few lines that contain pretty close words invloved.
        These sentences should be pretty close to each other in vector space
         */
        // line 3721: This is my way .
        // line 6348: This is my case .
        // line 9836: This is my house .
        // line 12493: This is my world .
        // line 16393: This is my work .
        // this is special sentence, that has nothing common with previous sentences
        // line 9853: We now have one .
        Assert.assertTrue(vec.hasWord("DOC_3720"));
        double similarityD = vec.similarity("day", "night");
        log.info(("day/night similarity: " + similarityD));
        double similarityW = vec.similarity("way", "work");
        log.info(("way/work similarity: " + similarityW));
        double similarityH = vec.similarity("house", "world");
        log.info(("house/world similarity: " + similarityH));
        double similarityC = vec.similarity("case", "way");
        log.info(("case/way similarity: " + similarityC));
        double similarity1 = vec.similarity("DOC_9835", "DOC_12492");
        log.info(("9835/12492 similarity: " + similarity1));
        // assertTrue(similarity1 > 0.7d);
        double similarity2 = vec.similarity("DOC_3720", "DOC_16392");
        log.info(("3720/16392 similarity: " + similarity2));
        // assertTrue(similarity2 > 0.7d);
        double similarity3 = vec.similarity("DOC_6347", "DOC_3720");
        log.info(("6347/3720 similarity: " + similarity3));
        // assertTrue(similarity2 > 0.7d);
        // likelihood in this case should be significantly lower
        // however, since corpus is small, and weight initialization is random-based, sometimes this test CAN fail
        double similarityX = vec.similarity("DOC_3720", "DOC_9852");
        log.info(("3720/9852 similarity: " + similarityX));
        Assert.assertTrue((similarityX < 0.5));
        double sim119 = vec.similarityToLabel("This is my case .", "DOC_6347");
        double sim120 = vec.similarityToLabel("This is my case .", "DOC_3720");
        log.info(((("1/2: " + sim119) + "/") + sim120));
        // assertEquals(similarity3, sim119, 0.001);
    }

    @Test
    public void testParallelIterator() throws IOException {
        TokenizerFactory factory = new DefaultTokenizerFactory();
        SentenceIterator iterator = new BasicLineIterator(new ClassPathResource("/big/raw_sentences.txt").getFile());
        SentenceTransformer transformer = new SentenceTransformer.Builder().iterator(iterator).allowMultithreading(true).tokenizerFactory(factory).build();
        ParallelTransformerIterator iter = ((ParallelTransformerIterator) (transformer.iterator()));
        for (int i = 0; i < 100; ++i) {
            int cnt = 0;
            long counter = 0;
            Sequence<VocabWord> sequence = null;
            while (iter.hasNext()) {
                sequence = iter.next();
                counter += sequence.size();
                cnt++;
            } 
            iter.reset();
            Assert.assertEquals(757172, counter);
        }
    }

    @Test
    public void testIterator() throws IOException {
        val folder_labeled = testDir.newFolder();
        val folder_unlabeled = testDir.newFolder();
        new ClassPathResource("/paravec/labeled").copyDirectory(folder_labeled);
        new ClassPathResource("/paravec/unlabeled").copyDirectory(folder_unlabeled);
        FileLabelAwareIterator labelAwareIterator = new FileLabelAwareIterator.Builder().addSourceFolder(folder_labeled).build();
        ClassPathResource resource_sentences = new ClassPathResource("/big/raw_sentences.txt");
        SentenceIterator iter = new BasicLineIterator(resource_sentences.getFile());
        int i = 0;
        for (; i < 10000; ++i) {
            int j = 0;
            int labels = 0;
            int words = 0;
            while (labelAwareIterator.hasNextDocument()) {
                ++j;
                LabelledDocument document = labelAwareIterator.nextDocument();
                labels += document.getLabels().size();
                List<VocabWord> lst = document.getReferencedContent();
                if (!(CollectionUtils.isEmpty(lst)))
                    words += lst.size();

            } 
            labelAwareIterator.reset();
            // System.out.println(words + " " + labels + " " + j);
            Assert.assertEquals(0, words);
            Assert.assertEquals(30, labels);
            Assert.assertEquals(30, j);
            j = 0;
            while (iter.hasNext()) {
                ++j;
                iter.nextSentence();
            } 
            Assert.assertEquals(97162, j);
            iter.reset();
        }
    }

    /* In this test we'll build w2v model, and will use it's vocab and weights for ParagraphVectors.
    there's no need in this test within travis, use it manually only for problems detection
     */
    @Test
    public void testParagraphVectorsOverExistingWordVectorsModel() throws Exception {
        // we build w2v from multiple sources, to cover everything
        ClassPathResource resource_sentences = new ClassPathResource("/big/raw_sentences.txt");
        val folder_mixed = testDir.newFolder();
        ClassPathResource resource_mixed = new ClassPathResource("/paravec");
        resource_mixed.copyDirectory(folder_mixed);
        SentenceIterator iter = new AggregatingSentenceIterator.Builder().addSentenceIterator(new BasicLineIterator(resource_sentences.getFile())).addSentenceIterator(new org.deeplearning4j.text.sentenceiterator.FileSentenceIterator(folder_mixed)).build();
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec wordVectors = new Word2Vec.Builder().seed(119).minWordFrequency(1).batchSize(250).iterations(1).epochs(3).learningRate(0.025).layerSize(150).minLearningRate(0.001).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram<VocabWord>()).useHierarchicSoftmax(true).windowSize(5).allowParallelTokenization(true).workers(1).iterate(iter).tokenizerFactory(t).build();
        wordVectors.fit();
        VocabWord day_A = wordVectors.getVocab().tokenFor("day");
        INDArray vector_day1 = wordVectors.getWordVectorMatrix("day").dup();
        // At this moment we have ready w2v model. It's time to use it for ParagraphVectors
        val folder_labeled = testDir.newFolder();
        val folder_unlabeled = testDir.newFolder();
        new ClassPathResource("/paravec/labeled").copyDirectory(folder_labeled);
        new ClassPathResource("/paravec/unlabeled").copyDirectory(folder_unlabeled);
        FileLabelAwareIterator labelAwareIterator = new FileLabelAwareIterator.Builder().addSourceFolder(folder_labeled).build();
        // documents from this iterator will be used for classification
        FileLabelAwareIterator unlabeledIterator = new FileLabelAwareIterator.Builder().addSourceFolder(folder_unlabeled).build();
        // we're building classifier now, with pre-built w2v model passed in
        ParagraphVectors paragraphVectors = new ParagraphVectors.Builder().seed(119).iterate(labelAwareIterator).learningRate(0.025).minLearningRate(0.001).iterations(10).epochs(1).layerSize(150).tokenizerFactory(t).sequenceLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.sequence.DBOW<VocabWord>()).useHierarchicSoftmax(true).allowParallelTokenization(true).workers(1).trainWordVectors(false).useExistingWordVectors(wordVectors).build();
        paragraphVectors.fit();
        VocabWord day_B = paragraphVectors.getVocab().tokenFor("day");
        Assert.assertEquals(day_A.getIndex(), day_B.getIndex());
        /* double similarityD = wordVectors.similarity("day", "night");
        log.info("day/night similarity: " + similarityD);
        assertTrue(similarityD > 0.5d);
         */
        INDArray vector_day2 = paragraphVectors.getWordVectorMatrix("day").dup();
        double crossDay = arraysSimilarity(vector_day1, vector_day2);
        log.info(("Day1: " + vector_day1));
        log.info(("Day2: " + vector_day2));
        log.info(("Cross-Day similarity: " + crossDay));
        log.info(("Cross-Day similiarity 2: " + (Transforms.cosineSim(Transforms.unitVec(vector_day1), Transforms.unitVec(vector_day2)))));
        Assert.assertTrue((crossDay > 0.9));
        /**
         * Here we're checking cross-vocabulary equality
         */
        /* Random rnd = new Random();
        VocabCache<VocabWord> cacheP = paragraphVectors.getVocab();
        VocabCache<VocabWord> cacheW = wordVectors.getVocab();
        for (int x = 0; x < 1000; x++) {
        int idx = rnd.nextInt(cacheW.numWords());

        String wordW = cacheW.wordAtIndex(idx);
        String wordP = cacheP.wordAtIndex(idx);

        assertEquals(wordW, wordP);

        INDArray arrayW = wordVectors.getWordVectorMatrix(wordW);
        INDArray arrayP = paragraphVectors.getWordVectorMatrix(wordP);

        double simWP = Transforms.cosineSim(arrayW, arrayP);
        assertTrue(simWP >= 0.9);
        }
         */
        log.info(("Zfinance: " + (paragraphVectors.getWordVectorMatrix("Zfinance"))));
        log.info(("Zhealth: " + (paragraphVectors.getWordVectorMatrix("Zhealth"))));
        log.info(("Zscience: " + (paragraphVectors.getWordVectorMatrix("Zscience"))));
        Assert.assertTrue(unlabeledIterator.hasNext());
        LabelledDocument document = unlabeledIterator.nextDocument();
        log.info((("Results for document '" + (document.getLabel())) + "'"));
        List<String> results = new java.util.ArrayList(paragraphVectors.predictSeveral(document, 3));
        for (String result : results) {
            double sim = paragraphVectors.similarityToLabel(document, result);
            log.info((((("Similarity to [" + result) + "] is [") + sim) + "]"));
        }
        String topPrediction = paragraphVectors.predict(document);
        Assert.assertEquals(("Z" + (document.getLabel())), topPrediction);
    }

    @Test
    public void testDirectInference() throws Exception {
        ClassPathResource resource_sentences = new ClassPathResource("/big/raw_sentences.txt");
        ClassPathResource resource_mixed = new ClassPathResource("/paravec");
        SentenceIterator iter = new AggregatingSentenceIterator.Builder().addSentenceIterator(new BasicLineIterator(resource_sentences.getFile())).addSentenceIterator(new org.deeplearning4j.text.sentenceiterator.FileSentenceIterator(resource_mixed.getFile())).build();
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec wordVectors = new Word2Vec.Builder().minWordFrequency(1).batchSize(250).iterations(1).epochs(3).learningRate(0.025).layerSize(150).minLearningRate(0.001).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram<VocabWord>()).useHierarchicSoftmax(true).windowSize(5).iterate(iter).tokenizerFactory(t).build();
        wordVectors.fit();
        ParagraphVectors pv = new ParagraphVectors.Builder().tokenizerFactory(t).iterations(10).useHierarchicSoftmax(true).trainWordVectors(true).useExistingWordVectors(wordVectors).negativeSample(0).sequenceLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.sequence.DM<VocabWord>()).build();
        INDArray vec1 = pv.inferVector("This text is pretty awesome");
        INDArray vec2 = pv.inferVector("Fantastic process of crazy things happening inside just for history purposes");
        log.info("vec1/vec2: {}", Transforms.cosineSim(vec1, vec2));
    }

    @Test
    public void testHash() {
        VocabWord w1 = new VocabWord(1.0, "D1");
        VocabWord w2 = new VocabWord(1.0, "Bo");
        log.info("W1 > Short hash: {}; Long hash: {}", w1.getLabel().hashCode(), w1.getStorageId());
        log.info("W2 > Short hash: {}; Long hash: {}", w2.getLabel().hashCode(), w2.getStorageId());
        Assert.assertNotEquals(w1.getStorageId(), w2.getStorageId());
    }

    @Test
    public void testJSONSerialization() {
        ParagraphVectors paragraphVectors = new ParagraphVectors.Builder().build();
        AbstractCache<VocabWord> cache = new AbstractCache.Builder<VocabWord>().build();
        val words = new VocabWord[3];
        words[0] = new VocabWord(1.0, "word");
        words[1] = new VocabWord(2.0, "test");
        words[2] = new VocabWord(3.0, "tester");
        for (int i = 0; i < (words.length); ++i) {
            cache.addToken(words[i]);
            cache.addWordToIndex(i, words[i].getLabel());
        }
        paragraphVectors.setVocab(cache);
        String json = null;
        Word2Vec unserialized = null;
        try {
            json = paragraphVectors.toJson();
            log.info("{}", json.toString());
            unserialized = ParagraphVectors.fromJson(json);
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
    public void testDoubleFit() throws Exception {
        ClassPathResource resource = new ClassPathResource("/big/raw_sentences.txt");
        File file = resource.getFile();
        SentenceIterator iter = new BasicLineIterator(file);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        LabelsSource source = new LabelsSource("DOC_");
        val builder = new ParagraphVectors.Builder();
        ParagraphVectors vec = builder.minWordFrequency(1).iterations(5).seed(119).epochs(1).layerSize(150).learningRate(0.025).labelsSource(source).windowSize(5).sequenceLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.sequence.DM<VocabWord>()).iterate(iter).trainWordVectors(true).usePreciseWeightInit(true).batchSize(8192).allowParallelTokenization(false).tokenizerFactory(t).workers(1).sampling(0).build();
        vec.fit();
        long num1 = vec.vocab().totalNumberOfDocs();
        vec.fit();
        System.out.println(vec.vocab().totalNumberOfDocs());
        long num2 = vec.vocab().totalNumberOfDocs();
        Assert.assertEquals(num1, num2);
    }
}

