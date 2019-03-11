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
package org.deeplearning4j.models.sequencevectors;


import java.io.File;
import java.util.Collection;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.VectorsConfiguration;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.sequencevectors.interfaces.SequenceElementFactory;
import org.deeplearning4j.models.sequencevectors.iterators.AbstractSequenceIterator;
import org.deeplearning4j.models.sequencevectors.sequence.SequenceElement;
import org.deeplearning4j.models.sequencevectors.transformers.impl.SentenceTransformer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabConstructor;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
public class SequenceVectorsTest {
    protected static final Logger logger = LoggerFactory.getLogger(SequenceVectorsTest.class);

    @Test
    public void testAbstractW2VModel() throws Exception {
        ClassPathResource resource = new ClassPathResource("big/raw_sentences.txt");
        File file = resource.getFile();
        SequenceVectorsTest.logger.info("dtype: {}", Nd4j.dataType());
        AbstractCache<VocabWord> vocabCache = new AbstractCache.Builder<VocabWord>().build();
        /* First we build line iterator */
        BasicLineIterator underlyingIterator = new BasicLineIterator(file);
        /* Now we need the way to convert lines into Sequences of VocabWords.
        In this example that's SentenceTransformer
         */
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        SentenceTransformer transformer = new SentenceTransformer.Builder().iterator(underlyingIterator).tokenizerFactory(t).build();
        /* And we pack that transformer into AbstractSequenceIterator */
        AbstractSequenceIterator<VocabWord> sequenceIterator = new AbstractSequenceIterator.Builder<>(transformer).build();
        /* Now we should build vocabulary out of sequence iterator.
        We can skip this phase, and just set SequenceVectors.resetModel(TRUE), and vocabulary will be mastered internally
         */
        VocabConstructor<VocabWord> constructor = new VocabConstructor.Builder<VocabWord>().addSource(sequenceIterator, 5).setTargetVocabCache(vocabCache).build();
        constructor.buildJointVocabulary(false, true);
        Assert.assertEquals(242, vocabCache.numWords());
        Assert.assertEquals(634303, vocabCache.totalWordOccurrences());
        VocabWord wordz = vocabCache.wordFor("day");
        SequenceVectorsTest.logger.info(("Wordz: " + wordz));
        /* Time to build WeightLookupTable instance for our new model */
        WeightLookupTable<VocabWord> lookupTable = new InMemoryLookupTable.Builder<VocabWord>().lr(0.025).vectorLength(150).useAdaGrad(false).cache(vocabCache).build();
        /* reset model is viable only if you're setting SequenceVectors.resetModel() to false
        if set to True - it will be called internally
         */
        lookupTable.resetWeights(true);
        /* Now we can build SequenceVectors model, that suits our needs */
        SequenceVectors<VocabWord> vectors = /* These two methods define our training goals. At least one goal should be set to TRUE. */
        // if set to true, vocabulary will be built from scratches internally
        // otherwise externally provided vocab will be used
        // number of iterations over whole training corpus
        // number of iterations over batch
        // we might want to set layer size here. otherwise it'll be derived from lookupTable
        // .layerSize(150)
        // batchSize is the number of sequences being processed by 1 thread at once
        // this value actually matters if you have iterations > 1
        // vocabulary built prior to modelling
        // abstract iterator that covers training corpus
        // WeightLookupTable
        // minimum number of occurencies for each element in training corpus. All elements below this value will be ignored
        // Please note: this value has effect only if resetModel() set to TRUE, for internal model building. Otherwise it'll be ignored, and actual vocabulary content will be used
        new SequenceVectors.Builder<VocabWord>(new VectorsConfiguration()).minWordFrequency(5).lookupTable(lookupTable).iterate(sequenceIterator).vocabCache(vocabCache).batchSize(250).iterations(1).epochs(1).resetModel(false).trainElementsRepresentation(true).trainSequencesRepresentation(false).build();
        /* Now, after all options are set, we just call fit() */
        SequenceVectorsTest.logger.info("Starting training...");
        vectors.fit();
        SequenceVectorsTest.logger.info("Model saved...");
        /* As soon as fit() exits, model considered built, and we can test it.
        Please note: all similarity context is handled via SequenceElement's labels, so if you're using SequenceVectors to build models for complex
        objects/relations please take care of Labels uniqueness and meaning for yourself.
         */
        double sim = vectors.similarity("day", "night");
        SequenceVectorsTest.logger.info(("Day/night similarity: " + sim));
        Assert.assertTrue((sim > 0.6));
        Collection<String> labels = vectors.wordsNearest("day", 10);
        SequenceVectorsTest.logger.info(("Nearest labels to 'day': " + labels));
        SequenceElementFactory<VocabWord> factory = new org.deeplearning4j.models.sequencevectors.serialization.AbstractElementFactory<VocabWord>(VocabWord.class);
        WordVectorSerializer.writeSequenceVectors(vectors, factory, "seqvec.mod");
        SequenceVectors<VocabWord> model = WordVectorSerializer.readSequenceVectors(factory, new File("seqvec.mod"));
        sim = model.similarity("day", "night");
        SequenceVectorsTest.logger.info(("day/night similarity: " + sim));
    }

    @Test
    public void testInternalVocabConstruction() throws Exception {
        ClassPathResource resource = new ClassPathResource("big/raw_sentences.txt");
        File file = resource.getFile();
        BasicLineIterator underlyingIterator = new BasicLineIterator(file);
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        SentenceTransformer transformer = new SentenceTransformer.Builder().iterator(underlyingIterator).tokenizerFactory(t).build();
        AbstractSequenceIterator<VocabWord> sequenceIterator = new AbstractSequenceIterator.Builder<>(transformer).build();
        SequenceVectors<VocabWord> vectors = new SequenceVectors.Builder<VocabWord>(new VectorsConfiguration()).minWordFrequency(5).iterate(sequenceIterator).batchSize(250).iterations(1).epochs(1).resetModel(false).trainElementsRepresentation(true).build();
        SequenceVectorsTest.logger.info("Fitting model...");
        vectors.fit();
        SequenceVectorsTest.logger.info("Model ready...");
        double sim = vectors.similarity("day", "night");
        SequenceVectorsTest.logger.info(("Day/night similarity: " + sim));
        Assert.assertTrue((sim > 0.6));
        Collection<String> labels = vectors.wordsNearest("day", 10);
        SequenceVectorsTest.logger.info(("Nearest labels to 'day': " + labels));
    }

    @Test
    public void testElementsLearningAlgo1() throws Exception {
        SequenceVectors<VocabWord> vectors = new SequenceVectors.Builder<VocabWord>(new VectorsConfiguration()).minWordFrequency(5).batchSize(250).iterations(1).elementsLearningAlgorithm("org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram").epochs(1).resetModel(false).trainElementsRepresentation(true).build();
    }

    @Test
    public void testSequenceLearningAlgo1() throws Exception {
        SequenceVectors<VocabWord> vectors = new SequenceVectors.Builder<VocabWord>(new VectorsConfiguration()).minWordFrequency(5).batchSize(250).iterations(1).sequenceLearningAlgorithm("org.deeplearning4j.models.embeddings.learning.impl.sequence.DBOW").epochs(1).resetModel(false).trainElementsRepresentation(false).build();
    }

    @Data
    private static class Blogger extends SequenceElement {
        @Getter
        @Setter
        private int id;

        public Blogger() {
            super();
        }

        public Blogger(int id) {
            super();
            this.id = id;
        }

        /**
         * This method should return string representation of this SequenceElement, so it can be used for
         *
         * @return 
         */
        @Override
        public String getLabel() {
            return String.valueOf(id);
        }

        /**
         *
         *
         * @return 
         */
        @Override
        public String toJSON() {
            return null;
        }

        @Override
        public String toString() {
            return ((((((((((((("VocabWord{" + "wordFrequency=") + (this.elementFrequency)) + ", index=") + (index)) + ", codes=") + (codes)) + ", word='") + (String.valueOf(id))) + '\'') + ", points=") + (points)) + ", codeLength=") + (codeLength)) + '}';
        }
    }
}

