/**
 * Copyright 2010-2013 Coda Hale and Yammer, Inc., 2014-2017 Dropwizard Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dropwizard.metrics;


import java.lang.management.RuntimeMXBean;
import org.junit.Test;
import org.mockito.Mockito;


public class JvmAttributeGaugeSetTest {
    private final RuntimeMXBean runtime = Mockito.mock(RuntimeMXBean.class);

    private final JvmAttributeGaugeSet gauges = new JvmAttributeGaugeSet(runtime);

    @Test
    public void hasASetOfGauges() throws Exception {
        assertThat(gauges.getMetrics().keySet()).containsOnly(MetricName.build("vendor"), MetricName.build("name"), MetricName.build("uptime"));
    }

    @Test
    public void hasAGaugeForTheJVMName() throws Exception {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get(MetricName.build("name"))));
        assertThat(gauge.getValue()).isEqualTo("9928@example.com");
    }

    @Test
    public void hasAGaugeForTheJVMVendor() throws Exception {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get(MetricName.build("vendor"))));
        assertThat(gauge.getValue()).isEqualTo("Oracle Corporation Java HotSpot(TM) 64-Bit Server VM 23.7-b01 (1.7)");
    }

    @Test
    public void hasAGaugeForTheJVMUptime() throws Exception {
        final Gauge gauge = ((Gauge) (gauges.getMetrics().get(MetricName.build("uptime"))));
        assertThat(gauge.getValue()).isEqualTo(100L);
    }

    @Test
    public void autoDiscoversTheRuntimeBean() throws Exception {
        final Gauge gauge = ((Gauge) (new JvmAttributeGaugeSet().getMetrics().get(MetricName.build("uptime"))));
        assertThat(((Long) (gauge.getValue()))).isPositive();
    }
}

