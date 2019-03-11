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
package org.flowable.rest.service.api.management;


import RestUrls.URL_JOB;
import RestUrls.URL_TIMER_JOB;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Calendar;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.flowable.engine.impl.cmd.ChangeDeploymentTenantIdCmd;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.job.api.Job;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.rest.service.api.RestUrls;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for all REST-operations related to the Job collection and a single job resource.
 *
 * @author Frederik Heremans
 */
public class JobResourceTest extends BaseSpringRestTestCase {
    /**
     * Test getting a single job.
     */
    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/management/JobResourceTest.testTimerProcess.bpmn20.xml" })
    public void testGetJob() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("timerProcess");
        Job timerJob = managementService.createTimerJobQuery().processInstanceId(processInstance.getId()).singleResult();
        Assert.assertNotNull(timerJob);
        Calendar now = Calendar.getInstance();
        now.set(Calendar.MILLISECOND, 0);
        processEngineConfiguration.getClock().setCurrentTime(now.getTime());
        CloseableHttpResponse response = executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_TIMER_JOB, timerJob.getId())))), HttpStatus.SC_OK);
        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        Assert.assertNotNull(responseNode);
        Assert.assertEquals(timerJob.getId(), responseNode.get("id").textValue());
        Assert.assertEquals(timerJob.getExceptionMessage(), responseNode.get("exceptionMessage").textValue());
        Assert.assertEquals(timerJob.getExecutionId(), responseNode.get("executionId").textValue());
        Assert.assertEquals(timerJob.getProcessDefinitionId(), responseNode.get("processDefinitionId").textValue());
        Assert.assertEquals(timerJob.getProcessInstanceId(), responseNode.get("processInstanceId").textValue());
        Assert.assertEquals(timerJob.getRetries(), responseNode.get("retries").intValue());
        Assert.assertEquals(timerJob.getDuedate(), getDateFromISOString(responseNode.get("dueDate").textValue()));
        Assert.assertEquals("", responseNode.get("tenantId").textValue());
        // Set tenant on deployment
        managementService.executeCommand(new ChangeDeploymentTenantIdCmd(deploymentId, "myTenant"));
        response = executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_TIMER_JOB, timerJob.getId())))), HttpStatus.SC_OK);
        responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        Assert.assertNotNull(responseNode);
        Assert.assertEquals("myTenant", responseNode.get("tenantId").textValue());
    }

    /**
     * Test getting an unexisting job.
     */
    @Test
    public void testGetUnexistingJob() throws Exception {
        CloseableHttpResponse response = executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_JOB, "unexistingjob")))), HttpStatus.SC_NOT_FOUND);
        closeResponse(response);
    }

    /**
     * Test executing a single job.
     */
    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/management/JobResourceTest.testTimerProcess.bpmn20.xml" })
    public void testExecuteJob() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("timerProcess");
        Job timerJob = managementService.createTimerJobQuery().processInstanceId(processInstance.getId()).singleResult();
        Assert.assertNotNull(timerJob);
        managementService.moveTimerToExecutableJob(timerJob.getId());
        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.put("action", "execute");
        HttpPost httpPost = new HttpPost(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_JOB, timerJob.getId()))));
        httpPost.setEntity(new StringEntity(requestNode.toString()));
        CloseableHttpResponse response = executeRequest(httpPost, HttpStatus.SC_NO_CONTENT);
        closeResponse(response);
        // Job should be executed
        Assert.assertNull(managementService.createJobQuery().processInstanceId(processInstance.getId()).singleResult());
    }

    /**
     * Test executing an unexisting job.
     */
    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/management/JobResourceTest.testTimerProcess.bpmn20.xml" })
    public void testExecuteUnexistingJob() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("timerProcess");
        Job timerJob = managementService.createTimerJobQuery().processInstanceId(processInstance.getId()).singleResult();
        Assert.assertNotNull(timerJob);
        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.put("action", "execute");
        HttpPost httpPost = new HttpPost(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_JOB, "unexistingjob"))));
        httpPost.setEntity(new StringEntity(requestNode.toString()));
        CloseableHttpResponse response = executeRequest(httpPost, HttpStatus.SC_NOT_FOUND);
        closeResponse(response);
    }

    /**
     * Test executing an unexisting job.
     */
    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/management/JobResourceTest.testTimerProcess.bpmn20.xml" })
    public void testIllegalActionOnJob() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("timerProcess");
        Job timerJob = managementService.createTimerJobQuery().processInstanceId(processInstance.getId()).singleResult();
        Assert.assertNotNull(timerJob);
        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.put("action", "unexistinAction");
        HttpPost httpPost = new HttpPost(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_JOB, timerJob.getId()))));
        httpPost.setEntity(new StringEntity(requestNode.toString()));
        CloseableHttpResponse response = executeRequest(httpPost, HttpStatus.SC_BAD_REQUEST);
        closeResponse(response);
    }

    /**
     * Test deleting a single job.
     */
    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/management/JobResourceTest.testTimerProcess.bpmn20.xml" })
    public void testDeleteJob() throws Exception {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("timerProcess");
        Job timerJob = managementService.createTimerJobQuery().processInstanceId(processInstance.getId()).singleResult();
        Assert.assertNotNull(timerJob);
        HttpDelete httpDelete = new HttpDelete(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_TIMER_JOB, timerJob.getId()))));
        CloseableHttpResponse response = executeRequest(httpDelete, HttpStatus.SC_NO_CONTENT);
        closeResponse(response);
        // Job should be deleted
        Assert.assertNull(managementService.createJobQuery().processInstanceId(processInstance.getId()).singleResult());
    }

    /**
     * Test getting an unexisting job.
     */
    @Test
    public void testDeleteUnexistingJob() throws Exception {
        HttpDelete httpDelete = new HttpDelete(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_JOB, "unexistingjob"))));
        CloseableHttpResponse response = executeRequest(httpDelete, HttpStatus.SC_NOT_FOUND);
        closeResponse(response);
    }
}

