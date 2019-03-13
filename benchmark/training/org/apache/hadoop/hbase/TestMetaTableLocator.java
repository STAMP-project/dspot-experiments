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
package org.apache.hadoop.hbase;


import ClientProtos.ClientService.BlockingInterface;
import RegionState.State;
import RegionState.State.OFFLINE;
import RegionState.State.OPEN;
import java.io.IOException;
import org.apache.hadoop.hbase.master.RegionState;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.GetRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.GetResponse;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.zookeeper.MetaTableLocator;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.apache.hbase.thirdparty.com.google.protobuf.RpcController;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test {@link org.apache.hadoop.hbase.zookeeper.MetaTableLocator}
 */
@Category({ MiscTests.class, MediumTests.class })
public class TestMetaTableLocator {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMetaTableLocator.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMetaTableLocator.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final ServerName SN = ServerName.valueOf("example.org", 1234, System.currentTimeMillis());

    private ZKWatcher watcher;

    private Abortable abortable;

    /**
     * Test normal operations
     */
    @Test
    public void testMetaLookup() throws IOException, InterruptedException, ServiceException, KeeperException {
        final ClientProtos.ClientService.BlockingInterface client = Mockito.mock(BlockingInterface.class);
        Mockito.when(client.get(((RpcController) (Mockito.any())), ((GetRequest) (Mockito.any())))).thenReturn(GetResponse.newBuilder().build());
        Assert.assertNull(MetaTableLocator.getMetaRegionLocation(this.watcher));
        for (RegionState.State state : State.values()) {
            if (state.equals(OPEN)) {
                continue;
            }
            MetaTableLocator.setMetaLocation(this.watcher, TestMetaTableLocator.SN, state);
            Assert.assertNull(MetaTableLocator.getMetaRegionLocation(this.watcher));
            Assert.assertEquals(state, MetaTableLocator.getMetaRegionState(this.watcher).getState());
        }
        MetaTableLocator.setMetaLocation(this.watcher, TestMetaTableLocator.SN, OPEN);
        Assert.assertEquals(TestMetaTableLocator.SN, MetaTableLocator.getMetaRegionLocation(this.watcher));
        Assert.assertEquals(OPEN, MetaTableLocator.getMetaRegionState(this.watcher).getState());
        MetaTableLocator.deleteMetaLocation(this.watcher);
        Assert.assertNull(MetaTableLocator.getMetaRegionState(this.watcher).getServerName());
        Assert.assertEquals(OFFLINE, MetaTableLocator.getMetaRegionState(this.watcher).getState());
        Assert.assertNull(MetaTableLocator.getMetaRegionLocation(this.watcher));
    }

    @Test(expected = NotAllMetaRegionsOnlineException.class)
    public void testTimeoutWaitForMeta() throws IOException, InterruptedException {
        MetaTableLocator.waitMetaRegionLocation(watcher, 100);
    }

    /**
     * Test waiting on meat w/ no timeout specified.
     */
    @Test
    public void testNoTimeoutWaitForMeta() throws IOException, InterruptedException, KeeperException {
        ServerName hsa = MetaTableLocator.getMetaRegionLocation(watcher);
        Assert.assertNull(hsa);
        // Now test waiting on meta location getting set.
        Thread t = new TestMetaTableLocator.WaitOnMetaThread();
        startWaitAliveThenWaitItLives(t, 1);
        // Set a meta location.
        MetaTableLocator.setMetaLocation(this.watcher, TestMetaTableLocator.SN, OPEN);
        hsa = TestMetaTableLocator.SN;
        // Join the thread... should exit shortly.
        t.join();
        // Now meta is available.
        Assert.assertTrue(MetaTableLocator.getMetaRegionLocation(watcher).equals(hsa));
    }

    /**
     * Wait on META.
     */
    class WaitOnMetaThread extends Thread {
        WaitOnMetaThread() {
            super("WaitOnMeta");
        }

        @Override
        public void run() {
            try {
                doWaiting();
            } catch (InterruptedException e) {
                throw new RuntimeException("Failed wait", e);
            }
            TestMetaTableLocator.LOG.info(("Exiting " + (getName())));
        }

        void doWaiting() throws InterruptedException {
            try {
                for (; ;) {
                    if ((MetaTableLocator.waitMetaRegionLocation(watcher, 10000)) != null) {
                        break;
                    }
                }
            } catch (NotAllMetaRegionsOnlineException e) {
                // Ignore
            }
        }
    }
}

