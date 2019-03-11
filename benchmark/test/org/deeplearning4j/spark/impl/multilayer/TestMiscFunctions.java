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
package org.deeplearning4j.spark.impl.multilayer;


import Activation.SOFTMAX;
import WeightInit.XAVIER;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.spark.api.java.JavaPairRDD;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.variational.VariationalAutoencoder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.spark.BaseSparkTest;
import org.deeplearning4j.spark.impl.graph.SparkComputationGraph;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import scala.Tuple2;

import static PoolingType.AVG;


/**
 * Created by Alex on 17/12/2016.
 */
public class TestMiscFunctions extends BaseSparkTest {
    @Test
    public void testFeedForwardWithKey() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().weightInit(XAVIER).list().layer(0, new DenseLayer.Builder().nIn(4).nOut(3).build()).layer(1, nIn(3).nOut(3).activation(SOFTMAX).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        DataSet ds = iter.next();
        List<INDArray> expected = new ArrayList<>();
        List<Tuple2<Integer, INDArray>> mapFeatures = new ArrayList<>();
        int count = 0;
        int arrayCount = 0;
        Random r = new Random(12345);
        while (count < 150) {
            int exampleCount = (r.nextInt(5)) + 1;// 1 to 5 inclusive examples

            if ((count + exampleCount) > 150)
                exampleCount = 150 - count;

            INDArray subset = ds.getFeatures().get(NDArrayIndex.interval(count, (count + exampleCount)), NDArrayIndex.all());
            expected.add(net.output(subset, false));
            mapFeatures.add(new Tuple2(arrayCount, subset));
            arrayCount++;
            count += exampleCount;
        } 
        // JavaPairRDD<Integer, INDArray> rdd = sc.parallelizePairs(mapFeatures);
        JavaPairRDD<Integer, INDArray> rdd = sc.parallelizePairs(mapFeatures);
        SparkDl4jMultiLayer multiLayer = new SparkDl4jMultiLayer(sc, net, null);
        Map<Integer, INDArray> map = multiLayer.feedForwardWithKey(rdd, 16).collectAsMap();
        for (int i = 0; i < (expected.size()); i++) {
            INDArray exp = expected.get(i);
            INDArray act = map.get(i);
            Assert.assertEquals(exp, act);
        }
    }

    @Test
    public void testFeedForwardWithKeyInputMask() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().weightInit(XAVIER).list().layer(new LSTM.Builder().nIn(4).nOut(3).build()).layer(new GlobalPoolingLayer(AVG)).layer(nIn(3).nOut(3).activation(SOFTMAX).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        List<org.nd4j.linalg.dataset.DataSet> ds = Arrays.asList(new org.nd4j.linalg.dataset.DataSet(Nd4j.rand(new int[]{ 1, 4, 5 }), Nd4j.create(new double[]{ 1, 1, 1, 0, 0 })), new org.nd4j.linalg.dataset.DataSet(Nd4j.rand(new int[]{ 1, 4, 5 }), Nd4j.create(new double[]{ 1, 1, 1, 1, 0 })), new org.nd4j.linalg.dataset.DataSet(Nd4j.rand(new int[]{ 1, 4, 5 }), Nd4j.create(new double[]{ 1, 1, 1, 1, 1 })));
        Map<Integer, INDArray> expected = new HashMap<>();
        List<Tuple2<Integer, Tuple2<INDArray, INDArray>>> mapFeatures = new ArrayList<>();
        int count = 0;
        int arrayCount = 0;
        Random r = new Random(12345);
        int i = 0;
        for (org.nd4j.linalg.dataset.DataSet d : ds) {
            INDArray f = d.getFeatures();
            INDArray fm = d.getFeaturesMaskArray();
            mapFeatures.add(new Tuple2(i, new Tuple2(f, fm)));
            INDArray out = net.output(f, false, fm, null);
            expected.put((i++), out);
        }
        JavaPairRDD<Integer, Tuple2<INDArray, INDArray>> rdd = sc.parallelizePairs(mapFeatures);
        SparkDl4jMultiLayer multiLayer = new SparkDl4jMultiLayer(sc, net, null);
        Map<Integer, INDArray> map = multiLayer.feedForwardWithMaskAndKey(rdd, 16).collectAsMap();
        for (i = 0; i < (expected.size()); i++) {
            INDArray exp = expected.get(i);
            INDArray act = map.get(i);
            Assert.assertEquals(exp, act);
        }
    }

    @Test
    public void testFeedForwardWithKeyGraph() {
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().weightInit(XAVIER).graphBuilder().addInputs("in1", "in2").addLayer("0", new DenseLayer.Builder().nIn(4).nOut(3).build(), "in1").addLayer("1", new DenseLayer.Builder().nIn(4).nOut(3).build(), "in2").addLayer("2", nIn(6).nOut(3).activation(SOFTMAX).build(), "0", "1").setOutputs("2").build();
        ComputationGraph net = new ComputationGraph(conf);
        net.init();
        DataSetIterator iter = new IrisDataSetIterator(150, 150);
        DataSet ds = iter.next();
        List<INDArray> expected = new ArrayList<>();
        List<Tuple2<Integer, INDArray[]>> mapFeatures = new ArrayList<>();
        int count = 0;
        int arrayCount = 0;
        Random r = new Random(12345);
        while (count < 150) {
            int exampleCount = (r.nextInt(5)) + 1;// 1 to 5 inclusive examples

            if ((count + exampleCount) > 150)
                exampleCount = 150 - count;

            INDArray subset = ds.getFeatures().get(NDArrayIndex.interval(count, (count + exampleCount)), NDArrayIndex.all());
            expected.add(net.outputSingle(false, subset, subset));
            mapFeatures.add(new Tuple2(arrayCount, new INDArray[]{ subset, subset }));
            arrayCount++;
            count += exampleCount;
        } 
        JavaPairRDD<Integer, INDArray[]> rdd = sc.parallelizePairs(mapFeatures);
        SparkComputationGraph graph = new SparkComputationGraph(sc, net, null);
        Map<Integer, INDArray[]> map = graph.feedForwardWithKey(rdd, 16).collectAsMap();
        for (int i = 0; i < (expected.size()); i++) {
            INDArray exp = expected.get(i);
            INDArray act = map.get(i)[0];
            Assert.assertEquals(exp, act);
        }
    }

    @Test
    public void testVaeReconstructionProbabilityWithKey() {
        // Simple test. We can't do a direct comparison, as the reconstruction probabilities are stochastic
        // due to sampling
        int nIn = 10;
        MultiLayerConfiguration mlc = new NeuralNetConfiguration.Builder().list().layer(0, nIn(nIn).nOut(5).encoderLayerSizes(12).decoderLayerSizes(13).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(mlc);
        net.init();
        List<Tuple2<Integer, INDArray>> toScore = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            INDArray arr = Nd4j.rand(1, nIn);
            toScore.add(new Tuple2<Integer, INDArray>(i, arr));
        }
        JavaPairRDD<Integer, INDArray> rdd = sc.parallelizePairs(toScore);
        JavaPairRDD<Integer, Double> reconstr = rdd.mapPartitionsToPair(new org.deeplearning4j.spark.impl.multilayer.scoring.VaeReconstructionProbWithKeyFunction<Integer>(sc.broadcast(net.params()), sc.broadcast(mlc.toJson()), true, 16, 128));
        Map<Integer, Double> l = reconstr.collectAsMap();
        Assert.assertEquals(100, l.size());
        for (int i = 0; i < 100; i++) {
            Assert.assertTrue(l.containsKey(i));
            Assert.assertTrue(((l.get(i)) < 0.0));// log probability: should be negative

        }
    }

    @Test
    public void testVaeReconstructionErrorWithKey() {
        // Simple test. We CAN do a direct comparison here vs. local, as reconstruction error is deterministic
        int nIn = 10;
        MultiLayerConfiguration mlc = new NeuralNetConfiguration.Builder().list().layer(0, nIn(nIn).nOut(5).encoderLayerSizes(12).decoderLayerSizes(13).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(mlc);
        net.init();
        VariationalAutoencoder vae = ((VariationalAutoencoder) (net.getLayer(0)));
        List<Tuple2<Integer, INDArray>> toScore = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            INDArray arr = Nd4j.rand(1, nIn);
            toScore.add(new Tuple2<Integer, INDArray>(i, arr));
        }
        JavaPairRDD<Integer, INDArray> rdd = sc.parallelizePairs(toScore);
        JavaPairRDD<Integer, Double> reconstrErrors = rdd.mapPartitionsToPair(new org.deeplearning4j.spark.impl.multilayer.scoring.VaeReconstructionErrorWithKeyFunction<Integer>(sc.broadcast(net.params()), sc.broadcast(mlc.toJson()), 16));
        Map<Integer, Double> l = reconstrErrors.collectAsMap();
        Assert.assertEquals(100, l.size());
        for (int i = 0; i < 100; i++) {
            Assert.assertTrue(l.containsKey(i));
            INDArray localToScore = toScore.get(i)._2();
            double localScore = vae.reconstructionError(localToScore).data().asDouble()[0];
            Assert.assertEquals(localScore, l.get(i), 1.0E-6);
        }
    }
}

