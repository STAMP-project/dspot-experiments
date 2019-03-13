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


import Consts.UTF_8;
import HttpMultipartMode.STRICT;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import javax.xml.bind.JAXBContext;
import org.apache.camel.component.cxf.CXFTestSupport;
import org.apache.camel.component.cxf.jaxrs.simplebinding.testbean.Customer;
import org.apache.camel.component.cxf.jaxrs.simplebinding.testbean.CustomerList;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;


/**
 * Tests for the Simple Binding style of CXF JAX-RS consumers.
 */
public class CxfRsConsumerSimpleBindingTest extends CamelTestSupport {
    private static final String PORT_PATH = (CXFTestSupport.getPort1()) + "/CxfRsConsumerTest";

    private static final String CXF_RS_ENDPOINT_URI = ("cxfrs://http://localhost:" + (CxfRsConsumerSimpleBindingTest.PORT_PATH)) + "/rest?resourceClasses=org.apache.camel.component.cxf.jaxrs.simplebinding.testbean.CustomerServiceResource&bindingStyle=SimpleConsumer";

    private JAXBContext jaxb;

    private CloseableHttpClient httpclient;

    @Test
    public void testGetCustomerOnlyHeaders() throws Exception {
        HttpGet get = new HttpGet((("http://localhost:" + (CxfRsConsumerSimpleBindingTest.PORT_PATH)) + "/rest/customerservice/customers/123"));
        get.addHeader("Accept", "text/xml");
        HttpResponse response = httpclient.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
        Customer entity = ((Customer) (jaxb.createUnmarshaller().unmarshal(response.getEntity().getContent())));
        assertEquals(123, entity.getId());
    }

    @Test
    public void testGetCustomerHttp404CustomStatus() throws Exception {
        HttpGet get = new HttpGet((("http://localhost:" + (CxfRsConsumerSimpleBindingTest.PORT_PATH)) + "/rest/customerservice/customers/456"));
        get.addHeader("Accept", "text/xml");
        HttpResponse response = httpclient.execute(get);
        assertEquals(404, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testUpdateCustomerBodyAndHeaders() throws Exception {
        HttpPut put = new HttpPut((("http://localhost:" + (CxfRsConsumerSimpleBindingTest.PORT_PATH)) + "/rest/customerservice/customers/123"));
        StringWriter sw = new StringWriter();
        jaxb.createMarshaller().marshal(new Customer(123, "Raul"), sw);
        put.setEntity(new StringEntity(sw.toString()));
        put.addHeader("Content-Type", "text/xml");
        put.addHeader("Accept", "text/xml");
        HttpResponse response = httpclient.execute(put);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testNewCustomerOnlyBody() throws Exception {
        HttpPost post = new HttpPost((("http://localhost:" + (CxfRsConsumerSimpleBindingTest.PORT_PATH)) + "/rest/customerservice/customers"));
        StringWriter sw = new StringWriter();
        jaxb.createMarshaller().marshal(new Customer(123, "Raul"), sw);
        post.setEntity(new StringEntity(sw.toString()));
        post.addHeader("Content-Type", "text/xml");
        post.addHeader("Accept", "text/xml");
        HttpResponse response = httpclient.execute(post);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testListVipCustomers() throws Exception {
        HttpGet get = new HttpGet((("http://localhost:" + (CxfRsConsumerSimpleBindingTest.PORT_PATH)) + "/rest/customerservice/customers/vip/gold"));
        get.addHeader("Content-Type", "text/xml");
        get.addHeader("Accept", "text/xml");
        HttpResponse response = httpclient.execute(get);
        assertEquals(200, response.getStatusLine().getStatusCode());
        CustomerList cl = ((CustomerList) (jaxb.createUnmarshaller().unmarshal(new StringReader(EntityUtils.toString(response.getEntity())))));
        List<Customer> vips = cl.getCustomers();
        assertEquals(2, vips.size());
        assertEquals(123, vips.get(0).getId());
        assertEquals(456, vips.get(1).getId());
    }

    @Test
    public void testUpdateVipCustomer() throws Exception {
        HttpPut put = new HttpPut((("http://localhost:" + (CxfRsConsumerSimpleBindingTest.PORT_PATH)) + "/rest/customerservice/customers/vip/gold/123"));
        StringWriter sw = new StringWriter();
        jaxb.createMarshaller().marshal(new Customer(123, "Raul2"), sw);
        put.setEntity(new StringEntity(sw.toString()));
        put.addHeader("Content-Type", "text/xml");
        put.addHeader("Accept", "text/xml");
        HttpResponse response = httpclient.execute(put);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testDeleteVipCustomer() throws Exception {
        HttpDelete delete = new HttpDelete((("http://localhost:" + (CxfRsConsumerSimpleBindingTest.PORT_PATH)) + "/rest/customerservice/customers/vip/gold/123"));
        delete.addHeader("Accept", "text/xml");
        HttpResponse response = httpclient.execute(delete);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testUploadInputStream() throws Exception {
        HttpPost post = new HttpPost((("http://localhost:" + (CxfRsConsumerSimpleBindingTest.PORT_PATH)) + "/rest/customerservice/customers/123/image_inputstream"));
        post.addHeader("Content-Type", "image/jpeg");
        post.addHeader("Accept", "text/xml");
        post.setEntity(new InputStreamEntity(this.getClass().getClassLoader().getResourceAsStream("java.jpg"), 100));
        HttpResponse response = httpclient.execute(post);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testUploadDataHandler() throws Exception {
        HttpPost post = new HttpPost((("http://localhost:" + (CxfRsConsumerSimpleBindingTest.PORT_PATH)) + "/rest/customerservice/customers/123/image_datahandler"));
        post.addHeader("Content-Type", "image/jpeg");
        post.addHeader("Accept", "text/xml");
        post.setEntity(new InputStreamEntity(this.getClass().getClassLoader().getResourceAsStream("java.jpg"), 100));
        HttpResponse response = httpclient.execute(post);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testMultipartPostWithParametersAndPayload() throws Exception {
        HttpPost post = new HttpPost((("http://localhost:" + (CxfRsConsumerSimpleBindingTest.PORT_PATH)) + "/rest/customerservice/customers/multipart/123?query=abcd"));
        MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(STRICT);
        builder.addBinaryBody("part1", new File(this.getClass().getClassLoader().getResource("java.jpg").toURI()), ContentType.create("image/jpeg"), "java.jpg");
        builder.addBinaryBody("part2", new File(this.getClass().getClassLoader().getResource("java.jpg").toURI()), ContentType.create("image/jpeg"), "java.jpg");
        StringWriter sw = new StringWriter();
        jaxb.createMarshaller().marshal(new Customer(123, "Raul"), sw);
        builder.addTextBody("body", sw.toString(), ContentType.create("text/xml", UTF_8));
        post.setEntity(builder.build());
        HttpResponse response = httpclient.execute(post);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testMultipartPostWithoutParameters() throws Exception {
        HttpPost post = new HttpPost((("http://localhost:" + (CxfRsConsumerSimpleBindingTest.PORT_PATH)) + "/rest/customerservice/customers/multipart/withoutParameters"));
        MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(STRICT);
        builder.addBinaryBody("part1", new File(this.getClass().getClassLoader().getResource("java.jpg").toURI()), ContentType.create("image/jpeg"), "java.jpg");
        builder.addBinaryBody("part2", new File(this.getClass().getClassLoader().getResource("java.jpg").toURI()), ContentType.create("image/jpeg"), "java.jpg");
        StringWriter sw = new StringWriter();
        jaxb.createMarshaller().marshal(new Customer(123, "Raul"), sw);
        builder.addTextBody("body", sw.toString(), ContentType.create("text/xml", UTF_8));
        post.setEntity(builder.build());
        HttpResponse response = httpclient.execute(post);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }
}

