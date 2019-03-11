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


import com.google.api.core.ApiFutures;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class MetricTest {
    private static final String NAME = "name";

    private static final String FILTER = "logName=projects/my-projectid/logs/syslog";

    private static final String DESCRIPTION = "description";

    private static final String NEW_NAME = "newName";

    private static final String NEW_FILTER = "logName=projects/my-projectid/logs/newSyslog";

    private static final String NEW_DESCRIPTION = "newDescription";

    private static final MetricInfo METRIC_INFO = MetricInfo.newBuilder(MetricTest.NAME, MetricTest.FILTER).setDescription(MetricTest.DESCRIPTION).build();

    private final Logging serviceMockReturnsOptions = createStrictMock(Logging.class);

    private final LoggingOptions mockOptions = createMock(LoggingOptions.class);

    private Logging logging;

    private Metric expectedMetric;

    private Metric metric;

    @Test
    public void testBuilder() {
        initializeExpectedMetric(2);
        replay(logging);
        Metric builtMetric = expectedMetric.toBuilder().setName(MetricTest.NEW_NAME).setFilter(MetricTest.NEW_FILTER).setDescription(MetricTest.NEW_DESCRIPTION).build();
        Assert.assertEquals(MetricTest.NEW_NAME, builtMetric.getName());
        Assert.assertEquals(MetricTest.NEW_DESCRIPTION, builtMetric.getDescription());
        Assert.assertEquals(MetricTest.NEW_FILTER, builtMetric.getFilter());
        Assert.assertSame(serviceMockReturnsOptions, builtMetric.getLogging());
    }

    @Test
    public void testToBuilder() {
        initializeExpectedMetric(2);
        replay(logging);
        compareMetric(expectedMetric, expectedMetric.toBuilder().build());
    }

    @Test
    public void testReload() {
        initializeExpectedMetric(2);
        MetricInfo updatedInfo = MetricTest.METRIC_INFO.toBuilder().setFilter(MetricTest.NEW_FILTER).build();
        Metric expectedMetric = new Metric(serviceMockReturnsOptions, new MetricInfo.BuilderImpl(updatedInfo));
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.getMetric(MetricTest.NAME)).andReturn(expectedMetric);
        replay(logging);
        initializeMetric();
        Metric updatedMetric = metric.reload();
        compareMetric(expectedMetric, updatedMetric);
    }

    @Test
    public void testReloadNull() {
        initializeExpectedMetric(1);
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.getMetric(MetricTest.NAME)).andReturn(null);
        replay(logging);
        initializeMetric();
        Assert.assertNull(metric.reload());
    }

    @Test
    public void testReloadAsync() throws InterruptedException, ExecutionException {
        initializeExpectedMetric(2);
        MetricInfo updatedInfo = MetricTest.METRIC_INFO.toBuilder().setFilter(MetricTest.NEW_FILTER).build();
        Metric expectedMetric = new Metric(serviceMockReturnsOptions, new MetricInfo.BuilderImpl(updatedInfo));
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.getMetricAsync(MetricTest.NAME)).andReturn(ApiFutures.immediateFuture(expectedMetric));
        replay(logging);
        initializeMetric();
        Metric updatedMetric = metric.reloadAsync().get();
        compareMetric(expectedMetric, updatedMetric);
    }

    @Test
    public void testReloadAsyncNull() throws InterruptedException, ExecutionException {
        initializeExpectedMetric(1);
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.getMetricAsync(MetricTest.NAME)).andReturn(ApiFutures.<Metric>immediateFuture(null));
        replay(logging);
        initializeMetric();
        Assert.assertNull(metric.reloadAsync().get());
    }

    @Test
    public void testUpdate() {
        initializeExpectedMetric(2);
        MetricInfo updatedInfo = MetricTest.METRIC_INFO.toBuilder().setFilter(MetricTest.NEW_FILTER).build();
        Metric expectedMetric = new Metric(serviceMockReturnsOptions, new MetricInfo.BuilderImpl(updatedInfo));
        expect(logging.getOptions()).andReturn(mockOptions).times(2);
        expect(logging.update(expectedMetric)).andReturn(expectedMetric);
        replay(logging);
        initializeMetric();
        Metric updatedMetric = metric.toBuilder().setFilter(MetricTest.NEW_FILTER).build().update();
        compareMetric(expectedMetric, updatedMetric);
    }

    @Test
    public void testUpdateAsync() throws InterruptedException, ExecutionException {
        initializeExpectedMetric(2);
        MetricInfo updatedInfo = MetricTest.METRIC_INFO.toBuilder().setFilter(MetricTest.NEW_FILTER).build();
        Metric expectedMetric = new Metric(serviceMockReturnsOptions, new MetricInfo.BuilderImpl(updatedInfo));
        expect(logging.getOptions()).andReturn(mockOptions).times(2);
        expect(logging.updateAsync(expectedMetric)).andReturn(ApiFutures.immediateFuture(expectedMetric));
        replay(logging);
        initializeMetric();
        Metric updatedMetric = metric.toBuilder().setFilter(MetricTest.NEW_FILTER).build().updateAsync().get();
        compareMetric(expectedMetric, updatedMetric);
    }

    @Test
    public void testDeleteTrue() {
        initializeExpectedMetric(1);
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.deleteMetric(MetricTest.NAME)).andReturn(true);
        replay(logging);
        initializeMetric();
        Assert.assertTrue(metric.delete());
    }

    @Test
    public void testDeleteFalse() {
        initializeExpectedMetric(1);
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.deleteMetric(MetricTest.NAME)).andReturn(false);
        replay(logging);
        initializeMetric();
        Assert.assertFalse(metric.delete());
    }

    @Test
    public void testDeleteAsyncTrue() throws InterruptedException, ExecutionException {
        initializeExpectedMetric(1);
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.deleteMetricAsync(MetricTest.NAME)).andReturn(ApiFutures.immediateFuture(true));
        replay(logging);
        initializeMetric();
        Assert.assertTrue(metric.deleteAsync().get());
    }

    @Test
    public void testDeleteAsyncFalse() throws InterruptedException, ExecutionException {
        initializeExpectedMetric(1);
        expect(logging.getOptions()).andReturn(mockOptions);
        expect(logging.deleteMetricAsync(MetricTest.NAME)).andReturn(ApiFutures.immediateFuture(false));
        replay(logging);
        initializeMetric();
        Assert.assertFalse(metric.deleteAsync().get());
    }
}

