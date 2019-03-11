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
package org.deeplearning4j.parallelism.parameterserver;


import Activation.IDENTITY;
import Activation.RELU;
import Activation.SOFTMAX;
import MultiLayerConfiguration.Builder;
import WeightInit.XAVIER;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.parallelism.ParallelWrapper;
import org.junit.Test;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Nesterovs;


/**
 * Created by agibsonccc on 12/17/16.
 */
@Slf4j
public class ParameterServerParallelWrapperTest {
    @Test
    public void testWrapper() throws Exception {
        int nChannels = 1;
        int outputNum = 10;
        // for GPU you usually want to have higher batchSize
        int batchSize = 128;
        int seed = 123;
        log.info("Load data....");
        DataSetIterator mnistTrain = new MnistDataSetIterator(batchSize, 1000);
        DataSetIterator mnistTest = new MnistDataSetIterator(batchSize, false, 12345);
        log.info("Build model....");
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().seed(seed).l2(5.0E-4).weightInit(XAVIER).updater(new Nesterovs(0.01, 0.9)).list().layer(0, nOut(20).activation(IDENTITY).build()).layer(1, kernelSize(2, 2).stride(2, 2).build()).layer(2, nOut(50).activation(IDENTITY).build()).layer(3, kernelSize(2, 2).stride(2, 2).build()).layer(4, nOut(500).build()).layer(5, nOut(outputNum).activation(SOFTMAX).build()).setInputType(InputType.convolutionalFlat(28, 28, 1));
        MultiLayerConfiguration conf = builder.build();
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        ParallelWrapper parameterServerParallelWrapper = new ParallelWrapper.Builder(model).trainerFactory(new ParameterServerTrainerContext()).workers(Runtime.getRuntime().availableProcessors()).reportScoreAfterAveraging(true).prefetchBuffer(3).build();
        parameterServerParallelWrapper.fit(mnistTrain);
        Thread.sleep(2000);
        parameterServerParallelWrapper.close();
    }
}

