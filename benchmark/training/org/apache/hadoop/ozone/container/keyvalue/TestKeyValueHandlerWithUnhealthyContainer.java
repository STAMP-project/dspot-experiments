/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.container.keyvalue;


import ContainerProtos.ContainerCommandResponseProto;
import ContainerProtos.Type.DeleteChunk;
import ContainerProtos.Type.GetBlock;
import ContainerProtos.Type.GetCommittedBlockLength;
import ContainerProtos.Type.GetSmallFile;
import ContainerProtos.Type.ReadChunk;
import ContainerProtos.Type.ReadContainer;
import java.io.IOException;
import java.util.UUID;
import org.apache.hadoop.hdds.protocol.datanode.proto.ContainerProtos;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that KeyValueHandler fails certain operations when the
 * container is unhealthy.
 */
public class TestKeyValueHandlerWithUnhealthyContainer {
    public static final Logger LOG = LoggerFactory.getLogger(TestKeyValueHandlerWithUnhealthyContainer.class);

    private static final String DATANODE_UUID = UUID.randomUUID().toString();

    private static final long DUMMY_CONTAINER_ID = 9999;

    @Test
    public void testRead() throws IOException {
        KeyValueContainer container = getMockUnhealthyContainer();
        KeyValueHandler handler = getDummyHandler();
        ContainerProtos.ContainerCommandResponseProto response = handler.handleReadContainer(getDummyCommandRequestProto(ReadContainer), container);
        Assert.assertThat(response.getResult(), Is.is(CONTAINER_UNHEALTHY));
    }

    @Test
    public void testGetBlock() throws IOException {
        KeyValueContainer container = getMockUnhealthyContainer();
        KeyValueHandler handler = getDummyHandler();
        ContainerProtos.ContainerCommandResponseProto response = handler.handleGetBlock(getDummyCommandRequestProto(GetBlock), container);
        Assert.assertThat(response.getResult(), Is.is(CONTAINER_UNHEALTHY));
    }

    @Test
    public void testGetCommittedBlockLength() throws IOException {
        KeyValueContainer container = getMockUnhealthyContainer();
        KeyValueHandler handler = getDummyHandler();
        ContainerProtos.ContainerCommandResponseProto response = handler.handleGetCommittedBlockLength(getDummyCommandRequestProto(GetCommittedBlockLength), container);
        Assert.assertThat(response.getResult(), Is.is(CONTAINER_UNHEALTHY));
    }

    @Test
    public void testReadChunk() throws IOException {
        KeyValueContainer container = getMockUnhealthyContainer();
        KeyValueHandler handler = getDummyHandler();
        ContainerProtos.ContainerCommandResponseProto response = handler.handleReadChunk(getDummyCommandRequestProto(ReadChunk), container, null);
        Assert.assertThat(response.getResult(), Is.is(CONTAINER_UNHEALTHY));
    }

    @Test
    public void testDeleteChunk() throws IOException {
        KeyValueContainer container = getMockUnhealthyContainer();
        KeyValueHandler handler = getDummyHandler();
        ContainerProtos.ContainerCommandResponseProto response = handler.handleDeleteChunk(getDummyCommandRequestProto(DeleteChunk), container);
        Assert.assertThat(response.getResult(), Is.is(CONTAINER_UNHEALTHY));
    }

    @Test
    public void testGetSmallFile() throws IOException {
        KeyValueContainer container = getMockUnhealthyContainer();
        KeyValueHandler handler = getDummyHandler();
        ContainerProtos.ContainerCommandResponseProto response = handler.handleGetSmallFile(getDummyCommandRequestProto(GetSmallFile), container);
        Assert.assertThat(response.getResult(), Is.is(CONTAINER_UNHEALTHY));
    }
}

