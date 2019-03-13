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
package org.apache.camel.processor;


import java.util.HashMap;
import java.util.Map;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class RecipientListBeanTest extends ContextTestSupport {
    @Test
    public void testRecipientListWithBean() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello c");
        String out = template.requestBody("direct:start", "direct:a,direct:b,direct:c", String.class);
        Assert.assertEquals("Hello c", out);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRecipientListWithParams() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello b");
        Map<String, Object> headers = new HashMap<>();
        headers.put("one", 21);
        headers.put("two", "direct:a,direct:b,direct:c");
        String out = template.requestBodyAndHeaders("direct:params", "Hello World", headers, String.class);
        Assert.assertEquals("Hello b", out);
        assertMockEndpointsSatisfied();
    }

    public class MyBean {
        public String[] foo(String body) {
            return body.split(",");
        }

        public String bar(int one, String two) {
            Assert.assertEquals(21, one);
            Assert.assertEquals("direct:a,direct:b,direct:c", two);
            return "direct:c,direct:b";
        }
    }
}

