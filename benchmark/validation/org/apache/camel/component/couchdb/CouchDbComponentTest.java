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
package org.apache.camel.component.couchdb;


import java.util.HashMap;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CouchDbComponentTest {
    @Mock
    private CamelContext context;

    @Test
    public void testEndpointCreated() throws Exception {
        Map<String, Object> params = new HashMap<>();
        String uri = "couchdb:http://localhost:5984/db";
        String remaining = "http://localhost:5984/db";
        Endpoint endpoint = createEndpoint(uri, remaining, params);
        Assert.assertNotNull(endpoint);
    }

    @Test
    public void testPropertiesSet() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("createDatabase", true);
        params.put("username", "coldplay");
        params.put("password", "chrism");
        params.put("heartbeat", 1000);
        params.put("style", "gothic");
        params.put("deletes", false);
        params.put("updates", false);
        String uri = "couchdb:http://localhost:14/db";
        String remaining = "http://localhost:14/db";
        CouchDbEndpoint endpoint = new CouchDbComponent(context).createEndpoint(uri, remaining, params);
        Assert.assertEquals("http", endpoint.getProtocol());
        Assert.assertEquals("localhost", endpoint.getHostname());
        Assert.assertEquals("db", endpoint.getDatabase());
        Assert.assertEquals("coldplay", endpoint.getUsername());
        Assert.assertEquals("gothic", endpoint.getStyle());
        Assert.assertEquals("chrism", endpoint.getPassword());
        Assert.assertTrue(endpoint.isCreateDatabase());
        Assert.assertFalse(endpoint.isDeletes());
        Assert.assertFalse(endpoint.isUpdates());
        Assert.assertEquals(14, endpoint.getPort());
        Assert.assertEquals(1000, endpoint.getHeartbeat());
    }
}

