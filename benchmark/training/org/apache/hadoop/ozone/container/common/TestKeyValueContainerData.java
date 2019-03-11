/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.ozone.container.common;


import ContainerProtos.ContainerDataProto.State;
import ContainerProtos.ContainerDataProto.State.OPEN;
import ContainerProtos.ContainerType;
import StorageUnit.GB;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.hdds.protocol.datanode.proto.ContainerProtos;
import org.apache.hadoop.ozone.container.keyvalue.KeyValueContainerData;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class is used to test the KeyValueContainerData.
 */
public class TestKeyValueContainerData {
    private static final long MAXSIZE = ((long) (GB.toBytes(5)));

    @Test
    public void testKeyValueData() {
        long containerId = 1L;
        ContainerProtos.ContainerType containerType = ContainerType.KeyValueContainer;
        String path = "/tmp";
        String containerDBType = "RocksDB";
        ContainerProtos.ContainerDataProto.State state = State.CLOSED;
        AtomicLong val = new AtomicLong(0);
        UUID pipelineId = UUID.randomUUID();
        UUID datanodeId = UUID.randomUUID();
        KeyValueContainerData kvData = new KeyValueContainerData(containerId, TestKeyValueContainerData.MAXSIZE, pipelineId.toString(), datanodeId.toString());
        Assert.assertEquals(containerType, kvData.getContainerType());
        Assert.assertEquals(containerId, kvData.getContainerID());
        Assert.assertEquals(OPEN, kvData.getState());
        Assert.assertEquals(0, kvData.getMetadata().size());
        Assert.assertEquals(0, kvData.getNumPendingDeletionBlocks());
        Assert.assertEquals(val.get(), kvData.getReadBytes());
        Assert.assertEquals(val.get(), kvData.getWriteBytes());
        Assert.assertEquals(val.get(), kvData.getReadCount());
        Assert.assertEquals(val.get(), kvData.getWriteCount());
        Assert.assertEquals(val.get(), kvData.getKeyCount());
        Assert.assertEquals(val.get(), kvData.getNumPendingDeletionBlocks());
        Assert.assertEquals(TestKeyValueContainerData.MAXSIZE, kvData.getMaxSize());
        kvData.setState(state);
        kvData.setContainerDBType(containerDBType);
        kvData.setChunksPath(path);
        kvData.setMetadataPath(path);
        kvData.incrReadBytes(10);
        kvData.incrWriteBytes(10);
        kvData.incrReadCount();
        kvData.incrWriteCount();
        kvData.incrKeyCount();
        kvData.incrPendingDeletionBlocks(1);
        Assert.assertEquals(state, kvData.getState());
        Assert.assertEquals(containerDBType, kvData.getContainerDBType());
        Assert.assertEquals(path, kvData.getChunksPath());
        Assert.assertEquals(path, kvData.getMetadataPath());
        Assert.assertEquals(10, kvData.getReadBytes());
        Assert.assertEquals(10, kvData.getWriteBytes());
        Assert.assertEquals(1, kvData.getReadCount());
        Assert.assertEquals(1, kvData.getWriteCount());
        Assert.assertEquals(1, kvData.getKeyCount());
        Assert.assertEquals(1, kvData.getNumPendingDeletionBlocks());
        Assert.assertEquals(pipelineId.toString(), kvData.getOriginPipelineId());
        Assert.assertEquals(datanodeId.toString(), kvData.getOriginNodeId());
    }
}

