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
package org.apache.hadoop.hbase.replication;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.replication.ReplicationPeerManager;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.zookeeper.KeeperException;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * All the modification method will fail once in the test and should finally succeed.
 */
@Category({ ReplicationTests.class, MediumTests.class })
public class TestReplicationProcedureRetry {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationProcedureRetry.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Test
    public void testErrorBeforeUpdate() throws IOException, ReplicationException {
        ((TestReplicationProcedureRetry.MockHMaster) (TestReplicationProcedureRetry.UTIL.getHBaseCluster().getMaster())).reset(true);
        doTest();
    }

    @Test
    public void testErrorAfterUpdate() throws IOException, ReplicationException {
        ((TestReplicationProcedureRetry.MockHMaster) (TestReplicationProcedureRetry.UTIL.getHBaseCluster().getMaster())).reset(false);
        doTest();
    }

    public static final class MockHMaster extends HMaster {
        volatile boolean addPeerCalled;

        volatile boolean removePeerCalled;

        volatile boolean updatePeerConfigCalled;

        volatile boolean enablePeerCalled;

        volatile boolean disablePeerCalled;

        private ReplicationPeerManager manager;

        public MockHMaster(Configuration conf) throws IOException, KeeperException {
            super(conf);
        }

        private Object invokeWithError(InvocationOnMock invocation, boolean errorBeforeUpdate) throws Throwable {
            if (errorBeforeUpdate) {
                throw new ReplicationException("mock error before update");
            }
            invocation.callRealMethod();
            throw new ReplicationException("mock error after update");
        }

        public void reset(boolean errorBeforeUpdate) throws ReplicationException {
            addPeerCalled = false;
            removePeerCalled = false;
            updatePeerConfigCalled = false;
            enablePeerCalled = false;
            disablePeerCalled = false;
            ReplicationPeerManager m = super.getReplicationPeerManager();
            manager = Mockito.spy(m);
            Mockito.doAnswer(( invocation) -> {
                if (!(addPeerCalled)) {
                    addPeerCalled = true;
                    return invokeWithError(invocation, errorBeforeUpdate);
                } else {
                    return invocation.callRealMethod();
                }
            }).when(manager).addPeer(ArgumentMatchers.anyString(), ArgumentMatchers.any(ReplicationPeerConfig.class), ArgumentMatchers.anyBoolean());
            Mockito.doAnswer(( invocation) -> {
                if (!(removePeerCalled)) {
                    removePeerCalled = true;
                    return invokeWithError(invocation, errorBeforeUpdate);
                } else {
                    return invocation.callRealMethod();
                }
            }).when(manager).removePeer(ArgumentMatchers.anyString());
            Mockito.doAnswer(( invocation) -> {
                if (!(updatePeerConfigCalled)) {
                    updatePeerConfigCalled = true;
                    return invokeWithError(invocation, errorBeforeUpdate);
                } else {
                    return invocation.callRealMethod();
                }
            }).when(manager).updatePeerConfig(ArgumentMatchers.anyString(), ArgumentMatchers.any(ReplicationPeerConfig.class));
            Mockito.doAnswer(( invocation) -> {
                if (!(enablePeerCalled)) {
                    enablePeerCalled = true;
                    return invokeWithError(invocation, errorBeforeUpdate);
                } else {
                    return invocation.callRealMethod();
                }
            }).when(manager).enablePeer(ArgumentMatchers.anyString());
            Mockito.doAnswer(( invocation) -> {
                if (!(disablePeerCalled)) {
                    disablePeerCalled = true;
                    return invokeWithError(invocation, errorBeforeUpdate);
                } else {
                    return invocation.callRealMethod();
                }
            }).when(manager).disablePeer(ArgumentMatchers.anyString());
        }

        @Override
        public ReplicationPeerManager getReplicationPeerManager() {
            return manager;
        }
    }
}

