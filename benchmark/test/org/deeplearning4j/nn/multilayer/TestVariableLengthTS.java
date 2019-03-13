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


import Activation.IDENTITY;
import Activation.TANH;
import ArrayType.INPUT;
import LossFunctions.LossFunction;
import LossFunctions.LossFunction.MEAN_ABSOLUTE_ERROR;
import LossFunctions.LossFunction.MSE;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import WeightInit.XAVIER;
import WeightInit.ZERO;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToRnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.deeplearning4j.util.TimeSeriesUtils;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.NoOp;
import org.nd4j.linalg.learning.config.Sgd;

import static PoolingType.AVG;
import static PoolingType.MAX;
import static PoolingType.SUM;


public class TestVariableLengthTS extends BaseDL4JTest {
    @Test
    public void testVariableLengthSimple() {
        // Test: Simple RNN layer + RNNOutputLayer
        // Length of 4 for standard
        // Length of 5 with last time step output mask set to 0
        // Expect the same gradients etc in both cases...
        int[] miniBatchSizes = new int[]{ 1, 2, 5 };
        int nOut = 1;
        Random r = new Random(12345);
        for (int nExamples : miniBatchSizes) {
            Nd4j.getRandom().setSeed(12345);
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(0.1)).seed(12345).list().layer(0, new GravesLSTM.Builder().activation(TANH).nIn(2).nOut(2).build()).layer(1, new RnnOutputLayer.Builder().lossFunction(MSE).nIn(2).nOut(1).activation(TANH).build()).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            INDArray in1 = Nd4j.rand(new int[]{ nExamples, 2, 4 });
            INDArray in2 = Nd4j.rand(new int[]{ nExamples, 2, 5 });
            in2.put(new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 3, true) }, in1);
            Assert.assertEquals(in1, in2.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 4)));
            INDArray labels1 = Nd4j.rand(new int[]{ nExamples, 1, 4 });
            INDArray labels2 = Nd4j.create(nExamples, 1, 5);
            labels2.put(new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 3, true) }, labels1);
            Assert.assertEquals(labels1, labels2.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 4)));
            INDArray labelMask = Nd4j.ones(nExamples, 5);
            for (int j = 0; j < nExamples; j++) {
                labelMask.putScalar(new int[]{ j, 4 }, 0);
            }
            net.setInput(in1);
            net.setLabels(labels1);
            net.computeGradientAndScore();
            double score1 = net.score();
            Gradient g1 = net.gradient();
            net.setInput(in2);
            net.setLabels(labels2);
            net.setLayerMaskArrays(null, labelMask);
            net.computeGradientAndScore();
            double score2 = net.score();
            Gradient g2 = net.gradient();
            // Scores and gradients should be identical for two cases (given mask array)
            Assert.assertEquals(score1, score2, 1.0E-6);
            Map<String, INDArray> g1map = g1.gradientForVariable();
            Map<String, INDArray> g2map = g2.gradientForVariable();
            for (String s : g1map.keySet()) {
                INDArray g1s = g1map.get(s);
                INDArray g2s = g2map.get(s);
                Assert.assertEquals(s, g1s, g2s);
            }
            // Finally: check that the values at the masked outputs don't actually make any differente to:
            // (a) score, (b) gradients
            for (int i = 0; i < nExamples; i++) {
                for (int j = 0; j < nOut; j++) {
                    double d = r.nextDouble();
                    labels2.putScalar(new int[]{ i, j, 4 }, d);
                }
                net.setLabels(labels2);
                net.computeGradientAndScore();
                double score2a = net.score();
                Gradient g2a = net.gradient();
                Assert.assertEquals(score2, score2a, 1.0E-6);
                for (String s : g2map.keySet()) {
                    INDArray g2s = g2map.get(s);
                    INDArray g2sa = g2a.getGradientFor(s);
                    Assert.assertEquals(s, g2s, g2sa);
                }
            }
        }
    }

    @Test
    public void testInputMasking() {
        // Idea: have masking on the input with 2 dense layers on input
        // Ensure that the parameter gradients for the inputs don't depend on the inputs when inputs are masked
        int[] miniBatchSizes = new int[]{ 1, 2, 5 };
        int nIn = 2;
        Random r = new Random(12345);
        for (int nExamples : miniBatchSizes) {
            Nd4j.getRandom().setSeed(1234);
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(0.1)).seed(12345).list().layer(0, new DenseLayer.Builder().activation(TANH).nIn(2).nOut(2).build()).layer(1, new DenseLayer.Builder().activation(TANH).nIn(2).nOut(2).build()).layer(2, new GravesLSTM.Builder().activation(TANH).nIn(2).nOut(2).build()).layer(3, new RnnOutputLayer.Builder().lossFunction(MEAN_ABSOLUTE_ERROR).nIn(2).nOut(1).activation(TANH).build()).inputPreProcessor(0, new RnnToFeedForwardPreProcessor()).inputPreProcessor(2, new FeedForwardToRnnPreProcessor()).build();
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            INDArray in1 = Nd4j.rand(new int[]{ nExamples, 2, 4 });
            INDArray in2 = Nd4j.rand(new int[]{ nExamples, 2, 5 });
            in2.put(new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 3, true) }, in1);
            Assert.assertEquals(in1, in2.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 4)));
            INDArray labels1 = Nd4j.rand(new int[]{ nExamples, 1, 4 });
            INDArray labels2 = Nd4j.create(nExamples, 1, 5);
            labels2.put(new INDArrayIndex[]{ NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 3, true) }, labels1);
            Assert.assertEquals(labels1, labels2.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, 4)));
            INDArray inputMask = Nd4j.ones(nExamples, 5);
            for (int j = 0; j < nExamples; j++) {
                inputMask.putScalar(new int[]{ j, 4 }, 0);
            }
            net.setInput(in1);
            net.setLabels(labels1);
            net.computeGradientAndScore();
            double score1 = net.score();
            Gradient g1 = net.gradient();
            Map<String, INDArray> map1 = g1.gradientForVariable();
            for (String s : map1.keySet()) {
                map1.put(s, map1.get(s).dup());// Note: gradients are a view normally -> second computeGradientAndScore would have modified the original gradient map values...

            }
            net.setInput(in2);
            net.setLabels(labels2);
            net.setLayerMaskArrays(inputMask, null);
            net.computeGradientAndScore();
            double score2 = net.score();
            Gradient g2 = net.gradient();
            net.setInput(in2);
            net.setLabels(labels2);
            net.setLayerMaskArrays(inputMask, null);
            List<INDArray> activations2 = net.feedForward();
            // Scores should differ here: masking the input, not the output. Therefore 4 vs. 5 time step outputs
            Assert.assertNotEquals(score1, score2, 0.005);
            Map<String, INDArray> g1map = g1.gradientForVariable();
            Map<String, INDArray> g2map = g2.gradientForVariable();
            for (String s : g1map.keySet()) {
                INDArray g1s = g1map.get(s);
                INDArray g2s = g2map.get(s);
                System.out.println("-------");
                System.out.println(("Variable: " + s));
                System.out.println(Arrays.toString(g1s.dup().data().asFloat()));
                System.out.println(Arrays.toString(g2s.dup().data().asFloat()));
                Assert.assertNotEquals(s, g1s, g2s);
            }
            // Modify the values at the masked time step, and check that neither the gradients, score or activations change
            for (int j = 0; j < nExamples; j++) {
                for (int k = 0; k < nIn; k++) {
                    in2.putScalar(new int[]{ j, k, 4 }, r.nextDouble());
                }
                net.setInput(in2);
                net.setLayerMaskArrays(inputMask, null);
                net.computeGradientAndScore();
                double score2a = net.score();
                Gradient g2a = net.gradient();
                Assert.assertEquals(score2, score2a, 1.0E-12);
                for (String s : g2.gradientForVariable().keySet()) {
                    Assert.assertEquals(g2.getGradientFor(s), g2a.getGradientFor(s));
                }
                List<INDArray> activations2a = net.feedForward();
                for (int k = 1; k < (activations2.size()); k++) {
                    Assert.assertEquals(activations2.get(k), activations2a.get(k));
                }
            }
            // Finally: check that the activations for the first two (dense) layers are zero at the appropriate time step
            FeedForwardToRnnPreProcessor temp = new FeedForwardToRnnPreProcessor();
            INDArray l0Before = activations2.get(1);
            INDArray l1Before = activations2.get(2);
            INDArray l0After = temp.preProcess(l0Before, nExamples, LayerWorkspaceMgr.noWorkspaces());
            INDArray l1After = temp.preProcess(l1Before, nExamples, LayerWorkspaceMgr.noWorkspaces());
            for (int j = 0; j < nExamples; j++) {
                for (int k = 0; k < nIn; k++) {
                    Assert.assertEquals(0.0, l0After.getDouble(j, k, 4), 0.0);
                    Assert.assertEquals(0.0, l1After.getDouble(j, k, 4), 0.0);
                }
            }
        }
    }

    @Test
    public void testOutputMaskingScoreMagnitudes() {
        // Idea: check magnitude of scores, with differeing number of values masked out
        // i.e., MSE with zero weight init and 1.0 labels: know what to expect in terms of score
        int nIn = 3;
        int[] timeSeriesLengths = new int[]{ 3, 10 };
        int[] outputSizes = new int[]{ 1, 2, 5 };
        int[] miniBatchSizes = new int[]{ 1, 4 };
        Random r = new Random(12345);
        for (int tsLength : timeSeriesLengths) {
            for (int nOut : outputSizes) {
                for (int miniBatch : miniBatchSizes) {
                    for (int nToMask = 0; nToMask < (tsLength - 1); nToMask++) {
                        String msg = (((("tsLen=" + tsLength) + ", nOut=") + nOut) + ", miniBatch=") + miniBatch;
                        INDArray labelMaskArray = Nd4j.ones(miniBatch, tsLength);
                        for (int i = 0; i < miniBatch; i++) {
                            // For each example: select which outputs to mask...
                            int nMasked = 0;
                            while (nMasked < nToMask) {
                                int tryIdx = r.nextInt(tsLength);
                                if ((labelMaskArray.getDouble(i, tryIdx)) == 0.0)
                                    continue;

                                labelMaskArray.putScalar(new int[]{ i, tryIdx }, 0.0);
                                nMasked++;
                            } 
                        }
                        INDArray input = Nd4j.rand(new int[]{ miniBatch, nIn, tsLength });
                        INDArray labels = Nd4j.ones(miniBatch, nOut, tsLength);
                        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345L).list().layer(0, new GravesLSTM.Builder().nIn(nIn).nOut(5).dist(new NormalDistribution(0, 1)).updater(new NoOp()).build()).layer(1, new RnnOutputLayer.Builder(LossFunction.MSE).activation(IDENTITY).nIn(5).nOut(nOut).weightInit(ZERO).updater(new NoOp()).build()).build();
                        MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                        mln.init();
                        // MSE loss function: 1/n * sum(squaredErrors)... but sum(squaredErrors) = n * (1-0) here -> sum(squaredErrors)
                        double expScore = tsLength - nToMask;// Sum over minibatches, then divide by minibatch size

                        mln.setLayerMaskArrays(null, labelMaskArray);
                        mln.setInput(input);
                        mln.setLabels(labels);
                        mln.computeGradientAndScore();
                        double score = mln.score();
                        Assert.assertEquals(msg, expScore, score, 0.1);
                    }
                }
            }
        }
    }

    @Test
    public void testOutputMasking() {
        // If labels are masked: want zero outputs for that time step.
        int nIn = 3;
        int[] timeSeriesLengths = new int[]{ 3, 10 };
        int[] outputSizes = new int[]{ 1, 2, 5 };
        int[] miniBatchSizes = new int[]{ 1, 4 };
        Random r = new Random(12345);
        for (int tsLength : timeSeriesLengths) {
            for (int nOut : outputSizes) {
                for (int miniBatch : miniBatchSizes) {
                    for (int nToMask = 0; nToMask < (tsLength - 1); nToMask++) {
                        INDArray labelMaskArray = Nd4j.ones(miniBatch, tsLength);
                        for (int i = 0; i < miniBatch; i++) {
                            // For each example: select which outputs to mask...
                            int nMasked = 0;
                            while (nMasked < nToMask) {
                                int tryIdx = r.nextInt(tsLength);
                                if ((labelMaskArray.getDouble(i, tryIdx)) == 0.0)
                                    continue;

                                labelMaskArray.putScalar(new int[]{ i, tryIdx }, 0.0);
                                nMasked++;
                            } 
                        }
                        INDArray input = Nd4j.rand(new int[]{ miniBatch, nIn, tsLength });
                        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345L).list().layer(0, new GravesLSTM.Builder().nIn(nIn).nOut(5).dist(new NormalDistribution(0, 1)).updater(new NoOp()).build()).layer(1, new RnnOutputLayer.Builder(LossFunction.MSE).activation(IDENTITY).nIn(5).nOut(nOut).weightInit(XAVIER).updater(new NoOp()).build()).build();
                        MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                        mln.init();
                        MultiLayerConfiguration conf2 = new NeuralNetConfiguration.Builder().seed(12345L).list().layer(0, new GravesLSTM.Builder().nIn(nIn).nOut(5).dist(new NormalDistribution(0, 1)).updater(new NoOp()).build()).layer(1, new RnnOutputLayer.Builder(LossFunction.MSE).activation(IDENTITY).nIn(5).nOut(nOut).weightInit(XAVIER).updater(new NoOp()).build()).build();
                        MultiLayerNetwork mln2 = new MultiLayerNetwork(conf2);
                        mln2.init();
                        mln.setLayerMaskArrays(null, labelMaskArray);
                        mln2.setLayerMaskArrays(null, labelMaskArray);
                        INDArray out = mln.output(input);
                        INDArray out2 = mln2.output(input);
                        for (int i = 0; i < miniBatch; i++) {
                            for (int j = 0; j < tsLength; j++) {
                                double m = labelMaskArray.getDouble(i, j);
                                if (m == 0.0) {
                                    // Expect outputs to be exactly 0.0
                                    INDArray outRow = out.get(NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.point(j));
                                    INDArray outRow2 = out2.get(NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.point(j));
                                    for (int k = 0; k < nOut; k++) {
                                        Assert.assertEquals(outRow.getDouble(k), 0.0, 0.0);
                                        Assert.assertEquals(outRow2.getDouble(k), 0.0, 0.0);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testMaskingBidirectionalRnn() {
        // Idea: mask some of the time steps, like [1,1,1,0,0]. We expect the activations for the first 3 time steps
        // to be the same as if we'd just fed in [1,1,1] for that example
        Nd4j.getRandom().setSeed(12345);
        int nIn = 4;
        int layerSize = 3;
        int nOut = 3;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().weightInit(XAVIER).activation(TANH).list().layer(0, new GravesBidirectionalLSTM.Builder().nIn(nIn).nOut(layerSize).build()).layer(1, new GravesBidirectionalLSTM.Builder().nIn(layerSize).nOut(layerSize).build()).layer(2, new RnnOutputLayer.Builder().lossFunction(MSE).nIn(layerSize).nOut(nOut).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        int tsLength = 5;
        int minibatch = 3;
        INDArray input = Nd4j.rand(new int[]{ minibatch, nIn, tsLength });
        INDArray labels = Nd4j.rand(new int[]{ minibatch, nOut, tsLength });
        INDArray featuresMask = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1, 1 }, new double[]{ 1, 1, 1, 1, 0 }, new double[]{ 1, 1, 1, 0, 0 } });
        INDArray labelsMask = featuresMask.dup();
        net.setLayerMaskArrays(featuresMask, labelsMask);
        INDArray outMasked = net.output(input);
        net.clearLayerMaskArrays();
        // Check forward pass:
        for (int i = 0; i < minibatch; i++) {
            INDArrayIndex[] idx = new INDArrayIndex[]{ NDArrayIndex.interval(i, i, true), NDArrayIndex.all(), NDArrayIndex.interval(0, (tsLength - i)) };
            INDArray expExampleOut = net.output(input.get(idx));
            INDArray actualExampleOut = outMasked.get(idx);
            // System.out.println(i);
            Assert.assertEquals(expExampleOut, actualExampleOut);
        }
        // Also: check the score examples method...
        DataSet ds = new DataSet(input, labels, featuresMask, labelsMask);
        INDArray exampleScores = net.scoreExamples(ds, false);
        Assert.assertArrayEquals(new long[]{ minibatch, 1 }, exampleScores.shape());// One score per time series (added over each time step)

        for (int i = 0; i < minibatch; i++) {
            INDArrayIndex[] idx = new INDArrayIndex[]{ NDArrayIndex.interval(i, i, true), NDArrayIndex.all(), NDArrayIndex.interval(0, (tsLength - i)) };
            DataSet dsSingle = new DataSet(input.get(idx), labels.get(idx));
            INDArray exampleSingleScore = net.scoreExamples(dsSingle, false);
            double exp = exampleSingleScore.getDouble(0);
            double act = exampleScores.getDouble(i);
            // System.out.println(i + "\t" + exp + "\t" + act);
            Assert.assertEquals(exp, act, 1.0E-6);
        }
    }

    @Test
    public void testMaskingLstmAndBidirectionalLstmGlobalPooling() {
        // Idea: mask some of the time steps, like [1,1,1,0,0]. We expect the activations out of the global pooling
        // to be the same as if we'd just fed in the in the present (1s) time steps only
        Nd4j.getRandom().setSeed(12345);
        int nIn = 2;
        int layerSize = 4;
        int nOut = 3;
        PoolingType[] poolingTypes = new PoolingType[]{ SUM, AVG, MAX };
        boolean[] isBidirectional = new boolean[]{ false, true };
        for (boolean bidirectional : isBidirectional) {
            for (PoolingType pt : poolingTypes) {
                System.out.println(((("Starting test: bidirectional = " + bidirectional) + ", poolingType = ") + pt));
                MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().weightInit(XAVIER).activation(TANH).list().layer(0, (bidirectional ? new GravesBidirectionalLSTM.Builder().nIn(nIn).nOut(layerSize).build() : new GravesLSTM.Builder().nIn(nIn).nOut(layerSize).build())).layer(1, (bidirectional ? new GravesBidirectionalLSTM.Builder().nIn(layerSize).nOut(layerSize).build() : new GravesLSTM.Builder().nIn(layerSize).nOut(layerSize).build())).layer(2, new GlobalPoolingLayer.Builder().poolingType(pt).build()).layer(3, new OutputLayer.Builder().lossFunction(MSE).nIn(layerSize).nOut(nOut).build()).build();
                MultiLayerNetwork net = new MultiLayerNetwork(conf);
                net.init();
                int tsLength = 5;
                int minibatch = 3;
                INDArray input = Nd4j.rand(new int[]{ minibatch, nIn, tsLength });
                INDArray labels = Nd4j.rand(new int[]{ minibatch, nOut });
                INDArray featuresMask = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1, 1 }, new double[]{ 1, 1, 1, 1, 0 }, new double[]{ 1, 1, 1, 0, 0 } });
                net.setLayerMaskArrays(featuresMask, null);
                INDArray outMasked = net.output(input);
                net.clearLayerMaskArrays();
                for (int i = 0; i < minibatch; i++) {
                    INDArrayIndex[] idx = new INDArrayIndex[]{ NDArrayIndex.interval(i, i, true), NDArrayIndex.all(), NDArrayIndex.interval(0, (tsLength - i)) };
                    INDArray inputSubset = input.get(idx);
                    INDArray expExampleOut = net.output(inputSubset);
                    INDArray actualExampleOut = outMasked.getRow(i);
                    // System.out.println(i);
                    Assert.assertEquals(expExampleOut, actualExampleOut);
                }
                // Also: check the score examples method...
                DataSet ds = new DataSet(input, labels, featuresMask, null);
                INDArray exampleScores = net.scoreExamples(ds, false);
                for (int i = 0; i < minibatch; i++) {
                    INDArrayIndex[] idx = new INDArrayIndex[]{ NDArrayIndex.interval(i, i, true), NDArrayIndex.all(), NDArrayIndex.interval(0, (tsLength - i)) };
                    DataSet dsSingle = new DataSet(input.get(idx), labels.getRow(i));
                    INDArray exampleSingleScore = net.scoreExamples(dsSingle, false);
                    double exp = exampleSingleScore.getDouble(0);
                    double act = exampleScores.getDouble(i);
                    // System.out.println(i + "\t" + exp + "\t" + act);
                    Assert.assertEquals(exp, act, 1.0E-6);
                }
            }
        }
    }

    @Test
    public void testReverse() {
        for (char c : new char[]{ 'f', 'c' }) {
            INDArray in = Nd4j.linspace(1, ((3 * 5) * 10), ((3 * 5) * 10), Nd4j.dataType()).reshape('f', 3, 5, 10).dup(c);
            INDArray inMask = Nd4j.linspace(1, 30, 30, Nd4j.dataType()).reshape('f', 3, 10).dup(c);// Minibatch, TS length

            INDArray inReverseExp = TestVariableLengthTS.reverseTimeSeries(in);
            INDArray inMaskReverseExp = Nd4j.create(inMask.shape());
            for (int i = 0; i < (inMask.size(1)); i++) {
                inMaskReverseExp.putColumn(i, inMask.getColumn((((inMask.size(1)) - i) - 1)));
            }
            INDArray inReverse = TimeSeriesUtils.reverseTimeSeries(in, LayerWorkspaceMgr.noWorkspaces(), INPUT);
            INDArray inMaskReverse = TimeSeriesUtils.reverseTimeSeriesMask(inMask, LayerWorkspaceMgr.noWorkspaces(), INPUT);
            Assert.assertEquals(inReverseExp, inReverse);
            Assert.assertEquals(inMaskReverseExp, inMaskReverse);
        }
    }
}

