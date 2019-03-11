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
import LossFunctions.LossFunction.MCXENT;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.BaseDL4JTest;
import org.deeplearning4j.api.storage.StatsStorageRouter;
import org.deeplearning4j.api.storage.listener.RoutingIterationListener;
import org.deeplearning4j.datasets.iterator.impl.IrisDataSetIterator;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.AutoEncoder;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.BaseTrainingListener;
import org.deeplearning4j.optimize.api.TrainingListener;
import org.deeplearning4j.optimize.listeners.CheckpointListener;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.optimize.listeners.TimeIterationListener;
import org.deeplearning4j.optimize.solvers.BaseOptimizer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.primitives.Triple;


/**
 * Created by Alex on 01/01/2017.
 */
@Slf4j
public class TestListeners extends BaseDL4JTest {
    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testSettingListenersUnsupervised() {
        // Pretrain layers should get copies of the listeners, in addition to the
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(0, new AutoEncoder.Builder().nIn(10).nOut(10).build()).layer(1, new VariationalAutoencoder.Builder().nIn(10).nOut(10).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(), new TestListeners.TestRoutingListener());
        for (Layer l : net.getLayers()) {
            Collection<TrainingListener> layerListeners = l.getListeners();
            Assert.assertEquals(l.getClass().toString(), 2, layerListeners.size());
            TrainingListener[] lArr = layerListeners.toArray(new TrainingListener[2]);
            Assert.assertTrue(((lArr[0]) instanceof ScoreIterationListener));
            Assert.assertTrue(((lArr[1]) instanceof TestListeners.TestRoutingListener));
        }
        Collection<TrainingListener> netListeners = net.getListeners();
        Assert.assertEquals(2, netListeners.size());
        TrainingListener[] lArr = netListeners.toArray(new TrainingListener[2]);
        Assert.assertTrue(((lArr[0]) instanceof ScoreIterationListener));
        Assert.assertTrue(((lArr[1]) instanceof TestListeners.TestRoutingListener));
        ComputationGraphConfiguration gConf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").addLayer("0", new AutoEncoder.Builder().nIn(10).nOut(10).build(), "in").addLayer("1", new VariationalAutoencoder.Builder().nIn(10).nOut(10).build(), "0").setOutputs("1").build();
        ComputationGraph cg = new ComputationGraph(gConf);
        cg.init();
        cg.setListeners(new ScoreIterationListener(), new TestListeners.TestRoutingListener());
        for (Layer l : cg.getLayers()) {
            Collection<TrainingListener> layerListeners = l.getListeners();
            Assert.assertEquals(2, layerListeners.size());
            lArr = layerListeners.toArray(new TrainingListener[2]);
            Assert.assertTrue(((lArr[0]) instanceof ScoreIterationListener));
            Assert.assertTrue(((lArr[1]) instanceof TestListeners.TestRoutingListener));
        }
        netListeners = cg.getListeners();
        Assert.assertEquals(2, netListeners.size());
        lArr = netListeners.toArray(new TrainingListener[2]);
        Assert.assertTrue(((lArr[0]) instanceof ScoreIterationListener));
        Assert.assertTrue(((lArr[1]) instanceof TestListeners.TestRoutingListener));
    }

    private static class TestRoutingListener extends BaseTrainingListener implements RoutingIterationListener {
        @Override
        public void setStorageRouter(StatsStorageRouter router) {
        }

        @Override
        public StatsStorageRouter getStorageRouter() {
            return null;
        }

        @Override
        public void setWorkerID(String workerID) {
        }

        @Override
        public String getWorkerID() {
            return null;
        }

        @Override
        public void setSessionID(String sessionID) {
        }

        @Override
        public String getSessionID() {
            return null;
        }

        @Override
        public RoutingIterationListener clone() {
            return null;
        }

        @Override
        public void iterationDone(Model model, int iteration, int epoch) {
        }
    }

    @Test
    public void testListenerSerialization() throws Exception {
        // Note: not all listeners are (or should be) serializable. But some should be - for Spark etc
        List<TrainingListener> listeners = new ArrayList<>();
        listeners.add(new ScoreIterationListener());
        listeners.add(new PerformanceListener(1, true, true));
        listeners.add(new TimeIterationListener(10000));
        listeners.add(new org.deeplearning4j.optimize.listeners.ComposableIterationListener(new ScoreIterationListener(), new PerformanceListener(1, true, true)));
        listeners.add(new CheckpointListener.Builder(tempDir.newFolder()).keepAll().saveEveryNIterations(3).build());// Doesn't usually need to be serialized, but no reason it can't be...

        DataSetIterator iter = new IrisDataSetIterator(10, 150);
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(listeners);
        net.fit(iter);
        List<TrainingListener> listeners2 = new ArrayList<>();
        for (TrainingListener il : listeners) {
            log.info("------------------");
            log.info("Testing listener: {}", il);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(il);
            byte[] bytes = baos.toByteArray();
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            TrainingListener il2 = ((TrainingListener) (ois.readObject()));
            listeners2.add(il2);
        }
        net.setListeners(listeners2);
        net.fit(iter);
    }

    @Test
    public void testListenerCalls() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().list().layer(new OutputLayer.Builder().nIn(4).nOut(3).activation(SOFTMAX).lossFunction(MCXENT).build()).build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        TestListeners.TestListener tl = new TestListeners.TestListener();
        net.setListeners(tl);
        DataSetIterator irisIter = new IrisDataSetIterator(50, 150);
        net.fit(irisIter, 2);
        List<Triple<TestListeners.Call, Integer, Integer>> exp = new ArrayList<>();
        exp.add(new Triple(TestListeners.Call.EPOCH_START, 0, 0));
        exp.add(new Triple(TestListeners.Call.ON_FWD, 0, 0));
        exp.add(new Triple(TestListeners.Call.ON_BWD, 0, 0));
        exp.add(new Triple(TestListeners.Call.ON_GRAD, 0, 0));
        exp.add(new Triple(TestListeners.Call.ITER_DONE, 0, 0));
        exp.add(new Triple(TestListeners.Call.ON_FWD, 1, 0));
        exp.add(new Triple(TestListeners.Call.ON_BWD, 1, 0));
        exp.add(new Triple(TestListeners.Call.ON_GRAD, 1, 0));
        exp.add(new Triple(TestListeners.Call.ITER_DONE, 1, 0));
        exp.add(new Triple(TestListeners.Call.ON_FWD, 2, 0));
        exp.add(new Triple(TestListeners.Call.ON_BWD, 2, 0));
        exp.add(new Triple(TestListeners.Call.ON_GRAD, 2, 0));
        exp.add(new Triple(TestListeners.Call.ITER_DONE, 2, 0));
        exp.add(new Triple(TestListeners.Call.EPOCH_END, 3, 0));// Post updating iter count, pre update epoch count

        exp.add(new Triple(TestListeners.Call.EPOCH_START, 3, 1));
        exp.add(new Triple(TestListeners.Call.ON_FWD, 3, 1));
        exp.add(new Triple(TestListeners.Call.ON_BWD, 3, 1));
        exp.add(new Triple(TestListeners.Call.ON_GRAD, 3, 1));
        exp.add(new Triple(TestListeners.Call.ITER_DONE, 3, 1));
        exp.add(new Triple(TestListeners.Call.ON_FWD, 4, 1));
        exp.add(new Triple(TestListeners.Call.ON_BWD, 4, 1));
        exp.add(new Triple(TestListeners.Call.ON_GRAD, 4, 1));
        exp.add(new Triple(TestListeners.Call.ITER_DONE, 4, 1));
        exp.add(new Triple(TestListeners.Call.ON_FWD, 5, 1));
        exp.add(new Triple(TestListeners.Call.ON_BWD, 5, 1));
        exp.add(new Triple(TestListeners.Call.ON_GRAD, 5, 1));
        exp.add(new Triple(TestListeners.Call.ITER_DONE, 5, 1));
        exp.add(new Triple(TestListeners.Call.EPOCH_END, 6, 1));
        Assert.assertEquals(exp, getCalls());
        tl = new TestListeners.TestListener();
        ComputationGraph cg = net.toComputationGraph();
        cg.setListeners(tl);
        cg.fit(irisIter, 2);
        Assert.assertEquals(exp, getCalls());
    }

    private static enum Call {

        ITER_DONE,
        EPOCH_START,
        EPOCH_END,
        ON_FWD,
        ON_GRAD,
        ON_BWD;}

    @Data
    private static class TestListener implements TrainingListener {
        private List<Triple<TestListeners.Call, Integer, Integer>> calls = new ArrayList<>();

        @Override
        public void iterationDone(Model model, int iteration, int epoch) {
            calls.add(new Triple(TestListeners.Call.ITER_DONE, iteration, epoch));
        }

        @Override
        public void onEpochStart(Model model) {
            calls.add(new Triple(TestListeners.Call.EPOCH_START, BaseOptimizer.getIterationCount(model), BaseOptimizer.getEpochCount(model)));
        }

        @Override
        public void onEpochEnd(Model model) {
            calls.add(new Triple(TestListeners.Call.EPOCH_END, BaseOptimizer.getIterationCount(model), BaseOptimizer.getEpochCount(model)));
        }

        @Override
        public void onForwardPass(Model model, List<INDArray> activations) {
            calls.add(new Triple(TestListeners.Call.ON_FWD, BaseOptimizer.getIterationCount(model), BaseOptimizer.getEpochCount(model)));
        }

        @Override
        public void onForwardPass(Model model, Map<String, INDArray> activations) {
            calls.add(new Triple(TestListeners.Call.ON_FWD, BaseOptimizer.getIterationCount(model), BaseOptimizer.getEpochCount(model)));
        }

        @Override
        public void onGradientCalculation(Model model) {
            calls.add(new Triple(TestListeners.Call.ON_GRAD, BaseOptimizer.getIterationCount(model), BaseOptimizer.getEpochCount(model)));
        }

        @Override
        public void onBackwardPass(Model model) {
            calls.add(new Triple(TestListeners.Call.ON_BWD, BaseOptimizer.getIterationCount(model), BaseOptimizer.getEpochCount(model)));
        }
    }
}

