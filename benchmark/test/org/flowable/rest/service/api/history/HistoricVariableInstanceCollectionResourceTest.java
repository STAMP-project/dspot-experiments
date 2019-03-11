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
package org.flowable.rest.service.api.history;


import RestUrls.URL_HISTORIC_VARIABLE_INSTANCES;
import java.util.HashMap;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.rest.service.api.RestUrls;
import org.flowable.task.api.Task;
import org.junit.Test;


/**
 * Test for REST-operation related to the historic variable instance query resource.
 *
 * @author Tijs Rademakers
 */
public class HistoricVariableInstanceCollectionResourceTest extends BaseSpringRestTestCase {
    /**
     * Test querying historic variable instance. GET history/historic-variable-instances
     */
    @Test
    @Deployment
    public void testQueryVariableInstances() throws Exception {
        HashMap<String, Object> processVariables = new HashMap<>();
        processVariables.put("stringVar", "Azerty");
        processVariables.put("intVar", 67890);
        processVariables.put("booleanVar", false);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", processVariables);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.setVariableLocal(task.getId(), "taskVariable", "test");
        ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey("oneTaskProcess", processVariables);
        String url = RestUrls.createRelativeResourceUrl(URL_HISTORIC_VARIABLE_INSTANCES);
        assertResultsPresentInDataResponse((url + "?variableName=stringVar"), 2, "stringVar", "Azerty");
        assertResultsPresentInDataResponse((url + "?variableName=booleanVar"), 2, "booleanVar", false);
        assertResultsPresentInDataResponse((url + "?variableName=booleanVar2"), 0, null, null);
        assertResultsPresentInDataResponse(((url + "?processInstanceId=") + (processInstance.getId())), 4, "taskVariable", "test");
        assertResultsPresentInDataResponse((((url + "?processInstanceId=") + (processInstance.getId())) + "&excludeTaskVariables=true"), 3, "intVar", 67890);
        assertResultsPresentInDataResponse(((url + "?processInstanceId=") + (processInstance2.getId())), 3, "stringVar", "Azerty");
        assertResultsPresentInDataResponse(((url + "?taskId=") + (task.getId())), 1, "taskVariable", "test");
        assertResultsPresentInDataResponse((((url + "?taskId=") + (task.getId())) + "&variableName=booleanVar"), 0, null, null);
        assertResultsPresentInDataResponse(((url + "?variableNameLike=") + (encode("%Var"))), 6, "stringVar", "Azerty");
        assertResultsPresentInDataResponse(((url + "?variableNameLike=") + (encode("%Var2"))), 0, null, null);
    }
}

