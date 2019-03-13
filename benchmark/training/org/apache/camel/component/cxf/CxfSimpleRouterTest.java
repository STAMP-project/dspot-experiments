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
package org.apache.camel.component.cxf;


import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.cxf.endpoint.Server;
import org.junit.Test;


public class CxfSimpleRouterTest extends CamelTestSupport {
    protected static final String SERVICE_CLASS = "serviceClass=org.apache.camel.component.cxf.HelloService";

    protected Server server;

    private String routerEndpointURI = ((("cxf://" + (getRouterAddress())) + "?") + (CxfSimpleRouterTest.SERVICE_CLASS)) + "&dataFormat=POJO";

    private String serviceEndpointURI = ((("cxf://" + (getServiceAddress())) + "?") + (CxfSimpleRouterTest.SERVICE_CLASS)) + "&dataFormat=POJO";

    @Test
    public void testInvokingServiceFromCXFClient() throws Exception {
        HelloService client = getCXFClient();
        String result = client.echo("hello world");
        assertEquals("we should get the right answer from router", result, "echo hello world");
    }

    @Test
    public void testOnwayInvocation() throws Exception {
        HelloService client = getCXFClient();
        int count = client.getInvocationCount();
        client.ping();
        // oneway ping invoked, so invocationCount ++
        assertEquals("The ping should be invocated", client.getInvocationCount(), (++count));
    }
}

