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
package org.deeplearning4j.nn.layers;


import Activation.IDENTITY;
import Activation.RELU;
import Activation.SOFTMAX;
import Activation.TANH;
import LossFunctions.LossFunction;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import WeightInit.XAVIER;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.InputPreProcessor;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.DropoutLayer;
import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;


/**
 *
 */
public class DropoutLayerTest extends BaseDL4JTest {
    @Test
    public void testInputTypes() {
        DropoutLayer config = new DropoutLayer.Builder(0.5).build();
        InputType in1 = InputType.feedForward(20);
        InputType in2 = InputType.convolutional(28, 28, 1);
        Assert.assertEquals(in1, config.getOutputType(0, in1));
        Assert.assertEquals(in2, config.getOutputType(0, in2));
        Assert.assertNull(config.getPreProcessorForInputType(in1));
        Assert.assertNull(config.getPreProcessorForInputType(in2));
    }

    @Test
    public void testDropoutLayerWithoutTraining() throws Exception {
        MultiLayerConfiguration confIntegrated = new NeuralNetConfiguration.Builder().seed(3648).list().layer(0, new ConvolutionLayer.Builder(1, 1).stride(1, 1).nIn(1).nOut(1).dropOut(0.25).activation(IDENTITY).weightInit(XAVIER).build()).layer(1, new org.deeplearning4j.nn.conf.layers.OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).weightInit(XAVIER).dropOut(0.25).nOut(4).build()).setInputType(InputType.convolutionalFlat(2, 2, 1)).build();
        MultiLayerNetwork netIntegrated = new MultiLayerNetwork(confIntegrated);
        netIntegrated.init();
        netIntegrated.getLayer(0).setParam("W", Nd4j.eye(1));
        netIntegrated.getLayer(0).setParam("b", Nd4j.zeros(1, 1));
        netIntegrated.getLayer(1).setParam("W", Nd4j.eye(4));
        netIntegrated.getLayer(1).setParam("b", Nd4j.zeros(4, 1));
        MultiLayerConfiguration confSeparate = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).seed(3648).list().layer(0, new DropoutLayer.Builder(0.25).build()).layer(1, new ConvolutionLayer.Builder(1, 1).stride(1, 1).nIn(1).nOut(1).activation(IDENTITY).weightInit(XAVIER).build()).layer(2, new DropoutLayer.Builder(0.25).build()).layer(3, new org.deeplearning4j.nn.conf.layers.OutputLayer.Builder(LossFunction.MCXENT).weightInit(XAVIER).activation(SOFTMAX).nOut(4).build()).setInputType(InputType.convolutionalFlat(2, 2, 1)).build();
        MultiLayerNetwork netSeparate = new MultiLayerNetwork(confSeparate);
        netSeparate.init();
        netSeparate.getLayer(1).setParam("W", Nd4j.eye(1));
        netSeparate.getLayer(1).setParam("b", Nd4j.zeros(1, 1));
        netSeparate.getLayer(3).setParam("W", Nd4j.eye(4));
        netSeparate.getLayer(3).setParam("b", Nd4j.zeros(4, 1));
        // Disable input modification for this test:
        for (Layer l : netIntegrated.getLayers()) {
            l.allowInputModification(false);
        }
        for (Layer l : netSeparate.getLayers()) {
            l.allowInputModification(false);
        }
        INDArray in = Nd4j.arange(1, 5).reshape(1, 4);
        Nd4j.getRandom().setSeed(12345);
        List<INDArray> actTrainIntegrated = netIntegrated.feedForward(in.dup(), true);
        Nd4j.getRandom().setSeed(12345);
        List<INDArray> actTrainSeparate = netSeparate.feedForward(in.dup(), true);
        Nd4j.getRandom().setSeed(12345);
        List<INDArray> actTestIntegrated = netIntegrated.feedForward(in.dup(), false);
        Nd4j.getRandom().setSeed(12345);
        List<INDArray> actTestSeparate = netSeparate.feedForward(in.dup(), false);
        // Check masks:
        INDArray maskIntegrated = getMask();
        INDArray maskSeparate = getMask();
        Assert.assertEquals(maskIntegrated, maskSeparate);
        Assert.assertEquals(actTrainIntegrated.get(1), actTrainSeparate.get(2));
        Assert.assertEquals(actTrainIntegrated.get(2), actTrainSeparate.get(4));
        Assert.assertEquals(actTestIntegrated.get(1), actTestSeparate.get(2));
        Assert.assertEquals(actTestIntegrated.get(2), actTestSeparate.get(4));
    }

    @Test
    public void testDropoutLayerWithDenseMnist() throws Exception {
        DataSetIterator iter = new MnistDataSetIterator(2, 2);
        DataSet next = iter.next();
        // Run without separate activation layer
        MultiLayerConfiguration confIntegrated = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).seed(123).list().layer(0, new DenseLayer.Builder().nIn(((28 * 28) * 1)).nOut(10).activation(RELU).weightInit(XAVIER).build()).layer(1, new org.deeplearning4j.nn.conf.layers.OutputLayer.Builder(LossFunction.MCXENT).weightInit(XAVIER).activation(SOFTMAX).dropOut(0.25).nIn(10).nOut(10).build()).build();
        MultiLayerNetwork netIntegrated = new MultiLayerNetwork(confIntegrated);
        netIntegrated.init();
        netIntegrated.fit(next);
        // Run with separate activation layer
        MultiLayerConfiguration confSeparate = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).seed(123).list().layer(0, new DenseLayer.Builder().nIn(((28 * 28) * 1)).nOut(10).activation(RELU).weightInit(XAVIER).build()).layer(1, new DropoutLayer.Builder(0.25).build()).layer(2, new org.deeplearning4j.nn.conf.layers.OutputLayer.Builder(LossFunction.MCXENT).weightInit(XAVIER).activation(SOFTMAX).nIn(10).nOut(10).build()).build();
        MultiLayerNetwork netSeparate = new MultiLayerNetwork(confSeparate);
        netSeparate.init();
        netSeparate.fit(next);
        // Disable input modification for this test:
        for (Layer l : netIntegrated.getLayers()) {
            l.allowInputModification(false);
        }
        for (Layer l : netSeparate.getLayers()) {
            l.allowInputModification(false);
        }
        // check parameters
        Assert.assertEquals(netIntegrated.getLayer(0).getParam("W"), netSeparate.getLayer(0).getParam("W"));
        Assert.assertEquals(netIntegrated.getLayer(0).getParam("b"), netSeparate.getLayer(0).getParam("b"));
        Assert.assertEquals(netIntegrated.getLayer(1).getParam("W"), netSeparate.getLayer(2).getParam("W"));
        Assert.assertEquals(netIntegrated.getLayer(1).getParam("b"), netSeparate.getLayer(2).getParam("b"));
        // check activations
        netIntegrated.setInput(next.getFeatures());
        netSeparate.setInput(next.getFeatures());
        Nd4j.getRandom().setSeed(12345);
        List<INDArray> actTrainIntegrated = netIntegrated.feedForward(true);
        Nd4j.getRandom().setSeed(12345);
        List<INDArray> actTrainSeparate = netSeparate.feedForward(true);
        Assert.assertEquals(actTrainIntegrated.get(1), actTrainSeparate.get(1));
        Assert.assertEquals(actTrainIntegrated.get(2), actTrainSeparate.get(3));
        Nd4j.getRandom().setSeed(12345);
        List<INDArray> actTestIntegrated = netIntegrated.feedForward(false);
        Nd4j.getRandom().setSeed(12345);
        List<INDArray> actTestSeparate = netSeparate.feedForward(false);
        Assert.assertEquals(actTestIntegrated.get(1), actTrainSeparate.get(1));
        Assert.assertEquals(actTestIntegrated.get(2), actTestSeparate.get(3));
    }

    @Test
    public void testDropoutLayerWithConvMnist() throws Exception {
        DataSetIterator iter = new MnistDataSetIterator(2, 2);
        DataSet next = iter.next();
        // Run without separate activation layer
        Nd4j.getRandom().setSeed(12345);
        MultiLayerConfiguration confIntegrated = new NeuralNetConfiguration.Builder().seed(123).list().layer(0, new ConvolutionLayer.Builder(4, 4).stride(2, 2).nIn(1).nOut(20).activation(TANH).weightInit(XAVIER).build()).layer(1, new org.deeplearning4j.nn.conf.layers.OutputLayer.Builder(LossFunction.MCXENT).weightInit(XAVIER).activation(SOFTMAX).dropOut(0.5).nOut(10).build()).setInputType(InputType.convolutionalFlat(28, 28, 1)).build();
        // Run with separate activation layer
        Nd4j.getRandom().setSeed(12345);
        // Manually configure preprocessors
        // This is necessary, otherwise CnnToFeedForwardPreprocessor will be in different locatinos
        // i.e., dropout on 4d activations in latter, and dropout on 2d activations in former
        Map<Integer, InputPreProcessor> preProcessorMap = new HashMap<>();
        preProcessorMap.put(1, new CnnToFeedForwardPreProcessor(13, 13, 20));
        MultiLayerConfiguration confSeparate = new NeuralNetConfiguration.Builder().seed(123).list().layer(0, new ConvolutionLayer.Builder(4, 4).stride(2, 2).nIn(1).nOut(20).activation(TANH).weightInit(XAVIER).build()).layer(1, new DropoutLayer.Builder(0.5).build()).layer(2, new org.deeplearning4j.nn.conf.layers.OutputLayer.Builder(LossFunction.MCXENT).weightInit(XAVIER).activation(SOFTMAX).nOut(10).build()).inputPreProcessors(preProcessorMap).setInputType(InputType.convolutionalFlat(28, 28, 1)).build();
        Nd4j.getRandom().setSeed(12345);
        MultiLayerNetwork netIntegrated = new MultiLayerNetwork(confIntegrated);
        netIntegrated.init();
        Nd4j.getRandom().setSeed(12345);
        MultiLayerNetwork netSeparate = new MultiLayerNetwork(confSeparate);
        netSeparate.init();
        Assert.assertEquals(netIntegrated.params(), netSeparate.params());
        Nd4j.getRandom().setSeed(12345);
        netIntegrated.fit(next);
        Nd4j.getRandom().setSeed(12345);
        netSeparate.fit(next);
        Assert.assertEquals(netIntegrated.params(), netSeparate.params());
        // check parameters
        Assert.assertEquals(netIntegrated.getLayer(0).getParam("W"), netSeparate.getLayer(0).getParam("W"));
        Assert.assertEquals(netIntegrated.getLayer(0).getParam("b"), netSeparate.getLayer(0).getParam("b"));
        Assert.assertEquals(netIntegrated.getLayer(1).getParam("W"), netSeparate.getLayer(2).getParam("W"));
        Assert.assertEquals(netIntegrated.getLayer(1).getParam("b"), netSeparate.getLayer(2).getParam("b"));
        // check activations
        netIntegrated.setInput(next.getFeatures());
        netSeparate.setInput(next.getFeatures());
        Nd4j.getRandom().setSeed(12345);
        List<INDArray> actTrainIntegrated = netIntegrated.feedForward(true);
        Nd4j.getRandom().setSeed(12345);
        List<INDArray> actTrainSeparate = netSeparate.feedForward(true);
        Assert.assertEquals(actTrainIntegrated.get(1), actTrainSeparate.get(1));
        Assert.assertEquals(actTrainIntegrated.get(2), actTrainSeparate.get(3));
        Nd4j.getRandom().setSeed(12345);
        List<INDArray> actTestIntegrated = netIntegrated.feedForward(false);
        Nd4j.getRandom().setSeed(12345);
        List<INDArray> actTestSeparate = netSeparate.feedForward(false);
        Assert.assertEquals(actTestIntegrated.get(1), actTrainSeparate.get(1));
        Assert.assertEquals(actTestIntegrated.get(2), actTestSeparate.get(3));
    }
}

