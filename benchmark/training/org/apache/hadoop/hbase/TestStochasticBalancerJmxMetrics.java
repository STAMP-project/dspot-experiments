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
package org.apache.hadoop.hbase;


import HConstants.HBASE_MASTER_LOADBALANCE_BYTABLE;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.master.balancer.BalancerTestBase;
import org.apache.hadoop.hbase.master.balancer.StochasticLoadBalancer;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static HConstants.ENSEMBLE_TABLE_NAME;


@Category({ MiscTests.class, MediumTests.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore
public class TestStochasticBalancerJmxMetrics extends BalancerTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStochasticBalancerJmxMetrics.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestStochasticBalancerJmxMetrics.class);

    private static HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static int connectorPort = 61120;

    private static StochasticLoadBalancer loadBalancer;

    /**
     * a simple cluster for testing JMX.
     */
    private static int[] mockCluster_ensemble = new int[]{ 0, 1, 2, 3 };

    private static int[] mockCluster_pertable_1 = new int[]{ 0, 1, 2 };

    private static int[] mockCluster_pertable_2 = new int[]{ 3, 1, 1 };

    private static int[] mockCluster_pertable_namespace = new int[]{ 1, 3, 1 };

    private static final String TABLE_NAME_1 = "Table1";

    private static final String TABLE_NAME_2 = "Table2";

    private static final String TABLE_NAME_NAMESPACE = "hbase:namespace";

    private static Configuration conf = null;

    /**
     * In Ensemble mode, there should be only one ensemble table
     */
    @Test
    public void testJmxMetrics_EnsembleMode() throws Exception {
        TestStochasticBalancerJmxMetrics.loadBalancer = new StochasticLoadBalancer();
        TestStochasticBalancerJmxMetrics.conf.setBoolean(HBASE_MASTER_LOADBALANCE_BYTABLE, false);
        TestStochasticBalancerJmxMetrics.loadBalancer.setConf(TestStochasticBalancerJmxMetrics.conf);
        TableName tableName = ENSEMBLE_TABLE_NAME;
        Map<ServerName, List<RegionInfo>> clusterState = mockClusterServers(TestStochasticBalancerJmxMetrics.mockCluster_ensemble);
        TestStochasticBalancerJmxMetrics.loadBalancer.balanceCluster(tableName, clusterState);
        String[] tableNames = new String[]{ tableName.getNameAsString() };
        String[] functionNames = TestStochasticBalancerJmxMetrics.loadBalancer.getCostFunctionNames();
        Set<String> jmxMetrics = readJmxMetricsWithRetry();
        Set<String> expectedMetrics = getExpectedJmxMetrics(tableNames, functionNames);
        // printMetrics(jmxMetrics, "existing metrics in ensemble mode");
        // printMetrics(expectedMetrics, "expected metrics in ensemble mode");
        // assert that every expected is in the JMX
        for (String expected : expectedMetrics) {
            Assert.assertTrue((("Metric " + expected) + " can not be found in JMX in ensemble mode."), jmxMetrics.contains(expected));
        }
    }

    /**
     * In per-table mode, each table has a set of metrics
     */
    @Test
    public void testJmxMetrics_PerTableMode() throws Exception {
        TestStochasticBalancerJmxMetrics.loadBalancer = new StochasticLoadBalancer();
        TestStochasticBalancerJmxMetrics.conf.setBoolean(HBASE_MASTER_LOADBALANCE_BYTABLE, true);
        TestStochasticBalancerJmxMetrics.loadBalancer.setConf(TestStochasticBalancerJmxMetrics.conf);
        // NOTE the size is normally set in setClusterMetrics, for test purpose, we set it manually
        // Tables: hbase:namespace, table1, table2
        // Functions: costFunctions, overall
        String[] functionNames = TestStochasticBalancerJmxMetrics.loadBalancer.getCostFunctionNames();
        TestStochasticBalancerJmxMetrics.loadBalancer.updateMetricsSize((3 * ((functionNames.length) + 1)));
        // table 1
        TableName tableName = TableName.valueOf(TestStochasticBalancerJmxMetrics.TABLE_NAME_1);
        Map<ServerName, List<RegionInfo>> clusterState = mockClusterServers(TestStochasticBalancerJmxMetrics.mockCluster_pertable_1);
        TestStochasticBalancerJmxMetrics.loadBalancer.balanceCluster(tableName, clusterState);
        // table 2
        tableName = TableName.valueOf(TestStochasticBalancerJmxMetrics.TABLE_NAME_2);
        clusterState = mockClusterServers(TestStochasticBalancerJmxMetrics.mockCluster_pertable_2);
        TestStochasticBalancerJmxMetrics.loadBalancer.balanceCluster(tableName, clusterState);
        // table hbase:namespace
        tableName = TableName.valueOf(TestStochasticBalancerJmxMetrics.TABLE_NAME_NAMESPACE);
        clusterState = mockClusterServers(TestStochasticBalancerJmxMetrics.mockCluster_pertable_namespace);
        TestStochasticBalancerJmxMetrics.loadBalancer.balanceCluster(tableName, clusterState);
        String[] tableNames = new String[]{ TestStochasticBalancerJmxMetrics.TABLE_NAME_1, TestStochasticBalancerJmxMetrics.TABLE_NAME_2, TestStochasticBalancerJmxMetrics.TABLE_NAME_NAMESPACE };
        Set<String> jmxMetrics = readJmxMetricsWithRetry();
        Set<String> expectedMetrics = getExpectedJmxMetrics(tableNames, functionNames);
        // printMetrics(jmxMetrics, "existing metrics in per-table mode");
        // printMetrics(expectedMetrics, "expected metrics in per-table mode");
        // assert that every expected is in the JMX
        for (String expected : expectedMetrics) {
            Assert.assertTrue((("Metric " + expected) + " can not be found in JMX in per-table mode."), jmxMetrics.contains(expected));
        }
    }
}

