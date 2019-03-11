/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.platform.db.migration.version.v64;


import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.CoreDbTester;


public class SetOrganizationMembersIntoMembersGroupTest {
    private static final String MEMBERS_NAME = "Members";

    private static final String ORGANIZATION_1 = "ORGANIZATION_1";

    private static final String ORGANIZATION_2 = "ORGANIZATION_2";

    @Rule
    public CoreDbTester db = CoreDbTester.createForSchema(SetOrganizationMembersIntoMembersGroupTest.class, "initial.sql");

    private SetOrganizationMembersIntoMembersGroup underTest = new SetOrganizationMembersIntoMembersGroup(db.database());

    @Test
    public void set_users_into_group_members() throws Exception {
        long user1 = insertUser("user1", true);
        long user2 = insertUser("user2", true);
        long user3 = insertUser("user3", true);
        long group1 = insertGroup(SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_1, SetOrganizationMembersIntoMembersGroupTest.MEMBERS_NAME);
        long group2 = insertGroup(SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_2, SetOrganizationMembersIntoMembersGroupTest.MEMBERS_NAME);
        insertOrganizationMember(user1, SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_1);
        insertOrganizationMember(user2, SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_2);
        insertOrganizationMember(user3, SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_1);
        underTest.execute();
        checkUserGroups(user1, group1);
        checkUserGroups(user2, group2);
        checkUserGroups(user3, group1);
    }

    @Test
    public void set_users_into_group_members_when_some_users_already_belongs_to_group() throws Exception {
        long user1 = insertUser("user1", true);
        long user2 = insertUser("user2", true);
        long user3 = insertUser("user3", true);
        long group1 = insertGroup(SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_1, SetOrganizationMembersIntoMembersGroupTest.MEMBERS_NAME);
        long group2 = insertGroup(SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_2, SetOrganizationMembersIntoMembersGroupTest.MEMBERS_NAME);
        insertOrganizationMember(user1, SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_1);
        insertOrganizationMember(user2, SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_2);
        insertOrganizationMember(user3, SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_1);
        insertUserGroups(user1, group1);
        underTest.execute();
        checkUserGroups(user1, group1);
        checkUserGroups(user2, group2);
        checkUserGroups(user3, group1);
    }

    @Test
    public void does_nothing_if_members_group_does_not_exist() throws Exception {
        long user1 = insertUser("user1", true);
        insertGroup(SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_1, "other");
        insertGroup(SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_2, "other");
        insertOrganizationMember(user1, SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_1);
        underTest.execute();
        checkUserGroups(user1);
    }

    @Test
    public void does_not_fail_when_users_already_belongs_to_group_members() throws Exception {
        long user1 = insertUser("user1", true);
        long user2 = insertUser("user2", true);
        long group1 = insertGroup(SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_1, SetOrganizationMembersIntoMembersGroupTest.MEMBERS_NAME);
        long group2 = insertGroup(SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_2, SetOrganizationMembersIntoMembersGroupTest.MEMBERS_NAME);
        insertOrganizationMember(user1, SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_1);
        insertOrganizationMember(user2, SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_2);
        insertUserGroups(user1, group1);
        insertUserGroups(user2, group2);
        underTest.execute();
        checkUserGroups(user1, group1);
        checkUserGroups(user2, group2);
    }

    @Test
    public void ignore_disabled_users() throws Exception {
        long user = insertUser("user", false);
        insertGroup(SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_1, SetOrganizationMembersIntoMembersGroupTest.MEMBERS_NAME);
        insertOrganizationMember(user, SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_1);
        underTest.execute();
        checkUserGroups(user);
    }

    @Test
    public void migration_is_renentrant() throws Exception {
        long user = insertUser("user1", true);
        long group = insertGroup(SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_1, SetOrganizationMembersIntoMembersGroupTest.MEMBERS_NAME);
        insertOrganizationMember(user, SetOrganizationMembersIntoMembersGroupTest.ORGANIZATION_1);
        underTest.execute();
        checkUserGroups(user, group);
        underTest.execute();
        checkUserGroups(user, group);
    }
}

