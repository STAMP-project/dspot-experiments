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


import ConvolutionMode.Truncate;
import PoolingType.MAX;
import java.io.File;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.dropout.Dropout;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInitRelu;
import org.deeplearning4j.nn.weights.WeightInitXavier;
import org.deeplearning4j.util.ModelSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.impl.ActivationLReLU;
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
public class RegressionTest050 extends BaseDL4JTest {
    @Test
    public void regressionTestMLP1() throws Exception {
        File f = new ClassPathResource("regression_testing/050/050_ModelSerializer_Regression_MLP_1.zip").getTempFileFromArchive();
        MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork(f, true);
        MultiLayerConfiguration conf = net.getLayerWiseConfigurations();
        Assert.assertEquals(2, conf.getConfs().size());
        DenseLayer l0 = ((DenseLayer) (conf.getConf(0).getLayer()));
        Assert.assertEquals("relu", l0.getActivationFn().toString());
        Assert.assertEquals(3, l0.getNIn());
        Assert.assertEquals(4, l0.getNOut());
        Assert.assertEquals(new WeightInitXavier(), l0.getWeightInitFn());
        Assert.assertEquals(new Nesterovs(0.15, 0.9), l0.getIUpdater());
        Assert.assertEquals(0.15, getLearningRate(), 1.0E-6);
        OutputLayer l1 = ((OutputLayer) (conf.getConf(1).getLayer()));
        Assert.assertEquals("softmax", l1.getActivationFn().toString());
        Assert.assertTrue(((l1.getLossFn()) instanceof LossMCXENT));
        Assert.assertEquals(4, l1.getNIn());
        Assert.assertEquals(5, l1.getNOut());
        Assert.assertEquals(new WeightInitXavier(), l1.getWeightInitFn());
        Assert.assertEquals(new Nesterovs(0.15, 0.9), l1.getIUpdater());
        Assert.assertEquals(0.9, getMomentum(), 1.0E-6);
        Assert.assertEquals(0.15, getLearningRate(), 1.0E-6);
        int numParams = ((int) (net.numParams()));
        Assert.assertEquals(Nd4j.linspace(1, numParams, numParams, Nd4j.dataType()), net.params());
        int updaterSize = ((int) (new Nesterovs().stateSize(net.numParams())));
        Assert.assertEquals(Nd4j.linspace(1, updaterSize, updaterSize, Nd4j.dataType()), net.getUpdater().getStateViewArray());
    }

    @Test
    public void regressionTestMLP2() throws Exception {
        File f = new ClassPathResource("regression_testing/050/050_ModelSerializer_Regression_MLP_2.zip").getTempFileFromArchive();
        MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork(f, true);
        MultiLayerConfiguration conf = net.getLayerWiseConfigurations();
        Assert.assertEquals(2, conf.getConfs().size());
        DenseLayer l0 = ((DenseLayer) (conf.getConf(0).getLayer()));
        Assert.assertTrue(((l0.getActivationFn()) instanceof ActivationLReLU));
        Assert.assertEquals(3, l0.getNIn());
        Assert.assertEquals(4, l0.getNOut());
        Assert.assertEquals(new org.deeplearning4j.nn.weights.WeightInitDistribution(new NormalDistribution(0.1, 1.2)), l0.getWeightInitFn());
        Assert.assertEquals(new RmsProp(0.15, 0.96, RmsProp.DEFAULT_RMSPROP_EPSILON), l0.getIUpdater());
        Assert.assertEquals(0.15, getLearningRate(), 1.0E-6);
        Assert.assertEquals(new Dropout(0.6), l0.getIDropout());
        Assert.assertEquals(0.1, TestUtils.getL1(l0), 1.0E-6);
        Assert.assertEquals(new WeightDecay(0.2, false), TestUtils.getWeightDecayReg(l0));
        OutputLayer l1 = ((OutputLayer) (conf.getConf(1).getLayer()));
        Assert.assertEquals("identity", l1.getActivationFn().toString());
        Assert.assertTrue(((l1.getLossFn()) instanceof LossMSE));
        Assert.assertEquals(4, l1.getNIn());
        Assert.assertEquals(5, l1.getNOut());
        Assert.assertEquals(new org.deeplearning4j.nn.weights.WeightInitDistribution(new NormalDistribution(0.1, 1.2)), l0.getWeightInitFn());
        Assert.assertEquals(new RmsProp(0.15, 0.96, RmsProp.DEFAULT_RMSPROP_EPSILON), l1.getIUpdater());
        Assert.assertEquals(0.15, getLearningRate(), 1.0E-6);
        Assert.assertEquals(new Dropout(0.6), l1.getIDropout());
        Assert.assertEquals(0.1, TestUtils.getL1(l1), 1.0E-6);
        Assert.assertEquals(new WeightDecay(0.2, false), TestUtils.getWeightDecayReg(l1));
        int numParams = ((int) (net.numParams()));
        Assert.assertEquals(Nd4j.linspace(1, numParams, numParams, Nd4j.dataType()), net.params());
        int updaterSize = ((int) (new RmsProp().stateSize(numParams)));
        Assert.assertEquals(Nd4j.linspace(1, updaterSize, updaterSize, Nd4j.dataType()), net.getUpdater().getStateViewArray());
    }

    @Test
    public void regressionTestCNN1() throws Exception {
        File f = new ClassPathResource("regression_testing/050/050_ModelSerializer_Regression_CNN_1.zip").getTempFileFromArchive();
        MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork(f, true);
        MultiLayerConfiguration conf = net.getLayerWiseConfigurations();
        Assert.assertEquals(3, conf.getConfs().size());
        ConvolutionLayer l0 = ((ConvolutionLayer) (conf.getConf(0).getLayer()));
        Assert.assertEquals("tanh", l0.getActivationFn().toString());
        Assert.assertEquals(3, l0.getNIn());
        Assert.assertEquals(3, l0.getNOut());
        Assert.assertEquals(new WeightInitRelu(), l0.getWeightInitFn());
        Assert.assertEquals(new RmsProp(0.15, 0.96, RmsProp.DEFAULT_RMSPROP_EPSILON), l0.getIUpdater());
        Assert.assertEquals(0.15, getLearningRate(), 1.0E-6);
        Assert.assertArrayEquals(new int[]{ 2, 2 }, l0.getKernelSize());
        Assert.assertArrayEquals(new int[]{ 1, 1 }, l0.getStride());
        Assert.assertArrayEquals(new int[]{ 0, 0 }, l0.getPadding());
        Assert.assertEquals(l0.getConvolutionMode(), Truncate);// Pre-0.7.0: no ConvolutionMode. Want to default to truncate here if not set

        SubsamplingLayer l1 = ((SubsamplingLayer) (conf.getConf(1).getLayer()));
        Assert.assertArrayEquals(new int[]{ 2, 2 }, l1.getKernelSize());
        Assert.assertArrayEquals(new int[]{ 1, 1 }, l1.getStride());
        Assert.assertArrayEquals(new int[]{ 0, 0 }, l1.getPadding());
        Assert.assertEquals(MAX, l1.getPoolingType());
        Assert.assertEquals(l1.getConvolutionMode(), Truncate);// Pre-0.7.0: no ConvolutionMode. Want to default to truncate here if not set

        OutputLayer l2 = ((OutputLayer) (conf.getConf(2).getLayer()));
        Assert.assertEquals("sigmoid", l2.getActivationFn().toString());
        Assert.assertTrue(((l2.getLossFn()) instanceof LossNegativeLogLikelihood));
        Assert.assertEquals(((26 * 26) * 3), l2.getNIn());
        Assert.assertEquals(5, l2.getNOut());
        Assert.assertEquals(new WeightInitRelu(), l0.getWeightInitFn());
        Assert.assertEquals(new RmsProp(0.15, 0.96, RmsProp.DEFAULT_RMSPROP_EPSILON), l0.getIUpdater());
        Assert.assertEquals(0.15, getLearningRate(), 1.0E-6);
        int numParams = ((int) (net.numParams()));
        Assert.assertEquals(Nd4j.linspace(1, numParams, numParams, Nd4j.dataType()), net.params());
        int updaterSize = ((int) (new RmsProp().stateSize(numParams)));
        Assert.assertEquals(Nd4j.linspace(1, updaterSize, updaterSize, Nd4j.dataType()), net.getUpdater().getStateViewArray());
    }
}

