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


import MetricsSystem.METRIC_REGISTRY;
import com.codahale.metrics.Counter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link MetricsSystem}.
 */
public final class MetricsSystemTest {
    private MetricsConfig mMetricsConfig;

    private static Counter sCounter = METRIC_REGISTRY.counter(MetricsSystem.getMetricName("counter"));

    /**
     * Tests the metrics for a master and a worker.
     */
    @Test
    public void metricsSystem() {
        MetricsSystem.startSinksFromConfig(mMetricsConfig);
        Assert.assertEquals(2, MetricsSystem.getNumSinks());
        // Make sure it doesn't crash.
        MetricsSystemTest.sCounter.inc();
        MetricsSystem.stopSinks();
    }
}

