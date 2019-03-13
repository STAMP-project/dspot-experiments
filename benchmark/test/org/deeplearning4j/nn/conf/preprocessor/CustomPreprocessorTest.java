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
package org.deeplearning4j.nn.conf.preprocessor;


import Activation.SOFTMAX;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.preprocessor.custom.MyCustomPreprocessor;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Alex on 09/09/2016.
 */
public class CustomPreprocessorTest extends BaseDL4JTest {
    @Test
    public void testCustomPreprocessor() {
        // Second: let's create a MultiLayerCofiguration with one, and check JSON and YAML config actually works...
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(0, new DenseLayer.Builder().nIn(10).nOut(10).build()).layer(1, nIn(10).activation(SOFTMAX).nOut(10).build()).inputPreProcessor(0, new MyCustomPreprocessor()).build();
        String json = conf.toJson();
        String yaml = conf.toYaml();
        System.out.println(json);
        MultiLayerConfiguration confFromJson = MultiLayerConfiguration.fromJson(json);
        Assert.assertEquals(conf, confFromJson);
        MultiLayerConfiguration confFromYaml = MultiLayerConfiguration.fromYaml(yaml);
        Assert.assertEquals(conf, confFromYaml);
        Assert.assertTrue(((confFromJson.getInputPreProcess(0)) instanceof MyCustomPreprocessor));
    }
}

