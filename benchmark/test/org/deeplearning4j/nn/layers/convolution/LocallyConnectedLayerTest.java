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
import Activation.SOFTMAX;
import ConvolutionMode.Strict;
import MultiLayerConfiguration.Builder;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import WeightInit.XAVIER;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;


/**
 *
 *
 * @author Max Pumperla
 */
public class LocallyConnectedLayerTest extends BaseDL4JTest {
    @Test
    public void test2dForward() {
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().seed(123).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).l2(2.0E-4).updater(new Nesterovs(0.9)).dropOut(0.5).list().layer(nOut(16).dropOut(0.5).convolutionMode(Strict).setInputSize(28, 28).activation(RELU).weightInit(XAVIER).build()).layer(// output layer
        nOut(10).weightInit(XAVIER).activation(SOFTMAX).build()).setInputType(InputType.convolutionalFlat(28, 28, 3));
        MultiLayerConfiguration conf = builder.build();
        MultiLayerNetwork network = new MultiLayerNetwork(conf);
        network.init();
        INDArray input = Nd4j.ones(10, 3, 28, 28);
        INDArray output = network.output(input, false);
        Assert.assertArrayEquals(new long[]{ 10, 10 }, output.shape());
    }

    @Test
    public void test1dForward() {
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().seed(123).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).l2(2.0E-4).updater(new Nesterovs(0.9)).dropOut(0.5).list().layer(nOut(16).dropOut(0.5).convolutionMode(Strict).setInputSize(28).activation(RELU).weightInit(XAVIER).build()).layer(// output layer
        nOut(10).weightInit(XAVIER).activation(SOFTMAX).build()).setInputType(InputType.recurrent(3, 28));
        MultiLayerConfiguration conf = builder.build();
        MultiLayerNetwork network = new MultiLayerNetwork(conf);
        network.init();
        INDArray input = Nd4j.ones(10, 3, 28);
        INDArray output = network.output(input, false);
        for (int i = 0; i < 100; i++) {
            // TODO: this falls flat for 1000 iterations on my machine
            output = network.output(input, false);
        }
        Assert.assertArrayEquals(new long[]{ ((28 - 8) + 1) * 10, 10 }, output.shape());
        network.fit(input, output);
    }
}

