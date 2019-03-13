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
package org.apache.camel.component.jetty;


import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.junit4.TestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/org/apache/camel/component/jetty/jetty-https.xml" })
public class SpringHttpsRouteTest {
    private static final String NULL_VALUE_MARKER = CamelTestSupport.class.getCanonicalName();

    protected String expectedBody = "<hello>world!</hello>";

    protected String pwd = "changeit";

    protected Properties originalValues = new Properties();

    protected transient Logger log = LoggerFactory.getLogger(TestSupport.class);

    @EndpointInject(uri = "mock:a")
    MockEndpoint mockEndpoint;

    @Produce
    private ProducerTemplate template;

    private Integer port;

    @Test
    public void testEndpoint() throws Exception {
        mockEndpoint.reset();
        mockEndpoint.expectedBodiesReceived(expectedBody);
        template.sendBodyAndHeader((("https://localhost:" + (port)) + "/test"), expectedBody, "Content-Type", "application/xml");
        mockEndpoint.assertIsSatisfied();
        List<Exchange> list = mockEndpoint.getReceivedExchanges();
        Exchange exchange = list.get(0);
        Assert.assertNotNull("exchange", exchange);
        Message in = exchange.getIn();
        Assert.assertNotNull("in", in);
        Map<String, Object> headers = in.getHeaders();
        log.info(("Headers: " + headers));
        Assert.assertTrue(("Should be more than one header but was: " + headers), ((headers.size()) > 0));
    }

    @Test
    public void testEndpointWithoutHttps() {
        mockEndpoint.reset();
        try {
            template.sendBodyAndHeader((("http://localhost:" + (port)) + "/test"), expectedBody, "Content-Type", "application/xml");
            Assert.fail("expect exception on access to https endpoint via http");
        } catch (RuntimeCamelException expected) {
        }
        Assert.assertTrue("mock endpoint was not called", mockEndpoint.getExchanges().isEmpty());
    }
}

