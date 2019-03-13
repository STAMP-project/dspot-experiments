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
package org.apache.camel.management;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class CamelChoiceWithManagementTest extends ContextTestSupport {
    private MockEndpoint a;

    private MockEndpoint b;

    private MockEndpoint c;

    private MockEndpoint d;

    private MockEndpoint e;

    @Test
    public void testFirstChoiceRoute() throws Exception {
        final String body = "<one/>";
        a.expectedBodiesReceived(body);
        a.expectedHeaderReceived("CBR1", "Yes");
        c.expectedBodiesReceived(body);
        c.expectedHeaderReceived("CBR1", "Yes");
        c.expectedHeaderReceived("Validation", "Yes");
        expectsMessageCount(0, b, d, e);
        template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(body);
                exchange.getIn().setHeader("CBR1", "Yes");
            }
        });
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testOtherwise() throws Exception {
        final String body = "<None/>";
        e.expectedBodiesReceived(body);
        expectsMessageCount(0, a, b, c, d);
        template.send("direct:start", new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(body);
            }
        });
        assertMockEndpointsSatisfied();
    }
}

