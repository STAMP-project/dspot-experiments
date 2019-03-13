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
package org.apache.hadoop.hbase.master.balancer;


import LoadBalancer.SYSTEM_TABLES_ON_MASTER;
import LoadBalancer.TABLES_ON_MASTER;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test options for regions on master; none, system, or any (i.e. master is like any other
 * regionserver). Checks how regions are deployed when each of the options are enabled.
 * It then does kill combinations to make sure the distribution is more than just for startup.
 * NOTE: Regions on Master does not work well. See HBASE-19828. Until addressed, disabling this
 * test.
 */
@Ignore
@Category({ MediumTests.class })
public class TestRegionsOnMasterOptions {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionsOnMasterOptions.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionsOnMasterOptions.class);

    @Rule
    public TestName name = new TestName();

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private Configuration c;

    private String tablesOnMasterOldValue;

    private String systemTablesOnMasterOldValue;

    private static final int SLAVES = 3;

    private static final int MASTERS = 2;

    // Make the count of REGIONS high enough so I can distingush case where master is only carrying
    // system regions from the case where it is carrying any region; i.e. 2 system regions vs more
    // if user + system.
    private static final int REGIONS = 12;

    private static final int SYSTEM_REGIONS = 2;// ns and meta -- no acl unless enabled.


    @Test
    public void testRegionsOnAllServers() throws Exception {
        c.setBoolean(TABLES_ON_MASTER, true);
        c.setBoolean(SYSTEM_TABLES_ON_MASTER, false);
        int rsCount = ((TestRegionsOnMasterOptions.REGIONS) + (TestRegionsOnMasterOptions.SYSTEM_REGIONS)) / ((TestRegionsOnMasterOptions.SLAVES) + 1/* Master */
        );
        checkBalance(rsCount, rsCount);
    }

    @Test
    public void testNoRegionOnMaster() throws Exception {
        c.setBoolean(TABLES_ON_MASTER, false);
        c.setBoolean(SYSTEM_TABLES_ON_MASTER, false);
        int rsCount = ((TestRegionsOnMasterOptions.REGIONS) + (TestRegionsOnMasterOptions.SYSTEM_REGIONS)) / (TestRegionsOnMasterOptions.SLAVES);
        checkBalance(0, rsCount);
    }
}

