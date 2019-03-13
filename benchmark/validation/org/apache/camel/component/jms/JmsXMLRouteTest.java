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
package org.apache.camel.component.jms;


import java.io.FileInputStream;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.camel.StringSource;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * For unit testing with XML streams that can be troublesome with the StreamCache
 */
public class JmsXMLRouteTest extends CamelTestSupport {
    private static final String TEST_LONDON = "src/test/data/message1.xml";

    private static final String TEST_TAMPA = "src/test/data/message2.xml";

    @Test
    public void testLondonWithFileStreamAsObject() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:london");
        mock.expectedMessageCount(1);
        mock.message(0).body(String.class).contains("James");
        Source source = new StreamSource(new FileInputStream(JmsXMLRouteTest.TEST_LONDON));
        assertNotNull(source);
        template.sendBody("direct:object", source);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testLondonWithFileStreamAsBytes() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:london");
        mock.expectedMessageCount(1);
        mock.message(0).body(String.class).contains("James");
        Source source = new StreamSource(new FileInputStream(JmsXMLRouteTest.TEST_LONDON));
        assertNotNull(source);
        template.sendBody("direct:bytes", source);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testLondonWithFileStreamAsDefault() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:london");
        mock.expectedMessageCount(1);
        mock.message(0).body(String.class).contains("James");
        Source source = new StreamSource(new FileInputStream(JmsXMLRouteTest.TEST_LONDON));
        assertNotNull(source);
        template.sendBody("direct:default", source);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testTampaWithFileStreamAsObject() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:tampa");
        mock.expectedMessageCount(1);
        mock.message(0).body(String.class).contains("Hiram");
        Source source = new StreamSource(new FileInputStream(JmsXMLRouteTest.TEST_TAMPA));
        assertNotNull(source);
        template.sendBody("direct:object", source);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testTampaWithFileStreamAsBytes() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:tampa");
        mock.expectedMessageCount(1);
        mock.message(0).body(String.class).contains("Hiram");
        Source source = new StreamSource(new FileInputStream(JmsXMLRouteTest.TEST_TAMPA));
        assertNotNull(source);
        template.sendBody("direct:bytes", source);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testTampaWithFileStreamAsDefault() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:tampa");
        mock.expectedMessageCount(1);
        mock.message(0).body(String.class).contains("Hiram");
        Source source = new StreamSource(new FileInputStream(JmsXMLRouteTest.TEST_TAMPA));
        assertNotNull(source);
        template.sendBody("direct:default", source);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testLondonWithStringSourceAsObject() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:london");
        mock.expectedMessageCount(1);
        mock.message(0).body(String.class).contains("James");
        Source source = new StringSource(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (((("<person user=\"james\">\n" + "  <firstName>James</firstName>\n") + "  <lastName>Strachan</lastName>\n") + "  <city>London</city>\n") + "</person>")));
        assertNotNull(source);
        template.sendBody("direct:object", source);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testLondonWithStringSourceAsBytes() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:london");
        mock.expectedMessageCount(1);
        mock.message(0).body(String.class).contains("James");
        Source source = new StringSource(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (((("<person user=\"james\">\n" + "  <firstName>James</firstName>\n") + "  <lastName>Strachan</lastName>\n") + "  <city>London</city>\n") + "</person>")));
        assertNotNull(source);
        template.sendBody("direct:bytes", source);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testLondonWithStringSourceAsDefault() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:london");
        mock.expectedMessageCount(1);
        mock.message(0).body(String.class).contains("James");
        Source source = new StringSource(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (((("<person user=\"james\">\n" + "  <firstName>James</firstName>\n") + "  <lastName>Strachan</lastName>\n") + "  <city>London</city>\n") + "</person>")));
        assertNotNull(source);
        template.sendBody("direct:default", source);
        assertMockEndpointsSatisfied();
    }
}

