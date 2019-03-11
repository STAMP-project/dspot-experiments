/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.aws.cloudwatch;


import PutCloudWatchMetric.MAXIMUM;
import PutCloudWatchMetric.METRIC_NAME;
import PutCloudWatchMetric.MINIMUM;
import PutCloudWatchMetric.NAMESPACE;
import PutCloudWatchMetric.REL_FAILURE;
import PutCloudWatchMetric.REL_SUCCESS;
import PutCloudWatchMetric.SAMPLECOUNT;
import PutCloudWatchMetric.SUM;
import PutCloudWatchMetric.TIMESTAMP;
import PutCloudWatchMetric.UNIT;
import PutCloudWatchMetric.VALUE;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.InvalidParameterValueException;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link PutCloudWatchMetric}.
 */
public class TestPutCloudWatchMetric {
    @Test
    public void testPutSimpleMetric() throws Exception {
        MockPutCloudWatchMetric mockPutCloudWatchMetric = new MockPutCloudWatchMetric();
        final TestRunner runner = TestRunners.newTestRunner(mockPutCloudWatchMetric);
        runner.setProperty(NAMESPACE, "TestNamespace");
        runner.setProperty(METRIC_NAME, "TestMetric");
        runner.setProperty(VALUE, "1.0");
        runner.setProperty(UNIT, "Count");
        runner.setProperty(TIMESTAMP, "1476296132575");
        runner.assertValid();
        runner.enqueue(new byte[]{  });
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertEquals(1, mockPutCloudWatchMetric.putMetricDataCallCount);
        Assert.assertEquals("TestNamespace", mockPutCloudWatchMetric.actualNamespace);
        MetricDatum datum = mockPutCloudWatchMetric.actualMetricData.get(0);
        Assert.assertEquals("TestMetric", datum.getMetricName());
        Assert.assertEquals(1.0, datum.getValue(), 1.0E-4);
    }

    @Test
    public void testValueLiteralDoubleInvalid() throws Exception {
        MockPutCloudWatchMetric mockPutCloudWatchMetric = new MockPutCloudWatchMetric();
        final TestRunner runner = TestRunners.newTestRunner(mockPutCloudWatchMetric);
        runner.setProperty(NAMESPACE, "TestNamespace");
        runner.setProperty(METRIC_NAME, "TestMetric");
        runner.setProperty(VALUE, "nan");
        runner.assertNotValid();
    }

    @Test
    public void testMissingBothValueAndStatisticSetInvalid() throws Exception {
        MockPutCloudWatchMetric mockPutCloudWatchMetric = new MockPutCloudWatchMetric();
        final TestRunner runner = TestRunners.newTestRunner(mockPutCloudWatchMetric);
        runner.setProperty(NAMESPACE, "TestNamespace");
        runner.setProperty(METRIC_NAME, "TestMetric");
        runner.assertNotValid();
    }

    @Test
    public void testContainsBothValueAndStatisticSetInvalid() throws Exception {
        MockPutCloudWatchMetric mockPutCloudWatchMetric = new MockPutCloudWatchMetric();
        final TestRunner runner = TestRunners.newTestRunner(mockPutCloudWatchMetric);
        runner.setProperty(NAMESPACE, "TestNamespace");
        runner.setProperty(METRIC_NAME, "TestMetric");
        runner.setProperty(VALUE, "1.0");
        runner.setProperty(UNIT, "Count");
        runner.setProperty(TIMESTAMP, "1476296132575");
        runner.setProperty(MINIMUM, "1.0");
        runner.setProperty(MAXIMUM, "2.0");
        runner.setProperty(SUM, "3.0");
        runner.setProperty(SAMPLECOUNT, "2");
        runner.assertNotValid();
    }

    @Test
    public void testContainsIncompleteStatisticSetInvalid() throws Exception {
        MockPutCloudWatchMetric mockPutCloudWatchMetric = new MockPutCloudWatchMetric();
        final TestRunner runner = TestRunners.newTestRunner(mockPutCloudWatchMetric);
        runner.setProperty(NAMESPACE, "TestNamespace");
        runner.setProperty(METRIC_NAME, "TestMetric");
        runner.setProperty(UNIT, "Count");
        runner.setProperty(TIMESTAMP, "1476296132575");
        runner.setProperty(MINIMUM, "1.0");
        runner.setProperty(MAXIMUM, "2.0");
        runner.setProperty(SUM, "3.0");
        // missing sample count
        runner.assertNotValid();
    }

    @Test
    public void testContainsBothValueAndIncompleteStatisticSetInvalid() throws Exception {
        MockPutCloudWatchMetric mockPutCloudWatchMetric = new MockPutCloudWatchMetric();
        final TestRunner runner = TestRunners.newTestRunner(mockPutCloudWatchMetric);
        runner.setProperty(NAMESPACE, "TestNamespace");
        runner.setProperty(METRIC_NAME, "TestMetric");
        runner.setProperty(VALUE, "1.0");
        runner.setProperty(UNIT, "Count");
        runner.setProperty(TIMESTAMP, "1476296132575");
        runner.setProperty(MINIMUM, "1.0");
        runner.assertNotValid();
    }

    @Test
    public void testMetricExpressionValid() throws Exception {
        MockPutCloudWatchMetric mockPutCloudWatchMetric = new MockPutCloudWatchMetric();
        final TestRunner runner = TestRunners.newTestRunner(mockPutCloudWatchMetric);
        runner.setProperty(NAMESPACE, "TestNamespace");
        runner.setProperty(METRIC_NAME, "TestMetric");
        runner.setProperty(VALUE, "${metric.value}");
        runner.assertValid();
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("metric.value", "1.23");
        runner.enqueue(new byte[]{  }, attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertEquals(1, mockPutCloudWatchMetric.putMetricDataCallCount);
        Assert.assertEquals("TestNamespace", mockPutCloudWatchMetric.actualNamespace);
        MetricDatum datum = mockPutCloudWatchMetric.actualMetricData.get(0);
        Assert.assertEquals("TestMetric", datum.getMetricName());
        Assert.assertEquals(1.23, datum.getValue(), 1.0E-4);
    }

    @Test
    public void testStatisticSet() throws Exception {
        MockPutCloudWatchMetric mockPutCloudWatchMetric = new MockPutCloudWatchMetric();
        final TestRunner runner = TestRunners.newTestRunner(mockPutCloudWatchMetric);
        runner.setProperty(NAMESPACE, "TestNamespace");
        runner.setProperty(METRIC_NAME, "TestMetric");
        runner.setProperty(MINIMUM, "${metric.min}");
        runner.setProperty(MAXIMUM, "${metric.max}");
        runner.setProperty(SUM, "${metric.sum}");
        runner.setProperty(SAMPLECOUNT, "${metric.count}");
        runner.assertValid();
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("metric.min", "1");
        attributes.put("metric.max", "2");
        attributes.put("metric.sum", "3");
        attributes.put("metric.count", "2");
        runner.enqueue(new byte[]{  }, attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertEquals(1, mockPutCloudWatchMetric.putMetricDataCallCount);
        Assert.assertEquals("TestNamespace", mockPutCloudWatchMetric.actualNamespace);
        MetricDatum datum = mockPutCloudWatchMetric.actualMetricData.get(0);
        Assert.assertEquals("TestMetric", datum.getMetricName());
        Assert.assertEquals(1.0, datum.getStatisticValues().getMinimum(), 1.0E-4);
        Assert.assertEquals(2.0, datum.getStatisticValues().getMaximum(), 1.0E-4);
        Assert.assertEquals(3.0, datum.getStatisticValues().getSum(), 1.0E-4);
        Assert.assertEquals(2.0, datum.getStatisticValues().getSampleCount(), 1.0E-4);
    }

    @Test
    public void testDimensions() throws Exception {
        MockPutCloudWatchMetric mockPutCloudWatchMetric = new MockPutCloudWatchMetric();
        final TestRunner runner = TestRunners.newTestRunner(mockPutCloudWatchMetric);
        runner.setProperty(NAMESPACE, "TestNamespace");
        runner.setProperty(METRIC_NAME, "TestMetric");
        runner.setProperty(VALUE, "1.0");
        runner.setProperty(UNIT, "Count");
        runner.setProperty(TIMESTAMP, "1476296132575");
        runner.setProperty(new PropertyDescriptor.Builder().dynamic(true).name("dim1").build(), "${metric.dim1}");
        runner.setProperty(new PropertyDescriptor.Builder().dynamic(true).name("dim2").build(), "val2");
        runner.assertValid();
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("metric.dim1", "1");
        runner.enqueue(new byte[]{  }, attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertEquals(1, mockPutCloudWatchMetric.putMetricDataCallCount);
        Assert.assertEquals("TestNamespace", mockPutCloudWatchMetric.actualNamespace);
        MetricDatum datum = mockPutCloudWatchMetric.actualMetricData.get(0);
        Assert.assertEquals("TestMetric", datum.getMetricName());
        Assert.assertEquals(1.0, datum.getValue(), 1.0E-4);
        List<Dimension> dimensions = datum.getDimensions();
        Collections.sort(dimensions, ( d1, d2) -> d1.getName().compareTo(d2.getName()));
        Assert.assertEquals(2, dimensions.size());
        Assert.assertEquals("dim1", dimensions.get(0).getName());
        Assert.assertEquals("1", dimensions.get(0).getValue());
        Assert.assertEquals("dim2", dimensions.get(1).getName());
        Assert.assertEquals("val2", dimensions.get(1).getValue());
    }

    @Test
    public void testMaximumDimensions() throws Exception {
        MockPutCloudWatchMetric mockPutCloudWatchMetric = new MockPutCloudWatchMetric();
        final TestRunner runner = TestRunners.newTestRunner(mockPutCloudWatchMetric);
        runner.setProperty(NAMESPACE, "TestNamespace");
        runner.setProperty(METRIC_NAME, "TestMetric");
        runner.setProperty(VALUE, "1.0");
        runner.setProperty(UNIT, "Count");
        runner.setProperty(TIMESTAMP, "1476296132575");
        for (int i = 0; i < 10; i++) {
            runner.setProperty(new PropertyDescriptor.Builder().dynamic(true).name(("dim" + i)).build(), "0");
        }
        runner.assertValid();
    }

    @Test
    public void testTooManyDimensions() throws Exception {
        MockPutCloudWatchMetric mockPutCloudWatchMetric = new MockPutCloudWatchMetric();
        final TestRunner runner = TestRunners.newTestRunner(mockPutCloudWatchMetric);
        runner.setProperty(NAMESPACE, "TestNamespace");
        runner.setProperty(METRIC_NAME, "TestMetric");
        runner.setProperty(VALUE, "1.0");
        runner.setProperty(UNIT, "Count");
        runner.setProperty(TIMESTAMP, "1476296132575");
        for (int i = 0; i < 11; i++) {
            runner.setProperty(new PropertyDescriptor.Builder().dynamic(true).name(("dim" + i)).build(), "0");
        }
        runner.assertNotValid();
    }

    @Test
    public void testMetricExpressionInvalidRoutesToFailure() throws Exception {
        MockPutCloudWatchMetric mockPutCloudWatchMetric = new MockPutCloudWatchMetric();
        final TestRunner runner = TestRunners.newTestRunner(mockPutCloudWatchMetric);
        runner.setProperty(NAMESPACE, "TestNamespace");
        runner.setProperty(METRIC_NAME, "TestMetric");
        runner.setProperty(VALUE, "${metric.value}");
        runner.assertValid();
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("metric.value", "nan");
        runner.enqueue(new byte[]{  }, attributes);
        runner.run();
        Assert.assertEquals(0, mockPutCloudWatchMetric.putMetricDataCallCount);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testInvalidUnitRoutesToFailure() throws Exception {
        MockPutCloudWatchMetric mockPutCloudWatchMetric = new MockPutCloudWatchMetric();
        mockPutCloudWatchMetric.throwException = new InvalidParameterValueException("Unit error message");
        final TestRunner runner = TestRunners.newTestRunner(mockPutCloudWatchMetric);
        runner.setProperty(NAMESPACE, "TestNamespace");
        runner.setProperty(METRIC_NAME, "TestMetric");
        runner.setProperty(UNIT, "BogusUnit");
        runner.setProperty(VALUE, "1");
        runner.assertValid();
        runner.enqueue(new byte[]{  });
        runner.run();
        Assert.assertEquals(1, mockPutCloudWatchMetric.putMetricDataCallCount);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testTimestampExpressionInvalidRoutesToFailure() throws Exception {
        MockPutCloudWatchMetric mockPutCloudWatchMetric = new MockPutCloudWatchMetric();
        final TestRunner runner = TestRunners.newTestRunner(mockPutCloudWatchMetric);
        runner.setProperty(NAMESPACE, "TestNamespace");
        runner.setProperty(METRIC_NAME, "TestMetric");
        runner.setProperty(UNIT, "Count");
        runner.setProperty(VALUE, "1");
        runner.setProperty(TIMESTAMP, "${timestamp.value}");
        runner.assertValid();
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("timestamp.value", "1476296132575broken");
        runner.enqueue(new byte[]{  }, attributes);
        runner.run();
        Assert.assertEquals(0, mockPutCloudWatchMetric.putMetricDataCallCount);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }
}

