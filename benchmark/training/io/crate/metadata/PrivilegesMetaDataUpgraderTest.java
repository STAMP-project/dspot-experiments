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


import MetaData.Custom;
import Privilege.Clazz;
import Privilege.State;
import Privilege.Type;
import Settings.EMPTY;
import UsersMetaData.TYPE;
import com.google.common.collect.ImmutableMap;
import io.crate.analyze.user.Privilege;
import io.crate.test.integration.CrateUnitTest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.collect.MapBuilder;
import org.elasticsearch.common.util.set.Sets;
import org.hamcrest.Matchers;
import org.junit.Test;


public class PrivilegesMetaDataUpgraderTest extends CrateUnitTest {
    private static final PrivilegesMetaDataUpgrader UPGRADER = new PrivilegesMetaDataUpgrader();

    @Test
    public void testNoUsersNothingChanged() throws Exception {
        Map<String, MetaData.Custom> customMap = new HashMap<>(1);
        customMap.put(TYPE, new UsersMetaData(ImmutableMap.of()));
        Map<String, MetaData.Custom> oldCustomMap = new HashMap(customMap);
        Map<String, MetaData.Custom> newCustomMap = PrivilegesMetaDataUpgraderTest.UPGRADER.apply(EMPTY, customMap);
        assertThat(newCustomMap, Matchers.is(oldCustomMap));
    }

    @Test
    public void testExistingUserWithoutAnyPrivilegeGetsAllPrivileges() throws Exception {
        Map<String, MetaData.Custom> customMap = new HashMap<>(1);
        customMap.put(TYPE, new UsersMetaData(UserDefinitions.SINGLE_USER_ONLY));
        Map<String, MetaData.Custom> oldCustomMap = new HashMap(customMap);
        Map<String, MetaData.Custom> newCustomMap = PrivilegesMetaDataUpgraderTest.UPGRADER.apply(EMPTY, customMap);
        assertThat(newCustomMap, Matchers.not(Matchers.is(oldCustomMap)));
        UsersPrivilegesMetaData privilegesMetaData = ((UsersPrivilegesMetaData) (newCustomMap.get(UsersPrivilegesMetaData.TYPE)));
        assertThat(privilegesMetaData, Matchers.notNullValue());
        Set<Privilege> userPrivileges = privilegesMetaData.getUserPrivileges("Arthur");
        assertThat(userPrivileges, Matchers.notNullValue());
        Set<Privilege.Type> privilegeTypes = userPrivileges.stream().map(( p) -> p.ident().type()).collect(Collectors.toSet());
        assertThat(privilegeTypes, Matchers.containsInAnyOrder(Type.values()));
    }

    @Test
    public void testExistingUserWithPrivilegesDoesntGetMore() throws Exception {
        Map<String, MetaData.Custom> customMap = new HashMap<>(1);
        customMap.put(TYPE, new UsersMetaData(UserDefinitions.SINGLE_USER_ONLY));
        customMap.put(UsersPrivilegesMetaData.TYPE, new UsersPrivilegesMetaData(MapBuilder.<String, Set<Privilege>>newMapBuilder().put("Arthur", Sets.newHashSet(new Privilege(State.GRANT, Type.DQL, Clazz.CLUSTER, null, "crate"))).map()));
        Map<String, MetaData.Custom> oldCustomMap = new HashMap(customMap);
        Map<String, MetaData.Custom> newCustomMap = PrivilegesMetaDataUpgraderTest.UPGRADER.apply(EMPTY, customMap);
        assertThat(newCustomMap, Matchers.is(oldCustomMap));
    }

    @Test
    public void testExistingUserWithEmptyPrivilegesDoesntGetMore() throws Exception {
        Map<String, MetaData.Custom> customMap = new HashMap<>(1);
        customMap.put(TYPE, new UsersMetaData(UserDefinitions.SINGLE_USER_ONLY));
        customMap.put(UsersPrivilegesMetaData.TYPE, new UsersPrivilegesMetaData(MapBuilder.<String, Set<Privilege>>newMapBuilder().put("Arthur", Collections.emptySet()).map()));
        Map<String, MetaData.Custom> oldCustomMap = new HashMap(customMap);
        Map<String, MetaData.Custom> newCustomMap = PrivilegesMetaDataUpgraderTest.UPGRADER.apply(EMPTY, customMap);
        assertThat(newCustomMap, Matchers.is(oldCustomMap));
    }
}

