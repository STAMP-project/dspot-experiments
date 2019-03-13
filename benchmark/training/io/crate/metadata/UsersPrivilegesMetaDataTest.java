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
package io.crate.metadata;


import DeprecationHandler.THROW_UNSUPPORTED_OPERATION;
import JsonXContent.jsonXContent;
import Privilege.Clazz;
import Privilege.State;
import Privilege.Type;
import ToXContent.EMPTY_PARAMS;
import io.crate.analyze.user.Privilege;
import io.crate.test.integration.CrateUnitTest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class UsersPrivilegesMetaDataTest extends CrateUnitTest {
    private static final Privilege GRANT_DQL = new Privilege(State.GRANT, Type.DQL, Clazz.CLUSTER, null, "crate");

    private static final Privilege GRANT_DML = new Privilege(State.GRANT, Type.DML, Clazz.CLUSTER, null, "crate");

    private static final Privilege REVOKE_DQL = new Privilege(State.REVOKE, Type.DQL, Clazz.CLUSTER, null, "crate");

    private static final Privilege REVOKE_DML = new Privilege(State.REVOKE, Type.DML, Clazz.CLUSTER, null, "crate");

    private static final Privilege DENY_DQL = new Privilege(State.DENY, Type.DQL, Clazz.CLUSTER, null, "crate");

    private static final Privilege GRANT_TABLE_DQL = new Privilege(State.GRANT, Type.DQL, Clazz.TABLE, "testSchema.test", "crate");

    private static final Privilege GRANT_TABLE_DDL = new Privilege(State.GRANT, Type.DDL, Clazz.TABLE, "testSchema.test2", "crate");

    private static final Privilege GRANT_VIEW_DQL = new Privilege(State.GRANT, Type.DQL, Clazz.VIEW, "testSchema.view1", "crate");

    private static final Privilege GRANT_VIEW_DDL = new Privilege(State.GRANT, Type.DDL, Clazz.VIEW, "testSchema.view2", "crate");

    private static final Privilege GRANT_VIEW_DML = new Privilege(State.GRANT, Type.DML, Clazz.VIEW, "view3", "crate");

    private static final Privilege GRANT_SCHEMA_DML = new Privilege(State.GRANT, Type.DML, Clazz.SCHEMA, "testSchema", "crate");

    private static final Set<Privilege> PRIVILEGES = new java.util.HashSet(Arrays.asList(UsersPrivilegesMetaDataTest.GRANT_DQL, UsersPrivilegesMetaDataTest.GRANT_DML));

    private static final List<String> USERNAMES = Arrays.asList("Ford", "Arthur");

    private static final String USER_WITHOUT_PRIVILEGES = "noPrivilegesUser";

    private static final String USER_WITH_DENIED_DQL = "userWithDeniedDQL";

    private static final String USER_WITH_SCHEMA_AND_TABLE_PRIVS = "userWithTableAndSchemaPrivs";

    private UsersPrivilegesMetaData usersPrivilegesMetaData;

    @Test
    public void testStreaming() throws IOException {
        BytesStreamOutput out = new BytesStreamOutput();
        usersPrivilegesMetaData.writeTo(out);
        StreamInput in = out.bytes().streamInput();
        UsersPrivilegesMetaData usersPrivilegesMetaData2 = new UsersPrivilegesMetaData(in);
        assertEquals(usersPrivilegesMetaData, usersPrivilegesMetaData2);
    }

    @Test
    public void testXContent() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        // reflects the logic used to process custom metadata in the cluster state
        builder.startObject();
        usersPrivilegesMetaData.toXContent(builder, EMPTY_PARAMS);
        builder.endObject();
        XContentParser parser = jsonXContent.createParser(xContentRegistry(), THROW_UNSUPPORTED_OPERATION, Strings.toString(builder));
        parser.nextToken();// start object

        UsersPrivilegesMetaData usersPrivilegesMetaData2 = UsersPrivilegesMetaData.fromXContent(parser);
        assertEquals(usersPrivilegesMetaData, usersPrivilegesMetaData2);
        // a metadata custom must consume its surrounded END_OBJECT token, no token must be left
        assertThat(parser.nextToken(), Matchers.nullValue());
    }

    @Test
    public void testApplyPrivilegesSameExists() throws Exception {
        long rowCount = usersPrivilegesMetaData.applyPrivileges(UsersPrivilegesMetaDataTest.USERNAMES, new java.util.HashSet(UsersPrivilegesMetaDataTest.PRIVILEGES));
        assertThat(rowCount, Is.is(0L));
    }

    @Test
    public void testRevokeWithoutGrant() throws Exception {
        long rowCount = usersPrivilegesMetaData.applyPrivileges(Collections.singletonList(UsersPrivilegesMetaDataTest.USER_WITHOUT_PRIVILEGES), Collections.singletonList(UsersPrivilegesMetaDataTest.REVOKE_DML));
        assertThat(rowCount, Is.is(0L));
        assertThat(usersPrivilegesMetaData.getUserPrivileges(UsersPrivilegesMetaDataTest.USER_WITHOUT_PRIVILEGES), Matchers.empty());
    }

    @Test
    public void testRevokeWithGrant() throws Exception {
        long rowCount = usersPrivilegesMetaData.applyPrivileges(Collections.singletonList("Arthur"), Collections.singletonList(UsersPrivilegesMetaDataTest.REVOKE_DML));
        assertThat(rowCount, Is.is(1L));
        assertThat(usersPrivilegesMetaData.getUserPrivileges("Arthur"), Matchers.contains(UsersPrivilegesMetaDataTest.GRANT_DQL));
    }

    @Test
    public void testRevokeWithGrantOfDifferentGrantor() throws Exception {
        long rowCount = usersPrivilegesMetaData.applyPrivileges(Collections.singletonList("Arthur"), Collections.singletonList(new Privilege(State.REVOKE, Type.DML, Clazz.CLUSTER, null, "hoschi")));
        assertThat(rowCount, Is.is(1L));
        assertThat(usersPrivilegesMetaData.getUserPrivileges("Arthur"), Matchers.contains(UsersPrivilegesMetaDataTest.GRANT_DQL));
    }

    @Test
    public void testDenyGrantedPrivilegeForUsers() throws Exception {
        long rowCount = usersPrivilegesMetaData.applyPrivileges(UsersPrivilegesMetaDataTest.USERNAMES, Collections.singletonList(UsersPrivilegesMetaDataTest.DENY_DQL));
        assertThat(rowCount, Is.is(2L));
    }

    @Test
    public void testDenyUngrantedPrivilegeStoresTheDeny() throws Exception {
        long rowCount = usersPrivilegesMetaData.applyPrivileges(Collections.singletonList(UsersPrivilegesMetaDataTest.USER_WITHOUT_PRIVILEGES), Collections.singletonList(UsersPrivilegesMetaDataTest.DENY_DQL));
        assertThat(rowCount, Is.is(1L));
        assertThat(usersPrivilegesMetaData.getUserPrivileges(UsersPrivilegesMetaDataTest.USER_WITHOUT_PRIVILEGES), Matchers.contains(UsersPrivilegesMetaDataTest.DENY_DQL));
    }

    @Test
    public void testRevokeDenyPrivilegeRemovesIt() throws Exception {
        long rowCount = usersPrivilegesMetaData.applyPrivileges(Collections.singletonList(UsersPrivilegesMetaDataTest.USER_WITH_DENIED_DQL), Collections.singletonList(UsersPrivilegesMetaDataTest.REVOKE_DQL));
        assertThat(rowCount, Is.is(1L));
        assertThat(usersPrivilegesMetaData.getUserPrivileges(UsersPrivilegesMetaDataTest.USER_WITH_DENIED_DQL), Matchers.empty());
    }

    @Test
    public void testDenyExistingDeniedPrivilegeIsNoOp() {
        long rowCount = usersPrivilegesMetaData.applyPrivileges(Collections.singletonList(UsersPrivilegesMetaDataTest.USER_WITH_DENIED_DQL), new java.util.HashSet(Collections.singletonList(UsersPrivilegesMetaDataTest.DENY_DQL)));
        assertThat(rowCount, Is.is(0L));
        assertThat(usersPrivilegesMetaData.getUserPrivileges(UsersPrivilegesMetaDataTest.USER_WITH_DENIED_DQL), Matchers.contains(UsersPrivilegesMetaDataTest.DENY_DQL));
    }

    @Test
    public void testTablePrivilegesAreTransferred() throws Exception {
        UsersPrivilegesMetaData usersMetaData = UsersPrivilegesMetaData.maybeCopyAndReplaceTableIdents(usersPrivilegesMetaData, UsersPrivilegesMetaDataTest.GRANT_TABLE_DQL.ident().ident(), "testSchema.testing");
        assertThat(usersMetaData, Matchers.notNullValue());
        Set<Privilege> updatedPrivileges = usersMetaData.getUserPrivileges(UsersPrivilegesMetaDataTest.USER_WITH_SCHEMA_AND_TABLE_PRIVS);
        Optional<Privilege> targetPrivilege = updatedPrivileges.stream().filter(( p) -> p.ident().ident().equals("testSchema.testing")).findAny();
        assertThat(targetPrivilege.isPresent(), Is.is(true));
        Optional<Privilege> sourcePrivilege = updatedPrivileges.stream().filter(( p) -> p.ident().ident().equals("testSchema.test")).findAny();
        assertThat(sourcePrivilege.isPresent(), Is.is(false));
        // unrelated table privileges must be still available
        Optional<Privilege> otherTablePrivilege = updatedPrivileges.stream().filter(( p) -> p.ident().ident().equals("testSchema.test2")).findAny();
        assertThat(otherTablePrivilege.isPresent(), Is.is(true));
        Optional<Privilege> schemaPrivilege = updatedPrivileges.stream().filter(( p) -> p.ident().clazz().equals(Privilege.Clazz.SCHEMA)).findAny();
        assertThat(((schemaPrivilege.isPresent()) && (schemaPrivilege.get().equals(UsersPrivilegesMetaDataTest.GRANT_SCHEMA_DML))), Is.is(true));
    }

    @Test
    public void testDropTablePrivileges() {
        long affectedPrivileges = usersPrivilegesMetaData.dropTableOrViewPrivileges(UsersPrivilegesMetaDataTest.GRANT_TABLE_DQL.ident().ident());
        assertThat(affectedPrivileges, Is.is(1L));
        Set<Privilege> updatedPrivileges = usersPrivilegesMetaData.getUserPrivileges(UsersPrivilegesMetaDataTest.USER_WITH_SCHEMA_AND_TABLE_PRIVS);
        Optional<Privilege> sourcePrivilege = updatedPrivileges.stream().filter(( p) -> p.ident().ident().equals("testSchema.test")).findAny();
        assertThat(sourcePrivilege.isPresent(), Is.is(false));
    }

    @Test
    public void testDropViewPrivileges() {
        long affectedPrivileges = usersPrivilegesMetaData.dropTableOrViewPrivileges(UsersPrivilegesMetaDataTest.GRANT_VIEW_DQL.ident().ident());
        assertThat(affectedPrivileges, Is.is(1L));
        Set<Privilege> updatedPrivileges = usersPrivilegesMetaData.getUserPrivileges(UsersPrivilegesMetaDataTest.USER_WITH_SCHEMA_AND_TABLE_PRIVS);
        Optional<Privilege> sourcePrivilege = updatedPrivileges.stream().filter(( p) -> p.ident().ident().equals("testSchema.view1")).findAny();
        assertThat(sourcePrivilege.isPresent(), Is.is(false));
    }
}

