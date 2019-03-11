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


import Activation.HARDSIGMOID;
import Activation.IDENTITY;
import Activation.RELU;
import Activation.SOFTMAX;
import Activation.TANH;
import BackpropType.Standard;
import BackpropType.TruncatedBPTT;
import GradientNormalization.ClipElementWiseAbsoluteValue;
import LossFunctions.LossFunction;
import LossFunctions.LossFunction.MSE;
import NeuralNetConfiguration.Builder;
import OptimizationAlgorithm.LBFGS;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import WeightInit.XAVIER;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.constraint.UnitNormConstraint;
import org.deeplearning4j.nn.conf.distribution.ConstantDistribution;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToRnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToCnnPreProcessor;
import org.deeplearning4j.nn.conf.serde.JsonMappers;
import org.deeplearning4j.nn.conf.weightnoise.DropConnect;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInitRelu;
import org.deeplearning4j.nn.weights.WeightInitXavier;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.shade.jackson.core.JsonProcessingException;


/**
 * Created by susaneraly on 2/15/17.
 */
@Slf4j
public class TransferLearningMLNTest extends BaseDL4JTest {
    @Test
    public void simpleFineTune() {
        long rng = 12345L;
        DataSet randomData = new DataSet(Nd4j.rand(10, 4), Nd4j.rand(10, 3));
        // original conf
        NeuralNetConfiguration.Builder confToChange = new NeuralNetConfiguration.Builder().seed(rng).optimizationAlgo(LBFGS).updater(new Nesterovs(0.01, 0.99));
        MultiLayerNetwork modelToFineTune = new MultiLayerNetwork(confToChange.list().layer(0, nOut(3).build()).layer(1, nOut(3).build()).build());
        modelToFineTune.init();
        // model after applying changes with transfer learning
        MultiLayerNetwork modelNow = new TransferLearning.Builder(modelToFineTune).fineTuneConfiguration(// Intent: override both weight and bias LR, unless bias LR is manually set also
        new FineTuneConfiguration.Builder().seed(rng).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new RmsProp(0.5)).l2(0.4).build()).build();
        for (Layer l : modelNow.getLayers()) {
            BaseLayer bl = ((BaseLayer) (l.conf().getLayer()));
            Assert.assertEquals(new RmsProp(0.5), bl.getIUpdater());
        }
        NeuralNetConfiguration.Builder confSet = new NeuralNetConfiguration.Builder().seed(rng).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new RmsProp(0.5)).l2(0.4);
        MultiLayerNetwork expectedModel = new MultiLayerNetwork(confSet.list().layer(0, nOut(3).build()).layer(1, nOut(3).build()).build());
        expectedModel.init();
        expectedModel.setParams(modelToFineTune.params().dup());
        Assert.assertEquals(expectedModel.params(), modelNow.params());
        // Check json
        MultiLayerConfiguration expectedConf = expectedModel.getLayerWiseConfigurations();
        Assert.assertEquals(expectedConf.toJson(), modelNow.getLayerWiseConfigurations().toJson());
        // Check params after fit
        modelNow.fit(randomData);
        expectedModel.fit(randomData);
        Assert.assertEquals(modelNow.score(), expectedModel.score(), 1.0E-6);
        INDArray pExp = expectedModel.params();
        INDArray pNow = modelNow.params();
        Assert.assertEquals(pExp, pNow);
    }

    @Test
    public void testNoutChanges() {
        DataSet randomData = new DataSet(Nd4j.rand(10, 4), Nd4j.rand(10, 2));
        NeuralNetConfiguration.Builder equivalentConf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.1));
        FineTuneConfiguration overallConf = new FineTuneConfiguration.Builder().updater(new Sgd(0.1)).build();
        MultiLayerNetwork modelToFineTune = new MultiLayerNetwork(equivalentConf.list().layer(0, nOut(5).build()).layer(1, nOut(2).build()).layer(2, nOut(3).build()).layer(3, nOut(3).build()).build());
        modelToFineTune.init();
        MultiLayerNetwork modelNow = new TransferLearning.Builder(modelToFineTune).fineTuneConfiguration(overallConf).nOutReplace(3, 2, XAVIER, XAVIER).nOutReplace(0, 3, XAVIER, new NormalDistribution(1, 0.1)).build();
        MultiLayerNetwork modelExpectedArch = new MultiLayerNetwork(equivalentConf.list().layer(0, nOut(3).build()).layer(1, nOut(2).build()).layer(2, nOut(3).build()).layer(3, nOut(2).build()).build());
        modelExpectedArch.init();
        // Will fail - expected because of dist and weight init changes
        // assertEquals(modelExpectedArch.getLayerWiseConfigurations().toJson(), modelNow.getLayerWiseConfigurations().toJson());
        BaseLayer bl0 = ((BaseLayer) (modelNow.getLayerWiseConfigurations().getConf(0).getLayer()));
        BaseLayer bl1 = ((BaseLayer) (modelNow.getLayerWiseConfigurations().getConf(1).getLayer()));
        BaseLayer bl3 = ((BaseLayer) (modelNow.getLayerWiseConfigurations().getConf(3).getLayer()));
        Assert.assertEquals(bl0.getWeightInitFn().getClass(), WeightInitXavier.class);
        try {
            Assert.assertEquals(JsonMappers.getMapper().writeValueAsString(bl1.getWeightInitFn()), JsonMappers.getMapper().writeValueAsString(new org.deeplearning4j.nn.weights.WeightInitDistribution(new NormalDistribution(1, 0.1))));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(bl3.getWeightInitFn(), new WeightInitXavier());
        // modelNow should have the same architecture as modelExpectedArch
        Assert.assertArrayEquals(modelExpectedArch.params().shape(), modelNow.params().shape());
        Assert.assertArrayEquals(modelExpectedArch.getLayer(0).params().shape(), modelNow.getLayer(0).params().shape());
        Assert.assertArrayEquals(modelExpectedArch.getLayer(1).params().shape(), modelNow.getLayer(1).params().shape());
        Assert.assertArrayEquals(modelExpectedArch.getLayer(2).params().shape(), modelNow.getLayer(2).params().shape());
        Assert.assertArrayEquals(modelExpectedArch.getLayer(3).params().shape(), modelNow.getLayer(3).params().shape());
        modelNow.setParams(modelExpectedArch.params());
        // fit should give the same results
        modelExpectedArch.fit(randomData);
        modelNow.fit(randomData);
        Assert.assertEquals(modelExpectedArch.score(), modelNow.score(), 1.0E-6);
        Assert.assertEquals(modelExpectedArch.params(), modelNow.params());
    }

    @Test
    public void testRemoveAndAdd() {
        DataSet randomData = new DataSet(Nd4j.rand(10, 4), Nd4j.rand(10, 3));
        NeuralNetConfiguration.Builder equivalentConf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.1));
        FineTuneConfiguration overallConf = new FineTuneConfiguration.Builder().updater(new Sgd(0.1)).build();
        MultiLayerNetwork modelToFineTune = // overallConf.list()
        new MultiLayerNetwork(equivalentConf.list().layer(0, nOut(5).build()).layer(1, nOut(2).build()).layer(2, nOut(3).build()).layer(3, nOut(3).build()).build());
        modelToFineTune.init();
        MultiLayerNetwork modelNow = new TransferLearning.Builder(modelToFineTune).fineTuneConfiguration(overallConf).nOutReplace(0, 7, XAVIER, XAVIER).nOutReplace(2, 5, XAVIER).removeOutputLayer().addLayer(nOut(3).updater(new Sgd(0.5)).activation(SOFTMAX).build()).build();
        MultiLayerNetwork modelExpectedArch = new MultiLayerNetwork(equivalentConf.list().layer(0, nOut(7).build()).layer(1, nOut(2).build()).layer(2, nOut(5).build()).layer(3, nOut(3).build()).build());
        modelExpectedArch.init();
        // modelNow should have the same architecture as modelExpectedArch
        Assert.assertArrayEquals(modelExpectedArch.params().shape(), modelNow.params().shape());
        Assert.assertArrayEquals(modelExpectedArch.getLayer(0).params().shape(), modelNow.getLayer(0).params().shape());
        Assert.assertArrayEquals(modelExpectedArch.getLayer(1).params().shape(), modelNow.getLayer(1).params().shape());
        Assert.assertArrayEquals(modelExpectedArch.getLayer(2).params().shape(), modelNow.getLayer(2).params().shape());
        Assert.assertArrayEquals(modelExpectedArch.getLayer(3).params().shape(), modelNow.getLayer(3).params().shape());
        modelNow.setParams(modelExpectedArch.params());
        // fit should give the same results
        modelExpectedArch.fit(randomData);
        modelNow.fit(randomData);
        double scoreExpected = modelExpectedArch.score();
        double scoreActual = modelNow.score();
        Assert.assertEquals(scoreExpected, scoreActual, 1.0E-8);
        Assert.assertEquals(modelExpectedArch.params(), modelNow.params());
    }

    @Test
    public void testRemoveAndProcessing() {
        int V_WIDTH = 130;
        int V_HEIGHT = 130;
        int V_NFRAMES = 150;
        MultiLayerConfiguration confForArchitecture = // Output: (15-3+0)/2+1 = 7 -> 7*7*10 = 490
        // (31-3+0)/2+1 = 15
        // Output: (130-10+0)/4+1 = 31 -> 31*31*30
        // l2 regularization on all layers
        new NeuralNetConfiguration.Builder().seed(12345).l2(0.001).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new AdaGrad(0.4)).list().layer(0, // 3 channels: RGB
        nOut(30).stride(4, 4).activation(RELU).weightInit(WeightInit.RELU).build()).layer(1, kernelSize(3, 3).stride(2, 2).build()).layer(2, nOut(10).stride(2, 2).activation(RELU).weightInit(WeightInit.RELU).build()).layer(3, nOut(50).weightInit(WeightInit.RELU).updater(new AdaGrad(0.5)).gradientNormalization(ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(10).build()).layer(4, nOut(50).weightInit(XAVIER).updater(new AdaGrad(0.6)).gradientNormalization(ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(10).build()).layer(5, // 4 possible shapes: circle, square, arc, line
        nOut(4).weightInit(XAVIER).gradientNormalization(ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(10).build()).inputPreProcessor(0, new RnnToCnnPreProcessor(V_HEIGHT, V_WIDTH, 3)).inputPreProcessor(3, new CnnToFeedForwardPreProcessor(7, 7, 10)).inputPreProcessor(4, new FeedForwardToRnnPreProcessor()).backpropType(TruncatedBPTT).tBPTTForwardLength((V_NFRAMES / 5)).tBPTTBackwardLength((V_NFRAMES / 5)).build();
        MultiLayerNetwork modelExpectedArch = new MultiLayerNetwork(confForArchitecture);
        modelExpectedArch.init();
        MultiLayerNetwork modelToTweak = new MultiLayerNetwork(// Output: (14-6+0)/2+1 = 5 -> 5*5*10 = 250
        // (31-5+0)/2+1 = 14
        // Output: (130-10+0)/4+1 = 31 -> 31*31*30
        new NeuralNetConfiguration.Builder().seed(12345).updater(new RmsProp(0.1)).list().layer(0, // 3 channels: RGB
        nOut(30).stride(4, 4).activation(RELU).weightInit(WeightInit.RELU).updater(new AdaGrad(0.1)).build()).layer(1, // change kernel size
        kernelSize(5, 5).stride(2, 2).build()).layer(2, nOut(10).stride(2, 2).activation(RELU).weightInit(WeightInit.RELU).build()).layer(3, nOut(50).weightInit(WeightInit.RELU).gradientNormalization(ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(10).updater(new RmsProp(0.01)).build()).layer(4, nOut(25).weightInit(XAVIER).build()).layer(5, nOut(4).weightInit(XAVIER).gradientNormalization(ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(10).build()).inputPreProcessor(0, new RnnToCnnPreProcessor(V_HEIGHT, V_WIDTH, 3)).inputPreProcessor(3, new CnnToFeedForwardPreProcessor(5, 5, 10)).inputPreProcessor(4, new FeedForwardToRnnPreProcessor()).backpropType(TruncatedBPTT).tBPTTForwardLength((V_NFRAMES / 5)).tBPTTBackwardLength((V_NFRAMES / 5)).build());
        modelToTweak.init();
        MultiLayerNetwork modelNow = new TransferLearning.Builder(modelToTweak).fineTuneConfiguration(// l2 regularization on all layers
        new FineTuneConfiguration.Builder().seed(12345).l2(0.001).updater(new AdaGrad(0.4)).weightInit(WeightInit.RELU).build()).removeLayersFromOutput(5).addLayer(kernelSize(3, 3).stride(2, 2).build()).addLayer(nOut(10).stride(2, 2).activation(RELU).weightInit(WeightInit.RELU).build()).addLayer(nOut(50).weightInit(WeightInit.RELU).updater(new AdaGrad(0.5)).gradientNormalization(ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(10).build()).addLayer(nOut(50).weightInit(XAVIER).updater(new AdaGrad(0.6)).gradientNormalization(ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(10).build()).addLayer(// 4 possible shapes: circle, square, arc, line
        nOut(4).weightInit(XAVIER).gradientNormalization(ClipElementWiseAbsoluteValue).gradientNormalizationThreshold(10).build()).setInputPreProcessor(3, new CnnToFeedForwardPreProcessor(7, 7, 10)).setInputPreProcessor(4, new FeedForwardToRnnPreProcessor()).build();
        // modelNow should have the same architecture as modelExpectedArch
        Assert.assertEquals(modelExpectedArch.getLayerWiseConfigurations().getConf(0).toJson(), modelNow.getLayerWiseConfigurations().getConf(0).toJson());
        // some learning related info the subsampling layer will not be overwritten
        // assertTrue(modelExpectedArch.getLayerWiseConfigurations().getConf(1).toJson().equals(modelNow.getLayerWiseConfigurations().getConf(1).toJson()));
        Assert.assertEquals(modelExpectedArch.getLayerWiseConfigurations().getConf(2).toJson(), modelNow.getLayerWiseConfigurations().getConf(2).toJson());
        Assert.assertEquals(modelExpectedArch.getLayerWiseConfigurations().getConf(3).toJson(), modelNow.getLayerWiseConfigurations().getConf(3).toJson());
        Assert.assertEquals(modelExpectedArch.getLayerWiseConfigurations().getConf(4).toJson(), modelNow.getLayerWiseConfigurations().getConf(4).toJson());
        Assert.assertEquals(modelExpectedArch.getLayerWiseConfigurations().getConf(5).toJson(), modelNow.getLayerWiseConfigurations().getConf(5).toJson());
        Assert.assertArrayEquals(modelExpectedArch.params().shape(), modelNow.params().shape());
        Assert.assertArrayEquals(modelExpectedArch.getLayer(0).params().shape(), modelNow.getLayer(0).params().shape());
        // subsampling has no params
        // assertArrayEquals(modelExpectedArch.getLayer(1).params().shape(), modelNow.getLayer(1).params().shape());
        Assert.assertArrayEquals(modelExpectedArch.getLayer(2).params().shape(), modelNow.getLayer(2).params().shape());
        Assert.assertArrayEquals(modelExpectedArch.getLayer(3).params().shape(), modelNow.getLayer(3).params().shape());
        Assert.assertArrayEquals(modelExpectedArch.getLayer(4).params().shape(), modelNow.getLayer(4).params().shape());
        Assert.assertArrayEquals(modelExpectedArch.getLayer(5).params().shape(), modelNow.getLayer(5).params().shape());
    }

    @Test
    public void testAllWithCNN() {
        DataSet randomData = new DataSet(Nd4j.rand(10, ((28 * 28) * 3)).reshape(10, 3, 28, 28), Nd4j.rand(10, 10));
        MultiLayerNetwork modelToFineTune = new MultiLayerNetwork(new NeuralNetConfiguration.Builder().seed(123).weightInit(XAVIER).updater(new Nesterovs(0.01, 0.9)).list().layer(0, nOut(20).activation(IDENTITY).build()).layer(1, kernelSize(2, 2).stride(2, 2).build()).layer(2, nOut(50).activation(IDENTITY).build()).layer(3, kernelSize(2, 2).stride(2, 2).build()).layer(4, nOut(500).build()).layer(5, nOut(250).build()).layer(6, nOut(100).activation(SOFTMAX).build()).setInputType(InputType.convolutionalFlat(28, 28, 3)).build());
        modelToFineTune.init();
        INDArray asFrozenFeatures = modelToFineTune.feedForwardToLayer(2, randomData.getFeatures(), false).get(2);// 10x20x12x12

        NeuralNetConfiguration.Builder equivalentConf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.2)).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT);
        FineTuneConfiguration overallConf = new FineTuneConfiguration.Builder().updater(new Sgd(0.2)).build();
        MultiLayerNetwork modelNow = new TransferLearning.Builder(modelToFineTune).fineTuneConfiguration(overallConf).setFeatureExtractor(1).nOutReplace(4, 600, XAVIER).removeLayersFromOutput(2).addLayer(nOut(300).build()).addLayer(nOut(150).build()).addLayer(nOut(50).build()).addLayer(nOut(10).build()).build();
        MultiLayerNetwork notFrozen = new MultiLayerNetwork(equivalentConf.list().layer(0, nOut(50).activation(IDENTITY).build()).layer(1, kernelSize(2, 2).stride(2, 2).build()).layer(2, nOut(600).build()).layer(3, nOut(300).build()).layer(4, nOut(150).build()).layer(5, nOut(50).build()).layer(6, nOut(10).activation(SOFTMAX).build()).setInputType(InputType.convolutionalFlat(12, 12, 20)).build());
        notFrozen.init();
        Assert.assertArrayEquals(modelToFineTune.getLayer(0).params().shape(), modelNow.getLayer(0).params().shape());
        // subsampling has no params
        // assertArrayEquals(modelExpectedArch.getLayer(1).params().shape(), modelNow.getLayer(1).params().shape());
        Assert.assertArrayEquals(notFrozen.getLayer(0).params().shape(), modelNow.getLayer(2).params().shape());
        modelNow.getLayer(2).setParams(notFrozen.getLayer(0).params());
        // subsampling has no params
        // assertArrayEquals(notFrozen.getLayer(1).params().shape(), modelNow.getLayer(3).params().shape());
        Assert.assertArrayEquals(notFrozen.getLayer(2).params().shape(), modelNow.getLayer(4).params().shape());
        modelNow.getLayer(4).setParams(notFrozen.getLayer(2).params());
        Assert.assertArrayEquals(notFrozen.getLayer(3).params().shape(), modelNow.getLayer(5).params().shape());
        modelNow.getLayer(5).setParams(notFrozen.getLayer(3).params());
        Assert.assertArrayEquals(notFrozen.getLayer(4).params().shape(), modelNow.getLayer(6).params().shape());
        modelNow.getLayer(6).setParams(notFrozen.getLayer(4).params());
        Assert.assertArrayEquals(notFrozen.getLayer(5).params().shape(), modelNow.getLayer(7).params().shape());
        modelNow.getLayer(7).setParams(notFrozen.getLayer(5).params());
        Assert.assertArrayEquals(notFrozen.getLayer(6).params().shape(), modelNow.getLayer(8).params().shape());
        modelNow.getLayer(8).setParams(notFrozen.getLayer(6).params());
        int i = 0;
        while (i < 3) {
            notFrozen.fit(new DataSet(asFrozenFeatures, randomData.getLabels()));
            modelNow.fit(randomData);
            i++;
        } 
        INDArray expectedParams = Nd4j.hstack(modelToFineTune.getLayer(0).params(), notFrozen.params());
        Assert.assertEquals(expectedParams, modelNow.params());
    }

    @Test
    public void testFineTuneOverride() {
        // Check that fine-tune overrides are selective - i.e., if I only specify a new LR, only the LR should be modified
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Adam(1.0E-4)).activation(TANH).weightInit(WeightInit.RELU).l1(0.1).l2(0.2).list().layer(0, nOut(5).build()).layer(1, nOut(4).activation(HARDSIGMOID).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        MultiLayerNetwork net2 = new TransferLearning.Builder(net).fineTuneConfiguration(// Should be set on MLC
        new FineTuneConfiguration.Builder().updater(new Adam(0.02)).backpropType(TruncatedBPTT).build()).build();
        // Check original net isn't modified:
        BaseLayer l0 = ((BaseLayer) (net.getLayer(0).conf().getLayer()));
        Assert.assertEquals(new Adam(1.0E-4), l0.getIUpdater());
        Assert.assertEquals(TANH.getActivationFunction(), l0.getActivationFn());
        Assert.assertEquals(new WeightInitRelu(), l0.getWeightInitFn());
        Assert.assertEquals(0.1, TestUtils.getL1(l0), 1.0E-6);
        BaseLayer l1 = ((BaseLayer) (net.getLayer(1).conf().getLayer()));
        Assert.assertEquals(new Adam(1.0E-4), l1.getIUpdater());
        Assert.assertEquals(HARDSIGMOID.getActivationFunction(), l1.getActivationFn());
        Assert.assertEquals(new WeightInitRelu(), l1.getWeightInitFn());
        Assert.assertEquals(0.2, TestUtils.getL2(l1), 1.0E-6);
        Assert.assertEquals(Standard, conf.getBackpropType());
        // Check new net has only the appropriate things modified (i.e., LR)
        l0 = ((BaseLayer) (net2.getLayer(0).conf().getLayer()));
        Assert.assertEquals(new Adam(0.02), l0.getIUpdater());
        Assert.assertEquals(TANH.getActivationFunction(), l0.getActivationFn());
        Assert.assertEquals(new WeightInitRelu(), l0.getWeightInitFn());
        Assert.assertEquals(0.1, TestUtils.getL1(l0), 1.0E-6);
        l1 = ((BaseLayer) (net2.getLayer(1).conf().getLayer()));
        Assert.assertEquals(new Adam(0.02), l1.getIUpdater());
        Assert.assertEquals(HARDSIGMOID.getActivationFunction(), l1.getActivationFn());
        Assert.assertEquals(new WeightInitRelu(), l1.getWeightInitFn());
        Assert.assertEquals(0.2, TestUtils.getL2(l1), 1.0E-6);
        Assert.assertEquals(TruncatedBPTT, net2.getLayerWiseConfigurations().getBackpropType());
    }

    @Test
    public void testAllWithCNNNew() {
        DataSet randomData = new DataSet(Nd4j.rand(10, ((28 * 28) * 3)).reshape(10, 3, 28, 28), Nd4j.rand(10, 10));
        MultiLayerNetwork modelToFineTune = new MultiLayerNetwork(// See note below
        new NeuralNetConfiguration.Builder().seed(123).weightInit(XAVIER).updater(new Nesterovs(0.01, 0.9)).list().layer(0, nOut(20).activation(IDENTITY).build()).layer(1, kernelSize(2, 2).stride(2, 2).build()).layer(2, nOut(50).activation(IDENTITY).build()).layer(3, kernelSize(2, 2).stride(2, 2).build()).layer(4, nOut(500).build()).layer(5, nOut(250).build()).layer(6, nOut(100).activation(SOFTMAX).build()).setInputType(InputType.convolutionalFlat(28, 28, 3)).build());
        modelToFineTune.init();
        INDArray asFrozenFeatures = modelToFineTune.feedForwardToLayer(2, randomData.getFeatures(), false).get(2);// 10x20x12x12

        NeuralNetConfiguration.Builder equivalentConf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.2));
        FineTuneConfiguration overallConf = new FineTuneConfiguration.Builder().updater(new Sgd(0.2)).build();
        MultiLayerNetwork modelNow = new TransferLearning.Builder(modelToFineTune).fineTuneConfiguration(overallConf).setFeatureExtractor(1).removeLayersFromOutput(5).addLayer(nOut(300).build()).addLayer(nOut(150).build()).addLayer(nOut(50).build()).addLayer(nOut(10).build()).setInputPreProcessor(2, new CnnToFeedForwardPreProcessor(12, 12, 20)).build();
        MultiLayerNetwork notFrozen = new MultiLayerNetwork(equivalentConf.list().layer(0, nOut(300).build()).layer(1, nOut(150).build()).layer(2, nOut(50).build()).layer(3, nOut(10).activation(SOFTMAX).build()).inputPreProcessor(0, new CnnToFeedForwardPreProcessor(12, 12, 20)).build());
        notFrozen.init();
        Assert.assertArrayEquals(modelToFineTune.getLayer(0).params().shape(), modelNow.getLayer(0).params().shape());
        // subsampling has no params
        // assertArrayEquals(modelExpectedArch.getLayer(1).params().shape(), modelNow.getLayer(1).params().shape());
        Assert.assertArrayEquals(notFrozen.getLayer(0).params().shape(), modelNow.getLayer(2).params().shape());
        modelNow.getLayer(2).setParams(notFrozen.getLayer(0).params());
        Assert.assertArrayEquals(notFrozen.getLayer(1).params().shape(), modelNow.getLayer(3).params().shape());
        modelNow.getLayer(3).setParams(notFrozen.getLayer(1).params());
        Assert.assertArrayEquals(notFrozen.getLayer(2).params().shape(), modelNow.getLayer(4).params().shape());
        modelNow.getLayer(4).setParams(notFrozen.getLayer(2).params());
        Assert.assertArrayEquals(notFrozen.getLayer(3).params().shape(), modelNow.getLayer(5).params().shape());
        modelNow.getLayer(5).setParams(notFrozen.getLayer(3).params());
        int i = 0;
        while (i < 3) {
            notFrozen.fit(new DataSet(asFrozenFeatures, randomData.getLabels()));
            modelNow.fit(randomData);
            i++;
        } 
        INDArray expectedParams = Nd4j.hstack(modelToFineTune.getLayer(0).params(), notFrozen.params());
        Assert.assertEquals(expectedParams, modelNow.params());
    }

    @Test
    public void testObjectOverrides() {
        // https://github.com/deeplearning4j/deeplearning4j/issues/4368
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().dropOut(0.5).weightNoise(new DropConnect(0.5)).l2(0.5).constrainWeights(new UnitNormConstraint()).list().layer(nOut(10).build()).build();
        MultiLayerNetwork orig = new MultiLayerNetwork(conf);
        orig.init();
        FineTuneConfiguration ftc = new FineTuneConfiguration.Builder().dropOut(0).weightNoise(null).constraints(null).l2(0.0).build();
        MultiLayerNetwork transfer = new TransferLearning.Builder(orig).fineTuneConfiguration(ftc).build();
        DenseLayer l = ((DenseLayer) (transfer.getLayer(0).conf().getLayer()));
        Assert.assertNull(l.getIDropout());
        Assert.assertNull(l.getWeightNoise());
        Assert.assertNull(l.getConstraints());
        Assert.assertNull(TestUtils.getL2Reg(l));
    }

    @Test
    public void testTransferLearningSubsequent() {
        final INDArray input = Nd4j.create(6, 6, 6, 6);
        final MultiLayerNetwork net = new MultiLayerNetwork(new NeuralNetConfiguration.Builder().weightInit(new ConstantDistribution(666)).list().setInputType(InputType.inferInputTypes(input)[0]).layer(new Convolution2D.Builder(3, 3).nOut(10).build()).layer(new Convolution2D.Builder(1, 1).nOut(3).build()).layer(new OutputLayer.Builder().nOut(2).lossFunction(MSE).build()).build());
        net.init();
        MultiLayerNetwork newGraph = new TransferLearning.Builder(net).fineTuneConfiguration(new FineTuneConfiguration.Builder().build()).nOutReplace(0, 7, new ConstantDistribution(333)).nOutReplace(1, 3, new ConstantDistribution(111)).removeLayersFromOutput(1).addLayer(nOut(2).lossFunction(MSE).build()).setInputPreProcessor(2, new CnnToFeedForwardPreProcessor(4, 4, 3)).build();
        newGraph.init();
        Assert.assertEquals("Incorrect # inputs", 7, newGraph.layerInputSize(1));
        newGraph.output(input);
    }

    @Test
    public void testChangeNOutNIn() {
        INDArray input = Nd4j.create(new long[]{ 1, 2, 4, 4 });
        MultiLayerNetwork net = new MultiLayerNetwork(new NeuralNetConfiguration.Builder().list().setInputType(InputType.inferInputTypes(input)[0]).layer(new Convolution2D.Builder(1, 1).nOut(10).build()).layer(new SubsamplingLayer.Builder(1, 1).build()).layer(new Convolution2D.Builder(1, 1).nOut(7).build()).layer(nOut(2).build()).build());
        net.init();
        final MultiLayerNetwork newNet = new TransferLearning.Builder(net).nOutReplace(0, 5, XAVIER).nInReplace(2, 5, XAVIER).build();
        newNet.init();
        Assert.assertEquals("Incorrect number of outputs!", 5, newNet.layerSize(0));
        Assert.assertEquals("Incorrect number of inputs!", 5, newNet.layerInputSize(2));
        newNet.output(input);
    }
}

