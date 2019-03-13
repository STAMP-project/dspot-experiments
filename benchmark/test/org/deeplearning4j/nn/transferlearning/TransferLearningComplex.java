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


import Activation.IDENTITY;
import Activation.LEAKYRELU;
import NeuralNetConfiguration.Builder;
import OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.BaseLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.graph.vertex.GraphVertex;
import org.deeplearning4j.nn.layers.FrozenLayer;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Sgd;


/**
 * Created by susaneraly on 2/20/17.
 */
@Slf4j
public class TransferLearningComplex extends BaseDL4JTest {
    @Test
    public void testMergeAndFreeze() {
        // in1 -> A -> B -> merge, in2 -> C -> merge -> D -> out
        // Goal here: test a number of things...
        // (a) Ensure that freezing C doesn't impact A and B. Only C should be frozen in this config
        // (b) Test global override (should be selective)
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().updater(new Adam(1.0E-4)).activation(LEAKYRELU).graphBuilder().addInputs("in1", "in2").addLayer("A", new DenseLayer.Builder().nIn(10).nOut(9).build(), "in1").addLayer("B", new DenseLayer.Builder().nIn(9).nOut(8).build(), "A").addLayer("C", new DenseLayer.Builder().nIn(7).nOut(6).build(), "in2").addLayer("D", new DenseLayer.Builder().nIn((8 + 7)).nOut(5).build(), "B", "C").addLayer("out", new OutputLayer.Builder().nIn(5).nOut(4).activation(LEAKYRELU).build(), "D").setOutputs("out").validateOutputLayerConfig(false).build();
        ComputationGraph graph = new ComputationGraph(conf);
        graph.init();
        int[] topologicalOrder = graph.topologicalSortOrder();
        org.deeplearning4j.nn.graph.vertex[] vertices = graph.getVertices();
        for (int i = 0; i < (topologicalOrder.length); i++) {
            GraphVertex v = vertices[topologicalOrder[i]];
            log.info(((i + "\t") + (v.getVertexName())));
        }
        ComputationGraph graph2 = setFeatureExtractor("C").validateOutputLayerConfig(false).build();
        boolean cFound = false;
        Layer[] layers = graph2.getLayers();
        for (Layer l : layers) {
            String name = l.conf().getLayer().getLayerName();
            log.info(((name + "\t frozen: ") + (l instanceof FrozenLayer)));
            if ("C".equals(l.conf().getLayer().getLayerName())) {
                // Only C should be frozen in this config
                cFound = true;
                Assert.assertTrue(name, (l instanceof FrozenLayer));
            } else {
                Assert.assertFalse(name, (l instanceof FrozenLayer));
            }
            // Also check config:
            BaseLayer bl = ((BaseLayer) (l.conf().getLayer()));
            Assert.assertEquals(new Adam(0.02), bl.getIUpdater());
            Assert.assertEquals(LEAKYRELU.getActivationFunction(), bl.getActivationFn());
        }
        Assert.assertTrue(cFound);
    }

    @Test
    public void testSimplerMergeBackProp() {
        NeuralNetConfiguration.Builder overallConf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.9)).activation(IDENTITY).optimizationAlgo(STOCHASTIC_GRADIENT_DESCENT);
        /* inCentre                inRight
        |                        |
        denseCentre0               denseRight0
        |                        |
        |------ mergeRight ------|
        |
        outRight
         */
        ComputationGraphConfiguration conf = overallConf.graphBuilder().addInputs("inCentre", "inRight").addLayer("denseCentre0", new DenseLayer.Builder().nIn(2).nOut(2).build(), "inCentre").addLayer("denseRight0", new DenseLayer.Builder().nIn(2).nOut(2).build(), "inRight").addVertex("mergeRight", new MergeVertex(), "denseCentre0", "denseRight0").addLayer("outRight", nIn(4).nOut(2).build(), "mergeRight").setOutputs("outRight").build();
        ComputationGraph modelToTune = new ComputationGraph(conf);
        modelToTune.init();
        MultiDataSet randData = new MultiDataSet(new INDArray[]{ Nd4j.rand(2, 2), Nd4j.rand(2, 2) }, new INDArray[]{ Nd4j.rand(2, 2) });
        INDArray denseCentre0 = modelToTune.feedForward(randData.getFeatures(), false).get("denseCentre0");
        MultiDataSet otherRandData = new MultiDataSet(new INDArray[]{ denseCentre0, randData.getFeatures(1) }, randData.getLabels());
        ComputationGraphConfiguration otherConf = overallConf.graphBuilder().addInputs("denseCentre0", "inRight").addLayer("denseRight0", new DenseLayer.Builder().nIn(2).nOut(2).build(), "inRight").addVertex("mergeRight", new MergeVertex(), "denseCentre0", "denseRight0").addLayer("outRight", nIn(4).nOut(2).build(), "mergeRight").setOutputs("outRight").build();
        ComputationGraph modelOther = new ComputationGraph(otherConf);
        modelOther.init();
        modelOther.getLayer("denseRight0").setParams(modelToTune.getLayer("denseRight0").params());
        modelOther.getLayer("outRight").setParams(modelToTune.getLayer("outRight").params());
        modelToTune.getVertex("denseCentre0").setLayerAsFrozen();
        ComputationGraph modelNow = setFeatureExtractor("denseCentre0").build();
        int n = 0;
        while (n < 5) {
            if (n == 0) {
                // confirm activations out of the merge are equivalent
                Assert.assertEquals(modelToTune.feedForward(randData.getFeatures(), false).get("mergeRight"), modelOther.feedForward(otherRandData.getFeatures(), false).get("mergeRight"));
                Assert.assertEquals(modelNow.feedForward(randData.getFeatures(), false).get("mergeRight"), modelOther.feedForward(otherRandData.getFeatures(), false).get("mergeRight"));
            }
            // confirm activations out of frozen vertex is the same as the input to the other model
            modelOther.fit(otherRandData);
            modelToTune.fit(randData);
            modelNow.fit(randData);
            Assert.assertEquals(otherRandData.getFeatures(0), modelNow.feedForward(randData.getFeatures(), false).get("denseCentre0"));
            Assert.assertEquals(otherRandData.getFeatures(0), modelToTune.feedForward(randData.getFeatures(), false).get("denseCentre0"));
            Assert.assertEquals(modelOther.getLayer("denseRight0").params(), modelNow.getLayer("denseRight0").params());
            Assert.assertEquals(modelOther.getLayer("denseRight0").params(), modelToTune.getLayer("denseRight0").params());
            Assert.assertEquals(modelOther.getLayer("outRight").params(), modelNow.getLayer("outRight").params());
            Assert.assertEquals(modelOther.getLayer("outRight").params(), modelToTune.getLayer("outRight").params());
            n++;
        } 
    }

    @Test
    public void testLessSimpleMergeBackProp() {
        NeuralNetConfiguration.Builder overallConf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.9)).activation(IDENTITY);
        /* inCentre                inRight
        |                        |
        denseCentre0               denseRight0
        |                        |
        |------ mergeRight ------|
        |            |
        outCentre     outRight
         */
        ComputationGraphConfiguration conf = overallConf.graphBuilder().addInputs("inCentre", "inRight").addLayer("denseCentre0", new DenseLayer.Builder().nIn(2).nOut(2).build(), "inCentre").addLayer("outCentre", nIn(2).nOut(2).build(), "denseCentre0").addLayer("denseRight0", new DenseLayer.Builder().nIn(3).nOut(2).build(), "inRight").addVertex("mergeRight", new MergeVertex(), "denseCentre0", "denseRight0").addLayer("outRight", nIn(4).nOut(2).build(), "mergeRight").setOutputs("outCentre", "outRight").build();
        ComputationGraph modelToTune = new ComputationGraph(conf);
        modelToTune.init();
        modelToTune.getVertex("denseCentre0").setLayerAsFrozen();
        MultiDataSet randData = new MultiDataSet(new INDArray[]{ Nd4j.rand(2, 2), Nd4j.rand(2, 3) }, new INDArray[]{ Nd4j.rand(2, 2), Nd4j.rand(2, 2) });
        INDArray denseCentre0 = modelToTune.feedForward(randData.getFeatures(), false).get("denseCentre0");
        MultiDataSet otherRandData = new MultiDataSet(new INDArray[]{ denseCentre0, randData.getFeatures(1) }, randData.getLabels());
        ComputationGraph modelNow = setFeatureExtractor("denseCentre0").build();
        Assert.assertTrue(((modelNow.getLayer("denseCentre0")) instanceof FrozenLayer));
        int n = 0;
        while (n < 5) {
            // confirm activations out of frozen vertex is the same as the input to the other model
            modelToTune.fit(randData);
            modelNow.fit(randData);
            Assert.assertEquals(otherRandData.getFeatures(0), modelNow.feedForward(randData.getFeatures(), false).get("denseCentre0"));
            Assert.assertEquals(otherRandData.getFeatures(0), modelToTune.feedForward(randData.getFeatures(), false).get("denseCentre0"));
            Assert.assertEquals(modelToTune.getLayer("denseRight0").params(), modelNow.getLayer("denseRight0").params());
            Assert.assertEquals(modelToTune.getLayer("outRight").params(), modelNow.getLayer("outRight").params());
            Assert.assertEquals(modelToTune.getLayer("outCentre").params(), modelNow.getLayer("outCentre").params());
            n++;
        } 
    }

    @Test
    public void testAddOutput() {
        NeuralNetConfiguration.Builder overallConf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.9)).activation(IDENTITY);
        ComputationGraphConfiguration conf = overallConf.graphBuilder().addInputs("inCentre", "inRight").addLayer("denseCentre0", new DenseLayer.Builder().nIn(2).nOut(2).build(), "inCentre").addLayer("denseRight0", new DenseLayer.Builder().nIn(2).nOut(2).build(), "inRight").addVertex("mergeRight", new MergeVertex(), "denseCentre0", "denseRight0").addLayer("outRight", nIn(4).nOut(2).build(), "mergeRight").setOutputs("outRight").build();
        ComputationGraph modelToTune = new ComputationGraph(conf);
        modelToTune.init();
        ComputationGraph modelNow = new TransferLearning.GraphBuilder(modelToTune).addLayer("outCentre", nIn(2).nOut(3).build(), "denseCentre0").setOutputs("outRight", "outCentre").build();
        Assert.assertEquals(2, modelNow.getNumOutputArrays());
        MultiDataSet rand = new MultiDataSet(new INDArray[]{ Nd4j.rand(2, 2), Nd4j.rand(2, 2) }, new INDArray[]{ Nd4j.rand(2, 2), Nd4j.rand(2, 3) });
        modelNow.fit(rand);
        log.info(modelNow.summary());
        log.info(modelNow.summary(InputType.feedForward(2), InputType.feedForward(2)));
    }
}

