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
package org.apache.camel.itest.security;


import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.SOAPFaultException;
import org.apache.camel.CamelContext;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration(locations = { "camel-context.xml" })
public class GreeterClientTest extends AbstractJUnit4SpringContextTests {
    private static final URL WSDL_LOC;

    static {
        URL tmp = null;
        try {
            tmp = GreeterClientTest.class.getClassLoader().getResource("wsdl/hello_world.wsdl");
        } catch (final Exception e) {
            e.printStackTrace();
        }
        WSDL_LOC = tmp;
    }

    private static final QName SERVICE_QNAME = new QName("http://apache.org/hello_world_soap_http", "SOAPService");

    private static final QName PORT_QNAME = new QName("http://apache.org/hello_world_soap_http", "SoapOverHttp");

    @Autowired
    protected CamelContext camelContext;

    @Test
    public void testServiceWithValidateUser() throws Exception {
        String response = sendMessageWithUsernameToken("jim", "jimspassword", "CXF");
        Assert.assertEquals(" Hello CXF", response);
        try {
            sendMessageWithUsernameToken("jim", "foo", "CXF");
            Assert.fail("should fail");
        } catch (Exception ex) {
            String msg = ex.getMessage();
            Assert.assertTrue("Get a wrong type exception.", (ex instanceof SOAPFaultException));
            Assert.assertTrue(("Get a wrong exception message: " + msg), ((msg.startsWith("The security token could not be authenticated or authorized")) || (msg.startsWith("A security error was encountered when verifying the messag"))));
        }
    }

    @Test
    public void testServiceWithNotAuthorizedUser() throws Exception {
        try {
            // this user doesn't have the right to access the processor
            sendMessageWithUsernameToken("bob", "bobspassword", "CXF");
            Assert.fail("should fail");
        } catch (Exception ex) {
            Assert.assertTrue("Get a wrong type exception.", (ex instanceof SOAPFaultException));
            Assert.assertTrue("Get a wrong exception message", ex.getMessage().startsWith("Cannot access the processor which has been protected."));
            Assert.assertTrue("Get a wrong exception message", ex.getMessage().endsWith("Caused by: [org.springframework.security.access.AccessDeniedException - Access is denied]"));
        }
    }
}

