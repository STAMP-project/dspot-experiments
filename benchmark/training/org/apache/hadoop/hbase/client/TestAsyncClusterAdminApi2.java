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


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Only used to test stopMaster/stopRegionServer/shutdown methods.
 */
@Category({ ClientTests.class, MediumTests.class })
public class TestAsyncClusterAdminApi2 extends TestAsyncAdminBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncClusterAdminApi2.class);

    @Test
    public void testStop() throws Exception {
        HRegionServer rs = TestAsyncAdminBase.TEST_UTIL.getMiniHBaseCluster().getRegionServer(0);
        Assert.assertFalse(rs.isStopped());
        admin.stopRegionServer(rs.getServerName()).join();
        Assert.assertTrue(rs.isStopped());
        HMaster master = TestAsyncAdminBase.TEST_UTIL.getMiniHBaseCluster().getMaster();
        Assert.assertFalse(master.isStopped());
        admin.stopMaster().join();
        Assert.assertTrue(master.isStopped());
    }

    @Test
    public void testShutdown() throws Exception {
        TestAsyncAdminBase.TEST_UTIL.getMiniHBaseCluster().getMasterThreads().forEach(( thread) -> {
            assertFalse(thread.getMaster().isStopped());
        });
        TestAsyncAdminBase.TEST_UTIL.getMiniHBaseCluster().getRegionServerThreads().forEach(( thread) -> {
            assertFalse(thread.getRegionServer().isStopped());
        });
        admin.shutdown().join();
        TestAsyncAdminBase.TEST_UTIL.getMiniHBaseCluster().getMasterThreads().forEach(( thread) -> {
            while (!(thread.getMaster().isStopped())) {
                trySleep(100, TimeUnit.MILLISECONDS);
            } 
        });
        TestAsyncAdminBase.TEST_UTIL.getMiniHBaseCluster().getRegionServerThreads().forEach(( thread) -> {
            while (!(thread.getRegionServer().isStopped())) {
                trySleep(100, TimeUnit.MILLISECONDS);
            } 
        });
    }
}

