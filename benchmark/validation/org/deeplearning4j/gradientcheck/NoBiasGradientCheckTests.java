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
package org.deeplearning4j.gradientcheck;


import Activation.SOFTMAX;
import Activation.TANH;
import DataType.DOUBLE;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.NoOp;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;


public class NoBiasGradientCheckTests extends BaseDL4JTest {
    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-6;

    private static final double DEFAULT_MAX_REL_ERROR = 0.001;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-8;

    static {
        Nd4j.setDataType(DOUBLE);
    }

    @Test
    public void testGradientNoBiasDenseOutput() {
        int nIn = 5;
        int nOut = 3;
        int layerSize = 6;
        for (int minibatch : new int[]{ 1, 4 }) {
            INDArray input = Nd4j.rand(minibatch, nIn);
            INDArray labels = Nd4j.zeros(minibatch, nOut);
            for (int i = 0; i < minibatch; i++) {
                labels.putScalar(i, (i % nOut), 1.0);
            }
            for (boolean denseHasBias : new boolean[]{ true, false }) {
                for (boolean outHasBias : new boolean[]{ true, false }) {
                    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).seed(12345L).list().layer(0, // Layer 0: Always have a bias
                    new DenseLayer.Builder().nIn(nIn).nOut(layerSize).dist(new NormalDistribution(0, 1)).activation(TANH).hasBias(true).build()).layer(1, new DenseLayer.Builder().nIn(layerSize).nOut(layerSize).dist(new NormalDistribution(0, 1)).activation(TANH).hasBias(denseHasBias).build()).layer(2, new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nIn(layerSize).nOut(nOut).dist(new NormalDistribution(0, 1)).hasBias(outHasBias).build()).build();
                    MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                    mln.init();
                    if (denseHasBias) {
                        Assert.assertEquals(((layerSize * layerSize) + layerSize), mln.getLayer(1).numParams());
                    } else {
                        Assert.assertEquals((layerSize * layerSize), mln.getLayer(1).numParams());
                    }
                    if (outHasBias) {
                        Assert.assertEquals(((layerSize * nOut) + nOut), mln.getLayer(2).numParams());
                    } else {
                        Assert.assertEquals((layerSize * nOut), mln.getLayer(2).numParams());
                    }
                    String msg = ((((("testGradientNoBiasDenseOutput(), minibatch = " + minibatch) + ", denseHasBias = ") + denseHasBias) + ", outHasBias = ") + outHasBias) + ")";
                    if (NoBiasGradientCheckTests.PRINT_RESULTS) {
                        System.out.println(msg);
                    }
                    boolean gradOK = GradientCheckUtil.checkGradients(mln, NoBiasGradientCheckTests.DEFAULT_EPS, NoBiasGradientCheckTests.DEFAULT_MAX_REL_ERROR, NoBiasGradientCheckTests.DEFAULT_MIN_ABS_ERROR, NoBiasGradientCheckTests.PRINT_RESULTS, NoBiasGradientCheckTests.RETURN_ON_FIRST_FAILURE, input, labels);
                    Assert.assertTrue(msg, gradOK);
                    TestUtils.testModelSerialization(mln);
                }
            }
        }
    }

    @Test
    public void testGradientNoBiasRnnOutput() {
        int nIn = 5;
        int nOut = 3;
        int tsLength = 3;
        int layerSize = 6;
        for (int minibatch : new int[]{ 1, 4 }) {
            INDArray input = Nd4j.rand(new int[]{ minibatch, nIn, tsLength });
            INDArray labels = TestUtils.randomOneHotTimeSeries(minibatch, nOut, tsLength);
            for (boolean rnnOutHasBias : new boolean[]{ true, false }) {
                MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).seed(12345L).list().layer(0, new LSTM.Builder().nIn(nIn).nOut(layerSize).dist(new NormalDistribution(0, 1)).activation(TANH).build()).layer(1, new RnnOutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nIn(layerSize).nOut(nOut).dist(new NormalDistribution(0, 1)).hasBias(rnnOutHasBias).build()).build();
                MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                mln.init();
                if (rnnOutHasBias) {
                    Assert.assertEquals(((layerSize * nOut) + nOut), mln.getLayer(1).numParams());
                } else {
                    Assert.assertEquals((layerSize * nOut), mln.getLayer(1).numParams());
                }
                String msg = ((("testGradientNoBiasRnnOutput(), minibatch = " + minibatch) + ", rnnOutHasBias = ") + rnnOutHasBias) + ")";
                if (NoBiasGradientCheckTests.PRINT_RESULTS) {
                    System.out.println(msg);
                }
                boolean gradOK = GradientCheckUtil.checkGradients(mln, NoBiasGradientCheckTests.DEFAULT_EPS, NoBiasGradientCheckTests.DEFAULT_MAX_REL_ERROR, NoBiasGradientCheckTests.DEFAULT_MIN_ABS_ERROR, NoBiasGradientCheckTests.PRINT_RESULTS, NoBiasGradientCheckTests.RETURN_ON_FIRST_FAILURE, input, labels);
                Assert.assertTrue(msg, gradOK);
                TestUtils.testModelSerialization(mln);
            }
        }
    }

    @Test
    public void testGradientNoBiasEmbedding() {
        int nIn = 5;
        int nOut = 3;
        int layerSize = 6;
        for (int minibatch : new int[]{ 1, 4 }) {
            INDArray input = Nd4j.zeros(minibatch, 1);
            for (int i = 0; i < minibatch; i++) {
                input.putScalar(i, 0, (i % layerSize));
            }
            INDArray labels = Nd4j.zeros(minibatch, nOut);
            for (int i = 0; i < minibatch; i++) {
                labels.putScalar(i, (i % nOut), 1.0);
            }
            for (boolean embeddingHasBias : new boolean[]{ true, false }) {
                MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).seed(12345L).list().layer(0, new EmbeddingLayer.Builder().nIn(nIn).nOut(layerSize).dist(new NormalDistribution(0, 1)).activation(TANH).hasBias(embeddingHasBias).build()).layer(1, new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nIn(layerSize).nOut(nOut).dist(new NormalDistribution(0, 1)).build()).build();
                MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                mln.init();
                if (embeddingHasBias) {
                    Assert.assertEquals(((nIn * layerSize) + layerSize), mln.getLayer(0).numParams());
                } else {
                    Assert.assertEquals((nIn * layerSize), mln.getLayer(0).numParams());
                }
                String msg = ((("testGradientNoBiasEmbedding(), minibatch = " + minibatch) + ", embeddingHasBias = ") + embeddingHasBias) + ")";
                if (NoBiasGradientCheckTests.PRINT_RESULTS) {
                    System.out.println(msg);
                }
                boolean gradOK = GradientCheckUtil.checkGradients(mln, NoBiasGradientCheckTests.DEFAULT_EPS, NoBiasGradientCheckTests.DEFAULT_MAX_REL_ERROR, NoBiasGradientCheckTests.DEFAULT_MIN_ABS_ERROR, NoBiasGradientCheckTests.PRINT_RESULTS, NoBiasGradientCheckTests.RETURN_ON_FIRST_FAILURE, input, labels);
                Assert.assertTrue(msg, gradOK);
                TestUtils.testModelSerialization(mln);
            }
        }
    }

    @Test
    public void testCnnWithSubsamplingNoBias() {
        int nOut = 4;
        int[] minibatchSizes = new int[]{ 1, 3 };
        int width = 5;
        int height = 5;
        int inputDepth = 1;
        int[] kernel = new int[]{ 2, 2 };
        int[] stride = new int[]{ 1, 1 };
        int[] padding = new int[]{ 0, 0 };
        int pNorm = 3;
        for (int minibatchSize : minibatchSizes) {
            INDArray input = Nd4j.rand(minibatchSize, ((width * height) * inputDepth));
            INDArray labels = Nd4j.zeros(minibatchSize, nOut);
            for (int i = 0; i < minibatchSize; i++) {
                labels.putScalar(new int[]{ i, i % nOut }, 1.0);
            }
            for (boolean cnnHasBias : new boolean[]{ true, false }) {
                MultiLayerConfiguration conf = // Output: (3-2+0)/1+1 = 2
                // output: (4-2+0)/1+1 =3 -> 3x3x3
                // output: (5-2+0)/1+1 = 4
                new NeuralNetConfiguration.Builder().updater(new NoOp()).dist(new NormalDistribution(0, 1)).list().layer(new ConvolutionLayer.Builder(kernel, stride, padding).nIn(inputDepth).hasBias(false).nOut(3).build()).layer(kernelSize(kernel).stride(stride).padding(padding).pnorm(pNorm).build()).layer(new ConvolutionLayer.Builder(kernel, stride, padding).hasBias(cnnHasBias).nOut(2).build()).layer(new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(4).build()).setInputType(InputType.convolutionalFlat(height, width, inputDepth)).build();
                MultiLayerNetwork net = new MultiLayerNetwork(conf);
                net.init();
                if (cnnHasBias) {
                    Assert.assertEquals(((((3 * 2) * (kernel[0])) * (kernel[1])) + 2), net.getLayer(2).numParams());
                } else {
                    Assert.assertEquals((((3 * 2) * (kernel[0])) * (kernel[1])), net.getLayer(2).numParams());
                }
                String msg = (("testCnnWithSubsamplingNoBias(), minibatch = " + minibatchSize) + ", cnnHasBias = ") + cnnHasBias;
                System.out.println(msg);
                boolean gradOK = GradientCheckUtil.checkGradients(net, NoBiasGradientCheckTests.DEFAULT_EPS, NoBiasGradientCheckTests.DEFAULT_MAX_REL_ERROR, NoBiasGradientCheckTests.DEFAULT_MIN_ABS_ERROR, NoBiasGradientCheckTests.PRINT_RESULTS, NoBiasGradientCheckTests.RETURN_ON_FIRST_FAILURE, input, labels);
                Assert.assertTrue(msg, gradOK);
                TestUtils.testModelSerialization(net);
            }
        }
    }
}

