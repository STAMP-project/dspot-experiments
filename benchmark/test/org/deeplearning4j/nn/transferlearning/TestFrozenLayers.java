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
package org.deeplearning4j.nn.transferlearning;


import LossFunctions.LossFunction.MEAN_ABSOLUTE_ERROR;
import java.util.LinkedHashMap;
import java.util.Map;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.FrozenLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


public class TestFrozenLayers extends BaseDL4JTest {
    @Test
    public void testFrozenMLN() {
        MultiLayerNetwork orig = TestFrozenLayers.getOriginalNet(12345);
        for (double l1 : new double[]{ 0.0, 0.3 }) {
            for (double l2 : new double[]{ 0.0, 0.4 }) {
                System.out.println("--------------------");
                String msg = (("l1=" + l1) + ", l2=") + l2;
                FineTuneConfiguration ftc = new FineTuneConfiguration.Builder().updater(new org.nd4j.linalg.learning.config.Sgd(0.5)).l1(l1).l2(l2).build();
                MultiLayerNetwork transfer = new TransferLearning.Builder(orig).fineTuneConfiguration(ftc).setFeatureExtractor(4).removeOutputLayer().addLayer(new OutputLayer.Builder().nIn(64).nOut(10).lossFunction(MEAN_ABSOLUTE_ERROR).build()).build();
                Assert.assertEquals(6, transfer.getnLayers());
                for (int i = 0; i < 5; i++) {
                    Assert.assertTrue(((transfer.getLayer(i)) instanceof FrozenLayer));
                }
                Map<String, INDArray> paramsBefore = new LinkedHashMap<>();
                for (Map.Entry<String, INDArray> entry : transfer.paramTable().entrySet()) {
                    paramsBefore.put(entry.getKey(), entry.getValue().dup());
                }
                for (int i = 0; i < 20; i++) {
                    INDArray f = Nd4j.rand(new int[]{ 16, 1, 28, 28 });
                    INDArray l = Nd4j.rand(new int[]{ 16, 10 });
                    transfer.fit(f, l);
                }
                for (Map.Entry<String, INDArray> entry : transfer.paramTable().entrySet()) {
                    String s = (msg + " - ") + (entry.getKey());
                    if (entry.getKey().startsWith("5_")) {
                        // Non-frozen layer
                        Assert.assertNotEquals(s, paramsBefore.get(entry.getKey()), entry.getValue());
                    } else {
                        Assert.assertEquals(s, paramsBefore.get(entry.getKey()), entry.getValue());
                    }
                }
            }
        }
    }

    @Test
    public void testFrozenCG() {
        ComputationGraph orig = TestFrozenLayers.getOriginalGraph(12345);
        for (double l1 : new double[]{ 0.0, 0.3 }) {
            for (double l2 : new double[]{ 0.0, 0.4 }) {
                String msg = (("l1=" + l1) + ", l2=") + l2;
                FineTuneConfiguration ftc = new FineTuneConfiguration.Builder().updater(new org.nd4j.linalg.learning.config.Sgd(0.5)).l1(l1).l2(l2).build();
                ComputationGraph transfer = new TransferLearning.GraphBuilder(orig).fineTuneConfiguration(ftc).setFeatureExtractor("4").removeVertexAndConnections("5").addLayer("5", new OutputLayer.Builder().nIn(64).nOut(10).lossFunction(MEAN_ABSOLUTE_ERROR).build(), "4").setOutputs("5").build();
                Assert.assertEquals(6, transfer.getNumLayers());
                for (int i = 0; i < 5; i++) {
                    Assert.assertTrue(((transfer.getLayer(i)) instanceof FrozenLayer));
                }
                Map<String, INDArray> paramsBefore = new LinkedHashMap<>();
                for (Map.Entry<String, INDArray> entry : transfer.paramTable().entrySet()) {
                    paramsBefore.put(entry.getKey(), entry.getValue().dup());
                }
                for (int i = 0; i < 20; i++) {
                    INDArray f = Nd4j.rand(new int[]{ 16, 1, 28, 28 });
                    INDArray l = Nd4j.rand(new int[]{ 16, 10 });
                    transfer.fit(new INDArray[]{ f }, new INDArray[]{ l });
                }
                for (Map.Entry<String, INDArray> entry : transfer.paramTable().entrySet()) {
                    String s = (msg + " - ") + (entry.getKey());
                    if (entry.getKey().startsWith("5_")) {
                        // Non-frozen layer
                        Assert.assertNotEquals(s, paramsBefore.get(entry.getKey()), entry.getValue());
                    } else {
                        Assert.assertEquals(s, paramsBefore.get(entry.getKey()), entry.getValue());
                    }
                }
            }
        }
    }
}

