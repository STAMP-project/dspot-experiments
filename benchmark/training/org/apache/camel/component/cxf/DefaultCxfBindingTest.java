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
import CxfConstants.CAMEL_CXF_ATTACHMENTS;
import CxfConstants.CAMEL_CXF_PROTOCOL_HEADERS_MERGED;
import CxfConstants.DATA_FORMAT_PROPERTY;
import CxfConstants.OPERATION_NAME;
import CxfConstants.OPERATION_NAMESPACE;
import DataFormat.PAYLOAD;
import Exchange.CONTENT_TYPE;
import Message.PROTOCOL_HEADERS;
import SoapBindingConstants.SOAP_ACTION;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.apache.camel.support.DefaultHeaderFilterStrategy;
import org.apache.camel.support.ExchangeHelper;
import org.apache.cxf.attachment.AttachmentImpl;
import org.apache.cxf.binding.Binding;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.message.Attachment;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.message.org.apache.camel.Attachment;
import org.apache.cxf.message.org.apache.cxf.message.Message;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class DefaultCxfBindingTest extends Assert {
    private static final String SOAP_MESSAGE_1 = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"" + ((" xmlns=\"http://www.mycompany.com/test/\" xmlns:ns1=\"http://www.mycompany.com/test/1/\">" + " <soap:Body> <request> <ns1:identifier>TEST</ns1:identifier> </request>") + " </soap:Body> </soap:Envelope>");

    private static final String SOAP_MESSAGE_2 = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"" + ((" xmlns=\"http://www.mycompany.com/test/\" xmlns:ns1=\"http://www.mycompany.com/test/1/\">" + " <soap:Body> <ns1:identifier xmlns:ns1=\"http://www.mycompany.com/test/\" xmlns=\"http://www.mycompany.com/test/1/\">TEST</ns1:identifier>") + " </soap:Body> </soap:Envelope>");

    private DefaultCamelContext context = new DefaultCamelContext();

    @Test
    public void testSetGetHeaderFilterStrategy() {
        DefaultCxfBinding cxfBinding = new DefaultCxfBinding();
        HeaderFilterStrategy hfs = new DefaultHeaderFilterStrategy();
        cxfBinding.setHeaderFilterStrategy(hfs);
        Assert.assertSame("The header filter strategy is set", hfs, cxfBinding.getHeaderFilterStrategy());
    }

    @Test
    public void testPayloadBodyNamespace() throws Exception {
        MessageImpl message = new MessageImpl();
        Map<String, String> nsMap = new HashMap<>();
        Document document = getDocument(DefaultCxfBindingTest.SOAP_MESSAGE_1);
        message.setContent(Node.class, document);
        DefaultCxfBinding.getPayloadBodyElements(message, nsMap);
        Assert.assertEquals(2, nsMap.size());
        Assert.assertEquals("http://www.mycompany.com/test/", nsMap.get("xmlns"));
        Element element = document.createElement("tag");
        DefaultCxfBinding.addNamespace(element, nsMap);
        Assert.assertEquals("http://www.mycompany.com/test/", element.getAttribute("xmlns"));
        Assert.assertEquals("http://www.mycompany.com/test/1/", element.getAttribute("xmlns:ns1"));
    }

    @Test
    public void testOverridePayloadBodyNamespace() throws Exception {
        MessageImpl message = new MessageImpl();
        Map<String, String> nsMap = new HashMap<>();
        Document document = getDocument(DefaultCxfBindingTest.SOAP_MESSAGE_2);
        message.setContent(Node.class, document);
        DefaultCxfBinding.getPayloadBodyElements(message, nsMap);
        Assert.assertEquals(2, nsMap.size());
        Assert.assertEquals("http://www.mycompany.com/test/", nsMap.get("xmlns"));
        Element element = ((Element) (document.getElementsByTagName("ns1:identifier").item(0)));
        Assert.assertNotNull("We should get the element", element);
        DefaultCxfBinding.addNamespace(element, nsMap);
        Assert.assertEquals("http://www.mycompany.com/test/1/", element.getAttribute("xmlns"));
        Assert.assertEquals("http://www.mycompany.com/test/", element.getAttribute("xmlns:ns1"));
    }

    @Test
    public void testSetCharsetWithContentType() {
        DefaultCxfBinding cxfBinding = new DefaultCxfBinding();
        cxfBinding.setHeaderFilterStrategy(new DefaultHeaderFilterStrategy());
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.getIn().setHeader(CONTENT_TYPE, "text/xml;charset=ISO-8859-1");
        cxfBinding.setCharsetWithContentType(exchange);
        String charset = ExchangeHelper.getCharsetName(exchange);
        Assert.assertEquals("Get a wrong charset", "ISO-8859-1", charset);
        exchange.getIn().setHeader(CONTENT_TYPE, "text/xml");
        cxfBinding.setCharsetWithContentType(exchange);
        charset = ExchangeHelper.getCharsetName(exchange);
        Assert.assertEquals("Get a worng charset name", "UTF-8", charset);
    }

    @Test
    public void testPopulateCxfRequestFromExchange() {
        DefaultCxfBinding cxfBinding = new DefaultCxfBinding();
        cxfBinding.setHeaderFilterStrategy(new DefaultHeaderFilterStrategy());
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        org.apache.cxf.message.Exchange cxfExchange = new ExchangeImpl();
        exchange.setProperty(DATA_FORMAT_PROPERTY, PAYLOAD);
        Map<String, Object> requestContext = new HashMap<>();
        exchange.getIn().setHeader("soapAction", "urn:hello:world");
        exchange.getIn().setHeader("MyFruitHeader", "peach");
        exchange.getIn().setHeader("MyBrewHeader", Arrays.asList("cappuccino", "espresso"));
        exchange.getIn().addAttachment("att-1", new DataHandler(new FileDataSource("pom.xml")));
        exchange.getIn().getAttachmentObject("att-1").setHeader("attachment-header", "value 1");
        cxfBinding.populateCxfRequestFromExchange(cxfExchange, exchange, requestContext);
        // check the protocol headers
        Map<String, List<String>> headers = CastUtils.cast(((Map<?, ?>) (requestContext.get(PROTOCOL_HEADERS))));
        Assert.assertNotNull(headers);
        Assert.assertEquals(3, headers.size());
        verifyHeader(headers, "soapaction", "urn:hello:world");
        verifyHeader(headers, "SoapAction", "urn:hello:world");
        verifyHeader(headers, "SOAPAction", "urn:hello:world");
        verifyHeader(headers, "myfruitheader", "peach");
        verifyHeader(headers, "myFruitHeader", "peach");
        verifyHeader(headers, "MYFRUITHEADER", "peach");
        verifyHeader(headers, "MyBrewHeader", Arrays.asList("cappuccino", "espresso"));
        Set<Attachment> attachments = CastUtils.cast(((Set<?>) (requestContext.get(CAMEL_CXF_ATTACHMENTS))));
        Assert.assertNotNull(attachments);
        Assert.assertNotNull(((attachments.size()) == 1));
        Attachment att = attachments.iterator().next();
        Assert.assertEquals("att-1", att.getId());
        Assert.assertEquals("value 1", att.getHeader("attachment-header"));
    }

    @Test
    public void testPopulateCxfSoapHeaderRequestFromExchange() {
        DefaultCxfBinding cxfBinding = new DefaultCxfBinding();
        cxfBinding.setHeaderFilterStrategy(new DefaultHeaderFilterStrategy());
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        org.apache.cxf.message.Exchange cxfExchange = new ExchangeImpl();
        Map<String, Object> requestContext = new HashMap<>();
        String expectedSoapActionHeader = "urn:hello:world";
        exchange.getIn().setHeader("soapAction", expectedSoapActionHeader);
        cxfBinding.populateCxfRequestFromExchange(cxfExchange, exchange, requestContext);
        String actualSoapActionHeader = ((String) (requestContext.get(SOAP_ACTION)));
        Assert.assertEquals(expectedSoapActionHeader, actualSoapActionHeader);
    }

    @Test
    public void testPopulateCxfSoapHeaderRequestFromExchangeWithExplicitOperationName() {
        DefaultCxfBinding cxfBinding = new DefaultCxfBinding();
        cxfBinding.setHeaderFilterStrategy(new DefaultHeaderFilterStrategy());
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        org.apache.cxf.message.Exchange cxfExchange = new ExchangeImpl();
        Map<String, Object> requestContext = new HashMap<>();
        String expectedSoapActionHeader = "urn:hello:world";
        exchange.getIn().setHeader(OPERATION_NAMESPACE, "http://test123");
        exchange.getIn().setHeader(OPERATION_NAME, "testOperation");
        cxfBinding.populateCxfRequestFromExchange(cxfExchange, exchange, requestContext);
        String actualSoapActionHeader = ((String) (requestContext.get(SOAP_ACTION)));
        Assert.assertNull(actualSoapActionHeader);
    }

    @Test
    public void testPopupalteExchangeFromCxfResponse() {
        DefaultCxfBinding cxfBinding = new DefaultCxfBinding();
        cxfBinding.setHeaderFilterStrategy(new DefaultHeaderFilterStrategy());
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        org.apache.cxf.message.Exchange cxfExchange = new ExchangeImpl();
        exchange.setProperty(DATA_FORMAT_PROPERTY, PAYLOAD);
        Map<String, Object> responseContext = new HashMap<>();
        responseContext.put(org.apache.cxf.message.Message, Integer.valueOf(200));
        Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        headers.put("content-type", Arrays.asList("text/xml;charset=UTF-8"));
        headers.put("Content-Length", Arrays.asList("241"));
        responseContext.put(org.apache.cxf.message.Message, headers);
        Message cxfMessage = new MessageImpl();
        cxfExchange.setInMessage(cxfMessage);
        Set<Attachment> attachments = new HashSet<>();
        AttachmentImpl attachment = new AttachmentImpl("att-1", new DataHandler(new FileDataSource("pom.xml")));
        attachment.setHeader("additional-header", "value 1");
        attachments.add(attachment);
        cxfMessage.setAttachments(attachments);
        cxfBinding.populateExchangeFromCxfResponse(exchange, cxfExchange, responseContext);
        Map<String, Object> camelHeaders = exchange.getOut().getHeaders();
        Assert.assertNotNull(camelHeaders);
        Assert.assertEquals(responseContext, camelHeaders.get(RESPONSE_CONTEXT));
        Map<String, org.apache.camel.Attachment> camelAttachments = exchange.getOut().getAttachmentObjects();
        Assert.assertNotNull(camelAttachments);
        Assert.assertNotNull(camelAttachments.get("att-1"));
        Assert.assertEquals("value 1", camelAttachments.get("att-1").getHeader("additional-header"));
    }

    @Test
    public void testPopupalteExchangeFromCxfResponseOfNullBody() {
        DefaultCxfBinding cxfBinding = new DefaultCxfBinding();
        cxfBinding.setHeaderFilterStrategy(new DefaultHeaderFilterStrategy());
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        org.apache.cxf.message.Exchange cxfExchange = new ExchangeImpl();
        exchange.setProperty(DATA_FORMAT_PROPERTY, PAYLOAD);
        Map<String, Object> responseContext = new HashMap<>();
        responseContext.put(org.apache.cxf.message.Message, Integer.valueOf(200));
        Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        responseContext.put(org.apache.cxf.message.Message, headers);
        Message cxfMessage = new MessageImpl();
        cxfExchange.setInMessage(cxfMessage);
        cxfBinding.populateExchangeFromCxfResponse(exchange, cxfExchange, responseContext);
        CxfPayload<?> cxfPayload = exchange.getOut().getBody(CxfPayload.class);
        Assert.assertNotNull(cxfPayload);
        List<?> body = cxfPayload.getBody();
        Assert.assertNotNull(body);
        Assert.assertEquals(0, body.size());
    }

    @Test
    public void testPopupalteCxfResponseFromExchange() {
        DefaultCxfBinding cxfBinding = new DefaultCxfBinding();
        cxfBinding.setHeaderFilterStrategy(new DefaultHeaderFilterStrategy());
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context, ExchangePattern.InOut);
        org.apache.cxf.message.Exchange cxfExchange = new ExchangeImpl();
        exchange.setProperty(DATA_FORMAT_PROPERTY, PAYLOAD);
        exchange.getOut().setHeader("soapAction", "urn:hello:world");
        exchange.getOut().setHeader("MyFruitHeader", "peach");
        exchange.getOut().addAttachment("att-1", new DataHandler(new FileDataSource("pom.xml")));
        exchange.getOut().getAttachmentObject("att-1").setHeader("attachment-header", "value 1");
        Endpoint endpoint = Mockito.mock(Endpoint.class);
        Binding binding = Mockito.mock(Binding.class);
        Mockito.when(endpoint.getBinding()).thenReturn(binding);
        Message cxfMessage = new MessageImpl();
        Mockito.when(binding.createMessage()).thenReturn(cxfMessage);
        cxfExchange.put(Endpoint.class, endpoint);
        cxfBinding.populateCxfResponseFromExchange(exchange, cxfExchange);
        cxfMessage = cxfExchange.getOutMessage();
        Assert.assertNotNull(cxfMessage);
        Map<String, List<String>> headers = CastUtils.cast(((Map<?, ?>) (cxfMessage.get(PROTOCOL_HEADERS))));
        Assert.assertNotNull(headers);
        Assert.assertTrue(((headers.size()) == 2));
        verifyHeader(headers, "soapaction", "urn:hello:world");
        verifyHeader(headers, "SoapAction", "urn:hello:world");
        verifyHeader(headers, "SOAPAction", "urn:hello:world");
        verifyHeader(headers, "myfruitheader", "peach");
        verifyHeader(headers, "myFruitHeader", "peach");
        verifyHeader(headers, "MYFRUITHEADER", "peach");
        Collection<Attachment> attachments = cxfMessage.getAttachments();
        Assert.assertNotNull(attachments);
        Assert.assertNotNull(((attachments.size()) == 1));
        Attachment att = attachments.iterator().next();
        Assert.assertEquals("att-1", att.getId());
        Assert.assertEquals("value 1", att.getHeader("attachment-header"));
    }

    @Test
    public void testPopupalteExchangeFromCxfRequest() {
        DefaultCxfBinding cxfBinding = new DefaultCxfBinding();
        cxfBinding.setHeaderFilterStrategy(new DefaultHeaderFilterStrategy());
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        org.apache.cxf.message.Exchange cxfExchange = new ExchangeImpl();
        exchange.setProperty(DATA_FORMAT_PROPERTY, PAYLOAD);
        Message cxfMessage = new MessageImpl();
        Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        headers.put("content-type", Arrays.asList("text/xml;charset=UTF-8"));
        headers.put("Content-Length", Arrays.asList("241"));
        headers.put("soapAction", Arrays.asList("urn:hello:world"));
        headers.put("myfruitheader", Arrays.asList("peach"));
        headers.put("mybrewheader", Arrays.asList("cappuccino", "espresso"));
        cxfMessage.put(org.apache.cxf.message.Message, headers);
        Set<Attachment> attachments = new HashSet<>();
        AttachmentImpl attachment = new AttachmentImpl("att-1", new DataHandler(new FileDataSource("pom.xml")));
        attachment.setHeader("attachment-header", "value 1");
        attachments.add(attachment);
        cxfMessage.setAttachments(attachments);
        cxfExchange.setInMessage(cxfMessage);
        cxfBinding.populateExchangeFromCxfRequest(cxfExchange, exchange);
        Map<String, Object> camelHeaders = exchange.getIn().getHeaders();
        Assert.assertNotNull(camelHeaders);
        Assert.assertEquals("urn:hello:world", camelHeaders.get("soapaction"));
        Assert.assertEquals("urn:hello:world", camelHeaders.get("SoapAction"));
        Assert.assertEquals("text/xml;charset=UTF-8", camelHeaders.get("content-type"));
        Assert.assertEquals("241", camelHeaders.get("content-length"));
        Assert.assertEquals("peach", camelHeaders.get("MyFruitHeader"));
        Assert.assertEquals(Arrays.asList("cappuccino", "espresso"), camelHeaders.get("MyBrewHeader"));
        Map<String, org.apache.camel.Attachment> camelAttachments = exchange.getIn().getAttachmentObjects();
        Assert.assertNotNull(camelAttachments);
        Assert.assertNotNull(camelAttachments.get("att-1"));
        Assert.assertEquals("value 1", camelAttachments.get("att-1").getHeader("attachment-header"));
    }

    @Test
    public void testPopupalteExchangeFromCxfRequestWithHeaderMerged() {
        DefaultCxfBinding cxfBinding = new DefaultCxfBinding();
        cxfBinding.setHeaderFilterStrategy(new DefaultHeaderFilterStrategy());
        Exchange exchange = new org.apache.camel.support.DefaultExchange(context);
        exchange.setProperty(CAMEL_CXF_PROTOCOL_HEADERS_MERGED, Boolean.TRUE);
        org.apache.cxf.message.Exchange cxfExchange = new ExchangeImpl();
        exchange.setProperty(DATA_FORMAT_PROPERTY, PAYLOAD);
        Message cxfMessage = new MessageImpl();
        Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        headers.put("myfruitheader", Arrays.asList("peach"));
        headers.put("mybrewheader", Arrays.asList("cappuccino", "espresso"));
        cxfMessage.put(org.apache.cxf.message.Message, headers);
        cxfExchange.setInMessage(cxfMessage);
        cxfBinding.populateExchangeFromCxfRequest(cxfExchange, exchange);
        Map<String, Object> camelHeaders = exchange.getIn().getHeaders();
        Assert.assertNotNull(camelHeaders);
        Assert.assertEquals("peach", camelHeaders.get("MyFruitHeader"));
        Assert.assertEquals("cappuccino, espresso", camelHeaders.get("MyBrewHeader"));
    }
}

