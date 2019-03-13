/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.state;


import OperatorStateHandle.Mode.SPLIT_DISTRIBUTE;
import OperatorStateHandle.StateMetaInfo;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class OperatorStateOutputCheckpointStreamTest {
    private static final int STREAM_CAPACITY = 128;

    @Test
    public void testCloseNotPropagated() throws Exception {
        OperatorStateCheckpointOutputStream stream = OperatorStateOutputCheckpointStreamTest.createStream();
        TestMemoryCheckpointOutputStream innerStream = ((TestMemoryCheckpointOutputStream) (stream.getDelegate()));
        stream.close();
        Assert.assertFalse(innerStream.isClosed());
        innerStream.close();
    }

    @Test
    public void testEmptyOperatorStream() throws Exception {
        OperatorStateCheckpointOutputStream stream = OperatorStateOutputCheckpointStreamTest.createStream();
        TestMemoryCheckpointOutputStream innerStream = ((TestMemoryCheckpointOutputStream) (stream.getDelegate()));
        OperatorStateHandle emptyHandle = stream.closeAndGetHandle();
        Assert.assertTrue(innerStream.isClosed());
        Assert.assertEquals(0, stream.getNumberOfPartitions());
        Assert.assertEquals(null, emptyHandle);
    }

    @Test
    public void testWriteReadRoundtrip() throws Exception {
        int numPartitions = 3;
        OperatorStateCheckpointOutputStream stream = OperatorStateOutputCheckpointStreamTest.createStream();
        OperatorStateHandle fullHandle = writeAllTestKeyGroups(stream, numPartitions);
        Assert.assertNotNull(fullHandle);
        Map<String, OperatorStateHandle.StateMetaInfo> stateNameToPartitionOffsets = fullHandle.getStateNameToPartitionOffsets();
        for (Map.Entry<String, OperatorStateHandle.StateMetaInfo> entry : stateNameToPartitionOffsets.entrySet()) {
            Assert.assertEquals(SPLIT_DISTRIBUTE, entry.getValue().getDistributionMode());
        }
        OperatorStateOutputCheckpointStreamTest.verifyRead(fullHandle, numPartitions);
    }
}

