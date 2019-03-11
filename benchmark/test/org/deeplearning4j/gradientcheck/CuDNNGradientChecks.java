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


import Activation.IDENTITY;
import Activation.SIGMOID;
import Activation.SOFTMAX;
import Activation.TANH;
import ConvolutionMode.Same;
import DataType.DOUBLE;
import LossFunctions.LossFunction;
import LossFunctions.LossFunction.MCXENT;
import MultiLayerConfiguration.Builder;
import NeuralNetConfiguration.ListBuilder;
import OptimizationAlgorithm.CONJUGATE_GRADIENT;
import SubsamplingLayer.PoolingType.MAX;
import WeightInit.XAVIER;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.dropout.Dropout;
import org.deeplearning4j.nn.conf.dropout.IDropout;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.layers.convolution.ConvolutionHelper;
import org.deeplearning4j.nn.layers.convolution.ConvolutionLayer;
import org.deeplearning4j.nn.layers.convolution.CudnnConvolutionHelper;
import org.deeplearning4j.nn.layers.convolution.subsampling.SubsamplingHelper;
import org.deeplearning4j.nn.layers.convolution.subsampling.SubsamplingLayer;
import org.deeplearning4j.nn.layers.dropout.CudnnDropoutHelper;
import org.deeplearning4j.nn.layers.normalization.BatchNormalizationHelper;
import org.deeplearning4j.nn.layers.normalization.CudnnBatchNormalizationHelper;
import org.deeplearning4j.nn.layers.normalization.CudnnLocalResponseNormalizationHelper;
import org.deeplearning4j.nn.layers.normalization.LocalResponseNormalizationHelper;
import org.deeplearning4j.nn.layers.recurrent.CudnnLSTMHelper;
import org.deeplearning4j.nn.layers.recurrent.LSTM;
import org.deeplearning4j.nn.layers.recurrent.LSTMHelper;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.function.Consumer;
import org.nd4j.linalg.learning.config.NoOp;


/**
 * Created by Alex on 09/09/2016.
 */
@Slf4j
public class CuDNNGradientChecks extends BaseDL4JTest {
    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-5;

    private static final double DEFAULT_MAX_REL_ERROR = 0.01;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-6;

    static {
        DataTypeUtil.setDTypeForContext(DOUBLE);
    }

    @Test
    public void testConvolutional() throws Exception {
        // Parameterized test, testing combinations of:
        // (a) activation function
        // (b) Whether to test at random initialization, or after some learning (i.e., 'characteristic mode of operation')
        // (c) Loss function (with specified output activations)
        Activation[] activFns = new Activation[]{ Activation.SIGMOID, Activation.TANH };
        boolean[] characteristic = new boolean[]{ false, true };// If true: run some backprop steps first

        int[] minibatchSizes = new int[]{ 1, 4 };
        int width = 6;
        int height = 6;
        int inputDepth = 2;
        int nOut = 3;
        Field f = ConvolutionLayer.class.getDeclaredField("helper");
        f.setAccessible(true);
        Random r = new Random(12345);
        for (Activation afn : activFns) {
            for (boolean doLearningFirst : characteristic) {
                for (int minibatchSize : minibatchSizes) {
                    INDArray input = Nd4j.rand(new int[]{ minibatchSize, inputDepth, height, width });
                    INDArray labels = Nd4j.zeros(minibatchSize, nOut);
                    for (int i = 0; i < minibatchSize; i++) {
                        labels.putScalar(i, r.nextInt(nOut), 1.0);
                    }
                    MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().optimizationAlgo(CONJUGATE_GRADIENT).dist(new UniformDistribution((-1), 1)).updater(new NoOp()).seed(12345L).list().layer(0, new ConvolutionLayer.Builder(2, 2).stride(2, 2).padding(1, 1).nOut(3).activation(afn).build()).layer(1, new ConvolutionLayer.Builder(2, 2).stride(2, 2).padding(0, 0).nOut(3).activation(afn).build()).layer(2, new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(nOut).build()).setInputType(InputType.convolutional(height, width, inputDepth));
                    MultiLayerConfiguration conf = builder.build();
                    MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                    mln.init();
                    ConvolutionLayer c0 = ((ConvolutionLayer) (mln.getLayer(0)));
                    ConvolutionHelper ch0 = ((ConvolutionHelper) (f.get(c0)));
                    Assert.assertTrue((ch0 instanceof CudnnConvolutionHelper));
                    ConvolutionLayer c1 = ((ConvolutionLayer) (mln.getLayer(1)));
                    ConvolutionHelper ch1 = ((ConvolutionHelper) (f.get(c1)));
                    Assert.assertTrue((ch1 instanceof CudnnConvolutionHelper));
                    // -------------------------------
                    // For debugging/comparison to no-cudnn case: set helper field to null
                    // f.set(c0, null);
                    // f.set(c1, null);
                    // assertNull(f.get(c0));
                    // assertNull(f.get(c1));
                    // -------------------------------
                    String name = new Object() {}.getClass().getEnclosingMethod().getName();
                    if (doLearningFirst) {
                        // Run a number of iterations of learning
                        mln.setInput(input);
                        mln.setLabels(labels);
                        mln.computeGradientAndScore();
                        double scoreBefore = mln.score();
                        for (int j = 0; j < 10; j++)
                            mln.fit(input, labels);

                        mln.computeGradientAndScore();
                        double scoreAfter = mln.score();
                        // Can't test in 'characteristic mode of operation' if not learning
                        String msg = ((((((((name + " - score did not (sufficiently) decrease during learning - activationFn=") + afn) + ", doLearningFirst= ") + doLearningFirst) + " (before=") + scoreBefore) + ", scoreAfter=") + scoreAfter) + ")";
                        Assert.assertTrue(msg, (scoreAfter < (0.8 * scoreBefore)));
                    }
                    if (CuDNNGradientChecks.PRINT_RESULTS) {
                        System.out.println(((((name + " - activationFn=") + afn) + ", doLearningFirst=") + doLearningFirst));
                        for (int j = 0; j < (mln.getnLayers()); j++)
                            System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

                    }
                    boolean gradOK = GradientCheckUtil.checkGradients(mln, CuDNNGradientChecks.DEFAULT_EPS, CuDNNGradientChecks.DEFAULT_MAX_REL_ERROR, CuDNNGradientChecks.DEFAULT_MIN_ABS_ERROR, CuDNNGradientChecks.PRINT_RESULTS, CuDNNGradientChecks.RETURN_ON_FIRST_FAILURE, input, labels);
                    Assert.assertTrue(gradOK);
                }
            }
        }
    }

    @Test
    public void testConvolutionalNoBias() throws Exception {
        int[] minibatchSizes = new int[]{ 1, 4 };
        int width = 6;
        int height = 6;
        int inputDepth = 2;
        int nOut = 3;
        Field f = ConvolutionLayer.class.getDeclaredField("helper");
        f.setAccessible(true);
        Random r = new Random(12345);
        for (int minibatchSize : minibatchSizes) {
            for (boolean convHasBias : new boolean[]{ true, false }) {
                INDArray input = Nd4j.rand(new int[]{ minibatchSize, inputDepth, height, width });
                INDArray labels = Nd4j.zeros(minibatchSize, nOut);
                for (int i = 0; i < minibatchSize; i++) {
                    labels.putScalar(i, r.nextInt(nOut), 1.0);
                }
                MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().dist(new UniformDistribution((-1), 1)).updater(new NoOp()).seed(12345L).list().layer(0, new ConvolutionLayer.Builder(2, 2).stride(2, 2).padding(1, 1).nOut(3).hasBias(convHasBias).activation(TANH).build()).layer(1, new ConvolutionLayer.Builder(2, 2).stride(2, 2).padding(0, 0).nOut(3).hasBias(convHasBias).activation(TANH).build()).layer(2, new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(nOut).build()).setInputType(InputType.convolutional(height, width, inputDepth));
                MultiLayerConfiguration conf = builder.build();
                MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                mln.init();
                ConvolutionLayer c0 = ((ConvolutionLayer) (mln.getLayer(0)));
                ConvolutionHelper ch0 = ((ConvolutionHelper) (f.get(c0)));
                Assert.assertTrue((ch0 instanceof CudnnConvolutionHelper));
                ConvolutionLayer c1 = ((ConvolutionLayer) (mln.getLayer(1)));
                ConvolutionHelper ch1 = ((ConvolutionHelper) (f.get(c1)));
                Assert.assertTrue((ch1 instanceof CudnnConvolutionHelper));
                String name = ((((new Object() {}.getClass().getEnclosingMethod().getName()) + ", minibatch = ") + minibatchSize) + ", convHasBias = ") + convHasBias;
                if (CuDNNGradientChecks.PRINT_RESULTS) {
                    System.out.println(name);
                    for (int j = 0; j < (mln.getnLayers()); j++)
                        System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

                }
                boolean gradOK = GradientCheckUtil.checkGradients(mln, CuDNNGradientChecks.DEFAULT_EPS, CuDNNGradientChecks.DEFAULT_MAX_REL_ERROR, CuDNNGradientChecks.DEFAULT_MIN_ABS_ERROR, CuDNNGradientChecks.PRINT_RESULTS, CuDNNGradientChecks.RETURN_ON_FIRST_FAILURE, input, labels);
                Assert.assertTrue(name, gradOK);
            }
        }
    }

    @Test
    public void testBatchNormCnn() throws Exception {
        // Note: CuDNN batch norm supports 4d only, as per 5.1 (according to api reference documentation)
        Nd4j.getRandom().setSeed(12345);
        int minibatch = 10;
        int depth = 1;
        int hw = 4;
        int nOut = 4;
        INDArray input = Nd4j.rand(new int[]{ minibatch, depth, hw, hw });
        INDArray labels = Nd4j.zeros(minibatch, nOut);
        Random r = new Random(12345);
        for (int i = 0; i < minibatch; i++) {
            labels.putScalar(i, r.nextInt(nOut), 1.0);
        }
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().updater(new NoOp()).seed(12345L).dist(new NormalDistribution(0, 2)).list().layer(0, new ConvolutionLayer.Builder().kernelSize(2, 2).stride(1, 1).nIn(depth).nOut(2).activation(IDENTITY).build()).layer(1, new BatchNormalization.Builder().build()).layer(2, new ActivationLayer.Builder().activation(TANH).build()).layer(3, new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(nOut).build()).setInputType(InputType.convolutional(hw, hw, depth));
        MultiLayerNetwork mln = new MultiLayerNetwork(builder.build());
        mln.init();
        Field f = org.deeplearning4j.nn.layers.normalization.BatchNormalization.class.getDeclaredField("helper");
        f.setAccessible(true);
        org.deeplearning4j.nn.layers.normalization.BatchNormalization b = ((org.deeplearning4j.nn.layers.normalization.BatchNormalization) (mln.getLayer(1)));
        BatchNormalizationHelper bn = ((BatchNormalizationHelper) (f.get(b)));
        Assert.assertTrue((bn instanceof CudnnBatchNormalizationHelper));
        // -------------------------------
        // For debugging/comparison to no-cudnn case: set helper field to null
        // f.set(b, null);
        // assertNull(f.get(b));
        // -------------------------------
        if (CuDNNGradientChecks.PRINT_RESULTS) {
            for (int j = 0; j < (mln.getnLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

        }
        // Mean and variance vars are not gradient checkable; mean/variance "gradient" is used to implement running mean/variance calc
        // i.e., runningMean = decay * runningMean + (1-decay) * batchMean
        // However, numerical gradient will be 0 as forward pass doesn't depend on this "parameter"
        Set<String> excludeParams = new HashSet<>(Arrays.asList("1_mean", "1_var", "1_log10stdev"));
        boolean gradOK = GradientCheckUtil.checkGradients(mln, CuDNNGradientChecks.DEFAULT_EPS, CuDNNGradientChecks.DEFAULT_MAX_REL_ERROR, CuDNNGradientChecks.DEFAULT_MIN_ABS_ERROR, CuDNNGradientChecks.PRINT_RESULTS, CuDNNGradientChecks.RETURN_ON_FIRST_FAILURE, input, labels, excludeParams);
        Assert.assertTrue(gradOK);
    }

    @Test
    public void testLRN() throws Exception {
        Nd4j.getRandom().setSeed(12345);
        int minibatch = 10;
        int depth = 6;
        int hw = 5;
        int nOut = 4;
        INDArray input = Nd4j.rand(new int[]{ minibatch, depth, hw, hw });
        INDArray labels = Nd4j.zeros(minibatch, nOut);
        Random r = new Random(12345);
        for (int i = 0; i < minibatch; i++) {
            labels.putScalar(i, r.nextInt(nOut), 1.0);
        }
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().updater(new NoOp()).seed(12345L).dist(new NormalDistribution(0, 2)).list().layer(0, new ConvolutionLayer.Builder().nOut(6).kernelSize(2, 2).stride(1, 1).activation(TANH).build()).layer(1, new LocalResponseNormalization.Builder().build()).layer(2, new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(nOut).build()).setInputType(InputType.convolutional(hw, hw, depth));
        MultiLayerNetwork mln = new MultiLayerNetwork(builder.build());
        mln.init();
        Field f = org.deeplearning4j.nn.layers.normalization.LocalResponseNormalization.class.getDeclaredField("helper");
        f.setAccessible(true);
        org.deeplearning4j.nn.layers.normalization.LocalResponseNormalization l = ((org.deeplearning4j.nn.layers.normalization.LocalResponseNormalization) (mln.getLayer(1)));
        LocalResponseNormalizationHelper lrn = ((LocalResponseNormalizationHelper) (f.get(l)));
        Assert.assertTrue((lrn instanceof CudnnLocalResponseNormalizationHelper));
        // -------------------------------
        // For debugging/comparison to no-cudnn case: set helper field to null
        // f.set(l, null);
        // assertNull(f.get(l));
        // -------------------------------
        if (CuDNNGradientChecks.PRINT_RESULTS) {
            for (int j = 0; j < (mln.getnLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(mln, CuDNNGradientChecks.DEFAULT_EPS, CuDNNGradientChecks.DEFAULT_MAX_REL_ERROR, CuDNNGradientChecks.DEFAULT_MIN_ABS_ERROR, CuDNNGradientChecks.PRINT_RESULTS, CuDNNGradientChecks.RETURN_ON_FIRST_FAILURE, input, labels);
        Assert.assertTrue(gradOK);
    }

    @Test
    public void testLSTM() throws Exception {
        Nd4j.getRandom().setSeed(12345);
        int minibatch = 10;
        int inputSize = 8;
        int lstmLayerSize = 7;
        int timeSeriesLength = 6;
        int nOut = 4;
        INDArray input = Nd4j.rand(new int[]{ minibatch, inputSize, timeSeriesLength });
        INDArray labels = Nd4j.zeros(minibatch, nOut, timeSeriesLength);
        Random r = new Random(12345);
        for (int i = 0; i < minibatch; i++) {
            for (int j = 0; j < timeSeriesLength; j++) {
                labels.putScalar(i, r.nextInt(nOut), j, 1.0);
            }
        }
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().updater(new NoOp()).seed(12345L).dist(new NormalDistribution(0, 2)).list().layer(0, new LSTM.Builder().nIn(input.size(1)).nOut(lstmLayerSize).gateActivationFunction(SIGMOID).activation(TANH).build()).layer(1, new LSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize).gateActivationFunction(SIGMOID).activation(TANH).build()).layer(2, new RnnOutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nIn(lstmLayerSize).nOut(nOut).build());
        MultiLayerNetwork mln = new MultiLayerNetwork(builder.build());
        mln.init();
        Field f = LSTM.class.getDeclaredField("helper");
        f.setAccessible(true);
        LSTM l = ((LSTM) (mln.getLayer(1)));
        LSTMHelper helper = ((LSTMHelper) (f.get(l)));
        Assert.assertTrue((helper instanceof CudnnLSTMHelper));
        // -------------------------------
        // For debugging/comparison to no-cudnn case: set helper field to null
        // f.set(l, null);
        // assertNull(f.get(l));
        // -------------------------------
        if (CuDNNGradientChecks.PRINT_RESULTS) {
            for (int j = 0; j < (mln.getnLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(mln, CuDNNGradientChecks.DEFAULT_EPS, CuDNNGradientChecks.DEFAULT_MAX_REL_ERROR, CuDNNGradientChecks.DEFAULT_MIN_ABS_ERROR, CuDNNGradientChecks.PRINT_RESULTS, CuDNNGradientChecks.RETURN_ON_FIRST_FAILURE, input, labels);
        Assert.assertTrue(gradOK);
    }

    @Test
    public void testLSTM2() throws Exception {
        Nd4j.getRandom().setSeed(12345);
        int minibatch = 10;
        int inputSize = 3;
        int lstmLayerSize = 4;
        int timeSeriesLength = 3;
        int nOut = 2;
        INDArray input = Nd4j.rand(new int[]{ minibatch, inputSize, timeSeriesLength });
        INDArray labels = Nd4j.zeros(minibatch, nOut, timeSeriesLength);
        Random r = new Random(12345);
        for (int i = 0; i < minibatch; i++) {
            for (int j = 0; j < timeSeriesLength; j++) {
                labels.putScalar(i, r.nextInt(nOut), j, 1.0);
            }
        }
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().updater(new NoOp()).seed(12345L).dist(new NormalDistribution(0, 2)).list().layer(0, new LSTM.Builder().nIn(input.size(1)).nOut(lstmLayerSize).gateActivationFunction(SIGMOID).activation(TANH).build()).layer(1, new LSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize).gateActivationFunction(SIGMOID).activation(TANH).build()).layer(2, new RnnOutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nIn(lstmLayerSize).nOut(nOut).build());
        MultiLayerNetwork mln = new MultiLayerNetwork(builder.build());
        mln.init();
        Field f = LSTM.class.getDeclaredField("helper");
        f.setAccessible(true);
        LSTM l = ((LSTM) (mln.getLayer(1)));
        LSTMHelper helper = ((LSTMHelper) (f.get(l)));
        Assert.assertTrue((helper instanceof CudnnLSTMHelper));
        // -------------------------------
        // For debugging/comparison to no-cudnn case: set helper field to null
        // f.set(l, null);
        // assertNull(f.get(l));
        // -------------------------------
        if (CuDNNGradientChecks.PRINT_RESULTS) {
            for (int j = 0; j < (mln.getnLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(mln, CuDNNGradientChecks.DEFAULT_EPS, CuDNNGradientChecks.DEFAULT_MAX_REL_ERROR, CuDNNGradientChecks.DEFAULT_MIN_ABS_ERROR, CuDNNGradientChecks.PRINT_RESULTS, CuDNNGradientChecks.RETURN_ON_FIRST_FAILURE, input, labels);
        Assert.assertTrue(gradOK);
    }

    @Test
    public void testCnnDilated() throws Exception {
        int nOut = 2;
        int minibatchSize = 3;
        int width = 8;
        int height = 8;
        int inputDepth = 3;
        int[] kernelSizes = new int[]{ 2, 3 };
        int[] strides = new int[]{ 1, 2 };
        int[] dilation = new int[]{ 2, 3 };
        ConvolutionMode[] cModes = new ConvolutionMode[]{ ConvolutionMode.Truncate, ConvolutionMode.Same };
        Nd4j.getRandom().setSeed(12345);
        Field f = ConvolutionLayer.class.getDeclaredField("helper");
        f.setAccessible(true);
        Field f2 = SubsamplingLayer.class.getDeclaredField("helper");
        f2.setAccessible(true);
        for (boolean subsampling : new boolean[]{ false, true }) {
            for (int k : kernelSizes) {
                for (int s : strides) {
                    for (int d : dilation) {
                        for (ConvolutionMode cm : cModes) {
                            // Use larger input with larger dilation values (to avoid invalid config)
                            int w = d * width;
                            int h = d * height;
                            INDArray input = Nd4j.rand(minibatchSize, ((w * h) * inputDepth));
                            INDArray labels = Nd4j.zeros(minibatchSize, nOut);
                            for (int i = 0; i < minibatchSize; i++) {
                                labels.putScalar(new int[]{ i, i % nOut }, 1.0);
                            }
                            NeuralNetConfiguration.ListBuilder b = new NeuralNetConfiguration.Builder().seed(12345).updater(new NoOp()).activation(TANH).convolutionMode(cm).list().layer(new ConvolutionLayer.Builder().name("layer 0").kernelSize(k, k).stride(s, s).dilation(d, d).nIn(inputDepth).nOut(2).build());
                            if (subsampling) {
                                b.layer(new SubsamplingLayer.Builder().poolingType(MAX).kernelSize(k, k).stride(s, s).dilation(d, d).build());
                            } else {
                                b.layer(new ConvolutionLayer.Builder().nIn(2).nOut(2).kernelSize(k, k).stride(s, s).dilation(d, d).build());
                            }
                            MultiLayerConfiguration conf = b.layer(new OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nOut(nOut).build()).setInputType(InputType.convolutionalFlat(h, w, inputDepth)).build();
                            MultiLayerNetwork net = new MultiLayerNetwork(conf);
                            net.init();
                            ConvolutionLayer c0 = ((ConvolutionLayer) (net.getLayer(0)));
                            ConvolutionHelper ch0 = ((ConvolutionHelper) (f.get(c0)));
                            Assert.assertTrue((ch0 instanceof CudnnConvolutionHelper));
                            if (subsampling) {
                                SubsamplingLayer s1 = ((SubsamplingLayer) (net.getLayer(1)));
                                SubsamplingHelper sh1 = ((SubsamplingHelper) (f2.get(s1)));
                                Assert.assertTrue((sh1 instanceof SubsamplingHelper));
                            } else {
                                ConvolutionLayer c1 = ((ConvolutionLayer) (net.getLayer(1)));
                                ConvolutionHelper ch1 = ((ConvolutionHelper) (f.get(c1)));
                                Assert.assertTrue((ch1 instanceof CudnnConvolutionHelper));
                            }
                            for (int i = 0; i < (net.getLayers().length); i++) {
                                System.out.println(((("nParams, layer " + i) + ": ") + (net.getLayer(i).numParams())));
                            }
                            String msg = ((((((((((subsampling ? "subsampling" : "conv") + " - mb=") + minibatchSize) + ", k=") + k) + ", s=") + s) + ", d=") + d) + ", cm=") + cm;
                            System.out.println(msg);
                            boolean gradOK = GradientCheckUtil.checkGradients(net, CuDNNGradientChecks.DEFAULT_EPS, CuDNNGradientChecks.DEFAULT_MAX_REL_ERROR, CuDNNGradientChecks.DEFAULT_MIN_ABS_ERROR, CuDNNGradientChecks.PRINT_RESULTS, CuDNNGradientChecks.RETURN_ON_FIRST_FAILURE, input, labels);
                            Assert.assertTrue(msg, gradOK);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testDropout() {
        int minibatch = 3;
        for (boolean cnn : new boolean[]{ false, true }) {
            Nd4j.getRandom().setSeed(12345);
            IDropout dropout = new Dropout(0.6);
            NeuralNetConfiguration.ListBuilder builder = new NeuralNetConfiguration.Builder().seed(12345).dist(new NormalDistribution(0, 1)).convolutionMode(Same).dropOut(dropout).activation(TANH).updater(new NoOp()).list();
            if (cnn) {
                builder.layer(new ConvolutionLayer.Builder().kernelSize(3, 3).stride(1, 1).nOut(3).build());
                builder.layer(new ConvolutionLayer.Builder().kernelSize(3, 3).stride(1, 1).nOut(3).build());
                builder.setInputType(InputType.convolutional(8, 8, 3));
            } else {
                builder.layer(new DenseLayer.Builder().nOut(12).build());
                builder.layer(new DenseLayer.Builder().nOut(12).build());
                builder.setInputType(InputType.feedForward(8));
            }
            builder.layer(new OutputLayer.Builder().nOut(10).activation(SOFTMAX).lossFunction(MCXENT).build());
            MultiLayerConfiguration conf = builder.build();
            MultiLayerNetwork mln = new MultiLayerNetwork(conf);
            mln.init();
            for (Layer l : mln.getLayers()) {
                Dropout d = ((Dropout) (l.conf().getLayer().getIDropout()));
                Assert.assertNotNull(d);
                CudnnDropoutHelper h = ((CudnnDropoutHelper) (d.getHelper()));
                Assert.assertNotNull(h);
            }
            String msg = ((cnn ? "CNN" : "Dense") + ": ") + (dropout.getClass().getSimpleName());
            INDArray f;
            if (cnn) {
                f = Nd4j.rand(new int[]{ minibatch, 3, 8, 8 }).muli(10).subi(5);
            } else {
                f = Nd4j.rand(minibatch, 8).muli(10).subi(5);
            }
            INDArray l = TestUtils.randomOneHot(minibatch, 10);
            // Consumer function to enforce CuDNN RNG repeatability - otherwise will fail due to randomness (inconsistent
            // dropout mask between forward passes)
            Consumer<MultiLayerNetwork> c = new Consumer<MultiLayerNetwork>() {
                @Override
                public void accept(MultiLayerNetwork net) {
                    Nd4j.getRandom().setSeed(12345);
                    for (Layer l : net.getLayers()) {
                        Dropout d = ((Dropout) (l.conf().getLayer().getIDropout()));
                        if (d != null) {
                            setMask(null);
                            setRngStates(null);
                        }
                    }
                }
            };
            log.info((("*** Starting test: " + msg) + " ***"));
            boolean gradOK = GradientCheckUtil.checkGradients(mln, CuDNNGradientChecks.DEFAULT_EPS, CuDNNGradientChecks.DEFAULT_MAX_REL_ERROR, CuDNNGradientChecks.DEFAULT_MIN_ABS_ERROR, CuDNNGradientChecks.PRINT_RESULTS, CuDNNGradientChecks.RETURN_ON_FIRST_FAILURE, f, l, null, null, false, (-1), null, c);
            Assert.assertTrue(msg, gradOK);
            TestUtils.testModelSerialization(mln);
        }
    }

    @Test
    public void testDenseBatchNorm() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).weightInit(XAVIER).updater(new NoOp()).list().layer(new DenseLayer.Builder().nIn(5).nOut(5).activation(TANH).build()).layer(new BatchNormalization.Builder().nOut(5).build()).layer(new OutputLayer.Builder().nIn(5).nOut(5).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        INDArray in = Nd4j.rand(3, 5);
        INDArray labels = TestUtils.randomOneHot(3, 5);
        // Mean and variance vars are not gradient checkable; mean/variance "gradient" is used to implement running mean/variance calc
        // i.e., runningMean = decay * runningMean + (1-decay) * batchMean
        // However, numerical gradient will be 0 as forward pass doesn't depend on this "parameter"
        Set<String> excludeParams = new HashSet<>(Arrays.asList("1_mean", "1_var", "1_log10stdev"));
        boolean gradOK = GradientCheckUtil.checkGradients(net, CuDNNGradientChecks.DEFAULT_EPS, CuDNNGradientChecks.DEFAULT_MAX_REL_ERROR, CuDNNGradientChecks.DEFAULT_MIN_ABS_ERROR, CuDNNGradientChecks.PRINT_RESULTS, CuDNNGradientChecks.RETURN_ON_FIRST_FAILURE, in, labels, excludeParams);
        Assert.assertTrue(gradOK);
        TestUtils.testModelSerialization(net);
    }
}

