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


import Condition.Operator.GREATER_THAN;
import Condition.Operator.LESS_THAN;
import EvaluatedCondition.EvaluationStatus.ERROR;
import EvaluatedCondition.EvaluationStatus.OK;
import Metric.ValueType;
import QualityGateEvaluator.Measure;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.Optional;
import java.util.OptionalDouble;
import javax.annotation.Nullable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.sonar.api.measures.Metric;


@RunWith(DataProviderRunner.class)
public class ConditionEvaluatorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void GREATER_THAN_double() {
        test(new ConditionEvaluatorTest.FakeMeasure(10.1), GREATER_THAN, "10.2", OK, "10.1");
        test(new ConditionEvaluatorTest.FakeMeasure(10.2), GREATER_THAN, "10.2", OK, "10.2");
        test(new ConditionEvaluatorTest.FakeMeasure(10.3), GREATER_THAN, "10.2", ERROR, "10.3");
    }

    @Test
    public void LESS_THAN_double() {
        test(new ConditionEvaluatorTest.FakeMeasure(10.1), LESS_THAN, "10.2", ERROR, "10.1");
        test(new ConditionEvaluatorTest.FakeMeasure(10.2), LESS_THAN, "10.2", OK, "10.2");
        test(new ConditionEvaluatorTest.FakeMeasure(10.3), LESS_THAN, "10.2", OK, "10.3");
    }

    @Test
    public void GREATER_THAN_int() {
        test(new ConditionEvaluatorTest.FakeMeasure(10), GREATER_THAN, "9", ERROR, "10");
        test(new ConditionEvaluatorTest.FakeMeasure(10), GREATER_THAN, "10", OK, "10");
        test(new ConditionEvaluatorTest.FakeMeasure(10), GREATER_THAN, "11", OK, "10");
        testOnLeak(ConditionEvaluatorTest.FakeMeasure.newFakeMeasureOnLeak(10), GREATER_THAN, "9", ERROR, "10");
        testOnLeak(ConditionEvaluatorTest.FakeMeasure.newFakeMeasureOnLeak(10), GREATER_THAN, "10", OK, "10");
        testOnLeak(ConditionEvaluatorTest.FakeMeasure.newFakeMeasureOnLeak(10), GREATER_THAN, "11", OK, "10");
    }

    @Test
    public void LESS_THAN_int() {
        test(new ConditionEvaluatorTest.FakeMeasure(10), LESS_THAN, "9", OK, "10");
        test(new ConditionEvaluatorTest.FakeMeasure(10), LESS_THAN, "10", OK, "10");
        test(new ConditionEvaluatorTest.FakeMeasure(10), LESS_THAN, "11", ERROR, "10");
        testOnLeak(ConditionEvaluatorTest.FakeMeasure.newFakeMeasureOnLeak(10), LESS_THAN, "9", OK, "10");
        testOnLeak(ConditionEvaluatorTest.FakeMeasure.newFakeMeasureOnLeak(10), LESS_THAN, "10", OK, "10");
        testOnLeak(ConditionEvaluatorTest.FakeMeasure.newFakeMeasureOnLeak(10), LESS_THAN, "11", ERROR, "10");
    }

    @Test
    public void evaluate_throws_IAE_if_fail_to_parse_threshold() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Quality Gate: unable to parse threshold '9bar' to compare against foo");
        test(new ConditionEvaluatorTest.FakeMeasure(10), LESS_THAN, "9bar", ERROR, "10da");
    }

    @Test
    public void no_value_present() {
        test(new ConditionEvaluatorTest.FakeMeasure(((Integer) (null))), LESS_THAN, "9", OK, null);
        test(null, LESS_THAN, "9", OK, null);
    }

    private static class FakeMeasures implements QualityGateEvaluator.Measures {
        private final Measure measure;

        FakeMeasures(@Nullable
        QualityGateEvaluator.Measure measure) {
            this.measure = measure;
        }

        @Override
        public Optional<QualityGateEvaluator.Measure> get(String metricKey) {
            return Optional.ofNullable(measure);
        }
    }

    static class FakeMeasure implements QualityGateEvaluator.Measure {
        private Double leakValue;

        private Double value;

        private ValueType valueType;

        private FakeMeasure() {
        }

        FakeMeasure(Metric.ValueType valueType) {
            this.valueType = valueType;
        }

        FakeMeasure(@Nullable
        Double value) {
            this.value = value;
            this.valueType = ValueType.FLOAT;
        }

        FakeMeasure(@Nullable
        Integer value) {
            this.value = (value == null) ? null : value.doubleValue();
            this.valueType = ValueType.INT;
        }

        static ConditionEvaluatorTest.FakeMeasure newFakeMeasureOnLeak(@Nullable
        Integer value) {
            ConditionEvaluatorTest.FakeMeasure that = new ConditionEvaluatorTest.FakeMeasure();
            that.leakValue = (value == null) ? null : value.doubleValue();
            that.valueType = ValueType.INT;
            return that;
        }

        @Override
        public ValueType getType() {
            return valueType;
        }

        @Override
        public OptionalDouble getValue() {
            return (value) == null ? OptionalDouble.empty() : OptionalDouble.of(value);
        }

        @Override
        public Optional<String> getStringValue() {
            return Optional.empty();
        }

        @Override
        public OptionalDouble getNewMetricValue() {
            return (leakValue) == null ? OptionalDouble.empty() : OptionalDouble.of(leakValue);
        }
    }
}

