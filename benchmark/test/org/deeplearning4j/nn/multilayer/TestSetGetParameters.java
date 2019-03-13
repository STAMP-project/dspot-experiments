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
package org.deeplearning4j.nn.multilayer;


import java.util.Map;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


public class TestSetGetParameters extends BaseDL4JTest {
    @Test
    public void testSetParameters() {
        // Set up a MLN, then do set(get) on parameters. Results should be identical compared to before doing this.
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(0, new DenseLayer.Builder().nIn(9).nOut(10).dist(new NormalDistribution(0, 1)).build()).layer(1, new DenseLayer.Builder().nIn(10).nOut(11).dist(new NormalDistribution(0, 1)).build()).layer(2, nIn(11).nOut(12).dist(new NormalDistribution(0, 1)).build()).layer(3, nIn(12).nOut(12).dist(new NormalDistribution(0, 1)).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        INDArray initParams = net.params().dup();
        Map<String, INDArray> initParams2 = net.paramTable();
        net.setParams(net.params());
        INDArray initParamsAfter = net.params();
        Map<String, INDArray> initParams2After = net.paramTable();
        for (String s : initParams2.keySet()) {
            Assert.assertTrue(("Params differ: " + s), initParams2.get(s).equals(initParams2After.get(s)));
        }
        Assert.assertEquals(initParams, initParamsAfter);
        // Now, try the other way: get(set(random))
        INDArray randomParams = Nd4j.rand(initParams.shape());
        net.setParams(randomParams.dup());
        Assert.assertEquals(net.params(), randomParams);
    }

    @Test
    public void testSetParametersRNN() {
        // Set up a MLN, then do set(get) on parameters. Results should be identical compared to before doing this.
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(0, new GravesLSTM.Builder().nIn(9).nOut(10).dist(new NormalDistribution(0, 1)).build()).layer(1, new GravesLSTM.Builder().nIn(10).nOut(11).dist(new NormalDistribution(0, 1)).build()).layer(2, nIn(11).nOut(12).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        INDArray initParams = net.params().dup();
        Map<String, INDArray> initParams2 = net.paramTable();
        net.setParams(net.params());
        INDArray initParamsAfter = net.params();
        Map<String, INDArray> initParams2After = net.paramTable();
        for (String s : initParams2.keySet()) {
            Assert.assertTrue(("Params differ: " + s), initParams2.get(s).equals(initParams2After.get(s)));
        }
        Assert.assertEquals(initParams, initParamsAfter);
        // Now, try the other way: get(set(random))
        INDArray randomParams = Nd4j.rand(initParams.shape());
        net.setParams(randomParams.dup());
        Assert.assertEquals(net.params(), randomParams);
    }

    @Test
    public void testInitWithParams() {
        Nd4j.getRandom().setSeed(12345);
        // Create configuration. Doesn't matter if this doesn't actually work for forward/backward pass here
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).list().layer(0, new ConvolutionLayer.Builder().nIn(10).nOut(10).kernelSize(2, 2).stride(2, 2).padding(2, 2).build()).layer(1, new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(2, new GravesLSTM.Builder().nIn(10).nOut(10).build()).layer(3, new GravesBidirectionalLSTM.Builder().nIn(10).nOut(10).build()).layer(4, nIn(10).nOut(10).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        INDArray params = net.params();
        MultiLayerNetwork net2 = new MultiLayerNetwork(conf);
        net2.init(params, true);
        MultiLayerNetwork net3 = new MultiLayerNetwork(conf);
        net3.init(params, false);
        Assert.assertEquals(params, net2.params());
        Assert.assertEquals(params, net3.params());
        Assert.assertFalse((params == (net2.params())));// Different objects due to clone

        Assert.assertTrue((params == (net3.params())));// Same object due to clone

        Map<String, INDArray> paramsMap = net.paramTable();
        Map<String, INDArray> paramsMap2 = net2.paramTable();
        Map<String, INDArray> paramsMap3 = net3.paramTable();
        for (String s : paramsMap.keySet()) {
            Assert.assertEquals(paramsMap.get(s), paramsMap2.get(s));
            Assert.assertEquals(paramsMap.get(s), paramsMap3.get(s));
        }
    }
}

