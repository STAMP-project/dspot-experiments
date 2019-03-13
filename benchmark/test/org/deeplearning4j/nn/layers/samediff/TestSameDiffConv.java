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
package org.deeplearning4j.nn.layers.samediff;


import Activation.SIGMOID;
import Activation.SOFTMAX;
import Activation.TANH;
import ConvolutionParamInitializer.BIAS_KEY;
import ConvolutionParamInitializer.WEIGHT_KEY;
import LossFunctions.LossFunction.MCXENT;
import WeightInit.XAVIER;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.gradientcheck.GradientCheckUtil;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.layers.samediff.testlayers.SameDiffConv;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.NoOp;


@Slf4j
public class TestSameDiffConv extends BaseDL4JTest {
    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-6;

    private static final double DEFAULT_MAX_REL_ERROR = 0.001;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-8;

    @Test
    public void testSameDiffConvBasic() {
        int nIn = 3;
        int nOut = 4;
        int kH = 2;
        int kW = 3;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(new SameDiffConv.Builder().nIn(nIn).nOut(nOut).kernelSize(kH, kW).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        Map<String, INDArray> pt1 = net.getLayer(0).paramTable();
        Assert.assertNotNull(pt1);
        Assert.assertEquals(2, pt1.size());
        Assert.assertNotNull(pt1.get(WEIGHT_KEY));
        Assert.assertNotNull(pt1.get(BIAS_KEY));
        Assert.assertArrayEquals(new long[]{ kH, kW, nIn, nOut }, pt1.get(WEIGHT_KEY).shape());
        Assert.assertArrayEquals(new long[]{ 1, nOut }, pt1.get(BIAS_KEY).shape());
        TestUtils.testModelSerialization(net);
    }

    @Test
    public void testSameDiffConvForward() {
        int imgH = 16;
        int imgW = 20;
        int count = 0;
        // Note: to avoid the exporential number of tests here, we'll randomly run every Nth test only.
        // With n=1, m=3 this is 1 out of every 3 tests (on average)
        Random r = new Random(12345);
        int n = 1;
        int m = 30;// 1 ot of every 30... 3888 possible combinations here

        for (int minibatch : new int[]{ 5, 1 }) {
            Activation[] afns = new Activation[]{ Activation.TANH, Activation.SIGMOID, Activation.ELU, Activation.IDENTITY, Activation.SOFTPLUS, Activation.SOFTSIGN, Activation.CUBE, Activation.HARDTANH, Activation.RELU };
            for (boolean hasBias : new boolean[]{ true, false }) {
                for (int nIn : new int[]{ 3, 4 }) {
                    for (int nOut : new int[]{ 4, 5 }) {
                        for (int[] kernel : new int[][]{ new int[]{ 2, 2 }, new int[]{ 2, 1 }, new int[]{ 3, 2 } }) {
                            for (int[] strides : new int[][]{ new int[]{ 1, 1 }, new int[]{ 2, 2 }, new int[]{ 2, 1 } }) {
                                for (int[] dilation : new int[][]{ new int[]{ 1, 1 }, new int[]{ 2, 2 }, new int[]{ 1, 2 } }) {
                                    for (ConvolutionMode cm : new ConvolutionMode[]{ ConvolutionMode.Truncate, ConvolutionMode.Same }) {
                                        for (Activation a : afns) {
                                            int i = r.nextInt(m);
                                            if (i >= n) {
                                                // Example: n=2, m=3... skip on i=2, run test on i=0, i=1
                                                continue;
                                            }
                                            String msg = (((((((((((((((((("Test " + (count++)) + " - minibatch=") + minibatch) + ", nIn=") + nIn) + ", nOut=") + nOut) + ", kernel=") + (Arrays.toString(kernel))) + ", stride=") + (Arrays.toString(strides))) + ", dilation=") + (Arrays.toString(dilation))) + ", ConvolutionMode=") + cm) + ", ActFn=") + a) + ", hasBias=") + hasBias;
                                            log.info(("Starting test: " + msg));
                                            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).list().layer(new SameDiffConv.Builder().weightInit(XAVIER).nIn(nIn).nOut(nOut).kernelSize(kernel).stride(strides).dilation(dilation).convolutionMode(cm).activation(a).hasBias(hasBias).build()).layer(new SameDiffConv.Builder().weightInit(XAVIER).nIn(nOut).nOut(nOut).kernelSize(kernel).stride(strides).dilation(dilation).convolutionMode(cm).activation(a).hasBias(hasBias).build()).build();
                                            MultiLayerNetwork net = new MultiLayerNetwork(conf);
                                            net.init();
                                            Assert.assertNotNull(net.paramTable());
                                            MultiLayerConfiguration conf2 = new NeuralNetConfiguration.Builder().weightInit(XAVIER).seed(12345).list().layer(new ConvolutionLayer.Builder().nIn(nIn).nOut(nOut).kernelSize(kernel).stride(strides).dilation(dilation).convolutionMode(cm).activation(a).hasBias(hasBias).build()).layer(new ConvolutionLayer.Builder().nIn(nOut).nOut(nOut).kernelSize(kernel).stride(strides).dilation(dilation).convolutionMode(cm).activation(a).hasBias(hasBias).build()).build();
                                            MultiLayerNetwork net2 = new MultiLayerNetwork(conf2);
                                            net2.init();
                                            // Check params: note that samediff/libnd4j conv params are [kH, kW, iC, oC]
                                            // DL4J are [nOut, nIn, kH, kW]
                                            Map<String, INDArray> params1 = net.paramTable();
                                            Map<String, INDArray> params2 = net2.paramTable();
                                            for (Map.Entry<String, INDArray> e : params1.entrySet()) {
                                                if (e.getKey().endsWith("_W")) {
                                                    INDArray p1 = e.getValue();
                                                    INDArray p2 = params2.get(e.getKey());
                                                    p2 = p2.permute(2, 3, 1, 0);
                                                    p1.assign(p2);
                                                } else {
                                                    Assert.assertEquals(params2.get(e.getKey()), e.getValue());
                                                }
                                            }
                                            INDArray in = Nd4j.rand(new int[]{ minibatch, nIn, imgH, imgW });
                                            INDArray out = net.output(in);
                                            INDArray outExp = net2.output(in);
                                            Assert.assertEquals(msg, outExp, out);
                                            // Also check serialization:
                                            MultiLayerNetwork netLoaded = TestUtils.testModelSerialization(net);
                                            INDArray outLoaded = netLoaded.output(in);
                                            Assert.assertEquals(msg, outExp, outLoaded);
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
    public void testSameDiffConvGradient() {
        int imgH = 8;
        int imgW = 8;
        int nIn = 3;
        int nOut = 4;
        int[] kernel = new int[]{ 2, 2 };
        int[] strides = new int[]{ 1, 1 };
        int[] dilation = new int[]{ 1, 1 };
        int count = 0;
        // Note: to avoid the exporential number of tests here, we'll randomly run every Nth test only.
        // With n=1, m=3 this is 1 out of every 3 tests (on average)
        Random r = new Random(12345);
        int n = 1;
        int m = 5;
        for (boolean workspaces : new boolean[]{ false, true }) {
            for (int minibatch : new int[]{ 5, 1 }) {
                for (boolean hasBias : new boolean[]{ true, false }) {
                    for (ConvolutionMode cm : new ConvolutionMode[]{ ConvolutionMode.Truncate, ConvolutionMode.Same }) {
                        int i = r.nextInt(m);
                        if (i >= n) {
                            // Example: n=2, m=3... skip on i=2, run test on i=0, i=1
                            continue;
                        }
                        String msg = (((((("Test " + (count++)) + " - minibatch=") + minibatch) + ", ConvolutionMode=") + cm) + ", hasBias=") + hasBias;
                        int outH = (cm == (ConvolutionMode.Same)) ? imgH : imgH - 2;
                        int outW = (cm == (ConvolutionMode.Same)) ? imgW : imgW - 2;
                        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).updater(new NoOp()).trainingWorkspaceMode((workspaces ? WorkspaceMode.ENABLED : WorkspaceMode.NONE)).inferenceWorkspaceMode((workspaces ? WorkspaceMode.ENABLED : WorkspaceMode.NONE)).list().layer(new SameDiffConv.Builder().weightInit(XAVIER).nIn(nIn).nOut(nOut).kernelSize(kernel).stride(strides).dilation(dilation).convolutionMode(cm).activation(TANH).hasBias(hasBias).build()).layer(new SameDiffConv.Builder().weightInit(XAVIER).nIn(nOut).nOut(nOut).kernelSize(kernel).stride(strides).dilation(dilation).convolutionMode(cm).activation(SIGMOID).hasBias(hasBias).build()).layer(new OutputLayer.Builder().activation(SOFTMAX).lossFunction(MCXENT).nIn(((nOut * outH) * outW)).nOut(nOut).build()).inputPreProcessor(2, new CnnToFeedForwardPreProcessor(outH, outW, nOut)).build();
                        MultiLayerNetwork net = new MultiLayerNetwork(conf);
                        net.init();
                        INDArray f = Nd4j.rand(new int[]{ minibatch, nIn, imgH, imgW });
                        INDArray l = TestUtils.randomOneHot(minibatch, nOut);
                        log.info(("Starting: " + msg));
                        boolean gradOK = GradientCheckUtil.checkGradients(net, TestSameDiffConv.DEFAULT_EPS, TestSameDiffConv.DEFAULT_MAX_REL_ERROR, TestSameDiffConv.DEFAULT_MIN_ABS_ERROR, TestSameDiffConv.PRINT_RESULTS, TestSameDiffConv.RETURN_ON_FIRST_FAILURE, f, l);
                        Assert.assertTrue(msg, gradOK);
                        TestUtils.testModelSerialization(net);
                    }
                }
            }
        }
    }
}

