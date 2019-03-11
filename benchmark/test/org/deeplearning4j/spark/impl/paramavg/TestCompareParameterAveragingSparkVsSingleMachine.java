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
package org.deeplearning4j.spark.impl.paramavg;


import RDDTrainingApproach.Export;
import java.util.Arrays;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.spark.api.TrainingMaster;
import org.deeplearning4j.spark.impl.graph.SparkComputationGraph;
import org.deeplearning4j.spark.impl.multilayer.SparkDl4jMultiLayer;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.learning.config.Sgd;


// import org.nd4j.jita.conf.Configuration;
// import org.nd4j.jita.conf.CudaEnvironment;
// import org.nd4j.linalg.api.ops.executioner.GridExecutioner;
// import org.nd4j.linalg.jcublas.ops.executioner.CudaGridExecutioner;
/**
 * Created by Alex on 18/06/2016.
 */
public class TestCompareParameterAveragingSparkVsSingleMachine {
    @Test
    public void testOneExecutor() {
        // Idea: single worker/executor on Spark should give identical results to a single machine
        int miniBatchSize = 10;
        int nWorkers = 1;
        for (boolean saveUpdater : new boolean[]{ true, false }) {
            JavaSparkContext sc = TestCompareParameterAveragingSparkVsSingleMachine.getContext(nWorkers);
            try {
                // Do training locally, for 3 minibatches
                int[] seeds = new int[]{ 1, 2, 3 };
                MultiLayerNetwork net = new MultiLayerNetwork(TestCompareParameterAveragingSparkVsSingleMachine.getConf(12345, new RmsProp(0.5)));
                net.init();
                INDArray initialParams = net.params().dup();
                for (int i = 0; i < (seeds.length); i++) {
                    DataSet ds = getOneDataSet(miniBatchSize, seeds[i]);
                    if (!saveUpdater)
                        net.setUpdater(null);

                    net.fit(ds);
                }
                INDArray finalParams = net.params().dup();
                // Do training on Spark with one executor, for 3 separate minibatches
                TrainingMaster tm = TestCompareParameterAveragingSparkVsSingleMachine.getTrainingMaster(1, miniBatchSize, saveUpdater);
                SparkDl4jMultiLayer sparkNet = new SparkDl4jMultiLayer(sc, TestCompareParameterAveragingSparkVsSingleMachine.getConf(12345, new RmsProp(0.5)), tm);
                sparkNet.setCollectTrainingStats(true);
                INDArray initialSparkParams = sparkNet.getNetwork().params().dup();
                for (int i = 0; i < (seeds.length); i++) {
                    List<DataSet> list = getOneDataSetAsIndividalExamples(miniBatchSize, seeds[i]);
                    JavaRDD<DataSet> rdd = sc.parallelize(list);
                    sparkNet.fit(rdd);
                }
                INDArray finalSparkParams = sparkNet.getNetwork().params().dup();
                Assert.assertEquals(initialParams, initialSparkParams);
                Assert.assertNotEquals(initialParams, finalParams);
                Assert.assertEquals(finalParams, finalSparkParams);
            } finally {
                sc.stop();
            }
        }
    }

    @Test
    public void testOneExecutorGraph() {
        // Idea: single worker/executor on Spark should give identical results to a single machine
        int miniBatchSize = 10;
        int nWorkers = 1;
        for (boolean saveUpdater : new boolean[]{ true, false }) {
            JavaSparkContext sc = TestCompareParameterAveragingSparkVsSingleMachine.getContext(nWorkers);
            try {
                // Do training locally, for 3 minibatches
                int[] seeds = new int[]{ 1, 2, 3 };
                ComputationGraph net = new ComputationGraph(TestCompareParameterAveragingSparkVsSingleMachine.getGraphConf(12345, new RmsProp(0.5)));
                net.init();
                INDArray initialParams = net.params().dup();
                for (int i = 0; i < (seeds.length); i++) {
                    DataSet ds = getOneDataSet(miniBatchSize, seeds[i]);
                    if (!saveUpdater)
                        net.setUpdater(null);

                    net.fit(ds);
                }
                INDArray finalParams = net.params().dup();
                // Do training on Spark with one executor, for 3 separate minibatches
                TrainingMaster tm = TestCompareParameterAveragingSparkVsSingleMachine.getTrainingMaster(1, miniBatchSize, saveUpdater);
                SparkComputationGraph sparkNet = new SparkComputationGraph(sc, TestCompareParameterAveragingSparkVsSingleMachine.getGraphConf(12345, new RmsProp(0.5)), tm);
                sparkNet.setCollectTrainingStats(true);
                INDArray initialSparkParams = sparkNet.getNetwork().params().dup();
                for (int i = 0; i < (seeds.length); i++) {
                    List<DataSet> list = getOneDataSetAsIndividalExamples(miniBatchSize, seeds[i]);
                    JavaRDD<DataSet> rdd = sc.parallelize(list);
                    sparkNet.fit(rdd);
                }
                INDArray finalSparkParams = sparkNet.getNetwork().params().dup();
                Assert.assertEquals(initialParams, initialSparkParams);
                Assert.assertNotEquals(initialParams, finalParams);
                Assert.assertEquals(finalParams, finalSparkParams);
            } finally {
                sc.stop();
            }
        }
    }

    @Test
    public void testAverageEveryStep() {
        // Idea: averaging every step with SGD (SGD updater + optimizer) is mathematically identical to doing the learning
        // on a single machine for synchronous distributed training
        // BUT: This is *ONLY* the case if all workers get an identical number of examples. This won't be the case if
        // we use RDD.randomSplit (which is what occurs if we use .fit(JavaRDD<DataSet> on a data set that needs splitting),
        // which might give a number of examples that isn't divisible by number of workers (like 39 examples on 4 executors)
        // This is also ONLY the case using SGD updater
        int miniBatchSizePerWorker = 10;
        int nWorkers = 4;
        for (boolean saveUpdater : new boolean[]{ true, false }) {
            JavaSparkContext sc = TestCompareParameterAveragingSparkVsSingleMachine.getContext(nWorkers);
            try {
                // Do training locally, for 3 minibatches
                int[] seeds = new int[]{ 1, 2, 3 };
                // CudaGridExecutioner executioner = (CudaGridExecutioner) Nd4j.getExecutioner();
                MultiLayerNetwork net = new MultiLayerNetwork(TestCompareParameterAveragingSparkVsSingleMachine.getConf(12345, new Sgd(0.5)));
                net.init();
                INDArray initialParams = net.params().dup();
                // executioner.addToWatchdog(initialParams, "initialParams");
                for (int i = 0; i < (seeds.length); i++) {
                    DataSet ds = getOneDataSet((miniBatchSizePerWorker * nWorkers), seeds[i]);
                    if (!saveUpdater)
                        net.setUpdater(null);

                    net.fit(ds);
                }
                INDArray finalParams = net.params().dup();
                // Do training on Spark with one executor, for 3 separate minibatches
                // TrainingMaster tm = getTrainingMaster(1, miniBatchSizePerWorker, saveUpdater);
                ParameterAveragingTrainingMaster tm = // .rddTrainingApproach(RDDTrainingApproach.Direct)
                new ParameterAveragingTrainingMaster.Builder(1).averagingFrequency(1).batchSizePerWorker(miniBatchSizePerWorker).saveUpdater(saveUpdater).workerPrefetchNumBatches(0).rddTrainingApproach(Export).build();
                SparkDl4jMultiLayer sparkNet = new SparkDl4jMultiLayer(sc, TestCompareParameterAveragingSparkVsSingleMachine.getConf(12345, new Sgd(0.5)), tm);
                sparkNet.setCollectTrainingStats(true);
                INDArray initialSparkParams = sparkNet.getNetwork().params().dup();
                // executioner.addToWatchdog(initialSparkParams, "initialSparkParams");
                for (int i = 0; i < (seeds.length); i++) {
                    List<DataSet> list = getOneDataSetAsIndividalExamples((miniBatchSizePerWorker * nWorkers), seeds[i]);
                    JavaRDD<DataSet> rdd = sc.parallelize(list);
                    sparkNet.fit(rdd);
                }
                System.out.println(sparkNet.getSparkTrainingStats().statsAsString());
                INDArray finalSparkParams = sparkNet.getNetwork().params().dup();
                System.out.println(("Initial (Local) params:       " + (Arrays.toString(initialParams.data().asFloat()))));
                System.out.println(("Initial (Spark) params:       " + (Arrays.toString(initialSparkParams.data().asFloat()))));
                System.out.println(("Final (Local) params: " + (Arrays.toString(finalParams.data().asFloat()))));
                System.out.println(("Final (Spark) params: " + (Arrays.toString(finalSparkParams.data().asFloat()))));
                Assert.assertEquals(initialParams, initialSparkParams);
                Assert.assertNotEquals(initialParams, finalParams);
                Assert.assertEquals(finalParams, finalSparkParams);
                double sparkScore = sparkNet.getScore();
                Assert.assertTrue((sparkScore > 0.0));
                Assert.assertEquals(net.score(), sparkScore, 0.001);
            } finally {
                sc.stop();
            }
        }
    }

    @Test
    public void testAverageEveryStepCNN() {
        // Idea: averaging every step with SGD (SGD updater + optimizer) is mathematically identical to doing the learning
        // on a single machine for synchronous distributed training
        // BUT: This is *ONLY* the case if all workers get an identical number of examples. This won't be the case if
        // we use RDD.randomSplit (which is what occurs if we use .fit(JavaRDD<DataSet> on a data set that needs splitting),
        // which might give a number of examples that isn't divisible by number of workers (like 39 examples on 4 executors)
        // This is also ONLY the case using SGD updater
        int miniBatchSizePerWorker = 10;
        int nWorkers = 4;
        for (boolean saveUpdater : new boolean[]{ true, false }) {
            JavaSparkContext sc = TestCompareParameterAveragingSparkVsSingleMachine.getContext(nWorkers);
            try {
                // Do training locally, for 3 minibatches
                int[] seeds = new int[]{ 1, 2, 3 };
                MultiLayerNetwork net = new MultiLayerNetwork(TestCompareParameterAveragingSparkVsSingleMachine.getConfCNN(12345, new Sgd(0.5)));
                net.init();
                INDArray initialParams = net.params().dup();
                for (int i = 0; i < (seeds.length); i++) {
                    DataSet ds = getOneDataSetCNN((miniBatchSizePerWorker * nWorkers), seeds[i]);
                    if (!saveUpdater)
                        net.setUpdater(null);

                    net.fit(ds);
                }
                INDArray finalParams = net.params().dup();
                // Do training on Spark with one executor, for 3 separate minibatches
                ParameterAveragingTrainingMaster tm = new ParameterAveragingTrainingMaster.Builder(1).averagingFrequency(1).batchSizePerWorker(miniBatchSizePerWorker).saveUpdater(saveUpdater).workerPrefetchNumBatches(0).rddTrainingApproach(Export).build();
                SparkDl4jMultiLayer sparkNet = new SparkDl4jMultiLayer(sc, TestCompareParameterAveragingSparkVsSingleMachine.getConfCNN(12345, new Sgd(0.5)), tm);
                sparkNet.setCollectTrainingStats(true);
                INDArray initialSparkParams = sparkNet.getNetwork().params().dup();
                for (int i = 0; i < (seeds.length); i++) {
                    List<DataSet> list = getOneDataSetAsIndividalExamplesCNN((miniBatchSizePerWorker * nWorkers), seeds[i]);
                    JavaRDD<DataSet> rdd = sc.parallelize(list);
                    sparkNet.fit(rdd);
                }
                System.out.println(sparkNet.getSparkTrainingStats().statsAsString());
                INDArray finalSparkParams = sparkNet.getNetwork().params().dup();
                System.out.println(("Initial (Local) params:       " + (Arrays.toString(initialParams.data().asFloat()))));
                System.out.println(("Initial (Spark) params:       " + (Arrays.toString(initialSparkParams.data().asFloat()))));
                System.out.println(("Final (Local) params: " + (Arrays.toString(finalParams.data().asFloat()))));
                System.out.println(("Final (Spark) params: " + (Arrays.toString(finalSparkParams.data().asFloat()))));
                Assert.assertArrayEquals(initialParams.data().asFloat(), initialSparkParams.data().asFloat(), 1.0E-8F);
                Assert.assertArrayEquals(finalParams.data().asFloat(), finalSparkParams.data().asFloat(), 1.0E-6F);
                double sparkScore = sparkNet.getScore();
                Assert.assertTrue((sparkScore > 0.0));
                Assert.assertEquals(net.score(), sparkScore, 0.001);
            } finally {
                sc.stop();
            }
        }
    }

    @Test
    public void testAverageEveryStepGraph() {
        // Idea: averaging every step with SGD (SGD updater + optimizer) is mathematically identical to doing the learning
        // on a single machine for synchronous distributed training
        // BUT: This is *ONLY* the case if all workers get an identical number of examples. This won't be the case if
        // we use RDD.randomSplit (which is what occurs if we use .fit(JavaRDD<DataSet> on a data set that needs splitting),
        // which might give a number of examples that isn't divisible by number of workers (like 39 examples on 4 executors)
        // This is also ONLY the case using SGD updater
        int miniBatchSizePerWorker = 10;
        int nWorkers = 4;
        for (boolean saveUpdater : new boolean[]{ true, false }) {
            JavaSparkContext sc = TestCompareParameterAveragingSparkVsSingleMachine.getContext(nWorkers);
            try {
                // Do training locally, for 3 minibatches
                int[] seeds = new int[]{ 1, 2, 3 };
                // CudaGridExecutioner executioner = (CudaGridExecutioner) Nd4j.getExecutioner();
                ComputationGraph net = new ComputationGraph(TestCompareParameterAveragingSparkVsSingleMachine.getGraphConf(12345, new Sgd(0.5)));
                net.init();
                INDArray initialParams = net.params().dup();
                // executioner.addToWatchdog(initialParams, "initialParams");
                for (int i = 0; i < (seeds.length); i++) {
                    DataSet ds = getOneDataSet((miniBatchSizePerWorker * nWorkers), seeds[i]);
                    if (!saveUpdater)
                        net.setUpdater(null);

                    net.fit(ds);
                }
                INDArray finalParams = net.params().dup();
                // executioner.addToWatchdog(finalParams, "finalParams");
                // Do training on Spark with one executor, for 3 separate minibatches
                TrainingMaster tm = TestCompareParameterAveragingSparkVsSingleMachine.getTrainingMaster(1, miniBatchSizePerWorker, saveUpdater);
                SparkComputationGraph sparkNet = new SparkComputationGraph(sc, TestCompareParameterAveragingSparkVsSingleMachine.getGraphConf(12345, new Sgd(0.5)), tm);
                sparkNet.setCollectTrainingStats(true);
                INDArray initialSparkParams = sparkNet.getNetwork().params().dup();
                // executioner.addToWatchdog(initialSparkParams, "initialSparkParams");
                for (int i = 0; i < (seeds.length); i++) {
                    List<DataSet> list = getOneDataSetAsIndividalExamples((miniBatchSizePerWorker * nWorkers), seeds[i]);
                    JavaRDD<DataSet> rdd = sc.parallelize(list);
                    sparkNet.fit(rdd);
                }
                System.out.println(sparkNet.getSparkTrainingStats().statsAsString());
                INDArray finalSparkParams = sparkNet.getNetwork().params().dup();
                // executioner.addToWatchdog(finalSparkParams, "finalSparkParams");
                float[] fp = finalParams.data().asFloat();
                float[] fps = finalSparkParams.data().asFloat();
                System.out.println(("Initial (Local) params:       " + (Arrays.toString(initialParams.data().asFloat()))));
                System.out.println(("Initial (Spark) params:       " + (Arrays.toString(initialSparkParams.data().asFloat()))));
                System.out.println(("Final (Local) params: " + (Arrays.toString(fp))));
                System.out.println(("Final (Spark) params: " + (Arrays.toString(fps))));
                Assert.assertEquals(initialParams, initialSparkParams);
                Assert.assertNotEquals(initialParams, finalParams);
                Assert.assertArrayEquals(fp, fps, 1.0E-5F);
                double sparkScore = sparkNet.getScore();
                Assert.assertTrue((sparkScore > 0.0));
                Assert.assertEquals(net.score(), sparkScore, 0.001);
            } finally {
                sc.stop();
            }
        }
    }

    @Test
    public void testAverageEveryStepGraphCNN() {
        // Idea: averaging every step with SGD (SGD updater + optimizer) is mathematically identical to doing the learning
        // on a single machine for synchronous distributed training
        // BUT: This is *ONLY* the case if all workers get an identical number of examples. This won't be the case if
        // we use RDD.randomSplit (which is what occurs if we use .fit(JavaRDD<DataSet> on a data set that needs splitting),
        // which might give a number of examples that isn't divisible by number of workers (like 39 examples on 4 executors)
        // This is also ONLY the case using SGD updater
        int miniBatchSizePerWorker = 10;
        int nWorkers = 4;
        for (boolean saveUpdater : new boolean[]{ true, false }) {
            JavaSparkContext sc = TestCompareParameterAveragingSparkVsSingleMachine.getContext(nWorkers);
            try {
                // Do training locally, for 3 minibatches
                int[] seeds = new int[]{ 1, 2, 3 };
                ComputationGraph net = new ComputationGraph(TestCompareParameterAveragingSparkVsSingleMachine.getGraphConfCNN(12345, new Sgd(0.5)));
                net.init();
                INDArray initialParams = net.params().dup();
                for (int i = 0; i < (seeds.length); i++) {
                    DataSet ds = getOneDataSetCNN((miniBatchSizePerWorker * nWorkers), seeds[i]);
                    if (!saveUpdater)
                        net.setUpdater(null);

                    net.fit(ds);
                }
                INDArray finalParams = net.params().dup();
                // Do training on Spark with one executor, for 3 separate minibatches
                TrainingMaster tm = TestCompareParameterAveragingSparkVsSingleMachine.getTrainingMaster(1, miniBatchSizePerWorker, saveUpdater);
                SparkComputationGraph sparkNet = new SparkComputationGraph(sc, TestCompareParameterAveragingSparkVsSingleMachine.getGraphConfCNN(12345, new Sgd(0.5)), tm);
                sparkNet.setCollectTrainingStats(true);
                INDArray initialSparkParams = sparkNet.getNetwork().params().dup();
                for (int i = 0; i < (seeds.length); i++) {
                    List<DataSet> list = getOneDataSetAsIndividalExamplesCNN((miniBatchSizePerWorker * nWorkers), seeds[i]);
                    JavaRDD<DataSet> rdd = sc.parallelize(list);
                    sparkNet.fit(rdd);
                }
                System.out.println(sparkNet.getSparkTrainingStats().statsAsString());
                INDArray finalSparkParams = sparkNet.getNetwork().params().dup();
                System.out.println(("Initial (Local) params:  " + (Arrays.toString(initialParams.data().asFloat()))));
                System.out.println(("Initial (Spark) params:  " + (Arrays.toString(initialSparkParams.data().asFloat()))));
                System.out.println(("Final (Local) params:    " + (Arrays.toString(finalParams.data().asFloat()))));
                System.out.println(("Final (Spark) params:    " + (Arrays.toString(finalSparkParams.data().asFloat()))));
                Assert.assertArrayEquals(initialParams.data().asFloat(), initialSparkParams.data().asFloat(), 1.0E-8F);
                Assert.assertArrayEquals(finalParams.data().asFloat(), finalSparkParams.data().asFloat(), 1.0E-6F);
                double sparkScore = sparkNet.getScore();
                Assert.assertTrue((sparkScore > 0.0));
                Assert.assertEquals(net.score(), sparkScore, 0.001);
            } finally {
                sc.stop();
            }
        }
    }
}

