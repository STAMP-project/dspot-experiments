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
package org.deeplearning4j.nn.layers;


import CacheMode.DEVICE;
import CacheMode.NONE;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


public class CacheModeTest extends BaseDL4JTest {
    @Test
    public void testConvCacheModeSimple() {
        MultiLayerConfiguration conf1 = CacheModeTest.getConf(NONE);
        MultiLayerConfiguration conf2 = CacheModeTest.getConf(DEVICE);
        MultiLayerNetwork net1 = new MultiLayerNetwork(conf1);
        net1.init();
        MultiLayerNetwork net2 = new MultiLayerNetwork(conf2);
        net2.init();
        INDArray in = Nd4j.rand(3, (28 * 28));
        INDArray labels = TestUtils.randomOneHot(3, 10);
        INDArray out1 = net1.output(in);
        INDArray out2 = net2.output(in);
        Assert.assertEquals(out1, out2);
        Assert.assertEquals(net1.params(), net2.params());
        net1.fit(in, labels);
        net2.fit(in, labels);
        Assert.assertEquals(net1.params(), net2.params());
    }

    @Test
    public void testLSTMCacheModeSimple() {
        for (boolean graves : new boolean[]{ true, false }) {
            MultiLayerConfiguration conf1 = CacheModeTest.getConfLSTM(NONE, graves);
            MultiLayerConfiguration conf2 = CacheModeTest.getConfLSTM(DEVICE, graves);
            MultiLayerNetwork net1 = new MultiLayerNetwork(conf1);
            net1.init();
            MultiLayerNetwork net2 = new MultiLayerNetwork(conf2);
            net2.init();
            INDArray in = Nd4j.rand(new int[]{ 3, 3, 10 });
            INDArray labels = TestUtils.randomOneHotTimeSeries(3, 10, 10);
            INDArray out1 = net1.output(in);
            INDArray out2 = net2.output(in);
            Assert.assertEquals(out1, out2);
            Assert.assertEquals(net1.params(), net2.params());
            net1.fit(in, labels);
            net2.fit(in, labels);
            Assert.assertEquals(net1.params(), net2.params());
        }
    }

    @Test
    public void testConvCacheModeSimpleCG() {
        ComputationGraphConfiguration conf1 = CacheModeTest.getConfCG(NONE);
        ComputationGraphConfiguration conf2 = CacheModeTest.getConfCG(DEVICE);
        ComputationGraph net1 = new ComputationGraph(conf1);
        net1.init();
        ComputationGraph net2 = new ComputationGraph(conf2);
        net2.init();
        INDArray in = Nd4j.rand(3, (28 * 28));
        INDArray labels = TestUtils.randomOneHot(3, 10);
        INDArray out1 = net1.outputSingle(in);
        INDArray out2 = net2.outputSingle(in);
        Assert.assertEquals(out1, out2);
        Assert.assertEquals(net1.params(), net2.params());
        net1.fit(new org.nd4j.linalg.dataset.DataSet(in, labels));
        net2.fit(new org.nd4j.linalg.dataset.DataSet(in, labels));
        Assert.assertEquals(net1.params(), net2.params());
    }
}

