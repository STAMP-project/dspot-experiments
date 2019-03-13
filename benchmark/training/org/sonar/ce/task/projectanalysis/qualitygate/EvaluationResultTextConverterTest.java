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
package org.sonar.ce.task.projectanalysis.qualitygate;


import Condition.Operator.LESS_THAN;
import Measure.Level;
import Metric.MetricType;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.i18n.I18n;
import org.sonar.api.utils.Durations;
import org.sonar.ce.task.projectanalysis.metric.Metric;


@RunWith(DataProviderRunner.class)
public class EvaluationResultTextConverterTest {
    private static final Metric INT_METRIC = new org.sonar.ce.task.projectanalysis.metric.MetricImpl(1, "key", "int_metric_name", MetricType.INT);

    private static final Metric SOME_VARIATION_METRIC = new org.sonar.ce.task.projectanalysis.metric.MetricImpl(2, "new_variation_of_trololo", "variation_of_trololo_name", MetricType.INT);

    private static final Condition LT_10_CONDITION = new Condition(EvaluationResultTextConverterTest.INT_METRIC, LESS_THAN.getDbValue(), "10");

    private static final EvaluationResult OK_EVALUATION_RESULT = new EvaluationResult(Level.OK, null);

    private static final String ERROR_THRESHOLD = "error_threshold";

    private I18n i18n = Mockito.mock(I18n.class);

    private Durations durations = Mockito.mock(Durations.class);

    private EvaluationResultTextConverter underTest = new EvaluationResultTextConverterImpl(i18n, durations);

    @Test(expected = NullPointerException.class)
    public void evaluate_throws_NPE_if_Condition_arg_is_null() {
        underTest.asText(null, EvaluationResultTextConverterTest.OK_EVALUATION_RESULT);
    }

    @Test(expected = NullPointerException.class)
    public void evaluate_throws_NPE_if_EvaluationResult_arg_is_null() {
        underTest.asText(EvaluationResultTextConverterTest.LT_10_CONDITION, null);
    }

    @Test
    public void evaluate_returns_null_if_EvaluationResult_has_level_OK() {
        assertThat(underTest.asText(EvaluationResultTextConverterTest.LT_10_CONDITION, EvaluationResultTextConverterTest.OK_EVALUATION_RESULT)).isNull();
    }
}

