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
package org.flowable.cmmn.rest.service.api.repository;


import CmmnRestUrls.URL_CASE_DEFINITION_IDENTITYLINK;
import CmmnRestUrls.URL_CASE_DEFINITION_IDENTITYLINKS_COLLECTION;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import junit.framework.TestCase;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.rest.service.BaseSpringRestTestCase;
import org.flowable.cmmn.rest.service.api.CmmnRestUrls;
import org.flowable.identitylink.api.IdentityLink;
import org.junit.Test;


/**
 * Test for all REST-operations related to single a Case Definition resource.
 *
 * @author Tijs Rademakers
 */
public class CaseDefinitionIdentityLinksResourceTest extends BaseSpringRestTestCase {
    /**
     * Test getting identitylinks for a case definition.
     */
    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/rest/service/api/repository/oneHumanTaskCase.cmmn" })
    public void testGetIdentityLinksForProcessDefinition() throws Exception {
        CaseDefinition caseDefinition = BaseSpringRestTestCase.repositoryService.createCaseDefinitionQuery().singleResult();
        BaseSpringRestTestCase.repositoryService.addCandidateStarterGroup(caseDefinition.getId(), "admin");
        BaseSpringRestTestCase.repositoryService.addCandidateStarterUser(caseDefinition.getId(), "kermit");
        HttpGet httpGet = new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINKS_COLLECTION, caseDefinition.getId()))));
        CloseableHttpResponse response = executeRequest(httpGet, HttpStatus.SC_OK);
        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        TestCase.assertNotNull(responseNode);
        TestCase.assertTrue(responseNode.isArray());
        TestCase.assertEquals(2, responseNode.size());
        boolean groupCandidateFound = false;
        boolean userCandidateFound = false;
        for (int i = 0; i < (responseNode.size()); i++) {
            ObjectNode link = ((ObjectNode) (responseNode.get(i)));
            TestCase.assertNotNull(link);
            if (!(link.get("user").isNull())) {
                TestCase.assertEquals("kermit", link.get("user").textValue());
                TestCase.assertEquals("candidate", link.get("type").textValue());
                TestCase.assertTrue(link.get("group").isNull());
                TestCase.assertTrue(link.get("url").asText().endsWith(CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINK, caseDefinition.getId(), "users", "kermit")));
                userCandidateFound = true;
            } else
                if (!(link.get("group").isNull())) {
                    TestCase.assertEquals("admin", link.get("group").textValue());
                    TestCase.assertEquals("candidate", link.get("type").textValue());
                    TestCase.assertTrue(link.get("user").isNull());
                    TestCase.assertTrue(link.get("url").asText().endsWith(CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINK, caseDefinition.getId(), "groups", "admin")));
                    groupCandidateFound = true;
                }

        }
        TestCase.assertTrue(groupCandidateFound);
        TestCase.assertTrue(userCandidateFound);
    }

    @Test
    public void testGetIdentityLinksForUnexistingCaseDefinition() throws Exception {
        HttpGet httpGet = new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINKS_COLLECTION, "unexisting"))));
        CloseableHttpResponse response = executeRequest(httpGet, HttpStatus.SC_NOT_FOUND);
        closeResponse(response);
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/rest/service/api/repository/oneHumanTaskCase.cmmn" })
    public void testAddCandidateStarterToCaseDefinition() throws Exception {
        CaseDefinition caseDefinition = BaseSpringRestTestCase.repositoryService.createCaseDefinitionQuery().singleResult();
        // Create user candidate
        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.put("user", "kermit");
        HttpPost httpPost = new HttpPost(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINKS_COLLECTION, caseDefinition.getId()))));
        httpPost.setEntity(new StringEntity(requestNode.toString()));
        CloseableHttpResponse response = executeRequest(httpPost, HttpStatus.SC_CREATED);
        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        TestCase.assertNotNull(responseNode);
        TestCase.assertEquals("kermit", responseNode.get("user").textValue());
        TestCase.assertEquals("candidate", responseNode.get("type").textValue());
        TestCase.assertTrue(responseNode.get("group").isNull());
        TestCase.assertTrue(responseNode.get("url").asText().endsWith(CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINK, caseDefinition.getId(), "users", "kermit")));
        List<IdentityLink> createdLinks = BaseSpringRestTestCase.repositoryService.getIdentityLinksForCaseDefinition(caseDefinition.getId());
        TestCase.assertEquals(1, createdLinks.size());
        TestCase.assertEquals("kermit", createdLinks.get(0).getUserId());
        TestCase.assertEquals("candidate", createdLinks.get(0).getType());
        BaseSpringRestTestCase.repositoryService.deleteCandidateStarterUser(caseDefinition.getId(), "kermit");
        // Create group candidate
        requestNode = objectMapper.createObjectNode();
        requestNode.put("group", "admin");
        httpPost.setEntity(new StringEntity(requestNode.toString()));
        response = executeRequest(httpPost, HttpStatus.SC_CREATED);
        responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        TestCase.assertNotNull(responseNode);
        TestCase.assertEquals("admin", responseNode.get("group").textValue());
        TestCase.assertEquals("candidate", responseNode.get("type").textValue());
        TestCase.assertTrue(responseNode.get("user").isNull());
        TestCase.assertTrue(responseNode.get("url").textValue().endsWith(CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINK, caseDefinition.getId(), "groups", "admin")));
        createdLinks = BaseSpringRestTestCase.repositoryService.getIdentityLinksForCaseDefinition(caseDefinition.getId());
        TestCase.assertEquals(1, createdLinks.size());
        TestCase.assertEquals("admin", createdLinks.get(0).getGroupId());
        TestCase.assertEquals("candidate", createdLinks.get(0).getType());
        BaseSpringRestTestCase.repositoryService.deleteCandidateStarterUser(caseDefinition.getId(), "admin");
    }

    @Test
    public void testAddCandidateStarterToUnexistingCaseDefinition() throws Exception {
        // Create user candidate
        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.put("user", "kermit");
        HttpPost httpPost = new HttpPost(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINKS_COLLECTION, "unexisting"))));
        httpPost.setEntity(new StringEntity(requestNode.toString()));
        CloseableHttpResponse response = executeRequest(httpPost, HttpStatus.SC_NOT_FOUND);
        closeResponse(response);
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/rest/service/api/repository/oneHumanTaskCaseWithStarters.cmmn" })
    public void testGetCandidateStarterFromCaseDefinition() throws Exception {
        CaseDefinition caseDefinition = BaseSpringRestTestCase.repositoryService.createCaseDefinitionQuery().singleResult();
        // Get user candidate
        HttpGet httpGet = new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINK, caseDefinition.getId(), "users", "kermit"))));
        CloseableHttpResponse response = executeRequest(httpGet, HttpStatus.SC_OK);
        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        TestCase.assertNotNull(responseNode);
        TestCase.assertEquals("kermit", responseNode.get("user").textValue());
        TestCase.assertEquals("candidate", responseNode.get("type").textValue());
        TestCase.assertTrue(responseNode.get("group").isNull());
        TestCase.assertTrue(responseNode.get("url").asText().endsWith(CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINK, caseDefinition.getId(), "users", "kermit")));
        // Get group candidate
        httpGet = new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINK, caseDefinition.getId(), "groups", "admin"))));
        response = executeRequest(httpGet, HttpStatus.SC_OK);
        responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        TestCase.assertNotNull(responseNode);
        TestCase.assertEquals("admin", responseNode.get("group").textValue());
        TestCase.assertEquals("candidate", responseNode.get("type").textValue());
        TestCase.assertTrue(responseNode.get("user").isNull());
        TestCase.assertTrue(responseNode.get("url").asText().endsWith(CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINK, caseDefinition.getId(), "groups", "admin")));
    }

    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/rest/service/api/repository/oneHumanTaskCase.cmmn" })
    public void testDeleteCandidateStarterFromCaseDefinition() throws Exception {
        CaseDefinition caseDefinition = BaseSpringRestTestCase.repositoryService.createCaseDefinitionQuery().singleResult();
        BaseSpringRestTestCase.repositoryService.addCandidateStarterGroup(caseDefinition.getId(), "admin");
        BaseSpringRestTestCase.repositoryService.addCandidateStarterUser(caseDefinition.getId(), "kermit");
        // Delete user candidate
        HttpDelete httpDelete = new HttpDelete(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINK, caseDefinition.getId(), "users", "kermit"))));
        CloseableHttpResponse response = executeRequest(httpDelete, HttpStatus.SC_NO_CONTENT);
        closeResponse(response);
        // Check if group-link remains
        List<IdentityLink> remainingLinks = BaseSpringRestTestCase.repositoryService.getIdentityLinksForCaseDefinition(caseDefinition.getId());
        TestCase.assertEquals(1, remainingLinks.size());
        TestCase.assertEquals("admin", remainingLinks.get(0).getGroupId());
        // Delete group candidate
        httpDelete = new HttpDelete(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINK, caseDefinition.getId(), "groups", "admin"))));
        response = executeRequest(httpDelete, HttpStatus.SC_NO_CONTENT);
        closeResponse(response);
        // Check if all links are removed
        remainingLinks = BaseSpringRestTestCase.repositoryService.getIdentityLinksForCaseDefinition(caseDefinition.getId());
        TestCase.assertEquals(0, remainingLinks.size());
    }

    @Test
    public void testDeleteCandidateStarterFromUnexistingCaseDefinition() throws Exception {
        HttpDelete httpDelete = new HttpDelete(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINK, "unexisting", "groups", "admin"))));
        CloseableHttpResponse response = executeRequest(httpDelete, HttpStatus.SC_NOT_FOUND);
        closeResponse(response);
    }

    @Test
    public void testGetCandidateStarterFromUnexistingCaseDefinition() throws Exception {
        HttpGet httpGet = new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (CmmnRestUrls.createRelativeResourceUrl(URL_CASE_DEFINITION_IDENTITYLINK, "unexisting", "groups", "admin"))));
        CloseableHttpResponse response = executeRequest(httpGet, HttpStatus.SC_NOT_FOUND);
        closeResponse(response);
    }
}

