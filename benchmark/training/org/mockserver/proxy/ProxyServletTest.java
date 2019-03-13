package org.mockserver.proxy;


import RetrieveType.LOGS;
import RetrieveType.RECORDED_EXPECTATIONS;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.MediaType;
import io.netty.channel.ChannelHandlerContext;
import java.util.Collections;
import org.apache.commons.codec.Charsets;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.matchers.Times;
import org.mockserver.mock.HttpStateHandler;
import org.mockserver.mock.action.ActionHandler;
import org.mockserver.model.HttpRequest;
import org.mockserver.scheduler.Scheduler;
import org.mockserver.serialization.ExpectationSerializer;
import org.mockserver.serialization.HttpRequestSerializer;
import org.mockserver.serialization.PortBindingSerializer;
import org.mockserver.servlet.responsewriter.ServletResponseWriter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class ProxyServletTest {
    private HttpRequestSerializer httpRequestSerializer = new HttpRequestSerializer(new MockServerLogger());

    private ExpectationSerializer expectationSerializer = new ExpectationSerializer(new MockServerLogger());

    private PortBindingSerializer portBindingSerializer = new PortBindingSerializer(new MockServerLogger());

    private HttpStateHandler httpStateHandler;

    private ActionHandler mockActionHandler;

    private MockServerLogger mockLogFormatter;

    private Scheduler scheduler;

    @InjectMocks
    private ProxyServlet proxyServlet;

    private MockHttpServletResponse response;

    @Test
    public void shouldRetrieveRequests() {
        // given
        MockHttpServletRequest expectationRetrieveRequestsRequest = buildHttpServletRequest("PUT", "/mockserver/retrieve", httpRequestSerializer.serialize(request("request_one")));
        httpStateHandler.log(new org.mockserver.log.model.RequestLogEntry(request("request_one")));
        // when
        proxyServlet.service(expectationRetrieveRequestsRequest, response);
        // then
        assertResponse(response, 200, httpRequestSerializer.serialize(Collections.singletonList(request("request_one"))));
    }

    @Test
    public void shouldClear() {
        // given
        httpStateHandler.add(new org.mockserver.mock.Expectation(request("request_one")).thenRespond(response("response_one")));
        httpStateHandler.log(new org.mockserver.log.model.RequestLogEntry(request("request_one")));
        MockHttpServletRequest clearRequest = buildHttpServletRequest("PUT", "/mockserver/clear", httpRequestSerializer.serialize(request("request_one")));
        // when
        proxyServlet.service(clearRequest, response);
        // then
        assertResponse(response, 200, "");
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(request("request_one")), Is.is(IsNull.nullValue()));
        MatcherAssert.assertThat(httpStateHandler.retrieve(request("/retrieve").withMethod("PUT").withBody(httpRequestSerializer.serialize(request("request_one")))), Is.is(response().withBody("[]", MediaType.JSON_UTF_8).withStatusCode(200)));
    }

    @Test
    public void shouldReturnStatus() {
        // given
        MockHttpServletRequest statusRequest = buildHttpServletRequest("PUT", "/status", "");
        // when
        proxyServlet.service(statusRequest, response);
        // then
        assertResponse(response, 200, portBindingSerializer.serialize(portBinding(80)));
    }

    @Test
    public void shouldBindNewPorts() {
        // given
        MockHttpServletRequest statusRequest = buildHttpServletRequest("PUT", "/bind", portBindingSerializer.serialize(portBinding(1080, 1090)));
        // when
        proxyServlet.service(statusRequest, response);
        // then
        assertResponse(response, 501, "");
    }

    @Test
    public void shouldStop() throws InterruptedException {
        // given
        MockHttpServletRequest statusRequest = buildHttpServletRequest("PUT", "/stop", "");
        // when
        proxyServlet.service(statusRequest, response);
        // then
        assertResponse(response, 501, "");
    }

    @Test
    public void shouldRetrieveRecordedExpectations() {
        // given
        httpStateHandler.log(new org.mockserver.log.model.RequestResponseLogEntry(request("request_one"), response("response_one")));
        MockHttpServletRequest expectationRetrieveExpectationsRequest = buildHttpServletRequest("PUT", "/mockserver/retrieve", httpRequestSerializer.serialize(request("request_one")));
        expectationRetrieveExpectationsRequest.setQueryString(("type=" + (RECORDED_EXPECTATIONS.name())));
        // when
        proxyServlet.service(expectationRetrieveExpectationsRequest, response);
        // then
        assertResponse(response, 200, expectationSerializer.serialize(Collections.singletonList(new org.mockserver.mock.Expectation(request("request_one"), Times.once(), null).thenRespond(response("response_one")))));
    }

    @Test
    public void shouldRetrieveLogMessages() {
        // given
        MockHttpServletRequest retrieveLogRequest = buildHttpServletRequest("PUT", "/mockserver/retrieve", httpRequestSerializer.serialize(request("request_one")));
        retrieveLogRequest.setQueryString(("type=" + (LOGS.name())));
        // when
        proxyServlet.service(retrieveLogRequest, response);
        // then
        MatcherAssert.assertThat(response.getStatus(), Is.is(200));
        String[] splitBody = new String(response.getContentAsByteArray(), Charsets.UTF_8).split((((NEW_LINE) + "------------------------------------") + (NEW_LINE)));
        MatcherAssert.assertThat(splitBody.length, Is.is(1));
        MatcherAssert.assertThat(splitBody[0], Is.is(CoreMatchers.endsWith(((((((((("retrieving logs that match:" + (NEW_LINE)) + (NEW_LINE)) + "\t{") + (NEW_LINE)) + "\t  \"path\" : \"request_one\"") + (NEW_LINE)) + "\t}") + (NEW_LINE)) + (NEW_LINE)))));
    }

    @Test
    public void shouldProxyRequestsOnDefaultPort() {
        // given
        HttpRequest request = request("request_one").withHeader("Host", "localhost").withMethod("GET");
        MockHttpServletRequest httpServletRequest = buildHttpServletRequest("GET", "request_one", "");
        httpServletRequest.addHeader("Host", "localhost");
        httpServletRequest.setLocalAddr("local_address");
        httpServletRequest.setLocalPort(80);
        // when
        proxyServlet.service(httpServletRequest, response);
        // then
        Mockito.verify(mockActionHandler).processAction(ArgumentMatchers.eq(request.withSecure(false).withKeepAlive(true)), ArgumentMatchers.any(ServletResponseWriter.class), ArgumentMatchers.isNull(ChannelHandlerContext.class), ArgumentMatchers.eq(ImmutableSet.of("local_address", "localhost", "127.0.0.1")), ArgumentMatchers.eq(true), ArgumentMatchers.eq(true));
    }

    @Test
    public void shouldProxyRequestsOnNonDefaultPort() {
        // given
        HttpRequest request = request("request_one").withHeader("Host", "localhost").withMethod("GET");
        MockHttpServletRequest httpServletRequest = buildHttpServletRequest("GET", "request_one", "");
        httpServletRequest.addHeader("Host", "localhost");
        httpServletRequest.setLocalAddr("local_address");
        httpServletRequest.setLocalPort(666);
        // when
        proxyServlet.service(httpServletRequest, response);
        // then
        Mockito.verify(mockActionHandler).processAction(ArgumentMatchers.eq(request.withSecure(false).withKeepAlive(true)), ArgumentMatchers.any(ServletResponseWriter.class), ArgumentMatchers.isNull(ChannelHandlerContext.class), ArgumentMatchers.eq(ImmutableSet.of("local_address:666", "localhost:666", "127.0.0.1:666")), ArgumentMatchers.eq(true), ArgumentMatchers.eq(true));
    }

    @Test
    public void shouldProxySecureRequestsOnDefaultPort() {
        // given
        HttpRequest request = request("request_one").withHeader("Host", "localhost").withMethod("GET");
        MockHttpServletRequest httpServletRequest = buildHttpServletRequest("GET", "request_one", "");
        httpServletRequest.addHeader("Host", "localhost");
        httpServletRequest.setSecure(true);
        httpServletRequest.setLocalAddr("local_address");
        httpServletRequest.setLocalPort(443);
        // when
        proxyServlet.service(httpServletRequest, response);
        // then
        Mockito.verify(mockActionHandler).processAction(ArgumentMatchers.eq(request.withSecure(true).withKeepAlive(true)), ArgumentMatchers.any(ServletResponseWriter.class), ArgumentMatchers.isNull(ChannelHandlerContext.class), ArgumentMatchers.eq(ImmutableSet.of("local_address", "localhost", "127.0.0.1")), ArgumentMatchers.eq(true), ArgumentMatchers.eq(true));
    }

    @Test
    public void shouldProxySecureRequestsOnNonDefaultPort() {
        // given
        HttpRequest request = request("request_one").withHeader("Host", "localhost").withMethod("GET");
        MockHttpServletRequest httpServletRequest = buildHttpServletRequest("GET", "request_one", "");
        httpServletRequest.addHeader("Host", "localhost");
        httpServletRequest.setSecure(true);
        httpServletRequest.setLocalAddr("local_address");
        httpServletRequest.setLocalPort(666);
        // when
        proxyServlet.service(httpServletRequest, response);
        // then
        Mockito.verify(mockActionHandler).processAction(ArgumentMatchers.eq(request.withSecure(true).withKeepAlive(true)), ArgumentMatchers.any(ServletResponseWriter.class), ArgumentMatchers.isNull(ChannelHandlerContext.class), ArgumentMatchers.eq(ImmutableSet.of("local_address:666", "localhost:666", "127.0.0.1:666")), ArgumentMatchers.eq(true), ArgumentMatchers.eq(true));
    }
}

