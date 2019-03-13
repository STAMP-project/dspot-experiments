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
package org.sonar.ce.task.projectanalysis.measure;


import Component.Type.DIRECTORY;
import Component.Type.FILE;
import Measure.Level.OK;
import Measure.NewMeasureBuilder;
import Metric.MetricType.BOOL;
import Metric.MetricType.FLOAT;
import Metric.MetricType.INT;
import Metric.MetricType.RATING;
import Metric.MetricType.STRING;
import Metric.MetricType.WORK_DUR;
import java.util.function.Predicate;
import org.junit.Test;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.metric.MetricImpl;


public class BestValueOptimizationTest {
    private static final ReportComponent FILE_COMPONENT = ReportComponent.builder(FILE, 1).build();

    private static final ReportComponent SOME_NON_FILE_COMPONENT = ReportComponent.builder(DIRECTORY, 2).build();

    private static final String SOME_DATA = "some_data";

    private static final MetricImpl METRIC_BOOLEAN_FALSE = BestValueOptimizationTest.createMetric(BOOL, 6.0);

    private static final MetricImpl METRIC_BOOLEAN_TRUE = BestValueOptimizationTest.createMetric(BOOL, 1.0);

    private static final double SOME_EMPTY_VARIATIONS = 0.0;

    @Test
    public void apply_returns_true_for_value_true_for_Boolean_Metric_and_best_value_1() {
        Predicate<Measure> underTest = BestValueOptimization.from(BestValueOptimizationTest.METRIC_BOOLEAN_TRUE, BestValueOptimizationTest.FILE_COMPONENT);
        assertThat(underTest.test(Measure.newMeasureBuilder().create(true))).isTrue();
        assertThat(underTest.test(Measure.newMeasureBuilder().setVariation(BestValueOptimizationTest.SOME_EMPTY_VARIATIONS).create(true))).isTrue();
        assertThat(underTest.test(Measure.newMeasureBuilder().create(false))).isFalse();
        assertThat(underTest.test(Measure.newMeasureBuilder().setVariation(BestValueOptimizationTest.SOME_EMPTY_VARIATIONS).create(false))).isFalse();
    }

    @Test
    public void apply_returns_false_if_component_is_not_a_FILE_for_Boolean_Metric_and_best_value_1() {
        Predicate<Measure> underTest = BestValueOptimization.from(BestValueOptimizationTest.METRIC_BOOLEAN_TRUE, BestValueOptimizationTest.SOME_NON_FILE_COMPONENT);
        assertThat(underTest.test(Measure.newMeasureBuilder().create(true))).isFalse();
        assertThat(underTest.test(Measure.newMeasureBuilder().create(false))).isFalse();
    }

    @Test
    public void apply_returns_false_if_measure_has_anything_else_than_value_for_Boolean_Metric_and_best_value_1() {
        Predicate<Measure> underTest = BestValueOptimization.from(BestValueOptimizationTest.METRIC_BOOLEAN_TRUE, BestValueOptimizationTest.FILE_COMPONENT);
        for (Measure.NewMeasureBuilder builder : BestValueOptimizationTest.builders_of_non_bestValueOptimized_measures()) {
            assertThat(underTest.test(builder.create(true))).isFalse();
            assertThat(underTest.test(builder.create(false))).isFalse();
        }
    }

    @Test
    public void apply_returns_false_if_measure_has_data_for_Boolean_Metric_and_best_value_1() {
        Predicate<Measure> underTest = BestValueOptimization.from(BestValueOptimizationTest.METRIC_BOOLEAN_TRUE, BestValueOptimizationTest.FILE_COMPONENT);
        assertThat(underTest.test(Measure.newMeasureBuilder().create(true, BestValueOptimizationTest.SOME_DATA))).isFalse();
        assertThat(underTest.test(Measure.newMeasureBuilder().create(false, BestValueOptimizationTest.SOME_DATA))).isFalse();
    }

    @Test
    public void apply_returns_true_for_value_false_for_Boolean_Metric_and_best_value_not_1() {
        Predicate<Measure> underTest = BestValueOptimization.from(BestValueOptimizationTest.METRIC_BOOLEAN_FALSE, BestValueOptimizationTest.FILE_COMPONENT);
        assertThat(underTest.test(Measure.newMeasureBuilder().create(true))).isFalse();
        assertThat(underTest.test(Measure.newMeasureBuilder().create(false))).isTrue();
        assertThat(underTest.test(Measure.newMeasureBuilder().setVariation(BestValueOptimizationTest.SOME_EMPTY_VARIATIONS).create(false))).isTrue();
    }

    @Test
    public void apply_returns_false_if_component_is_not_a_FILE_for_Boolean_Metric_and_best_value_not_1() {
        Predicate<Measure> underTest = BestValueOptimization.from(BestValueOptimizationTest.METRIC_BOOLEAN_FALSE, BestValueOptimizationTest.SOME_NON_FILE_COMPONENT);
        assertThat(underTest.test(Measure.newMeasureBuilder().create(true))).isFalse();
        assertThat(underTest.test(Measure.newMeasureBuilder().create(false))).isFalse();
    }

    @Test
    public void apply_returns_false_if_measure_has_anything_else_than_value_for_Boolean_Metric_and_best_value_not_1() {
        Predicate<Measure> underTest = BestValueOptimization.from(BestValueOptimizationTest.METRIC_BOOLEAN_FALSE, BestValueOptimizationTest.FILE_COMPONENT);
        for (Measure.NewMeasureBuilder builder : BestValueOptimizationTest.builders_of_non_bestValueOptimized_measures()) {
            assertThat(underTest.test(builder.create(true))).isFalse();
            assertThat(underTest.test(builder.create(false))).isFalse();
        }
    }

    @Test
    public void apply_returns_false_if_measure_has_data_for_Boolean_Metric_and_best_value_not_1() {
        Predicate<Measure> underTest = BestValueOptimization.from(BestValueOptimizationTest.METRIC_BOOLEAN_FALSE, BestValueOptimizationTest.FILE_COMPONENT);
        assertThat(underTest.test(Measure.newMeasureBuilder().create(true, BestValueOptimizationTest.SOME_DATA))).isFalse();
        assertThat(underTest.test(Measure.newMeasureBuilder().create(false, BestValueOptimizationTest.SOME_DATA))).isFalse();
    }

    @Test
    public void verify_value_comparison_for_int_metric() {
        Predicate<Measure> underTest = BestValueOptimization.from(BestValueOptimizationTest.createMetric(INT, 10), BestValueOptimizationTest.FILE_COMPONENT);
        assertThat(underTest.test(Measure.newMeasureBuilder().create(10))).isTrue();
        assertThat(underTest.test(Measure.newMeasureBuilder().setVariation(BestValueOptimizationTest.SOME_EMPTY_VARIATIONS).create(10))).isTrue();
        assertThat(underTest.test(Measure.newMeasureBuilder().create(11))).isFalse();
    }

    @Test
    public void verify_value_comparison_for_long_metric() {
        Predicate<Measure> underTest = BestValueOptimization.from(BestValueOptimizationTest.createMetric(WORK_DUR, 9511L), BestValueOptimizationTest.FILE_COMPONENT);
        assertThat(underTest.test(Measure.newMeasureBuilder().create(9511L))).isTrue();
        assertThat(underTest.test(Measure.newMeasureBuilder().setVariation(BestValueOptimizationTest.SOME_EMPTY_VARIATIONS).create(9511L))).isTrue();
        assertThat(underTest.test(Measure.newMeasureBuilder().create(963L))).isFalse();
    }

    @Test
    public void verify_value_comparison_for_rating_metric() {
        Predicate<Measure> underTest = BestValueOptimization.from(BestValueOptimizationTest.createMetric(RATING, A.getIndex()), BestValueOptimizationTest.FILE_COMPONENT);
        assertThat(underTest.test(Measure.newMeasureBuilder().create(A.getIndex()))).isTrue();
        assertThat(underTest.test(Measure.newMeasureBuilder().setVariation(((double) (A.getIndex()))).createNoValue())).isTrue();
        assertThat(underTest.test(Measure.newMeasureBuilder().create(B.getIndex()))).isFalse();
        assertThat(underTest.test(Measure.newMeasureBuilder().setVariation(((double) (B.getIndex()))).createNoValue())).isFalse();
    }

    @Test
    public void verify_value_comparison_for_double_metric() {
        Predicate<Measure> underTest = BestValueOptimization.from(BestValueOptimizationTest.createMetric(FLOAT, 36.5), BestValueOptimizationTest.FILE_COMPONENT);
        assertThat(underTest.test(Measure.newMeasureBuilder().create(36.5, 1))).isTrue();
        assertThat(underTest.test(Measure.newMeasureBuilder().setVariation(BestValueOptimizationTest.SOME_EMPTY_VARIATIONS).create(36.5, 1))).isTrue();
        assertThat(underTest.test(Measure.newMeasureBuilder().create(36.6, 1))).isFalse();
    }

    @Test
    public void apply_returns_false_for_String_measure() {
        Predicate<Measure> underTest = BestValueOptimization.from(BestValueOptimizationTest.createMetric(FLOAT, 36.5), BestValueOptimizationTest.FILE_COMPONENT);
        assertThat(underTest.test(Measure.newMeasureBuilder().create("aaa"))).isFalse();
    }

    @Test
    public void apply_returns_false_for_LEVEL_measure() {
        Predicate<Measure> underTest = BestValueOptimization.from(BestValueOptimizationTest.createMetric(STRING, 36.5), BestValueOptimizationTest.FILE_COMPONENT);
        assertThat(underTest.test(Measure.newMeasureBuilder().create(OK))).isFalse();
    }
}

