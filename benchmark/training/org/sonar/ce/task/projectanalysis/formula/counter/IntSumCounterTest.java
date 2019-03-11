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
package org.sonar.ce.task.projectanalysis.formula.counter;


import java.util.Optional;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.ce.task.projectanalysis.formula.CounterInitializationContext;
import org.sonar.ce.task.projectanalysis.measure.Measure;


public class IntSumCounterTest {
    private static final String METRIC_KEY = "metric";

    CounterInitializationContext counterInitializationContext = Mockito.mock(CounterInitializationContext.class);

    SumCounter sumCounter = new IntSumCounter(IntSumCounterTest.METRIC_KEY);

    @Test
    public void no_value_when_no_aggregation() {
        assertThat(sumCounter.getValue()).isNotPresent();
    }

    @Test
    public void aggregate_from_context() {
        Mockito.when(counterInitializationContext.getMeasure(IntSumCounterTest.METRIC_KEY)).thenReturn(Optional.of(Measure.newMeasureBuilder().create(10)));
        sumCounter.initialize(counterInitializationContext);
        assertThat(sumCounter.getValue().get()).isEqualTo(10);
    }

    @Test
    public void no_value_when_aggregate_from_context_but_no_measure() {
        Mockito.when(counterInitializationContext.getMeasure(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        sumCounter.initialize(counterInitializationContext);
        assertThat(sumCounter.getValue()).isNotPresent();
    }

    @Test
    public void aggregate_from_counter() {
        Mockito.when(counterInitializationContext.getMeasure(IntSumCounterTest.METRIC_KEY)).thenReturn(Optional.of(Measure.newMeasureBuilder().create(10)));
        SumCounter anotherCounter = new IntSumCounter(IntSumCounterTest.METRIC_KEY);
        anotherCounter.initialize(counterInitializationContext);
        sumCounter.aggregate(anotherCounter);
        assertThat(sumCounter.getValue().get()).isEqualTo(10);
    }

    @Test
    public void no_value_when_aggregate_from_empty_aggregator() {
        SumCounter anotherCounter = new IntSumCounter(IntSumCounterTest.METRIC_KEY);
        sumCounter.aggregate(anotherCounter);
        assertThat(sumCounter.getValue()).isNotPresent();
    }
}

