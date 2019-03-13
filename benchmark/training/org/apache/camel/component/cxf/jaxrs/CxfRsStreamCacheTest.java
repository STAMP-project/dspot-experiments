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
package org.apache.camel.component.cxf.jaxrs;


import org.apache.camel.component.cxf.CXFTestSupport;
import org.apache.camel.component.cxf.jaxrs.testbean.Customer;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;


public class CxfRsStreamCacheTest extends CamelTestSupport {
    private static final String PUT_REQUEST = "<Customer><name>Mary</name><id>123</id></Customer>";

    private static final String CONTEXT = "/CxfRsStreamCacheTest";

    private static final String CXT = (CXFTestSupport.getPort1()) + (CxfRsStreamCacheTest.CONTEXT);

    private static final String RESPONSE = "<pong xmlns=\"test/service\"/>";

    private String cxfRsEndpointUri = ((("cxfrs://http://localhost:" + (CxfRsStreamCacheTest.CXT)) + "/rest?synchronous=") + (isSynchronous())) + "&dataFormat=PAYLOAD&resourceClasses=org.apache.camel.component.cxf.jaxrs.testbean.CustomerService";

    @Test
    public void testPutConsumer() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.message(0).body().isInstanceOf(Customer.class);
        MockEndpoint onComplete = getMockEndpoint("mock:onComplete");
        onComplete.expectedMessageCount(1);
        HttpPut put = new HttpPut((("http://localhost:" + (CxfRsStreamCacheTest.CXT)) + "/rest/customerservice/customers"));
        StringEntity entity = new StringEntity(CxfRsStreamCacheTest.PUT_REQUEST, "ISO-8859-1");
        entity.setContentType("text/xml; charset=ISO-8859-1");
        put.addHeader("test", "header1;header2");
        put.setEntity(entity);
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        try {
            HttpResponse response = httpclient.execute(put);
            assertEquals(200, response.getStatusLine().getStatusCode());
            assertEquals(CxfRsStreamCacheTest.RESPONSE, EntityUtils.toString(response.getEntity()));
        } finally {
            httpclient.close();
        }
        mock.assertIsSatisfied();
        onComplete.assertIsSatisfied();
    }
}

