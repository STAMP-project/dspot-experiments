/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.metrics.influxdb;


import java.time.Instant;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.Histogram;
import org.apache.flink.metrics.Meter;
import org.apache.flink.metrics.SimpleCounter;
import org.apache.flink.metrics.util.TestHistogram;
import org.apache.flink.metrics.util.TestMeter;
import org.apache.flink.util.TestLogger;
import org.junit.Test;


/**
 * Test for {@link MetricMapper} checking that metrics are converted to InfluxDB client objects as expected.
 */
public class MetricMapperTest extends TestLogger {
    private static final String NAME = "a-metric-name";

    private static final MeasurementInfo INFO = MetricMapperTest.getMeasurementInfo(MetricMapperTest.NAME);

    private static final Instant TIMESTAMP = Instant.now();

    @Test
    public void testMapGauge() {
        verifyPoint(MetricMapper.map(MetricMapperTest.INFO, MetricMapperTest.TIMESTAMP, ((Gauge<Number>) (() -> 42))), "value=42");
        verifyPoint(MetricMapper.map(MetricMapperTest.INFO, MetricMapperTest.TIMESTAMP, ((Gauge<Number>) (() -> null))), "value=null");
        verifyPoint(MetricMapper.map(MetricMapperTest.INFO, MetricMapperTest.TIMESTAMP, ((Gauge<String>) (() -> "hello"))), "value=hello");
        verifyPoint(MetricMapper.map(MetricMapperTest.INFO, MetricMapperTest.TIMESTAMP, ((Gauge<Long>) (() -> 42L))), "value=42");
    }

    @Test
    public void testMapCounter() {
        Counter counter = new SimpleCounter();
        counter.inc(42L);
        verifyPoint(MetricMapper.map(MetricMapperTest.INFO, MetricMapperTest.TIMESTAMP, counter), "count=42");
    }

    @Test
    public void testMapHistogram() {
        Histogram histogram = new TestHistogram();
        verifyPoint(MetricMapper.map(MetricMapperTest.INFO, MetricMapperTest.TIMESTAMP, histogram), "count=3", "max=6", "mean=4.0", "min=7", "p50=0.5", "p75=0.75", "p95=0.95", "p98=0.98", "p99=0.99", "p999=0.999", "stddev=5.0");
    }

    @Test
    public void testMapMeter() {
        Meter meter = new TestMeter();
        verifyPoint(MetricMapper.map(MetricMapperTest.INFO, MetricMapperTest.TIMESTAMP, meter), "count=100", "rate=5.0");
    }
}

