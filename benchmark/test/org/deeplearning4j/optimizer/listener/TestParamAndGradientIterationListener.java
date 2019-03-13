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
package org.deeplearning4j.optimizer.listener;


import Activation.SOFTMAX;
import LossFunctions.LossFunction;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import java.io.File;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.TrainingListener;
import org.deeplearning4j.optimize.listeners.ParamAndGradientIterationListener;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.learning.config.Sgd;


public class TestParamAndGradientIterationListener extends BaseDL4JTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void test() throws Exception {
        IrisDataSetIterator iter = new IrisDataSetIterator(30, 150);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT).updater(new Sgd(1.0E-5)).list().layer(0, new DenseLayer.Builder().nIn(4).nOut(20).build()).layer(1, new DenseLayer.Builder().nIn(20).nOut(30).build()).layer(2, new org.deeplearning4j.nn.conf.layers.OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nIn(30).nOut(3).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        File f = testDir.newFile("paramAndGradTest.txt");
        TrainingListener listener = ParamAndGradientIterationListener.builder().outputToFile(true).file(f).outputToConsole(true).outputToLogger(false).iterations(2).printHeader(true).printMean(false).printMinMax(false).printMeanAbsValue(true).delimiter("\t").build();
        net.setListeners(listener);
        for (int i = 0; i < 2; i++) {
            net.fit(iter);
        }
    }
}

