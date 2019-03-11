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


import BranchType.LONG;
import FileStatus.SAME;
import ScannerReport.Metadata;
import System2.INSTANCE;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.analysis.Branch;
import org.sonar.ce.task.projectanalysis.analysis.MutableAnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.DefaultBranchImpl;
import org.sonar.ce.task.projectanalysis.component.MutableTreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.component.ReportModulesPath;
import org.sonar.ce.task.projectanalysis.issue.IssueRelocationToRoot;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.component.SnapshotTesting;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.server.project.Project;


@RunWith(DataProviderRunner.class)
public class BuildComponentTreeStepTest {
    private static final String NO_SCANNER_PROJECT_VERSION = null;

    private static final String NO_SCANNER_CODE_PERIOD_VERSION = null;

    private static final int ROOT_REF = 1;

    private static final int MODULE_REF = 2;

    private static final int DIR_REF_1 = 3;

    private static final int FILE_1_REF = 4;

    private static final int FILE_2_REF = 5;

    private static final int DIR_REF_2 = 6;

    private static final int FILE_3_REF = 7;

    private static final int LEAFLESS_MODULE_REF = 8;

    private static final int LEAFLESS_DIR_REF = 9;

    private static final int UNCHANGED_FILE_REF = 10;

    private static final String REPORT_PROJECT_KEY = "REPORT_PROJECT_KEY";

    private static final String REPORT_MODULE_KEY = "MODULE_KEY";

    private static final String REPORT_DIR_PATH_1 = "src/main/java/dir1";

    private static final String REPORT_FILE_PATH_1 = "src/main/java/dir1/File1.java";

    private static final String REPORT_FILE_NAME_1 = "File1.java";

    private static final String REPORT_DIR_PATH_2 = "src/main/java/dir2";

    private static final String REPORT_FILE_PATH_2 = "src/main/java/dir2/File2.java";

    private static final String REPORT_FILE_PATH_3 = "src/main/java/dir2/File3.java";

    private static final String REPORT_LEAFLESS_MODULE_KEY = "LEAFLESS_MODULE_KEY";

    private static final String REPORT_LEAFLESS_DIR_PATH = "src/main/java/leafless";

    private static final String REPORT_UNCHANGED_FILE_PATH = "src/main/java/leafless/File3.java";

    private static final long ANALYSIS_DATE = 123456789L;

    @Rule
    public DbTester dbTester = DbTester.create(INSTANCE);

    @Rule
    public BatchReportReaderRule reportReader = new BatchReportReaderRule().setMetadata(BuildComponentTreeStepTest.createReportMetadata(BuildComponentTreeStepTest.NO_SCANNER_PROJECT_VERSION, BuildComponentTreeStepTest.NO_SCANNER_CODE_PERIOD_VERSION));

    @Rule
    public MutableTreeRootHolderRule treeRootHolder = new MutableTreeRootHolderRule();

    @Rule
    public MutableAnalysisMetadataHolderRule analysisMetadataHolder = new MutableAnalysisMetadataHolderRule();

    private ReportModulesPath reportModulesPath = new ReportModulesPath(reportReader);

    private IssueRelocationToRoot issueRelocationToRoot = Mockito.mock(IssueRelocationToRoot.class);

    private DbClient dbClient = dbTester.getDbClient();

    private BuildComponentTreeStep underTest = new BuildComponentTreeStep(dbClient, reportReader, treeRootHolder, analysisMetadataHolder, issueRelocationToRoot, reportModulesPath);

    @Test(expected = NullPointerException.class)
    public void fails_if_root_component_does_not_exist_in_reportReader() {
        setAnalysisMetadataHolder();
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void verify_tree_is_correctly_built() {
        setAnalysisMetadataHolder();
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, BuildComponentTreeStepTest.MODULE_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.MODULE_REF, MODULE, BuildComponentTreeStepTest.REPORT_MODULE_KEY, BuildComponentTreeStepTest.DIR_REF_1, BuildComponentTreeStepTest.DIR_REF_2));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.DIR_REF_1, DIRECTORY, BuildComponentTreeStepTest.REPORT_DIR_PATH_1, BuildComponentTreeStepTest.FILE_1_REF, BuildComponentTreeStepTest.FILE_2_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.FILE_1_REF, FILE, BuildComponentTreeStepTest.REPORT_FILE_PATH_1));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.FILE_2_REF, FILE, BuildComponentTreeStepTest.REPORT_FILE_PATH_2));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.DIR_REF_2, DIRECTORY, BuildComponentTreeStepTest.REPORT_DIR_PATH_2, BuildComponentTreeStepTest.FILE_3_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.FILE_3_REF, FILE, BuildComponentTreeStepTest.REPORT_FILE_PATH_3));
        TestComputationStepContext context = new TestComputationStepContext();
        underTest.execute(context);
        Component root = treeRootHolder.getRoot();
        assertThat(root).isNotNull();
        verifyComponent(root, Component.Type.PROJECT, BuildComponentTreeStepTest.ROOT_REF, 1);
        Component dir = root.getChildren().iterator().next();
        verifyComponent(dir, Component.Type.DIRECTORY, null, 2);
        Component dir1 = dir.getChildren().get(0);
        verifyComponent(dir1, Component.Type.DIRECTORY, null, 1);
        verifyComponent(dir1.getChildren().get(0), Component.Type.FILE, BuildComponentTreeStepTest.FILE_1_REF, 0);
        Component dir2 = dir.getChildren().get(1);
        verifyComponent(dir2, Component.Type.DIRECTORY, null, 2);
        verifyComponent(dir2.getChildren().get(0), Component.Type.FILE, BuildComponentTreeStepTest.FILE_2_REF, 0);
        verifyComponent(dir2.getChildren().get(1), Component.Type.FILE, BuildComponentTreeStepTest.FILE_3_REF, 0);
        context.getStatistics().assertValue("components", 7);
    }

    @Test
    public void compute_keys_and_uuids() {
        setAnalysisMetadataHolder();
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, BuildComponentTreeStepTest.MODULE_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.MODULE_REF, MODULE, BuildComponentTreeStepTest.REPORT_MODULE_KEY, BuildComponentTreeStepTest.DIR_REF_1));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.DIR_REF_1, DIRECTORY, BuildComponentTreeStepTest.REPORT_DIR_PATH_1, BuildComponentTreeStepTest.FILE_1_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.FILE_1_REF, FILE, BuildComponentTreeStepTest.REPORT_FILE_PATH_1));
        underTest.execute(new TestComputationStepContext());
        verifyComponentByRef(BuildComponentTreeStepTest.ROOT_REF, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, analysisMetadataHolder.getProject().getName());
        verifyComponentByKey((((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1)), BuildComponentTreeStepTest.REPORT_DIR_PATH_1);
        verifyComponentByRef(BuildComponentTreeStepTest.FILE_1_REF, (((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_FILE_PATH_1)), BuildComponentTreeStepTest.REPORT_FILE_NAME_1);
    }

    @Test
    public void return_existing_uuids() {
        setAnalysisMetadataHolder();
        OrganizationDto organizationDto = dbTester.organizations().insert();
        ComponentDto project = insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto, "ABCD").setDbKey(BuildComponentTreeStepTest.REPORT_PROJECT_KEY));
        ComponentDto directory = ComponentTesting.newDirectory(project, "CDEF", BuildComponentTreeStepTest.REPORT_DIR_PATH_1);
        insertComponent(directory.setDbKey((((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1))));
        insertComponent(ComponentTesting.newFileDto(project, directory, "DEFG").setDbKey((((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_FILE_PATH_1))).setPath(BuildComponentTreeStepTest.REPORT_FILE_PATH_1));
        // new structure, without modules
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, BuildComponentTreeStepTest.DIR_REF_1));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.DIR_REF_1, DIRECTORY, BuildComponentTreeStepTest.REPORT_DIR_PATH_1, BuildComponentTreeStepTest.FILE_1_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.FILE_1_REF, FILE, BuildComponentTreeStepTest.REPORT_FILE_PATH_1));
        underTest.execute(new TestComputationStepContext());
        verifyComponentByRef(BuildComponentTreeStepTest.ROOT_REF, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, analysisMetadataHolder.getProject().getName(), "ABCD");
        verifyComponentByKey((((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1)), (((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1)), BuildComponentTreeStepTest.REPORT_DIR_PATH_1, "CDEF");
        verifyComponentByRef(BuildComponentTreeStepTest.FILE_1_REF, (((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_FILE_PATH_1)), BuildComponentTreeStepTest.REPORT_FILE_NAME_1, "DEFG");
    }

    @Test
    public void return_existing_uuids_with_modules() {
        setAnalysisMetadataHolder();
        OrganizationDto organizationDto = dbTester.organizations().insert();
        ComponentDto project = insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto, "ABCD").setDbKey(BuildComponentTreeStepTest.REPORT_PROJECT_KEY));
        ComponentDto module = insertComponent(ComponentTesting.newModuleDto("BCDE", project).setDbKey(BuildComponentTreeStepTest.REPORT_MODULE_KEY));
        ComponentDto directory = ComponentTesting.newDirectory(module, "CDEF", BuildComponentTreeStepTest.REPORT_DIR_PATH_1);
        insertComponent(directory.setDbKey((((BuildComponentTreeStepTest.REPORT_MODULE_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1))));
        insertComponent(ComponentTesting.newFileDto(module, directory, "DEFG").setDbKey((((BuildComponentTreeStepTest.REPORT_MODULE_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_FILE_PATH_1))).setPath(BuildComponentTreeStepTest.REPORT_FILE_PATH_1));
        // new structure, without modules
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, BuildComponentTreeStepTest.DIR_REF_1));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.DIR_REF_1, DIRECTORY, ("module/" + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1)), BuildComponentTreeStepTest.FILE_1_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.FILE_1_REF, FILE, ("module/" + (BuildComponentTreeStepTest.REPORT_FILE_PATH_1))));
        reportReader.setMetadata(Metadata.newBuilder().putModulesProjectRelativePathByKey(BuildComponentTreeStepTest.REPORT_MODULE_KEY, "module").build());
        underTest.execute(new TestComputationStepContext());
        verifyComponentByRef(BuildComponentTreeStepTest.ROOT_REF, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, analysisMetadataHolder.getProject().getName(), "ABCD");
        verifyComponentByKey((((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":module/") + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1)), (((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":module/") + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1)), ("module/" + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1)), "CDEF");
        verifyComponentByRef(BuildComponentTreeStepTest.FILE_1_REF, (((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":module/") + (BuildComponentTreeStepTest.REPORT_FILE_PATH_1)), BuildComponentTreeStepTest.REPORT_FILE_NAME_1, "DEFG");
    }

    @Test
    public void generate_keys_when_using_new_branch() {
        Branch branch = Mockito.mock(Branch.class);
        Mockito.when(branch.getName()).thenReturn("origin/feature");
        Mockito.when(branch.isMain()).thenReturn(false);
        Mockito.when(branch.isLegacyFeature()).thenReturn(false);
        Mockito.when(branch.generateKey(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("generated");
        analysisMetadataHolder.setRootComponentRef(BuildComponentTreeStepTest.ROOT_REF).setAnalysisDate(BuildComponentTreeStepTest.ANALYSIS_DATE).setProject(Project.from(ComponentTesting.newPrivateProjectDto(OrganizationTesting.newOrganizationDto()).setDbKey(BuildComponentTreeStepTest.REPORT_PROJECT_KEY))).setBranch(branch);
        BuildComponentTreeStep underTest = new BuildComponentTreeStep(dbClient, reportReader, treeRootHolder, analysisMetadataHolder, issueRelocationToRoot, reportModulesPath);
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, BuildComponentTreeStepTest.MODULE_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.MODULE_REF, MODULE, BuildComponentTreeStepTest.REPORT_MODULE_KEY, BuildComponentTreeStepTest.DIR_REF_1));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.DIR_REF_1, DIRECTORY, BuildComponentTreeStepTest.REPORT_DIR_PATH_1, BuildComponentTreeStepTest.FILE_1_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.FILE_1_REF, FILE, BuildComponentTreeStepTest.REPORT_FILE_PATH_1));
        underTest.execute(new TestComputationStepContext());
        verifyComponentByRef(BuildComponentTreeStepTest.ROOT_REF, "generated", BuildComponentTreeStepTest.REPORT_PROJECT_KEY, analysisMetadataHolder.getProject().getName(), null);
        verifyComponentByKey((((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1)), "generated", BuildComponentTreeStepTest.REPORT_DIR_PATH_1);
        verifyComponentByRef(BuildComponentTreeStepTest.FILE_1_REF, "generated", (((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_FILE_PATH_1)), BuildComponentTreeStepTest.REPORT_FILE_NAME_1, null);
    }

    @Test
    public void do_not_prune_modules_and_directories_without_leaf_descendants_on_long_branch() {
        Branch branch = Mockito.mock(Branch.class);
        Mockito.when(branch.getName()).thenReturn("origin/feature");
        Mockito.when(branch.isMain()).thenReturn(false);
        Mockito.when(branch.getType()).thenReturn(LONG);
        Mockito.when(branch.isLegacyFeature()).thenReturn(false);
        Mockito.when(branch.generateKey(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn("generated");
        analysisMetadataHolder.setRootComponentRef(BuildComponentTreeStepTest.ROOT_REF).setAnalysisDate(BuildComponentTreeStepTest.ANALYSIS_DATE).setProject(Project.from(ComponentTesting.newPrivateProjectDto(OrganizationTesting.newOrganizationDto()).setDbKey(BuildComponentTreeStepTest.REPORT_PROJECT_KEY))).setBranch(branch);
        BuildComponentTreeStep underTest = new BuildComponentTreeStep(dbClient, reportReader, treeRootHolder, analysisMetadataHolder, issueRelocationToRoot, reportModulesPath);
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, BuildComponentTreeStepTest.MODULE_REF, BuildComponentTreeStepTest.LEAFLESS_MODULE_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.MODULE_REF, MODULE, BuildComponentTreeStepTest.REPORT_MODULE_KEY, BuildComponentTreeStepTest.DIR_REF_1));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.DIR_REF_1, DIRECTORY, BuildComponentTreeStepTest.REPORT_DIR_PATH_1, BuildComponentTreeStepTest.FILE_1_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.FILE_1_REF, FILE, BuildComponentTreeStepTest.REPORT_FILE_PATH_1));
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.LEAFLESS_MODULE_REF, MODULE, BuildComponentTreeStepTest.REPORT_LEAFLESS_MODULE_KEY, BuildComponentTreeStepTest.LEAFLESS_DIR_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.LEAFLESS_DIR_REF, DIRECTORY, BuildComponentTreeStepTest.REPORT_LEAFLESS_DIR_PATH, BuildComponentTreeStepTest.UNCHANGED_FILE_REF));
        ScannerReport.Component unchangedFile = ScannerReport.Component.newBuilder().setType(FILE).setRef(BuildComponentTreeStepTest.UNCHANGED_FILE_REF).setProjectRelativePath(BuildComponentTreeStepTest.REPORT_UNCHANGED_FILE_PATH).setStatus(SAME).setLines(1).build();
        reportReader.putComponent(unchangedFile);
        underTest.execute(new TestComputationStepContext());
        verifyComponentByRef(BuildComponentTreeStepTest.ROOT_REF, "generated", BuildComponentTreeStepTest.REPORT_PROJECT_KEY, analysisMetadataHolder.getProject().getName(), null);
        verifyComponentMissingByRef(BuildComponentTreeStepTest.MODULE_REF);
        verifyComponentByKey((((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1)), "generated", "dir1");
        verifyComponentByRef(BuildComponentTreeStepTest.FILE_1_REF, "generated", (((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_FILE_PATH_1)), BuildComponentTreeStepTest.REPORT_FILE_NAME_1, null);
        verifyComponentMissingByRef(BuildComponentTreeStepTest.LEAFLESS_MODULE_REF);
        verifyComponentByKey((((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_LEAFLESS_DIR_PATH)), "generated", "leafless");
        verifyComponentByRef(BuildComponentTreeStepTest.UNCHANGED_FILE_REF, "generated", (((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_UNCHANGED_FILE_PATH)), "File3.java", null);
    }

    @Test
    public void generate_keys_when_using_existing_branch() {
        ComponentDto projectDto = dbTester.components().insertMainBranch();
        ComponentDto branchDto = dbTester.components().insertProjectBranch(projectDto);
        Branch branch = Mockito.mock(Branch.class);
        Mockito.when(branch.getName()).thenReturn(branchDto.getBranch());
        Mockito.when(branch.isMain()).thenReturn(false);
        Mockito.when(branch.isLegacyFeature()).thenReturn(false);
        Mockito.when(branch.generateKey(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(branchDto.getDbKey());
        analysisMetadataHolder.setRootComponentRef(BuildComponentTreeStepTest.ROOT_REF).setAnalysisDate(BuildComponentTreeStepTest.ANALYSIS_DATE).setProject(Project.from(projectDto)).setBranch(branch);
        BuildComponentTreeStep underTest = new BuildComponentTreeStep(dbClient, reportReader, treeRootHolder, analysisMetadataHolder, issueRelocationToRoot, reportModulesPath);
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, branchDto.getKey()));
        underTest.execute(new TestComputationStepContext());
        verifyComponentByRef(BuildComponentTreeStepTest.ROOT_REF, branchDto.getDbKey(), branchDto.getKey(), analysisMetadataHolder.getProject().getName(), branchDto.uuid());
    }

    @Test
    public void generate_keys_when_using_main_branch() {
        Branch branch = new DefaultBranchImpl();
        analysisMetadataHolder.setRootComponentRef(BuildComponentTreeStepTest.ROOT_REF).setAnalysisDate(BuildComponentTreeStepTest.ANALYSIS_DATE).setProject(Project.from(ComponentTesting.newPrivateProjectDto(OrganizationTesting.newOrganizationDto()).setDbKey(BuildComponentTreeStepTest.REPORT_PROJECT_KEY))).setBranch(branch);
        BuildComponentTreeStep underTest = new BuildComponentTreeStep(dbClient, reportReader, treeRootHolder, analysisMetadataHolder, issueRelocationToRoot, reportModulesPath);
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, BuildComponentTreeStepTest.MODULE_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.MODULE_REF, MODULE, BuildComponentTreeStepTest.REPORT_MODULE_KEY, BuildComponentTreeStepTest.DIR_REF_1));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.DIR_REF_1, DIRECTORY, BuildComponentTreeStepTest.REPORT_DIR_PATH_1, BuildComponentTreeStepTest.FILE_1_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.FILE_1_REF, FILE, BuildComponentTreeStepTest.REPORT_FILE_PATH_1));
        underTest.execute(new TestComputationStepContext());
        verifyComponentByRef(BuildComponentTreeStepTest.ROOT_REF, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, analysisMetadataHolder.getProject().getName(), null);
        verifyComponentMissingByRef(BuildComponentTreeStepTest.MODULE_REF);
        verifyComponentByKey((((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1)), BuildComponentTreeStepTest.REPORT_DIR_PATH_1);
        verifyComponentByRef(BuildComponentTreeStepTest.FILE_1_REF, (((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_FILE_PATH_1)), (((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_FILE_PATH_1)), BuildComponentTreeStepTest.REPORT_FILE_NAME_1, null);
    }

    @Test
    public void generate_keys_when_using_legacy_branch() {
        analysisMetadataHolder.setRootComponentRef(BuildComponentTreeStepTest.ROOT_REF).setAnalysisDate(BuildComponentTreeStepTest.ANALYSIS_DATE).setProject(Project.from(ComponentTesting.newPrivateProjectDto(OrganizationTesting.newOrganizationDto()).setDbKey(BuildComponentTreeStepTest.REPORT_PROJECT_KEY))).setBranch(new DefaultBranchImpl("origin/feature"));
        BuildComponentTreeStep underTest = new BuildComponentTreeStep(dbClient, reportReader, treeRootHolder, analysisMetadataHolder, issueRelocationToRoot, reportModulesPath);
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, BuildComponentTreeStepTest.MODULE_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.MODULE_REF, MODULE, BuildComponentTreeStepTest.REPORT_MODULE_KEY, BuildComponentTreeStepTest.DIR_REF_1));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.DIR_REF_1, DIRECTORY, BuildComponentTreeStepTest.REPORT_DIR_PATH_1, BuildComponentTreeStepTest.FILE_1_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.FILE_1_REF, FILE, BuildComponentTreeStepTest.REPORT_FILE_PATH_1));
        underTest.execute(new TestComputationStepContext());
        verifyComponentByRef(BuildComponentTreeStepTest.ROOT_REF, ((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":origin/feature"), analysisMetadataHolder.getProject().getName(), null);
        verifyComponentMissingByRef(BuildComponentTreeStepTest.MODULE_REF);
        verifyComponentByKey((((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":origin/feature:") + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1)), BuildComponentTreeStepTest.REPORT_DIR_PATH_1);
        verifyComponentByRef(BuildComponentTreeStepTest.FILE_1_REF, (((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":origin/feature:") + (BuildComponentTreeStepTest.REPORT_FILE_PATH_1)), BuildComponentTreeStepTest.REPORT_FILE_NAME_1, null);
    }

    @Test
    public void compute_keys_and_uuids_on_project_having_module_and_directory() {
        setAnalysisMetadataHolder();
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, BuildComponentTreeStepTest.MODULE_REF, BuildComponentTreeStepTest.DIR_REF_2));
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.MODULE_REF, MODULE, BuildComponentTreeStepTest.REPORT_MODULE_KEY, BuildComponentTreeStepTest.DIR_REF_1));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.DIR_REF_1, DIRECTORY, BuildComponentTreeStepTest.REPORT_DIR_PATH_1, BuildComponentTreeStepTest.FILE_1_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.FILE_1_REF, FILE, BuildComponentTreeStepTest.REPORT_FILE_PATH_1));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.DIR_REF_2, DIRECTORY, BuildComponentTreeStepTest.REPORT_DIR_PATH_2, BuildComponentTreeStepTest.FILE_2_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.FILE_2_REF, FILE, BuildComponentTreeStepTest.REPORT_FILE_PATH_2));
        underTest.execute(new TestComputationStepContext());
        verifyComponentByRef(BuildComponentTreeStepTest.ROOT_REF, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, analysisMetadataHolder.getProject().getName());
        verifyComponentMissingByRef(BuildComponentTreeStepTest.MODULE_REF);
        verifyComponentByKey((((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1)), "dir1");
        verifyComponentByRef(BuildComponentTreeStepTest.FILE_1_REF, (((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_FILE_PATH_1)), BuildComponentTreeStepTest.REPORT_FILE_NAME_1);
        verifyComponentByKey((((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_DIR_PATH_2)), "dir2");
        verifyComponentByRef(BuildComponentTreeStepTest.FILE_2_REF, (((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_FILE_PATH_2)), "File2.java");
    }

    @Test
    public void compute_keys_and_uuids_on_multi_modules() {
        setAnalysisMetadataHolder();
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, BuildComponentTreeStepTest.MODULE_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.MODULE_REF, MODULE, BuildComponentTreeStepTest.REPORT_MODULE_KEY, 100));
        reportReader.putComponent(BuildComponentTreeStepTest.component(100, MODULE, "SUB_MODULE_KEY", BuildComponentTreeStepTest.DIR_REF_1));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.DIR_REF_1, DIRECTORY, BuildComponentTreeStepTest.REPORT_DIR_PATH_1, BuildComponentTreeStepTest.FILE_1_REF));
        reportReader.putComponent(BuildComponentTreeStepTest.componentWithPath(BuildComponentTreeStepTest.FILE_1_REF, FILE, BuildComponentTreeStepTest.REPORT_FILE_PATH_1));
        underTest.execute(new TestComputationStepContext());
        verifyComponentByRef(BuildComponentTreeStepTest.ROOT_REF, BuildComponentTreeStepTest.REPORT_PROJECT_KEY, analysisMetadataHolder.getProject().getName());
        verifyComponentMissingByRef(BuildComponentTreeStepTest.MODULE_REF);
        verifyComponentMissingByKey(BuildComponentTreeStepTest.REPORT_MODULE_KEY);
        verifyComponentMissingByRef(100);
        verifyComponentMissingByKey("SUB_MODULE_KEY");
        verifyComponentByKey((((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_DIR_PATH_1)), BuildComponentTreeStepTest.REPORT_DIR_PATH_1);
        verifyComponentByRef(BuildComponentTreeStepTest.FILE_1_REF, (((BuildComponentTreeStepTest.REPORT_PROJECT_KEY) + ":") + (BuildComponentTreeStepTest.REPORT_FILE_PATH_1)), BuildComponentTreeStepTest.REPORT_FILE_NAME_1);
    }

    @Test
    public void set_no_base_project_snapshot_when_no_snapshot() {
        setAnalysisMetadataHolder();
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY));
        underTest.execute(new TestComputationStepContext());
        assertThat(analysisMetadataHolder.isFirstAnalysis()).isTrue();
    }

    @Test
    public void set_no_base_project_snapshot_when_no_last_snapshot() {
        setAnalysisMetadataHolder();
        OrganizationDto organizationDto = dbTester.organizations().insert();
        ComponentDto project = insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto, "ABCD").setDbKey(BuildComponentTreeStepTest.REPORT_PROJECT_KEY));
        insertSnapshot(SnapshotTesting.newAnalysis(project).setLast(false));
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY));
        underTest.execute(new TestComputationStepContext());
        assertThat(analysisMetadataHolder.isFirstAnalysis()).isTrue();
    }

    @Test
    public void set_base_project_snapshot_when_last_snapshot_exist() {
        setAnalysisMetadataHolder();
        OrganizationDto organizationDto = dbTester.organizations().insert();
        ComponentDto project = insertComponent(ComponentTesting.newPrivateProjectDto(organizationDto, "ABCD").setDbKey(BuildComponentTreeStepTest.REPORT_PROJECT_KEY));
        insertSnapshot(SnapshotTesting.newAnalysis(project).setLast(true));
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY));
        underTest.execute(new TestComputationStepContext());
        assertThat(analysisMetadataHolder.isFirstAnalysis()).isFalse();
    }

    @Test
    public void set_codePeriodVersion_to_not_provided_when_both_codePeriod_and_project_version_are_not_set_on_first_analysis() {
        setAnalysisMetadataHolder();
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY));
        underTest.execute(new TestComputationStepContext());
        assertThat(treeRootHolder.getReportTreeRoot().getProjectAttributes().getCodePeriodVersion()).isEqualTo("not provided");
    }

    @Test
    public void set_codePeriodVersion_to_projectVersion_when_codePeriodVersion_is_unset_and_projectVersion_is_set_on_first_analysis() {
        String scannerProjectVersion = randomAlphabetic(12);
        setAnalysisMetadataHolder();
        reportReader.setMetadata(BuildComponentTreeStepTest.createReportMetadata(scannerProjectVersion, BuildComponentTreeStepTest.NO_SCANNER_CODE_PERIOD_VERSION));
        reportReader.putComponent(BuildComponentTreeStepTest.component(BuildComponentTreeStepTest.ROOT_REF, PROJECT, BuildComponentTreeStepTest.REPORT_PROJECT_KEY));
        underTest.execute(new TestComputationStepContext());
        assertThat(treeRootHolder.getReportTreeRoot().getProjectAttributes().getCodePeriodVersion()).isEqualTo(scannerProjectVersion);
    }
}

