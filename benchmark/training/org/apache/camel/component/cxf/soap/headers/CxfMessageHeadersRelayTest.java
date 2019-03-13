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
package org.apache.camel.component.cxf.soap.headers;


import CxfConstants.OPERATION_NAME;
import Direction.DIRECTION_IN;
import Direction.DIRECTION_OUT;
import Header.HEADER_LIST;
import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Holder;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.cxf.CXFTestSupport;
import org.apache.camel.component.cxf.CxfEndpoint;
import org.apache.camel.component.cxf.CxfPayload;
import org.apache.camel.component.cxf.common.header.CxfHeaderFilterStrategy;
import org.apache.camel.component.cxf.common.header.MessageHeaderFilter;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.staxutils.StaxUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import static HeaderService.SERVICE;


/**
 * This test suite verifies message header filter features
 */
// END SNIPPET: InsertResponseOutHeaderProcessor
@ContextConfiguration
public class CxfMessageHeadersRelayTest extends AbstractJUnit4SpringContextTests {
    static int portE1 = CXFTestSupport.getPort("CxfMessageHeadersRelayTest.1");

    static int portE2 = CXFTestSupport.getPort("CxfMessageHeadersRelayTest.2");

    static int portE3 = CXFTestSupport.getPort("CxfMessageHeadersRelayTest.3");

    static int portE4 = CXFTestSupport.getPort("CxfMessageHeadersRelayTest.4");

    static int portE5 = CXFTestSupport.getPort("CxfMessageHeadersRelayTest.5");

    @Autowired
    protected CamelContext context;

    protected ProducerTemplate template;

    private Endpoint relayEndpoint;

    private Endpoint noRelayEndpoint;

    private Endpoint relayEndpointWithInsertion;

    @Test
    public void testInHeaderCXFClientRelay() throws Exception {
        HeaderService s = new HeaderService(getClass().getClassLoader().getResource("soap_header.wsdl"), SERVICE);
        HeaderTester proxy = s.getSoapPortRelay();
        ((BindingProvider) (proxy)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CxfMessageHeadersRelayTest.portE1)) + "/CxfMessageHeadersRelayTest/HeaderService/"));
        InHeader me = new InHeader();
        me.setRequestType("CXF user");
        InHeaderResponse response = proxy.inHeader(me, Constants.IN_HEADER_DATA);
        Assert.assertTrue("Expected in band header to propagate but it didn't", response.getResponseType().equals("pass"));
    }

    @Test
    public void testOutHeaderCXFClientRelay() throws Exception {
        HeaderService s = new HeaderService(getClass().getClassLoader().getResource("soap_header.wsdl"), SERVICE);
        HeaderTester proxy = s.getSoapPortRelay();
        ((BindingProvider) (proxy)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CxfMessageHeadersRelayTest.portE1)) + "/CxfMessageHeadersRelayTest/HeaderService/"));
        OutHeader me = new OutHeader();
        me.setRequestType("CXF user");
        Holder<OutHeaderResponse> result = new Holder(new OutHeaderResponse());
        Holder<SOAPHeaderData> header = new Holder(new SOAPHeaderData());
        proxy.outHeader(me, result, header);
        Assert.assertTrue("Expected in band header to propagate but it didn't", result.value.getResponseType().equals("pass"));
        Assert.assertTrue(("Expected in band response header to propagate but it either didn't " + " or its contents do not match"), Constants.equals(Constants.OUT_HEADER_DATA, header.value));
    }

    @Test
    public void testInOutHeaderCXFClientRelay() throws Exception {
        HeaderService s = new HeaderService(getClass().getClassLoader().getResource("soap_header.wsdl"), SERVICE);
        HeaderTester proxy = s.getSoapPortRelay();
        ((BindingProvider) (proxy)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CxfMessageHeadersRelayTest.portE1)) + "/CxfMessageHeadersRelayTest/HeaderService/"));
        InoutHeader me = new InoutHeader();
        me.setRequestType("CXF user");
        Holder<SOAPHeaderData> header = new Holder(Constants.IN_OUT_REQUEST_HEADER_DATA);
        InoutHeaderResponse result = proxy.inoutHeader(me, header);
        Assert.assertTrue("Expected in band header to propagate but it didn't", result.getResponseType().equals("pass"));
        Assert.assertTrue(("Expected in band response header to propagate but it either didn't " + " or its contents do not match"), Constants.equals(Constants.IN_OUT_RESPONSE_HEADER_DATA, header.value));
    }

    @Test
    public void testInOutOfBandHeaderCXFClientRelay() throws Exception {
        HeaderService s = new HeaderService(getClass().getClassLoader().getResource("soap_header.wsdl"), SERVICE);
        HeaderTester proxy = s.getSoapPortRelay();
        ((BindingProvider) (proxy)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CxfMessageHeadersRelayTest.portE1)) + "/CxfMessageHeadersRelayTest/HeaderService/"));
        CxfMessageHeadersRelayTest.addOutOfBoundHeader(proxy, false);
        Me me = new Me();
        me.setFirstName("john");
        me.setLastName("Doh");
        Me response = proxy.inOutOfBandHeader(me);
        Assert.assertTrue("Expected the out of band header to propagate but it didn't", response.getFirstName().equals("pass"));
    }

    @Test
    public void testInoutOutOfBandHeaderCXFClientRelay() throws Exception {
        HeaderService s = new HeaderService(getClass().getClassLoader().getResource("soap_header.wsdl"), SERVICE);
        HeaderTester proxy = s.getSoapPortRelay();
        ((BindingProvider) (proxy)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CxfMessageHeadersRelayTest.portE1)) + "/CxfMessageHeadersRelayTest/HeaderService/"));
        CxfMessageHeadersRelayTest.addOutOfBoundHeader(proxy, false);
        Me me = new Me();
        me.setFirstName("john");
        me.setLastName("Doh");
        Me response = proxy.inoutOutOfBandHeader(me);
        Assert.assertTrue("Expected the out of band header to propagate but it didn't", response.getFirstName().equals("pass"));
        CxfMessageHeadersRelayTest.validateReturnedOutOfBandHeader(proxy);
    }

    @Test
    public void testInoutOutOfBandHeaderCXFClientRelayWithHeaderInsertion() throws Exception {
        HeaderService s = new HeaderService(getClass().getClassLoader().getResource("soap_header.wsdl"), SERVICE);
        HeaderTester proxy = s.getSoapPortRelayWithInsertion();
        ((BindingProvider) (proxy)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CxfMessageHeadersRelayTest.portE2)) + "/CxfMessageHeadersRelayTest/HeaderService/"));
        CxfMessageHeadersRelayTest.addOutOfBoundHeader(proxy, false);
        Me me = new Me();
        me.setFirstName("john");
        me.setLastName("Doh");
        Me response = proxy.inoutOutOfBandHeader(me);
        Assert.assertTrue("Expected the out of band header to propagate but it didn't", response.getFirstName().equals("pass"));
        InvocationHandler handler = Proxy.getInvocationHandler(proxy);
        BindingProvider bp = null;
        if (!(handler instanceof BindingProvider)) {
            Assert.fail("Unable to cast dynamic proxy InocationHandler to BindingProvider type");
        }
        bp = ((BindingProvider) (handler));
        Map<String, Object> responseContext = bp.getResponseContext();
        CxfMessageHeadersRelayTest.validateReturnedOutOfBandHeaderWithInsertion(responseContext, true);
    }

    @Test
    public void testOutOutOfBandHeaderCXFClientRelay() throws Exception {
        HeaderService s = new HeaderService(getClass().getClassLoader().getResource("soap_header.wsdl"), SERVICE);
        HeaderTester proxy = s.getSoapPortRelay();
        ((BindingProvider) (proxy)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CxfMessageHeadersRelayTest.portE1)) + "/CxfMessageHeadersRelayTest/HeaderService/"));
        Me me = new Me();
        me.setFirstName("john");
        me.setLastName("Doh");
        Me response = proxy.outOutOfBandHeader(me);
        Assert.assertTrue("Expected the out of band header to propagate but it didn't", response.getFirstName().equals("pass"));
        CxfMessageHeadersRelayTest.validateReturnedOutOfBandHeader(proxy);
    }

    @Test
    public void testInOutOfBandHeaderCXFClientNoRelay() throws Exception {
        HeaderService s = new HeaderService(getClass().getClassLoader().getResource("soap_header.wsdl"), SERVICE);
        HeaderTester proxy = s.getSoapPortNoRelay();
        ((BindingProvider) (proxy)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CxfMessageHeadersRelayTest.portE3)) + "/CxfMessageHeadersRelayTest/HeaderService/"));
        CxfMessageHeadersRelayTest.addOutOfBoundHeader(proxy, false);
        Me me = new Me();
        me.setFirstName("john");
        me.setLastName("Doh");
        Me response = proxy.inOutOfBandHeader(me);
        Assert.assertTrue("Expected the in out of band header *not* to propagate but it did", response.getFirstName().equals("pass"));
    }

    @Test
    public void testOutOutOfBandHeaderCXFClientNoRelay() throws Exception {
        HeaderService s = new HeaderService(getClass().getClassLoader().getResource("soap_header.wsdl"), SERVICE);
        HeaderTester proxy = s.getSoapPortNoRelay();
        ((BindingProvider) (proxy)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CxfMessageHeadersRelayTest.portE3)) + "/CxfMessageHeadersRelayTest/HeaderService/"));
        Me me = new Me();
        me.setFirstName("john");
        me.setLastName("Doh");
        Thread.sleep(5000);
        Me response = proxy.outOutOfBandHeader(me);
        Assert.assertTrue("Expected the out out of band header *not* to propagate but it did", response.getFirstName().equals("pass"));
        CxfMessageHeadersRelayTest.validateReturnedOutOfBandHeader(proxy, false);
    }

    @Test
    public void testInoutOutOfBandHeaderCXFClientNoRelay() throws Exception {
        HeaderService s = new HeaderService(getClass().getClassLoader().getResource("soap_header.wsdl"), SERVICE);
        HeaderTester proxy = s.getSoapPortNoRelay();
        ((BindingProvider) (proxy)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CxfMessageHeadersRelayTest.portE3)) + "/CxfMessageHeadersRelayTest/HeaderService/"));
        CxfMessageHeadersRelayTest.addOutOfBoundHeader(proxy, false);
        Me me = new Me();
        me.setFirstName("john");
        me.setLastName("Doh");
        Me response = proxy.inoutOutOfBandHeader(me);
        Assert.assertTrue("Expected the in out of band header to *not* propagate but it did", response.getFirstName().equals("pass"));
        CxfMessageHeadersRelayTest.validateReturnedOutOfBandHeader(proxy, false);
    }

    @Test
    public void testInHeaderCXFClientNoRelay() throws Exception {
        HeaderService s = new HeaderService(getClass().getClassLoader().getResource("soap_header.wsdl"), SERVICE);
        HeaderTester proxy = s.getSoapPortNoRelay();
        ((BindingProvider) (proxy)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CxfMessageHeadersRelayTest.portE3)) + "/CxfMessageHeadersRelayTest/HeaderService/"));
        InHeader me = new InHeader();
        me.setRequestType("CXF user");
        InHeaderResponse response = null;
        try {
            response = proxy.inHeader(me, Constants.IN_HEADER_DATA);
        } catch (Exception e) {
            // do nothing
        }
        Assert.assertTrue("Expected in in band header *not* to propagate but it did", response.getResponseType().equals("pass"));
    }

    @Test
    public void testOutHeaderCXFClientNoRelay() throws Exception {
        Thread.sleep(5000);
        HeaderService s = new HeaderService(getClass().getClassLoader().getResource("soap_header.wsdl"), SERVICE);
        HeaderTester proxy = s.getSoapPortNoRelay();
        ((BindingProvider) (proxy)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CxfMessageHeadersRelayTest.portE3)) + "/CxfMessageHeadersRelayTest/HeaderService/"));
        OutHeader me = new OutHeader();
        me.setRequestType("CXF user");
        Holder<OutHeaderResponse> result = new Holder(new OutHeaderResponse());
        Holder<SOAPHeaderData> header = new Holder(new SOAPHeaderData());
        try {
            proxy.outHeader(me, result, header);
        } catch (Exception e) {
            // do nothing
        }
        Assert.assertTrue("Ultimate remote HeaderTester.outHeader() destination was not reached", result.value.getResponseType().equals("pass"));
        Assert.assertTrue("Expected in band response header *not* to propagate but it did", ((header.value) == null));
    }

    @Test
    public void testInoutHeaderCXFClientNoRelay() throws Exception {
        HeaderService s = new HeaderService(getClass().getClassLoader().getResource("soap_header.wsdl"), SERVICE);
        HeaderTester proxy = s.getSoapPortNoRelay();
        ((BindingProvider) (proxy)).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, (("http://localhost:" + (CxfMessageHeadersRelayTest.portE3)) + "/CxfMessageHeadersRelayTest/HeaderService/"));
        InoutHeader me = new InoutHeader();
        me.setRequestType("CXF user");
        Holder<SOAPHeaderData> header = new Holder(Constants.IN_OUT_REQUEST_HEADER_DATA);
        InoutHeaderResponse result = null;
        try {
            result = proxy.inoutHeader(me, header);
        } catch (Exception e) {
            // do nothing
        }
        Assert.assertTrue("Expected in band out header *not* to propagate but it did", result.getResponseType().equals("pass"));
        Assert.assertTrue("Expected in band response header *not* to propagate but did", ((header.value) == null));
    }

    @Test
    public void testInoutHeaderCXFClientNoServiceClassNoRelay() throws Exception {
        // TODO: Fix this test later
        QName qname = QName.valueOf("{http://apache.org/camel/component/cxf/soap/headers}SOAPHeaderInfo");
        String uri = "cxf:bean:routerNoRelayNoServiceClassEndpoint?headerFilterStrategy=#dropAllMessageHeadersStrategy";
        String requestHeader = "<ns2:SOAPHeaderInfo xmlns:ns2=\"http://apache.org/camel/" + (("component/cxf/soap/headers\"><originator>CxfSoapHeaderRoutePropagationTest.testInOutHeader Requestor" + "</originator><message>Invoking CxfSoapHeaderRoutePropagationTest.testInOutHeader() Request") + "</message></ns2:SOAPHeaderInfo>");
        String requestBody = "<ns2:inoutHeader xmlns:ns2=\"http://apache.org/camel/component/cxf/soap/headers\">" + "<requestType>CXF user</requestType></ns2:inoutHeader>";
        List<Source> elements = new ArrayList<>();
        elements.add(new DOMSource(StaxUtils.read(new StringReader(requestBody)).getDocumentElement()));
        final List<SoapHeader> headers = new ArrayList<>();
        headers.add(new SoapHeader(qname, StaxUtils.read(new StringReader(requestHeader)).getDocumentElement()));
        final CxfPayload<SoapHeader> cxfPayload = new CxfPayload(headers, elements, null);
        Exchange exchange = template.request(uri, new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(cxfPayload);
                exchange.getIn().setHeader(OPERATION_NAME, "inoutHeader");
                exchange.getIn().setHeader(HEADER_LIST, headers);
            }
        });
        CxfPayload<?> out = exchange.getOut().getBody(CxfPayload.class);
        Assert.assertEquals(1, out.getBodySources().size());
        Assert.assertTrue(((out.getBodySources().get(0)) instanceof DOMSource));
        Assert.assertEquals(0, out.getHeaders().size());
        String responseExp = "<ns2:inoutHeaderResponse xmlns:ns2=\"http://apache.org/camel/" + ("component/cxf/soap/headers\"><responseType>pass</responseType>" + "</ns2:inoutHeaderResponse>");
        String response = StaxUtils.toString(out.getBody().get(0));
        // REVISIT use a more reliable comparison to tolerate some namespaces being added to the root element
        Assert.assertTrue(response, ((response.startsWith(responseExp.substring(0, 87))) && (response.endsWith(responseExp.substring(88, responseExp.length())))));
    }

    @Test
    public void testMessageHeadersRelaysSpringContext() throws Exception {
        CxfEndpoint endpoint = context.getEndpoint("cxf:bean:serviceExtraRelays?headerFilterStrategy=#customMessageFilterStrategy", CxfEndpoint.class);
        CxfHeaderFilterStrategy strategy = ((CxfHeaderFilterStrategy) (endpoint.getHeaderFilterStrategy()));
        List<MessageHeaderFilter> filters = strategy.getMessageHeaderFilters();
        Assert.assertEquals("Expected number of filters ", 2, filters.size());
        Map<String, MessageHeaderFilter> messageHeaderFilterMap = strategy.getMessageHeaderFiltersMap();
        for (String ns : new CustomHeaderFilter().getActivationNamespaces()) {
            Assert.assertEquals(("Expected a filter class for namespace: " + ns), CustomHeaderFilter.class, messageHeaderFilterMap.get(ns).getClass());
        }
    }

    @Test
    public void testInOutOfBandHeaderCamelTemplateDirect() throws Exception {
        doTestInOutOfBandHeaderCamelTemplate("direct:directProducer");
    }

    @Test
    public void testOutOutOfBandHeaderCamelTemplateDirect() throws Exception {
        doTestOutOutOfBandHeaderCamelTemplate("direct:directProducer");
    }

    @Test
    public void testInOutOutOfBandHeaderCamelTemplateDirect() throws Exception {
        doTestInOutOutOfBandHeaderCamelTemplate("direct:directProducer");
    }

    @Test
    public void testInOutOfBandHeaderCamelTemplateRelay() throws Exception {
        doTestInOutOfBandHeaderCamelTemplate("direct:relayProducer");
    }

    @Test
    public void testOutOutOfBandHeaderCamelTemplateRelay() throws Exception {
        doTestOutOutOfBandHeaderCamelTemplate("direct:relayProducer");
    }

    @Test
    public void testInOutOutOfBandHeaderCamelTemplateRelay() throws Exception {
        doTestInOutOutOfBandHeaderCamelTemplate("direct:relayProducer");
    }

    public static class InsertRequestOutHeaderProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            List<SoapHeader> soapHeaders = CastUtils.cast(((List<?>) (exchange.getIn().getHeader(HEADER_LIST))));
            // Insert a new header
            String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><outofbandHeader " + (("xmlns=\"http://cxf.apache.org/outofband/Header\" hdrAttribute=\"testHdrAttribute\" " + "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" soap:mustUnderstand=\"1\">") + "<name>New_testOobHeader</name><value>New_testOobHeaderValue</value></outofbandHeader>");
            SoapHeader newHeader = new SoapHeader(soapHeaders.get(0).getName(), StaxUtils.read(new StringReader(xml)).getDocumentElement());
            // make sure direction is IN since it is a request message.
            newHeader.setDirection(DIRECTION_IN);
            // newHeader.setMustUnderstand(false);
            soapHeaders.add(newHeader);
        }
    }

    // START SNIPPET: InsertResponseOutHeaderProcessor
    public static class InsertResponseOutHeaderProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            // You should be able to get the header if exchange is routed from camel-cxf endpoint
            List<SoapHeader> soapHeaders = CastUtils.cast(((List<?>) (exchange.getIn().getHeader(HEADER_LIST))));
            if (soapHeaders == null) {
                // we just create a new soap headers in case the header is null
                soapHeaders = new ArrayList();
            }
            // Insert a new header
            String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><outofbandHeader " + (("xmlns=\"http://cxf.apache.org/outofband/Header\" hdrAttribute=\"testHdrAttribute\" " + "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" soap:mustUnderstand=\"1\">") + "<name>New_testOobHeader</name><value>New_testOobHeaderValue</value></outofbandHeader>");
            SoapHeader newHeader = new SoapHeader(soapHeaders.get(0).getName(), StaxUtils.read(new StringReader(xml)).getDocumentElement());
            // make sure direction is OUT since it is a response message.
            newHeader.setDirection(DIRECTION_OUT);
            // newHeader.setMustUnderstand(false);
            soapHeaders.add(newHeader);
        }
    }
}

