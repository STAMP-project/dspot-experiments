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
package org.apache.zookeeper.server;


import KeeperException.NoNodeException;
import KeeperException.NodeExistsException;
import java.io.IOException;
import org.apache.zookeeper.ZKTestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeserializationPerfTest extends ZKTestCase {
    protected static final Logger LOG = LoggerFactory.getLogger(DeserializationPerfTest.class);

    @Test
    public void testSingleDeserialize() throws NoNodeException, NodeExistsException, IOException, InterruptedException {
        DeserializationPerfTest.deserializeTree(1, 0, 20);
    }

    @Test
    public void testWideDeserialize() throws NoNodeException, NodeExistsException, IOException, InterruptedException {
        DeserializationPerfTest.deserializeTree(2, 10000, 20);
    }

    @Test
    public void testDeepDeserialize() throws NoNodeException, NodeExistsException, IOException, InterruptedException {
        DeserializationPerfTest.deserializeTree(400, 1, 20);
    }

    @Test
    public void test10Wide5DeepDeserialize() throws NoNodeException, NodeExistsException, IOException, InterruptedException {
        DeserializationPerfTest.deserializeTree(5, 10, 20);
    }

    @Test
    public void test15Wide5DeepDeserialize() throws NoNodeException, NodeExistsException, IOException, InterruptedException {
        DeserializationPerfTest.deserializeTree(5, 15, 20);
    }

    @Test
    public void test25Wide4DeepDeserialize() throws NoNodeException, NodeExistsException, IOException, InterruptedException {
        DeserializationPerfTest.deserializeTree(4, 25, 20);
    }

    @Test
    public void test40Wide4DeepDeserialize() throws NoNodeException, NodeExistsException, IOException, InterruptedException {
        DeserializationPerfTest.deserializeTree(4, 40, 20);
    }

    @Test
    public void test300Wide3DeepDeserialize() throws NoNodeException, NodeExistsException, IOException, InterruptedException {
        DeserializationPerfTest.deserializeTree(3, 300, 20);
    }
}

