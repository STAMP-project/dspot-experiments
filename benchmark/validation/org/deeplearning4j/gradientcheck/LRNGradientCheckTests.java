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


import Activation.TANH;
import DataType.DOUBLE;
import MultiLayerConfiguration.Builder;
import java.util.Random;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.LocalResponseNormalization;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.NoOp;

import static LossFunctions.LossFunction.MCXENT;


/**
 * Created by Alex on 08/09/2016.
 */
public class LRNGradientCheckTests extends BaseDL4JTest {
    private static final boolean PRINT_RESULTS = true;

    private static final boolean RETURN_ON_FIRST_FAILURE = false;

    private static final double DEFAULT_EPS = 1.0E-5;

    private static final double DEFAULT_MAX_REL_ERROR = 1.0E-5;

    private static final double DEFAULT_MIN_ABS_ERROR = 1.0E-9;

    static {
        Nd4j.setDataType(DOUBLE);
    }

    @Test
    public void testGradientLRNSimple() {
        Nd4j.getRandom().setSeed(12345);
        int minibatch = 10;
        int depth = 6;
        int hw = 5;
        int nOut = 4;
        INDArray input = Nd4j.rand(new int[]{ minibatch, depth, hw, hw });
        INDArray labels = Nd4j.zeros(minibatch, nOut);
        Random r = new Random(12345);
        for (int i = 0; i < minibatch; i++) {
            labels.putScalar(i, r.nextInt(nOut), 1.0);
        }
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().updater(new NoOp()).seed(12345L).dist(new NormalDistribution(0, 2)).list().layer(0, new ConvolutionLayer.Builder().nOut(6).kernelSize(2, 2).stride(1, 1).activation(TANH).build()).layer(1, new LocalResponseNormalization.Builder().build()).layer(2, new org.deeplearning4j.nn.conf.layers.OutputLayer.Builder(MCXENT).activation(Activation.SOFTMAX).nOut(nOut).build()).setInputType(InputType.convolutional(hw, hw, depth));
        MultiLayerNetwork mln = new MultiLayerNetwork(builder.build());
        mln.init();
        if (LRNGradientCheckTests.PRINT_RESULTS) {
            for (int j = 0; j < (mln.getnLayers()); j++)
                System.out.println(((("Layer " + j) + " # params: ") + (mln.getLayer(j).numParams())));

        }
        boolean gradOK = GradientCheckUtil.checkGradients(mln, LRNGradientCheckTests.DEFAULT_EPS, LRNGradientCheckTests.DEFAULT_MAX_REL_ERROR, LRNGradientCheckTests.DEFAULT_MIN_ABS_ERROR, LRNGradientCheckTests.PRINT_RESULTS, LRNGradientCheckTests.RETURN_ON_FIRST_FAILURE, input, labels);
        Assert.assertTrue(gradOK);
        TestUtils.testModelSerialization(mln);
    }
}

