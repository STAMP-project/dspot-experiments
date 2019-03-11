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
package org.deeplearning4j.parallelism.factory;


import Activation.IDENTITY;
import Activation.RELU;
import Activation.SOFTMAX;
import MultiLayerConfiguration.Builder;
import WeightInit.XAVIER;
import lombok.val;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.parallelism.ParallelWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.learning.config.Nesterovs;


public class SymmetricTrainerContextTest {
    int nChannels = 1;

    int outputNum = 10;

    // for GPU you usually want to have higher batchSize
    int seed = 123;

    @Test
    public void testEqualUuid1() {
        MultiLayerConfiguration.Builder builder = // .learningRateDecayPolicy(LearningRatePolicy.Inverse).lrPolicyDecayRate(0.001).lrPolicyPower(0.75)
        new NeuralNetConfiguration.Builder().seed(seed).l2(5.0E-4).weightInit(XAVIER).updater(new Nesterovs(0.01, 0.9)).list().layer(0, nOut(20).activation(IDENTITY).build()).layer(1, kernelSize(2, 2).stride(2, 2).build()).layer(2, nOut(50).activation(IDENTITY).build()).layer(3, kernelSize(2, 2).stride(2, 2).build()).layer(4, nOut(500).build()).layer(5, nOut(outputNum).activation(SOFTMAX).build()).setInputType(InputType.convolutionalFlat(28, 28, nChannels));
        MultiLayerConfiguration conf = builder.build();
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        // ParallelWrapper will take care of load balancing between GPUs.
        ParallelWrapper wrapper = // optinal parameter, set to false ONLY if your system has support P2P memory access across PCIe (hint: AWS do not support P2P)
        // if set to TRUE, on every averaging model score will be reported
        // rare averaging improves performance, but might reduce model accuracy
        // set number of workers equal or higher then number of available devices. x1-x2 are good values to start with
        // DataSets prefetching options. Set this value with respect to number of actual devices
        prefetchBuffer(24).workers(2).averagingFrequency(3).reportScoreAfterAveraging(true).build();
        val trainer = new org.deeplearning4j.parallelism.trainer.SymmetricTrainer(model, "alpha", 3, WorkspaceMode.NONE, wrapper, true);
        Assert.assertEquals("alpha_thread_3", trainer.getUuid());
    }
}

