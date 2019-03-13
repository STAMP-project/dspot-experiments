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
import LossFunction.MCXENT;
import NeuralNetConfiguration.ListBuilder;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.NoOp;


/**
 *
 *
 * @author Alex Black
 */
@Slf4j
public class DropoutGradientCheck extends BaseDL4JTest {
    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-6;

    private static final double DEFAULT_MAX_REL_ERROR = 0.001;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-8;

    static {
        Nd4j.setDataType(DOUBLE);
    }

    @Test
    public void testDropoutGradient() {
        int minibatch = 3;
        for (boolean cnn : new boolean[]{ false, true }) {
            for (int i = 0; i < 5; i++) {
                IDropout dropout;
                switch (i) {
                    case 0 :
                        dropout = new Dropout(0.6);
                        break;
                    case 1 :
                        dropout = new AlphaDropout(0.6);
                        break;
                    case 2 :
                        dropout = new GaussianDropout(0.1);// 0.01 rate -> stdev 0.1; 0.1 rate -> stdev 0.333

                        break;
                    case 3 :
                        dropout = new GaussianNoise(0.3);
                        break;
                    case 4 :
                        dropout = new SpatialDropout(0.6);
                        break;
                    default :
                        throw new RuntimeException();
                }
                if ((!cnn) && (i == 4)) {
                    // Skip spatial dropout for dense layer (not applicable)
                    continue;
                }
                NeuralNetConfiguration.ListBuilder builder = new NeuralNetConfiguration.Builder().dist(new NormalDistribution(0, 1)).convolutionMode(Same).dropOut(dropout).activation(TANH).updater(new NoOp()).list();
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
                // Remove spatial dropout from output layer - can't be used for 2d input
                if (i == 4) {
                    conf.getConf(2).getLayer().setIDropout(null);
                }
                MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                mln.init();
                String msg = ((cnn ? "CNN" : "Dense") + ": ") + (dropout.getClass().getSimpleName());
                INDArray f;
                if (cnn) {
                    f = Nd4j.rand(new int[]{ minibatch, 3, 8, 8 }).muli(10).subi(5);
                } else {
                    f = Nd4j.rand(minibatch, 8).muli(10).subi(5);
                }
                INDArray l = TestUtils.randomOneHot(minibatch, 10);
                log.info((("*** Starting test: " + msg) + " ***"));
                boolean gradOK = GradientCheckUtil.checkGradients(mln, DropoutGradientCheck.DEFAULT_EPS, DropoutGradientCheck.DEFAULT_MAX_REL_ERROR, DropoutGradientCheck.DEFAULT_MIN_ABS_ERROR, DropoutGradientCheck.PRINT_RESULTS, DropoutGradientCheck.RETURN_ON_FIRST_FAILURE, f, l, null, null, false, (-1), null, 12345);// Last arg: ensures RNG is reset at each iter... otherwise will fail due to randomness!

                Assert.assertTrue(msg, gradOK);
                TestUtils.testModelSerialization(mln);
            }
        }
    }

    @Test
    public void testCompGraphMultiInput() {
        // Validate nets where the one output array is used as the input to multiple layers...
        Nd4j.getRandom().setSeed(12345);
        int mb = 3;
        ComputationGraphConfiguration conf = // 0.33 stdev. Gaussian dropout: out = in * N(1,stdev)
        new NeuralNetConfiguration.Builder().dist(new NormalDistribution(0, 1)).convolutionMode(Same).dropOut(new GaussianDropout(0.1)).activation(TANH).updater(new NoOp()).graphBuilder().addInputs("in").addLayer("0", new DenseLayer.Builder().nIn(5).nOut(5).build(), "in").addLayer("1", new DenseLayer.Builder().nIn(5).nOut(5).build(), "0").addLayer("2", new DenseLayer.Builder().nIn(5).nOut(5).build(), "0").addLayer("3", new DenseLayer.Builder().nIn(5).nOut(5).build(), "0").addLayer("out", new OutputLayer.Builder().nIn(15).nOut(5).activation(SOFTMAX).lossFunction(MCXENT).build(), "1", "2", "3").setOutputs("out").build();
        ComputationGraph cg = new ComputationGraph(conf);
        cg.init();
        INDArray[] in = new INDArray[]{ Nd4j.rand(mb, 5) };
        INDArray[] l = new INDArray[]{ TestUtils.randomOneHot(mb, 5) };
        boolean ok = GradientCheckUtil.checkGradients(cg, DropoutGradientCheck.DEFAULT_EPS, DropoutGradientCheck.DEFAULT_MAX_REL_ERROR, DropoutGradientCheck.DEFAULT_MIN_ABS_ERROR, DropoutGradientCheck.PRINT_RESULTS, DropoutGradientCheck.RETURN_ON_FIRST_FAILURE, in, l, null, null, null, 12345);
        Assert.assertTrue(ok);
    }
}

