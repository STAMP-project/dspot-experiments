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
package org.apache.camel.component.cxf.common.header;


import Client.REQUEST_CONTEXT;
import Client.RESPONSE_CONTEXT;
import CxfConstants.CAMEL_CXF_PROTOCOL_HEADERS_MERGED;
import Exchange.CONTENT_TYPE;
import Exchange.HTTP_METHOD;
import Exchange.HTTP_PATH;
import Exchange.HTTP_RESPONSE_CODE;
import Exchange.HTTP_URI;
import Message.BASE_PATH;
import Message.ENCODING;
import Message.HTTP_REQUEST_METHOD;
import Message.PATH_INFO;
import Message.REQUEST_URI;
import Message.RESPONSE_CODE;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultHeaderFilterStrategy;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.message.org.apache.cxf.message.Message;
import org.junit.Assert;
import org.junit.Test;


public class CxfHeaderHelperTest extends Assert {
    private DefaultCamelContext context = new DefaultCamelContext();

    @Test
    public void testPropagateCamelToCxf() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.getIn().setHeader("soapAction", "urn:hello:world");
        exchange.getIn().setHeader("MyFruitHeader", "peach");
        exchange.getIn().setHeader("MyBrewHeader", Arrays.asList("cappuccino", "espresso"));
        exchange.getIn().setHeader(CONTENT_TYPE, "text/xml");
        exchange.getIn().setHeader(HTTP_RESPONSE_CODE, "200");
        exchange.getIn().setHeader(HTTP_URI, "/hello/cxf");
        exchange.getIn().setHeader(HTTP_METHOD, "GET");
        exchange.getIn().setHeader(HTTP_PATH, "/hello/cxf");
        Map<String, Object> requestContext = Collections.singletonMap("request", "true");
        Map<String, Object> responseContext = Collections.singletonMap("response", "true");
        exchange.getIn().setHeader(REQUEST_CONTEXT, requestContext);
        exchange.getIn().setHeader(RESPONSE_CONTEXT, responseContext);
        Message cxfMessage = new MessageImpl();
        CxfHeaderHelper.propagateCamelToCxf(new DefaultHeaderFilterStrategy(), exchange.getIn().getHeaders(), cxfMessage, exchange);
        Assert.assertEquals("text/xml", cxfMessage.get(Message.CONTENT_TYPE));
        Assert.assertEquals("200", cxfMessage.get(RESPONSE_CODE));
        Assert.assertEquals(requestContext, cxfMessage.get(REQUEST_CONTEXT));
        Assert.assertEquals(responseContext, cxfMessage.get(RESPONSE_CONTEXT));
        Assert.assertNull(cxfMessage.get(HTTP_RESPONSE_CODE));
        // check the protocol headers
        Map<String, List<String>> cxfHeaders = CastUtils.cast(((Map<?, ?>) (cxfMessage.get(org.apache.cxf.message.Message))));
        Assert.assertNotNull(cxfHeaders);
        Assert.assertTrue(((cxfHeaders.size()) == 7));
        verifyHeader(cxfHeaders, "soapaction", "urn:hello:world");
        verifyHeader(cxfHeaders, "SoapAction", "urn:hello:world");
        verifyHeader(cxfHeaders, "SOAPAction", "urn:hello:world");
        verifyHeader(cxfHeaders, "myfruitheader", "peach");
        verifyHeader(cxfHeaders, "myFruitHeader", "peach");
        verifyHeader(cxfHeaders, "MYFRUITHEADER", "peach");
        verifyHeader(cxfHeaders, "MyBrewHeader", Arrays.asList("cappuccino", "espresso"));
        verifyHeader(cxfHeaders, Message.CONTENT_TYPE, "text/xml");
        verifyHeader(cxfHeaders, REQUEST_URI, "/hello/cxf");
        verifyHeader(cxfHeaders, HTTP_REQUEST_METHOD, "GET");
        verifyHeader(cxfHeaders, PATH_INFO, "/hello/cxf");
        Assert.assertNull(cxfHeaders.get(HTTP_RESPONSE_CODE));
        Assert.assertNull(cxfHeaders.get(HTTP_URI));
        Assert.assertNull(cxfHeaders.get(HTTP_METHOD));
        Assert.assertNull(cxfHeaders.get(HTTP_PATH));
    }

    @Test
    public void testPropagateCxfToCamel() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        Message cxfMessage = new MessageImpl();
        Map<String, List<String>> cxfHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        cxfHeaders.put("Content-Length", Arrays.asList("241"));
        cxfHeaders.put("soapAction", Arrays.asList("urn:hello:world"));
        cxfHeaders.put("myfruitheader", Arrays.asList("peach"));
        cxfHeaders.put("mybrewheader", Arrays.asList("cappuccino", "espresso"));
        cxfHeaders.put(Message.CONTENT_TYPE, Arrays.asList("text/xml"));
        cxfHeaders.put(ENCODING, Arrays.asList("UTF-8"));
        cxfHeaders.put(RESPONSE_CODE, Arrays.asList("201"));// Ignored

        cxfHeaders.put(REQUEST_URI, Arrays.asList("/base/hello/cxf"));
        cxfHeaders.put(HTTP_REQUEST_METHOD, Arrays.asList("GET"));
        cxfHeaders.put(PATH_INFO, Arrays.asList("/base/hello/cxf"));
        cxfHeaders.put(BASE_PATH, Arrays.asList("/base"));
        cxfMessage.put(org.apache.cxf.message.Message, cxfHeaders);
        cxfMessage.put(RESPONSE_CODE, "200");
        Map<String, Object> requestContext = Collections.singletonMap("request", "true");
        Map<String, Object> responseContext = Collections.singletonMap("response", "true");
        cxfMessage.put(REQUEST_CONTEXT, requestContext);
        cxfMessage.put(RESPONSE_CONTEXT, responseContext);
        CxfHeaderHelper.propagateCxfToCamel(new DefaultHeaderFilterStrategy(), cxfMessage, exchange.getIn(), exchange);
        Map<String, Object> camelHeaders = exchange.getIn().getHeaders();
        Assert.assertEquals("urn:hello:world", camelHeaders.get("soapaction"));
        Assert.assertEquals("urn:hello:world", camelHeaders.get("SoapAction"));
        Assert.assertEquals("241", camelHeaders.get("content-length"));
        Assert.assertEquals("peach", camelHeaders.get("MyFruitHeader"));
        Assert.assertEquals(Arrays.asList("cappuccino", "espresso"), camelHeaders.get("MyBrewHeader"));
        Assert.assertEquals("text/xml; charset=UTF-8", camelHeaders.get(CONTENT_TYPE));
        Assert.assertEquals("/base/hello/cxf", camelHeaders.get(HTTP_URI));
        Assert.assertEquals("GET", camelHeaders.get(HTTP_METHOD));
        Assert.assertEquals("/hello/cxf", camelHeaders.get(HTTP_PATH));
        Assert.assertEquals("200", camelHeaders.get(HTTP_RESPONSE_CODE));
        Assert.assertEquals(requestContext, camelHeaders.get(REQUEST_CONTEXT));
        Assert.assertEquals(responseContext, camelHeaders.get(RESPONSE_CONTEXT));
        Assert.assertNull(camelHeaders.get(RESPONSE_CODE));
        Assert.assertNull(camelHeaders.get(REQUEST_URI));
        Assert.assertNull(camelHeaders.get(HTTP_REQUEST_METHOD));
        Assert.assertNull(camelHeaders.get(PATH_INFO));
        Assert.assertNull(camelHeaders.get(RESPONSE_CODE));
    }

    @Test
    public void testPropagateCxfToCamelWithMerged() {
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.setProperty(CAMEL_CXF_PROTOCOL_HEADERS_MERGED, Boolean.TRUE);
        Message cxfMessage = new MessageImpl();
        Map<String, List<String>> cxfHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        cxfHeaders.put("myfruitheader", Arrays.asList("peach"));
        cxfHeaders.put("mybrewheader", Arrays.asList("cappuccino", "espresso"));
        cxfMessage.put(org.apache.cxf.message.Message, cxfHeaders);
        CxfHeaderHelper.propagateCxfToCamel(new DefaultHeaderFilterStrategy(), cxfMessage, exchange.getIn(), exchange);
        Map<String, Object> camelHeaders = exchange.getIn().getHeaders();
        Assert.assertEquals("peach", camelHeaders.get("MyFruitHeader"));
        Assert.assertEquals("cappuccino, espresso", camelHeaders.get("MyBrewHeader"));
    }
}

