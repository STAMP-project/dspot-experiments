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
package org.sonar.scanner.mediumtest.branch;


import BranchType.SHORT;
import ScannerReport.Metadata;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.scanner.mediumtest.AnalysisResult;
import org.sonar.scanner.mediumtest.ScannerMediumTester;
import org.sonar.scanner.protocol.output.ScannerReport;
import org.sonar.xoo.XooPlugin;
import org.sonar.xoo.rule.XooRulesDefinition;


public class BranchMediumTest {
    private static final String PROJECT_KEY = "sample";

    private static final String FILE_PATH = "HelloJava.xoo";

    private static final String FILE_CONTENT = "xoooo";

    private File baseDir;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ScannerMediumTester tester = new ScannerMediumTester().registerPlugin("xoo", new XooPlugin()).addDefaultQProfile("xoo", "Sonar Way").addRules(new XooRulesDefinition()).addActiveRule("xoo", "OneIssuePerLine", null, "One issue per line", "MAJOR", "OneIssuePerLine.internal", "xoo");

    @Test
    public void should_not_skip_report_for_unchanged_files_in_short_branch() {
        // sanity check, normally report gets generated
        AnalysisResult result = getResult(tester);
        final DefaultInputFile file = ((DefaultInputFile) (result.inputFile(BranchMediumTest.FILE_PATH)));
        assertThat(getResult(tester).getReportComponent(file)).isNotNull();
        int fileId = file.scannerId();
        assertThat(result.getReportReader().readChangesets(fileId)).isNotNull();
        assertThat(result.getReportReader().hasCoverage(fileId)).isTrue();
        assertThat(result.getReportReader().readFileSource(fileId)).isNotNull();
        // file is not skipped for short branches (need coverage, duplications coming soon)
        AnalysisResult result2 = getResult(tester.setBranchType(SHORT));
        final DefaultInputFile fileOnShortBranch = ((DefaultInputFile) (result2.inputFile(BranchMediumTest.FILE_PATH)));
        assertThat(result2.getReportComponent(fileOnShortBranch)).isNotNull();
        fileId = fileOnShortBranch.scannerId();
        assertThat(result2.getReportReader().readChangesets(fileId)).isNull();
        assertThat(result2.getReportReader().hasCoverage(fileId)).isTrue();
        assertThat(result2.getReportReader().readFileSource(fileId)).isNull();
    }

    @Test
    public void verify_metadata() {
        String branchName = "feature";
        String branchTarget = "branch-1.x";
        AnalysisResult result = getResult(tester.setBranchName(branchName).setBranchTarget(branchTarget).setLongLivingSonarReferenceBranch(branchTarget).setBranchType(SHORT));
        ScannerReport.Metadata metadata = result.getReportReader().readMetadata();
        assertThat(metadata.getBranchName()).isEqualTo(branchName);
        assertThat(metadata.getBranchType()).isEqualTo(ScannerReport.Metadata.BranchType.SHORT);
        assertThat(metadata.getMergeBranchName()).isEqualTo(branchTarget);
    }
}

