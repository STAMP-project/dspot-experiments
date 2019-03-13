/**
 * This file is part of a module with proprietary Enterprise Features.
 *
 * Licensed to Crate.io Inc. ("Crate.io") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *
 * To use this file, Crate.io must have given you permission to enable and
 * use such Enterprise Features and you must have a valid Enterprise or
 * Subscription Agreement with Crate.io.  If you enable or use the Enterprise
 * Features, you represent and warrant that you have a valid Enterprise or
 * Subscription Agreement with Crate.io.  Your use of the Enterprise Features
 * if governed by the terms and conditions of your Enterprise or Subscription
 * Agreement with Crate.io.
 */
package io.crate.auth.user;


import Privilege.Type.DDL;
import Privilege.Type.DML;
import Privilege.Type.DQL;
import io.crate.exceptions.UnauthorizedException;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Test;


public class StatementPrivilegeValidatorTest extends CrateDummyClusterServiceUnitTest {
    private List<List<Object>> validationCallArguments;

    private User user;

    private SQLExecutor e;

    private UserManager userManager;

    private User superUser;

    @Test
    public void testSuperUserByPassesValidation() throws Exception {
        analyzeAsSuperUser("select * from sys.cluster");
        assertThat(validationCallArguments.size(), Matchers.is(0));
    }

    @Test
    public void testCreateUserNotAllowedAsNormalUser() throws Exception {
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage(("User \"normal\" is not authorized to execute the statement. " + "Superuser permissions are required"));
        analyze("create user ford");
    }

    @Test
    public void testDropUserNotAllowedAsNormalUser() throws Exception {
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage(("User \"normal\" is not authorized to execute the statement. " + "Superuser permissions are required"));
        analyze("drop user ford");
    }

    @Test
    public void testAlterOtherUsersNotAllowedAsNormalUser() {
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage(("A regular user can use ALTER USER only on himself. " + "To modify other users superuser permissions are required"));
        analyze("alter user ford set (password = 'pass')");
    }

    @Test
    public void testAlterOwnUserIsAllowed() {
        analyze("alter user normal set (password = 'pass')");
        assertThat(validationCallArguments.size(), Matchers.is(0));
    }

    @Test
    public void testPrivilegesNotAllowedAsNormalUser() throws Exception {
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage(("User \"normal\" is not authorized to execute the statement. " + "Superuser permissions are required"));
        analyze("grant dql to normal");
    }

    @Test
    public void testOptimizeNotAllowedAsNormalUser() throws Exception {
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage(("User \"normal\" is not authorized to execute the statement. " + "Superuser permissions are required"));
        analyze("optimize table users");
    }

    @Test
    public void testSetGlobalNotAllowedAsNormalUser() throws Exception {
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage(("User \"normal\" is not authorized to execute the statement. " + "Superuser permissions are required"));
        analyze("set global stats.enabled = true");
    }

    @Test
    public void testResetNotAllowedAsNormalUser() throws Exception {
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage(("User \"normal\" is not authorized to execute the statement. " + "Superuser permissions are required"));
        analyze("reset global stats.enabled");
    }

    @Test
    public void testKillNotAllowedAsNormalUser() throws Exception {
        expectedException.expect(UnauthorizedException.class);
        expectedException.expectMessage(("User \"normal\" is not authorized to execute the statement. " + "Superuser permissions are required"));
        analyze("kill all");
    }

    @Test
    public void testAlterTable() throws Exception {
        analyze("alter table users set (number_of_replicas=1)");
        assertAskedForTable(DDL, "doc.users");
    }

    @Test
    public void testCopyFrom() throws Exception {
        analyze("copy users from 'file:///tmp'");
        assertAskedForTable(DML, "doc.users");
    }

    @Test
    public void testCopyTo() throws Exception {
        analyze("copy users to DIRECTORY '/tmp'");
        assertAskedForTable(DQL, "doc.users");
    }

    @Test
    public void testCreateTable() throws Exception {
        analyze("create table my_schema.my_table (id int)");
        assertAskedForSchema(DDL, "my_schema");
    }

    @Test
    public void testCreateBlobTable() throws Exception {
        analyze("create blob table myblobs");
        assertAskedForSchema(DDL, "blob");
    }

    @Test
    public void testCreateRepository() throws Exception {
        analyze("create repository new_repository TYPE fs with (location='/tmp', compress=True)");
        assertAskedForCluster(DDL);
    }

    @Test
    public void testDropRepository() throws Exception {
        analyze("drop repository my_repo");
        assertAskedForCluster(DDL);
    }

    @Test
    public void testCreateSnapshot() throws Exception {
        analyze("create snapshot my_repo.my_snapshot table users");
        assertAskedForCluster(DDL);
    }

    @Test
    public void testRestoreSnapshot() throws Exception {
        analyze("restore snapshot my_repo.my_snapshot table my_table");
        assertAskedForCluster(DDL);
    }

    @Test
    public void testDropSnapshot() throws Exception {
        analyze("drop snapshot my_repo.my_snap_1");
        assertAskedForCluster(DDL);
    }

    @Test
    public void testDelete() throws Exception {
        analyze("delete from users");
        assertAskedForTable(DML, "doc.users");
    }

    @Test
    public void testInsertFromValues() throws Exception {
        analyze("insert into users (id) values (1)");
        assertAskedForTable(DML, "doc.users");
    }

    @Test
    public void testInsertFromSubquery() throws Exception {
        analyze("insert into users (id) ( select id from parted )");
        assertAskedForTable(DML, "doc.users");
        assertAskedForTable(DQL, "doc.parted");
    }

    @Test
    public void testUpdate() throws Exception {
        analyze("update users set name = 'ford' where id = 1");
        assertAskedForTable(DML, "doc.users");
    }

    @Test
    public void testSelectSingleRelation() throws Exception {
        analyze("select * from sys.cluster");
        assertAskedForTable(DQL, "sys.cluster");
    }

    @Test
    public void testSelectMultiRelation() throws Exception {
        analyze("select * from sys.cluster, users");
        assertAskedForTable(DQL, "sys.cluster");
        assertAskedForTable(DQL, "doc.users");
    }

    @Test
    public void testSelectUnion() throws Exception {
        analyze("select name from sys.cluster union all select name from users");
        assertAskedForTable(DQL, "sys.cluster");
        assertAskedForTable(DQL, "doc.users");
    }

    /**
     * Union with order by (and/or limit) results
     * in a {@link io.crate.analyze.relations.OrderedLimitedRelation}
     * which wraps the {@link io.crate.analyze.relations.UnionSelect}
     */
    @Test
    public void testSelectUnionWithOrderBy() throws Exception {
        analyze("select name from sys.cluster union all select name from users order by 1");
        assertAskedForTable(DQL, "sys.cluster");
        assertAskedForTable(DQL, "doc.users");
    }

    @Test
    public void testSelectWithSubSelect() throws Exception {
        analyze(("select * from (" + (" select users.id from users join parted on users.id = parted.id::long order by users.name limit 2" + ") as users_parted order by users_parted.id")));
        assertAskedForTable(DQL, "doc.users");
        assertAskedForTable(DQL, "doc.parted");
    }

    @Test
    public void testCreateFunction() throws Exception {
        analyze(("create function bar()" + " returns long language dummy_lang AS 'function() { return 1; }'"));
        assertAskedForSchema(DDL, "doc");
    }

    @Test
    public void testDropFunction() throws Exception {
        analyze("drop function bar(long, object)");
        assertAskedForSchema(DDL, "doc");
    }

    @Test
    public void testDropTable() throws Exception {
        analyze("drop table users");
        assertAskedForTable(DDL, "doc.users");
    }

    @Test
    public void testDropBlobTable() throws Exception {
        analyze("drop blob table blobs");
        assertAskedForTable(DDL, "blob.blobs");
    }

    @Test
    public void testCreateAnalyzer() throws Exception {
        analyze("create analyzer a1 (tokenizer lowercase)");
        assertAskedForCluster(DDL);
    }

    @Test
    public void testRefresh() throws Exception {
        analyze("refresh table users, parted partition (date = 1395874800000)");
        assertAskedForTable(DQL, "doc.users");
        assertAskedForTable(DQL, "doc.parted");
    }

    @Test
    public void testRenameTable() throws Exception {
        analyze("alter table users rename to users_new");
        assertAskedForTable(DDL, "doc.users");
    }

    @Test
    public void testAlterBlobTable() throws Exception {
        analyze("alter blob table blobs set (number_of_replicas=1)");
        assertAskedForTable(DDL, "blob.blobs");
    }

    @Test
    public void testSetSessionRequiresNoPermissions() throws Exception {
        // allowed without any permissions as it affects only the user session;
        analyze("set session foo = 'bar'");
        assertThat(validationCallArguments.size(), Matchers.is(0));
    }

    @Test
    public void testAddColumn() throws Exception {
        analyze("alter table users add column foo string");
        assertAskedForTable(DDL, "doc.users");
    }

    @Test
    public void testOpenCloseTable() throws Exception {
        analyze("alter table users close");
        assertAskedForTable(DDL, "doc.users");
    }

    @Test
    public void testShowTable() throws Exception {
        analyze("show create table users");
        assertAskedForTable(DQL, "doc.users");
    }

    @Test
    public void testBeginRequiresNoPermission() throws Exception {
        // Begin is currently ignored; In other RDMS it's used to start transactions with contain
        // other statements; these other statements need to be validated
        analyze("begin");
        assertThat(validationCallArguments.size(), Matchers.is(0));
    }

    @Test
    public void testExplainSelect() throws Exception {
        analyze("explain select * from users");
        assertAskedForTable(DQL, "doc.users");
    }

    @Test
    public void testExplainCopyFrom() throws Exception {
        analyze("explain copy users from 'file:///tmp'");
        assertAskedForTable(DML, "doc.users");
    }

    @Test
    public void testUserWithDDLCanCreateViewOnTablesWhereDQLPermissionsAreAvailable() {
        analyze("create view xx.v1 as select * from doc.users");
        assertAskedForSchema(DDL, "xx");
        assertAskedForTable(DQL, "doc.users");
    }

    @Test
    public void testQueryOnViewRequiresOwnerToHavePrivilegesOnInvolvedRelations() {
        analyze("select * from doc.v1");
        assertAskedForView(DQL, "doc.v1");
        assertAskedForTable(DQL, "doc.users", superUser);
    }

    @Test
    public void testDroppingAViewRequiresDDLPermissionOnView() {
        analyze("drop view doc.v1");
        assertAskedForView(DDL, "doc.v1");
    }

    @Test
    public void testTableFunctionsDoNotRequireAnyPermissions() {
        analyze("select 1");
        assertThat(validationCallArguments.size(), Matchers.is(0));
    }

    @Test
    public void testPermissionCheckIsDoneOnSchemaAndTableNotOnTableAlias() {
        analyze("select * from doc.users as t");
        assertAskedForTable(DQL, "doc.users");
    }

    @Test
    public void testDecommissionRequiresSuperUserPrivileges() {
        expectedException.expectMessage(("User \"normal\" is not authorized to execute the statement. " + "Superuser permissions are required"));
        analyze("alter cluster decommission 'n1'");
    }
}

