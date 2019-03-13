/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.engine.test.cfg.executioncount;


import java.util.Map;
import org.flowable.common.engine.impl.db.DbSqlSessionFactory;
import org.flowable.common.engine.impl.history.HistoryLevel;
import org.flowable.common.engine.impl.interceptor.CommandInterceptor;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.job.api.Job;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class VerifyDatabaseOperationsTest extends PluggableFlowableTestCase {
    protected boolean oldIsBulkInsertableValue;

    protected boolean oldExecutionTreeFetchValue;

    protected boolean oldExecutionRelationshipCountValue;

    protected boolean oldTaskRelationshipCountValue;

    protected boolean oldenableProcessDefinitionInfoCacheValue;

    protected CommandInterceptor oldFirstCommandInterceptor;

    protected DbSqlSessionFactory oldDbSqlSessionFactory;

    protected HistoryLevel oldHistoryLevel;

    @Test
    public void testStartToEnd() {
        if (!(processEngineConfiguration.isAsyncHistoryEnabled())) {
            deployStartProcessInstanceAndProfile("process01.bpmn20.xml", "process01");
            assertDatabaseSelects("StartProcessInstanceCmd", "selectLatestProcessDefinitionByKey", 1L, "selectEntityLinksByScopeIdAndType", 1L);
            assertDatabaseInserts("StartProcessInstanceCmd", "HistoricActivityInstanceEntityImpl-bulk-with-3", 1L, "HistoricProcessInstanceEntityImpl", 1L);
            assertNoUpdatesAndDeletes("StartProcessInstanceCmd");
            Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().count());
            Assert.assertEquals(1, historyService.createHistoricProcessInstanceQuery().finished().count());
        }
    }

    @Test
    public void testVariablesAndPassthrough() {
        if (!(processEngineConfiguration.isAsyncHistoryEnabled())) {
            deployStartProcessInstanceAndProfile("process-variables-servicetask01.bpmn20.xml", "process-variables-servicetask01");
            assertDatabaseSelects("StartProcessInstanceCmd", "selectLatestProcessDefinitionByKey", 1L, "selectEntityLinksByScopeIdAndType", 1L);
            assertDatabaseInserts("StartProcessInstanceCmd", "HistoricVariableInstanceEntityImpl-bulk-with-4", 1L, "HistoricProcessInstanceEntityImpl", 1L, "HistoricActivityInstanceEntityImpl-bulk-with-17", 1L);
            assertNoUpdatesAndDeletes("StartProcessInstanceCmd");
            Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().count());
            Assert.assertEquals(1, historyService.createHistoricProcessInstanceQuery().finished().count());
        }
    }

    @Test
    public void testManyVariablesViaServiceTaskAndPassthroughs() {
        if (!(processEngineConfiguration.isAsyncHistoryEnabled())) {
            deployStartProcessInstanceAndProfile("process-variables-servicetask02.bpmn20.xml", "process-variables-servicetask02");
            assertDatabaseSelects("StartProcessInstanceCmd", "selectLatestProcessDefinitionByKey", 1L, "selectEntityLinksByScopeIdAndType", 1L);
            assertDatabaseInserts("StartProcessInstanceCmd", "HistoricVariableInstanceEntityImpl-bulk-with-50", 1L, "HistoricProcessInstanceEntityImpl", 1L, "HistoricActivityInstanceEntityImpl-bulk-with-17", 1L);
            assertNoUpdatesAndDeletes("StartProcessInstanceCmd");
            Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().count());
            Assert.assertEquals(1, historyService.createHistoricProcessInstanceQuery().finished().count());
        }
    }

    @Test
    public void testOnlyPassThroughs() {
        if (!(processEngineConfiguration.isAsyncHistoryEnabled())) {
            deployStartProcessInstanceAndProfile("process02.bpmn20.xml", "process02");
            assertDatabaseSelects("StartProcessInstanceCmd", "selectLatestProcessDefinitionByKey", 1L, "selectEntityLinksByScopeIdAndType", 1L);
            assertDatabaseInserts("StartProcessInstanceCmd", "HistoricActivityInstanceEntityImpl-bulk-with-17", 1L, "HistoricProcessInstanceEntityImpl", 1L);
            assertNoUpdatesAndDeletes("StartProcessInstanceCmd");
            Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().count());
            Assert.assertEquals(1, historyService.createHistoricProcessInstanceQuery().finished().count());
        }
    }

    @Test
    public void testParallelForkAndJoin() {
        if (!(processEngineConfiguration.isAsyncHistoryEnabled())) {
            deployStartProcessInstanceAndProfile("process03.bpmn20.xml", "process03");
            assertDatabaseSelects("StartProcessInstanceCmd", "selectLatestProcessDefinitionByKey", 1L, "selectEntityLinksByScopeIdAndType", 1L);
            assertDatabaseInserts("StartProcessInstanceCmd", "HistoricActivityInstanceEntityImpl-bulk-with-13", 1L, "HistoricProcessInstanceEntityImpl", 1L);
            assertNoUpdatesAndDeletes("StartProcessInstanceCmd");
            Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().count());
            Assert.assertEquals(1, historyService.createHistoricProcessInstanceQuery().finished().count());
        }
    }

    @Test
    public void testNestedParallelForkAndJoin() {
        if (!(processEngineConfiguration.isAsyncHistoryEnabled())) {
            deployStartProcessInstanceAndProfile("process04.bpmn20.xml", "process04");
            assertDatabaseSelects("StartProcessInstanceCmd", "selectLatestProcessDefinitionByKey", 1L, "selectEntityLinksByScopeIdAndType", 1L);
            assertDatabaseInserts("StartProcessInstanceCmd", "HistoricActivityInstanceEntityImpl-bulk-with-41", 1L, "HistoricProcessInstanceEntityImpl", 1L);
            assertNoUpdatesAndDeletes("StartProcessInstanceCmd");
            Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().count());
            Assert.assertEquals(1, historyService.createHistoricProcessInstanceQuery().finished().count());
        }
    }

    @Test
    public void testExclusiveGateway() {
        if (!(processEngineConfiguration.isAsyncHistoryEnabled())) {
            deployStartProcessInstanceAndProfile("process05.bpmn20.xml", "process05");
            assertDatabaseSelects("StartProcessInstanceCmd", "selectLatestProcessDefinitionByKey", 1L, "selectEntityLinksByScopeIdAndType", 1L);
            assertDatabaseInserts("StartProcessInstanceCmd", "HistoricActivityInstanceEntityImpl-bulk-with-9", 1L, "HistoricProcessInstanceEntityImpl", 1L, "HistoricVariableInstanceEntityImpl", 1L);
            assertNoUpdatesAndDeletes("StartProcessInstanceCmd");
            Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().count());
            Assert.assertEquals(1, historyService.createHistoricProcessInstanceQuery().finished().count());
        }
    }

    @Test
    public void testAsyncJob() {
        if (!(processEngineConfiguration.isAsyncHistoryEnabled())) {
            deployStartProcessInstanceAndProfile("process06.bpmn20.xml", "process06", false);
            Job job = managementService.createJobQuery().singleResult();
            managementService.executeJob(job.getId());
            stopProfiling();
            assertDatabaseSelects("StartProcessInstanceCmd", "selectLatestProcessDefinitionByKey", 1L);
            assertDatabaseInserts("StartProcessInstanceCmd", "JobEntityImpl", 1L, "ExecutionEntityImpl-bulk-with-2", 1L, "ActivityInstanceEntityImpl-bulk-with-2", 1L, "HistoricActivityInstanceEntityImpl-bulk-with-2", 1L, "HistoricProcessInstanceEntityImpl", 1L);
            assertNoUpdatesAndDeletes("StartProcessInstanceCmd");
            assertDatabaseInserts("org.flowable.job.service.impl.cmd.ExecuteJobCmd", "HistoricActivityInstanceEntityImpl-bulk-with-3", 1L);
            assertDatabaseDeletes("org.flowable.job.service.impl.cmd.ExecuteJobCmd", "JobEntityImpl", 1L, "ExecutionEntityImpl", 2L, "Bulk-delete-deleteTasksByExecutionId", 1L, "Bulk-delete-deleteActivityInstancesByProcessInstanceId", 1L);
            Assert.assertEquals(0, runtimeService.createProcessInstanceQuery().count());
            Assert.assertEquals(1, historyService.createHistoricProcessInstanceQuery().finished().count());
        }
    }

    @Test
    public void testOneTaskProcess() {
        if (!(processEngineConfiguration.isAsyncHistoryEnabled())) {
            deployStartProcessInstanceAndProfile("process-usertask-01.bpmn20.xml", "process-usertask-01", false);
            Task task = taskService.createTaskQuery().singleResult();
            taskService.complete(task.getId());
            stopProfiling();
            assertExecutedCommands("StartProcessInstanceCmd", "org.flowable.task.service.impl.TaskQueryImpl", "CompleteTaskCmd");
            // Start process instance
            assertDatabaseSelects("StartProcessInstanceCmd", "selectLatestProcessDefinitionByKey", 1L, "selectEntityLinksByReferenceScopeIdAndType", 2L);
            assertDatabaseInserts("StartProcessInstanceCmd", "ExecutionEntityImpl-bulk-with-2", 1L, "TaskEntityImpl", 1L, "EntityLinkEntityImpl", 1L, "ActivityInstanceEntityImpl-bulk-with-3", 1L, "HistoricActivityInstanceEntityImpl-bulk-with-3", 1L, "HistoricTaskInstanceEntityImpl", 1L, "HistoricTaskLogEntryEntityImpl", 1L, "HistoricProcessInstanceEntityImpl", 1L, "HistoricEntityLinkEntityImpl", 1L);
            assertNoUpdatesAndDeletes("StartProcessInstanceCmd");
            // org.flowable.task.service.Task Query
            assertDatabaseSelects("org.flowable.task.service.impl.TaskQueryImpl", "selectTaskByQueryCriteria", 1L);
            assertNoInserts("org.flowable.task.service.impl.TaskQueryImpl");
            assertNoUpdates("org.flowable.task.service.impl.TaskQueryImpl");
            assertNoDeletes("org.flowable.task.service.impl.TaskQueryImpl");
            // org.flowable.task.service.Task Complete
            assertDatabaseSelects("CompleteTaskCmd", "selectById org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl", 1L, "selectById org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntityImpl", 1L, "selectById org.flowable.task.service.impl.persistence.entity.TaskEntityImpl", 1L, "selectById org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl", 1L, "selectById org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl", 1L, "selectUnfinishedActivityInstanceExecutionIdAndActivityId", 2L, "selectExecutionsWithSameRootProcessInstanceId", 1L, "selectTasksByExecutionId", 2L, "selectVariablesByExecutionId", 1L, "selectIdentityLinksByProcessInstance", 1L, "selectEntityLinksByScopeIdAndType", 1L, "selectEventSubscriptionsByExecution", 1L, "selectTimerJobsByExecutionId", 1L, "selectSuspendedJobsByExecutionId", 1L, "selectDeadLetterJobsByExecutionId", 1L, "selectJobsByExecutionId", 1L);
            assertDatabaseInserts("CompleteTaskCmd", "HistoricActivityInstanceEntityImpl-bulk-with-2", 1L, "HistoricTaskLogEntryEntityImpl", 1L);
            assertDatabaseUpdates("CompleteTaskCmd", "org.flowable.task.service.impl.persistence.entity.HistoricTaskInstanceEntityImpl", 1L, "org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl", 2L, "org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl", 1L, "org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl", 1L);
            assertDatabaseDeletes("CompleteTaskCmd", "TaskEntityImpl", 1L, "ExecutionEntityImpl", 2L, "Bulk-delete-deleteTasksByExecutionId", 1L, "Bulk-delete-deleteEntityLinksByScopeIdAndScopeType", 1L, "Bulk-delete-deleteActivityInstancesByProcessInstanceId", 1L);
        }
    }

    @Test
    public void testOneTaskWithBoundaryTimerProcess() {
        if (!(processEngineConfiguration.isAsyncHistoryEnabled())) {
            deployStartProcessInstanceAndProfile("process-usertask-02.bpmn20.xml", "process-usertask-02", false);
            Task task = taskService.createTaskQuery().singleResult();
            taskService.complete(task.getId());
            stopProfiling();
            assertExecutedCommands("StartProcessInstanceCmd", "org.flowable.task.service.impl.TaskQueryImpl", "CompleteTaskCmd");
            // Start process instance
            assertDatabaseSelects("StartProcessInstanceCmd", "selectLatestProcessDefinitionByKey", 1L, "selectEntityLinksByReferenceScopeIdAndType", 2L);
            assertDatabaseInserts("StartProcessInstanceCmd", "ExecutionEntityImpl-bulk-with-3", 1L, "TaskEntityImpl", 1L, "TimerJobEntityImpl", 1L, "EntityLinkEntityImpl", 1L, "ActivityInstanceEntityImpl-bulk-with-3", 1L, "HistoricActivityInstanceEntityImpl-bulk-with-3", 1L, "HistoricTaskInstanceEntityImpl", 1L, "HistoricTaskLogEntryEntityImpl", 1L, "HistoricProcessInstanceEntityImpl", 1L, "HistoricEntityLinkEntityImpl", 1L);
            assertNoUpdatesAndDeletes("StartProcessInstanceCmd");
            // org.flowable.task.service.Task Complete
            assertDatabaseDeletes("CompleteTaskCmd", "TaskEntityImpl", 1L, "TimerJobEntityImpl", 1L, "ExecutionEntityImpl", 3L, "Bulk-delete-deleteActivityInstancesByProcessInstanceId", 1L, "Bulk-delete-deleteTasksByExecutionId", 1L, "Bulk-delete-deleteEntityLinksByScopeIdAndScopeType", 1L);
        }
    }

    @Test
    public void testRemoveTaskVariables() {
        if (!(processEngineConfiguration.isAsyncHistoryEnabled())) {
            // TODO: move to separate class
            deployStartProcessInstanceAndProfile("process-usertask-01.bpmn20.xml", "process-usertask-01", false);
            Task task = taskService.createTaskQuery().singleResult();
            long variableCount = 3;
            Map<String, Object> vars = createVariables(variableCount, "local");
            taskService.setVariablesLocal(task.getId(), vars);
            vars.put("someRandomVariable", "someRandomValue");
            // remove existing variables
            taskService.removeVariablesLocal(task.getId(), vars.keySet());
            // try to remove when variable count is zero (nothing left to remove). DB should not be hit.
            taskService.removeVariablesLocal(task.getId(), vars.keySet());
            taskService.removeVariablesLocal(task.getId(), vars.keySet());
            taskService.complete(task.getId());
            stopProfiling();
            assertExecutedCommands("StartProcessInstanceCmd", "org.flowable.task.service.impl.TaskQueryImpl", "SetTaskVariablesCmd", "RemoveTaskVariablesCmd", "CompleteTaskCmd");
            assertDatabaseInserts("SetTaskVariablesCmd", "HistoricVariableInstanceEntityImpl-bulk-with-3", 1L, "VariableInstanceEntityImpl-bulk-with-3", 1L);
            // check that only "variableCount" number of delete statements have been executed
            assertDatabaseDeletes("RemoveTaskVariablesCmd", "VariableInstanceEntityImpl", variableCount, "HistoricVariableInstanceEntityImpl", variableCount);
        }
    }

    @Test
    public void testClaimTask() {
        if (!(processEngineConfiguration.isAsyncHistoryEnabled())) {
            // TODO: move to separate class
            deployStartProcessInstanceAndProfile("process-usertask-01.bpmn20.xml", "process-usertask-01", false);
            Task task = taskService.createTaskQuery().singleResult();
            taskService.claim(task.getId(), "firstUser");
            taskService.unclaim(task.getId());
            taskService.complete(task.getId());
            stopProfiling();
            assertExecutedCommands("StartProcessInstanceCmd", "org.flowable.task.service.impl.TaskQueryImpl", "ClaimTaskCmd", "CompleteTaskCmd");
            assertNoDeletes("ClaimTaskCmd");
            assertDatabaseInserts("ClaimTaskCmd", "HistoricTaskLogEntryEntityImpl", 2L, "CommentEntityImpl", 2L, "HistoricIdentityLinkEntityImpl-bulk-with-2", 1L, "IdentityLinkEntityImpl", 1L, "HistoricIdentityLinkEntityImpl", 1L);
        }
    }

    @Test
    public void testTaskCandidateUsers() {
        if (!(processEngineConfiguration.isAsyncHistoryEnabled())) {
            // TODO: move to separate class
            deployStartProcessInstanceAndProfile("process-usertask-01.bpmn20.xml", "process-usertask-01", false);
            Task task = taskService.createTaskQuery().singleResult();
            taskService.addCandidateUser(task.getId(), "user01");
            taskService.addCandidateUser(task.getId(), "user02");
            taskService.deleteCandidateUser(task.getId(), "user01");
            taskService.deleteCandidateUser(task.getId(), "user02");
            // Try to remove candidate users that are no (longer) part of the identity links for the task
            // Identity Link Count is zero. The DB should not be hit.
            taskService.deleteCandidateUser(task.getId(), "user02");
            taskService.deleteCandidateUser(task.getId(), "user03");
            taskService.deleteCandidateUser(task.getId(), "user04");
            taskService.complete(task.getId());
            stopProfiling();
            assertExecutedCommands("StartProcessInstanceCmd", "org.flowable.task.service.impl.TaskQueryImpl", "AddIdentityLinkCmd", "DeleteIdentityLinkCmd", "CompleteTaskCmd");
            // Check "AddIdentityLinkCmd" (2 invocations)
            assertNoDeletes("AddIdentityLinkCmd");
            assertDatabaseInserts("AddIdentityLinkCmd", "HistoricTaskLogEntryEntityImpl", 2L, "CommentEntityImpl", 2L, "HistoricIdentityLinkEntityImpl-bulk-with-2", 2L, "IdentityLinkEntityImpl-bulk-with-2", 2L);
            assertDatabaseSelects("AddIdentityLinkCmd", "selectById org.flowable.task.service.impl.persistence.entity.TaskEntityImpl", 2L, "selectById org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl", 2L, "selectIdentityLinksByTaskId", 2L, "selectExecutionsWithSameRootProcessInstanceId", 2L, "selectIdentityLinksByProcessInstance", 2L);
            assertDatabaseUpdates("AddIdentityLinkCmd", "org.flowable.task.service.impl.persistence.entity.TaskEntityImpl", 2L);
            // Check "DeleteIdentityLinkCmd"
            // not sure if the HistoricIdentityLinkEntityImpl should be deleted
            assertDatabaseDeletes("DeleteIdentityLinkCmd", "IdentityLinkEntityImpl", 2L, "HistoricIdentityLinkEntityImpl", 2L);
            assertDatabaseInserts("DeleteIdentityLinkCmd", "CommentEntityImpl", 5L, "HistoricTaskLogEntryEntityImpl", 2L);
            assertDatabaseSelects("DeleteIdentityLinkCmd", "selectById org.flowable.task.service.impl.persistence.entity.TaskEntityImpl", 5L, "selectIdentityLinkByTaskUserGroupAndType", 5L, "selectById org.flowable.identitylink.service.impl.persistence.entity.HistoricIdentityLinkEntityImpl", 2L, "selectIdentityLinksByTaskId", 5L);
            assertDatabaseUpdates("DeleteIdentityLinkCmd", "org.flowable.task.service.impl.persistence.entity.TaskEntityImpl", 2L);
        }
    }

    @Test
    public void testTaskCandidateGroups() {
        if (!(processEngineConfiguration.isAsyncHistoryEnabled())) {
            // TODO: move to separate class
            deployStartProcessInstanceAndProfile("process-usertask-01.bpmn20.xml", "process-usertask-01", false);
            Task task = taskService.createTaskQuery().singleResult();
            taskService.addCandidateGroup(task.getId(), "group01");
            taskService.addCandidateGroup(task.getId(), "group02");
            taskService.deleteCandidateGroup(task.getId(), "group01");
            taskService.deleteCandidateGroup(task.getId(), "group02");
            // Try to remove candidate Groups that are no (longer) part of the identity links for the task
            // Identity Link Count is zero. The DB should not be hit.
            taskService.deleteCandidateGroup(task.getId(), "group02");
            taskService.deleteCandidateGroup(task.getId(), "group03");
            taskService.deleteCandidateGroup(task.getId(), "group04");
            taskService.complete(task.getId());
            stopProfiling();
            assertExecutedCommands("StartProcessInstanceCmd", "org.flowable.task.service.impl.TaskQueryImpl", "AddIdentityLinkCmd", "DeleteIdentityLinkCmd", "CompleteTaskCmd");
            // Check "AddIdentityLinkCmd"
            assertNoDeletes("AddIdentityLinkCmd");
            assertDatabaseInserts("AddIdentityLinkCmd", "CommentEntityImpl", 2L, "HistoricTaskLogEntryEntityImpl", 2L, "IdentityLinkEntityImpl", 2L, "HistoricIdentityLinkEntityImpl", 2L);
            assertDatabaseSelects("AddIdentityLinkCmd", "selectById org.flowable.task.service.impl.persistence.entity.TaskEntityImpl", 2L, "selectIdentityLinksByTaskId", 2L);
            assertDatabaseUpdates("AddIdentityLinkCmd", "org.flowable.task.service.impl.persistence.entity.TaskEntityImpl", 2L);
            // Check "DeleteIdentityLinkCmd"
            // not sure if the HistoricIdentityLinkEntityImpl should be deleted
            assertDatabaseDeletes("DeleteIdentityLinkCmd", "IdentityLinkEntityImpl", 2L, "HistoricIdentityLinkEntityImpl", 2L);
            assertDatabaseInserts("DeleteIdentityLinkCmd", "CommentEntityImpl", 5L, "HistoricTaskLogEntryEntityImpl", 2L);
            assertDatabaseSelects("DeleteIdentityLinkCmd", "selectById org.flowable.task.service.impl.persistence.entity.TaskEntityImpl", 5L, "selectIdentityLinkByTaskUserGroupAndType", 5L, "selectById org.flowable.identitylink.service.impl.persistence.entity.HistoricIdentityLinkEntityImpl", 2L, "selectIdentityLinksByTaskId", 5L);
            assertDatabaseUpdates("DeleteIdentityLinkCmd", "org.flowable.task.service.impl.persistence.entity.TaskEntityImpl", 2L);
        }
    }
}

