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
package org.deeplearning4j.models.word2vec.iterator;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.io.ClassPathResource;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class Word2VecDataSetIteratorTest {
    /**
     * Basically all we want from this test - being able to finish without exceptions.
     */
    @Test
    public void testIterator1() throws Exception {
        File inputFile = new ClassPathResource("/big/raw_sentences.txt").getFile();
        SentenceIterator iter = new BasicLineIterator(inputFile.getAbsolutePath());
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new CommonPreprocessor());
        Word2Vec vec = // we make sure we'll have some missing words
        new Word2Vec.Builder().minWordFrequency(10).iterations(1).learningRate(0.025).layerSize(150).seed(42).sampling(0).negativeSample(0).useHierarchicSoftmax(true).windowSize(5).modelUtils(new org.deeplearning4j.models.embeddings.reader.impl.BasicModelUtils<org.deeplearning4j.models.word2vec.VocabWord>()).useAdaGrad(false).iterate(iter).workers(8).tokenizerFactory(t).elementsLearningAlgorithm(new org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW<org.deeplearning4j.models.word2vec.VocabWord>()).build();
        vec.fit();
        List<String> labels = new ArrayList<>();
        labels.add("positive");
        labels.add("negative");
        Word2VecDataSetIterator iterator = new Word2VecDataSetIterator(vec, getLASI(iter, labels), labels, 1);
        INDArray array = iterator.next().getFeatures();
        while (iterator.hasNext()) {
            DataSet ds = iterator.next();
            Assert.assertArrayEquals(array.shape(), ds.getFeatures().shape());
        } 
    }
}

