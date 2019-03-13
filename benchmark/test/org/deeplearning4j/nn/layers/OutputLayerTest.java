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


import Activation.IDENTITY;
import Activation.SOFTMAX;
import Activation.TANH;
import ConvolutionMode.Same;
import LossFunctions.LossFunction.MCXENT;
import OptimizationAlgorithm.LINE_GRADIENT_DESCENT;
import WeightInit.ZERO;
import java.util.Collections;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.layers.OutputLayer.Builder;
import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToRnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.recurrent.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.deeplearning4j.optimize.api.TrainingListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.NoOp;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import static org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction.MCXENT;


/**
 * Created by agibsonccc on 9/1/14.
 */
@Slf4j
public class OutputLayerTest extends BaseDL4JTest {
    @Test
    public void testSetParams() {
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(LINE_GRADIENT_DESCENT).updater(new Sgd(0.1)).layer(new Builder().nIn(4).nOut(3).weightInit(ZERO).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        OutputLayer l = ((OutputLayer) (conf.getLayer().instantiate(conf, Collections.<TrainingListener>singletonList(new ScoreIterationListener(1)), 0, params, true)));
        params = l.params();
        l.setParams(params);
        Assert.assertEquals(params, l.params());
    }

    @Test
    public void testOutputLayersRnnForwardPass() {
        // Test output layer with RNNs (
        // Expect all outputs etc. to be 2d
        int nIn = 2;
        int nOut = 5;
        int layerSize = 4;
        int timeSeriesLength = 6;
        int miniBatchSize = 3;
        Random r = new Random(12345L);
        INDArray input = Nd4j.zeros(miniBatchSize, nIn, timeSeriesLength);
        for (int i = 0; i < miniBatchSize; i++) {
            for (int j = 0; j < nIn; j++) {
                for (int k = 0; k < timeSeriesLength; k++) {
                    input.putScalar(new int[]{ i, j, k }, ((r.nextDouble()) - 0.5));
                }
            }
        }
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345L).list().layer(0, new GravesLSTM.Builder().nIn(nIn).nOut(layerSize).dist(new NormalDistribution(0, 1)).activation(TANH).updater(new NoOp()).build()).layer(1, new Builder(MCXENT).activation(SOFTMAX).nIn(layerSize).nOut(nOut).dist(new NormalDistribution(0, 1)).updater(new NoOp()).build()).inputPreProcessor(1, new RnnToFeedForwardPreProcessor()).build();
        MultiLayerNetwork mln = new MultiLayerNetwork(conf);
        mln.init();
        INDArray out2d = mln.feedForward(input).get(2);
        Assert.assertArrayEquals(out2d.shape(), new long[]{ miniBatchSize * timeSeriesLength, nOut });
        INDArray out = mln.output(input);
        Assert.assertArrayEquals(out.shape(), new long[]{ miniBatchSize * timeSeriesLength, nOut });
        INDArray preout = mln.output(input);
        Assert.assertArrayEquals(preout.shape(), new long[]{ miniBatchSize * timeSeriesLength, nOut });
        // As above, but for RnnOutputLayer. Expect all activations etc. to be 3d
        MultiLayerConfiguration confRnn = new NeuralNetConfiguration.Builder().seed(12345L).list().layer(0, new GravesLSTM.Builder().nIn(nIn).nOut(layerSize).dist(new NormalDistribution(0, 1)).activation(TANH).updater(new NoOp()).build()).layer(1, new org.deeplearning4j.nn.conf.layers.RnnOutputLayer.Builder(MCXENT).activation(SOFTMAX).nIn(layerSize).nOut(nOut).dist(new NormalDistribution(0, 1)).updater(new NoOp()).build()).build();
        MultiLayerNetwork mlnRnn = new MultiLayerNetwork(confRnn);
        mln.init();
        INDArray out3d = mlnRnn.feedForward(input).get(2);
        Assert.assertArrayEquals(out3d.shape(), new long[]{ miniBatchSize, nOut, timeSeriesLength });
        INDArray outRnn = mlnRnn.output(input);
        Assert.assertArrayEquals(outRnn.shape(), new long[]{ miniBatchSize, nOut, timeSeriesLength });
        INDArray preoutRnn = mlnRnn.output(input);
        Assert.assertArrayEquals(preoutRnn.shape(), new long[]{ miniBatchSize, nOut, timeSeriesLength });
    }

    @Test
    public void testRnnOutputLayerIncEdgeCases() {
        // Basic test + test edge cases: timeSeriesLength==1, miniBatchSize==1, both
        int[] tsLength = new int[]{ 5, 1, 5, 1 };
        int[] miniBatch = new int[]{ 7, 7, 1, 1 };
        int nIn = 3;
        int nOut = 6;
        int layerSize = 4;
        FeedForwardToRnnPreProcessor proc = new FeedForwardToRnnPreProcessor();
        for (int t = 0; t < (tsLength.length); t++) {
            Nd4j.getRandom().setSeed(12345);
            int timeSeriesLength = tsLength[t];
            int miniBatchSize = miniBatch[t];
            Random r = new Random(12345L);
            INDArray input = Nd4j.zeros(miniBatchSize, nIn, timeSeriesLength);
            for (int i = 0; i < miniBatchSize; i++) {
                for (int j = 0; j < nIn; j++) {
                    for (int k = 0; k < timeSeriesLength; k++) {
                        input.putScalar(new int[]{ i, j, k }, ((r.nextDouble()) - 0.5));
                    }
                }
            }
            INDArray labels3d = Nd4j.zeros(miniBatchSize, nOut, timeSeriesLength);
            for (int i = 0; i < miniBatchSize; i++) {
                for (int j = 0; j < timeSeriesLength; j++) {
                    int idx = r.nextInt(nOut);
                    labels3d.putScalar(new int[]{ i, idx, j }, 1.0F);
                }
            }
            INDArray labels2d = proc.backprop(labels3d, miniBatchSize, LayerWorkspaceMgr.noWorkspaces());
            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345L).list().layer(0, new GravesLSTM.Builder().nIn(nIn).nOut(layerSize).dist(new NormalDistribution(0, 1)).activation(TANH).updater(new NoOp()).build()).layer(1, new Builder(MCXENT).activation(SOFTMAX).nIn(layerSize).nOut(nOut).dist(new NormalDistribution(0, 1)).updater(new NoOp()).build()).inputPreProcessor(1, new RnnToFeedForwardPreProcessor()).build();
            MultiLayerNetwork mln = new MultiLayerNetwork(conf);
            mln.init();
            INDArray out2d = mln.feedForward(input).get(2);
            INDArray out3d = proc.preProcess(out2d, miniBatchSize, LayerWorkspaceMgr.noWorkspaces());
            MultiLayerConfiguration confRnn = new NeuralNetConfiguration.Builder().seed(12345L).list().layer(0, new GravesLSTM.Builder().nIn(nIn).nOut(layerSize).dist(new NormalDistribution(0, 1)).activation(TANH).updater(new NoOp()).build()).layer(1, new org.deeplearning4j.nn.conf.layers.RnnOutputLayer.Builder(MCXENT).activation(SOFTMAX).nIn(layerSize).nOut(nOut).dist(new NormalDistribution(0, 1)).updater(new NoOp()).build()).build();
            MultiLayerNetwork mlnRnn = new MultiLayerNetwork(confRnn);
            mlnRnn.init();
            INDArray outRnn = mlnRnn.feedForward(input).get(2);
            mln.setLabels(labels2d);
            mlnRnn.setLabels(labels3d);
            mln.computeGradientAndScore();
            mlnRnn.computeGradientAndScore();
            // score is average over all examples.
            // However: OutputLayer version has miniBatch*timeSeriesLength "examples" (after reshaping)
            // RnnOutputLayer has miniBatch examples
            // Hence: expect difference in scores by factor of timeSeriesLength
            double score = (mln.score()) * timeSeriesLength;
            double scoreRNN = mlnRnn.score();
            Assert.assertTrue((!(Double.isNaN(score))));
            Assert.assertTrue((!(Double.isNaN(scoreRNN))));
            double relError = (Math.abs((score - scoreRNN))) / ((Math.abs(score)) + (Math.abs(scoreRNN)));
            System.out.println(relError);
            Assert.assertTrue((relError < 1.0E-6));
            // Check labels and inputs for output layer:
            OutputLayer ol = ((OutputLayer) (mln.getOutputLayer()));
            Assert.assertArrayEquals(ol.getInput().shape(), new long[]{ miniBatchSize * timeSeriesLength, layerSize });
            Assert.assertArrayEquals(ol.getLabels().shape(), new long[]{ miniBatchSize * timeSeriesLength, nOut });
            RnnOutputLayer rnnol = ((RnnOutputLayer) (mlnRnn.getOutputLayer()));
            // assertArrayEquals(rnnol.getInput().shape(),new int[]{miniBatchSize,layerSize,timeSeriesLength});
            // Input may be set by BaseLayer methods. Thus input may end up as reshaped 2d version instead of original 3d version.
            // Not ideal, but everything else works.
            Assert.assertArrayEquals(rnnol.getLabels().shape(), new long[]{ miniBatchSize, nOut, timeSeriesLength });
            // Check shapes of output for both:
            Assert.assertArrayEquals(out2d.shape(), new long[]{ miniBatchSize * timeSeriesLength, nOut });
            INDArray out = mln.output(input);
            Assert.assertArrayEquals(out.shape(), new long[]{ miniBatchSize * timeSeriesLength, nOut });
            INDArray preout = mln.output(input);
            Assert.assertArrayEquals(preout.shape(), new long[]{ miniBatchSize * timeSeriesLength, nOut });
            INDArray outFFRnn = mlnRnn.feedForward(input).get(2);
            Assert.assertArrayEquals(outFFRnn.shape(), new long[]{ miniBatchSize, nOut, timeSeriesLength });
            INDArray outRnn2 = mlnRnn.output(input);
            Assert.assertArrayEquals(outRnn2.shape(), new long[]{ miniBatchSize, nOut, timeSeriesLength });
            INDArray preoutRnn = mlnRnn.output(input);
            Assert.assertArrayEquals(preoutRnn.shape(), new long[]{ miniBatchSize, nOut, timeSeriesLength });
        }
    }

    @Test
    public void testCompareRnnOutputRnnLoss() {
        Nd4j.getRandom().setSeed(12345);
        int timeSeriesLength = 4;
        int nIn = 5;
        int layerSize = 6;
        int nOut = 6;
        int miniBatchSize = 3;
        MultiLayerConfiguration conf1 = new NeuralNetConfiguration.Builder().seed(12345L).updater(new NoOp()).list().layer(new LSTM.Builder().nIn(nIn).nOut(layerSize).activation(TANH).dist(new NormalDistribution(0, 1.0)).updater(new NoOp()).build()).layer(new DenseLayer.Builder().nIn(layerSize).nOut(nOut).activation(IDENTITY).build()).layer(new RnnLossLayer.Builder(MCXENT).activation(SOFTMAX).build()).build();
        MultiLayerNetwork mln = new MultiLayerNetwork(conf1);
        mln.init();
        MultiLayerConfiguration conf2 = new NeuralNetConfiguration.Builder().seed(12345L).updater(new NoOp()).list().layer(new LSTM.Builder().nIn(nIn).nOut(layerSize).activation(TANH).dist(new NormalDistribution(0, 1.0)).updater(new NoOp()).build()).layer(new org.deeplearning4j.nn.conf.layers.RnnOutputLayer.Builder(MCXENT).activation(SOFTMAX).nIn(layerSize).nOut(nOut).build()).build();
        MultiLayerNetwork mln2 = new MultiLayerNetwork(conf2);
        mln2.init();
        mln2.setParams(mln.params());
        INDArray in = Nd4j.rand(new int[]{ miniBatchSize, nIn, timeSeriesLength });
        INDArray out1 = mln.output(in);
        INDArray out2 = mln.output(in);
        Assert.assertEquals(out1, out2);
        Random r = new Random(12345);
        INDArray labels = Nd4j.create(miniBatchSize, nOut, timeSeriesLength);
        for (int i = 0; i < miniBatchSize; i++) {
            for (int j = 0; j < timeSeriesLength; j++) {
                labels.putScalar(i, r.nextInt(nOut), j, 1.0);
            }
        }
        mln.setInput(in);
        mln.setLabels(labels);
        mln2.setInput(in);
        mln2.setLabels(labels);
        mln.computeGradientAndScore();
        mln2.computeGradientAndScore();
        Assert.assertEquals(mln.gradient().gradient(), mln2.gradient().gradient());
        Assert.assertEquals(mln.score(), mln2.score(), 1.0E-6);
        TestUtils.testModelSerialization(mln);
    }

    @Test
    public void testCnnLossLayer() {
        for (WorkspaceMode ws : WorkspaceMode.values()) {
            log.info(("*** Testing workspace: " + ws));
            for (Activation a : new Activation[]{ Activation.TANH, Activation.SELU }) {
                // Check that (A+identity) is equal to (identity+A), for activation A
                // i.e., should get same output and weight gradients for both
                MultiLayerConfiguration conf1 = new NeuralNetConfiguration.Builder().seed(12345L).updater(new NoOp()).convolutionMode(Same).inferenceWorkspaceMode(ws).trainingWorkspaceMode(ws).list().layer(new ConvolutionLayer.Builder().nIn(3).nOut(4).activation(IDENTITY).kernelSize(2, 2).stride(1, 1).dist(new NormalDistribution(0, 1.0)).updater(new NoOp()).build()).layer(new CnnLossLayer.Builder(LossFunction.MSE).activation(a).build()).build();
                MultiLayerConfiguration conf2 = new NeuralNetConfiguration.Builder().seed(12345L).updater(new NoOp()).convolutionMode(Same).inferenceWorkspaceMode(ws).trainingWorkspaceMode(ws).list().layer(new ConvolutionLayer.Builder().nIn(3).nOut(4).activation(a).kernelSize(2, 2).stride(1, 1).dist(new NormalDistribution(0, 1.0)).updater(new NoOp()).build()).layer(new CnnLossLayer.Builder(LossFunction.MSE).activation(IDENTITY).build()).build();
                MultiLayerNetwork mln = new MultiLayerNetwork(conf1);
                mln.init();
                MultiLayerNetwork mln2 = new MultiLayerNetwork(conf2);
                mln2.init();
                mln2.setParams(mln.params());
                INDArray in = Nd4j.rand(new int[]{ 3, 3, 5, 5 });
                INDArray out1 = mln.output(in);
                INDArray out2 = mln2.output(in);
                Assert.assertEquals(out1, out2);
                INDArray labels = Nd4j.rand(out1.shape());
                mln.setInput(in);
                mln.setLabels(labels);
                mln2.setInput(in);
                mln2.setLabels(labels);
                mln.computeGradientAndScore();
                mln2.computeGradientAndScore();
                Assert.assertEquals(mln.score(), mln2.score(), 1.0E-6);
                Assert.assertEquals(mln.gradient().gradient(), mln2.gradient().gradient());
                // Also check computeScoreForExamples
                INDArray in2a = Nd4j.rand(new int[]{ 1, 3, 5, 5 });
                INDArray labels2a = Nd4j.rand(new int[]{ 1, 4, 5, 5 });
                INDArray in2 = Nd4j.concat(0, in2a, in2a);
                INDArray labels2 = Nd4j.concat(0, labels2a, labels2a);
                INDArray s = mln.scoreExamples(new org.nd4j.linalg.dataset.DataSet(in2, labels2), false);
                Assert.assertArrayEquals(new long[]{ 2, 1 }, s.shape());
                Assert.assertEquals(s.getDouble(0), s.getDouble(1), 1.0E-6);
                TestUtils.testModelSerialization(mln);
            }
        }
    }

    @Test
    public void testCnnLossLayerCompGraph() {
        for (WorkspaceMode ws : WorkspaceMode.values()) {
            log.info(("*** Testing workspace: " + ws));
            for (Activation a : new Activation[]{ Activation.TANH, Activation.SELU }) {
                // Check that (A+identity) is equal to (identity+A), for activation A
                // i.e., should get same output and weight gradients for both
                ComputationGraphConfiguration conf1 = new NeuralNetConfiguration.Builder().seed(12345L).updater(new NoOp()).convolutionMode(Same).inferenceWorkspaceMode(ws).trainingWorkspaceMode(ws).graphBuilder().addInputs("in").addLayer("0", new ConvolutionLayer.Builder().nIn(3).nOut(4).activation(IDENTITY).kernelSize(2, 2).stride(1, 1).dist(new NormalDistribution(0, 1.0)).updater(new NoOp()).build(), "in").addLayer("1", new CnnLossLayer.Builder(LossFunction.MSE).activation(a).build(), "0").setOutputs("1").build();
                ComputationGraphConfiguration conf2 = new NeuralNetConfiguration.Builder().seed(12345L).updater(new NoOp()).convolutionMode(Same).inferenceWorkspaceMode(ws).trainingWorkspaceMode(ws).graphBuilder().addInputs("in").addLayer("0", new ConvolutionLayer.Builder().nIn(3).nOut(4).activation(a).kernelSize(2, 2).stride(1, 1).dist(new NormalDistribution(0, 1.0)).updater(new NoOp()).build(), "in").addLayer("1", new CnnLossLayer.Builder(LossFunction.MSE).activation(IDENTITY).build(), "0").setOutputs("1").build();
                ComputationGraph graph = new ComputationGraph(conf1);
                graph.init();
                ComputationGraph graph2 = new ComputationGraph(conf2);
                graph2.init();
                graph2.setParams(graph.params());
                INDArray in = Nd4j.rand(new int[]{ 3, 3, 5, 5 });
                INDArray out1 = graph.outputSingle(in);
                INDArray out2 = graph2.outputSingle(in);
                Assert.assertEquals(out1, out2);
                INDArray labels = Nd4j.rand(out1.shape());
                graph.setInput(0, in);
                graph.setLabels(labels);
                graph2.setInput(0, in);
                graph2.setLabels(labels);
                graph.computeGradientAndScore();
                graph2.computeGradientAndScore();
                Assert.assertEquals(graph.score(), graph2.score(), 1.0E-6);
                Assert.assertEquals(graph.gradient().gradient(), graph2.gradient().gradient());
                // Also check computeScoreForExamples
                INDArray in2a = Nd4j.rand(new int[]{ 1, 3, 5, 5 });
                INDArray labels2a = Nd4j.rand(new int[]{ 1, 4, 5, 5 });
                INDArray in2 = Nd4j.concat(0, in2a, in2a);
                INDArray labels2 = Nd4j.concat(0, labels2a, labels2a);
                INDArray s = graph.scoreExamples(new org.nd4j.linalg.dataset.DataSet(in2, labels2), false);
                Assert.assertArrayEquals(new long[]{ 2, 1 }, s.shape());
                Assert.assertEquals(s.getDouble(0), s.getDouble(1), 1.0E-6);
                TestUtils.testModelSerialization(graph);
            }
        }
    }

    @Test
    public void testCnnOutputLayerSoftmax() {
        // Check that softmax is applied channels-wise
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345L).updater(new NoOp()).convolutionMode(Same).list().layer(new ConvolutionLayer.Builder().nIn(3).nOut(4).activation(IDENTITY).dist(new NormalDistribution(0, 1.0)).updater(new NoOp()).build()).layer(new CnnLossLayer.Builder(LossFunction.MSE).activation(SOFTMAX).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        INDArray in = Nd4j.rand(new int[]{ 2, 3, 4, 5 });
        INDArray out = net.output(in);
        double min = out.minNumber().doubleValue();
        double max = out.maxNumber().doubleValue();
        Assert.assertTrue(((min >= 0) && (max <= 1.0)));
        INDArray sum = out.sum(1);
        Assert.assertEquals(Nd4j.ones(2, 4, 5), sum);
    }

    @Test
    public void testOutputLayerDefaults() {
        new NeuralNetConfiguration.Builder().list().layer(new Builder().nIn(10).nOut(10).build()).build();
        new NeuralNetConfiguration.Builder().list().layer(new org.deeplearning4j.nn.conf.layers.LossLayer.Builder().build()).build();
        new NeuralNetConfiguration.Builder().list().layer(new org.deeplearning4j.nn.conf.layers.CnnLossLayer.Builder().build()).build();
        new NeuralNetConfiguration.Builder().list().layer(new org.deeplearning4j.nn.conf.layers.CenterLossOutputLayer.Builder().build()).build();
    }
}

