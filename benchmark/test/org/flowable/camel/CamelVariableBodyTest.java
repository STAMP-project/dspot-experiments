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
package org.flowable.camel;


import java.util.HashMap;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.spring.impl.test.SpringFlowableTestCase;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;


@Tag("camel")
@ContextConfiguration("classpath:generic-camel-flowable-context.xml")
public class CamelVariableBodyTest extends SpringFlowableTestCase {
    @Autowired
    protected CamelContext camelContext;

    protected MockEndpoint service1;

    @Test
    @Deployment(resources = { "process/HelloCamelBody.bpmn20.xml" })
    public void testCamelBody() throws Exception {
        service1.expectedBodiesReceived("hello world");
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("camelBody", "hello world");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("HelloCamel", varMap);
        // Ensure that the variable is equal to the expected value.
        assertEquals("hello world", runtimeService.getVariable(processInstance.getId(), "camelBody"));
        service1.assertIsSatisfied();
        Task task = taskService.createTaskQuery().singleResult();
        // Ensure that the name of the task is correct.
        assertEquals("Hello Task", task.getName());
        // Complete the task.
        taskService.complete(task.getId());
    }
}

