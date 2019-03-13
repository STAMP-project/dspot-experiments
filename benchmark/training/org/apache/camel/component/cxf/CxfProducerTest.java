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


import Client.RESPONSE_CONTEXT;
import Exchange.FILE_NAME;
import java.net.ConnectException;
import java.util.Map;
import javax.xml.ws.Endpoint;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.helpers.CastUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CxfProducerTest extends Assert {
    protected static final String ECHO_OPERATION = "echo";

    protected static final String GREET_ME_OPERATION = "greetMe";

    protected static final String TEST_MESSAGE = "Hello World!";

    private static final Logger LOG = LoggerFactory.getLogger(CxfProducerTest.class);

    protected CamelContext camelContext;

    protected ProducerTemplate template;

    protected Server server;

    protected Endpoint endpoint;

    @Test
    public void testInvokingSimpleServerWithParams() throws Exception {
        Exchange exchange = sendSimpleMessage();
        org.apache.camel.Message out = exchange.getOut();
        String result = out.getBody(String.class);
        CxfProducerTest.LOG.info(("Received output text: " + result));
        Map<String, Object> responseContext = CastUtils.cast(((Map<?, ?>) (out.getHeader(RESPONSE_CONTEXT))));
        Assert.assertNotNull(responseContext);
        Assert.assertEquals("We should get the response context here", "UTF-8", responseContext.get(org.apache.cxf.message.Message));
        Assert.assertEquals("reply body on Camel", ("echo " + (CxfProducerTest.TEST_MESSAGE)), result);
        // check the other camel header copying
        String fileName = out.getHeader(FILE_NAME, String.class);
        Assert.assertEquals("Should get the file name from out message header", "testFile", fileName);
        // check if the header object is turned into String
        Object requestObject = out.getHeader("requestObject");
        Assert.assertTrue("We should get the right requestObject.", (requestObject instanceof DefaultCxfBinding));
    }

    @Test
    public void testInvokingAWrongServer() throws Exception {
        Exchange reply = sendSimpleMessage(getWrongEndpointUri());
        Assert.assertNotNull("We should get the exception here", reply.getException());
        Assert.assertTrue(((reply.getException().getCause()) instanceof ConnectException));
        // Test the data format PAYLOAD
        reply = sendSimpleMessageWithPayloadMessage(((getWrongEndpointUri()) + "&dataFormat=PAYLOAD"));
        Assert.assertNotNull("We should get the exception here", reply.getException());
        Assert.assertTrue(((reply.getException().getCause()) instanceof ConnectException));
        // Test the data format MESSAGE
        reply = sendSimpleMessageWithRawMessage(((getWrongEndpointUri()) + "&dataFormat=RAW"));
        Assert.assertNotNull("We should get the exception here", reply.getException());
        Assert.assertTrue(((reply.getException().getCause()) instanceof ConnectException));
    }

    @Test
    public void testInvokingJaxWsServerWithParams() throws Exception {
        Exchange exchange = sendJaxWsMessage();
        org.apache.camel.Message out = exchange.getOut();
        String result = out.getBody(String.class);
        CxfProducerTest.LOG.info(("Received output text: " + result));
        Map<String, Object> responseContext = CastUtils.cast(((Map<?, ?>) (out.getHeader(RESPONSE_CONTEXT))));
        Assert.assertNotNull(responseContext);
        Assert.assertEquals("Get the wrong wsdl operation name", "{http://apache.org/hello_world_soap_http}greetMe", responseContext.get("javax.xml.ws.wsdl.operation").toString());
        Assert.assertEquals("reply body on Camel", ("Hello " + (CxfProducerTest.TEST_MESSAGE)), result);
        // check the other camel header copying
        String fileName = out.getHeader(FILE_NAME, String.class);
        Assert.assertEquals("Should get the file name from out message header", "testFile", fileName);
    }
}

