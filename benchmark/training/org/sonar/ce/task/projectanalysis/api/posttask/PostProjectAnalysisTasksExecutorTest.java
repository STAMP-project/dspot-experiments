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
package org.sonar.ce.task.projectanalysis.api.posttask;


import BranchImpl.Type.SHORT;
import CeTask.Component;
import PostProjectAnalysisTask.ProjectAnalysis;
import ScannerReport.ContextProperty;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.ce.posttask.Project;
import org.sonar.api.ce.posttask.QualityGate;
import org.sonar.api.ce.posttask.ScannerContext;
import org.sonar.api.utils.System2;
import org.sonar.ce.task.CeTask;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.analysis.Branch;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.ce.task.projectanalysis.component.DefaultBranchImpl;
import org.sonar.ce.task.projectanalysis.qualitygate.Condition;
import org.sonar.ce.task.projectanalysis.qualitygate.MutableQualityGateHolderRule;
import org.sonar.ce.task.projectanalysis.qualitygate.MutableQualityGateStatusHolderRule;
import org.sonar.ce.task.projectanalysis.qualitygate.org.sonar.api.ce.posttask.QualityGate;
import org.sonar.db.component.BranchType;


@RunWith(DataProviderRunner.class)
public class PostProjectAnalysisTasksExecutorTest {
    private static final long QUALITY_GATE_ID = 98451;

    private static final String QUALITY_GATE_NAME = "qualityGate name";

    private static final Condition CONDITION_1 = PostProjectAnalysisTasksExecutorTest.createCondition("metric key 1");

    private static final Condition CONDITION_2 = PostProjectAnalysisTasksExecutorTest.createCondition("metric key 2");

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule();

    @Rule
    public MutableQualityGateHolderRule qualityGateHolder = new MutableQualityGateHolderRule();

    @Rule
    public MutableQualityGateStatusHolderRule qualityGateStatusHolder = new MutableQualityGateStatusHolderRule();

    @Rule
    public BatchReportReaderRule reportReader = new BatchReportReaderRule();

    private String organizationUuid = "org1";

    private String organizationKey = (organizationUuid) + "_key";

    private String organizationName = (organizationUuid) + "_name";

    private System2 system2 = Mockito.mock(System2.class);

    private ArgumentCaptor<PostProjectAnalysisTask.ProjectAnalysis> projectAnalysisArgumentCaptor = ArgumentCaptor.forClass(ProjectAnalysis.class);

    private Component component = new CeTask.Component("component uuid", "component key", "component name");

    private CeTask ceTask = new CeTask.Builder().setOrganizationUuid(organizationUuid).setType("type").setUuid("uuid").setComponent(component).setMainComponent(component).build();

    private PostProjectAnalysisTask postProjectAnalysisTask = Mockito.mock(PostProjectAnalysisTask.class);

    private PostProjectAnalysisTasksExecutor underTest = new PostProjectAnalysisTasksExecutor(ceTask, analysisMetadataHolder, qualityGateHolder, qualityGateStatusHolder, reportReader, system2, new PostProjectAnalysisTask[]{ postProjectAnalysisTask });

    @Test
    public void ceTask_uuid_is_UUID_of_CeTask() {
        underTest.finished(true);
        Mockito.verify(postProjectAnalysisTask).finished(projectAnalysisArgumentCaptor.capture());
        assertThat(projectAnalysisArgumentCaptor.getValue().getCeTask().getId()).isEqualTo(ceTask.getUuid());
    }

    @Test
    public void project_uuid_key_and_name_come_from_CeTask() {
        underTest.finished(true);
        Mockito.verify(postProjectAnalysisTask).finished(projectAnalysisArgumentCaptor.capture());
        Project project = projectAnalysisArgumentCaptor.getValue().getProject();
        assertThat(project.getUuid()).isEqualTo(ceTask.getComponent().get().getUuid());
        assertThat(project.getKey()).isEqualTo(ceTask.getComponent().get().getKey().get());
        assertThat(project.getName()).isEqualTo(ceTask.getComponent().get().getName().get());
    }

    @Test
    public void date_comes_from_AnalysisMetadataHolder() {
        analysisMetadataHolder.setAnalysisDate(8465132498L);
        analysisMetadataHolder.setUuid(RandomStringUtils.randomAlphanumeric(40));
        underTest.finished(true);
        Mockito.verify(postProjectAnalysisTask).finished(projectAnalysisArgumentCaptor.capture());
        assertThat(projectAnalysisArgumentCaptor.getValue().getDate()).isEqualTo(new Date(analysisMetadataHolder.getAnalysisDate()));
    }

    @Test
    public void date_comes_from_system2_if_not_set_in_AnalysisMetadataHolder() {
        long now = 1999663L;
        Mockito.when(system2.now()).thenReturn(now);
        underTest.finished(false);
        Mockito.verify(postProjectAnalysisTask).finished(projectAnalysisArgumentCaptor.capture());
        assertThat(projectAnalysisArgumentCaptor.getValue().getDate()).isEqualTo(new Date(now));
    }

    @Test
    public void analysisDate_and_analysisUuid_comes_from_AnalysisMetadataHolder_when_set() {
        analysisMetadataHolder.setAnalysisDate(8465132498L);
        analysisMetadataHolder.setUuid(RandomStringUtils.randomAlphanumeric(40));
        underTest.finished(true);
        Mockito.verify(postProjectAnalysisTask).finished(projectAnalysisArgumentCaptor.capture());
        assertThat(projectAnalysisArgumentCaptor.getValue().getAnalysis().get().getDate()).isEqualTo(new Date(analysisMetadataHolder.getAnalysisDate()));
        assertThat(projectAnalysisArgumentCaptor.getValue().getAnalysis().get().getAnalysisUuid()).isEqualTo(analysisMetadataHolder.getUuid());
    }

    @Test
    public void analysis_is_empty_when_not_set_in_AnalysisMetadataHolder() {
        underTest.finished(false);
        Mockito.verify(postProjectAnalysisTask).finished(projectAnalysisArgumentCaptor.capture());
        assertThat(projectAnalysisArgumentCaptor.getValue().getAnalysis()).isEmpty();
    }

    @Test
    public void branch_is_empty_when_legacy_branch_implementation_is_used() {
        analysisMetadataHolder.setBranch(new DefaultBranchImpl("feature/foo"));
        underTest.finished(true);
        Mockito.verify(postProjectAnalysisTask).finished(projectAnalysisArgumentCaptor.capture());
        assertThat(projectAnalysisArgumentCaptor.getValue().getBranch()).isEmpty();
    }

    @Test
    public void branch_comes_from_AnalysisMetadataHolder_when_set() {
        analysisMetadataHolder.setBranch(new Branch() {
            @Override
            public BranchType getType() {
                return BranchType.SHORT;
            }

            @Override
            public boolean isMain() {
                return false;
            }

            @Override
            public boolean isLegacyFeature() {
                return false;
            }

            @Override
            public Optional<String> getMergeBranchUuid() {
                return Optional.empty();
            }

            @Override
            public String getName() {
                return "feature/foo";
            }

            @Override
            public boolean supportsCrossProjectCpd() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getPullRequestKey() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String generateKey(String projectKey, @Nullable
            String fileOrDirPath) {
                throw new UnsupportedOperationException();
            }
        });
        underTest.finished(true);
        Mockito.verify(postProjectAnalysisTask).finished(projectAnalysisArgumentCaptor.capture());
        org.sonar.api.ce.posttask.Branch branch = projectAnalysisArgumentCaptor.getValue().getBranch().get();
        assertThat(branch.isMain()).isFalse();
        assertThat(branch.getName()).hasValue("feature/foo");
        assertThat(branch.getType()).isEqualTo(SHORT);
    }

    @Test
    public void qualityGate_is_null_when_finished_method_argument_is_false() {
        underTest.finished(false);
        Mockito.verify(postProjectAnalysisTask).finished(projectAnalysisArgumentCaptor.capture());
        assertThat(projectAnalysisArgumentCaptor.getValue().getQualityGate()).isNull();
    }

    @Test
    public void qualityGate_is_populated_when_finished_method_argument_is_true() {
        underTest.finished(true);
        Mockito.verify(postProjectAnalysisTask).finished(projectAnalysisArgumentCaptor.capture());
        QualityGate qualityGate = projectAnalysisArgumentCaptor.getValue().getQualityGate();
        assertThat(qualityGate.getStatus()).isEqualTo(org.sonar.api.ce.posttask.QualityGate);
        assertThat(qualityGate.getId()).isEqualTo(String.valueOf(PostProjectAnalysisTasksExecutorTest.QUALITY_GATE_ID));
        assertThat(qualityGate.getName()).isEqualTo(PostProjectAnalysisTasksExecutorTest.QUALITY_GATE_NAME);
        assertThat(qualityGate.getConditions()).hasSize(2);
    }

    @Test
    public void scannerContext_loads_properties_from_scanner_report() {
        reportReader.putContextProperties(Arrays.asList(ContextProperty.newBuilder().setKey("foo").setValue("bar").build()));
        underTest.finished(true);
        Mockito.verify(postProjectAnalysisTask).finished(projectAnalysisArgumentCaptor.capture());
        ScannerContext scannerContext = projectAnalysisArgumentCaptor.getValue().getScannerContext();
        assertThat(scannerContext.getProperties()).containsExactly(entry("foo", "bar"));
    }
}

