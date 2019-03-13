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


import CoreMetrics.BRANCH_COVERAGE;
import CoreMetrics.CONDITIONS_TO_COVER;
import CoreMetrics.COVERAGE;
import CoreMetrics.LINES_TO_COVER;
import CoreMetrics.LINE_COVERAGE;
import CoreMetrics.UNCOVERED_CONDITIONS;
import CoreMetrics.UNCOVERED_LINES;
import ScannerReport.LineCoverage;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.ce.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.formula.coverage.LinesAndConditionsWithUncoveredMetricKeys;
import org.sonar.ce.task.projectanalysis.measure.Measure;
import org.sonar.ce.task.projectanalysis.measure.MeasureRepoEntry;
import org.sonar.ce.task.projectanalysis.measure.MeasureRepositoryRule;
import org.sonar.ce.task.projectanalysis.metric.MetricRepositoryRule;
import org.sonar.ce.task.step.TestComputationStepContext;


public class ReportCoverageMeasuresStepTest {
    private static final int ROOT_REF = 1;

    private static final int DIRECTORY_REF = 1234;

    private static final int FILE_1_REF = 12341;

    private static final int UNIT_TEST_FILE_REF = 12342;

    private static final int FILE_2_REF = 12343;

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public MetricRepositoryRule metricRepository = new MetricRepositoryRule().add(LINES_TO_COVER).add(CONDITIONS_TO_COVER).add(UNCOVERED_LINES).add(UNCOVERED_CONDITIONS).add(COVERAGE).add(BRANCH_COVERAGE).add(LINE_COVERAGE);

    @Rule
    public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

    @Rule
    public BatchReportReaderRule reportReader = new BatchReportReaderRule();

    CoverageMeasuresStep underTest = new CoverageMeasuresStep(treeRootHolder, metricRepository, measureRepository, reportReader);

    @Test
    public void verify_aggregates_values_for_lines_and_conditions() {
        reportReader.putCoverage(ReportCoverageMeasuresStepTest.FILE_1_REF, Arrays.asList(LineCoverage.newBuilder().setLine(2).setHits(false).build(), LineCoverage.newBuilder().setLine(3).setHits(true).build(), LineCoverage.newBuilder().setLine(4).setHits(true).setConditions(4).setCoveredConditions(1).build(), LineCoverage.newBuilder().setLine(5).setConditions(8).setCoveredConditions(2).build(), LineCoverage.newBuilder().setLine(6).setHits(false).setConditions(3).setCoveredConditions(0).build(), LineCoverage.newBuilder().setLine(7).setHits(false).build()));
        reportReader.putCoverage(ReportCoverageMeasuresStepTest.FILE_2_REF, Arrays.asList(LineCoverage.newBuilder().setLine(2).setHits(true).build(), LineCoverage.newBuilder().setLine(3).setHits(false).build(), LineCoverage.newBuilder().setLine(5).setHits(true).setConditions(5).setCoveredConditions(1).build(), LineCoverage.newBuilder().setLine(6).setConditions(10).setCoveredConditions(3).build(), LineCoverage.newBuilder().setLine(7).setHits(false).setConditions(1).setCoveredConditions(0).build(), LineCoverage.newBuilder().setLine(8).setHits(false).build()));
        underTest.execute(new TestComputationStepContext());
        MeasureRepoEntry[] nonFileRepoEntries = new MeasureRepoEntry[]{ MeasureRepoEntry.entryOf(CoreMetrics.LINES_TO_COVER_KEY, Measure.newMeasureBuilder().create((5 + 5))), MeasureRepoEntry.entryOf(CoreMetrics.CONDITIONS_TO_COVER_KEY, Measure.newMeasureBuilder().create((((((4 + 8) + 3) + 5) + 10) + 1))), MeasureRepoEntry.entryOf(CoreMetrics.UNCOVERED_LINES_KEY, Measure.newMeasureBuilder().create((3 + 3))), MeasureRepoEntry.entryOf(CoreMetrics.UNCOVERED_CONDITIONS_KEY, Measure.newMeasureBuilder().create((((((3 + 6) + 3) + 4) + 7) + 1))) };
        assertThat(MeasureRepoEntry.toEntries(measureRepository.getAddedRawMeasures(ReportCoverageMeasuresStepTest.DIRECTORY_REF))).contains(nonFileRepoEntries);
        assertThat(MeasureRepoEntry.toEntries(measureRepository.getAddedRawMeasures(ReportCoverageMeasuresStepTest.ROOT_REF))).contains(nonFileRepoEntries);
    }

    @Test
    public void verify_aggregates_values_for_code_line_and_branch_coverage() {
        LinesAndConditionsWithUncoveredMetricKeys metricKeys = new LinesAndConditionsWithUncoveredMetricKeys(CoreMetrics.LINES_TO_COVER_KEY, CoreMetrics.CONDITIONS_TO_COVER_KEY, CoreMetrics.UNCOVERED_LINES_KEY, CoreMetrics.UNCOVERED_CONDITIONS_KEY);
        String codeCoverageKey = CoreMetrics.COVERAGE_KEY;
        String lineCoverageKey = CoreMetrics.LINE_COVERAGE_KEY;
        String branchCoverageKey = CoreMetrics.BRANCH_COVERAGE_KEY;
        verify_coverage_aggregates_values(metricKeys, codeCoverageKey, lineCoverageKey, branchCoverageKey);
    }
}

