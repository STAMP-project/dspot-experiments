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
package org.apache.camel.component.http4;


import java.util.List;
import org.apache.camel.Exchange;
import org.apache.http.impl.bootstrap.HttpServer;
import org.junit.Test;


/**
 *
 */
public class HttpProducerTwoParametersWithSameKeyTest extends BaseHttpTest {
    private HttpServer localServer;

    @Test
    public void testTwoParametersWithSameKey() throws Exception {
        Exchange out = template.request((((("http4://" + (localServer.getInetAddress().getHostName())) + ":") + (localServer.getLocalPort())) + "/myapp?from=me&to=foo&to=bar"), null);
        assertNotNull(out);
        assertFalse("Should not fail", out.isFailed());
        assertEquals("OK", out.getOut().getBody(String.class));
        assertEquals("yes", out.getOut().getHeader("bar"));
        List<?> foo = out.getOut().getHeader("foo", List.class);
        assertNotNull(foo);
        assertEquals(2, foo.size());
        assertEquals("123", foo.get(0));
        assertEquals("456", foo.get(1));
    }
}

