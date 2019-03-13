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
package org.apache.camel.language.xpath;


import javax.xml.xpath.XPathFactory;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;


/**
 *
 */
public class XPathLanguageTest extends CamelSpringTestSupport {
    private static final String KEY = ((XPathFactory.DEFAULT_PROPERTY_NAME) + ":") + "http://java.sun.com/jaxp/xpath/dom";

    private boolean jvmAdequate = true;

    private String oldPropertyValue;

    @Test
    public void testSpringDSLXPathSaxonFlag() throws Exception {
        if (!(jvmAdequate)) {
            return;
        }
        MockEndpoint mockEndpoint = getMockEndpoint("mock:testSaxonWithFlagResult");
        mockEndpoint.expectedMessageCount(1);
        template.sendBody("direct:testSaxonWithFlag", "<a>Hello|there|Camel</a>");
        assertMockEndpointsSatisfied();
        Exchange received = mockEndpoint.getExchanges().get(0);
        Object body = received.getIn().getBody();
        assertEquals("Hello", body);
    }

    @Test
    public void testSpringDSLXPathFactory() throws Exception {
        if (!(jvmAdequate)) {
            return;
        }
        MockEndpoint mockEndpoint = getMockEndpoint("mock:testSaxonWithFactoryResult");
        mockEndpoint.expectedMessageCount(1);
        template.sendBody("direct:testSaxonWithFactory", "<a>Hello|there|Camel</a>");
        assertMockEndpointsSatisfied();
        Exchange received = mockEndpoint.getExchanges().get(0);
        Object body = received.getIn().getBody();
        assertEquals("Hello", body);
    }

    @Test
    public void testSpringDSLXPathSaxonFlagPredicate() throws Exception {
        if (!(jvmAdequate)) {
            return;
        }
        MockEndpoint mockEndpoint = getMockEndpoint("mock:testSaxonWithFlagResultPredicate");
        mockEndpoint.expectedMessageCount(1);
        template.sendBody("direct:testSaxonWithFlagPredicate", "<a>Hello|there|Camel</a>");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSpringDSLXPathFactoryPredicate() throws Exception {
        if (!(jvmAdequate)) {
            return;
        }
        MockEndpoint mockEndpoint = getMockEndpoint("mock:testSaxonWithFactoryResultPredicate");
        mockEndpoint.expectedMessageCount(1);
        template.sendBody("direct:testSaxonWithFactoryPredicate", "<a>Hello|there|Camel</a>");
        assertMockEndpointsSatisfied();
    }
}

