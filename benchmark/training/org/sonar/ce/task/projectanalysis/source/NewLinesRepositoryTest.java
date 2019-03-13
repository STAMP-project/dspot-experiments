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
package org.sonar.ce.task.projectanalysis.source;


import Component.Type.FILE;
import java.util.Optional;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.ce.task.projectanalysis.analysis.AnalysisMetadataHolderRule;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.period.Period;
import org.sonar.ce.task.projectanalysis.period.PeriodHolderRule;
import org.sonar.ce.task.projectanalysis.scm.ScmInfoRepositoryRule;


public class NewLinesRepositoryTest {
    private static final ReportComponent FILE = ReportComponent.builder(Component.Type.FILE, 1).build();

    @Rule
    public BatchReportReaderRule reader = new BatchReportReaderRule();

    @Rule
    public AnalysisMetadataHolderRule analysisMetadataHolder = new AnalysisMetadataHolderRule();

    @Rule
    public PeriodHolderRule periodHolder = new PeriodHolderRule();

    @Rule
    public ScmInfoRepositoryRule scmInfoRepository = new ScmInfoRepositoryRule();

    private NewLinesRepository repository = new NewLinesRepository(reader, analysisMetadataHolder, periodHolder, scmInfoRepository);

    @Test
    public void load_new_lines_from_report_if_available_and_pullrequest() {
        setPullRequest();
        createChangedLinesInReport(1, 2, 5);
        Optional<Set<Integer>> newLines = repository.getNewLines(NewLinesRepositoryTest.FILE);
        assertThat(newLines).isPresent();
        assertThat(newLines.get()).containsOnly(1, 2, 5);
        assertThat(repository.newLinesAvailable()).isTrue();
    }

    @Test
    public void calculate_new_lines_from_period() {
        periodHolder.setPeriod(new Period("", null, 1000L, ""));
        scmInfoRepository.setScmInfo(NewLinesRepositoryTest.FILE.getReportAttributes().getRef(), createChangesets(1100L, 900L, 1000L, 800L));
        Optional<Set<Integer>> newLines = repository.getNewLines(NewLinesRepositoryTest.FILE);
        assertThat(newLines).isPresent();
        assertThat(newLines.get()).containsOnly(1);
        assertThat(repository.newLinesAvailable()).isTrue();
    }

    @Test
    public void return_empty_if_no_period_and_not_pullrequest() {
        periodHolder.setPeriod(null);
        // even though we have lines in the report and scm data, nothing should be returned since we have no period
        createChangedLinesInReport(1, 2, 5);
        scmInfoRepository.setScmInfo(NewLinesRepositoryTest.FILE.getReportAttributes().getRef(), createChangesets(1100L, 900L, 1000L, 800L));
        Optional<Set<Integer>> newLines = repository.getNewLines(NewLinesRepositoryTest.FILE);
        assertThat(newLines).isNotPresent();
        assertThat(repository.newLinesAvailable()).isFalse();
    }

    @Test
    public void return_empty_if_no_report_and_no_scm_info() {
        periodHolder.setPeriod(new Period("", null, 1000L, ""));
        Optional<Set<Integer>> newLines = repository.getNewLines(NewLinesRepositoryTest.FILE);
        assertThat(newLines).isNotPresent();
        assertThat(repository.newLinesAvailable()).isTrue();
    }
}

