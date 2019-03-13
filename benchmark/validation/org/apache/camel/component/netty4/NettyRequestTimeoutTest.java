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
package org.apache.camel.component.netty4;


import NettyConstants.NETTY_REQUEST_TIMEOUT;
import io.netty.handler.timeout.ReadTimeoutException;
import org.apache.camel.CamelExecutionException;
import org.junit.Test;


public class NettyRequestTimeoutTest extends BaseNettyTest {
    @Test
    public void testRequestTimeoutOK() throws Exception {
        String out = template.requestBody("netty4:tcp://localhost:{{port}}?textline=true&sync=true&requestTimeout=500", "Hello Camel", String.class);
        assertEquals("Bye World", out);
    }

    @Test
    public void testRequestTimeout() throws Exception {
        try {
            template.requestBody("netty4:tcp://localhost:{{port}}?textline=true&sync=true&requestTimeout=100", "Hello Camel", String.class);
            fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            ReadTimeoutException cause = assertIsInstanceOf(ReadTimeoutException.class, e.getCause());
            assertNotNull(cause);
        }
    }

    @Test
    public void testRequestTimeoutViaHeader() throws Exception {
        try {
            template.requestBodyAndHeader("netty4:tcp://localhost:{{port}}?textline=true&sync=true", "Hello Camel", NETTY_REQUEST_TIMEOUT, 100, String.class);
            fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            ReadTimeoutException cause = assertIsInstanceOf(ReadTimeoutException.class, e.getCause());
            assertNotNull(cause);
        }
    }

    @Test
    public void testRequestTimeoutAndOk() throws Exception {
        try {
            template.requestBody("netty4:tcp://localhost:{{port}}?textline=true&sync=true&requestTimeout=100", "Hello Camel", String.class);
            fail("Should have thrown exception");
        } catch (CamelExecutionException e) {
            ReadTimeoutException cause = assertIsInstanceOf(ReadTimeoutException.class, e.getCause());
            assertNotNull(cause);
        }
        // now we try again but this time the is no delay on server and thus faster
        String out = template.requestBody("netty4:tcp://localhost:{{port}}?textline=true&sync=true&requestTimeout=100", "Hello World", String.class);
        assertEquals("Bye World", out);
    }
}

