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
package org.deeplearning4j.gradientcheck;


import Activation.IDENTITY;
import Activation.SOFTMAX;
import Activation.TANH;
import DataType.DOUBLE;
import LossFunction.MSE;
import OptimizationAlgorithm.CONJUGATE_GRADIENT;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import WeightInit.XAVIER;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.misc.ElementWiseMultiplicationLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.NoOp;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.nd4j.linalg.ops.transforms.Transforms;


/**
 *
 *
 * @author Alex Black 14 Aug 2015
 */
@Slf4j
public class GradientCheckTests extends BaseDL4JTest {
    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-6;

    private static final double DEFAULT_MAX_REL_ERROR = 0.001;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-8;

    static {
        Nd4j.setDataType(DOUBLE);
    }

    @Test
    public void testMinibatchApplication() {
        IrisDataSetIterator iter = new IrisDataSetIterator(30, 150);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().miniBatch(false).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new NoOp()).list().layer(0, new DenseLayer.Builder().nIn(4).nOut(3).dist(new NormalDistribution(0, 1)).activation(TANH).build()).layer(1, nIn(3).nOut(3).build()).build();
        MultiLayerNetwork mln = new MultiLayerNetwork(conf);
        mln.init();
        Assert.assertEquals(1, mln.getInputMiniBatchSize());
        DataNormalization scaler = new NormalizerMinMaxScaler();
        scaler.fit(iter);
        iter.setPreProcessor(scaler);
        DataSet ds = iter.next();
        boolean doLearningFirst = true;
        String outputActivation = "tanh";
        String afn = outputActivation;
        String lf = "negativeloglikelihood";
        if (doLearningFirst) {
            // Run a number of iterations of learning
            mln.setInput(ds.getFeatures());
            mln.setLabels(ds.getLabels());
            mln.computeGradientAndScore();
            double scoreBefore = mln.score();
            for (int j = 0; j < 10; j++)
                mln.fit(ds);

            mln.computeGradientAndScore();
            double scoreAfter = mln.score();
            // Can't test in 'characteristic mode of operation' if not learning
            String msg = ((((((((((("testMinibatchApplication() - score did not (sufficiently) decrease during learning - activationFn=" + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst) + " (before=") + scoreBefore) + ", scoreAfter=") + scoreAfter) + ")";
        }
        if (GradientCheckTests.PRINT_RESULTS) {
            System.out.println(((((((("testMinibatchApplication() - activationFn=" + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst));
            for (int j = 0; j < (mln.getnLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(mln, GradientCheckTests.DEFAULT_EPS, GradientCheckTests.DEFAULT_MAX_REL_ERROR, GradientCheckTests.DEFAULT_MIN_ABS_ERROR, GradientCheckTests.PRINT_RESULTS, GradientCheckTests.RETURN_ON_FIRST_FAILURE, ds.getFeatures(), ds.getLabels());
        String msg = (((((("testMinibatchApplication() - activationFn=" + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst;
        Assert.assertTrue(msg, gradOK);
        TestUtils.testModelSerialization(mln);
    }

    @Test
    public void testGradientMLP2LayerIrisSimple() {
        // Parameterized test, testing combinations of:
        // (a) activation function
        // (b) Whether to test at random initialization, or after some learning (i.e., 'characteristic mode of operation')
        // (c) Loss function (with specified output activations)
        Activation[] activFns = new Activation[]{ Activation.SIGMOID, Activation.TANH };
        boolean[] characteristic = new boolean[]{ false, true };// If true: run some backprop steps first

        LossFunction[] lossFunctions = new LossFunction[]{ LossFunction.MCXENT, LossFunction.MSE };
        Activation[] outputActivations = new Activation[]{ Activation.SOFTMAX, Activation.TANH };// i.e., lossFunctions[i] used with outputActivations[i] here

        DataNormalization scaler = new NormalizerMinMaxScaler();
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        scaler.fit(iter);
        iter.setPreProcessor(scaler);
        DataSet ds = iter.next();
        INDArray input = ds.getFeatures();
        INDArray labels = ds.getLabels();
        for (Activation afn : activFns) {
            for (boolean doLearningFirst : characteristic) {
                for (int i = 0; i < (lossFunctions.length); i++) {
                    LossFunction lf = lossFunctions[i];
                    Activation outputActivation = outputActivations[i];
                    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(CONJUGATE_GRADIENT).updater(new NoOp()).seed(12345L).list().layer(0, new DenseLayer.Builder().nIn(4).nOut(3).dist(new NormalDistribution(0, 1)).activation(afn).build()).layer(1, nIn(3).nOut(3).dist(new NormalDistribution(0, 1)).build()).build();
                    MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                    mln.init();
                    if (doLearningFirst) {
                        // Run a number of iterations of learning
                        mln.setInput(ds.getFeatures());
                        mln.setLabels(ds.getLabels());
                        mln.computeGradientAndScore();
                        double scoreBefore = mln.score();
                        for (int j = 0; j < 10; j++)
                            mln.fit(ds);

                        mln.computeGradientAndScore();
                        double scoreAfter = mln.score();
                        // Can't test in 'characteristic mode of operation' if not learning
                        String msg = ((((((((((("testGradMLP2LayerIrisSimple() - score did not (sufficiently) decrease during learning - activationFn=" + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst) + " (before=") + scoreBefore) + ", scoreAfter=") + scoreAfter) + ")";
                        // assertTrue(msg, scoreAfter < 0.8 * scoreBefore);
                    }
                    if (GradientCheckTests.PRINT_RESULTS) {
                        System.out.println(((((((("testGradientMLP2LayerIrisSimpleRandom() - activationFn=" + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst));
                        for (int j = 0; j < (mln.getnLayers()); j++)
                            System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

                    }
                    boolean gradOK = GradientCheckUtil.checkGradients(mln, GradientCheckTests.DEFAULT_EPS, GradientCheckTests.DEFAULT_MAX_REL_ERROR, GradientCheckTests.DEFAULT_MIN_ABS_ERROR, GradientCheckTests.PRINT_RESULTS, GradientCheckTests.RETURN_ON_FIRST_FAILURE, input, labels);
                    String msg = (((((("testGradMLP2LayerIrisSimple() - activationFn=" + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst;
                    Assert.assertTrue(msg, gradOK);
                    TestUtils.testModelSerialization(mln);
                }
            }
        }
    }

    @Test
    public void testGradientMLP2LayerIrisL1L2Simple() {
        // As above (testGradientMLP2LayerIrisSimple()) but with L2, L1, and both L2/L1 applied
        // Need to run gradient through updater, so that L2 can be applied
        Activation[] activFns = new Activation[]{ Activation.SIGMOID, Activation.TANH, Activation.THRESHOLDEDRELU };
        boolean[] characteristic = new boolean[]{ false, true };// If true: run some backprop steps first

        LossFunction[] lossFunctions = new LossFunction[]{ LossFunction.MCXENT, LossFunction.MSE };
        Activation[] outputActivations = new Activation[]{ Activation.SOFTMAX, Activation.TANH };// i.e., lossFunctions[i] used with outputActivations[i] here

        DataNormalization scaler = new NormalizerMinMaxScaler();
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        scaler.fit(iter);
        iter.setPreProcessor(scaler);
        DataSet ds = iter.next();
        INDArray input = ds.getFeatures();
        INDArray labels = ds.getLabels();
        // use l2vals[i] with l1vals[i]
        double[] l2vals = new double[]{ 0.4, 0.0, 0.4, 0.4 };
        double[] l1vals = new double[]{ 0.0, 0.0, 0.5, 0.0 };
        double[] biasL2 = new double[]{ 0.0, 0.0, 0.0, 0.2 };
        double[] biasL1 = new double[]{ 0.0, 0.0, 0.6, 0.0 };
        for (Activation afn : activFns) {
            for (boolean doLearningFirst : characteristic) {
                for (int i = 0; i < (lossFunctions.length); i++) {
                    for (int k = 0; k < (l2vals.length); k++) {
                        LossFunction lf = lossFunctions[i];
                        Activation outputActivation = outputActivations[i];
                        double l2 = l2vals[k];
                        double l1 = l1vals[k];
                        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().l2(l2).l1(l1).l2Bias(biasL2[k]).l1Bias(biasL1[k]).optimizationAlgo(CONJUGATE_GRADIENT).seed(12345L).list().layer(0, new DenseLayer.Builder().nIn(4).nOut(3).dist(new NormalDistribution(0, 1)).updater(new NoOp()).activation(afn).build()).layer(1, nIn(3).nOut(3).dist(new NormalDistribution(0, 1)).updater(new NoOp()).activation(outputActivation).build()).build();
                        MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                        mln.init();
                        doLearningFirst = false;
                        if (doLearningFirst) {
                            // Run a number of iterations of learning
                            mln.setInput(ds.getFeatures());
                            mln.setLabels(ds.getLabels());
                            mln.computeGradientAndScore();
                            double scoreBefore = mln.score();
                            for (int j = 0; j < 10; j++)
                                mln.fit(ds);

                            mln.computeGradientAndScore();
                            double scoreAfter = mln.score();
                            // Can't test in 'characteristic mode of operation' if not learning
                            String msg = ((((((((((((((("testGradMLP2LayerIrisSimple() - score did not (sufficiently) decrease during learning - activationFn=" + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst) + ", l2=") + l2) + ", l1=") + l1) + " (before=") + scoreBefore) + ", scoreAfter=") + scoreAfter) + ")";
                            Assert.assertTrue(msg, (scoreAfter < (0.8 * scoreBefore)));
                        }
                        if (GradientCheckTests.PRINT_RESULTS) {
                            System.out.println(((((((((((("testGradientMLP2LayerIrisSimpleRandom() - activationFn=" + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst) + ", l2=") + l2) + ", l1=") + l1));
                            for (int j = 0; j < (mln.getnLayers()); j++)
                                System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

                        }
                        boolean gradOK = GradientCheckUtil.checkGradients(mln, GradientCheckTests.DEFAULT_EPS, GradientCheckTests.DEFAULT_MAX_REL_ERROR, GradientCheckTests.DEFAULT_MIN_ABS_ERROR, GradientCheckTests.PRINT_RESULTS, GradientCheckTests.RETURN_ON_FIRST_FAILURE, input, labels);
                        String msg = (((((((((("testGradMLP2LayerIrisSimple() - activationFn=" + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst) + ", l2=") + l2) + ", l1=") + l1;
                        Assert.assertTrue(msg, gradOK);
                        TestUtils.testModelSerialization(mln);
                    }
                }
            }
        }
    }

    @Test
    public void testEmbeddingLayerPreluSimple() {
        Random r = new Random(12345);
        int nExamples = 5;
        INDArray input = Nd4j.zeros(nExamples, 1);
        INDArray labels = Nd4j.zeros(nExamples, 3);
        for (int i = 0; i < nExamples; i++) {
            input.putScalar(i, r.nextInt(4));
            labels.putScalar(new int[]{ i, r.nextInt(3) }, 1.0);
        }
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().l2(0.2).l1(0.1).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).seed(12345L).list().layer(new EmbeddingLayer.Builder().nIn(4).nOut(3).weightInit(XAVIER).updater(new NoOp()).build()).layer(new PReLULayer.Builder().inputShape(3).sharedAxes(1).updater(new NoOp()).build()).layer(nIn(3).nOut(3).weightInit(XAVIER).dist(new NormalDistribution(0, 1)).updater(new NoOp()).activation(SOFTMAX).build()).build();
        MultiLayerNetwork mln = new MultiLayerNetwork(conf);
        mln.init();
        if (GradientCheckTests.PRINT_RESULTS) {
            System.out.println("testEmbeddingLayerSimple");
            for (int j = 0; j < (mln.getnLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(mln, GradientCheckTests.DEFAULT_EPS, GradientCheckTests.DEFAULT_MAX_REL_ERROR, GradientCheckTests.DEFAULT_MIN_ABS_ERROR, GradientCheckTests.PRINT_RESULTS, GradientCheckTests.RETURN_ON_FIRST_FAILURE, input, labels);
        String msg = "testEmbeddingLayerSimple";
        Assert.assertTrue(msg, gradOK);
    }

    @Test
    public void testEmbeddingLayerSimple() {
        Random r = new Random(12345);
        int nExamples = 5;
        INDArray input = Nd4j.zeros(nExamples, 1);
        INDArray labels = Nd4j.zeros(nExamples, 3);
        for (int i = 0; i < nExamples; i++) {
            input.putScalar(i, r.nextInt(4));
            labels.putScalar(new int[]{ i, r.nextInt(3) }, 1.0);
        }
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().l2(0.2).l1(0.1).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).seed(12345L).list().layer(0, new EmbeddingLayer.Builder().nIn(4).nOut(3).weightInit(XAVIER).updater(new NoOp()).activation(TANH).build()).layer(1, nIn(3).nOut(3).weightInit(XAVIER).updater(new NoOp()).activation(SOFTMAX).build()).build();
        MultiLayerNetwork mln = new MultiLayerNetwork(conf);
        mln.init();
        if (GradientCheckTests.PRINT_RESULTS) {
            System.out.println("testEmbeddingLayerSimple");
            for (int j = 0; j < (mln.getnLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(mln, GradientCheckTests.DEFAULT_EPS, GradientCheckTests.DEFAULT_MAX_REL_ERROR, GradientCheckTests.DEFAULT_MIN_ABS_ERROR, GradientCheckTests.PRINT_RESULTS, GradientCheckTests.RETURN_ON_FIRST_FAILURE, input, labels);
        String msg = "testEmbeddingLayerSimple";
        Assert.assertTrue(msg, gradOK);
        TestUtils.testModelSerialization(mln);
    }

    @Test
    public void testAutoEncoder() {
        // As above (testGradientMLP2LayerIrisSimple()) but with L2, L1, and both L2/L1 applied
        // Need to run gradient through updater, so that L2 can be applied
        Activation[] activFns = new Activation[]{ Activation.SIGMOID, Activation.TANH };
        boolean[] characteristic = new boolean[]{ false, true };// If true: run some backprop steps first

        LossFunction[] lossFunctions = new LossFunction[]{ LossFunction.MCXENT, LossFunction.MSE };
        Activation[] outputActivations = new Activation[]{ Activation.SOFTMAX, Activation.TANH };
        DataNormalization scaler = new NormalizerMinMaxScaler();
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        scaler.fit(iter);
        iter.setPreProcessor(scaler);
        DataSet ds = iter.next();
        INDArray input = ds.getFeatures();
        INDArray labels = ds.getLabels();
        NormalizerStandardize norm = new NormalizerStandardize();
        norm.fit(ds);
        norm.transform(ds);
        double[] l2vals = new double[]{ 0.2, 0.0, 0.2 };
        double[] l1vals = new double[]{ 0.0, 0.3, 0.3 };// i.e., use l2vals[i] with l1vals[i]

        for (Activation afn : activFns) {
            for (boolean doLearningFirst : characteristic) {
                for (int i = 0; i < (lossFunctions.length); i++) {
                    for (int k = 0; k < (l2vals.length); k++) {
                        LossFunction lf = lossFunctions[i];
                        Activation outputActivation = outputActivations[i];
                        double l2 = l2vals[k];
                        double l1 = l1vals[k];
                        Nd4j.getRandom().setSeed(12345);
                        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new NoOp()).l2(l2).l1(l1).optimizationAlgo(CONJUGATE_GRADIENT).seed(12345L).dist(new NormalDistribution(0, 1)).list().layer(0, new AutoEncoder.Builder().nIn(4).nOut(3).activation(afn).build()).layer(1, nIn(3).nOut(3).activation(outputActivation).build()).build();
                        MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                        mln.init();
                        String msg;
                        if (doLearningFirst) {
                            // Run a number of iterations of learning
                            mln.setInput(ds.getFeatures());
                            mln.setLabels(ds.getLabels());
                            mln.computeGradientAndScore();
                            double scoreBefore = mln.score();
                            for (int j = 0; j < 10; j++)
                                mln.fit(ds);

                            mln.computeGradientAndScore();
                            double scoreAfter = mln.score();
                            // Can't test in 'characteristic mode of operation' if not learning
                            msg = ((((((((((((((("testGradMLP2LayerIrisSimple() - score did not (sufficiently) decrease during learning - activationFn=" + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst) + ", l2=") + l2) + ", l1=") + l1) + " (before=") + scoreBefore) + ", scoreAfter=") + scoreAfter) + ")";
                            Assert.assertTrue(msg, (scoreAfter < scoreBefore));
                        }
                        msg = (((((((((("testGradMLP2LayerIrisSimple() - activationFn=" + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst) + ", l2=") + l2) + ", l1=") + l1;
                        if (GradientCheckTests.PRINT_RESULTS) {
                            System.out.println(msg);
                            for (int j = 0; j < (mln.getnLayers()); j++)
                                System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

                        }
                        boolean gradOK = GradientCheckUtil.checkGradients(mln, GradientCheckTests.DEFAULT_EPS, GradientCheckTests.DEFAULT_MAX_REL_ERROR, GradientCheckTests.DEFAULT_MIN_ABS_ERROR, GradientCheckTests.PRINT_RESULTS, GradientCheckTests.RETURN_ON_FIRST_FAILURE, input, labels);
                        Assert.assertTrue(msg, gradOK);
                        TestUtils.testModelSerialization(mln);
                    }
                }
            }
        }
    }

    @Test
    public void elementWiseMultiplicationLayerTest() {
        for (Activation a : new Activation[]{ Activation.IDENTITY, Activation.TANH }) {
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(CONJUGATE_GRADIENT).updater(new NoOp()).seed(12345L).weightInit(new UniformDistribution(0, 1)).graphBuilder().addInputs("features").addLayer("dense", new DenseLayer.Builder().nIn(4).nOut(4).activation(TANH).build(), "features").addLayer("elementWiseMul", new ElementWiseMultiplicationLayer.Builder().nIn(4).nOut(4).activation(a).build(), "dense").addLayer("loss", new LossLayer.Builder(LossFunction.COSINE_PROXIMITY).activation(IDENTITY).build(), "elementWiseMul").setOutputs("loss").build();
            ComputationGraph netGraph = new ComputationGraph(conf);
            netGraph.init();
            log.info(("params before learning: " + (netGraph.getLayer(1).paramTable())));
            // Run a number of iterations of learning manually make some pseudo data
            // the ides is simple: since we do a element wise multiplication layer (just a scaling), we want the cos sim
            // is mainly decided by the fourth value, if everything runs well, we will get a large weight for the fourth value
            INDArray features = Nd4j.create(new double[][]{ new double[]{ 1, 2, 3, 4 }, new double[]{ 1, 2, 3, 1 }, new double[]{ 1, 2, 3, 0 } });
            INDArray labels = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 8 }, new double[]{ 1, 1, 1, 2 }, new double[]{ 1, 1, 1, 1 } });
            netGraph.setInputs(features);
            netGraph.setLabels(labels);
            netGraph.computeGradientAndScore();
            double scoreBefore = netGraph.score();
            String msg;
            for (int epoch = 0; epoch < 5; epoch++)
                netGraph.fit(new INDArray[]{ features }, new INDArray[]{ labels });

            netGraph.computeGradientAndScore();
            double scoreAfter = netGraph.score();
            // Can't test in 'characteristic mode of operation' if not learning
            msg = (((("elementWiseMultiplicationLayerTest() - score did not (sufficiently) decrease during learning - activationFn=" + ((((((("Id" + ", lossFn=") + "Cos-sim") + ", outputActivation=") + "Id") + ", doLearningFirst=") + "true") + " (before=")) + scoreBefore) + ", scoreAfter=") + scoreAfter) + ")";
            Assert.assertTrue(msg, (scoreAfter < (0.8 * scoreBefore)));
            // expectation in case linear regression(with only element wise multiplication layer): large weight for the fourth weight
            log.info(("params after learning: " + (netGraph.getLayer(1).paramTable())));
            boolean gradOK = GradientCheckUtil.checkGradients(netGraph, GradientCheckTests.DEFAULT_EPS, GradientCheckTests.DEFAULT_MAX_REL_ERROR, GradientCheckTests.DEFAULT_MIN_ABS_ERROR, GradientCheckTests.PRINT_RESULTS, GradientCheckTests.RETURN_ON_FIRST_FAILURE, new INDArray[]{ features }, new INDArray[]{ labels });
            msg = "elementWiseMultiplicationLayerTest() - activationFn=" + (((((("ID" + ", lossFn=") + "Cos-sim") + ", outputActivation=") + "Id") + ", doLearningFirst=") + "true");
            Assert.assertTrue(msg, gradOK);
            TestUtils.testModelSerialization(netGraph);
        }
    }

    @Test
    public void testEmbeddingSequenceLayer() {
        Nd4j.getRandom().setSeed(12345);
        for (boolean maskArray : new boolean[]{ false, true }) {
            for (int inputRank : new int[]{ 2, 3 }) {
                MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).updater(new NoOp()).weightInit(new NormalDistribution(0, 1)).list().layer(new EmbeddingSequenceLayer.Builder().nIn(8).nOut(4).build()).layer(new RnnOutputLayer.Builder().nIn(4).nOut(3).activation(TANH).lossFunction(MSE).build()).build();
                MultiLayerNetwork net = new MultiLayerNetwork(conf);
                net.init();
                INDArray in = Transforms.floor(Nd4j.rand(3, 6).muli(8));// Integers 0 to 7 inclusive

                INDArray label = Nd4j.rand(new int[]{ 3, 3, 6 });
                if (inputRank == 3) {
                    // Reshape from [3,6] to [3,1,6]
                    in = in.reshape('c', 3, 1, 6);
                }
                INDArray fMask = null;
                if (maskArray) {
                    fMask = Nd4j.create(new double[][]{ new double[]{ 1, 1, 1, 1, 1, 1 }, new double[]{ 1, 1, 0, 0, 0, 0 }, new double[]{ 1, 0, 0, 0, 0, 0 } });
                }
                String msg = (("mask=" + maskArray) + ", inputRank=") + inputRank;
                boolean gradOK = GradientCheckUtil.checkGradients(net, GradientCheckTests.DEFAULT_EPS, GradientCheckTests.DEFAULT_MAX_REL_ERROR, GradientCheckTests.DEFAULT_MIN_ABS_ERROR, GradientCheckTests.PRINT_RESULTS, GradientCheckTests.RETURN_ON_FIRST_FAILURE, in, label, fMask, null);
                Assert.assertTrue(msg, gradOK);
                TestUtils.testModelSerialization(net);
                // Also: if mask is present, double check that the masked steps don't impact score
                if (maskArray) {
                    DataSet ds = new DataSet(in, label, fMask, null);
                    double score = net.score(ds);
                    if (inputRank == 2) {
                        in.putScalar(1, 2, 0);
                        in.putScalar(2, 1, 0);
                        in.putScalar(2, 2, 0);
                    } else {
                        in.putScalar(1, 0, 2, 0);
                        in.putScalar(2, 0, 1, 0);
                        in.putScalar(2, 0, 2, 0);
                    }
                    double score2 = net.score(ds);
                    Assert.assertEquals(score, score2, 1.0E-6);
                    if (inputRank == 2) {
                        in.putScalar(1, 2, 1);
                        in.putScalar(2, 1, 1);
                        in.putScalar(2, 2, 1);
                    } else {
                        in.putScalar(1, 0, 2, 1);
                        in.putScalar(2, 0, 1, 1);
                        in.putScalar(2, 0, 2, 1);
                    }
                    double score3 = net.score(ds);
                    Assert.assertEquals(score, score3, 1.0E-6);
                }
            }
        }
    }

    @Test
    public void testGradientWeightDecay() {
        Activation[] activFns = new Activation[]{ Activation.SIGMOID, Activation.TANH, Activation.THRESHOLDEDRELU };
        boolean[] characteristic = new boolean[]{ false, true };// If true: run some backprop steps first

        LossFunction[] lossFunctions = new LossFunction[]{ LossFunction.MCXENT, LossFunction.MSE };
        Activation[] outputActivations = new Activation[]{ Activation.SOFTMAX, Activation.TANH };// i.e., lossFunctions[i] used with outputActivations[i] here

        DataNormalization scaler = new NormalizerMinMaxScaler();
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        scaler.fit(iter);
        iter.setPreProcessor(scaler);
        DataSet ds = iter.next();
        INDArray input = ds.getFeatures();
        INDArray labels = ds.getLabels();
        // use l2vals[i] with l1vals[i]
        double[] l2vals = new double[]{ 0.4, 0.0, 0.4, 0.4, 0.0, 0.0 };
        double[] l1vals = new double[]{ 0.0, 0.0, 0.5, 0.0, 0.5, 0.0 };
        double[] biasL2 = new double[]{ 0.0, 0.0, 0.0, 0.2, 0.0, 0.0 };
        double[] biasL1 = new double[]{ 0.0, 0.0, 0.6, 0.0, 0.0, 0.5 };
        double[] wdVals = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.4, 0.0 };
        double[] wdBias = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.4 };
        for (Activation afn : activFns) {
            for (int i = 0; i < (lossFunctions.length); i++) {
                for (int k = 0; k < (l2vals.length); k++) {
                    LossFunction lf = lossFunctions[i];
                    Activation outputActivation = outputActivations[i];
                    double l2 = l2vals[k];
                    double l1 = l1vals[k];
                    MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().l2(l2).l1(l1).l2Bias(biasL2[k]).l1Bias(biasL1[k]).weightDecay(wdVals[k]).weightDecayBias(wdBias[k]).optimizationAlgo(CONJUGATE_GRADIENT).seed(12345L).list().layer(0, new DenseLayer.Builder().nIn(4).nOut(3).dist(new NormalDistribution(0, 1)).updater(new NoOp()).activation(afn).build()).layer(1, nIn(3).nOut(3).dist(new NormalDistribution(0, 1)).updater(new NoOp()).activation(outputActivation).build()).build();
                    MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                    mln.init();
                    boolean gradOK1 = GradientCheckUtil.checkGradients(mln, GradientCheckTests.DEFAULT_EPS, GradientCheckTests.DEFAULT_MAX_REL_ERROR, GradientCheckTests.DEFAULT_MIN_ABS_ERROR, GradientCheckTests.PRINT_RESULTS, GradientCheckTests.RETURN_ON_FIRST_FAILURE, input, labels);
                    String msg = (((((((("testGradientWeightDecay() - activationFn=" + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", l2=") + l2) + ", l1=") + l1;
                    Assert.assertTrue(msg, gradOK1);
                    TestUtils.testModelSerialization(mln);
                }
            }
        }
    }

    @Test
    public void testGradientMLP2LayerIrisLayerNorm() {
        // Parameterized test, testing combinations of:
        // (a) activation function
        // (b) Whether to test at random initialization, or after some learning (i.e., 'characteristic mode of operation')
        // (c) Loss function (with specified output activations)
        // (d) Layer Normalization enabled / disabled
        Activation[] activFns = new Activation[]{ Activation.SIGMOID, Activation.TANH };
        boolean[] characteristic = new boolean[]{ true, false };// If true: run some backprop steps first

        LossFunction[] lossFunctions = new LossFunction[]{ LossFunction.MCXENT, LossFunction.MSE };
        Activation[] outputActivations = new Activation[]{ Activation.SOFTMAX, Activation.TANH };// i.e., lossFunctions[i] used with outputActivations[i] here

        DataNormalization scaler = new NormalizerMinMaxScaler();
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        scaler.fit(iter);
        iter.setPreProcessor(scaler);
        DataSet ds = iter.next();
        INDArray input = ds.getFeatures();
        INDArray labels = ds.getLabels();
        for (Activation afn : activFns) {
            for (boolean doLearningFirst : characteristic) {
                for (int i = 0; i < (lossFunctions.length); i++) {
                    for (boolean layerNorm : new boolean[]{ true, false }) {
                        LossFunction lf = lossFunctions[i];
                        Activation outputActivation = outputActivations[i];
                        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(CONJUGATE_GRADIENT).updater(new NoOp()).seed(12345L).list().layer(0, new DenseLayer.Builder().nIn(4).nOut(3).dist(new NormalDistribution(0, 1)).hasLayerNorm(layerNorm).activation(afn).build()).layer(1, nIn(3).nOut(3).dist(new NormalDistribution(0, 1)).build()).build();
                        MultiLayerNetwork mln = new MultiLayerNetwork(conf);
                        mln.init();
                        if (doLearningFirst) {
                            // Run a number of iterations of learning
                            mln.setInput(ds.getFeatures());
                            mln.setLabels(ds.getLabels());
                            mln.computeGradientAndScore();
                            double scoreBefore = mln.score();
                            for (int j = 0; j < 10; j++)
                                mln.fit(ds);

                            mln.computeGradientAndScore();
                            double scoreAfter = mln.score();
                            // Can't test in 'characteristic mode of operation' if not learning
                            String msg = ((((((((((((("testGradMLP2LayerIrisSimple() - score did not (sufficiently) decrease during learning - activationFn=" + afn) + ", lossFn=") + lf) + ", layerNorm=") + layerNorm) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst) + " (before=") + scoreBefore) + ", scoreAfter=") + scoreAfter) + ")";
                            // assertTrue(msg, scoreAfter < 0.8 * scoreBefore);
                        }
                        if (GradientCheckTests.PRINT_RESULTS) {
                            System.out.println(((((((((("testGradientMLP2LayerIrisSimpleRandom() - activationFn=" + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst) + ", layerNorm=") + layerNorm));
                            for (int j = 0; j < (mln.getnLayers()); j++)
                                System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

                        }
                        boolean gradOK = GradientCheckUtil.checkGradients(mln, GradientCheckTests.DEFAULT_EPS, GradientCheckTests.DEFAULT_MAX_REL_ERROR, GradientCheckTests.DEFAULT_MIN_ABS_ERROR, GradientCheckTests.PRINT_RESULTS, GradientCheckTests.RETURN_ON_FIRST_FAILURE, input, labels);
                        String msg = (((((((("testGradMLP2LayerIrisSimple() - activationFn=" + afn) + ", lossFn=") + lf) + ", outputActivation=") + outputActivation) + ", doLearningFirst=") + doLearningFirst) + ", layerNorm=") + layerNorm;
                        Assert.assertTrue(msg, gradOK);
                        TestUtils.testModelSerialization(mln);
                    }
                }
            }
        }
    }
}

