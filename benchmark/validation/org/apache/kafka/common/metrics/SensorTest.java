/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.metrics;


import Sensor.RecordingLevel;
import Sensor.RecordingLevel.DEBUG;
import Sensor.RecordingLevel.INFO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.stats.Avg;
import org.apache.kafka.common.metrics.stats.Meter;
import org.apache.kafka.common.metrics.stats.Rate;
import org.apache.kafka.common.metrics.stats.Sum;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.SystemTime;
import org.apache.kafka.common.utils.Time;
import org.junit.Assert;
import org.junit.Test;


public class SensorTest {
    @Test
    public void testRecordLevelEnum() {
        Sensor.RecordingLevel configLevel = RecordingLevel.INFO;
        Assert.assertTrue(INFO.shouldRecord(configLevel.id));
        Assert.assertFalse(DEBUG.shouldRecord(configLevel.id));
        configLevel = RecordingLevel.DEBUG;
        Assert.assertTrue(INFO.shouldRecord(configLevel.id));
        Assert.assertTrue(DEBUG.shouldRecord(configLevel.id));
        Assert.assertEquals(RecordingLevel.valueOf(DEBUG.toString()), DEBUG);
        Assert.assertEquals(RecordingLevel.valueOf(INFO.toString()), INFO);
    }

    @Test
    public void testShouldRecord() {
        MetricConfig debugConfig = new MetricConfig().recordLevel(DEBUG);
        MetricConfig infoConfig = new MetricConfig().recordLevel(INFO);
        Sensor infoSensor = new Sensor(null, "infoSensor", null, debugConfig, new SystemTime(), 0, RecordingLevel.INFO);
        Assert.assertTrue(infoSensor.shouldRecord());
        infoSensor = new Sensor(null, "infoSensor", null, debugConfig, new SystemTime(), 0, RecordingLevel.DEBUG);
        Assert.assertTrue(infoSensor.shouldRecord());
        Sensor debugSensor = new Sensor(null, "debugSensor", null, infoConfig, new SystemTime(), 0, RecordingLevel.INFO);
        Assert.assertTrue(debugSensor.shouldRecord());
        debugSensor = new Sensor(null, "debugSensor", null, infoConfig, new SystemTime(), 0, RecordingLevel.DEBUG);
        Assert.assertFalse(debugSensor.shouldRecord());
    }

    @Test
    public void testExpiredSensor() {
        MetricConfig config = new MetricConfig();
        Time mockTime = new MockTime();
        Metrics metrics = new Metrics(config, Arrays.asList(((MetricsReporter) (new JmxReporter()))), mockTime, true);
        long inactiveSensorExpirationTimeSeconds = 60L;
        Sensor sensor = new Sensor(metrics, "sensor", null, config, mockTime, inactiveSensorExpirationTimeSeconds, RecordingLevel.INFO);
        Assert.assertTrue(sensor.add(metrics.metricName("test1", "grp1"), new Avg()));
        Map<String, String> emptyTags = Collections.emptyMap();
        MetricName rateMetricName = new MetricName("rate", "test", "", emptyTags);
        MetricName totalMetricName = new MetricName("total", "test", "", emptyTags);
        Meter meter = new Meter(rateMetricName, totalMetricName);
        Assert.assertTrue(sensor.add(meter));
        mockTime.sleep(TimeUnit.SECONDS.toMillis((inactiveSensorExpirationTimeSeconds + 1)));
        Assert.assertFalse(sensor.add(metrics.metricName("test3", "grp1"), new Avg()));
        Assert.assertFalse(sensor.add(meter));
        metrics.close();
    }

    @Test
    public void testIdempotentAdd() {
        final Metrics metrics = new Metrics();
        final Sensor sensor = metrics.sensor("sensor");
        Assert.assertTrue(sensor.add(metrics.metricName("test-metric", "test-group"), new Avg()));
        // adding the same metric to the same sensor is a no-op
        Assert.assertTrue(sensor.add(metrics.metricName("test-metric", "test-group"), new Avg()));
        // but adding the same metric to a DIFFERENT sensor is an error
        final Sensor anotherSensor = metrics.sensor("another-sensor");
        try {
            anotherSensor.add(metrics.metricName("test-metric", "test-group"), new Avg());
            Assert.fail("should have thrown");
        } catch (final IllegalArgumentException ignored) {
            // pass
        }
        // note that adding a different metric with the same name is also a no-op
        Assert.assertTrue(sensor.add(metrics.metricName("test-metric", "test-group"), new Sum()));
        // so after all this, we still just have the original metric registered
        Assert.assertEquals(1, sensor.metrics().size());
        Assert.assertEquals(Avg.class, sensor.metrics().get(0).measurable().getClass());
    }

    /**
     * The Sensor#checkQuotas should be thread-safe since the method may be used by many ReplicaFetcherThreads.
     */
    @Test
    public void testCheckQuotasInMultiThreads() throws InterruptedException, ExecutionException {
        final Metrics metrics = new Metrics(// increasing the value of samples make SampledStat store more samples
        // decreasing the value of time window make SampledStat always record the given value
        new MetricConfig().quota(Quota.upperBound(Double.MAX_VALUE)).timeWindow(1, TimeUnit.MILLISECONDS).samples(100));
        final Sensor sensor = metrics.sensor("sensor");
        Assert.assertTrue(sensor.add(metrics.metricName("test-metric", "test-group"), new Rate()));
        final int threadCount = 10;
        final CountDownLatch latch = new CountDownLatch(1);
        ExecutorService service = Executors.newFixedThreadPool(threadCount);
        List<Future<Throwable>> workers = new ArrayList<>(threadCount);
        boolean needShutdown = true;
        try {
            for (int i = 0; i != threadCount; ++i) {
                final int index = i;
                workers.add(service.submit(new Callable<Throwable>() {
                    @Override
                    public Throwable call() {
                        try {
                            Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
                            for (int j = 0; j != 20; ++j) {
                                sensor.record((j * index), ((System.currentTimeMillis()) + j), false);
                                sensor.checkQuotas();
                            }
                            return null;
                        } catch (Throwable e) {
                            return e;
                        }
                    }
                }));
            }
            latch.countDown();
            service.shutdown();
            Assert.assertTrue(service.awaitTermination(10, TimeUnit.SECONDS));
            needShutdown = false;
            for (Future<Throwable> callable : workers) {
                Assert.assertTrue("If this failure happen frequently, we can try to increase the wait time", callable.isDone());
                Assert.assertNull("Sensor#checkQuotas SHOULD be thread-safe!", callable.get());
            }
        } finally {
            if (needShutdown) {
                service.shutdownNow();
            }
        }
    }
}

