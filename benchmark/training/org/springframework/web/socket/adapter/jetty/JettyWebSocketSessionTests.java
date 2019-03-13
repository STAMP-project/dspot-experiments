/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.web.socket.adapter.jetty;


import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.web.socket.handler.TestPrincipal;


/**
 * Unit tests for {@link org.springframework.web.socket.adapter.jetty.JettyWebSocketSession}.
 *
 * @author Rossen Stoyanchev
 */
public class JettyWebSocketSessionTests {
    private final Map<String, Object> attributes = new HashMap<>();

    @Test
    @SuppressWarnings("resource")
    public void getPrincipalWithConstructorArg() {
        TestPrincipal user = new TestPrincipal("joe");
        JettyWebSocketSession session = new JettyWebSocketSession(attributes, user);
        Assert.assertSame(user, session.getPrincipal());
    }

    @Test
    @SuppressWarnings("resource")
    public void getPrincipalFromNativeSession() {
        TestPrincipal user = new TestPrincipal("joe");
        UpgradeRequest request = Mockito.mock(UpgradeRequest.class);
        BDDMockito.given(request.getUserPrincipal()).willReturn(user);
        UpgradeResponse response = Mockito.mock(UpgradeResponse.class);
        BDDMockito.given(response.getAcceptedSubProtocol()).willReturn(null);
        Session nativeSession = Mockito.mock(Session.class);
        BDDMockito.given(nativeSession.getUpgradeRequest()).willReturn(request);
        BDDMockito.given(nativeSession.getUpgradeResponse()).willReturn(response);
        JettyWebSocketSession session = new JettyWebSocketSession(attributes);
        session.initializeNativeSession(nativeSession);
        Mockito.reset(nativeSession);
        Assert.assertSame(user, session.getPrincipal());
        Mockito.verifyNoMoreInteractions(nativeSession);
    }

    @Test
    @SuppressWarnings("resource")
    public void getPrincipalNotAvailable() {
        UpgradeRequest request = Mockito.mock(UpgradeRequest.class);
        BDDMockito.given(request.getUserPrincipal()).willReturn(null);
        UpgradeResponse response = Mockito.mock(UpgradeResponse.class);
        BDDMockito.given(response.getAcceptedSubProtocol()).willReturn(null);
        Session nativeSession = Mockito.mock(Session.class);
        BDDMockito.given(nativeSession.getUpgradeRequest()).willReturn(request);
        BDDMockito.given(nativeSession.getUpgradeResponse()).willReturn(response);
        JettyWebSocketSession session = new JettyWebSocketSession(attributes);
        session.initializeNativeSession(nativeSession);
        Mockito.reset(nativeSession);
        Assert.assertNull(session.getPrincipal());
        Mockito.verifyNoMoreInteractions(nativeSession);
    }

    @Test
    @SuppressWarnings("resource")
    public void getAcceptedProtocol() {
        String protocol = "foo";
        UpgradeRequest request = Mockito.mock(UpgradeRequest.class);
        BDDMockito.given(request.getUserPrincipal()).willReturn(null);
        UpgradeResponse response = Mockito.mock(UpgradeResponse.class);
        BDDMockito.given(response.getAcceptedSubProtocol()).willReturn(protocol);
        Session nativeSession = Mockito.mock(Session.class);
        BDDMockito.given(nativeSession.getUpgradeRequest()).willReturn(request);
        BDDMockito.given(nativeSession.getUpgradeResponse()).willReturn(response);
        JettyWebSocketSession session = new JettyWebSocketSession(attributes);
        session.initializeNativeSession(nativeSession);
        Mockito.reset(nativeSession);
        Assert.assertSame(protocol, session.getAcceptedProtocol());
        Mockito.verifyNoMoreInteractions(nativeSession);
    }
}

