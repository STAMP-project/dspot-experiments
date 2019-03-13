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
package org.sonar.ce.task.projectanalysis.step;


import Component.Type.DIRECTORY;
import Component.Type.FILE;
import Component.Type.PROJECT;
import System2.INSTANCE;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.utils.DateUtils;
import org.sonar.api.utils.System2;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.DbIdsRepositoryImpl;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.period.PeriodHolderRule;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.component.SnapshotTesting;
import org.sonar.db.organization.OrganizationDto;


@RunWith(DataProviderRunner.class)
public class ReportPersistAnalysisStepTest extends BaseStepTest {
    private static final String PROJECT_KEY = "PROJECT_KEY";

    private static final String ANALYSIS_UUID = "U1";

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule();

    @Rule
    public PeriodHolderRule periodsHolder = new PeriodHolderRule();

    private System2 system2 = Mockito.mock(System2.class);

    private DbIdsRepositoryImpl dbIdsRepository;

    private DbClient dbClient = dbTester.getDbClient();

    private long analysisDate;

    private long now;

    private PersistAnalysisStep underTest;

    @Test
    public void persist_snapshots_with_leak_period() {
        OrganizationDto organizationDto = dbTester.organizations().insert();
        ComponentDto projectDto = ComponentTesting.newPrivateProjectDto(organizationDto, "ABCD").setDbKey(ReportPersistAnalysisStepTest.PROJECT_KEY).setName("Project");
        dbClient.componentDao().insert(dbTester.getSession(), projectDto);
        org.sonar.db.component.SnapshotDto snapshotDto = SnapshotTesting.newAnalysis(projectDto).setCreatedAt(DateUtils.parseDateQuietly("2015-01-01").getTime());
        dbClient.snapshotDao().insert(dbTester.getSession(), snapshotDto);
        dbTester.getSession().commit();
        periodsHolder.setPeriod(new org.sonar.ce.task.projectanalysis.period.Period(LEAK_PERIOD_MODE_DATE, "2015-01-01", analysisDate, "u1"));
        Component project = ReportComponent.builder(PROJECT, 1).setUuid("ABCD").setKey(ReportPersistAnalysisStepTest.PROJECT_KEY).build();
        treeRootHolder.setRoot(project);
        dbIdsRepository.setComponentId(project, projectDto.getId());
        underTest.execute(new TestComputationStepContext());
        org.sonar.db.component.SnapshotDto projectSnapshot = getUnprocessedSnapshot(projectDto.uuid());
        assertThat(projectSnapshot.getPeriodMode()).isEqualTo(LEAK_PERIOD_MODE_DATE);
        assertThat(projectSnapshot.getPeriodDate()).isEqualTo(analysisDate);
        assertThat(projectSnapshot.getPeriodModeParameter()).isNotNull();
    }

    @Test
    public void only_persist_snapshots_with_leak_period_on_project_and_module() {
        periodsHolder.setPeriod(new org.sonar.ce.task.projectanalysis.period.Period(LEAK_PERIOD_MODE_PREVIOUS_VERSION, null, analysisDate, "u1"));
        OrganizationDto organizationDto = dbTester.organizations().insert();
        ComponentDto projectDto = ComponentTesting.newPrivateProjectDto(organizationDto, "ABCD").setDbKey(ReportPersistAnalysisStepTest.PROJECT_KEY).setName("Project");
        dbClient.componentDao().insert(dbTester.getSession(), projectDto);
        org.sonar.db.component.SnapshotDto projectSnapshot = SnapshotTesting.newAnalysis(projectDto);
        dbClient.snapshotDao().insert(dbTester.getSession(), projectSnapshot);
        ComponentDto moduleDto = ComponentTesting.newModuleDto("BCDE", projectDto).setDbKey("MODULE_KEY").setName("Module");
        dbClient.componentDao().insert(dbTester.getSession(), moduleDto);
        ComponentDto directoryDto = ComponentTesting.newDirectory(moduleDto, "CDEF", "MODULE_KEY:src/main/java/dir").setDbKey("MODULE_KEY:src/main/java/dir");
        dbClient.componentDao().insert(dbTester.getSession(), directoryDto);
        ComponentDto fileDto = ComponentTesting.newFileDto(moduleDto, directoryDto, "DEFG").setDbKey("MODULE_KEY:src/main/java/dir/Foo.java");
        dbClient.componentDao().insert(dbTester.getSession(), fileDto);
        dbTester.getSession().commit();
        Component file = ReportComponent.builder(FILE, 3).setUuid("DEFG").setKey("MODULE_KEY:src/main/java/dir/Foo.java").build();
        Component directory = ReportComponent.builder(DIRECTORY, 2).setUuid("CDEF").setKey("MODULE_KEY:src/main/java/dir").addChildren(file).build();
        Component project = ReportComponent.builder(PROJECT, 1).setUuid("ABCD").setKey(ReportPersistAnalysisStepTest.PROJECT_KEY).addChildren(directory).build();
        treeRootHolder.setRoot(project);
        dbIdsRepository.setComponentId(project, projectDto.getId());
        dbIdsRepository.setComponentId(directory, directoryDto.getId());
        dbIdsRepository.setComponentId(file, fileDto.getId());
        underTest.execute(new TestComputationStepContext());
        org.sonar.db.component.SnapshotDto newProjectSnapshot = getUnprocessedSnapshot(projectDto.uuid());
        assertThat(newProjectSnapshot.getPeriodMode()).isEqualTo(LEAK_PERIOD_MODE_PREVIOUS_VERSION);
    }

    @Test
    public void set_no_period_on_snapshots_when_no_period() {
        ComponentDto projectDto = ComponentTesting.newPrivateProjectDto(dbTester.organizations().insert(), "ABCD").setDbKey(ReportPersistAnalysisStepTest.PROJECT_KEY).setName("Project");
        dbClient.componentDao().insert(dbTester.getSession(), projectDto);
        org.sonar.db.component.SnapshotDto snapshotDto = SnapshotTesting.newAnalysis(projectDto);
        dbClient.snapshotDao().insert(dbTester.getSession(), snapshotDto);
        dbTester.getSession().commit();
        Component project = ReportComponent.builder(PROJECT, 1).setUuid("ABCD").setKey(ReportPersistAnalysisStepTest.PROJECT_KEY).build();
        treeRootHolder.setRoot(project);
        dbIdsRepository.setComponentId(project, projectDto.getId());
        underTest.execute(new TestComputationStepContext());
        org.sonar.db.component.SnapshotDto projectSnapshot = getUnprocessedSnapshot(projectDto.uuid());
        assertThat(projectSnapshot.getPeriodMode()).isNull();
        assertThat(projectSnapshot.getPeriodDate()).isNull();
        assertThat(projectSnapshot.getPeriodModeParameter()).isNull();
    }
}

