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


import RestUrls.URL_HISTORIC_TASK_INSTANCE_IDENTITY_LINKS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.rest.service.api.RestUrls;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for REST-operation related to the historic task instance identity links resource.
 *
 * @author Tijs Rademakers
 */
public class HistoricTaskInstanceIdentityLinkCollectionResourceTest extends BaseSpringRestTestCase {
    protected ISO8601DateFormat dateFormat = new ISO8601DateFormat();

    /**
     * Test querying historic task instance. GET history/historic-task-instances/{taskId}/identitylinks
     */
    @Test
    @Deployment
    public void testGetIdentityLinks() throws Exception {
        HashMap<String, Object> processVariables = new HashMap<>();
        processVariables.put("stringVar", "Azerty");
        processVariables.put("intVar", 67890);
        processVariables.put("booleanVar", false);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess", processVariables);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.complete(task.getId());
        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.setOwner(task.getId(), "test");
        String url = RestUrls.createRelativeResourceUrl(URL_HISTORIC_TASK_INSTANCE_IDENTITY_LINKS, task.getId());
        // Do the actual call
        CloseableHttpResponse response = executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + url)), HttpStatus.SC_OK);
        // Check status and size
        JsonNode linksArray = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        Assert.assertEquals(2, linksArray.size());
        Map<String, JsonNode> linksMap = new HashMap<>();
        for (JsonNode linkNode : linksArray) {
            linksMap.put(linkNode.get("type").asText(), linkNode);
        }
        JsonNode assigneeNode = linksMap.get("assignee");
        Assert.assertNotNull(assigneeNode);
        Assert.assertEquals("fozzie", assigneeNode.get("userId").asText());
        Assert.assertTrue(assigneeNode.get("groupId").isNull());
        Assert.assertEquals(task.getId(), assigneeNode.get("taskId").asText());
        Assert.assertNotNull(assigneeNode.get("taskUrl").asText());
        Assert.assertTrue(assigneeNode.get("processInstanceId").isNull());
        Assert.assertTrue(assigneeNode.get("processInstanceUrl").isNull());
        JsonNode ownerNode = linksMap.get("owner");
        Assert.assertNotNull(ownerNode);
        Assert.assertEquals("test", ownerNode.get("userId").asText());
        Assert.assertTrue(ownerNode.get("groupId").isNull());
        Assert.assertEquals(task.getId(), ownerNode.get("taskId").asText());
        Assert.assertNotNull(ownerNode.get("taskUrl").asText());
        Assert.assertTrue(ownerNode.get("processInstanceId").isNull());
        Assert.assertTrue(ownerNode.get("processInstanceUrl").isNull());
    }
}

