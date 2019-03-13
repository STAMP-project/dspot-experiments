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
import ConvolutionMode.Same;
import DataType.DOUBLE;
import LossFunctions.LossFunction;
import SubsamplingLayer.PoolingType;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.convolutional.Cropping1D;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.NoOp;

import static PoolingType.AVG;


@Slf4j
public class CNN1DGradientCheckTest extends BaseDL4JTest {
    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-6;

    private static final double DEFAULT_MAX_REL_ERROR = 0.001;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-8;

    static {
        Nd4j.setDataType(DOUBLE);
    }

    @Test
    public void testCnn1DWithLocallyConnected1D() {
        Nd4j.getRandom().setSeed(1337);
        int[] minibatchSizes = new int[]{ 2, 3 };
        int length = 7;
        int convNIn = 2;
        int convNOut1 = 3;
        int convNOut2 = 4;
        int finalNOut = 4;
        int[] kernels = new int[]{ 1 };
        int stride = 1;
        int padding = 0;
        Activation[] activations = new Activation[]{ Activation.SIGMOID };
        for (Activation afn : activations) {
            for (int minibatchSize : minibatchSizes) {
                for (int kernel : kernels) {
                    INDArray input = Nd4j.rand(new int[]{ minibatchSize, convNIn, length });
                    INDArray labels = Nd4j.zeros(minibatchSize, finalNOut, length);
                    for (int i = 0; i < minibatchSize; i++) {
                        for (int j = 0; j < length; j++) {
                            labels.putScalar(new int[]{ i, i % finalNOut, j }, 1.0);
                        }
                    }
                    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).dist(new NormalDistribution(0, 1)).convolutionMode(Same).list().layer(kernelSize(kernel).stride(stride).padding(padding).nIn(convNIn).nOut(convNOut1).build()).layer(kernelSize(kernel).stride(stride).padding(padding).nIn(convNOut1).nOut(convNOut2).hasBias(false).build()).layer(new RnnOutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(finalNOut).build()).setInputType(InputType.recurrent(convNIn, length)).build();
                    String json = conf.toJson();
                    MultiLayerConfiguration c2 = MultiLayerConfiguration.fromJson(json);
                    Assert.assertEquals(conf, c2);
                    MultiLayerNetwork net = new MultiLayerNetwork(conf);
                    net.init();
                    String msg = (((("Minibatch=" + minibatchSize) + ", activationFn=") + afn) + ", kernel = ") + kernel;
                    if (CNN1DGradientCheckTest.PRINT_RESULTS) {
                        System.out.println(msg);
                        for (int j = 0; j < (net.getnLayers()); j++)
                            System.out.println(((("Layer " + j) + " # params: ") + (net.getLayer(j).numParams())));

                    }
                    boolean gradOK = GradientCheckUtil.checkGradients(net, CNN1DGradientCheckTest.DEFAULT_EPS, CNN1DGradientCheckTest.DEFAULT_MAX_REL_ERROR, CNN1DGradientCheckTest.DEFAULT_MIN_ABS_ERROR, CNN1DGradientCheckTest.PRINT_RESULTS, CNN1DGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels);
                    Assert.assertTrue(msg, gradOK);
                    TestUtils.testModelSerialization(net);
                }
            }
        }
    }

    @Test
    public void testCnn1DWithCropping1D() {
        Nd4j.getRandom().setSeed(1337);
        int[] minibatchSizes = new int[]{ 1, 3 };
        int length = 7;
        int convNIn = 2;
        int convNOut1 = 3;
        int convNOut2 = 4;
        int finalNOut = 4;
        int[] kernels = new int[]{ 1, 2, 4 };
        int stride = 1;
        int padding = 0;
        int cropping = 1;
        int croppedLength = length - (2 * cropping);
        Activation[] activations = new Activation[]{ Activation.SIGMOID };
        SubsamplingLayer[] poolingTypes = new SubsamplingLayer.PoolingType[]{ PoolingType.MAX, PoolingType.AVG, PoolingType.PNORM };
        for (Activation afn : activations) {
            for (SubsamplingLayer.PoolingType poolingType : poolingTypes) {
                for (int minibatchSize : minibatchSizes) {
                    for (int kernel : kernels) {
                        INDArray input = Nd4j.rand(new int[]{ minibatchSize, convNIn, length });
                        INDArray labels = Nd4j.zeros(minibatchSize, finalNOut, croppedLength);
                        for (int i = 0; i < minibatchSize; i++) {
                            for (int j = 0; j < croppedLength; j++) {
                                labels.putScalar(new int[]{ i, i % finalNOut, j }, 1.0);
                            }
                        }
                        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).dist(new NormalDistribution(0, 1)).convolutionMode(Same).list().layer(kernelSize(kernel).stride(stride).padding(padding).nIn(convNIn).nOut(convNOut1).build()).layer(new Cropping1D.Builder(cropping).build()).layer(kernelSize(kernel).stride(stride).padding(padding).nIn(convNOut1).nOut(convNOut2).build()).layer(new RnnOutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(finalNOut).build()).setInputType(InputType.recurrent(convNIn, length)).build();
                        String json = conf.toJson();
                        MultiLayerConfiguration c2 = MultiLayerConfiguration.fromJson(json);
                        Assert.assertEquals(conf, c2);
                        MultiLayerNetwork net = new MultiLayerNetwork(conf);
                        net.init();
                        String msg = (((((("PoolingType=" + poolingType) + ", minibatch=") + minibatchSize) + ", activationFn=") + afn) + ", kernel = ") + kernel;
                        if (CNN1DGradientCheckTest.PRINT_RESULTS) {
                            System.out.println(msg);
                            for (int j = 0; j < (net.getnLayers()); j++)
                                System.out.println(((("Layer " + j) + " # params: ") + (net.getLayer(j).numParams())));

                        }
                        boolean gradOK = GradientCheckUtil.checkGradients(net, CNN1DGradientCheckTest.DEFAULT_EPS, CNN1DGradientCheckTest.DEFAULT_MAX_REL_ERROR, CNN1DGradientCheckTest.DEFAULT_MIN_ABS_ERROR, CNN1DGradientCheckTest.PRINT_RESULTS, CNN1DGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels);
                        Assert.assertTrue(msg, gradOK);
                        TestUtils.testModelSerialization(net);
                    }
                }
            }
        }
    }

    @Test
    public void testCnn1DWithZeroPadding1D() {
        Nd4j.getRandom().setSeed(1337);
        int[] minibatchSizes = new int[]{ 1, 3 };
        int length = 7;
        int convNIn = 2;
        int convNOut1 = 3;
        int convNOut2 = 4;
        int finalNOut = 4;
        int[] kernels = new int[]{ 1, 2, 4 };
        int stride = 1;
        int pnorm = 2;
        int padding = 0;
        int zeroPadding = 2;
        int paddedLength = length + (2 * zeroPadding);
        Activation[] activations = new Activation[]{ Activation.SIGMOID };
        SubsamplingLayer[] poolingTypes = new SubsamplingLayer.PoolingType[]{ PoolingType.MAX, PoolingType.AVG, PoolingType.PNORM };
        for (Activation afn : activations) {
            for (SubsamplingLayer.PoolingType poolingType : poolingTypes) {
                for (int minibatchSize : minibatchSizes) {
                    for (int kernel : kernels) {
                        INDArray input = Nd4j.rand(new int[]{ minibatchSize, convNIn, length });
                        INDArray labels = Nd4j.zeros(minibatchSize, finalNOut, paddedLength);
                        for (int i = 0; i < minibatchSize; i++) {
                            for (int j = 0; j < paddedLength; j++) {
                                labels.putScalar(new int[]{ i, i % finalNOut, j }, 1.0);
                            }
                        }
                        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).dist(new NormalDistribution(0, 1)).convolutionMode(Same).list().layer(kernelSize(kernel).stride(stride).padding(padding).nIn(convNIn).nOut(convNOut1).build()).layer(new ZeroPadding1DLayer.Builder(zeroPadding).build()).layer(kernelSize(kernel).stride(stride).padding(padding).nIn(convNOut1).nOut(convNOut2).build()).layer(new ZeroPadding1DLayer.Builder(0).build()).layer(kernelSize(kernel).stride(stride).padding(padding).pnorm(pnorm).build()).layer(new RnnOutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(finalNOut).build()).setInputType(InputType.recurrent(convNIn, length)).build();
                        String json = conf.toJson();
                        MultiLayerConfiguration c2 = MultiLayerConfiguration.fromJson(json);
                        Assert.assertEquals(conf, c2);
                        MultiLayerNetwork net = new MultiLayerNetwork(conf);
                        net.init();
                        String msg = (((((("PoolingType=" + poolingType) + ", minibatch=") + minibatchSize) + ", activationFn=") + afn) + ", kernel = ") + kernel;
                        if (CNN1DGradientCheckTest.PRINT_RESULTS) {
                            System.out.println(msg);
                            for (int j = 0; j < (net.getnLayers()); j++)
                                System.out.println(((("Layer " + j) + " # params: ") + (net.getLayer(j).numParams())));

                        }
                        boolean gradOK = GradientCheckUtil.checkGradients(net, CNN1DGradientCheckTest.DEFAULT_EPS, CNN1DGradientCheckTest.DEFAULT_MAX_REL_ERROR, CNN1DGradientCheckTest.DEFAULT_MIN_ABS_ERROR, CNN1DGradientCheckTest.PRINT_RESULTS, CNN1DGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels);
                        Assert.assertTrue(msg, gradOK);
                        TestUtils.testModelSerialization(net);
                    }
                }
            }
        }
    }

    @Test
    public void testCnn1DWithSubsampling1D() {
        Nd4j.getRandom().setSeed(12345);
        int[] minibatchSizes = new int[]{ 1, 3 };
        int length = 7;
        int convNIn = 2;
        int convNOut1 = 3;
        int convNOut2 = 4;
        int finalNOut = 4;
        int[] kernels = new int[]{ 1, 2, 4 };
        int stride = 1;
        int padding = 0;
        int pnorm = 2;
        Activation[] activations = new Activation[]{ Activation.SIGMOID, Activation.TANH };
        SubsamplingLayer[] poolingTypes = new SubsamplingLayer.PoolingType[]{ PoolingType.MAX, PoolingType.AVG, PoolingType.PNORM };
        for (Activation afn : activations) {
            for (SubsamplingLayer.PoolingType poolingType : poolingTypes) {
                for (int minibatchSize : minibatchSizes) {
                    for (int kernel : kernels) {
                        INDArray input = Nd4j.rand(new int[]{ minibatchSize, convNIn, length });
                        INDArray labels = Nd4j.zeros(minibatchSize, finalNOut, length);
                        for (int i = 0; i < minibatchSize; i++) {
                            for (int j = 0; j < length; j++) {
                                labels.putScalar(new int[]{ i, i % finalNOut, j }, 1.0);
                            }
                        }
                        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).dist(new NormalDistribution(0, 1)).convolutionMode(Same).list().layer(0, kernelSize(kernel).stride(stride).padding(padding).nIn(convNIn).nOut(convNOut1).build()).layer(1, kernelSize(kernel).stride(stride).padding(padding).nIn(convNOut1).nOut(convNOut2).build()).layer(2, kernelSize(kernel).stride(stride).padding(padding).pnorm(pnorm).build()).layer(3, new RnnOutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(finalNOut).build()).setInputType(InputType.recurrent(convNIn, length)).build();
                        String json = conf.toJson();
                        MultiLayerConfiguration c2 = MultiLayerConfiguration.fromJson(json);
                        Assert.assertEquals(conf, c2);
                        MultiLayerNetwork net = new MultiLayerNetwork(conf);
                        net.init();
                        String msg = (((((("PoolingType=" + poolingType) + ", minibatch=") + minibatchSize) + ", activationFn=") + afn) + ", kernel = ") + kernel;
                        if (CNN1DGradientCheckTest.PRINT_RESULTS) {
                            System.out.println(msg);
                            for (int j = 0; j < (net.getnLayers()); j++)
                                System.out.println(((("Layer " + j) + " # params: ") + (net.getLayer(j).numParams())));

                        }
                        boolean gradOK = GradientCheckUtil.checkGradients(net, CNN1DGradientCheckTest.DEFAULT_EPS, CNN1DGradientCheckTest.DEFAULT_MAX_REL_ERROR, CNN1DGradientCheckTest.DEFAULT_MIN_ABS_ERROR, CNN1DGradientCheckTest.PRINT_RESULTS, CNN1DGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels);
                        Assert.assertTrue(msg, gradOK);
                        TestUtils.testModelSerialization(net);
                    }
                }
            }
        }
    }

    @Test
    public void testCnn1dWithMasking() {
        int length = 12;
        int convNIn = 2;
        int convNOut1 = 3;
        int convNOut2 = 4;
        int finalNOut = 3;
        int pnorm = 2;
        SubsamplingLayer[] poolingTypes = new SubsamplingLayer.PoolingType[]{ PoolingType.MAX, PoolingType.AVG };
        for (SubsamplingLayer.PoolingType poolingType : poolingTypes) {
            for (ConvolutionMode cm : new ConvolutionMode[]{ ConvolutionMode.Same, ConvolutionMode.Truncate }) {
                for (int stride : new int[]{ 1, 2 }) {
                    String s = (((cm + ", stride=") + stride) + ", pooling=") + poolingType;
                    log.info(("Starting test: " + s));
                    Nd4j.getRandom().setSeed(12345);
                    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).activation(TANH).dist(new NormalDistribution(0, 1)).convolutionMode(cm).seed(12345).list().layer(new Convolution1DLayer.Builder().kernelSize(2).stride(stride).nIn(convNIn).nOut(convNOut1).build()).layer(kernelSize(2).stride(stride).pnorm(pnorm).build()).layer(new Convolution1DLayer.Builder().kernelSize(2).stride(stride).nIn(convNOut1).nOut(convNOut2).build()).layer(new GlobalPoolingLayer(AVG)).layer(new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(finalNOut).build()).setInputType(InputType.recurrent(convNIn, length)).build();
                    MultiLayerNetwork net = new MultiLayerNetwork(conf);
                    net.init();
                    INDArray f = Nd4j.rand(new int[]{ 2, convNIn, length });
                    INDArray fm = Nd4j.create(2, length);
                    fm.get(NDArrayIndex.point(0), NDArrayIndex.all()).assign(1);
                    fm.get(NDArrayIndex.point(1), NDArrayIndex.interval(0, 6)).assign(1);
                    INDArray label = TestUtils.randomOneHot(2, finalNOut);
                    boolean gradOK = GradientCheckUtil.checkGradients(net, CNN1DGradientCheckTest.DEFAULT_EPS, CNN1DGradientCheckTest.DEFAULT_MAX_REL_ERROR, CNN1DGradientCheckTest.DEFAULT_MIN_ABS_ERROR, CNN1DGradientCheckTest.PRINT_RESULTS, CNN1DGradientCheckTest.RETURN_ON_FIRST_FAILURE, f, label, fm, null);
                    Assert.assertTrue(s, gradOK);
                    TestUtils.testModelSerialization(net);
                    // TODO also check that masked step values don't impact forward pass, score or gradients
                    DataSet ds = new DataSet(f, label, fm, null);
                    double scoreBefore = net.score(ds);
                    net.setInput(f);
                    net.setLabels(label);
                    net.setLayerMaskArrays(fm, null);
                    net.computeGradientAndScore();
                    INDArray gradBefore = net.getFlattenedGradients().dup();
                    f.putScalar(1, 0, 10, 10.0);
                    f.putScalar(1, 1, 11, 20.0);
                    double scoreAfter = net.score(ds);
                    net.setInput(f);
                    net.setLabels(label);
                    net.setLayerMaskArrays(fm, null);
                    net.computeGradientAndScore();
                    INDArray gradAfter = net.getFlattenedGradients().dup();
                    Assert.assertEquals(scoreBefore, scoreAfter, 1.0E-6);
                    Assert.assertEquals(gradBefore, gradAfter);
                }
            }
        }
    }
}

