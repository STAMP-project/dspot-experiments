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
package org.sonar.server.measure.index;


import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.System2;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.SnapshotDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.es.EsTester;
import org.sonar.server.es.IndexingResult;


public class ProjectMeasuresIndexerTest {
    private System2 system2 = System2.INSTANCE;

    @Rule
    public EsTester es = EsTester.create();

    @Rule
    public DbTester db = DbTester.create(system2);

    private ProjectMeasuresIndexer underTest = new ProjectMeasuresIndexer(db.getDbClient(), es.client());

    @Test
    public void index_nothing() {
        underTest.indexOnStartup(Collections.emptySet());
        assertThat(es.countDocuments(ProjectMeasuresIndexDefinition.INDEX_TYPE_PROJECT_MEASURES)).isZero();
    }

    @Test
    public void indexOnStartup_indexes_all_projects() {
        OrganizationDto organization = db.organizations().insert();
        SnapshotDto project1 = db.components().insertProjectAndSnapshot(newPrivateProjectDto(organization));
        SnapshotDto project2 = db.components().insertProjectAndSnapshot(newPrivateProjectDto(organization));
        SnapshotDto project3 = db.components().insertProjectAndSnapshot(newPrivateProjectDto(organization));
        underTest.indexOnStartup(Collections.emptySet());
        assertThatIndexContainsOnly(project1, project2, project3);
    }

    /**
     * Provisioned projects don't have analysis yet
     */
    @Test
    public void indexOnStartup_indexes_provisioned_projects() {
        ComponentDto project = db.components().insertPrivateProject();
        underTest.indexOnStartup(Collections.emptySet());
        assertThatIndexContainsOnly(project);
    }

    @Test
    public void indexOnStartup_ignores_non_main_branches() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setKey("feature/foo"));
        underTest.indexOnStartup(Collections.emptySet());
        assertThatIndexContainsOnly(project);
    }

    @Test
    public void indexOnAnalysis_indexes_provisioned_project() {
        ComponentDto project1 = db.components().insertPrivateProject();
        ComponentDto project2 = db.components().insertPrivateProject();
        underTest.indexOnAnalysis(project1.uuid());
        assertThatIndexContainsOnly(project1);
    }

    @Test
    public void update_index_when_project_key_is_updated() {
        ComponentDto project = db.components().insertPrivateProject();
        IndexingResult result = indexProject(project, Cause.PROJECT_KEY_UPDATE);
        assertThatIndexContainsOnly(project);
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getSuccess()).isEqualTo(1L);
    }

    @Test
    public void update_index_when_project_is_created() {
        ComponentDto project = db.components().insertPrivateProject();
        IndexingResult result = indexProject(project, Cause.PROJECT_CREATION);
        assertThatIndexContainsOnly(project);
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getSuccess()).isEqualTo(1L);
    }

    @Test
    public void update_index_when_project_tags_are_updated() {
        ComponentDto project = db.components().insertPrivateProject(( p) -> p.setTagsString("foo"));
        indexProject(project, Cause.PROJECT_CREATION);
        assertThatProjectHasTag(project, "foo");
        project.setTagsString("bar");
        db.getDbClient().componentDao().updateTags(db.getSession(), project);
        IndexingResult result = indexProject(project, Cause.PROJECT_TAGS_UPDATE);
        assertThatProjectHasTag(project, "bar");
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getSuccess()).isEqualTo(1L);
    }

    @Test
    public void delete_doc_from_index_when_project_is_deleted() {
        ComponentDto project = db.components().insertPrivateProject();
        indexProject(project, Cause.PROJECT_CREATION);
        assertThatIndexContainsOnly(project);
        db.getDbClient().componentDao().delete(db.getSession(), project.getId());
        IndexingResult result = indexProject(project, Cause.PROJECT_DELETION);
        assertThat(es.countDocuments(ProjectMeasuresIndexDefinition.INDEX_TYPE_PROJECT_MEASURES)).isEqualTo(0);
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getSuccess()).isEqualTo(1L);
    }

    @Test
    public void do_nothing_if_no_projects_to_index() {
        // this project should not be indexed
        db.components().insertPrivateProject();
        underTest.index(db.getSession(), Collections.emptyList());
        assertThat(es.countDocuments(ProjectMeasuresIndexDefinition.INDEX_TYPE_PROJECT_MEASURES)).isEqualTo(0);
    }

    @Test
    public void errors_during_indexing_are_recovered() {
        ComponentDto project = db.components().insertPrivateProject();
        es.lockWrites(ProjectMeasuresIndexDefinition.INDEX_TYPE_PROJECT_MEASURES);
        IndexingResult result = indexProject(project, Cause.PROJECT_CREATION);
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getFailures()).isEqualTo(1L);
        // index is still read-only, fail to recover
        result = recover();
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getFailures()).isEqualTo(1L);
        assertThat(es.countDocuments(ProjectMeasuresIndexDefinition.INDEX_TYPE_PROJECT_MEASURES)).isEqualTo(0);
        assertThatEsQueueTableHasSize(1);
        es.unlockWrites(ProjectMeasuresIndexDefinition.INDEX_TYPE_PROJECT_MEASURES);
        result = recover();
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getFailures()).isEqualTo(0L);
        assertThatEsQueueTableHasSize(0);
        assertThatIndexContainsOnly(project);
    }

    @Test
    public void non_main_branches_are_not_indexed_during_analysis() {
        ComponentDto project = db.components().insertPrivateProject();
        ComponentDto branch = db.components().insertProjectBranch(project, ( b) -> b.setKey("feature/foo"));
        underTest.indexOnAnalysis(branch.uuid());
        assertThat(es.countDocuments(ProjectMeasuresIndexDefinition.INDEX_TYPE_PROJECT_MEASURES)).isEqualTo(0);
    }
}

