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
package io.crate.auth.user;


import Privilege.Clazz;
import Privilege.Clazz.CLUSTER;
import Privilege.Clazz.SCHEMA;
import Privilege.Clazz.TABLE;
import Privilege.State;
import Privilege.Type;
import Privilege.Type.DDL;
import Privilege.Type.DQL;
import io.crate.analyze.user.Privilege;
import io.crate.test.integration.CrateUnitTest;
import java.util.Collection;
import java.util.Collections;
import org.elasticsearch.common.util.set.Sets;
import org.hamcrest.Matchers;
import org.junit.Test;


public class UserPrivilegesTest extends CrateUnitTest {
    private static final Collection<Privilege> PRIVILEGES_CLUSTER_DQL = Sets.newHashSet(new Privilege(State.GRANT, Type.DQL, Clazz.CLUSTER, null, "crate"));

    private static final Collection<Privilege> PRIVILEGES_SCHEMA_DQL = Sets.newHashSet(new Privilege(State.GRANT, Type.DQL, Clazz.SCHEMA, "doc", "crate"));

    private static final Collection<Privilege> PRIVILEGES_TABLE_DQL = Sets.newHashSet(new Privilege(State.GRANT, Type.DQL, Clazz.TABLE, "doc.t1", "crate"));

    private static final UserPrivileges USER_PRIVILEGES_CLUSTER = new UserPrivileges(UserPrivilegesTest.PRIVILEGES_CLUSTER_DQL);

    private static final UserPrivileges USER_PRIVILEGES_SCHEMA = new UserPrivileges(UserPrivilegesTest.PRIVILEGES_SCHEMA_DQL);

    private static final UserPrivileges USER_PRIVILEGES_TABLE = new UserPrivileges(UserPrivilegesTest.PRIVILEGES_TABLE_DQL);

    @Test
    public void testMatchPrivilegesEmpty() throws Exception {
        UserPrivileges userPrivileges = new UserPrivileges(Collections.emptyList());
        assertThat(userPrivileges.matchPrivilege(DDL, CLUSTER, null, "doc"), Matchers.is(false));
        assertThat(userPrivileges.matchPrivilege(DDL, SCHEMA, "doc", "doc"), Matchers.is(false));
        assertThat(userPrivileges.matchPrivilege(DDL, TABLE, "doc.t1", "doc"), Matchers.is(false));
        assertThat(userPrivileges.matchPrivilegeOfAnyType(CLUSTER, null), Matchers.is(false));
        assertThat(userPrivileges.matchPrivilegeOfAnyType(SCHEMA, "doc"), Matchers.is(false));
        assertThat(userPrivileges.matchPrivilegeOfAnyType(TABLE, "doc.t1"), Matchers.is(false));
    }

    @Test
    public void testMatchPrivilegeNoType() throws Exception {
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_CLUSTER.matchPrivilege(DDL, CLUSTER, null, "doc"), Matchers.is(false));
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_CLUSTER.matchPrivilege(DDL, SCHEMA, "doc", "doc"), Matchers.is(false));
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_CLUSTER.matchPrivilege(DDL, TABLE, "doc.t1", "doc"), Matchers.is(false));
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_CLUSTER.matchPrivilegeOfAnyType(CLUSTER, null), Matchers.is(true));
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_CLUSTER.matchPrivilegeOfAnyType(SCHEMA, "doc"), Matchers.is(true));
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_CLUSTER.matchPrivilegeOfAnyType(TABLE, "doc.t1"), Matchers.is(true));
    }

    @Test
    public void testMatchPrivilegeType() throws Exception {
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_CLUSTER.matchPrivilege(DQL, CLUSTER, null, "doc"), Matchers.is(true));
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_CLUSTER.matchPrivilegeOfAnyType(CLUSTER, null), Matchers.is(true));
    }

    @Test
    public void testMatchPrivilegeSchema() throws Exception {
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_CLUSTER.matchPrivilege(DQL, SCHEMA, "doc", "doc"), Matchers.is(true));
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_CLUSTER.matchPrivilegeOfAnyType(SCHEMA, "doc"), Matchers.is(true));
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_SCHEMA.matchPrivilege(DQL, SCHEMA, "doc", "doc"), Matchers.is(true));
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_SCHEMA.matchPrivilegeOfAnyType(SCHEMA, "doc"), Matchers.is(true));
    }

    @Test
    public void testMatchPrivilegeTable() throws Exception {
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_CLUSTER.matchPrivilege(DQL, TABLE, "doc.t1", "doc"), Matchers.is(true));
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_CLUSTER.matchPrivilegeOfAnyType(TABLE, "doc.t1"), Matchers.is(true));
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_SCHEMA.matchPrivilege(DQL, TABLE, "doc.t1", "doc"), Matchers.is(true));
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_SCHEMA.matchPrivilegeOfAnyType(TABLE, "doc.t1"), Matchers.is(true));
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_TABLE.matchPrivilege(DQL, TABLE, "doc.t1", "doc"), Matchers.is(true));
        assertThat(UserPrivilegesTest.USER_PRIVILEGES_TABLE.matchPrivilegeOfAnyType(TABLE, "doc.t1"), Matchers.is(true));
    }

    @Test
    public void testMatchPrivilegeDenyResultsInNoMatch() throws Exception {
        Collection<Privilege> privileges = Sets.newHashSet(new Privilege(State.DENY, Type.DQL, Clazz.CLUSTER, null, "crate"));
        UserPrivileges userPrivileges = new UserPrivileges(privileges);
        assertThat(userPrivileges.matchPrivilege(DQL, CLUSTER, null, "doc"), Matchers.is(false));
        assertThat(userPrivileges.matchPrivilege(DQL, SCHEMA, "doc", "doc"), Matchers.is(false));
        assertThat(userPrivileges.matchPrivilege(DQL, TABLE, "doc.t1", "doc"), Matchers.is(false));
        assertThat(userPrivileges.matchPrivilegeOfAnyType(CLUSTER, null), Matchers.is(false));
        assertThat(userPrivileges.matchPrivilegeOfAnyType(SCHEMA, "doc"), Matchers.is(false));
        assertThat(userPrivileges.matchPrivilegeOfAnyType(TABLE, "doc.t1"), Matchers.is(false));
    }

    @Test
    public void testMatchPrivilegeComplexSetIncludingDeny() throws Exception {
        Collection<Privilege> privileges = Sets.newHashSet(new Privilege(State.GRANT, Type.DQL, Clazz.CLUSTER, null, "crate"), new Privilege(State.DENY, Type.DQL, Clazz.SCHEMA, "doc", "crate"), new Privilege(State.GRANT, Type.DQL, Clazz.TABLE, "doc.t1", "crate"));
        UserPrivileges userPrivileges = new UserPrivileges(privileges);
        assertThat(userPrivileges.matchPrivilege(DQL, TABLE, "doc.t1", "doc"), Matchers.is(true));
        assertThat(userPrivileges.matchPrivilege(DQL, TABLE, "doc.t2", "doc"), Matchers.is(false));
        assertThat(userPrivileges.matchPrivilege(DQL, SCHEMA, "my_schema", "doc"), Matchers.is(true));
    }
}

