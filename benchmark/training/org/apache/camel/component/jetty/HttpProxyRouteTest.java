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


import Exchange.CONTENT_TYPE;
import org.apache.camel.util.StopWatch;
import org.apache.camel.util.TimeUtils;
import org.junit.Test;


public class HttpProxyRouteTest extends BaseJettyTest {
    private int size = 10;

    @Test
    public void testHttpProxy() throws Exception {
        log.info((("Sending " + (size)) + " messages to a http endpoint which is proxied/bridged"));
        StopWatch watch = new StopWatch();
        for (int i = 0; i < (size); i++) {
            String out = template.requestBody(("http://localhost:{{port}}?foo=" + i), null, String.class);
            assertEquals(("Bye " + i), out);
        }
        log.info(("Time taken: " + (TimeUtils.printDuration(watch.taken()))));
    }

    @Test
    public void testHttpProxyWithDifferentPath() throws Exception {
        String out = template.requestBody("http://localhost:{{port}}/proxy", null, String.class);
        assertEquals("/otherEndpoint", out);
        out = template.requestBody("http://localhost:{{port}}/proxy/path", null, String.class);
        assertEquals("/otherEndpoint/path", out);
    }

    @Test
    public void testHttpProxyHostHeader() throws Exception {
        String out = template.requestBody("http://localhost:{{port}}/proxyServer", null, String.class);
        assertEquals("Get a wrong host header", ("localhost:" + (BaseJettyTest.getPort2())), out);
    }

    @Test
    public void testHttpProxyFormHeader() throws Exception {
        String out = template.requestBodyAndHeader("http://localhost:{{port}}/form", "username=abc&pass=password", CONTENT_TYPE, "application/x-www-form-urlencoded", String.class);
        assertEquals("Get a wrong response message", "username=abc&pass=password", out);
    }
}

