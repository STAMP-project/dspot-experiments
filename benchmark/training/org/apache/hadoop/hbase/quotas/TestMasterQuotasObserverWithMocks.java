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
package org.apache.hadoop.hbase.quotas;


import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.coprocessor.CoprocessorHost;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.security.access.AccessController;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test class for MasterQuotasObserver that does not require a cluster.
 */
@Category(SmallTests.class)
public class TestMasterQuotasObserverWithMocks {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterQuotasObserverWithMocks.class);

    private HMaster master;

    private Configuration conf;

    @Test
    public void testAddDefaultObserver() {
        master.updateConfigurationForQuotasObserver(conf);
        Assert.assertEquals(MasterQuotasObserver.class.getName(), conf.get(CoprocessorHost.MASTER_COPROCESSOR_CONF_KEY));
    }

    @Test
    public void testDoNotAddDefaultObserver() {
        conf.setBoolean(MasterQuotasObserver.REMOVE_QUOTA_ON_TABLE_DELETE, false);
        master.updateConfigurationForQuotasObserver(conf);
        // Configuration#getStrings returns null when unset
        Assert.assertNull(conf.getStrings(CoprocessorHost.MASTER_COPROCESSOR_CONF_KEY));
    }

    @Test
    public void testAppendsObserver() {
        conf.set(CoprocessorHost.MASTER_COPROCESSOR_CONF_KEY, AccessController.class.getName());
        master.updateConfigurationForQuotasObserver(conf);
        Set<String> coprocs = new java.util.HashSet(conf.getStringCollection(CoprocessorHost.MASTER_COPROCESSOR_CONF_KEY));
        Assert.assertEquals(2, coprocs.size());
        Assert.assertTrue(("Observed coprocessors were: " + coprocs), coprocs.contains(AccessController.class.getName()));
        Assert.assertTrue(("Observed coprocessors were: " + coprocs), coprocs.contains(MasterQuotasObserver.class.getName()));
    }
}

