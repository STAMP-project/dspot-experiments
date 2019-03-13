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
package org.deeplearning4j.spark.parameterserver.train;


import Activation.SOFTMAX;
import Activation.TANH;
import LossFunctions.LossFunction.MCXENT;
import MeshBuildMode.PLAIN;
import WeightInit.XAVIER;
import java.io.File;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.JavaRDD;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.BaseTrainingListener;
import org.deeplearning4j.optimize.solvers.accumulation.encoding.ThresholdAlgorithm;
import org.deeplearning4j.optimize.solvers.accumulation.encoding.threshold.AdaptiveThresholdAlgorithm;
import org.deeplearning4j.spark.api.RDDTrainingApproach;
import org.deeplearning4j.spark.api.TrainingMaster;
import org.deeplearning4j.spark.impl.graph.SparkComputationGraph;
import org.deeplearning4j.spark.impl.multilayer.SparkDl4jMultiLayer;
import org.deeplearning4j.spark.parameterserver.BaseSparkTest;
import org.deeplearning4j.spark.parameterserver.training.SharedTrainingMaster;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.AMSGrad;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.parameterserver.distributed.conf.VoidConfiguration;


@Slf4j
public class GradientSharingTrainingTest extends BaseSparkTest {
    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    @Test
    public void trainSanityCheck() throws Exception {
        INDArray last = null;
        INDArray lastDup = null;
        for (String s : new String[]{ "paths", "direct", "export" }) {
            System.out.println("--------------------------------------------------------------------------------------------------------------");
            log.info("Starting: {}", s);
            boolean isPaths = "paths".equals(s);
            RDDTrainingApproach rddTrainingApproach;
            switch (s) {
                case "direct" :
                    rddTrainingApproach = RDDTrainingApproach.Direct;
                    break;
                case "export" :
                    rddTrainingApproach = RDDTrainingApproach.Export;
                    break;
                case "paths" :
                    rddTrainingApproach = RDDTrainingApproach.Direct;// Actualy not used for fitPaths

                    break;
                default :
                    throw new RuntimeException();
            }
            File temp = testDir.newFolder();
            // TODO this probably won't work everywhere...
            String controller = Inet4Address.getLocalHost().getHostAddress();
            String networkMask = ((controller.substring(0, controller.lastIndexOf('.'))) + ".0") + "/16";
            VoidConfiguration voidConfiguration = // everyone is connected to the master
            // Local network mask
            // Should be open for IN/OUT communications on all Spark nodes
            VoidConfiguration.builder().unicastPort(40123).networkMask(networkMask).controllerAddress(controller).meshBuildMode(PLAIN).build();
            TrainingMaster tm = // Workers per node
            // Minibatch size for each worker
            rngSeed(12345).collectTrainingStats(false).batchSizePerWorker(16).workersPerNode(2).rddTrainingApproach(rddTrainingApproach).exportDirectory(("file:///" + (temp.getAbsolutePath().replaceAll("\\\\", "/")))).build();
            ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).updater(new AMSGrad(0.1)).graphBuilder().addInputs("in").layer("out", new OutputLayer.Builder().nIn(784).nOut(10).activation(SOFTMAX).lossFunction(MCXENT).build(), "in").setOutputs("out").build();
            SparkComputationGraph sparkNet = new SparkComputationGraph(sc, conf, tm);
            sparkNet.setCollectTrainingStats(tm.getIsCollectTrainingStats());
            System.out.println(Arrays.toString(sparkNet.getNetwork().params().get(NDArrayIndex.point(0), NDArrayIndex.interval(0, 256)).dup().data().asFloat()));
            File f = testDir.newFolder();
            DataSetIterator iter = new MnistDataSetIterator(16, true, 12345);
            int count = 0;
            List<String> paths = new ArrayList<>();
            List<DataSet> ds = new ArrayList<>();
            while ((iter.hasNext()) && ((count++) < 8)) {
                DataSet d = iter.next();
                if (isPaths) {
                    File out = new File(f, (count + ".bin"));
                    d.save(out);
                    String path = "file:///" + (out.getAbsolutePath().replaceAll("\\\\", "/"));
                    paths.add(path);
                }
                ds.add(d);
            } 
            int numIter = 1;
            double[] acc = new double[numIter + 1];
            for (int i = 0; i < numIter; i++) {
                // Check accuracy before:
                DataSetIterator testIter = new org.deeplearning4j.datasets.iterator.EarlyTerminationDataSetIterator(new MnistDataSetIterator(32, false, 12345), 10);
                Evaluation eBefore = sparkNet.getNetwork().evaluate(testIter);
                INDArray paramsBefore = sparkNet.getNetwork().params().dup();
                ComputationGraph after;
                switch (s) {
                    case "direct" :
                    case "export" :
                        JavaRDD<DataSet> dsRDD = sc.parallelize(ds);
                        after = sparkNet.fit(dsRDD);
                        break;
                    case "paths" :
                        JavaRDD<String> pathRdd = sc.parallelize(paths);
                        after = sparkNet.fitPaths(pathRdd);
                        break;
                    default :
                        throw new RuntimeException();
                }
                INDArray paramsAfter = after.params();
                System.out.println(Arrays.toString(paramsBefore.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, 256)).dup().data().asFloat()));
                System.out.println(Arrays.toString(paramsAfter.get(NDArrayIndex.point(0), NDArrayIndex.interval(0, 256)).dup().data().asFloat()));
                System.out.println(Arrays.toString(Transforms.abs(paramsAfter.sub(paramsBefore)).get(NDArrayIndex.point(0), NDArrayIndex.interval(0, 256)).dup().data().asFloat()));
                Assert.assertNotEquals(paramsBefore, paramsAfter);
                testIter = new org.deeplearning4j.datasets.iterator.EarlyTerminationDataSetIterator(new MnistDataSetIterator(32, false, 12345), 10);
                Evaluation eAfter = after.evaluate(testIter);
                double accAfter = eAfter.accuracy();
                double accBefore = eBefore.accuracy();
                Assert.assertTrue(((("after: " + accAfter) + ", before=") + accBefore), (accAfter >= (accBefore + 0.005)));
                if (i == 0) {
                    acc[0] = eBefore.accuracy();
                }
                acc[(i + 1)] = eAfter.accuracy();
            }
            log.info("Accuracies: {}", Arrays.toString(acc));
            last = sparkNet.getNetwork().params();
            lastDup = last.dup();
        }
    }

    @Test
    public void differentNetsTrainingTest() throws Exception {
        int batch = 3;
        File temp = testDir.newFolder();
        DataSet ds = new IrisDataSetIterator(150, 150).next();
        List<DataSet> list = ds.asList();
        Collections.shuffle(list, new Random(12345));
        int pos = 0;
        int dsCount = 0;
        while (pos < (list.size())) {
            List<DataSet> l2 = new ArrayList<>();
            for (int i = 0; (i < 3) && (pos < (list.size())); i++) {
                l2.add(list.get((pos++)));
            }
            DataSet d = DataSet.merge(l2);
            File f = new File(temp, ((dsCount++) + ".bin"));
            d.save(f);
        } 
        INDArray last = null;
        INDArray lastDup = null;
        for (int i = 0; i < 2; i++) {
            System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
            log.info("Starting: {}", i);
            MultiLayerConfiguration conf;
            if (i == 0) {
                conf = new NeuralNetConfiguration.Builder().weightInit(XAVIER).seed(12345).list().layer(new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
            } else {
                conf = new NeuralNetConfiguration.Builder().weightInit(XAVIER).seed(12345).list().layer(new DenseLayer.Builder().nIn(4).nOut(4).activation(TANH).build()).layer(new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
            }
            MultiLayerNetwork net = new MultiLayerNetwork(conf);
            net.init();
            // TODO this probably won't work everywhere...
            String controller = Inet4Address.getLocalHost().getHostAddress();
            String networkMask = ((controller.substring(0, controller.lastIndexOf('.'))) + ".0") + "/16";
            VoidConfiguration voidConfiguration = // Local network mask
            // Should be open for IN/OUT communications on all Spark nodes
            VoidConfiguration.builder().unicastPort(40123).networkMask(networkMask).controllerAddress(controller).build();
            TrainingMaster tm = // Workers per node
            // Minibatch size for each worker
            rngSeed(12345).collectTrainingStats(false).batchSizePerWorker(batch).workersPerNode(2).build();
            SparkDl4jMultiLayer sparkNet = new SparkDl4jMultiLayer(sc, net, tm);
            System.out.println(Arrays.toString(sparkNet.getNetwork().params().get(NDArrayIndex.point(0), NDArrayIndex.interval(0, 256)).dup().data().asFloat()));
            String fitPath = "file:///" + (temp.getAbsolutePath().replaceAll("\\\\", "/"));
            INDArray paramsBefore = net.params().dup();
            for (int j = 0; j < 3; j++) {
                sparkNet.fit(fitPath);
            }
            INDArray paramsAfter = net.params();
            Assert.assertNotEquals(paramsBefore, paramsAfter);
            // Also check we don't have any issues
            if (i == 0) {
                last = sparkNet.getNetwork().params();
                lastDup = last.dup();
            } else {
                Assert.assertEquals(lastDup, last);
            }
        }
    }

    @Test
    public void testEpochUpdating() throws Exception {
        // Ensure that epoch counter is incremented properly on the workers
        File temp = testDir.newFolder();
        // TODO this probably won't work everywhere...
        String controller = Inet4Address.getLocalHost().getHostAddress();
        String networkMask = ((controller.substring(0, controller.lastIndexOf('.'))) + ".0") + "/16";
        VoidConfiguration voidConfiguration = // everyone is connected to the master
        // Local network mask
        // Should be open for IN/OUT communications on all Spark nodes
        VoidConfiguration.builder().unicastPort(40123).networkMask(networkMask).controllerAddress(controller).meshBuildMode(PLAIN).build();
        SharedTrainingMaster tm = // Workers per node
        // Minibatch size for each worker
        rngSeed(12345).collectTrainingStats(false).batchSizePerWorker(16).workersPerNode(2).exportDirectory(("file:///" + (temp.getAbsolutePath().replaceAll("\\\\", "/")))).build();
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().seed(12345).updater(new AMSGrad(0.1)).graphBuilder().addInputs("in").layer("out", new OutputLayer.Builder().nIn(784).nOut(10).activation(SOFTMAX).lossFunction(MCXENT).build(), "in").setOutputs("out").build();
        SparkComputationGraph sparkNet = new SparkComputationGraph(sc, conf, tm);
        sparkNet.setListeners(new GradientSharingTrainingTest.TestListener());
        DataSetIterator iter = new MnistDataSetIterator(16, true, 12345);
        int count = 0;
        List<String> paths = new ArrayList<>();
        List<DataSet> ds = new ArrayList<>();
        File f = testDir.newFolder();
        while ((iter.hasNext()) && ((count++) < 8)) {
            DataSet d = iter.next();
            File out = new File(f, (count + ".bin"));
            d.save(out);
            String path = "file:///" + (out.getAbsolutePath().replaceAll("\\\\", "/"));
            paths.add(path);
            ds.add(d);
        } 
        JavaRDD<String> pathRdd = sc.parallelize(paths);
        for (int i = 0; i < 3; i++) {
            ThresholdAlgorithm ta = tm.getThresholdAlgorithm();
            sparkNet.fitPaths(pathRdd);
            // Check also that threshold algorithm was updated/averaged
            ThresholdAlgorithm taAfter = tm.getThresholdAlgorithm();
            Assert.assertTrue("Threshold algorithm should have been updated with different instance after averaging", (ta != taAfter));
            AdaptiveThresholdAlgorithm ataAfter = ((AdaptiveThresholdAlgorithm) (taAfter));
            Assert.assertFalse(Double.isNaN(ataAfter.getLastSparsity()));
            Assert.assertFalse(Double.isNaN(ataAfter.getLastThreshold()));
        }
        Set<Integer> expectedEpochs = new HashSet<>(Arrays.asList(0, 1, 2));
        Assert.assertEquals(expectedEpochs, GradientSharingTrainingTest.TestListener.epochs);
    }

    private static class TestListener extends BaseTrainingListener implements Serializable {
        private static final Set<Integer> iterations = Collections.newSetFromMap(new ConcurrentHashMap<>());

        private static final Set<Integer> epochs = Collections.newSetFromMap(new ConcurrentHashMap<>());

        @Override
        public void iterationDone(Model model, int iteration, int epoch) {
            GradientSharingTrainingTest.TestListener.iterations.add(iteration);
            GradientSharingTrainingTest.TestListener.epochs.add(epoch);
        }
    }
}

