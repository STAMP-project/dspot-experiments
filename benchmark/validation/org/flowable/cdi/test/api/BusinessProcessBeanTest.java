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
package org.flowable.cdi.test.api;


import org.flowable.cdi.BusinessProcess;
import org.flowable.cdi.test.CdiFlowableTestCase;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Daniel Meyer
 */
public class BusinessProcessBeanTest extends CdiFlowableTestCase {
    /* General test asserting that the business process bean is functional */
    @Test
    @Deployment
    public void test() throws Exception {
        BusinessProcess businessProcess = getBeanInstance(BusinessProcess.class);
        // start the process
        businessProcess.startProcessByKey("businessProcessBeanTest").getId();
        // ensure that the process is started:
        Assert.assertNotNull(processEngine.getRuntimeService().createProcessInstanceQuery().singleResult());
        // ensure that there is a single task waiting
        Task task = processEngine.getTaskService().createTaskQuery().singleResult();
        Assert.assertNotNull(task);
        String value = "value";
        businessProcess.setVariable("key", value);
        Assert.assertEquals(value, businessProcess.getVariable("key"));
        // complete the task
        Assert.assertEquals(task.getId(), businessProcess.startTask(task.getId()).getId());
        businessProcess.completeTask();
        // assert the task is completed
        Assert.assertNull(processEngine.getTaskService().createTaskQuery().singleResult());
        // assert that the process is ended:
        Assert.assertNull(processEngine.getRuntimeService().createProcessInstanceQuery().singleResult());
    }

    @Test
    @Deployment
    public void testProcessWithoutWaitState() {
        BusinessProcess businessProcess = getBeanInstance(BusinessProcess.class);
        // start the process
        businessProcess.startProcessByKey("businessProcessBeanTest").getId();
        // assert that the process is ended:
        Assert.assertNull(processEngine.getRuntimeService().createProcessInstanceQuery().singleResult());
    }

    @Test
    @Deployment(resources = "org/flowable/cdi/test/api/BusinessProcessBeanTest.test.bpmn20.xml")
    public void testResolveProcessInstanceBean() {
        BusinessProcess businessProcess = getBeanInstance(BusinessProcess.class);
        Assert.assertNull(getBeanInstance(ProcessInstance.class));
        Assert.assertNull(getBeanInstance("processInstanceId"));
        Assert.assertNull(getBeanInstance(Execution.class));
        Assert.assertNull(getBeanInstance("executionId"));
        String pid = businessProcess.startProcessByKey("businessProcessBeanTest").getId();
        // assert that now we can resolve the ProcessInstance-bean
        Assert.assertEquals(pid, getBeanInstance(ProcessInstance.class).getId());
        Assert.assertEquals(pid, getBeanInstance("processInstanceId"));
        Assert.assertEquals(pid, getBeanInstance(Execution.class).getId());
        Assert.assertEquals(pid, getBeanInstance("executionId"));
        taskService.complete(taskService.createTaskQuery().singleResult().getId());
    }

    @Test
    @Deployment(resources = "org/flowable/cdi/test/api/BusinessProcessBeanTest.test.bpmn20.xml")
    public void testResolveTaskBean() {
        BusinessProcess businessProcess = getBeanInstance(BusinessProcess.class);
        Assert.assertNull(getBeanInstance(Task.class));
        Assert.assertNull(getBeanInstance("taskId"));
        businessProcess.startProcessByKey("businessProcessBeanTest");
        String taskId = taskService.createTaskQuery().singleResult().getId();
        businessProcess.startTask(taskId);
        // assert that now we can resolve the Task-bean
        Assert.assertEquals(taskId, getBeanInstance(Task.class).getId());
        Assert.assertEquals(taskId, getBeanInstance("taskId"));
        taskService.complete(taskService.createTaskQuery().singleResult().getId());
    }
}

