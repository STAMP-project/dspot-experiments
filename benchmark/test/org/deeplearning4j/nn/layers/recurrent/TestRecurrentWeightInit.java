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
package org.deeplearning4j.nn.layers.recurrent;


import NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.recurrent.SimpleRnn;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;


public class TestRecurrentWeightInit {
    @Test
    public void testRWInit() {
        for (boolean rwInit : new boolean[]{ false, true }) {
            for (int i = 0; i < 3; i++) {
                NeuralNetConfiguration.ListBuilder b = new NeuralNetConfiguration.Builder().weightInit(new UniformDistribution(0, 1)).list();
                if (rwInit) {
                    switch (i) {
                        case 0 :
                            b.layer(new LSTM.Builder().nIn(10).nOut(10).weightInitRecurrent(new UniformDistribution(2, 3)).build());
                            break;
                        case 1 :
                            b.layer(new GravesLSTM.Builder().nIn(10).nOut(10).weightInitRecurrent(new UniformDistribution(2, 3)).build());
                            break;
                        case 2 :
                            b.layer(new SimpleRnn.Builder().nIn(10).nOut(10).weightInitRecurrent(new UniformDistribution(2, 3)).build());
                            break;
                        default :
                            throw new RuntimeException();
                    }
                } else {
                    switch (i) {
                        case 0 :
                            b.layer(new LSTM.Builder().nIn(10).nOut(10).build());
                            break;
                        case 1 :
                            b.layer(new GravesLSTM.Builder().nIn(10).nOut(10).build());
                            break;
                        case 2 :
                            b.layer(new SimpleRnn.Builder().nIn(10).nOut(10).build());
                            break;
                        default :
                            throw new RuntimeException();
                    }
                }
                MultiLayerNetwork net = new MultiLayerNetwork(b.build());
                net.init();
                INDArray rw = net.getParam("0_RW");
                double min = rw.minNumber().doubleValue();
                double max = rw.maxNumber().doubleValue();
                if (rwInit) {
                    Assert.assertTrue(String.valueOf(min), (min >= 2.0));
                    Assert.assertTrue(String.valueOf(max), (max <= 3.0));
                } else {
                    Assert.assertTrue(String.valueOf(min), (min >= 0.0));
                    Assert.assertTrue(String.valueOf(max), (max <= 1.0));
                }
            }
        }
    }
}

