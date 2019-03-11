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
package org.deeplearning4j.nn.conf.weightnoise;


import Activation.SOFTMAX;
import WeightInit.ONES;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.schedule.ScheduleType;


public class TestWeightNoise extends BaseDL4JTest {
    @Test
    public void testWeightNoiseConfigJson() {
        IWeightNoise[] weightNoises = new IWeightNoise[]{ new DropConnect(0.5), new DropConnect(new org.nd4j.linalg.schedule.SigmoidSchedule(ScheduleType.ITERATION, 0.5, 0.5, 100)), new WeightNoise(new NormalDistribution(0, 0.1)) };
        for (IWeightNoise wn : weightNoises) {
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().weightNoise(wn).list().layer(new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(new DenseLayer.Builder().nIn(10).nOut(10).weightNoise(new DropConnect(0.25)).build()).layer(new OutputLayer.Builder().nIn(10).nOut(10).activation(SOFTMAX).build()).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            Assert.assertEquals(wn, getWeightNoise());
            Assert.assertEquals(new DropConnect(0.25), getWeightNoise());
            Assert.assertEquals(wn, getWeightNoise());
            TestUtils.testModelSerialization(net);
            ComputationGraphConfiguration conf2 = new NeuralNetConfiguration.Builder().weightNoise(wn).graphBuilder().addInputs("in").layer("0", new DenseLayer.Builder().nIn(10).nOut(10).build(), "in").layer("1", new DenseLayer.Builder().nIn(10).nOut(10).weightNoise(new DropConnect(0.25)).build(), "0").layer("2", new OutputLayer.Builder().nIn(10).nOut(10).activation(SOFTMAX).build(), "1").setOutputs("2").build();
            ComputationGraph graph = new ComputationGraph(conf2);
            graph.init();
            Assert.assertEquals(wn, getWeightNoise());
            Assert.assertEquals(new DropConnect(0.25), getWeightNoise());
            Assert.assertEquals(wn, getWeightNoise());
            TestUtils.testModelSerialization(graph);
            graph.fit(new DataSet(Nd4j.create(1, 10), Nd4j.create(1, 10)));
        }
    }

    @Test
    public void testCalls() {
        List<DataSet> trainData = new ArrayList<>();
        trainData.add(new DataSet(Nd4j.rand(5, 10), Nd4j.rand(5, 10)));
        trainData.add(new DataSet(Nd4j.rand(5, 10), Nd4j.rand(5, 10)));
        trainData.add(new DataSet(Nd4j.rand(5, 10), Nd4j.rand(5, 10)));
        List<List<TestWeightNoise.WeightNoiseCall>> expCalls = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<TestWeightNoise.WeightNoiseCall> expCallsForLayer = new ArrayList<>();
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(i, "W", 0, 0, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(i, "b", 0, 0, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(i, "W", 1, 0, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(i, "b", 1, 0, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(i, "W", 2, 0, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(i, "b", 2, 0, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(i, "W", 3, 1, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(i, "b", 3, 1, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(i, "W", 4, 1, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(i, "b", 4, 1, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(i, "W", 5, 1, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(i, "b", 5, 1, true));
            // 2 test calls
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(i, "W", 6, 2, false));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(i, "b", 6, 2, false));
            expCalls.add(expCallsForLayer);
        }
        TestWeightNoise.CustomWeightNoise wn1 = new TestWeightNoise.CustomWeightNoise();
        TestWeightNoise.CustomWeightNoise wn2 = new TestWeightNoise.CustomWeightNoise();
        TestWeightNoise.CustomWeightNoise wn3 = new TestWeightNoise.CustomWeightNoise();
        List<TestWeightNoise.CustomWeightNoise> list = Arrays.asList(wn1, wn2, wn3);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(new DenseLayer.Builder().nIn(10).nOut(10).weightNoise(wn1).build()).layer(new DenseLayer.Builder().nIn(10).nOut(10).weightNoise(wn2).build()).layer(new OutputLayer.Builder().nIn(10).nOut(10).activation(SOFTMAX).weightNoise(wn3).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.fit(new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(trainData.iterator()));
        net.fit(new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(trainData.iterator()));
        net.output(trainData.get(0).getFeatures());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(expCalls.get(i), getAllCalls());
        }
        wn1 = new TestWeightNoise.CustomWeightNoise();
        wn2 = new TestWeightNoise.CustomWeightNoise();
        wn3 = new TestWeightNoise.CustomWeightNoise();
        list = Arrays.asList(wn1, wn2, wn3);
        ComputationGraphConfiguration conf2 = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").layer("0", new DenseLayer.Builder().nIn(10).nOut(10).weightNoise(wn1).build(), "in").layer("1", new DenseLayer.Builder().nIn(10).nOut(10).weightNoise(wn2).build(), "0").layer("2", new OutputLayer.Builder().nIn(10).nOut(10).activation(SOFTMAX).weightNoise(wn3).build(), "1").setOutputs("2").build();
        ComputationGraph graph = new ComputationGraph(conf2);
        graph.init();
        int[] layerIdxs = new int[]{ graph.getLayer(0).getIndex(), graph.getLayer(1).getIndex(), graph.getLayer(2).getIndex() };
        expCalls.clear();
        for (int i = 0; i < 3; i++) {
            List<TestWeightNoise.WeightNoiseCall> expCallsForLayer = new ArrayList<>();
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(layerIdxs[i], "W", 0, 0, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(layerIdxs[i], "b", 0, 0, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(layerIdxs[i], "W", 1, 0, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(layerIdxs[i], "b", 1, 0, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(layerIdxs[i], "W", 2, 0, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(layerIdxs[i], "b", 2, 0, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(layerIdxs[i], "W", 3, 1, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(layerIdxs[i], "b", 3, 1, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(layerIdxs[i], "W", 4, 1, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(layerIdxs[i], "b", 4, 1, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(layerIdxs[i], "W", 5, 1, true));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(layerIdxs[i], "b", 5, 1, true));
            // 2 test calls
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(layerIdxs[i], "W", 6, 2, false));
            expCallsForLayer.add(new TestWeightNoise.WeightNoiseCall(layerIdxs[i], "b", 6, 2, false));
            expCalls.add(expCallsForLayer);
        }
        graph.fit(new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(trainData.iterator()));
        graph.fit(new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(trainData.iterator()));
        graph.output(trainData.get(0).getFeatures());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(String.valueOf(i), expCalls.get(i), getAllCalls());
        }
    }

    @Data
    private static class CustomWeightNoise implements IWeightNoise {
        private List<TestWeightNoise.WeightNoiseCall> allCalls = new ArrayList<>();

        @Override
        public INDArray getParameter(Layer layer, String paramKey, int iteration, int epoch, boolean train, LayerWorkspaceMgr workspaceMgr) {
            allCalls.add(new TestWeightNoise.WeightNoiseCall(layer.getIndex(), paramKey, iteration, epoch, train));
            return layer.getParam(paramKey);
        }

        @Override
        public IWeightNoise clone() {
            return new TestWeightNoise.CustomWeightNoise();
        }
    }

    @AllArgsConstructor
    @Data
    private static class WeightNoiseCall {
        private int layerIdx;

        private String paramKey;

        private int iter;

        private int epoch;

        private boolean train;
    }

    @Test
    public void testDropConnectValues() {
        Nd4j.getRandom().setSeed(12345);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().weightInit(ONES).list().layer(new OutputLayer.Builder().nIn(10).nOut(10).activation(SOFTMAX).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        Layer l = net.getLayer(0);
        DropConnect d = new DropConnect(0.5);
        INDArray outTest = d.getParameter(l, "W", 0, 0, false, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertTrue(((l.getParam("W")) == outTest));// Should be same object

        INDArray outTrain = d.getParameter(l, "W", 0, 0, true, LayerWorkspaceMgr.noWorkspaces());
        Assert.assertNotEquals(l.getParam("W"), outTrain);
        Assert.assertEquals(l.getParam("W"), Nd4j.ones(10, 10));
        int countZeros = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.longer.MatchCondition(outTrain, Conditions.equals(0))).getInt(0);
        int countOnes = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.longer.MatchCondition(outTrain, Conditions.equals(1))).getInt(0);
        Assert.assertEquals(100, (countZeros + countOnes));// Should only be 0 or 2

        // Stochastic, but this should hold for most cases
        Assert.assertTrue(((countZeros >= 25) && (countZeros <= 75)));
        Assert.assertTrue(((countOnes >= 25) && (countOnes <= 75)));
    }
}

