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
package org.apache.camel.component.cxf.jaxrs.simplebinding;


import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import org.apache.camel.component.cxf.CXFTestSupport;
import org.apache.camel.component.cxf.jaxrs.simplebinding.testbean.Customer;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;


/**
 * Tests for the Simple Binding style of CXF JAX-RS consumers.
 */
public class CxfRsConsumerSimpleBindingImplTest extends CamelTestSupport {
    private static final String PORT_PATH = (CXFTestSupport.getPort1()) + "/CxfRsConsumerTest";

    private static final String CXF_RS_ENDPOINT_URI = ("cxfrs://http://localhost:" + (CxfRsConsumerSimpleBindingImplTest.PORT_PATH)) + "/rest?resourceClasses=org.apache.camel.component.cxf.jaxrs.simplebinding.testbean.CustomerServiceImpl&bindingStyle=SimpleConsumer";

    private JAXBContext jaxb;

    private CloseableHttpClient httpclient;

    @Test
    public void testGetCustomerOnlyHeaders() throws Exception {
        HttpGet get = new HttpGet((("http://localhost:" + (CxfRsConsumerSimpleBindingImplTest.PORT_PATH)) + "/rest/customerservice/customers/123"));
        get.addHeader("Accept", "text/xml");
        HttpResponse response = httpclient.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
        Customer entity = ((Customer) (jaxb.createUnmarshaller().unmarshal(response.getEntity().getContent())));
        assertEquals(123, entity.getId());
    }

    @Test
    public void testNewCustomerWithQueryParam() throws Exception {
        HttpPost post = new HttpPost((("http://localhost:" + (CxfRsConsumerSimpleBindingImplTest.PORT_PATH)) + "/rest/customerservice/customers?age=12"));
        StringWriter sw = new StringWriter();
        jaxb.createMarshaller().marshal(new Customer(123, "Raul"), sw);
        post.setEntity(new StringEntity(sw.toString()));
        post.addHeader("Content-Type", "text/xml");
        post.addHeader("Accept", "text/xml");
        HttpResponse response = httpclient.execute(post);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }
}

