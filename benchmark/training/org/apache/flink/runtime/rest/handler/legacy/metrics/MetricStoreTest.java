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
package org.apache.flink.runtime.rest.handler.legacy.metrics;


import MetricDump.CounterDump;
import QueryScopeInfo.JobManagerQueryScopeInfo;
import java.io.IOException;
import org.apache.flink.runtime.metrics.dump.MetricDump;
import org.apache.flink.runtime.metrics.dump.QueryScopeInfo;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the MetricStore.
 */
public class MetricStoreTest extends TestLogger {
    @Test
    public void testAdd() throws IOException {
        MetricStore store = MetricStoreTest.setupStore(new MetricStore());
        Assert.assertEquals("0", store.getJobManagerMetricStore().getMetric("abc.metric1", "-1"));
        Assert.assertEquals("1", store.getTaskManagerMetricStore("tmid").getMetric("abc.metric2", "-1"));
        Assert.assertEquals("2", store.getJobMetricStore("jobid").getMetric("abc.metric3", "-1"));
        Assert.assertEquals("3", store.getJobMetricStore("jobid").getMetric("abc.metric4", "-1"));
        Assert.assertEquals("4", store.getTaskMetricStore("jobid", "taskid").getMetric("8.abc.metric5", "-1"));
        Assert.assertEquals("5", store.getTaskMetricStore("jobid", "taskid").getMetric("8.opname.abc.metric6", "-1"));
        Assert.assertEquals("6", store.getTaskMetricStore("jobid", "taskid").getMetric("8.opname.abc.metric7", "-1"));
    }

    @Test
    public void testMalformedNameHandling() {
        MetricStore store = new MetricStore();
        // -----verify that no exceptions are thrown
        // null
        store.add(null);
        // empty name
        QueryScopeInfo.JobManagerQueryScopeInfo info = new QueryScopeInfo.JobManagerQueryScopeInfo("");
        MetricDump.CounterDump cd = new MetricDump.CounterDump(info, "", 0);
        store.add(cd);
        // -----verify that no side effects occur
        Assert.assertEquals(0, store.getJobManager().metrics.size());
        Assert.assertEquals(0, store.getTaskManagers().size());
        Assert.assertEquals(0, store.getJobs().size());
    }
}

