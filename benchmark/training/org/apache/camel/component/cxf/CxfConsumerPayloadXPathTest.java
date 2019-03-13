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


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.XPathBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.cxf.binding.soap.SoapHeader;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


public class CxfConsumerPayloadXPathTest extends CamelTestSupport {
    public static final String HEADER_SIZE = "tstsize";

    @Test
    public void size1XPathStringResultTest() throws Exception {
        simpleTest(1, new CxfConsumerPayloadXPathTest.TestRouteWithXPathStringResultBuilder());
    }

    @Test
    public void size100XPathStringResultTest() throws Exception {
        simpleTest(100, new CxfConsumerPayloadXPathTest.TestRouteWithXPathStringResultBuilder());
    }

    @Test
    public void size1000XPathStringResultTest() throws Exception {
        simpleTest(1000, new CxfConsumerPayloadXPathTest.TestRouteWithXPathStringResultBuilder());
    }

    @Test
    public void size10000XPathStringResultTest() throws Exception {
        simpleTest(10000, new CxfConsumerPayloadXPathTest.TestRouteWithXPathStringResultBuilder());
    }

    @Test
    public void size1XPathTest() throws Exception {
        simpleTest(1, new CxfConsumerPayloadXPathTest.TestRouteWithXPathBuilder());
    }

    @Test
    public void size100XPathTest() throws Exception {
        simpleTest(100, new CxfConsumerPayloadXPathTest.TestRouteWithXPathBuilder());
    }

    @Test
    public void size1000XPathTest() throws Exception {
        simpleTest(1000, new CxfConsumerPayloadXPathTest.TestRouteWithXPathBuilder());
    }

    @Test
    public void size10000XPathTest() throws Exception {
        simpleTest(10000, new CxfConsumerPayloadXPathTest.TestRouteWithXPathBuilder());
    }

    // the textnode appears to have siblings!
    @Test
    public void size10000DomTest() throws Exception {
        simpleTest(10000, new CxfConsumerPayloadXPathTest.TestRouteWithDomBuilder());
    }

    @Test
    public void size1000DomFirstTest() throws Exception {
        simpleTest(1000, new CxfConsumerPayloadXPathTest.TestRouteWithDomFirstOneOnlyBuilder());
    }

    private class TestRouteWithXPathBuilder extends CxfConsumerPayloadXPathTest.BaseRouteBuilder {
        @Override
        public void configure() {
            from((("cxf://" + (testAddress)) + "?dataFormat=PAYLOAD")).streamCaching().process(new CxfConsumerPayloadXPathTest.XPathProcessor()).process(new CxfConsumerPayloadXPathTest.ResponseProcessor());
        }
    }

    private class TestRouteWithXPathStringResultBuilder extends CxfConsumerPayloadXPathTest.BaseRouteBuilder {
        @Override
        public void configure() {
            from((("cxf://" + (testAddress)) + "?dataFormat=PAYLOAD")).streamCaching().process(new CxfConsumerPayloadXPathTest.XPathStringResultProcessor()).process(new CxfConsumerPayloadXPathTest.ResponseProcessor());
        }
    }

    private class TestRouteWithDomFirstOneOnlyBuilder extends CxfConsumerPayloadXPathTest.BaseRouteBuilder {
        @Override
        public void configure() {
            from((("cxf://" + (testAddress)) + "?dataFormat=PAYLOAD")).streamCaching().process(new CxfConsumerPayloadXPathTest.DomFirstOneOnlyProcessor()).process(new CxfConsumerPayloadXPathTest.ResponseProcessor());
        }
    }

    private class TestRouteWithDomBuilder extends CxfConsumerPayloadXPathTest.BaseRouteBuilder {
        @Override
        public void configure() {
            from((("cxf://" + (testAddress)) + "?dataFormat=PAYLOAD")).streamCaching().process(new CxfConsumerPayloadXPathTest.DomProcessor()).process(new CxfConsumerPayloadXPathTest.ResponseProcessor());
        }
    }

    // implementation simular to xpath() in route: no data loss
    private class XPathStringResultProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            Object obj = exchange.getIn().getBody();
            // xpath expression directly results in a: String
            String content = ((String) (XPathBuilder.xpath("//xml/text()").stringResult().evaluate(context, obj, Object.class)));
            exchange.getOut().setBody(content);
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        }
    }

    // this version leads to data loss
    private class XPathProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            Object obj = exchange.getIn().getBody();
            // xpath expression results in a: net.sf.saxon.dom.DOMNodeList
            // after which it is converted to a String
            String content = XPathBuilder.xpath("//xml/text()").evaluate(context, obj, String.class);
            exchange.getOut().setBody(content);
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        }
    }

    // this version leads to data loss
    private class DomFirstOneOnlyProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            Object obj = exchange.getIn().getBody();
            @SuppressWarnings("unchecked")
            CxfPayload<SoapHeader> payload = ((CxfPayload<SoapHeader>) (obj));
            Element el = payload.getBody().get(0);
            Text textnode = ((Text) (el.getFirstChild()));
            exchange.getOut().setBody(textnode.getNodeValue());
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        }
    }

    private class DomProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            Object obj = exchange.getIn().getBody();
            @SuppressWarnings("unchecked")
            CxfPayload<SoapHeader> payload = ((CxfPayload<SoapHeader>) (obj));
            Element el = payload.getBody().get(0);
            Text textnode = ((Text) (el.getFirstChild()));
            StringBuilder b = new StringBuilder();
            b.append(textnode.getNodeValue());
            textnode = ((Text) (textnode.getNextSibling()));
            while (textnode != null) {
                // the textnode appears to have siblings!
                b.append(textnode.getNodeValue());
                textnode = ((Text) (textnode.getNextSibling()));
            } 
            exchange.getOut().setBody(b.toString());
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
        }
    }

    private class ResponseProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            Object obj = exchange.getIn().getBody();
            String content = ((String) (obj));
            String msgOut = constructSoapMessage(content);
            exchange.getOut().setBody(msgOut);
            exchange.getOut().setHeaders(exchange.getIn().getHeaders());
            exchange.getOut().setHeader(CxfConsumerPayloadXPathTest.HEADER_SIZE, ("" + (content.length())));
        }
    }

    private abstract class BaseRouteBuilder extends RouteBuilder {
        protected final String testAddress = getAvailableUrl("test");

        public String getTestAddress() {
            return testAddress;
        }
    }
}

