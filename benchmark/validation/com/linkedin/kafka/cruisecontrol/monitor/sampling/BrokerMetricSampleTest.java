/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.monitor.sampling;


import BrokerMetricSample.LATEST_SUPPORTED_VERSION;
import BrokerMetricSample.MIN_SUPPORTED_VERSION;
import com.linkedin.cruisecontrol.metricdef.MetricDef;
import com.linkedin.cruisecontrol.metricdef.MetricInfo;
import com.linkedin.kafka.cruisecontrol.metricsreporter.exception.UnknownVersionException;
import com.linkedin.kafka.cruisecontrol.metricsreporter.metric.RawMetricType;
import com.linkedin.kafka.cruisecontrol.monitor.metricdefinition.KafkaMetricDef;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;

import static BrokerMetricSample.LATEST_SUPPORTED_VERSION;
import static BrokerMetricSample.MIN_SUPPORTED_VERSION;


public class BrokerMetricSampleTest {
    @Test
    public void testSerdeWithLatestSerdeVersion() throws UnknownVersionException {
        MetricDef brokerMetricDef = KafkaMetricDef.brokerMetricDef();
        BrokerMetricSample sample = new BrokerMetricSample("host", 0, LATEST_SUPPORTED_VERSION);
        double value = 1.0;
        for (MetricInfo metricInfo : brokerMetricDef.all()) {
            sample.record(metricInfo, value);
            value += 1;
        }
        sample.close(((long) (value)));
        byte[] bytes = sample.toBytes();
        Assert.assertEquals(461, bytes.length);
        BrokerMetricSample deserializedSample = BrokerMetricSample.fromBytes(bytes);
        Assert.assertEquals("host", deserializedSample.entity().host());
        Assert.assertEquals(0, deserializedSample.entity().brokerId());
        Assert.assertEquals(LATEST_SUPPORTED_VERSION, deserializedSample.deserializationVersion());
        // Disk usage is not one of the broker raw metric type so we add 1.
        int expectedNumRecords = 1;
        expectedNumRecords += RawMetricType.brokerMetricTypesDiffByVersion().entrySet().stream().mapToInt(( entry) -> entry.getValue().size()).sum();
        Assert.assertEquals(expectedNumRecords, deserializedSample.allMetricValues().size());
        value = 1.0;
        for (MetricInfo metricInfo : brokerMetricDef.all()) {
            Assert.assertEquals(value, deserializedSample.metricValue(metricInfo.id()), 0.0);
            value += 1;
        }
        Assert.assertEquals(value, deserializedSample.sampleTime(), 0.0);
    }

    @Test
    public void testSerdeWithOldSerdeVersion() throws UnknownVersionException {
        MetricDef brokerMetricDef = KafkaMetricDef.brokerMetricDef();
        BrokerMetricSample sample = new BrokerMetricSample("host", 0, MIN_SUPPORTED_VERSION);
        double value = 1.0;
        for (MetricInfo metricInfo : brokerMetricDef.all()) {
            sample.record(metricInfo, value);
            value += 1;
        }
        sample.close(((long) (value)));
        byte[] bytes = sample.toBytes();
        Assert.assertEquals(461, bytes.length);
        BrokerMetricSample deserializedSample = BrokerMetricSample.fromBytes(bytes);
        Assert.assertEquals("host", deserializedSample.entity().host());
        Assert.assertEquals(0, deserializedSample.entity().brokerId());
        Assert.assertEquals(MIN_SUPPORTED_VERSION, deserializedSample.deserializationVersion());
        // Disk usage is not one of the broker raw metric type so we add 1.
        int expectedNumRecords = (RawMetricType.brokerMetricTypesDiffForVersion(MIN_SUPPORTED_VERSION).size()) + 1;
        Assert.assertEquals(expectedNumRecords, deserializedSample.allMetricValues().size());
        value = 1.0;
        for (MetricInfo metricInfo : brokerMetricDef.all()) {
            Assert.assertEquals(value, deserializedSample.metricValue(metricInfo.id()), 0.0);
            value += 1;
            if (value > expectedNumRecords) {
                break;
            }
        }
        Assert.assertNotEquals(value, deserializedSample.sampleTime(), 0.0);
    }

    @Test
    public void patternTest() {
        Pattern pattern = Pattern.compile("topic1|.*aaa.*");
        Assert.assertTrue(pattern.matcher("topic1").matches());
        Assert.assertTrue(pattern.matcher("bbaaask").matches());
        pattern = Pattern.compile("");
        Assert.assertFalse(pattern.matcher("sf").matches());
    }
}

