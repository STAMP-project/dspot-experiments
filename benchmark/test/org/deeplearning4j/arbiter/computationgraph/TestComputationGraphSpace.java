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
package org.deeplearning4j.arbiter.computationgraph;


import Activation.RELU;
import Activation.SOFTMAX;
import Activation.TANH;
import LossFunction.MCXENT;
import java.util.List;
import java.util.Random;
import org.deeplearning4j.arbiter.ComputationGraphSpace;
import org.deeplearning4j.arbiter.TestUtils;
import org.deeplearning4j.arbiter.layers.DenseLayerSpace;
import org.deeplearning4j.arbiter.layers.OutputLayerSpace;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.continuous.ContinuousParameterSpace;
import org.deeplearning4j.arbiter.util.LeafUtils;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.BaseLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.learning.config.Sgd;


public class TestComputationGraphSpace {
    @Test
    public void testBasic() {
        ComputationGraphConfiguration expected = new NeuralNetConfiguration.Builder().updater(new Sgd(0.005)).seed(12345).graphBuilder().addInputs("in").addLayer("0", new DenseLayer.Builder().nIn(10).nOut(10).build(), "in").addLayer("1", new DenseLayer.Builder().nIn(10).nOut(10).build(), "0").addLayer("2", new OutputLayer.Builder().lossFunction(MCXENT).activation(SOFTMAX).nIn(10).nOut(5).build(), "1").setOutputs("2").build();
        ComputationGraphSpace cgs = new ComputationGraphSpace.Builder().updater(new Sgd(0.005)).seed(12345).addInputs("in").addLayer("0", new DenseLayerSpace.Builder().nIn(10).nOut(10).build(), "in").addLayer("1", new DenseLayerSpace.Builder().nIn(10).nOut(10).build(), "0").addLayer("2", new OutputLayerSpace.Builder().lossFunction(MCXENT).activation(SOFTMAX).nIn(10).nOut(5).build(), "1").setOutputs("2").setInputTypes(InputType.feedForward(10)).build();
        int nParams = cgs.numParameters();
        Assert.assertEquals(0, nParams);
        ComputationGraphConfiguration conf = cgs.getValue(new double[0]).getConfiguration();
        Assert.assertEquals(expected, conf);
    }

    @Test
    public void testBasic2() {
        ComputationGraphSpace mls = new ComputationGraphSpace.Builder().updater(new org.deeplearning4j.arbiter.conf.updater.SgdSpace(new ContinuousParameterSpace(1.0E-4, 0.1))).l2(new ContinuousParameterSpace(0.2, 0.5)).addInputs("in").addLayer("0", new DenseLayerSpace.Builder().nIn(10).nOut(10).activation(new org.deeplearning4j.arbiter.optimize.parameter.discrete.DiscreteParameterSpace(Activation.RELU, Activation.TANH)).build(), "in").addLayer("1", new OutputLayerSpace.Builder().nIn(10).nOut(10).activation(SOFTMAX).build(), "0").setOutputs("1").setInputTypes(InputType.feedForward(10)).build();
        int nParams = mls.numParameters();
        Assert.assertEquals(3, nParams);
        // Assign numbers to each leaf ParameterSpace object (normally done by candidate generator)
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
        int reluCount = 0;
        int tanhCount = 0;
        Random r = new Random(12345);
        for (int i = 0; i < 50; i++) {
            double[] rvs = new double[nParams];
            for (int j = 0; j < (rvs.length); j++)
                rvs[j] = r.nextDouble();

            ComputationGraphConfiguration conf = mls.getValue(rvs).getConfiguration();
            int nLayers = conf.getVertexInputs().size();
            Assert.assertEquals(2, nLayers);
            for (int j = 0; j < nLayers; j++) {
                NeuralNetConfiguration layerConf = getLayerConf();
                double lr = getLearningRate();
                Assert.assertTrue(((lr >= 1.0E-4) && (lr <= 0.1)));
                double l2 = TestUtils.getL2(((BaseLayer) (layerConf.getLayer())));
                Assert.assertTrue(((l2 >= 0.2) && (l2 <= 0.5)));
                if (j == (nLayers - 1)) {
                    // Output layer
                    Assert.assertEquals(SOFTMAX.getActivationFunction(), getActivationFn());
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
        System.out.println(((("ReLU vs. Tanh: " + reluCount) + "\t") + tanhCount));
        Assert.assertTrue((reluCount > 0));
        Assert.assertTrue((tanhCount > 0));
    }
}

