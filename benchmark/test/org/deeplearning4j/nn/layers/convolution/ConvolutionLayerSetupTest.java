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
package org.deeplearning4j.nn.layers.convolution;


import Activation.RELU;
import BatchNormalizationParamInitializer.BETA;
import BatchNormalizationParamInitializer.GAMMA;
import MultiLayerConfiguration.Builder;
import NeuralNetConfiguration.ListBuilder;
import OptimizationAlgorithm.CONJUGATE_GRADIENT;
import WeightInit.XAVIER;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.util.FeatureUtil;


/**
 *
 *
 * @author Adam Gibson
 */
public class ConvolutionLayerSetupTest extends BaseDL4JTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testConvolutionLayerSetup() {
        MultiLayerConfiguration.Builder builder = inComplete();
        builder.setInputType(InputType.convolutionalFlat(28, 28, 1));
        MultiLayerConfiguration completed = complete().build();
        MultiLayerConfiguration test = builder.build();
        Assert.assertEquals(completed, test);
    }

    @Test
    public void testDenseToOutputLayer() {
        Nd4j.getRandom().setSeed(12345);
        final int numRows = 76;
        final int numColumns = 76;
        int nChannels = 3;
        int outputNum = 6;
        int seed = 123;
        // setup the network
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().seed(seed).l1(0.1).l2(2.0E-4).dropOut(0.5).miniBatch(true).optimizationAlgo(CONJUGATE_GRADIENT).list().layer(0, build()).layer(1, build()).layer(2, build()).layer(3, build()).layer(4, build()).layer(5, build()).setInputType(InputType.convolutional(numRows, numColumns, nChannels));
        DataSet d = new DataSet(Nd4j.rand(new int[]{ 10, nChannels, numRows, numColumns }), FeatureUtil.toOutcomeMatrix(new int[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, 6));
        MultiLayerNetwork network = new MultiLayerNetwork(builder.build());
        network.init();
        network.fit(d);
    }

    @Test
    public void testMnistLenet() throws Exception {
        MultiLayerConfiguration.Builder incomplete = incompleteMnistLenet();
        incomplete.setInputType(InputType.convolutionalFlat(28, 28, 1));
        MultiLayerConfiguration testConf = incomplete.build();
        Assert.assertEquals(800, getNIn());
        Assert.assertEquals(500, getNIn());
        // test instantiation
        DataSetIterator iter = new MnistDataSetIterator(10, 10);
        MultiLayerNetwork network = new MultiLayerNetwork(testConf);
        network.init();
        network.fit(iter.next());
    }

    @Test
    public void testMultiChannel() throws Exception {
        INDArray in = Nd4j.rand(new int[]{ 10, 3, 28, 28 });
        INDArray labels = Nd4j.rand(10, 2);
        DataSet next = new DataSet(in, labels);
        NeuralNetConfiguration.ListBuilder builder = ((NeuralNetConfiguration.ListBuilder) (incompleteLFW()));
        builder.setInputType(InputType.convolutional(28, 28, 3));
        MultiLayerConfiguration conf = builder.build();
        ConvolutionLayer layer2 = ((ConvolutionLayer) (conf.getConf(2).getLayer()));
        Assert.assertEquals(6, layer2.getNIn());
        MultiLayerNetwork network = new MultiLayerNetwork(conf);
        network.init();
        network.fit(next);
    }

    @Test
    public void testLRN() throws Exception {
        List<String> labels = new ArrayList<>(Arrays.asList("Zico", "Ziwang_Xu"));
        File dir = testDir.newFolder();
        new ClassPathResource("lfwtest/").copyDirectory(dir);
        String rootDir = dir.getAbsolutePath();
        RecordReader reader = new ImageRecordReader(28, 28, 3);
        reader.initialize(new FileSplit(new File(rootDir)));
        DataSetIterator recordReader = new org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator(reader, 10, 1, labels.size());
        labels.remove("lfwtest");
        NeuralNetConfiguration.ListBuilder builder = ((NeuralNetConfiguration.ListBuilder) (incompleteLRN()));
        builder.setInputType(InputType.convolutional(28, 28, 3));
        MultiLayerConfiguration conf = builder.build();
        ConvolutionLayer layer2 = ((ConvolutionLayer) (conf.getConf(3).getLayer()));
        Assert.assertEquals(6, layer2.getNIn());
    }

    @Test
    public void testDeconvolution() {
        MultiLayerConfiguration.Builder builder = // (56-2+2*1)/2+1 = 29 -> 29x29x3
        // out = stride * (in-1) + filter - 2*pad -> 2 * (28-1) + 2 - 0 = 56 -> 56x56x3
        new NeuralNetConfiguration.Builder().list().layer(0, build()).layer(1, build()).layer(2, build()).setInputType(InputType.convolutional(28, 28, 1));
        MultiLayerConfiguration conf = builder.build();
        Assert.assertNotNull(conf.getInputPreProcess(2));
        Assert.assertTrue(((conf.getInputPreProcess(2)) instanceof CnnToFeedForwardPreProcessor));
        CnnToFeedForwardPreProcessor proc = ((CnnToFeedForwardPreProcessor) (conf.getInputPreProcess(2)));
        Assert.assertEquals(29, proc.getInputHeight());
        Assert.assertEquals(29, proc.getInputWidth());
        Assert.assertEquals(3, proc.getNumChannels());
        Assert.assertEquals(((29 * 29) * 3), getNIn());
    }

    @Test
    public void testSubSamplingWithPadding() {
        MultiLayerConfiguration.Builder builder = // (14-2+2)/2+1 = 8 -> 8x8x3
        // (28-2+0)/2+1 = 14
        new NeuralNetConfiguration.Builder().list().layer(0, build()).layer(1, build()).layer(2, build()).setInputType(InputType.convolutional(28, 28, 1));
        MultiLayerConfiguration conf = builder.build();
        Assert.assertNotNull(conf.getInputPreProcess(2));
        Assert.assertTrue(((conf.getInputPreProcess(2)) instanceof CnnToFeedForwardPreProcessor));
        CnnToFeedForwardPreProcessor proc = ((CnnToFeedForwardPreProcessor) (conf.getInputPreProcess(2)));
        Assert.assertEquals(8, proc.getInputHeight());
        Assert.assertEquals(8, proc.getInputWidth());
        Assert.assertEquals(3, proc.getNumChannels());
        Assert.assertEquals(((8 * 8) * 3), getNIn());
    }

    @Test
    public void testUpsampling() {
        MultiLayerConfiguration.Builder builder = // 14 * 3 = 42!
        // (28-2+0)/2+1 = 14
        new NeuralNetConfiguration.Builder().list().layer(build()).layer(build()).layer(build()).setInputType(InputType.convolutional(28, 28, 1));
        MultiLayerConfiguration conf = builder.build();
        Assert.assertNotNull(conf.getInputPreProcess(2));
        Assert.assertTrue(((conf.getInputPreProcess(2)) instanceof CnnToFeedForwardPreProcessor));
        CnnToFeedForwardPreProcessor proc = ((CnnToFeedForwardPreProcessor) (conf.getInputPreProcess(2)));
        Assert.assertEquals(42, proc.getInputHeight());
        Assert.assertEquals(42, proc.getInputWidth());
        Assert.assertEquals(3, proc.getNumChannels());
        Assert.assertEquals(((42 * 42) * 3), getNIn());
    }

    @Test
    public void testSpaceToBatch() {
        int[] blocks = new int[]{ 2, 2 };
        MultiLayerConfiguration.Builder builder = // Divide space dimensions by blocks, i.e. 14/2 = 7
        // (28-2+0)/2+1 = 14
        new NeuralNetConfiguration.Builder().list().layer(build()).layer(new SpaceToBatchLayer.Builder(blocks).build()).layer(build()).setInputType(InputType.convolutional(28, 28, 1));
        MultiLayerConfiguration conf = builder.build();
        Assert.assertNotNull(conf.getInputPreProcess(2));
        Assert.assertTrue(((conf.getInputPreProcess(2)) instanceof CnnToFeedForwardPreProcessor));
        CnnToFeedForwardPreProcessor proc = ((CnnToFeedForwardPreProcessor) (conf.getInputPreProcess(2)));
        Assert.assertEquals(7, proc.getInputHeight());
        Assert.assertEquals(7, proc.getInputWidth());
        Assert.assertEquals(3, proc.getNumChannels());
    }

    @Test
    public void testSpaceToDepth() {
        int blocks = 2;
        MultiLayerConfiguration.Builder builder = // nIn of the next layer gets multiplied by 2*2.
        // Divide space dimensions by blocks, i.e. 14/2 = 7 -> 7x7x12 out (3x2x2 depth)
        // (28-2+0)/2+1 = 14 -> 14x14x3 out
        new NeuralNetConfiguration.Builder().list().layer(build()).layer(build()).layer(build()).setInputType(InputType.convolutional(28, 28, 1));
        MultiLayerConfiguration conf = builder.build();
        Assert.assertNotNull(conf.getInputPreProcess(2));
        Assert.assertTrue(((conf.getInputPreProcess(2)) instanceof CnnToFeedForwardPreProcessor));
        CnnToFeedForwardPreProcessor proc = ((CnnToFeedForwardPreProcessor) (conf.getInputPreProcess(2)));
        Assert.assertEquals(7, proc.getInputHeight());
        Assert.assertEquals(7, proc.getInputWidth());
        Assert.assertEquals(12, proc.getNumChannels());
    }

    @Test
    public void testCNNDBNMultiLayer() throws Exception {
        DataSetIterator iter = new MnistDataSetIterator(2, 2);
        DataSet next = iter.next();
        // Run with separate activation layer
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).seed(123).weightInit(XAVIER).list().layer(0, build()).layer(1, new BatchNormalization.Builder().build()).layer(2, build()).layer(3, build()).layer(4, build()).layer(5, build()).layer(6, build()).setInputType(InputType.convolutionalFlat(28, 28, 1)).build();
        MultiLayerNetwork network = new MultiLayerNetwork(conf);
        network.init();
        network.setInput(next.getFeatures());
        INDArray activationsActual = network.output(next.getFeatures());
        Assert.assertEquals(10, activationsActual.shape()[1], 0.01);
        network.fit(next);
        INDArray actualGammaParam = network.getLayer(1).getParam(GAMMA);
        INDArray actualBetaParam = network.getLayer(1).getParam(BETA);
        Assert.assertTrue((actualGammaParam != null));
        Assert.assertTrue((actualBetaParam != null));
    }

    @Test
    public void testSeparableConv2D() {
        MultiLayerConfiguration.Builder builder = // (14-2+2)/2+1 = 8 -> 8x8x3
        // (28-2+0)/2+1 = 14
        new NeuralNetConfiguration.Builder().list().layer(build()).layer(build()).layer(2, build()).setInputType(InputType.convolutional(28, 28, 1));
        MultiLayerConfiguration conf = builder.build();
        Assert.assertNotNull(conf.getInputPreProcess(2));
        Assert.assertTrue(((conf.getInputPreProcess(2)) instanceof CnnToFeedForwardPreProcessor));
        CnnToFeedForwardPreProcessor proc = ((CnnToFeedForwardPreProcessor) (conf.getInputPreProcess(2)));
        Assert.assertEquals(8, proc.getInputHeight());
        Assert.assertEquals(8, proc.getInputWidth());
        Assert.assertEquals(3, proc.getNumChannels());
        Assert.assertEquals(((8 * 8) * 3), getNIn());
    }

    @Test
    public void testDeconv2D() {
        MultiLayerConfiguration.Builder builder = // (56-2+2*1)/2+1 = 29 -> 29x29x3
        // out = stride * (in-1) + filter - 2*pad -> 2 * (28-1) + 2 - 0 = 56 -> 56x56x3
        new NeuralNetConfiguration.Builder().list().layer(build()).layer(build()).layer(2, build()).setInputType(InputType.convolutional(28, 28, 1));
        MultiLayerConfiguration conf = builder.build();
        Assert.assertNotNull(conf.getInputPreProcess(2));
        Assert.assertTrue(((conf.getInputPreProcess(2)) instanceof CnnToFeedForwardPreProcessor));
        CnnToFeedForwardPreProcessor proc = ((CnnToFeedForwardPreProcessor) (conf.getInputPreProcess(2)));
        Assert.assertEquals(29, proc.getInputHeight());
        Assert.assertEquals(29, proc.getInputWidth());
        Assert.assertEquals(3, proc.getNumChannels());
        Assert.assertEquals(((29 * 29) * 3), getNIn());
    }
}

