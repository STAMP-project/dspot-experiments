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
package org.sonar.ce.task.projectanalysis.formula.coverage;


import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.ExternalResource;
import org.sonar.ce.task.projectanalysis.component.Component;
import org.sonar.ce.task.projectanalysis.formula.CounterInitializationContext;
import org.sonar.ce.task.projectanalysis.measure.Measure;


public class CoverageUtilsTest {
    private static final String SOME_METRIC_KEY = "some key";

    public static final double DEFAULT_VARIATION = 0.0;

    @Rule
    public CoverageUtilsTest.CounterInitializationContextRule fileAggregateContext = new CoverageUtilsTest.CounterInitializationContextRule();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void verify_calculate_coverage() {
        assertThat(CoverageUtils.calculateCoverage(5, 10)).isEqualTo(50.0);
    }

    @Test
    public void getLongMeasureValue_returns_0_if_measure_does_not_exist() {
        assertThat(CoverageUtils.getLongMeasureValue(fileAggregateContext, CoverageUtilsTest.SOME_METRIC_KEY)).isEqualTo(0L);
    }

    @Test
    public void getLongMeasureValue_returns_0_if_measure_is_NO_VALUE() {
        fileAggregateContext.put(CoverageUtilsTest.SOME_METRIC_KEY, Measure.newMeasureBuilder().createNoValue());
        assertThat(CoverageUtils.getLongMeasureValue(fileAggregateContext, CoverageUtilsTest.SOME_METRIC_KEY)).isEqualTo(0L);
    }

    @Test
    public void getLongMeasureValue_returns_value_if_measure_is_INT() {
        fileAggregateContext.put(CoverageUtilsTest.SOME_METRIC_KEY, Measure.newMeasureBuilder().create(152));
        assertThat(CoverageUtils.getLongMeasureValue(fileAggregateContext, CoverageUtilsTest.SOME_METRIC_KEY)).isEqualTo(152L);
    }

    @Test
    public void getLongMeasureValue_returns_value_if_measure_is_LONG() {
        fileAggregateContext.put(CoverageUtilsTest.SOME_METRIC_KEY, Measure.newMeasureBuilder().create(152L));
        assertThat(CoverageUtils.getLongMeasureValue(fileAggregateContext, CoverageUtilsTest.SOME_METRIC_KEY)).isEqualTo(152L);
    }

    @Test
    public void getLongMeasureValue_throws_ISE_if_measure_is_DOUBLE() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("value can not be converted to long because current value type is a DOUBLE");
        fileAggregateContext.put(CoverageUtilsTest.SOME_METRIC_KEY, Measure.newMeasureBuilder().create(152.0, 1));
        CoverageUtils.getLongMeasureValue(fileAggregateContext, CoverageUtilsTest.SOME_METRIC_KEY);
    }

    @Test
    public void getMeasureVariations_returns_0_in_all_MeasureVariations_if_there_is_no_measure() {
        assertThat(CoverageUtils.getMeasureVariations(fileAggregateContext, CoverageUtilsTest.SOME_METRIC_KEY)).isEqualTo(CoverageUtilsTest.DEFAULT_VARIATION);
    }

    @Test
    public void getMeasureVariations_returns_0_in_all_MeasureVariations_if_there_is_measure_has_no_variations() {
        fileAggregateContext.put(CoverageUtilsTest.SOME_METRIC_KEY, Measure.newMeasureBuilder().createNoValue());
        assertThat(CoverageUtils.getMeasureVariations(fileAggregateContext, CoverageUtilsTest.SOME_METRIC_KEY)).isEqualTo(CoverageUtilsTest.DEFAULT_VARIATION);
    }

    @Test
    public void getMeasureVariations_returns_MeasureVariations_of_measure_when_it_has_one() {
        fileAggregateContext.put(CoverageUtilsTest.SOME_METRIC_KEY, Measure.newMeasureBuilder().setVariation(5.0).createNoValue());
        assertThat(CoverageUtils.getMeasureVariations(fileAggregateContext, CoverageUtilsTest.SOME_METRIC_KEY)).isEqualTo(5.0);
    }

    private static class CounterInitializationContextRule extends ExternalResource implements CounterInitializationContext {
        private final Map<String, Measure> measures = new HashMap<>();

        public CoverageUtilsTest.CounterInitializationContextRule put(String metricKey, Measure measure) {
            Preconditions.checkNotNull(metricKey);
            Preconditions.checkNotNull(measure);
            Preconditions.checkState((!(measures.containsKey(metricKey))));
            measures.put(metricKey, measure);
            return this;
        }

        @Override
        protected void after() {
            measures.clear();
        }

        @Override
        public Component getLeaf() {
            throw new UnsupportedOperationException("getFile is not supported");
        }

        @Override
        public Optional<Measure> getMeasure(String metricKey) {
            return Optional.ofNullable(measures.get(metricKey));
        }
    }
}

