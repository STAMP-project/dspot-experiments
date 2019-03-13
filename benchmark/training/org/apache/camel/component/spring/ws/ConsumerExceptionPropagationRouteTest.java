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
package org.apache.camel.component.spring.ws;


import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import org.apache.camel.component.spring.ws.bean.CamelEndpointMapping;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.test.server.MockWebServiceClient;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ConsumerExceptionPropagationRouteTest extends CamelTestSupport {
    private final String xmlRequestForGoogleStockQuote = "<GetQuote xmlns=\"http://www.webserviceX.NET/\"><symbol>GOOG</symbol></GetQuote>";

    @Autowired
    private CamelEndpointMapping endpointMapping;

    @Autowired
    private ApplicationContext applicationContext;

    private MockWebServiceClient mockClient;

    @Test
    public void consumeWebserviceAndTestForSoapFault() throws Exception {
        StreamSource source = new StreamSource(new StringReader(xmlRequestForGoogleStockQuote));
        mockClient.sendRequest(withPayload(source)).andExpect(serverOrReceiverFault());
    }
}

