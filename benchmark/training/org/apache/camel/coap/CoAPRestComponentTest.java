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


import MediaTypeRegistry.TEXT_PLAIN;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.junit.Test;


public class CoAPRestComponentTest extends CamelTestSupport {
    static int coapport = AvailablePortFinder.getNextAvailable();

    @Test
    public void testCoAP() throws Exception {
        NetworkConfig.createStandardWithoutFile();
        CoapClient client;
        CoapResponse rsp;
        client = new CoapClient((("coap://localhost:" + (CoAPRestComponentTest.coapport)) + "/TestResource/Ducky"));
        rsp = client.get();
        assertEquals(ResponseCode.CONTENT, rsp.getCode());
        assertEquals("Hello Ducky", rsp.getResponseText());
        rsp = client.post("data", TEXT_PLAIN);
        assertEquals(ResponseCode.CONTENT, rsp.getCode());
        assertEquals("Hello Ducky: data", rsp.getResponseText());
        client = new CoapClient((("coap://localhost:" + (CoAPRestComponentTest.coapport)) + "/TestParams?id=Ducky"));
        client.setTimeout(1000000);
        rsp = client.get();
        assertEquals(ResponseCode.CONTENT, rsp.getCode());
        assertEquals("Hello Ducky", rsp.getResponseText());
        rsp = client.post("data", TEXT_PLAIN);
        assertEquals(ResponseCode.CONTENT, rsp.getCode());
        assertEquals("Hello Ducky: data", rsp.getResponseText());
        assertEquals(TEXT_PLAIN, rsp.getOptions().getContentFormat());
    }

    @Test
    public void testCoAPMethodNotAllowedResponse() throws Exception {
        NetworkConfig.createStandardWithoutFile();
        CoapClient client = new CoapClient((("coap://localhost:" + (CoAPRestComponentTest.coapport)) + "/TestResource/Ducky"));
        client.setTimeout(1000000);
        CoapResponse rsp = client.delete();
        assertEquals(ResponseCode.METHOD_NOT_ALLOWED, rsp.getCode());
    }

    @Test
    public void testCoAPNotFoundResponse() throws Exception {
        NetworkConfig.createStandardWithoutFile();
        CoapClient client = new CoapClient((("coap://localhost:" + (CoAPRestComponentTest.coapport)) + "/foo/bar/cheese"));
        client.setTimeout(1000000);
        CoapResponse rsp = client.get();
        assertEquals(ResponseCode.NOT_FOUND, rsp.getCode());
    }
}

