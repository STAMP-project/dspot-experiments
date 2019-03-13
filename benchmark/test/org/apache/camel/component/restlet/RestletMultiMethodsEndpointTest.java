/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.restlet;


import Status.CLIENT_ERROR_METHOD_NOT_ALLOWED;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.junit.Test;


/**
 * This unit test verifies a single route can service multiple methods.
 */
public class RestletMultiMethodsEndpointTest extends RestletTestSupport {
    @Test
    public void testPostMethod() throws Exception {
        HttpResponse response = doExecute(new HttpPost((("http://localhost:" + (RestletTestSupport.portNum)) + "/users/homer")));
        RestletTestSupport.assertHttpResponse(response, 200, "text/plain", "POST");
    }

    @Test
    public void testPutMethod() throws Exception {
        HttpResponse response = doExecute(new HttpPut((("http://localhost:" + (RestletTestSupport.portNum)) + "/users/homer")));
        RestletTestSupport.assertHttpResponse(response, 200, "text/plain", "PUT");
    }

    @Test
    public void testGetMethod() throws Exception {
        HttpResponse response = doExecute(new HttpGet((("http://localhost:" + (RestletTestSupport.portNum)) + "/users/homer")));
        RestletTestSupport.assertHttpResponse(response, 200, "text/plain", "GET");
    }

    @Test
    public void testDeleteMethod() throws Exception {
        HttpResponse response = doExecute(new HttpDelete((("http://localhost:" + (RestletTestSupport.portNum)) + "/users/homer")));
        // delete is not allowed so we return 405
        assertEquals(CLIENT_ERROR_METHOD_NOT_ALLOWED.getCode(), response.getStatusLine().getStatusCode());
        Header header = response.getFirstHeader("Allow");
        assertNotNull(header);
        String value = header.getValue().toUpperCase(Locale.US);
        assertTrue("POST should be allowed", value.contains("POST"));
        assertTrue("GET should be allowed", value.contains("GET"));
        assertTrue("PUT should be allowed", value.contains("PUT"));
    }
}

