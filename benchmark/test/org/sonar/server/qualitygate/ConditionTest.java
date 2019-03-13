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
package org.sonar.server.qualitygate;


import Condition.Operator;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ConditionTest {
    private static final String METRIC_KEY = "metric_key";

    private static final Operator OPERATOR = Operator.GREATER_THAN;

    private static final String ERROR_THRESHOLD = "2";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Condition underTest = new Condition(ConditionTest.METRIC_KEY, ConditionTest.OPERATOR, ConditionTest.ERROR_THRESHOLD);

    @Test
    public void constructor_throws_NPE_if_metricKey_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("metricKey can't be null");
        new Condition(null, ConditionTest.OPERATOR, ConditionTest.ERROR_THRESHOLD);
    }

    @Test
    public void constructor_throws_NPE_if_operator_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("operator can't be null");
        new Condition(ConditionTest.METRIC_KEY, null, ConditionTest.ERROR_THRESHOLD);
    }

    @Test
    public void constructor_throws_NPE_if_errorThreshold_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("errorThreshold can't be null");
        new Condition(ConditionTest.METRIC_KEY, ConditionTest.OPERATOR, null);
    }

    @Test
    public void verify_getters() {
        assertThat(underTest.getMetricKey()).isEqualTo(ConditionTest.METRIC_KEY);
        assertThat(underTest.getOperator()).isEqualTo(ConditionTest.OPERATOR);
        assertThat(underTest.getErrorThreshold()).contains(ConditionTest.ERROR_THRESHOLD);
    }

    @Test
    public void toString_is_override() {
        assertThat(underTest.toString()).isEqualTo("Condition{metricKey='metric_key', operator=GREATER_THAN, errorThreshold='2'}");
    }

    @Test
    public void equals_is_based_on_all_fields() {
        assertThat(underTest).isEqualTo(underTest);
        assertThat(underTest).isNotEqualTo(null);
        assertThat(underTest).isNotEqualTo(new Object());
        assertThat(underTest).isEqualTo(new Condition(ConditionTest.METRIC_KEY, ConditionTest.OPERATOR, ConditionTest.ERROR_THRESHOLD));
        assertThat(underTest).isNotEqualTo(new Condition("other_metric_key", ConditionTest.OPERATOR, ConditionTest.ERROR_THRESHOLD));
        Arrays.stream(Operator.values()).filter(( s) -> !(OPERATOR.equals(s))).forEach(( otherOperator) -> assertThat(underTest).isNotEqualTo(new Condition(METRIC_KEY, otherOperator, ERROR_THRESHOLD)));
        assertThat(underTest).isNotEqualTo(new Condition(ConditionTest.METRIC_KEY, ConditionTest.OPERATOR, "other_error_threshold"));
    }

    @Test
    public void hashcode_is_based_on_all_fields() {
        assertThat(underTest.hashCode()).isEqualTo(underTest.hashCode());
        assertThat(underTest.hashCode()).isNotEqualTo(null);
        assertThat(underTest.hashCode()).isNotEqualTo(new Object().hashCode());
        assertThat(underTest.hashCode()).isEqualTo(new Condition(ConditionTest.METRIC_KEY, ConditionTest.OPERATOR, ConditionTest.ERROR_THRESHOLD).hashCode());
        assertThat(underTest.hashCode()).isNotEqualTo(new Condition("other_metric_key", ConditionTest.OPERATOR, ConditionTest.ERROR_THRESHOLD).hashCode());
        Arrays.stream(Operator.values()).filter(( s) -> !(OPERATOR.equals(s))).forEach(( otherOperator) -> assertThat(underTest.hashCode()).isNotEqualTo(new Condition(METRIC_KEY, otherOperator, ERROR_THRESHOLD).hashCode()));
        assertThat(underTest.hashCode()).isNotEqualTo(new Condition(ConditionTest.METRIC_KEY, ConditionTest.OPERATOR, "other_error_threshold").hashCode());
    }
}

