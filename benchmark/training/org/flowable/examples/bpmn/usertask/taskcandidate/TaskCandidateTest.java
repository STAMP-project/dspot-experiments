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
package org.flowable.examples.bpmn.usertask.taskcandidate;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Joram Barrez, Saeid Mirzaei
 */
public class TaskCandidateTest extends PluggableFlowableTestCase {
    private static final String KERMIT = "kermit";

    private static final String GONZO = "gonzo";

    @Test
    @Deployment
    public void testSingleCandidateGroup() {
        // Deploy and start process
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("singleCandidateGroup");
        // org.flowable.task.service.Task should not yet be assigned to kermit
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(TaskCandidateTest.KERMIT).list();
        assertTrue(tasks.isEmpty());
        // The task should be visible in the candidate task list
        tasks = taskService.createTaskQuery().taskCandidateUser(TaskCandidateTest.KERMIT).list();
        assertEquals(1, tasks.size());
        Task task = tasks.get(0);
        assertEquals("Pay out expenses", task.getName());
        // Claim the task
        taskService.claim(task.getId(), TaskCandidateTest.KERMIT);
        // The task must now be gone from the candidate task list
        tasks = taskService.createTaskQuery().taskCandidateUser(TaskCandidateTest.KERMIT).list();
        assertTrue(tasks.isEmpty());
        // The task will be visible on the personal task list
        tasks = taskService.createTaskQuery().taskAssignee(TaskCandidateTest.KERMIT).list();
        assertEquals(1, tasks.size());
        task = tasks.get(0);
        assertEquals("Pay out expenses", task.getName());
        // Completing the task ends the process
        taskService.complete(task.getId());
        assertProcessEnded(processInstance.getId());
    }

    @Test
    @Deployment
    public void testMultipleCandidateGroups() {
        // Deploy and start process
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("multipleCandidatesGroup");
        // org.flowable.task.service.Task should not yet be assigned to anyone
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(TaskCandidateTest.KERMIT).list();
        assertTrue(tasks.isEmpty());
        tasks = taskService.createTaskQuery().taskAssignee(TaskCandidateTest.GONZO).list();
        assertTrue(tasks.isEmpty());
        // The task should be visible in the candidate task list of Gonzo and
        // Kermit
        // and anyone in the management/accountancy group
        assertEquals(1, taskService.createTaskQuery().taskCandidateUser(TaskCandidateTest.KERMIT).list().size());
        assertEquals(1, taskService.createTaskQuery().taskCandidateUser(TaskCandidateTest.GONZO).list().size());
        assertEquals(1, taskService.createTaskQuery().taskCandidateGroup("management").count());
        assertEquals(1, taskService.createTaskQuery().taskCandidateGroup("accountancy").count());
        assertEquals(0, taskService.createTaskQuery().taskCandidateGroup("sales").count());
        // Gonzo claims the task
        tasks = taskService.createTaskQuery().taskCandidateUser(TaskCandidateTest.GONZO).list();
        Task task = tasks.get(0);
        assertEquals("Approve expenses", task.getName());
        taskService.claim(task.getId(), TaskCandidateTest.GONZO);
        // The task must now be gone from the candidate task lists
        assertTrue(taskService.createTaskQuery().taskCandidateUser(TaskCandidateTest.KERMIT).list().isEmpty());
        assertTrue(taskService.createTaskQuery().taskCandidateUser(TaskCandidateTest.GONZO).list().isEmpty());
        assertEquals(0, taskService.createTaskQuery().taskCandidateGroup("management").count());
        // The task will be visible on the personal task list of Gonzo
        assertEquals(1, taskService.createTaskQuery().taskAssignee(TaskCandidateTest.GONZO).count());
        // But not on the personal task list of (for example) Kermit
        assertEquals(0, taskService.createTaskQuery().taskAssignee(TaskCandidateTest.KERMIT).count());
        // Completing the task ends the process
        taskService.complete(task.getId());
        assertProcessEnded(processInstance.getId());
    }

    @Test
    @Deployment
    public void testMultipleCandidateUsers() {
        runtimeService.startProcessInstanceByKey("multipleCandidateUsersExample", Collections.singletonMap("Variable", ((Object) ("var"))));
        assertEquals(1, taskService.createTaskQuery().taskCandidateUser(TaskCandidateTest.GONZO).list().size());
        assertEquals(1, taskService.createTaskQuery().taskCandidateUser(TaskCandidateTest.KERMIT).list().size());
        List<Task> tasks = taskService.createTaskQuery().taskInvolvedUser(TaskCandidateTest.KERMIT).list();
        assertEquals(1, tasks.size());
        Task task = tasks.get(0);
        taskService.setVariableLocal(task.getId(), "taskVar", 123);
        tasks = taskService.createTaskQuery().taskInvolvedUser(TaskCandidateTest.KERMIT).includeProcessVariables().includeTaskLocalVariables().list();
        task = tasks.get(0);
        assertEquals(1, task.getProcessVariables().size());
        assertEquals(1, task.getTaskLocalVariables().size());
        taskService.addUserIdentityLink(task.getId(), TaskCandidateTest.GONZO, "test");
        tasks = taskService.createTaskQuery().taskInvolvedUser(TaskCandidateTest.GONZO).includeProcessVariables().includeTaskLocalVariables().list();
        assertEquals(1, tasks.size());
        assertEquals(1, task.getProcessVariables().size());
        assertEquals(1, task.getTaskLocalVariables().size());
    }

    @Test
    @Deployment
    public void testMixedCandidateUserAndGroup() {
        runtimeService.startProcessInstanceByKey("mixedCandidateUserAndGroupExample");
        assertEquals(1, taskService.createTaskQuery().taskCandidateUser(TaskCandidateTest.GONZO).list().size());
        assertEquals(1, taskService.createTaskQuery().taskCandidateUser(TaskCandidateTest.KERMIT).list().size());
    }

    // test if candidate group works with expression, when there is a function
    // with one parameter
    @Test
    @Deployment
    public void testCandidateExpressionOneParam() {
        Map<String, Object> params = new HashMap<>();
        params.put("testBean", new TestBean());
        runtimeService.startProcessInstanceByKey("candidateWithExpression", params);
        assertEquals(1, taskService.createTaskQuery().taskCandidateUser(TaskCandidateTest.KERMIT).list().size());
    }

    // test if candidate group works with expression, when there is a function
    // with two parameters
    @Test
    @Deployment
    public void testCandidateExpressionTwoParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("testBean", new TestBean());
        runtimeService.startProcessInstanceByKey("candidateWithExpression", params);
        assertEquals(1, taskService.createTaskQuery().taskCandidateUser(TaskCandidateTest.KERMIT).count());
        assertEquals(1, taskService.createTaskQuery().taskCandidateGroup("sales").count());
    }
}

