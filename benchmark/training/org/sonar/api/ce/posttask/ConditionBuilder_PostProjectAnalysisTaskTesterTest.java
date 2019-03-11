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
package org.sonar.api.ce.posttask;


import PostProjectAnalysisTaskTester.ConditionBuilder;
import QualityGate.Condition;
import QualityGate.EvaluationStatus;
import QualityGate.EvaluationStatus.NO_VALUE;
import QualityGate.Operator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ConditionBuilder_PostProjectAnalysisTaskTesterTest {
    private static final String SOME_METRIC_KEY = "some metric key";

    private static final Operator SOME_OPERATOR = Operator.GREATER_THAN;

    private static final String SOME_ERROR_THRESHOLD = "some error threshold";

    private static final EvaluationStatus SOME_STATUS_BUT_NO_VALUE = EvaluationStatus.OK;

    private static final String SOME_VALUE = "some value";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ConditionBuilder underTest = PostProjectAnalysisTaskTester.newConditionBuilder();

    @Test
    public void setMetricKey_throws_NPE_if_operator_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("metricKey cannot be null");
        underTest.setMetricKey(null);
    }

    @Test
    public void setOperator_throws_NPE_if_operator_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("operator cannot be null");
        underTest.setOperator(null);
    }

    @Test
    public void buildNoValue_throws_NPE_if_metricKey_is_null() {
        underTest.setOperator(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_OPERATOR).setErrorThreshold(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_ERROR_THRESHOLD);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("metricKey cannot be null");
        underTest.buildNoValue();
    }

    @Test
    public void buildNoValue_throws_NPE_if_operator_is_null() {
        underTest.setMetricKey(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_METRIC_KEY).setErrorThreshold(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_ERROR_THRESHOLD);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("operator cannot be null");
        underTest.buildNoValue();
    }

    @Test
    public void buildNoValue_throws_NPE_if_errorThreshold_is_null() {
        underTest.setMetricKey(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_METRIC_KEY).setOperator(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_OPERATOR);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("errorThreshold cannot be null");
        underTest.buildNoValue();
    }

    @Test
    public void buildNoValue_returns_Condition_which_getStatus_method_returns_NO_VALUE() {
        initValidBuilder();
        assertThat(underTest.buildNoValue().getStatus()).isEqualTo(NO_VALUE);
    }

    @Test
    public void buildNoValue_returns_Condition_which_getValue_method_throws_ISE() {
        initValidBuilder();
        QualityGate.Condition condition = underTest.buildNoValue();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("There is no value when status is NO_VALUE");
        condition.getValue();
    }

    @Test
    public void buildNoValue_returns_new_instance_at_each_call() {
        initValidBuilder();
        assertThat(underTest.buildNoValue()).isNotSameAs(underTest.buildNoValue());
    }

    @Test
    public void buildNoValue_has_no_value_in_toString_and_status_is_NO_VALUE() {
        initValidBuilder();
        assertThat(underTest.buildNoValue().toString()).isEqualTo(("Condition{status=NO_VALUE, metricKey='some metric key', operator=GREATER_THAN, " + "errorThreshold='some error threshold'}"));
    }

    @Test
    public void verify_getters_of_object_returned_by_buildNoValue() {
        initValidBuilder().setOnLeakPeriod(true);
        QualityGate.Condition condition = underTest.buildNoValue();
        assertThat(condition.getMetricKey()).isEqualTo(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_METRIC_KEY);
        assertThat(condition.getOperator()).isEqualTo(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_OPERATOR);
        assertThat(condition.getErrorThreshold()).isEqualTo(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_ERROR_THRESHOLD);
    }

    @Test
    public void build_throws_NPE_if_status_is_null() {
        initValidBuilder();
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("status cannot be null");
        underTest.build(null, ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_VALUE);
    }

    @Test
    public void build_throws_IAE_if_status_is_NO_VALUE() {
        initValidBuilder();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("status cannot be NO_VALUE, use method buildNoValue() instead");
        underTest.build(NO_VALUE, ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_VALUE);
    }

    @Test
    public void build_throws_NPE_if_value_is_null() {
        initValidBuilder();
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("value cannot be null, use method buildNoValue() instead");
        underTest.build(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_STATUS_BUT_NO_VALUE, null);
    }

    @Test
    public void build_throws_NPE_if_metricKey_is_null() {
        underTest.setOperator(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_OPERATOR).setErrorThreshold(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_ERROR_THRESHOLD);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("metricKey cannot be null");
        underTest.build(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_STATUS_BUT_NO_VALUE, ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_VALUE);
    }

    @Test
    public void build_throws_NPE_if_operator_is_null() {
        underTest.setMetricKey(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_METRIC_KEY).setErrorThreshold(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_ERROR_THRESHOLD);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("operator cannot be null");
        underTest.build(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_STATUS_BUT_NO_VALUE, ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_VALUE);
    }

    @Test
    public void build_throws_NPE_if_errorThreshold_is_null() {
        underTest.setMetricKey(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_METRIC_KEY).setOperator(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_OPERATOR);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("errorThreshold cannot be null");
        underTest.build(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_STATUS_BUT_NO_VALUE, ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_VALUE);
    }

    @Test
    public void build_returns_new_instance_at_each_call() {
        initValidBuilder();
        assertThat(underTest.build(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_STATUS_BUT_NO_VALUE, ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_VALUE)).isNotSameAs(underTest.build(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_STATUS_BUT_NO_VALUE, ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_VALUE));
    }

    @Test
    public void build_has_value_in_toString_and_specified_status() {
        initValidBuilder();
        assertThat(underTest.build(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_STATUS_BUT_NO_VALUE, ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_VALUE).toString()).isEqualTo(("Condition{status=OK, metricKey='some metric key', operator=GREATER_THAN, " + "errorThreshold='some error threshold', value='some value'}"));
    }

    @Test
    public void build_returns_Condition_which_getStatus_method_returns_NO_VALUE() {
        initValidBuilder();
        assertThat(underTest.buildNoValue().getStatus()).isEqualTo(NO_VALUE);
    }

    @Test
    public void build_returns_Condition_which_getValue_method_throws_ISE() {
        initValidBuilder();
        QualityGate.Condition condition = underTest.buildNoValue();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("There is no value when status is NO_VALUE");
        condition.getValue();
    }

    @Test
    public void verify_getters_of_object_returned_by_build() {
        initValidBuilder().setOnLeakPeriod(true);
        QualityGate.Condition condition = underTest.build(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_STATUS_BUT_NO_VALUE, ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_VALUE);
        assertThat(condition.getStatus()).isEqualTo(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_STATUS_BUT_NO_VALUE);
        assertThat(condition.getMetricKey()).isEqualTo(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_METRIC_KEY);
        assertThat(condition.getOperator()).isEqualTo(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_OPERATOR);
        assertThat(condition.getErrorThreshold()).isEqualTo(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_ERROR_THRESHOLD);
        assertThat(condition.getValue()).isEqualTo(ConditionBuilder_PostProjectAnalysisTaskTesterTest.SOME_VALUE);
    }
}

