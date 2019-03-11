/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.integration.accesscontrol;


import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.apache.nifi.integration.util.NiFiTestServer;
import org.apache.nifi.integration.util.NiFiTestUser;
import org.apache.nifi.web.api.dto.AccessConfigurationDTO;
import org.apache.nifi.web.api.dto.AccessStatusDTO;
import org.apache.nifi.web.api.entity.AccessConfigurationEntity;
import org.apache.nifi.web.api.entity.AccessStatusEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * Access token endpoint test.
 */
public class ITAccessTokenEndpoint {
    private static final String CLIENT_ID = "token-endpoint-id";

    private static final String CONTEXT_PATH = "/nifi-api";

    private static String flowXmlPath;

    private static NiFiTestServer SERVER;

    private static NiFiTestUser TOKEN_USER;

    private static String BASE_URL;

    // -----------
    // LOGIN CONIG
    // -----------
    /**
     * Test getting access configuration.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testGetAccessConfig() throws Exception {
        String url = (ITAccessTokenEndpoint.BASE_URL) + "/access/config";
        Response response = ITAccessTokenEndpoint.TOKEN_USER.testGet(url);
        // ensure the request is successful
        Assert.assertEquals(200, response.getStatus());
        // extract the process group
        AccessConfigurationEntity accessConfigEntity = response.readEntity(AccessConfigurationEntity.class);
        // ensure there is content
        Assert.assertNotNull(accessConfigEntity);
        // extract the process group dto
        AccessConfigurationDTO accessConfig = accessConfigEntity.getConfig();
        // verify config
        Assert.assertTrue(accessConfig.getSupportsLogin());
    }

    /**
     * Obtains a token and creates a processor using it.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testCreateProcessorUsingToken() throws Exception {
        String url = (ITAccessTokenEndpoint.BASE_URL) + "/access/token";
        Response response = ITAccessTokenEndpoint.TOKEN_USER.testCreateToken(url, "user@nifi", "whatever");
        // ensure the request is successful
        Assert.assertEquals(201, response.getStatus());
        // get the token
        String token = response.readEntity(String.class);
        // attempt to create a processor with it
        createProcessor(token);
    }

    /**
     * Verifies the response when bad credentials are specified.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testInvalidCredentials() throws Exception {
        String url = (ITAccessTokenEndpoint.BASE_URL) + "/access/token";
        Response response = ITAccessTokenEndpoint.TOKEN_USER.testCreateToken(url, "user@nifi", "not a real password");
        // ensure the request is successful
        Assert.assertEquals(400, response.getStatus());
    }

    /**
     * Verifies the response when the user is known.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testUnknownUser() throws Exception {
        String url = (ITAccessTokenEndpoint.BASE_URL) + "/access/token";
        Response response = ITAccessTokenEndpoint.TOKEN_USER.testCreateToken(url, "not a real user", "not a real password");
        // ensure the request is successful
        Assert.assertEquals(400, response.getStatus());
    }

    /**
     * Request access using access token.
     *
     * @throws Exception
     * 		ex
     */
    @Test
    public void testRequestAccessUsingToken() throws Exception {
        String accessStatusUrl = (ITAccessTokenEndpoint.BASE_URL) + "/access";
        String accessTokenUrl = (ITAccessTokenEndpoint.BASE_URL) + "/access/token";
        Response response = ITAccessTokenEndpoint.TOKEN_USER.testGet(accessStatusUrl);
        // ensure the request is successful
        Assert.assertEquals(200, response.getStatus());
        AccessStatusEntity accessStatusEntity = response.readEntity(AccessStatusEntity.class);
        AccessStatusDTO accessStatus = accessStatusEntity.getAccessStatus();
        // verify unknown
        Assert.assertEquals("UNKNOWN", accessStatus.getStatus());
        response = ITAccessTokenEndpoint.TOKEN_USER.testCreateToken(accessTokenUrl, "unregistered-user@nifi", "password");
        // ensure the request is successful
        Assert.assertEquals(201, response.getStatus());
        // get the token
        String token = response.readEntity(String.class);
        // authorization header
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", ("Bearer " + token));
        // check the status with the token
        response = ITAccessTokenEndpoint.TOKEN_USER.testGetWithHeaders(accessStatusUrl, null, headers);
        // ensure the request is successful
        Assert.assertEquals(200, response.getStatus());
        accessStatusEntity = response.readEntity(AccessStatusEntity.class);
        accessStatus = accessStatusEntity.getAccessStatus();
        // verify unregistered
        Assert.assertEquals("ACTIVE", accessStatus.getStatus());
    }
}

