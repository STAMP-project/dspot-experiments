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
package org.sonar.server.component.index;


import ProjectIndexer.Cause.PERMISSION_CHANGE;
import ProjectIndexer.Cause.PROJECT_KEY_UPDATE;
import ProjectIndexer.Cause.PROJECT_TAGS_UPDATE;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.System2;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.IndexingResult;


public class ComponentIndexerTest {
    private System2 system2 = System2.INSTANCE;

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public DbTester db = DbTester.create(system2);

    private DbClient dbClient = db.getDbClient();

    private DbSession dbSession = db.getSession();

    private ComponentIndexer underTest = new ComponentIndexer(db.getDbClient(), es.client());

    @Test
    public void test_getIndexTypes() {
        assertThat(underTest.getIndexTypes()).containsExactly(ComponentIndexDefinition.INDEX_TYPE_COMPONENT);
    }

    @Test
    public void indexOnStartup_does_nothing_if_no_projects() {
        underTest.indexOnStartup(Collections.emptySet());
        assertThatIndexHasSize(0);
    }

    @Test
    public void indexOnStartup_indexes_all_components() {
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject();
        underTest.indexOnStartup(Collections.emptySet());
        assertThatIndexContainsOnly(project1, project2);
    }

    @Test
    public void map_fields() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPrivateProject(organization, ( p) -> p.setLanguage("java"));
        underTest.indexOnStartup(Collections.emptySet());
        assertThatIndexContainsOnly(project);
        ComponentDoc doc = es.getDocuments(ComponentIndexDefinition.INDEX_TYPE_COMPONENT, ComponentDoc.class).get(0);
        assertThat(doc.getId()).isEqualTo(project.uuid());
        assertThat(doc.getKey()).isEqualTo(project.getDbKey());
        assertThat(doc.getProjectUuid()).isEqualTo(project.projectUuid());
        assertThat(doc.getName()).isEqualTo(project.name());
        assertThat(doc.getLanguage()).isEqualTo(project.language());
        assertThat(doc.getOrganization()).isEqualTo(project.getOrganizationUuid());
    }

    @Test
    public void indexOnStartup_does_not_index_non_main_branches() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setKey("feature/foo"));
        underTest.indexOnStartup(Collections.emptySet());
        assertThatIndexContainsOnly(project);
    }

    @Test
    public void indexOnAnalysis_indexes_project() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        underTest.indexOnAnalysis(project.uuid());
        assertThatIndexContainsOnly(project, file);
    }

    @Test
    public void indexOnAnalysis_indexes_new_components() {
        ComponentDto project = db.components().insertPrivateProject();
        underTest.indexOnAnalysis(project.uuid());
        assertThatIndexContainsOnly(project);
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        underTest.indexOnAnalysis(project.uuid());
        assertThatIndexContainsOnly(project, file);
    }

    @Test
    public void indexOnAnalysis_updates_index_on_changes() {
        ComponentDto project = db.components().insertPrivateProject();
        underTest.indexOnAnalysis(project.uuid());
        assertThatComponentHasName(project, project.name());
        // modify
        project.setName("NewName");
        updateDb(project);
        // verify that index is updated
        underTest.indexOnAnalysis(project.uuid());
        assertThatIndexContainsOnly(project);
        assertThatComponentHasName(project, "NewName");
    }

    @Test
    public void indexOnAnalysis_does_not_index_non_main_branches() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setKey("feature/foo"));
        underTest.indexOnAnalysis(branch.uuid());
        assertThatIndexHasSize(0);
    }

    @Test
    public void do_not_update_index_on_project_tag_update() {
        ComponentDto project = db.components().insertPrivateProject();
        indexProject(project, PROJECT_TAGS_UPDATE);
        assertThatIndexHasSize(0);
    }

    @Test
    public void do_not_update_index_on_permission_change() {
        ComponentDto project = db.components().insertPrivateProject();
        indexProject(project, PERMISSION_CHANGE);
        assertThatIndexHasSize(0);
    }

    @Test
    public void update_index_on_project_creation() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        IndexingResult result = indexProject(project, Cause.PROJECT_CREATION);
        assertThatIndexContainsOnly(project, file);
        // two requests (one per component)
        assertThat(result.getTotal()).isEqualTo(2L);
        assertThat(result.getSuccess()).isEqualTo(2L);
    }

    @Test
    public void do_not_delete_orphans_when_updating_project() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        indexProject(project, Cause.PROJECT_CREATION);
        assertThatIndexContainsOnly(project, file);
        db.getDbClient().componentDao().delete(db.getSession(), file.getId());
        IndexingResult result = indexProject(project, PROJECT_KEY_UPDATE);
        assertThatIndexContainsOnly(project, file);
        // single request for project, no request for file
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getSuccess()).isEqualTo(1);
    }

    @Test
    public void delete_some_components() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file1 = db.components().insertComponent(newFileDto(project));
        ComponentDto file2 = db.components().insertComponent(newFileDto(project));
        indexProject(project, Cause.PROJECT_CREATION);
        underTest.delete(project.uuid(), Collections.singletonList(file1.uuid()));
        assertThatIndexContainsOnly(project, file2);
    }

    @Test
    public void delete_project() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        indexProject(project, Cause.PROJECT_CREATION);
        assertThatIndexHasSize(2);
        db.getDbClient().componentDao().delete(db.getSession(), project.getId());
        db.getDbClient().componentDao().delete(db.getSession(), file.getId());
        indexProject(project, Cause.PROJECT_DELETION);
        assertThatIndexHasSize(0);
    }

    @Test
    public void errors_during_indexing_are_recovered() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto file = db.components().insertComponent(newFileDto(project));
        es.lockWrites(ComponentIndexDefinition.INDEX_TYPE_COMPONENT);
        IndexingResult result = indexProject(project, Cause.PROJECT_CREATION);
        assertThat(result.getTotal()).isEqualTo(2L);
        assertThat(result.getFailures()).isEqualTo(2L);
        // index is still read-only, fail to recover
        result = recover();
        assertThat(result.getTotal()).isEqualTo(2L);
        assertThat(result.getFailures()).isEqualTo(2L);
        assertThat(es.countDocuments(ComponentIndexDefinition.INDEX_TYPE_COMPONENT)).isEqualTo(0);
        es.unlockWrites(ComponentIndexDefinition.INDEX_TYPE_COMPONENT);
        result = recover();
        assertThat(result.getTotal()).isEqualTo(2L);
        assertThat(result.getFailures()).isEqualTo(0L);
        assertThatIndexContainsOnly(project, file);
    }
}

