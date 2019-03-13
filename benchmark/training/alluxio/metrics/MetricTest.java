/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.metrics;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link Metric}.
 */
public final class MetricTest {
    @Test
    public void proto() {
        Metric metric = MetricTest.createRandom();
        Metric other = Metric.fromProto(metric.toProto());
        checkEquality(metric, other);
    }

    @Test
    public void testFullNameParsing() {
        String fullName = "Client.192_1_1_1|A.metric.tag1:A::/.tag2:B:/";
        Metric metric = Metric.from(fullName, 1);
        Assert.assertEquals(fullName, metric.getFullMetricName());
    }

    @Test
    public void testMetricNameWithTags() {
        Assert.assertEquals("metric.t1:v1.t2:v2:", Metric.getMetricNameWithTags("metric", "t1", "v1", "t2", "v2:"));
    }
}

