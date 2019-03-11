/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.examples.logging.snippets;


import com.google.cloud.logging.Logging;
import com.google.cloud.logging.Metric;
import com.google.cloud.logging.testing.RemoteLoggingHelper;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class ITMetricSnippets {
    private static final String METRIC_NAME = RemoteLoggingHelper.formatForTest("it_metric_snippets");

    private static final String METRIC_FILTER = "severity>=ERROR";

    private static final String DESCRIPTION = "description";

    private static final String UPDATED_DESCRIPTION = "A more detailed description";

    private static Logging logging;

    private static MetricSnippets metricSnippets;

    @Test
    public void testMetric() throws InterruptedException, ExecutionException {
        Metric metric = ITMetricSnippets.metricSnippets.reload();
        Assert.assertNotNull(metric);
        Metric updatedMetric = ITMetricSnippets.metricSnippets.update();
        Assert.assertEquals(ITMetricSnippets.UPDATED_DESCRIPTION, updatedMetric.getDescription());
        updatedMetric = ITMetricSnippets.metricSnippets.reloadAsync();
        Assert.assertNotNull(updatedMetric);
        Assert.assertEquals(ITMetricSnippets.UPDATED_DESCRIPTION, updatedMetric.getDescription());
        metric.update();
        updatedMetric = ITMetricSnippets.metricSnippets.updateAsync();
        Assert.assertEquals(ITMetricSnippets.UPDATED_DESCRIPTION, updatedMetric.getDescription());
        Assert.assertTrue(ITMetricSnippets.metricSnippets.delete());
        Assert.assertFalse(ITMetricSnippets.metricSnippets.deleteAsync());
    }
}

