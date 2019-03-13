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
package org.deeplearning4j.spark.impl.misc;


import Activation.TANH;
import RDDTrainingApproach.Direct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.spark.api.java.JavaRDD;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.FrozenLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.transferlearning.FineTuneConfiguration;
import org.deeplearning4j.spark.BaseSparkTest;
import org.deeplearning4j.spark.impl.graph.SparkComputationGraph;
import org.deeplearning4j.spark.impl.multilayer.SparkDl4jMultiLayer;
import org.deeplearning4j.spark.impl.paramavg.ParameterAveragingTrainingMaster;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;

import static LossFunctions.LossFunction.MCXENT;


/**
 * Created by Alex on 10/07/2017.
 */
public class TestFrozenLayers extends BaseSparkTest {
    @Test
    public void testSparkFrozenLayers() {
        NeuralNetConfiguration.Builder overallConf = new NeuralNetConfiguration.Builder().updater(new Sgd(0.1)).activation(TANH);
        FineTuneConfiguration finetune = new FineTuneConfiguration.Builder().updater(new Sgd(0.1)).build();
        int nIn = 6;
        int nOut = 3;
        MultiLayerNetwork origModel = new MultiLayerNetwork(overallConf.clone().list().layer(0, new org.deeplearning4j.nn.conf.layers.DenseLayer.Builder().nIn(6).nOut(5).build()).layer(1, new org.deeplearning4j.nn.conf.layers.DenseLayer.Builder().nIn(5).nOut(4).build()).layer(2, new org.deeplearning4j.nn.conf.layers.DenseLayer.Builder().nIn(4).nOut(3).build()).layer(3, new org.deeplearning4j.nn.conf.layers.OutputLayer.Builder(MCXENT).activation(Activation.SOFTMAX).nIn(3).nOut(3).build()).build());
        origModel.init();
        MultiLayerNetwork withFrozen = new org.deeplearning4j.nn.transferlearning.TransferLearning.Builder(origModel).fineTuneConfiguration(finetune).setFeatureExtractor(1).build();
        Map<String, INDArray> m = withFrozen.paramTable();
        Map<String, INDArray> pCopy = new HashMap<>();
        for (Map.Entry<String, INDArray> entry : m.entrySet()) {
            pCopy.put(entry.getKey(), entry.getValue().dup());
        }
        int avgFreq = 2;
        int batchSize = 8;
        ParameterAveragingTrainingMaster tm = new ParameterAveragingTrainingMaster.Builder(batchSize).averagingFrequency(avgFreq).batchSizePerWorker(batchSize).rddTrainingApproach(Direct).workerPrefetchNumBatches(0).build();
        SparkDl4jMultiLayer sNet = new SparkDl4jMultiLayer(sc, withFrozen.clone(), tm);
        Assert.assertTrue(((withFrozen.getLayer(0)) instanceof FrozenLayer));
        Assert.assertTrue(((withFrozen.getLayer(1)) instanceof FrozenLayer));
        int numMinibatches = 4 * (sc.defaultParallelism());
        List<DataSet> list = new ArrayList<>();
        for (int i = 0; i < numMinibatches; i++) {
            INDArray f = Nd4j.rand(batchSize, nIn);
            INDArray l = Nd4j.zeros(batchSize, nOut);
            for (int j = 0; j < batchSize; j++) {
                l.putScalar(j, (j % nOut), 1.0);
            }
            list.add(new DataSet(f, l));
        }
        JavaRDD<DataSet> rdd = sc.parallelize(list);
        sNet.fit(rdd);
        MultiLayerNetwork fitted = sNet.getNetwork();
        Map<String, INDArray> fittedParams = fitted.paramTable();
        for (Map.Entry<String, INDArray> entry : fittedParams.entrySet()) {
            INDArray orig = pCopy.get(entry.getKey());
            INDArray now = entry.getValue();
            boolean isFrozen = (entry.getKey().startsWith("0_")) || (entry.getKey().startsWith("1_"));
            if (isFrozen) {
                // Layer should be frozen -> no change
                Assert.assertEquals(entry.getKey(), orig, now);
            } else {
                // Not frozen -> should be different
                Assert.assertNotEquals(entry.getKey(), orig, now);
            }
        }
    }

    @Test
    public void testSparkFrozenLayersCompGraph() {
        FineTuneConfiguration finetune = new FineTuneConfiguration.Builder().updater(new Sgd(0.1)).build();
        int nIn = 6;
        int nOut = 3;
        ComputationGraph origModel = new ComputationGraph(new NeuralNetConfiguration.Builder().updater(new Sgd(0.1)).activation(TANH).graphBuilder().addInputs("in").addLayer("0", new org.deeplearning4j.nn.conf.layers.DenseLayer.Builder().nIn(6).nOut(5).build(), "in").addLayer("1", new org.deeplearning4j.nn.conf.layers.DenseLayer.Builder().nIn(5).nOut(4).build(), "0").addLayer("2", new org.deeplearning4j.nn.conf.layers.DenseLayer.Builder().nIn(4).nOut(3).build(), "1").addLayer("3", new org.deeplearning4j.nn.conf.layers.OutputLayer.Builder(MCXENT).activation(Activation.SOFTMAX).nIn(3).nOut(3).build(), "2").setOutputs("3").build());
        origModel.init();
        ComputationGraph withFrozen = new org.deeplearning4j.nn.transferlearning.TransferLearning.GraphBuilder(origModel).fineTuneConfiguration(finetune).setFeatureExtractor("1").build();
        Map<String, INDArray> m = withFrozen.paramTable();
        Map<String, INDArray> pCopy = new HashMap<>();
        for (Map.Entry<String, INDArray> entry : m.entrySet()) {
            pCopy.put(entry.getKey(), entry.getValue().dup());
        }
        int avgFreq = 2;
        int batchSize = 8;
        ParameterAveragingTrainingMaster tm = new ParameterAveragingTrainingMaster.Builder(batchSize).averagingFrequency(avgFreq).batchSizePerWorker(batchSize).rddTrainingApproach(Direct).workerPrefetchNumBatches(0).build();
        SparkComputationGraph sNet = new SparkComputationGraph(sc, withFrozen.clone(), tm);
        Assert.assertTrue(((withFrozen.getLayer(0)) instanceof FrozenLayer));
        Assert.assertTrue(((withFrozen.getLayer(1)) instanceof FrozenLayer));
        int numMinibatches = 4 * (sc.defaultParallelism());
        List<DataSet> list = new ArrayList<>();
        for (int i = 0; i < numMinibatches; i++) {
            INDArray f = Nd4j.rand(batchSize, nIn);
            INDArray l = Nd4j.zeros(batchSize, nOut);
            for (int j = 0; j < batchSize; j++) {
                l.putScalar(j, (j % nOut), 1.0);
            }
            list.add(new DataSet(f, l));
        }
        JavaRDD<DataSet> rdd = sc.parallelize(list);
        sNet.fit(rdd);
        ComputationGraph fitted = sNet.getNetwork();
        Map<String, INDArray> fittedParams = fitted.paramTable();
        for (Map.Entry<String, INDArray> entry : fittedParams.entrySet()) {
            INDArray orig = pCopy.get(entry.getKey());
            INDArray now = entry.getValue();
            boolean isFrozen = (entry.getKey().startsWith("0_")) || (entry.getKey().startsWith("1_"));
            if (isFrozen) {
                // Layer should be frozen -> no change
                Assert.assertEquals(entry.getKey(), orig, now);
            } else {
                // Not frozen -> should be different
                Assert.assertNotEquals(entry.getKey(), orig, now);
            }
        }
    }
}

