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
package org.apache.camel.example.camel.transport;


import java.net.MalformedURLException;
import org.apache.hello_world_soap_http.Greeter;
import org.apache.hello_world_soap_http.PingMeFault;
import org.apache.hello_world_soap_http.types.FaultDetail;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;


public class CamelTransportClientServerTest extends Assert {
    static AbstractApplicationContext context;

    static int port;

    @Test
    public void testClientInvocation() throws MalformedURLException {
        Client client = new Client((("http://localhost:" + (CamelTransportClientServerTest.port)) + "/GreeterContext/GreeterPort"));
        Greeter port = client.getProxy();
        Assert.assertNotNull("The proxy should not be null", port);
        String resp = port.sayHi();
        Assert.assertEquals("Get a wrong response ", "Bonjour from EndpointA", resp);
        resp = port.sayHi();
        Assert.assertEquals("Get a wrong response ", "Bonjour from EndpointB", resp);
        resp = port.greetMe("Mike");
        Assert.assertEquals("Get a wrong response ", "Hello Mike from EndpointA", resp);
        resp = port.greetMe("James");
        Assert.assertEquals("Get a wrong response ", "Hello James from EndpointB", resp);
        port.greetMeOneWay(System.getProperty("user.name"));
        try {
            port.pingMe("hello");
            Assert.fail("exception expected but none thrown");
        } catch (PingMeFault ex) {
            Assert.assertEquals("Wrong exception message received", "PingMeFault raised by server EndpointB", ex.getMessage());
            FaultDetail detail = ex.getFaultInfo();
            Assert.assertEquals("Wrong FaultDetail major:", 2, detail.getMajor());
            Assert.assertEquals("Wrong FaultDetail minor:", 1, detail.getMinor());
        }
    }
}

