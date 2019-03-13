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


import CeTask.Component;
import ScannerReport.Metadata;
import ScannerReport.Metadata.Builder;
import ScannerReport.Metadata.Plugin;
import ScannerReport.Metadata.QProfile;
import System2.INSTANCE;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.utils.MessageException;
import org.sonar.ce.task.CeTask;
import org.sonar.ce.task.projectanalysis.analysis.MutableAnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.analysis.Organization;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.ce.task.step.ComputationStep;
import org.sonar.ce.task.step.TestComputationStepContext;
import org.sonar.core.platform.PluginInfo;
import org.sonar.core.platform.PluginRepository;
import org.sonar.db.DbClient;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.organization.OrganizationFlags;
import org.sonar.server.organization.TestDefaultOrganizationProvider;
import org.sonar.server.project.Project;


@RunWith(DataProviderRunner.class)
public class LoadReportAnalysisMetadataHolderStepTest {
    private static final String PROJECT_KEY = "project_key";

    private static final long ANALYSIS_DATE = 123456789L;

    @Rule
    public DbTester db = DbTester.create(INSTANCE);

    @Rule
    public BatchReportReaderRule reportReader = new BatchReportReaderRule();

    @Rule
    public MutableAnalysisMetadataHolderRule analysisMetadataHolder = new MutableAnalysisMetadataHolderRule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DbClient dbClient = db.getDbClient();

    private DefaultOrganizationProvider defaultOrganizationProvider = TestDefaultOrganizationProvider.from(db);

    private LoadReportAnalysisMetadataHolderStepTest.TestPluginRepository pluginRepository = new LoadReportAnalysisMetadataHolderStepTest.TestPluginRepository();

    private OrganizationFlags organizationFlags = Mockito.mock(OrganizationFlags.class);

    private ComponentDto project;

    private ComputationStep underTest;

    @Test
    public void set_root_component_ref() {
        reportReader.setMetadata(LoadReportAnalysisMetadataHolderStepTest.newBatchReportBuilder().setRootComponentRef(1).build());
        underTest.execute(new TestComputationStepContext());
        assertThat(analysisMetadataHolder.getRootComponentRef()).isEqualTo(1);
    }

    @Test
    public void set_analysis_date() {
        reportReader.setMetadata(LoadReportAnalysisMetadataHolderStepTest.newBatchReportBuilder().setAnalysisDate(LoadReportAnalysisMetadataHolderStepTest.ANALYSIS_DATE).build());
        underTest.execute(new TestComputationStepContext());
        assertThat(analysisMetadataHolder.getAnalysisDate()).isEqualTo(LoadReportAnalysisMetadataHolderStepTest.ANALYSIS_DATE);
    }

    @Test
    public void set_project_from_dto() {
        reportReader.setMetadata(LoadReportAnalysisMetadataHolderStepTest.newBatchReportBuilder().setRootComponentRef(1).build());
        underTest.execute(new TestComputationStepContext());
        Project project = analysisMetadataHolder.getProject();
        assertThat(project.getUuid()).isEqualTo(this.project.uuid());
        assertThat(project.getKey()).isEqualTo(this.project.getDbKey());
        assertThat(project.getName()).isEqualTo(this.project.name());
        assertThat(project.getDescription()).isEqualTo(this.project.description());
    }

    @Test
    public void set_cross_project_duplication_to_true() {
        reportReader.setMetadata(LoadReportAnalysisMetadataHolderStepTest.newBatchReportBuilder().setCrossProjectDuplicationActivated(true).build());
        underTest.execute(new TestComputationStepContext());
        assertThat(analysisMetadataHolder.isCrossProjectDuplicationEnabled()).isEqualTo(true);
    }

    @Test
    public void set_cross_project_duplication_to_false() {
        reportReader.setMetadata(LoadReportAnalysisMetadataHolderStepTest.newBatchReportBuilder().setCrossProjectDuplicationActivated(false).build());
        underTest.execute(new TestComputationStepContext());
        assertThat(analysisMetadataHolder.isCrossProjectDuplicationEnabled()).isEqualTo(false);
    }

    @Test
    public void set_cross_project_duplication_to_false_when_nothing_in_the_report() {
        reportReader.setMetadata(LoadReportAnalysisMetadataHolderStepTest.newBatchReportBuilder().build());
        underTest.execute(new TestComputationStepContext());
        assertThat(analysisMetadataHolder.isCrossProjectDuplicationEnabled()).isEqualTo(false);
    }

    @Test
    public void execute_fails_with_ISE_if_component_is_null_in_CE_task() {
        CeTask res = Mockito.mock(CeTask.class);
        Mockito.when(res.getComponent()).thenReturn(Optional.empty());
        Mockito.when(res.getOrganizationUuid()).thenReturn(defaultOrganizationProvider.get().getUuid());
        reportReader.setMetadata(Metadata.newBuilder().build());
        ComputationStep underTest = createStep(res);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("component missing on ce task");
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void execute_fails_with_MessageException_if_main_projectKey_is_null_in_CE_task() {
        CeTask res = Mockito.mock(CeTask.class);
        Optional<CeTask.Component> component = Optional.of(new CeTask.Component("main_prj_uuid", null, null));
        Mockito.when(res.getComponent()).thenReturn(component);
        Mockito.when(res.getMainComponent()).thenReturn(component);
        Mockito.when(res.getOrganizationUuid()).thenReturn(defaultOrganizationProvider.get().getUuid());
        reportReader.setMetadata(Metadata.newBuilder().build());
        ComputationStep underTest = createStep(res);
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("Compute Engine task main component key is null. Project with UUID main_prj_uuid must have been deleted since report was uploaded. Can not proceed.");
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void execute_fails_with_MessageException_if_projectKey_is_null_in_CE_task() {
        CeTask res = Mockito.mock(CeTask.class);
        Optional<CeTask.Component> component = Optional.of(new CeTask.Component("prj_uuid", null, null));
        Mockito.when(res.getComponent()).thenReturn(component);
        Mockito.when(res.getMainComponent()).thenReturn(Optional.of(new CeTask.Component("main_prj_uuid", "main_prj_key", null)));
        Mockito.when(res.getOrganizationUuid()).thenReturn(defaultOrganizationProvider.get().getUuid());
        reportReader.setMetadata(Metadata.newBuilder().build());
        ComputationStep underTest = createStep(res);
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("Compute Engine task component key is null. Project with UUID prj_uuid must have been deleted since report was uploaded. Can not proceed.");
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void execute_fails_with_MessageException_when_projectKey_in_report_is_different_from_componentKey_in_CE_task() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto otherProject = db.components().insertPublicProject(organization);
        reportReader.setMetadata(Metadata.newBuilder().setProjectKey(otherProject.getDbKey()).build());
        expectedException.expect(MessageException.class);
        expectedException.expectMessage((((("ProjectKey in report (" + (otherProject.getDbKey())) + ") is not consistent with projectKey under which the report has been submitted (") + (LoadReportAnalysisMetadataHolderStepTest.PROJECT_KEY)) + ")"));
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void execute_sets_branch_even_if_MessageException_is_thrown_because_projectKey_in_report_is_different_from_componentKey_in_CE_task() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto otherProject = db.components().insertPublicProject(organization);
        reportReader.setMetadata(Metadata.newBuilder().setProjectKey(otherProject.getDbKey()).build());
        try {
            underTest.execute(new TestComputationStepContext());
        } catch (MessageException e) {
            assertThat(analysisMetadataHolder.getBranch()).isNotNull();
        }
    }

    @Test
    public void execute_sets_analysis_date_even_if_MessageException_is_thrown_because_projectKey_is_different_from_componentKey_in_CE_task() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto otherProject = db.components().insertPublicProject(organization);
        reportReader.setMetadata(Metadata.newBuilder().setProjectKey(otherProject.getDbKey()).setAnalysisDate(LoadReportAnalysisMetadataHolderStepTest.ANALYSIS_DATE).build());
        try {
            underTest.execute(new TestComputationStepContext());
        } catch (MessageException e) {
            assertThat(analysisMetadataHolder.getAnalysisDate()).isEqualTo(LoadReportAnalysisMetadataHolderStepTest.ANALYSIS_DATE);
        }
    }

    @Test
    public void execute_fails_with_MessageException_when_report_has_no_organizationKey_but_does_not_belong_to_the_default_organization() {
        reportReader.setMetadata(LoadReportAnalysisMetadataHolderStepTest.newBatchReportBuilder().build());
        OrganizationDto nonDefaultOrganizationDto = db.organizations().insert();
        ComputationStep underTest = createStep(createCeTask(LoadReportAnalysisMetadataHolderStepTest.PROJECT_KEY, nonDefaultOrganizationDto.getUuid()));
        expectedException.expect(MessageException.class);
        expectedException.expectMessage((((("Report does not specify an OrganizationKey but it has been submitted to another organization (" + (nonDefaultOrganizationDto.getKey())) + ") than the default one (") + (db.getDefaultOrganization().getKey())) + ")"));
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void execute_set_organization_from_ce_task_when_organizationKey_is_not_set_in_report() {
        reportReader.setMetadata(LoadReportAnalysisMetadataHolderStepTest.newBatchReportBuilder().build());
        underTest.execute(new TestComputationStepContext());
        Organization organization = analysisMetadataHolder.getOrganization();
        OrganizationDto defaultOrganization = db.getDefaultOrganization();
        assertThat(organization.getUuid()).isEqualTo(defaultOrganization.getUuid());
        assertThat(organization.getKey()).isEqualTo(defaultOrganization.getKey());
        assertThat(organization.getName()).isEqualTo(defaultOrganization.getName());
    }

    @Test
    public void execute_ensures_that_report_has_quality_profiles_matching_the_project_organization() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPublicProject(organization);
        ScannerReport.Metadata.Builder metadataBuilder = LoadReportAnalysisMetadataHolderStepTest.newBatchReportBuilder();
        metadataBuilder.setOrganizationKey(organization.getKey()).setProjectKey(project.getDbKey());
        metadataBuilder.getMutableQprofilesPerLanguage().put("js", QProfile.newBuilder().setKey("p1").setName("Sonar way").setLanguage("js").build());
        reportReader.setMetadata(metadataBuilder.build());
        db.qualityProfiles().insert(organization, ( p) -> p.setLanguage("js").setKee("p1"));
        ComputationStep underTest = createStep(createCeTask(project.getDbKey(), organization.getUuid()));
        // no errors
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void execute_fails_with_MessageException_when_report_has_quality_profiles_on_other_organizations() {
        OrganizationDto organization1 = db.organizations().insert();
        OrganizationDto organization2 = db.organizations().insert();
        ComponentDto projectInOrg1 = db.components().insertPublicProject(organization1);
        ScannerReport.Metadata.Builder metadataBuilder = LoadReportAnalysisMetadataHolderStepTest.newBatchReportBuilder();
        metadataBuilder.setOrganizationKey(organization1.getKey()).setProjectKey(projectInOrg1.getDbKey());
        metadataBuilder.putQprofilesPerLanguage("js", QProfile.newBuilder().setKey("jsInOrg1").setName("Sonar way").setLanguage("js").build());
        metadataBuilder.putQprofilesPerLanguage("php", QProfile.newBuilder().setKey("phpInOrg2").setName("PHP way").setLanguage("php").build());
        reportReader.setMetadata(metadataBuilder.build());
        db.qualityProfiles().insert(organization1, ( p) -> p.setLanguage("js").setKee("jsInOrg1"));
        db.qualityProfiles().insert(organization2, ( p) -> p.setLanguage("php").setKee("phpInOrg2"));
        ComputationStep underTest = createStep(createCeTask(projectInOrg1.getDbKey(), organization1.getUuid()));
        expectedException.expect(MessageException.class);
        expectedException.expectMessage((("Quality profiles with following keys don't exist in organization [" + (organization1.getKey())) + "]: phpInOrg2"));
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void execute_does_not_fail_when_report_has_a_quality_profile_that_does_not_exist_anymore() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPublicProject(organization);
        ScannerReport.Metadata.Builder metadataBuilder = LoadReportAnalysisMetadataHolderStepTest.newBatchReportBuilder();
        metadataBuilder.setOrganizationKey(organization.getKey()).setProjectKey(project.getDbKey());
        metadataBuilder.putQprofilesPerLanguage("js", QProfile.newBuilder().setKey("p1").setName("Sonar way").setLanguage("js").build());
        reportReader.setMetadata(metadataBuilder.build());
        ComputationStep underTest = createStep(createCeTask(project.getDbKey(), organization.getUuid()));
        underTest.execute(new TestComputationStepContext());
    }

    @Test
    public void execute_read_plugins_from_report() {
        // the installed plugins
        pluginRepository.add(new PluginInfo("java"), new PluginInfo("customjava").setBasePlugin("java"), new PluginInfo("php"));
        // the plugins sent by scanner
        ScannerReport.Metadata.Builder metadataBuilder = LoadReportAnalysisMetadataHolderStepTest.newBatchReportBuilder();
        metadataBuilder.putPluginsByKey("java", Plugin.newBuilder().setKey("java").setUpdatedAt(10L).build());
        metadataBuilder.putPluginsByKey("php", Plugin.newBuilder().setKey("php").setUpdatedAt(20L).build());
        metadataBuilder.putPluginsByKey("customjava", Plugin.newBuilder().setKey("customjava").setUpdatedAt(30L).build());
        metadataBuilder.putPluginsByKey("uninstalled", Plugin.newBuilder().setKey("uninstalled").setUpdatedAt(40L).build());
        reportReader.setMetadata(metadataBuilder.build());
        underTest.execute(new TestComputationStepContext());
        Assertions.assertThat(analysisMetadataHolder.getScannerPluginsByKey().values()).extracting(ScannerPlugin::getKey, ScannerPlugin::getBasePluginKey, ScannerPlugin::getUpdatedAt).containsExactlyInAnyOrder(tuple("java", null, 10L), tuple("php", null, 20L), tuple("customjava", "java", 30L), tuple("uninstalled", null, 40L));
    }

    private static class TestPluginRepository implements PluginRepository {
        private final Map<String, PluginInfo> pluginsMap = new HashMap<>();

        void add(PluginInfo... plugins) {
            Arrays.stream(plugins).forEach(( p) -> pluginsMap.put(p.getKey(), p));
        }

        @Override
        public Collection<PluginInfo> getPluginInfos() {
            return pluginsMap.values();
        }

        @Override
        public PluginInfo getPluginInfo(String key) {
            if (!(pluginsMap.containsKey(key))) {
                throw new IllegalArgumentException();
            }
            return pluginsMap.get(key);
        }

        @Override
        public org.sonar.api.Plugin getPluginInstance(String key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasPlugin(String key) {
            return pluginsMap.containsKey(key);
        }
    }
}

