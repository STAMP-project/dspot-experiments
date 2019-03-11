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


import Component.Type.FILE;
import Component.Type.PROJECT;
import DistributionFormula.DistributionCounter;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.ce.task.projectanalysis.component.ReportComponent;
import org.sonar.ce.task.projectanalysis.metric.Metric;


public class DistributionFormulaTest {
    private static final DistributionFormula BASIC_DISTRIBUTION_FORMULA = new DistributionFormula(CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION_KEY);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    CounterInitializationContext counterInitializationContext = Mockito.mock(CounterInitializationContext.class);

    CreateMeasureContext projectCreateMeasureContext = new DumbCreateMeasureContext(ReportComponent.builder(PROJECT, 1).build(), Mockito.mock(Metric.class));

    CreateMeasureContext fileCreateMeasureContext = new DumbCreateMeasureContext(ReportComponent.builder(FILE, 1).build(), Mockito.mock(Metric.class));

    @Test
    public void check_new_counter_class() {
        assertThat(DistributionFormulaTest.BASIC_DISTRIBUTION_FORMULA.createNewCounter().getClass()).isEqualTo(DistributionCounter.class);
    }

    @Test
    public void fail_with_NPE_when_creating_counter_with_null_metric() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Metric key cannot be null");
        new DistributionFormula(null);
    }

    @Test
    public void check_output_metric_key_is_function_complexity_distribution() {
        assertThat(DistributionFormulaTest.BASIC_DISTRIBUTION_FORMULA.getOutputMetricKeys()).containsOnly(CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION_KEY);
    }

    @Test
    public void create_measure() {
        DistributionFormula.DistributionCounter counter = DistributionFormulaTest.BASIC_DISTRIBUTION_FORMULA.createNewCounter();
        addMeasure(CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION_KEY, "0=3;3=7;6=10");
        counter.initialize(counterInitializationContext);
        assertThat(DistributionFormulaTest.BASIC_DISTRIBUTION_FORMULA.createMeasure(counter, projectCreateMeasureContext).get().getData()).isEqualTo("0=3;3=7;6=10");
    }

    @Test
    public void create_measure_when_counter_is_aggregating_from_another_counter() {
        DistributionFormula.DistributionCounter anotherCounter = DistributionFormulaTest.BASIC_DISTRIBUTION_FORMULA.createNewCounter();
        addMeasure(CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION_KEY, "0=3;3=7;6=10");
        anotherCounter.initialize(counterInitializationContext);
        DistributionFormula.DistributionCounter counter = DistributionFormulaTest.BASIC_DISTRIBUTION_FORMULA.createNewCounter();
        counter.aggregate(anotherCounter);
        assertThat(DistributionFormulaTest.BASIC_DISTRIBUTION_FORMULA.createMeasure(counter, projectCreateMeasureContext).get().getData()).isEqualTo("0=3;3=7;6=10");
    }

    @Test
    public void create_no_measure_when_no_value() {
        DistributionFormula.DistributionCounter counter = DistributionFormulaTest.BASIC_DISTRIBUTION_FORMULA.createNewCounter();
        Mockito.when(counterInitializationContext.getMeasure(CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION_KEY)).thenReturn(Optional.empty());
        counter.initialize(counterInitializationContext);
        assertThat(DistributionFormulaTest.BASIC_DISTRIBUTION_FORMULA.createMeasure(counter, projectCreateMeasureContext)).isNotPresent();
    }

    @Test
    public void not_create_measure_when_on_file() {
        DistributionFormula.DistributionCounter counter = DistributionFormulaTest.BASIC_DISTRIBUTION_FORMULA.createNewCounter();
        addMeasure(CoreMetrics.FUNCTION_COMPLEXITY_DISTRIBUTION_KEY, "0=3;3=7;6=10");
        counter.initialize(counterInitializationContext);
        assertThat(DistributionFormulaTest.BASIC_DISTRIBUTION_FORMULA.createMeasure(counter, fileCreateMeasureContext)).isNotPresent();
    }
}

