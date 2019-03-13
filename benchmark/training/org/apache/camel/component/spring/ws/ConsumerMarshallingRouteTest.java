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


import org.apache.camel.component.spring.ws.bean.CamelEndpointMapping;
import org.apache.camel.component.spring.ws.jaxb.QuoteRequest;
import org.apache.camel.component.spring.ws.jaxb.QuoteResponse;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.core.WebServiceTemplate;


@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ConsumerMarshallingRouteTest extends CamelTestSupport {
    @Autowired
    private CamelEndpointMapping endpointMapping;

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Test
    public void consumeWebserviceWithPojoRequest() throws Exception {
        consumePojoRequestStringResponseWithEnpoint("direct:webservice-marshall");
    }

    @Test
    public void consumeWebserviceWithPojoRequestAndPojoResponse() throws Exception {
        QuoteRequest request = new QuoteRequest();
        request.setSymbol("GOOG");
        Object result = template.requestBody("direct:webservice-marshall-unmarshall", request);
        assertNotNull(result);
        assertTrue((result instanceof QuoteResponse));
        QuoteResponse quoteResponse = ((QuoteResponse) (result));
        assertEquals("Google Inc.", quoteResponse.getName());
    }

    @Test
    public void consumeWebserviceWithPojoRequestAsInOnly() throws Exception {
        QuoteRequest request = new QuoteRequest();
        request.setSymbol("GOOG");
        Object result = template.requestBody("direct:webservice-marshall-asinonly", request);
        assertNull(result);
    }

    @Test
    public void consumeWebserviceWithPojoRequestAsIn() throws Exception {
        consumePojoRequestStringResponseWithEnpoint("direct:webservice-marshall-asin");
    }
}

