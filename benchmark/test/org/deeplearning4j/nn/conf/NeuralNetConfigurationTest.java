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
package org.deeplearning4j.nn.conf;


import Activation.TANH;
import DefaultParamInitializer.WEIGHT_KEY;
import LossFunctions.LossFunction.KL_DIVERGENCE;
import LossFunctions.LossFunction.MSE;
import WeightInit.UNIFORM;
import java.util.List;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.layers.BaseLayer;
import org.deeplearning4j.nn.conf.layers.BatchNormalization;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.deeplearning4j.nn.conf.stepfunctions.DefaultStepFunction;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.ConvexOptimizer;
import org.deeplearning4j.optimize.stepfunctions.NegativeDefaultStepFunction;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.learning.regularization.Regularization;


/**
 * Created by agibsonccc on 11/27/14.
 */
public class NeuralNetConfigurationTest extends BaseDL4JTest {
    final DataSet trainingSet = createData();

    @Test
    public void testJson() {
        NeuralNetConfiguration conf = NeuralNetConfigurationTest.getConfig(1, 1, new WeightInitXavier(), true);
        String json = conf.toJson();
        NeuralNetConfiguration read = NeuralNetConfiguration.fromJson(json);
        Assert.assertEquals(conf, read);
    }

    @Test
    public void testYaml() {
        NeuralNetConfiguration conf = NeuralNetConfigurationTest.getConfig(1, 1, new WeightInitXavier(), true);
        String json = conf.toYaml();
        NeuralNetConfiguration read = NeuralNetConfiguration.fromYaml(json);
        Assert.assertEquals(conf, read);
    }

    @Test
    public void testClone() {
        NeuralNetConfiguration conf = NeuralNetConfigurationTest.getConfig(1, 1, new WeightInitUniform(), true);
        BaseLayer bl = ((BaseLayer) (conf.getLayer()));
        conf.setStepFunction(new DefaultStepFunction());
        NeuralNetConfiguration conf2 = conf.clone();
        Assert.assertEquals(conf, conf2);
        Assert.assertNotSame(conf, conf2);
        Assert.assertNotSame(conf.getLayer(), conf2.getLayer());
        Assert.assertNotSame(conf.getStepFunction(), conf2.getStepFunction());
    }

    @Test
    public void testRNG() {
        DenseLayer layer = new DenseLayer.Builder().nIn(trainingSet.numInputs()).nOut(trainingSet.numOutcomes()).weightInit(UNIFORM).activation(TANH).build();
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().seed(123).optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT).layer(layer).build();
        long numParams = conf.getLayer().initializer().numParams(conf);
        INDArray params = Nd4j.create(1, numParams);
        Layer model = conf.getLayer().instantiate(conf, null, 0, params, true);
        INDArray modelWeights = model.getParam(WEIGHT_KEY);
        DenseLayer layer2 = new DenseLayer.Builder().nIn(trainingSet.numInputs()).nOut(trainingSet.numOutcomes()).weightInit(UNIFORM).activation(TANH).build();
        NeuralNetConfiguration conf2 = new NeuralNetConfiguration.Builder().seed(123).optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT).layer(layer2).build();
        long numParams2 = conf2.getLayer().initializer().numParams(conf);
        INDArray params2 = Nd4j.create(1, numParams);
        Layer model2 = conf2.getLayer().instantiate(conf2, null, 0, params2, true);
        INDArray modelWeights2 = model2.getParam(WEIGHT_KEY);
        Assert.assertEquals(modelWeights, modelWeights2);
    }

    @Test
    public void testSetSeedSize() {
        Nd4j.getRandom().setSeed(123);
        Layer model = NeuralNetConfigurationTest.getLayer(trainingSet.numInputs(), trainingSet.numOutcomes(), new WeightInitXavier(), true);
        INDArray modelWeights = model.getParam(WEIGHT_KEY);
        Nd4j.getRandom().setSeed(123);
        Layer model2 = NeuralNetConfigurationTest.getLayer(trainingSet.numInputs(), trainingSet.numOutcomes(), new WeightInitXavier(), true);
        INDArray modelWeights2 = model2.getParam(WEIGHT_KEY);
        Assert.assertEquals(modelWeights, modelWeights2);
    }

    @Test
    public void testSetSeedNormalized() {
        Nd4j.getRandom().setSeed(123);
        Layer model = NeuralNetConfigurationTest.getLayer(trainingSet.numInputs(), trainingSet.numOutcomes(), new WeightInitXavier(), true);
        INDArray modelWeights = model.getParam(WEIGHT_KEY);
        Nd4j.getRandom().setSeed(123);
        Layer model2 = NeuralNetConfigurationTest.getLayer(trainingSet.numInputs(), trainingSet.numOutcomes(), new WeightInitXavier(), true);
        INDArray modelWeights2 = model2.getParam(WEIGHT_KEY);
        Assert.assertEquals(modelWeights, modelWeights2);
    }

    @Test
    public void testSetSeedXavier() {
        Nd4j.getRandom().setSeed(123);
        Layer model = NeuralNetConfigurationTest.getLayer(trainingSet.numInputs(), trainingSet.numOutcomes(), new WeightInitUniform(), true);
        INDArray modelWeights = model.getParam(WEIGHT_KEY);
        Nd4j.getRandom().setSeed(123);
        Layer model2 = NeuralNetConfigurationTest.getLayer(trainingSet.numInputs(), trainingSet.numOutcomes(), new WeightInitUniform(), true);
        INDArray modelWeights2 = model2.getParam(WEIGHT_KEY);
        Assert.assertEquals(modelWeights, modelWeights2);
    }

    @Test
    public void testSetSeedDistribution() {
        Nd4j.getRandom().setSeed(123);
        Layer model = NeuralNetConfigurationTest.getLayer(trainingSet.numInputs(), trainingSet.numOutcomes(), new WeightInitDistribution(new NormalDistribution(1, 1)), true);
        INDArray modelWeights = model.getParam(WEIGHT_KEY);
        Nd4j.getRandom().setSeed(123);
        Layer model2 = NeuralNetConfigurationTest.getLayer(trainingSet.numInputs(), trainingSet.numOutcomes(), new WeightInitDistribution(new NormalDistribution(1, 1)), true);
        INDArray modelWeights2 = model2.getParam(WEIGHT_KEY);
        Assert.assertEquals(modelWeights, modelWeights2);
    }

    @Test
    public void testLearningRateByParam() {
        double lr = 0.01;
        double biasLr = 0.02;
        int[] nIns = new int[]{ 4, 3, 3 };
        int[] nOuts = new int[]{ 3, 3, 3 };
        int oldScore = 1;
        int newScore = 1;
        int iteration = 3;
        INDArray gradientW = Nd4j.ones(nIns[0], nOuts[0]);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.3)).list().layer(0, new DenseLayer.Builder().nIn(nIns[0]).nOut(nOuts[0]).updater(new Sgd(lr)).biasUpdater(new Sgd(biasLr)).build()).layer(1, new BatchNormalization.Builder().nIn(nIns[1]).nOut(nOuts[1]).updater(new Sgd(0.7)).build()).layer(2, new OutputLayer.Builder().nIn(nIns[2]).nOut(nOuts[2]).lossFunction(MSE).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        ConvexOptimizer opt = new org.deeplearning4j.optimize.solvers.StochasticGradientDescent(net.getDefaultConfiguration(), new NegativeDefaultStepFunction(), null, net);
        Assert.assertEquals(lr, getLearningRate(), 1.0E-4);
        Assert.assertEquals(biasLr, getLearningRate(), 1.0E-4);
        Assert.assertEquals(0.7, getLearningRate(), 1.0E-4);
        Assert.assertEquals(0.3, getLearningRate(), 1.0E-4);// From global LR

        Assert.assertEquals(0.3, getLearningRate(), 1.0E-4);// From global LR

    }

    @Test
    public void testLeakyreluAlpha() {
        // FIXME: Make more generic to use neuralnetconfs
        int sizeX = 4;
        int scaleX = 10;
        System.out.println("Here is a leaky vector..");
        INDArray leakyVector = Nd4j.linspace((-1), 1, sizeX, Nd4j.dataType());
        leakyVector = leakyVector.mul(scaleX);
        System.out.println(leakyVector);
        double myAlpha = 0.5;
        System.out.println("======================");
        System.out.println("Exec and Return: Leaky Relu transformation with alpha = 0.5 ..");
        System.out.println("======================");
        INDArray outDef = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.scalar.LeakyReLU(leakyVector.dup(), myAlpha));
        System.out.println(outDef);
        String confActivation = "leakyrelu";
        Object[] confExtra = new Object[]{ myAlpha };
        INDArray outMine = Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.scalar.LeakyReLU(leakyVector.dup(), myAlpha));
        System.out.println("======================");
        System.out.println("Exec and Return: Leaky Relu transformation with a value via getOpFactory");
        System.out.println("======================");
        System.out.println(outMine);
        // Test equality for ndarray elementwise
        // assertArrayEquals(..)
    }

    @Test
    public void testL1L2ByParam() {
        double l1 = 0.01;
        double l2 = 0.07;
        int[] nIns = new int[]{ 4, 3, 3 };
        int[] nOuts = new int[]{ 3, 3, 3 };
        int oldScore = 1;
        int newScore = 1;
        int iteration = 3;
        INDArray gradientW = Nd4j.ones(nIns[0], nOuts[0]);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().l1(l1).l2(l2).list().layer(0, new DenseLayer.Builder().nIn(nIns[0]).nOut(nOuts[0]).build()).layer(1, new BatchNormalization.Builder().nIn(nIns[1]).nOut(nOuts[1]).l2(0.5).build()).layer(2, new OutputLayer.Builder().nIn(nIns[2]).nOut(nOuts[2]).lossFunction(MSE).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        ConvexOptimizer opt = new org.deeplearning4j.optimize.solvers.StochasticGradientDescent(net.getDefaultConfiguration(), new NegativeDefaultStepFunction(), null, net);
        Assert.assertEquals(l1, TestUtils.getL1(net.getLayer(0).conf().getLayer().getRegularizationByParam("W")), 1.0E-4);
        List<Regularization> r = net.getLayer(0).conf().getLayer().getRegularizationByParam("b");
        Assert.assertEquals(0, r.size());
        r = net.getLayer(1).conf().getLayer().getRegularizationByParam("beta");
        Assert.assertTrue(((r == null) || (r.isEmpty())));
        r = net.getLayer(1).conf().getLayer().getRegularizationByParam("gamma");
        Assert.assertTrue(((r == null) || (r.isEmpty())));
        r = net.getLayer(1).conf().getLayer().getRegularizationByParam("mean");
        Assert.assertTrue(((r == null) || (r.isEmpty())));
        r = net.getLayer(1).conf().getLayer().getRegularizationByParam("var");
        Assert.assertTrue(((r == null) || (r.isEmpty())));
        Assert.assertEquals(l2, TestUtils.getL2(net.getLayer(2).conf().getLayer().getRegularizationByParam("W")), 1.0E-4);
        r = net.getLayer(2).conf().getLayer().getRegularizationByParam("b");
        Assert.assertTrue(((r == null) || (r.isEmpty())));
    }

    @Test
    public void testLayerPretrainConfig() {
        boolean pretrain = true;
        VariationalAutoencoder layer = new VariationalAutoencoder.Builder().nIn(10).nOut(5).updater(new Sgd(0.1)).lossFunction(KL_DIVERGENCE).build();
        NeuralNetConfiguration conf = new NeuralNetConfiguration.Builder().seed(42).layer(layer).build();
    }
}

