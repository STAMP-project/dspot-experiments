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


import Measure.Level.ERROR;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.sonar.ce.task.projectanalysis.measure.Measure;
import org.sonar.ce.task.projectanalysis.metric.Metric;

import static MetricType.values;


@RunWith(DataProviderRunner.class)
public class ConditionEvaluatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ConditionEvaluator underTest = new ConditionEvaluator();

    @Test
    public void test_input_numbers() {
        try {
            Metric metric = ConditionEvaluatorTest.createMetric(MetricType.FLOAT);
            Measure measure = Measure.newMeasureBuilder().create(10.2, 1, null);
            underTest.evaluate(ConditionEvaluatorTest.createCondition(metric, Operator.LESS_THAN, "20"), measure);
        } catch (NumberFormatException ex) {
            Assert.fail();
        }
        try {
            Metric metric = ConditionEvaluatorTest.createMetric(MetricType.INT);
            Measure measure = Measure.newMeasureBuilder().create(5, null);
            underTest.evaluate(ConditionEvaluatorTest.createCondition(metric, Operator.LESS_THAN, "20.1"), measure);
        } catch (NumberFormatException ex) {
            Assert.fail();
        }
        try {
            Metric metric = ConditionEvaluatorTest.createMetric(MetricType.PERCENT);
            Measure measure = Measure.newMeasureBuilder().create(10.2, 1, null);
            underTest.evaluate(ConditionEvaluatorTest.createCondition(metric, Operator.LESS_THAN, "20.1"), measure);
        } catch (NumberFormatException ex) {
            Assert.fail();
        }
    }

    @Test
    public void testGreater() {
        Metric metric = ConditionEvaluatorTest.createMetric(MetricType.FLOAT);
        Measure measure = Measure.newMeasureBuilder().create(10.2, 1, null);
        EvaluationResultAssert.assertThat(underTest.evaluate(ConditionEvaluatorTest.createCondition(metric, Operator.GREATER_THAN, "10.1"), measure)).hasLevel(Level.ERROR).hasValue(10.2);
        EvaluationResultAssert.assertThat(underTest.evaluate(ConditionEvaluatorTest.createCondition(metric, Operator.GREATER_THAN, "10.2"), measure)).hasLevel(Level.OK).hasValue(10.2);
        EvaluationResultAssert.assertThat(underTest.evaluate(ConditionEvaluatorTest.createCondition(metric, Operator.GREATER_THAN, "10.3"), measure)).hasLevel(Level.OK).hasValue(10.2);
    }

    @Test
    public void testSmaller() {
        Metric metric = ConditionEvaluatorTest.createMetric(MetricType.FLOAT);
        Measure measure = Measure.newMeasureBuilder().create(10.2, 1, null);
        EvaluationResultAssert.assertThat(underTest.evaluate(ConditionEvaluatorTest.createCondition(metric, Operator.LESS_THAN, "10.1"), measure)).hasLevel(Level.OK).hasValue(10.2);
        EvaluationResultAssert.assertThat(underTest.evaluate(ConditionEvaluatorTest.createCondition(metric, Operator.LESS_THAN, "10.2"), measure)).hasLevel(Level.OK).hasValue(10.2);
        EvaluationResultAssert.assertThat(underTest.evaluate(ConditionEvaluatorTest.createCondition(metric, Operator.LESS_THAN, "10.3"), measure)).hasLevel(Level.ERROR).hasValue(10.2);
    }

    @Test
    public void getLevel_throws_IEA_if_error_threshold_is_not_parsable_long() {
        Metric metric = ConditionEvaluatorTest.createMetric(MetricType.WORK_DUR);
        Measure measure = Measure.newMeasureBuilder().create(60L, null);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Quality Gate: Unable to parse value 'polop' to compare against name");
        underTest.evaluate(ConditionEvaluatorTest.createCondition(metric, Operator.LESS_THAN, "polop"), measure);
    }

    @Test
    public void testErrorLevel() {
        Metric metric = ConditionEvaluatorTest.createMetric(MetricType.FLOAT);
        Measure measure = Measure.newMeasureBuilder().create(10.2, 1, null);
        EvaluationResultAssert.assertThat(underTest.evaluate(ConditionEvaluatorTest.createCondition(metric, Operator.LESS_THAN, "10.3"), measure)).hasLevel(Level.ERROR);
        EvaluationResultAssert.assertThat(underTest.evaluate(ConditionEvaluatorTest.createCondition(metric, Operator.LESS_THAN, "10.1"), measure)).hasLevel(Level.OK);
        EvaluationResultAssert.assertThat(underTest.evaluate(new Condition(metric, Operator.LESS_THAN.getDbValue(), "10.3"), measure)).hasLevel(ERROR);
    }

    @Test
    public void condition_is_always_ok_when_measure_is_noValue() {
        for (Metric.MetricType metricType : FluentIterable.from(Arrays.asList(values())).filter(Predicates.not(Predicates.in(ImmutableSet.of(MetricType.BOOL, MetricType.DATA, MetricType.DISTRIB, MetricType.STRING))))) {
            Metric metric = ConditionEvaluatorTest.createMetric(metricType);
            Measure measure = Measure.newMeasureBuilder().createNoValue();
            EvaluationResultAssert.assertThat(underTest.evaluate(ConditionEvaluatorTest.createCondition(metric, Operator.LESS_THAN, "10.2"), measure)).hasLevel(Level.OK);
        }
    }

    @Test
    public void fail_when_condition_on_leak_period_is_using_unsupported_metric() {
        Metric metric = ConditionEvaluatorTest.createNewMetric(MetricType.LEVEL);
        Measure measure = Measure.newMeasureBuilder().setVariation(0.0).createNoValue();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Unsupported metric type LEVEL");
        underTest.evaluate(new Condition(metric, Operator.LESS_THAN.getDbValue(), "3"), measure);
    }

    @Test
    public void test_condition_on_rating() {
        Metric metric = ConditionEvaluatorTest.createMetric(MetricType.RATING);
        Measure measure = Measure.newMeasureBuilder().create(4, "D");
        EvaluationResultAssert.assertThat(underTest.evaluate(new Condition(metric, Operator.GREATER_THAN.getDbValue(), "4"), measure)).hasLevel(Level.OK).hasValue(4);
        EvaluationResultAssert.assertThat(underTest.evaluate(new Condition(metric, Operator.GREATER_THAN.getDbValue(), "2"), measure)).hasLevel(Level.ERROR).hasValue(4);
    }
}

