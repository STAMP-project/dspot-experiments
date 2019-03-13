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
import QueryScopeInfo.OperatorQueryScopeInfo;
import ScopeFormat.SCOPE_HOST;
import ScopeFormat.SCOPE_JOB_ID;
import ScopeFormat.SCOPE_JOB_NAME;
import ScopeFormat.SCOPE_OPERATOR_ID;
import ScopeFormat.SCOPE_OPERATOR_NAME;
import ScopeFormat.SCOPE_TASKMANAGER_ID;
import ScopeFormat.SCOPE_TASK_ATTEMPT_ID;
import ScopeFormat.SCOPE_TASK_ATTEMPT_NUM;
import ScopeFormat.SCOPE_TASK_NAME;
import ScopeFormat.SCOPE_TASK_SUBTASK_INDEX;
import ScopeFormat.SCOPE_TASK_VERTEX_ID;
import java.util.Map;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.metrics.MetricRegistryConfiguration;
import org.apache.flink.runtime.metrics.MetricRegistryImpl;
import org.apache.flink.runtime.metrics.dump.QueryScopeInfo;
import org.apache.flink.runtime.metrics.util.DummyCharacterFilter;
import org.apache.flink.util.AbstractID;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link OperatorMetricGroup}.
 */
public class OperatorGroupTest extends TestLogger {
    private MetricRegistryImpl registry;

    @Test
    public void testGenerateScopeDefault() throws Exception {
        TaskManagerMetricGroup tmGroup = new TaskManagerMetricGroup(registry, "theHostName", "test-tm-id");
        TaskManagerJobMetricGroup jmGroup = new TaskManagerJobMetricGroup(registry, tmGroup, new JobID(), "myJobName");
        TaskMetricGroup taskGroup = new TaskMetricGroup(registry, jmGroup, new JobVertexID(), new AbstractID(), "aTaskName", 11, 0);
        OperatorMetricGroup opGroup = new OperatorMetricGroup(registry, taskGroup, new OperatorID(), "myOpName");
        Assert.assertArrayEquals(new String[]{ "theHostName", "taskmanager", "test-tm-id", "myJobName", "myOpName", "11" }, opGroup.getScopeComponents());
        Assert.assertEquals("theHostName.taskmanager.test-tm-id.myJobName.myOpName.11.name", opGroup.getMetricIdentifier("name"));
    }

    @Test
    public void testGenerateScopeCustom() throws Exception {
        Configuration cfg = new Configuration();
        cfg.setString(SCOPE_NAMING_OPERATOR, "<tm_id>.<job_id>.<task_id>.<operator_name>.<operator_id>");
        MetricRegistryImpl registry = new MetricRegistryImpl(MetricRegistryConfiguration.fromConfiguration(cfg));
        try {
            String tmID = "test-tm-id";
            JobID jid = new JobID();
            JobVertexID vertexId = new JobVertexID();
            OperatorID operatorID = new OperatorID();
            String operatorName = "operatorName";
            OperatorMetricGroup operatorGroup = new TaskManagerMetricGroup(registry, "theHostName", tmID).addTaskForJob(jid, "myJobName", vertexId, new ExecutionAttemptID(), "aTaskname", 13, 2).getOrAddOperator(operatorID, operatorName);
            Assert.assertArrayEquals(new String[]{ tmID, jid.toString(), vertexId.toString(), operatorName, operatorID.toString() }, operatorGroup.getScopeComponents());
            Assert.assertEquals(String.format("%s.%s.%s.%s.%s.name", tmID, jid, vertexId, operatorName, operatorID), operatorGroup.getMetricIdentifier("name"));
        } finally {
            registry.shutdown().get();
        }
    }

    @Test
    public void testIOMetricGroupInstantiation() throws Exception {
        TaskManagerMetricGroup tmGroup = new TaskManagerMetricGroup(registry, "theHostName", "test-tm-id");
        TaskManagerJobMetricGroup jmGroup = new TaskManagerJobMetricGroup(registry, tmGroup, new JobID(), "myJobName");
        TaskMetricGroup taskGroup = new TaskMetricGroup(registry, jmGroup, new JobVertexID(), new AbstractID(), "aTaskName", 11, 0);
        OperatorMetricGroup opGroup = new OperatorMetricGroup(registry, taskGroup, new OperatorID(), "myOpName");
        Assert.assertNotNull(opGroup.getIOMetricGroup());
        Assert.assertNotNull(opGroup.getIOMetricGroup().getNumRecordsInCounter());
        Assert.assertNotNull(opGroup.getIOMetricGroup().getNumRecordsOutCounter());
    }

    @Test
    public void testVariables() {
        JobID jid = new JobID();
        JobVertexID tid = new JobVertexID();
        AbstractID eid = new AbstractID();
        OperatorID oid = new OperatorID();
        TaskManagerMetricGroup tmGroup = new TaskManagerMetricGroup(registry, "theHostName", "test-tm-id");
        TaskManagerJobMetricGroup jmGroup = new TaskManagerJobMetricGroup(registry, tmGroup, jid, "myJobName");
        TaskMetricGroup taskGroup = new TaskMetricGroup(registry, jmGroup, tid, eid, "aTaskName", 11, 0);
        OperatorMetricGroup opGroup = new OperatorMetricGroup(registry, taskGroup, oid, "myOpName");
        Map<String, String> variables = opGroup.getAllVariables();
        OperatorGroupTest.testVariable(variables, SCOPE_HOST, "theHostName");
        OperatorGroupTest.testVariable(variables, SCOPE_TASKMANAGER_ID, "test-tm-id");
        OperatorGroupTest.testVariable(variables, SCOPE_JOB_ID, jid.toString());
        OperatorGroupTest.testVariable(variables, SCOPE_JOB_NAME, "myJobName");
        OperatorGroupTest.testVariable(variables, SCOPE_TASK_VERTEX_ID, tid.toString());
        OperatorGroupTest.testVariable(variables, SCOPE_TASK_NAME, "aTaskName");
        OperatorGroupTest.testVariable(variables, SCOPE_TASK_ATTEMPT_ID, eid.toString());
        OperatorGroupTest.testVariable(variables, SCOPE_TASK_SUBTASK_INDEX, "11");
        OperatorGroupTest.testVariable(variables, SCOPE_TASK_ATTEMPT_NUM, "0");
        OperatorGroupTest.testVariable(variables, SCOPE_OPERATOR_ID, oid.toString());
        OperatorGroupTest.testVariable(variables, SCOPE_OPERATOR_NAME, "myOpName");
    }

    @Test
    public void testCreateQueryServiceMetricInfo() {
        JobID jid = new JobID();
        JobVertexID vid = new JobVertexID();
        AbstractID eid = new AbstractID();
        OperatorID oid = new OperatorID();
        TaskManagerMetricGroup tm = new TaskManagerMetricGroup(registry, "host", "id");
        TaskManagerJobMetricGroup job = new TaskManagerJobMetricGroup(registry, tm, jid, "jobname");
        TaskMetricGroup task = new TaskMetricGroup(registry, job, vid, eid, "taskName", 4, 5);
        OperatorMetricGroup operator = new OperatorMetricGroup(registry, task, oid, "operator");
        QueryScopeInfo.OperatorQueryScopeInfo info = operator.createQueryServiceMetricInfo(new DummyCharacterFilter());
        Assert.assertEquals("", info.scope);
        Assert.assertEquals(jid.toString(), info.jobID);
        Assert.assertEquals(vid.toString(), info.vertexID);
        Assert.assertEquals(4, info.subtaskIndex);
        Assert.assertEquals("operator", info.operatorName);
    }
}

