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
package org.sonar.ce.task.projectanalysis.formula;


import AverageFormula.AverageCounter;
import Component.Type.PROJECT;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.measure.Measure;
import org.sonar.ce.task.projectanalysis.metric.Metric;


public class AverageFormulaTest {
    private static final AverageFormula BASIC_AVERAGE_FORMULA = AverageFormula.Builder.newBuilder().setOutputMetricKey(CoreMetrics.FUNCTION_COMPLEXITY_KEY).setMainMetricKey(CoreMetrics.COMPLEXITY_IN_FUNCTIONS_KEY).setByMetricKey(CoreMetrics.FUNCTIONS_KEY).build();

    CounterInitializationContext counterInitializationContext = Mockito.mock(CounterInitializationContext.class);

    CreateMeasureContext createMeasureContext = new DumbCreateMeasureContext(ReportComponent.builder(PROJECT, 1).build(), Mockito.mock(Metric.class));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void fail_with_NPE_when_building_formula_without_output_metric() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Output metric key cannot be null");
        AverageFormula.Builder.newBuilder().setOutputMetricKey(null).setMainMetricKey(CoreMetrics.COMPLEXITY_IN_FUNCTIONS_KEY).setByMetricKey(CoreMetrics.FUNCTIONS_KEY).build();
    }

    @Test
    public void fail_with_NPE_when_building_formula_without_main_metric() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Main metric Key cannot be null");
        AverageFormula.Builder.newBuilder().setOutputMetricKey(CoreMetrics.FUNCTION_COMPLEXITY_KEY).setMainMetricKey(null).setByMetricKey(CoreMetrics.FUNCTIONS_KEY).build();
    }

    @Test
    public void fail_with_NPE_when_building_formula_without_by_metric() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("By metric Key cannot be null");
        AverageFormula.Builder.newBuilder().setOutputMetricKey(CoreMetrics.FUNCTION_COMPLEXITY_KEY).setMainMetricKey(CoreMetrics.COMPLEXITY_IN_FUNCTIONS_KEY).setByMetricKey(null).build();
    }

    @Test
    public void check_new_counter_class() {
        assertThat(AverageFormulaTest.BASIC_AVERAGE_FORMULA.createNewCounter().getClass()).isEqualTo(AverageCounter.class);
    }

    @Test
    public void check_output_metric_key_is_function_complexity_key() {
        assertThat(AverageFormulaTest.BASIC_AVERAGE_FORMULA.getOutputMetricKeys()).containsOnly(CoreMetrics.FUNCTION_COMPLEXITY_KEY);
    }

    @Test
    public void create_measure_when_counter_is_aggregated_from_context() {
        AverageFormula.AverageCounter counter = AverageFormulaTest.BASIC_AVERAGE_FORMULA.createNewCounter();
        addMeasure(CoreMetrics.COMPLEXITY_IN_FUNCTIONS_KEY, 10.0);
        addMeasure(CoreMetrics.FUNCTIONS_KEY, 2.0);
        counter.initialize(counterInitializationContext);
        assertThat(AverageFormulaTest.BASIC_AVERAGE_FORMULA.createMeasure(counter, createMeasureContext).get().getDoubleValue()).isEqualTo(5.0);
    }

    @Test
    public void create_measure_when_counter_is_aggregated_from_another_counter() {
        AverageFormula.AverageCounter anotherCounter = AverageFormulaTest.BASIC_AVERAGE_FORMULA.createNewCounter();
        addMeasure(CoreMetrics.COMPLEXITY_IN_FUNCTIONS_KEY, 10.0);
        addMeasure(CoreMetrics.FUNCTIONS_KEY, 2.0);
        anotherCounter.initialize(counterInitializationContext);
        AverageFormula.AverageCounter counter = AverageFormulaTest.BASIC_AVERAGE_FORMULA.createNewCounter();
        counter.aggregate(anotherCounter);
        assertThat(AverageFormulaTest.BASIC_AVERAGE_FORMULA.createMeasure(counter, createMeasureContext).get().getDoubleValue()).isEqualTo(5.0);
    }

    @Test
    public void create_double_measure() {
        AverageFormula.AverageCounter counter = AverageFormulaTest.BASIC_AVERAGE_FORMULA.createNewCounter();
        addMeasure(CoreMetrics.COMPLEXITY_IN_FUNCTIONS_KEY, 10.0);
        addMeasure(CoreMetrics.FUNCTIONS_KEY, 2.0);
        counter.initialize(counterInitializationContext);
        assertThat(AverageFormulaTest.BASIC_AVERAGE_FORMULA.createMeasure(counter, createMeasureContext).get().getDoubleValue()).isEqualTo(5.0);
    }

    @Test
    public void create_integer_measure() {
        AverageFormula.AverageCounter counter = AverageFormulaTest.BASIC_AVERAGE_FORMULA.createNewCounter();
        addMeasure(CoreMetrics.COMPLEXITY_IN_FUNCTIONS_KEY, 10);
        addMeasure(CoreMetrics.FUNCTIONS_KEY, 2);
        counter.initialize(counterInitializationContext);
        assertThat(AverageFormulaTest.BASIC_AVERAGE_FORMULA.createMeasure(counter, createMeasureContext).get().getDoubleValue()).isEqualTo(5);
    }

    @Test
    public void create_long_measure() {
        AverageFormula.AverageCounter counter = AverageFormulaTest.BASIC_AVERAGE_FORMULA.createNewCounter();
        addMeasure(CoreMetrics.COMPLEXITY_IN_FUNCTIONS_KEY, 10L);
        addMeasure(CoreMetrics.FUNCTIONS_KEY, 2L);
        counter.initialize(counterInitializationContext);
        assertThat(AverageFormulaTest.BASIC_AVERAGE_FORMULA.createMeasure(counter, createMeasureContext).get().getDoubleValue()).isEqualTo(5L);
    }

    @Test
    public void not_create_measure_when_aggregated_measure_has_no_value() {
        AverageFormula.AverageCounter counter = AverageFormulaTest.BASIC_AVERAGE_FORMULA.createNewCounter();
        addMeasure(CoreMetrics.COMPLEXITY_IN_FUNCTIONS_KEY, 10L);
        Mockito.when(counterInitializationContext.getMeasure(CoreMetrics.FUNCTIONS_KEY)).thenReturn(Optional.of(Measure.newMeasureBuilder().createNoValue()));
        counter.initialize(counterInitializationContext);
        assertThat(AverageFormulaTest.BASIC_AVERAGE_FORMULA.createMeasure(counter, createMeasureContext)).isNotPresent();
    }

    @Test
    public void fail_with_IAE_when_aggregate_from_component_and_context_with_not_numeric_measures() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Measure of type 'STRING' are not supported");
        AverageFormula.AverageCounter counter = AverageFormulaTest.BASIC_AVERAGE_FORMULA.createNewCounter();
        addMeasure(CoreMetrics.COMPLEXITY_IN_FUNCTIONS_KEY, 10L);
        Mockito.when(counterInitializationContext.getMeasure(CoreMetrics.FUNCTIONS_KEY)).thenReturn(Optional.of(Measure.newMeasureBuilder().create("data")));
        counter.initialize(counterInitializationContext);
        AverageFormulaTest.BASIC_AVERAGE_FORMULA.createMeasure(counter, createMeasureContext);
    }

    @Test
    public void no_measure_created_when_counter_has_no_value() {
        AverageFormula.AverageCounter counter = AverageFormulaTest.BASIC_AVERAGE_FORMULA.createNewCounter();
        Mockito.when(counterInitializationContext.getMeasure(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        counter.initialize(counterInitializationContext);
        assertThat(AverageFormulaTest.BASIC_AVERAGE_FORMULA.createMeasure(counter, createMeasureContext)).isNotPresent();
    }

    @Test
    public void not_create_measure_when_only_one_measure() {
        AverageFormula.AverageCounter counter = AverageFormulaTest.BASIC_AVERAGE_FORMULA.createNewCounter();
        addMeasure(CoreMetrics.COMPLEXITY_IN_FUNCTIONS_KEY, 10L);
        Mockito.when(counterInitializationContext.getMeasure(CoreMetrics.FUNCTIONS_KEY)).thenReturn(Optional.empty());
        counter.initialize(counterInitializationContext);
        assertThat(AverageFormulaTest.BASIC_AVERAGE_FORMULA.createMeasure(counter, createMeasureContext)).isNotPresent();
    }

    @Test
    public void not_create_measure_when_by_value_is_zero() {
        AverageFormula.AverageCounter counter = AverageFormulaTest.BASIC_AVERAGE_FORMULA.createNewCounter();
        addMeasure(CoreMetrics.COMPLEXITY_IN_FUNCTIONS_KEY, 10.0);
        addMeasure(CoreMetrics.FUNCTIONS_KEY, 0.0);
        counter.initialize(counterInitializationContext);
        assertThat(AverageFormulaTest.BASIC_AVERAGE_FORMULA.createMeasure(counter, createMeasureContext)).isNotPresent();
    }
}

