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
package org.apache.kafka.tools;


import PushHttpMetricsReporter.METRICS_PERIOD_CONFIG;
import PushHttpMetricsReporter.METRICS_URL_CONFIG;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.metrics.Gauge;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.metrics.MetricConfig;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(PushHttpMetricsReporter.class)
public class PushHttpMetricsReporterTest {
    private static final java.net.URL URL;

    static {
        try {
            URL = new java.net.URL("http://fake:80");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private PushHttpMetricsReporter reporter;

    private Time time = new MockTime();

    @MockStrict
    private ScheduledExecutorService executor;

    private Capture<Runnable> reportRunnable = EasyMock.newCapture();

    @MockStrict
    private HttpURLConnection httpReq;

    @MockStrict
    private OutputStream httpOut;

    private Capture<byte[]> httpPayload = EasyMock.newCapture();

    @MockStrict
    private InputStream httpErr;

    @Test
    public void testConfigureClose() throws Exception {
        expectConfigure();
        expectClose();
        replayAll();
        configure();
        reporter.close();
        verifyAll();
    }

    @Test(expected = ConfigException.class)
    public void testConfigureBadUrl() throws Exception {
        Map<String, String> config = new HashMap<>();
        config.put(METRICS_URL_CONFIG, "malformed;url");
        config.put(METRICS_PERIOD_CONFIG, "5");
        reporter.configure(config);
    }

    @Test(expected = ConfigException.class)
    public void testConfigureMissingPeriod() throws Exception {
        Map<String, String> config = new HashMap<>();
        config.put(METRICS_URL_CONFIG, PushHttpMetricsReporterTest.URL.toString());
        reporter.configure(config);
    }

    @Test
    public void testNoMetrics() throws Exception {
        expectConfigure();
        expectRequest(200);
        expectClose();
        replayAll();
        configure();
        reportRunnable.getValue().run();
        JsonNode payload = new ObjectMapper().readTree(httpPayload.getValue());
        Assert.assertTrue(payload.isObject());
        assertPayloadHasClientInfo(payload);
        // Should contain an empty list of metrics, i.e. we report updates even if there are no metrics to report to
        // indicate liveness
        JsonNode metrics = payload.get("metrics");
        Assert.assertTrue(metrics.isArray());
        Assert.assertEquals(0, metrics.size());
        reporter.close();
        verifyAll();
    }

    // For error conditions, we expect them to come with a response body that we can read & log
    @Test
    public void testClientError() throws Exception {
        expectConfigure();
        expectRequest(400, true);
        expectClose();
        replayAll();
        configure();
        reportRunnable.getValue().run();
        reporter.close();
        verifyAll();
    }

    @Test
    public void testServerError() throws Exception {
        expectConfigure();
        expectRequest(500, true);
        expectClose();
        replayAll();
        configure();
        reportRunnable.getValue().run();
        reporter.close();
        verifyAll();
    }

    @Test
    public void testMetricValues() throws Exception {
        expectConfigure();
        expectRequest(200);
        expectClose();
        replayAll();
        configure();
        KafkaMetric metric1 = new KafkaMetric(new Object(), new MetricName("name1", "group1", "desc1", Collections.singletonMap("key1", "value1")), new PushHttpMetricsReporterTest.ImmutableValue(1.0), null, time);
        KafkaMetric newMetric1 = new KafkaMetric(new Object(), new MetricName("name1", "group1", "desc1", Collections.singletonMap("key1", "value1")), new PushHttpMetricsReporterTest.ImmutableValue((-1.0)), null, time);
        KafkaMetric metric2 = new KafkaMetric(new Object(), new MetricName("name2", "group2", "desc2", Collections.singletonMap("key2", "value2")), new PushHttpMetricsReporterTest.ImmutableValue(2.0), null, time);
        KafkaMetric metric3 = new KafkaMetric(new Object(), new MetricName("name3", "group3", "desc3", Collections.singletonMap("key3", "value3")), new PushHttpMetricsReporterTest.ImmutableValue(3.0), null, time);
        KafkaMetric metric4 = new KafkaMetric(new Object(), new MetricName("name4", "group4", "desc4", Collections.singletonMap("key4", "value4")), new PushHttpMetricsReporterTest.ImmutableValue("value4"), null, time);
        reporter.init(Arrays.asList(metric1, metric2, metric4));
        reporter.metricChange(newMetric1);// added in init, modified

        reporter.metricChange(metric3);// added by change

        reporter.metricRemoval(metric2);// added in init, deleted by removal

        reportRunnable.getValue().run();
        JsonNode payload = new ObjectMapper().readTree(httpPayload.getValue());
        Assert.assertTrue(payload.isObject());
        assertPayloadHasClientInfo(payload);
        // We should be left with the modified version of metric1 and metric3
        JsonNode metrics = payload.get("metrics");
        Assert.assertTrue(metrics.isArray());
        Assert.assertEquals(3, metrics.size());
        List<JsonNode> metricsList = Arrays.asList(metrics.get(0), metrics.get(1), metrics.get(2));
        // Sort metrics based on name so that we can verify the value for each metric below
        metricsList.sort(( m1, m2) -> m1.get("name").textValue().compareTo(m2.get("name").textValue()));
        JsonNode m1 = metricsList.get(0);
        Assert.assertEquals("name1", m1.get("name").textValue());
        Assert.assertEquals("group1", m1.get("group").textValue());
        JsonNode m1Tags = m1.get("tags");
        Assert.assertTrue(m1Tags.isObject());
        Assert.assertEquals(1, m1Tags.size());
        Assert.assertEquals("value1", m1Tags.get("key1").textValue());
        Assert.assertEquals((-1.0), m1.get("value").doubleValue(), 0.0);
        JsonNode m3 = metricsList.get(1);
        Assert.assertEquals("name3", m3.get("name").textValue());
        Assert.assertEquals("group3", m3.get("group").textValue());
        JsonNode m3Tags = m3.get("tags");
        Assert.assertTrue(m3Tags.isObject());
        Assert.assertEquals(1, m3Tags.size());
        Assert.assertEquals("value3", m3Tags.get("key3").textValue());
        Assert.assertEquals(3.0, m3.get("value").doubleValue(), 0.0);
        JsonNode m4 = metricsList.get(2);
        Assert.assertEquals("name4", m4.get("name").textValue());
        Assert.assertEquals("group4", m4.get("group").textValue());
        JsonNode m4Tags = m4.get("tags");
        Assert.assertTrue(m4Tags.isObject());
        Assert.assertEquals(1, m4Tags.size());
        Assert.assertEquals("value4", m4Tags.get("key4").textValue());
        Assert.assertEquals("value4", m4.get("value").textValue());
        reporter.close();
        verifyAll();
    }

    static class ImmutableValue<T> implements Gauge<T> {
        private final T value;

        public ImmutableValue(T value) {
            this.value = value;
        }

        @Override
        public T value(MetricConfig config, long now) {
            return value;
        }
    }
}

