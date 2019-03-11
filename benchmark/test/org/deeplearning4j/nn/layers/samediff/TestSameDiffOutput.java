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


import Activation.IDENTITY;
import Activation.TANH;
import LossFunctions.LossFunction.MSE;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LossLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.layers.samediff.testlayers.SameDiffMSELossLayer;
import org.deeplearning4j.nn.layers.samediff.testlayers.SameDiffMSEOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;


@Slf4j
public class TestSameDiffOutput extends BaseDL4JTest {
    @Test
    public void testOutputMSELossLayer() {
        Nd4j.getRandom().setSeed(12345);
        MultiLayerConfiguration confSD = new NeuralNetConfiguration.Builder().seed(12345).updater(new Adam(0.01)).list().layer(new DenseLayer.Builder().nIn(5).nOut(5).activation(TANH).build()).layer(new SameDiffMSELossLayer()).build();
        MultiLayerConfiguration confStd = new NeuralNetConfiguration.Builder().seed(12345).updater(new Adam(0.01)).list().layer(new DenseLayer.Builder().nIn(5).nOut(5).activation(TANH).build()).layer(new LossLayer.Builder().activation(IDENTITY).lossFunction(MSE).build()).build();
        MultiLayerNetwork netSD = new MultiLayerNetwork(confSD);
        netSD.init();
        MultiLayerNetwork netStd = new MultiLayerNetwork(confStd);
        netStd.init();
        INDArray in = Nd4j.rand(3, 5);
        INDArray label = Nd4j.rand(3, 5);
        INDArray outSD = netSD.output(in);
        INDArray outStd = netStd.output(in);
        Assert.assertEquals(outStd, outSD);
        DataSet ds = new DataSet(in, label);
        double scoreSD = netSD.score(ds);
        double scoreStd = netStd.score(ds);
        Assert.assertEquals(scoreStd, scoreSD, 1.0E-6);
        for (int i = 0; i < 3; i++) {
            netSD.fit(ds);
            netStd.fit(ds);
            Assert.assertEquals(netStd.params(), netSD.params());
            Assert.assertEquals(netStd.getFlattenedGradients(), netSD.getFlattenedGradients());
        }
    }

    @Test
    public void testMSEOutputLayer() {
        Nd4j.getRandom().setSeed(12345);
        for (Activation a : new Activation[]{ Activation.IDENTITY, Activation.TANH, Activation.SOFTMAX }) {
            log.info(("Starting test: " + a));
            MultiLayerConfiguration confSD = new NeuralNetConfiguration.Builder().seed(12345).updater(new Adam(0.01)).list().layer(new DenseLayer.Builder().nIn(5).nOut(5).activation(TANH).build()).layer(new SameDiffMSEOutputLayer(5, 5, a, WeightInit.XAVIER)).build();
            MultiLayerConfiguration confStd = new NeuralNetConfiguration.Builder().seed(12345).updater(new Adam(0.01)).list().layer(new DenseLayer.Builder().nIn(5).nOut(5).activation(TANH).build()).layer(new OutputLayer.Builder().nIn(5).nOut(5).activation(a).lossFunction(MSE).build()).build();
            MultiLayerNetwork netSD = new MultiLayerNetwork(confSD);
            netSD.init();
            MultiLayerNetwork netStd = new MultiLayerNetwork(confStd);
            netStd.init();
            netSD.params().assign(netStd.params());
            Assert.assertEquals(netStd.paramTable(), netSD.paramTable());
            int minibatch = 2;
            INDArray in = Nd4j.rand(minibatch, 5);
            INDArray label = Nd4j.rand(minibatch, 5);
            INDArray outSD = netSD.output(in);
            INDArray outStd = netStd.output(in);
            Assert.assertEquals(outStd, outSD);
            DataSet ds = new DataSet(in, label);
            double scoreSD = netSD.score(ds);
            double scoreStd = netStd.score(ds);
            Assert.assertEquals(scoreStd, scoreSD, 1.0E-6);
            netSD.setInput(in);
            netSD.setLabels(label);
            netStd.setInput(in);
            netStd.setLabels(label);
            // System.out.println(((SameDiffOutputLayer) netSD.getLayer(1)).sameDiff.summary());
            netSD.computeGradientAndScore();
            netStd.computeGradientAndScore();
            Assert.assertEquals(netStd.getFlattenedGradients(), netSD.getFlattenedGradients());
            for (int i = 0; i < 3; i++) {
                netSD.fit(ds);
                netStd.fit(ds);
                String s = String.valueOf(i);
                Assert.assertEquals(s, netStd.params(), netSD.params());
                Assert.assertEquals(s, netStd.getFlattenedGradients(), netSD.getFlattenedGradients());
            }
        }
    }
}

