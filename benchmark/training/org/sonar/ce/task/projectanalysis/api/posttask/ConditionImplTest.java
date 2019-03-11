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


import ConditionImpl.Builder;
import QualityGate.EvaluationStatus.NO_VALUE;
import QualityGate.EvaluationStatus.OK;
import QualityGate.Operator.GREATER_THAN;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(DataProviderRunner.class)
public class ConditionImplTest {
    private static final String METRIC_KEY = "metricKey";

    private static final String ERROR_THRESHOLD = "error threshold";

    private static final String VALUE = "value";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Builder builder = ConditionImpl.newBuilder().setStatus(OK).setMetricKey(ConditionImplTest.METRIC_KEY).setOperator(GREATER_THAN).setErrorThreshold(ConditionImplTest.ERROR_THRESHOLD).setValue(ConditionImplTest.VALUE);

    @Test
    public void build_throws_NPE_if_status_is_null() {
        builder.setStatus(null);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("status can not be null");
        builder.build();
    }

    @Test
    public void build_throws_NPE_if_metricKey_is_null() {
        builder.setMetricKey(null);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("metricKey can not be null");
        builder.build();
    }

    @Test
    public void build_throws_NPE_if_operator_is_null() {
        builder.setOperator(null);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("operator can not be null");
        builder.build();
    }

    @Test
    public void build_throws_NPE_if_error_threshold_is_null() {
        builder.setErrorThreshold(null);
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("errorThreshold can not be null");
        builder.build();
    }

    @Test
    public void getValue_throws_ISE_when_condition_type_is_NO_VALUE() {
        builder.setStatus(NO_VALUE).setValue(null);
        ConditionImpl condition = builder.build();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("There is no value when status is NO_VALUE");
        condition.getValue();
    }

    @Test
    public void build_throws_IAE_if_value_is_not_null_but_status_is_NO_VALUE() {
        builder.setStatus(NO_VALUE);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("value must be null when status is NO_VALUE");
        builder.build();
    }

    @Test
    public void toString_ConditionImpl_of_type_different_from_NO_VALUE() {
        assertThat(builder.build().toString()).isEqualTo("ConditionImpl{status=OK, metricKey='metricKey', operator=GREATER_THAN, errorThreshold='error threshold', value='value'}");
    }

    @Test
    public void toString_ConditionImpl_of_type_NO_VALUE() {
        builder.setStatus(NO_VALUE).setValue(null);
        assertThat(builder.build().toString()).isEqualTo("ConditionImpl{status=NO_VALUE, metricKey='metricKey', operator=GREATER_THAN, errorThreshold='error threshold', value='null'}");
    }

    @Test
    public void verify_getters() {
        ConditionImpl underTest = builder.build();
        assertThat(underTest.getStatus()).isEqualTo(OK);
        assertThat(underTest.getMetricKey()).isEqualTo(ConditionImplTest.METRIC_KEY);
        assertThat(underTest.getOperator()).isEqualTo(GREATER_THAN);
        assertThat(underTest.getErrorThreshold()).isEqualTo(ConditionImplTest.ERROR_THRESHOLD);
        assertThat(underTest.getValue()).isEqualTo(ConditionImplTest.VALUE);
    }
}

