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


import TableType.TABLE;
import TableType.VIEW;
import java.util.Collections;
import org.apache.drill.categories.HiveStorageTest;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.shaded.guava.com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SlowTest.class, HiveStorageTest.class })
public class TestStorageBasedHiveAuthorization extends BaseTestHiveImpersonation {
    // DB whose warehouse directory has permissions 755, available everyone to read
    private static final String db_general = "db_general";

    // Tables in "db_general"
    private static final String g_student_u0_700 = "student_u0_700";

    private static final String g_vw_g_student_u0_700 = "vw_u0_700_student_u0_700";

    private static final String g_student_u0g0_750 = "student_u0g0_750";

    private static final String g_student_all_755 = "student_all_755";

    private static final String g_voter_u1_700 = "voter_u1_700";

    private static final String g_voter_u2g1_750 = "voter_u2g1_750";

    private static final String g_voter_all_755 = "voter_all_755";

    private static final String g_partitioned_student_u0_700 = "partitioned_student_u0_700";

    // DB whose warehouse directory has permissions 700 and owned by user0
    private static final String db_u0_only = "db_u0_only";

    // Tables in "db_u0_only"
    private static final String u0_student_all_755 = "student_all_755";

    private static final String u0_voter_all_755 = "voter_all_755";

    private static final String u0_vw_voter_all_755 = "vw_voter_all_755";

    // DB whose warehouse directory has permissions 750 and owned by user1 and group1
    private static final String db_u1g1_only = "db_u1g1_only";

    // Tables in "db_u1g1_only"
    private static final String u1g1_student_all_755 = "student_all_755";

    private static final String u1g1_student_u1_700 = "student_u1_700";

    private static final String u1g1_voter_all_755 = "voter_all_755";

    private static final String u1g1_voter_u1_700 = "voter_u1_700";

    // Create a view on "student_u0_700". View is owned by user0:group0 and has permissions 750
    private static final String v_student_u0g0_750 = "v_student_u0g0_750";

    // Create a view on "v_student_u0g0_750". View is owned by user1:group1 and has permissions 750
    private static final String v_student_u1g1_750 = "v_student_u1g1_750";

    // Create a view on "partitioned_student_u0_700". View is owned by user0:group0 and has permissions 750
    private static final String v_partitioned_student_u0g0_750 = "v_partitioned_student_u0g0_750";

    // Create a view on "v_partitioned_student_u0g0_750". View is owned by user1:group1 and has permissions 750
    private static final String v_partitioned_student_u1g1_750 = "v_partitioned_student_u1g1_750";

    // rwx  -   -
    // 1. Only owning user have read, write and execute rights
    private static final short _700 = ((short) (448));

    // rwx  r-x -
    // 1. Owning user have read, write and execute rights
    // 2. Owning group have read and execute rights
    private static final short _750 = ((short) (488));

    // rwx  r-x r-x
    // 1. Owning user have read, write and execute rights
    // 2. Owning group have read and execute rights
    // 3. Others have read and execute rights
    private static final short _755 = ((short) (493));

    // Irrespective of each db permissions, all dbs show up in "SHOW SCHEMAS"
    @Test
    public void showSchemas() throws Exception {
        testBuilder().sqlQuery("SHOW SCHEMAS LIKE 'hive.%'").unOrdered().baselineColumns("SCHEMA_NAME").baselineValues("hive.db_general").baselineValues("hive.db_u0_only").baselineValues("hive.db_u1g1_only").baselineValues("hive.default").go();
    }

    /**
     * Should only contain the tables that the user
     * has access to read.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void user0_db_general_showTables() throws Exception {
        updateClient(org1Users[0]);
        showTablesHelper(TestStorageBasedHiveAuthorization.db_general, ImmutableList.of(TestStorageBasedHiveAuthorization.g_student_u0_700, TestStorageBasedHiveAuthorization.g_student_u0g0_750, TestStorageBasedHiveAuthorization.g_student_all_755, TestStorageBasedHiveAuthorization.g_voter_all_755, TestStorageBasedHiveAuthorization.g_partitioned_student_u0_700, TestStorageBasedHiveAuthorization.g_vw_g_student_u0_700));
    }

    @Test
    public void user0_db_u0_only_showTables() throws Exception {
        updateClient(org1Users[0]);
        showTablesHelper(TestStorageBasedHiveAuthorization.db_u0_only, ImmutableList.of(TestStorageBasedHiveAuthorization.u0_student_all_755, TestStorageBasedHiveAuthorization.u0_voter_all_755, TestStorageBasedHiveAuthorization.u0_vw_voter_all_755));
    }

    /**
     * If the user has no read access to the db, the list will be always empty even if the user has
     * read access to the tables inside the db.
     */
    @Test
    public void user0_db_u1g1_only_showTables() throws Exception {
        updateClient(org1Users[0]);
        showTablesHelper(TestStorageBasedHiveAuthorization.db_u1g1_only, Collections.emptyList());
    }

    @Test
    public void user0_db_general_infoSchema() throws Exception {
        updateClient(org1Users[0]);
        fromInfoSchemaHelper(TestStorageBasedHiveAuthorization.db_general, ImmutableList.of(TestStorageBasedHiveAuthorization.g_student_u0_700, TestStorageBasedHiveAuthorization.g_student_u0g0_750, TestStorageBasedHiveAuthorization.g_student_all_755, TestStorageBasedHiveAuthorization.g_voter_all_755, TestStorageBasedHiveAuthorization.g_partitioned_student_u0_700, TestStorageBasedHiveAuthorization.g_vw_g_student_u0_700), ImmutableList.of(TABLE, TABLE, TABLE, TABLE, TABLE, VIEW));
    }

    @Test
    public void user0_db_u0_only_infoSchema() throws Exception {
        updateClient(org1Users[0]);
        fromInfoSchemaHelper(TestStorageBasedHiveAuthorization.db_u0_only, ImmutableList.of(TestStorageBasedHiveAuthorization.u0_student_all_755, TestStorageBasedHiveAuthorization.u0_voter_all_755, TestStorageBasedHiveAuthorization.u0_vw_voter_all_755), ImmutableList.of(TABLE, TABLE, VIEW));
    }

    @Test
    public void user0_db_u1g1_only_infoSchema() throws Exception {
        updateClient(org1Users[0]);
        fromInfoSchemaHelper(TestStorageBasedHiveAuthorization.db_u1g1_only, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * user0 is 700 owner
     */
    @Test
    public void user0_allowed_g_student_u0_700() throws Exception {
        updateClient(org1Users[0]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_student_u0_700);
    }

    @Test
    public void user0_allowed_g_vw_u0_700_over_g_student_u0_700() throws Exception {
        updateClient(org1Users[0]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_vw_g_student_u0_700);
    }

    @Test
    public void user1_forbidden_g_vw_u0_700_over_g_student_u0_700() throws Exception {
        updateClient(org1Users[1]);
        queryHiveViewFailed(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_vw_g_student_u0_700);
    }

    @Test
    public void user2_forbidden_g_vw_u0_700_over_g_student_u0_700() throws Exception {
        updateClient(org1Users[2]);
        queryHiveViewFailed(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_vw_g_student_u0_700);
    }

    @Test
    public void user0_allowed_u0_vw_voter_all_755() throws Exception {
        updateClient(org1Users[0]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_u0_only, TestStorageBasedHiveAuthorization.u0_vw_voter_all_755);
    }

    @Test
    public void user1_forbidden_u0_vw_voter_all_755() throws Exception {
        updateClient(org1Users[1]);
        queryHiveViewFailed(TestStorageBasedHiveAuthorization.db_u0_only, TestStorageBasedHiveAuthorization.u0_vw_voter_all_755);
    }

    @Test
    public void user2_forbidden_u0_vw_voter_all_755() throws Exception {
        updateClient(org1Users[2]);
        queryHiveViewFailed(TestStorageBasedHiveAuthorization.db_u0_only, TestStorageBasedHiveAuthorization.u0_vw_voter_all_755);
    }

    /**
     * user0 is 750 owner
     */
    @Test
    public void user0_allowed_g_student_u0g0_750() throws Exception {
        updateClient(org1Users[0]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_student_u0g0_750);
    }

    /**
     * table owned by user2 and group2,
     * but user0 can access because Others allowed to read and execute
     */
    @Test
    public void user0_allowed_g_student_all_755() throws Exception {
        updateClient(org1Users[0]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_student_all_755);
    }

    /**
     * user0 can't access because, user1 is 700 owner
     */
    @Test
    public void user0_forbidden_g_voter_u1_700() throws Exception {
        updateClient(org1Users[0]);
        TestStorageBasedHiveAuthorization.queryTableNotFound(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_voter_u1_700);
    }

    /**
     * user0 can't access, because only user2 and group1 members
     */
    @Test
    public void user0_forbidden_g_voter_u2g1_750() throws Exception {
        updateClient(org1Users[0]);
        TestStorageBasedHiveAuthorization.queryTableNotFound(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_voter_u2g1_750);
    }

    /**
     * user0 allowed because others have r-x access. Despite
     * of user1 and group1 ownership over the table.
     */
    @Test
    public void user0_allowed_g_voter_all_755() throws Exception {
        updateClient(org1Users[0]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_voter_all_755);
    }

    /**
     * user0 is 755 owner
     */
    @Test
    public void user0_allowed_u0_student_all_755() throws Exception {
        updateClient(org1Users[0]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_u0_only, TestStorageBasedHiveAuthorization.u0_student_all_755);
    }

    /**
     * user0 is 755 owner
     */
    @Test
    public void user0_allowed_u0_voter_all_755() throws Exception {
        updateClient(org1Users[0]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_u0_only, TestStorageBasedHiveAuthorization.u0_voter_all_755);
    }

    /**
     * user0 is 700 owner
     */
    @Test
    public void user0_allowed_g_partitioned_student_u0_700() throws Exception {
        updateClient(org1Users[0]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_partitioned_student_u0_700);
    }

    /**
     * user0 doesn't have access to database db_u1g1_only
     */
    @Test
    public void user0_forbidden_u1g1_student_all_755() throws Exception {
        updateClient(org1Users[0]);
        TestStorageBasedHiveAuthorization.queryTableNotFound(TestStorageBasedHiveAuthorization.db_u1g1_only, TestStorageBasedHiveAuthorization.u1g1_student_all_755);
    }

    @Test
    public void user0_allowed_v_student_u0g0_750() throws Exception {
        updateClient(org1Users[0]);
        BaseTestHiveImpersonation.queryView(TestStorageBasedHiveAuthorization.v_student_u0g0_750);
    }

    @Test
    public void user0_forbidden_v_student_u1g1_750() throws Exception {
        updateClient(org1Users[0]);
        BaseTestHiveImpersonation.queryViewNotAuthorized(TestStorageBasedHiveAuthorization.v_student_u1g1_750);
    }

    @Test
    public void user0_allowed_v_partitioned_student_u0g0_750() throws Exception {
        updateClient(org1Users[0]);
        BaseTestHiveImpersonation.queryView(TestStorageBasedHiveAuthorization.v_partitioned_student_u0g0_750);
    }

    @Test
    public void user0_forbidden_v_partitioned_student_u1g1_750() throws Exception {
        updateClient(org1Users[0]);
        BaseTestHiveImpersonation.queryViewNotAuthorized(TestStorageBasedHiveAuthorization.v_partitioned_student_u1g1_750);
    }

    @Test
    public void user1_db_general_showTables() throws Exception {
        updateClient(org1Users[1]);
        showTablesHelper(TestStorageBasedHiveAuthorization.db_general, ImmutableList.of(TestStorageBasedHiveAuthorization.g_student_u0g0_750, TestStorageBasedHiveAuthorization.g_student_all_755, TestStorageBasedHiveAuthorization.g_voter_u1_700, TestStorageBasedHiveAuthorization.g_voter_u2g1_750, TestStorageBasedHiveAuthorization.g_voter_all_755, TestStorageBasedHiveAuthorization.g_vw_g_student_u0_700));
    }

    @Test
    public void user1_db_u1g1_only_showTables() throws Exception {
        updateClient(org1Users[1]);
        showTablesHelper(TestStorageBasedHiveAuthorization.db_u1g1_only, ImmutableList.of(TestStorageBasedHiveAuthorization.u1g1_student_all_755, TestStorageBasedHiveAuthorization.u1g1_student_u1_700, TestStorageBasedHiveAuthorization.u1g1_voter_all_755, TestStorageBasedHiveAuthorization.u1g1_voter_u1_700));
    }

    @Test
    public void user1_db_u0_only_showTables() throws Exception {
        updateClient(org1Users[1]);
        showTablesHelper(TestStorageBasedHiveAuthorization.db_u0_only, newArrayList(TestStorageBasedHiveAuthorization.u0_vw_voter_all_755));
    }

    @Test
    public void user1_db_general_infoSchema() throws Exception {
        updateClient(org1Users[1]);
        fromInfoSchemaHelper(TestStorageBasedHiveAuthorization.db_general, ImmutableList.of(TestStorageBasedHiveAuthorization.g_student_u0g0_750, TestStorageBasedHiveAuthorization.g_student_all_755, TestStorageBasedHiveAuthorization.g_voter_u1_700, TestStorageBasedHiveAuthorization.g_voter_u2g1_750, TestStorageBasedHiveAuthorization.g_voter_all_755, TestStorageBasedHiveAuthorization.g_vw_g_student_u0_700), ImmutableList.of(TABLE, TABLE, TABLE, TABLE, TABLE, VIEW));
    }

    @Test
    public void user1_db_u1g1_only_infoSchema() throws Exception {
        updateClient(org1Users[1]);
        fromInfoSchemaHelper(TestStorageBasedHiveAuthorization.db_u1g1_only, ImmutableList.of(TestStorageBasedHiveAuthorization.u1g1_student_all_755, TestStorageBasedHiveAuthorization.u1g1_student_u1_700, TestStorageBasedHiveAuthorization.u1g1_voter_all_755, TestStorageBasedHiveAuthorization.u1g1_voter_u1_700), ImmutableList.of(TABLE, TABLE, TABLE, TABLE));
    }

    @Test
    public void user1_db_u0_only_infoSchema() throws Exception {
        updateClient(org1Users[1]);
        fromInfoSchemaHelper(TestStorageBasedHiveAuthorization.db_u0_only, newArrayList(TestStorageBasedHiveAuthorization.u0_vw_voter_all_755), newArrayList(VIEW));
    }

    /**
     * user1 can't access, because user0 is 700 owner
     */
    @Test
    public void user1_forbidden_g_student_u0_700() throws Exception {
        updateClient(org1Users[1]);
        TestStorageBasedHiveAuthorization.queryTableNotFound(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_student_u0_700);
    }

    /**
     * user1 allowed because he's a member of group0
     */
    @Test
    public void user1_allowed_g_student_u0g0_750() throws Exception {
        updateClient(org1Users[1]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_student_u0g0_750);
    }

    /**
     * user1 allowed because Others have r-x access
     */
    @Test
    public void user1_allowed_g_student_all_755() throws Exception {
        updateClient(org1Users[1]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_student_all_755);
    }

    /**
     * user1 is 700 owner
     */
    @Test
    public void user1_allowed_g_voter_u1_700() throws Exception {
        updateClient(org1Users[1]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_voter_u1_700);
    }

    /**
     * user1 allowed because he's member of group1
     */
    @Test
    public void user1_allowed_g_voter_u2g1_750() throws Exception {
        updateClient(org1Users[1]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_voter_u2g1_750);
    }

    /**
     * user1 is 755 owner
     */
    @Test
    public void user1_allowed_g_voter_all_755() throws Exception {
        updateClient(org1Users[1]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_voter_all_755);
    }

    /**
     * here access restricted at db level, only user0 can access  db_u0_only
     */
    @Test
    public void user1_forbidden_u0_student_all_755() throws Exception {
        updateClient(org1Users[1]);
        TestStorageBasedHiveAuthorization.queryTableNotFound(TestStorageBasedHiveAuthorization.db_u0_only, TestStorageBasedHiveAuthorization.u0_student_all_755);
    }

    /**
     * here access restricted at db level, only user0 can access db_u0_only
     */
    @Test
    public void user1_forbidden_u0_voter_all_755() throws Exception {
        updateClient(org1Users[1]);
        TestStorageBasedHiveAuthorization.queryTableNotFound(TestStorageBasedHiveAuthorization.db_u0_only, TestStorageBasedHiveAuthorization.u0_voter_all_755);
    }

    @Test
    public void user1_allowed_v_student_u0g0_750() throws Exception {
        updateClient(org1Users[1]);
        BaseTestHiveImpersonation.queryView(TestStorageBasedHiveAuthorization.v_student_u0g0_750);
    }

    @Test
    public void user1_allowed_v_student_u1g1_750() throws Exception {
        updateClient(org1Users[1]);
        BaseTestHiveImpersonation.queryView(TestStorageBasedHiveAuthorization.v_student_u1g1_750);
    }

    @Test
    public void user1_allowed_v_partitioned_student_u0g0_750() throws Exception {
        updateClient(org1Users[1]);
        BaseTestHiveImpersonation.queryView(TestStorageBasedHiveAuthorization.v_partitioned_student_u0g0_750);
    }

    @Test
    public void user1_allowed_v_partitioned_student_u1g1_750() throws Exception {
        updateClient(org1Users[1]);
        BaseTestHiveImpersonation.queryView(TestStorageBasedHiveAuthorization.v_partitioned_student_u1g1_750);
    }

    @Test
    public void user2_db_general_showTables() throws Exception {
        updateClient(org1Users[2]);
        showTablesHelper(TestStorageBasedHiveAuthorization.db_general, ImmutableList.of(TestStorageBasedHiveAuthorization.g_student_all_755, TestStorageBasedHiveAuthorization.g_voter_u2g1_750, TestStorageBasedHiveAuthorization.g_voter_all_755, TestStorageBasedHiveAuthorization.g_vw_g_student_u0_700));
    }

    @Test
    public void user2_db_u1g1_only_showTables() throws Exception {
        updateClient(org1Users[2]);
        showTablesHelper(TestStorageBasedHiveAuthorization.db_u1g1_only, ImmutableList.of(TestStorageBasedHiveAuthorization.u1g1_student_all_755, TestStorageBasedHiveAuthorization.u1g1_voter_all_755));
    }

    @Test
    public void user2_db_u0_only_showTables() throws Exception {
        updateClient(org1Users[2]);
        showTablesHelper(TestStorageBasedHiveAuthorization.db_u0_only, newArrayList(TestStorageBasedHiveAuthorization.u0_vw_voter_all_755));
    }

    @Test
    public void user2_db_general_infoSchema() throws Exception {
        updateClient(org1Users[2]);
        fromInfoSchemaHelper(TestStorageBasedHiveAuthorization.db_general, ImmutableList.of(TestStorageBasedHiveAuthorization.g_student_all_755, TestStorageBasedHiveAuthorization.g_voter_u2g1_750, TestStorageBasedHiveAuthorization.g_voter_all_755, TestStorageBasedHiveAuthorization.g_vw_g_student_u0_700), ImmutableList.of(TABLE, TABLE, TABLE, VIEW));
    }

    @Test
    public void user2_db_u1g1_only_infoSchema() throws Exception {
        updateClient(org1Users[2]);
        fromInfoSchemaHelper(TestStorageBasedHiveAuthorization.db_u1g1_only, ImmutableList.of(TestStorageBasedHiveAuthorization.u1g1_student_all_755, TestStorageBasedHiveAuthorization.u1g1_voter_all_755), ImmutableList.of(TABLE, TABLE));
    }

    @Test
    public void user2_db_u0_only_infoSchema() throws Exception {
        updateClient(org1Users[2]);
        fromInfoSchemaHelper(TestStorageBasedHiveAuthorization.db_u0_only, newArrayList(TestStorageBasedHiveAuthorization.u0_vw_voter_all_755), newArrayList(VIEW));
    }

    /**
     * user2 can't access, because user0 is 700 owner
     */
    @Test
    public void user2_forbidden_g_student_u0_700() throws Exception {
        updateClient(org1Users[2]);
        TestStorageBasedHiveAuthorization.queryTableNotFound(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_student_u0_700);
    }

    /**
     * user2 can't access, only user0 and group0 members have access
     */
    @Test
    public void user2_forbidden_g_student_u0g0_750() throws Exception {
        updateClient(org1Users[2]);
        TestStorageBasedHiveAuthorization.queryTableNotFound(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_student_u0_700);
    }

    /**
     * user2 is 755 owner
     */
    @Test
    public void user2_allowed_g_student_all_755() throws Exception {
        updateClient(org1Users[2]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_student_all_755);
    }

    /**
     * user2 can't access, because user1 is 700 owner
     */
    @Test
    public void user2_forbidden_g_voter_u1_700() throws Exception {
        updateClient(org1Users[2]);
        TestStorageBasedHiveAuthorization.queryTableNotFound(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_voter_u1_700);
    }

    /**
     * user2 is 750 owner
     */
    @Test
    public void user2_allowed_g_voter_u2g1_750() throws Exception {
        updateClient(org1Users[2]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_voter_u2g1_750);
    }

    /**
     * user2 is member of group1
     */
    @Test
    public void user2_allowed_g_voter_all_755() throws Exception {
        updateClient(org1Users[2]);
        TestStorageBasedHiveAuthorization.queryHiveTableOrView(TestStorageBasedHiveAuthorization.db_general, TestStorageBasedHiveAuthorization.g_voter_all_755);
    }

    /**
     * here access restricted at db level, only user0 can access db_u0_only
     */
    @Test
    public void user2_forbidden_u0_student_all_755() throws Exception {
        updateClient(org1Users[2]);
        TestStorageBasedHiveAuthorization.queryTableNotFound(TestStorageBasedHiveAuthorization.db_u0_only, TestStorageBasedHiveAuthorization.u0_student_all_755);
    }

    /**
     * here access restricted at db level, only user0 can access db_u0_only
     */
    @Test
    public void user2_forbidden_u0_voter_all_755() throws Exception {
        updateClient(org1Users[2]);
        TestStorageBasedHiveAuthorization.queryTableNotFound(TestStorageBasedHiveAuthorization.db_u0_only, TestStorageBasedHiveAuthorization.u0_voter_all_755);
    }

    @Test
    public void user2_forbidden_v_student_u0g0_750() throws Exception {
        updateClient(org1Users[2]);
        BaseTestHiveImpersonation.queryViewNotAuthorized(TestStorageBasedHiveAuthorization.v_student_u0g0_750);
    }

    @Test
    public void user2_allowed_v_student_u1g1_750() throws Exception {
        updateClient(org1Users[2]);
        BaseTestHiveImpersonation.queryView(TestStorageBasedHiveAuthorization.v_student_u1g1_750);
    }

    @Test
    public void user2_forbidden_v_partitioned_student_u0g0_750() throws Exception {
        updateClient(org1Users[2]);
        BaseTestHiveImpersonation.queryViewNotAuthorized(TestStorageBasedHiveAuthorization.v_partitioned_student_u0g0_750);
    }

    @Test
    public void user2_allowed_v_partitioned_student_u1g1_750() throws Exception {
        updateClient(org1Users[2]);
        BaseTestHiveImpersonation.queryView(TestStorageBasedHiveAuthorization.v_partitioned_student_u1g1_750);
    }
}

