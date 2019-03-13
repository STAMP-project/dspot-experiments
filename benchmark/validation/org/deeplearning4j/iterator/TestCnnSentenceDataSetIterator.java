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
package org.deeplearning4j.iterator;


import CnnSentenceDataSetIterator.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.deeplearning4j.iterator.provider.CollectionLabeledSentenceProvider;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.io.ClassPathResource;


/**
 * Created by Alex on 28/01/2017.
 */
public class TestCnnSentenceDataSetIterator {
    @Test
    public void testSentenceIterator() throws Exception {
        WordVectors w2v = WordVectorSerializer.readWord2VecModel(new ClassPathResource("word2vec/googleload/sample_vec.bin").getFile());
        int vectorSize = w2v.lookupTable().layerSize();
        // Collection<String> words = w2v.lookupTable().getVocabCache().words();
        // for(String s : words){
        // System.out.println(s);
        // }
        List<String> sentences = new ArrayList<>();
        // First word: all present
        sentences.add("these balance Database model");
        sentences.add("into same THISWORDDOESNTEXIST are");
        int maxLength = 4;
        List<String> s1 = Arrays.asList("these", "balance", "Database", "model");
        List<String> s2 = Arrays.asList("into", "same", "are");
        List<String> labelsForSentences = Arrays.asList("Positive", "Negative");
        INDArray expLabels = Nd4j.create(new double[][]{ new double[]{ 0, 1 }, new double[]{ 1, 0 } });// Order of labels: alphabetic. Positive -> [0,1]

        boolean[] alongHeightVals = new boolean[]{ true, false };
        for (boolean norm : new boolean[]{ true, false }) {
            for (boolean alongHeight : alongHeightVals) {
                INDArray expectedFeatures;
                if (alongHeight) {
                    expectedFeatures = Nd4j.create(2, 1, maxLength, vectorSize);
                } else {
                    expectedFeatures = Nd4j.create(2, 1, vectorSize, maxLength);
                }
                int[] fmShape;
                if (alongHeight) {
                    fmShape = new int[]{ 2, 1, 4, 1 };
                } else {
                    fmShape = new int[]{ 2, 1, 1, 4 };
                }
                INDArray expectedFeatureMask = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1 }, new double[]{ 1, 1, 1, 0 } }).reshape('c', fmShape);
                for (int i = 0; i < 4; i++) {
                    INDArray v = (norm) ? w2v.getWordVectorMatrixNormalized(s1.get(i)) : w2v.getWordVectorMatrix(s1.get(i));
                    if (alongHeight) {
                        expectedFeatures.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.point(i), NDArrayIndex.all()).assign(v);
                    } else {
                        expectedFeatures.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(i)).assign(v);
                    }
                }
                for (int i = 0; i < 3; i++) {
                    INDArray v = (norm) ? w2v.getWordVectorMatrixNormalized(s2.get(i)) : w2v.getWordVectorMatrix(s2.get(i));
                    if (alongHeight) {
                        expectedFeatures.get(NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.point(i), NDArrayIndex.all()).assign(v);
                    } else {
                        expectedFeatures.get(NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(i)).assign(v);
                    }
                }
                LabeledSentenceProvider p = new CollectionLabeledSentenceProvider(sentences, labelsForSentences, null);
                CnnSentenceDataSetIterator dsi = new CnnSentenceDataSetIterator.Builder(Format.CNN2D).sentenceProvider(p).useNormalizedWordVectors(norm).wordVectors(w2v).maxSentenceLength(256).minibatchSize(32).sentencesAlongHeight(alongHeight).build();
                // System.out.println("alongHeight = " + alongHeight);
                DataSet ds = dsi.next();
                Assert.assertArrayEquals(expectedFeatures.shape(), ds.getFeatures().shape());
                Assert.assertEquals(expectedFeatures, ds.getFeatures());
                Assert.assertEquals(expLabels, ds.getLabels());
                Assert.assertEquals(expectedFeatureMask, ds.getFeaturesMaskArray());
                Assert.assertNull(ds.getLabelsMaskArray());
                INDArray s1F = dsi.loadSingleSentence(sentences.get(0));
                INDArray s2F = dsi.loadSingleSentence(sentences.get(1));
                INDArray sub1 = ds.getFeatures().get(NDArrayIndex.interval(0, 0, true), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all());
                INDArray sub2;
                if (alongHeight) {
                    sub2 = ds.getFeatures().get(NDArrayIndex.interval(1, 1, true), NDArrayIndex.all(), NDArrayIndex.interval(0, 3), NDArrayIndex.all());
                } else {
                    sub2 = ds.getFeatures().get(NDArrayIndex.interval(1, 1, true), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 3));
                }
                Assert.assertArrayEquals(sub1.shape(), s1F.shape());
                Assert.assertArrayEquals(sub2.shape(), s2F.shape());
                Assert.assertEquals(sub1, s1F);
                Assert.assertEquals(sub2, s2F);
            }
        }
    }

    @Test
    public void testSentenceIteratorCNN1D_RNN() throws Exception {
        WordVectors w2v = WordVectorSerializer.readWord2VecModel(new ClassPathResource("word2vec/googleload/sample_vec.bin").getFile());
        int vectorSize = w2v.lookupTable().layerSize();
        List<String> sentences = new ArrayList<>();
        // First word: all present
        sentences.add("these balance Database model");
        sentences.add("into same THISWORDDOESNTEXIST are");
        int maxLength = 4;
        List<String> s1 = Arrays.asList("these", "balance", "Database", "model");
        List<String> s2 = Arrays.asList("into", "same", "are");
        List<String> labelsForSentences = Arrays.asList("Positive", "Negative");
        INDArray expLabels = Nd4j.create(new double[][]{ new double[]{ 0, 1 }, new double[]{ 1, 0 } });// Order of labels: alphabetic. Positive -> [0,1]

        for (boolean norm : new boolean[]{ true, false }) {
            for (CnnSentenceDataSetIterator.Format f : new CnnSentenceDataSetIterator.Format[]{ Format.CNN1D, Format.RNN }) {
                INDArray expectedFeatures = Nd4j.create(2, vectorSize, maxLength);
                int[] fmShape = new int[]{ 2, 4 };
                INDArray expectedFeatureMask = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1 }, new double[]{ 1, 1, 1, 0 } }).reshape('c', fmShape);
                for (int i = 0; i < 4; i++) {
                    INDArray v = (norm) ? w2v.getWordVectorMatrixNormalized(s1.get(i)) : w2v.getWordVectorMatrix(s1.get(i));
                    expectedFeatures.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(i)).assign(v);
                }
                for (int i = 0; i < 3; i++) {
                    INDArray v = (norm) ? w2v.getWordVectorMatrixNormalized(s2.get(i)) : w2v.getWordVectorMatrix(s2.get(i));
                    expectedFeatures.get(NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.point(i)).assign(v);
                }
                LabeledSentenceProvider p = new CollectionLabeledSentenceProvider(sentences, labelsForSentences, null);
                CnnSentenceDataSetIterator dsi = new CnnSentenceDataSetIterator.Builder(f).sentenceProvider(p).useNormalizedWordVectors(norm).wordVectors(w2v).maxSentenceLength(256).minibatchSize(32).build();
                DataSet ds = dsi.next();
                Assert.assertArrayEquals(expectedFeatures.shape(), ds.getFeatures().shape());
                Assert.assertEquals(expectedFeatures, ds.getFeatures());
                Assert.assertEquals(expLabels, ds.getLabels());
                Assert.assertEquals(expectedFeatureMask, ds.getFeaturesMaskArray());
                Assert.assertNull(ds.getLabelsMaskArray());
                INDArray s1F = dsi.loadSingleSentence(sentences.get(0));
                INDArray s2F = dsi.loadSingleSentence(sentences.get(1));
                INDArray sub1 = ds.getFeatures().get(NDArrayIndex.interval(0, 0, true), NDArrayIndex.all(), NDArrayIndex.all());
                INDArray sub2 = ds.getFeatures().get(NDArrayIndex.interval(1, 1, true), NDArrayIndex.all(), NDArrayIndex.interval(0, 3));
                Assert.assertArrayEquals(sub1.shape(), s1F.shape());
                Assert.assertArrayEquals(sub2.shape(), s2F.shape());
                Assert.assertEquals(sub1, s1F);
                Assert.assertEquals(sub2, s2F);
            }
        }
    }

    @Test
    public void testCnnSentenceDataSetIteratorNoTokensEdgeCase() throws Exception {
        WordVectors w2v = WordVectorSerializer.readWord2VecModel(new ClassPathResource("word2vec/googleload/sample_vec.bin").getFile());
        int vectorSize = w2v.lookupTable().layerSize();
        List<String> sentences = new ArrayList<>();
        // First 2 sentences - no valid words
        sentences.add("NOVALID WORDSHERE");
        sentences.add("!!!");
        sentences.add("these balance Database model");
        sentences.add("into same THISWORDDOESNTEXIST are");
        int maxLength = 4;
        List<String> s1 = Arrays.asList("these", "balance", "Database", "model");
        List<String> s2 = Arrays.asList("into", "same", "are");
        List<String> labelsForSentences = Arrays.asList("Positive", "Negative", "Positive", "Negative");
        INDArray expLabels = Nd4j.create(new double[][]{ new double[]{ 0, 1 }, new double[]{ 1, 0 } });// Order of labels: alphabetic. Positive -> [0,1]

        LabeledSentenceProvider p = new CollectionLabeledSentenceProvider(sentences, labelsForSentences, null);
        CnnSentenceDataSetIterator dsi = new CnnSentenceDataSetIterator.Builder(Format.CNN2D).sentenceProvider(p).wordVectors(w2v).useNormalizedWordVectors(true).maxSentenceLength(256).minibatchSize(32).sentencesAlongHeight(false).build();
        // System.out.println("alongHeight = " + alongHeight);
        DataSet ds = dsi.next();
        INDArray expectedFeatures = Nd4j.create(2, 1, vectorSize, maxLength);
        INDArray expectedFeatureMask = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1 }, new double[]{ 1, 1, 1, 0 } }).reshape('c', 2, 1, 1, 4);
        for (int i = 0; i < 4; i++) {
            expectedFeatures.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(i)).assign(w2v.getWordVectorMatrixNormalized(s1.get(i)));
        }
        for (int i = 0; i < 3; i++) {
            expectedFeatures.get(NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(i)).assign(w2v.getWordVectorMatrixNormalized(s2.get(i)));
        }
        Assert.assertArrayEquals(expectedFeatures.shape(), ds.getFeatures().shape());
        Assert.assertEquals(expectedFeatures, ds.getFeatures());
        Assert.assertEquals(expLabels, ds.getLabels());
        Assert.assertEquals(expectedFeatureMask, ds.getFeaturesMaskArray());
        Assert.assertNull(ds.getLabelsMaskArray());
    }

    @Test
    public void testCnnSentenceDataSetIteratorNoValidTokensNextEdgeCase() throws Exception {
        // Case: 2 minibatches, of size 2
        // First minibatch: OK
        // Second minibatch: would be empty
        // Therefore: after first minibatch is returned, .hasNext() should return false
        WordVectors w2v = WordVectorSerializer.readWord2VecModel(new ClassPathResource("word2vec/googleload/sample_vec.bin").getFile());
        int vectorSize = w2v.lookupTable().layerSize();
        List<String> sentences = new ArrayList<>();
        sentences.add("these balance Database model");
        sentences.add("into same THISWORDDOESNTEXIST are");
        // Last 2 sentences - no valid words
        sentences.add("NOVALID WORDSHERE");
        sentences.add("!!!");
        int maxLength = 4;
        List<String> s1 = Arrays.asList("these", "balance", "Database", "model");
        List<String> s2 = Arrays.asList("into", "same", "are");
        List<String> labelsForSentences = Arrays.asList("Positive", "Negative", "Positive", "Negative");
        INDArray expLabels = Nd4j.create(new double[][]{ new double[]{ 0, 1 }, new double[]{ 1, 0 } });// Order of labels: alphabetic. Positive -> [0,1]

        LabeledSentenceProvider p = new CollectionLabeledSentenceProvider(sentences, labelsForSentences, null);
        CnnSentenceDataSetIterator dsi = new CnnSentenceDataSetIterator.Builder(Format.CNN2D).sentenceProvider(p).wordVectors(w2v).useNormalizedWordVectors(true).maxSentenceLength(256).minibatchSize(2).sentencesAlongHeight(false).build();
        Assert.assertTrue(dsi.hasNext());
        DataSet ds = dsi.next();
        Assert.assertFalse(dsi.hasNext());
        INDArray expectedFeatures = Nd4j.create(2, 1, vectorSize, maxLength);
        INDArray expectedFeatureMask = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1 }, new double[]{ 1, 1, 1, 0 } }).reshape('c', 2, 1, 1, 4);
        for (int i = 0; i < 4; i++) {
            expectedFeatures.get(NDArrayIndex.point(0), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(i)).assign(w2v.getWordVectorMatrixNormalized(s1.get(i)));
        }
        for (int i = 0; i < 3; i++) {
            expectedFeatures.get(NDArrayIndex.point(1), NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(i)).assign(w2v.getWordVectorMatrixNormalized(s2.get(i)));
        }
        Assert.assertArrayEquals(expectedFeatures.shape(), ds.getFeatures().shape());
        Assert.assertEquals(expectedFeatures, ds.getFeatures());
        Assert.assertEquals(expLabels, ds.getLabels());
        Assert.assertEquals(expectedFeatureMask, ds.getFeaturesMaskArray());
        Assert.assertNull(ds.getLabelsMaskArray());
    }
}

