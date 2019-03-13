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
package org.sonar.scanner.sensor;


import CoreMetrics.LINES;
import CoreMetrics.NCLOC;
import CoreMetrics.NCLOC_KEY;
import FileStructure.Domain.SGNIFICANT_CODE;
import FileStructure.Domain.SYNTAX_HIGHLIGHTINGS;
import InputFile.Status.SAME;
import ScannerReport.Measure;
import TypeOfText.KEYWORD;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.sonar.api.batch.bootstrap.ProjectDefinition;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputDir;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.DefaultInputModule;
import org.sonar.api.batch.fs.internal.DefaultInputProject;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.code.internal.DefaultSignificantCode;
import org.sonar.api.batch.sensor.highlighting.internal.DefaultHighlighting;
import org.sonar.api.batch.sensor.issue.ExternalIssue;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.batch.sensor.issue.internal.DefaultExternalIssue;
import org.sonar.api.batch.sensor.issue.internal.DefaultIssue;
import org.sonar.api.batch.sensor.issue.internal.DefaultIssueLocation;
import org.sonar.api.batch.sensor.measure.internal.DefaultMeasure;
import org.sonar.api.batch.sensor.symbol.internal.DefaultSymbolTable;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.scanner.issue.IssuePublisher;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.protocol.output.ScannerReportReader;
import org.sonar.scanner.protocol.output.ScannerReportWriter;
import org.sonar.scanner.report.ReportPublisher;
import org.sonar.scanner.repository.ContextPropertiesCache;
import org.sonar.scanner.scan.branch.BranchConfiguration;


public class DefaultSensorStorageTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DefaultSensorStorage underTest;

    private MapSettings settings;

    private IssuePublisher moduleIssues;

    private ScannerReportWriter reportWriter;

    private ContextPropertiesCache contextPropertiesCache = new ContextPropertiesCache();

    private BranchConfiguration branchConfiguration;

    private DefaultInputProject project;

    private ScannerReportReader reportReader;

    private ReportPublisher reportPublisher;

    @Test
    public void shouldFailIfUnknownMetric() {
        InputFile file = new TestInputFileBuilder("foo", "src/Foo.php").build();
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Unknown metric: lines");
        underTest.store(new DefaultMeasure().on(file).forMetric(LINES).withValue(10));
    }

    @Test
    public void shouldIgnoreMeasuresOnFolders() {
        underTest.store(new DefaultMeasure().on(new DefaultInputDir("foo", "bar")).forMetric(LINES).withValue(10));
        Mockito.verifyNoMoreInteractions(reportPublisher);
    }

    @Test
    public void shouldIgnoreMeasuresOnModules() throws IOException {
        ProjectDefinition module = ProjectDefinition.create().setBaseDir(temp.newFolder()).setWorkDir(temp.newFolder());
        ProjectDefinition root = ProjectDefinition.create().addSubProject(module);
        underTest.store(new DefaultMeasure().on(new DefaultInputModule(module)).forMetric(LINES).withValue(10));
        Mockito.verifyNoMoreInteractions(reportPublisher);
    }

    @Test
    public void should_save_issue() {
        InputFile file = new TestInputFileBuilder("foo", "src/Foo.php").build();
        DefaultIssue issue = new DefaultIssue(project).at(new DefaultIssueLocation().on(file));
        underTest.store(issue);
        ArgumentCaptor<Issue> argumentCaptor = ArgumentCaptor.forClass(Issue.class);
        Mockito.verify(moduleIssues).initAndAddIssue(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(issue);
    }

    @Test
    public void should_save_external_issue() {
        InputFile file = new TestInputFileBuilder("foo", "src/Foo.php").build();
        DefaultExternalIssue externalIssue = new DefaultExternalIssue(project).at(new DefaultIssueLocation().on(file));
        underTest.store(externalIssue);
        ArgumentCaptor<ExternalIssue> argumentCaptor = ArgumentCaptor.forClass(ExternalIssue.class);
        Mockito.verify(moduleIssues).initAndAddExternalIssue(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo(externalIssue);
    }

    @Test
    public void should_skip_issue_on_short_branch_when_file_status_is_SAME() {
        InputFile file = new TestInputFileBuilder("foo", "src/Foo.php").setStatus(SAME).build();
        Mockito.when(branchConfiguration.isShortOrPullRequest()).thenReturn(true);
        DefaultIssue issue = new DefaultIssue(project).at(new DefaultIssueLocation().on(file));
        underTest.store(issue);
        Mockito.verifyZeroInteractions(moduleIssues);
    }

    @Test
    public void should_save_highlighting() {
        DefaultInputFile file = new TestInputFileBuilder("foo", "src/Foo.php").setContents("// comment").build();
        DefaultHighlighting highlighting = new DefaultHighlighting(underTest).onFile(file).highlight(0, 1, KEYWORD);
        underTest.store(highlighting);
        assertThat(reportWriter.hasComponentData(SYNTAX_HIGHLIGHTINGS, file.scannerId())).isTrue();
    }

    @Test
    public void should_skip_highlighting_on_short_branch_when_file_status_is_SAME() {
        DefaultInputFile file = new TestInputFileBuilder("foo", "src/Foo.php").setContents("// comment").setStatus(SAME).build();
        Mockito.when(branchConfiguration.isShortOrPullRequest()).thenReturn(true);
        DefaultHighlighting highlighting = new DefaultHighlighting(underTest).onFile(file).highlight(0, 1, KEYWORD);
        underTest.store(highlighting);
        assertThat(reportWriter.hasComponentData(SYNTAX_HIGHLIGHTINGS, file.scannerId())).isFalse();
    }

    @Test
    public void should_save_file_measure() {
        DefaultInputFile file = new TestInputFileBuilder("foo", "src/Foo.php").build();
        underTest.store(new DefaultMeasure().on(file).forMetric(NCLOC).withValue(10));
        ScannerReport.Measure m = reportReader.readComponentMeasures(file.scannerId()).next();
        assertThat(m.getIntValue().getValue()).isEqualTo(10);
        assertThat(m.getMetricKey()).isEqualTo(NCLOC_KEY);
    }

    @Test
    public void should_not_skip_file_measures_on_short_lived_branch_or_pull_request_when_file_status_is_SAME() {
        DefaultInputFile file = new TestInputFileBuilder("foo", "src/Foo.php").setStatus(SAME).build();
        Mockito.when(branchConfiguration.isShortOrPullRequest()).thenReturn(true);
        underTest.store(new DefaultMeasure().on(file).forMetric(NCLOC).withValue(10));
        ScannerReport.Measure m = reportReader.readComponentMeasures(file.scannerId()).next();
        assertThat(m.getIntValue().getValue()).isEqualTo(10);
        assertThat(m.getMetricKey()).isEqualTo(NCLOC_KEY);
    }

    @Test
    public void should_skip_significant_code_on_pull_request_when_file_status_is_SAME() {
        DefaultInputFile file = new TestInputFileBuilder("foo", "src/Foo.php").setStatus(SAME).setContents("foo").build();
        Mockito.when(branchConfiguration.isShortOrPullRequest()).thenReturn(true);
        underTest.store(new DefaultSignificantCode().onFile(file).addRange(file.selectLine(1)));
        assertThat(reportWriter.hasComponentData(SGNIFICANT_CODE, file.scannerId())).isFalse();
    }

    @Test
    public void should_save_significant_code() {
        DefaultInputFile file = new TestInputFileBuilder("foo", "src/Foo.php").setContents("foo").build();
        underTest.store(new DefaultSignificantCode().onFile(file).addRange(file.selectLine(1)));
        assertThat(reportWriter.hasComponentData(SGNIFICANT_CODE, file.scannerId())).isTrue();
    }

    @Test
    public void should_save_project_measure() throws IOException {
        String projectKey = "myProject";
        DefaultInputModule module = new DefaultInputModule(ProjectDefinition.create().setKey(projectKey).setBaseDir(temp.newFolder()).setWorkDir(temp.newFolder()));
        underTest.store(new DefaultMeasure().on(module).forMetric(NCLOC).withValue(10));
        ScannerReport.Measure m = reportReader.readComponentMeasures(module.scannerId()).next();
        assertThat(m.getIntValue().getValue()).isEqualTo(10);
        assertThat(m.getMetricKey()).isEqualTo(NCLOC_KEY);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void duplicateHighlighting() throws Exception {
        InputFile inputFile = new TestInputFileBuilder("foo", "src/Foo.java").setModuleBaseDir(temp.newFolder().toPath()).build();
        DefaultHighlighting h = new DefaultHighlighting(null).onFile(inputFile);
        underTest.store(h);
        underTest.store(h);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void duplicateSignificantCode() throws Exception {
        InputFile inputFile = new TestInputFileBuilder("foo", "src/Foo.java").setModuleBaseDir(temp.newFolder().toPath()).build();
        DefaultSignificantCode h = new DefaultSignificantCode(null).onFile(inputFile);
        underTest.store(h);
        underTest.store(h);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void duplicateSymbolTable() throws Exception {
        InputFile inputFile = new TestInputFileBuilder("foo", "src/Foo.java").setModuleBaseDir(temp.newFolder().toPath()).build();
        DefaultSymbolTable st = new DefaultSymbolTable(null).onFile(inputFile);
        underTest.store(st);
        underTest.store(st);
    }

    @Test
    public void shouldStoreContextProperty() {
        underTest.storeProperty("foo", "bar");
        assertThat(contextPropertiesCache.getAll()).containsOnly(entry("foo", "bar"));
    }
}

