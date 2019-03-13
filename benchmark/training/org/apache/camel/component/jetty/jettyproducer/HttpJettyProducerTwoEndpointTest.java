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
package org.apache.camel.component.jetty.jettyproducer;


import org.apache.camel.Exchange;
import org.apache.camel.component.jetty.BaseJettyTest;
import org.junit.Test;


public class HttpJettyProducerTwoEndpointTest extends BaseJettyTest {
    @Test
    public void testTwoEndpoints() throws Exception {
        // these tests does not run well on Windows
        if (isPlatform("windows")) {
            return;
        }
        // give Jetty time to startup properly
        Thread.sleep(1000);
        Exchange a = template.request("direct:a", null);
        assertNotNull(a);
        Exchange b = template.request("direct:b", null);
        assertNotNull(b);
        assertEquals("Bye cheese", a.getOut().getBody(String.class));
        assertEquals(246, a.getOut().getHeader("foo", Integer.class).intValue());
        assertEquals("Bye cake", b.getOut().getBody(String.class));
        assertEquals(912, b.getOut().getHeader("foo", Integer.class).intValue());
        assertEquals(5, context.getEndpoints().size());
    }
}

