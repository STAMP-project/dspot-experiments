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
package com.twitter.distributedlog.metadata;


import CreateMode.PERSISTENT;
import DistributedLogConstants.FIRST_LOGSEGMENT_SEQNO;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import com.twitter.distributedlog.DistributedLogConfiguration;
import com.twitter.distributedlog.ZooKeeperClient;
import com.twitter.distributedlog.ZooKeeperClusterTestCase;
import com.twitter.distributedlog.util.Utils;
import java.io.IOException;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;


public class TestZkMetadataResolver extends ZooKeeperClusterTestCase {
    private static final BKDLConfig bkdlConfig = new BKDLConfig("127.0.0.1:7000", "ledgers");

    private static final BKDLConfig bkdlConfig2 = new BKDLConfig("127.0.0.1:7000", "ledgers2");

    private ZooKeeperClient zkc;

    private ZkMetadataResolver resolver;

    @Test(timeout = 60000)
    public void testResolveFailures() throws Exception {
        // resolve unexisted path
        try {
            resolver.resolve(createURI("/unexisted/path"));
            Assert.fail("Should fail if no metadata resolved.");
        } catch (IOException e) {
            // expected
        }
        // resolve existed unbound path
        Utils.zkCreateFullPathOptimistic(zkc, "/existed/path", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        try {
            resolver.resolve(createURI("/existed/path"));
            Assert.fail("Should fail if no metadata resolved.");
        } catch (IOException e) {
            // expected
        }
    }

    @Test(timeout = 60000)
    public void testResolve() throws Exception {
        DLMetadata dlMetadata = DLMetadata.create(TestZkMetadataResolver.bkdlConfig);
        dlMetadata.create(createURI("/messaging/distributedlog-testresolve"));
        DLMetadata dlMetadata2 = DLMetadata.create(TestZkMetadataResolver.bkdlConfig2);
        dlMetadata2.create(createURI("/messaging/distributedlog-testresolve/child"));
        Assert.assertEquals(dlMetadata, resolver.resolve(createURI("/messaging/distributedlog-testresolve")));
        Assert.assertEquals(dlMetadata2, resolver.resolve(createURI("/messaging/distributedlog-testresolve/child")));
        Assert.assertEquals(dlMetadata2, resolver.resolve(createURI("/messaging/distributedlog-testresolve/child/unknown")));
        Utils.zkCreateFullPathOptimistic(zkc, "/messaging/distributedlog-testresolve/child/child2", new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        Assert.assertEquals(dlMetadata2, resolver.resolve(createURI("/messaging/distributedlog-testresolve/child/child2")));
    }

    @Test(timeout = 60000)
    public void testEncodeRegionID() throws Exception {
        DistributedLogConfiguration dlConf = new DistributedLogConfiguration();
        URI uri = createURI("/messaging/distributedlog-testencoderegionid/dl1");
        DLMetadata meta1 = DLMetadata.create(new BKDLConfig("127.0.0.1:7000", "ledgers"));
        meta1.create(uri);
        BKDLConfig read1 = BKDLConfig.resolveDLConfig(zkc, uri);
        BKDLConfig.propagateConfiguration(read1, dlConf);
        Assert.assertFalse(dlConf.getEncodeRegionIDInLogSegmentMetadata());
        BKDLConfig.clearCachedDLConfigs();
        DLMetadata meta2 = DLMetadata.create(new BKDLConfig("127.0.0.1:7000", "ledgers").setEncodeRegionID(true));
        meta2.update(uri);
        BKDLConfig read2 = BKDLConfig.resolveDLConfig(zkc, uri);
        BKDLConfig.propagateConfiguration(read2, dlConf);
        Assert.assertTrue(dlConf.getEncodeRegionIDInLogSegmentMetadata());
        BKDLConfig.clearCachedDLConfigs();
        DLMetadata meta3 = DLMetadata.create(new BKDLConfig("127.0.0.1:7000", "ledgers").setEncodeRegionID(false));
        meta3.update(uri);
        BKDLConfig read3 = BKDLConfig.resolveDLConfig(zkc, uri);
        BKDLConfig.propagateConfiguration(read3, dlConf);
        Assert.assertFalse(dlConf.getEncodeRegionIDInLogSegmentMetadata());
        BKDLConfig.clearCachedDLConfigs();
    }

    @Test(timeout = 60000)
    public void testFirstLogSegmentSequenceNumber() throws Exception {
        DistributedLogConfiguration dlConf = new DistributedLogConfiguration();
        URI uri = createURI("/messaging/distributedlog-testfirstledgerseqno/dl1");
        DLMetadata meta1 = DLMetadata.create(new BKDLConfig("127.0.0.1:7000", "ledgers"));
        meta1.create(uri);
        BKDLConfig read1 = BKDLConfig.resolveDLConfig(zkc, uri);
        BKDLConfig.propagateConfiguration(read1, dlConf);
        Assert.assertEquals(FIRST_LOGSEGMENT_SEQNO, dlConf.getFirstLogSegmentSequenceNumber());
        BKDLConfig.clearCachedDLConfigs();
        DLMetadata meta2 = DLMetadata.create(new BKDLConfig("127.0.0.1:7000", "ledgers").setFirstLogSegmentSeqNo(9999L));
        meta2.update(uri);
        BKDLConfig read2 = BKDLConfig.resolveDLConfig(zkc, uri);
        BKDLConfig.propagateConfiguration(read2, dlConf);
        Assert.assertEquals(9999L, dlConf.getFirstLogSegmentSequenceNumber());
        BKDLConfig.clearCachedDLConfigs();
        DLMetadata meta3 = DLMetadata.create(new BKDLConfig("127.0.0.1:7000", "ledgers").setFirstLogSegmentSeqNo(99L));
        meta3.update(uri);
        BKDLConfig read3 = BKDLConfig.resolveDLConfig(zkc, uri);
        BKDLConfig.propagateConfiguration(read3, dlConf);
        Assert.assertEquals(99L, dlConf.getFirstLogSegmentSequenceNumber());
        BKDLConfig.clearCachedDLConfigs();
    }

    @Test(timeout = 60000)
    public void testFederatedNamespace() throws Exception {
        DistributedLogConfiguration dlConf = new DistributedLogConfiguration();
        URI uri = createURI("/messaging/distributedlog-testfederatednamespace/dl1");
        DLMetadata meta1 = DLMetadata.create(new BKDLConfig("127.0.0.1:7000", "ledgers"));
        meta1.create(uri);
        BKDLConfig read1 = BKDLConfig.resolveDLConfig(zkc, uri);
        BKDLConfig.propagateConfiguration(read1, dlConf);
        Assert.assertTrue(dlConf.getCreateStreamIfNotExists());
        BKDLConfig.clearCachedDLConfigs();
        DLMetadata meta2 = DLMetadata.create(new BKDLConfig("127.0.0.1:7000", "ledgers").setFederatedNamespace(true));
        meta2.update(uri);
        BKDLConfig read2 = BKDLConfig.resolveDLConfig(zkc, uri);
        BKDLConfig.propagateConfiguration(read2, dlConf);
        Assert.assertFalse(dlConf.getCreateStreamIfNotExists());
        BKDLConfig.clearCachedDLConfigs();
        DLMetadata meta3 = DLMetadata.create(new BKDLConfig("127.0.0.1:7000", "ledgers").setFederatedNamespace(false));
        meta3.update(uri);
        BKDLConfig read3 = BKDLConfig.resolveDLConfig(zkc, uri);
        BKDLConfig.propagateConfiguration(read3, dlConf);
        // if it is non-federated namespace, it won't change the create stream behavior.
        Assert.assertFalse(dlConf.getCreateStreamIfNotExists());
        BKDLConfig.clearCachedDLConfigs();
    }
}

