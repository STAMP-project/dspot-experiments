/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.websocket.jsr356.server;


import HttpHeader.SEC_WEBSOCKET_EXTENSIONS;
import HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL;
import JsrCreator.PROP_LOCAL_ADDRESS;
import JsrCreator.PROP_REMOTE_ADDRESS;
import ServerEndpointConfig.Configurator;
import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.util.QuoteUtil;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.BlockheadClient;
import org.eclipse.jetty.websocket.common.test.BlockheadClientRequest;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class ConfiguratorTest {
    private static final Logger LOG = Log.getLogger(ConfiguratorTest.class);

    public static class EmptyConfigurator extends ServerEndpointConfig.Configurator {}

    @ServerEndpoint(value = "/empty", configurator = ConfiguratorTest.EmptyConfigurator.class)
    public static class EmptySocket {
        @OnMessage
        public String echo(String message) {
            return message;
        }
    }

    public static class NoExtensionsConfigurator extends ServerEndpointConfig.Configurator {
        @Override
        public List<Extension> getNegotiatedExtensions(List<Extension> installed, List<Extension> requested) {
            return Collections.emptyList();
        }
    }

    @ServerEndpoint(value = "/no-extensions", configurator = ConfiguratorTest.NoExtensionsConfigurator.class)
    public static class NoExtensionsSocket {
        @OnMessage
        public String echo(Session session, String message) {
            List<Extension> negotiatedExtensions = session.getNegotiatedExtensions();
            if (negotiatedExtensions == null) {
                return "negotiatedExtensions=null";
            } else {
                return "negotiatedExtensions=" + (negotiatedExtensions.stream().map(( ext) -> ext.getName()).collect(Collectors.joining(",", "[", "]")));
            }
        }
    }

    public static class CaptureHeadersConfigurator extends ServerEndpointConfig.Configurator {
        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            super.modifyHandshake(sec, request, response);
            sec.getUserProperties().put("request-headers", request.getHeaders());
        }
    }

    @ServerEndpoint(value = "/capture-request-headers", configurator = ConfiguratorTest.CaptureHeadersConfigurator.class)
    public static class CaptureHeadersSocket {
        @OnMessage
        public String getHeaders(Session session, String headerKey) {
            StringBuilder response = new StringBuilder();
            response.append("Request Header [").append(headerKey).append("]: ");
            @SuppressWarnings("unchecked")
            Map<String, List<String>> headers = ((Map<String, List<String>>) (session.getUserProperties().get("request-headers")));
            if (headers == null) {
                response.append("<no headers found in session.getUserProperties()>");
            } else {
                List<String> values = headers.get(headerKey);
                if (values == null) {
                    response.append("<header not found>");
                } else {
                    response.append(QuoteUtil.join(values, ","));
                }
            }
            return response.toString();
        }
    }

    public static class ProtocolsConfigurator extends ServerEndpointConfig.Configurator {
        public static AtomicReference<String> seenProtocols = new AtomicReference<>();

        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            super.modifyHandshake(sec, request, response);
        }

        @Override
        public String getNegotiatedSubprotocol(List<String> supported, List<String> requested) {
            String seen = QuoteUtil.join(requested, ",");
            ConfiguratorTest.ProtocolsConfigurator.seenProtocols.compareAndSet(null, seen);
            return super.getNegotiatedSubprotocol(supported, requested);
        }
    }

    @ServerEndpoint(value = "/protocols", configurator = ConfiguratorTest.ProtocolsConfigurator.class)
    public static class ProtocolsSocket {
        @OnMessage
        public String onMessage(Session session, String msg) {
            StringBuilder response = new StringBuilder();
            response.append("Requested Protocols: [").append(ConfiguratorTest.ProtocolsConfigurator.seenProtocols.get()).append("]");
            return response.toString();
        }
    }

    public static class UniqueUserPropsConfigurator extends ServerEndpointConfig.Configurator {
        private AtomicInteger upgradeCount = new AtomicInteger(0);

        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            int upgradeNum = upgradeCount.addAndGet(1);
            ConfiguratorTest.LOG.debug("Upgrade Num: {}", upgradeNum);
            sec.getUserProperties().put("upgradeNum", Integer.toString(upgradeNum));
            switch (upgradeNum) {
                case 1 :
                    sec.getUserProperties().put("apple", "fruit from tree");
                    break;
                case 2 :
                    sec.getUserProperties().put("blueberry", "fruit from bush");
                    break;
                case 3 :
                    sec.getUserProperties().put("strawberry", "fruit from annual");
                    break;
                default :
                    sec.getUserProperties().put(("fruit" + upgradeNum), "placeholder");
                    break;
            }
            super.modifyHandshake(sec, request, response);
        }
    }

    @ServerEndpoint(value = "/unique-user-props", configurator = ConfiguratorTest.UniqueUserPropsConfigurator.class)
    public static class UniqueUserPropsSocket {
        @OnMessage
        public String onMessage(Session session, String msg) {
            String value = ((String) (session.getUserProperties().get(msg)));
            StringBuilder response = new StringBuilder();
            response.append("Requested User Property: [").append(msg).append("] = ");
            if (value == null) {
                response.append("<null>");
            } else {
                response.append('"').append(value).append('"');
            }
            return response.toString();
        }
    }

    public static class AddrConfigurator extends ServerEndpointConfig.Configurator {
        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            InetSocketAddress local = ((InetSocketAddress) (sec.getUserProperties().get(PROP_LOCAL_ADDRESS)));
            InetSocketAddress remote = ((InetSocketAddress) (sec.getUserProperties().get(PROP_REMOTE_ADDRESS)));
            sec.getUserProperties().put("found.local", local);
            sec.getUserProperties().put("found.remote", remote);
            super.modifyHandshake(sec, request, response);
        }
    }

    @ServerEndpoint(value = "/addr", configurator = ConfiguratorTest.AddrConfigurator.class)
    public static class AddressSocket {
        @OnMessage
        public String onMessage(Session session, String msg) {
            StringBuilder response = new StringBuilder();
            appendPropValue(session, response, "javax.websocket.endpoint.localAddress");
            appendPropValue(session, response, "javax.websocket.endpoint.remoteAddress");
            appendPropValue(session, response, "found.local");
            appendPropValue(session, response, "found.remote");
            return response.toString();
        }

        private void appendPropValue(Session session, StringBuilder response, String key) {
            InetSocketAddress value = ((InetSocketAddress) (session.getUserProperties().get(key)));
            response.append("[").append(key).append("] = ");
            response.append(ConfiguratorTest.toSafeAddr(value));
            response.append(System.lineSeparator());
        }
    }

    public static class SelectedProtocolConfigurator extends ServerEndpointConfig.Configurator {
        @Override
        public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
            List<String> selectedProtocol = response.getHeaders().get("Sec-WebSocket-Protocol");
            String protocol = "<>";
            if ((selectedProtocol != null) && (!(selectedProtocol.isEmpty())))
                protocol = selectedProtocol.get(0);

            config.getUserProperties().put("selected-subprotocol", protocol);
        }
    }

    public static class GmtTimeDecoder implements Decoder.Text<Calendar> {
        private TimeZone TZ;

        @Override
        public Calendar decode(String s) throws DecodeException {
            if ((TZ) == null)
                throw new DecodeException(s, ".init() not called");

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                dateFormat.setTimeZone(TZ);
                Date time = dateFormat.parse(s);
                Calendar cal = Calendar.getInstance();
                cal.setTimeZone(TZ);
                cal.setTime(time);
                return cal;
            } catch (ParseException e) {
                throw new DecodeException(s, "Unable to decode Time", e);
            }
        }

        @Override
        public void init(EndpointConfig config) {
            TZ = TimeZone.getTimeZone("GMT+0");
        }

        @Override
        public void destroy() {
        }

        @Override
        public boolean willDecode(String s) {
            return true;
        }
    }

    @ServerEndpoint(value = "/timedecoder", subprotocols = { "time", "gmt" }, configurator = ConfiguratorTest.SelectedProtocolConfigurator.class, decoders = { ConfiguratorTest.GmtTimeDecoder.class })
    public static class TimeDecoderSocket {
        private TimeZone TZ = TimeZone.getTimeZone("GMT+0");

        @OnMessage
        public String onMessage(Calendar cal) {
            return String.format("cal=%s", newDateFormat().format(cal.getTime()));
        }

        private SimpleDateFormat newDateFormat() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss Z", Locale.ENGLISH);
            dateFormat.setTimeZone(TZ);
            return dateFormat;
        }
    }

    public static class ConfigNormalConfigurator extends ServerEndpointConfig.Configurator {
        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            sec.getUserProperties().put("self.configurator", this.getClass().getName());
        }
    }

    public static class ConfigOverrideConfigurator extends ServerEndpointConfig.Configurator {
        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            sec.getUserProperties().put("self.configurator", this.getClass().getName());
        }
    }

    @ServerEndpoint(value = "/config-normal", configurator = ConfiguratorTest.ConfigNormalConfigurator.class)
    public static class ConfigNormalSocket {
        @OnMessage
        public String onMessage(Session session, String msg) {
            return String.format("UserProperties[self.configurator] = %s", session.getUserProperties().get("self.configurator"));
        }
    }

    private static BlockheadClient client;

    private static Server server;

    private static URI baseServerUri;

    @Test
    public void testEmptyConfigurator() throws Exception {
        URI uri = ConfiguratorTest.baseServerUri.resolve("/empty");
        BlockheadClientRequest request = ConfiguratorTest.client.newWsRequest(uri);
        request.header(SEC_WEBSOCKET_EXTENSIONS, "identity");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            HttpFields responseHeaders = clientConn.getUpgradeResponseHeaders();
            HttpField extensionHeader = responseHeaders.getField(SEC_WEBSOCKET_EXTENSIONS);
            MatcherAssert.assertThat("response.extensions", extensionHeader.getValue(), Matchers.is("identity"));
        }
    }

    @Test
    public void testNoExtensionsConfigurator() throws Exception {
        URI uri = ConfiguratorTest.baseServerUri.resolve("/no-extensions");
        BlockheadClientRequest request = ConfiguratorTest.client.newWsRequest(uri);
        request.header(SEC_WEBSOCKET_EXTENSIONS, "identity");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            HttpFields responseHeaders = clientConn.getUpgradeResponseHeaders();
            HttpField extensionHeader = responseHeaders.getField(SEC_WEBSOCKET_EXTENSIONS);
            MatcherAssert.assertThat("response.extensions", extensionHeader, Matchers.is(Matchers.nullValue()));
            clientConn.write(new TextFrame().setPayload("NegoExts"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Frame Response", frame.getPayloadAsUTF8(), Matchers.is("negotiatedExtensions=[]"));
        }
    }

    @Test
    public void testCaptureRequestHeadersConfigurator() throws Exception {
        URI uri = ConfiguratorTest.baseServerUri.resolve("/capture-request-headers");
        BlockheadClientRequest request = ConfiguratorTest.client.newWsRequest(uri);
        request.header("X-Dummy", "Bogus");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            clientConn.write(new TextFrame().setPayload("X-Dummy"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Frame Response", frame.getPayloadAsUTF8(), Matchers.is("Request Header [X-Dummy]: \"Bogus\""));
        }
    }

    @Test
    public void testUniqueUserPropsConfigurator() throws Exception {
        URI uri = ConfiguratorTest.baseServerUri.resolve("/unique-user-props");
        // First request
        BlockheadClientRequest request = ConfiguratorTest.client.newWsRequest(uri);
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            clientConn.write(new TextFrame().setPayload("apple"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Frame Response", frame.getPayloadAsUTF8(), Matchers.is("Requested User Property: [apple] = \"fruit from tree\""));
        }
        // Second request
        request = ConfiguratorTest.client.newWsRequest(uri);
        connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            clientConn.write(new TextFrame().setPayload("apple"));
            clientConn.write(new TextFrame().setPayload("blueberry"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            // should have no value
            MatcherAssert.assertThat("Frame Response", frame.getPayloadAsUTF8(), Matchers.is("Requested User Property: [apple] = <null>"));
            frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Frame Response", frame.getPayloadAsUTF8(), Matchers.is("Requested User Property: [blueberry] = \"fruit from bush\""));
        }
    }

    @Test
    public void testUserPropsAddress() throws Exception {
        URI uri = ConfiguratorTest.baseServerUri.resolve("/addr");
        BlockheadClientRequest request = ConfiguratorTest.client.newWsRequest(uri);
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            InetSocketAddress expectedLocal = clientConn.getLocalSocketAddress();
            InetSocketAddress expectedRemote = clientConn.getRemoteSocketAddress();
            clientConn.write(new TextFrame().setPayload("addr"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            StringWriter expected = new StringWriter();
            PrintWriter out = new PrintWriter(expected);
            // local <-> remote are opposite on server (duh)
            out.printf("[javax.websocket.endpoint.localAddress] = %s%n", ConfiguratorTest.toSafeAddr(expectedRemote));
            out.printf("[javax.websocket.endpoint.remoteAddress] = %s%n", ConfiguratorTest.toSafeAddr(expectedLocal));
            out.printf("[found.local] = %s%n", ConfiguratorTest.toSafeAddr(expectedRemote));
            out.printf("[found.remote] = %s%n", ConfiguratorTest.toSafeAddr(expectedLocal));
            MatcherAssert.assertThat("Frame Response", frame.getPayloadAsUTF8(), Matchers.is(expected.toString()));
        }
    }

    /**
     * Test of Sec-WebSocket-Protocol, as seen in RFC-6455, 1 protocol
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testProtocol_Single() throws Exception {
        URI uri = ConfiguratorTest.baseServerUri.resolve("/protocols");
        ConfiguratorTest.ProtocolsConfigurator.seenProtocols.set(null);
        BlockheadClientRequest request = ConfiguratorTest.client.newWsRequest(uri);
        request.header(SEC_WEBSOCKET_SUBPROTOCOL, "echo");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            clientConn.write(new TextFrame().setPayload("getProtocols"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Frame Response", frame.getPayloadAsUTF8(), Matchers.is("Requested Protocols: [\"echo\"]"));
        }
    }

    /**
     * Test of Sec-WebSocket-Protocol, as seen in RFC-6455, 3 protocols
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testProtocol_Triple() throws Exception {
        URI uri = ConfiguratorTest.baseServerUri.resolve("/protocols");
        ConfiguratorTest.ProtocolsConfigurator.seenProtocols.set(null);
        BlockheadClientRequest request = ConfiguratorTest.client.newWsRequest(uri);
        request.header(SEC_WEBSOCKET_SUBPROTOCOL, "echo, chat, status");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            clientConn.write(new TextFrame().setPayload("getProtocols"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Frame Response", frame.getPayloadAsUTF8(), Matchers.is("Requested Protocols: [\"echo\",\"chat\",\"status\"]"));
        }
    }

    /**
     * Test of Sec-WebSocket-Protocol, using all lowercase header
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testProtocol_LowercaseHeader() throws Exception {
        URI uri = ConfiguratorTest.baseServerUri.resolve("/protocols");
        ConfiguratorTest.ProtocolsConfigurator.seenProtocols.set(null);
        BlockheadClientRequest request = ConfiguratorTest.client.newWsRequest(uri);
        request.header("sec-websocket-protocol", "echo, chat, status");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            clientConn.write(new TextFrame().setPayload("getProtocols"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Frame Response", frame.getPayloadAsUTF8(), Matchers.is("Requested Protocols: [\"echo\",\"chat\",\"status\"]"));
        }
    }

    /**
     * Test of Sec-WebSocket-Protocol, using non-spec case header
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testProtocol_AltHeaderCase() throws Exception {
        URI uri = ConfiguratorTest.baseServerUri.resolve("/protocols");
        ConfiguratorTest.ProtocolsConfigurator.seenProtocols.set(null);
        BlockheadClientRequest request = ConfiguratorTest.client.newWsRequest(uri);
        // We see "Websocket" (no capital "S" often)
        request.header("Sec-Websocket-Protocol", "echo, chat, status");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            clientConn.write(new TextFrame().setPayload("getProtocols"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Frame Response", frame.getPayloadAsUTF8(), Matchers.is("Requested Protocols: [\"echo\",\"chat\",\"status\"]"));
        }
    }

    /**
     * Test of Sec-WebSocket-Protocol, using non-spec case header
     */
    @Test
    public void testDecoderWithProtocol() throws Exception {
        URI uri = ConfiguratorTest.baseServerUri.resolve("/timedecoder");
        BlockheadClientRequest request = ConfiguratorTest.client.newWsRequest(uri);
        // We see "Websocket" (no capital "S" often)
        request.header("SeC-WeBsOcKeT-PrOtOcOl", "gmt");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            clientConn.write(new TextFrame().setPayload("2016-06-20T14:27:44"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Frame Response", frame.getPayloadAsUTF8(), Matchers.is("cal=2016.06.20 AD at 14:27:44 +0000"));
        }
    }

    /**
     * Test that a Configurator declared in the annotation is used
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAnnotationConfigurator() throws Exception {
        URI uri = ConfiguratorTest.baseServerUri.resolve("/config-normal");
        BlockheadClientRequest request = ConfiguratorTest.client.newWsRequest(uri);
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            clientConn.write(new TextFrame().setPayload("tellme"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Frame Response", frame.getPayloadAsUTF8(), Matchers.is(("UserProperties[self.configurator] = " + (ConfiguratorTest.ConfigNormalConfigurator.class.getName()))));
        }
    }

    /**
     * Test that a provided ServerEndpointConfig can override the annotation Configurator
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testOverrideConfigurator() throws Exception {
        URI uri = ConfiguratorTest.baseServerUri.resolve("/config-override");
        BlockheadClientRequest request = ConfiguratorTest.client.newWsRequest(uri);
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            clientConn.write(new TextFrame().setPayload("tellme"));
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Frame Response", frame.getPayloadAsUTF8(), Matchers.is(("UserProperties[self.configurator] = " + (ConfiguratorTest.ConfigOverrideConfigurator.class.getName()))));
        }
    }
}

