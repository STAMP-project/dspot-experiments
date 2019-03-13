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
package org.sonar.core.metric;


import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;


public class ScannerMetricsTest {
    // metrics that are always added, regardless of plugins
    private static final List<Metric> SENSOR_METRICS_WITHOUT_METRIC_PLUGIN = ScannerMetricsTest.metrics();

    @Test
    public void check_number_of_allowed_core_metrics() {
        assertThat(ScannerMetricsTest.SENSOR_METRICS_WITHOUT_METRIC_PLUGIN).hasSize(22);
    }

    @Test
    public void check_metrics_from_plugin() {
        List<Metric> metrics = ScannerMetricsTest.metrics(new ScannerMetricsTest.FakeMetrics());
        metrics.removeAll(ScannerMetricsTest.SENSOR_METRICS_WITHOUT_METRIC_PLUGIN);
        assertThat(metrics).hasSize(2);
    }

    @Test
    public void should_not_crash_on_null_metrics_from_faulty_plugins() {
        Metrics faultyMetrics = () -> null;
        Metrics okMetrics = new ScannerMetricsTest.FakeMetrics();
        List<Metric> metrics = ScannerMetricsTest.metrics(okMetrics, faultyMetrics);
        metrics.removeAll(ScannerMetricsTest.SENSOR_METRICS_WITHOUT_METRIC_PLUGIN);
        assertThat(metrics).isEqualTo(okMetrics.getMetrics());
    }

    private static class FakeMetrics implements Metrics {
        @Override
        public List<Metric> getMetrics() {
            return Arrays.asList(create(), create());
        }
    }
}

