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
package org.flowable.rest.service.api.identity;


import RestUrls.URL_USER;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.flowable.idm.api.User;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.rest.service.api.RestUrls;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Frederik Heremans
 */
public class UserResourceTest extends BaseSpringRestTestCase {
    /**
     * Test getting a single user.
     */
    @Test
    public void testGetUser() throws Exception {
        User savedUser = null;
        try {
            User newUser = identityService.newUser("testuser");
            newUser.setFirstName("Fred");
            newUser.setLastName("McDonald");
            newUser.setDisplayName("Fred McDonald");
            newUser.setEmail("no-reply@activiti.org");
            identityService.saveUser(newUser);
            savedUser = newUser;
            CloseableHttpResponse response = executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_USER, newUser.getId())))), HttpStatus.SC_OK);
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            Assert.assertNotNull(responseNode);
            Assert.assertEquals("testuser", responseNode.get("id").textValue());
            Assert.assertEquals("Fred", responseNode.get("firstName").textValue());
            Assert.assertEquals("McDonald", responseNode.get("lastName").textValue());
            Assert.assertEquals("Fred McDonald", responseNode.get("displayName").textValue());
            Assert.assertEquals("no-reply@activiti.org", responseNode.get("email").textValue());
            Assert.assertTrue(responseNode.get("url").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_USER, newUser.getId())));
        } finally {
            // Delete user after test passes or fails
            if (savedUser != null) {
                identityService.deleteUser(savedUser.getId());
            }
        }
    }

    /**
     * Test getting an unexisting user.
     */
    @Test
    public void testGetUnexistingUser() throws Exception {
        closeResponse(executeRequest(new HttpGet(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_USER, "unexisting")))), HttpStatus.SC_NOT_FOUND));
    }

    /**
     * Test deleting a single user.
     */
    @Test
    public void testDeleteUser() throws Exception {
        User savedUser = null;
        try {
            User newUser = identityService.newUser("testuser");
            newUser.setFirstName("Fred");
            newUser.setLastName("McDonald");
            newUser.setEmail("no-reply@activiti.org");
            identityService.saveUser(newUser);
            savedUser = newUser;
            closeResponse(executeRequest(new HttpDelete(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_USER, newUser.getId())))), HttpStatus.SC_NO_CONTENT));
            // Check if user is deleted
            Assert.assertEquals(0, identityService.createUserQuery().userId(newUser.getId()).count());
            savedUser = null;
        } finally {
            // Delete user after test fails
            if (savedUser != null) {
                identityService.deleteUser(savedUser.getId());
            }
        }
    }

    /**
     * Test deleting an unexisting user.
     */
    @Test
    public void testDeleteUnexistingUser() throws Exception {
        closeResponse(executeRequest(new HttpDelete(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_USER, "unexisting")))), HttpStatus.SC_NOT_FOUND));
    }

    /**
     * Test updating a single user.
     */
    @Test
    public void testUpdateUser() throws Exception {
        User savedUser = null;
        try {
            User newUser = identityService.newUser("testuser");
            newUser.setFirstName("Fred");
            newUser.setLastName("McDonald");
            newUser.setEmail("no-reply@activiti.org");
            identityService.saveUser(newUser);
            savedUser = newUser;
            ObjectNode taskUpdateRequest = objectMapper.createObjectNode();
            taskUpdateRequest.put("firstName", "Tijs");
            taskUpdateRequest.put("lastName", "Barrez");
            taskUpdateRequest.put("displayName", "Tijs Barrez");
            taskUpdateRequest.put("email", "no-reply@alfresco.org");
            taskUpdateRequest.put("password", "updatedpassword");
            HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_USER, newUser.getId()))));
            httpPut.setEntity(new StringEntity(taskUpdateRequest.toString()));
            CloseableHttpResponse response = executeRequest(httpPut, HttpStatus.SC_OK);
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            Assert.assertNotNull(responseNode);
            Assert.assertEquals("testuser", responseNode.get("id").textValue());
            Assert.assertEquals("Tijs", responseNode.get("firstName").textValue());
            Assert.assertEquals("Barrez", responseNode.get("lastName").textValue());
            Assert.assertEquals("Tijs Barrez", responseNode.get("displayName").textValue());
            Assert.assertEquals("no-reply@alfresco.org", responseNode.get("email").textValue());
            Assert.assertTrue(responseNode.get("url").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_USER, newUser.getId())));
            // Check user is updated in activiti
            newUser = identityService.createUserQuery().userId(newUser.getId()).singleResult();
            Assert.assertEquals("Barrez", newUser.getLastName());
            Assert.assertEquals("Tijs", newUser.getFirstName());
            Assert.assertEquals("Tijs Barrez", newUser.getDisplayName());
            Assert.assertEquals("no-reply@alfresco.org", newUser.getEmail());
            Assert.assertEquals("updatedpassword", newUser.getPassword());
        } finally {
            // Delete user after test fails
            if (savedUser != null) {
                identityService.deleteUser(savedUser.getId());
            }
        }
    }

    /**
     * Test updating a single user passing in no fields in the json, user should remain unchanged.
     */
    @Test
    public void testUpdateUserNoFields() throws Exception {
        User savedUser = null;
        try {
            User newUser = identityService.newUser("testuser");
            newUser.setFirstName("Fred");
            newUser.setLastName("McDonald");
            newUser.setDisplayName("Fred McDonald");
            newUser.setEmail("no-reply@activiti.org");
            identityService.saveUser(newUser);
            savedUser = newUser;
            ObjectNode taskUpdateRequest = objectMapper.createObjectNode();
            HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_USER, newUser.getId()))));
            httpPut.setEntity(new StringEntity(taskUpdateRequest.toString()));
            CloseableHttpResponse response = executeRequest(httpPut, HttpStatus.SC_OK);
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            Assert.assertNotNull(responseNode);
            Assert.assertEquals("testuser", responseNode.get("id").textValue());
            Assert.assertEquals("Fred", responseNode.get("firstName").textValue());
            Assert.assertEquals("McDonald", responseNode.get("lastName").textValue());
            Assert.assertEquals("Fred McDonald", responseNode.get("displayName").textValue());
            Assert.assertEquals("no-reply@activiti.org", responseNode.get("email").textValue());
            Assert.assertTrue(responseNode.get("url").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_USER, newUser.getId())));
            // Check user is updated in activiti
            newUser = identityService.createUserQuery().userId(newUser.getId()).singleResult();
            Assert.assertEquals("McDonald", newUser.getLastName());
            Assert.assertEquals("Fred", newUser.getFirstName());
            Assert.assertEquals("Fred McDonald", newUser.getDisplayName());
            Assert.assertEquals("no-reply@activiti.org", newUser.getEmail());
            Assert.assertNull(newUser.getPassword());
        } finally {
            // Delete user after test fails
            if (savedUser != null) {
                identityService.deleteUser(savedUser.getId());
            }
        }
    }

    /**
     * Test updating a single user passing in no fields in the json, user should remain unchanged.
     */
    @Test
    public void testUpdateUserNullFields() throws Exception {
        User savedUser = null;
        try {
            User newUser = identityService.newUser("testuser");
            newUser.setFirstName("Fred");
            newUser.setLastName("McDonald");
            newUser.setDisplayName("Fred McDonald");
            newUser.setEmail("no-reply@activiti.org");
            identityService.saveUser(newUser);
            savedUser = newUser;
            ObjectNode taskUpdateRequest = objectMapper.createObjectNode();
            taskUpdateRequest.putNull("firstName");
            taskUpdateRequest.putNull("lastName");
            taskUpdateRequest.putNull("displayName");
            taskUpdateRequest.putNull("email");
            taskUpdateRequest.putNull("password");
            HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_USER, newUser.getId()))));
            httpPut.setEntity(new StringEntity(taskUpdateRequest.toString()));
            CloseableHttpResponse response = executeRequest(httpPut, HttpStatus.SC_OK);
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            Assert.assertNotNull(responseNode);
            Assert.assertEquals("testuser", responseNode.get("id").textValue());
            Assert.assertTrue(responseNode.get("firstName").isNull());
            Assert.assertTrue(responseNode.get("lastName").isNull());
            Assert.assertTrue(responseNode.get("displayName").isNull());
            Assert.assertTrue(responseNode.get("email").isNull());
            Assert.assertTrue(responseNode.get("url").textValue().endsWith(RestUrls.createRelativeResourceUrl(URL_USER, newUser.getId())));
            // Check user is updated in activiti
            newUser = identityService.createUserQuery().userId(newUser.getId()).singleResult();
            Assert.assertNull(newUser.getLastName());
            Assert.assertNull(newUser.getFirstName());
            Assert.assertNull(newUser.getDisplayName());
            Assert.assertNull(newUser.getEmail());
        } finally {
            // Delete user after test fails
            if (savedUser != null) {
                identityService.deleteUser(savedUser.getId());
            }
        }
    }

    /**
     * Test updating an unexisting user.
     */
    @Test
    public void testUpdateUnexistingUser() throws Exception {
        HttpPut httpPut = new HttpPut(((BaseSpringRestTestCase.SERVER_URL_PREFIX) + (RestUrls.createRelativeResourceUrl(URL_USER, "unexisting"))));
        httpPut.setEntity(new StringEntity(objectMapper.createObjectNode().toString()));
        closeResponse(executeRequest(httpPut, HttpStatus.SC_NOT_FOUND));
    }
}

