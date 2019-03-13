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


import java.util.Calendar;
import java.util.List;
import org.flowable.cmmn.engine.test.impl.CmmnJobTestHelper;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.runtime.Clock;
import org.flowable.engine.impl.test.JobTestHelper;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.job.api.Job;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tijs Rademakers
 */
public class BpmnTimerTaskTest extends AbstractProcessEngineIntegrationTest {
    @Test
    public void testBpmnTimerTask() {
        ProcessInstance processInstance = processEngineRuntimeService.startProcessInstanceByKey("timerProcess");
        List<Task> processTasks = processEngineTaskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        Assert.assertEquals(1, processTasks.size());
        List<Job> timerJobs = processEngineManagementService.createTimerJobQuery().processInstanceId(processInstance.getId()).list();
        Assert.assertEquals(1, timerJobs.size());
        timerJobs = cmmnManagementService.createTimerJobQuery().processInstanceId(processInstance.getId()).executable().list();
        Assert.assertEquals(0, timerJobs.size());
        Clock clock = AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration.getClock();
        Calendar currentCalendar = clock.getCurrentCalendar();
        currentCalendar.add(Calendar.MINUTE, 15);
        clock.setCurrentCalendar(currentCalendar);
        timerJobs = cmmnManagementService.createTimerJobQuery().processInstanceId(processInstance.getId()).executable().list();
        Assert.assertEquals(1, timerJobs.size());
        String timerJobId = timerJobs.get(0).getId();
        try {
            CmmnJobTestHelper.waitForJobExecutorToProcessAllJobs(AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration, 7000, 200, true);
            Assert.fail("should throw time limit exceeded");
        } catch (FlowableException e) {
            Assert.assertEquals("Time limit of 7000 was exceeded", e.getMessage());
        }
        AbstractProcessEngineIntegrationTest.processEngine.getProcessEngineConfiguration().setClock(clock);
        timerJobs = processEngineManagementService.createTimerJobQuery().processInstanceId(processInstance.getId()).executable().list();
        Assert.assertEquals(1, timerJobs.size());
        Assert.assertEquals(timerJobId, timerJobs.get(0).getId());
        JobTestHelper.waitForJobExecutorToProcessAllJobs(AbstractProcessEngineIntegrationTest.processEngine.getProcessEngineConfiguration(), processEngineManagementService, 7000, 200, true);
        timerJobs = processEngineManagementService.createTimerJobQuery().processInstanceId(processInstance.getId()).list();
        Assert.assertEquals(0, timerJobs.size());
        Task task = processEngineTaskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        Assert.assertNotNull(task);
        Assert.assertEquals("secondTask", task.getTaskDefinitionKey());
        processEngineTaskService.complete(task.getId());
        Assert.assertEquals(0, processEngineRuntimeService.createProcessInstanceQuery().count());
        AbstractProcessEngineIntegrationTest.cmmnEngineConfiguration.resetClock();
        resetClock();
    }
}

