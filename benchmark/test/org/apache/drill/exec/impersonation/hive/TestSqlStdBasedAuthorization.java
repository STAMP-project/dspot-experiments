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
package org.apache.drill.exec.impersonation.hive;


import org.apache.drill.categories.HiveStorageTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.shaded.guava.com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SlowTest.class, HiveStorageTest.class })
public class TestSqlStdBasedAuthorization extends BaseTestHiveImpersonation {
    private static final String db_general = "db_general";

    // Tables in "db_general"
    private static final String g_student_user0 = "student_user0";

    private static final String vw_student_user0 = "vw_student_user0";

    private static final String g_voter_role0 = "voter_role0";

    private static final String vw_voter_role0 = "vw_voter_role0";

    private static final String g_student_user2 = "student_user2";

    private static final String vw_student_user2 = "vw_student_user2";

    // Create a view on "g_student_user0". View is owned by user0:group0 and has permissions 750
    private static final String v_student_u0g0_750 = "v_student_u0g0_750";

    // Create a view on "v_student_u0g0_750". View is owned by user1:group1 and has permissions 750
    private static final String v_student_u1g1_750 = "v_student_u1g1_750";

    // Role for testing purpose
    private static final String test_role0 = "role0";

    @Test
    public void user0_showTables() throws Exception {
        updateClient(org1Users[0]);
        // Users are expected to see all tables in a database even if they don't have permissions to read from tables.
        showTablesHelper(TestSqlStdBasedAuthorization.db_general, ImmutableList.of(TestSqlStdBasedAuthorization.g_student_user0, TestSqlStdBasedAuthorization.g_student_user2, TestSqlStdBasedAuthorization.g_voter_role0, TestSqlStdBasedAuthorization.vw_student_user0, TestSqlStdBasedAuthorization.vw_voter_role0, TestSqlStdBasedAuthorization.vw_student_user2));
    }

    @Test
    public void user0_allowed_g_student_user0() throws Exception {
        // SELECT on "student_user0" table is granted to user "user0"
        updateClient(org1Users[0]);
        test(((("USE " + (BaseTestHiveImpersonation.hivePluginName)) + ".") + (TestSqlStdBasedAuthorization.db_general)));
        test(String.format("SELECT * FROM %s ORDER BY name LIMIT 2", TestSqlStdBasedAuthorization.g_student_user0));
    }

    @Test
    public void user0_allowed_vw_student_user0() throws Exception {
        TestSqlStdBasedAuthorization.queryHiveView(org1Users[0], TestSqlStdBasedAuthorization.vw_student_user0);
    }

    @Test
    public void user0_forbidden_g_voter_role0() throws Exception {
        // SELECT on table "student_user0" is NOT granted to user "user0" directly or indirectly through role "role0" as
        // user "user0" is not part of role "role0"
        updateClient(org1Users[0]);
        test(((("USE " + (BaseTestHiveImpersonation.hivePluginName)) + ".") + (TestSqlStdBasedAuthorization.db_general)));
        final String query = String.format("SELECT * FROM %s ORDER BY name LIMIT 2", TestSqlStdBasedAuthorization.g_voter_role0);
        errorMsgTestHelper(query, ("Principal [name=user0_1, type=USER] does not have following privileges for " + "operation QUERY [[SELECT] on Object [type=TABLE_OR_VIEW, name=db_general.voter_role0]]\n"));
    }

    @Test
    public void user0_forbidden_vw_voter_role0() throws Exception {
        TestSqlStdBasedAuthorization.queryHiveViewNotAuthorized(org1Users[0], TestSqlStdBasedAuthorization.vw_voter_role0);
    }

    @Test
    public void user0_forbidden_v_student_u1g1_750() throws Exception {
        updateClient(org1Users[0]);
        BaseTestHiveImpersonation.queryViewNotAuthorized(TestSqlStdBasedAuthorization.v_student_u1g1_750);
    }

    @Test
    public void user0_allowed_v_student_u0g0_750() throws Exception {
        updateClient(org1Users[0]);
        BaseTestHiveImpersonation.queryView(TestSqlStdBasedAuthorization.v_student_u0g0_750);
    }

    @Test
    public void user1_showTables() throws Exception {
        updateClient(org1Users[1]);
        // Users are expected to see all tables in a database even if they don't have permissions to read from tables.
        showTablesHelper(TestSqlStdBasedAuthorization.db_general, ImmutableList.of(TestSqlStdBasedAuthorization.g_student_user0, TestSqlStdBasedAuthorization.g_student_user2, TestSqlStdBasedAuthorization.g_voter_role0, TestSqlStdBasedAuthorization.vw_student_user0, TestSqlStdBasedAuthorization.vw_voter_role0, TestSqlStdBasedAuthorization.vw_student_user2));
    }

    @Test
    public void user1_forbidden_g_student_user0() throws Exception {
        // SELECT on table "student_user0" is NOT granted to user "user1"
        updateClient(org1Users[1]);
        test(((("USE " + (BaseTestHiveImpersonation.hivePluginName)) + ".") + (TestSqlStdBasedAuthorization.db_general)));
        final String query = String.format("SELECT * FROM %s ORDER BY name LIMIT 2", TestSqlStdBasedAuthorization.g_student_user0);
        errorMsgTestHelper(query, ("Principal [name=user1_1, type=USER] does not have following privileges for " + "operation QUERY [[SELECT] on Object [type=TABLE_OR_VIEW, name=db_general.student_user0]]\n"));
    }

    @Test
    public void user1_forbidden_vw_student_user0() throws Exception {
        TestSqlStdBasedAuthorization.queryHiveViewNotAuthorized(org1Users[1], TestSqlStdBasedAuthorization.vw_student_user0);
    }

    @Test
    public void user1_allowed_g_voter_role0() throws Exception {
        // SELECT on "voter_role0" table is granted to role "role0" and user "user1" is part the role "role0"
        updateClient(org1Users[1]);
        test(((("USE " + (BaseTestHiveImpersonation.hivePluginName)) + ".") + (TestSqlStdBasedAuthorization.db_general)));
        test(String.format("SELECT * FROM %s ORDER BY name LIMIT 2", TestSqlStdBasedAuthorization.g_voter_role0));
    }

    @Test
    public void user1_allowed_vw_voter_role0() throws Exception {
        TestSqlStdBasedAuthorization.queryHiveView(org1Users[1], TestSqlStdBasedAuthorization.vw_voter_role0);
    }

    @Test
    public void user1_allowed_g_voter_role0_but_forbidden_g_student_user2() throws Exception {
        // SELECT on "voter_role0" table is granted to role "role0" and user "user1" is part the role "role0"
        // SELECT on "student_user2" table is NOT granted to either role "role0" or user "user1"
        updateClient(org1Users[1]);
        test(((("USE " + (BaseTestHiveImpersonation.hivePluginName)) + ".") + (TestSqlStdBasedAuthorization.db_general)));
        final String query = String.format("SELECT * FROM %s v JOIN %s s on v.name = s.name limit 2;", TestSqlStdBasedAuthorization.g_voter_role0, TestSqlStdBasedAuthorization.g_student_user2);
        errorMsgTestHelper(query, ("Principal [name=user1_1, type=USER] does not have following privileges for " + "operation QUERY [[SELECT] on Object [type=TABLE_OR_VIEW, name=db_general.student_user2]]"));
    }

    @Test
    public void user1_allowed_vw_voter_role0_but_forbidden_vw_student_user2() throws Exception {
        // SELECT on "vw_voter_role0" table is granted to role "role0" and user "user1" is part the role "role0"
        // SELECT on "vw_student_user2" table is NOT granted to either role "role0" or user "user1"
        updateClient(org1Users[1]);
        test(((("USE " + (BaseTestHiveImpersonation.hivePluginName)) + ".") + (TestSqlStdBasedAuthorization.db_general)));
        final String query = String.format("SELECT * FROM %s v JOIN %s s on v.name = s.name limit 2;", TestSqlStdBasedAuthorization.vw_voter_role0, TestSqlStdBasedAuthorization.vw_student_user2);
        errorMsgTestHelper(query, ("Principal [name=user1_1, type=USER] does not have following privileges for " + "operation QUERY [[SELECT] on Object [type=TABLE_OR_VIEW, name=db_general.vw_student_user2]]"));
    }

    @Test
    public void user1_allowed_v_student_u0g0_750() throws Exception {
        updateClient(org1Users[1]);
        BaseTestHiveImpersonation.queryView(TestSqlStdBasedAuthorization.v_student_u0g0_750);
    }

    @Test
    public void user1_allowed_v_student_u1g1_750() throws Exception {
        updateClient(org1Users[1]);
        BaseTestHiveImpersonation.queryView(TestSqlStdBasedAuthorization.v_student_u1g1_750);
    }

    @Test
    public void user2_allowed_g_voter_role0() throws Exception {
        // SELECT on "voter_role0" table is granted to role "role0" and user "user2" is part the role "role0"
        updateClient(org1Users[2]);
        test(((("USE " + (BaseTestHiveImpersonation.hivePluginName)) + ".") + (TestSqlStdBasedAuthorization.db_general)));
        test(String.format("SELECT * FROM %s ORDER BY name LIMIT 2", TestSqlStdBasedAuthorization.g_voter_role0));
    }

    @Test
    public void user2_allowed_vw_voter_role0() throws Exception {
        TestSqlStdBasedAuthorization.queryHiveView(org1Users[2], TestSqlStdBasedAuthorization.vw_voter_role0);
    }

    @Test
    public void user2_allowed_g_student_user2() throws Exception {
        // SELECT on "student_user2" table is granted to user "user2"
        updateClient(org1Users[2]);
        test(((("USE " + (BaseTestHiveImpersonation.hivePluginName)) + ".") + (TestSqlStdBasedAuthorization.db_general)));
        test(String.format("SELECT * FROM %s ORDER BY name LIMIT 2", TestSqlStdBasedAuthorization.g_student_user2));
    }

    @Test
    public void user2_allowed_vw_student_user2() throws Exception {
        TestSqlStdBasedAuthorization.queryHiveView(org1Users[2], TestSqlStdBasedAuthorization.vw_student_user2);
    }

    @Test
    public void user2_allowed_g_voter_role0_and_g_student_user2() throws Exception {
        // SELECT on "voter_role0" table is granted to role "role0" and user "user2" is part the role "role0"
        // SELECT on "student_user2" table is granted to user "user2"
        updateClient(org1Users[2]);
        test(((("USE " + (BaseTestHiveImpersonation.hivePluginName)) + ".") + (TestSqlStdBasedAuthorization.db_general)));
        test(String.format("SELECT * FROM %s v JOIN %s s on v.name = s.name limit 2;", TestSqlStdBasedAuthorization.g_voter_role0, TestSqlStdBasedAuthorization.g_student_user2));
    }

    @Test
    public void user2_allowed_vw_voter_role0_and_vw_student_user2() throws Exception {
        updateClient(org1Users[2]);
        test(((("USE " + (BaseTestHiveImpersonation.hivePluginName)) + ".") + (TestSqlStdBasedAuthorization.db_general)));
        test(String.format("SELECT * FROM %s v JOIN %s s on v.name = s.name limit 2;", TestSqlStdBasedAuthorization.vw_voter_role0, TestSqlStdBasedAuthorization.vw_student_user2));
    }

    @Test
    public void user2_forbidden_v_student_u0g0_750() throws Exception {
        updateClient(org1Users[2]);
        BaseTestHiveImpersonation.queryViewNotAuthorized(TestSqlStdBasedAuthorization.v_student_u0g0_750);
    }

    @Test
    public void user2_allowed_v_student_u1g1_750() throws Exception {
        updateClient(org1Users[2]);
        BaseTestHiveImpersonation.queryView(TestSqlStdBasedAuthorization.v_student_u1g1_750);
    }
}

