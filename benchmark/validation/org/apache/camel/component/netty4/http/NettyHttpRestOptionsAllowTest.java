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


import Exchange.HTTP_METHOD;
import HttpMethods.OPTIONS;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.junit.Test;


public class NettyHttpRestOptionsAllowTest extends BaseNettyTest {
    static final String ALLOW_METHODS = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,CONNECT,PATCH";

    @Test
    public void shouldGetAllowMethods() throws Exception {
        Exchange response = template.request("netty4-http:http://localhost:{{port}}/myapp", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader(HTTP_METHOD, OPTIONS);
                exchange.getIn().setBody("");
            }
        });
        String body = response.getOut().getBody(String.class);
        String allowHeader = ((String) (response.getOut().getHeader("Allow")));
        int code = ((int) (response.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE)));
        assertEquals(NettyHttpRestOptionsAllowTest.ALLOW_METHODS, allowHeader);
        assertEquals(200, code);
        assertEquals("", body);
    }
}

