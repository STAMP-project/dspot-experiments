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


import RestUrls.URL_HISTORIC_PROCESS_INSTANCES;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Calendar;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.flowable.engine.impl.cmd.ChangeDeploymentTenantIdCmd;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.rest.service.api.RestUrls;
import org.flowable.task.api.Task;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for REST-operation related to the historic process instance query resource.
 *
 * @author Tijs Rademakers
 */
public class HistoricProcessInstanceCollectionResourceTest extends BaseSpringRestTestCase {
    /**
     * Test querying historic process instance based on variables. GET history/historic-process-instances
     */
    @Test
    @Deployment
    public void testQueryProcessInstances() throws Exception {
        Calendar startTime = Calendar.getInstance();
        processEngineConfiguration.getClock().setCurrentTime(startTime.getTime());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("oneTaskProcess");
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskService.complete(task.getId());
        startTime.add(Calendar.DAY_OF_YEAR, 1);
        processEngineConfiguration.getClock().setCurrentTime(startTime.getTime());
        ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey("oneTaskProcess");
        String url = RestUrls.createRelativeResourceUrl(URL_HISTORIC_PROCESS_INSTANCES);
        assertResultsPresentInDataResponse((url + "?finished=true"), processInstance.getId());
        assertResultsPresentInDataResponse((url + "?finished=false"), processInstance2.getId());
        assertResultsPresentInDataResponse(((url + "?processDefinitionId=") + (processInstance.getProcessDefinitionId())), processInstance.getId(), processInstance2.getId());
        assertResultsPresentInDataResponse((((url + "?processDefinitionId=") + (processInstance.getProcessDefinitionId())) + "&finished=true"), processInstance.getId());
        assertResultsPresentInDataResponse((url + "?processDefinitionKey=oneTaskProcess"), processInstance.getId(), processInstance2.getId());
        // Without tenant ID, before setting tenant
        assertResultsPresentInDataResponse((url + "?withoutTenantId=true"), processInstance.getId(), processInstance2.getId());
        // Set tenant on deployment
        managementService.executeCommand(new ChangeDeploymentTenantIdCmd(deploymentId, "myTenant"));
        startTime.add(Calendar.DAY_OF_YEAR, 1);
        processEngineConfiguration.getClock().setCurrentTime(startTime.getTime());
        ProcessInstance processInstance3 = runtimeService.startProcessInstanceByKeyAndTenantId("oneTaskProcess", "myTenant");
        // Without tenant ID, after setting tenant
        assertResultsPresentInDataResponse((url + "?withoutTenantId=true"), processInstance.getId(), processInstance2.getId());
        // Tenant id
        assertResultsPresentInDataResponse((url + "?tenantId=myTenant"), processInstance3.getId());
        assertResultsPresentInDataResponse((url + "?tenantId=anotherTenant"));
        // Tenant id like
        assertResultsPresentInDataResponse(((url + "?tenantIdLike=") + (encode("%enant"))), processInstance3.getId());
        assertResultsPresentInDataResponse((url + "?tenantIdLike=anotherTenant"));
        CloseableHttpResponse response = executeRequest(new HttpGet((((BaseSpringRestTestCase.SERVER_URL_PREFIX) + url) + "?processDefinitionKey=oneTaskProcess&sort=startTime")), 200);
        // Check status and size
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        JsonNode dataNode = objectMapper.readTree(response.getEntity().getContent()).get("data");
        closeResponse(response);
        Assert.assertEquals(3, dataNode.size());
        Assert.assertEquals(processInstance.getId(), dataNode.get(0).get("id").asText());
        Assert.assertEquals(processInstance2.getId(), dataNode.get(1).get("id").asText());
        Assert.assertEquals(processInstance3.getId(), dataNode.get(2).get("id").asText());
    }
}

