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
package org.deeplearning4j.regressiontest;


import ConvolutionMode.Same;
import GradientNormalization.ClipElementWiseAbsoluteValue;
import PoolingType.MAX;
import java.io.File;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.dropout.Dropout;
import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInitRelu;
import org.deeplearning4j.nn.weights.WeightInitXavier;
import org.deeplearning4j.util.ModelSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.learning.regularization.WeightDecay;
import org.nd4j.linalg.lossfunctions.impl.LossMCXENT;
import org.nd4j.linalg.lossfunctions.impl.LossMSE;
import org.nd4j.linalg.lossfunctions.impl.LossNegativeLogLikelihood;


/**
 * Regression tests for DL4J 0.5.0 - i.e., can we still load basic models generated in 0.5.0?
 * See dl4j-test-resources/src/main/resources/regression_testing/050/050_regression_test_readme.md
 *
 * @author Alex Black
 */
public class RegressionTest080 extends BaseDL4JTest {
    @Test
    public void regressionTestMLP1() throws Exception {
        File f = new ClassPathResource("regression_testing/080/080_ModelSerializer_Regression_MLP_1.zip").getTempFileFromArchive();
        MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork(f, true);
        MultiLayerConfiguration conf = net.getLayerWiseConfigurations();
        Assert.assertEquals(2, conf.getConfs().size());
        DenseLayer l0 = ((DenseLayer) (conf.getConf(0).getLayer()));
        Assert.assertTrue(((l0.getActivationFn()) instanceof ActivationReLU));
        Assert.assertEquals(3, l0.getNIn());
        Assert.assertEquals(4, l0.getNOut());
        Assert.assertEquals(new WeightInitXavier(), l0.getWeightInitFn());
        Assert.assertTrue(((l0.getIUpdater()) instanceof Nesterovs));
        Nesterovs n = ((Nesterovs) (l0.getIUpdater()));
        Assert.assertEquals(0.9, n.getMomentum(), 1.0E-6);
        Assert.assertEquals(0.15, getLearningRate(), 1.0E-6);
        Assert.assertEquals(0.15, n.getLearningRate(), 1.0E-6);
        OutputLayer l1 = ((OutputLayer) (conf.getConf(1).getLayer()));
        Assert.assertTrue(((l1.getActivationFn()) instanceof ActivationSoftmax));
        Assert.assertTrue(((l1.getLossFn()) instanceof LossMCXENT));
        Assert.assertEquals(4, l1.getNIn());
        Assert.assertEquals(5, l1.getNOut());
        Assert.assertEquals(new WeightInitXavier(), l1.getWeightInitFn());
        Assert.assertTrue(((l1.getIUpdater()) instanceof Nesterovs));
        Assert.assertEquals(0.9, getMomentum(), 1.0E-6);
        Assert.assertEquals(0.15, getLearningRate(), 1.0E-6);
        Assert.assertEquals(0.15, n.getLearningRate(), 1.0E-6);
        int numParams = ((int) (net.numParams()));
        Assert.assertEquals(Nd4j.linspace(1, numParams, numParams, Nd4j.dataType()), net.params());
        int updaterSize = ((int) (new Nesterovs().stateSize(numParams)));
        Assert.assertEquals(Nd4j.linspace(1, updaterSize, updaterSize, Nd4j.dataType()), net.getUpdater().getStateViewArray());
    }

    @Test
    public void regressionTestMLP2() throws Exception {
        File f = new ClassPathResource("regression_testing/080/080_ModelSerializer_Regression_MLP_2.zip").getTempFileFromArchive();
        MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork(f, true);
        MultiLayerConfiguration conf = net.getLayerWiseConfigurations();
        Assert.assertEquals(2, conf.getConfs().size());
        DenseLayer l0 = ((DenseLayer) (conf.getConf(0).getLayer()));
        Assert.assertTrue(((l0.getActivationFn()) instanceof ActivationLReLU));
        Assert.assertEquals(3, l0.getNIn());
        Assert.assertEquals(4, l0.getNOut());
        Assert.assertEquals(new org.deeplearning4j.nn.weights.WeightInitDistribution(new NormalDistribution(0.1, 1.2)), l0.getWeightInitFn());
        Assert.assertTrue(((l0.getIUpdater()) instanceof RmsProp));
        RmsProp r = ((RmsProp) (l0.getIUpdater()));
        Assert.assertEquals(0.96, r.getRmsDecay(), 1.0E-6);
        Assert.assertEquals(0.15, r.getLearningRate(), 1.0E-6);
        Assert.assertEquals(0.15, getLearningRate(), 1.0E-6);
        Assert.assertEquals(new Dropout(0.6), l0.getIDropout());
        Assert.assertEquals(0.1, TestUtils.getL1(l0), 1.0E-6);
        Assert.assertEquals(new WeightDecay(0.2, false), TestUtils.getWeightDecayReg(l0));
        Assert.assertEquals(ClipElementWiseAbsoluteValue, l0.getGradientNormalization());
        Assert.assertEquals(1.5, l0.getGradientNormalizationThreshold(), 1.0E-5);
        OutputLayer l1 = ((OutputLayer) (conf.getConf(1).getLayer()));
        Assert.assertTrue(((l1.getActivationFn()) instanceof ActivationIdentity));
        Assert.assertTrue(((l1.getLossFn()) instanceof LossMSE));
        Assert.assertEquals(4, l1.getNIn());
        Assert.assertEquals(5, l1.getNOut());
        Assert.assertEquals(new org.deeplearning4j.nn.weights.WeightInitDistribution(new NormalDistribution(0.1, 1.2)), l1.getWeightInitFn());
        Assert.assertTrue(((l1.getIUpdater()) instanceof RmsProp));
        r = ((RmsProp) (l1.getIUpdater()));
        Assert.assertEquals(0.96, r.getRmsDecay(), 1.0E-6);
        Assert.assertEquals(0.15, r.getLearningRate(), 1.0E-6);
        Assert.assertEquals(0.15, getLearningRate(), 1.0E-6);
        Assert.assertEquals(new Dropout(0.6), l1.getIDropout());
        Assert.assertEquals(0.1, TestUtils.getL1(l1), 1.0E-6);
        Assert.assertEquals(new WeightDecay(0.2, false), TestUtils.getWeightDecayReg(l1));
        Assert.assertEquals(ClipElementWiseAbsoluteValue, l1.getGradientNormalization());
        Assert.assertEquals(1.5, l1.getGradientNormalizationThreshold(), 1.0E-5);
        int numParams = ((int) (net.numParams()));
        Assert.assertEquals(Nd4j.linspace(1, numParams, numParams, Nd4j.dataType()), net.params());
        int updaterSize = ((int) (new RmsProp().stateSize(numParams)));
        Assert.assertEquals(Nd4j.linspace(1, updaterSize, updaterSize, Nd4j.dataType()), net.getUpdater().getStateViewArray());
    }

    @Test
    public void regressionTestCNN1() throws Exception {
        File f = new ClassPathResource("regression_testing/080/080_ModelSerializer_Regression_CNN_1.zip").getTempFileFromArchive();
        MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork(f, true);
        MultiLayerConfiguration conf = net.getLayerWiseConfigurations();
        Assert.assertEquals(3, conf.getConfs().size());
        ConvolutionLayer l0 = ((ConvolutionLayer) (conf.getConf(0).getLayer()));
        Assert.assertTrue(((l0.getActivationFn()) instanceof ActivationTanH));
        Assert.assertEquals(3, l0.getNIn());
        Assert.assertEquals(3, l0.getNOut());
        Assert.assertEquals(new WeightInitRelu(), l0.getWeightInitFn());
        Assert.assertTrue(((l0.getIUpdater()) instanceof RmsProp));
        RmsProp r = ((RmsProp) (l0.getIUpdater()));
        Assert.assertEquals(0.96, r.getRmsDecay(), 1.0E-6);
        Assert.assertEquals(0.15, r.getLearningRate(), 1.0E-6);
        Assert.assertEquals(0.15, getLearningRate(), 1.0E-6);
        Assert.assertArrayEquals(new int[]{ 2, 2 }, l0.getKernelSize());
        Assert.assertArrayEquals(new int[]{ 1, 1 }, l0.getStride());
        Assert.assertArrayEquals(new int[]{ 0, 0 }, l0.getPadding());
        Assert.assertEquals(l0.getConvolutionMode(), Same);
        SubsamplingLayer l1 = ((SubsamplingLayer) (conf.getConf(1).getLayer()));
        Assert.assertArrayEquals(new int[]{ 2, 2 }, l1.getKernelSize());
        Assert.assertArrayEquals(new int[]{ 1, 1 }, l1.getStride());
        Assert.assertArrayEquals(new int[]{ 0, 0 }, l1.getPadding());
        Assert.assertEquals(MAX, l1.getPoolingType());
        Assert.assertEquals(l1.getConvolutionMode(), Same);
        OutputLayer l2 = ((OutputLayer) (conf.getConf(2).getLayer()));
        Assert.assertTrue(((l2.getActivationFn()) instanceof ActivationSigmoid));
        Assert.assertTrue(((l2.getLossFn()) instanceof LossNegativeLogLikelihood));
        Assert.assertEquals(((26 * 26) * 3), l2.getNIn());
        Assert.assertEquals(5, l2.getNOut());
        Assert.assertEquals(new WeightInitRelu(), l2.getWeightInitFn());
        Assert.assertTrue(((l2.getIUpdater()) instanceof RmsProp));
        r = ((RmsProp) (l2.getIUpdater()));
        Assert.assertEquals(0.96, r.getRmsDecay(), 1.0E-6);
        Assert.assertEquals(0.15, r.getLearningRate(), 1.0E-6);
        Assert.assertTrue(((conf.getInputPreProcess(2)) instanceof CnnToFeedForwardPreProcessor));
        int numParams = ((int) (net.numParams()));
        Assert.assertEquals(Nd4j.linspace(1, numParams, numParams, Nd4j.dataType()), net.params());
        int updaterSize = ((int) (new RmsProp().stateSize(numParams)));
        Assert.assertEquals(Nd4j.linspace(1, updaterSize, updaterSize, Nd4j.dataType()), net.getUpdater().getStateViewArray());
    }

    @Test
    public void regressionTestLSTM1() throws Exception {
        File f = new ClassPathResource("regression_testing/080/080_ModelSerializer_Regression_LSTM_1.zip").getTempFileFromArchive();
        MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork(f, true);
        MultiLayerConfiguration conf = net.getLayerWiseConfigurations();
        Assert.assertEquals(3, conf.getConfs().size());
        GravesLSTM l0 = ((GravesLSTM) (conf.getConf(0).getLayer()));
        Assert.assertTrue(((l0.getActivationFn()) instanceof ActivationTanH));
        Assert.assertEquals(3, l0.getNIn());
        Assert.assertEquals(4, l0.getNOut());
        Assert.assertEquals(ClipElementWiseAbsoluteValue, l0.getGradientNormalization());
        Assert.assertEquals(1.5, l0.getGradientNormalizationThreshold(), 1.0E-5);
        GravesBidirectionalLSTM l1 = ((GravesBidirectionalLSTM) (conf.getConf(1).getLayer()));
        Assert.assertTrue(((l1.getActivationFn()) instanceof ActivationSoftSign));
        Assert.assertEquals(4, l1.getNIn());
        Assert.assertEquals(4, l1.getNOut());
        Assert.assertEquals(ClipElementWiseAbsoluteValue, l1.getGradientNormalization());
        Assert.assertEquals(1.5, l1.getGradientNormalizationThreshold(), 1.0E-5);
        RnnOutputLayer l2 = ((RnnOutputLayer) (conf.getConf(2).getLayer()));
        Assert.assertEquals(4, l2.getNIn());
        Assert.assertEquals(5, l2.getNOut());
        Assert.assertTrue(((l2.getActivationFn()) instanceof ActivationSoftmax));
        Assert.assertTrue(((l2.getLossFn()) instanceof LossMCXENT));
    }

    @Test
    public void regressionTestCGLSTM1() throws Exception {
        File f = new ClassPathResource("regression_testing/080/080_ModelSerializer_Regression_CG_LSTM_1.zip").getTempFileFromArchive();
        ComputationGraph net = ModelSerializer.restoreComputationGraph(f, true);
        ComputationGraphConfiguration conf = net.getConfiguration();
        Assert.assertEquals(3, conf.getVertices().size());
        GravesLSTM l0 = ((GravesLSTM) (getLayerConf().getLayer()));
        Assert.assertTrue(((l0.getActivationFn()) instanceof ActivationTanH));
        Assert.assertEquals(3, l0.getNIn());
        Assert.assertEquals(4, l0.getNOut());
        Assert.assertEquals(ClipElementWiseAbsoluteValue, l0.getGradientNormalization());
        Assert.assertEquals(1.5, l0.getGradientNormalizationThreshold(), 1.0E-5);
        GravesBidirectionalLSTM l1 = ((GravesBidirectionalLSTM) (getLayerConf().getLayer()));
        Assert.assertTrue(((l1.getActivationFn()) instanceof ActivationSoftSign));
        Assert.assertEquals(4, l1.getNIn());
        Assert.assertEquals(4, l1.getNOut());
        Assert.assertEquals(ClipElementWiseAbsoluteValue, l1.getGradientNormalization());
        Assert.assertEquals(1.5, l1.getGradientNormalizationThreshold(), 1.0E-5);
        RnnOutputLayer l2 = ((RnnOutputLayer) (getLayerConf().getLayer()));
        Assert.assertEquals(4, l2.getNIn());
        Assert.assertEquals(5, l2.getNOut());
        Assert.assertTrue(((l2.getActivationFn()) instanceof ActivationSoftmax));
        Assert.assertTrue(((l2.getLossFn()) instanceof LossMCXENT));
    }
}

