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
package org.apache.hive.jdbc;


import FairSchedulerConfiguration.ALLOCATION_FILE;
import HiveConf.ConfVars.HIVE_SERVER2_ENABLE_DOAS.varname;
import YarnConfiguration.DEFAULT_QUEUE_NAME;
import YarnConfiguration.RM_SCHEDULER;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.security.GroupMappingServiceProvider;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Test;


public class TestSchedulerQueue {
    // hadoop group mapping that maps user to same group
    public static class HiveTestSimpleGroupMapping implements GroupMappingServiceProvider {
        public static String primaryTag = "";

        @Override
        public List<String> getGroups(String user) throws IOException {
            List<String> results = new ArrayList<String>();
            results.add((user + (TestSchedulerQueue.HiveTestSimpleGroupMapping.primaryTag)));
            results.add((user + "-group"));
            return results;
        }

        @Override
        public void cacheGroupsRefresh() throws IOException {
        }

        @Override
        public void cacheGroupsAdd(List<String> groups) throws IOException {
        }
    }

    private MiniHS2 miniHS2 = null;

    private static HiveConf conf = new HiveConf();

    private Connection hs2Conn = null;

    /**
     * Verify:
     *  Test is running with MR2 and queue mapping defaults are set.
     *  Queue mapping is set for the connected user.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFairSchedulerQueueMapping() throws Exception {
        hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL(), "user1", "bar");
        verifyProperty(varname, "false");
        verifyProperty("mapreduce.framework.name", "yarn");
        verifyProperty(HiveConf.ConfVars.HIVE_SERVER2_MAP_FAIR_SCHEDULER_QUEUE.varname, "true");
        verifyProperty(RM_SCHEDULER, "org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler");
        verifyProperty("mapreduce.job.queuename", "root.user1");
    }

    /**
     * Verify:
     *  Test is running with MR2 and queue mapping are set correctly for primary group rule.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFairSchedulerPrimaryQueueMapping() throws Exception {
        miniHS2.setConfProperty(ALLOCATION_FILE, "fair-scheduler-test.xml");
        TestSchedulerQueue.HiveTestSimpleGroupMapping.primaryTag = "-test";
        hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL(), "user2", "bar");
        verifyProperty("mapreduce.job.queuename", ("root.user2" + (TestSchedulerQueue.HiveTestSimpleGroupMapping.primaryTag)));
    }

    /**
     * Verify:
     *  Test is running with MR2 and queue mapping are set correctly for primary group rule.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFairSchedulerSecondaryQueueMapping() throws Exception {
        miniHS2.setConfProperty(ALLOCATION_FILE, "fair-scheduler-test.xml");
        hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL(), "user3", "bar");
        verifyProperty("mapreduce.job.queuename", "root.user3-group");
    }

    /**
     * Verify that the queue refresh doesn't happen when configured to be off.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testQueueMappingCheckDisabled() throws Exception {
        miniHS2.setConfProperty(HiveConf.ConfVars.HIVE_SERVER2_MAP_FAIR_SCHEDULER_QUEUE.varname, "false");
        hs2Conn = DriverManager.getConnection(miniHS2.getJdbcURL(), "user1", "bar");
        verifyProperty(HiveConf.ConfVars.HIVE_SERVER2_MAP_FAIR_SCHEDULER_QUEUE.varname, "false");
        verifyProperty("mapreduce.job.queuename", DEFAULT_QUEUE_NAME);
    }
}

