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
package org.sonar.ce.task.projectanalysis.api.posttask;


import Condition.Operator.LESS_THAN;
import ConditionStatus.EvaluationStatus.OK;
import ConditionStatus.NO_VALUE_STATUS;
import com.google.common.collect.ImmutableMap;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Collections;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.sonar.api.ce.posttask.QualityGate;
import org.sonar.ce.task.projectanalysis.qualitygate.Condition;
import org.sonar.ce.task.projectanalysis.qualitygate.ConditionStatus;


@RunWith(DataProviderRunner.class)
public class ConditionToConditionTest {
    private static final String METRIC_KEY = "metricKey";

    private static final String ERROR_THRESHOLD = "error threshold";

    private static final Map<Condition, ConditionStatus> NO_STATUS_PER_CONDITIONS = Collections.emptyMap();

    private static final String SOME_VALUE = "some value";

    private static final ConditionStatus SOME_CONDITION_STATUS = ConditionStatus.create(OK, ConditionToConditionTest.SOME_VALUE);

    private static final Condition SOME_CONDITION = new Condition(ConditionToConditionTest.newMetric(ConditionToConditionTest.METRIC_KEY), LESS_THAN.getDbValue(), ConditionToConditionTest.ERROR_THRESHOLD);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void apply_throws_NPE_if_Condition_argument_is_null() {
        ConditionToCondition underTest = new ConditionToCondition(ConditionToConditionTest.NO_STATUS_PER_CONDITIONS);
        expectedException.expect(NullPointerException.class);
        underTest.apply(null);
    }

    @Test
    public void apply_throws_ISE_if_there_is_no_ConditionStatus_for_Condition_argument() {
        ConditionToCondition underTest = new ConditionToCondition(ConditionToConditionTest.NO_STATUS_PER_CONDITIONS);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(("Missing ConditionStatus for condition on metric key " + (ConditionToConditionTest.METRIC_KEY)));
        underTest.apply(ConditionToConditionTest.SOME_CONDITION);
    }

    @Test
    public void apply_converts_key_from_metric() {
        ConditionToCondition underTest = new ConditionToCondition(ImmutableMap.of(ConditionToConditionTest.SOME_CONDITION, ConditionToConditionTest.SOME_CONDITION_STATUS));
        assertThat(underTest.apply(ConditionToConditionTest.SOME_CONDITION).getMetricKey()).isEqualTo(ConditionToConditionTest.METRIC_KEY);
    }

    @Test
    public void apply_copies_thresholds() {
        ConditionToCondition underTest = new ConditionToCondition(ImmutableMap.of(ConditionToConditionTest.SOME_CONDITION, ConditionToConditionTest.SOME_CONDITION_STATUS));
        assertThat(underTest.apply(ConditionToConditionTest.SOME_CONDITION).getErrorThreshold()).isEqualTo(ConditionToConditionTest.ERROR_THRESHOLD);
    }

    @Test
    public void apply_copies_value() {
        Condition otherCondition = new Condition(ConditionToConditionTest.newMetric(ConditionToConditionTest.METRIC_KEY), LESS_THAN.getDbValue(), ConditionToConditionTest.ERROR_THRESHOLD);
        ConditionToCondition underTest = new ConditionToCondition(ImmutableMap.of(ConditionToConditionTest.SOME_CONDITION, ConditionToConditionTest.SOME_CONDITION_STATUS, otherCondition, NO_VALUE_STATUS));
        assertThat(underTest.apply(ConditionToConditionTest.SOME_CONDITION).getValue()).isEqualTo(ConditionToConditionTest.SOME_VALUE);
        QualityGate.Condition res = underTest.apply(otherCondition);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("There is no value when status is NO_VALUE");
        res.getValue();
    }
}

