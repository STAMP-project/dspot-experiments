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
package org.flowable.standalone.escapeclause;


import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;


public class ExecutionQueryEscapeClauseTest extends AbstractEscapeClauseTestCase {
    private String deploymentOneId;

    private String deploymentTwoId;

    private ProcessInstance processInstance1;

    private ProcessInstance processInstance2;

    @Test
    public void testQueryByTenantIdLike() {
        Execution execution = runtimeService.createExecutionQuery().onlyChildExecutions().executionTenantIdLike("%\\%%").singleResult();
        assertNotNull(execution);
        execution = runtimeService.createExecutionQuery().onlyChildExecutions().executionTenantIdLike("%\\_%").singleResult();
        assertNotNull(execution);
    }

    @Test
    public void testQueryLikeByQueryVariableValue() {
        Execution execution = runtimeService.createExecutionQuery().variableValueLike("var1", "%\\%%").singleResult();
        assertNotNull(execution);
        assertEquals(processInstance1.getId(), execution.getId());
        execution = runtimeService.createExecutionQuery().variableValueLike("var1", "%\\_%").singleResult();
        assertNotNull(execution);
        assertEquals(processInstance2.getId(), execution.getId());
    }

    @Test
    public void testQueryLikeIgnoreCaseByQueryVariableValue() {
        Execution execution = runtimeService.createExecutionQuery().variableValueLikeIgnoreCase("var1", "%\\%%").singleResult();
        assertNotNull(execution);
        assertEquals(processInstance1.getId(), execution.getId());
        execution = runtimeService.createExecutionQuery().variableValueLikeIgnoreCase("var1", "%\\_%").singleResult();
        assertNotNull(execution);
        assertEquals(processInstance2.getId(), execution.getId());
    }

    @Test
    public void testQueryLikeByQueryProcessVariableValue() {
        Execution execution = runtimeService.createExecutionQuery().onlyChildExecutions().processVariableValueLike("var1", "%\\%%").singleResult();
        assertNotNull(execution);
        execution = runtimeService.createExecutionQuery().onlyChildExecutions().processVariableValueLike("var1", "%\\_%").singleResult();
        assertNotNull(execution);
    }

    @Test
    public void testQueryLikeIgnoreCaseByQueryProcessVariableValue() {
        Execution execution = runtimeService.createExecutionQuery().onlyChildExecutions().processVariableValueLikeIgnoreCase("var1", "%\\%%").singleResult();
        assertNotNull(execution);
        execution = runtimeService.createExecutionQuery().onlyChildExecutions().processVariableValueLikeIgnoreCase("var1", "%\\_%").singleResult();
        assertNotNull(execution);
    }
}

