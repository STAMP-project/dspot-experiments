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
package org.apache.hadoop.hbase.client;


import HConstants.ZOOKEEPER_QUORUM;
import TableName.META_TABLE_NAME;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.RegionLocations;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.zookeeper.ReadOnlyZKClient;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MediumTests.class, ClientTests.class })
public class TestZKAsyncRegistry {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestZKAsyncRegistry.class);

    static final Logger LOG = LoggerFactory.getLogger(TestZKAsyncRegistry.class);

    static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static ZKAsyncRegistry REGISTRY;

    @Test
    public void test() throws IOException, InterruptedException, ExecutionException {
        TestZKAsyncRegistry.LOG.info("STARTED TEST");
        String clusterId = TestZKAsyncRegistry.REGISTRY.getClusterId().get();
        String expectedClusterId = TestZKAsyncRegistry.TEST_UTIL.getHBaseCluster().getMaster().getClusterId();
        Assert.assertEquals(((("Expected " + expectedClusterId) + ", found=") + clusterId), expectedClusterId, clusterId);
        Assert.assertEquals(TestZKAsyncRegistry.TEST_UTIL.getHBaseCluster().getClusterMetrics().getLiveServerMetrics().size(), TestZKAsyncRegistry.REGISTRY.getCurrentNrHRS().get().intValue());
        Assert.assertEquals(TestZKAsyncRegistry.TEST_UTIL.getHBaseCluster().getMaster().getServerName(), TestZKAsyncRegistry.REGISTRY.getMasterAddress().get());
        Assert.assertEquals((-1), TestZKAsyncRegistry.REGISTRY.getMasterInfoPort().get().intValue());
        RegionReplicaTestHelper.waitUntilAllMetaReplicasHavingRegionLocation(TestZKAsyncRegistry.REGISTRY, 3);
        RegionLocations locs = TestZKAsyncRegistry.REGISTRY.getMetaRegionLocation().get();
        Assert.assertEquals(3, locs.getRegionLocations().length);
        IntStream.range(0, 3).forEach(( i) -> {
            HRegionLocation loc = locs.getRegionLocation(i);
            Assert.assertNotNull((("Replica " + i) + " doesn't have location"), loc);
            Assert.assertEquals(META_TABLE_NAME, loc.getRegion().getTable());
            Assert.assertEquals(i, loc.getRegion().getReplicaId());
        });
    }

    @Test
    public void testIndependentZKConnections() throws IOException {
        try (ReadOnlyZKClient zk1 = TestZKAsyncRegistry.REGISTRY.getZKClient()) {
            Configuration otherConf = new Configuration(TestZKAsyncRegistry.TEST_UTIL.getConfiguration());
            otherConf.set(ZOOKEEPER_QUORUM, "127.0.0.1");
            try (ZKAsyncRegistry otherRegistry = new ZKAsyncRegistry(otherConf)) {
                ReadOnlyZKClient zk2 = otherRegistry.getZKClient();
                Assert.assertNotSame(("Using a different configuration / quorum should result in different " + "backing zk connection."), zk1, zk2);
                Assert.assertNotEquals("Using a different configrution / quorum should be reflected in the zk connection.", zk1.getConnectString(), zk2.getConnectString());
            }
        } finally {
            TestZKAsyncRegistry.LOG.info("DONE!");
        }
    }
}

