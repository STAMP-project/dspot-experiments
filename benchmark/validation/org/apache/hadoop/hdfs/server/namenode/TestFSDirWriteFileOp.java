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
package org.apache.hadoop.hdfs.server.namenode;


import AddBlockFlag.IGNORE_CLIENT_LOCALITY;
import java.io.IOException;
import java.util.EnumSet;
import org.apache.hadoop.hdfs.AddBlockFlag;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.namenode.FSDirWriteFileOp.ValidateAddBlockResult;
import org.apache.hadoop.net.Node;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestFSDirWriteFileOp {
    @Test
    @SuppressWarnings("unchecked")
    public void testIgnoreClientLocality() throws IOException {
        ValidateAddBlockResult addBlockResult = new ValidateAddBlockResult(1024L, 3, ((byte) (1)), null, null, null);
        EnumSet<AddBlockFlag> addBlockFlags = EnumSet.of(IGNORE_CLIENT_LOCALITY);
        BlockManager bmMock = Mockito.mock(BlockManager.class);
        ArgumentCaptor<Node> nodeCaptor = ArgumentCaptor.forClass(Node.class);
        Mockito.when(bmMock.chooseTarget4NewBlock(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.anySet(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyList(), ArgumentMatchers.anyByte(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(null);
        FSDirWriteFileOp.chooseTargetForNewBlock(bmMock, "localhost", null, null, addBlockFlags, addBlockResult);
        // There should be no other interactions with the block manager when the
        // IGNORE_CLIENT_LOCALITY is passed in because there is no need to discover
        // the local node requesting the new block
        Mockito.verify(bmMock, Mockito.times(1)).chooseTarget4NewBlock(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), nodeCaptor.capture(), ArgumentMatchers.anySet(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyList(), ArgumentMatchers.anyByte(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verifyNoMoreInteractions(bmMock);
        Assert.assertNull(("Source node was assigned a value. Expected 'null' value because " + "chooseTarget was flagged to ignore source node locality"), nodeCaptor.getValue());
    }
}

