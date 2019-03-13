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
package org.deeplearning4j.datasets.iterator;


import Activation.RELU;
import Activation.SOFTMAX;
import GradientNormalization.RenormalizeL2PerLayer;
import MultiLayerConfiguration.Builder;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import WeightInit.XAVIER;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.image.loader.LFWLoader;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.fetchers.DataSetType;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;


public class DataSetIteratorTest extends BaseDL4JTest {
    @Test
    public void testBatchSizeOfOneIris() throws Exception {
        // Test for (a) iterators returning correct number of examples, and
        // (b) Labels are a proper one-hot vector (i.e., sum is 1.0)
        // Iris:
        DataSetIterator iris = new IrisDataSetIterator(1, 5);
        int irisC = 0;
        while (iris.hasNext()) {
            irisC++;
            DataSet ds = iris.next();
            Assert.assertTrue(((ds.getLabels().sum(Integer.MAX_VALUE).getDouble(0)) == 1.0));
        } 
        Assert.assertEquals(5, irisC);
    }

    @Test
    public void testBatchSizeOfOneMnist() throws Exception {
        // MNIST:
        DataSetIterator mnist = new MnistDataSetIterator(1, 5);
        int mnistC = 0;
        while (mnist.hasNext()) {
            mnistC++;
            DataSet ds = mnist.next();
            Assert.assertTrue(((ds.getLabels().sum(Integer.MAX_VALUE).getDouble(0)) == 1.0));
        } 
        Assert.assertEquals(5, mnistC);
    }

    @Test
    public void testMnist() throws Exception {
        ClassPathResource cpr = new ClassPathResource("mnist_first_200.txt");
        CSVRecordReader rr = new CSVRecordReader(0, ',');
        rr.initialize(new org.datavec.api.split.FileSplit(cpr.getTempFileFromArchive()));
        RecordReaderDataSetIterator dsi = new RecordReaderDataSetIterator(rr, 10, 0, 10);
        MnistDataSetIterator iter = new MnistDataSetIterator(10, 200, false, true, false, 0);
        while (dsi.hasNext()) {
            DataSet dsExp = dsi.next();
            DataSet dsAct = iter.next();
            INDArray fExp = dsExp.getFeatures();
            fExp.divi(255);
            INDArray lExp = dsExp.getLabels();
            INDArray fAct = dsAct.getFeatures();
            INDArray lAct = dsAct.getLabels();
            Assert.assertEquals(fExp, fAct.castTo(fExp.dataType()));
            Assert.assertEquals(lExp, lAct.castTo(lExp.dataType()));
        } 
        Assert.assertFalse(iter.hasNext());
    }

    @Test
    public void testLfwIterator() throws Exception {
        int numExamples = 1;
        int row = 28;
        int col = 28;
        int channels = 1;
        LFWDataSetIterator iter = new LFWDataSetIterator(numExamples, new int[]{ row, col, channels }, true);
        Assert.assertTrue(iter.hasNext());
        DataSet data = iter.next();
        Assert.assertEquals(numExamples, data.getLabels().size(0));
        Assert.assertEquals(row, data.getFeatures().size(2));
    }

    @Test
    public void testTinyImageNetIterator() throws Exception {
        int numClasses = 200;
        int row = 64;
        int col = 64;
        int channels = 3;
        TinyImageNetDataSetIterator iter = new TinyImageNetDataSetIterator(1, DataSetType.TEST);
        Assert.assertTrue(iter.hasNext());
        DataSet data = iter.next();
        Assert.assertEquals(numClasses, data.getLabels().size(1));
        Assert.assertArrayEquals(new long[]{ 1, channels, row, col }, data.getFeatures().shape());
    }

    @Test
    public void testTinyImageNetIterator2() throws Exception {
        int numClasses = 200;
        int row = 224;
        int col = 224;
        int channels = 3;
        TinyImageNetDataSetIterator iter = new TinyImageNetDataSetIterator(1, new int[]{ row, col }, DataSetType.TEST);
        Assert.assertTrue(iter.hasNext());
        DataSet data = iter.next();
        Assert.assertEquals(numClasses, data.getLabels().size(1));
        Assert.assertArrayEquals(new long[]{ 1, channels, row, col }, data.getFeatures().shape());
    }

    @Test
    public void testLfwModel() throws Exception {
        final int numRows = 28;
        final int numColumns = 28;
        int numChannels = 3;
        int outputNum = LFWLoader.NUM_LABELS;
        int numSamples = LFWLoader.NUM_IMAGES;
        int batchSize = 2;
        int seed = 123;
        int listenerFreq = 1;
        LFWDataSetIterator lfw = new LFWDataSetIterator(batchSize, numSamples, new int[]{ numRows, numColumns, numChannels }, outputNum, false, true, 1.0, new Random(seed));
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().seed(seed).gradientNormalization(RenormalizeL2PerLayer).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).list().layer(0, nOut(6).weightInit(XAVIER).activation(RELU).build()).layer(1, stride(1, 1).build()).layer(2, nOut(outputNum).weightInit(XAVIER).activation(SOFTMAX).build()).setInputType(InputType.convolutionalFlat(numRows, numColumns, numChannels));
        MultiLayerNetwork model = new MultiLayerNetwork(builder.build());
        model.init();
        model.setListeners(new ScoreIterationListener(listenerFreq));
        model.fit(lfw.next());
        DataSet dataTest = lfw.next();
        INDArray output = model.output(dataTest.getFeatures());
        Evaluation eval = new Evaluation(outputNum);
        eval.eval(dataTest.getLabels(), output);
        System.out.println(eval.stats());
    }

    @Test
    public void testCifar10Iterator() throws Exception {
        int numExamples = 1;
        int row = 32;
        int col = 32;
        int channels = 3;
        Cifar10DataSetIterator iter = new Cifar10DataSetIterator(numExamples);
        Assert.assertTrue(iter.hasNext());
        DataSet data = iter.next();
        Assert.assertEquals(numExamples, data.getLabels().size(0));
        Assert.assertEquals(((channels * row) * col), data.getFeatures().ravel().length());
    }

    @Test
    public void testIteratorDataSetIteratorCombining() {
        // Test combining of a bunch of small (size 1) data sets together
        int batchSize = 3;
        int numBatches = 4;
        int featureSize = 5;
        int labelSize = 6;
        Nd4j.getRandom().setSeed(12345);
        List<DataSet> orig = new ArrayList<>();
        for (int i = 0; i < (batchSize * numBatches); i++) {
            INDArray features = Nd4j.rand(1, featureSize);
            INDArray labels = Nd4j.rand(1, labelSize);
            orig.add(new DataSet(features, labels));
        }
        DataSetIterator iter = new IteratorDataSetIterator(orig.iterator(), batchSize);
        int count = 0;
        while (iter.hasNext()) {
            DataSet ds = iter.next();
            Assert.assertArrayEquals(new long[]{ batchSize, featureSize }, ds.getFeatures().shape());
            Assert.assertArrayEquals(new long[]{ batchSize, labelSize }, ds.getLabels().shape());
            List<INDArray> fList = new ArrayList<>();
            List<INDArray> lList = new ArrayList<>();
            for (int i = 0; i < batchSize; i++) {
                DataSet dsOrig = orig.get(((count * batchSize) + i));
                fList.add(dsOrig.getFeatures());
                lList.add(dsOrig.getLabels());
            }
            INDArray fExp = Nd4j.vstack(fList);
            INDArray lExp = Nd4j.vstack(lList);
            Assert.assertEquals(fExp, ds.getFeatures());
            Assert.assertEquals(lExp, ds.getLabels());
            count++;
        } 
        Assert.assertEquals(count, numBatches);
    }

    @Test
    public void testIteratorDataSetIteratorSplitting() {
        // Test splitting large data sets into smaller ones
        int origBatchSize = 4;
        int origNumDSs = 3;
        int batchSize = 3;
        int numBatches = 4;
        int featureSize = 5;
        int labelSize = 6;
        Nd4j.getRandom().setSeed(12345);
        List<DataSet> orig = new ArrayList<>();
        for (int i = 0; i < origNumDSs; i++) {
            INDArray features = Nd4j.rand(origBatchSize, featureSize);
            INDArray labels = Nd4j.rand(origBatchSize, labelSize);
            orig.add(new DataSet(features, labels));
        }
        List<DataSet> expected = new ArrayList<>();
        expected.add(new DataSet(orig.get(0).getFeatures().getRows(0, 1, 2), orig.get(0).getLabels().getRows(0, 1, 2)));
        expected.add(new DataSet(Nd4j.vstack(orig.get(0).getFeatures().getRows(3), orig.get(1).getFeatures().getRows(0, 1)), Nd4j.vstack(orig.get(0).getLabels().getRows(3), orig.get(1).getLabels().getRows(0, 1))));
        expected.add(new DataSet(Nd4j.vstack(orig.get(1).getFeatures().getRows(2, 3), orig.get(2).getFeatures().getRows(0)), Nd4j.vstack(orig.get(1).getLabels().getRows(2, 3), orig.get(2).getLabels().getRows(0))));
        expected.add(new DataSet(orig.get(2).getFeatures().getRows(1, 2, 3), orig.get(2).getLabels().getRows(1, 2, 3)));
        DataSetIterator iter = new IteratorDataSetIterator(orig.iterator(), batchSize);
        int count = 0;
        while (iter.hasNext()) {
            DataSet ds = iter.next();
            Assert.assertEquals(expected.get(count), ds);
            count++;
        } 
        Assert.assertEquals(count, numBatches);
    }
}

