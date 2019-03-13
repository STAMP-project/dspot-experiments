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
package org.deeplearning4j.arbiter.multilayernetwork;


import Activation.IDENTITY;
import Activation.RELU;
import Activation.SOFTMAX;
import Activation.TANH;
import ConvolutionMode.Same;
import LossFunction.MCXENT;
import LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD;
import PoolingType.SUM;
import SubsamplingLayer.PoolingType.MAX;
import WeightInit.XAVIER;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.deeplearning4j.arbiter.DL4JConfiguration;
import org.deeplearning4j.arbiter.MultiLayerSpace;
import org.deeplearning4j.arbiter.TestUtils;
import org.deeplearning4j.arbiter.optimize.api.Candidate;
import org.deeplearning4j.arbiter.optimize.api.CandidateGenerator;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.api.data.DataProvider;
import org.deeplearning4j.arbiter.optimize.api.saving.ResultSaver;
import org.deeplearning4j.arbiter.optimize.api.score.ScoreFunction;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxCandidatesCondition;
import org.deeplearning4j.arbiter.optimize.api.termination.TerminationCondition;
import org.deeplearning4j.arbiter.optimize.config.OptimizationConfiguration;
import org.deeplearning4j.arbiter.optimize.parameter.continuous.ContinuousParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.integer.IntegerParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.math.Op;
import org.deeplearning4j.arbiter.optimize.runner.IOptimizationRunner;
import org.deeplearning4j.arbiter.saver.local.FileModelSaver;
import org.deeplearning4j.arbiter.scoring.impl.TestSetAccuracyScoreFunction;
import org.deeplearning4j.arbiter.task.MultiLayerNetworkTaskCreator;
import org.deeplearning4j.arbiter.util.LeafUtils;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.constraint.NonNegativeConstraint;
import org.deeplearning4j.nn.conf.constraint.UnitNormConstraint;
import org.deeplearning4j.nn.conf.dropout.Dropout;
import org.deeplearning4j.nn.conf.dropout.IDropout;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.variational.BernoulliReconstructionDistribution;
import org.deeplearning4j.nn.conf.layers.variational.GaussianReconstructionDistribution;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.deeplearning4j.nn.layers.recurrent.BidirectionalLayer;
import org.deeplearning4j.nn.layers.recurrent.LSTM;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.ILossFunction;


public class TestMultiLayerSpace {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void testBasic() {
        MultiLayerConfiguration expected = new NeuralNetConfiguration.Builder().updater(new Sgd(0.005)).seed(12345).list().layer(0, new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(1, new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(2, new OutputLayer.Builder().lossFunction(MCXENT).activation(SOFTMAX).nIn(10).nOut(5).build()).build();
        MultiLayerSpace mls = // 2 identical layers
        new MultiLayerSpace.Builder().updater(new Sgd(0.005)).seed(12345).addLayer(new DenseLayerSpace.Builder().nIn(10).nOut(10).build(), new org.deeplearning4j.arbiter.optimize.parameter.FixedValue(2)).addLayer(new OutputLayerSpace.Builder().lossFunction(MCXENT).activation(SOFTMAX).nIn(10).nOut(5).build()).build();
        int nParams = mls.numParameters();
        Assert.assertEquals(0, nParams);
        MultiLayerConfiguration conf = mls.getValue(new double[0]).getMultiLayerConfiguration();
        Assert.assertEquals(expected, conf);
    }

    @Test
    public void testBasic0() {
        MultiLayerConfiguration expected = new NeuralNetConfiguration.Builder().l1Bias(0.4).l2Bias(0.5).constrainBias(new NonNegativeConstraint()).updater(new Sgd(0.005)).seed(12345).list().layer(0, new DenseLayer.Builder().l1Bias(0.6).nIn(10).nOut(10).build()).layer(1, new DenseLayer.Builder().l2Bias(0.7).constrainBias(new UnitNormConstraint()).nIn(10).nOut(10).build()).layer(2, new OutputLayer.Builder().lossFunction(MCXENT).activation(SOFTMAX).nIn(10).nOut(5).build()).build();
        MultiLayerSpace mls = new MultiLayerSpace.Builder().l1Bias(0.4).l2Bias(0.5).constrainBias(new NonNegativeConstraint()).updater(new Sgd(0.005)).seed(12345).addLayer(new DenseLayerSpace.Builder().l1Bias(new ContinuousParameterSpace(0, 1)).nIn(10).nOut(10).build()).addLayer(new DenseLayerSpace.Builder().l2Bias(0.7).constrainBias(new UnitNormConstraint()).nIn(10).nOut(10).build()).addLayer(new OutputLayerSpace.Builder().lossFunction(MCXENT).activation(SOFTMAX).nIn(10).nOut(5).build()).build();
        int nParams = mls.numParameters();
        Assert.assertEquals(1, nParams);
        // Assign numbers to each leaf ParameterSpace object (normally done by candidate generator - manual here for testing)
        List<ParameterSpace> noDuplicatesList = LeafUtils.getUniqueObjects(mls.collectLeaves());
        // Second: assign each a number
        int c = 0;
        for (ParameterSpace ps : noDuplicatesList) {
            int np = ps.numParameters();
            if (np == 1) {
                ps.setIndices((c++));
            } else {
                int[] values = new int[np];
                for (int j = 0; j < np; j++)
                    values[(c++)] = j;

                ps.setIndices(values);
            }
        }
        MultiLayerConfiguration conf = mls.getValue(new double[]{ 0.6 }).getMultiLayerConfiguration();
        Assert.assertEquals(expected, conf);
    }

    @Test
    public void testILossFunctionGetsSet() {
        ILossFunction lossFunction = new org.nd4j.linalg.lossfunctions.impl.LossMCXENT(Nd4j.create(new float[]{ 1.0F, 2.0F }, new long[]{ 1, 2 }));
        MultiLayerConfiguration expected = new NeuralNetConfiguration.Builder().updater(new Sgd(0.005)).seed(12345).list().layer(0, new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(1, new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(2, new OutputLayer.Builder().lossFunction(lossFunction).activation(SOFTMAX).nIn(10).nOut(5).build()).build();
        MultiLayerSpace mls = // 2 identical layers
        new MultiLayerSpace.Builder().updater(new Sgd(0.005)).seed(12345).addLayer(new DenseLayerSpace.Builder().nIn(10).nOut(10).build(), new org.deeplearning4j.arbiter.optimize.parameter.FixedValue(2)).addLayer(new OutputLayerSpace.Builder().iLossFunction(lossFunction).activation(SOFTMAX).nIn(10).nOut(5).build()).build();
        int nParams = mls.numParameters();
        Assert.assertEquals(0, nParams);
        MultiLayerConfiguration conf = mls.getValue(new double[0]).getMultiLayerConfiguration();
        Assert.assertEquals(expected, conf);
    }

    @Test
    public void testBasic2() {
        MultiLayerSpace mls = // 1-3 identical layers
        new MultiLayerSpace.Builder().updater(new org.deeplearning4j.arbiter.conf.updater.SgdSpace(new ContinuousParameterSpace(1.0E-4, 0.1))).l2(new ContinuousParameterSpace(0.2, 0.5)).convolutionMode(Same).addLayer(new ConvolutionLayerSpace.Builder().nIn(3).nOut(3).kernelSize(2, 2).stride(1, 1).build()).addLayer(new DenseLayerSpace.Builder().nIn(10).nOut(10).activation(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(Activation.RELU, Activation.TANH)).build(), new IntegerParameterSpace(1, 3)).addLayer(new OutputLayerSpace.Builder().nIn(10).nOut(10).activation(SOFTMAX).build()).build();
        int nParams = mls.numParameters();
        Assert.assertEquals(4, nParams);
        // Assign numbers to each leaf ParameterSpace object (normally done by candidate generator - manual here for testing)
        List<ParameterSpace> noDuplicatesList = LeafUtils.getUniqueObjects(mls.collectLeaves());
        // Second: assign each a number
        int c = 0;
        for (ParameterSpace ps : noDuplicatesList) {
            int np = ps.numParameters();
            if (np == 1) {
                ps.setIndices((c++));
            } else {
                int[] values = new int[np];
                for (int j = 0; j < np; j++)
                    values[(c++)] = j;

                ps.setIndices(values);
            }
        }
        int[] nLayerCounts = new int[3];
        int reluCount = 0;
        int tanhCount = 0;
        Random r = new Random(12345);
        for (int i = 0; i < 50; i++) {
            double[] rvs = new double[nParams];
            for (int j = 0; j < (rvs.length); j++)
                rvs[j] = r.nextDouble();

            MultiLayerConfiguration conf = mls.getValue(rvs).getMultiLayerConfiguration();
            int nLayers = conf.getConfs().size();
            Assert.assertTrue(((nLayers >= 3) && (nLayers <= 5)));// 1 conv + 1-3 dense layers + 1 output layer: 2 to 4

            int nLayersExOutputLayer = nLayers - 1;
            (nLayerCounts[(nLayersExOutputLayer - 2)])++;
            for (int j = 0; j < nLayers; j++) {
                NeuralNetConfiguration layerConf = conf.getConf(j);
                double lr = getLearningRate();
                Assert.assertTrue(((lr >= 1.0E-4) && (lr <= 0.1)));
                double l2 = TestUtils.getL2(((BaseLayer) (layerConf.getLayer())));
                Assert.assertTrue(((l2 >= 0.2) && (l2 <= 0.5)));
                if (j == (nLayers - 1)) {
                    // Output layer
                    Assert.assertEquals(SOFTMAX.getActivationFunction(), getActivationFn());
                } else
                    if (j == 0) {
                        // Conv layer
                        ConvolutionLayer cl = ((ConvolutionLayer) (layerConf.getLayer()));
                        Assert.assertEquals(3, cl.getNIn());
                        Assert.assertEquals(3, cl.getNOut());
                        Assert.assertEquals(Same, cl.getConvolutionMode());
                    } else {
                        IActivation actFn = ((BaseLayer) (layerConf.getLayer())).getActivationFn();
                        Assert.assertTrue(((RELU.getActivationFunction().equals(actFn)) || (TANH.getActivationFunction().equals(actFn))));
                        if (RELU.getActivationFunction().equals(actFn))
                            reluCount++;
                        else
                            tanhCount++;

                    }

            }
        }
        for (int i = 0; i < 3; i++) {
            Assert.assertTrue(((nLayerCounts[i]) >= 5));// Expect approx equal (50/3 each), but some variation randomly

        }
        System.out.println(("Number of layers: " + (Arrays.toString(nLayerCounts))));
        System.out.println(((("ReLU vs. Tanh: " + reluCount) + "\t") + tanhCount));
    }

    @Test
    public void testGlobalPoolingBasic() {
        MultiLayerConfiguration expected = new NeuralNetConfiguration.Builder().updater(new Sgd(0.005)).seed(12345).list().layer(0, new GravesLSTM.Builder().nIn(10).nOut(10).build()).layer(1, new GlobalPoolingLayer.Builder().poolingType(SUM).pnorm(7).build()).layer(2, new OutputLayer.Builder().lossFunction(MCXENT).activation(SOFTMAX).nIn(10).nOut(5).build()).build();
        MultiLayerSpace mls = new MultiLayerSpace.Builder().updater(new Sgd(0.005)).seed(12345).addLayer(new GravesLSTMLayerSpace.Builder().nIn(10).nOut(10).build()).addLayer(new GlobalPoolingLayerSpace.Builder().poolingType(SUM).pNorm(7).build()).addLayer(new OutputLayerSpace.Builder().lossFunction(MCXENT).activation(SOFTMAX).nIn(10).nOut(5).build()).build();
        int nParams = mls.numParameters();
        Assert.assertEquals(0, nParams);
        MultiLayerConfiguration conf = mls.getValue(new double[0]).getMultiLayerConfiguration();
        Assert.assertEquals(expected, conf);
    }

    @Test
    public void testVariationalAutoencoderLayerSpaceBasic() {
        MultiLayerSpace mls = new MultiLayerSpace.Builder().updater(new Sgd(0.005)).seed(12345).addLayer(new VariationalAutoencoderLayerSpace.Builder().nIn(new IntegerParameterSpace(50, 75)).nOut(200).encoderLayerSizes(234, 567).decoderLayerSizes(123, 456).reconstructionDistribution(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace<org.deeplearning4j.nn.conf.layers.variational.ReconstructionDistribution>(new GaussianReconstructionDistribution(), new BernoulliReconstructionDistribution())).build()).build();
        int numParams = mls.numParameters();
        // Assign numbers to each leaf ParameterSpace object (normally done by candidate generator - manual here for testing)
        List<ParameterSpace> noDuplicatesList = LeafUtils.getUniqueObjects(mls.collectLeaves());
        // Second: assign each a number
        int c = 0;
        for (ParameterSpace ps : noDuplicatesList) {
            int np = ps.numParameters();
            if (np == 1) {
                ps.setIndices((c++));
            } else {
                int[] values = new int[np];
                for (int j = 0; j < np; j++)
                    values[(c++)] = j;

                ps.setIndices(values);
            }
        }
        double[] zeros = new double[numParams];
        DL4JConfiguration configuration = mls.getValue(zeros);
        MultiLayerConfiguration conf = configuration.getMultiLayerConfiguration();
        Assert.assertEquals(1, conf.getConfs().size());
        NeuralNetConfiguration nnc = conf.getConf(0);
        VariationalAutoencoder vae = ((VariationalAutoencoder) (nnc.getLayer()));
        Assert.assertEquals(50, vae.getNIn());
        Assert.assertEquals(200, vae.getNOut());
        Assert.assertArrayEquals(new int[]{ 234, 567 }, vae.getEncoderLayerSizes());
        Assert.assertArrayEquals(new int[]{ 123, 456 }, vae.getDecoderLayerSizes());
        Assert.assertTrue(((vae.getOutputDistribution()) instanceof GaussianReconstructionDistribution));
        double[] ones = new double[numParams];
        for (int i = 0; i < (ones.length); i++)
            ones[i] = 1.0;

        configuration = mls.getValue(ones);
        conf = configuration.getMultiLayerConfiguration();
        Assert.assertEquals(1, conf.getConfs().size());
        nnc = conf.getConf(0);
        vae = ((VariationalAutoencoder) (nnc.getLayer()));
        Assert.assertEquals(75, vae.getNIn());
        Assert.assertEquals(200, vae.getNOut());
        Assert.assertArrayEquals(new int[]{ 234, 567 }, vae.getEncoderLayerSizes());
        Assert.assertArrayEquals(new int[]{ 123, 456 }, vae.getDecoderLayerSizes());
        Assert.assertTrue(((vae.getOutputDistribution()) instanceof BernoulliReconstructionDistribution));
    }

    @Test
    public void testInputTypeBasic() throws Exception {
        ParameterSpace<Integer> layerSizeHyperparam = new IntegerParameterSpace(20, 60);
        MultiLayerSpace hyperparameterSpace = new MultiLayerSpace.Builder().l2(1.0E-4).weightInit(XAVIER).updater(new Nesterovs()).addLayer(new ConvolutionLayerSpace.Builder().kernelSize(5, 5).nIn(1).stride(1, 1).nOut(layerSizeHyperparam).activation(IDENTITY).build()).addLayer(new SubsamplingLayerSpace.Builder().poolingType(MAX).kernelSize(2, 2).stride(2, 2).build()).addLayer(// Note that nIn need not be specified in later layers
        new ConvolutionLayerSpace.Builder().kernelSize(5, 5).stride(1, 1).nOut(50).activation(IDENTITY).build()).addLayer(new SubsamplingLayerSpace.Builder().poolingType(MAX).kernelSize(2, 2).stride(2, 2).build()).addLayer(new DenseLayerSpace.Builder().activation(RELU).nOut(500).build()).addLayer(new OutputLayerSpace.Builder().lossFunction(NEGATIVELOGLIKELIHOOD).nOut(10).activation(SOFTMAX).build()).setInputType(InputType.convolutionalFlat(28, 28, 1)).build();
        DataProvider dataProvider = new TestMultiLayerSpace.TestDataSetProvider();
        File f = testDir.newFolder();
        if (f.exists())
            f.delete();

        f.mkdir();
        ResultSaver modelSaver = new FileModelSaver(f.getAbsolutePath());
        ScoreFunction scoreFunction = new TestSetAccuracyScoreFunction();
        int maxCandidates = 4;
        TerminationCondition[] terminationConditions;
        terminationConditions = new TerminationCondition[]{ new MaxCandidatesCondition(maxCandidates) };
        // Given these configuration options, let's put them all together:
        OptimizationConfiguration configuration = new OptimizationConfiguration.Builder().candidateGenerator(new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(hyperparameterSpace, null)).dataProvider(dataProvider).modelSaver(modelSaver).scoreFunction(scoreFunction).terminationConditions(terminationConditions).build();
        IOptimizationRunner runner = new org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner(configuration, new MultiLayerNetworkTaskCreator());
        runner.execute();
        Assert.assertEquals(maxCandidates, runner.getResults().size());
    }

    @Test
    public void testSameRanges() {
        ParameterSpace<Double> l1Hyperparam = new ContinuousParameterSpace(0.001, 0.1);
        ParameterSpace<Double> l2Hyperparam = new ContinuousParameterSpace(0.001, 0.1);
        MultiLayerSpace hyperparameterSpace = new MultiLayerSpace.Builder().addLayer(new DenseLayerSpace.Builder().nIn(10).nOut(10).build()).l1(l1Hyperparam).l2(l2Hyperparam).build();
        CandidateGenerator c = new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(hyperparameterSpace, null);
        Candidate candidate = c.getCandidate();
    }

    @Test
    public void testWeightedLossFunction() {
        MultiLayerConfiguration expected = new NeuralNetConfiguration.Builder().updater(new Sgd(0.005)).seed(12345).list().layer(0, new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(1, new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(2, new OutputLayer.Builder().lossFunction(new org.nd4j.linalg.lossfunctions.impl.LossMSE(Nd4j.create(new double[]{ 1, 2, 3, 4, 5 }, new long[]{ 1, 5 }))).nIn(10).nOut(5).build()).build();
        MultiLayerSpace mls = // 2 identical layers
        new MultiLayerSpace.Builder().updater(new Sgd(0.005)).seed(12345).addLayer(new DenseLayerSpace.Builder().nIn(10).nOut(10).build(), new org.deeplearning4j.arbiter.optimize.parameter.FixedValue(2)).addLayer(new OutputLayerSpace.Builder().iLossFunction(new org.nd4j.linalg.lossfunctions.impl.LossMSE(Nd4j.create(new double[]{ 1, 2, 3, 4, 5 }, new long[]{ 1, 5 }))).nIn(10).nOut(5).build()).build();
        int nParams = mls.numParameters();
        Assert.assertEquals(0, nParams);
        MultiLayerConfiguration conf = mls.getValue(new double[0]).getMultiLayerConfiguration();
        Assert.assertEquals(expected, conf);
        String json = mls.toJson();
        MultiLayerSpace fromJson = MultiLayerSpace.fromJson(json);
        Assert.assertEquals(mls, fromJson);
    }

    @Test
    public void testBidirectional() throws Exception {
        MultiLayerSpace mls = new MultiLayerSpace.Builder().updater(new Sgd(0.005)).seed(12345).layer(new Bidirectional(new LSTMLayerSpace.Builder().nIn(10).nOut(10).build())).build();
        DL4JConfiguration conf = mls.getValue(new double[0]);
        MultiLayerConfiguration c2 = conf.getMultiLayerConfiguration();
        MultiLayerNetwork net = new MultiLayerNetwork(c2);
        net.init();
        Assert.assertEquals(1, net.getnLayers());
        Assert.assertTrue(((net.getLayer(0)) instanceof BidirectionalLayer));
        BidirectionalLayer bl = ((BidirectionalLayer) (net.getLayer(0)));
        Field f = BidirectionalLayer.class.getDeclaredField("fwd");
        Field b = BidirectionalLayer.class.getDeclaredField("bwd");
        f.setAccessible(true);
        b.setAccessible(true);
        LSTM lstmFwd = ((LSTM) (f.get(bl)));
        LSTM lstmBwd = ((LSTM) (b.get(bl)));
        Assert.assertEquals(10, getNIn());
        Assert.assertEquals(10, getNOut());
        Assert.assertEquals(10, getNIn());
        Assert.assertEquals(10, getNOut());
    }

    @Test
    public void testMathOps() {
        ParameterSpace<Integer> firstLayerSize = new IntegerParameterSpace(10, 30);
        ParameterSpace<Integer> secondLayerSize = new org.deeplearning4j.arbiter.optimize.parameter.math.MathOp(firstLayerSize, Op.MUL, 3);
        ParameterSpace<Double> firstLayerLR = new ContinuousParameterSpace(0.01, 0.1);
        ParameterSpace<Double> secondLayerLR = new org.deeplearning4j.arbiter.optimize.parameter.math.MathOp(firstLayerLR, Op.ADD, 0.2);
        MultiLayerSpace mls = new MultiLayerSpace.Builder().updater(new Sgd(0.005)).seed(12345).layer(new DenseLayerSpace.Builder().nOut(firstLayerSize).updater(new org.deeplearning4j.arbiter.conf.updater.AdamSpace(firstLayerLR)).build()).layer(new OutputLayerSpace.Builder().nOut(secondLayerSize).updater(new org.deeplearning4j.arbiter.conf.updater.AdamSpace(secondLayerLR)).activation(SOFTMAX).build()).setInputType(InputType.feedForward(10)).build();
        int nParams = mls.numParameters();
        Assert.assertEquals(2, nParams);
        new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(mls, null);// Initializes the indices

        Random r = new Random(12345);
        for (int i = 0; i < 10; i++) {
            double[] d = new double[nParams];
            for (int j = 0; j < (d.length); j++) {
                d[j] = r.nextDouble();
            }
            MultiLayerConfiguration conf = mls.getValue(d).getMultiLayerConfiguration();
            long l0Size = ((FeedForwardLayer) (conf.getConf(0).getLayer())).getNOut();
            long l1Size = ((FeedForwardLayer) (conf.getConf(1).getLayer())).getNOut();
            Assert.assertEquals((3 * l0Size), l1Size);
            double l0Lr = getIUpdater().getLearningRate(0, 0);
            double l1Lr = getIUpdater().getLearningRate(0, 0);
            Assert.assertEquals((l0Lr + 0.2), l1Lr, 1.0E-6);
        }
    }

    @Test
    public void testDropoutSpace() {
        ParameterSpace<Double> dropout = new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(0.0, 0.5);
        MultiLayerSpace mls = new MultiLayerSpace.Builder().updater(new Sgd(0.005)).dropOut(dropout).seed(12345).layer(new DenseLayerSpace.Builder().nOut(10).build()).layer(new OutputLayerSpace.Builder().nOut(10).activation(SOFTMAX).build()).setInputType(InputType.feedForward(10)).build();
        int nParams = mls.numParameters();
        Assert.assertEquals(1, nParams);
        new org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator(mls, null);// Initializes the indices

        Random r = new Random(12345);
        int countNull = 0;
        int count05 = 0;
        for (int i = 0; i < 10; i++) {
            double[] d = new double[nParams];
            for (int j = 0; j < (d.length); j++) {
                d[j] = r.nextDouble();
            }
            MultiLayerConfiguration conf = mls.getValue(d).getMultiLayerConfiguration();
            IDropout d0 = conf.getConf(0).getLayer().getIDropout();
            IDropout d1 = conf.getConf(1).getLayer().getIDropout();
            if (d0 == null) {
                Assert.assertNull(d1);
                countNull++;
            } else {
                Dropout do0 = ((Dropout) (d0));
                Dropout do1 = ((Dropout) (d1));
                Assert.assertEquals(0.5, do0.getP(), 0.0);
                Assert.assertEquals(0.5, do1.getP(), 0.0);
                count05++;
            }
        }
        Assert.assertTrue((countNull > 0));
        Assert.assertTrue((count05 > 0));
    }

    private static class TestDataSetProvider implements DataProvider {
        @Override
        public Object trainData(Map<String, Object> dataParameters) {
            return new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(Collections.singletonList(new org.nd4j.linalg.dataset.DataSet(Nd4j.create(1, 1, 28, 28), Nd4j.create(1, 10))));
        }

        @Override
        public Object testData(Map<String, Object> dataParameters) {
            return new org.deeplearning4j.datasets.iterator.ExistingDataSetIterator(Collections.singletonList(new org.nd4j.linalg.dataset.DataSet(Nd4j.create(1, 1, 28, 28), Nd4j.create(1, 10))));
        }

        @Override
        public Class<?> getDataType() {
            return DataSetIterator.class;
        }
    }

    @Test
    public void testDropout() {
        MultiLayerSpace mls = new MultiLayerSpace.Builder().updater(new Sgd(0.005)).seed(12345).addLayer(new ConvolutionLayerSpace.Builder().nOut(2).dropOut(new ContinuousParameterSpace(0.4, 0.6)).build()).addLayer(new GlobalPoolingLayerSpace.Builder().dropOut(new ContinuousParameterSpace(0.4, 0.6)).build()).addLayer(new OutputLayerSpace.Builder().activation(SOFTMAX).nIn(10).nOut(5).build()).setInputType(InputType.convolutional(28, 28, 1)).build();
        int nParams = mls.numParameters();
        List<ParameterSpace> l = LeafUtils.getUniqueObjects(mls.collectLeaves());
        int x = 0;
        for (ParameterSpace p : l) {
            int n = p.numParameters();
            int[] arr = new int[n];
            for (int i = 0; i < (arr.length); i++) {
                arr[i] = x++;
            }
            p.setIndices(arr);
        }
        MultiLayerConfiguration conf = mls.getValue(new double[nParams]).getMultiLayerConfiguration();
    }

    @Test
    public void testDropout2() {
        MultiLayerSpace mls = new MultiLayerSpace.Builder().updater(new Sgd(0.005)).seed(12345).addLayer(new ConvolutionLayerSpace.Builder().nOut(2).dropOut(new ContinuousParameterSpace(0.4, 0.6)).build()).addLayer(new DropoutLayerSpace.Builder().dropOut(new ContinuousParameterSpace(0.4, 0.6)).build()).addLayer(new OutputLayerSpace.Builder().activation(SOFTMAX).nIn(10).nOut(5).build()).setInputType(InputType.convolutional(28, 28, 1)).build();
        int nParams = mls.numParameters();
        List<ParameterSpace> l = LeafUtils.getUniqueObjects(mls.collectLeaves());
        int x = 0;
        for (ParameterSpace p : l) {
            int n = p.numParameters();
            int[] arr = new int[n];
            for (int i = 0; i < (arr.length); i++) {
                arr[i] = x++;
            }
            p.setIndices(arr);
        }
        MultiLayerConfiguration conf = mls.getValue(new double[nParams]).getMultiLayerConfiguration();
    }
}

