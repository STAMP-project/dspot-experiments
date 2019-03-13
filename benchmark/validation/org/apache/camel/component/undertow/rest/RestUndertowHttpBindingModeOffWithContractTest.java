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
package org.apache.camel.component.undertow.rest;


import Exchange.CONTENT_TYPE;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.undertow.BaseUndertowTest;
import org.junit.Test;


public class RestUndertowHttpBindingModeOffWithContractTest extends BaseUndertowTest {
    @Test
    public void testBindingModeOffWithContract() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:input");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(UserPojoEx.class);
        String body = "{\"id\": 123, \"name\": \"Donald Duck\"}";
        Object answer = template.requestBodyAndHeader("undertow:http://localhost:{{port}}/users/new", body, CONTENT_TYPE, "application/json");
        assertNotNull(answer);
        String answerString = new String(((byte[]) (answer)));
        assertTrue(("Unexpected response: " + answerString), answerString.contains("\"active\":true"));
        assertMockEndpointsSatisfied();
        Object obj = mock.getReceivedExchanges().get(0).getIn().getBody();
        assertEquals(UserPojoEx.class, obj.getClass());
        UserPojoEx user = ((UserPojoEx) (obj));
        assertNotNull(user);
        assertEquals(123, user.getId());
        assertEquals("Donald Duck", user.getName());
        assertEquals(true, user.isActive());
    }
}

