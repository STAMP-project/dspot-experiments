/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.common.logback;


import BuiltInProperty.ELAPSED_NANOS;
import BuiltInProperty.REMOTE_HOST;
import BuiltInProperty.REQ_DIRECTION;
import HttpHeaderNames.DATE;
import HttpHeaderNames.SET_COOKIE;
import HttpHeaderNames.USER_AGENT;
import ThriftSerializationFormats.BINARY;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.google.common.collect.ImmutableList;
import com.linecorp.armeria.client.ClientRequestContext;
import com.linecorp.armeria.common.DefaultRpcRequest;
import com.linecorp.armeria.common.DefaultRpcResponse;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpMethod;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.common.RpcRequest;
import com.linecorp.armeria.common.RpcResponse;
import com.linecorp.armeria.common.logging.RequestLogBuilder;
import com.linecorp.armeria.common.thrift.ThriftCall;
import com.linecorp.armeria.common.thrift.ThriftReply;
import com.linecorp.armeria.common.util.SafeCloseable;
import com.linecorp.armeria.server.PathMappingContext;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.VirtualHost;
import io.netty.util.AttributeKey;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.thrift.protocol.TMessageType;
import org.assertj.core.util.Lists;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;


public class RequestContextExportingAppenderTest {
    private static final AttributeKey<CustomValue> MY_ATTR = AttributeKey.valueOf(RequestContextExportingAppenderTest.class, "MY_ATTR");

    private static final RpcRequest RPC_REQ = new DefaultRpcRequest(Object.class, "hello", "world");

    private static final RpcResponse RPC_RES = new DefaultRpcResponse("Hello, world!");

    private static final ThriftCall THRIFT_CALL = new ThriftCall(new org.apache.thrift.protocol.TMessage("hello", TMessageType.CALL, 1), new com.linecorp.armeria.common.logback.HelloService.hello_args("world"));

    private static final ThriftReply THRIFT_REPLY = new ThriftReply(new org.apache.thrift.protocol.TMessage("hello", TMessageType.REPLY, 1), new com.linecorp.armeria.common.logback.HelloService.hello_result("Hello, world!"));

    private static final LoggerContext context = ((LoggerContext) (LoggerFactory.getILoggerFactory()));

    private static final Logger rootLogger = ((Logger) (LoggerFactory.getLogger(ROOT_LOGGER_NAME)));

    private static final Logger logger = ((Logger) (LoggerFactory.getLogger(RequestContextExportingAppenderTest.class)));

    @Rule
    public final TestName testName = new TestName();

    private Logger testLogger;

    @Test
    public void testMutabilityAndImmutability() {
        final AttributeKey<Object> someAttr = AttributeKey.valueOf(RequestContextExportingAppenderTest.class, "SOME_ATTR");
        final RequestContextExportingAppender a = new RequestContextExportingAppender();
        // Ensure mutability before start.
        a.addBuiltIn(ELAPSED_NANOS);
        assertThat(a.getBuiltIns()).containsExactly(ELAPSED_NANOS);
        a.addAttribute("some-attr", someAttr);
        assertThat(a.getAttributes()).containsOnlyKeys("some-attr").containsValue(someAttr);
        a.addHttpRequestHeader(USER_AGENT);
        assertThat(a.getHttpRequestHeaders()).containsExactly(USER_AGENT);
        a.addHttpResponseHeader(SET_COOKIE);
        assertThat(a.getHttpResponseHeaders()).containsExactly(SET_COOKIE);
        final ListAppender<ILoggingEvent> la = new ListAppender();
        a.addAppender(la);
        a.start();
        la.start();
        // Ensure immutability after start.
        assertThatThrownBy(() -> a.addBuiltIn(BuiltInProperty.REQ_PATH)).isExactlyInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> a.addAttribute("my-attr", MY_ATTR)).isExactlyInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> a.addHttpRequestHeader(HttpHeaderNames.ACCEPT)).isExactlyInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> a.addHttpResponseHeader(HttpHeaderNames.DATE)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testXmlConfig() throws Exception {
        try {
            final JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(RequestContextExportingAppenderTest.context);
            RequestContextExportingAppenderTest.context.reset();
            configurator.doConfigure(getClass().getResource("testXmlConfig.xml"));
            final RequestContextExportingAppender rcea = ((RequestContextExportingAppender) (RequestContextExportingAppenderTest.logger.getAppender("RCEA")));
            assertThat(rcea).isNotNull();
            assertThat(rcea.getBuiltIns()).containsExactly(REMOTE_HOST);
            assertThat(rcea.getHttpRequestHeaders()).containsExactly(USER_AGENT);
            assertThat(rcea.getHttpResponseHeaders()).containsExactly(SET_COOKIE);
            final AttributeKey<Object> fooAttr = AttributeKey.valueOf("com.example.AttrKeys#FOO");
            final AttributeKey<Object> barAttr = AttributeKey.valueOf("com.example.AttrKeys#BAR");
            assertThat(rcea.getAttributes()).containsOnly(new java.util.AbstractMap.SimpleEntry("foo", fooAttr), new java.util.AbstractMap.SimpleEntry("bar", barAttr));
        } finally {
            // Revert to the original configuration.
            final JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(RequestContextExportingAppenderTest.context);
            RequestContextExportingAppenderTest.context.reset();
            configurator.doConfigure(getClass().getResource("/logback-test.xml"));
        }
    }

    @Test
    public void testWithoutContext() {
        final List<ILoggingEvent> events = prepare();
        final ILoggingEvent e = log(events);
        assertThat(e.getMDCPropertyMap()).isEmpty();
    }

    @Test
    public void testMdcPropertyPreservation() throws Exception {
        final List<ILoggingEvent> events = prepare(( a) -> a.addBuiltIn(REQ_DIRECTION));
        MDC.put("some-prop", "some-value");
        final ServiceRequestContext ctx = RequestContextExportingAppenderTest.newServiceContext("/foo", null);
        try (SafeCloseable ignored = ctx.push()) {
            final ILoggingEvent e = log(events);
            final Map<String, String> mdc = e.getMDCPropertyMap();
            assertThat(mdc).containsEntry("req.direction", "INBOUND").containsEntry("some-prop", "some-value").hasSize(2);
        } finally {
            MDC.remove("some-prop");
        }
    }

    @Test
    public void testServiceContextWithoutLogs() throws Exception {
        final List<ILoggingEvent> events = prepare(( a) -> {
            // Export all properties.
            for (BuiltInProperty p : BuiltInProperty.values()) {
                a.addBuiltIn(p);
            }
        });
        final ServiceRequestContext ctx = RequestContextExportingAppenderTest.newServiceContext("/foo", null);
        try (SafeCloseable ignored = ctx.push()) {
            final ILoggingEvent e = log(events);
            final Map<String, String> mdc = e.getMDCPropertyMap();
            assertThat(mdc).containsEntry("local.host", "server.com").containsEntry("local.ip", "5.6.7.8").containsEntry("local.port", "8080").containsEntry("remote.host", "client.com").containsEntry("remote.ip", "1.2.3.4").containsEntry("remote.port", "5678").containsEntry("client.ip", "9.10.11.12").containsEntry("req.direction", "INBOUND").containsEntry("req.authority", "server.com:8080").containsEntry("req.method", "GET").containsEntry("req.path", "/foo").containsEntry("req.query", null).containsEntry("scheme", "unknown+h2").containsEntry("tls.session_id", "0101020305080d15").containsEntry("tls.proto", "TLSv1.2").containsEntry("tls.cipher", "some-cipher").hasSize(16);
        }
    }

    @Test
    public void testServiceContextWithMinimalLogs() throws Exception {
        final List<ILoggingEvent> events = prepare(( a) -> {
            // Export all properties.
            for (BuiltInProperty p : BuiltInProperty.values()) {
                a.addBuiltIn(p);
            }
        });
        final ServiceRequestContext ctx = RequestContextExportingAppenderTest.newServiceContext("/foo", "name=alice");
        try (SafeCloseable ignored = ctx.push()) {
            final RequestLogBuilder log = ctx.logBuilder();
            log.endRequest();
            log.endResponse();
            final ILoggingEvent e = log(events);
            final Map<String, String> mdc = e.getMDCPropertyMap();
            assertThat(mdc).containsEntry("local.host", "server.com").containsEntry("local.ip", "5.6.7.8").containsEntry("local.port", "8080").containsEntry("remote.host", "client.com").containsEntry("remote.ip", "1.2.3.4").containsEntry("remote.port", "5678").containsEntry("client.ip", "9.10.11.12").containsEntry("req.direction", "INBOUND").containsEntry("req.authority", "server.com:8080").containsEntry("req.method", "GET").containsEntry("req.path", "/foo").containsEntry("req.query", "name=alice").containsEntry("scheme", "none+h2").containsEntry("req.content_length", "0").containsEntry("res.status_code", "0").containsEntry("res.content_length", "0").containsEntry("tls.session_id", "0101020305080d15").containsEntry("tls.proto", "TLSv1.2").containsEntry("tls.cipher", "some-cipher").containsKey("elapsed_nanos").hasSize(20);
        }
    }

    @Test
    public void testServiceContextWithFullLogs() throws Exception {
        final List<ILoggingEvent> events = prepare(( a) -> {
            // Export all properties.
            for (BuiltInProperty p : BuiltInProperty.values()) {
                a.addBuiltIn(p);
            }
            // .. and an attribute.
            a.addAttribute("my_attr", RequestContextExportingAppenderTest.MY_ATTR, new CustomValueStringifier());
            // .. and some HTTP headers.
            a.addHttpRequestHeader(USER_AGENT);
            a.addHttpResponseHeader(DATE);
        });
        final ServiceRequestContext ctx = RequestContextExportingAppenderTest.newServiceContext("/foo", "bar=baz");
        try (SafeCloseable ignored = ctx.push()) {
            final RequestLogBuilder log = ctx.logBuilder();
            log.serializationFormat(BINARY);
            log.requestLength(64);
            log.requestHeaders(HttpHeaders.of(USER_AGENT, "some-client"));
            log.requestContent(RequestContextExportingAppenderTest.RPC_REQ, RequestContextExportingAppenderTest.THRIFT_CALL);
            log.endRequest();
            log.responseLength(128);
            log.responseHeaders(HttpHeaders.of(200).set(DATE, "some-date"));
            log.responseContent(RequestContextExportingAppenderTest.RPC_RES, RequestContextExportingAppenderTest.THRIFT_REPLY);
            log.endResponse();
            final ILoggingEvent e = log(events);
            final Map<String, String> mdc = e.getMDCPropertyMap();
            assertThat(mdc).containsEntry("local.host", "server.com").containsEntry("local.ip", "5.6.7.8").containsEntry("local.port", "8080").containsEntry("remote.host", "client.com").containsEntry("remote.ip", "1.2.3.4").containsEntry("remote.port", "5678").containsEntry("client.ip", "9.10.11.12").containsEntry("req.direction", "INBOUND").containsEntry("req.authority", "server.com:8080").containsEntry("req.method", "GET").containsEntry("req.path", "/foo").containsEntry("req.query", "bar=baz").containsEntry("scheme", "tbinary+h2").containsEntry("req.content_length", "64").containsEntry("req.rpc_method", "hello").containsEntry("req.rpc_params", "[world]").containsEntry("res.status_code", "200").containsEntry("res.content_length", "128").containsEntry("res.rpc_result", "Hello, world!").containsEntry("req.http_headers.user-agent", "some-client").containsEntry("res.http_headers.date", "some-date").containsEntry("tls.session_id", "0101020305080d15").containsEntry("tls.proto", "TLSv1.2").containsEntry("tls.cipher", "some-cipher").containsEntry("attrs.my_attr", "some-attr").containsKey("elapsed_nanos").hasSize(26);
        }
    }

    @Test
    public void testClientContextWithMinimalLogs() throws Exception {
        final List<ILoggingEvent> events = prepare(( a) -> {
            // Export all properties.
            for (BuiltInProperty p : BuiltInProperty.values()) {
                a.addBuiltIn(p);
            }
        });
        final ClientRequestContext ctx = RequestContextExportingAppenderTest.newClientContext("/foo", "type=bar");
        try (SafeCloseable ignored = ctx.push()) {
            final ILoggingEvent e = log(events);
            final Map<String, String> mdc = e.getMDCPropertyMap();
            assertThat(mdc).containsEntry("local.host", "client.com").containsEntry("local.ip", "5.6.7.8").containsEntry("local.port", "5678").containsEntry("remote.host", "server.com").containsEntry("remote.ip", "1.2.3.4").containsEntry("remote.port", "8080").containsEntry("req.direction", "OUTBOUND").containsEntry("req.authority", "server.com:8080").containsEntry("req.method", "GET").containsEntry("req.path", "/foo").containsEntry("req.query", "type=bar").containsEntry("scheme", "unknown+h2").containsEntry("tls.session_id", "0101020305080d15").containsEntry("tls.proto", "TLSv1.2").containsEntry("tls.cipher", "some-cipher").hasSize(15);
        }
    }

    @Test
    public void testClientContextWithFullLogs() throws Exception {
        final List<ILoggingEvent> events = prepare(( a) -> {
            // Export all properties.
            for (BuiltInProperty p : BuiltInProperty.values()) {
                a.addBuiltIn(p);
            }
            // .. and an attribute.
            a.addAttribute("my_attr", RequestContextExportingAppenderTest.MY_ATTR, new CustomValueStringifier());
            // .. and some HTTP headers.
            a.addHttpRequestHeader(USER_AGENT);
            a.addHttpResponseHeader(DATE);
        });
        final ClientRequestContext ctx = RequestContextExportingAppenderTest.newClientContext("/bar", null);
        try (SafeCloseable ignored = ctx.push()) {
            final RequestLogBuilder log = ctx.logBuilder();
            log.serializationFormat(BINARY);
            log.requestLength(64);
            log.requestHeaders(HttpHeaders.of(USER_AGENT, "some-client"));
            log.requestContent(RequestContextExportingAppenderTest.RPC_REQ, RequestContextExportingAppenderTest.THRIFT_CALL);
            log.endRequest();
            log.responseLength(128);
            log.responseHeaders(HttpHeaders.of(200).set(DATE, "some-date"));
            log.responseContent(RequestContextExportingAppenderTest.RPC_RES, RequestContextExportingAppenderTest.THRIFT_REPLY);
            log.endResponse();
            final ILoggingEvent e = log(events);
            final Map<String, String> mdc = e.getMDCPropertyMap();
            assertThat(mdc).containsEntry("local.host", "client.com").containsEntry("local.ip", "5.6.7.8").containsEntry("local.port", "5678").containsEntry("remote.host", "server.com").containsEntry("remote.ip", "1.2.3.4").containsEntry("remote.port", "8080").containsEntry("req.direction", "OUTBOUND").containsEntry("req.authority", "server.com:8080").containsEntry("req.method", "GET").containsEntry("req.path", "/bar").containsEntry("req.query", null).containsEntry("scheme", "tbinary+h2").containsEntry("req.content_length", "64").containsEntry("req.rpc_method", "hello").containsEntry("req.rpc_params", "[world]").containsEntry("res.status_code", "200").containsEntry("res.content_length", "128").containsEntry("res.rpc_result", "Hello, world!").containsEntry("req.http_headers.user-agent", "some-client").containsEntry("res.http_headers.date", "some-date").containsEntry("tls.session_id", "0101020305080d15").containsEntry("tls.proto", "TLSv1.2").containsEntry("tls.cipher", "some-cipher").containsEntry("attrs.my_attr", "some-attr").containsKey("elapsed_nanos").hasSize(25);
        }
    }

    private static class DummyPathMappingContext implements PathMappingContext {
        private final VirtualHost virtualHost;

        private final String hostname;

        private final String path;

        @Nullable
        private final String query;

        private final HttpHeaders headers;

        private final List<Object> summary;

        @Nullable
        private Throwable delayedCause;

        DummyPathMappingContext(VirtualHost virtualHost, String hostname, String path, @Nullable
        String query, HttpHeaders headers) {
            this.virtualHost = Objects.requireNonNull(virtualHost, "virtualHost");
            this.hostname = Objects.requireNonNull(hostname, "hostname");
            this.path = Objects.requireNonNull(path, "path");
            this.query = query;
            this.headers = Objects.requireNonNull(headers, "headers");
            summary = Lists.newArrayList(hostname, path, headers.method());
        }

        @Override
        public VirtualHost virtualHost() {
            return virtualHost;
        }

        @Override
        public String hostname() {
            return hostname;
        }

        @Override
        public HttpMethod method() {
            return headers.method();
        }

        @Override
        public String path() {
            return path;
        }

        @Nullable
        @Override
        public String query() {
            return query;
        }

        @Nullable
        @Override
        public MediaType consumeType() {
            return null;
        }

        @Override
        public List<MediaType> produceTypes() {
            return ImmutableList.of();
        }

        @Override
        public boolean isCorsPreflight() {
            return false;
        }

        @Override
        public List<Object> summary() {
            return summary;
        }

        @Override
        public void delayThrowable(Throwable delayedCause) {
            this.delayedCause = delayedCause;
        }

        @Override
        public Optional<Throwable> delayedThrowable() {
            return Optional.ofNullable(delayedCause);
        }
    }
}

