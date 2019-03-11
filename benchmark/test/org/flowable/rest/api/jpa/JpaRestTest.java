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
package org.flowable.rest.api.jpa;


import RestUrls.URL_HISTORIC_PROCESS_INSTANCES;
import RestUrls.URL_HISTORIC_VARIABLE_INSTANCES;
import RestUrls.URL_TASK_COLLECTION;
import RestUrls.URL_TASK_VARIABLES_COLLECTION;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.rest.api.jpa.model.Message;
import org.flowable.rest.api.jpa.repository.MessageRepository;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.rest.service.api.RestUrls;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


public class JpaRestTest extends BaseJPARestTestCase {
    protected MessageRepository messageRepository;

    @Test
    @Deployment(resources = { "org/flowable/rest/api/jpa/jpa-process.bpmn20.xml" })
    public void testGetJpaVariableViaTaskVariablesCollections() throws Exception {
        // Get JPA managed entity through the repository
        Message message = findOne(1L);
        Assert.assertNotNull(message);
        Assert.assertEquals("Hello World", message.getText());
        // add the entity to the process variables and start the process
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("message", message);
        ProcessInstance processInstance = BaseSpringRestTestCase.processEngine.getRuntimeService().startProcessInstanceByKey("jpa-process", processVariables);
        Assert.assertNotNull(processInstance);
        Task task = BaseSpringRestTestCase.processEngine.getTaskService().createTaskQuery().singleResult();
        Assert.assertEquals("Activiti is awesome!", task.getName());
        // Request all variables (no scope provides) which include global and
        // local
        HttpResponse response = executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_TASK_VARIABLES_COLLECTION, task.getId())))), HttpStatus.SC_OK);
        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent()).get(0);
        // check for message variable of type serializable
        Assert.assertNotNull(responseNode);
        Assert.assertEquals("message", responseNode.get("name").asText());
        Assert.assertEquals("global", responseNode.get("scope").asText());
        Assert.assertEquals("serializable", responseNode.get("type").asText());
        Assert.assertNotNull(responseNode.get("valueUrl"));
    }

    @Test
    @Deployment(resources = { "org/flowable/rest/api/jpa/jpa-process.bpmn20.xml" })
    public void testGetJpaVariableViaTaskCollection() throws Exception {
        // Get JPA managed entity through the repository
        Message message = findOne(1L);
        Assert.assertNotNull(message);
        Assert.assertEquals("Hello World", message.getText());
        // add the entity to the process variables and start the process
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("message", message);
        ProcessInstance processInstance = BaseSpringRestTestCase.processEngine.getRuntimeService().startProcessInstanceByKey("jpa-process", processVariables);
        Assert.assertNotNull(processInstance);
        Task task = BaseSpringRestTestCase.processEngine.getTaskService().createTaskQuery().singleResult();
        Assert.assertEquals("Activiti is awesome!", task.getName());
        // Request all variables (no scope provides) which include global and
        // local
        HttpResponse response = executeRequest(new HttpGet((((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_TASK_COLLECTION))) + "?includeProcessVariables=true")), HttpStatus.SC_OK);
        JsonNode dataNode = objectMapper.readTree(response.getEntity().getContent()).get("data").get(0);
        Assert.assertNotNull(dataNode);
        JsonNode variableNode = dataNode.get("variables").get(0);
        Assert.assertNotNull(variableNode);
        // check for message variable of type serializable
        Assert.assertEquals("message", variableNode.get("name").asText());
        Assert.assertEquals("global", variableNode.get("scope").asText());
        Assert.assertEquals("serializable", variableNode.get("type").asText());
        Assert.assertNotNull(variableNode.get("valueUrl"));
    }

    @Test
    @Deployment(resources = { "org/flowable/rest/api/jpa/jpa-process.bpmn20.xml" })
    public void testGetJpaVariableViaHistoricProcessCollection() throws Exception {
        // Get JPA managed entity through the repository
        Message message = findOne(1L);
        Assert.assertNotNull(message);
        Assert.assertEquals("Hello World", message.getText());
        // add the entity to the process variables and start the process
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("message", message);
        ProcessInstance processInstance = BaseSpringRestTestCase.processEngine.getRuntimeService().startProcessInstanceByKey("jpa-process", processVariables);
        Assert.assertNotNull(processInstance);
        Task task = BaseSpringRestTestCase.processEngine.getTaskService().createTaskQuery().singleResult();
        Assert.assertEquals("Activiti is awesome!", task.getName());
        // Request all variables (no scope provides) which include global and
        // local
        HttpResponse response = executeRequest(new HttpGet((((((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_HISTORIC_PROCESS_INSTANCES))) + "?processInstanceId=") + (processInstance.getId())) + "&includeProcessVariables=true")), HttpStatus.SC_OK);
        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
        // check for message variable of type serializable
        Assert.assertNotNull(responseNode);
        JsonNode variablesArrayNode = responseNode.get("data").get(0).get("variables");
        Assert.assertEquals(1, variablesArrayNode.size());
        JsonNode variableNode = variablesArrayNode.get(0);
        Assert.assertEquals("message", variableNode.get("name").asText());
        Assert.assertEquals("serializable", variableNode.get("type").asText());
        Assert.assertNotNull(variableNode.get("valueUrl"));
    }

    @Test
    @Deployment(resources = { "org/flowable/rest/api/jpa/jpa-process.bpmn20.xml" })
    public void testGetJpaVariableViaHistoricVariablesCollections() throws Exception {
        // Get JPA managed entity through the repository
        Message message = findOne(1L);
        Assert.assertNotNull(message);
        Assert.assertEquals("Hello World", message.getText());
        // add the entity to the process variables and start the process
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("message", message);
        ProcessInstance processInstance = BaseSpringRestTestCase.processEngine.getRuntimeService().startProcessInstanceByKey("jpa-process", processVariables);
        Assert.assertNotNull(processInstance);
        Task task = BaseSpringRestTestCase.processEngine.getTaskService().createTaskQuery().singleResult();
        Assert.assertEquals("Activiti is awesome!", task.getName());
        // Request all variables (no scope provides) which include global and
        // local
        HttpResponse response = executeRequest(new HttpGet(((((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_HISTORIC_VARIABLE_INSTANCES))) + "?processInstanceId=") + (processInstance.getId()))), HttpStatus.SC_OK);
        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
        // check for message variable of type serializable
        Assert.assertNotNull(responseNode);
        JsonNode variableNode = responseNode.get("data").get(0).get("variable");
        Assert.assertEquals("message", variableNode.get("name").asText());
        Assert.assertEquals("serializable", variableNode.get("type").asText());
        Assert.assertNotNull(variableNode.get("valueUrl"));
    }
}

