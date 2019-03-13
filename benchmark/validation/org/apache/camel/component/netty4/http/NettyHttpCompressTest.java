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
package org.apache.camel.component.netty4.http;


import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


public class NettyHttpCompressTest extends BaseNettyTest {
    @Test
    public void testContentType() throws Exception {
        byte[] data = "Hello World".getBytes(Charset.forName("UTF-8"));
        Map<String, Object> headers = new HashMap<>();
        headers.put("content-type", "text/plain; charset=\"UTF-8\"");
        headers.put("Accept-Encoding", "compress, gzip");
        String out = template.requestBodyAndHeaders("netty4-http:http://localhost:{{port}}/foo?decoders=#myDecoders", data, headers, String.class);
        // The decoded out has some space to clean up.
        assertEquals("Bye World", out.trim());
    }
}

