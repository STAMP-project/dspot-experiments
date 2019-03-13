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
package org.apache.camel.component.servlet.rest;


import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletUnitClient;
import org.apache.camel.component.servlet.ServletCamelRouterTestSupport;
import org.junit.Test;


public class RestServletGetWildcardsTest extends ServletCamelRouterTestSupport {
    @Test
    public void testServletProducerGet() throws Exception {
        WebRequest req = new GetMethodWebRequest(((ServletCamelRouterTestSupport.CONTEXT_URL) + "/services/users/123/basic"));
        ServletUnitClient client = newClient();
        client.setExceptionsThrownOnErrorStatus(false);
        WebResponse response = client.getResponse(req);
        assertEquals(200, response.getResponseCode());
        assertEquals("123;Donald Duck", response.getText());
    }

    @Test
    public void testServletProducerGetWildcards() throws Exception {
        WebRequest req = new GetMethodWebRequest(((ServletCamelRouterTestSupport.CONTEXT_URL) + "/services/users/456/name=g*"));
        ServletUnitClient client = newClient();
        client.setExceptionsThrownOnErrorStatus(false);
        WebResponse response = client.getResponse(req);
        assertEquals(200, response.getResponseCode());
        assertEquals("456;Goofy", response.getText());
    }
}

