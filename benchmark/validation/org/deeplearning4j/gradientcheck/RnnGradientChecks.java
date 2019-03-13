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
import Bidirectional.Mode;
import DataType.DOUBLE;
import LossFunctions.LossFunction.MCXENT;
import WeightInit.XAVIER;
import java.util.Random;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.conf.layers.recurrent.Bidirectional;
import org.deeplearning4j.nn.conf.layers.recurrent.SimpleRnn;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.NoOp;


public class RnnGradientChecks extends BaseDL4JTest {
    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-6;

    private static final double DEFAULT_MAX_REL_ERROR = 0.001;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-8;

    static {
        Nd4j.setDataType(DOUBLE);
    }

    @Test
    public void testBidirectionalWrapper() {
        int nIn = 3;
        int nOut = 5;
        int tsLength = 4;
        Bidirectional[] modes = new Bidirectional.Mode[]{ Mode.CONCAT, Mode.ADD, Mode.AVERAGE, Mode.MUL };
        Random r = new Random(12345);
        for (int mb : new int[]{ 1, 3 }) {
            for (boolean inputMask : new boolean[]{ false, true }) {
                for (boolean simple : new boolean[]{ false, true }) {
                    for (boolean hasLayerNorm : new boolean[]{ true, false }) {
                        INDArray in = Nd4j.rand(new int[]{ mb, nIn, tsLength });
                        INDArray labels = Nd4j.create(mb, nOut, tsLength);
                        for (int i = 0; i < mb; i++) {
                            for (int j = 0; j < tsLength; j++) {
                                labels.putScalar(i, r.nextInt(nOut), j, 1.0);
                            }
                        }
                        String maskType = (inputMask) ? "inputMask" : "none";
                        INDArray inMask = null;
                        if (inputMask) {
                            inMask = Nd4j.ones(mb, tsLength);
                            for (int i = 0; i < mb; i++) {
                                int firstMaskedStep = (tsLength - 1) - i;
                                if (firstMaskedStep == 0) {
                                    firstMaskedStep = tsLength;
                                }
                                for (int j = firstMaskedStep; j < tsLength; j++) {
                                    inMask.putScalar(i, j, 1.0);
                                }
                            }
                        }
                        for (Bidirectional.Mode m : modes) {
                            String name = (((((((("mb=" + mb) + ", maskType=") + maskType) + ", mode=") + m) + ", hasLayerNorm=") + hasLayerNorm) + ", rnnType=") + (simple ? "SimpleRnn" : "LSTM");
                            System.out.println(("Starting test: " + name));
                            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).weightInit(XAVIER).list().layer(new LSTM.Builder().nIn(nIn).nOut(3).build()).layer(new Bidirectional(m, (simple ? new SimpleRnn.Builder().nIn(3).nOut(3).hasLayerNorm(hasLayerNorm).build() : new LSTM.Builder().nIn(3).nOut(3).build()))).layer(new RnnOutputLayer.Builder().nOut(nOut).activation(SOFTMAX).build()).build();
                            MultiLayerNetwork net = new MultiLayerNetwork(conf);
                            net.init();
                            boolean gradOK = GradientCheckUtil.checkGradients(net, RnnGradientChecks.DEFAULT_EPS, RnnGradientChecks.DEFAULT_MAX_REL_ERROR, RnnGradientChecks.DEFAULT_MIN_ABS_ERROR, RnnGradientChecks.PRINT_RESULTS, RnnGradientChecks.RETURN_ON_FIRST_FAILURE, in, labels, inMask, null);
                            Assert.assertTrue(gradOK);
                            TestUtils.testModelSerialization(net);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testSimpleRnn() {
        int nOut = 5;
        double[] l1s = new double[]{ 0.0, 0.4 };
        double[] l2s = new double[]{ 0.0, 0.6 };
        Random r = new Random(12345);
        for (int mb : new int[]{ 1, 3 }) {
            for (int tsLength : new int[]{ 1, 4 }) {
                for (int nIn : new int[]{ 3, 1 }) {
                    for (int layerSize : new int[]{ 4, 1 }) {
                        for (boolean inputMask : new boolean[]{ false, true }) {
                            for (boolean hasLayerNorm : new boolean[]{ true, false }) {
                                for (int l = 0; l < (l1s.length); l++) {
                                    INDArray in = Nd4j.rand(new int[]{ mb, nIn, tsLength });
                                    INDArray labels = Nd4j.create(mb, nOut, tsLength);
                                    for (int i = 0; i < mb; i++) {
                                        for (int j = 0; j < tsLength; j++) {
                                            labels.putScalar(i, r.nextInt(nOut), j, 1.0);
                                        }
                                    }
                                    String maskType = (inputMask) ? "inputMask" : "none";
                                    INDArray inMask = null;
                                    if (inputMask) {
                                        inMask = Nd4j.ones(mb, tsLength);
                                        for (int i = 0; i < mb; i++) {
                                            int firstMaskedStep = (tsLength - 1) - i;
                                            if (firstMaskedStep == 0) {
                                                firstMaskedStep = tsLength;
                                            }
                                            for (int j = firstMaskedStep; j < tsLength; j++) {
                                                inMask.putScalar(i, j, 0.0);
                                            }
                                        }
                                    }
                                    String name = (((((((((("testSimpleRnn() - mb=" + mb) + ", tsLength = ") + tsLength) + ", maskType=") + maskType) + ", l1=") + (l1s[l])) + ", l2=") + (l2s[l])) + ", hasLayerNorm=") + hasLayerNorm;
                                    System.out.println(("Starting test: " + name));
                                    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).weightInit(XAVIER).activation(TANH).l1(l1s[l]).l2(l2s[l]).list().layer(new SimpleRnn.Builder().nIn(nIn).nOut(layerSize).hasLayerNorm(hasLayerNorm).build()).layer(new SimpleRnn.Builder().nIn(layerSize).nOut(layerSize).hasLayerNorm(hasLayerNorm).build()).layer(new RnnOutputLayer.Builder().nIn(layerSize).nOut(nOut).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
                                    MultiLayerNetwork net = new MultiLayerNetwork(conf);
                                    net.init();
                                    boolean gradOK = GradientCheckUtil.checkGradients(net, RnnGradientChecks.DEFAULT_EPS, RnnGradientChecks.DEFAULT_MAX_REL_ERROR, RnnGradientChecks.DEFAULT_MIN_ABS_ERROR, RnnGradientChecks.PRINT_RESULTS, RnnGradientChecks.RETURN_ON_FIRST_FAILURE, in, labels, inMask, null);
                                    Assert.assertTrue(gradOK);
                                    TestUtils.testModelSerialization(net);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testLastTimeStepLayer() {
        int nIn = 3;
        int nOut = 5;
        int tsLength = 4;
        int layerSize = 8;
        Random r = new Random(12345);
        for (int mb : new int[]{ 1, 3 }) {
            for (boolean inputMask : new boolean[]{ false, true }) {
                for (boolean simple : new boolean[]{ false, true }) {
                    for (boolean hasLayerNorm : new boolean[]{ true, false }) {
                        INDArray in = Nd4j.rand(new int[]{ mb, nIn, tsLength });
                        INDArray labels = Nd4j.create(mb, nOut);
                        for (int i = 0; i < mb; i++) {
                            labels.putScalar(i, r.nextInt(nOut), 1.0);
                        }
                        String maskType = (inputMask) ? "inputMask" : "none";
                        INDArray inMask = null;
                        if (inputMask) {
                            inMask = Nd4j.ones(mb, tsLength);
                            for (int i = 0; i < mb; i++) {
                                int firstMaskedStep = (tsLength - 1) - i;
                                if (firstMaskedStep == 0) {
                                    firstMaskedStep = tsLength;
                                }
                                for (int j = firstMaskedStep; j < tsLength; j++) {
                                    inMask.putScalar(i, j, 0.0);
                                }
                            }
                        }
                        String name = (((((((("testLastTimeStepLayer() - mb=" + mb) + ", tsLength = ") + tsLength) + ", maskType=") + maskType) + ", hasLayerNorm=") + hasLayerNorm) + ", rnnType=") + (simple ? "SimpleRnn" : "LSTM");
                        if (RnnGradientChecks.PRINT_RESULTS) {
                            System.out.println(("Starting test: " + name));
                        }
                        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().activation(TANH).updater(new NoOp()).weightInit(XAVIER).list().layer((simple ? new SimpleRnn.Builder().nOut(layerSize).hasLayerNorm(hasLayerNorm).build() : new LSTM.Builder().nOut(layerSize).build())).layer(new org.deeplearning4j.nn.conf.layers.recurrent.LastTimeStep((simple ? new SimpleRnn.Builder().nOut(layerSize).hasLayerNorm(hasLayerNorm).build() : new LSTM.Builder().nOut(layerSize).build()))).layer(new OutputLayer.Builder().nOut(nOut).activation(SOFTMAX).lossFunction(MCXENT).build()).setInputType(InputType.recurrent(nIn)).build();
                        MultiLayerNetwork net = new MultiLayerNetwork(conf);
                        net.init();
                        boolean gradOK = GradientCheckUtil.checkGradients(net, RnnGradientChecks.DEFAULT_EPS, RnnGradientChecks.DEFAULT_MAX_REL_ERROR, RnnGradientChecks.DEFAULT_MIN_ABS_ERROR, RnnGradientChecks.PRINT_RESULTS, RnnGradientChecks.RETURN_ON_FIRST_FAILURE, in, labels, inMask, null);
                        Assert.assertTrue(name, gradOK);
                        TestUtils.testModelSerialization(net);
                    }
                }
            }
        }
    }
}

