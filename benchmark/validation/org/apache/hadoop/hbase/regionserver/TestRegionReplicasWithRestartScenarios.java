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
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestRegionReplicasWithRestartScenarios {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionReplicasWithRestartScenarios.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionReplicasWithRestartScenarios.class);

    @Rule
    public TestName name = new TestName();

    private static final int NB_SERVERS = 3;

    private Table table;

    private static final HBaseTestingUtility HTU = new HBaseTestingUtility();

    private static final byte[] f = HConstants.CATALOG_FAMILY;

    @Test
    public void testRegionReplicasCreated() throws Exception {
        Collection<HRegion> onlineRegions = getRS().getOnlineRegionsLocalContext();
        boolean res = checkDuplicates(onlineRegions);
        Assert.assertFalse(res);
        Collection<HRegion> onlineRegions2 = getSecondaryRS().getOnlineRegionsLocalContext();
        res = checkDuplicates(onlineRegions2);
        Assert.assertFalse(res);
        Collection<HRegion> onlineRegions3 = getTertiaryRS().getOnlineRegionsLocalContext();
        checkDuplicates(onlineRegions3);
        Assert.assertFalse(res);
        int totalRegions = ((onlineRegions.size()) + (onlineRegions2.size())) + (onlineRegions3.size());
        Assert.assertEquals(61, totalRegions);
    }
}

