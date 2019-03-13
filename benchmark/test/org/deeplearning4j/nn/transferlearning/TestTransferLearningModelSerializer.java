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
package org.deeplearning4j.nn.transferlearning;


import Activation.TANH;
import java.util.Map;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.TestUtils;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.GraphVertex;
import org.deeplearning4j.nn.conf.layers.Layer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.FrozenLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;

import static LossFunctions.LossFunction.MCXENT;


/**
 * Created by Alex on 10/07/2017.
 */
public class TestTransferLearningModelSerializer extends BaseDL4JTest {
    @Test
    public void testModelSerializerFrozenLayers() throws Exception {
        FineTuneConfiguration finetune = new FineTuneConfiguration.Builder().updater(new Sgd(0.1)).build();
        int nIn = 6;
        int nOut = 3;
        MultiLayerConfiguration origConf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.1)).activation(TANH).dropOut(0.5).list().layer(0, new org.deeplearning4j.nn.conf.layers.DenseLayer.Builder().nIn(nIn).nOut(5).build()).layer(1, new org.deeplearning4j.nn.conf.layers.DenseLayer.Builder().nIn(5).nOut(4).build()).layer(2, new org.deeplearning4j.nn.conf.layers.DenseLayer.Builder().nIn(4).nOut(3).build()).layer(3, new org.deeplearning4j.nn.conf.layers.OutputLayer.Builder(MCXENT).activation(Activation.SOFTMAX).nIn(3).nOut(nOut).build()).build();
        MultiLayerNetwork origModel = new MultiLayerNetwork(origConf);
        origModel.init();
        MultiLayerNetwork withFrozen = new TransferLearning.Builder(origModel).fineTuneConfiguration(finetune).setFeatureExtractor(1).build();
        Assert.assertTrue(((withFrozen.getLayer(0)) instanceof FrozenLayer));
        Assert.assertTrue(((withFrozen.getLayer(1)) instanceof FrozenLayer));
        Assert.assertTrue(((withFrozen.getLayerWiseConfigurations().getConf(0).getLayer()) instanceof org.deeplearning4j.nn.conf.layers.misc.FrozenLayer));
        Assert.assertTrue(((withFrozen.getLayerWiseConfigurations().getConf(1).getLayer()) instanceof org.deeplearning4j.nn.conf.layers.misc.FrozenLayer));
        MultiLayerNetwork restored = TestUtils.testModelSerialization(withFrozen);
        Assert.assertTrue(((restored.getLayer(0)) instanceof FrozenLayer));
        Assert.assertTrue(((restored.getLayer(1)) instanceof FrozenLayer));
        Assert.assertFalse(((restored.getLayer(2)) instanceof FrozenLayer));
        Assert.assertFalse(((restored.getLayer(3)) instanceof FrozenLayer));
        INDArray in = Nd4j.rand(3, nIn);
        INDArray out = withFrozen.output(in);
        INDArray out2 = restored.output(in);
        Assert.assertEquals(out, out2);
        // Sanity check on train mode:
        out = withFrozen.output(in, true);
        out2 = restored.output(in, true);
    }

    @Test
    public void testModelSerializerFrozenLayersCompGraph() throws Exception {
        FineTuneConfiguration finetune = new FineTuneConfiguration.Builder().updater(new Sgd(0.1)).build();
        int nIn = 6;
        int nOut = 3;
        ComputationGraphConfiguration origConf = new NeuralNetConfiguration.Builder().activation(TANH).graphBuilder().addInputs("in").addLayer("0", new org.deeplearning4j.nn.conf.layers.DenseLayer.Builder().nIn(nIn).nOut(5).build(), "in").addLayer("1", new org.deeplearning4j.nn.conf.layers.DenseLayer.Builder().nIn(5).nOut(4).build(), "0").addLayer("2", new org.deeplearning4j.nn.conf.layers.DenseLayer.Builder().nIn(4).nOut(3).build(), "1").addLayer("3", new org.deeplearning4j.nn.conf.layers.OutputLayer.Builder(MCXENT).activation(Activation.SOFTMAX).nIn(3).nOut(nOut).build(), "2").setOutputs("3").build();
        ComputationGraph origModel = new ComputationGraph(origConf);
        origModel.init();
        ComputationGraph withFrozen = new TransferLearning.GraphBuilder(origModel).fineTuneConfiguration(finetune).setFeatureExtractor("1").build();
        Assert.assertTrue(((withFrozen.getLayer(0)) instanceof FrozenLayer));
        Assert.assertTrue(((withFrozen.getLayer(1)) instanceof FrozenLayer));
        Map<String, GraphVertex> m = withFrozen.getConfiguration().getVertices();
        Layer l0 = getLayerConf().getLayer();
        Layer l1 = getLayerConf().getLayer();
        Assert.assertTrue((l0 instanceof org.deeplearning4j.nn.conf.layers.misc.FrozenLayer));
        Assert.assertTrue((l1 instanceof org.deeplearning4j.nn.conf.layers.misc.FrozenLayer));
        ComputationGraph restored = TestUtils.testModelSerialization(withFrozen);
        Assert.assertTrue(((restored.getLayer(0)) instanceof FrozenLayer));
        Assert.assertTrue(((restored.getLayer(1)) instanceof FrozenLayer));
        Assert.assertFalse(((restored.getLayer(2)) instanceof FrozenLayer));
        Assert.assertFalse(((restored.getLayer(3)) instanceof FrozenLayer));
        INDArray in = Nd4j.rand(3, nIn);
        INDArray out = withFrozen.outputSingle(in);
        INDArray out2 = restored.outputSingle(in);
        Assert.assertEquals(out, out2);
        // Sanity check on train mode:
        out = withFrozen.outputSingle(true, in);
        out2 = restored.outputSingle(true, in);
    }
}

