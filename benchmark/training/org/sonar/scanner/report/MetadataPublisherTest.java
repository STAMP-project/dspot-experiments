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
package org.sonar.scanner.report;


import BranchType.LONG;
import CoreProperties.PROJECT_BRANCH_PROPERTY;
import ScannerReport.Metadata;
import ScannerReport.Metadata.BranchType.SHORT;
import com.google.common.collect.ImmutableMap;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.batch.bootstrap.ProjectDefinition;
import org.sonar.api.batch.fs.internal.DefaultInputModule;
import org.sonar.api.batch.fs.internal.InputModuleHierarchy;
import org.sonar.api.batch.scm.ScmProvider;
import org.sonar.scanner.ProjectInfo;
import org.sonar.scanner.bootstrap.ScannerPlugin;
import org.sonar.scanner.bootstrap.ScannerPluginRepository;
import org.sonar.scanner.cpd.CpdSettings;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.protocol.output.ScannerReport.Metadata.Plugin;
import org.sonar.scanner.protocol.output.ScannerReportReader;
import org.sonar.scanner.protocol.output.ScannerReportWriter;
import org.sonar.scanner.rule.QProfile;
import org.sonar.scanner.rule.QualityProfiles;
import org.sonar.scanner.scan.ScanProperties;
import org.sonar.scanner.scan.branch.BranchConfiguration;
import org.sonar.scanner.scm.ScmConfiguration;


@RunWith(DataProviderRunner.class)
public class MetadataPublisherTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private DefaultInputModule rootModule;

    private MetadataPublisher underTest;

    private ScanProperties properties = Mockito.mock(ScanProperties.class);

    private QualityProfiles qProfiles = Mockito.mock(QualityProfiles.class);

    private ProjectInfo projectInfo = Mockito.mock(ProjectInfo.class);

    private CpdSettings cpdSettings = Mockito.mock(CpdSettings.class);

    private InputModuleHierarchy inputModuleHierarchy;

    private ScannerPluginRepository pluginRepository = Mockito.mock(ScannerPluginRepository.class);

    private BranchConfiguration branches;

    private ScmConfiguration scmConfiguration;

    private ScmProvider scmProvider = Mockito.mock(ScmProvider.class);

    @Test
    public void write_metadata() throws Exception {
        Date date = new Date();
        Mockito.when(qProfiles.findAll()).thenReturn(Arrays.asList(new QProfile("q1", "Q1", "java", date)));
        Mockito.when(pluginRepository.getPluginsByKey()).thenReturn(ImmutableMap.of("java", new ScannerPlugin("java", 12345L, null), "php", new ScannerPlugin("php", 45678L, null)));
        File outputDir = temp.newFolder();
        ScannerReportWriter writer = new ScannerReportWriter(outputDir);
        underTest.publish(writer);
        ScannerReportReader reader = new ScannerReportReader(outputDir);
        ScannerReport.Metadata metadata = reader.readMetadata();
        assertThat(metadata.getAnalysisDate()).isEqualTo(1234567L);
        assertThat(metadata.getProjectKey()).isEqualTo("root");
        assertThat(metadata.getModulesProjectRelativePathByKeyMap()).containsOnly(entry("module", "modulePath"), entry("root", ""));
        assertThat(metadata.getProjectVersion()).isEmpty();
        assertThat(metadata.getQprofilesPerLanguageMap()).containsOnly(entry("java", org.sonar.scanner.protocol.output.ScannerReport.Metadata.QProfile.newBuilder().setKey("q1").setName("Q1").setLanguage("java").setRulesUpdatedAt(date.getTime()).build()));
        assertThat(metadata.getPluginsByKey()).containsOnly(entry("java", Plugin.newBuilder().setKey("java").setUpdatedAt(12345).build()), entry("php", Plugin.newBuilder().setKey("php").setUpdatedAt(45678).build()));
    }

    @Test
    public void write_project_branch() throws Exception {
        Mockito.when(cpdSettings.isCrossProjectDuplicationEnabled()).thenReturn(false);
        ProjectDefinition projectDef = ProjectDefinition.create().setKey("foo").setProperty(PROJECT_BRANCH_PROPERTY, "myBranch");
        createPublisher(projectDef);
        File outputDir = temp.newFolder();
        ScannerReportWriter writer = new ScannerReportWriter(outputDir);
        underTest.publish(writer);
        ScannerReportReader reader = new ScannerReportReader(outputDir);
        ScannerReport.Metadata metadata = reader.readMetadata();
        assertThat(metadata.getAnalysisDate()).isEqualTo(1234567L);
        assertThat(metadata.getProjectKey()).isEqualTo("root");
        assertThat(metadata.getDeprecatedBranch()).isEqualTo("myBranch");
        assertThat(metadata.getCrossProjectDuplicationActivated()).isFalse();
    }

    @Test
    public void write_project_organization() throws Exception {
        Mockito.when(properties.organizationKey()).thenReturn(Optional.of("SonarSource"));
        File outputDir = temp.newFolder();
        ScannerReportWriter writer = new ScannerReportWriter(outputDir);
        underTest.publish(writer);
        ScannerReportReader reader = new ScannerReportReader(outputDir);
        ScannerReport.Metadata metadata = reader.readMetadata();
        assertThat(metadata.getOrganizationKey()).isEqualTo("SonarSource");
    }

    @Test
    public void write_long_lived_branch_info() throws Exception {
        String branchName = "long-lived";
        Mockito.when(branches.branchName()).thenReturn(branchName);
        Mockito.when(branches.branchType()).thenReturn(LONG);
        File outputDir = temp.newFolder();
        underTest.publish(new ScannerReportWriter(outputDir));
        ScannerReportReader reader = new ScannerReportReader(outputDir);
        ScannerReport.Metadata metadata = reader.readMetadata();
        assertThat(metadata.getBranchName()).isEqualTo(branchName);
        assertThat(metadata.getBranchType()).isEqualTo(ScannerReport.Metadata.BranchType.LONG);
    }

    @Test
    public void write_short_lived_branch_info() throws Exception {
        String branchName = "feature";
        String branchTarget = "short-lived";
        Mockito.when(branches.branchName()).thenReturn(branchName);
        Mockito.when(branches.longLivingSonarReferenceBranch()).thenReturn(branchTarget);
        File outputDir = temp.newFolder();
        underTest.publish(new ScannerReportWriter(outputDir));
        ScannerReportReader reader = new ScannerReportReader(outputDir);
        ScannerReport.Metadata metadata = reader.readMetadata();
        assertThat(metadata.getBranchName()).isEqualTo(branchName);
        assertThat(metadata.getBranchType()).isEqualTo(SHORT);
        assertThat(metadata.getMergeBranchName()).isEqualTo(branchTarget);
    }

    @Test
    public void write_project_basedir() throws Exception {
        String path = "some/dir";
        Path relativePathFromScmRoot = Paths.get(path);
        Mockito.when(scmProvider.relativePathFromScmRoot(ArgumentMatchers.any(Path.class))).thenReturn(relativePathFromScmRoot);
        File outputDir = temp.newFolder();
        underTest.publish(new ScannerReportWriter(outputDir));
        ScannerReportReader reader = new ScannerReportReader(outputDir);
        ScannerReport.Metadata metadata = reader.readMetadata();
        assertThat(metadata.getRelativePathFromScmRoot()).isEqualTo(path);
    }

    @Test
    public void write_revision_id() throws Exception {
        String revisionId = "some-sha1";
        Mockito.when(scmProvider.revisionId(ArgumentMatchers.any(Path.class))).thenReturn(revisionId);
        File outputDir = temp.newFolder();
        underTest.publish(new ScannerReportWriter(outputDir));
        ScannerReportReader reader = new ScannerReportReader(outputDir);
        ScannerReport.Metadata metadata = reader.readMetadata();
        assertThat(metadata.getScmRevisionId()).isEqualTo(revisionId);
    }

    @Test
    public void should_not_crash_when_scm_provider_does_not_support_relativePathFromScmRoot() throws IOException {
        String revisionId = "some-sha1";
        ScmProvider fakeScmProvider = new ScmProvider() {
            @Override
            public String key() {
                return "foo";
            }

            @Override
            public String revisionId(Path path) {
                return revisionId;
            }
        };
        Mockito.when(scmConfiguration.provider()).thenReturn(fakeScmProvider);
        File outputDir = temp.newFolder();
        underTest.publish(new ScannerReportWriter(outputDir));
        ScannerReportReader reader = new ScannerReportReader(outputDir);
        ScannerReport.Metadata metadata = reader.readMetadata();
        assertThat(metadata.getScmRevisionId()).isEqualTo(revisionId);
    }

    @Test
    public void should_not_crash_when_scm_provider_does_not_support_revisionId() throws IOException {
        String relativePathFromScmRoot = "some/path";
        ScmProvider fakeScmProvider = new ScmProvider() {
            @Override
            public String key() {
                return "foo";
            }

            @Override
            public Path relativePathFromScmRoot(Path path) {
                return Paths.get(relativePathFromScmRoot);
            }
        };
        Mockito.when(scmConfiguration.provider()).thenReturn(fakeScmProvider);
        File outputDir = temp.newFolder();
        underTest.publish(new ScannerReportWriter(outputDir));
        ScannerReportReader reader = new ScannerReportReader(outputDir);
        ScannerReport.Metadata metadata = reader.readMetadata();
        assertThat(metadata.getRelativePathFromScmRoot()).isEqualTo(relativePathFromScmRoot);
    }
}

