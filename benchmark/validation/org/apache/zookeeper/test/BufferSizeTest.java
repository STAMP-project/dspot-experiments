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
package org.apache.zookeeper.test;


import CreateMode.PERSISTENT;
import CreateMode.PERSISTENT_SEQUENTIAL;
import Ids.OPEN_ACL_UNSAFE;
import java.io.File;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;


public class BufferSizeTest extends ClientBase {
    public static final int TEST_MAXBUFFER = 100;

    private static final File TEST_DATA = new File(System.getProperty("test.data.dir", "src/test/resources/data"), "buffersize");

    private ZooKeeper zk;

    @Test
    public void testCreatesReqs() throws Exception {
        testRequests(new BufferSizeTest.ClientOp() {
            @Override
            public void execute(byte[] data) throws Exception {
                zk.create("/create_test", data, OPEN_ACL_UNSAFE, PERSISTENT_SEQUENTIAL);
            }
        });
    }

    @Test
    public void testSetReqs() throws Exception {
        final String path = "/set_test";
        zk.create(path, new byte[1], OPEN_ACL_UNSAFE, PERSISTENT);
        testRequests(new BufferSizeTest.ClientOp() {
            @Override
            public void execute(byte[] data) throws Exception {
                zk.setData(path, data, (-1));
            }
        });
    }

    private interface ClientOp {
        void execute(byte[] data) throws Exception;
    }

    @Test
    public void testStartup() throws Exception {
        final String path = "/test_node";
        zk.create(path, new byte[(BufferSizeTest.TEST_MAXBUFFER) - 60], OPEN_ACL_UNSAFE, PERSISTENT);
        zk.setData(path, new byte[(BufferSizeTest.TEST_MAXBUFFER) - 50], (-1));
        stopServer();
        startServer();
    }

    @Test
    public void testStartupFailureCreate() throws Exception {
        // Empty snapshot and logfile containing a 5000-byte create
        testStartupFailure(new File(BufferSizeTest.TEST_DATA, "create"), "Server started despite create exceeding jute.maxbuffer!");
    }

    @Test
    public void testStartupFailureSet() throws Exception {
        // Empty snapshot and logfile containing a 1-byte create and 5000-byte set
        testStartupFailure(new File(BufferSizeTest.TEST_DATA, "set"), "Server started despite set exceeding jute.maxbuffer!");
    }

    @Test
    public void testStartupFailureSnapshot() throws Exception {
        // Snapshot containing 5000-byte znode and logfile containing create txn
        testStartupFailure(new File(BufferSizeTest.TEST_DATA, "snapshot"), "Server started despite znode exceeding jute.maxbuffer!");
    }
}

