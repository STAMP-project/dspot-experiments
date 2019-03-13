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
package org.springframework.web.socket.adapter.standard;


import java.util.HashMap;
import java.util.Map;
import javax.websocket.Session;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.handler.TestPrincipal;


/**
 * Unit tests for {@link org.springframework.web.socket.adapter.standard.StandardWebSocketSession}.
 *
 * @author Rossen Stoyanchev
 */
public class StandardWebSocketSessionTests {
    private final HttpHeaders headers = new HttpHeaders();

    private final Map<String, Object> attributes = new HashMap<>();

    @Test
    @SuppressWarnings("resource")
    public void getPrincipalWithConstructorArg() {
        TestPrincipal user = new TestPrincipal("joe");
        StandardWebSocketSession session = new StandardWebSocketSession(this.headers, this.attributes, null, null, user);
        Assert.assertSame(user, session.getPrincipal());
    }

    @Test
    @SuppressWarnings("resource")
    public void getPrincipalWithNativeSession() {
        TestPrincipal user = new TestPrincipal("joe");
        Session nativeSession = Mockito.mock(Session.class);
        BDDMockito.given(nativeSession.getUserPrincipal()).willReturn(user);
        StandardWebSocketSession session = new StandardWebSocketSession(this.headers, this.attributes, null, null);
        session.initializeNativeSession(nativeSession);
        Assert.assertSame(user, session.getPrincipal());
    }

    @Test
    @SuppressWarnings("resource")
    public void getPrincipalNone() {
        Session nativeSession = Mockito.mock(Session.class);
        BDDMockito.given(nativeSession.getUserPrincipal()).willReturn(null);
        StandardWebSocketSession session = new StandardWebSocketSession(this.headers, this.attributes, null, null);
        session.initializeNativeSession(nativeSession);
        Mockito.reset(nativeSession);
        Assert.assertNull(session.getPrincipal());
        Mockito.verifyNoMoreInteractions(nativeSession);
    }

    @Test
    @SuppressWarnings("resource")
    public void getAcceptedProtocol() {
        String protocol = "foo";
        Session nativeSession = Mockito.mock(Session.class);
        BDDMockito.given(nativeSession.getNegotiatedSubprotocol()).willReturn(protocol);
        StandardWebSocketSession session = new StandardWebSocketSession(this.headers, this.attributes, null, null);
        session.initializeNativeSession(nativeSession);
        Mockito.reset(nativeSession);
        Assert.assertEquals(protocol, session.getAcceptedProtocol());
        Mockito.verifyNoMoreInteractions(nativeSession);
    }
}

