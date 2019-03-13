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
package org.apache.hadoop.hbase.regionserver;


import java.util.Collection;
import java.util.Map;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestRegionReplicasAreDistributed {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionReplicasAreDistributed.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionReplicasAreDistributed.class);

    private static final int NB_SERVERS = 3;

    private static Table table;

    private static final HBaseTestingUtility HTU = new HBaseTestingUtility();

    private static final byte[] f = HConstants.CATALOG_FAMILY;

    Map<ServerName, Collection<RegionInfo>> serverVsOnlineRegions;

    Map<ServerName, Collection<RegionInfo>> serverVsOnlineRegions2;

    Map<ServerName, Collection<RegionInfo>> serverVsOnlineRegions3;

    Map<ServerName, Collection<RegionInfo>> serverVsOnlineRegions4;

    @Test
    public void testRegionReplicasCreatedAreDistributed() throws Exception {
        try {
            checkAndAssertRegionDistribution(false);
            // now diesbale and enable the table again. It should be truly distributed
            TestRegionReplicasAreDistributed.HTU.getAdmin().disableTable(TestRegionReplicasAreDistributed.table.getName());
            TestRegionReplicasAreDistributed.LOG.info(("Disabled the table " + (TestRegionReplicasAreDistributed.table.getName())));
            TestRegionReplicasAreDistributed.LOG.info(("enabling the table " + (TestRegionReplicasAreDistributed.table.getName())));
            TestRegionReplicasAreDistributed.HTU.getAdmin().enableTable(TestRegionReplicasAreDistributed.table.getName());
            TestRegionReplicasAreDistributed.LOG.info(("Enabled the table " + (TestRegionReplicasAreDistributed.table.getName())));
            boolean res = checkAndAssertRegionDistribution(true);
            Assert.assertTrue("Region retainment not done ", res);
        } finally {
            TestRegionReplicasAreDistributed.HTU.getAdmin().disableTable(TestRegionReplicasAreDistributed.table.getName());
            TestRegionReplicasAreDistributed.HTU.getAdmin().deleteTable(TestRegionReplicasAreDistributed.table.getName());
        }
    }
}

