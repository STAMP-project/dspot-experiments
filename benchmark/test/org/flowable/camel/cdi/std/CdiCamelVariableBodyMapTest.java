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
package org.flowable.camel.cdi.std;


import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.flowable.camel.cdi.BaseCamelCdiFlowableTestCase;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


/**
 * Adapted from {@link CamelVariableBodyMapTest} to test {@link CdiCamelBehaviorBodyAsMapImpl}.
 *
 * @author Zach Visagie
 */
public class CdiCamelVariableBodyMapTest extends StdCamelCdiFlowableTestCase {
    protected MockEndpoint service1;

    @Inject
    protected CamelContext camelContext;

    @Test
    @Deployment(resources = { "process/HelloCamelCdiBodyMap.bpmn20.xml" })
    public void testCamelBody() throws Exception {
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("camelBody", "hello world");
        service1.expectedBodiesReceived(varMap);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("HelloCamel", varMap);
        // Ensure that the variable is equal to the expected value.
        Assert.assertEquals("hello world", runtimeService.getVariable(processInstance.getId(), "camelBody"));
        service1.assertIsSatisfied();
        Task task = taskService.createTaskQuery().singleResult();
        // Ensure that the name of the task is correct.
        Assert.assertEquals("Hello Task", task.getName());
        // Complete the task.
        taskService.complete(task.getId());
    }
}

