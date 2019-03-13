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
package com.twitter.distributedlog.impl.federated;


import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.twitter.distributedlog.DistributedLogConfiguration;
import com.twitter.distributedlog.TestDistributedLogBase;
import com.twitter.distributedlog.TestZooKeeperClientBuilder;
import com.twitter.distributedlog.ZooKeeperClient;
import com.twitter.distributedlog.ZooKeeperClientUtils;
import com.twitter.distributedlog.callback.NamespaceListener;
import com.twitter.distributedlog.exceptions.LogExistsException;
import com.twitter.distributedlog.exceptions.UnexpectedException;
import com.twitter.distributedlog.metadata.LogMetadataStore;
import com.twitter.distributedlog.util.DLUtils;
import com.twitter.distributedlog.util.FutureUtils;
import com.twitter.distributedlog.util.OrderedScheduler;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * Test ZK based metadata store.
 */
public class TestFederatedZKLogMetadataStore extends TestDistributedLogBase {
    private static final int zkSessionTimeoutMs = 2000;

    private static final int maxLogsPerSubnamespace = 10;

    static class TestNamespaceListener implements NamespaceListener {
        final CountDownLatch doneLatch = new CountDownLatch(1);

        final AtomicReference<Iterator<String>> resultHolder = new AtomicReference<Iterator<String>>();

        @Override
        public void onStreamsChanged(Iterator<String> streams) {
            resultHolder.set(streams);
            if (streams.hasNext()) {
                doneLatch.countDown();
            }
        }

        Iterator<String> getResult() {
            return resultHolder.get();
        }

        void waitForDone() throws InterruptedException {
            doneLatch.await();
        }
    }

    static class TestNamespaceListenerWithExpectedSize implements NamespaceListener {
        final int expectedSize;

        final CountDownLatch doneLatch = new CountDownLatch(1);

        final AtomicReference<Set<String>> resultHolder = new AtomicReference<Set<String>>();

        TestNamespaceListenerWithExpectedSize(int expectedSize) {
            this.expectedSize = expectedSize;
        }

        Set<String> getResult() {
            return resultHolder.get();
        }

        @Override
        public void onStreamsChanged(Iterator<String> logsIter) {
            List<String> logList = Lists.newArrayList(logsIter);
            if ((logList.size()) < (expectedSize)) {
                return;
            }
            resultHolder.set(Sets.newTreeSet(logList));
            doneLatch.countDown();
        }

        void waitForDone() throws InterruptedException {
            doneLatch.await();
        }
    }

    @Rule
    public TestName runtime = new TestName();

    protected final DistributedLogConfiguration baseConf = new DistributedLogConfiguration().setFederatedMaxLogsPerSubnamespace(TestFederatedZKLogMetadataStore.maxLogsPerSubnamespace);

    protected ZooKeeperClient zkc;

    protected FederatedZKLogMetadataStore metadataStore;

    protected OrderedScheduler scheduler;

    protected URI uri;

    @Test(timeout = 60000)
    public void testBasicOperations() throws Exception {
        TestFederatedZKLogMetadataStore.TestNamespaceListener listener = new TestFederatedZKLogMetadataStore.TestNamespaceListener();
        metadataStore.registerNamespaceListener(listener);
        String logName = "test-log-1";
        URI logUri = FutureUtils.result(metadataStore.createLog(logName));
        Assert.assertEquals(uri, logUri);
        Optional<URI> logLocation = FutureUtils.result(metadataStore.getLogLocation(logName));
        Assert.assertTrue(logLocation.isPresent());
        Assert.assertEquals(uri, logLocation.get());
        Optional<URI> notExistLogLocation = FutureUtils.result(metadataStore.getLogLocation("non-existent-log"));
        Assert.assertFalse(notExistLogLocation.isPresent());
        // listener should receive notification
        listener.waitForDone();
        Iterator<String> logsIter = listener.getResult();
        Assert.assertTrue(logsIter.hasNext());
        Assert.assertEquals(logName, logsIter.next());
        Assert.assertFalse(logsIter.hasNext());
        // get logs should return the log
        Iterator<String> newLogsIter = FutureUtils.result(metadataStore.getLogs());
        Assert.assertTrue(newLogsIter.hasNext());
        Assert.assertEquals(logName, newLogsIter.next());
        Assert.assertFalse(newLogsIter.hasNext());
    }

    @Test(timeout = 60000)
    public void testMultipleListeners() throws Exception {
        TestFederatedZKLogMetadataStore.TestNamespaceListener listener1 = new TestFederatedZKLogMetadataStore.TestNamespaceListener();
        TestFederatedZKLogMetadataStore.TestNamespaceListener listener2 = new TestFederatedZKLogMetadataStore.TestNamespaceListener();
        metadataStore.registerNamespaceListener(listener1);
        metadataStore.registerNamespaceListener(listener2);
        String logName = "test-multiple-listeners";
        URI logUri = FutureUtils.result(metadataStore.createLog(logName));
        Assert.assertEquals(uri, logUri);
        listener1.waitForDone();
        listener2.waitForDone();
        Iterator<String> logsIter1 = listener1.getResult();
        Iterator<String> logsIter2 = listener2.getResult();
        Assert.assertTrue(Iterators.elementsEqual(logsIter1, logsIter2));
    }

    @Test(timeout = 60000)
    public void testCreateLog() throws Exception {
        DistributedLogConfiguration conf = new DistributedLogConfiguration();
        conf.addConfiguration(baseConf);
        ZooKeeperClient anotherZkc = TestZooKeeperClientBuilder.newBuilder().uri(uri).sessionTimeoutMs(TestFederatedZKLogMetadataStore.zkSessionTimeoutMs).build();
        FederatedZKLogMetadataStore anotherMetadataStore = new FederatedZKLogMetadataStore(conf, uri, anotherZkc, scheduler);
        for (int i = 0; i < (2 * (TestFederatedZKLogMetadataStore.maxLogsPerSubnamespace)); i++) {
            LogMetadataStore createStore;
            LogMetadataStore checkStore;
            if ((i % 2) == 0) {
                createStore = metadataStore;
                checkStore = anotherMetadataStore;
            } else {
                createStore = anotherMetadataStore;
                checkStore = metadataStore;
            }
            String logName = "test-create-log-" + i;
            URI logUri = FutureUtils.result(createStore.createLog(logName));
            Optional<URI> logLocation = FutureUtils.result(checkStore.getLogLocation(logName));
            Assert.assertTrue((("Log " + logName) + " doesn't exist"), logLocation.isPresent());
            Assert.assertEquals((("Different log location " + (logLocation.get())) + " is found"), logUri, logLocation.get());
        }
        Assert.assertEquals(2, metadataStore.getSubnamespaces().size());
        Assert.assertEquals(2, anotherMetadataStore.getSubnamespaces().size());
    }

    @Test(timeout = 60000)
    public void testDuplicatedLogs() throws Exception {
        DistributedLogConfiguration conf = new DistributedLogConfiguration();
        conf.addConfiguration(baseConf);
        String logName = "test-log";
        FutureUtils.result(metadataStore.createLog(logName));
        URI subNs1 = FutureUtils.result(metadataStore.createSubNamespace());
        URI subNs2 = FutureUtils.result(metadataStore.createSubNamespace());
        String duplicatedLogName = "test-duplicated-logs";
        // Create same log in different sub namespaces
        metadataStore.createLogInNamespaceSync(subNs1, duplicatedLogName);
        metadataStore.createLogInNamespaceSync(subNs2, duplicatedLogName);
        try {
            FutureUtils.result(metadataStore.createLog("non-existent-log"));
            Assert.fail("should throw exception when duplicated log found");
        } catch (UnexpectedException ue) {
            // should throw unexpected exception
            Assert.assertTrue(metadataStore.duplicatedLogFound.get());
        }
        try {
            FutureUtils.result(metadataStore.getLogLocation(logName));
            Assert.fail("should throw exception when duplicated log found");
        } catch (UnexpectedException ue) {
            // should throw unexpected exception
            Assert.assertTrue(metadataStore.duplicatedLogFound.get());
        }
        try {
            FutureUtils.result(metadataStore.getLogLocation("non-existent-log"));
            Assert.fail("should throw exception when duplicated log found");
        } catch (UnexpectedException ue) {
            // should throw unexpected exception
            Assert.assertTrue(metadataStore.duplicatedLogFound.get());
        }
        try {
            FutureUtils.result(metadataStore.getLogLocation(duplicatedLogName));
            Assert.fail("should throw exception when duplicated log found");
        } catch (UnexpectedException ue) {
            // should throw unexpected exception
            Assert.assertTrue(metadataStore.duplicatedLogFound.get());
        }
        try {
            FutureUtils.result(metadataStore.getLogs());
            Assert.fail("should throw exception when duplicated log found");
        } catch (UnexpectedException ue) {
            // should throw unexpected exception
            Assert.assertTrue(metadataStore.duplicatedLogFound.get());
        }
    }

    @Test(timeout = 60000)
    public void testGetLogLocationWhenCacheMissed() throws Exception {
        String logName = "test-get-location-when-cache-missed";
        URI logUri = FutureUtils.result(metadataStore.createLog(logName));
        Assert.assertEquals(uri, logUri);
        metadataStore.removeLogFromCache(logName);
        Optional<URI> logLocation = FutureUtils.result(metadataStore.getLogLocation(logName));
        Assert.assertTrue(logLocation.isPresent());
        Assert.assertEquals(logUri, logLocation.get());
    }

    @Test(timeout = 60000, expected = LogExistsException.class)
    public void testCreateLogWhenCacheMissed() throws Exception {
        String logName = "test-create-log-when-cache-missed";
        URI logUri = FutureUtils.result(metadataStore.createLog(logName));
        Assert.assertEquals(uri, logUri);
        metadataStore.removeLogFromCache(logName);
        FutureUtils.result(metadataStore.createLog(logName));
    }

    @Test(timeout = 60000, expected = LogExistsException.class)
    public void testCreateLogWhenLogExists() throws Exception {
        String logName = "test-create-log-when-log-exists";
        URI logUri = FutureUtils.result(metadataStore.createLog(logName));
        Assert.assertEquals(uri, logUri);
        FutureUtils.result(metadataStore.createLog(logName));
    }

    @Test(timeout = 60000)
    public void testGetLogs() throws Exception {
        int numLogs = 3 * (TestFederatedZKLogMetadataStore.maxLogsPerSubnamespace);
        Set<String> expectedLogs = createLogs(numLogs, "test-get-logs");
        Set<String> receivedLogs;
        do {
            TimeUnit.MILLISECONDS.sleep(20);
            receivedLogs = new TreeSet<String>();
            Iterator<String> logs = FutureUtils.result(metadataStore.getLogs());
            receivedLogs.addAll(Lists.newArrayList(logs));
        } while ((receivedLogs.size()) < numLogs );
        Assert.assertEquals(numLogs, receivedLogs.size());
        Assert.assertTrue(Sets.difference(expectedLogs, receivedLogs).isEmpty());
    }

    @Test(timeout = 60000)
    public void testNamespaceListener() throws Exception {
        int numLogs = 3 * (TestFederatedZKLogMetadataStore.maxLogsPerSubnamespace);
        TestFederatedZKLogMetadataStore.TestNamespaceListenerWithExpectedSize listener = new TestFederatedZKLogMetadataStore.TestNamespaceListenerWithExpectedSize(numLogs);
        metadataStore.registerNamespaceListener(listener);
        Set<String> expectedLogs = createLogs(numLogs, "test-namespace-listener");
        listener.waitForDone();
        Set<String> receivedLogs = listener.getResult();
        Assert.assertEquals(numLogs, receivedLogs.size());
        Assert.assertTrue(Sets.difference(expectedLogs, receivedLogs).isEmpty());
        Random r = new Random(System.currentTimeMillis());
        int logId = r.nextInt(numLogs);
        String logName = "test-namespace-listener" + logId;
        TestFederatedZKLogMetadataStore.TestNamespaceListener deleteListener = new TestFederatedZKLogMetadataStore.TestNamespaceListener();
        metadataStore.registerNamespaceListener(deleteListener);
        deleteLog(logName);
        deleteListener.waitForDone();
        Set<String> logsAfterDeleted = Sets.newTreeSet(Lists.newArrayList(deleteListener.getResult()));
        Assert.assertEquals((numLogs - 1), logsAfterDeleted.size());
        expectedLogs.remove(logName);
        Assert.assertTrue(Sets.difference(expectedLogs, receivedLogs).isEmpty());
    }

    @Test(timeout = 60000)
    public void testCreateLogPickingFirstAvailableSubNamespace() throws Exception {
        URI subNs1 = FutureUtils.result(metadataStore.createSubNamespace());
        URI subNs2 = FutureUtils.result(metadataStore.createSubNamespace());
        Set<String> logs0 = createLogs(uri, ((TestFederatedZKLogMetadataStore.maxLogsPerSubnamespace) - 1), "test-ns0-");
        Set<String> logs1 = createLogs(subNs1, TestFederatedZKLogMetadataStore.maxLogsPerSubnamespace, "test-ns1-");
        Set<String> logs2 = createLogs(subNs2, TestFederatedZKLogMetadataStore.maxLogsPerSubnamespace, "test-ns2-");
        Set<String> allLogs = Sets.newTreeSet();
        allLogs.addAll(logs0);
        allLogs.addAll(logs1);
        allLogs.addAll(logs2);
        // make sure the metadata store saw all 29 logs
        Set<String> receivedLogs;
        do {
            TimeUnit.MILLISECONDS.sleep(20);
            receivedLogs = new TreeSet<String>();
            Iterator<String> logs = FutureUtils.result(metadataStore.getLogs());
            receivedLogs.addAll(Lists.newArrayList(logs));
        } while ((receivedLogs.size()) < ((3 * (TestFederatedZKLogMetadataStore.maxLogsPerSubnamespace)) - 1) );
        TestFederatedZKLogMetadataStore.TestNamespaceListenerWithExpectedSize listener = new TestFederatedZKLogMetadataStore.TestNamespaceListenerWithExpectedSize(((3 * (TestFederatedZKLogMetadataStore.maxLogsPerSubnamespace)) + 1));
        metadataStore.registerNamespaceListener(listener);
        Set<URI> uris = FutureUtils.result(metadataStore.fetchSubNamespaces(null));
        Assert.assertEquals(3, uris.size());
        String testLogName = "test-pick-first-available-ns";
        URI createdURI = FutureUtils.result(metadataStore.createLog(testLogName));
        allLogs.add(testLogName);
        Assert.assertEquals(uri, createdURI);
        uris = FutureUtils.result(metadataStore.fetchSubNamespaces(null));
        Assert.assertEquals(3, uris.size());
        testLogName = "test-create-new-ns";
        URI newURI = FutureUtils.result(metadataStore.createLog(testLogName));
        allLogs.add(testLogName);
        Assert.assertFalse(uris.contains(newURI));
        uris = FutureUtils.result(metadataStore.fetchSubNamespaces(null));
        Assert.assertEquals(4, uris.size());
        listener.waitForDone();
        receivedLogs = listener.getResult();
        Assert.assertEquals(((3 * (TestFederatedZKLogMetadataStore.maxLogsPerSubnamespace)) + 1), receivedLogs.size());
        Assert.assertEquals(allLogs, receivedLogs);
    }

    @Test(timeout = 60000)
    public void testZooKeeperSessionExpired() throws Exception {
        Set<String> allLogs = createLogs((2 * (TestFederatedZKLogMetadataStore.maxLogsPerSubnamespace)), "test-zookeeper-session-expired-");
        TestFederatedZKLogMetadataStore.TestNamespaceListenerWithExpectedSize listener = new TestFederatedZKLogMetadataStore.TestNamespaceListenerWithExpectedSize(((2 * (TestFederatedZKLogMetadataStore.maxLogsPerSubnamespace)) + 1));
        metadataStore.registerNamespaceListener(listener);
        ZooKeeperClientUtils.expireSession(zkc, DLUtils.getZKServersFromDLUri(uri), TestFederatedZKLogMetadataStore.zkSessionTimeoutMs);
        String testLogName = "test-log-name";
        allLogs.add(testLogName);
        DistributedLogConfiguration anotherConf = new DistributedLogConfiguration();
        anotherConf.addConfiguration(baseConf);
        ZooKeeperClient anotherZkc = TestZooKeeperClientBuilder.newBuilder().uri(uri).sessionTimeoutMs(TestFederatedZKLogMetadataStore.zkSessionTimeoutMs).build();
        FederatedZKLogMetadataStore anotherMetadataStore = new FederatedZKLogMetadataStore(anotherConf, uri, anotherZkc, scheduler);
        FutureUtils.result(anotherMetadataStore.createLog(testLogName));
        listener.waitForDone();
        Set<String> receivedLogs = listener.getResult();
        Assert.assertEquals(((2 * (TestFederatedZKLogMetadataStore.maxLogsPerSubnamespace)) + 1), receivedLogs.size());
        Assert.assertEquals(allLogs, receivedLogs);
    }
}

