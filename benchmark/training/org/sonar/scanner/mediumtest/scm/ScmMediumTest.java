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
package org.sonar.scanner.mediumtest.scm;


import ScannerReport.Changesets;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.utils.log.LogTester;
import org.sonar.scanner.mediumtest.ScannerMediumTester;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.scanner.protocol.output.ScannerReport.Changesets.Changeset;
import org.sonar.scanner.repository.FileData;
import org.sonar.xoo.XooPlugin;
import org.sonar.xoo.rule.XooRulesDefinition;


public class ScmMediumTest {
    private static final String MISSING_BLAME_INFORMATION_FOR_THE_FOLLOWING_FILES = "Missing blame information for the following files:";

    private static final String CHANGED_CONTENT_SCM_ON_SERVER_XOO = "src/changed_content_scm_on_server.xoo";

    private static final String NO_BLAME_SCM_ON_SERVER_XOO = "src/no_blame_scm_on_server.xoo";

    private static final String SAME_CONTENT_SCM_ON_SERVER_XOO = "src/same_content_scm_on_server.xoo";

    private static final String SAME_CONTENT_NO_SCM_ON_SERVER_XOO = "src/same_content_no_scm_on_server.xoo";

    private static final String SAMPLE_XOO_CONTENT = "Sample xoo\ncontent";

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public ScannerMediumTester tester = // active a rule just to be sure that xoo files are published
    new ScannerMediumTester().registerPlugin("xoo", new XooPlugin()).addDefaultQProfile("xoo", "Sonar Way").addRules(new XooRulesDefinition()).addActiveRule("xoo", "xoo:OneIssuePerFile", null, "One Issue Per File", null, null, null).addFileData(ScmMediumTest.CHANGED_CONTENT_SCM_ON_SERVER_XOO, new FileData(DigestUtils.md5Hex(ScmMediumTest.SAMPLE_XOO_CONTENT), null)).addFileData(ScmMediumTest.SAME_CONTENT_NO_SCM_ON_SERVER_XOO, new FileData(DigestUtils.md5Hex(ScmMediumTest.SAMPLE_XOO_CONTENT), null)).addFileData(ScmMediumTest.SAME_CONTENT_SCM_ON_SERVER_XOO, new FileData(DigestUtils.md5Hex(ScmMediumTest.SAMPLE_XOO_CONTENT), "1.1")).addFileData(ScmMediumTest.NO_BLAME_SCM_ON_SERVER_XOO, new FileData(DigestUtils.md5Hex(ScmMediumTest.SAMPLE_XOO_CONTENT), "1.1"));

    @Test
    public void testScmMeasure() throws IOException, URISyntaxException {
        File baseDir = prepareProject();
        tester.newAnalysis().properties(ImmutableMap.<String, String>builder().put("sonar.projectBaseDir", baseDir.getAbsolutePath()).put("sonar.projectKey", "com.foo.project").put("sonar.sources", "src").put("sonar.scm.provider", "xoo").build()).execute();
        ScannerReport.Changesets fileScm = getChangesets(baseDir, "src/sample.xoo");
        assertThat(fileScm.getChangesetIndexByLineList()).hasSize(5);
        Changeset changesetLine1 = fileScm.getChangeset(fileScm.getChangesetIndexByLine(0));
        assertThat(changesetLine1.getAuthor()).isEmpty();
        Changeset changesetLine2 = fileScm.getChangeset(fileScm.getChangesetIndexByLine(1));
        assertThat(changesetLine2.getAuthor()).isEqualTo(getNonAsciiAuthor().toLowerCase());
        Changeset changesetLine3 = fileScm.getChangeset(fileScm.getChangesetIndexByLine(2));
        assertThat(changesetLine3.getAuthor()).isEqualTo("julien");
        Changeset changesetLine4 = fileScm.getChangeset(fileScm.getChangesetIndexByLine(3));
        assertThat(changesetLine4.getAuthor()).isEqualTo("julien");
        Changeset changesetLine5 = fileScm.getChangeset(fileScm.getChangesetIndexByLine(4));
        assertThat(changesetLine5.getAuthor()).isEqualTo("simon");
    }

    @Test
    public void noScmOnEmptyFile() throws IOException, URISyntaxException {
        File baseDir = prepareProject();
        // Clear file content
        FileUtils.write(new File(baseDir, "src/sample.xoo"), "");
        tester.newAnalysis().properties(ImmutableMap.<String, String>builder().put("sonar.task", "scan").put("sonar.projectBaseDir", baseDir.getAbsolutePath()).put("sonar.projectKey", "com.foo.project").put("sonar.projectName", "Foo Project").put("sonar.projectVersion", "1.0-SNAPSHOT").put("sonar.projectDescription", "Description of Foo Project").put("sonar.sources", "src").put("sonar.scm.provider", "xoo").build()).execute();
        ScannerReport.Changesets changesets = getChangesets(baseDir, "src/sample.xoo");
        assertThat(changesets).isNull();
    }

    @Test
    public void log_files_with_missing_blame() throws IOException, URISyntaxException {
        File baseDir = prepareProject();
        File xooFileWithoutBlame = new File(baseDir, "src/sample_no_blame.xoo");
        FileUtils.write(xooFileWithoutBlame, "Sample xoo\ncontent\n3\n4\n5");
        tester.newAnalysis().properties(ImmutableMap.<String, String>builder().put("sonar.task", "scan").put("sonar.projectBaseDir", baseDir.getAbsolutePath()).put("sonar.projectKey", "com.foo.project").put("sonar.projectName", "Foo Project").put("sonar.projectVersion", "1.0-SNAPSHOT").put("sonar.projectDescription", "Description of Foo Project").put("sonar.sources", "src").put("sonar.scm.provider", "xoo").build()).execute();
        ScannerReport.Changesets file1Scm = getChangesets(baseDir, "src/sample.xoo");
        assertThat(file1Scm).isNotNull();
        ScannerReport.Changesets fileWithoutBlameScm = getChangesets(baseDir, "src/sample_no_blame.xoo");
        assertThat(fileWithoutBlameScm).isNull();
        assertThat(logTester.logs()).containsSubsequence("2 files to be analyzed", "1/2 files analyzed", ScmMediumTest.MISSING_BLAME_INFORMATION_FOR_THE_FOLLOWING_FILES, "  * src/sample_no_blame.xoo");
    }

    // SONAR-6397
    @Test
    public void optimize_blame() throws IOException, URISyntaxException {
        File baseDir = prepareProject();
        File changedContentScmOnServer = new File(baseDir, ScmMediumTest.CHANGED_CONTENT_SCM_ON_SERVER_XOO);
        FileUtils.write(changedContentScmOnServer, ((ScmMediumTest.SAMPLE_XOO_CONTENT) + "\nchanged"));
        File xooScmFile = new File(baseDir, ((ScmMediumTest.CHANGED_CONTENT_SCM_ON_SERVER_XOO) + ".scm"));
        // revision,author,dateTime
        FileUtils.write(xooScmFile, ("1,foo,2013-01-04\n" + ("1,bar,2013-01-04\n" + "2,biz,2014-01-04\n")));
        File sameContentScmOnServer = new File(baseDir, ScmMediumTest.SAME_CONTENT_SCM_ON_SERVER_XOO);
        FileUtils.write(sameContentScmOnServer, ScmMediumTest.SAMPLE_XOO_CONTENT);
        // No need to write .scm file since this file should not be blamed
        File noBlameScmOnServer = new File(baseDir, ScmMediumTest.NO_BLAME_SCM_ON_SERVER_XOO);
        FileUtils.write(noBlameScmOnServer, ((ScmMediumTest.SAMPLE_XOO_CONTENT) + "\nchanged"));
        // No .scm file to emulate a failure during blame
        File sameContentNoScmOnServer = new File(baseDir, ScmMediumTest.SAME_CONTENT_NO_SCM_ON_SERVER_XOO);
        FileUtils.write(sameContentNoScmOnServer, ScmMediumTest.SAMPLE_XOO_CONTENT);
        xooScmFile = new File(baseDir, ((ScmMediumTest.SAME_CONTENT_NO_SCM_ON_SERVER_XOO) + ".scm"));
        // revision,author,dateTime
        FileUtils.write(xooScmFile, ("1,foo,2013-01-04\n" + "1,bar,2013-01-04\n"));
        tester.newAnalysis().properties(ImmutableMap.<String, String>builder().put("sonar.task", "scan").put("sonar.projectBaseDir", baseDir.getAbsolutePath()).put("sonar.projectKey", "com.foo.project").put("sonar.projectName", "Foo Project").put("sonar.projectVersion", "1.0-SNAPSHOT").put("sonar.projectDescription", "Description of Foo Project").put("sonar.sources", "src").put("sonar.scm.provider", "xoo").build()).execute();
        assertThat(getChangesets(baseDir, "src/sample.xoo")).isNotNull();
        assertThat(getChangesets(baseDir, ScmMediumTest.CHANGED_CONTENT_SCM_ON_SERVER_XOO).getCopyFromPrevious()).isFalse();
        assertThat(getChangesets(baseDir, ScmMediumTest.SAME_CONTENT_SCM_ON_SERVER_XOO).getCopyFromPrevious()).isTrue();
        assertThat(getChangesets(baseDir, ScmMediumTest.SAME_CONTENT_NO_SCM_ON_SERVER_XOO).getCopyFromPrevious()).isFalse();
        assertThat(getChangesets(baseDir, ScmMediumTest.NO_BLAME_SCM_ON_SERVER_XOO)).isNull();
        // 5 .xoo files + 3 .scm files, but only 4 marked for publishing. 1 file is SAME so not included in the total
        assertThat(logTester.logs()).containsSubsequence("8 files indexed");
        assertThat(logTester.logs()).containsSubsequence("4 files to be analyzed", "3/4 files analyzed");
        assertThat(logTester.logs()).containsSubsequence(ScmMediumTest.MISSING_BLAME_INFORMATION_FOR_THE_FOLLOWING_FILES, "  * src/no_blame_scm_on_server.xoo");
    }

    @Test
    public void forceReload() throws IOException, URISyntaxException {
        File baseDir = prepareProject();
        File xooFileNoScm = new File(baseDir, ScmMediumTest.SAME_CONTENT_SCM_ON_SERVER_XOO);
        FileUtils.write(xooFileNoScm, ScmMediumTest.SAMPLE_XOO_CONTENT);
        File xooScmFile = new File(baseDir, ((ScmMediumTest.SAME_CONTENT_SCM_ON_SERVER_XOO) + ".scm"));
        // revision,author,dateTime
        FileUtils.write(xooScmFile, ("1,foo,2013-01-04\n" + "1,bar,2013-01-04\n"));
        ScannerMediumTester.AnalysisBuilder analysisBuilder = tester.newAnalysis().properties(// Force reload
        ImmutableMap.<String, String>builder().put("sonar.task", "scan").put("sonar.projectBaseDir", baseDir.getAbsolutePath()).put("sonar.projectKey", "com.foo.project").put("sonar.projectName", "Foo Project").put("sonar.projectVersion", "1.0-SNAPSHOT").put("sonar.projectDescription", "Description of Foo Project").put("sonar.sources", "src").put("sonar.scm.provider", "xoo").put("sonar.scm.forceReloadAll", "true").build());
        analysisBuilder.execute();
        ScannerReport.Changesets file1Scm = getChangesets(baseDir, "src/sample.xoo");
        assertThat(file1Scm).isNotNull();
        ScannerReport.Changesets file2Scm = getChangesets(baseDir, ScmMediumTest.SAME_CONTENT_SCM_ON_SERVER_XOO);
        assertThat(file2Scm).isNotNull();
    }

    @Test
    public void configureUsingScmURL() throws IOException, URISyntaxException {
        File baseDir = prepareProject();
        tester.newAnalysis().properties(ImmutableMap.<String, String>builder().put("sonar.task", "scan").put("sonar.projectBaseDir", baseDir.getAbsolutePath()).put("sonar.projectKey", "com.foo.project").put("sonar.projectName", "Foo Project").put("sonar.projectVersion", "1.0-SNAPSHOT").put("sonar.projectDescription", "Description of Foo Project").put("sonar.sources", "src").put("sonar.links.scm_dev", "scm:xoo:foobar").build()).execute();
        ScannerReport.Changesets file1Scm = getChangesets(baseDir, "src/sample.xoo");
        assertThat(file1Scm).isNotNull();
    }

    @Test
    public void testAutoDetection() throws IOException, URISyntaxException {
        File baseDir = prepareProject();
        new File(baseDir, ".xoo").createNewFile();
        tester.newAnalysis().properties(ImmutableMap.<String, String>builder().put("sonar.task", "scan").put("sonar.projectBaseDir", baseDir.getAbsolutePath()).put("sonar.projectKey", "com.foo.project").put("sonar.projectName", "Foo Project").put("sonar.projectVersion", "1.0-SNAPSHOT").put("sonar.projectDescription", "Description of Foo Project").put("sonar.sources", "src").build()).execute();
        ScannerReport.Changesets file1Scm = getChangesets(baseDir, "src/sample.xoo");
        assertThat(file1Scm).isNotNull();
    }

    @Test
    public void testDisableScmSensor() throws IOException, URISyntaxException {
        File baseDir = prepareProject();
        tester.newAnalysis().properties(ImmutableMap.<String, String>builder().put("sonar.task", "scan").put("sonar.projectBaseDir", baseDir.getAbsolutePath()).put("sonar.projectKey", "com.foo.project").put("sonar.projectName", "Foo Project").put("sonar.projectVersion", "1.0-SNAPSHOT").put("sonar.projectDescription", "Description of Foo Project").put("sonar.sources", "src").put("sonar.scm.disabled", "true").put("sonar.scm.provider", "xoo").put("sonar.cpd.xoo.skip", "true").build()).execute();
        ScannerReport.Changesets changesets = getChangesets(baseDir, "src/sample.xoo");
        assertThat(changesets).isNull();
    }
}

