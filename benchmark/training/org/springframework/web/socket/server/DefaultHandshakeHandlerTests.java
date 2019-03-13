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
package org.springframework.web.socket.server;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.socket.AbstractHttpRequestTests;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;


/**
 * Test fixture for {@link org.springframework.web.socket.server.support.DefaultHandshakeHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class DefaultHandshakeHandlerTests extends AbstractHttpRequestTests {
    private DefaultHandshakeHandler handshakeHandler;

    @Mock
    private RequestUpgradeStrategy upgradeStrategy;

    @Test
    public void supportedSubProtocols() {
        this.handshakeHandler.setSupportedProtocols("stomp", "mqtt");
        BDDMockito.given(this.upgradeStrategy.getSupportedVersions()).willReturn(new String[]{ "13" });
        this.servletRequest.setMethod("GET");
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(this.request.getHeaders());
        headers.setUpgrade("WebSocket");
        headers.setConnection("Upgrade");
        headers.setSecWebSocketVersion("13");
        headers.setSecWebSocketKey("82/ZS2YHjEnUN97HLL8tbw==");
        headers.setSecWebSocketProtocol("STOMP");
        WebSocketHandler handler = new TextWebSocketHandler();
        Map<String, Object> attributes = Collections.emptyMap();
        this.handshakeHandler.doHandshake(this.request, this.response, handler, attributes);
        Mockito.verify(this.upgradeStrategy).upgrade(this.request, this.response, "STOMP", Collections.emptyList(), null, handler, attributes);
    }

    @Test
    public void supportedExtensions() {
        WebSocketExtension extension1 = new WebSocketExtension("ext1");
        WebSocketExtension extension2 = new WebSocketExtension("ext2");
        BDDMockito.given(this.upgradeStrategy.getSupportedVersions()).willReturn(new String[]{ "13" });
        BDDMockito.given(this.upgradeStrategy.getSupportedExtensions(this.request)).willReturn(Collections.singletonList(extension1));
        this.servletRequest.setMethod("GET");
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(this.request.getHeaders());
        headers.setUpgrade("WebSocket");
        headers.setConnection("Upgrade");
        headers.setSecWebSocketVersion("13");
        headers.setSecWebSocketKey("82/ZS2YHjEnUN97HLL8tbw==");
        headers.setSecWebSocketExtensions(Arrays.asList(extension1, extension2));
        WebSocketHandler handler = new TextWebSocketHandler();
        Map<String, Object> attributes = Collections.<String, Object>emptyMap();
        this.handshakeHandler.doHandshake(this.request, this.response, handler, attributes);
        Mockito.verify(this.upgradeStrategy).upgrade(this.request, this.response, null, Collections.singletonList(extension1), null, handler, attributes);
    }

    @Test
    public void subProtocolCapableHandler() {
        BDDMockito.given(this.upgradeStrategy.getSupportedVersions()).willReturn(new String[]{ "13" });
        this.servletRequest.setMethod("GET");
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(this.request.getHeaders());
        headers.setUpgrade("WebSocket");
        headers.setConnection("Upgrade");
        headers.setSecWebSocketVersion("13");
        headers.setSecWebSocketKey("82/ZS2YHjEnUN97HLL8tbw==");
        headers.setSecWebSocketProtocol("v11.stomp");
        WebSocketHandler handler = new DefaultHandshakeHandlerTests.SubProtocolCapableHandler("v12.stomp", "v11.stomp");
        Map<String, Object> attributes = Collections.<String, Object>emptyMap();
        this.handshakeHandler.doHandshake(this.request, this.response, handler, attributes);
        Mockito.verify(this.upgradeStrategy).upgrade(this.request, this.response, "v11.stomp", Collections.emptyList(), null, handler, attributes);
    }

    @Test
    public void subProtocolCapableHandlerNoMatch() {
        BDDMockito.given(this.upgradeStrategy.getSupportedVersions()).willReturn(new String[]{ "13" });
        this.servletRequest.setMethod("GET");
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(this.request.getHeaders());
        headers.setUpgrade("WebSocket");
        headers.setConnection("Upgrade");
        headers.setSecWebSocketVersion("13");
        headers.setSecWebSocketKey("82/ZS2YHjEnUN97HLL8tbw==");
        headers.setSecWebSocketProtocol("v10.stomp");
        WebSocketHandler handler = new DefaultHandshakeHandlerTests.SubProtocolCapableHandler("v12.stomp", "v11.stomp");
        Map<String, Object> attributes = Collections.<String, Object>emptyMap();
        this.handshakeHandler.doHandshake(this.request, this.response, handler, attributes);
        Mockito.verify(this.upgradeStrategy).upgrade(this.request, this.response, null, Collections.emptyList(), null, handler, attributes);
    }

    private static class SubProtocolCapableHandler extends TextWebSocketHandler implements SubProtocolCapable {
        private final List<String> subProtocols;

        public SubProtocolCapableHandler(String... subProtocols) {
            this.subProtocols = Arrays.asList(subProtocols);
        }

        @Override
        public List<String> getSubProtocols() {
            return this.subProtocols;
        }
    }
}

