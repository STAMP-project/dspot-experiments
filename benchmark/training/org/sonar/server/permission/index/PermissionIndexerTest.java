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
package org.sonar.server.permission.index;


import ProjectIndexer.Cause.PROJECT_CREATION;
import ProjectIndexer.Cause.PROJECT_DELETION;
import ProjectIndexer.Cause.PROJECT_TAGS_UPDATE;
import System2.INSTANCE;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.web.UserRole;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.es.EsQueueDto;
import org.sonar.db.user.GroupDto;
import org.sonar.db.user.UserDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.IndexType;
import org.sonar.server.es.IndexingResult;
import org.sonar.server.tester.UserSessionRule;


public class PermissionIndexerTest {
    private static final IndexType INDEX_TYPE_FOO_AUTH = new IndexType(FooIndexDefinition.INDEX_TYPE_FOO.getIndex(), IndexAuthorizationConstants.TYPE_AUTHORIZATION);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    @Rule
    public EsTester es = EsTester.createCustom(new FooIndexDefinition());

    @Rule
    public UserSessionRule userSession = UserSessionRule.standalone();

    private FooIndex fooIndex = new FooIndex(es.client(), new WebAuthorizationTypeSupport(userSession));

    private FooIndexer fooIndexer = new FooIndexer(es.client());

    private PermissionIndexer underTest = new PermissionIndexer(db.getDbClient(), es.client(), fooIndexer);

    @Test
    public void indexOnStartup_grants_access_to_any_user_and_to_group_Anyone_on_public_projects() {
        ComponentDto project = createAndIndexPublicProject();
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        indexOnStartup();
        verifyAnyoneAuthorized(project);
        verifyAuthorized(project, user1);
        verifyAuthorized(project, user2);
    }

    @Test
    public void deletion_resilience_will_deindex_projects() {
        ComponentDto project1 = createUnindexedPublicProject();
        ComponentDto project2 = createUnindexedPublicProject();
        // UserDto user1 = db.users().insertUser();
        indexOnStartup();
        assertThat(es.countDocuments(PermissionIndexerTest.INDEX_TYPE_FOO_AUTH)).isEqualTo(2);
        // Simulate a indexation issue
        db.getDbClient().componentDao().delete(db.getSession(), project1.getId());
        underTest.prepareForRecovery(db.getSession(), Arrays.asList(project1.uuid()), PROJECT_DELETION);
        assertThat(db.countRowsOfTable(db.getSession(), "es_queue")).isEqualTo(1);
        Collection<EsQueueDto> esQueueDtos = db.getDbClient().esQueueDao().selectForRecovery(db.getSession(), Long.MAX_VALUE, 2);
        underTest.index(db.getSession(), esQueueDtos);
        assertThat(db.countRowsOfTable(db.getSession(), "es_queue")).isEqualTo(0);
        assertThat(es.countDocuments(PermissionIndexerTest.INDEX_TYPE_FOO_AUTH)).isEqualTo(1);
    }

    @Test
    public void indexOnStartup_grants_access_to_user() {
        ComponentDto project = createAndIndexPrivateProject();
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        db.users().insertProjectPermissionOnUser(user1, UserRole.USER, project);
        db.users().insertProjectPermissionOnUser(user2, UserRole.ADMIN, project);
        indexOnStartup();
        // anonymous
        verifyAnyoneNotAuthorized(project);
        // user1 has access
        verifyAuthorized(project, user1);
        // user2 has not access (only USER permission is accepted)
        verifyNotAuthorized(project, user2);
    }

    @Test
    public void indexOnStartup_grants_access_to_group_on_private_project() {
        ComponentDto project = createAndIndexPrivateProject();
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        UserDto user3 = db.users().insertUser();
        GroupDto group1 = db.users().insertGroup();
        GroupDto group2 = db.users().insertGroup();
        db.users().insertProjectPermissionOnGroup(group1, UserRole.USER, project);
        db.users().insertProjectPermissionOnGroup(group2, UserRole.ADMIN, project);
        indexOnStartup();
        // anonymous
        verifyAnyoneNotAuthorized(project);
        // group1 has access
        verifyAuthorized(project, user1, group1);
        // group2 has not access (only USER permission is accepted)
        verifyNotAuthorized(project, user2, group2);
        // user3 is not in any group
        verifyNotAuthorized(project, user3);
    }

    @Test
    public void indexOnStartup_grants_access_to_user_and_group() {
        ComponentDto project = createAndIndexPrivateProject();
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        GroupDto group = db.users().insertGroup();
        db.users().insertMember(group, user2);
        db.users().insertProjectPermissionOnUser(user1, UserRole.USER, project);
        db.users().insertProjectPermissionOnGroup(group, UserRole.USER, project);
        indexOnStartup();
        // anonymous
        verifyAnyoneNotAuthorized(project);
        // has direct access
        verifyAuthorized(project, user1);
        // has access through group
        verifyAuthorized(project, user1, group);
        // no access
        verifyNotAuthorized(project, user2);
    }

    @Test
    public void indexOnStartup_does_not_grant_access_to_anybody_on_private_project() {
        ComponentDto project = createAndIndexPrivateProject();
        UserDto user = db.users().insertUser();
        GroupDto group = db.users().insertGroup();
        indexOnStartup();
        verifyAnyoneNotAuthorized(project);
        verifyNotAuthorized(project, user);
        verifyNotAuthorized(project, user, group);
    }

    @Test
    public void indexOnStartup_grants_access_to_anybody_on_public_project() {
        ComponentDto project = createAndIndexPublicProject();
        UserDto user = db.users().insertUser();
        GroupDto group = db.users().insertGroup();
        indexOnStartup();
        verifyAnyoneAuthorized(project);
        verifyAuthorized(project, user);
        verifyAuthorized(project, user, group);
    }

    @Test
    public void indexOnStartup_grants_access_to_anybody_on_view() {
        ComponentDto view = createAndIndexView();
        UserDto user = db.users().insertUser();
        GroupDto group = db.users().insertGroup();
        indexOnStartup();
        verifyAnyoneAuthorized(view);
        verifyAuthorized(view, user);
        verifyAuthorized(view, user, group);
    }

    @Test
    public void indexOnStartup_grants_access_on_many_projects() {
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        ComponentDto project = null;
        for (int i = 0; i < 10; i++) {
            project = createAndIndexPrivateProject();
            db.users().insertProjectPermissionOnUser(user1, UserRole.USER, project);
        }
        indexOnStartup();
        verifyAnyoneNotAuthorized(project);
        verifyAuthorized(project, user1);
        verifyNotAuthorized(project, user2);
    }

    @Test
    public void public_projects_are_visible_to_anybody_whatever_the_organization() {
        ComponentDto projectOnOrg1 = createAndIndexPublicProject(db.organizations().insert());
        ComponentDto projectOnOrg2 = createAndIndexPublicProject(db.organizations().insert());
        UserDto user = db.users().insertUser();
        indexOnStartup();
        verifyAnyoneAuthorized(projectOnOrg1);
        verifyAnyoneAuthorized(projectOnOrg2);
        verifyAuthorized(projectOnOrg1, user);
        verifyAuthorized(projectOnOrg2, user);
    }

    @Test
    public void indexOnAnalysis_does_nothing_because_CE_does_not_touch_permissions() {
        ComponentDto project = createAndIndexPublicProject();
        underTest.indexOnAnalysis(project.uuid());
        assertThatAuthIndexHasSize(0);
        verifyAnyoneNotAuthorized(project);
    }

    @Test
    public void permissions_are_not_updated_on_project_tags_update() {
        ComponentDto project = createAndIndexPublicProject();
        indexPermissions(project, PROJECT_TAGS_UPDATE);
        assertThatAuthIndexHasSize(0);
        verifyAnyoneNotAuthorized(project);
    }

    @Test
    public void permissions_are_not_updated_on_project_key_update() {
        ComponentDto project = createAndIndexPublicProject();
        indexPermissions(project, PROJECT_TAGS_UPDATE);
        assertThatAuthIndexHasSize(0);
        verifyAnyoneNotAuthorized(project);
    }

    @Test
    public void index_permissions_on_project_creation() {
        ComponentDto project = createAndIndexPrivateProject();
        UserDto user = db.users().insertUser();
        db.users().insertProjectPermissionOnUser(user, UserRole.USER, project);
        indexPermissions(project, PROJECT_CREATION);
        assertThatAuthIndexHasSize(1);
        verifyAuthorized(project, user);
    }

    @Test
    public void index_permissions_on_permission_change() {
        ComponentDto project = createAndIndexPrivateProject();
        UserDto user1 = db.users().insertUser();
        UserDto user2 = db.users().insertUser();
        db.users().insertProjectPermissionOnUser(user1, UserRole.USER, project);
        indexPermissions(project, PROJECT_CREATION);
        verifyAuthorized(project, user1);
        verifyNotAuthorized(project, user2);
        db.users().insertProjectPermissionOnUser(user2, UserRole.USER, project);
        indexPermissions(project, PERMISSION_CHANGE);
        verifyAuthorized(project, user1);
        verifyAuthorized(project, user1);
    }

    @Test
    public void delete_permissions_on_project_deletion() {
        ComponentDto project = createAndIndexPrivateProject();
        UserDto user = db.users().insertUser();
        db.users().insertProjectPermissionOnUser(user, UserRole.USER, project);
        indexPermissions(project, PROJECT_CREATION);
        verifyAuthorized(project, user);
        db.getDbClient().componentDao().delete(db.getSession(), project.getId());
        indexPermissions(project, PROJECT_DELETION);
        verifyNotAuthorized(project, user);
        assertThatAuthIndexHasSize(0);
    }

    @Test
    public void errors_during_indexing_are_recovered() {
        ComponentDto project = createAndIndexPublicProject();
        es.lockWrites(PermissionIndexerTest.INDEX_TYPE_FOO_AUTH);
        IndexingResult result = indexPermissions(project, PERMISSION_CHANGE);
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getFailures()).isEqualTo(1L);
        // index is still read-only, fail to recover
        result = recover();
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getFailures()).isEqualTo(1L);
        assertThatAuthIndexHasSize(0);
        assertThatEsQueueTableHasSize(1);
        es.unlockWrites(PermissionIndexerTest.INDEX_TYPE_FOO_AUTH);
        result = recover();
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getFailures()).isEqualTo(0L);
        verifyAnyoneAuthorized(project);
        assertThatEsQueueTableHasSize(0);
    }
}

