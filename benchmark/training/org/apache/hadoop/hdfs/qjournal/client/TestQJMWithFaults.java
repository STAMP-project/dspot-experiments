/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.qjournal.client;


import CommonConfigurationKeysPublic.IPC_CLIENT_CONNECT_MAX_RETRIES_KEY;
import ProtobufRpcEngine.LOG;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.qjournal.MiniJournalCluster;
import org.apache.hadoop.hdfs.qjournal.QJMTestUtil;
import org.apache.hadoop.hdfs.qjournal.protocol.QJournalProtocol;
import org.apache.hadoop.hdfs.qjournal.server.JournalFaultInjector;
import org.apache.hadoop.hdfs.server.namenode.EditLogFileOutputStream;
import org.apache.hadoop.hdfs.server.protocol.NamespaceInfo;
import org.apache.hadoop.hdfs.util.Holder;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestQJMWithFaults {
    private static final Logger LOG = LoggerFactory.getLogger(TestQJMWithFaults.class);

    private static final String RAND_SEED_PROPERTY = "TestQJMWithFaults.random-seed";

    private static final int NUM_WRITER_ITERS = 500;

    private static final int SEGMENTS_PER_WRITER = 2;

    private static final Configuration conf = new Configuration();

    static {
        // Don't retry connections - it just slows down the tests.
        TestQJMWithFaults.conf.setInt(IPC_CLIENT_CONNECT_MAX_RETRIES_KEY, 0);
        // Make tests run faster by avoiding fsync()
        EditLogFileOutputStream.setShouldSkipFsyncForTesting(true);
    }

    // Set up fault injection mock.
    private static final JournalFaultInjector faultInjector = JournalFaultInjector.instance = Mockito.mock(JournalFaultInjector.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Sets up two of the nodes to each drop a single RPC, at all
     * possible combinations of RPCs. This may result in the
     * active writer failing to write. After this point, a new writer
     * should be able to recover and continue writing without
     * data loss.
     */
    @Test
    public void testRecoverAfterDoubleFailures() throws Exception {
        final long MAX_IPC_NUMBER = TestQJMWithFaults.determineMaxIpcNumber();
        for (int failA = 1; failA <= MAX_IPC_NUMBER; failA++) {
            for (int failB = 1; failB <= MAX_IPC_NUMBER; failB++) {
                String injectionStr = ((("(" + failA) + ", ") + failB) + ")";
                TestQJMWithFaults.LOG.info((((("\n\n-------------------------------------------\n" + "Beginning test, failing at ") + injectionStr) + "\n") + "-------------------------------------------\n\n"));
                MiniJournalCluster cluster = new MiniJournalCluster.Builder(TestQJMWithFaults.conf).build();
                cluster.waitActive();
                QuorumJournalManager qjm = null;
                try {
                    qjm = TestQJMWithFaults.createInjectableQJM(cluster);
                    qjm.format(QJMTestUtil.FAKE_NSINFO, false);
                    List<AsyncLogger> loggers = qjm.getLoggerSetForTests().getLoggersForTests();
                    failIpcNumber(loggers.get(0), failA);
                    failIpcNumber(loggers.get(1), failB);
                    int lastAckedTxn = TestQJMWithFaults.doWorkload(cluster, qjm);
                    if (lastAckedTxn < 6) {
                        TestQJMWithFaults.LOG.info(((("Failed after injecting failures at " + injectionStr) + ". This is expected since we injected a failure in the ") + "majority."));
                    }
                    qjm.close();
                    qjm = null;
                    // Now should be able to recover
                    qjm = TestQJMWithFaults.createInjectableQJM(cluster);
                    long lastRecoveredTxn = QJMTestUtil.recoverAndReturnLastTxn(qjm);
                    Assert.assertTrue((lastRecoveredTxn >= lastAckedTxn));
                    QJMTestUtil.writeSegment(cluster, qjm, (lastRecoveredTxn + 1), 3, true);
                } catch (Throwable t) {
                    // Test failure! Rethrow with the test setup info so it can be
                    // easily triaged.
                    throw new RuntimeException(("Test failed with injection: " + injectionStr), t);
                } finally {
                    cluster.shutdown();
                    cluster = null;
                    IOUtils.closeStream(qjm);
                    qjm = null;
                }
            }
        }
    }

    /**
     * Expect {@link UnknownHostException} if a hostname can't be resolved.
     */
    @Test
    public void testUnresolvableHostName() throws Exception {
        expectedException.expect(UnknownHostException.class);
        new QuorumJournalManager(TestQJMWithFaults.conf, new URI((("qjournal://" + ("bogus:12345" + "/")) + (QJMTestUtil.JID))), QJMTestUtil.FAKE_NSINFO);
    }

    /**
     * Test case in which three JournalNodes randomly flip flop between
     * up and down states every time they get an RPC.
     *
     * The writer keeps track of the latest ACKed edit, and on every
     * recovery operation, ensures that it recovers at least to that
     * point or higher. Since at any given point, a majority of JNs
     * may be injecting faults, any writer operation is allowed to fail,
     * so long as the exception message indicates it failed due to injected
     * faults.
     *
     * Given a random seed, the test should be entirely deterministic.
     */
    @Test
    public void testRandomized() throws Exception {
        long seed;
        Long userSpecifiedSeed = Long.getLong(TestQJMWithFaults.RAND_SEED_PROPERTY);
        if (userSpecifiedSeed != null) {
            TestQJMWithFaults.LOG.info("Using seed specified in system property");
            seed = userSpecifiedSeed;
            // If the user specifies a seed, then we should gather all the
            // IPC trace information so that debugging is easier. This makes
            // the test run about 25% slower otherwise.
            GenericTestUtils.setLogLevel(ProtobufRpcEngine.LOG, Level.ALL);
        } else {
            seed = new Random().nextLong();
        }
        TestQJMWithFaults.LOG.info(("Random seed: " + seed));
        Random r = new Random(seed);
        MiniJournalCluster cluster = new MiniJournalCluster.Builder(TestQJMWithFaults.conf).build();
        cluster.waitActive();
        // Format the cluster using a non-faulty QJM.
        QuorumJournalManager qjmForInitialFormat = TestQJMWithFaults.createInjectableQJM(cluster);
        qjmForInitialFormat.format(QJMTestUtil.FAKE_NSINFO, false);
        qjmForInitialFormat.close();
        try {
            long txid = 0;
            long lastAcked = 0;
            for (int i = 0; i < (TestQJMWithFaults.NUM_WRITER_ITERS); i++) {
                TestQJMWithFaults.LOG.info((("Starting writer " + i) + "\n-------------------"));
                QuorumJournalManager qjm = TestQJMWithFaults.createRandomFaultyQJM(cluster, r);
                try {
                    long recovered;
                    try {
                        recovered = QJMTestUtil.recoverAndReturnLastTxn(qjm);
                    } catch (Throwable t) {
                        TestQJMWithFaults.LOG.info("Failed recovery", t);
                        checkException(t);
                        continue;
                    }
                    Assert.assertTrue(((("Recovered only up to txnid " + recovered) + " but had gotten an ack for ") + lastAcked), (recovered >= lastAcked));
                    txid = recovered + 1;
                    // Periodically purge old data on disk so it's easier to look
                    // at failure cases.
                    if ((txid > 100) && ((i % 10) == 1)) {
                        qjm.purgeLogsOlderThan((txid - 100));
                    }
                    Holder<Throwable> thrown = new Holder<Throwable>(null);
                    for (int j = 0; j < (TestQJMWithFaults.SEGMENTS_PER_WRITER); j++) {
                        lastAcked = writeSegmentUntilCrash(cluster, qjm, txid, 4, thrown);
                        if ((thrown.held) != null) {
                            TestQJMWithFaults.LOG.info("Failed write", thrown.held);
                            checkException(thrown.held);
                            break;
                        }
                        txid += 4;
                    }
                } finally {
                    qjm.close();
                }
            }
        } finally {
            cluster.shutdown();
        }
    }

    private static class RandomFaultyChannel extends IPCLoggerChannel {
        private final Random random;

        private final float injectionProbability = 0.1F;

        private boolean isUp = true;

        public RandomFaultyChannel(Configuration conf, NamespaceInfo nsInfo, String journalId, InetSocketAddress addr, long seed) {
            super(conf, nsInfo, journalId, addr);
            this.random = new Random(seed);
        }

        @Override
        protected QJournalProtocol createProxy() throws IOException {
            QJournalProtocol realProxy = super.createProxy();
            return TestQJMWithFaults.mockProxy(new TestQJMWithFaults.WrapEveryCall<Object>(realProxy) {
                @Override
                void beforeCall(InvocationOnMock invocation) throws Exception {
                    if ((random.nextFloat()) < (injectionProbability)) {
                        isUp = !(isUp);
                        TestQJMWithFaults.LOG.info(((("transitioned " + (addr)) + " to ") + (isUp ? "up" : "down")));
                    }
                    if (!(isUp)) {
                        throw new IOException("Injected - faking being down");
                    }
                    if (invocation.getMethod().getName().equals("acceptRecovery")) {
                        if ((random.nextFloat()) < (injectionProbability)) {
                            Mockito.doThrow(new IOException("Injected - faking fault before persisting paxos data")).when(TestQJMWithFaults.faultInjector).beforePersistPaxosData();
                        } else
                            if ((random.nextFloat()) < (injectionProbability)) {
                                Mockito.doThrow(new IOException("Injected - faking fault after persisting paxos data")).when(TestQJMWithFaults.faultInjector).afterPersistPaxosData();
                            }

                    }
                }

                @Override
                public void afterCall(InvocationOnMock invocation, boolean succeeded) {
                    Mockito.reset(TestQJMWithFaults.faultInjector);
                }
            });
        }

        @Override
        protected ExecutorService createSingleThreadExecutor() {
            return new DirectExecutorService();
        }
    }

    private static class InvocationCountingChannel extends IPCLoggerChannel {
        private int rpcCount = 0;

        private final Map<Integer, Callable<Void>> injections = Maps.newHashMap();

        public InvocationCountingChannel(Configuration conf, NamespaceInfo nsInfo, String journalId, InetSocketAddress addr) {
            super(conf, nsInfo, journalId, addr);
        }

        int getRpcCount() {
            return rpcCount;
        }

        void failIpcNumber(final int idx) {
            Preconditions.checkArgument((idx > 0), "id must be positive");
            inject(idx, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    throw new IOException(("injected failed IPC at " + idx));
                }
            });
        }

        private void inject(int beforeRpcNumber, Callable<Void> injectedCode) {
            injections.put(beforeRpcNumber, injectedCode);
        }

        @Override
        protected QJournalProtocol createProxy() throws IOException {
            final QJournalProtocol realProxy = super.createProxy();
            QJournalProtocol mock = TestQJMWithFaults.mockProxy(new TestQJMWithFaults.WrapEveryCall<Object>(realProxy) {
                void beforeCall(InvocationOnMock invocation) throws Exception {
                    (rpcCount)++;
                    String param = "";
                    for (Object val : invocation.getArguments()) {
                        param += val + ",";
                    }
                    String callStr = ((((("[" + (addr)) + "] ") + (invocation.getMethod().getName())) + "(") + param) + ")";
                    Callable<Void> inject = injections.get(rpcCount);
                    if (inject != null) {
                        TestQJMWithFaults.LOG.info(((("Injecting code before IPC #" + (rpcCount)) + ": ") + callStr));
                        inject.call();
                    } else {
                        TestQJMWithFaults.LOG.info(((("IPC call #" + (rpcCount)) + ": ") + callStr));
                    }
                }
            });
            return mock;
        }
    }

    private abstract static class WrapEveryCall<T> implements Answer<T> {
        private final Object realObj;

        WrapEveryCall(Object realObj) {
            this.realObj = realObj;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T answer(InvocationOnMock invocation) throws Throwable {
            // Don't want to inject an error on close() since that isn't
            // actually an IPC call!
            if (!(Closeable.class.equals(invocation.getMethod().getDeclaringClass()))) {
                beforeCall(invocation);
            }
            boolean success = false;
            try {
                T ret = ((T) (invocation.getMethod().invoke(realObj, invocation.getArguments())));
                success = true;
                return ret;
            } catch (InvocationTargetException ite) {
                throw ite.getCause();
            } finally {
                afterCall(invocation, success);
            }
        }

        abstract void beforeCall(InvocationOnMock invocation) throws Exception;

        void afterCall(InvocationOnMock invocation, boolean succeeded) {
        }
    }
}

