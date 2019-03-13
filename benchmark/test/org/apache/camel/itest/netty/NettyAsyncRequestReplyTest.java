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
package org.apache.camel.itest.netty;


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Doing request/reply over Netty with async processing.
 */
public class NettyAsyncRequestReplyTest extends CamelTestSupport {
    private int port;

    @Test
    public void testNetty() throws Exception {
        String out = template.requestBody((("netty:tcp://localhost:" + (port)) + "?textline=true&sync=true"), "World", String.class);
        assertEquals("Bye World", out);
        String out2 = template.requestBody((("netty:tcp://localhost:" + (port)) + "?textline=true&sync=true"), "Camel", String.class);
        assertEquals("Bye Camel", out2);
    }
}

