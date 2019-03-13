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


import org.apache.log4j.Logger;
import org.apache.zookeeper.PortAssignment;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZKTestCase;
import org.junit.Test;


/**
 * If snapshots are corrupted to the empty file or deleted, Zookeeper should
 *  not proceed to read its transactiong log files
 *  Test that zxid == -1 in the presence of emptied/deleted snapshots
 */
public class EmptiedSnapshotRecoveryTest extends ZKTestCase implements Watcher {
    private static final Logger LOG = Logger.getLogger(RestoreCommittedLogTest.class);

    private static String HOSTPORT = "127.0.0.1:" + (PortAssignment.unique());

    private static final int CONNECTION_TIMEOUT = 3000;

    private static final int N_TRANSACTIONS = 150;

    private static final int SNAP_COUNT = 100;

    /**
     * Test resilience to empty Snapshots
     *
     * @throws Exception
     * 		an exception might be thrown here
     */
    @Test
    public void testRestoreWithEmptySnapFiles() throws Exception {
        runTest(true);
    }

    /**
     * Test resilience to deletion of Snapshots
     *
     * @throws Exception
     * 		an exception might be thrown here
     */
    @Test
    public void testRestoreWithNoSnapFiles() throws Exception {
        runTest(false);
    }
}

