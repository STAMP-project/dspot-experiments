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
package com.linecorp.armeria.client.thrift;


import HelloService.Iface;
import com.linecorp.armeria.client.Clients;
import com.linecorp.armeria.client.SessionProtocolNegotiationCache;
import com.linecorp.armeria.client.SessionProtocolNegotiationException;
import com.linecorp.armeria.common.ClosedSessionException;
import com.linecorp.armeria.common.SessionProtocol;
import com.linecorp.armeria.common.thrift.ThriftProtocolFactories;
import com.linecorp.armeria.service.test.thrift.main.HelloService;
import com.linecorp.armeria.service.test.thrift.main.HelloService.Processor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test to verify interaction between armeria client and official thrift
 * library's {@link TServlet}.
 */
public class ThriftOverHttpClientTServletIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(ThriftOverHttpClientTServletIntegrationTest.class);

    private static final int MAX_RETRIES = 9;

    private static final String TSERVLET_PATH = "/thrift";

    @SuppressWarnings("unchecked")
    private static final Servlet thriftServlet = new org.apache.thrift.server.TServlet(new Processor(( name) -> ("Hello, " + name) + '!'), ThriftProtocolFactories.BINARY);

    private static final Servlet rootServlet = new HttpServlet() {
        private static final long serialVersionUID = 6765028749367036441L;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
            resp.setStatus(404);
            resp.setContentLength(0);
        }
    };

    private static final AtomicBoolean sendConnectionClose = new AtomicBoolean();

    private static Server http1server;

    private static Server http2server;

    @Test
    public void sendHelloViaHttp1() throws Exception {
        final AtomicReference<SessionProtocol> sessionProtocol = new AtomicReference<>();
        final HelloService.Iface client = ThriftOverHttpClientTServletIntegrationTest.newSchemeCapturingClient(ThriftOverHttpClientTServletIntegrationTest.http1uri(SessionProtocol.HTTP), sessionProtocol);
        for (int i = 0; i <= (ThriftOverHttpClientTServletIntegrationTest.MAX_RETRIES); i++) {
            try {
                Assert.assertEquals("Hello, old world!", client.hello("old world"));
                assertThat(sessionProtocol.get()).isEqualTo(SessionProtocol.H1C);
                if (i != 0) {
                    ThriftOverHttpClientTServletIntegrationTest.logger.warn("Succeeded after {} retries.", i);
                }
                break;
            } catch (ClosedSessionException e) {
                // Flaky test; try again.
                // FIXME(trustin): Fix flakiness.
                if (i == (ThriftOverHttpClientTServletIntegrationTest.MAX_RETRIES)) {
                    throw e;
                }
            }
        }
    }

    /**
     * When an upgrade request is rejected with 'Connection: close', the client should retry the connection
     * attempt silently with explicit H1C.
     */
    @Test
    public void sendHelloViaHttp1WithConnectionClose() throws Exception {
        ThriftOverHttpClientTServletIntegrationTest.sendConnectionClose.set(true);
        final AtomicReference<SessionProtocol> sessionProtocol = new AtomicReference<>();
        final HelloService.Iface client = ThriftOverHttpClientTServletIntegrationTest.newSchemeCapturingClient(ThriftOverHttpClientTServletIntegrationTest.http1uri(SessionProtocol.HTTP), sessionProtocol);
        for (int i = 0; i <= (ThriftOverHttpClientTServletIntegrationTest.MAX_RETRIES); i++) {
            try {
                Assert.assertEquals("Hello, ancient world!", client.hello("ancient world"));
                assertThat(sessionProtocol.get()).isEqualTo(SessionProtocol.H1C);
                if (i != 0) {
                    ThriftOverHttpClientTServletIntegrationTest.logger.warn("Succeeded after {} retries.", i);
                }
                break;
            } catch (ClosedSessionException e) {
                // Flaky test; try again.
                // FIXME(trustin): Fix flakiness.
                if (i == (ThriftOverHttpClientTServletIntegrationTest.MAX_RETRIES)) {
                    throw e;
                }
            }
        }
    }

    @Test
    public void sendHelloViaHttp2() throws Exception {
        final AtomicReference<SessionProtocol> sessionProtocol = new AtomicReference<>();
        final HelloService.Iface client = ThriftOverHttpClientTServletIntegrationTest.newSchemeCapturingClient(ThriftOverHttpClientTServletIntegrationTest.http2uri(SessionProtocol.HTTP), sessionProtocol);
        Assert.assertEquals("Hello, new world!", client.hello("new world"));
        assertThat(sessionProtocol.get()).isEqualTo(SessionProtocol.H2C);
    }

    /**
     * {@link SessionProtocolNegotiationException} should be raised if a user specified H2C explicitly and the
     * client failed to upgrade.
     */
    @Test
    public void testRejectedUpgrade() throws Exception {
        final InetSocketAddress remoteAddress = new InetSocketAddress("127.0.0.1", ThriftOverHttpClientTServletIntegrationTest.http1Port());
        Assert.assertFalse(SessionProtocolNegotiationCache.isUnsupported(remoteAddress, SessionProtocol.H2C));
        final HelloService.Iface client = Clients.newClient(ThriftOverHttpClientTServletIntegrationTest.http1uri(SessionProtocol.H2C), Iface.class);
        try {
            client.hello("unused");
            Assert.fail();
        } catch (SessionProtocolNegotiationException e) {
            // Test if a failed upgrade attempt triggers an exception with
            // both 'expected' and 'actual' protocols.
            assertThat(e.expected()).isEqualTo(SessionProtocol.H2C);
            assertThat(e.actual()).contains(SessionProtocol.H1C);
            // .. and if the negotiation cache is updated.
            Assert.assertTrue(SessionProtocolNegotiationCache.isUnsupported(remoteAddress, SessionProtocol.H2C));
        }
        try {
            client.hello("unused");
            Assert.fail();
        } catch (SessionProtocolNegotiationException e) {
            // Test if no upgrade attempt is made thanks to the cache.
            assertThat(e.expected()).isEqualTo(SessionProtocol.H2C);
            // It has no idea about the actual protocol, because it did not create any connection.
            assertThat(e.actual().isPresent()).isFalse();
        }
    }

    private static class ConnectionCloseFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            chain.doFilter(request, response);
            if (ThriftOverHttpClientTServletIntegrationTest.sendConnectionClose.get()) {
                setHeader("Connection", "close");
            }
        }

        @Override
        public void destroy() {
        }
    }
}

