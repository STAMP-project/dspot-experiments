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
package org.flowable.cmmn.test.listener;


import java.util.List;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.test.impl.CustomCmmnConfigurationFlowableTestCase;
import org.flowable.task.api.Task;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.task.service.delegate.TaskListener;
import org.junit.Test;


/**
 *
 *
 * @author Joram Barrez
 */
public class TaskListenerTest extends CustomCmmnConfigurationFlowableTestCase {
    @Test
    @CmmnDeployment
    public void testCreateEvent() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTaskListeners").start();
        assertVariable(caseInstance, "variableFromClassDelegate", "Hello World from class delegate");
        assertVariable(caseInstance, "variableFromDelegateExpression", "Hello World from delegate expression");
        assertVariable(caseInstance, "expressionVariable", "Hello World from expression");
    }

    @Test
    @CmmnDeployment
    public void testCompleteEvent() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTaskListeners").start();
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).list();
        for (Task task : tasks) {
            if (!(task.getName().equals("Keepalive"))) {
                cmmnTaskService.complete(task.getId());
            }
        }
        assertVariable(caseInstance, "variableFromClassDelegate", "Hello World from class delegate");
        assertVariable(caseInstance, "variableFromDelegateExpression", "Hello World from delegate expression");
        assertVariable(caseInstance, "expressionVariable", "Hello World from expression");
    }

    @Test
    @CmmnDeployment
    public void testAssignEvent() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("testTaskListeners").start();
        List<Task> tasks = cmmnTaskService.createTaskQuery().caseInstanceId(caseInstance.getId()).list();
        for (Task task : tasks) {
            if (!(task.getName().equals("Keepalive"))) {
                cmmnTaskService.setAssignee(task.getId(), "testAssignee");
            }
        }
        assertVariable(caseInstance, "variableFromClassDelegate", "Hello World from class delegate");
        assertVariable(caseInstance, "variableFromDelegateExpression", "Hello World from delegate expression");
        assertVariable(caseInstance, "expressionVariable", "Hello World from expression");
    }

    static class TestDelegateTaskListener implements TaskListener {
        @Override
        public void notify(DelegateTask delegateTask) {
            delegateTask.setVariable("variableFromDelegateExpression", "Hello World from delegate expression");
        }
    }
}

