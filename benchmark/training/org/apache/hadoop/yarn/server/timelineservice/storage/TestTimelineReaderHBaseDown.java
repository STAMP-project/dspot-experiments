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
package org.apache.hadoop.yarn.server.timelineservice.storage;


import java.util.concurrent.TimeoutException;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.server.timelineservice.reader.TimelineReaderServer;
import org.junit.Test;


public class TestTimelineReaderHBaseDown {
    @Test(timeout = 300000)
    public void testTimelineReaderHBaseUp() throws Exception {
        HBaseTestingUtility util = new HBaseTestingUtility();
        TestTimelineReaderHBaseDown.configure(util);
        try {
            util.startMiniCluster();
            DataGeneratorForTest.createSchema(util.getConfiguration());
            DataGeneratorForTest.loadApps(util, System.currentTimeMillis());
            TimelineReaderServer server = TestTimelineReaderHBaseDown.getTimelineReaderServer();
            server.init(util.getConfiguration());
            HBaseTimelineReaderImpl htr = TestTimelineReaderHBaseDown.getHBaseTimelineReaderImpl(server);
            server.start();
            TestTimelineReaderHBaseDown.checkQuery(htr);
        } finally {
            util.shutdownMiniCluster();
        }
    }

    @Test(timeout = 300000)
    public void testTimelineReaderInitWhenHBaseIsDown() throws InterruptedException, TimeoutException {
        HBaseTestingUtility util = new HBaseTestingUtility();
        TestTimelineReaderHBaseDown.configure(util);
        TimelineReaderServer server = TestTimelineReaderHBaseDown.getTimelineReaderServer();
        // init timeline reader when hbase is not running
        server.init(util.getConfiguration());
        HBaseTimelineReaderImpl htr = TestTimelineReaderHBaseDown.getHBaseTimelineReaderImpl(server);
        server.start();
        TestTimelineReaderHBaseDown.waitForHBaseDown(htr);
    }

    @Test(timeout = 300000)
    public void testTimelineReaderDetectsHBaseDown() throws Exception {
        HBaseTestingUtility util = new HBaseTestingUtility();
        TestTimelineReaderHBaseDown.configure(util);
        try {
            // start minicluster
            util.startMiniCluster();
            DataGeneratorForTest.createSchema(util.getConfiguration());
            DataGeneratorForTest.loadApps(util, System.currentTimeMillis());
            // init timeline reader
            TimelineReaderServer server = TestTimelineReaderHBaseDown.getTimelineReaderServer();
            server.init(util.getConfiguration());
            HBaseTimelineReaderImpl htr = TestTimelineReaderHBaseDown.getHBaseTimelineReaderImpl(server);
            // stop hbase after timeline reader init
            util.shutdownMiniHBaseCluster();
            // start server and check that it detects hbase is down
            server.start();
            TestTimelineReaderHBaseDown.waitForHBaseDown(htr);
        } finally {
            util.shutdownMiniCluster();
        }
    }

    @Test(timeout = 300000)
    public void testTimelineReaderDetectsZooKeeperDown() throws Exception {
        HBaseTestingUtility util = new HBaseTestingUtility();
        TestTimelineReaderHBaseDown.configure(util);
        try {
            // start minicluster
            util.startMiniCluster();
            DataGeneratorForTest.createSchema(util.getConfiguration());
            DataGeneratorForTest.loadApps(util, System.currentTimeMillis());
            // init timeline reader
            TimelineReaderServer server = TestTimelineReaderHBaseDown.getTimelineReaderServer();
            server.init(util.getConfiguration());
            HBaseTimelineReaderImpl htr = TestTimelineReaderHBaseDown.getHBaseTimelineReaderImpl(server);
            // stop hbase and zookeeper after timeline reader init
            util.shutdownMiniCluster();
            // start server and check that it detects hbase is down
            server.start();
            TestTimelineReaderHBaseDown.waitForHBaseDown(htr);
        } finally {
            util.shutdownMiniCluster();
        }
    }

    @Test(timeout = 300000)
    public void testTimelineReaderRecoversAfterHBaseReturns() throws Exception {
        HBaseTestingUtility util = new HBaseTestingUtility();
        TestTimelineReaderHBaseDown.configure(util);
        try {
            // start minicluster
            util.startMiniCluster();
            DataGeneratorForTest.createSchema(util.getConfiguration());
            DataGeneratorForTest.loadApps(util, System.currentTimeMillis());
            // init timeline reader
            TimelineReaderServer server = TestTimelineReaderHBaseDown.getTimelineReaderServer();
            server.init(util.getConfiguration());
            HBaseTimelineReaderImpl htr = TestTimelineReaderHBaseDown.getHBaseTimelineReaderImpl(server);
            // stop hbase after timeline reader init
            util.shutdownMiniHBaseCluster();
            // start server and check that it detects hbase is down
            server.start();
            TestTimelineReaderHBaseDown.waitForHBaseDown(htr);
            util.startMiniHBaseCluster(1, 1);
            GenericTestUtils.waitFor(() -> !(htr.isHBaseDown()), 1000, 150000);
        } finally {
            util.shutdownMiniCluster();
        }
    }
}

