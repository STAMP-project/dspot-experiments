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
package org.apache.flink.runtime.metrics.groups;


import MetricOptions.SCOPE_NAMING_OPERATOR;
import MetricOptions.SCOPE_NAMING_TASK;
import MetricOptions.SCOPE_NAMING_TM;
import MetricOptions.SCOPE_NAMING_TM_JOB;
import QueryScopeInfo.TaskQueryScopeInfo;
import ScopeFormat.SCOPE_OPERATOR_NAME;
import TaskMetricGroup.METRICS_OPERATOR_NAME_MAX_LENGTH;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Metric;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.metrics.MetricRegistryConfiguration;
import org.apache.flink.runtime.metrics.MetricRegistryImpl;
import org.apache.flink.runtime.metrics.dump.QueryScopeInfo;
import org.apache.flink.runtime.metrics.util.DummyCharacterFilter;
import org.apache.flink.util.AbstractID;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link TaskMetricGroup}.
 */
public class TaskMetricGroupTest extends TestLogger {
    private MetricRegistryImpl registry;

    // ------------------------------------------------------------------------
    // scope tests
    // -----------------------------------------------------------------------
    @Test
    public void testGenerateScopeDefault() {
        JobVertexID vertexId = new JobVertexID();
        AbstractID executionId = new AbstractID();
        TaskManagerMetricGroup tmGroup = new TaskManagerMetricGroup(registry, "theHostName", "test-tm-id");
        TaskManagerJobMetricGroup jmGroup = new TaskManagerJobMetricGroup(registry, tmGroup, new JobID(), "myJobName");
        TaskMetricGroup taskGroup = new TaskMetricGroup(registry, jmGroup, vertexId, executionId, "aTaskName", 13, 2);
        Assert.assertArrayEquals(new String[]{ "theHostName", "taskmanager", "test-tm-id", "myJobName", "aTaskName", "13" }, taskGroup.getScopeComponents());
        Assert.assertEquals("theHostName.taskmanager.test-tm-id.myJobName.aTaskName.13.name", taskGroup.getMetricIdentifier("name"));
    }

    @Test
    public void testGenerateScopeCustom() throws Exception {
        Configuration cfg = new Configuration();
        cfg.setString(SCOPE_NAMING_TM, "abc");
        cfg.setString(SCOPE_NAMING_TM_JOB, "def");
        cfg.setString(SCOPE_NAMING_TASK, "<tm_id>.<job_id>.<task_id>.<task_attempt_id>");
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(cfg));
        JobID jid = new JobID();
        JobVertexID vertexId = new JobVertexID();
        AbstractID executionId = new AbstractID();
        TaskManagerMetricGroup tmGroup = new TaskManagerMetricGroup(registry, "theHostName", "test-tm-id");
        TaskManagerJobMetricGroup jmGroup = new TaskManagerJobMetricGroup(registry, tmGroup, jid, "myJobName");
        TaskMetricGroup taskGroup = new TaskMetricGroup(registry, jmGroup, vertexId, executionId, "aTaskName", 13, 2);
        Assert.assertArrayEquals(new String[]{ "test-tm-id", jid.toString(), vertexId.toString(), executionId.toString() }, taskGroup.getScopeComponents());
        Assert.assertEquals(String.format("test-tm-id.%s.%s.%s.name", jid, vertexId, executionId), taskGroup.getMetricIdentifier("name"));
        registry.shutdown().get();
    }

    @Test
    public void testGenerateScopeWilcard() throws Exception {
        Configuration cfg = new Configuration();
        cfg.setString(SCOPE_NAMING_TASK, "*.<task_attempt_id>.<subtask_index>");
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(cfg));
        AbstractID executionId = new AbstractID();
        TaskManagerMetricGroup tmGroup = new TaskManagerMetricGroup(registry, "theHostName", "test-tm-id");
        TaskManagerJobMetricGroup jmGroup = new TaskManagerJobMetricGroup(registry, tmGroup, new JobID(), "myJobName");
        TaskMetricGroup taskGroup = new TaskMetricGroup(registry, jmGroup, new JobVertexID(), executionId, "aTaskName", 13, 1);
        Assert.assertArrayEquals(new String[]{ "theHostName", "taskmanager", "test-tm-id", "myJobName", executionId.toString(), "13" }, taskGroup.getScopeComponents());
        Assert.assertEquals((("theHostName.taskmanager.test-tm-id.myJobName." + executionId) + ".13.name"), taskGroup.getMetricIdentifier("name"));
        registry.shutdown().get();
    }

    @Test
    public void testCreateQueryServiceMetricInfo() {
        JobID jid = new JobID();
        JobVertexID vid = new JobVertexID();
        AbstractID eid = new AbstractID();
        TaskManagerMetricGroup tm = new TaskManagerMetricGroup(registry, "host", "id");
        TaskManagerJobMetricGroup job = new TaskManagerJobMetricGroup(registry, tm, jid, "jobname");
        TaskMetricGroup task = new TaskMetricGroup(registry, job, vid, eid, "taskName", 4, 5);
        QueryScopeInfo.TaskQueryScopeInfo info = task.createQueryServiceMetricInfo(new DummyCharacterFilter());
        Assert.assertEquals("", info.scope);
        Assert.assertEquals(jid.toString(), info.jobID);
        Assert.assertEquals(vid.toString(), info.vertexID);
        Assert.assertEquals(4, info.subtaskIndex);
    }

    @Test
    public void testTaskMetricGroupCleanup() throws Exception {
        TaskMetricGroupTest.CountingMetricRegistry registry = new TaskMetricGroupTest.CountingMetricRegistry(new Configuration());
        TaskManagerMetricGroup taskManagerMetricGroup = new TaskManagerMetricGroup(registry, "localhost", "0");
        TaskManagerJobMetricGroup taskManagerJobMetricGroup = new TaskManagerJobMetricGroup(registry, taskManagerMetricGroup, new JobID(), "job");
        TaskMetricGroup taskMetricGroup = new TaskMetricGroup(registry, taskManagerJobMetricGroup, new JobVertexID(), new AbstractID(), "task", 0, 0);
        // the io metric should have registered predefined metrics
        Assert.assertTrue(((registry.getNumberRegisteredMetrics()) > 0));
        taskMetricGroup.close();
        // now all registered metrics should have been unregistered
        Assert.assertEquals(0, registry.getNumberRegisteredMetrics());
        shutdown().get();
    }

    @Test
    public void testOperatorNameTruncation() throws Exception {
        Configuration cfg = new Configuration();
        cfg.setString(SCOPE_NAMING_OPERATOR, SCOPE_OPERATOR_NAME);
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(cfg));
        TaskManagerMetricGroup tm = new TaskManagerMetricGroup(registry, "host", "id");
        TaskManagerJobMetricGroup job = new TaskManagerJobMetricGroup(registry, tm, new JobID(), "jobname");
        TaskMetricGroup taskMetricGroup = new TaskMetricGroup(registry, job, new JobVertexID(), new AbstractID(), "task", 0, 0);
        String originalName = new String(new char[100]).replace("\u0000", "-");
        OperatorMetricGroup operatorMetricGroup = taskMetricGroup.getOrAddOperator(originalName);
        String storedName = operatorMetricGroup.getScopeComponents()[0];
        Assert.assertEquals(METRICS_OPERATOR_NAME_MAX_LENGTH, storedName.length());
        Assert.assertEquals(originalName.substring(0, METRICS_OPERATOR_NAME_MAX_LENGTH), storedName);
        registry.shutdown().get();
    }

    private static class CountingMetricRegistry extends MetricRegistryImpl {
        private int counter = 0;

        CountingMetricRegistry(Configuration config) {
            super(MetricRegistryConfiguration.fromConfiguration(config));
        }

        @Override
        public void register(Metric metric, String metricName, AbstractMetricGroup group) {
            super.register(metric, metricName, group);
            (counter)++;
        }

        @Override
        public void unregister(Metric metric, String metricName, AbstractMetricGroup group) {
            super.unregister(metric, metricName, group);
            (counter)--;
        }

        int getNumberRegisteredMetrics() {
            return counter;
        }
    }
}

