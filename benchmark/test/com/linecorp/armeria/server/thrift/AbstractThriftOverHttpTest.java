/**
 * Copyright 2015 LINE Corporation
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
package com.linecorp.armeria.server.thrift;


import HelloService.hello_args;
import HelloService.hello_result;
import HttpHeaderNames.ACCEPT;
import OnewayHelloService.Iface;
import RequestLogAvailability.COMPLETE;
import TMessageType.CALL;
import TMessageType.EXCEPTION;
import TMessageType.REPLY;
import ThriftProtocolFactories.BINARY;
import com.google.common.base.Strings;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.common.RpcRequest;
import com.linecorp.armeria.common.RpcResponse;
import com.linecorp.armeria.common.logging.RequestLog;
import com.linecorp.armeria.common.thrift.ThriftCall;
import com.linecorp.armeria.common.thrift.ThriftReply;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.Service;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.service.test.thrift.main.HelloService;
import com.linecorp.armeria.service.test.thrift.main.HelloService.AsyncIface;
import com.linecorp.armeria.service.test.thrift.main.OnewayHelloService;
import com.linecorp.armeria.service.test.thrift.main.SleepService;
import com.linecorp.armeria.testing.internal.AnticipatedException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.transport.TTransport;
import org.junit.Test;


public abstract class AbstractThriftOverHttpTest {
    private static final String LARGER_THAN_TLS = Strings.repeat("A", 16384);

    private static final Server server;

    private static int httpPort;

    private static int httpsPort;

    private static volatile boolean recordMessageLogs;

    private static final BlockingQueue<RequestLog> requestLogs = new LinkedBlockingQueue<>();

    abstract static class HelloServiceBase implements AsyncIface {
        @Override
        @SuppressWarnings("unchecked")
        public void hello(String name, AsyncMethodCallback resultHandler) throws TException {
            resultHandler.onComplete(getResponse(name));
        }

        protected String getResponse(String name) {
            return ("Hello, " + name) + '!';
        }
    }

    static class HelloServiceChild extends AbstractThriftOverHttpTest.HelloServiceBase {
        @Override
        protected String getResponse(String name) {
            return ("Goodbye, " + name) + '!';
        }
    }

    static {
        final ServerBuilder sb = new ServerBuilder();
        try {
            sb.http(0);
            sb.https(0);
            sb.tlsSelfSigned();
            sb.service("/hello", THttpService.of(((AsyncIface) (( name, resultHandler) -> resultHandler.onComplete((("Hello, " + name) + '!'))))));
            sb.service("/hellochild", THttpService.of(new AbstractThriftOverHttpTest.HelloServiceChild()));
            sb.service("/hello_oneway_sync", THttpService.of(((OnewayHelloService.Iface) (( name) -> {
                if (!("success".equals(name))) {
                    throw new AnticipatedException("expected 'success'");
                }
            }))));
            sb.service("/hello_oneway_async", THttpService.of(((OnewayHelloService.AsyncIface) (( name, resultHandler) -> {
                if ("success".equals(name)) {
                    resultHandler.onComplete(null);
                } else {
                    resultHandler.onError(new AnticipatedException("expected 'success'"));
                }
            }))));
            sb.service("/exception", THttpService.of(((AsyncIface) (( name, resultHandler) -> resultHandler.onError(new AnticipatedException(name))))));
            sb.service("/sleep", THttpService.of(((SleepService.AsyncIface) (( milliseconds, resultHandler) -> RequestContext.current().eventLoop().schedule(() -> resultHandler.onComplete(milliseconds), milliseconds, TimeUnit.MILLISECONDS)))));
            // Response larger than a h1 TLS record
            sb.service("/large", THttpService.of(((AsyncIface) (( name, resultHandler) -> resultHandler.onComplete(LARGER_THAN_TLS)))));
            sb.decorator(LoggingService.newDecorator());
            final Function<Service<HttpRequest, HttpResponse>, Service<HttpRequest, HttpResponse>> logCollectingDecorator = ( s) -> new com.linecorp.armeria.server.SimpleDecoratingService<HttpRequest, HttpResponse>(s) {
                @Override
                public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
                    if (AbstractThriftOverHttpTest.recordMessageLogs) {
                        ctx.log().addListener(AbstractThriftOverHttpTest.requestLogs::add, COMPLETE);
                    }
                    return delegate().serve(ctx, req);
                }
            };
            sb.decorator(logCollectingDecorator);
        } catch (Exception e) {
            throw new Error(e);
        }
        server = sb.build();
    }

    @Test
    public void testHttpInvocation() throws Exception {
        try (TTransport transport = newTransport("http", "/hello")) {
            final HelloService.Client client = new HelloService.Client.Factory().getClient(BINARY.getProtocol(transport));
            assertThat(client.hello("Trustin")).isEqualTo("Hello, Trustin!");
        }
    }

    @Test
    public void testInheritedThriftService() throws Exception {
        try (TTransport transport = newTransport("http", "/hellochild")) {
            final HelloService.Client client = new HelloService.Client.Factory().getClient(BINARY.getProtocol(transport));
            assertThat(client.hello("Trustin")).isEqualTo("Goodbye, Trustin!");
        }
    }

    @Test
    public void testOnewaySyncInvocation() throws Exception {
        AbstractThriftOverHttpTest.recordMessageLogs = true;
        try (TTransport transport = newTransport("http", "/hello_oneway_sync")) {
            final OnewayHelloService.Client client = new OnewayHelloService.Client.Factory().getClient(BINARY.getProtocol(transport));
            // Success
            client.hello("success");
            AbstractThriftOverHttpTest.verifyOneWayInvocation(Iface.class, "success");
        }
        try (TTransport transport = newTransport("http", "/hello_oneway_sync")) {
            final OnewayHelloService.Client client = new OnewayHelloService.Client.Factory().getClient(BINARY.getProtocol(transport));
            // Failure
            client.hello("failure");
            AbstractThriftOverHttpTest.verifyOneWayInvocation(Iface.class, "failure");
        }
    }

    @Test
    public void testOnewayAsyncInvocation() throws Exception {
        AbstractThriftOverHttpTest.recordMessageLogs = true;
        try (TTransport transport = newTransport("http", "/hello_oneway_async")) {
            final OnewayHelloService.Client client = new OnewayHelloService.Client.Factory().getClient(BINARY.getProtocol(transport));
            // Success
            client.hello("success");
            AbstractThriftOverHttpTest.verifyOneWayInvocation(AsyncIface.class, "success");
        }
        try (TTransport transport = newTransport("http", "/hello_oneway_async")) {
            final OnewayHelloService.Client client = new OnewayHelloService.Client.Factory().getClient(BINARY.getProtocol(transport));
            // Failure
            client.hello("failure");
            AbstractThriftOverHttpTest.verifyOneWayInvocation(AsyncIface.class, "failure");
        }
    }

    @Test
    public void testHttpsInvocation() throws Exception {
        try (TTransport transport = newTransport("https", "/hello")) {
            final HelloService.Client client = new HelloService.Client.Factory().getClient(BINARY.getProtocol(transport));
            assertThat(client.hello("Trustin")).isEqualTo("Hello, Trustin!");
        }
    }

    @Test
    public void testLargeHttpsInvocation() throws Exception {
        try (TTransport transport = newTransport("https", "/large")) {
            final HelloService.Client client = new HelloService.Client.Factory().getClient(BINARY.getProtocol(transport));
            assertThat(client.hello("Trustin")).isEqualTo(AbstractThriftOverHttpTest.LARGER_THAN_TLS);
        }
    }

    @Test
    public void testAcceptHeaderWithCommaSeparatedMediaTypes() throws Exception {
        try (TTransport transport = newTransport("http", "/hello", HttpHeaders.of(ACCEPT, "text/plain, */*"))) {
            final HelloService.Client client = new HelloService.Client.Factory().getClient(BINARY.getProtocol(transport));
            assertThat(client.hello("Trustin")).isEqualTo("Hello, Trustin!");
        }
    }

    @Test
    public void testAcceptHeaderWithQValues() throws Exception {
        // Server should choose TBINARY because it has higher q-value (0.5) than that of TTEXT (0.2)
        try (TTransport transport = newTransport("http", "/hello", HttpHeaders.of(ACCEPT, ("application/x-thrift; protocol=TTEXT; q=0.2, " + "application/x-thrift; protocol=TBINARY; q=0.5")))) {
            final HelloService.Client client = new HelloService.Client.Factory().getClient(BINARY.getProtocol(transport));
            assertThat(client.hello("Trustin")).isEqualTo("Hello, Trustin!");
        }
    }

    @Test
    public void testAcceptHeaderWithDefaultQValues() throws Exception {
        // Server should choose TBINARY because it has higher q-value (default 1.0) than that of TTEXT (0.2)
        try (TTransport transport = newTransport("http", "/hello", HttpHeaders.of(ACCEPT, ("application/x-thrift; protocol=TTEXT; q=0.2, " + "application/x-thrift; protocol=TBINARY")))) {
            final HelloService.Client client = new HelloService.Client.Factory().getClient(BINARY.getProtocol(transport));
            assertThat(client.hello("Trustin")).isEqualTo("Hello, Trustin!");
        }
    }

    @Test
    public void testAcceptHeaderWithUnsupportedMediaTypes() throws Exception {
        // Server should choose TBINARY because it does not support the media type
        // with the highest preference (text/plain).
        try (TTransport transport = newTransport("http", "/hello", HttpHeaders.of(ACCEPT, "application/x-thrift; protocol=TBINARY; q=0.2, text/plain"))) {
            final HelloService.Client client = new HelloService.Client.Factory().getClient(BINARY.getProtocol(transport));
            assertThat(client.hello("Trustin")).isEqualTo("Hello, Trustin!");
        }
    }

    @Test(timeout = 10000)
    public void testMessageLogsForCall() throws Exception {
        try (TTransport transport = newTransport("http", "/hello")) {
            final HelloService.Client client = new HelloService.Client.Factory().getClient(BINARY.getProtocol(transport));
            AbstractThriftOverHttpTest.recordMessageLogs = true;
            client.hello("Trustin");
        }
        final RequestLog log = AbstractThriftOverHttpTest.requestLogs.take();
        assertThat(log.requestHeaders()).isInstanceOf(HttpHeaders.class);
        assertThat(log.requestContent()).isInstanceOf(RpcRequest.class);
        assertThat(log.rawRequestContent()).isInstanceOf(ThriftCall.class);
        final RpcRequest request = ((RpcRequest) (log.requestContent()));
        assertThat(request.serviceType()).isEqualTo(AsyncIface.class);
        assertThat(request.method()).isEqualTo("hello");
        assertThat(request.params()).containsExactly("Trustin");
        final ThriftCall rawRequest = ((ThriftCall) (log.rawRequestContent()));
        assertThat(rawRequest.header().type).isEqualTo(CALL);
        assertThat(rawRequest.header().name).isEqualTo("hello");
        assertThat(rawRequest.args()).isInstanceOf(hello_args.class);
        assertThat(getName()).isEqualTo("Trustin");
        assertThat(log.responseHeaders()).isInstanceOf(HttpHeaders.class);
        assertThat(log.responseContent()).isInstanceOf(RpcResponse.class);
        assertThat(log.rawResponseContent()).isInstanceOf(ThriftReply.class);
        final RpcResponse response = ((RpcResponse) (log.responseContent()));
        assertThat(response.get()).isEqualTo("Hello, Trustin!");
        final ThriftReply rawResponse = ((ThriftReply) (log.rawResponseContent()));
        assertThat(rawResponse.header().type).isEqualTo(REPLY);
        assertThat(rawResponse.header().name).isEqualTo("hello");
        assertThat(rawResponse.result()).isInstanceOf(hello_result.class);
        assertThat(getSuccess()).isEqualTo("Hello, Trustin!");
    }

    @Test(timeout = 10000)
    public void testMessageLogsForException() throws Exception {
        try (TTransport transport = newTransport("http", "/exception")) {
            final HelloService.Client client = new HelloService.Client.Factory().getClient(BINARY.getProtocol(transport));
            AbstractThriftOverHttpTest.recordMessageLogs = true;
            assertThatThrownBy(() -> client.hello("Trustin")).isInstanceOf(TApplicationException.class);
        }
        final RequestLog log = AbstractThriftOverHttpTest.requestLogs.take();
        assertThat(log.requestHeaders()).isInstanceOf(HttpHeaders.class);
        assertThat(log.requestContent()).isInstanceOf(RpcRequest.class);
        assertThat(log.rawRequestContent()).isInstanceOf(ThriftCall.class);
        final RpcRequest request = ((RpcRequest) (log.requestContent()));
        assertThat(request.serviceType()).isEqualTo(AsyncIface.class);
        assertThat(request.method()).isEqualTo("hello");
        assertThat(request.params()).containsExactly("Trustin");
        final ThriftCall rawRequest = ((ThriftCall) (log.rawRequestContent()));
        assertThat(rawRequest.header().type).isEqualTo(CALL);
        assertThat(rawRequest.header().name).isEqualTo("hello");
        assertThat(rawRequest.args()).isInstanceOf(hello_args.class);
        assertThat(getName()).isEqualTo("Trustin");
        assertThat(log.responseHeaders()).isInstanceOf(HttpHeaders.class);
        assertThat(log.responseContent()).isInstanceOf(RpcResponse.class);
        assertThat(log.rawResponseContent()).isInstanceOf(ThriftReply.class);
        final RpcResponse response = ((RpcResponse) (log.responseContent()));
        assertThat(response.cause()).isNotNull();
        final ThriftReply rawResponse = ((ThriftReply) (log.rawResponseContent()));
        assertThat(rawResponse.header().type).isEqualTo(EXCEPTION);
        assertThat(rawResponse.header().name).isEqualTo("hello");
        assertThat(rawResponse.exception()).isNotNull();
    }
}

