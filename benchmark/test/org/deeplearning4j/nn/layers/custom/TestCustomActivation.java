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
package org.deeplearning4j.nn.layers.custom;


import Activation.SOFTMAX;
import LossFunctions.LossFunction;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.layers.custom.testclasses.CustomActivation;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.learning.config.Sgd;


/**
 * Created by Alex on 19/12/2016.
 */
public class TestCustomActivation extends BaseDL4JTest {
    @Test
    public void testCustomActivationFn() {
        // Second: let's create a MultiLayerCofiguration with one, and check JSON and YAML config actually works...
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.1)).list().layer(0, new DenseLayer.Builder().nIn(10).nOut(10).activation(new CustomActivation()).build()).layer(1, new org.deeplearning4j.nn.conf.layers.OutputLayer.Builder(LossFunction.MCXENT).activation(SOFTMAX).nIn(10).nOut(10).build()).build();
        String json = conf.toJson();
        String yaml = conf.toYaml();
        System.out.println(json);
        MultiLayerConfiguration confFromJson = MultiLayerConfiguration.fromJson(json);
        Assert.assertEquals(conf, confFromJson);
        MultiLayerConfiguration confFromYaml = MultiLayerConfiguration.fromYaml(yaml);
        Assert.assertEquals(conf, confFromYaml);
    }
}

