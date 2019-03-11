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
package org.nd4j.parameterserver.distributed;


import NodeRole.CLIENT;
import NodeRole.SHARD;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.parameterserver.distributed.conf.VoidConfiguration;
import org.nd4j.parameterserver.distributed.enums.NodeRole;
import org.nd4j.parameterserver.distributed.logic.ClientRouter;
import org.nd4j.parameterserver.distributed.logic.routing.InterleavedRouter;
import org.nd4j.parameterserver.distributed.logic.sequence.BasicSequenceProvider;
import org.nd4j.parameterserver.distributed.messages.Frame;
import org.nd4j.parameterserver.distributed.messages.requests.CbowRequestMessage;
import org.nd4j.parameterserver.distributed.messages.requests.SkipGramRequestMessage;
import org.nd4j.parameterserver.distributed.training.impl.CbowTrainer;
import org.nd4j.parameterserver.distributed.training.impl.SkipGramTrainer;
import org.nd4j.parameterserver.distributed.transport.RoutedTransport;
import org.nd4j.parameterserver.distributed.transport.Transport;


/**
 * This set of tests doesn't has any assertions within.
 * All we care about here - performance and availability
 *
 * Tests for all environments are paired: one test for blocking messages, other one for non-blocking messages.
 *
 * @author raver119@gmail.com
 */
@Slf4j
@Ignore
@Deprecated
public class VoidParameterServerStressTest {
    private static final int NUM_WORDS = 100000;

    /**
     * This is one of the MOST IMPORTANT tests
     */
    @Test(timeout = 60000L)
    public void testPerformanceUnicast1() {
        List<String> list = new ArrayList<>();
        for (int t = 0; t < 1; t++) {
            list.add(("127.0.0.1:3838" + t));
        }
        VoidConfiguration voidConfiguration = VoidConfiguration.builder().numberOfShards(list.size()).shardAddresses(list).build();
        voidConfiguration.setUnicastControllerPort(49823);
        VoidParameterServer[] shards = new VoidParameterServer[list.size()];
        for (int t = 0; t < (shards.length); t++) {
            shards[t] = new VoidParameterServer(NodeRole.SHARD);
            Transport transport = new RoutedTransport();
            transport.setIpAndPort("127.0.0.1", Integer.valueOf(("3838" + t)));
            shards[t].setShardIndex(((short) (t)));
            shards[t].init(voidConfiguration, transport, new SkipGramTrainer());
            Assert.assertEquals(SHARD, shards[t].getNodeRole());
        }
        VoidParameterServer clientNode = new VoidParameterServer(NodeRole.CLIENT);
        RoutedTransport transport = new RoutedTransport();
        ClientRouter router = new InterleavedRouter(0);
        transport.setRouter(router);
        transport.setIpAndPort("127.0.0.1", voidConfiguration.getUnicastControllerPort());
        router.init(voidConfiguration, transport);
        clientNode.init(voidConfiguration, transport, new SkipGramTrainer());
        Assert.assertEquals(CLIENT, clientNode.getNodeRole());
        final List<Long> times = new CopyOnWriteArrayList<>();
        // at this point, everything should be started, time for tests
        clientNode.initializeSeqVec(100, VoidParameterServerStressTest.NUM_WORDS, 123, 25, true, false);
        log.info("Initialization finished, going to tests...");
        Thread[] threads = new Thread[4];
        for (int t = 0; t < (threads.length); t++) {
            final int e = t;
            threads[t] = new Thread(() -> {
                List<Long> results = new ArrayList<>();
                int chunk = (VoidParameterServerStressTest.NUM_WORDS) / (threads.length);
                int start = e * chunk;
                int end = (e + 1) * chunk;
                for (int i = 0; i < 200; i++) {
                    long time1 = System.nanoTime();
                    INDArray array = clientNode.getVector(RandomUtils.nextInt(start, end));
                    long time2 = System.nanoTime();
                    results.add((time2 - time1));
                    if (((i + 1) % 100) == 0)
                        log.info("Thread {} cnt {}", e, (i + 1));

                }
                times.addAll(results);
            });
            threads[t].setDaemon(true);
            threads[t].start();
        }
        for (int t = 0; t < (threads.length); t++) {
            try {
                threads[t].join();
            } catch (Exception e) {
            }
        }
        List<Long> newTimes = new ArrayList<>(times);
        Collections.sort(newTimes);
        log.info("p50: {} us", ((newTimes.get(((newTimes.size()) / 2))) / 1000));
        // shutdown everything
        for (VoidParameterServer shard : shards) {
            shard.getTransport().shutdown();
        }
        clientNode.getTransport().shutdown();
    }

    /**
     * This test checks for single Shard scenario, when Shard is also a Client
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000L)
    public void testPerformanceUnicast3() throws Exception {
        VoidConfiguration voidConfiguration = VoidConfiguration.builder().numberOfShards(1).shardAddresses(Arrays.asList("127.0.0.1:49823")).build();
        voidConfiguration.setUnicastControllerPort(49823);
        Transport transport = new RoutedTransport();
        transport.setIpAndPort("127.0.0.1", Integer.valueOf("49823"));
        VoidParameterServer parameterServer = new VoidParameterServer(NodeRole.SHARD);
        parameterServer.setShardIndex(((short) (0)));
        parameterServer.init(voidConfiguration, transport, new CbowTrainer());
        parameterServer.initializeSeqVec(100, VoidParameterServerStressTest.NUM_WORDS, 123L, 100, true, false);
        final List<Long> times = new ArrayList<>();
        log.info("Starting loop...");
        for (int i = 0; i < 200; i++) {
            Frame<CbowRequestMessage> frame = new Frame(BasicSequenceProvider.getInstance().getNextValue());
            for (int f = 0; f < 128; f++) {
                frame.stackMessage(VoidParameterServerStressTest.getCRM());
            }
            long time1 = System.nanoTime();
            parameterServer.execDistributed(frame);
            long time2 = System.nanoTime();
            times.add((time2 - time1));
            if ((i % 50) == 0)
                log.info("{} frames passed...", i);

        }
        Collections.sort(times);
        log.info("p50: {} us", ((times.get(((times.size()) / 2))) / 1000));
        parameterServer.shutdown();
    }

    /**
     * This test checks multiple Clients hammering single Shard
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000L)
    public void testPerformanceUnicast4() throws Exception {
        VoidConfiguration voidConfiguration = VoidConfiguration.builder().numberOfShards(1).shardAddresses(Arrays.asList("127.0.0.1:49823")).build();
        voidConfiguration.setUnicastControllerPort(49823);
        Transport transport = new RoutedTransport();
        transport.setIpAndPort("127.0.0.1", Integer.valueOf("49823"));
        VoidParameterServer parameterServer = new VoidParameterServer(NodeRole.SHARD);
        parameterServer.setShardIndex(((short) (0)));
        parameterServer.init(voidConfiguration, transport, new SkipGramTrainer());
        parameterServer.initializeSeqVec(100, VoidParameterServerStressTest.NUM_WORDS, 123L, 100, true, false);
        VoidParameterServer[] clients = new VoidParameterServer[1];
        for (int c = 0; c < (clients.length); c++) {
            clients[c] = new VoidParameterServer(NodeRole.CLIENT);
            Transport clientTransport = new RoutedTransport();
            clientTransport.setIpAndPort("127.0.0.1", Integer.valueOf(("4872" + c)));
            clients[c].init(voidConfiguration, clientTransport, new SkipGramTrainer());
            Assert.assertEquals(CLIENT, clients[c].getNodeRole());
        }
        final List<Long> times = new CopyOnWriteArrayList<>();
        log.info("Starting loop...");
        Thread[] threads = new Thread[clients.length];
        for (int t = 0; t < (threads.length); t++) {
            final int c = t;
            threads[t] = new Thread(() -> {
                List<Long> results = new ArrayList<>();
                AtomicLong sequence = new AtomicLong(0);
                for (int i = 0; i < 500; i++) {
                    Frame<SkipGramRequestMessage> frame = new Frame(sequence.incrementAndGet());
                    for (int f = 0; f < 128; f++) {
                        frame.stackMessage(VoidParameterServerStressTest.getSGRM());
                    }
                    long time1 = System.nanoTime();
                    clients[c].execDistributed(frame);
                    long time2 = System.nanoTime();
                    results.add((time2 - time1));
                    if (((i + 1) % 50) == 0)
                        log.info("Thread_{} finished {} frames...", c, i);

                }
                times.addAll(results);
            });
            threads[t].setDaemon(true);
            threads[t].start();
        }
        for (Thread thread : threads)
            thread.join();

        List<Long> newTimes = new ArrayList<>(times);
        Collections.sort(newTimes);
        log.info("p50: {} us", ((newTimes.get(((newTimes.size()) / 2))) / 1000));
        for (VoidParameterServer client : clients) {
            client.shutdown();
        }
        parameterServer.shutdown();
    }
}

