/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.container.common.helpers;


import ContainerProtos.ChunkInfo;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hdds.protocol.datanode.proto.ContainerProtos;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests to test block deleting service.
 */
public class TestBlockData {
    static final Logger LOG = LoggerFactory.getLogger(TestBlockData.class);

    @Rule
    public TestRule timeout = new Timeout(10000);

    @Test
    public void testAddAndRemove() {
        final BlockData computed = new BlockData(null);
        final List<ContainerProtos.ChunkInfo> expected = new ArrayList<>();
        TestBlockData.assertChunks(expected, computed);
        long offset = 0;
        int n = 5;
        for (int i = 0; i < n; i++) {
            offset += TestBlockData.assertAddChunk(expected, computed, offset);
        }
        for (; !(expected.isEmpty());) {
            TestBlockData.removeChunk(expected, computed);
        }
    }

    private static int chunkCount = 0;

    @Test
    public void testSetChunks() {
        final BlockData computed = new BlockData(null);
        final List<ContainerProtos.ChunkInfo> expected = new ArrayList<>();
        TestBlockData.assertChunks(expected, computed);
        long offset = 0;
        int n = 5;
        for (int i = 0; i < n; i++) {
            offset += TestBlockData.addChunk(expected, offset).getLen();
            TestBlockData.LOG.info(("setChunk: " + (TestBlockData.toString(expected))));
            computed.setChunks(expected);
            TestBlockData.assertChunks(expected, computed);
        }
    }
}

