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
package org.flowable.engine.test.db;


import EngineConfigurationConstants.KEY_JOB_SERVICE_CONFIG;
import java.util.Calendar;
import java.util.List;
import org.flowable.common.engine.impl.Page;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.interceptor.CommandExecutor;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.job.api.Job;
import org.flowable.job.service.JobServiceConfiguration;
import org.flowable.job.service.impl.persistence.entity.TimerJobEntity;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Daniel Meyer
 */
public class ProcessInstanceSuspensionTest extends PluggableFlowableTestCase {
    @Test
    @Deployment(resources = { "org/flowable/engine/test/db/oneJobProcess.bpmn20.xml" })
    public void testJobsNotVisibleToAcquisitionIfInstanceSuspended() {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().singleResult();
        ProcessInstance pi = runtimeService.startProcessInstanceByKey(pd.getKey());
        // now there is one job:
        Job job = managementService.createTimerJobQuery().singleResult();
        assertNotNull(job);
        makeSureJobDue(job);
        // the acquirejobs command sees the job:
        List<TimerJobEntity> acquiredJobs = executeAcquireJobsCommand();
        assertEquals(1, acquiredJobs.size());
        // suspend the process instance:
        runtimeService.suspendProcessInstanceById(pi.getId());
        // now, the acquirejobs command does not see the job:
        acquiredJobs = executeAcquireJobsCommand();
        assertEquals(0, acquiredJobs.size());
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/db/oneJobProcess.bpmn20.xml" })
    public void testJobsNotVisibleToAcquisitionIfDefinitionSuspended() {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().singleResult();
        runtimeService.startProcessInstanceByKey(pd.getKey());
        // now there is one job:
        Job job = managementService.createTimerJobQuery().singleResult();
        assertNotNull(job);
        makeSureJobDue(job);
        // the acquire jobs command sees the job:
        List<TimerJobEntity> acquiredJobs = executeAcquireJobsCommand();
        assertEquals(1, acquiredJobs.size());
        // suspend the process instance:
        repositoryService.suspendProcessDefinitionById(pd.getId(), true, null);
        // now, the acquire jobs command does not see the job:
        acquiredJobs = executeAcquireJobsCommand();
        assertEquals(0, acquiredJobs.size());
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/db/oneJobProcess.bpmn20.xml" })
    public void testJobsVisibleToAcquisitionIfDefinitionSuspendedWithoutProcessInstances() {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().singleResult();
        runtimeService.startProcessInstanceByKey(pd.getKey());
        // now there is one job:
        Job job = managementService.createTimerJobQuery().singleResult();
        assertNotNull(job);
        makeSureJobDue(job);
        // the acquire jobs command sees the job:
        List<TimerJobEntity> acquiredJobs = executeAcquireJobsCommand();
        assertEquals(1, acquiredJobs.size());
        // suspend the process instance:
        repositoryService.suspendProcessDefinitionById(pd.getId());
        // the acquire jobs command still sees the job, because the process instances are not suspended:
        acquiredJobs = executeAcquireJobsCommand();
        assertEquals(1, acquiredJobs.size());
    }

    @Test
    @Deployment
    public void testSuspendedProcessTimerExecution() throws Exception {
        // Process with boundary timer-event that fires in 1 hour
        ProcessInstance procInst = runtimeService.startProcessInstanceByKey("suspendProcess");
        assertNotNull(procInst);
        assertEquals(1, managementService.createTimerJobQuery().processInstanceId(procInst.getId()).count());
        // Roll time ahead to be sure timer is due to fire
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        processEngineConfiguration.getClock().setCurrentTime(tomorrow.getTime());
        // Check if timer is eligible to be executed, when process in not yet suspended
        final JobServiceConfiguration jobServiceConfiguration = ((JobServiceConfiguration) (processEngineConfiguration.getServiceConfigurations().get(KEY_JOB_SERVICE_CONFIG)));
        CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutor();
        List<TimerJobEntity> jobs = commandExecutor.execute(new org.flowable.common.engine.impl.interceptor.Command<List<TimerJobEntity>>() {
            @Override
            public List<TimerJobEntity> execute(CommandContext commandContext) {
                return jobServiceConfiguration.getTimerJobEntityManager().findTimerJobsToExecute(new Page(0, 1));
            }
        });
        assertEquals(1, jobs.size());
        // Suspend process instance
        runtimeService.suspendProcessInstanceById(procInst.getId());
        // Check if the timer is NOT acquired, even though the duedate is reached
        jobs = commandExecutor.execute(new org.flowable.common.engine.impl.interceptor.Command<List<TimerJobEntity>>() {
            @Override
            public List<TimerJobEntity> execute(CommandContext commandContext) {
                return jobServiceConfiguration.getTimerJobEntityManager().findTimerJobsToExecute(new Page(0, 1));
            }
        });
        assertEquals(0, jobs.size());
    }
}

