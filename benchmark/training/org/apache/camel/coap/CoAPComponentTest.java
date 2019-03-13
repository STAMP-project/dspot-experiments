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
package org.apache.camel.coap;


import CoAP.ResponseCode.CONTENT;
import CoAPConstants.COAP_RESPONSE_CODE;
import Exchange.CONTENT_TYPE;
import MediaTypeRegistry.APPLICATION_OCTET_STREAM;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.junit.Test;


public class CoAPComponentTest extends CoAPTestSupport {
    @Produce(uri = "direct:start")
    protected ProducerTemplate sender;

    @Test
    public void testCoAPComponent() throws Exception {
        CoapClient client = createClient("/TestResource");
        CoapResponse response = client.get();
        assertEquals("Hello ", response.getResponseText());
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        mock.expectedBodiesReceived("Hello Camel CoAP");
        mock.expectedHeaderReceived(CONTENT_TYPE, MediaTypeRegistry.toString(APPLICATION_OCTET_STREAM));
        mock.expectedHeaderReceived(COAP_RESPONSE_CODE, CONTENT.toString());
        sender.sendBody("Camel CoAP");
        assertMockEndpointsSatisfied();
    }
}

