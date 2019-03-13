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


import Convolution.Type;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer.PoolingType;
import org.deeplearning4j.nn.weights.WeightInit;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.convolution.Convolution;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;


/**
 *
 *
 * @author Jeffrey Tang.
 */
public class MultiNeuralNetConfLayerBuilderTest extends BaseDL4JTest {
    int numIn = 10;

    int numOut = 5;

    double drop = 0.3;

    Activation act = Activation.SOFTMAX;

    PoolingType poolType = PoolingType.MAX;

    int[] filterSize = new int[]{ 2, 2 };

    int filterDepth = 6;

    int[] stride = new int[]{ 2, 2 };

    int k = 1;

    Type convType = Type.FULL;

    LossFunction loss = LossFunction.MCXENT;

    WeightInit weight = WeightInit.XAVIER;

    double corrupt = 0.4;

    double sparsity = 0.3;

    @Test
    public void testNeuralNetConfigAPI() {
        LossFunction newLoss = LossFunction.SQUARED_LOSS;
        int newNumIn = (numIn) + 1;
        int newNumOut = (numOut) + 1;
        WeightInit newWeight = WeightInit.UNIFORM;
        double newDrop = 0.5;
        int[] newFS = new int[]{ 3, 3 };
        int newFD = 7;
        int[] newStride = new int[]{ 3, 3 };
        Convolution.Type newConvType = Type.SAME;
        PoolingType newPoolType = PoolingType.AVG;
        double newCorrupt = 0.5;
        double newSparsity = 0.5;
        MultiLayerConfiguration multiConf1 = new NeuralNetConfiguration.Builder().list().layer(0, new DenseLayer.Builder().nIn(newNumIn).nOut(newNumOut).activation(act).build()).layer(1, new DenseLayer.Builder().nIn((newNumIn + 1)).nOut((newNumOut + 1)).activation(act).build()).build();
        NeuralNetConfiguration firstLayer = multiConf1.getConf(0);
        NeuralNetConfiguration secondLayer = multiConf1.getConf(1);
        Assert.assertFalse(firstLayer.equals(secondLayer));
    }
}

