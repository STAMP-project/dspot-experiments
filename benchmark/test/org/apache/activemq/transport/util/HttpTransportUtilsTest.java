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
package org.apache.activemq.transport.util;


import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class HttpTransportUtilsTest {
    @Test
    public void testGenerateWsRemoteAddress() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getScheme()).thenReturn("http");
        Mockito.when(request.getRemoteAddr()).thenReturn("localhost");
        Mockito.when(request.getRemotePort()).thenReturn(8080);
        Assert.assertEquals("ws://localhost:8080", HttpTransportUtils.generateWsRemoteAddress(request));
    }

    @Test
    public void testGenerateWssRemoteAddress() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getScheme()).thenReturn("https");
        Mockito.when(request.getRemoteAddr()).thenReturn("localhost");
        Mockito.when(request.getRemotePort()).thenReturn(8443);
        Assert.assertEquals("wss://localhost:8443", HttpTransportUtils.generateWsRemoteAddress(request));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullHttpServleRequest() {
        HttpTransportUtils.generateWsRemoteAddress(null);
    }
}

