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


import NodeRole.BACKUP;
import NodeRole.CLIENT;
import NodeRole.SHARD;
import Transport.ThreadingModel.DEDICATED_THREADS;
import WordVectorStorage.SYN_0;
import WordVectorStorage.SYN_1;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.parameterserver.distributed.conf.VoidConfiguration;
import org.nd4j.parameterserver.distributed.enums.NodeRole;
import org.nd4j.parameterserver.distributed.logic.storage.WordVectorStorage;
import org.nd4j.parameterserver.distributed.messages.aggregations.DotAggregation;
import org.nd4j.parameterserver.distributed.messages.intercom.DistributedInitializationMessage;
import org.nd4j.parameterserver.distributed.messages.intercom.DistributedSgDotMessage;
import org.nd4j.parameterserver.distributed.messages.intercom.DistributedSolidMessage;
import org.nd4j.parameterserver.distributed.messages.requests.AssignRequestMessage;
import org.nd4j.parameterserver.distributed.messages.requests.InitializationRequestMessage;
import org.nd4j.parameterserver.distributed.messages.requests.SkipGramRequestMessage;
import org.nd4j.parameterserver.distributed.messages.requests.VectorRequestMessage;
import org.nd4j.parameterserver.distributed.training.impl.SkipGramTrainer;
import org.nd4j.parameterserver.distributed.transport.MulticastTransport;
import org.nd4j.parameterserver.distributed.transport.Transport;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
@Ignore
@Deprecated
public class VoidParameterServerTest {
    private static List<String> localIPs;

    private static List<String> badIPs;

    private static final Transport transport = new MulticastTransport();

    @Test(timeout = 30000L)
    public void testNodeRole1() throws Exception {
        final VoidConfiguration conf = VoidConfiguration.builder().multicastPort(45678).numberOfShards(10).multicastNetwork("224.0.1.1").shardAddresses(VoidParameterServerTest.localIPs).ttl(4).build();
        conf.setUnicastControllerPort(34567);
        VoidParameterServer node = new VoidParameterServer();
        node.init(conf, VoidParameterServerTest.transport, new SkipGramTrainer());
        Assert.assertEquals(SHARD, node.getNodeRole());
        node.shutdown();
    }

    @Test(timeout = 30000L)
    public void testNodeRole2() throws Exception {
        final VoidConfiguration conf = VoidConfiguration.builder().multicastPort(45678).numberOfShards(10).shardAddresses(VoidParameterServerTest.badIPs).backupAddresses(VoidParameterServerTest.localIPs).multicastNetwork("224.0.1.1").ttl(4).build();
        conf.setUnicastControllerPort(34567);
        VoidParameterServer node = new VoidParameterServer();
        node.init(conf, VoidParameterServerTest.transport, new SkipGramTrainer());
        Assert.assertEquals(BACKUP, node.getNodeRole());
        node.shutdown();
    }

    @Test(timeout = 30000L)
    public void testNodeRole3() throws Exception {
        final VoidConfiguration conf = VoidConfiguration.builder().multicastPort(45678).numberOfShards(10).shardAddresses(VoidParameterServerTest.badIPs).backupAddresses(VoidParameterServerTest.badIPs).multicastNetwork("224.0.1.1").ttl(4).build();
        conf.setUnicastControllerPort(34567);
        VoidParameterServer node = new VoidParameterServer();
        node.init(conf, VoidParameterServerTest.transport, new SkipGramTrainer());
        Assert.assertEquals(CLIENT, node.getNodeRole());
        node.shutdown();
    }

    @Test(timeout = 60000L)
    public void testNodeInitialization1() throws Exception {
        final AtomicInteger failCnt = new AtomicInteger(0);
        final AtomicInteger passCnt = new AtomicInteger(0);
        final VoidConfiguration conf = VoidConfiguration.builder().multicastPort(45678).numberOfShards(10).shardAddresses(VoidParameterServerTest.localIPs).multicastNetwork("224.0.1.1").ttl(4).build();
        conf.setUnicastControllerPort(34567);
        Thread[] threads = new Thread[10];
        for (int t = 0; t < (threads.length); t++) {
            threads[t] = new Thread(new Runnable() {
                @Override
                public void run() {
                    VoidParameterServer node = new VoidParameterServer();
                    node.init(conf, VoidParameterServerTest.transport, new SkipGramTrainer());
                    if ((node.getNodeRole()) != (NodeRole.SHARD))
                        failCnt.incrementAndGet();

                    passCnt.incrementAndGet();
                    node.shutdown();
                }
            });
            threads[t].start();
        }
        for (int t = 0; t < (threads.length); t++) {
            threads[t].join();
        }
        Assert.assertEquals(0, failCnt.get());
        Assert.assertEquals(threads.length, passCnt.get());
    }

    /**
     * This is very important test, it covers basic messages handling over network.
     * Here we have 1 client, 1 connected Shard + 2 shards available over multicast UDP
     *
     * PLEASE NOTE: This test uses manual stepping through messages
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000L)
    public void testNodeInitialization2() throws Exception {
        final AtomicInteger failCnt = new AtomicInteger(0);
        final AtomicInteger passCnt = new AtomicInteger(0);
        final AtomicInteger startCnt = new AtomicInteger(0);
        INDArray exp = Nd4j.create(new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0 });
        final VoidConfiguration clientConf = VoidConfiguration.builder().multicastPort(45678).numberOfShards(3).shardAddresses(VoidParameterServerTest.localIPs).multicastNetwork("224.0.1.1").streamId(119).forcedRole(CLIENT).ttl(4).build();
        clientConf.setUnicastControllerPort(34567);
        final VoidConfiguration shardConf1 = VoidConfiguration.builder().multicastPort(45678).numberOfShards(3).streamId(119).shardAddresses(VoidParameterServerTest.localIPs).multicastNetwork("224.0.1.1").ttl(4).build();
        shardConf1.setUnicastControllerPort(34568);
        final VoidConfiguration shardConf2 = VoidConfiguration.builder().multicastPort(45678).numberOfShards(3).streamId(119).shardAddresses(VoidParameterServerTest.localIPs).multicastNetwork("224.0.1.1").ttl(4).build();
        shardConf2.setUnicastControllerPort(34569);// we'll never get anything on this port

        final VoidConfiguration shardConf3 = VoidConfiguration.builder().multicastPort(45678).numberOfShards(3).streamId(119).shardAddresses(VoidParameterServerTest.localIPs).multicastNetwork("224.0.1.1").ttl(4).build();
        shardConf3.setUnicastControllerPort(34570);// we'll never get anything on this port

        VoidParameterServer clientNode = new VoidParameterServer(true);
        clientNode.setShardIndex(((short) (0)));
        clientNode.init(clientConf);
        clientNode.getTransport().launch(DEDICATED_THREADS);
        Assert.assertEquals(CLIENT, clientNode.getNodeRole());
        Thread[] threads = new Thread[3];
        final VoidConfiguration[] voidConfigurations = new VoidConfiguration[]{ shardConf1, shardConf2, shardConf3 };
        VoidParameterServer[] shards = new VoidParameterServer[threads.length];
        for (int t = 0; t < (threads.length); t++) {
            final int x = t;
            threads[t] = new Thread(() -> {
                shards[x] = new VoidParameterServer(true);
                shards[x].setShardIndex(((short) (x)));
                shards[x].init(voidConfigurations[x]);
                shards[x].getTransport().launch(DEDICATED_THREADS);
                Assert.assertEquals(SHARD, shards[x].getNodeRole());
                startCnt.incrementAndGet();
                passCnt.incrementAndGet();
            });
            threads[t].setDaemon(true);
            threads[t].start();
        }
        // we block until all threads are really started before sending commands
        while ((startCnt.get()) < (threads.length))
            Thread.sleep(500);

        // give additional time to start handlers
        Thread.sleep(1000);
        // now we'll send commands from Client, and we'll check how these messages will be handled
        DistributedInitializationMessage message = DistributedInitializationMessage.builder().numWords(100).columnsPerShard(10).seed(123).useHs(false).useNeg(true).vectorLength(100).build();
        log.info("MessageType: {}", message.getMessageType());
        clientNode.getTransport().sendMessage(message);
        // at this point each and every shard should already have this message
        // now we check message queue within Shards
        for (int t = 0; t < (threads.length); t++) {
            VoidMessage incMessage = shards[t].getTransport().takeMessage();
            Assert.assertNotEquals(("Failed for shard " + t), null, incMessage);
            Assert.assertEquals(("Failed for shard " + t), message.getMessageType(), incMessage.getMessageType());
            // we should put message back to corresponding
            shards[t].getTransport().putMessage(incMessage);
        }
        /* at this moment we're 100% sure that:
        1) Client was able to send message to one of shards
        2) Selected Shard successfully received message from Client
        3) Shard retransmits message to all shards

        Now, we're passing this message to VoidParameterServer manually, and check for execution result
         */
        for (int t = 0; t < (threads.length); t++) {
            VoidMessage incMessage = shards[t].getTransport().takeMessage();
            Assert.assertNotEquals(("Failed for shard " + t), null, incMessage);
            shards[t].handleMessage(message);
            /**
             * Now we're checking how data storage was initialized
             */
            Assert.assertEquals(null, shards[t].getNegTable());
            Assert.assertEquals(null, shards[t].getSyn1());
            Assert.assertNotEquals(null, shards[t].getExpTable());
            Assert.assertNotEquals(null, shards[t].getSyn0());
            Assert.assertNotEquals(null, shards[t].getSyn1Neg());
        }
        // now we'll check passing for negTable, but please note - we're not sending it right now
        INDArray negTable = Nd4j.create(100000).assign(12.0F);
        DistributedSolidMessage negMessage = new DistributedSolidMessage(WordVectorStorage.NEGATIVE_TABLE, negTable, false);
        for (int t = 0; t < (threads.length); t++) {
            shards[t].handleMessage(negMessage);
            Assert.assertNotEquals(null, shards[t].getNegTable());
            Assert.assertEquals(negTable, shards[t].getNegTable());
        }
        // now we assign each row to something
        for (int t = 0; t < (threads.length); t++) {
            shards[t].handleMessage(new org.nd4j.parameterserver.distributed.messages.intercom.DistributedAssignMessage(WordVectorStorage.SYN_0, 1, ((double) (t))));
            Assert.assertEquals(Nd4j.create(message.getColumnsPerShard()).assign(((double) (t))), shards[t].getSyn0().getRow(1));
        }
        // and now we'll request for aggregated vector for row 1
        clientNode.getVector(1);
        VoidMessage vecm = shards[0].getTransport().takeMessage();
        Assert.assertEquals(7, vecm.getMessageType());
        VectorRequestMessage vrm = ((VectorRequestMessage) (vecm));
        Assert.assertEquals(1, vrm.getRowIndex());
        shards[0].handleMessage(vecm);
        Thread.sleep(100);
        // at this moment all 3 shards should already have distributed message
        for (int t = 0; t < (threads.length); t++) {
            VoidMessage dm = shards[t].getTransport().takeMessage();
            Assert.assertEquals(20, dm.getMessageType());
            shards[t].handleMessage(dm);
        }
        // at this moment we should have messages propagated across all shards
        Thread.sleep(100);
        for (int t = (threads.length) - 1; t >= 0; t--) {
            VoidMessage msg;
            while ((msg = shards[t].getTransport().takeMessage()) != null) {
                shards[t].handleMessage(msg);
            } 
        }
        // and at this moment, Shard_0 should contain aggregated vector for us
        Assert.assertEquals(true, shards[0].clipboard.isTracking(0L, 1L));
        Assert.assertEquals(true, shards[0].clipboard.isReady(0L, 1L));
        INDArray jointVector = shards[0].clipboard.nextCandidate().getAccumulatedResult();
        log.info("Joint vector: {}", jointVector);
        Assert.assertEquals(exp, jointVector);
        /**
         * now we're going to test real SkipGram round
         */
        // first, we're setting data to something predefined
        for (int t = 0; t < (threads.length); t++) {
            shards[t].handleMessage(new org.nd4j.parameterserver.distributed.messages.intercom.DistributedAssignMessage(WordVectorStorage.SYN_0, 0, 0.0));
            shards[t].handleMessage(new org.nd4j.parameterserver.distributed.messages.intercom.DistributedAssignMessage(WordVectorStorage.SYN_0, 1, 1.0));
            shards[t].handleMessage(new org.nd4j.parameterserver.distributed.messages.intercom.DistributedAssignMessage(WordVectorStorage.SYN_0, 2, 2.0));
            shards[t].handleMessage(new org.nd4j.parameterserver.distributed.messages.intercom.DistributedAssignMessage(WordVectorStorage.SYN_1_NEGATIVE, 0, 0.0));
            shards[t].handleMessage(new org.nd4j.parameterserver.distributed.messages.intercom.DistributedAssignMessage(WordVectorStorage.SYN_1_NEGATIVE, 1, 1.0));
            shards[t].handleMessage(new org.nd4j.parameterserver.distributed.messages.intercom.DistributedAssignMessage(WordVectorStorage.SYN_1_NEGATIVE, 2, 2.0));
        }
        DistributedSgDotMessage ddot = new DistributedSgDotMessage(2L, new int[]{ 0, 1, 2 }, new int[]{ 0, 1, 2 }, 0, 1, new byte[]{ 0, 1 }, true, ((short) (0)), 0.01F);
        for (int t = 0; t < (threads.length); t++) {
            shards[t].handleMessage(ddot);
        }
        Thread.sleep(100);
        for (int t = (threads.length) - 1; t >= 0; t--) {
            VoidMessage msg;
            while ((msg = shards[t].getTransport().takeMessage()) != null) {
                shards[t].handleMessage(msg);
            } 
        }
        // at this moment ot should be caclulated everywhere
        exp = Nd4j.create(new double[]{ 0.0, 30.0, 120.0 });
        for (int t = 0; t < (threads.length); t++) {
            Assert.assertEquals(true, shards[t].clipboard.isReady(0L, 2L));
            DotAggregation dot = ((DotAggregation) (shards[t].clipboard.unpin(0L, 2L)));
            INDArray aggregated = dot.getAccumulatedResult();
            Assert.assertEquals(exp, aggregated);
        }
        for (int t = 0; t < (threads.length); t++) {
            threads[t].join();
        }
        for (int t = 0; t < (threads.length); t++) {
            shards[t].shutdown();
        }
        Assert.assertEquals(threads.length, passCnt.get());
        for (VoidParameterServer server : shards) {
            server.shutdown();
        }
        clientNode.shutdown();
    }

    /**
     * PLEASE NOTE: This test uses automatic feeding through messages
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000L)
    public void testNodeInitialization3() throws Exception {
        final AtomicInteger failCnt = new AtomicInteger(0);
        final AtomicInteger passCnt = new AtomicInteger(0);
        final AtomicInteger startCnt = new AtomicInteger(0);
        Nd4j.create(1);
        final VoidConfiguration clientConf = VoidConfiguration.builder().multicastPort(45678).numberOfShards(3).shardAddresses(VoidParameterServerTest.localIPs).multicastNetwork("224.0.1.1").streamId(119).forcedRole(CLIENT).ttl(4).build();
        clientConf.setUnicastControllerPort(34567);
        final VoidConfiguration shardConf1 = VoidConfiguration.builder().multicastPort(45678).numberOfShards(3).streamId(119).shardAddresses(VoidParameterServerTest.localIPs).multicastNetwork("224.0.1.1").ttl(4).build();
        shardConf1.setUnicastControllerPort(34567);
        final VoidConfiguration shardConf2 = VoidConfiguration.builder().multicastPort(45678).numberOfShards(3).streamId(119).shardAddresses(VoidParameterServerTest.localIPs).multicastNetwork("224.0.1.1").ttl(4).build();
        shardConf2.setUnicastControllerPort(34569);// we'll never get anything on this port

        final VoidConfiguration shardConf3 = VoidConfiguration.builder().multicastPort(45678).numberOfShards(3).streamId(119).shardAddresses(VoidParameterServerTest.localIPs).multicastNetwork("224.0.1.1").ttl(4).build();
        shardConf3.setUnicastControllerPort(34570);// we'll never get anything on this port

        VoidParameterServer clientNode = new VoidParameterServer();
        clientNode.setShardIndex(((short) (0)));
        clientNode.init(clientConf);
        clientNode.getTransport().launch(DEDICATED_THREADS);
        Assert.assertEquals(CLIENT, clientNode.getNodeRole());
        Thread[] threads = new Thread[3];
        final VoidConfiguration[] voidConfigurations = new VoidConfiguration[]{ shardConf1, shardConf2, shardConf3 };
        VoidParameterServer[] shards = new VoidParameterServer[threads.length];
        final AtomicBoolean runner = new AtomicBoolean(true);
        for (int t = 0; t < (threads.length); t++) {
            final int x = t;
            threads[t] = new Thread(() -> {
                shards[x] = new VoidParameterServer();
                shards[x].setShardIndex(((short) (x)));
                shards[x].init(voidConfigurations[x]);
                shards[x].getTransport().launch(DEDICATED_THREADS);
                Assert.assertEquals(SHARD, shards[x].getNodeRole());
                startCnt.incrementAndGet();
                try {
                    while (runner.get())
                        Thread.sleep(100);

                } catch (Exception e) {
                }
            });
            threads[t].setDaemon(true);
            threads[t].start();
        }
        // waiting till all shards are initialized
        while ((startCnt.get()) < (threads.length))
            Thread.sleep(20);

        InitializationRequestMessage irm = InitializationRequestMessage.builder().numWords(100).columnsPerShard(50).seed(123).useHs(true).useNeg(false).vectorLength(150).build();
        // after this point we'll assume all Shards are initialized
        // mostly because Init message is blocking
        clientNode.getTransport().sendMessage(irm);
        log.info("------------------");
        AssignRequestMessage arm = new AssignRequestMessage(WordVectorStorage.SYN_0, 192.0F, 11);
        clientNode.getTransport().sendMessage(arm);
        Thread.sleep(1000);
        // This is blocking method
        INDArray vec = clientNode.getVector(SYN_0, 11);
        Assert.assertEquals(Nd4j.create(150).assign(192.0F), vec);
        // now we go for gradients-like test
        // first of all we set exptable to something predictable
        INDArray expSyn0 = Nd4j.create(150).assign(0.01F);
        INDArray expSyn1_1 = Nd4j.create(150).assign(0.020005);
        INDArray expSyn1_2 = Nd4j.create(150).assign(0.019995F);
        INDArray expTable = Nd4j.create(10000).assign(0.5F);
        AssignRequestMessage expReqMsg = new AssignRequestMessage(WordVectorStorage.EXP_TABLE, expTable);
        clientNode.getTransport().sendMessage(expReqMsg);
        arm = new AssignRequestMessage(WordVectorStorage.SYN_0, 0.01, (-1));
        clientNode.getTransport().sendMessage(arm);
        arm = new AssignRequestMessage(WordVectorStorage.SYN_1, 0.02, (-1));
        clientNode.getTransport().sendMessage(arm);
        Thread.sleep(500);
        // no we'll send single SkipGram request that involves calculation for 0 -> {1,2}, and will check result against pre-calculated values
        SkipGramRequestMessage sgrm = new SkipGramRequestMessage(0, 1, new int[]{ 1, 2 }, new byte[]{ 0, 1 }, ((short) (0)), 0.001, 119L);
        clientNode.getTransport().sendMessage(sgrm);
        // TODO: we might want to introduce optional CompletedMessage here
        // now we just wait till everything is finished
        Thread.sleep(1000);
        // This is blocking method
        INDArray row_syn0 = clientNode.getVector(SYN_0, 0);
        INDArray row_syn1_1 = clientNode.getVector(SYN_1, 1);
        INDArray row_syn1_2 = clientNode.getVector(SYN_1, 2);
        Assert.assertEquals(expSyn0, row_syn0);
        Assert.assertArrayEquals(expSyn1_1.data().asFloat(), row_syn1_1.data().asFloat(), 1.0E-6F);
        Assert.assertArrayEquals(expSyn1_2.data().asFloat(), row_syn1_2.data().asFloat(), 1.0E-6F);
        runner.set(false);
        for (int t = 0; t < (threads.length); t++) {
            threads[t].join();
        }
        for (VoidParameterServer server : shards) {
            server.shutdown();
        }
        clientNode.shutdown();
    }
}

