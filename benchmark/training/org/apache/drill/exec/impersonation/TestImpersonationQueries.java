/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.impersonation;


import org.apache.drill.categories.SecurityTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.common.exceptions.UserRemoteException;
import org.apache.drill.test.BaseTestQuery;
import org.apache.hadoop.fs.Path;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test queries involving direct impersonation and multilevel impersonation including join queries where each side is
 * a nested view.
 */
@Category({ SlowTest.class, SecurityTest.class })
public class TestImpersonationQueries extends BaseTestImpersonation {
    @Test
    public void testDirectImpersonation_HasUserReadPermissions() throws Exception {
        // Table lineitem is owned by "user0_1:group0_1" with permissions 750. Try to read the table as "user0_1". We
        // shouldn't expect any errors.
        BaseTestQuery.updateClient(BaseTestImpersonation.org1Users[0]);
        BaseTestQuery.test("SELECT * FROM %s.lineitem ORDER BY l_orderkey LIMIT 1", BaseTestImpersonation.getWSSchema(BaseTestImpersonation.org1Users[0]));
    }

    @Test
    public void testDirectImpersonation_HasGroupReadPermissions() throws Exception {
        // Table lineitem is owned by "user0_1:group0_1" with permissions 750. Try to read the table as "user1_1". We
        // shouldn't expect any errors as "user1_1" is part of the "group0_1"
        BaseTestQuery.updateClient(BaseTestImpersonation.org1Users[1]);
        BaseTestQuery.test("SELECT * FROM %s.lineitem ORDER BY l_orderkey LIMIT 1", BaseTestImpersonation.getWSSchema(BaseTestImpersonation.org1Users[0]));
    }

    @Test
    public void testDirectImpersonation_NoReadPermissions() throws Exception {
        UserRemoteException ex = null;
        try {
            // Table lineitem is owned by "user0_1:group0_1" with permissions 750. Now try to read the table as "user2_1". We
            // should expect a permission denied error as "user2_1" is not part of the "group0_1"
            BaseTestQuery.updateClient(BaseTestImpersonation.org1Users[2]);
            BaseTestQuery.test("SELECT * FROM %s.lineitem ORDER BY l_orderkey LIMIT 1", BaseTestImpersonation.getWSSchema(BaseTestImpersonation.org1Users[0]));
        } catch (UserRemoteException e) {
            ex = e;
        }
        Assert.assertNotNull("UserRemoteException is expected", ex);
        Assert.assertThat(ex.getMessage(), StringContains.containsString(("PERMISSION ERROR: " + (String.format("Not authorized to read table [lineitem] in schema [%s.user0_1]", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME)))));
    }

    @Test
    public void testMultiLevelImpersonationEqualToMaxUserHops() throws Exception {
        BaseTestQuery.updateClient(BaseTestImpersonation.org1Users[4]);
        BaseTestQuery.test("SELECT * from %s.u4_lineitem LIMIT 1;", BaseTestImpersonation.getWSSchema(BaseTestImpersonation.org1Users[4]));
    }

    @Test
    public void testMultiLevelImpersonationExceedsMaxUserHops() throws Exception {
        UserRemoteException ex = null;
        try {
            BaseTestQuery.updateClient(BaseTestImpersonation.org1Users[5]);
            BaseTestQuery.test("SELECT * from %s.u4_lineitem LIMIT 1;", BaseTestImpersonation.getWSSchema(BaseTestImpersonation.org1Users[4]));
        } catch (UserRemoteException e) {
            ex = e;
        }
        Assert.assertNotNull("UserRemoteException is expected", ex);
        Assert.assertThat(ex.getMessage(), StringContains.containsString(("Cannot issue token for view expansion as issuing the token exceeds the maximum allowed number " + "of user hops (3) in chained impersonation")));
    }

    @Test
    public void testMultiLevelImpersonationJoinEachSideReachesMaxUserHops() throws Exception {
        BaseTestQuery.updateClient(BaseTestImpersonation.org1Users[4]);
        BaseTestQuery.test("SELECT * from %s.u4_lineitem l JOIN %s.u3_orders o ON l.l_orderkey = o.o_orderkey LIMIT 1", BaseTestImpersonation.getWSSchema(BaseTestImpersonation.org1Users[4]), BaseTestImpersonation.getWSSchema(BaseTestImpersonation.org2Users[3]));
    }

    @Test
    public void testMultiLevelImpersonationJoinOneSideExceedsMaxUserHops() throws Exception {
        UserRemoteException ex = null;
        try {
            BaseTestQuery.updateClient(BaseTestImpersonation.org1Users[4]);
            BaseTestQuery.test("SELECT * from %s.u4_lineitem l JOIN %s.u4_orders o ON l.l_orderkey = o.o_orderkey LIMIT 1", BaseTestImpersonation.getWSSchema(BaseTestImpersonation.org1Users[4]), BaseTestImpersonation.getWSSchema(BaseTestImpersonation.org2Users[4]));
        } catch (UserRemoteException e) {
            ex = e;
        }
        Assert.assertNotNull("UserRemoteException is expected", ex);
        Assert.assertThat(ex.getMessage(), StringContains.containsString(("Cannot issue token for view expansion as issuing the token exceeds the maximum allowed number " + "of user hops (3) in chained impersonation")));
    }

    @Test
    public void sequenceFileChainedImpersonationWithView() throws Exception {
        // create a view named "simple_seq_view" on "simple.seq". View is owned by user0:group0 and has permissions 750
        BaseTestImpersonation.createView(BaseTestImpersonation.org1Users[0], BaseTestImpersonation.org1Groups[0], "simple_seq_view", String.format("SELECT convert_from(t.binary_key, 'UTF8') as k FROM %s.`%s` t", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME, new Path(BaseTestImpersonation.getUserHome(BaseTestImpersonation.org1Users[0]), "simple.seq")));
        try {
            BaseTestQuery.updateClient(BaseTestImpersonation.org1Users[1]);
            BaseTestQuery.test("SELECT k FROM %s.%s.%s", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME, "tmp", "simple_seq_view");
        } catch (UserRemoteException e) {
            Assert.assertNull("This test should pass.", e);
        }
        BaseTestImpersonation.createView(BaseTestImpersonation.org1Users[1], BaseTestImpersonation.org1Groups[1], "simple_seq_view_2", String.format("SELECT k FROM %s.%s.%s", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME, "tmp", "simple_seq_view"));
        try {
            BaseTestQuery.updateClient(BaseTestImpersonation.org1Users[2]);
            BaseTestQuery.test("SELECT k FROM %s.%s.%s", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME, "tmp", "simple_seq_view_2");
        } catch (UserRemoteException e) {
            Assert.assertNull("This test should pass.", e);
        }
    }

    @Test
    public void avroChainedImpersonationWithView() throws Exception {
        BaseTestImpersonation.createView(BaseTestImpersonation.org1Users[0], BaseTestImpersonation.org1Groups[0], "simple_avro_view", String.format("SELECT h_boolean, e_double FROM %s.`%s` t", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME, new Path(BaseTestImpersonation.getUserHome(BaseTestImpersonation.org1Users[0]), "simple.avro")));
        try {
            BaseTestQuery.updateClient(BaseTestImpersonation.org1Users[1]);
            BaseTestQuery.test("SELECT h_boolean FROM %s.%s.%s", BaseTestImpersonation.MINI_DFS_STORAGE_PLUGIN_NAME, "tmp", "simple_avro_view");
        } catch (UserRemoteException e) {
            Assert.assertNull("This test should pass.", e);
        }
    }
}

