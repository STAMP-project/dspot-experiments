/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.analyze;


import DataTypes.INTEGER;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.crate.analyze.relations.RelationAnalyzer;
import io.crate.auth.user.User;
import io.crate.exceptions.RelationUnknown;
import io.crate.exceptions.UnsupportedFeatureException;
import io.crate.metadata.RelationName;
import io.crate.metadata.doc.DocTableInfo;
import io.crate.metadata.doc.DocTableInfoFactory;
import io.crate.metadata.doc.TestingDocTableInfoFactory;
import io.crate.metadata.table.TestingTableInfo;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import io.crate.testing.SQLExecutor;
import org.elasticsearch.common.inject.Provider;
import org.elasticsearch.common.settings.Settings;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;


public class PrivilegesDCLAnalyzerTest extends CrateDummyClusterServiceUnitTest {
    private static final User GRANTOR_TEST_USER = User.of("test");

    private static final RelationName CUSTOM_SCHEMA_IDENT = new RelationName("my_schema", "locations");

    private static final DocTableInfo CUSTOM_SCHEMA_INFO = TestingTableInfo.builder(PrivilegesDCLAnalyzerTest.CUSTOM_SCHEMA_IDENT, TableDefinitions.SHARD_ROUTING).add("id", INTEGER, ImmutableList.<String>of()).build();

    private static final DocTableInfoFactory CUSTOM_SCHEMA_TABLE_FACTORY = new TestingDocTableInfoFactory(ImmutableMap.of(PrivilegesDCLAnalyzerTest.CUSTOM_SCHEMA_IDENT, PrivilegesDCLAnalyzerTest.CUSTOM_SCHEMA_INFO));

    private static final RelationName CUSTOM_SCHEMA_VIEW = new RelationName("my_schema", "locations_view");

    private SQLExecutor e;

    private Provider<RelationAnalyzer> analyzerProvider = () -> null;

    @Test
    public void testGrantPrivilegesToUsersOnCluster() {
        PrivilegesAnalyzedStatement analysis = analyzePrivilegesStatement("GRANT DQL, DML TO user1, user2");
        assertThat(analysis.userNames(), Matchers.contains("user1", "user2"));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(GRANT, DQL, CLUSTER, null), privilegeOf(GRANT, DML, CLUSTER, null)));
    }

    @Test
    public void testDenyPrivilegesToUsers() {
        PrivilegesAnalyzedStatement analysis = analyzePrivilegesStatement("DENY DQL, DML TO user1, user2");
        assertThat(analysis.userNames(), Matchers.contains("user1", "user2"));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(DENY, DQL, CLUSTER, null), privilegeOf(DENY, DML, CLUSTER, null)));
    }

    @Test
    public void testGrantPrivilegesToUsersOnTables() {
        PrivilegesAnalyzedStatement analysis = analyzePrivilegesStatement("GRANT DQL, DML on table t2, locations TO user1, user2");
        assertThat(analysis.userNames(), Matchers.contains("user1", "user2"));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(GRANT, DQL, TABLE, "doc.t2"), privilegeOf(GRANT, DML, TABLE, "doc.t2"), privilegeOf(GRANT, DQL, TABLE, "doc.locations"), privilegeOf(GRANT, DML, TABLE, "doc.locations")));
    }

    @Test
    public void testGrantPrivilegesToUsersOnViews() {
        PrivilegesAnalyzedStatement analysis = analyzePrivilegesStatement("GRANT DQL, DML on table t2, locations TO user1, user2");
        assertThat(analysis.userNames(), Matchers.contains("user1", "user2"));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(GRANT, DQL, TABLE, "doc.t2"), privilegeOf(GRANT, DML, TABLE, "doc.t2"), privilegeOf(GRANT, DQL, TABLE, "doc.locations"), privilegeOf(GRANT, DML, TABLE, "doc.locations")));
    }

    @Test
    public void testRevokePrivilegesFromUsersOnCluster() {
        PrivilegesAnalyzedStatement analysis = analyzePrivilegesStatement("REVOKE DQL, DML FROM user1, user2");
        assertThat(analysis.userNames(), Matchers.contains("user1", "user2"));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(REVOKE, DQL, CLUSTER, null), privilegeOf(REVOKE, DML, CLUSTER, null)));
    }

    @Test
    public void testRevokePrivilegesFromUsersOnSchemas() {
        PrivilegesAnalyzedStatement analysis = analyzePrivilegesStatement("REVOKE DQL, DML On schema doc, sys FROM user1, user2");
        assertThat(analysis.userNames(), Matchers.contains("user1", "user2"));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(REVOKE, DQL, SCHEMA, "doc"), privilegeOf(REVOKE, DML, SCHEMA, "doc"), privilegeOf(REVOKE, DQL, SCHEMA, "sys"), privilegeOf(REVOKE, DML, SCHEMA, "sys")));
    }

    @Test
    public void testRevokePrivilegesFromUsersOnTables() {
        PrivilegesAnalyzedStatement analysis = analyzePrivilegesStatement("REVOKE DQL, DML On table doc.t2, locations FROM user1, user2");
        assertThat(analysis.userNames(), Matchers.contains("user1", "user2"));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(REVOKE, DQL, TABLE, "doc.t2"), privilegeOf(REVOKE, DML, TABLE, "doc.t2"), privilegeOf(REVOKE, DQL, TABLE, "doc.locations"), privilegeOf(REVOKE, DML, TABLE, "doc.locations")));
    }

    @Test
    public void testRevokePrivilegesFromUsersOnViews() {
        PrivilegesAnalyzedStatement analysis = analyzePrivilegesStatement("REVOKE DQL, DML On view doc.t2, locations FROM user1, user2");
        assertThat(analysis.userNames(), Matchers.contains("user1", "user2"));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(REVOKE, DQL, VIEW, "doc.t2"), privilegeOf(REVOKE, DML, VIEW, "doc.t2"), privilegeOf(REVOKE, DQL, VIEW, "doc.locations"), privilegeOf(REVOKE, DML, VIEW, "doc.locations")));
    }

    @Test
    public void testGrantDenyRevokeAllPrivileges() {
        PrivilegesAnalyzedStatement analysis = analyzePrivilegesStatement("GRANT ALL PRIVILEGES TO user1");
        assertThat(analysis.privileges().size(), CoreMatchers.is(3));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(GRANT, DQL, CLUSTER, null), privilegeOf(GRANT, DML, CLUSTER, null), privilegeOf(GRANT, DDL, CLUSTER, null)));
        analysis = analyzePrivilegesStatement("DENY ALL PRIVILEGES TO user1");
        assertThat(analysis.privileges().size(), CoreMatchers.is(3));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(DENY, DQL, CLUSTER, null), privilegeOf(DENY, DML, CLUSTER, null), privilegeOf(DENY, DDL, CLUSTER, null)));
        analysis = analyzePrivilegesStatement("REVOKE ALL PRIVILEGES FROM user1");
        assertThat(analysis.privileges().size(), CoreMatchers.is(3));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(REVOKE, DQL, CLUSTER, null), privilegeOf(REVOKE, DML, CLUSTER, null), privilegeOf(REVOKE, DDL, CLUSTER, null)));
    }

    @Test
    public void testGrantRevokeAllPrivilegesOnSchema() {
        PrivilegesAnalyzedStatement analysis = analyzePrivilegesStatement("GRANT ALL PRIVILEGES ON Schema doc TO user1");
        assertThat(analysis.privileges().size(), CoreMatchers.is(3));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(GRANT, DQL, SCHEMA, "doc"), privilegeOf(GRANT, DML, SCHEMA, "doc"), privilegeOf(GRANT, DDL, SCHEMA, "doc")));
        analysis = analyzePrivilegesStatement("REVOKE ALL PRIVILEGES ON Schema doc FROM user1");
        assertThat(analysis.privileges().size(), CoreMatchers.is(3));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(REVOKE, DQL, SCHEMA, "doc"), privilegeOf(REVOKE, DML, SCHEMA, "doc"), privilegeOf(REVOKE, DDL, SCHEMA, "doc")));
    }

    @Test
    public void testGrantRevokeAllPrivilegesOnTable() {
        PrivilegesAnalyzedStatement analysis = analyzePrivilegesStatement("GRANT ALL PRIVILEGES On table my_schema.locations TO user1");
        assertThat(analysis.privileges().size(), CoreMatchers.is(3));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(GRANT, DQL, TABLE, "my_schema.locations"), privilegeOf(GRANT, DML, TABLE, "my_schema.locations"), privilegeOf(GRANT, DDL, TABLE, "my_schema.locations")));
        analysis = analyzePrivilegesStatement("REVOKE ALL PRIVILEGES On table my_schema.locations FROM user1");
        assertThat(analysis.privileges().size(), CoreMatchers.is(3));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(REVOKE, DQL, TABLE, "my_schema.locations"), privilegeOf(REVOKE, DML, TABLE, "my_schema.locations"), privilegeOf(REVOKE, DDL, TABLE, "my_schema.locations")));
    }

    @Test
    public void testGrantRevokeAllPrivilegesOnViews() {
        PrivilegesAnalyzedStatement analysis = analyzePrivilegesStatement("GRANT ALL PRIVILEGES On view my_schema.locations_view TO user1");
        assertThat(analysis.privileges().size(), CoreMatchers.is(3));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(GRANT, DQL, VIEW, "my_schema.locations_view"), privilegeOf(GRANT, DML, VIEW, "my_schema.locations_view"), privilegeOf(GRANT, DDL, VIEW, "my_schema.locations_view")));
        analysis = analyzePrivilegesStatement("REVOKE ALL PRIVILEGES On view my_schema.locations_view FROM user1");
        assertThat(analysis.privileges().size(), CoreMatchers.is(3));
        assertThat(analysis.privileges(), Matchers.containsInAnyOrder(privilegeOf(REVOKE, DQL, VIEW, "my_schema.locations_view"), privilegeOf(REVOKE, DML, VIEW, "my_schema.locations_view"), privilegeOf(REVOKE, DDL, VIEW, "my_schema.locations_view")));
    }

    @Test
    public void testGrantWithoutUserManagementEnabledThrowsException() {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("User management is not enabled");
        e = SQLExecutor.builder(clusterService).settings(Settings.builder().put(ENTERPRISE_LICENSE_SETTING.getKey(), false).build()).build();
        e.analyze("GRANT DQL TO test");
    }

    @Test
    public void testGrantOnUnknownSchemaDoesntThrowsException() {
        analyzePrivilegesStatement("GRANT DQL on schema hoichi TO user1");
    }

    @Test
    public void testRevokeFromUnknownTableDoNotThrowException() {
        PrivilegesAnalyzedStatement analysis = analyzePrivilegesStatement("Revoke DQL on table doc.hoichi FROM user1");
        assertThat(analysis.privileges().size(), CoreMatchers.is(1));
        assertThat(analysis.privileges(), Matchers.contains(privilegeOf(REVOKE, DQL, TABLE, "doc.hoichi")));
    }

    @Test
    public void testGrantToUnknownTableThrowsException() {
        expectedException.expect(RelationUnknown.class);
        expectedException.expectMessage("Relation 'doc.hoichi' unknown");
        analyzePrivilegesStatement("Grant DQL on table doc.hoichi to user1");
    }

    @Test
    public void testGrantToUnknownViewThrowsException() {
        expectedException.expect(RelationUnknown.class);
        expectedException.expectMessage("Relation 'doc.hoichi' unknown");
        analyzePrivilegesStatement("Grant DQL on view doc.hoichi to user1");
    }

    @Test
    public void testGrantOnInformationSchemaTableThrowsException() {
        expectedException.expect(UnsupportedFeatureException.class);
        expectedException.expectMessage("GRANT/DENY/REVOKE Privileges on information_schema is not supported");
        analyzePrivilegesStatement("GRANT DQL ON TABLE information_schema.tables TO user1");
    }

    @Test
    public void testRevokeOnInformationSchemaTableDoNotThrowException() {
        analyzePrivilegesStatement("REVOKE DQL ON TABLE information_schema.tables FROM user1");
    }

    @Test
    public void testRevokeOnInformationSchemaViewDoNotThrowException() {
        analyzePrivilegesStatement("REVOKE DQL ON TABLE information_schema.views FROM user1");
    }

    @Test
    public void testDenyOnInformationSchemaTableThrowsException() {
        expectedException.expect(UnsupportedFeatureException.class);
        expectedException.expectMessage("GRANT/DENY/REVOKE Privileges on information_schema is not supported");
        analyzePrivilegesStatement("DENY DQL ON TABLE information_schema.tables TO user1");
    }

    @Test
    public void testDenyOnInformationSchemaViewThrowsException() {
        expectedException.expect(UnsupportedFeatureException.class);
        expectedException.expectMessage("GRANT/DENY/REVOKE Privileges on information_schema is not supported");
        analyzePrivilegesStatement("DENY DQL ON TABLE information_schema.views TO user1");
    }

    @Test
    public void testGrantOnInformationSchemaThrowsException() {
        expectedException.expect(UnsupportedFeatureException.class);
        expectedException.expectMessage("GRANT/DENY/REVOKE Privileges on information_schema is not supported");
        analyzePrivilegesStatement("GRANT DQL ON SCHEMA information_schema TO user1");
    }

    @Test
    public void testDenyOnInformationSchemaThrowsException() {
        expectedException.expect(UnsupportedFeatureException.class);
        expectedException.expectMessage("GRANT/DENY/REVOKE Privileges on information_schema is not supported");
        analyzePrivilegesStatement("DENY DQL ON SCHEMA information_schema TO user1");
    }
}

