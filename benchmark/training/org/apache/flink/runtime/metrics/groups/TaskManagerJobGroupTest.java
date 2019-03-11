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


import MetricOptions.SCOPE_NAMING_TM;
import MetricOptions.SCOPE_NAMING_TM_JOB;
import QueryScopeInfo.JobQueryScopeInfo;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.metrics.MetricRegistryConfiguration;
import org.apache.flink.runtime.metrics.MetricRegistryImpl;
import org.apache.flink.runtime.metrics.dump.QueryScopeInfo;
import org.apache.flink.runtime.metrics.util.DummyCharacterFilter;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link TaskManagerJobMetricGroup}.
 */
public class TaskManagerJobGroupTest extends TestLogger {
    private MetricRegistryImpl registry;

    @Test
    public void testGenerateScopeDefault() {
        TaskManagerMetricGroup tmGroup = new TaskManagerMetricGroup(registry, "theHostName", "test-tm-id");
        JobMetricGroup jmGroup = new TaskManagerJobMetricGroup(registry, tmGroup, new JobID(), "myJobName");
        Assert.assertArrayEquals(new String[]{ "theHostName", "taskmanager", "test-tm-id", "myJobName" }, jmGroup.getScopeComponents());
        Assert.assertEquals("theHostName.taskmanager.test-tm-id.myJobName.name", jmGroup.getMetricIdentifier("name"));
    }

    @Test
    public void testGenerateScopeCustom() throws Exception {
        Configuration cfg = new Configuration();
        cfg.setString(SCOPE_NAMING_TM, "abc");
        cfg.setString(SCOPE_NAMING_TM_JOB, "some-constant.<job_name>");
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(cfg));
        JobID jid = new JobID();
        TaskManagerMetricGroup tmGroup = new TaskManagerMetricGroup(registry, "theHostName", "test-tm-id");
        JobMetricGroup jmGroup = new TaskManagerJobMetricGroup(registry, tmGroup, jid, "myJobName");
        Assert.assertArrayEquals(new String[]{ "some-constant", "myJobName" }, jmGroup.getScopeComponents());
        Assert.assertEquals("some-constant.myJobName.name", jmGroup.getMetricIdentifier("name"));
        registry.shutdown().get();
    }

    @Test
    public void testGenerateScopeCustomWildcard() throws Exception {
        Configuration cfg = new Configuration();
        cfg.setString(SCOPE_NAMING_TM, "peter.<tm_id>");
        cfg.setString(SCOPE_NAMING_TM_JOB, "*.some-constant.<job_id>");
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(cfg));
        JobID jid = new JobID();
        TaskManagerMetricGroup tmGroup = new TaskManagerMetricGroup(registry, "theHostName", "test-tm-id");
        JobMetricGroup jmGroup = new TaskManagerJobMetricGroup(registry, tmGroup, jid, "myJobName");
        Assert.assertArrayEquals(new String[]{ "peter", "test-tm-id", "some-constant", jid.toString() }, jmGroup.getScopeComponents());
        Assert.assertEquals((("peter.test-tm-id.some-constant." + jid) + ".name"), jmGroup.getMetricIdentifier("name"));
        registry.shutdown().get();
    }

    @Test
    public void testCreateQueryServiceMetricInfo() {
        JobID jid = new JobID();
        TaskManagerMetricGroup tm = new TaskManagerMetricGroup(registry, "host", "id");
        TaskManagerJobMetricGroup job = new TaskManagerJobMetricGroup(registry, tm, jid, "jobname");
        QueryScopeInfo.JobQueryScopeInfo info = job.createQueryServiceMetricInfo(new DummyCharacterFilter());
        Assert.assertEquals("", info.scope);
        Assert.assertEquals(jid.toString(), info.jobID);
    }
}

