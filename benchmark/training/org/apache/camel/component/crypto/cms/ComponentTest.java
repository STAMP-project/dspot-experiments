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
package org.apache.camel.component.crypto.cms;


import java.io.InputStream;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class ComponentTest extends CamelTestSupport {
    private SimpleRegistry simpleReg;

    @Test
    public void execute() throws Exception {
        String message = "Testmessage";
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived(message);
        sendBody("direct:start", message.getBytes("UTF-8"));
        assertMockEndpointsSatisfied();
    }

    @Test
    public void signedWithOutdatedCert() throws Exception {
        String message = "Testmessage";
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);
        MockEndpoint mockException = getMockEndpoint("mock:exception");
        mockException.expectedMessageCount(1);
        sendBody("direct:outdated", message.getBytes("UTF-8"));
        assertMockEndpointsSatisfied();
    }

    @Test
    public void decryptAndVerify() throws Exception {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("signed_enveloped_other_CMS_vendor.binary");
        assertNotNull(input);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Testmessage");
        sendBody("direct:decryptAndVerify", input);
        assertMockEndpointsSatisfied();
        input.close();
    }

    @Test
    public void orignatorUnprotectedAttributes() throws Exception {
        String message = "Testmessage";
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived(message);
        sendBody("direct:encryptDecryptOriginatorAttributes", message.getBytes("UTF-8"));
        assertMockEndpointsSatisfied();
    }

    @Test(expected = IllegalStateException.class)
    public void wrongOperation() throws Exception {
        CryptoCmsComponent c = new CryptoCmsComponent(new DefaultCamelContext());
        c.createEndpoint("uri", "wrongoperation", null);
    }
}

