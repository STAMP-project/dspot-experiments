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
package org.deeplearning4j.nn.conf.layers;


import Activation.SIGMOID;
import Activation.SOFTMAX;
import ComputationGraphConfiguration.GraphBuilder;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import Updater.RMSPROP;
import WeightInit.RELU_UNIFORM;
import WeightInit.XAVIER;
import java.util.HashMap;
import java.util.Map;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.Distribution;
import org.deeplearning4j.nn.conf.distribution.GaussianDistribution;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.weightnoise.DropConnect;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.schedule.ScheduleType;


public class LayerConfigValidationTest extends BaseDL4JTest {
    @Test
    public void testDropConnect() {
        // Warning thrown only since some layers may not have l1 or l2
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.1)).weightNoise(new DropConnect(0.5)).list().layer(0, new DenseLayer.Builder().nIn(2).nOut(2).build()).layer(1, new DenseLayer.Builder().nIn(2).nOut(2).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
    }

    @Test
    public void testL1L2NotSet() {
        // Warning thrown only since some layers may not have l1 or l2
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.3)).list().layer(0, new DenseLayer.Builder().nIn(2).nOut(2).build()).layer(1, new DenseLayer.Builder().nIn(2).nOut(2).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
    }

    @Test
    public void testWeightInitDistNotSet() {
        // Warning thrown only since global dist can be set with a different weight init locally
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.3)).dist(new GaussianDistribution(0.001, 2)).list().layer(0, new DenseLayer.Builder().nIn(2).nOut(2).build()).layer(1, new DenseLayer.Builder().nIn(2).nOut(2).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
    }

    @Test
    public void testNesterovsNotSetGlobal() {
        // Warnings only thrown
        Map<Integer, Double> testMomentumAfter = new HashMap<>();
        testMomentumAfter.put(0, 0.1);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Nesterovs(1.0, new org.nd4j.linalg.schedule.MapSchedule(ScheduleType.ITERATION, testMomentumAfter))).list().layer(0, new DenseLayer.Builder().nIn(2).nOut(2).build()).layer(1, new DenseLayer.Builder().nIn(2).nOut(2).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
    }

    @Test
    public void testCompGraphNullLayer() {
        ComputationGraphConfiguration.GraphBuilder gb = /* Graph Builder */
        new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(0.01)).seed(42).miniBatch(false).l1(0.2).l2(0.2).updater(RMSPROP).graphBuilder().addInputs("in").addLayer(("L" + 1), new GravesLSTM.Builder().nIn(20).updater(RMSPROP).nOut(10).weightInit(XAVIER).dropOut(0.4).l1(0.3).activation(SIGMOID).build(), "in").addLayer("output", new RnnOutputLayer.Builder().nIn(20).nOut(10).activation(SOFTMAX).weightInit(RELU_UNIFORM).build(), ("L" + 1)).setOutputs("output");
        ComputationGraphConfiguration conf = gb.build();
        ComputationGraph cg = new ComputationGraph(conf);
        cg.init();
    }

    @Test
    public void testPredefinedConfigValues() {
        double expectedMomentum = 0.9;
        double expectedAdamMeanDecay = 0.9;
        double expectedAdamVarDecay = 0.999;
        double expectedRmsDecay = 0.95;
        Distribution expectedDist = new NormalDistribution(0, 1);
        double expectedL1 = 0.0;
        double expectedL2 = 0.0;
        // Nesterovs Updater
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Nesterovs(0.9)).list().layer(0, new DenseLayer.Builder().nIn(2).nOut(2).l2(0.5).build()).layer(1, new DenseLayer.Builder().nIn(2).nOut(2).updater(new Nesterovs(0.3, 0.4)).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        BaseLayer layerConf = ((BaseLayer) (net.getLayer(0).conf().getLayer()));
        Assert.assertEquals(expectedMomentum, getMomentum(), 0.001);
        Assert.assertNull(TestUtils.getL1Reg(layerConf.getRegularization()));
        Assert.assertEquals(0.5, TestUtils.getL2(layerConf), 0.001);
        BaseLayer layerConf1 = ((BaseLayer) (net.getLayer(1).conf().getLayer()));
        Assert.assertEquals(0.4, getMomentum(), 0.001);
        // Adam Updater
        conf = new NeuralNetConfiguration.Builder().updater(new Adam(0.3)).weightInit(new org.deeplearning4j.nn.weights.WeightInitDistribution(expectedDist)).list().layer(0, new DenseLayer.Builder().nIn(2).nOut(2).l2(0.5).l1(0.3).build()).layer(1, new DenseLayer.Builder().nIn(2).nOut(2).build()).build();
        net = new MultiLayerNetwork(conf);
        net.init();
        layerConf = ((BaseLayer) (net.getLayer(0).conf().getLayer()));
        Assert.assertEquals(0.3, TestUtils.getL1(layerConf), 0.001);
        Assert.assertEquals(0.5, TestUtils.getL2(layerConf), 0.001);
        layerConf1 = ((BaseLayer) (net.getLayer(1).conf().getLayer()));
        Assert.assertEquals(expectedAdamMeanDecay, getBeta1(), 0.001);
        Assert.assertEquals(expectedAdamVarDecay, getBeta2(), 0.001);
        Assert.assertEquals(new org.deeplearning4j.nn.weights.WeightInitDistribution(expectedDist), layerConf1.getWeightInitFn());
        Assert.assertNull(TestUtils.getL1Reg(layerConf1.getRegularization()));
        Assert.assertNull(TestUtils.getL2Reg(layerConf1.getRegularization()));
        // RMSProp Updater
        conf = new NeuralNetConfiguration.Builder().updater(new RmsProp(0.3)).list().layer(0, new DenseLayer.Builder().nIn(2).nOut(2).build()).layer(1, new DenseLayer.Builder().nIn(2).nOut(2).updater(new RmsProp(0.3, 0.4, RmsProp.DEFAULT_RMSPROP_EPSILON)).build()).build();
        net = new MultiLayerNetwork(conf);
        net.init();
        layerConf = ((BaseLayer) (net.getLayer(0).conf().getLayer()));
        Assert.assertEquals(expectedRmsDecay, getRmsDecay(), 0.001);
        Assert.assertNull(TestUtils.getL1Reg(layerConf.getRegularization()));
        Assert.assertNull(TestUtils.getL2Reg(layerConf.getRegularization()));
        layerConf1 = ((BaseLayer) (net.getLayer(1).conf().getLayer()));
        Assert.assertEquals(0.4, getRmsDecay(), 0.001);
    }
}

