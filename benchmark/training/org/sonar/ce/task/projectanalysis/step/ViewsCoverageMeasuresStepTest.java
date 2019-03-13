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
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.formula.coverage.LinesAndConditionsWithUncoveredMetricKeys;
import org.sonar.ce.task.projectanalysis.measure.MeasureRepositoryRule;
import org.sonar.ce.task.projectanalysis.metric.MetricRepositoryRule;


public class ViewsCoverageMeasuresStepTest {
    private static final int ROOT_REF = 1;

    private static final int SUBVIEW_REF = 12;

    private static final int SUB_SUBVIEW_REF = 121;

    private static final int PROJECTVIEW_1_REF = 1211;

    private static final int PROJECTVIEW_2_REF = 1212;

    private static final int PROJECTVIEW_3_REF = 123;

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public MetricRepositoryRule metricRepository = new MetricRepositoryRule().add(LINES_TO_COVER).add(CONDITIONS_TO_COVER).add(UNCOVERED_LINES).add(UNCOVERED_CONDITIONS).add(COVERAGE).add(BRANCH_COVERAGE).add(LINE_COVERAGE);

    @Rule
    public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

    CoverageMeasuresStep underTest = new CoverageMeasuresStep(treeRootHolder, metricRepository, measureRepository);

    @Test
    public void verify_aggregates_values_for_ut_lines_and_conditions() {
        LinesAndConditionsWithUncoveredMetricKeys metricKeys = new LinesAndConditionsWithUncoveredMetricKeys(CoreMetrics.LINES_TO_COVER_KEY, CoreMetrics.CONDITIONS_TO_COVER_KEY, CoreMetrics.UNCOVERED_LINES_KEY, CoreMetrics.UNCOVERED_CONDITIONS_KEY);
        verify_lines_and_conditions_aggregates_values(metricKeys);
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

