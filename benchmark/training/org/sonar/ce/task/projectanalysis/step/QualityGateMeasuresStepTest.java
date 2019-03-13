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


import Component.Type.DIRECTORY;
import Component.Type.PROJECT;
import ConditionStatus.EvaluationStatus;
import ConditionStatus.EvaluationStatus.NO_VALUE;
import QualityGateStatus.ERROR;
import QualityGateStatus.OK;
import com.google.common.collect.ImmutableList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.assertj.core.api.AbstractAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.ce.task.projectanalysis.measure.Measure;
import org.sonar.ce.task.projectanalysis.measure.MeasureAssert;
import org.sonar.ce.task.projectanalysis.measure.MeasureRepositoryRule;
import org.sonar.ce.task.projectanalysis.metric.MetricImpl;
import org.sonar.ce.task.projectanalysis.metric.MetricRepositoryRule;
import org.sonar.ce.task.projectanalysis.qualitygate.Condition;
import org.sonar.ce.task.projectanalysis.qualitygate.ConditionStatus;
import org.sonar.ce.task.projectanalysis.qualitygate.EvaluationResultTextConverter;
import org.sonar.ce.task.projectanalysis.qualitygate.MutableQualityGateStatusHolderRule;
import org.sonar.ce.task.projectanalysis.qualitygate.QualityGateHolderRule;
import org.sonar.ce.task.projectanalysis.qualitygate.QualityGateStatus;
import org.sonar.ce.task.projectanalysis.qualitygate.QualityGateStatusHolder;
import org.sonar.ce.task.step.TestComputationStepContext;


public class QualityGateMeasuresStepTest {
    private static final MetricImpl INT_METRIC_1 = QualityGateMeasuresStepTest.createIntMetric(1);

    private static final String INT_METRIC_1_KEY = QualityGateMeasuresStepTest.INT_METRIC_1.getKey();

    private static final MetricImpl INT_METRIC_2 = QualityGateMeasuresStepTest.createIntMetric(2);

    private static final String INT_METRIC_2_KEY = QualityGateMeasuresStepTest.INT_METRIC_2.getKey();

    private static final int PROJECT_REF = 1;

    private static final ReportComponent PROJECT_COMPONENT = ReportComponent.builder(PROJECT, QualityGateMeasuresStepTest.PROJECT_REF).build();

    private static final long SOME_QG_ID = 7521551;

    private static final String SOME_QG_NAME = "name";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();

    @Rule
    public QualityGateHolderRule qualityGateHolder = new QualityGateHolderRule();

    @Rule
    public MutableQualityGateStatusHolderRule qualityGateStatusHolder = new MutableQualityGateStatusHolderRule();

    @Rule
    public MetricRepositoryRule metricRepository = new MetricRepositoryRule();

    @Rule
    public MeasureRepositoryRule measureRepository = MeasureRepositoryRule.create(treeRootHolder, metricRepository);

    private EvaluationResultTextConverter resultTextConverter = Mockito.mock(EvaluationResultTextConverter.class);

    private QualityGateMeasuresStep underTest = new QualityGateMeasuresStep(treeRootHolder, qualityGateHolder, qualityGateStatusHolder, measureRepository, metricRepository, resultTextConverter, new SmallChangesetQualityGateSpecialCase(measureRepository, metricRepository));

    @Test
    public void no_measure_if_tree_has_no_project() {
        ReportComponent notAProjectComponent = ReportComponent.builder(DIRECTORY, 1).build();
        treeRootHolder.setRoot(notAProjectComponent);
        underTest.execute(new TestComputationStepContext());
        measureRepository.getAddedRawMeasures(1).isEmpty();
    }

    @Test
    public void no_measure_if_there_is_no_qualitygate() {
        qualityGateHolder.setQualityGate(null);
        underTest.execute(new TestComputationStepContext());
        measureRepository.getAddedRawMeasures(QualityGateMeasuresStepTest.PROJECT_COMPONENT).isEmpty();
    }

    @Test
    public void mutableQualityGateStatusHolder_is_not_populated_if_there_is_no_qualitygate() {
        qualityGateHolder.setQualityGate(null);
        underTest.execute(new TestComputationStepContext());
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Quality gate status has not been set yet");
        qualityGateStatusHolder.getStatus();
    }

    @Test
    public void new_measures_are_created_even_if_there_is_no_rawMeasure_for_metric_of_condition() {
        Condition equals2Condition = QualityGateMeasuresStepTest.createLessThanCondition(QualityGateMeasuresStepTest.INT_METRIC_1, "2");
        qualityGateHolder.setQualityGate(new org.sonar.ce.task.projectanalysis.qualitygate.QualityGate(QualityGateMeasuresStepTest.SOME_QG_ID, QualityGateMeasuresStepTest.SOME_QG_NAME, ImmutableList.of(equals2Condition)));
        underTest.execute(new TestComputationStepContext());
        Optional<Measure> addedRawMeasure = measureRepository.getAddedRawMeasure(QualityGateMeasuresStepTest.PROJECT_COMPONENT, QualityGateMeasuresStepTest.INT_METRIC_1_KEY);
        MeasureAssert.assertThat(addedRawMeasure).isAbsent();
        MeasureAssert.assertThat(getAlertStatusMeasure()).hasQualityGateLevel(Level.OK).hasQualityGateText("");
        MeasureAssert.assertThat(getQGDetailsMeasure()).hasValue(toJson());
        QualityGateMeasuresStepTest.QualityGateStatusHolderAssertions.assertThat(qualityGateStatusHolder).hasStatus(OK).hasConditionCount(1).hasCondition(equals2Condition, NO_VALUE, null);
    }

    @Test
    public void rawMeasure_is_updated_if_present_and_new_measures_are_created_if_project_has_measure_for_metric_of_condition() {
        int rawValue = 3;
        Condition equals2Condition = QualityGateMeasuresStepTest.createLessThanCondition(QualityGateMeasuresStepTest.INT_METRIC_1, "2");
        Measure rawMeasure = Measure.newMeasureBuilder().create(rawValue, null);
        qualityGateHolder.setQualityGate(new org.sonar.ce.task.projectanalysis.qualitygate.QualityGate(QualityGateMeasuresStepTest.SOME_QG_ID, QualityGateMeasuresStepTest.SOME_QG_NAME, ImmutableList.of(equals2Condition)));
        measureRepository.addRawMeasure(QualityGateMeasuresStepTest.PROJECT_REF, QualityGateMeasuresStepTest.INT_METRIC_1_KEY, rawMeasure);
        underTest.execute(new TestComputationStepContext());
        Optional<Measure> addedRawMeasure = measureRepository.getAddedRawMeasure(QualityGateMeasuresStepTest.PROJECT_COMPONENT, QualityGateMeasuresStepTest.INT_METRIC_1_KEY);
        MeasureAssert.assertThat(addedRawMeasure).hasQualityGateLevel(Level.OK).hasQualityGateText(QualityGateMeasuresStepTest.dumbResultTextAnswer(equals2Condition, Level.OK, rawValue));
        MeasureAssert.assertThat(getAlertStatusMeasure()).hasQualityGateLevel(Level.OK).hasQualityGateText(QualityGateMeasuresStepTest.dumbResultTextAnswer(equals2Condition, Level.OK, rawValue));
        MeasureAssert.assertThat(getQGDetailsMeasure().get()).hasValue(toJson());
        QualityGateMeasuresStepTest.QualityGateStatusHolderAssertions.assertThat(qualityGateStatusHolder).hasStatus(OK).hasConditionCount(1).hasCondition(equals2Condition, ConditionStatus.EvaluationStatus.OK, String.valueOf(rawValue));
    }

    @Test
    public void new_measures_have_ERROR_level_if_at_least_one_updated_measure_has_ERROR_level() {
        int rawValue = 3;
        Condition equalsOneErrorCondition = QualityGateMeasuresStepTest.createLessThanCondition(QualityGateMeasuresStepTest.INT_METRIC_1, "4");
        Condition equalsOneOkCondition = QualityGateMeasuresStepTest.createLessThanCondition(QualityGateMeasuresStepTest.INT_METRIC_2, "2");
        Measure rawMeasure = Measure.newMeasureBuilder().create(rawValue, null);
        qualityGateHolder.setQualityGate(new org.sonar.ce.task.projectanalysis.qualitygate.QualityGate(QualityGateMeasuresStepTest.SOME_QG_ID, QualityGateMeasuresStepTest.SOME_QG_NAME, ImmutableList.of(equalsOneErrorCondition, equalsOneOkCondition)));
        measureRepository.addRawMeasure(QualityGateMeasuresStepTest.PROJECT_REF, QualityGateMeasuresStepTest.INT_METRIC_1_KEY, rawMeasure);
        measureRepository.addRawMeasure(QualityGateMeasuresStepTest.PROJECT_REF, QualityGateMeasuresStepTest.INT_METRIC_2_KEY, rawMeasure);
        underTest.execute(new TestComputationStepContext());
        Optional<Measure> rawMeasure1 = measureRepository.getAddedRawMeasure(QualityGateMeasuresStepTest.PROJECT_REF, QualityGateMeasuresStepTest.INT_METRIC_1_KEY);
        Optional<Measure> rawMeasure2 = measureRepository.getAddedRawMeasure(QualityGateMeasuresStepTest.PROJECT_REF, QualityGateMeasuresStepTest.INT_METRIC_2_KEY);
        MeasureAssert.assertThat(rawMeasure1.get()).hasQualityGateLevel(Level.ERROR).hasQualityGateText(QualityGateMeasuresStepTest.dumbResultTextAnswer(equalsOneErrorCondition, Level.ERROR, rawValue));
        MeasureAssert.assertThat(rawMeasure2.get()).hasQualityGateLevel(Level.OK).hasQualityGateText(QualityGateMeasuresStepTest.dumbResultTextAnswer(equalsOneOkCondition, Level.OK, rawValue));
        MeasureAssert.assertThat(getAlertStatusMeasure()).hasQualityGateLevel(Level.ERROR).hasQualityGateText((((QualityGateMeasuresStepTest.dumbResultTextAnswer(equalsOneErrorCondition, Level.ERROR, rawValue)) + ", ") + (QualityGateMeasuresStepTest.dumbResultTextAnswer(equalsOneOkCondition, Level.OK, rawValue))));
        MeasureAssert.assertThat(getQGDetailsMeasure()).hasValue(toJson());
        QualityGateMeasuresStepTest.QualityGateStatusHolderAssertions.assertThat(qualityGateStatusHolder).hasStatus(ERROR).hasConditionCount(2).hasCondition(equalsOneErrorCondition, ConditionStatus.EvaluationStatus.ERROR, String.valueOf(rawValue)).hasCondition(equalsOneOkCondition, ConditionStatus.EvaluationStatus.OK, String.valueOf(rawValue));
    }

    @Test
    public void new_measure_has_ERROR_level_of_all_conditions_for_a_specific_metric_if_its_the_worst() {
        int rawValue = 3;
        Condition fixedCondition = QualityGateMeasuresStepTest.createLessThanCondition(QualityGateMeasuresStepTest.INT_METRIC_1, "4");
        Condition periodCondition = QualityGateMeasuresStepTest.createLessThanCondition(QualityGateMeasuresStepTest.INT_METRIC_1, "2");
        qualityGateHolder.setQualityGate(new org.sonar.ce.task.projectanalysis.qualitygate.QualityGate(QualityGateMeasuresStepTest.SOME_QG_ID, QualityGateMeasuresStepTest.SOME_QG_NAME, ImmutableList.of(fixedCondition, periodCondition)));
        Measure measure = Measure.newMeasureBuilder().create(rawValue, null);
        measureRepository.addRawMeasure(QualityGateMeasuresStepTest.PROJECT_REF, QualityGateMeasuresStepTest.INT_METRIC_1_KEY, measure);
        underTest.execute(new TestComputationStepContext());
        Optional<Measure> rawMeasure1 = measureRepository.getAddedRawMeasure(QualityGateMeasuresStepTest.PROJECT_REF, QualityGateMeasuresStepTest.INT_METRIC_1_KEY);
        MeasureAssert.assertThat(rawMeasure1.get()).hasQualityGateLevel(Level.ERROR).hasQualityGateText(QualityGateMeasuresStepTest.dumbResultTextAnswer(fixedCondition, Level.ERROR, rawValue));
    }

    @Test
    public void new_measure_has_condition_on_leak_period_when_all_conditions_on_specific_metric_has_same_QG_level() {
        int rawValue = 0;
        Condition fixedCondition = QualityGateMeasuresStepTest.createLessThanCondition(QualityGateMeasuresStepTest.INT_METRIC_1, "1");
        Condition periodCondition = QualityGateMeasuresStepTest.createLessThanCondition(QualityGateMeasuresStepTest.INT_METRIC_1, "1");
        qualityGateHolder.setQualityGate(new org.sonar.ce.task.projectanalysis.qualitygate.QualityGate(QualityGateMeasuresStepTest.SOME_QG_ID, QualityGateMeasuresStepTest.SOME_QG_NAME, ImmutableList.of(fixedCondition, periodCondition)));
        Measure measure = Measure.newMeasureBuilder().setVariation(rawValue).create(rawValue, null);
        measureRepository.addRawMeasure(QualityGateMeasuresStepTest.PROJECT_REF, QualityGateMeasuresStepTest.INT_METRIC_1_KEY, measure);
        underTest.execute(new TestComputationStepContext());
        Optional<Measure> rawMeasure1 = measureRepository.getAddedRawMeasure(QualityGateMeasuresStepTest.PROJECT_REF, QualityGateMeasuresStepTest.INT_METRIC_1_KEY);
        MeasureAssert.assertThat(rawMeasure1.get()).hasQualityGateLevel(Level.ERROR).hasQualityGateText(QualityGateMeasuresStepTest.dumbResultTextAnswer(periodCondition, Level.ERROR, rawValue));
    }

    private static class QualityGateStatusHolderAssertions extends AbstractAssert<QualityGateMeasuresStepTest.QualityGateStatusHolderAssertions, QualityGateStatusHolder> {
        private QualityGateStatusHolderAssertions(QualityGateStatusHolder actual) {
            super(actual, QualityGateMeasuresStepTest.QualityGateStatusHolderAssertions.class);
        }

        public static QualityGateMeasuresStepTest.QualityGateStatusHolderAssertions assertThat(QualityGateStatusHolder holder) {
            return new QualityGateMeasuresStepTest.QualityGateStatusHolderAssertions(holder);
        }

        public QualityGateMeasuresStepTest.QualityGateStatusHolderAssertions hasStatus(QualityGateStatus status) {
            if ((actual.getStatus()) != status) {
                failWithMessage("Expected QualityGateStatusHolder to have global status <%s> but was <%s>", status, actual.getStatus());
            }
            return this;
        }

        public QualityGateMeasuresStepTest.QualityGateStatusHolderAssertions hasConditionCount(int count) {
            int conditionCount = actual.getStatusPerConditions().size();
            if (conditionCount != count) {
                failWithMessage("Expected QualityGateStatusHolder to have <%s> conditions but it has <%s>", count, conditionCount);
            }
            return this;
        }

        public QualityGateMeasuresStepTest.QualityGateStatusHolderAssertions hasCondition(Condition condition, ConditionStatus.EvaluationStatus evaluationStatus, @Nullable
        String expectedValue) {
            for (Map.Entry<Condition, ConditionStatus> entry : actual.getStatusPerConditions().entrySet()) {
                if ((entry.getKey()) == condition) {
                    ConditionStatus.EvaluationStatus actualStatus = entry.getValue().getStatus();
                    if (actualStatus != evaluationStatus) {
                        failWithMessage("Expected Status of condition <%s> in QualityGateStatusHolder to be <%s> but it was <%s>", condition, evaluationStatus, actualStatus);
                    }
                    String actualValue = entry.getValue().getValue();
                    if (!(Objects.equals(expectedValue, actualValue))) {
                        failWithMessage("Expected Value of condition <%s> in QualityGateStatusHolder to be <%s> but it was <%s>", condition, expectedValue, actualValue);
                    }
                    return this;
                }
            }
            failWithMessage("Expected QualityGateStatusHolder to have an entry for <%s> but none was found", condition);
            return this;
        }
    }
}

