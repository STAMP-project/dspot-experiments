/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.graph;


import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests that DiGraph after concurrent access is in consistent state.
 *
 * <p>Inconsistent state means that any of edges is half added.
 */
@RunWith(JUnit4.class)
public class DigraphConcurrentTest {
    private static final Random RANDOM = new Random();

    // need to have contention.
    private static final int THREAD_COUNT = (Runtime.getRuntime().availableProcessors()) * 3;

    /**
     * Created 100_000 nodes randomly. Adds 10 outgoing and 10 incoming edges for every node.
     */
    @Test
    public void testValidStateOfGraphAfterConcurrentAccess() throws InterruptedException {
        Digraph<Integer> digraph = new Digraph();
        final int numberOfNodes = 100000;
        Runnable workerBoth10 = new DigraphConcurrentTest.CreateNodeWorker(digraph, 10, 10, new AtomicInteger());
        runWorkers(numberOfNodes, workerBoth10);
        assertThat(digraph.getNodeCount()).isEqualTo(numberOfNodes);
        assertGraphIsInConsistentState(digraph);
    }

    /**
     * Created 10_000 nodes randomly. Adds different number outgoing and incoming edges for every
     * node.
     */
    @Test
    public void testValidStateOfGraphAfterConcurrentAccessWithVariertyOfNodes() throws InterruptedException {
        Digraph<Integer> digraph = new Digraph();
        final int numberOfNodes = 50000;
        final AtomicInteger idGenerator = new AtomicInteger();
        Runnable workerBoth3 = new DigraphConcurrentTest.CreateNodeWorker(digraph, 3, 3, idGenerator);
        Runnable workerBoth10 = new DigraphConcurrentTest.CreateNodeWorker(digraph, 10, 10, idGenerator);
        Runnable workerBoth100 = new DigraphConcurrentTest.CreateNodeWorker(digraph, 100, 100, idGenerator);
        Runnable workerOutgoing20 = new DigraphConcurrentTest.CreateNodeWorker(digraph, 20, 0, idGenerator);
        Runnable workerIncoming20 = new DigraphConcurrentTest.CreateNodeWorker(digraph, 0, 20, idGenerator);
        // have already 5 workers
        runWorkers((numberOfNodes / 5), workerBoth3, workerBoth10, workerBoth100, workerIncoming20, workerOutgoing20);
        assertThat(digraph.getNodeCount()).isEqualTo(numberOfNodes);
        assertGraphIsInConsistentState(digraph);
    }

    /**
     * Creates 20_000 nodes. Then iterate over node and for every node does: Creates 100 tasks: 50
     * tasks for adding 10 outgoing edges and 50 tasks for 10 incoming edges per each tasks. Then
     * executes all tasks in parallel in high contention. Only after all tasks for this node have
     * finished, switch to next node. this behaviour introduces very high contention around one single
     * node.
     */
    @Test
    public void testAddEdgeWithHighContention() throws InterruptedException, ExecutionException, TimeoutException {
        final int numberOfNodes = 20000;
        final int edgePerNode = 1000;
        Digraph<Integer> digraph = new Digraph();
        DigraphConcurrentTest.CreateNodeWorker workerZeroEdges = new DigraphConcurrentTest.CreateNodeWorker(digraph, 0, 0, new AtomicInteger());
        runWorkers(numberOfNodes, workerZeroEdges);
        assertThat(digraph.getNodeCount()).isEqualTo(numberOfNodes);
        ExecutorService executorService = Executors.newFixedThreadPool(DigraphConcurrentTest.THREAD_COUNT);
        for (Node<Integer> node : digraph.getNodes()) {
            // TODO(dbabkin): think about moving this interrupt callback logic to common library or make
            // research, may be one exists already in any open source project.
            AtomicBoolean isTestInterrupted = new AtomicBoolean(false);
            Runnable interruptedCallBack = () -> {
                isTestInterrupted.set(true);
                executorService.shutdownNow();
            };
            CountDownLatch countDownLatch = new CountDownLatch(1);
            List<Future<?>> futures = new ArrayList<>();
            // fork 100 tasks executed in parallel in high contention for one node:
            // 100 =  50 tasks for adding 10 outgoing edges and 50 tasks for adding 10 incoming edges
            for (int i = 0; i < (edgePerNode / 20); i++) {
                // add 10 outgoing edges
                Runnable add10Outgoing = new DigraphConcurrentTest.CreateEdgeNightContention(digraph, countDownLatch, node.getLabel(), 10, true, interruptedCallBack);
                futures.add(executorService.submit(add10Outgoing));
                // add 10 incoming edges
                Runnable add10Incoming = new DigraphConcurrentTest.CreateEdgeNightContention(digraph, countDownLatch, node.getLabel(), 10, false, interruptedCallBack);
                futures.add(executorService.submit(add10Incoming));
            }
            // red lights go out, race has begun. http://www.formula1-dictionary.net/start_sequence.html
            countDownLatch.countDown();
            // wait for every tasks completed before switch to the next node.
            for (Future<?> future : futures) {
                if (isTestInterrupted.get()) {
                    break;
                }
                future.get(1, TimeUnit.MINUTES);
            }
            if (isTestInterrupted.get()) {
                Assert.fail("Test had been interrupted.");
            }
        }
        assertGraphIsInConsistentState(digraph);
    }

    private static final class CreateNodeWorker implements Runnable {
        private final Digraph<Integer> digraph;

        private final int outgoingEdgesPerNode;

        private final int incomingEdgesPerNode;

        private final AtomicInteger idGenerator;

        private CreateNodeWorker(Digraph<Integer> digraph, int outgoingEdgesPerNode, int incomingEdgesPerNode, AtomicInteger idGenerator) {
            this.digraph = Objects.requireNonNull(digraph);
            Preconditions.checkArgument((outgoingEdgesPerNode >= 0));
            this.outgoingEdgesPerNode = outgoingEdgesPerNode;
            Preconditions.checkArgument((incomingEdgesPerNode >= 0));
            this.incomingEdgesPerNode = incomingEdgesPerNode;
            this.idGenerator = idGenerator;
        }

        @Override
        public void run() {
            Integer newNodeId = idGenerator.getAndIncrement();
            digraph.createNode(newNodeId);
            addEdgesFromNew(newNodeId);
            addEdgesToNew(newNodeId);
        }

        private void addEdgesFromNew(int newNodeId) {
            int count = Math.min(newNodeId, outgoingEdgesPerNode);
            /* outgoing */
            DigraphConcurrentTest.addEdges(digraph, newNodeId, count, true);
        }

        private void addEdgesToNew(int newNodeId) {
            int count = Math.min(newNodeId, incomingEdgesPerNode);
            /* outgoing */
            DigraphConcurrentTest.addEdges(digraph, newNodeId, count, false);
        }
    }

    private static final class CreateEdgeNightContention implements Runnable {
        private final Digraph<Integer> digraph;

        private final CountDownLatch countDownLatch;

        private final int nodeId;

        private final int numberEdgeToAdd;

        private final boolean outgoing;

        private final Runnable inerruptCallBack;

        private CreateEdgeNightContention(Digraph<Integer> digraph, CountDownLatch countDownLatch, int nodeId, int numberEdgeToAdd, boolean outgoing, Runnable inerruptCallBack) {
            this.digraph = Objects.requireNonNull(digraph);
            this.countDownLatch = Objects.requireNonNull(countDownLatch);
            Preconditions.checkArgument((nodeId >= 0));
            this.nodeId = nodeId;
            Preconditions.checkArgument((numberEdgeToAdd >= 0));
            this.numberEdgeToAdd = numberEdgeToAdd;
            this.outgoing = outgoing;
            this.inerruptCallBack = Objects.requireNonNull(inerruptCallBack);
        }

        @Override
        public void run() {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                inerruptCallBack.run();
                // hardly ever will see that in the log. Who cares?
                Assert.fail(e.getMessage());
            }
            DigraphConcurrentTest.addEdges(digraph, nodeId, numberEdgeToAdd, outgoing);
        }
    }
}

