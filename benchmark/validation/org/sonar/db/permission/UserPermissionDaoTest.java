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
package org.sonar.db.permission;


import System2.INSTANCE;
import UserRole.CODEVIEWER;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.UserDto;


public class UserPermissionDaoTest {
    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private DbSession dbSession = db.getSession();

    private UserPermissionDao underTest = new UserPermissionDao();

    @Test
    public void select_global_permissions() {
        OrganizationDto organization = db.organizations().insert();
        OrganizationDto org2 = db.organizations().insert();
        UserDto user1 = insertUser(( u) -> u.setLogin("login1").setName("Marius").setEmail("email1@email.com"), organization, org2);
        UserDto user2 = insertUser(( u) -> u.setLogin("login2").setName("Marie").setEmail("email2@email.com"), organization, org2);
        UserDto user3 = insertUser(( u) -> u.setLogin("zanother").setName("Zoe").setEmail("zanother3@another.com"), organization);
        ComponentDto project = db.components().insertPrivateProject(organization);
        UserPermissionDto global1 = addGlobalPermission(organization, SYSTEM_ADMIN, user1);
        UserPermissionDto global2 = addGlobalPermission(organization, SYSTEM_ADMIN, user2);
        UserPermissionDto global3 = addGlobalPermission(organization, PROVISIONING, user2);
        UserPermissionDto project1Perm = addProjectPermission(organization, USER, user3, project);
        // permissions on another organization, to be excluded
        UserPermissionDto org2Global1 = addGlobalPermission(org2, SYSTEM_ADMIN, user1);
        UserPermissionDto org2Global2 = addGlobalPermission(org2, PROVISIONING, user2);
        // global permissions of users who has at least one global permission, ordered by user name then permission
        PermissionQuery query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).withAtLeastOnePermission().build();
        expectPermissions(query, Arrays.asList(user2.getId(), user1.getId()), global2, global3, global1);
        // default query returns all users, whatever their permissions nor organizations
        // (that's a non-sense, but still this is required for api/permissions/groups
        // when filtering users by name)
        query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).build();
        expectPermissions(query, Arrays.asList(user2.getId(), user1.getId(), user3.getId()), global2, global3, org2Global2, global1, org2Global1, project1Perm);
        // global permissions "admin"
        query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).setPermission(SYSTEM_ADMIN).build();
        expectPermissions(query, Arrays.asList(user2.getId(), user1.getId()), global2, global1);
        // empty if nobody has the specified global permission
        query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).setPermission("missing").build();
        expectPermissions(query, Collections.emptyList());
        // search by user name (matches 2 users)
        query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).withAtLeastOnePermission().setSearchQuery("mari").build();
        expectPermissions(query, Arrays.asList(user2.getId(), user1.getId()), global2, global3, global1);
        // search by user login
        query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).withAtLeastOnePermission().setSearchQuery("ogin2").build();
        expectPermissions(query, Collections.singletonList(user2.getId()), global2, global3);
        // search by user email
        query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).withAtLeastOnePermission().setSearchQuery("mail2").build();
        expectPermissions(query, Collections.singletonList(user2.getId()), global2, global3);
        // search by user name (matches 2 users) and global permission
        query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).setSearchQuery("Mari").setPermission(PROVISIONING).build();
        expectPermissions(query, Collections.singletonList(user2.getId()), global3);
        // search by user name (no match)
        query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).setSearchQuery("Unknown").build();
        expectPermissions(query, Collections.emptyList());
    }

    @Test
    public void select_project_permissions() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user1 = insertUser(( u) -> u.setLogin("login1").setName("Marius").setEmail("email1@email.com"), organization);
        UserDto user2 = insertUser(( u) -> u.setLogin("login2").setName("Marie").setEmail("email2@email.com"), organization);
        UserDto user3 = insertUser(( u) -> u.setLogin("zanother").setName("Zoe").setEmail("zanother3@another.com"), organization);
        addGlobalPermission(organization, SYSTEM_ADMIN, user1);
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        ComponentDto project2 = db.components().insertPrivateProject(organization);
        UserPermissionDto perm1 = addProjectPermission(organization, USER, user1, project1);
        UserPermissionDto perm2 = addProjectPermission(organization, ISSUE_ADMIN, user1, project1);
        UserPermissionDto perm3 = addProjectPermission(organization, ISSUE_ADMIN, user2, project1);
        addProjectPermission(organization, ISSUE_ADMIN, user3, project2);
        // project permissions of users who has at least one permission on this project
        PermissionQuery query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).withAtLeastOnePermission().setComponentUuid(project1.uuid()).build();
        expectPermissions(query, Arrays.asList(user2.getId(), user1.getId()), perm3, perm2, perm1);
        // empty if nobody has the specified global permission
        query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).setPermission("missing").setComponentUuid(project1.uuid()).build();
        expectPermissions(query, Collections.emptyList());
        // search by user name (matches 2 users), users with at least one permission
        query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).setSearchQuery("Mari").withAtLeastOnePermission().setComponentUuid(project1.uuid()).build();
        expectPermissions(query, Arrays.asList(user2.getId(), user1.getId()), perm3, perm2, perm1);
        // search by user name (matches 2 users) and project permission
        query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).setSearchQuery("Mari").setPermission(ISSUE_ADMIN).setComponentUuid(project1.uuid()).build();
        expectPermissions(query, Arrays.asList(user2.getId(), user1.getId()), perm3, perm2);
        // search by user name (no match)
        query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).setSearchQuery("Unknown").setComponentUuid(project1.uuid()).build();
        expectPermissions(query, Collections.emptyList());
        // permissions of unknown project
        query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).setComponentUuid("missing").withAtLeastOnePermission().build();
        expectPermissions(query, Collections.emptyList());
    }

    @Test
    public void selectUserIdsByQuery_is_ordering_by_users_having_permissions_first_then_by_name_lowercase() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user1 = insertUser(( u) -> u.setLogin("login1").setName("Z").setEmail("email1@email.com"), organization);
        UserDto user2 = insertUser(( u) -> u.setLogin("login2").setName("A").setEmail("email2@email.com"), organization);
        UserDto user3 = insertUser(( u) -> u.setLogin("login3").setName("Z").setEmail("zanother3@another.com"), organization);
        UserDto user4 = insertUser(( u) -> u.setLogin("login4").setName("A").setEmail("zanother3@another.com"), organization);
        addGlobalPermission(organization, SYSTEM_ADMIN, user1);
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        addProjectPermission(organization, USER, user2, project1);
        PermissionQuery query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).build();
        assertThat(underTest.selectUserIdsByQuery(dbSession, query)).containsExactly(user2.getId(), user1.getId(), user4.getId(), user3.getId());
    }

    @Test
    public void selectUserIdsByQuery_is_not_ordering_by_number_of_permissions() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user1 = insertUser(( u) -> u.setLogin("login1").setName("Z").setEmail("email1@email.com"), organization);
        UserDto user2 = insertUser(( u) -> u.setLogin("login2").setName("A").setEmail("email2@email.com"), organization);
        addGlobalPermission(organization, SYSTEM_ADMIN, user1);
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        addProjectPermission(organization, USER, user2, project1);
        addProjectPermission(organization, USER, user1, project1);
        addProjectPermission(organization, ADMIN, user1, project1);
        PermissionQuery query = PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).build();
        // Even if user1 has 3 permissions, the name is used to order
        assertThat(underTest.selectUserIdsByQuery(dbSession, query)).containsExactly(user2.getId(), user1.getId());
    }

    @Test
    public void countUsersByProjectPermission() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user1 = insertUser(organization);
        UserDto user2 = insertUser(organization);
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        ComponentDto project2 = db.components().insertPrivateProject(organization);
        addGlobalPermission(organization, SYSTEM_ADMIN, user1);
        addProjectPermission(organization, USER, user1, project1);
        addProjectPermission(organization, ISSUE_ADMIN, user1, project1);
        addProjectPermission(organization, ISSUE_ADMIN, user2, project1);
        addProjectPermission(organization, ISSUE_ADMIN, user2, project2);
        // no projects -> return empty list
        assertThat(underTest.countUsersByProjectPermission(dbSession, Collections.emptyList())).isEmpty();
        // one project
        expectCount(Collections.singletonList(project1.getId()), new CountPerProjectPermission(project1.getId(), USER, 1), new CountPerProjectPermission(project1.getId(), ISSUE_ADMIN, 2));
        // multiple projects
        expectCount(Arrays.asList(project1.getId(), project2.getId(), (-1L)), new CountPerProjectPermission(project1.getId(), USER, 1), new CountPerProjectPermission(project1.getId(), ISSUE_ADMIN, 2), new CountPerProjectPermission(project2.getId(), ISSUE_ADMIN, 1));
    }

    @Test
    public void selectUserIdsByQuery() {
        OrganizationDto org1 = db.organizations().insert();
        OrganizationDto org2 = db.organizations().insert();
        UserDto user1 = insertUser(( u) -> u.setLogin("login1").setName("Marius").setEmail("email1@email.com"), org1, org2);
        UserDto user2 = insertUser(( u) -> u.setLogin("login2").setName("Marie").setEmail("email2@email.com"), org1, org2);
        ComponentDto project1 = db.components().insertPrivateProject(org1);
        ComponentDto project2 = db.components().insertPrivateProject(org2);
        addProjectPermission(org1, USER, user1, project1);
        addProjectPermission(org1, USER, user2, project1);
        addProjectPermission(org2, USER, user1, project2);
        addProjectPermission(org1, ISSUE_ADMIN, user2, project1);
        addProjectPermission(org2, ISSUE_ADMIN, user2, project2);
        // logins are ordered by user name: user2 ("Marie") then user1 ("Marius")
        PermissionQuery query = PermissionQuery.builder().setOrganizationUuid(project1.getOrganizationUuid()).setComponentUuid(project1.uuid()).withAtLeastOnePermission().build();
        assertThat(underTest.selectUserIdsByQuery(dbSession, query)).containsExactly(user2.getId(), user1.getId());
        query = PermissionQuery.builder().setOrganizationUuid("anotherOrg").setComponentUuid(project1.uuid()).withAtLeastOnePermission().build();
        assertThat(underTest.selectUserIdsByQuery(dbSession, query)).isEmpty();
        // on a project without permissions
        query = PermissionQuery.builder().setOrganizationUuid(org1.getUuid()).setComponentUuid("missing").withAtLeastOnePermission().build();
        assertThat(underTest.selectUserIdsByQuery(dbSession, query)).isEmpty();
        // search all users whose name matches "mar", whatever the permissions
        query = PermissionQuery.builder().setOrganizationUuid(org1.getUuid()).setSearchQuery("mar").build();
        assertThat(underTest.selectUserIdsByQuery(dbSession, query)).containsExactly(user2.getId(), user1.getId());
        // search all users whose name matches "mariu", whatever the permissions
        query = PermissionQuery.builder().setOrganizationUuid(org1.getUuid()).setSearchQuery("mariu").build();
        assertThat(underTest.selectUserIdsByQuery(dbSession, query)).containsExactly(user1.getId());
        // search all users whose name matches "mariu", whatever the permissions
        query = PermissionQuery.builder().setOrganizationUuid(org1.getUuid()).setSearchQuery("mariu").setComponentUuid(project1.uuid()).build();
        assertThat(underTest.selectUserIdsByQuery(dbSession, query)).containsExactly(user1.getId());
        // search all users whose name matches "mariu", whatever the organization
        query = PermissionQuery.builder().setOrganizationUuid("missingOrg").setSearchQuery("mariu").build();
        assertThat(underTest.selectUserIdsByQuery(dbSession, query)).isEmpty();
    }

    @Test
    public void selectUserIdsByQuery_is_paginated() {
        OrganizationDto organization = db.organizations().insert();
        List<Integer> userIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String name = "user-" + i;
            UserDto user = insertUser(( u) -> u.setName(name), organization);
            addGlobalPermission(organization, PROVISIONING, user);
            addGlobalPermission(organization, SYSTEM_ADMIN, user);
            userIds.add(user.getId());
        }
        assertThat(underTest.selectUserIdsByQuery(dbSession, PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).setPageSize(3).setPageIndex(1).build())).containsExactly(userIds.get(0), userIds.get(1), userIds.get(2));
        assertThat(underTest.selectUserIdsByQuery(dbSession, PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).setPageSize(2).setPageIndex(3).build())).containsExactly(userIds.get(4), userIds.get(5));
        assertThat(underTest.selectUserIdsByQuery(dbSession, PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).setPageSize(50).setPageIndex(1).build())).hasSize(10);
    }

    @Test
    public void selectUserIdsByQuery_is_sorted_by_insensitive_name() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user1 = insertUser(( u) -> u.setName("user1"), organization);
        addGlobalPermission(organization, PROVISIONING, user1);
        UserDto user3 = insertUser(( u) -> u.setName("user3"), organization);
        addGlobalPermission(organization, SYSTEM_ADMIN, user3);
        UserDto user2 = insertUser(( u) -> u.setName("User2"), organization);
        addGlobalPermission(organization, PROVISIONING, user2);
        assertThat(underTest.selectUserIdsByQuery(dbSession, PermissionQuery.builder().setOrganizationUuid(organization.getUuid()).build())).containsExactly(user1.getId(), user2.getId(), user3.getId());
    }

    @Test
    public void deleteGlobalPermission() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user1 = insertUser(organization);
        UserDto user2 = insertUser(organization);
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        ComponentDto project2 = db.components().insertPrivateProject(organization);
        addGlobalPermission(organization, "perm1", user1);
        addGlobalPermission(organization, "perm2", user1);
        addProjectPermission(organization, "perm1", user1, project1);
        addProjectPermission(organization, "perm3", user2, project1);
        addProjectPermission(organization, "perm4", user2, project2);
        // user2 does not have global permissions -> do nothing
        underTest.deleteGlobalPermission(dbSession, user2.getId(), "perm1", db.getDefaultOrganization().getUuid());
        assertThat(db.countRowsOfTable(dbSession, "user_roles")).isEqualTo(5);
        // global permission is not granted -> do nothing
        underTest.deleteGlobalPermission(dbSession, user1.getId(), "notGranted", db.getDefaultOrganization().getUuid());
        assertThat(db.countRowsOfTable(dbSession, "user_roles")).isEqualTo(5);
        // permission is on project -> do nothing
        underTest.deleteGlobalPermission(dbSession, user1.getId(), "perm3", db.getDefaultOrganization().getUuid());
        assertThat(db.countRowsOfTable(dbSession, "user_roles")).isEqualTo(5);
        // global permission on another organization-> do nothing
        underTest.deleteGlobalPermission(dbSession, user1.getId(), "notGranted", "anotherOrg");
        assertThat(db.countRowsOfTable(dbSession, "user_roles")).isEqualTo(5);
        // global permission exists -> delete it, but not the project permission with the same name !
        underTest.deleteGlobalPermission(dbSession, user1.getId(), "perm1", organization.getUuid());
        assertThat(db.countSql(dbSession, "select count(id) from user_roles where role='perm1' and resource_id is null")).isEqualTo(0);
        assertThat(db.countRowsOfTable(dbSession, "user_roles")).isEqualTo(4);
    }

    @Test
    public void deleteProjectPermission() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user1 = insertUser(organization);
        UserDto user2 = insertUser(organization);
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        ComponentDto project2 = db.components().insertPrivateProject(organization);
        addGlobalPermission(organization, "perm", user1);
        addProjectPermission(organization, "perm", user1, project1);
        addProjectPermission(organization, "perm", user1, project2);
        addProjectPermission(organization, "perm", user2, project1);
        // no such provision -> ignore
        underTest.deleteProjectPermission(dbSession, user1.getId(), "anotherPerm", project1.getId());
        assertThat(db.countRowsOfTable(dbSession, "user_roles")).isEqualTo(4);
        underTest.deleteProjectPermission(dbSession, user1.getId(), "perm", project1.getId());
        assertThatProjectPermissionDoesNotExist(user1, "perm", project1);
        assertThat(db.countRowsOfTable(dbSession, "user_roles")).isEqualTo(3);
    }

    @Test
    public void deleteProjectPermissions() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user1 = insertUser(organization);
        UserDto user2 = insertUser(organization);
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        ComponentDto project2 = db.components().insertPrivateProject(organization);
        addGlobalPermission(organization, "perm", user1);
        addProjectPermission(organization, "perm", user1, project1);
        addProjectPermission(organization, "perm", user2, project1);
        addProjectPermission(organization, "perm", user1, project2);
        underTest.deleteProjectPermissions(dbSession, project1.getId());
        assertThat(db.countRowsOfTable(dbSession, "user_roles")).isEqualTo(2);
        assertThatProjectHasNoPermissions(project1);
    }

    @Test
    public void selectGlobalPermissionsOfUser() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user1 = insertUser(organization);
        UserDto user2 = insertUser(organization);
        UserDto user3 = insertUser(organization);
        OrganizationDto org = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization);
        addGlobalPermission(db.getDefaultOrganization(), "perm1", user1);
        addGlobalPermission(org, "perm2", user2);
        addGlobalPermission(org, "perm3", user1);
        addProjectPermission(organization, "perm4", user1, project);
        addProjectPermission(organization, "perm5", user1, project);
        assertThat(underTest.selectGlobalPermissionsOfUser(dbSession, user1.getId(), org.getUuid())).containsOnly("perm3");
        assertThat(underTest.selectGlobalPermissionsOfUser(dbSession, user1.getId(), db.getDefaultOrganization().getUuid())).containsOnly("perm1");
        assertThat(underTest.selectGlobalPermissionsOfUser(dbSession, user1.getId(), "otherOrg")).isEmpty();
        assertThat(underTest.selectGlobalPermissionsOfUser(dbSession, user3.getId(), org.getUuid())).isEmpty();
    }

    @Test
    public void selectProjectPermissionsOfUser() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user1 = insertUser(organization);
        UserDto user2 = insertUser(organization);
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        ComponentDto project2 = db.components().insertPrivateProject(organization);
        ComponentDto project3 = db.components().insertPrivateProject(organization);
        addGlobalPermission(organization, "perm1", user1);
        addProjectPermission(organization, "perm2", user1, project1);
        addProjectPermission(organization, "perm3", user1, project1);
        addProjectPermission(organization, "perm4", user1, project2);
        addProjectPermission(organization, "perm5", user2, project1);
        assertThat(underTest.selectProjectPermissionsOfUser(dbSession, user1.getId(), project1.getId())).containsOnly("perm2", "perm3");
        assertThat(underTest.selectProjectPermissionsOfUser(dbSession, user1.getId(), project2.getId())).containsOnly("perm4");
        assertThat(underTest.selectProjectPermissionsOfUser(dbSession, user1.getId(), project3.getId())).isEmpty();
    }

    @Test
    public void selectGroupIdsWithPermissionOnProjectBut_returns_empty_if_project_does_not_exist() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = randomPublicOrPrivateProject(organization);
        UserDto user = insertUser(organization);
        db.users().insertProjectPermissionOnUser(user, "foo", project);
        assertThat(underTest.selectUserIdsWithPermissionOnProjectBut(dbSession, 1234, UserRole.USER)).isEmpty();
    }

    @Test
    public void selectGroupIdsWithPermissionOnProjectBut_returns_only_users_of_projects_which_do_not_have_permission() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = randomPublicOrPrivateProject(organization);
        UserDto user1 = insertUser(organization);
        UserDto user2 = insertUser(organization);
        db.users().insertProjectPermissionOnUser(user1, "p1", project);
        db.users().insertProjectPermissionOnUser(user2, "p2", project);
        assertThat(underTest.selectUserIdsWithPermissionOnProjectBut(dbSession, project.getId(), "p2")).containsOnly(user1.getId());
        assertThat(underTest.selectUserIdsWithPermissionOnProjectBut(dbSession, project.getId(), "p1")).containsOnly(user2.getId());
        assertThat(underTest.selectUserIdsWithPermissionOnProjectBut(dbSession, project.getId(), "p3")).containsOnly(user1.getId(), user2.getId());
    }

    @Test
    public void selectGroupIdsWithPermissionOnProjectBut_does_not_return_groups_which_have_no_permission_at_all_on_specified_project() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = randomPublicOrPrivateProject(organization);
        UserDto user1 = insertUser(organization);
        UserDto user2 = insertUser(organization);
        db.users().insertProjectPermissionOnUser(user1, "p1", project);
        db.users().insertProjectPermissionOnUser(user2, "p2", project);
        assertThat(underTest.selectUserIdsWithPermissionOnProjectBut(dbSession, project.getId(), "p2")).containsOnly(user1.getId());
        assertThat(underTest.selectUserIdsWithPermissionOnProjectBut(dbSession, project.getId(), "p1")).containsOnly(user2.getId());
    }

    @Test
    public void deleteByOrganization_does_not_fail_if_table_is_empty() {
        underTest.deleteByOrganization(dbSession, "some uuid");
        dbSession.commit();
    }

    @Test
    public void deleteByOrganization_does_not_fail_if_organization_has_no_user_permission() {
        OrganizationDto organization = db.organizations().insert();
        underTest.deleteByOrganization(dbSession, organization.getUuid());
        dbSession.commit();
    }

    @Test
    public void deleteByOrganization_deletes_all_user_permission_of_specified_organization() {
        OrganizationDto organization1 = db.organizations().insert();
        OrganizationDto organization2 = db.organizations().insert();
        OrganizationDto organization3 = db.organizations().insert();
        UserDto user1 = insertUser(organization1, organization2, organization3);
        UserDto user2 = insertUser(organization1, organization2, organization3);
        UserDto user3 = insertUser(organization1, organization2, organization3);
        db.users().insertPermissionOnUser(organization1, user1, "foo");
        db.users().insertPermissionOnUser(organization1, user2, "foo");
        db.users().insertPermissionOnUser(organization1, user2, "bar");
        db.users().insertPermissionOnUser(organization2, user2, "foo");
        db.users().insertPermissionOnUser(organization2, user3, "foo");
        db.users().insertPermissionOnUser(organization2, user3, "bar");
        db.users().insertPermissionOnUser(organization3, user3, "foo");
        db.users().insertPermissionOnUser(organization3, user1, "foo");
        db.users().insertPermissionOnUser(organization3, user1, "bar");
        underTest.deleteByOrganization(dbSession, organization3.getUuid());
        dbSession.commit();
        verifyOrganizationUuidsInTable(organization1.getUuid(), organization2.getUuid());
        underTest.deleteByOrganization(dbSession, organization2.getUuid());
        dbSession.commit();
        verifyOrganizationUuidsInTable(organization1.getUuid());
        underTest.deleteByOrganization(dbSession, organization1.getUuid());
        dbSession.commit();
        verifyOrganizationUuidsInTable();
    }

    @Test
    public void delete_permissions_of_an_organization_member() {
        OrganizationDto organization1 = db.organizations().insert();
        OrganizationDto organization2 = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization1);
        UserDto user1 = insertUser(organization1, organization2);
        UserDto user2 = insertUser(organization1, organization2);
        // user 1 permissions
        db.users().insertPermissionOnUser(organization1, user1, OrganizationPermission.SCAN);
        db.users().insertPermissionOnUser(organization1, user1, OrganizationPermission.ADMINISTER);
        db.users().insertProjectPermissionOnUser(user1, CODEVIEWER, project);
        db.users().insertPermissionOnUser(organization2, user1, OrganizationPermission.SCAN);
        // user 2 permission
        db.users().insertPermissionOnUser(organization1, user2, OrganizationPermission.SCAN);
        db.users().insertProjectPermissionOnUser(user2, CODEVIEWER, project);
        underTest.deleteOrganizationMemberPermissions(dbSession, organization1.getUuid(), user1.getId());
        dbSession.commit();
        // user 1 permissions
        assertOrgPermissionsOfUser(user1, organization1);
        assertOrgPermissionsOfUser(user1, organization2, OrganizationPermission.SCAN);
        assertProjectPermissionsOfUser(user1, project);
        // user 2 permissions
        assertOrgPermissionsOfUser(user2, organization1, OrganizationPermission.SCAN);
        assertProjectPermissionsOfUser(user2, project, CODEVIEWER);
    }

    @Test
    public void deleteByUserId() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user1 = insertUser(organization);
        UserDto user2 = insertUser(organization);
        ComponentDto project = db.components().insertPrivateProject(organization);
        db.users().insertPermissionOnUser(user1, OrganizationPermission.SCAN);
        db.users().insertPermissionOnUser(user1, OrganizationPermission.ADMINISTER);
        db.users().insertProjectPermissionOnUser(user1, OrganizationPermission.ADMINISTER_QUALITY_GATES.getKey(), project);
        db.users().insertPermissionOnUser(user2, OrganizationPermission.SCAN);
        db.users().insertProjectPermissionOnUser(user2, OrganizationPermission.ADMINISTER_QUALITY_GATES.getKey(), project);
        underTest.deleteByUserId(dbSession, user1.getId());
        dbSession.commit();
        assertThat(db.select("select user_id as \"userId\", resource_id as \"projectId\", role as \"permission\" from user_roles")).extracting(( row) -> row.get("userId"), ( row) -> row.get("projectId"), ( row) -> row.get("permission")).containsOnly(tuple(user2.getId().longValue(), null, OrganizationPermission.SCAN.getKey()), tuple(user2.getId().longValue(), project.getId(), OrganizationPermission.ADMINISTER_QUALITY_GATES.getKey()));
    }

    @Test
    public void deleteProjectPermissionOfAnyUser_has_no_effect_if_specified_component_does_not_exist() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user = insertUser(organization);
        db.users().insertPermissionOnUser(organization, user, OrganizationPermission.SCAN);
        int deletedCount = underTest.deleteProjectPermissionOfAnyUser(dbSession, 124L, OrganizationPermission.SCAN.getKey());
        assertThat(deletedCount).isEqualTo(0);
        assertThat(underTest.selectGlobalPermissionsOfUser(dbSession, user.getId(), organization.getUuid())).containsOnly(OrganizationPermission.SCAN.getKey());
    }

    @Test
    public void deleteProjectPermissionOfAnyUser_has_no_effect_if_specified_component_has_no_permission_at_all() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user = insertUser(organization);
        db.users().insertPermissionOnUser(organization, user, OrganizationPermission.SCAN);
        ComponentDto project = randomPublicOrPrivateProject(organization);
        int deletedCount = underTest.deleteProjectPermissionOfAnyUser(dbSession, project.getId(), OrganizationPermission.SCAN.getKey());
        assertThat(deletedCount).isEqualTo(0);
        assertThat(underTest.selectGlobalPermissionsOfUser(dbSession, user.getId(), organization.getUuid())).containsOnly(OrganizationPermission.SCAN.getKey());
    }

    @Test
    public void deleteProjectPermissionOfAnyUser_has_no_effect_if_specified_component_does_not_have_specified_permission() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user = insertUser(organization);
        db.users().insertPermissionOnUser(organization, user, OrganizationPermission.SCAN);
        ComponentDto project = randomPublicOrPrivateProject(organization);
        db.users().insertProjectPermissionOnUser(user, OrganizationPermission.SCAN.getKey(), project);
        int deletedCount = underTest.deleteProjectPermissionOfAnyUser(dbSession, project.getId(), "p1");
        assertThat(deletedCount).isEqualTo(0);
        assertThat(underTest.selectGlobalPermissionsOfUser(dbSession, user.getId(), organization.getUuid())).containsOnly(OrganizationPermission.SCAN.getKey());
        assertThat(underTest.selectProjectPermissionsOfUser(dbSession, user.getId(), project.getId())).containsOnly(OrganizationPermission.SCAN.getKey());
    }

    @Test
    public void deleteProjectPermissionOfAnyUser_deletes_specified_permission_for_any_user_on_the_specified_component() {
        OrganizationDto organization = db.organizations().insert();
        UserDto user1 = insertUser(organization);
        UserDto user2 = insertUser(organization);
        db.users().insertPermissionOnUser(organization, user1, OrganizationPermission.SCAN);
        db.users().insertPermissionOnUser(organization, user2, OrganizationPermission.SCAN);
        ComponentDto project1 = randomPublicOrPrivateProject(organization);
        ComponentDto project2 = randomPublicOrPrivateProject(organization);
        db.users().insertProjectPermissionOnUser(user1, OrganizationPermission.SCAN.getKey(), project1);
        db.users().insertProjectPermissionOnUser(user2, OrganizationPermission.SCAN.getKey(), project1);
        db.users().insertProjectPermissionOnUser(user1, OrganizationPermission.SCAN.getKey(), project2);
        db.users().insertProjectPermissionOnUser(user2, OrganizationPermission.SCAN.getKey(), project2);
        db.users().insertProjectPermissionOnUser(user2, OrganizationPermission.PROVISION_PROJECTS.getKey(), project2);
        int deletedCount = underTest.deleteProjectPermissionOfAnyUser(dbSession, project1.getId(), OrganizationPermission.SCAN.getKey());
        assertThat(deletedCount).isEqualTo(2);
        assertThat(underTest.selectGlobalPermissionsOfUser(dbSession, user1.getId(), organization.getUuid())).containsOnly(OrganizationPermission.SCAN.getKey());
        assertThat(underTest.selectGlobalPermissionsOfUser(dbSession, user2.getId(), organization.getUuid())).containsOnly(OrganizationPermission.SCAN.getKey());
        assertThat(underTest.selectProjectPermissionsOfUser(dbSession, user1.getId(), project1.getId())).isEmpty();
        assertThat(underTest.selectProjectPermissionsOfUser(dbSession, user2.getId(), project1.getId())).isEmpty();
        assertThat(underTest.selectProjectPermissionsOfUser(dbSession, user1.getId(), project2.getId())).containsOnly(OrganizationPermission.SCAN.getKey());
        assertThat(underTest.selectProjectPermissionsOfUser(dbSession, user2.getId(), project2.getId())).containsOnly(OrganizationPermission.SCAN.getKey(), OrganizationPermission.PROVISION_PROJECTS.getKey());
        deletedCount = underTest.deleteProjectPermissionOfAnyUser(dbSession, project2.getId(), OrganizationPermission.SCAN.getKey());
        assertThat(deletedCount).isEqualTo(2);
        assertThat(underTest.selectGlobalPermissionsOfUser(dbSession, user1.getId(), organization.getUuid())).containsOnly(OrganizationPermission.SCAN.getKey());
        assertThat(underTest.selectGlobalPermissionsOfUser(dbSession, user2.getId(), organization.getUuid())).containsOnly(OrganizationPermission.SCAN.getKey());
        assertThat(underTest.selectProjectPermissionsOfUser(dbSession, user1.getId(), project1.getId())).isEmpty();
        assertThat(underTest.selectProjectPermissionsOfUser(dbSession, user2.getId(), project1.getId())).isEmpty();
        assertThat(underTest.selectProjectPermissionsOfUser(dbSession, user1.getId(), project2.getId())).containsOnly();
        assertThat(underTest.selectProjectPermissionsOfUser(dbSession, user2.getId(), project2.getId())).containsOnly(OrganizationPermission.PROVISION_PROJECTS.getKey());
    }
}

