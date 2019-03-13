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
package org.deeplearning4j.nn.conf.dropout;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.primitives.Pair;


public class TestDropout extends BaseDL4JTest {
    @Test
    public void testBasicConfig() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().dropOut(0.6).list().layer(build()).layer(build()).layer(build()).build();
        Assert.assertEquals(new Dropout(0.6), conf.getConf(0).getLayer().getIDropout());
        Assert.assertEquals(new Dropout(0.7), conf.getConf(1).getLayer().getIDropout());
        Assert.assertEquals(new AlphaDropout(0.5), conf.getConf(2).getLayer().getIDropout());
        ComputationGraphConfiguration conf2 = new NeuralNetConfiguration.Builder().dropOut(0.6).graphBuilder().addInputs("in").addLayer("0", build(), "in").addLayer("1", build(), "0").addLayer("2", build(), "1").setOutputs("2").build();
        Assert.assertEquals(new Dropout(0.6), getLayerConf().getLayer().getIDropout());
        Assert.assertEquals(new Dropout(0.7), getLayerConf().getLayer().getIDropout());
        Assert.assertEquals(new AlphaDropout(0.5), getLayerConf().getLayer().getIDropout());
    }

    @Test
    public void testCalls() {
        TestDropout.CustomDropout d1 = new TestDropout.CustomDropout();
        TestDropout.CustomDropout d2 = new TestDropout.CustomDropout();
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(build()).layer(build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        List<DataSet> l = new ArrayList<>();
        l.add(new DataSet(Nd4j.rand(5, 4), Nd4j.rand(5, 3)));
        l.add(new DataSet(Nd4j.rand(5, 4), Nd4j.rand(5, 3)));
        l.add(new DataSet(Nd4j.rand(5, 4), Nd4j.rand(5, 3)));
        DataSetIterator iter = new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(l);
        net.fit(iter);
        net.fit(iter);
        List<Pair<Integer, Integer>> expList = Arrays.asList(new Pair(0, 0), new Pair(1, 0), new Pair(2, 0), new Pair(3, 1), new Pair(4, 1), new Pair(5, 1));
        Assert.assertEquals(expList, getAllCalls());
        Assert.assertEquals(expList, getAllCalls());
        Assert.assertEquals(expList, getAllReverseCalls());
        Assert.assertEquals(expList, getAllReverseCalls());
        d1 = new TestDropout.CustomDropout();
        d2 = new TestDropout.CustomDropout();
        ComputationGraphConfiguration conf2 = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").addLayer("0", build(), "in").addLayer("1", build(), "0").setOutputs("1").build();
        ComputationGraph net2 = new ComputationGraph(conf2);
        net2.init();
        net2.fit(iter);
        net2.fit(iter);
        Assert.assertEquals(expList, getAllCalls());
        Assert.assertEquals(expList, getAllCalls());
    }

    @Data
    public static class CustomDropout implements IDropout {
        private List<Pair<Integer, Integer>> allCalls = new ArrayList<>();

        private List<Pair<Integer, Integer>> allReverseCalls = new ArrayList<>();

        @Override
        public INDArray applyDropout(INDArray inputActivations, INDArray result, int iteration, int epoch, LayerWorkspaceMgr workspaceMgr) {
            allCalls.add(new Pair(iteration, epoch));
            return inputActivations;
        }

        @Override
        public INDArray backprop(INDArray gradAtOutput, INDArray gradAtInput, int iteration, int epoch) {
            allReverseCalls.add(new Pair(iteration, epoch));
            return gradAtInput;
        }

        @Override
        public void clear() {
        }

        @Override
        public IDropout clone() {
            return this;
        }
    }

    @Test
    public void testSerialization() {
        IDropout[] dropouts = new IDropout[]{ new Dropout(0.5), new AlphaDropout(0.5), new GaussianDropout(0.1), new GaussianNoise(0.1) };
        for (IDropout id : dropouts) {
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().dropOut(id).list().layer(build()).layer(build()).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            TestUtils.testModelSerialization(net);
            ComputationGraphConfiguration conf2 = new NeuralNetConfiguration.Builder().dropOut(id).graphBuilder().addInputs("in").addLayer("0", build(), "in").addLayer("1", build(), "0").setOutputs("1").build();
            ComputationGraph net2 = new ComputationGraph(conf2);
            net2.init();
            TestUtils.testModelSerialization(net2);
        }
    }

    @Test
    public void testDropoutValues() {
        Nd4j.getRandom().setSeed(12345);
        Dropout d = new Dropout(0.5);
        INDArray in = Nd4j.ones(10, 10);
        INDArray out = d.applyDropout(in, Nd4j.create(10, 10), 0, 0, LayerWorkspaceMgr.noWorkspacesImmutable());
        Assert.assertEquals(in, Nd4j.ones(10, 10));
        int countZeros = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.longer.MatchCondition(out, Conditions.equals(0))).getInt(0);
        int countTwos = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.longer.MatchCondition(out, Conditions.equals(2))).getInt(0);
        Assert.assertEquals(100, (countZeros + countTwos));// Should only be 0 or 2

        // Stochastic, but this should hold for most cases
        Assert.assertTrue(((countZeros >= 25) && (countZeros <= 75)));
        Assert.assertTrue(((countTwos >= 25) && (countTwos <= 75)));
        // Test schedule:
        d = new Dropout(build());
        for (int i = 0; i < 10; i++) {
            out = d.applyDropout(in, Nd4j.create(in.shape()), i, 0, LayerWorkspaceMgr.noWorkspacesImmutable());
            Assert.assertEquals(in, Nd4j.ones(10, 10));
            countZeros = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.longer.MatchCondition(out, Conditions.equals(0))).getInt(0);
            if (i < 5) {
                countTwos = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.longer.MatchCondition(out, Conditions.equals(2))).getInt(0);
                Assert.assertEquals(String.valueOf(i), 100, (countZeros + countTwos));// Should only be 0 or 2

                // Stochastic, but this should hold for most cases
                Assert.assertTrue(((countZeros >= 25) && (countZeros <= 75)));
                Assert.assertTrue(((countTwos >= 25) && (countTwos <= 75)));
            } else {
                int countInverse = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.reduce.longer.MatchCondition(out, Conditions.equals((1.0 / 0.1)))).getInt(0);
                Assert.assertEquals(100, (countZeros + countInverse));// Should only be 0 or 10

                // Stochastic, but this should hold for most cases
                Assert.assertTrue((countZeros >= 80));
                Assert.assertTrue((countInverse <= 20));
            }
        }
    }

    @Test
    public void testGaussianDropoutValues() {
        Nd4j.getRandom().setSeed(12345);
        GaussianDropout d = new GaussianDropout(0.1);// sqrt(0.1/(1-0.1)) = 0.3333 stdev

        INDArray in = Nd4j.ones(50, 50);
        INDArray out = d.applyDropout(in, Nd4j.create(in.shape()), 0, 0, LayerWorkspaceMgr.noWorkspacesImmutable());
        Assert.assertEquals(in, Nd4j.ones(50, 50));
        double mean = out.meanNumber().doubleValue();
        double stdev = out.stdNumber().doubleValue();
        Assert.assertEquals(1.0, mean, 0.05);
        Assert.assertEquals(0.333, stdev, 0.02);
    }

    @Test
    public void testGaussianNoiseValues() {
        Nd4j.getRandom().setSeed(12345);
        GaussianNoise d = new GaussianNoise(0.1);// sqrt(0.1/(1-0.1)) = 0.3333 stdev

        INDArray in = Nd4j.ones(50, 50);
        INDArray out = d.applyDropout(in, Nd4j.create(in.shape()), 0, 0, LayerWorkspaceMgr.noWorkspacesImmutable());
        Assert.assertEquals(in, Nd4j.ones(50, 50));
        double mean = out.meanNumber().doubleValue();
        double stdev = out.stdNumber().doubleValue();
        Assert.assertEquals(1.0, mean, 0.05);
        Assert.assertEquals(0.1, stdev, 0.01);
    }

    @Test
    public void testAlphaDropoutValues() {
        Nd4j.getRandom().setSeed(12345);
        double p = 0.4;
        AlphaDropout d = new AlphaDropout(p);
        double SELU_ALPHA = 1.6732632423543772;
        double SELU_LAMBDA = 1.0507009873554805;
        double alphaPrime = (-SELU_LAMBDA) * SELU_ALPHA;
        double a = 1.0 / (Math.sqrt((p + (((alphaPrime * alphaPrime) * p) * (1 - p)))));
        double b = (((-1.0) / (Math.sqrt((p + (((alphaPrime * alphaPrime) * p) * (1 - p)))))) * (1 - p)) * alphaPrime;
        double actA = d.a(p);
        double actB = d.b(p);
        Assert.assertEquals(a, actA, 1.0E-6);
        Assert.assertEquals(b, actB, 1.0E-6);
        INDArray in = Nd4j.ones(10, 10);
        INDArray out = d.applyDropout(in, Nd4j.create(in.shape()), 0, 0, LayerWorkspaceMgr.noWorkspacesImmutable());
        int countValueDropped = 0;
        int countEqn = 0;
        double eqn = (a * 1) + b;
        double valueDropped = (a * alphaPrime) + b;
        for (int i = 0; i < 100; i++) {
            double v = out.getDouble(i);
            if ((v >= (valueDropped - 1.0E-6)) && (v <= (valueDropped + 1.0E-6))) {
                countValueDropped++;
            } else
                if ((v >= (eqn - 1.0E-6)) && (v <= (eqn + 1.0E-6))) {
                    countEqn++;
                }

        }
        Assert.assertEquals(100, (countValueDropped + countEqn));
        Assert.assertTrue(((countValueDropped >= 25) && (countValueDropped <= 75)));
        Assert.assertTrue(((countEqn >= 25) && (countEqn <= 75)));
    }

    @Test
    public void testSpatialDropout5DValues() {
        Nd4j.getRandom().setSeed(12345);
        SpatialDropout d = new SpatialDropout(0.5);
        INDArray in = Nd4j.ones(10, 10, 5, 5, 5);
        INDArray out = d.applyDropout(in, Nd4j.create(in.shape()), 0, 0, LayerWorkspaceMgr.noWorkspacesImmutable());
        Assert.assertEquals(in, Nd4j.ones(10, 10, 5, 5, 5));
        // Now, we expect all values for a given depth to be the same... 0 or 2
        int countZero = 0;
        int countTwo = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double value = out.getDouble(i, j, 0, 0, 0);
                Assert.assertTrue(((value == 0) || (value == 2.0)));
                INDArray exp = Nd4j.valueArrayOf(new int[]{ 5, 5, 5 }, value);
                INDArray act = out.get(point(i), point(j), all(), all(), all());
                Assert.assertEquals(exp, act);
                if (value == 0.0) {
                    countZero++;
                } else {
                    countTwo++;
                }
            }
        }
        // Stochastic, but this should hold for most cases
        Assert.assertTrue(((countZero >= 25) && (countZero <= 75)));
        Assert.assertTrue(((countTwo >= 25) && (countTwo <= 75)));
        // Test schedule:
        d = new SpatialDropout(build());
        for (int i = 0; i < 10; i++) {
            out = d.applyDropout(in, Nd4j.create(in.shape()), i, 0, LayerWorkspaceMgr.noWorkspacesImmutable());
            Assert.assertEquals(in, Nd4j.ones(10, 10, 5, 5, 5));
            if (i < 5) {
                countZero = 0;
                countTwo = 0;
                for (int m = 0; m < 10; m++) {
                    for (int j = 0; j < 10; j++) {
                        double value = out.getDouble(m, j, 0, 0, 0);
                        Assert.assertTrue(((value == 0) || (value == 2.0)));
                        INDArray exp = Nd4j.valueArrayOf(new int[]{ 5, 5, 5 }, value);
                        INDArray act = out.get(point(m), point(j), all(), all(), all());
                        Assert.assertEquals(exp, act);
                        if (value == 0.0) {
                            countZero++;
                        } else {
                            countTwo++;
                        }
                    }
                }
                // Stochastic, but this should hold for most cases
                Assert.assertTrue(((countZero >= 25) && (countZero <= 75)));
                Assert.assertTrue(((countTwo >= 25) && (countTwo <= 75)));
            } else {
                countZero = 0;
                int countInverse = 0;
                for (int m = 0; m < 10; m++) {
                    for (int j = 0; j < 10; j++) {
                        double value = out.getDouble(m, j, 0, 0, 0);
                        Assert.assertTrue(((value == 0) || (value == 10.0)));
                        INDArray exp = Nd4j.valueArrayOf(new int[]{ 5, 5, 5 }, value);
                        INDArray act = out.get(point(m), point(j), all(), all(), all());
                        Assert.assertEquals(exp, act);
                        if (value == 0.0) {
                            countZero++;
                        } else {
                            countInverse++;
                        }
                    }
                }
                // Stochastic, but this should hold for most cases
                Assert.assertTrue((countZero >= 80));
                Assert.assertTrue((countInverse <= 20));
            }
        }
    }

    @Test
    public void testSpatialDropoutValues() {
        Nd4j.getRandom().setSeed(12345);
        SpatialDropout d = new SpatialDropout(0.5);
        INDArray in = Nd4j.ones(10, 10, 5, 5);
        INDArray out = d.applyDropout(in, Nd4j.create(in.shape()), 0, 0, LayerWorkspaceMgr.noWorkspacesImmutable());
        Assert.assertEquals(in, Nd4j.ones(10, 10, 5, 5));
        // Now, we expect all values for a given depth to be the same... 0 or 2
        int countZero = 0;
        int countTwo = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double value = out.getDouble(i, j, 0, 0);
                Assert.assertTrue(((value == 0) || (value == 2.0)));
                INDArray exp = Nd4j.valueArrayOf(new int[]{ 5, 5 }, value);
                INDArray act = out.get(point(i), point(j), all(), all());
                Assert.assertEquals(exp, act);
                if (value == 0.0) {
                    countZero++;
                } else {
                    countTwo++;
                }
            }
        }
        // Stochastic, but this should hold for most cases
        Assert.assertTrue(((countZero >= 25) && (countZero <= 75)));
        Assert.assertTrue(((countTwo >= 25) && (countTwo <= 75)));
        // Test schedule:
        d = new SpatialDropout(build());
        for (int i = 0; i < 10; i++) {
            out = d.applyDropout(in, Nd4j.create(in.shape()), i, 0, LayerWorkspaceMgr.noWorkspacesImmutable());
            Assert.assertEquals(in, Nd4j.ones(10, 10, 5, 5));
            if (i < 5) {
                countZero = 0;
                countTwo = 0;
                for (int m = 0; m < 10; m++) {
                    for (int j = 0; j < 10; j++) {
                        double value = out.getDouble(m, j, 0, 0);
                        Assert.assertTrue(((value == 0) || (value == 2.0)));
                        INDArray exp = Nd4j.valueArrayOf(new int[]{ 5, 5 }, value);
                        INDArray act = out.get(point(m), point(j), all(), all());
                        Assert.assertEquals(exp, act);
                        if (value == 0.0) {
                            countZero++;
                        } else {
                            countTwo++;
                        }
                    }
                }
                // Stochastic, but this should hold for most cases
                Assert.assertTrue(((countZero >= 25) && (countZero <= 75)));
                Assert.assertTrue(((countTwo >= 25) && (countTwo <= 75)));
            } else {
                countZero = 0;
                int countInverse = 0;
                for (int m = 0; m < 10; m++) {
                    for (int j = 0; j < 10; j++) {
                        double value = out.getDouble(m, j, 0, 0);
                        Assert.assertTrue(((value == 0) || (value == 10.0)));
                        INDArray exp = Nd4j.valueArrayOf(new int[]{ 5, 5 }, value);
                        INDArray act = out.get(point(m), point(j), all(), all());
                        Assert.assertEquals(exp, act);
                        if (value == 0.0) {
                            countZero++;
                        } else {
                            countInverse++;
                        }
                    }
                }
                // Stochastic, but this should hold for most cases
                Assert.assertTrue((countZero >= 80));
                Assert.assertTrue((countInverse <= 20));
            }
        }
    }

    @Test
    public void testSpatialDropoutValues3D() {
        Nd4j.getRandom().setSeed(12345);
        SpatialDropout d = new SpatialDropout(0.5);
        INDArray in = Nd4j.ones(10, 8, 12);
        INDArray out = d.applyDropout(in, Nd4j.create(in.shape()), 0, 0, LayerWorkspaceMgr.noWorkspacesImmutable());
        Assert.assertEquals(in, Nd4j.ones(10, 8, 12));
        // Now, we expect all values for a given depth to be the same... 0 or 2
        int countZero = 0;
        int countTwo = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 8; j++) {
                double value = out.getDouble(i, j, 0);
                Assert.assertTrue(((value == 0) || (value == 2.0)));
                INDArray exp = Nd4j.valueArrayOf(new int[]{ 1, 12 }, value);
                INDArray act = out.get(point(i), point(j), all());
                Assert.assertEquals(exp, act);
                if (value == 0.0) {
                    countZero++;
                } else {
                    countTwo++;
                }
            }
        }
        // Stochastic, but this should hold for most cases
        Assert.assertTrue(((countZero >= 20) && (countZero <= 60)));
        Assert.assertTrue(((countTwo >= 20) && (countTwo <= 60)));
        // Test schedule:
        d = new SpatialDropout(build());
        for (int i = 0; i < 10; i++) {
            out = d.applyDropout(in, Nd4j.create(in.shape()), i, 0, LayerWorkspaceMgr.noWorkspacesImmutable());
            Assert.assertEquals(in, Nd4j.ones(10, 8, 12));
            if (i < 5) {
                countZero = 0;
                countTwo = 0;
                for (int m = 0; m < 10; m++) {
                    for (int j = 0; j < 8; j++) {
                        double value = out.getDouble(m, j, 0);
                        Assert.assertTrue(((value == 0) || (value == 2.0)));
                        INDArray exp = Nd4j.valueArrayOf(new int[]{ 1, 12 }, value);
                        INDArray act = out.get(point(m), point(j), all());
                        Assert.assertEquals(exp, act);
                        if (value == 0.0) {
                            countZero++;
                        } else {
                            countTwo++;
                        }
                    }
                }
                // Stochastic, but this should hold for most cases
                Assert.assertTrue(((countZero >= 20) && (countZero <= 60)));
                Assert.assertTrue(((countTwo >= 20) && (countTwo <= 60)));
            } else {
                countZero = 0;
                int countInverse = 0;
                for (int m = 0; m < 10; m++) {
                    for (int j = 0; j < 8; j++) {
                        double value = out.getDouble(m, j, 0);
                        Assert.assertTrue(((value == 0) || (value == 10.0)));
                        INDArray exp = Nd4j.valueArrayOf(new int[]{ 1, 12 }, value);
                        INDArray act = out.get(point(m), point(j), all());
                        Assert.assertEquals(exp, act);
                        if (value == 0.0) {
                            countZero++;
                        } else {
                            countInverse++;
                        }
                    }
                }
                // Stochastic, but this should hold for most cases
                Assert.assertTrue((countZero >= 60));
                Assert.assertTrue((countInverse <= 15));
            }
        }
    }

    @Test
    public void testSpatialDropoutJSON() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(build()).build();
        String asJson = conf.toJson();
        MultiLayerConfiguration fromJson = MultiLayerConfiguration.fromJson(asJson);
        Assert.assertEquals(conf, fromJson);
    }
}

