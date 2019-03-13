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


import java.io.IOException;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import org.junit.Test;
import org.springframework.context.support.AbstractXmlApplicationContext;


public class CxfPayloadRouterContentLengthTest extends CamelTestSupport {
    /* The response message is generated directly. The issue here is that the
    xsi and xs namespaces are defined on the SOAP envelope but are used
    within the payload. This can cause issues with some type conversions in
    PAYLOAD mode, as the Camel-CXF endpoint will return some kind of window
    within the StAX parsing (and the namespace definitions are outside).

    If some CXF implementation bean is used as the service the namespaces
    will be defined within the payload (and everything works fine).
     */
    private static final String RESPONSE_STRING = "This is the response string";

    private static final String RESPONSE_MESSAGE = ((("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body>" + "<ns0:payload xmlns:ns0=\"http://schema.apache.org/test\"><ns0:response>") + (CxfPayloadRouterContentLengthTest.RESPONSE_STRING)) + "</ns0:response></ns0:payload>") + "</s:Body></s:Envelope>";

    private static final String REQUEST_MESSAGE = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"><s:Body>" + ("<ns0:payload xmlns:ns0=\"http://schema.apache.org/test\"><ns0:request>foo</ns0:request></ns0:payload>" + "</s:Body></s:Envelope>");

    // The Camel-Test with CXF will re-use jetty instances, so the ports1 to 6 are already blocked
    private static final int JETTY_PORT = AvailablePortFinder.getNextAvailable();

    private AbstractXmlApplicationContext applicationContext;

    private Server server;

    static {
        System.setProperty("CXFTestSupport.jettyPort", Integer.toString(CxfPayloadRouterContentLengthTest.JETTY_PORT));
    }

    @Test
    public void testInvokeRouter() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        long contentLength = 0;
        boolean isChunked = false;
        String receivedContent = null;
        try {
            HttpPost httppost = new HttpPost((("http://localhost:" + (CXFTestSupport.getPort1())) + "/TEST/PROXY"));
            StringEntity reqEntity = new StringEntity(CxfPayloadRouterContentLengthTest.REQUEST_MESSAGE, ContentType.TEXT_XML);
            reqEntity.setChunked(false);
            httppost.setEntity(reqEntity);
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity respEntity = response.getEntity();
                contentLength = respEntity.getContentLength();
                isChunked = respEntity.isChunked();
                receivedContent = EntityUtils.toString(respEntity);
                EntityUtils.consume(response.getEntity());
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        assertNotNull(receivedContent);
        // chunked encoding is fine, we don't need to check the content length
        if (!isChunked) {
            assertEquals(receivedContent.length(), contentLength);
        }
        assertTrue((((("[" + receivedContent) + "] does not contain [") + (CxfPayloadRouterContentLengthTest.RESPONSE_STRING)) + "]"), receivedContent.contains(CxfPayloadRouterContentLengthTest.RESPONSE_STRING));
        // check whether the response was cut off by the client because the
        // Content-Length was wrong
        assertTrue((("[" + receivedContent) + "] does not contain the closing Envelope tag."), receivedContent.matches(".*\\</.*:Envelope\\>"));
    }
}

