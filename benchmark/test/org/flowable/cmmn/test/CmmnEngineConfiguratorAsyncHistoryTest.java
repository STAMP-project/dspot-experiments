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
package org.flowable.cmmn.test;


import CmmnAsyncHistoryConstants.JOB_HANDLER_TYPE_DEFAULT_ASYNC_HISTORY;
import CmmnAsyncHistoryConstants.JOB_HANDLER_TYPE_DEFAULT_ASYNC_HISTORY_ZIPPED;
import JobServiceConfiguration.JOB_EXECUTION_SCOPE_ALL;
import PlanItemInstanceState.ACTIVE;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.engine.test.impl.CmmnJobTestHelper;
import org.flowable.engine.ProcessEngine;
import org.flowable.job.api.HistoryJob;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class CmmnEngineConfiguratorAsyncHistoryTest {
    private ProcessEngine processEngine;

    private CmmnEngine cmmnEngine;

    @Test
    public void testSharedAsyncHistoryExecutor() {
        // The async history executor should be the same instance
        AsyncExecutor processEngineAsyncExecutor = processEngine.getProcessEngineConfiguration().getAsyncHistoryExecutor();
        AsyncExecutor cmmnEngineAsyncExecutor = cmmnEngine.getCmmnEngineConfiguration().getAsyncHistoryExecutor();
        Assert.assertNotNull(processEngineAsyncExecutor);
        Assert.assertNotNull(cmmnEngineAsyncExecutor);
        Assert.assertSame(processEngineAsyncExecutor, cmmnEngineAsyncExecutor);
        // Running them together should have moved the job execution scope to 'all' (from process which is null)
        Assert.assertEquals(JOB_EXECUTION_SCOPE_ALL, processEngine.getProcessEngineConfiguration().getAsyncHistoryExecutor().getJobServiceConfiguration().getHistoryJobExecutionScope());
        // 2 job handlers / engine
        Assert.assertEquals(4, processEngineAsyncExecutor.getJobServiceConfiguration().getHistoryJobHandlers().size());
        // Deploy and start test processes/cases
        // Trigger one plan item instance to start the process
        processEngine.getRepositoryService().createDeployment().addClasspathResource("org/flowable/cmmn/test/oneTaskProcess.bpmn20.xml").deploy();
        cmmnEngine.getCmmnRepositoryService().createDeployment().addClasspathResource("org/flowable/cmmn/test/ProcessTaskTest.testOneTaskProcessNonBlocking.cmmn").deploy();
        cmmnEngine.getCmmnRuntimeService().createCaseInstanceBuilder().caseDefinitionKey("myCase").start();
        cmmnEngine.getCmmnRuntimeService().triggerPlanItemInstance(cmmnEngine.getCmmnRuntimeService().createPlanItemInstanceQuery().planItemInstanceState(ACTIVE).singleResult().getId());
        // As async history is enabled, there should be  no historical entries yet, but there should be history jobs
        Assert.assertEquals(0, cmmnEngine.getCmmnHistoryService().createHistoricCaseInstanceQuery().count());
        Assert.assertEquals(0, processEngine.getHistoryService().createHistoricProcessInstanceQuery().count());
        // 3 history jobs expected:
        // - one for the case instance start
        // - one for the plan item instance trigger
        // - one for the process instance start
        Assert.assertEquals(3, cmmnEngine.getCmmnManagementService().createHistoryJobQuery().count());
        Assert.assertEquals(3, processEngine.getManagementService().createHistoryJobQuery().count());
        // Expected 2 jobs originating from the cmmn engine and 1 for the process engine
        int cmmnHistoryJobs = 0;
        int bpmnHistoryJobs = 0;
        for (HistoryJob historyJob : cmmnEngine.getCmmnManagementService().createHistoryJobQuery().list()) {
            if ((historyJob.getJobHandlerType().equals(JOB_HANDLER_TYPE_DEFAULT_ASYNC_HISTORY)) || (historyJob.getJobHandlerType().equals(JOB_HANDLER_TYPE_DEFAULT_ASYNC_HISTORY_ZIPPED))) {
                cmmnHistoryJobs++;
            } else
                if ((historyJob.getJobHandlerType().equals(HistoryJsonConstants.JOB_HANDLER_TYPE_DEFAULT_ASYNC_HISTORY)) || (historyJob.getJobHandlerType().equals(HistoryJsonConstants.JOB_HANDLER_TYPE_DEFAULT_ASYNC_HISTORY_ZIPPED))) {
                    bpmnHistoryJobs++;
                }

            // Execution scope should be all (see the CmmnEngineConfigurator)
            Assert.assertEquals(JOB_EXECUTION_SCOPE_ALL, historyJob.getScopeType());
        }
        Assert.assertEquals(1, bpmnHistoryJobs);
        Assert.assertEquals(2, cmmnHistoryJobs);
        // Starting the async history executor should process all of these
        CmmnJobTestHelper.waitForAsyncHistoryExecutorToProcessAllJobs(cmmnEngine.getCmmnEngineConfiguration(), 10000L, 200L, true);
        Assert.assertEquals(1, cmmnEngine.getCmmnHistoryService().createHistoricCaseInstanceQuery().count());
        Assert.assertEquals(1, processEngine.getHistoryService().createHistoricProcessInstanceQuery().count());
        Assert.assertEquals(0, cmmnEngine.getCmmnManagementService().createHistoryJobQuery().count());
        Assert.assertEquals(0, processEngine.getManagementService().createHistoryJobQuery().count());
    }

    @Test
    public void testProcessAsyncHistoryNotChanged() {
        // This test validates that the shared async history executor does not intervene when running a process regularly
        processEngine.getRepositoryService().createDeployment().addClasspathResource("org/flowable/cmmn/test/oneTaskProcess.bpmn20.xml").deploy();
        processEngine.getRuntimeService().startProcessInstanceByKey("oneTask");
        Assert.assertEquals(1, processEngine.getManagementService().createHistoryJobQuery().count());
        HistoryJob historyJob = processEngine.getManagementService().createHistoryJobQuery().singleResult();
        Assert.assertEquals(HistoryJsonConstants.JOB_HANDLER_TYPE_DEFAULT_ASYNC_HISTORY, historyJob.getJobHandlerType());
    }
}

