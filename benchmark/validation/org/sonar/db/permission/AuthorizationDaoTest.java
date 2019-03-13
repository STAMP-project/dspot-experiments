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
import UserRole.ADMIN;
import UserRole.CODEVIEWER;
import UserRole.ISSUE_ADMIN;
import UserRole.USER;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.core.util.stream.MoreCollectors;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.user.GroupDto;
import org.sonar.db.user.UserDto;


public class AuthorizationDaoTest {
    private static final Long PROJECT_ID = 300L;

    private static final int MISSING_ID = -1;

    private static final String A_PERMISSION = "a-permission";

    private static final String DOES_NOT_EXIST = "does-not-exist";

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    private final Random random = new Random();

    private DbSession dbSession = db.getSession();

    private AuthorizationDao underTest = new AuthorizationDao();

    private OrganizationDto organization;

    private UserDto user;

    private GroupDto group1;

    private GroupDto group2;

    private Set<Long> randomPublicProjectIds;

    private Set<Long> randomPrivateProjectIds;

    private Set<Integer> randomExistingUserIds;

    private String randomPermission = "p" + (random.nextInt());

    /**
     * Union of the permissions granted to:
     * - the user
     * - the groups which user is member
     * - anyone
     */
    @Test
    public void selectOrganizationPermissions_for_logged_in_user() {
        ComponentDto project = db.components().insertPrivateProject(organization);
        db.users().insertMember(group1, user);
        db.users().insertPermissionOnUser(organization, user, "perm1");
        db.users().insertProjectPermissionOnUser(user, "perm42", project);
        db.users().insertPermissionOnGroup(group1, "perm2");
        db.users().insertPermissionOnAnyone(organization, "perm3");
        // ignored permissions, user is not member of this group
        db.users().insertPermissionOnGroup(group2, "ignored");
        Set<String> permissions = underTest.selectOrganizationPermissions(dbSession, organization.getUuid(), user.getId());
        assertThat(permissions).containsOnly("perm1", "perm2", "perm3");
    }

    /**
     * Anonymous user only benefits from the permissions granted to
     * "Anyone"
     */
    @Test
    public void selectOrganizationPermissions_for_anonymous_user() {
        db.users().insertPermissionOnAnyone(organization, "perm1");
        // ignored permissions
        db.users().insertPermissionOnUser(organization, user, "ignored");
        db.users().insertPermissionOnGroup(group1, "ignored");
        Set<String> permissions = underTest.selectOrganizationPermissionsOfAnonymous(dbSession, organization.getUuid());
        assertThat(permissions).containsOnly("perm1");
    }

    @Test
    public void countUsersWithGlobalPermissionExcludingGroup() {
        // users with global permission "perm1" :
        // - "u1" and "u2" through group "g1"
        // - "u1" and "u3" through group "g2"
        // - "u4"
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        UserDto user3 = db.users().insertUser();
        UserDto user4 = db.users().insertUser();
        UserDto user5 = db.users().insertUser();
        OrganizationDto organization = db.organizations().insert();
        GroupDto group1 = db.users().insertGroup(organization, "g1");
        db.users().insertPermissionOnGroup(group1, "perm1");
        db.users().insertPermissionOnGroup(group1, "perm2");
        db.users().insertMember(group1, user1);
        db.users().insertMember(group1, user2);
        GroupDto group2 = db.users().insertGroup(organization, "g2");
        db.users().insertPermissionOnGroup(group2, "perm1");
        db.users().insertPermissionOnGroup(group2, "perm2");
        db.users().insertMember(group2, user1);
        db.users().insertMember(group2, user3);
        // group3 has the permission "perm1" but has no users
        GroupDto group3 = db.users().insertGroup(organization, "g2");
        db.users().insertPermissionOnGroup(group3, "perm1");
        db.users().insertPermissionOnUser(organization, user4, "perm1");
        db.users().insertPermissionOnUser(organization, user4, "perm2");
        db.users().insertPermissionOnAnyone(organization, "perm1");
        // other organizations are ignored
        OrganizationDto org2 = db.organizations().insert();
        db.users().insertPermissionOnUser(org2, user1, "perm1");
        // excluding group "g1" -> remain u1, u3 and u4
        assertThat(underTest.countUsersWithGlobalPermissionExcludingGroup(db.getSession(), organization.getUuid(), "perm1", group1.getId())).isEqualTo(3);
        // excluding group "g2" -> remain u1, u2 and u4
        assertThat(underTest.countUsersWithGlobalPermissionExcludingGroup(db.getSession(), organization.getUuid(), "perm1", group2.getId())).isEqualTo(3);
        // excluding group "g3" -> remain u1, u2, u3 and u4
        assertThat(underTest.countUsersWithGlobalPermissionExcludingGroup(db.getSession(), organization.getUuid(), "perm1", group3.getId())).isEqualTo(4);
        // nobody has the permission
        assertThat(underTest.countUsersWithGlobalPermissionExcludingGroup(db.getSession(), organization.getUuid(), "missingPermission", group1.getId())).isEqualTo(0);
    }

    @Test
    public void countUsersWithGlobalPermissionExcludingUser() {
        // group g1 has the permission p1 and has members user1 and user2
        // user3 has the permission
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        UserDto user3 = db.users().insertUser();
        OrganizationDto organization = db.organizations().insert();
        GroupDto group1 = db.users().insertGroup(organization, "g1");
        db.users().insertPermissionOnGroup(group1, "p1");
        db.users().insertPermissionOnGroup(group1, "p2");
        db.users().insertMember(group1, user1);
        db.users().insertMember(group1, user2);
        db.users().insertPermissionOnUser(organization, user3, "p1");
        db.users().insertPermissionOnAnyone(organization, "p1");
        // other organizations are ignored
        OrganizationDto org2 = db.organizations().insert();
        db.users().insertPermissionOnUser(org2, user1, "p1");
        // excluding user1 -> remain user2 and user3
        assertThat(underTest.countUsersWithGlobalPermissionExcludingUser(db.getSession(), organization.getUuid(), "p1", user1.getId())).isEqualTo(2);
        // excluding user3 -> remain the members of group g1
        assertThat(underTest.countUsersWithGlobalPermissionExcludingUser(db.getSession(), organization.getUuid(), "p1", user3.getId())).isEqualTo(2);
        // excluding unknown user
        assertThat(underTest.countUsersWithGlobalPermissionExcludingUser(db.getSession(), organization.getUuid(), "p1", (-1))).isEqualTo(3);
        // nobody has the permission
        assertThat(underTest.countUsersWithGlobalPermissionExcludingUser(db.getSession(), organization.getUuid(), "missingPermission", group1.getId())).isEqualTo(0);
    }

    @Test
    public void selectUserIdsWithGlobalPermission() {
        // group g1 has the permission p1 and has members user1 and user2
        // user3 has the permission
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        UserDto user3 = db.users().insertUser();
        OrganizationDto organization = db.organizations().insert();
        GroupDto group1 = db.users().insertGroup(organization);
        db.users().insertPermissionOnGroup(group1, OrganizationPermission.ADMINISTER);
        db.users().insertPermissionOnGroup(group1, OrganizationPermission.PROVISION_PROJECTS);
        db.users().insertMember(group1, user1);
        db.users().insertMember(group1, user2);
        db.users().insertPermissionOnUser(organization, user3, OrganizationPermission.ADMINISTER);
        db.users().insertPermissionOnAnyone(organization, OrganizationPermission.ADMINISTER);
        // other organizations are ignored
        OrganizationDto org2 = db.organizations().insert();
        db.users().insertPermissionOnUser(org2, user1, OrganizationPermission.ADMINISTER);
        assertThat(underTest.selectUserIdsWithGlobalPermission(db.getSession(), organization.getUuid(), OrganizationPermission.ADMINISTER.getKey())).containsExactlyInAnyOrder(user1.getId(), user2.getId(), user3.getId());
        assertThat(underTest.selectUserIdsWithGlobalPermission(db.getSession(), organization.getUuid(), OrganizationPermission.PROVISION_PROJECTS.getKey())).containsExactlyInAnyOrder(user1.getId(), user2.getId());
        assertThat(underTest.selectUserIdsWithGlobalPermission(db.getSession(), org2.getUuid(), OrganizationPermission.ADMINISTER.getKey())).containsExactlyInAnyOrder(user1.getId());
    }

    @Test
    public void keepAuthorizedProjectIds_returns_empty_for_group_AnyOne_if_project_set_is_empty_on_public_project() {
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.emptySet(), null, USER)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_empty_for_user_if_project_set_is_empty_on_public_project() {
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.emptySet(), user.getId(), USER)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_empty_for_group_AnyOne_for_non_existent_projects() {
        Set<Long> randomNonProjectsSet = IntStream.range(0, (1 + (Math.abs(random.nextInt(5))))).mapToLong(( i) -> 3562 + i).boxed().collect(MoreCollectors.toSet());
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomNonProjectsSet, null, USER)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_empty_for_user_for_non_existent_projects() {
        Set<Long> randomNonProjectsSet = IntStream.range(0, (1 + (Math.abs(random.nextInt(5))))).mapToLong(( i) -> 9666 + i).boxed().collect(MoreCollectors.toSet());
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomNonProjectsSet, user.getId(), USER)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_any_public_project_for_group_AnyOne_without_any_permission_in_DB_and_permission_USER() {
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomPublicProjectIds, null, USER)).containsAll(randomPublicProjectIds);
    }

    @Test
    public void keepAuthorizedProjectIds_returns_any_public_project_for_user_without_any_permission_in_DB_and_permission_USER() {
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomPublicProjectIds, user.getId(), USER)).containsAll(randomPublicProjectIds);
    }

    @Test
    public void keepAuthorizedProjectIds_returns_any_public_project_for_group_AnyOne_without_any_permission_in_DB_and_permission_CODEVIEWER() {
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomPublicProjectIds, null, CODEVIEWER)).containsAll(randomPublicProjectIds);
    }

    @Test
    public void keepAuthorizedProjectIds_returns_any_public_project_for_user_without_any_permission_in_DB_and_permission_CODEVIEWER() {
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomPublicProjectIds, user.getId(), CODEVIEWER)).containsAll(randomPublicProjectIds);
    }

    @Test
    public void keepAuthorizedProjectIds_returns_empty_for_other_permission_for_group_AnyOne_on_public_project_without_any_permission_in_DB() {
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomPublicProjectIds, null, randomPermission)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_empty_for_any_permission_for_user_on_public_project_without_any_permission_in_DB() {
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomPublicProjectIds, user.getId(), randomPermission)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_public_project_if_user_is_granted_project_permission_directly() {
        ComponentDto project = db.components().insertPublicProject(organization);
        ComponentDto otherProject = db.components().insertPublicProject(organization);
        UserDto otherUser = db.users().insertUser();
        db.users().insertProjectPermissionOnUser(user, randomPermission, project);
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(project.getId()), otherUser.getId(), randomPermission)).isEmpty();
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(otherProject.getId()), user.getId(), randomPermission)).isEmpty();
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(project.getId()), user.getId(), randomPermission)).containsOnly(project.getId());
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(project.getId()), user.getId(), "another perm")).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_public_project_if_user_is_granted_project_permission_by_group() {
        ComponentDto project = db.components().insertPublicProject(organization);
        ComponentDto otherProject = db.components().insertPublicProject(organization);
        UserDto otherUser = db.users().insertUser();
        db.users().insertMember(group1, user);
        db.users().insertProjectPermissionOnGroup(group1, randomPermission, project);
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(project.getId()), user.getId(), randomPermission)).containsOnly(project.getId());
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(otherProject.getId()), user.getId(), randomPermission)).isEmpty();
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(project.getId()), otherUser.getId(), randomPermission)).isEmpty();
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(project.getId()), user.getId(), "another perm")).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_public_project_if_group_AnyOne_is_granted_project_permission_directly() {
        ComponentDto project = db.components().insertPublicProject(organization);
        ComponentDto otherProject = db.components().insertPublicProject(organization);
        db.users().insertProjectPermissionOnAnyone(randomPermission, project);
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(project.getId()), null, randomPermission)).containsOnly(project.getId());
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(project.getId()), null, "another perm")).isEmpty();
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(otherProject.getId()), null, randomPermission)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_empty_for_user_on_private_project_without_any_permission_in_DB_and_permission_USER() {
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomPrivateProjectIds, user.getId(), USER)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_empty_for_group_AnyOne_on_private_project_without_any_permission_in_DB_and_permission_USER() {
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomPrivateProjectIds, null, USER)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_empty_for_user_on_private_project_without_any_permission_in_DB_and_permission_CODEVIEWER() {
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomPrivateProjectIds, user.getId(), CODEVIEWER)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_empty_for_group_AnyOne_on_private_project_without_any_permission_in_DB_and_permission_CODEVIEWER() {
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomPrivateProjectIds, null, CODEVIEWER)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_empty_for_user_and_any_permission_on_private_project_without_any_permission_in_DB() {
        PermissionsTestHelper.ALL_PERMISSIONS.forEach(( perm) -> {
            assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomPrivateProjectIds, user.getId(), perm)).isEmpty();
        });
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomPrivateProjectIds, user.getId(), randomPermission)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_empty_for_group_AnyOne_and_any_permission_on_private_project_without_any_permission_in_DB() {
        PermissionsTestHelper.ALL_PERMISSIONS.forEach(( perm) -> {
            assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomPrivateProjectIds, null, perm)).isEmpty();
        });
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, randomPrivateProjectIds, null, randomPermission)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_private_project_if_user_is_granted_project_permission_directly() {
        ComponentDto project = db.components().insertPrivateProject(organization);
        ComponentDto otherProject = db.components().insertPrivateProject(organization);
        UserDto otherUser = db.users().insertUser();
        db.users().insertProjectPermissionOnUser(user, randomPermission, project);
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(project.getId()), user.getId(), randomPermission)).containsOnly(project.getId());
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(project.getId()), user.getId(), "another perm")).isEmpty();
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(otherProject.getId()), user.getId(), randomPermission)).isEmpty();
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(project.getId()), otherUser.getId(), randomPermission)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_returns_private_project_if_user_is_granted_project_permission_by_group() {
        ComponentDto project = db.components().insertPrivateProject(organization);
        ComponentDto otherProject = db.components().insertPrivateProject(organization);
        UserDto otherUser = db.users().insertUser();
        db.users().insertMember(group1, user);
        db.users().insertProjectPermissionOnGroup(group1, randomPermission, project);
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(project.getId()), user.getId(), randomPermission)).containsOnly(project.getId());
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(project.getId()), user.getId(), "another perm")).isEmpty();
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(otherProject.getId()), user.getId(), randomPermission)).isEmpty();
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.singleton(project.getId()), otherUser.getId(), randomPermission)).isEmpty();
    }

    @Test
    public void user_should_be_authorized() {
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        ComponentDto project2 = db.components().insertPrivateProject(organization);
        ComponentDto project3 = db.components().insertPrivateProject(organization);
        UserDto user = db.users().insertUser("u1");
        GroupDto group = db.users().insertGroup(organization);
        db.users().insertProjectPermissionOnUser(user, USER, project2);
        db.users().insertProjectPermissionOnUser(user, USER, project3);
        db.users().insertMember(group, user);
        db.users().insertProjectPermissionOnGroup(group, USER, project1);
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Sets.newHashSet(project2.getId(), project3.getId()), user.getId(), USER)).containsOnly(project2.getId(), project3.getId());
        // user does not have the role "admin"
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Sets.newHashSet(project2.getId()), user.getId(), ADMIN)).isEmpty();
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Collections.emptySet(), user.getId(), ADMIN)).isEmpty();
    }

    @Test
    public void group_should_be_authorized() {
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        ComponentDto project2 = db.components().insertPrivateProject(organization);
        ComponentDto project3 = db.components().insertPrivateProject(organization);
        UserDto user1 = db.users().insertUser("u1");
        GroupDto group = db.users().insertGroup(organization);
        db.users().insertMembers(group, user1);
        db.users().insertProjectPermissionOnUser(user1, USER, project1);
        db.users().insertProjectPermissionOnGroup(group, USER, project2);
        db.users().insertProjectPermissionOnGroup(group, USER, project3);
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Sets.newHashSet(project2.getId(), project3.getId()), user1.getId(), USER)).containsOnly(project2.getId(), project3.getId());
        // group does not have the role "admin"
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Sets.newHashSet(project2.getId(), project3.getId()), user1.getId(), ADMIN)).isEmpty();
    }

    @Test
    public void anonymous_should_be_authorized() {
        ComponentDto project1 = db.components().insertPublicProject(organization);
        ComponentDto project2 = db.components().insertPublicProject(organization);
        UserDto user1 = db.users().insertUser("u1");
        GroupDto group = db.users().insertGroup(organization);
        db.users().insertMembers(group, user1);
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Sets.newHashSet(project1.getId(), project2.getId()), null, USER)).containsOnly(project1.getId(), project2.getId());
        // group does not have the role "admin"
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, Sets.newHashSet(project1.getId()), null, "admin")).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectIds_should_be_able_to_handle_lots_of_projects() {
        List<ComponentDto> projects = IntStream.range(0, 2000).mapToObj(( i) -> db.components().insertPublicProject(organization)).collect(Collectors.toList());
        Collection<Long> ids = projects.stream().map(ComponentDto::getId).collect(Collectors.toSet());
        assertThat(underTest.keepAuthorizedProjectIds(dbSession, ids, null, USER)).containsOnly(ids.toArray(new Long[0]));
    }

    @Test
    public void keepAuthorizedProjectUuids_should_be_able_to_handle_lots_of_projects() {
        List<ComponentDto> projects = IntStream.range(0, 2000).mapToObj(( i) -> db.components().insertPublicProject(organization)).collect(Collectors.toList());
        Collection<String> uuids = projects.stream().map(ComponentDto::uuid).collect(Collectors.toSet());
        assertThat(underTest.keepAuthorizedProjectUuids(dbSession, uuids, null, USER)).containsOnly(uuids.toArray(new String[0]));
    }

    @Test
    public void keepAuthorizedUsersForRoleAndProject_returns_empty_if_user_set_is_empty_on_public_project() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPublicProject(organization);
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.emptySet(), USER, project.getId())).isEmpty();
    }

    @Test
    public void keepAuthorizedUsersForRoleAndProject_returns_empty_for_non_existent_users() {
        ComponentDto project = (random.nextBoolean()) ? db.components().insertPublicProject(organization) : db.components().insertPrivateProject(organization);
        Set<Integer> randomNonExistingUserIdsSet = IntStream.range(0, (1 + (Math.abs(random.nextInt(5))))).map(( i) -> i + 1990).boxed().collect(MoreCollectors.toSet());
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, randomNonExistingUserIdsSet, USER, project.getId())).isEmpty();
    }

    @Test
    public void keepAuthorizedUsersForRoleAndProject_returns_any_users_for_public_project_without_any_permission_in_DB_and_permission_USER() {
        ComponentDto project = db.components().insertPublicProject(organization);
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, randomExistingUserIds, USER, project.getId())).containsAll(randomExistingUserIds);
    }

    @Test
    public void keepAuthorizedUsersForRoleAndProject_returns_any_users_for_public_project_without_any_permission_in_DB_and_permission_CODEVIEWER() {
        ComponentDto project = db.components().insertPublicProject(organization);
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, randomExistingUserIds, CODEVIEWER, project.getId())).containsAll(randomExistingUserIds);
    }

    @Test
    public void keepAuthorizedUsersForRoleAndProject_returns_empty_for_any_users_on_public_project_without_any_permission_in_DB() {
        ComponentDto project = db.components().insertPublicProject(organization);
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, randomExistingUserIds, randomPermission, project.getId())).isEmpty();
    }

    @Test
    public void keepAuthorizedUsersForRoleAndProject_returns_user_if_granted_project_permission_directly_on_public_project() {
        ComponentDto project = db.components().insertPublicProject(organization);
        ComponentDto otherProject = db.components().insertPublicProject(organization);
        UserDto otherUser = db.users().insertUser();
        db.users().insertProjectPermissionOnUser(user, randomPermission, project);
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), randomPermission, project.getId())).containsOnly(user.getId());
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), "another perm", project.getId())).isEmpty();
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(otherUser.getId()), randomPermission, project.getId())).isEmpty();
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), randomPermission, otherProject.getId())).isEmpty();
    }

    @Test
    public void keepAuthorizedUsersForRoleAndProject_returns_user_if_granted_project_permission_by_group_on_public_project() {
        ComponentDto project = db.components().insertPublicProject(organization);
        ComponentDto otherProject = db.components().insertPublicProject(organization);
        UserDto otherUser = db.users().insertUser();
        db.users().insertMember(group1, user);
        db.users().insertProjectPermissionOnGroup(group1, randomPermission, project);
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), randomPermission, project.getId())).containsOnly(user.getId());
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), "another perm", project.getId())).isEmpty();
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), randomPermission, otherProject.getId())).isEmpty();
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(otherUser.getId()), randomPermission, project.getId())).isEmpty();
    }

    @Test
    public void keepAuthorizedUsersForRoleAndProject_does_not_return_user_if_granted_project_permission_by_AnyOne_on_public_project() {
        ComponentDto project = db.components().insertPublicProject(organization);
        ComponentDto otherProject = db.components().insertPublicProject(organization);
        UserDto otherUser = db.users().insertUser();
        db.users().insertProjectPermissionOnAnyone(randomPermission, project);
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), randomPermission, project.getId())).isEmpty();
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), "another perm", project.getId())).isEmpty();
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), randomPermission, otherProject.getId())).isEmpty();
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(otherUser.getId()), randomPermission, project.getId())).isEmpty();
    }

    @Test
    public void keepAuthorizedUsersForRoleAndProject_returns_empty_for_any_user_on_private_project_without_any_permission_in_DB_and_permission_USER() {
        ComponentDto project = db.components().insertPrivateProject(organization);
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, randomExistingUserIds, USER, project.getId())).isEmpty();
    }

    @Test
    public void keepAuthorizedUsersForRoleAndProject_returns_empty_for_any_user_on_private_project_without_any_permission_in_DB_and_permission_CODEVIEWER() {
        ComponentDto project = db.components().insertPrivateProject(organization);
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, randomExistingUserIds, CODEVIEWER, project.getId())).isEmpty();
    }

    @Test
    public void keepAuthorizedUsersForRoleAndProject_returns_empty_for_any_users_and_any_permission_on_private_project_without_any_permission_in_DB() {
        ComponentDto project = db.components().insertPrivateProject(organization);
        PermissionsTestHelper.ALL_PERMISSIONS.forEach(( perm) -> {
            assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, randomExistingUserIds, perm, project.getId())).isEmpty();
        });
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, randomExistingUserIds, randomPermission, project.getId())).isEmpty();
    }

    @Test
    public void keepAuthorizedUsersForRoleAndProject_returns_user_if_granted_project_permission_directly_on_private_project() {
        ComponentDto project = db.components().insertPrivateProject(organization);
        ComponentDto otherProject = db.components().insertPublicProject(organization);
        UserDto otherUser = db.users().insertUser();
        db.users().insertProjectPermissionOnUser(user, randomPermission, project);
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), randomPermission, project.getId())).containsOnly(user.getId());
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), "another perm", project.getId())).isEmpty();
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(otherUser.getId()), randomPermission, project.getId())).isEmpty();
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), randomPermission, otherProject.getId())).isEmpty();
    }

    @Test
    public void keepAuthorizedUsersForRoleAndProject_returns_user_if_granted_project_permission_by_group_on_private_project() {
        ComponentDto project = db.components().insertPrivateProject(organization);
        ComponentDto otherProject = db.components().insertPublicProject(organization);
        UserDto otherUser = db.users().insertUser();
        db.users().insertMember(group1, user);
        db.users().insertProjectPermissionOnGroup(group1, randomPermission, project);
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), randomPermission, project.getId())).containsOnly(user.getId());
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), "another perm", project.getId())).isEmpty();
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(user.getId()), randomPermission, otherProject.getId())).isEmpty();
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Collections.singleton(otherUser.getId()), randomPermission, project.getId())).isEmpty();
    }

    @Test
    public void keep_authorized_users_returns_empty_list_for_role_and_project_for_anonymous() {
        ComponentDto project1 = db.components().insertPrivateProject(organization);
        ComponentDto project2 = db.components().insertPrivateProject(organization);
        ComponentDto project3 = db.components().insertPrivateProject(organization);
        UserDto user1 = db.users().insertUser("u1");
        UserDto user2 = db.users().insertUser("u2");
        UserDto user3 = db.users().insertUser("u3");
        GroupDto group1 = db.users().insertGroup(organization);
        GroupDto group2 = db.users().insertGroup(organization);
        db.users().insertMembers(group1, user1, user2);
        db.users().insertMembers(group2, user3);
        db.users().insertProjectPermissionOnUser(user1, USER, project1);
        db.users().insertProjectPermissionOnUser(user2, USER, project1);
        db.users().insertProjectPermissionOnUser(user3, USER, project1);
        db.users().insertProjectPermissionOnGroup(group2, USER, project3);
        assertThat(// Only 100 and 101 has 'user' role on project
        underTest.keepAuthorizedUsersForRoleAndProject(dbSession, Sets.newHashSet(100, 101, 102), "user", AuthorizationDaoTest.PROJECT_ID)).isEmpty();
    }

    @Test
    public void keepAuthorizedUsersForRoleAndProject_should_be_able_to_handle_lots_of_users() {
        List<UserDto> users = IntStream.range(0, 2000).mapToObj(( i) -> db.users().insertUser()).collect(Collectors.toList());
        assertThat(underTest.keepAuthorizedUsersForRoleAndProject(dbSession, users.stream().map(UserDto::getId).collect(Collectors.toSet()), "user", AuthorizationDaoTest.PROJECT_ID)).isEmpty();
    }

    @Test
    public void countUsersWithGlobalPermissionExcludingGroupMember() {
        // u1 has the direct permission, u2 and u3 have the permission through their group
        UserDto u1 = db.users().insertUser();
        db.users().insertPermissionOnUser(organization, u1, AuthorizationDaoTest.A_PERMISSION);
        db.users().insertPermissionOnGroup(group1, AuthorizationDaoTest.A_PERMISSION);
        db.users().insertPermissionOnGroup(group1, "another-permission");
        UserDto u2 = db.users().insertUser();
        db.users().insertMember(group1, u2);
        UserDto u3 = db.users().insertUser();
        db.users().insertMember(group1, u3);
        // excluding u2 membership --> remain u1 and u3
        int count = underTest.countUsersWithGlobalPermissionExcludingGroupMember(dbSession, organization.getUuid(), AuthorizationDaoTest.A_PERMISSION, group1.getId(), u2.getId());
        assertThat(count).isEqualTo(2);
        // excluding unknown memberships
        count = underTest.countUsersWithGlobalPermissionExcludingGroupMember(dbSession, organization.getUuid(), AuthorizationDaoTest.A_PERMISSION, group1.getId(), AuthorizationDaoTest.MISSING_ID);
        assertThat(count).isEqualTo(3);
        count = underTest.countUsersWithGlobalPermissionExcludingGroupMember(dbSession, organization.getUuid(), AuthorizationDaoTest.A_PERMISSION, AuthorizationDaoTest.MISSING_ID, u2.getId());
        assertThat(count).isEqualTo(3);
        // another organization
        count = underTest.countUsersWithGlobalPermissionExcludingGroupMember(dbSession, AuthorizationDaoTest.DOES_NOT_EXIST, AuthorizationDaoTest.A_PERMISSION, group1.getId(), u2.getId());
        assertThat(count).isEqualTo(0);
        // another permission
        count = underTest.countUsersWithGlobalPermissionExcludingGroupMember(dbSession, organization.getUuid(), AuthorizationDaoTest.DOES_NOT_EXIST, group1.getId(), u2.getId());
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void countUsersWithGlobalPermissionExcludingUserPermission() {
        // u1 and u2 have the direct permission, u3 has the permission through his group
        UserDto u1 = db.users().insertUser();
        db.users().insertPermissionOnUser(organization, u1, AuthorizationDaoTest.A_PERMISSION);
        UserDto u2 = db.users().insertUser();
        db.users().insertPermissionOnUser(organization, u2, AuthorizationDaoTest.A_PERMISSION);
        db.users().insertPermissionOnGroup(group1, AuthorizationDaoTest.A_PERMISSION);
        UserDto u3 = db.users().insertUser();
        db.users().insertMember(group1, u3);
        // excluding u2 permission --> remain u1 and u3
        int count = underTest.countUsersWithGlobalPermissionExcludingUserPermission(dbSession, organization.getUuid(), AuthorizationDaoTest.A_PERMISSION, u2.getId());
        assertThat(count).isEqualTo(2);
        // excluding unknown user
        count = underTest.countUsersWithGlobalPermissionExcludingUserPermission(dbSession, organization.getUuid(), AuthorizationDaoTest.A_PERMISSION, AuthorizationDaoTest.MISSING_ID);
        assertThat(count).isEqualTo(3);
        // another organization
        count = underTest.countUsersWithGlobalPermissionExcludingUserPermission(dbSession, AuthorizationDaoTest.DOES_NOT_EXIST, AuthorizationDaoTest.A_PERMISSION, u2.getId());
        assertThat(count).isEqualTo(0);
        // another permission
        count = underTest.countUsersWithGlobalPermissionExcludingUserPermission(dbSession, organization.getUuid(), AuthorizationDaoTest.DOES_NOT_EXIST, u2.getId());
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void selectOrganizationUuidsOfUserWithGlobalPermission_returns_empty_set_if_user_does_not_exist() {
        // another user
        db.users().insertPermissionOnUser(user, OrganizationPermission.ADMINISTER_QUALITY_GATES);
        Set<String> orgUuids = underTest.selectOrganizationUuidsOfUserWithGlobalPermission(dbSession, AuthorizationDaoTest.MISSING_ID, SYSTEM_ADMIN);
        assertThat(orgUuids).isEmpty();
    }

    @Test
    public void selectOrganizationUuidsOfUserWithGlobalPermission_returns_empty_set_if_user_does_not_have_permission_at_all() {
        db.users().insertPermissionOnUser(user, OrganizationPermission.ADMINISTER_QUALITY_GATES);
        // user is not part of this group
        db.users().insertPermissionOnGroup(group1, OrganizationPermission.SCAN);
        Set<String> orgUuids = underTest.selectOrganizationUuidsOfUserWithGlobalPermission(dbSession, user.getId(), OrganizationPermission.SCAN.getKey());
        assertThat(orgUuids).isEmpty();
    }

    @Test
    public void selectOrganizationUuidsOfUserWithGlobalPermission_returns_organizations_on_which_user_has_permission() {
        db.users().insertPermissionOnGroup(group1, OrganizationPermission.SCAN);
        db.users().insertPermissionOnGroup(group2, QUALITY_GATE_ADMIN);
        db.users().insertMember(group1, user);
        db.users().insertMember(group2, user);
        Set<String> orgUuids = underTest.selectOrganizationUuidsOfUserWithGlobalPermission(dbSession, user.getId(), OrganizationPermission.SCAN.getKey());
        assertThat(orgUuids).containsExactly(group1.getOrganizationUuid());
    }

    @Test
    public void selectOrganizationUuidsOfUserWithGlobalPermission_handles_user_permissions_and_group_permissions() {
        // organization: through group membership
        db.users().insertPermissionOnGroup(group1, OrganizationPermission.SCAN);
        db.users().insertMember(group1, user);
        // org2 : direct user permission
        OrganizationDto org2 = db.organizations().insert();
        db.users().insertPermissionOnUser(org2, user, OrganizationPermission.SCAN);
        // org3 : another permission QUALITY_GATE_ADMIN
        OrganizationDto org3 = db.organizations().insert();
        db.users().insertPermissionOnUser(org3, user, QUALITY_GATE_ADMIN);
        // exclude project permission
        db.users().insertProjectPermissionOnUser(user, ADMIN, db.components().insertPrivateProject());
        Set<String> orgUuids = underTest.selectOrganizationUuidsOfUserWithGlobalPermission(dbSession, user.getId(), OrganizationPermission.SCAN.getKey());
        assertThat(orgUuids).containsOnly(organization.getUuid(), org2.getUuid());
    }

    @Test
    public void selectOrganizationUuidsOfUserWithGlobalPermission_ignores_anonymous_permissions() {
        db.users().insertPermissionOnAnyone(organization, OrganizationPermission.SCAN);
        db.users().insertPermissionOnUser(organization, user, OrganizationPermission.ADMINISTER_QUALITY_GATES);
        Set<String> orgUuids = underTest.selectOrganizationUuidsOfUserWithGlobalPermission(dbSession, user.getId(), OrganizationPermission.SCAN.getKey());
        assertThat(orgUuids).isEmpty();
    }

    @Test
    public void selectProjectPermissionsOfAnonymous_returns_permissions_of_anonymous_user_on_specified_public_project() {
        ComponentDto project = db.components().insertPublicProject(organization);
        db.users().insertProjectPermissionOnAnyone("p1", project);
        db.users().insertProjectPermissionOnUser(db.users().insertUser(), "p2", project);
        ComponentDto otherProject = db.components().insertPublicProject();
        db.users().insertProjectPermissionOnAnyone("p3", otherProject);
        assertThat(underTest.selectProjectPermissionsOfAnonymous(dbSession, project.uuid())).containsOnly("p1");
    }

    @Test
    public void selectProjectPermissionsOfAnonymous_returns_empty_set_when_project_does_not_exist() {
        assertThat(underTest.selectProjectPermissionsOfAnonymous(dbSession, "does_not_exist")).isEmpty();
    }

    @Test
    public void selectProjectPermissions_returns_empty_set_when_logged_in_user_and_project_does_not_exist() {
        assertThat(underTest.selectProjectPermissions(dbSession, "does_not_exist", user.getId())).isEmpty();
    }

    @Test
    public void selectProjectPermissions_returns_permissions_of_logged_in_user_on_specified_public_project_through_anonymous_permissions() {
        ComponentDto project = db.components().insertPublicProject(organization);
        db.users().insertProjectPermissionOnAnyone("p1", project);
        db.users().insertProjectPermissionOnAnyone("p2", project);
        assertThat(underTest.selectProjectPermissions(dbSession, project.uuid(), user.getId())).containsOnly("p1", "p2");
    }

    @Test
    public void selectProjectPermissions_returns_permissions_of_logged_in_user_on_specified_project() {
        ComponentDto project = db.components().insertPrivateProject(organization);
        db.users().insertProjectPermissionOnUser(user, CODEVIEWER, project);
        db.users().insertProjectPermissionOnUser(db.users().insertUser(), ISSUE_ADMIN, project);
        assertThat(underTest.selectProjectPermissions(dbSession, project.uuid(), user.getId())).containsOnly(CODEVIEWER);
    }

    @Test
    public void selectProjectPermissions_returns_permissions_of_logged_in_user_on_specified_project_through_group_membership() {
        ComponentDto project = db.components().insertPrivateProject(organization);
        db.users().insertProjectPermissionOnGroup(group1, CODEVIEWER, project);
        db.users().insertProjectPermissionOnGroup(group2, ISSUE_ADMIN, project);
        db.users().insertMember(group1, user);
        assertThat(underTest.selectProjectPermissions(dbSession, project.uuid(), user.getId())).containsOnly(CODEVIEWER);
    }

    @Test
    public void selectProjectPermissions_returns_permissions_of_logged_in_user_on_specified_private_project_through_all_possible_configurations() {
        ComponentDto project = db.components().insertPrivateProject(organization);
        db.users().insertProjectPermissionOnUser(user, CODEVIEWER, project);
        db.users().insertProjectPermissionOnGroup(group1, USER, project);
        db.users().insertMember(group1, user);
        assertThat(underTest.selectProjectPermissions(dbSession, project.uuid(), user.getId())).containsOnly(CODEVIEWER, USER);
    }

    @Test
    public void selectProjectPermissions_returns_permissions_of_logged_in_user_on_specified_public_project_through_all_possible_configurations() {
        ComponentDto project = db.components().insertPublicProject(organization);
        db.users().insertProjectPermissionOnUser(user, "p1", project);
        db.users().insertProjectPermissionOnAnyone("p2", project);
        db.users().insertProjectPermissionOnGroup(group1, "p3", project);
        db.users().insertMember(group1, user);
        assertThat(underTest.selectProjectPermissions(dbSession, project.uuid(), user.getId())).containsOnly("p1", "p2", "p3");
    }

    @Test
    public void keepAuthorizedProjectUuids_filters_projects_authorized_to_logged_in_user_by_direct_permission() {
        ComponentDto privateProject = db.components().insertPrivateProject(organization);
        ComponentDto publicProject = db.components().insertPublicProject(organization);
        UserDto user = db.users().insertUser();
        db.users().insertProjectPermissionOnUser(user, ADMIN, privateProject);
        assertThat(underTest.keepAuthorizedProjectUuids(dbSession, Sets.newHashSet(privateProject.uuid(), publicProject.uuid()), user.getId(), ADMIN)).containsOnly(privateProject.uuid());
        // user does not have the permission "issueadmin"
        assertThat(underTest.keepAuthorizedProjectUuids(dbSession, Sets.newHashSet(privateProject.uuid(), publicProject.uuid()), user.getId(), ISSUE_ADMIN)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectUuids_filters_projects_authorized_to_logged_in_user_by_group_permission() {
        ComponentDto privateProject = db.components().insertPrivateProject(organization);
        ComponentDto publicProject = db.components().insertPublicProject(organization);
        UserDto user = db.users().insertUser();
        GroupDto group = db.users().insertGroup(organization);
        db.users().insertMember(group, user);
        db.users().insertProjectPermissionOnGroup(group, ADMIN, privateProject);
        assertThat(underTest.keepAuthorizedProjectUuids(dbSession, Sets.newHashSet(privateProject.uuid(), publicProject.uuid()), user.getId(), ADMIN)).containsOnly(privateProject.uuid());
        // user does not have the permission "issueadmin"
        assertThat(underTest.keepAuthorizedProjectUuids(dbSession, Sets.newHashSet(privateProject.uuid(), publicProject.uuid()), user.getId(), ISSUE_ADMIN)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectUuids_returns_empty_list_if_input_is_empty() {
        ComponentDto publicProject = db.components().insertPublicProject(organization);
        UserDto user = db.users().insertUser();
        assertThat(underTest.keepAuthorizedProjectUuids(dbSession, Collections.emptySet(), user.getId(), USER)).isEmpty();
        // projects do not exist
        assertThat(underTest.keepAuthorizedProjectUuids(dbSession, Sets.newHashSet("does_not_exist"), user.getId(), USER)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectUuids_returns_empty_list_if_input_does_not_reference_existing_projects() {
        ComponentDto publicProject = db.components().insertPublicProject(organization);
        UserDto user = db.users().insertUser();
        assertThat(underTest.keepAuthorizedProjectUuids(dbSession, Sets.newHashSet("does_not_exist"), user.getId(), USER)).isEmpty();
    }

    @Test
    public void keepAuthorizedProjectUuids_returns_public_projects_if_permission_USER_or_CODEVIEWER() {
        ComponentDto publicProject = db.components().insertPublicProject(organization);
        UserDto user = db.users().insertUser();
        // logged-in user
        assertThat(underTest.keepAuthorizedProjectUuids(dbSession, Sets.newHashSet(publicProject.uuid()), user.getId(), CODEVIEWER)).containsOnly(publicProject.uuid());
        assertThat(underTest.keepAuthorizedProjectUuids(dbSession, Sets.newHashSet(publicProject.uuid()), user.getId(), USER)).containsOnly(publicProject.uuid());
        assertThat(underTest.keepAuthorizedProjectUuids(dbSession, Sets.newHashSet(publicProject.uuid()), user.getId(), ADMIN)).isEmpty();
        // anonymous
        assertThat(underTest.keepAuthorizedProjectUuids(dbSession, Sets.newHashSet(publicProject.uuid()), null, CODEVIEWER)).containsOnly(publicProject.uuid());
        assertThat(underTest.keepAuthorizedProjectUuids(dbSession, Sets.newHashSet(publicProject.uuid()), null, USER)).containsOnly(publicProject.uuid());
        assertThat(underTest.keepAuthorizedProjectUuids(dbSession, Sets.newHashSet(publicProject.uuid()), null, ADMIN)).isEmpty();
    }

    @Test
    public void selectQualityProfileAdministratorLogins_return_users_with_quality_profile_administrator_permission() {
        OrganizationDto organization1 = db.organizations().insert();
        UserDto user1 = db.users().insertUser();
        db.users().insertPermissionOnUser(organization1, user1, OrganizationPermission.ADMINISTER_QUALITY_PROFILES);
        OrganizationDto organization2 = db.organizations().insert();
        UserDto user2 = db.users().insertUser();
        db.users().insertPermissionOnUser(organization2, user2, OrganizationPermission.ADMINISTER_QUALITY_PROFILES);
        List<String> logins = underTest.selectQualityProfileAdministratorLogins(dbSession);
        assertThat(logins).containsExactlyInAnyOrder(user1.getLogin(), user2.getLogin());
    }

    @Test
    public void selectQualityProfileAdministratorLogins_return_users_within_quality_profile_administrator_group() {
        OrganizationDto organization1 = db.organizations().insert();
        GroupDto qualityProfileAdministratorGroup1 = db.users().insertGroup(organization1);
        db.users().insertPermissionOnGroup(qualityProfileAdministratorGroup1, OrganizationPermission.ADMINISTER_QUALITY_PROFILES);
        UserDto user1 = db.users().insertUser();
        db.users().insertMember(qualityProfileAdministratorGroup1, user1);
        OrganizationDto organization2 = db.organizations().insert();
        GroupDto qualityProfileAdministratorGroup2 = db.users().insertGroup(organization2);
        db.users().insertPermissionOnGroup(qualityProfileAdministratorGroup2, OrganizationPermission.ADMINISTER_QUALITY_PROFILES);
        UserDto user2 = db.users().insertUser();
        db.users().insertMember(qualityProfileAdministratorGroup2, user2);
        List<String> logins = underTest.selectQualityProfileAdministratorLogins(dbSession);
        assertThat(logins).containsExactlyInAnyOrder(user1.getLogin(), user2.getLogin());
    }

    @Test
    public void selectQualityProfileAdministratorLogins_does_not_return_non_quality_profile_administrator_logins() {
        OrganizationDto organization1 = db.organizations().insert();
        UserDto user1 = db.users().insertUser();
        db.users().insertPermissionOnUser(organization1, user1, OrganizationPermission.ADMINISTER);
        db.users().insertUser();
        List<String> logins = underTest.selectQualityProfileAdministratorLogins(dbSession);
        assertThat(logins).isEmpty();
    }

    @Test
    public void selectGlobalAdministratorLogins() {
        OrganizationDto organization1 = db.organizations().insert();
        UserDto user1 = db.users().insertUser();
        db.users().insertPermissionOnUser(organization1, user1, OrganizationPermission.ADMINISTER);
        OrganizationDto organization2 = db.organizations().insert();
        UserDto user2 = db.users().insertUser();
        db.users().insertPermissionOnUser(organization2, user2, OrganizationPermission.ADMINISTER);
        GroupDto administratorGroup2 = db.users().insertGroup(organization2);
        db.users().insertPermissionOnGroup(administratorGroup2, OrganizationPermission.ADMINISTER);
        UserDto user3 = db.users().insertUser();
        db.users().insertMember(administratorGroup2, user3);
        ComponentDto project = db.components().insertPrivateProject();
        UserDto user4 = db.users().insertUser();
        db.users().insertPermissionOnUser(organization1, user4, OrganizationPermission.ADMINISTER_QUALITY_PROFILES);
        db.users().insertProjectPermissionOnUser(user4, "admin", project);
        db.users().insertUser();
        List<String> logins = underTest.selectGlobalAdministratorLogins(dbSession);
        assertThat(logins).containsExactlyInAnyOrder(user1.getLogin(), user2.getLogin(), user3.getLogin());
    }

    @Test
    public void keepAuthorizedLoginsOnProject_return_correct_users_on_public_project() {
        ComponentDto project = db.components().insertPublicProject(organization);
        UserDto user1 = db.users().insertUser();
        // admin with "direct" ADMIN role
        UserDto admin1 = db.users().insertUser();
        db.users().insertProjectPermissionOnUser(admin1, ADMIN, project);
        // admin2 with ADMIN role through group
        UserDto admin2 = db.users().insertUser();
        GroupDto adminGroup = db.users().insertGroup(organization, "ADMIN");
        db.users().insertMember(adminGroup, admin2);
        db.users().insertProjectPermissionOnGroup(adminGroup, ADMIN, project);
        assertThat(underTest.keepAuthorizedLoginsOnProject(dbSession, Sets.newHashSet(user1.getLogin()), project.getKey(), USER)).containsOnly(user1.getLogin());
        assertThat(underTest.keepAuthorizedLoginsOnProject(dbSession, Sets.newHashSet(user1.getLogin(), admin1.getLogin(), admin2.getLogin()), project.getKey(), USER)).containsOnly(user1.getLogin(), admin1.getLogin(), admin2.getLogin());
        assertThat(underTest.keepAuthorizedLoginsOnProject(dbSession, Sets.newHashSet(user1.getLogin(), admin1.getLogin(), admin2.getLogin()), project.getKey(), ADMIN)).containsOnly(admin1.getLogin(), admin2.getLogin());
    }

    @Test
    public void keepAuthorizedLoginsOnProject_return_correct_users_on_private_project() {
        ComponentDto project = db.components().insertPrivateProject(organization);
        GroupDto userGroup = db.users().insertGroup(organization, "USERS");
        GroupDto adminGroup = db.users().insertGroup(organization, "ADMIN");
        db.users().insertProjectPermissionOnGroup(userGroup, USER, project);
        db.users().insertProjectPermissionOnGroup(adminGroup, ADMIN, project);
        // admin with "direct" ADMIN role
        UserDto admin1 = db.users().insertUser();
        db.users().insertProjectPermissionOnUser(admin1, ADMIN, project);
        // admin2 with ADMIN role through group
        UserDto admin2 = db.users().insertUser();
        db.users().insertMember(adminGroup, admin2);
        // user1 with "direct" USER role
        UserDto user1 = db.users().insertUser();
        db.users().insertProjectPermissionOnUser(user1, USER, project);
        // user2 with USER role through group
        UserDto user2 = db.users().insertUser();
        db.users().insertMember(userGroup, user2);
        // user without role
        UserDto userWithNoRole = db.users().insertUser();
        assertThat(underTest.keepAuthorizedLoginsOnProject(dbSession, Sets.newHashSet(userWithNoRole.getLogin()), project.getKey(), USER)).isEmpty();
        assertThat(underTest.keepAuthorizedLoginsOnProject(dbSession, Sets.newHashSet(user1.getLogin()), project.getKey(), USER)).containsOnly(user1.getLogin());
        Set<String> allLogins = Sets.newHashSet(admin1.getLogin(), admin2.getLogin(), user1.getLogin(), user2.getLogin(), userWithNoRole.getLogin());
        // Admin does not have the USER permission set
        assertThat(underTest.keepAuthorizedLoginsOnProject(dbSession, allLogins, project.getKey(), USER)).containsOnly(user1.getLogin(), user2.getLogin());
        assertThat(underTest.keepAuthorizedLoginsOnProject(dbSession, allLogins, project.getKey(), ADMIN)).containsOnly(admin1.getLogin(), admin2.getLogin());
    }

    @Test
    public void keepAuthorizedLoginsOnProject_return_correct_users_on_branch() {
        ComponentDto project = db.components().insertPrivateProject(organization);
        ComponentDto branch = db.components().insertProjectBranch(project, ( c) -> c.setBranchType((random.nextBoolean() ? BranchType.SHORT : BranchType.LONG)));
        GroupDto userGroup = db.users().insertGroup(organization, "USERS");
        GroupDto adminGroup = db.users().insertGroup(organization, "ADMIN");
        db.users().insertProjectPermissionOnGroup(userGroup, USER, project);
        db.users().insertProjectPermissionOnGroup(adminGroup, ADMIN, project);
        // admin with "direct" ADMIN role
        UserDto admin1 = db.users().insertUser();
        db.users().insertProjectPermissionOnUser(admin1, ADMIN, project);
        // admin2 with ADMIN role through group
        UserDto admin2 = db.users().insertUser();
        db.users().insertMember(adminGroup, admin2);
        // user1 with "direct" USER role
        UserDto user1 = db.users().insertUser();
        db.users().insertProjectPermissionOnUser(user1, USER, project);
        // user2 with USER role through group
        UserDto user2 = db.users().insertUser();
        db.users().insertMember(userGroup, user2);
        // user without role
        UserDto userWithNoRole = db.users().insertUser();
        assertThat(underTest.keepAuthorizedLoginsOnProject(dbSession, Sets.newHashSet(userWithNoRole.getLogin()), branch.getKey(), USER)).isEmpty();
        assertThat(underTest.keepAuthorizedLoginsOnProject(dbSession, Sets.newHashSet(user1.getLogin()), branch.getKey(), USER)).containsOnly(user1.getLogin());
        Set<String> allLogins = Sets.newHashSet(admin1.getLogin(), admin2.getLogin(), user1.getLogin(), user2.getLogin(), userWithNoRole.getLogin());
        // Admin does not have the USER permission set
        assertThat(underTest.keepAuthorizedLoginsOnProject(dbSession, allLogins, branch.getKey(), USER)).containsOnly(user1.getLogin(), user2.getLogin());
        assertThat(underTest.keepAuthorizedLoginsOnProject(dbSession, allLogins, branch.getKey(), ADMIN)).containsOnly(admin1.getLogin(), admin2.getLogin());
    }
}

