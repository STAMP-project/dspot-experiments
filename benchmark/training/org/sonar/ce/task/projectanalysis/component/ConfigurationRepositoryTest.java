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
package org.sonar.ce.task.projectanalysis.component;


import System2.INSTANCE;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.analysis.Branch;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.server.project.Project;


public class ConfigurationRepositoryTest {
    private static Project PROJECT = Project.from(ComponentTesting.newPrivateProjectDto(OrganizationTesting.newOrganizationDto()));

    @Rule
    public final DbTester db = DbTester.create(INSTANCE);

    private DbClient dbClient = db.getDbClient();

    private MapSettings globalSettings = new MapSettings();

    private Branch branch = Mockito.mock(Branch.class);

    private AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule();

    private ConfigurationRepository underTest;

    @Test
    public void get_project_settings_from_global_settings() {
        analysisMetadataHolder.setProject(ConfigurationRepositoryTest.PROJECT);
        globalSettings.setProperty("key", "value");
        Configuration config = underTest.getConfiguration();
        assertThat(config.get("key")).hasValue("value");
    }

    @Test
    public void get_project_settings_from_db() {
        ComponentDto project = db.components().insertPrivateProject();
        analysisMetadataHolder.setProject(Project.from(project));
        insertProjectProperty(project, "key", "value");
        Configuration config = underTest.getConfiguration();
        assertThat(config.get("key")).hasValue("value");
    }

    @Test
    public void call_twice_get_project_settings() {
        analysisMetadataHolder.setProject(ConfigurationRepositoryTest.PROJECT);
        globalSettings.setProperty("key", "value");
        Configuration config = underTest.getConfiguration();
        assertThat(config.get("key")).hasValue("value");
        config = underTest.getConfiguration();
        assertThat(config.get("key")).hasValue("value");
    }

    @Test
    public void project_settings_override_global_settings() {
        globalSettings.setProperty("key", "value1");
        ComponentDto project = db.components().insertPrivateProject();
        insertProjectProperty(project, "key", "value2");
        analysisMetadataHolder.setProject(Project.from(project));
        Configuration config = underTest.getConfiguration();
        assertThat(config.get("key")).hasValue("value2");
    }

    @Test
    public void project_settings_are_cached_to_avoid_db_access() {
        ComponentDto project = db.components().insertPrivateProject();
        insertProjectProperty(project, "key", "value");
        analysisMetadataHolder.setProject(Project.from(project));
        Configuration config = underTest.getConfiguration();
        assertThat(config.get("key")).hasValue("value");
        db.executeUpdateSql("delete from properties");
        db.commit();
        assertThat(config.get("key")).hasValue("value");
    }

    @Test
    public void branch_settings() {
        ComponentDto project = db.components().insertMainBranch();
        ComponentDto branchDto = db.components().insertProjectBranch(project);
        Branch branch = Mockito.mock(Branch.class);
        Mockito.when(branch.getName()).thenReturn(branchDto.getBranch());
        analysisMetadataHolder.setProject(Project.from(project)).setBranch(branch);
        globalSettings.setProperty("global", "global value");
        insertProjectProperty(project, "project", "project value");
        insertProjectProperty(branchDto, "branch", "branch value");
        Configuration config = underTest.getConfiguration();
        assertThat(config.get("global")).hasValue("global value");
        assertThat(config.get("project")).hasValue("project value");
        assertThat(config.get("branch")).hasValue("branch value");
    }
}

