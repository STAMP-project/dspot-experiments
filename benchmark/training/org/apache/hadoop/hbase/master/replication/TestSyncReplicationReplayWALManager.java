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
package org.apache.hadoop.hbase.master.replication;


import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.master.ServerListener;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureScheduler;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureSuspendedException;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility.NoopProcedure;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MasterTests.class, SmallTests.class })
public class TestSyncReplicationReplayWALManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSyncReplicationReplayWALManager.class);

    private static HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private SyncReplicationReplayWALManager manager;

    private MasterProcedureScheduler scheduler;

    private Set<ServerName> onlineServers;

    private List<ServerListener> listeners;

    private Queue<Procedure<?>> wokenProcedures;

    @Test
    public void testUsedWorkers() throws ProcedureSuspendedException {
        String peerId1 = "1";
        String peerId2 = "2";
        ServerName sn1 = ServerName.valueOf("host1", 123, 12345);
        ServerName sn2 = ServerName.valueOf("host2", 234, 23456);
        ServerName sn3 = ServerName.valueOf("host3", 345, 34567);
        onlineServers.add(sn1);
        manager.registerPeer(peerId1);
        manager.registerPeer(peerId2);
        // confirm that different peer ids does not affect each other
        Assert.assertEquals(sn1, manager.acquirePeerWorker(peerId1, new NoopProcedure()));
        Assert.assertEquals(sn1, manager.acquirePeerWorker(peerId2, new NoopProcedure()));
        onlineServers.add(sn2);
        Assert.assertEquals(sn2, manager.acquirePeerWorker(peerId1, new NoopProcedure()));
        Assert.assertEquals(sn2, manager.acquirePeerWorker(peerId2, new NoopProcedure()));
        NoopProcedure<?> proc = new NoopProcedure();
        try {
            manager.acquirePeerWorker(peerId1, proc);
            Assert.fail("Should suspend");
        } catch (ProcedureSuspendedException e) {
            // expected
        }
        manager.releasePeerWorker(peerId1, sn1, scheduler);
        Assert.assertEquals(1, wokenProcedures.size());
        Assert.assertSame(proc, wokenProcedures.poll());
        Assert.assertEquals(sn1, manager.acquirePeerWorker(peerId1, new NoopProcedure()));
        NoopProcedure<?> proc1 = new NoopProcedure();
        NoopProcedure<?> proc2 = new NoopProcedure();
        try {
            manager.acquirePeerWorker(peerId1, proc1);
            Assert.fail("Should suspend");
        } catch (ProcedureSuspendedException e) {
            // expected
        }
        try {
            manager.acquirePeerWorker(peerId1, proc2);
            Assert.fail("Should suspend");
        } catch (ProcedureSuspendedException e) {
            // expected
        }
        listeners.get(0).serverAdded(sn3);
        Assert.assertEquals(2, wokenProcedures.size());
        Assert.assertSame(proc2, wokenProcedures.poll());
        Assert.assertSame(proc1, wokenProcedures.poll());
    }
}

