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
package org.apache.camel.component.bean;


import java.util.Map;
import org.apache.camel.Body;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Headers;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test of bean can propagate headers in a pipeline
 */
public class BeanPipelineTest extends ContextTestSupport {
    @Test
    public void testBeanInPipeline() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World from James");
        mock.expectedHeaderReceived("from", "James");
        template.sendBodyAndHeader("direct:input", "Hello World", "from", "Claus");
        mock.assertIsSatisfied();
    }

    public static class FooBean {
        public void onlyPlainBody(Object body) {
            Assert.assertEquals("Hello World", body);
        }
    }

    public static class BarBean {
        public void doNotUseMe(String body) {
            Assert.fail("Should not invoce me");
        }

        public void usingExchange(Exchange exchange) {
            String body = exchange.getIn().getBody(String.class);
            Assert.assertEquals("Hello World", body);
            Assert.assertEquals("Claus", exchange.getIn().getHeader("from"));
            exchange.getOut().setHeader("from", "James");
            exchange.getOut().setBody("Hello World from James");
        }
    }

    public static class BazBean {
        public void doNotUseMe(String body) {
            Assert.fail("Should not invoce me");
        }

        public void withAnnotations(@Headers
        Map<String, Object> headers, @Body
        String body) {
            Assert.assertEquals("Hello World from James", body);
            Assert.assertEquals("James", headers.get("from"));
        }
    }
}

