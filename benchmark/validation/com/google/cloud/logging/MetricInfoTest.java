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
package com.google.cloud.logging;


import org.junit.Assert;
import org.junit.Test;


public class MetricInfoTest {
    private static final String NAME = "name";

    private static final String FILTER = "logName=projects/my-projectid/logs/syslog";

    private static final String DESCRIPTION = "description";

    private static final String NEW_NAME = "newName";

    private static final String NEW_FILTER = "logName=projects/my-projectid/logs/newSyslog";

    private static final String NEW_DESCRIPTION = "newDescription";

    private static final MetricInfo METRIC_INFO = MetricInfo.newBuilder(MetricInfoTest.NAME, MetricInfoTest.FILTER).setDescription(MetricInfoTest.DESCRIPTION).build();

    @Test
    public void testOf() {
        MetricInfo metricInfo = MetricInfo.of(MetricInfoTest.NAME, MetricInfoTest.FILTER);
        Assert.assertEquals(MetricInfoTest.NAME, metricInfo.getName());
        Assert.assertEquals(MetricInfoTest.FILTER, metricInfo.getFilter());
        Assert.assertNull(metricInfo.getDescription());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(MetricInfoTest.NAME, MetricInfoTest.METRIC_INFO.getName());
        Assert.assertEquals(MetricInfoTest.FILTER, MetricInfoTest.METRIC_INFO.getFilter());
        Assert.assertEquals(MetricInfoTest.DESCRIPTION, MetricInfoTest.METRIC_INFO.getDescription());
    }

    @Test
    public void testToBuilder() {
        compareMetricInfo(MetricInfoTest.METRIC_INFO, MetricInfoTest.METRIC_INFO.toBuilder().build());
        MetricInfo metricInfo = MetricInfoTest.METRIC_INFO.toBuilder().setName(MetricInfoTest.NEW_NAME).setDescription(MetricInfoTest.NEW_DESCRIPTION).setFilter(MetricInfoTest.NEW_FILTER).build();
        Assert.assertEquals(MetricInfoTest.NEW_NAME, metricInfo.getName());
        Assert.assertEquals(MetricInfoTest.NEW_FILTER, metricInfo.getFilter());
        Assert.assertEquals(MetricInfoTest.NEW_DESCRIPTION, metricInfo.getDescription());
        metricInfo = metricInfo.toBuilder().setName(MetricInfoTest.NAME).setDescription(MetricInfoTest.DESCRIPTION).setFilter(MetricInfoTest.FILTER).build();
        compareMetricInfo(MetricInfoTest.METRIC_INFO, metricInfo);
    }

    @Test
    public void testToAndFromPb() {
        compareMetricInfo(MetricInfoTest.METRIC_INFO, MetricInfo.fromPb(MetricInfoTest.METRIC_INFO.toPb()));
        MetricInfo metricInfo = MetricInfo.of(MetricInfoTest.NAME, MetricInfoTest.FILTER);
        compareMetricInfo(metricInfo, MetricInfo.fromPb(metricInfo.toPb()));
    }
}

