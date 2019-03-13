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
package org.apache.camel.itest.shiro;


import ShiroSecurityConstants.SHIRO_SECURITY_PASSWORD;
import ShiroSecurityConstants.SHIRO_SECURITY_USERNAME;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class ShiroOverJmsTest extends CamelTestSupport {
    private byte[] passPhrase = new byte[]{ ((byte) (8)), ((byte) (9)), ((byte) (10)), ((byte) (11)), ((byte) (12)), ((byte) (13)), ((byte) (14)), ((byte) (15)), ((byte) (16)), ((byte) (17)), ((byte) (18)), ((byte) (19)), ((byte) (20)), ((byte) (21)), ((byte) (22)), ((byte) (23)) };

    @Test
    public void testShiroOverJms() throws Exception {
        getMockEndpoint("mock:error").expectedMessageCount(0);
        getMockEndpoint("mock:foo").expectedBodiesReceived("Hello World");
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye World");
        Map<String, Object> headers = new HashMap<>();
        headers.put(SHIRO_SECURITY_USERNAME, "ringo");
        headers.put(SHIRO_SECURITY_PASSWORD, "starr");
        template.requestBodyAndHeaders("direct:start", "Hello World", headers);
        assertMockEndpointsSatisfied();
    }
}

