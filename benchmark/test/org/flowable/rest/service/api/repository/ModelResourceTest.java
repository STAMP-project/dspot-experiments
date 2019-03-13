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
package org.flowable.rest.service.api.repository;


import RestUrls.URL_DEPLOYMENT;
import RestUrls.URL_MODEL;
import RestUrls.URL_MODEL_SOURCE;
import RestUrls.URL_MODEL_SOURCE_EXTRA;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Calendar;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.flowable.engine.repository.Model;
import org.flowable.engine.test.Deployment;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.rest.service.api.RestUrls;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Frederik Heremans
 */
public class ModelResourceTest extends BaseSpringRestTestCase {
    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/repository/oneTaskProcess.bpmn20.xml" })
    public void testGetModel() throws Exception {
        Model model = null;
        try {
            Calendar now = Calendar.getInstance();
            now.set(Calendar.MILLISECOND, 0);
            processEngineConfiguration.getClock().setCurrentTime(now.getTime());
            model = repositoryService.newModel();
            model.setCategory("Model category");
            model.setKey("Model key");
            model.setMetaInfo("Model metainfo");
            model.setName("Model name");
            model.setVersion(2);
            model.setDeploymentId(deploymentId);
            model.setTenantId("myTenant");
            repositoryService.saveModel(model);
            repositoryService.addModelEditorSource(model.getId(), "This is the editor source".getBytes());
            repositoryService.addModelEditorSourceExtra(model.getId(), "This is the extra editor source".getBytes());
            HttpGet httpGet = new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL, model.getId()))));
            CloseableHttpResponse response = executeRequest(httpGet, HttpStatus.SC_OK);
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            Assert.assertNotNull(responseNode);
            Assert.assertEquals("Model name", responseNode.get("name").textValue());
            Assert.assertEquals("Model key", responseNode.get("key").textValue());
            Assert.assertEquals("Model category", responseNode.get("category").textValue());
            Assert.assertEquals(2, responseNode.get("version").intValue());
            Assert.assertEquals("Model metainfo", responseNode.get("metaInfo").textValue());
            Assert.assertEquals(deploymentId, responseNode.get("deploymentId").textValue());
            Assert.assertEquals(model.getId(), responseNode.get("id").textValue());
            Assert.assertEquals("myTenant", responseNode.get("tenantId").textValue());
            Assert.assertEquals(now.getTime().getTime(), getDateFromISOString(responseNode.get("createTime").textValue()).getTime());
            Assert.assertEquals(now.getTime().getTime(), getDateFromISOString(responseNode.get("lastUpdateTime").textValue()).getTime());
            Assert.assertTrue(responseNode.get("url").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_MODEL, model.getId())));
            Assert.assertTrue(responseNode.get("deploymentUrl").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_DEPLOYMENT, deploymentId)));
            Assert.assertTrue(responseNode.get("sourceUrl").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_MODEL_SOURCE, model.getId())));
            Assert.assertTrue(responseNode.get("sourceExtraUrl").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_MODEL_SOURCE_EXTRA, model.getId())));
        } finally {
            try {
                repositoryService.deleteModel(model.getId());
            } catch (Throwable ignore) {
                // Ignore, model might not be created
            }
        }
    }

    @Test
    public void testGetUnexistingModel() throws Exception {
        HttpGet httpGet = new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL, "unexisting"))));
        closeResponse(executeRequest(httpGet, HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void testDeleteModel() throws Exception {
        Model model = null;
        try {
            Calendar now = Calendar.getInstance();
            now.set(Calendar.MILLISECOND, 0);
            processEngineConfiguration.getClock().setCurrentTime(now.getTime());
            model = repositoryService.newModel();
            model.setCategory("Model category");
            model.setKey("Model key");
            model.setMetaInfo("Model metainfo");
            model.setName("Model name");
            model.setVersion(2);
            repositoryService.saveModel(model);
            HttpDelete httpDelete = new HttpDelete(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL, model.getId()))));
            closeResponse(executeRequest(httpDelete, HttpStatus.SC_NO_CONTENT));
            // Check if the model is really gone
            Assert.assertNull(repositoryService.createModelQuery().modelId(model.getId()).singleResult());
            model = null;
        } finally {
            if (model != null) {
                try {
                    repositoryService.deleteModel(model.getId());
                } catch (Throwable ignore) {
                    // Ignore, model might not be created
                }
            }
        }
    }

    @Test
    public void testDeleteUnexistingModel() throws Exception {
        HttpDelete httpDelete = new HttpDelete(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL, "unexisting"))));
        closeResponse(executeRequest(httpDelete, HttpStatus.SC_NOT_FOUND));
    }

    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/repository/oneTaskProcess.bpmn20.xml" })
    public void testUpdateModel() throws Exception {
        Model model = null;
        try {
            Calendar createTime = Calendar.getInstance();
            createTime.set(Calendar.MILLISECOND, 0);
            processEngineConfiguration.getClock().setCurrentTime(createTime.getTime());
            model = repositoryService.newModel();
            model.setCategory("Model category");
            model.setKey("Model key");
            model.setMetaInfo("Model metainfo");
            model.setName("Model name");
            model.setVersion(2);
            repositoryService.saveModel(model);
            Calendar updateTime = Calendar.getInstance();
            updateTime.set(Calendar.MILLISECOND, 0);
            updateTime.add(Calendar.HOUR, 1);
            processEngineConfiguration.getClock().setCurrentTime(updateTime.getTime());
            // Create update request
            ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.put("name", "Updated name");
            requestNode.put("category", "Updated category");
            requestNode.put("key", "Updated key");
            requestNode.put("metaInfo", "Updated metainfo");
            requestNode.put("deploymentId", deploymentId);
            requestNode.put("version", 3);
            requestNode.put("tenantId", "myTenant");
            HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL, model.getId()))));
            httpPut.setEntity(new StringEntity(requestNode.toString()));
            CloseableHttpResponse response = executeRequest(httpPut, HttpStatus.SC_OK);
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            Assert.assertNotNull(responseNode);
            Assert.assertEquals("Updated name", responseNode.get("name").textValue());
            Assert.assertEquals("Updated key", responseNode.get("key").textValue());
            Assert.assertEquals("Updated category", responseNode.get("category").textValue());
            Assert.assertEquals(3, responseNode.get("version").intValue());
            Assert.assertEquals("Updated metainfo", responseNode.get("metaInfo").textValue());
            Assert.assertEquals(deploymentId, responseNode.get("deploymentId").textValue());
            Assert.assertEquals(model.getId(), responseNode.get("id").textValue());
            Assert.assertEquals("myTenant", responseNode.get("tenantId").textValue());
            Assert.assertEquals(createTime.getTime().getTime(), getDateFromISOString(responseNode.get("createTime").textValue()).getTime());
            Assert.assertEquals(updateTime.getTime().getTime(), getDateFromISOString(responseNode.get("lastUpdateTime").textValue()).getTime());
            Assert.assertTrue(responseNode.get("url").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_MODEL, model.getId())));
            Assert.assertTrue(responseNode.get("deploymentUrl").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_DEPLOYMENT, deploymentId)));
        } finally {
            try {
                repositoryService.deleteModel(model.getId());
            } catch (Throwable ignore) {
                // Ignore, model might not be created
            }
        }
    }

    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/repository/oneTaskProcess.bpmn20.xml" })
    public void testUpdateModelOverrideWithNull() throws Exception {
        Model model = null;
        try {
            Calendar createTime = Calendar.getInstance();
            createTime.set(Calendar.MILLISECOND, 0);
            processEngineConfiguration.getClock().setCurrentTime(createTime.getTime());
            model = repositoryService.newModel();
            model.setCategory("Model category");
            model.setKey("Model key");
            model.setMetaInfo("Model metainfo");
            model.setName("Model name");
            model.setTenantId("myTenant");
            model.setVersion(2);
            repositoryService.saveModel(model);
            Calendar updateTime = Calendar.getInstance();
            updateTime.set(Calendar.MILLISECOND, 0);
            processEngineConfiguration.getClock().setCurrentTime(updateTime.getTime());
            // Create update request
            ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.put("name", ((String) (null)));
            requestNode.put("category", ((String) (null)));
            requestNode.put("key", ((String) (null)));
            requestNode.put("metaInfo", ((String) (null)));
            requestNode.put("deploymentId", ((String) (null)));
            requestNode.put("version", ((String) (null)));
            requestNode.put("tenantId", ((String) (null)));
            HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL, model.getId()))));
            httpPut.setEntity(new StringEntity(requestNode.toString()));
            CloseableHttpResponse response = executeRequest(httpPut, HttpStatus.SC_OK);
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            Assert.assertNotNull(responseNode);
            Assert.assertNull(responseNode.get("name").textValue());
            Assert.assertNull(responseNode.get("key").textValue());
            Assert.assertNull(responseNode.get("category").textValue());
            Assert.assertNull(responseNode.get("version").textValue());
            Assert.assertNull(responseNode.get("metaInfo").textValue());
            Assert.assertNull(responseNode.get("deploymentId").textValue());
            Assert.assertNull(responseNode.get("tenantId").textValue());
            Assert.assertEquals(model.getId(), responseNode.get("id").textValue());
            Assert.assertEquals(createTime.getTime().getTime(), getDateFromISOString(responseNode.get("createTime").textValue()).getTime());
            Assert.assertEquals(updateTime.getTime().getTime(), getDateFromISOString(responseNode.get("lastUpdateTime").textValue()).getTime());
            Assert.assertTrue(responseNode.get("url").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_MODEL, model.getId())));
            model = repositoryService.getModel(model.getId());
            Assert.assertNull(model.getName());
            Assert.assertNull(model.getKey());
            Assert.assertNull(model.getCategory());
            Assert.assertNull(model.getMetaInfo());
            Assert.assertNull(model.getDeploymentId());
            Assert.assertEquals("", model.getTenantId());
        } finally {
            try {
                repositoryService.deleteModel(model.getId());
            } catch (Throwable ignore) {
                // Ignore, model might not be created
            }
        }
    }

    @Test
    @Deployment(resources = { "org/flowable/rest/service/api/repository/oneTaskProcess.bpmn20.xml" })
    public void testUpdateModelNoFields() throws Exception {
        Model model = null;
        try {
            Calendar now = Calendar.getInstance();
            now.set(Calendar.MILLISECOND, 0);
            processEngineConfiguration.getClock().setCurrentTime(now.getTime());
            model = repositoryService.newModel();
            model.setCategory("Model category");
            model.setKey("Model key");
            model.setMetaInfo("Model metainfo");
            model.setName("Model name");
            model.setVersion(2);
            model.setDeploymentId(deploymentId);
            repositoryService.saveModel(model);
            // Use empty request-node, nothing should be changed after update
            ObjectNode requestNode = objectMapper.createObjectNode();
            HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL, model.getId()))));
            httpPut.setEntity(new StringEntity(requestNode.toString()));
            CloseableHttpResponse response = executeRequest(httpPut, HttpStatus.SC_OK);
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            Assert.assertNotNull(responseNode);
            Assert.assertEquals("Model name", responseNode.get("name").textValue());
            Assert.assertEquals("Model key", responseNode.get("key").textValue());
            Assert.assertEquals("Model category", responseNode.get("category").textValue());
            Assert.assertEquals(2, responseNode.get("version").intValue());
            Assert.assertEquals("Model metainfo", responseNode.get("metaInfo").textValue());
            Assert.assertEquals(deploymentId, responseNode.get("deploymentId").textValue());
            Assert.assertEquals(model.getId(), responseNode.get("id").textValue());
            Assert.assertEquals(now.getTime().getTime(), getDateFromISOString(responseNode.get("createTime").textValue()).getTime());
            Assert.assertEquals(now.getTime().getTime(), getDateFromISOString(responseNode.get("lastUpdateTime").textValue()).getTime());
            Assert.assertTrue(responseNode.get("url").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_MODEL, model.getId())));
            Assert.assertTrue(responseNode.get("deploymentUrl").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_DEPLOYMENT, deploymentId)));
        } finally {
            try {
                repositoryService.deleteModel(model.getId());
            } catch (Throwable ignore) {
                // Ignore, model might not be created
            }
        }
    }

    @Test
    public void testUpdateUnexistingModel() throws Exception {
        HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_MODEL, "unexisting"))));
        httpPut.setEntity(new StringEntity(objectMapper.createObjectNode().toString()));
        closeResponse(executeRequest(httpPut, HttpStatus.SC_NOT_FOUND));
    }
}

