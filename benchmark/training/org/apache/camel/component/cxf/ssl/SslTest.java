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
package org.apache.camel.component.cxf.ssl;


import org.apache.camel.Exchange;
import org.apache.camel.component.cxf.CXFTestSupport;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.hamcrest.core.Is;
import org.junit.Test;


public class SslTest extends CamelSpringTestSupport {
    protected static final String GREET_ME_OPERATION = "greetMe";

    protected static final String TEST_MESSAGE = "Hello World!";

    protected static final String JAXWS_SERVER_ADDRESS = ("https://localhost:" + (CXFTestSupport.getPort1())) + "/CxfSslTest/SoapContext/SoapPort";

    @Test
    public void testInvokingTrustRoute() throws Exception {
        Exchange reply = sendJaxWsMessage("direct:trust");
        assertFalse("We expect no exception here", reply.isFailed());
    }

    @Test
    public void testInvokingNoTrustRoute() throws Exception {
        Exchange reply = sendJaxWsMessage("direct:noTrust");
        assertTrue("We expect the exception here", reply.isFailed());
        Throwable e = reply.getException().getCause();
        assertThat(e.getClass().getCanonicalName(), Is.is("javax.net.ssl.SSLHandshakeException"));
    }

    @Test
    public void testInvokingWrongTrustRoute() throws Exception {
        Exchange reply = sendJaxWsMessage("direct:wrongTrust");
        assertTrue("We expect the exception here", reply.isFailed());
        Throwable e = reply.getException().getCause();
        assertThat(e.getClass().getCanonicalName(), Is.is("javax.net.ssl.SSLHandshakeException"));
    }
}

