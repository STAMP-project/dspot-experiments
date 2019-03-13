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
package org.deeplearning4j.nn.updater.custom;


import Activation.TANH;
import LossFunctions.LossFunction.MSE;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;


/**
 * Created by Alex on 09/05/2017.
 */
public class TestCustomUpdater {
    @Test
    public void testCustomUpdater() {
        // Create a simple custom updater, equivalent to SGD updater
        double lr = 0.03;
        Nd4j.getRandom().setSeed(12345);
        MultiLayerConfiguration conf1 = // Specify custom IUpdater
        new NeuralNetConfiguration.Builder().seed(12345).activation(TANH).updater(new CustomIUpdater(lr)).list().layer(0, new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(1, new OutputLayer.Builder().nIn(10).nOut(10).lossFunction(MSE).build()).build();
        Nd4j.getRandom().setSeed(12345);
        MultiLayerConfiguration conf2 = new NeuralNetConfiguration.Builder().seed(12345).activation(TANH).updater(new Sgd(lr)).list().layer(0, new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(1, new OutputLayer.Builder().nIn(10).nOut(10).lossFunction(MSE).build()).build();
        // First: Check updater config
        Assert.assertTrue(((getIUpdater()) instanceof CustomIUpdater));
        Assert.assertTrue(((getIUpdater()) instanceof CustomIUpdater));
        Assert.assertTrue(((getIUpdater()) instanceof Sgd));
        Assert.assertTrue(((getIUpdater()) instanceof Sgd));
        CustomIUpdater u0_0 = ((CustomIUpdater) (getIUpdater()));
        CustomIUpdater u0_1 = ((CustomIUpdater) (getIUpdater()));
        Assert.assertEquals(lr, u0_0.getLearningRate(), 1.0E-6);
        Assert.assertEquals(lr, u0_1.getLearningRate(), 1.0E-6);
        Sgd u1_0 = ((Sgd) (getIUpdater()));
        Sgd u1_1 = ((Sgd) (getIUpdater()));
        Assert.assertEquals(lr, u1_0.getLearningRate(), 1.0E-6);
        Assert.assertEquals(lr, u1_1.getLearningRate(), 1.0E-6);
        // Second: check JSON
        String asJson = conf1.toJson();
        MultiLayerConfiguration fromJson = MultiLayerConfiguration.fromJson(asJson);
        Assert.assertEquals(conf1, fromJson);
        Nd4j.getRandom().setSeed(12345);
        MultiLayerNetwork net1 = new MultiLayerNetwork(conf1);
        net1.init();
        Nd4j.getRandom().setSeed(12345);
        MultiLayerNetwork net2 = new MultiLayerNetwork(conf2);
        net2.init();
        // Third: check gradients are equal
        INDArray in = Nd4j.rand(5, 10);
        INDArray labels = Nd4j.rand(5, 10);
        net1.setInput(in);
        net2.setInput(in);
        net1.setLabels(labels);
        net2.setLabels(labels);
        net1.computeGradientAndScore();
        net2.computeGradientAndScore();
        Assert.assertEquals(net1.getFlattenedGradients(), net2.getFlattenedGradients());
    }
}

