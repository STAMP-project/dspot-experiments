/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.socket.server.support;


import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.web.socket.AbstractHttpRequestTests;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;


/**
 * Test fixture for {@link HandshakeInterceptorChain}.
 *
 * @author Rossen Stoyanchev
 */
public class HandshakeInterceptorChainTests extends AbstractHttpRequestTests {
    private HandshakeInterceptor i1;

    private HandshakeInterceptor i2;

    private HandshakeInterceptor i3;

    private List<HandshakeInterceptor> interceptors;

    private WebSocketHandler wsHandler;

    private Map<String, Object> attributes;

    @Test
    public void success() throws Exception {
        BDDMockito.given(i1.beforeHandshake(request, response, wsHandler, attributes)).willReturn(true);
        BDDMockito.given(i2.beforeHandshake(request, response, wsHandler, attributes)).willReturn(true);
        BDDMockito.given(i3.beforeHandshake(request, response, wsHandler, attributes)).willReturn(true);
        HandshakeInterceptorChain chain = new HandshakeInterceptorChain(interceptors, wsHandler);
        chain.applyBeforeHandshake(request, response, attributes);
        Mockito.verify(i1).beforeHandshake(request, response, wsHandler, attributes);
        Mockito.verify(i2).beforeHandshake(request, response, wsHandler, attributes);
        Mockito.verify(i3).beforeHandshake(request, response, wsHandler, attributes);
        Mockito.verifyNoMoreInteractions(i1, i2, i3);
    }

    @Test
    public void applyBeforeHandshakeWithFalseReturnValue() throws Exception {
        BDDMockito.given(i1.beforeHandshake(request, response, wsHandler, attributes)).willReturn(true);
        BDDMockito.given(i2.beforeHandshake(request, response, wsHandler, attributes)).willReturn(false);
        HandshakeInterceptorChain chain = new HandshakeInterceptorChain(interceptors, wsHandler);
        chain.applyBeforeHandshake(request, response, attributes);
        Mockito.verify(i1).beforeHandshake(request, response, wsHandler, attributes);
        Mockito.verify(i1).afterHandshake(request, response, wsHandler, null);
        Mockito.verify(i2).beforeHandshake(request, response, wsHandler, attributes);
        Mockito.verifyNoMoreInteractions(i1, i2, i3);
    }

    @Test
    public void applyAfterHandshakeOnly() {
        HandshakeInterceptorChain chain = new HandshakeInterceptorChain(interceptors, wsHandler);
        chain.applyAfterHandshake(request, response, null);
        Mockito.verifyNoMoreInteractions(i1, i2, i3);
    }
}

