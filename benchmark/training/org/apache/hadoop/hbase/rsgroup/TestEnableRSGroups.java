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
package org.apache.hadoop.hbase.rsgroup;


import CoprocessorHost.MASTER_COPROCESSOR_CONF_KEY;
import HConstants.HBASE_MASTER_LOADBALANCER_CLASS;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test enable RSGroup
 */
@Category({ MediumTests.class })
public class TestEnableRSGroups {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestEnableRSGroups.class);

    protected static final Logger LOG = LoggerFactory.getLogger(TestEnableRSGroups.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Test
    public void testEnableRSGroup() throws IOException, InterruptedException {
        TestEnableRSGroups.TEST_UTIL.getMiniHBaseCluster().stopMaster(0);
        TestEnableRSGroups.TEST_UTIL.getMiniHBaseCluster().waitOnMaster(0);
        TestEnableRSGroups.LOG.info("stopped master...");
        final Configuration conf = TestEnableRSGroups.TEST_UTIL.getMiniHBaseCluster().getConfiguration();
        conf.set(MASTER_COPROCESSOR_CONF_KEY, RSGroupAdminEndpoint.class.getName());
        conf.set(HBASE_MASTER_LOADBALANCER_CLASS, RSGroupBasedLoadBalancer.class.getName());
        TestEnableRSGroups.TEST_UTIL.getMiniHBaseCluster().startMaster();
        TestEnableRSGroups.TEST_UTIL.getMiniHBaseCluster().waitForActiveAndReadyMaster(60000);
        TestEnableRSGroups.LOG.info("started master...");
        // check if master started successfully
        Assert.assertTrue(((TestEnableRSGroups.TEST_UTIL.getMiniHBaseCluster().getMaster()) != null));
        // wait RSGroupBasedLoadBalancer online
        RSGroupBasedLoadBalancer loadBalancer = ((RSGroupBasedLoadBalancer) (TestEnableRSGroups.TEST_UTIL.getMiniHBaseCluster().getMaster().getLoadBalancer()));
        long start = System.currentTimeMillis();
        while ((((System.currentTimeMillis()) - start) <= 60000) && (!(loadBalancer.isOnline()))) {
            TestEnableRSGroups.LOG.info("waiting for rsgroup load balancer onLine...");
            Thread.sleep(200);
        } 
        Assert.assertTrue(loadBalancer.isOnline());
    }
}

