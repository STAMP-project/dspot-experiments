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


import MetaData.Builder;
import UsersMetaData.TYPE;
import io.crate.metadata.UserDefinitions;
import io.crate.metadata.UsersMetaData;
import io.crate.metadata.UsersPrivilegesMetaData;
import io.crate.test.integration.CrateUnitTest;
import java.util.Collections;
import org.elasticsearch.cluster.metadata.MetaData;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;


public class TransportUserActionTest extends CrateUnitTest {
    @Test
    public void testCreateFirstUser() throws Exception {
        MetaData.Builder mdBuilder = new MetaData.Builder();
        TransportCreateUserAction.putUser(mdBuilder, "root", null);
        UsersMetaData metaData = ((UsersMetaData) (mdBuilder.getCustom(TYPE)));
        assertThat(metaData.userNames().size(), Is.is(1));
        assertThat(metaData.userNames().get(0), Is.is("root"));
    }

    @Test
    public void testEmptyPrivilegesAreCreatedForNewUsers() throws Exception {
        MetaData.Builder mdBuilder = new MetaData.Builder();
        TransportCreateUserAction.putUser(mdBuilder, "root", null);
        UsersPrivilegesMetaData metaData = ((UsersPrivilegesMetaData) (mdBuilder.getCustom(UsersPrivilegesMetaData.TYPE)));
        assertThat(metaData.getUserPrivileges("root"), Is.is(Collections.emptySet()));
    }

    @Test
    public void testCreateUserAlreadyExists() throws Exception {
        MetaData.Builder mdBuilder = new MetaData.Builder().putCustom(TYPE, new UsersMetaData(UserDefinitions.SINGLE_USER_ONLY));
        assertThat(TransportCreateUserAction.putUser(mdBuilder, "Arthur", null), Is.is(true));
    }

    @Test
    public void testCreateUser() throws Exception {
        MetaData.Builder mdBuilder = new MetaData.Builder().putCustom(TYPE, new UsersMetaData(UserDefinitions.SINGLE_USER_ONLY));
        TransportCreateUserAction.putUser(mdBuilder, "Trillian", null);
        UsersMetaData newMetaData = ((UsersMetaData) (mdBuilder.getCustom(TYPE)));
        assertThat(newMetaData.userNames(), Matchers.containsInAnyOrder("Trillian", "Arthur"));
    }

    @Test
    public void testDropUserNoUsersAtAll() throws Exception {
        assertThat(TransportDropUserAction.dropUser(MetaData.builder(), null, "root"), Is.is(false));
    }

    @Test
    public void testDropNonExistingUser() throws Exception {
        boolean res = TransportDropUserAction.dropUser(MetaData.builder(), new UsersMetaData(UserDefinitions.SINGLE_USER_ONLY), "trillian");
        assertThat(res, Is.is(false));
    }

    @Test
    public void testDropUser() throws Exception {
        UsersMetaData oldMetaData = new UsersMetaData(UserDefinitions.DUMMY_USERS);
        MetaData.Builder mdBuilder = MetaData.builder();
        boolean res = TransportDropUserAction.dropUser(mdBuilder, oldMetaData, "Arthur");
        assertThat(TransportUserActionTest.users(mdBuilder), Matchers.contains("Ford"));
        assertThat(res, Is.is(true));
    }
}

