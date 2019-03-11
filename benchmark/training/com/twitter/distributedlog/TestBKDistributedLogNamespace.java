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
package com.twitter.distributedlog;


import CreateMode.PERSISTENT;
import DistributedLogManagerFactory.ClientSharingOption.PerStreamClients;
import DistributedLogManagerFactory.ClientSharingOption.SharedClients;
import DistributedLogManagerFactory.ClientSharingOption.SharedZKClientPerStreamBKClient;
import KeeperException.Code.NOAUTH;
import KeeperException.NoAuthException;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import com.google.common.collect.Sets;
import com.twitter.distributedlog.callback.NamespaceListener;
import com.twitter.distributedlog.exceptions.InvalidStreamNameException;
import com.twitter.distributedlog.exceptions.LockingException;
import com.twitter.distributedlog.exceptions.ZKException;
import com.twitter.distributedlog.impl.BKDLUtils;
import com.twitter.distributedlog.namespace.DistributedLogNamespace;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestBKDistributedLogNamespace extends TestDistributedLogBase {
    @Rule
    public TestName runtime = new TestName();

    static final Logger LOG = LoggerFactory.getLogger(TestBKDistributedLogNamespace.class);

    protected static DistributedLogConfiguration conf = new DistributedLogConfiguration().setLockTimeout(10).setEnableLedgerAllocatorPool(true).setLedgerAllocatorPoolName("test");

    private ZooKeeperClient zooKeeperClient;

    @Test(timeout = 60000)
    public void testCreateIfNotExists() throws Exception {
        URI uri = createDLMURI(("/" + (runtime.getMethodName())));
        ensureURICreated(zooKeeperClient.get(), uri);
        DistributedLogConfiguration newConf = new DistributedLogConfiguration();
        newConf.addConfiguration(TestBKDistributedLogNamespace.conf);
        newConf.setCreateStreamIfNotExists(false);
        String streamName = "test-stream";
        DistributedLogNamespace namespace = com.twitter.distributedlog.namespace.DistributedLogNamespaceBuilder.newBuilder().conf(newConf).uri(uri).build();
        DistributedLogManager dlm = namespace.openLog(streamName);
        LogWriter writer;
        try {
            writer = dlm.startLogSegmentNonPartitioned();
            writer.write(DLMTestUtil.getLogRecordInstance(1L));
            Assert.fail("Should fail to write data if stream doesn't exist.");
        } catch (IOException ioe) {
            // expected
        }
        dlm.close();
        // create the stream
        BKDistributedLogManager.createLog(TestBKDistributedLogNamespace.conf, zooKeeperClient, uri, streamName);
        DistributedLogManager newDLM = namespace.openLog(streamName);
        LogWriter newWriter = newDLM.startLogSegmentNonPartitioned();
        newWriter.write(DLMTestUtil.getLogRecordInstance(1L));
        newWriter.close();
        newDLM.close();
    }

    @Test(timeout = 60000)
    @SuppressWarnings("deprecation")
    public void testClientSharingOptions() throws Exception {
        URI uri = createDLMURI("/clientSharingOptions");
        BKDistributedLogNamespace namespace = BKDistributedLogNamespace.newBuilder().conf(TestBKDistributedLogNamespace.conf).uri(uri).build();
        {
            BKDistributedLogManager bkdlm1 = ((BKDistributedLogManager) (namespace.createDistributedLogManager("perstream1", PerStreamClients)));
            BKDistributedLogManager bkdlm2 = ((BKDistributedLogManager) (namespace.createDistributedLogManager("perstream2", PerStreamClients)));
            assert (bkdlm1.getReaderBKC()) != (bkdlm2.getReaderBKC());
            assert (bkdlm1.getWriterBKC()) != (bkdlm2.getWriterBKC());
            assert (bkdlm1.getReaderZKC()) != (bkdlm2.getReaderZKC());
            assert (bkdlm1.getWriterZKC()) != (bkdlm2.getWriterZKC());
        }
        {
            BKDistributedLogManager bkdlm1 = ((BKDistributedLogManager) (namespace.createDistributedLogManager("sharedZK1", SharedZKClientPerStreamBKClient)));
            BKDistributedLogManager bkdlm2 = ((BKDistributedLogManager) (namespace.createDistributedLogManager("sharedZK2", SharedZKClientPerStreamBKClient)));
            assert (bkdlm1.getReaderBKC()) != (bkdlm2.getReaderBKC());
            assert (bkdlm1.getWriterBKC()) != (bkdlm2.getWriterBKC());
            assert (bkdlm1.getReaderZKC()) == (bkdlm2.getReaderZKC());
            assert (bkdlm1.getWriterZKC()) == (bkdlm2.getWriterZKC());
        }
        {
            BKDistributedLogManager bkdlm1 = ((BKDistributedLogManager) (namespace.createDistributedLogManager("sharedBoth1", SharedClients)));
            BKDistributedLogManager bkdlm2 = ((BKDistributedLogManager) (namespace.createDistributedLogManager("sharedBoth2", SharedClients)));
            assert (bkdlm1.getReaderBKC()) == (bkdlm2.getReaderBKC());
            assert (bkdlm1.getWriterBKC()) == (bkdlm2.getWriterBKC());
            assert (bkdlm1.getReaderZKC()) == (bkdlm2.getReaderZKC());
            assert (bkdlm1.getWriterZKC()) == (bkdlm2.getWriterZKC());
        }
    }

    @Test(timeout = 60000)
    public void testInvalidStreamName() throws Exception {
        Assert.assertFalse(BKDLUtils.isReservedStreamName("test"));
        Assert.assertTrue(BKDLUtils.isReservedStreamName(".test"));
        URI uri = createDLMURI(("/" + (runtime.getMethodName())));
        BKDistributedLogNamespace namespace = BKDistributedLogNamespace.newBuilder().conf(TestBKDistributedLogNamespace.conf).uri(uri).build();
        try {
            namespace.openLog(".test1");
            Assert.fail("Should fail to create invalid stream .test");
        } catch (InvalidStreamNameException isne) {
            // expected
        }
        DistributedLogManager dlm = namespace.openLog("test1");
        LogWriter writer = dlm.startLogSegmentNonPartitioned();
        writer.write(DLMTestUtil.getLogRecordInstance(1));
        writer.close();
        dlm.close();
        try {
            namespace.openLog(".test2");
            Assert.fail("Should fail to create invalid stream .test2");
        } catch (InvalidStreamNameException isne) {
            // expected
        }
        try {
            namespace.openLog("/test2");
            Assert.fail("should fail to create invalid stream /test2");
        } catch (InvalidStreamNameException isne) {
            // expected
        }
        try {
            char[] chars = new char[6];
            for (int i = 0; i < (chars.length); i++) {
                chars[i] = 'a';
            }
            chars[0] = 0;
            String streamName = new String(chars);
            namespace.openLog(streamName);
            Assert.fail(("should fail to create invalid stream " + streamName));
        } catch (InvalidStreamNameException isne) {
            // expected
        }
        try {
            char[] chars = new char[6];
            for (int i = 0; i < (chars.length); i++) {
                chars[i] = 'a';
            }
            chars[3] = '\u0010';
            String streamName = new String(chars);
            namespace.openLog(streamName);
            Assert.fail(("should fail to create invalid stream " + streamName));
        } catch (InvalidStreamNameException isne) {
            // expected
        }
        DistributedLogManager newDLM = namespace.openLog("test_2-3");
        LogWriter newWriter = newDLM.startLogSegmentNonPartitioned();
        newWriter.write(DLMTestUtil.getLogRecordInstance(1));
        newWriter.close();
        newDLM.close();
        Iterator<String> streamIter = namespace.getLogs();
        Set<String> streamSet = Sets.newHashSet(streamIter);
        Assert.assertEquals(2, streamSet.size());
        Assert.assertTrue(streamSet.contains("test1"));
        Assert.assertTrue(streamSet.contains("test_2-3"));
        Map<String, byte[]> streamMetadatas = namespace.enumerateLogsWithMetadataInNamespace();
        Assert.assertEquals(2, streamMetadatas.size());
        Assert.assertTrue(streamMetadatas.containsKey("test1"));
        Assert.assertTrue(streamMetadatas.containsKey("test_2-3"));
        namespace.close();
    }

    @Test(timeout = 60000)
    public void testNamespaceListener() throws Exception {
        URI uri = createDLMURI(("/" + (runtime.getMethodName())));
        zooKeeperClient.get().create(uri.getPath(), new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        DistributedLogNamespace namespace = com.twitter.distributedlog.namespace.DistributedLogNamespaceBuilder.newBuilder().conf(TestBKDistributedLogNamespace.conf).uri(uri).build();
        final CountDownLatch[] latches = new CountDownLatch[3];
        for (int i = 0; i < 3; i++) {
            latches[i] = new CountDownLatch(1);
        }
        final AtomicInteger numUpdates = new AtomicInteger(0);
        final AtomicInteger numFailures = new AtomicInteger(0);
        final AtomicReference<Collection<String>> receivedStreams = new AtomicReference<Collection<String>>(null);
        namespace.registerNamespaceListener(new NamespaceListener() {
            @Override
            public void onStreamsChanged(Iterator<String> streams) {
                Set<String> streamSet = Sets.newHashSet(streams);
                int updates = numUpdates.incrementAndGet();
                if ((streamSet.size()) != (updates - 1)) {
                    numFailures.incrementAndGet();
                }
                receivedStreams.set(streamSet);
                latches[(updates - 1)].countDown();
            }
        });
        latches[0].await();
        BKDistributedLogManager.createLog(TestBKDistributedLogNamespace.conf, zooKeeperClient, uri, "test1");
        latches[1].await();
        BKDistributedLogManager.createLog(TestBKDistributedLogNamespace.conf, zooKeeperClient, uri, "test2");
        latches[2].await();
        Assert.assertEquals(0, numFailures.get());
        Assert.assertNotNull(receivedStreams.get());
        Set<String> streamSet = new HashSet<String>();
        streamSet.addAll(receivedStreams.get());
        Assert.assertEquals(2, receivedStreams.get().size());
        Assert.assertEquals(2, streamSet.size());
        Assert.assertTrue(streamSet.contains("test1"));
        Assert.assertTrue(streamSet.contains("test2"));
    }

    @Test(timeout = 60000)
    public void testAclPermsZkAccessConflict() throws Exception {
        String namespace = "/" + (runtime.getMethodName());
        initDlogMeta(namespace, "test-un", "test-stream");
        URI uri = createDLMURI(namespace);
        ZooKeeperClient zkc = TestZooKeeperClientBuilder.newBuilder().name("unpriv").uri(uri).build();
        try {
            zkc.get().create(((uri.getPath()) + "/test-stream/test-garbage"), new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
            Assert.fail("write should have failed due to perms");
        } catch (KeeperException ex) {
            TestBKDistributedLogNamespace.LOG.info("caught exception trying to write with no perms", ex);
        }
        try {
            zkc.get().setData(((uri.getPath()) + "/test-stream"), new byte[0], 0);
            Assert.fail("write should have failed due to perms");
        } catch (KeeperException ex) {
            TestBKDistributedLogNamespace.LOG.info("caught exception trying to write with no perms", ex);
        }
    }

    @Test(timeout = 60000)
    public void testAclPermsZkAccessNoConflict() throws Exception {
        String namespace = "/" + (runtime.getMethodName());
        initDlogMeta(namespace, "test-un", "test-stream");
        URI uri = createDLMURI(namespace);
        ZooKeeperClient zkc = TestZooKeeperClientBuilder.newBuilder().name("unpriv").uri(uri).build();
        zkc.get().getChildren(((uri.getPath()) + "/test-stream"), false, new Stat());
        zkc.get().getData(((uri.getPath()) + "/test-stream"), false, new Stat());
    }

    @Test(timeout = 60000)
    public void testAclModifyPermsDlmConflict() throws Exception {
        String streamName = "test-stream";
        // Reopening and writing again with the same un will succeed.
        initDlogMeta(("/" + (runtime.getMethodName())), "test-un", streamName);
        try {
            // Reopening and writing again with a different un will fail.
            initDlogMeta(("/" + (runtime.getMethodName())), "not-test-un", streamName);
            Assert.fail("write should have failed due to perms");
        } catch (ZKException ex) {
            TestBKDistributedLogNamespace.LOG.info("caught exception trying to write with no perms {}", ex);
            Assert.assertEquals(NOAUTH, ex.getKeeperExceptionCode());
        } catch (Exception ex) {
            TestBKDistributedLogNamespace.LOG.info("caught wrong exception trying to write with no perms {}", ex);
            Assert.fail(((("wrong exception " + (ex.getClass().getName())) + " expected ") + (LockingException.class.getName())));
        }
        // Should work again.
        initDlogMeta(("/" + (runtime.getMethodName())), "test-un", streamName);
    }

    @Test(timeout = 60000)
    public void testAclModifyPermsDlmNoConflict() throws Exception {
        String streamName = "test-stream";
        // Establish the uri.
        initDlogMeta(("/" + (runtime.getMethodName())), "test-un", streamName);
        // Reopening and writing again with the same un will succeed.
        initDlogMeta(("/" + (runtime.getMethodName())), "test-un", streamName);
    }

    @Test(timeout = 60000)
    public void testValidateAndGetFullLedgerAllocatorPoolPath() throws Exception {
        DistributedLogConfiguration testConf = new DistributedLogConfiguration();
        testConf.setEnableLedgerAllocatorPool(true);
        String namespace = "/" + (runtime.getMethodName());
        URI uri = createDLMURI(namespace);
        testConf.setLedgerAllocatorPoolName("test");
        testConf.setLedgerAllocatorPoolPath("test");
        TestBKDistributedLogNamespace.validateBadAllocatorConfiguration(testConf, uri);
        testConf.setLedgerAllocatorPoolPath(".");
        TestBKDistributedLogNamespace.validateBadAllocatorConfiguration(testConf, uri);
        testConf.setLedgerAllocatorPoolPath("..");
        TestBKDistributedLogNamespace.validateBadAllocatorConfiguration(testConf, uri);
        testConf.setLedgerAllocatorPoolPath("./");
        TestBKDistributedLogNamespace.validateBadAllocatorConfiguration(testConf, uri);
        testConf.setLedgerAllocatorPoolPath(".test/");
        TestBKDistributedLogNamespace.validateBadAllocatorConfiguration(testConf, uri);
        testConf.setLedgerAllocatorPoolPath(".test");
        testConf.setLedgerAllocatorPoolName(null);
        TestBKDistributedLogNamespace.validateBadAllocatorConfiguration(testConf, uri);
    }
}

