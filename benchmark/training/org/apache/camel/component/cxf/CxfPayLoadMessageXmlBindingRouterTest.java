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
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;


public class CxfPayLoadMessageXmlBindingRouterTest extends CamelTestSupport {
    protected static final String ROUTER_ADDRESS = ("http://localhost:" + (CXFTestSupport.getPort1())) + "/CxfPayLoadMessageXmlBindingRouterTest/router";

    protected static final String SERVICE_ADDRESS = ("http://localhost:" + (CXFTestSupport.getPort2())) + "/CxfPayLoadMessageXmlBindingRouterTest/helloworld";

    protected AbstractXmlApplicationContext applicationContext;

    @Test
    public void testInvokingServiceFromCXFClient() throws Exception {
        HelloService client = getCXFClient();
        String result = client.echo("hello world");
        assertEquals("we should get the right answer from router", result, "echo hello world");
        int count = client.getInvocationCount();
        client.ping();
        // oneway ping invoked, so invocationCount ++
        assertEquals("The ping should be invocated", client.getInvocationCount(), (++count));
    }
}

