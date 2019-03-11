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


import java.io.File;
import org.apache.zookeeper.ZKTestCase;
import org.junit.Assert;
import org.junit.Test;


public class DatadirCleanupManagerTest extends ZKTestCase {
    private DatadirCleanupManager purgeMgr;

    private File snapDir;

    private File dataLogDir;

    @Test
    public void testPurgeTask() throws Exception {
        purgeMgr = new DatadirCleanupManager(snapDir, dataLogDir, 3, 1);
        purgeMgr.start();
        Assert.assertEquals("Data log directory is not set as configured", dataLogDir, purgeMgr.getDataLogDir());
        Assert.assertEquals("Snapshot directory is not set as configured", snapDir, purgeMgr.getSnapDir());
        Assert.assertEquals("Snapshot retain count is not set as configured", 3, purgeMgr.getSnapRetainCount());
        Assert.assertEquals("Purge task is not started", PurgeTaskStatus.STARTED, purgeMgr.getPurgeTaskStatus());
        purgeMgr.shutdown();
        Assert.assertEquals("Purge task is still running after shutdown", PurgeTaskStatus.COMPLETED, purgeMgr.getPurgeTaskStatus());
    }

    @Test
    public void testWithZeroPurgeInterval() throws Exception {
        purgeMgr = new DatadirCleanupManager(snapDir, dataLogDir, 3, 0);
        purgeMgr.start();
        Assert.assertEquals("Purge task is scheduled with zero purge interval", PurgeTaskStatus.NOT_STARTED, purgeMgr.getPurgeTaskStatus());
        purgeMgr.shutdown();
        Assert.assertEquals("Purge task is scheduled with zero purge interval", PurgeTaskStatus.NOT_STARTED, purgeMgr.getPurgeTaskStatus());
    }

    @Test
    public void testWithNegativePurgeInterval() throws Exception {
        purgeMgr = new DatadirCleanupManager(snapDir, dataLogDir, 3, (-1));
        purgeMgr.start();
        Assert.assertEquals("Purge task is scheduled with negative purge interval", PurgeTaskStatus.NOT_STARTED, purgeMgr.getPurgeTaskStatus());
        purgeMgr.shutdown();
        Assert.assertEquals("Purge task is scheduled with negative purge interval", PurgeTaskStatus.NOT_STARTED, purgeMgr.getPurgeTaskStatus());
    }
}

