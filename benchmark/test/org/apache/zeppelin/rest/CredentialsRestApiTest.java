/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.rest;


import Status.BAD_REQUEST;
import com.google.gson.Gson;
import java.io.IOException;
import javax.ws.rs.core.Response;
import org.apache.zeppelin.service.SecurityService;
import org.apache.zeppelin.user.Credentials;
import org.junit.Assert;
import org.junit.Test;


public class CredentialsRestApiTest {
    private final Gson gson = new Gson();

    private CredentialRestApi credentialRestApi;

    private Credentials credentials;

    private SecurityService securityService;

    @Test
    public void testInvalidRequest() throws IOException {
        String jsonInvalidRequestEntityNull = "{\"entity\" : null, \"username\" : \"test\", " + "\"password\" : \"testpass\"}";
        String jsonInvalidRequestNameNull = "{\"entity\" : \"test\", \"username\" : null, " + "\"password\" : \"testpass\"}";
        String jsonInvalidRequestPasswordNull = "{\"entity\" : \"test\", \"username\" : \"test\", " + "\"password\" : null}";
        String jsonInvalidRequestAllNull = "{\"entity\" : null, \"username\" : null, " + "\"password\" : null}";
        Response response = credentialRestApi.putCredentials(jsonInvalidRequestEntityNull);
        Assert.assertEquals(BAD_REQUEST, response.getStatusInfo().toEnum());
        response = credentialRestApi.putCredentials(jsonInvalidRequestNameNull);
        Assert.assertEquals(BAD_REQUEST, response.getStatusInfo().toEnum());
        response = credentialRestApi.putCredentials(jsonInvalidRequestPasswordNull);
        Assert.assertEquals(BAD_REQUEST, response.getStatusInfo().toEnum());
        response = credentialRestApi.putCredentials(jsonInvalidRequestAllNull);
        Assert.assertEquals(BAD_REQUEST, response.getStatusInfo().toEnum());
    }

    @Test
    public void testCredentialsAPIs() throws IOException {
        String requestData1 = "{\"entity\" : \"entityname\", \"username\" : \"myuser\", \"password\" " + ": \"mypass\"}";
        String entity = "entityname";
        credentialRestApi.putCredentials(requestData1);
        Assert.assertNotNull("CredentialMap should be null", testGetUserCredentials());
        credentialRestApi.removeCredentialEntity(entity);
        Assert.assertNull("CredentialMap should be null", testGetUserCredentials().get("entity1"));
        credentialRestApi.removeCredentials();
        Assert.assertEquals("Compare CredentialMap", testGetUserCredentials().toString(), "{}");
    }
}

