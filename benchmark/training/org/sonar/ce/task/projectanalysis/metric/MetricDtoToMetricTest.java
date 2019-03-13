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
package org.sonar.ce.task.projectanalysis.metric;


import Metric.MetricType;
import Metric.MetricType.INT;
import org.junit.Test;
import org.sonar.db.metric.MetricDto;

import static MetricDtoToMetric.INSTANCE;


public class MetricDtoToMetricTest {
    private static final double SOME_BEST_VALUE = 951;

    private MetricDtoToMetric underTest = INSTANCE;

    @Test(expected = NullPointerException.class)
    public void apply_throws_NPE_if_arg_is_null() {
        underTest.apply(null);
    }

    @Test
    public void verify_mapping_from_dto() {
        for (Metric.MetricType metricType : MetricType.values()) {
            MetricDto metricDto = MetricDtoToMetricTest.createMetricDto(metricType);
            Metric metric = underTest.apply(metricDto);
            assertThat(metric.getId()).isEqualTo(metricDto.getId());
            assertThat(metric.getKey()).isEqualTo(metricDto.getKey());
            assertThat(metric.getName()).isEqualTo(metricDto.getShortName());
            assertThat(metric.getType()).isEqualTo(metricType);
            assertThat(metric.isBestValueOptimized()).isFalse();
            assertThat(metric.getBestValue()).isEqualTo(MetricDtoToMetricTest.SOME_BEST_VALUE);
        }
    }

    @Test
    public void verify_mapping_of_isBestValueOptimized() {
        assertThat(underTest.apply(MetricDtoToMetricTest.createMetricDto(INT).setOptimizedBestValue(true)).isBestValueOptimized()).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void apply_throws_IAE_if_valueType_can_not_be_parsed() {
        underTest.apply(new MetricDto().setId(1).setKey("key").setValueType("trololo"));
    }
}

