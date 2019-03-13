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
package com.twitter.distributedlog.impl;


import KeeperException.Code.BADVERSION;
import KeeperException.Code.NODEEXISTS;
import KeeperException.Code.NONODE;
import com.google.common.collect.Lists;
import com.twitter.distributedlog.DistributedLogConfiguration;
import com.twitter.distributedlog.LogSegmentMetadata;
import com.twitter.distributedlog.TestDistributedLogBase;
import com.twitter.distributedlog.ZooKeeperClient;
import com.twitter.distributedlog.ZooKeeperClientUtils;
import com.twitter.distributedlog.callback.LogSegmentNamesListener;
import com.twitter.distributedlog.exceptions.ZKException;
import com.twitter.distributedlog.util.DLUtils;
import com.twitter.distributedlog.util.FutureUtils;
import com.twitter.distributedlog.util.OrderedScheduler;
import com.twitter.distributedlog.util.Transaction;
import com.twitter.util.Await;
import com.twitter.util.Future;
import com.twitter.util.Promise;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.bookkeeper.meta.ZkVersion;
import org.apache.bookkeeper.versioning.Version;
import org.apache.bookkeeper.versioning.Versioned;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test ZK based log segment metadata store.
 */
public class TestZKLogSegmentMetadataStore extends TestDistributedLogBase {
    private static final Logger logger = LoggerFactory.getLogger(TestZKLogSegmentMetadataStore.class);

    private static final int zkSessionTimeoutMs = 2000;

    @Rule
    public TestName runtime = new TestName();

    protected final DistributedLogConfiguration baseConf = new DistributedLogConfiguration();

    protected ZooKeeperClient zkc;

    protected ZKLogSegmentMetadataStore lsmStore;

    protected OrderedScheduler scheduler;

    protected URI uri;

    protected String rootZkPath;

    @Test(timeout = 60000)
    public void testCreateLogSegment() throws Exception {
        LogSegmentMetadata segment = createLogSegment(1L);
        Transaction<Object> createTxn = lsmStore.transaction();
        lsmStore.createLogSegment(createTxn, segment);
        FutureUtils.result(createTxn.execute());
        // the log segment should be created
        Assert.assertNotNull((("LogSegment " + segment) + " should be created"), zkc.get().exists(segment.getZkPath(), false));
        LogSegmentMetadata segment2 = createLogSegment(1L);
        Transaction<Object> createTxn2 = lsmStore.transaction();
        lsmStore.createLogSegment(createTxn2, segment2);
        try {
            FutureUtils.result(createTxn2.execute());
            Assert.fail("Should fail if log segment exists");
        } catch (Throwable t) {
            // expected
            Assert.assertTrue("Should throw NodeExistsException if log segment exists", (t instanceof ZKException));
            ZKException zke = ((ZKException) (t));
            Assert.assertEquals("Should throw NodeExistsException if log segment exists", NODEEXISTS, zke.getKeeperExceptionCode());
        }
    }

    @Test(timeout = 60000)
    public void testDeleteLogSegment() throws Exception {
        LogSegmentMetadata segment = createLogSegment(1L);
        Transaction<Object> createTxn = lsmStore.transaction();
        lsmStore.createLogSegment(createTxn, segment);
        FutureUtils.result(createTxn.execute());
        // the log segment should be created
        Assert.assertNotNull((("LogSegment " + segment) + " should be created"), zkc.get().exists(segment.getZkPath(), false));
        Transaction<Object> deleteTxn = lsmStore.transaction();
        lsmStore.deleteLogSegment(deleteTxn, segment);
        FutureUtils.result(deleteTxn.execute());
        Assert.assertNull((("LogSegment " + segment) + " should be deleted"), zkc.get().exists(segment.getZkPath(), false));
    }

    @Test(timeout = 60000)
    public void testDeleteNonExistentLogSegment() throws Exception {
        LogSegmentMetadata segment = createLogSegment(1L);
        Transaction<Object> deleteTxn = lsmStore.transaction();
        lsmStore.deleteLogSegment(deleteTxn, segment);
        try {
            FutureUtils.result(deleteTxn.execute());
            Assert.fail("Should fail deletion if log segment doesn't exist");
        } catch (Throwable t) {
            Assert.assertTrue("Should throw NoNodeException if log segment doesn't exist", (t instanceof ZKException));
            ZKException zke = ((ZKException) (t));
            Assert.assertEquals("Should throw NoNodeException if log segment doesn't exist", NONODE, zke.getKeeperExceptionCode());
        }
    }

    @Test(timeout = 60000)
    public void testUpdateNonExistentLogSegment() throws Exception {
        LogSegmentMetadata segment = createLogSegment(1L);
        Transaction<Object> updateTxn = lsmStore.transaction();
        lsmStore.updateLogSegment(updateTxn, segment);
        try {
            FutureUtils.result(updateTxn.execute());
            Assert.fail("Should fail update if log segment doesn't exist");
        } catch (Throwable t) {
            Assert.assertTrue("Should throw NoNodeException if log segment doesn't exist", (t instanceof ZKException));
            ZKException zke = ((ZKException) (t));
            Assert.assertEquals("Should throw NoNodeException if log segment doesn't exist", NONODE, zke.getKeeperExceptionCode());
        }
    }

    @Test(timeout = 60000)
    public void testUpdateLogSegment() throws Exception {
        LogSegmentMetadata segment = createLogSegment(1L, 99L);
        Transaction<Object> createTxn = lsmStore.transaction();
        lsmStore.createLogSegment(createTxn, segment);
        FutureUtils.result(createTxn.execute());
        // the log segment should be created
        Assert.assertNotNull((("LogSegment " + segment) + " should be created"), zkc.get().exists(segment.getZkPath(), false));
        LogSegmentMetadata modifiedSegment = createLogSegment(1L, 999L);
        Transaction<Object> updateTxn = lsmStore.transaction();
        lsmStore.updateLogSegment(updateTxn, modifiedSegment);
        FutureUtils.result(updateTxn.execute());
        // the log segment should be updated
        LogSegmentMetadata readSegment = FutureUtils.result(LogSegmentMetadata.read(zkc, segment.getZkPath(), true));
        Assert.assertEquals("Last entry id should be changed from 99L to 999L", 999L, readSegment.getLastEntryId());
    }

    @Test(timeout = 60000)
    public void testCreateDeleteLogSegmentSuccess() throws Exception {
        LogSegmentMetadata segment1 = createLogSegment(1L);
        LogSegmentMetadata segment2 = createLogSegment(2L);
        // create log segment 1
        Transaction<Object> createTxn = lsmStore.transaction();
        lsmStore.createLogSegment(createTxn, segment1);
        FutureUtils.result(createTxn.execute());
        // the log segment should be created
        Assert.assertNotNull((("LogSegment " + segment1) + " should be created"), zkc.get().exists(segment1.getZkPath(), false));
        // delete log segment 1 and create log segment 2
        Transaction<Object> createDeleteTxn = lsmStore.transaction();
        lsmStore.createLogSegment(createDeleteTxn, segment2);
        lsmStore.deleteLogSegment(createDeleteTxn, segment1);
        FutureUtils.result(createDeleteTxn.execute());
        // segment 1 should be deleted, segment 2 should be created
        Assert.assertNull((("LogSegment " + segment1) + " should be deleted"), zkc.get().exists(segment1.getZkPath(), false));
        Assert.assertNotNull((("LogSegment " + segment2) + " should be created"), zkc.get().exists(segment2.getZkPath(), false));
    }

    @Test(timeout = 60000)
    public void testCreateDeleteLogSegmentFailure() throws Exception {
        LogSegmentMetadata segment1 = createLogSegment(1L);
        LogSegmentMetadata segment2 = createLogSegment(2L);
        LogSegmentMetadata segment3 = createLogSegment(3L);
        // create log segment 1
        Transaction<Object> createTxn = lsmStore.transaction();
        lsmStore.createLogSegment(createTxn, segment1);
        FutureUtils.result(createTxn.execute());
        // the log segment should be created
        Assert.assertNotNull((("LogSegment " + segment1) + " should be created"), zkc.get().exists(segment1.getZkPath(), false));
        // delete log segment 1 and delete log segment 2
        Transaction<Object> createDeleteTxn = lsmStore.transaction();
        lsmStore.deleteLogSegment(createDeleteTxn, segment1);
        lsmStore.deleteLogSegment(createDeleteTxn, segment2);
        lsmStore.createLogSegment(createDeleteTxn, segment3);
        try {
            FutureUtils.result(createDeleteTxn.execute());
            Assert.fail("Should fail transaction if one operation failed");
        } catch (Throwable t) {
            Assert.assertTrue("Transaction is aborted", (t instanceof ZKException));
            ZKException zke = ((ZKException) (t));
            Assert.assertEquals("Transaction is aborted", NONODE, zke.getKeeperExceptionCode());
        }
        // segment 1 should not be deleted
        Assert.assertNotNull((("LogSegment " + segment1) + " should not be deleted"), zkc.get().exists(segment1.getZkPath(), false));
        // segment 3 should not be created
        Assert.assertNull((("LogSegment " + segment3) + " should be created"), zkc.get().exists(segment3.getZkPath(), false));
    }

    @Test(timeout = 60000)
    public void testGetLogSegment() throws Exception {
        LogSegmentMetadata segment = createLogSegment(1L, 99L);
        Transaction<Object> createTxn = lsmStore.transaction();
        lsmStore.createLogSegment(createTxn, segment);
        FutureUtils.result(createTxn.execute());
        // the log segment should be created
        Assert.assertNotNull((("LogSegment " + segment) + " should be created"), zkc.get().exists(segment.getZkPath(), false));
        LogSegmentMetadata readSegment = FutureUtils.result(lsmStore.getLogSegment(segment.getZkPath()));
        Assert.assertEquals("Log segment should match", segment, readSegment);
    }

    @Test(timeout = 60000)
    public void testGetLogSegmentNames() throws Exception {
        Transaction<Object> createTxn = lsmStore.transaction();
        List<LogSegmentMetadata> createdSegments = Lists.newArrayListWithExpectedSize(10);
        for (int i = 0; i < 10; i++) {
            LogSegmentMetadata segment = createLogSegment(i);
            createdSegments.add(segment);
            lsmStore.createLogSegment(createTxn, segment);
        }
        FutureUtils.result(createTxn.execute());
        String rootPath = "/" + (runtime.getMethodName());
        List<String> children = zkc.get().getChildren(rootPath, false);
        Collections.sort(children);
        Assert.assertEquals("Should find 10 log segments", 10, children.size());
        List<String> logSegmentNames = FutureUtils.result(lsmStore.getLogSegmentNames(rootPath));
        Collections.sort(logSegmentNames);
        Assert.assertEquals("Should find 10 log segments", 10, logSegmentNames.size());
        Assert.assertEquals(children, logSegmentNames);
        List<Future<LogSegmentMetadata>> getFutures = Lists.newArrayListWithExpectedSize(10);
        for (int i = 0; i < 10; i++) {
            getFutures.add(lsmStore.getLogSegment(((rootPath + "/") + (logSegmentNames.get(i)))));
        }
        List<LogSegmentMetadata> segments = FutureUtils.result(Future.collect(getFutures));
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(createdSegments.get(i), segments.get(i));
        }
    }

    @Test(timeout = 60000)
    public void testRegisterListenerAfterLSMStoreClosed() throws Exception {
        lsmStore.close();
        LogSegmentMetadata segment = createLogSegment(1L);
        lsmStore.registerLogSegmentListener(segment.getZkPath(), new LogSegmentNamesListener() {
            @Override
            public void onSegmentsUpdated(List<String> segments) {
                // no-op;
            }
        });
        Assert.assertTrue("No listener is registered", lsmStore.listeners.isEmpty());
    }

    @Test(timeout = 60000)
    public void testLogSegmentNamesListener() throws Exception {
        int numSegments = 3;
        Transaction<Object> createTxn = lsmStore.transaction();
        for (int i = 0; i < numSegments; i++) {
            LogSegmentMetadata segment = createLogSegment(i);
            lsmStore.createLogSegment(createTxn, segment);
        }
        FutureUtils.result(createTxn.execute());
        String rootPath = "/" + (runtime.getMethodName());
        List<String> children = zkc.get().getChildren(rootPath, false);
        Collections.sort(children);
        final AtomicInteger numNotifications = new AtomicInteger(0);
        final List<List<String>> segmentLists = Lists.newArrayListWithExpectedSize(2);
        LogSegmentNamesListener listener = new LogSegmentNamesListener() {
            @Override
            public void onSegmentsUpdated(List<String> segments) {
                TestZKLogSegmentMetadataStore.logger.info("Received segments : {}", segments);
                segmentLists.add(segments);
                numNotifications.incrementAndGet();
            }
        };
        lsmStore.registerLogSegmentListener(rootPath, listener);
        Assert.assertEquals(1, lsmStore.listeners.size());
        Assert.assertTrue("Should contain listener", lsmStore.listeners.containsKey(rootPath));
        Assert.assertTrue("Should contain listener", lsmStore.listeners.get(rootPath).contains(listener));
        while ((numNotifications.get()) < 1) {
            TimeUnit.MILLISECONDS.sleep(10);
        } 
        Assert.assertEquals("Should receive one segment list update", 1, numNotifications.get());
        List<String> firstSegmentList = segmentLists.get(0);
        Collections.sort(firstSegmentList);
        Assert.assertEquals("List of segments should be same", children, firstSegmentList);
        TestZKLogSegmentMetadataStore.logger.info("Create another {} segments.", numSegments);
        // create another log segment, it should trigger segment list updated
        Transaction<Object> anotherCreateTxn = lsmStore.transaction();
        for (int i = numSegments; i < (2 * numSegments); i++) {
            LogSegmentMetadata segment = createLogSegment(i);
            lsmStore.createLogSegment(anotherCreateTxn, segment);
        }
        FutureUtils.result(anotherCreateTxn.execute());
        List<String> newChildren = zkc.get().getChildren(rootPath, false);
        Collections.sort(newChildren);
        TestZKLogSegmentMetadataStore.logger.info("All log segments become {}", newChildren);
        while ((numNotifications.get()) < 2) {
            TimeUnit.MILLISECONDS.sleep(10);
        } 
        Assert.assertEquals("Should receive second segment list update", 2, numNotifications.get());
        List<String> secondSegmentList = segmentLists.get(1);
        Collections.sort(secondSegmentList);
        Assert.assertEquals("List of segments should be updated", (2 * numSegments), secondSegmentList.size());
        Assert.assertEquals("List of segments should be updated", newChildren, secondSegmentList);
    }

    @Test(timeout = 60000)
    public void testLogSegmentNamesListenerOnDeletion() throws Exception {
        int numSegments = 3;
        Transaction<Object> createTxn = lsmStore.transaction();
        for (int i = 0; i < numSegments; i++) {
            LogSegmentMetadata segment = createLogSegment(i);
            lsmStore.createLogSegment(createTxn, segment);
        }
        FutureUtils.result(createTxn.execute());
        String rootPath = "/" + (runtime.getMethodName());
        List<String> children = zkc.get().getChildren(rootPath, false);
        Collections.sort(children);
        final AtomicInteger numNotifications = new AtomicInteger(0);
        final List<List<String>> segmentLists = Lists.newArrayListWithExpectedSize(2);
        LogSegmentNamesListener listener = new LogSegmentNamesListener() {
            @Override
            public void onSegmentsUpdated(List<String> segments) {
                TestZKLogSegmentMetadataStore.logger.info("Received segments : {}", segments);
                segmentLists.add(segments);
                numNotifications.incrementAndGet();
            }
        };
        lsmStore.registerLogSegmentListener(rootPath, listener);
        Assert.assertEquals(1, lsmStore.listeners.size());
        Assert.assertTrue("Should contain listener", lsmStore.listeners.containsKey(rootPath));
        Assert.assertTrue("Should contain listener", lsmStore.listeners.get(rootPath).contains(listener));
        while ((numNotifications.get()) < 1) {
            TimeUnit.MILLISECONDS.sleep(10);
        } 
        Assert.assertEquals("Should receive one segment list update", 1, numNotifications.get());
        List<String> firstSegmentList = segmentLists.get(0);
        Collections.sort(firstSegmentList);
        Assert.assertEquals("List of segments should be same", children, firstSegmentList);
        // delete all log segments, it should trigger segment list updated
        Transaction<Object> deleteTxn = lsmStore.transaction();
        for (int i = 0; i < numSegments; i++) {
            LogSegmentMetadata segment = createLogSegment(i);
            lsmStore.deleteLogSegment(deleteTxn, segment);
        }
        FutureUtils.result(deleteTxn.execute());
        List<String> newChildren = zkc.get().getChildren(rootPath, false);
        Collections.sort(newChildren);
        while ((numNotifications.get()) < 2) {
            TimeUnit.MILLISECONDS.sleep(10);
        } 
        Assert.assertEquals("Should receive second segment list update", 2, numNotifications.get());
        List<String> secondSegmentList = segmentLists.get(1);
        Collections.sort(secondSegmentList);
        Assert.assertEquals("List of segments should be updated", 0, secondSegmentList.size());
        Assert.assertEquals("List of segments should be updated", newChildren, secondSegmentList);
        // delete the root path
        zkc.get().delete(rootPath, (-1));
        while (!(lsmStore.listeners.isEmpty())) {
            TimeUnit.MILLISECONDS.sleep(10);
        } 
        Assert.assertTrue("listener should be removed after root path is deleted", lsmStore.listeners.isEmpty());
    }

    @Test(timeout = 60000)
    public void testLogSegmentNamesListenerOnSessionExpired() throws Exception {
        int numSegments = 3;
        Transaction<Object> createTxn = lsmStore.transaction();
        for (int i = 0; i < numSegments; i++) {
            LogSegmentMetadata segment = createLogSegment(i);
            lsmStore.createLogSegment(createTxn, segment);
        }
        FutureUtils.result(createTxn.execute());
        String rootPath = "/" + (runtime.getMethodName());
        List<String> children = zkc.get().getChildren(rootPath, false);
        Collections.sort(children);
        final AtomicInteger numNotifications = new AtomicInteger(0);
        final List<List<String>> segmentLists = Lists.newArrayListWithExpectedSize(2);
        LogSegmentNamesListener listener = new LogSegmentNamesListener() {
            @Override
            public void onSegmentsUpdated(List<String> segments) {
                TestZKLogSegmentMetadataStore.logger.info("Received segments : {}", segments);
                segmentLists.add(segments);
                numNotifications.incrementAndGet();
            }
        };
        lsmStore.registerLogSegmentListener(rootPath, listener);
        Assert.assertEquals(1, lsmStore.listeners.size());
        Assert.assertTrue("Should contain listener", lsmStore.listeners.containsKey(rootPath));
        Assert.assertTrue("Should contain listener", lsmStore.listeners.get(rootPath).contains(listener));
        while ((numNotifications.get()) < 1) {
            TimeUnit.MILLISECONDS.sleep(10);
        } 
        Assert.assertEquals("Should receive one segment list update", 1, numNotifications.get());
        List<String> firstSegmentList = segmentLists.get(0);
        Collections.sort(firstSegmentList);
        Assert.assertEquals("List of segments should be same", children, firstSegmentList);
        ZooKeeperClientUtils.expireSession(zkc, DLUtils.getZKServersFromDLUri(uri), TestDistributedLogBase.conf.getZKSessionTimeoutMilliseconds());
        while ((numNotifications.get()) < 2) {
            TimeUnit.MILLISECONDS.sleep(10);
        } 
        Assert.assertEquals("Should receive second segment list update", 2, numNotifications.get());
        List<String> secondSegmentList = segmentLists.get(1);
        Collections.sort(secondSegmentList);
        Assert.assertEquals("List of segments should be same", children, secondSegmentList);
        TestZKLogSegmentMetadataStore.logger.info("Create another {} segments.", numSegments);
        // create another log segment, it should trigger segment list updated
        Transaction<Object> anotherCreateTxn = lsmStore.transaction();
        for (int i = numSegments; i < (2 * numSegments); i++) {
            LogSegmentMetadata segment = createLogSegment(i);
            lsmStore.createLogSegment(anotherCreateTxn, segment);
        }
        FutureUtils.result(anotherCreateTxn.execute());
        List<String> newChildren = zkc.get().getChildren(rootPath, false);
        Collections.sort(newChildren);
        TestZKLogSegmentMetadataStore.logger.info("All log segments become {}", newChildren);
        while ((numNotifications.get()) < 3) {
            TimeUnit.MILLISECONDS.sleep(10);
        } 
        Assert.assertEquals("Should receive third segment list update", 3, numNotifications.get());
        List<String> thirdSegmentList = segmentLists.get(2);
        Collections.sort(thirdSegmentList);
        Assert.assertEquals("List of segments should be updated", (2 * numSegments), thirdSegmentList.size());
        Assert.assertEquals("List of segments should be updated", newChildren, thirdSegmentList);
    }

    @Test(timeout = 60000)
    public void testStoreMaxLogSegmentSequenceNumber() throws Exception {
        Transaction<Object> updateTxn = lsmStore.transaction();
        Versioned<Long> value = new Versioned<Long>(999L, new ZkVersion(0));
        final Promise<Version> result = new Promise<Version>();
        lsmStore.storeMaxLogSegmentSequenceNumber(updateTxn, rootZkPath, value, new Transaction.OpListener<Version>() {
            @Override
            public void onCommit(Version r) {
                result.setValue(r);
            }

            @Override
            public void onAbort(Throwable t) {
                result.setException(t);
            }
        });
        FutureUtils.result(updateTxn.execute());
        Assert.assertEquals(1, getZnodeVersion());
        Stat stat = new Stat();
        byte[] data = zkc.get().getData(rootZkPath, false, stat);
        Assert.assertEquals(999L, DLUtils.deserializeLogSegmentSequenceNumber(data));
        Assert.assertEquals(1, stat.getVersion());
    }

    @Test(timeout = 60000)
    public void testStoreMaxLogSegmentSequenceNumberBadVersion() throws Exception {
        Transaction<Object> updateTxn = lsmStore.transaction();
        Versioned<Long> value = new Versioned<Long>(999L, new ZkVersion(10));
        final Promise<Version> result = new Promise<Version>();
        lsmStore.storeMaxLogSegmentSequenceNumber(updateTxn, rootZkPath, value, new Transaction.OpListener<Version>() {
            @Override
            public void onCommit(Version r) {
                result.setValue(r);
            }

            @Override
            public void onAbort(Throwable t) {
                result.setException(t);
            }
        });
        try {
            FutureUtils.result(updateTxn.execute());
            Assert.fail("Should fail on storing log segment sequence number if providing bad version");
        } catch (ZKException zke) {
            Assert.assertEquals(BADVERSION, zke.getKeeperExceptionCode());
        }
        try {
            Await.result(result);
            Assert.fail("Should fail on storing log segment sequence number if providing bad version");
        } catch (KeeperException ke) {
            Assert.assertEquals(BADVERSION, ke.code());
        }
        Stat stat = new Stat();
        byte[] data = zkc.get().getData(rootZkPath, false, stat);
        Assert.assertEquals(0, stat.getVersion());
        Assert.assertEquals(0, data.length);
    }

    @Test(timeout = 60000)
    public void testStoreMaxLogSegmentSequenceNumberOnNonExistentPath() throws Exception {
        Transaction<Object> updateTxn = lsmStore.transaction();
        Versioned<Long> value = new Versioned<Long>(999L, new ZkVersion(10));
        final Promise<Version> result = new Promise<Version>();
        String nonExistentPath = (rootZkPath) + "/non-existent";
        lsmStore.storeMaxLogSegmentSequenceNumber(updateTxn, nonExistentPath, value, new Transaction.OpListener<Version>() {
            @Override
            public void onCommit(Version r) {
                result.setValue(r);
            }

            @Override
            public void onAbort(Throwable t) {
                result.setException(t);
            }
        });
        try {
            FutureUtils.result(updateTxn.execute());
            Assert.fail("Should fail on storing log segment sequence number if path doesn't exist");
        } catch (ZKException zke) {
            Assert.assertEquals(NONODE, zke.getKeeperExceptionCode());
        }
        try {
            Await.result(result);
            Assert.fail("Should fail on storing log segment sequence number if path doesn't exist");
        } catch (KeeperException ke) {
            Assert.assertEquals(NONODE, ke.code());
        }
    }

    @Test(timeout = 60000)
    public void testStoreMaxTxnId() throws Exception {
        Transaction<Object> updateTxn = lsmStore.transaction();
        Versioned<Long> value = new Versioned<Long>(999L, new ZkVersion(0));
        final Promise<Version> result = new Promise<Version>();
        lsmStore.storeMaxTxnId(updateTxn, rootZkPath, value, new Transaction.OpListener<Version>() {
            @Override
            public void onCommit(Version r) {
                result.setValue(r);
            }

            @Override
            public void onAbort(Throwable t) {
                result.setException(t);
            }
        });
        FutureUtils.result(updateTxn.execute());
        Assert.assertEquals(1, getZnodeVersion());
        Stat stat = new Stat();
        byte[] data = zkc.get().getData(rootZkPath, false, stat);
        Assert.assertEquals(999L, DLUtils.deserializeTransactionId(data));
        Assert.assertEquals(1, stat.getVersion());
    }

    @Test(timeout = 60000)
    public void testStoreMaxTxnIdBadVersion() throws Exception {
        Transaction<Object> updateTxn = lsmStore.transaction();
        Versioned<Long> value = new Versioned<Long>(999L, new ZkVersion(10));
        final Promise<Version> result = new Promise<Version>();
        lsmStore.storeMaxTxnId(updateTxn, rootZkPath, value, new Transaction.OpListener<Version>() {
            @Override
            public void onCommit(Version r) {
                result.setValue(r);
            }

            @Override
            public void onAbort(Throwable t) {
                result.setException(t);
            }
        });
        try {
            FutureUtils.result(updateTxn.execute());
            Assert.fail("Should fail on storing log record transaction id if providing bad version");
        } catch (ZKException zke) {
            Assert.assertEquals(BADVERSION, zke.getKeeperExceptionCode());
        }
        try {
            Await.result(result);
            Assert.fail("Should fail on storing log record transaction id if providing bad version");
        } catch (KeeperException ke) {
            Assert.assertEquals(BADVERSION, ke.code());
        }
        Stat stat = new Stat();
        byte[] data = zkc.get().getData(rootZkPath, false, stat);
        Assert.assertEquals(0, stat.getVersion());
        Assert.assertEquals(0, data.length);
    }

    @Test(timeout = 60000)
    public void testStoreMaxTxnIdOnNonExistentPath() throws Exception {
        Transaction<Object> updateTxn = lsmStore.transaction();
        Versioned<Long> value = new Versioned<Long>(999L, new ZkVersion(10));
        final Promise<Version> result = new Promise<Version>();
        String nonExistentPath = (rootZkPath) + "/non-existent";
        lsmStore.storeMaxLogSegmentSequenceNumber(updateTxn, nonExistentPath, value, new Transaction.OpListener<Version>() {
            @Override
            public void onCommit(Version r) {
                result.setValue(r);
            }

            @Override
            public void onAbort(Throwable t) {
                result.setException(t);
            }
        });
        try {
            FutureUtils.result(updateTxn.execute());
            Assert.fail("Should fail on storing log record transaction id if path doesn't exist");
        } catch (ZKException zke) {
            Assert.assertEquals(NONODE, zke.getKeeperExceptionCode());
        }
        try {
            Await.result(result);
            Assert.fail("Should fail on storing log record transaction id if path doesn't exist");
        } catch (KeeperException ke) {
            Assert.assertEquals(NONODE, ke.code());
        }
    }
}

