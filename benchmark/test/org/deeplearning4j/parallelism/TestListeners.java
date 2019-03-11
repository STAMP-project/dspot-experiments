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
package org.deeplearning4j.parallelism;


import Activation.TANH;
import MultiLayerConfiguration.Builder;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.api.storage.StatsStorageRouter;
import org.deeplearning4j.api.storage.listener.RoutingIterationListener;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.BaseTrainingListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;


/**
 * Created by Alex on 23/03/2017.
 */
public class TestListeners {
    @Test
    public void testListeners() {
        TestListeners.TestListener.clearCounts();
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().list().layer(0, nIn(10).nOut(10).activation(TANH).build());
        MultiLayerConfiguration conf = builder.build();
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        TestListeners.testListenersForModel(model, Collections.singletonList(new TestListeners.TestListener()));
    }

    @Test
    public void testListenersGraph() {
        TestListeners.TestListener.clearCounts();
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").addLayer("0", nIn(10).nOut(10).activation(TANH).build(), "in").setOutputs("0").build();
        ComputationGraph model = new ComputationGraph(conf);
        model.init();
        TestListeners.testListenersForModel(model, Collections.singletonList(new TestListeners.TestListener()));
    }

    @Test
    public void testListenersViaModel() {
        TestListeners.TestListener.clearCounts();
        MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder().list().layer(0, nIn(10).nOut(10).activation(TANH).build());
        MultiLayerConfiguration conf = builder.build();
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        StatsStorage ss = new InMemoryStatsStorage();
        model.setListeners(new TestListeners.TestListener(), new org.deeplearning4j.ui.stats.StatsListener(ss));
        TestListeners.testListenersForModel(model, null);
        Assert.assertEquals(1, ss.listSessionIDs().size());
        Assert.assertEquals(2, ss.listWorkerIDsForSession(ss.listSessionIDs().get(0)).size());
    }

    @Test
    public void testListenersViaModelGraph() {
        TestListeners.TestListener.clearCounts();
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder().graphBuilder().addInputs("in").addLayer("0", nIn(10).nOut(10).activation(TANH).build(), "in").setOutputs("0").build();
        ComputationGraph model = new ComputationGraph(conf);
        model.init();
        StatsStorage ss = new InMemoryStatsStorage();
        model.setListeners(new TestListeners.TestListener(), new org.deeplearning4j.ui.stats.StatsListener(ss));
        TestListeners.testListenersForModel(model, null);
        Assert.assertEquals(1, ss.listSessionIDs().size());
        Assert.assertEquals(2, ss.listWorkerIDsForSession(ss.listSessionIDs().get(0)).size());
    }

    private static class TestListener extends BaseTrainingListener implements RoutingIterationListener {
        private static final AtomicInteger forwardPassCount = new AtomicInteger();

        private static final AtomicInteger backwardPassCount = new AtomicInteger();

        private static final AtomicInteger instanceCount = new AtomicInteger();

        private static final Set<String> workerIDs = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

        private static final Set<String> sessionIDs = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

        public static void clearCounts() {
            TestListeners.TestListener.forwardPassCount.set(0);
            TestListeners.TestListener.backwardPassCount.set(0);
            TestListeners.TestListener.instanceCount.set(0);
            TestListeners.TestListener.workerIDs.clear();
            TestListeners.TestListener.sessionIDs.clear();
        }

        public TestListener() {
            TestListeners.TestListener.instanceCount.incrementAndGet();
        }

        @Override
        public void onEpochStart(Model model) {
        }

        @Override
        public void onEpochEnd(Model model) {
        }

        @Override
        public void onForwardPass(Model model, List<INDArray> activations) {
            TestListeners.TestListener.forwardPassCount.incrementAndGet();
        }

        @Override
        public void onForwardPass(Model model, Map<String, INDArray> activations) {
            TestListeners.TestListener.forwardPassCount.incrementAndGet();
        }

        @Override
        public void onGradientCalculation(Model model) {
        }

        @Override
        public void onBackwardPass(Model model) {
            TestListeners.TestListener.backwardPassCount.getAndIncrement();
        }

        @Override
        public void setStorageRouter(StatsStorageRouter router) {
        }

        @Override
        public StatsStorageRouter getStorageRouter() {
            return null;
        }

        @Override
        public void setWorkerID(String workerID) {
            TestListeners.TestListener.workerIDs.add(workerID);
        }

        @Override
        public String getWorkerID() {
            return null;
        }

        @Override
        public void setSessionID(String sessionID) {
            TestListeners.TestListener.sessionIDs.add(sessionID);
        }

        @Override
        public String getSessionID() {
            return "session_id";
        }

        @Override
        public RoutingIterationListener clone() {
            return new TestListeners.TestListener();
        }

        @Override
        public void iterationDone(Model model, int iteration, int epoch) {
        }
    }
}

