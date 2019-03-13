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
package org.apache.hadoop.hdfs.server.blockmanagement;


import ReplicaState.FINALIZED;
import com.google.common.base.Joiner;
import java.util.Queue;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.server.blockmanagement.PendingDataNodeMessages.ReportedBlockInfo;
import org.apache.hadoop.hdfs.server.protocol.DatanodeStorage;
import org.junit.Assert;
import org.junit.Test;


public class TestPendingDataNodeMessages {
    final PendingDataNodeMessages msgs = new PendingDataNodeMessages();

    private final Block block1Gs1 = new Block(1, 0, 1);

    private final Block block1Gs2 = new Block(1, 0, 2);

    private final Block block1Gs2DifferentInstance = new Block(1, 0, 2);

    private final Block block2Gs1 = new Block(2, 0, 1);

    @Test
    public void testQueues() {
        DatanodeDescriptor fakeDN = DFSTestUtil.getLocalDatanodeDescriptor();
        DatanodeStorage storage = new DatanodeStorage("STORAGE_ID");
        DatanodeStorageInfo storageInfo = new DatanodeStorageInfo(fakeDN, storage);
        msgs.enqueueReportedBlock(storageInfo, block1Gs1, FINALIZED);
        msgs.enqueueReportedBlock(storageInfo, block1Gs2, FINALIZED);
        Assert.assertEquals(2, msgs.count());
        // Nothing queued yet for block 2
        Assert.assertNull(msgs.takeBlockQueue(block2Gs1));
        Assert.assertEquals(2, msgs.count());
        Queue<ReportedBlockInfo> q = msgs.takeBlockQueue(block1Gs2DifferentInstance);
        Assert.assertEquals(("ReportedBlockInfo [block=blk_1_1, dn=127.0.0.1:9866, reportedState=FINALIZED]," + "ReportedBlockInfo [block=blk_1_2, dn=127.0.0.1:9866, reportedState=FINALIZED]"), Joiner.on(",").join(q));
        Assert.assertEquals(0, msgs.count());
        // Should be null if we pull again
        Assert.assertNull(msgs.takeBlockQueue(block1Gs1));
        Assert.assertEquals(0, msgs.count());
    }
}

