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


import org.apache.camel.CamelExecutionException;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.junit.Test;


public class RestRestletPostJsonPojoOnExceptionTest extends RestletTestSupport {
    @Test
    public void testRestletPostPojoError() throws Exception {
        getMockEndpoint("mock:input").expectedMessageCount(0);
        getMockEndpoint("mock:error").expectedMessageCount(1);
        String body = "This is not json";
        try {
            template.sendBody((("http://localhost:" + (RestletTestSupport.portNum)) + "/users/new"), body);
            fail("Should have thrown an exception");
        } catch (CamelExecutionException e) {
            HttpOperationFailedException cause = assertIsInstanceOf(HttpOperationFailedException.class, e.getCause());
            assertEquals(400, cause.getStatusCode());
            assertEquals("Invalid json data", cause.getResponseBody());
        }
        assertMockEndpointsSatisfied();
    }
}

