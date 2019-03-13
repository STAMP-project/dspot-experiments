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
package org.sonar.server.measure.live;


import BranchType.LONG;
import BranchType.PULL_REQUEST;
import BranchType.SHORT;
import Condition.Operator;
import CoreMetrics.ALERT_STATUS_KEY;
import CoreMetrics.QUALITY_GATE_DETAILS_KEY;
import EvaluatedQualityGate.Builder;
import Metric.Level.OK;
import Metric.ValueType.FLOAT;
import Metric.ValueType.STRING;
import QualityGateEvaluator.Measure;
import QualityGateEvaluator.Measures;
import ShortLivingBranchQualityGate.GATE;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.db.DbTester;
import org.sonar.db.component.BranchDto;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.measure.LiveMeasureDto;
import org.sonar.db.metric.MetricDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.db.qualitygate.QGateWithOrgDto;
import org.sonar.db.qualitygate.QualityGateConditionDto;
import org.sonar.server.qualitygate.Condition;
import org.sonar.server.qualitygate.EvaluatedQualityGate;
import org.sonar.server.qualitygate.QualityGate;
import org.sonar.server.qualitygate.QualityGateEvaluator;


public class LiveQualityGateComputerImplTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public DbTester db = DbTester.create();

    private LiveQualityGateComputerImplTest.TestQualityGateEvaluator qualityGateEvaluator = new LiveQualityGateComputerImplTest.TestQualityGateEvaluator();

    private LiveQualityGateComputerImpl underTest = new LiveQualityGateComputerImpl(db.getDbClient(), new org.sonar.server.qualitygate.QualityGateFinder(db.getDbClient()), qualityGateEvaluator);

    @Test
    public void loadQualityGate_returns_hardcoded_gate_for_short_living_branches() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPublicProject(organization);
        BranchDto branch = newBranchDto(project).setBranchType(SHORT);
        db.components().insertProjectBranch(project, branch);
        QualityGate result = underTest.loadQualityGate(db.getSession(), organization, project, branch);
        assertThat(result).isSameAs(GATE);
    }

    @Test
    public void loadQualityGate_returns_hardcoded_gate_for_pull_requests() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPublicProject(organization);
        BranchDto pullRequest = newBranchDto(project).setBranchType(PULL_REQUEST);
        db.components().insertProjectBranch(project, pullRequest);
        QualityGate result = underTest.loadQualityGate(db.getSession(), organization, project, pullRequest);
        assertThat(result).isSameAs(GATE);
    }

    @Test
    public void loadQualityGate_on_long_branch_returns_organization_default_gate() {
        OrganizationDto organization = db.organizations().insert();
        ComponentDto project = db.components().insertPublicProject(organization);
        BranchDto branch = newBranchDto(project).setBranchType(LONG);
        db.components().insertProjectBranch(project, branch);
        MetricDto metric = db.measures().insertMetric();
        QGateWithOrgDto gate = db.qualityGates().insertQualityGate(organization);
        db.qualityGates().setDefaultQualityGate(organization, gate);
        QualityGateConditionDto condition = db.qualityGates().addCondition(gate, metric);
        QualityGate result = underTest.loadQualityGate(db.getSession(), organization, project, branch);
        assertThat(result.getId()).isEqualTo(("" + (gate.getId())));
        assertThat(result.getConditions()).extracting(Condition::getMetricKey, Condition::getOperator, Condition::getErrorThreshold).containsExactlyInAnyOrder(tuple(metric.getKey(), Operator.fromDbValue(condition.getOperator()), condition.getErrorThreshold()));
    }

    @Test
    public void getMetricsRelatedTo() {
        Condition condition = new Condition("metric1", Operator.GREATER_THAN, "10");
        QualityGate gate = new QualityGate("1", "foo", ImmutableSet.of(condition));
        Set<String> result = underTest.getMetricsRelatedTo(gate);
        // the metrics needed to compute the status of gate
        // generated metrics
        assertThat(result).containsExactlyInAnyOrder(condition.getMetricKey(), ALERT_STATUS_KEY, QUALITY_GATE_DETAILS_KEY);
    }

    @Test
    public void refreshGateStatus_generates_gate_related_measures() {
        ComponentDto project = ComponentTesting.newPublicProjectDto(OrganizationTesting.newOrganizationDto());
        MetricDto conditionMetric = newMetricDto();
        MetricDto statusMetric = newMetricDto().setKey(ALERT_STATUS_KEY);
        MetricDto detailsMetric = newMetricDto().setKey(QUALITY_GATE_DETAILS_KEY);
        Condition condition = new Condition(conditionMetric.getKey(), Operator.GREATER_THAN, "10");
        QualityGate gate = new QualityGate("1", "foo", ImmutableSet.of(condition));
        MeasureMatrix matrix = new MeasureMatrix(Collections.singleton(project), Arrays.asList(conditionMetric, statusMetric, detailsMetric), Collections.emptyList());
        EvaluatedQualityGate result = underTest.refreshGateStatus(project, gate, matrix);
        QualityGateEvaluator.Measures measures = qualityGateEvaluator.getCalledMeasures();
        assertThat(measures.get(conditionMetric.getKey())).isEmpty();
        assertThat(result.getStatus()).isEqualTo(OK);
        assertThat(result.getEvaluatedConditions()).extracting(EvaluatedCondition::getStatus).containsExactly(EvaluatedCondition.EvaluationStatus.OK);
        assertThat(matrix.getMeasure(project, ALERT_STATUS_KEY).get().getDataAsString()).isEqualTo(OK.name());
        // json format
        assertThat(matrix.getMeasure(project, QUALITY_GATE_DETAILS_KEY).get().getDataAsString()).isNotEmpty().startsWith("{").endsWith("}");
    }

    @Test
    public void refreshGateStatus_provides_measures_to_evaluator() {
        ComponentDto project = ComponentTesting.newPublicProjectDto(OrganizationTesting.newOrganizationDto());
        MetricDto numericMetric = newMetricDto().setValueType(FLOAT.name());
        MetricDto numericNewMetric = newMetricDto().setValueType(FLOAT.name()).setKey("new_metric");
        MetricDto stringMetric = newMetricDto().setValueType(STRING.name());
        MetricDto statusMetric = newMetricDto().setKey(ALERT_STATUS_KEY);
        MetricDto detailsMetric = newMetricDto().setKey(QUALITY_GATE_DETAILS_KEY);
        QualityGate gate = new QualityGate("1", "foo", Collections.emptySet());
        LiveMeasureDto numericMeasure = new LiveMeasureDto().setMetricId(numericMetric.getId()).setValue(1.23).setVariation(4.56).setComponentUuid(project.uuid());
        LiveMeasureDto numericNewMeasure = new LiveMeasureDto().setMetricId(numericNewMetric.getId()).setValue(7.8).setVariation(8.9).setComponentUuid(project.uuid());
        LiveMeasureDto stringMeasure = new LiveMeasureDto().setMetricId(stringMetric.getId()).setData("bar").setComponentUuid(project.uuid());
        MeasureMatrix matrix = new MeasureMatrix(Collections.singleton(project), Arrays.asList(statusMetric, detailsMetric, numericMetric, numericNewMetric, stringMetric), Arrays.asList(numericMeasure, numericNewMeasure, stringMeasure));
        underTest.refreshGateStatus(project, gate, matrix);
        QualityGateEvaluator.Measures measures = qualityGateEvaluator.getCalledMeasures();
        QualityGateEvaluator.Measure loadedStringMeasure = measures.get(stringMetric.getKey()).get();
        assertThat(loadedStringMeasure.getStringValue()).hasValue("bar");
        assertThat(loadedStringMeasure.getValue()).isEmpty();
        assertThat(loadedStringMeasure.getType()).isEqualTo(STRING);
        QualityGateEvaluator.Measure loadedNumericMeasure = measures.get(numericMetric.getKey()).get();
        assertThat(loadedNumericMeasure.getStringValue()).isEmpty();
        assertThat(loadedNumericMeasure.getValue()).hasValue(1.23);
        assertThat(loadedNumericMeasure.getType()).isEqualTo(FLOAT);
        QualityGateEvaluator.Measure loadedNumericNewMeasure = measures.get(numericNewMetric.getKey()).get();
        assertThat(loadedNumericNewMeasure.getStringValue()).isEmpty();
        assertThat(loadedNumericNewMeasure.getNewMetricValue()).hasValue(8.9);
        assertThat(loadedNumericNewMeasure.getType()).isEqualTo(FLOAT);
    }

    private static class TestQualityGateEvaluator implements QualityGateEvaluator {
        private Measures measures;

        @Override
        public EvaluatedQualityGate evaluate(QualityGate gate, Measures measures) {
            Preconditions.checkState(((this.measures) == null));
            this.measures = measures;
            EvaluatedQualityGate.Builder builder = EvaluatedQualityGate.newBuilder().setQualityGate(gate).setStatus(OK);
            for (Condition condition : gate.getConditions()) {
                builder.addCondition(condition, EvaluatedCondition.EvaluationStatus.OK, "bar");
            }
            return builder.build();
        }

        private Measures getCalledMeasures() {
            return measures;
        }

        @Override
        public Set<String> getMetricKeys(QualityGate gate) {
            return gate.getConditions().stream().map(Condition::getMetricKey).collect(Collectors.toSet());
        }
    }
}

