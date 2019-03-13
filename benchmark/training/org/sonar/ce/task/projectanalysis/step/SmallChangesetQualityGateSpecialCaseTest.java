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


import CoreMetrics.NEW_BUGS;
import CoreMetrics.NEW_COVERAGE;
import CoreMetrics.NEW_LINES;
import CoreMetrics.NEW_LINES_KEY;
import QualityGateMeasuresStep.MetricEvaluationResult;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.measure.Measure;
import org.sonar.ce.task.projectanalysis.measure.MeasureRepositoryRule;
import org.sonar.ce.task.projectanalysis.metric.MetricRepositoryRule;
import org.sonar.ce.task.projectanalysis.qualitygate.Condition;

import static Measure.Level.ERROR;


public class SmallChangesetQualityGateSpecialCaseTest {
    public static final int PROJECT_REF = 1234;

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public MetricRepositoryRule metricRepository = new MetricRepositoryRule().add(NEW_LINES).add(NEW_COVERAGE).add(NEW_BUGS);

    @Rule
    public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

    private final SmallChangesetQualityGateSpecialCase underTest = new SmallChangesetQualityGateSpecialCase(measureRepository, metricRepository);

    @Test
    public void ignore_errors_about_new_coverage_for_small_changesets() {
        QualityGateMeasuresStep.MetricEvaluationResult metricEvaluationResult = generateEvaluationResult(CoreMetrics.NEW_COVERAGE_KEY, Level.ERROR);
        Component project = generateNewRootProject();
        measureRepository.addRawMeasure(SmallChangesetQualityGateSpecialCaseTest.PROJECT_REF, NEW_LINES_KEY, Measure.newMeasureBuilder().setVariation(19).create(1000));
        boolean result = underTest.appliesTo(project, metricEvaluationResult);
        assertThat(result).isTrue();
    }

    @Test
    public void should_not_change_for_bigger_changesets() {
        QualityGateMeasuresStep.MetricEvaluationResult metricEvaluationResult = generateEvaluationResult(CoreMetrics.NEW_COVERAGE_KEY, Level.ERROR);
        Component project = generateNewRootProject();
        measureRepository.addRawMeasure(SmallChangesetQualityGateSpecialCaseTest.PROJECT_REF, NEW_LINES_KEY, Measure.newMeasureBuilder().setVariation(20).create(1000));
        boolean result = underTest.appliesTo(project, metricEvaluationResult);
        assertThat(result).isFalse();
    }

    @Test
    public void should_not_change_issue_related_metrics() {
        QualityGateMeasuresStep.MetricEvaluationResult metricEvaluationResult = generateEvaluationResult(CoreMetrics.NEW_BUGS_KEY, Level.ERROR);
        Component project = generateNewRootProject();
        measureRepository.addRawMeasure(SmallChangesetQualityGateSpecialCaseTest.PROJECT_REF, NEW_LINES_KEY, Measure.newMeasureBuilder().setVariation(19).create(1000));
        boolean result = underTest.appliesTo(project, metricEvaluationResult);
        assertThat(result).isFalse();
    }

    @Test
    public void should_not_change_green_conditions() {
        QualityGateMeasuresStep.MetricEvaluationResult metricEvaluationResult = generateEvaluationResult(CoreMetrics.NEW_BUGS_KEY, Level.OK);
        Component project = generateNewRootProject();
        measureRepository.addRawMeasure(SmallChangesetQualityGateSpecialCaseTest.PROJECT_REF, NEW_LINES_KEY, Measure.newMeasureBuilder().setVariation(19).create(1000));
        boolean result = underTest.appliesTo(project, metricEvaluationResult);
        assertThat(result).isFalse();
    }

    @Test
    public void should_not_change_quality_gate_if_new_lines_is_not_defined() {
        QualityGateMeasuresStep.MetricEvaluationResult metricEvaluationResult = generateEvaluationResult(CoreMetrics.NEW_COVERAGE_KEY, Level.ERROR);
        Component project = generateNewRootProject();
        boolean result = underTest.appliesTo(project, metricEvaluationResult);
        assertThat(result).isFalse();
    }

    @Test
    public void should_silently_ignore_null_values() {
        boolean result = underTest.appliesTo(Mockito.mock(Component.class), null);
        assertThat(result).isFalse();
    }

    @Test
    public void apply() {
        Comparable<?> value = Mockito.mock(Comparable.class);
        Condition condition = Mockito.mock(Condition.class);
        QualityGateMeasuresStep.MetricEvaluationResult original = new QualityGateMeasuresStep.MetricEvaluationResult(new org.sonar.ce.task.projectanalysis.qualitygate.EvaluationResult(ERROR, value), condition);
        QualityGateMeasuresStep.MetricEvaluationResult modified = underTest.apply(original);
        assertThat(modified.evaluationResult.getLevel()).isSameAs(Level.OK);
        assertThat(modified.evaluationResult.getValue()).isSameAs(value);
        assertThat(modified.condition).isSameAs(condition);
    }
}

