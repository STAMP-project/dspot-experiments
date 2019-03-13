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
package org.apache.flink.runtime.metrics.dump;


import QueryScopeInfo.INFO_CATEGORY_JM;
import QueryScopeInfo.INFO_CATEGORY_JOB;
import QueryScopeInfo.INFO_CATEGORY_OPERATOR;
import QueryScopeInfo.INFO_CATEGORY_TASK;
import QueryScopeInfo.INFO_CATEGORY_TM;
import QueryScopeInfo.JobManagerQueryScopeInfo;
import QueryScopeInfo.JobQueryScopeInfo;
import QueryScopeInfo.OperatorQueryScopeInfo;
import QueryScopeInfo.TaskManagerQueryScopeInfo;
import QueryScopeInfo.TaskQueryScopeInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link QueryScopeInfo} classes.
 */
public class QueryScopeInfoTest {
    @Test
    public void testJobManagerQueryScopeInfo() {
        QueryScopeInfo.JobManagerQueryScopeInfo info = new QueryScopeInfo.JobManagerQueryScopeInfo();
        Assert.assertEquals(INFO_CATEGORY_JM, info.getCategory());
        Assert.assertEquals("", info.scope);
        info = info.copy("world");
        Assert.assertEquals(INFO_CATEGORY_JM, info.getCategory());
        Assert.assertEquals("world", info.scope);
        info = new QueryScopeInfo.JobManagerQueryScopeInfo("hello");
        Assert.assertEquals(INFO_CATEGORY_JM, info.getCategory());
        Assert.assertEquals("hello", info.scope);
        info = info.copy("world");
        Assert.assertEquals(INFO_CATEGORY_JM, info.getCategory());
        Assert.assertEquals("hello.world", info.scope);
    }

    @Test
    public void testTaskManagerQueryScopeInfo() {
        QueryScopeInfo.TaskManagerQueryScopeInfo info = new QueryScopeInfo.TaskManagerQueryScopeInfo("tmid");
        Assert.assertEquals(INFO_CATEGORY_TM, info.getCategory());
        Assert.assertEquals("", info.scope);
        Assert.assertEquals("tmid", info.taskManagerID);
        info = info.copy("world");
        Assert.assertEquals(INFO_CATEGORY_TM, info.getCategory());
        Assert.assertEquals("world", info.scope);
        Assert.assertEquals("tmid", info.taskManagerID);
        info = new QueryScopeInfo.TaskManagerQueryScopeInfo("tmid", "hello");
        Assert.assertEquals(INFO_CATEGORY_TM, info.getCategory());
        Assert.assertEquals("hello", info.scope);
        Assert.assertEquals("tmid", info.taskManagerID);
        info = info.copy("world");
        Assert.assertEquals(INFO_CATEGORY_TM, info.getCategory());
        Assert.assertEquals("hello.world", info.scope);
        Assert.assertEquals("tmid", info.taskManagerID);
    }

    @Test
    public void testJobQueryScopeInfo() {
        QueryScopeInfo.JobQueryScopeInfo info = new QueryScopeInfo.JobQueryScopeInfo("jobid");
        Assert.assertEquals(INFO_CATEGORY_JOB, info.getCategory());
        Assert.assertEquals("", info.scope);
        Assert.assertEquals("jobid", info.jobID);
        info = info.copy("world");
        Assert.assertEquals(INFO_CATEGORY_JOB, info.getCategory());
        Assert.assertEquals("world", info.scope);
        Assert.assertEquals("jobid", info.jobID);
        info = new QueryScopeInfo.JobQueryScopeInfo("jobid", "hello");
        Assert.assertEquals(INFO_CATEGORY_JOB, info.getCategory());
        Assert.assertEquals("hello", info.scope);
        Assert.assertEquals("jobid", info.jobID);
        info = info.copy("world");
        Assert.assertEquals(INFO_CATEGORY_JOB, info.getCategory());
        Assert.assertEquals("hello.world", info.scope);
        Assert.assertEquals("jobid", info.jobID);
    }

    @Test
    public void testTaskQueryScopeInfo() {
        QueryScopeInfo.TaskQueryScopeInfo info = new QueryScopeInfo.TaskQueryScopeInfo("jobid", "taskid", 2);
        Assert.assertEquals(INFO_CATEGORY_TASK, info.getCategory());
        Assert.assertEquals("", info.scope);
        Assert.assertEquals("jobid", info.jobID);
        Assert.assertEquals("taskid", info.vertexID);
        Assert.assertEquals(2, info.subtaskIndex);
        info = info.copy("world");
        Assert.assertEquals(INFO_CATEGORY_TASK, info.getCategory());
        Assert.assertEquals("world", info.scope);
        Assert.assertEquals("jobid", info.jobID);
        Assert.assertEquals("taskid", info.vertexID);
        Assert.assertEquals(2, info.subtaskIndex);
        info = new QueryScopeInfo.TaskQueryScopeInfo("jobid", "taskid", 2, "hello");
        Assert.assertEquals(INFO_CATEGORY_TASK, info.getCategory());
        Assert.assertEquals("hello", info.scope);
        Assert.assertEquals("jobid", info.jobID);
        Assert.assertEquals("taskid", info.vertexID);
        Assert.assertEquals(2, info.subtaskIndex);
        info = info.copy("world");
        Assert.assertEquals(INFO_CATEGORY_TASK, info.getCategory());
        Assert.assertEquals("hello.world", info.scope);
        Assert.assertEquals("jobid", info.jobID);
        Assert.assertEquals("taskid", info.vertexID);
        Assert.assertEquals(2, info.subtaskIndex);
    }

    @Test
    public void testOperatorQueryScopeInfo() {
        QueryScopeInfo.OperatorQueryScopeInfo info = new QueryScopeInfo.OperatorQueryScopeInfo("jobid", "taskid", 2, "opname");
        Assert.assertEquals(INFO_CATEGORY_OPERATOR, info.getCategory());
        Assert.assertEquals("", info.scope);
        Assert.assertEquals("jobid", info.jobID);
        Assert.assertEquals("taskid", info.vertexID);
        Assert.assertEquals("opname", info.operatorName);
        Assert.assertEquals(2, info.subtaskIndex);
        info = info.copy("world");
        Assert.assertEquals(INFO_CATEGORY_OPERATOR, info.getCategory());
        Assert.assertEquals("world", info.scope);
        Assert.assertEquals("jobid", info.jobID);
        Assert.assertEquals("taskid", info.vertexID);
        Assert.assertEquals("opname", info.operatorName);
        Assert.assertEquals(2, info.subtaskIndex);
        info = new QueryScopeInfo.OperatorQueryScopeInfo("jobid", "taskid", 2, "opname", "hello");
        Assert.assertEquals(INFO_CATEGORY_OPERATOR, info.getCategory());
        Assert.assertEquals("hello", info.scope);
        Assert.assertEquals("jobid", info.jobID);
        Assert.assertEquals("taskid", info.vertexID);
        Assert.assertEquals("opname", info.operatorName);
        Assert.assertEquals(2, info.subtaskIndex);
        info = info.copy("world");
        Assert.assertEquals(INFO_CATEGORY_OPERATOR, info.getCategory());
        Assert.assertEquals("hello.world", info.scope);
        Assert.assertEquals("jobid", info.jobID);
        Assert.assertEquals("taskid", info.vertexID);
        Assert.assertEquals("opname", info.operatorName);
        Assert.assertEquals(2, info.subtaskIndex);
    }
}

