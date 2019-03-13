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
import Convolution3D.DataFormat;
import Convolution3D.DataFormat.NCDHW;
import DataType.DOUBLE;
import LossFunctions.LossFunction;
import Subsampling3DLayer.PoolingType;
import WeightInit.LECUN_NORMAL;
import WeightInit.XAVIER;
import java.util.Arrays;
import lombok.extern.java.Log;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.convolutional.Cropping3D;
import org.deeplearning4j.nn.conf.preprocessor.Cnn3DToFeedForwardPreProcessor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.NoOp;


@Log
public class CNN3DGradientCheckTest extends BaseDL4JTest {
    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-6;

    private static final double DEFAULT_MAX_REL_ERROR = 0.001;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-8;

    static {
        Nd4j.setDataType(DOUBLE);
    }

    @Test
    public void testCnn3DPlain() {
        Nd4j.getRandom().setSeed(1337);
        // Note: we checked this with a variety of parameters, but it takes a lot of time.
        int[] depths = new int[]{ 6 };
        int[] heights = new int[]{ 6 };
        int[] widths = new int[]{ 6 };
        int[] minibatchSizes = new int[]{ 3 };
        int convNIn = 2;
        int convNOut1 = 3;
        int convNOut2 = 4;
        int denseNOut = 5;
        int finalNOut = 42;
        int[][] kernels = new int[][]{ new int[]{ 2, 2, 2 } };
        int[][] strides = new int[][]{ new int[]{ 1, 1, 1 } };
        Activation[] activations = new Activation[]{ Activation.SIGMOID };
        ConvolutionMode[] modes = new ConvolutionMode[]{ ConvolutionMode.Truncate, ConvolutionMode.Same };
        for (Activation afn : activations) {
            for (int miniBatchSize : minibatchSizes) {
                for (int depth : depths) {
                    for (int height : heights) {
                        for (int width : widths) {
                            for (ConvolutionMode mode : modes) {
                                for (int[] kernel : kernels) {
                                    for (int[] stride : strides) {
                                        for (Convolution3D.DataFormat df : DataFormat.values()) {
                                            int outDepth = (mode == (ConvolutionMode.Same)) ? depth / (stride[0]) : ((depth - (kernel[0])) / (stride[0])) + 1;
                                            int outHeight = (mode == (ConvolutionMode.Same)) ? height / (stride[1]) : ((height - (kernel[1])) / (stride[1])) + 1;
                                            int outWidth = (mode == (ConvolutionMode.Same)) ? width / (stride[2]) : ((width - (kernel[2])) / (stride[2])) + 1;
                                            INDArray input;
                                            if (df == (DataFormat.NDHWC)) {
                                                input = Nd4j.rand(new int[]{ miniBatchSize, depth, height, width, convNIn });
                                            } else {
                                                input = Nd4j.rand(new int[]{ miniBatchSize, convNIn, depth, height, width });
                                            }
                                            INDArray labels = Nd4j.zeros(miniBatchSize, finalNOut);
                                            for (int i = 0; i < miniBatchSize; i++) {
                                                labels.putScalar(new int[]{ i, i % finalNOut }, 1.0);
                                            }
                                            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).weightInit(LECUN_NORMAL).dist(new NormalDistribution(0, 1)).list().layer(0, new Convolution3D.Builder().activation(afn).kernelSize(kernel).stride(stride).nIn(convNIn).nOut(convNOut1).hasBias(false).convolutionMode(mode).dataFormat(df).build()).layer(1, new Convolution3D.Builder().activation(afn).kernelSize(1, 1, 1).nIn(convNOut1).nOut(convNOut2).hasBias(false).convolutionMode(mode).dataFormat(df).build()).layer(2, new DenseLayer.Builder().nOut(denseNOut).build()).layer(new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(finalNOut).build()).inputPreProcessor(2, new Cnn3DToFeedForwardPreProcessor(outDepth, outHeight, outWidth, convNOut2, (df == (DataFormat.NCDHW)))).setInputType(InputType.convolutional3D(df, depth, height, width, convNIn)).build();
                                            String json = conf.toJson();
                                            MultiLayerConfiguration c2 = MultiLayerConfiguration.fromJson(json);
                                            Assert.assertEquals(conf, c2);
                                            MultiLayerNetwork net = new MultiLayerNetwork(conf);
                                            net.init();
                                            String msg = (((((((((((((((("DataFormat = " + df) + ", minibatch size = ") + miniBatchSize) + ", activationFn=") + afn) + ", kernel = ") + (Arrays.toString(kernel))) + ", stride = ") + (Arrays.toString(stride))) + ", mode = ") + (mode.toString())) + ", input depth ") + depth) + ", input height ") + height) + ", input width ") + width;
                                            if (CNN3DGradientCheckTest.PRINT_RESULTS) {
                                                log.info(msg);
                                                for (int j = 0; j < (net.getnLayers()); j++) {
                                                    log.info(((("Layer " + j) + " # params: ") + (net.getLayer(j).numParams())));
                                                }
                                            }
                                            boolean gradOK = GradientCheckUtil.checkGradients(net, CNN3DGradientCheckTest.DEFAULT_EPS, CNN3DGradientCheckTest.DEFAULT_MAX_REL_ERROR, CNN3DGradientCheckTest.DEFAULT_MIN_ABS_ERROR, CNN3DGradientCheckTest.PRINT_RESULTS, CNN3DGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels);
                                            Assert.assertTrue(msg, gradOK);
                                            TestUtils.testModelSerialization(net);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testCnn3DZeroPadding() {
        Nd4j.getRandom().setSeed(42);
        int depth = 4;
        int height = 4;
        int width = 4;
        int[] minibatchSizes = new int[]{ 3 };
        int convNIn = 2;
        int convNOut1 = 3;
        int convNOut2 = 4;
        int denseNOut = 5;
        int finalNOut = 42;
        int[] kernel = new int[]{ 2, 2, 2 };
        int[] zeroPadding = new int[]{ 1, 1, 2, 2, 3, 3 };
        Activation[] activations = new Activation[]{ Activation.SIGMOID };
        ConvolutionMode[] modes = new ConvolutionMode[]{ ConvolutionMode.Truncate, ConvolutionMode.Same };
        for (Activation afn : activations) {
            for (int miniBatchSize : minibatchSizes) {
                for (ConvolutionMode mode : modes) {
                    int outDepth = (mode == (ConvolutionMode.Same)) ? depth : (depth - (kernel[0])) + 1;
                    int outHeight = (mode == (ConvolutionMode.Same)) ? height : (height - (kernel[1])) + 1;
                    int outWidth = (mode == (ConvolutionMode.Same)) ? width : (width - (kernel[2])) + 1;
                    outDepth += (zeroPadding[0]) + (zeroPadding[1]);
                    outHeight += (zeroPadding[2]) + (zeroPadding[3]);
                    outWidth += (zeroPadding[4]) + (zeroPadding[5]);
                    INDArray input = Nd4j.rand(new int[]{ miniBatchSize, convNIn, depth, height, width });
                    INDArray labels = Nd4j.zeros(miniBatchSize, finalNOut);
                    for (int i = 0; i < miniBatchSize; i++) {
                        labels.putScalar(new int[]{ i, i % finalNOut }, 1.0);
                    }
                    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).weightInit(LECUN_NORMAL).dist(new NormalDistribution(0, 1)).list().layer(0, new Convolution3D.Builder().activation(afn).kernelSize(kernel).nIn(convNIn).nOut(convNOut1).hasBias(false).convolutionMode(mode).dataFormat(NCDHW).build()).layer(1, new Convolution3D.Builder().activation(afn).kernelSize(1, 1, 1).nIn(convNOut1).nOut(convNOut2).hasBias(false).convolutionMode(mode).dataFormat(NCDHW).build()).layer(2, new ZeroPadding3DLayer.Builder(zeroPadding).build()).layer(3, new DenseLayer.Builder().nOut(denseNOut).build()).layer(new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(finalNOut).build()).inputPreProcessor(3, new Cnn3DToFeedForwardPreProcessor(outDepth, outHeight, outWidth, convNOut2, true)).setInputType(InputType.convolutional3D(depth, height, width, convNIn)).build();
                    String json = conf.toJson();
                    MultiLayerConfiguration c2 = MultiLayerConfiguration.fromJson(json);
                    Assert.assertEquals(conf, c2);
                    MultiLayerNetwork net = new MultiLayerNetwork(conf);
                    net.init();
                    String msg = (((((((((((("Minibatch size = " + miniBatchSize) + ", activationFn=") + afn) + ", kernel = ") + (Arrays.toString(kernel))) + ", mode = ") + (mode.toString())) + ", input depth ") + depth) + ", input height ") + height) + ", input width ") + width;
                    if (CNN3DGradientCheckTest.PRINT_RESULTS) {
                        log.info(msg);
                        for (int j = 0; j < (net.getnLayers()); j++) {
                            log.info(((("Layer " + j) + " # params: ") + (net.getLayer(j).numParams())));
                        }
                    }
                    boolean gradOK = GradientCheckUtil.checkGradients(net, CNN3DGradientCheckTest.DEFAULT_EPS, CNN3DGradientCheckTest.DEFAULT_MAX_REL_ERROR, CNN3DGradientCheckTest.DEFAULT_MIN_ABS_ERROR, CNN3DGradientCheckTest.PRINT_RESULTS, CNN3DGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels);
                    Assert.assertTrue(msg, gradOK);
                    TestUtils.testModelSerialization(net);
                }
            }
        }
    }

    @Test
    public void testCnn3DPooling() {
        Nd4j.getRandom().setSeed(42);
        int depth = 4;
        int height = 4;
        int width = 4;
        int[] minibatchSizes = new int[]{ 3 };
        int convNIn = 2;
        int convNOut = 4;
        int denseNOut = 5;
        int finalNOut = 42;
        int[] kernel = new int[]{ 2, 2, 2 };
        Activation[] activations = new Activation[]{ Activation.SIGMOID };
        Subsampling3DLayer[] poolModes = new PoolingType[]{ PoolingType.AVG };
        ConvolutionMode[] modes = new ConvolutionMode[]{ ConvolutionMode.Truncate };
        for (Activation afn : activations) {
            for (int miniBatchSize : minibatchSizes) {
                for (Subsampling3DLayer.PoolingType pool : poolModes) {
                    for (ConvolutionMode mode : modes) {
                        for (Convolution3D.DataFormat df : DataFormat.values()) {
                            int outDepth = depth / (kernel[0]);
                            int outHeight = height / (kernel[1]);
                            int outWidth = width / (kernel[2]);
                            INDArray input = Nd4j.rand((df == (DataFormat.NCDHW) ? new int[]{ miniBatchSize, convNIn, depth, height, width } : new int[]{ miniBatchSize, depth, height, width, convNIn }));
                            INDArray labels = Nd4j.zeros(miniBatchSize, finalNOut);
                            for (int i = 0; i < miniBatchSize; i++) {
                                labels.putScalar(new int[]{ i, i % finalNOut }, 1.0);
                            }
                            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).weightInit(XAVIER).dist(new NormalDistribution(0, 1)).list().layer(0, new Convolution3D.Builder().activation(afn).kernelSize(1, 1, 1).nIn(convNIn).nOut(convNOut).hasBias(false).convolutionMode(mode).dataFormat(df).build()).layer(1, new Subsampling3DLayer.Builder(kernel).poolingType(pool).convolutionMode(mode).dataFormat(df).build()).layer(2, new DenseLayer.Builder().nOut(denseNOut).build()).layer(new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(finalNOut).build()).inputPreProcessor(2, new Cnn3DToFeedForwardPreProcessor(outDepth, outHeight, outWidth, convNOut, df)).setInputType(InputType.convolutional3D(df, depth, height, width, convNIn)).build();
                            String json = conf.toJson();
                            MultiLayerConfiguration c2 = MultiLayerConfiguration.fromJson(json);
                            Assert.assertEquals(conf, c2);
                            MultiLayerNetwork net = new MultiLayerNetwork(conf);
                            net.init();
                            String msg = (((((((((((((("Minibatch size = " + miniBatchSize) + ", activationFn=") + afn) + ", kernel = ") + (Arrays.toString(kernel))) + ", mode = ") + (mode.toString())) + ", input depth ") + depth) + ", input height ") + height) + ", input width ") + width) + ", dataFormat=") + df;
                            if (CNN3DGradientCheckTest.PRINT_RESULTS) {
                                log.info(msg);
                                for (int j = 0; j < (net.getnLayers()); j++) {
                                    log.info(((("Layer " + j) + " # params: ") + (net.getLayer(j).numParams())));
                                }
                            }
                            boolean gradOK = GradientCheckUtil.checkGradients(net, CNN3DGradientCheckTest.DEFAULT_EPS, CNN3DGradientCheckTest.DEFAULT_MAX_REL_ERROR, CNN3DGradientCheckTest.DEFAULT_MIN_ABS_ERROR, CNN3DGradientCheckTest.PRINT_RESULTS, CNN3DGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels);
                            Assert.assertTrue(msg, gradOK);
                            TestUtils.testModelSerialization(net);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testCnn3DUpsampling() {
        Nd4j.getRandom().setSeed(42);
        int depth = 2;
        int height = 2;
        int width = 2;
        int[] minibatchSizes = new int[]{ 3 };
        int convNIn = 2;
        int convNOut = 4;
        int denseNOut = 5;
        int finalNOut = 42;
        int[] upsamplingSize = new int[]{ 2, 2, 2 };
        Activation[] activations = new Activation[]{ Activation.SIGMOID };
        ConvolutionMode[] modes = new ConvolutionMode[]{ ConvolutionMode.Truncate };
        for (Activation afn : activations) {
            for (int miniBatchSize : minibatchSizes) {
                for (ConvolutionMode mode : modes) {
                    int outDepth = depth * (upsamplingSize[0]);
                    int outHeight = height * (upsamplingSize[1]);
                    int outWidth = width * (upsamplingSize[2]);
                    INDArray input = Nd4j.rand(new int[]{ miniBatchSize, convNIn, depth, height, width });
                    INDArray labels = Nd4j.zeros(miniBatchSize, finalNOut);
                    for (int i = 0; i < miniBatchSize; i++) {
                        labels.putScalar(new int[]{ i, i % finalNOut }, 1.0);
                    }
                    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).weightInit(LECUN_NORMAL).dist(new NormalDistribution(0, 1)).list().layer(0, new Convolution3D.Builder().activation(afn).kernelSize(1, 1, 1).nIn(convNIn).nOut(convNOut).hasBias(false).convolutionMode(mode).dataFormat(NCDHW).build()).layer(1, new Upsampling3D.Builder(upsamplingSize[0]).build()).layer(2, new DenseLayer.Builder().nOut(denseNOut).build()).layer(new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(finalNOut).build()).inputPreProcessor(2, new Cnn3DToFeedForwardPreProcessor(outDepth, outHeight, outWidth, convNOut, true)).setInputType(InputType.convolutional3D(depth, height, width, convNIn)).build();
                    String json = conf.toJson();
                    MultiLayerConfiguration c2 = MultiLayerConfiguration.fromJson(json);
                    Assert.assertEquals(conf, c2);
                    MultiLayerNetwork net = new MultiLayerNetwork(conf);
                    net.init();
                    String msg = (((((((((((("Minibatch size = " + miniBatchSize) + ", activationFn=") + afn) + ", kernel = ") + (Arrays.toString(upsamplingSize))) + ", mode = ") + (mode.toString())) + ", input depth ") + depth) + ", input height ") + height) + ", input width ") + width;
                    if (CNN3DGradientCheckTest.PRINT_RESULTS) {
                        log.info(msg);
                        for (int j = 0; j < (net.getnLayers()); j++) {
                            log.info(((("Layer " + j) + " # params: ") + (net.getLayer(j).numParams())));
                        }
                    }
                    boolean gradOK = GradientCheckUtil.checkGradients(net, CNN3DGradientCheckTest.DEFAULT_EPS, CNN3DGradientCheckTest.DEFAULT_MAX_REL_ERROR, CNN3DGradientCheckTest.DEFAULT_MIN_ABS_ERROR, CNN3DGradientCheckTest.PRINT_RESULTS, CNN3DGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels);
                    Assert.assertTrue(msg, gradOK);
                    TestUtils.testModelSerialization(net);
                }
            }
        }
    }

    @Test
    public void testCnn3DCropping() {
        Nd4j.getRandom().setSeed(42);
        int depth = 6;
        int height = 6;
        int width = 6;
        int[] minibatchSizes = new int[]{ 3 };
        int convNIn = 2;
        int convNOut1 = 3;
        int convNOut2 = 4;
        int denseNOut = 5;
        int finalNOut = 8;
        int[] kernel = new int[]{ 1, 1, 1 };
        int[] cropping = new int[]{ 0, 0, 1, 1, 2, 2 };
        Activation[] activations = new Activation[]{ Activation.SIGMOID };
        ConvolutionMode[] modes = new ConvolutionMode[]{ ConvolutionMode.Same };
        for (Activation afn : activations) {
            for (int miniBatchSize : minibatchSizes) {
                for (ConvolutionMode mode : modes) {
                    int outDepth = (mode == (ConvolutionMode.Same)) ? depth : (depth - (kernel[0])) + 1;
                    int outHeight = (mode == (ConvolutionMode.Same)) ? height : (height - (kernel[1])) + 1;
                    int outWidth = (mode == (ConvolutionMode.Same)) ? width : (width - (kernel[2])) + 1;
                    outDepth -= (cropping[0]) + (cropping[1]);
                    outHeight -= (cropping[2]) + (cropping[3]);
                    outWidth -= (cropping[4]) + (cropping[5]);
                    INDArray input = Nd4j.rand(new int[]{ miniBatchSize, convNIn, depth, height, width });
                    INDArray labels = Nd4j.zeros(miniBatchSize, finalNOut);
                    for (int i = 0; i < miniBatchSize; i++) {
                        labels.putScalar(new int[]{ i, i % finalNOut }, 1.0);
                    }
                    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).weightInit(LECUN_NORMAL).dist(new NormalDistribution(0, 1)).list().layer(0, new Convolution3D.Builder().activation(afn).kernelSize(kernel).nIn(convNIn).nOut(convNOut1).hasBias(false).convolutionMode(mode).dataFormat(NCDHW).build()).layer(1, new Convolution3D.Builder().activation(afn).kernelSize(1, 1, 1).nIn(convNOut1).nOut(convNOut2).hasBias(false).convolutionMode(mode).dataFormat(NCDHW).build()).layer(2, new Cropping3D.Builder(cropping).build()).layer(3, new DenseLayer.Builder().nOut(denseNOut).build()).layer(new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(finalNOut).build()).inputPreProcessor(3, new Cnn3DToFeedForwardPreProcessor(outDepth, outHeight, outWidth, convNOut2, true)).setInputType(InputType.convolutional3D(depth, height, width, convNIn)).build();
                    String json = conf.toJson();
                    MultiLayerConfiguration c2 = MultiLayerConfiguration.fromJson(json);
                    Assert.assertEquals(conf, c2);
                    MultiLayerNetwork net = new MultiLayerNetwork(conf);
                    net.init();
                    String msg = (((((((((((("Minibatch size = " + miniBatchSize) + ", activationFn=") + afn) + ", kernel = ") + (Arrays.toString(kernel))) + ", mode = ") + (mode.toString())) + ", input depth ") + depth) + ", input height ") + height) + ", input width ") + width;
                    if (CNN3DGradientCheckTest.PRINT_RESULTS) {
                        log.info(msg);
                        for (int j = 0; j < (net.getnLayers()); j++) {
                            log.info(((("Layer " + j) + " # params: ") + (net.getLayer(j).numParams())));
                        }
                    }
                    boolean gradOK = GradientCheckUtil.checkGradients(net, CNN3DGradientCheckTest.DEFAULT_EPS, CNN3DGradientCheckTest.DEFAULT_MAX_REL_ERROR, CNN3DGradientCheckTest.DEFAULT_MIN_ABS_ERROR, CNN3DGradientCheckTest.PRINT_RESULTS, CNN3DGradientCheckTest.RETURN_ON_FIRST_FAILURE, input, labels);
                    Assert.assertTrue(msg, gradOK);
                    TestUtils.testModelSerialization(net);
                }
            }
        }
    }
}

