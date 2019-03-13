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
package org.apache.camel.component.cxf.common.message;


import Message.BASE_PATH;
import Message.REQUEST_URI;
import javax.servlet.http.HttpServletRequest;
import org.apache.camel.Exchange;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.apache.cxf.common.security.SimplePrincipal;
import org.apache.cxf.message.Message;
import org.apache.cxf.security.SecurityContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DefaultCxfMessageMapperTest extends Assert {
    @Test
    public void testRequestUriAndPath() {
        final String requestURI = "/path;a=b";
        final String requestPath = "/path";
        DefaultCxfMessageMapper mapper = new DefaultCxfMessageMapper();
        Exchange camelExchange = setupCamelExchange(requestURI, requestPath, null);
        Message cxfMessage = mapper.createCxfMessageFromCamelExchange(camelExchange, Mockito.mock(HeaderFilterStrategy.class));
        Assert.assertEquals(requestURI, cxfMessage.get(REQUEST_URI).toString());
        Assert.assertEquals(requestPath, cxfMessage.get(BASE_PATH).toString());
    }

    @Test
    public void testSecurityContext() {
        DefaultCxfMessageMapper mapper = new DefaultCxfMessageMapper();
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getUserPrincipal()).thenReturn(new SimplePrincipal("barry"));
        Mockito.when(request.isUserInRole("role1")).thenReturn(true);
        Mockito.when(request.isUserInRole("role2")).thenReturn(false);
        Exchange camelExchange = setupCamelExchange("/", "/", request);
        Message cxfMessage = mapper.createCxfMessageFromCamelExchange(camelExchange, Mockito.mock(HeaderFilterStrategy.class));
        SecurityContext sc = cxfMessage.get(SecurityContext.class);
        Assert.assertNotNull(sc);
        Assert.assertEquals("barry", sc.getUserPrincipal().getName());
        Assert.assertTrue(sc.isUserInRole("role1"));
        Assert.assertFalse(sc.isUserInRole("role2"));
    }
}

