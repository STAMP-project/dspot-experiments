/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.config;


import org.junit.Assert;
import org.junit.Test;


public class PluginRoleUsersStoreTest {
    private PluginRoleUsersStore pluginRoleUsersStore;

    @Test
    public void assignRole_ShouldAssignPluginRoleToAnUser() throws Exception {
        Assert.assertThat(pluginRoleUsersStore.pluginRoles(), hasSize(0));
        PluginRoleConfig pluginRoleConfig = new PluginRoleConfig("spacetiger", "ldap");
        pluginRoleUsersStore.assignRole("wing-commander", pluginRoleConfig);
        Assert.assertThat(pluginRoleUsersStore.pluginRoles(), hasSize(1));
        Assert.assertThat(pluginRoleUsersStore.usersInRole(pluginRoleConfig), hasItem(new RoleUser("wing-commander")));
    }

    @Test
    public void removePluginRole_ShouldRemovePluginRoleFromStore() throws Exception {
        PluginRoleConfig pluginRoleConfig = new PluginRoleConfig("spacetiger", "ldap");
        pluginRoleUsersStore.assignRole("wing-commander", pluginRoleConfig);
        Assert.assertThat(pluginRoleUsersStore.pluginRoles(), hasSize(1));
        Assert.assertThat(pluginRoleUsersStore.usersInRole(pluginRoleConfig), hasItem(new RoleUser("wing-commander")));
        pluginRoleUsersStore.remove(pluginRoleConfig);
        Assert.assertThat(pluginRoleUsersStore.pluginRoles(), hasSize(0));
    }

    @Test
    public void revokeAllRolesFor_ShouldRevokeAllRolesForAGivenUser() throws Exception {
        PluginRoleConfig pluginRoleSpaceTiger = new PluginRoleConfig("spacetiger", "ldap");
        PluginRoleConfig pluginRoleBlackBird = new PluginRoleConfig("blackbird", "ldap");
        pluginRoleUsersStore.assignRole("wing-commander", pluginRoleSpaceTiger);
        pluginRoleUsersStore.assignRole("wing-commander", pluginRoleBlackBird);
        pluginRoleUsersStore.assignRole("bob", pluginRoleBlackBird);
        Assert.assertThat(pluginRoleUsersStore.pluginRoles(), hasSize(2));
        Assert.assertThat(pluginRoleUsersStore.usersInRole(pluginRoleSpaceTiger), hasSize(1));
        Assert.assertThat(pluginRoleUsersStore.usersInRole(pluginRoleBlackBird), hasSize(2));
        Assert.assertThat(pluginRoleUsersStore.usersInRole(pluginRoleSpaceTiger), hasItem(new RoleUser("wing-commander")));
        Assert.assertThat(pluginRoleUsersStore.usersInRole(pluginRoleBlackBird), containsInAnyOrder(new RoleUser("wing-commander"), new RoleUser("bob")));
        pluginRoleUsersStore.revokeAllRolesFor("wing-commander");
        Assert.assertThat(pluginRoleUsersStore.pluginRoles(), hasSize(1));
        Assert.assertThat(pluginRoleUsersStore.usersInRole(pluginRoleSpaceTiger), hasSize(0));
        Assert.assertThat(pluginRoleUsersStore.usersInRole(pluginRoleBlackBird), hasSize(1));
    }
}

