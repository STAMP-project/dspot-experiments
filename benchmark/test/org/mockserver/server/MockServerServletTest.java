package org.mockserver.server;


import RetrieveType.ACTIVE_EXPECTATIONS;
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
import org.mockserver.mock.Expectation;
import org.mockserver.mock.HttpStateHandler;
import org.mockserver.mock.action.ActionHandler;
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
public class MockServerServletTest {
    private HttpRequestSerializer httpRequestSerializer = new HttpRequestSerializer(new MockServerLogger());

    private ExpectationSerializer expectationSerializer = new ExpectationSerializer(new MockServerLogger());

    private PortBindingSerializer portBindingSerializer = new PortBindingSerializer(new MockServerLogger());

    private HttpStateHandler httpStateHandler;

    private ActionHandler mockActionHandler;

    private Scheduler scheduler;

    @InjectMocks
    private MockServerServlet mockServerServlet;

    private MockHttpServletResponse response;

    @Test
    public void shouldRetrieveRequests() {
        // given
        MockHttpServletRequest expectationRetrieveRequestsRequest = buildHttpServletRequest("PUT", "/mockserver/retrieve", httpRequestSerializer.serialize(request("request_one")));
        httpStateHandler.log(new org.mockserver.log.model.RequestLogEntry(request("request_one")));
        // when
        mockServerServlet.service(expectationRetrieveRequestsRequest, response);
        // then
        assertResponse(response, 200, httpRequestSerializer.serialize(Collections.singletonList(request("request_one"))));
    }

    @Test
    public void shouldClear() {
        // given
        httpStateHandler.add(new Expectation(request("request_one")).thenRespond(response("response_one")));
        httpStateHandler.log(new org.mockserver.log.model.RequestLogEntry(request("request_one")));
        MockHttpServletRequest clearRequest = buildHttpServletRequest("PUT", "/mockserver/clear", httpRequestSerializer.serialize(request("request_one")));
        // when
        mockServerServlet.service(clearRequest, response);
        // then
        assertResponse(response, 200, "");
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(request("request_one")), Is.is(IsNull.nullValue()));
        MatcherAssert.assertThat(httpStateHandler.retrieve(request("/retrieve").withMethod("PUT").withBody(httpRequestSerializer.serialize(request("request_one")))), Is.is(response().withBody("[]", MediaType.JSON_UTF_8).withStatusCode(200)));
    }

    @Test
    public void shouldReturnStatus() {
        // given
        MockHttpServletRequest statusRequest = buildHttpServletRequest("PUT", "/mockserver/status", "");
        // when
        mockServerServlet.service(statusRequest, response);
        // then
        assertResponse(response, 200, portBindingSerializer.serialize(portBinding(80)));
    }

    @Test
    public void shouldBindNewPorts() {
        // given
        MockHttpServletRequest statusRequest = buildHttpServletRequest("PUT", "/mockserver/bind", portBindingSerializer.serialize(portBinding(1080, 1090)));
        // when
        mockServerServlet.service(statusRequest, response);
        // then
        assertResponse(response, 501, "");
    }

    @Test
    public void shouldStop() throws InterruptedException {
        // given
        MockHttpServletRequest statusRequest = buildHttpServletRequest("PUT", "/mockserver/stop", "");
        // when
        mockServerServlet.service(statusRequest, response);
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
        mockServerServlet.service(expectationRetrieveExpectationsRequest, response);
        // then
        assertResponse(response, 200, expectationSerializer.serialize(Collections.singletonList(new Expectation(request("request_one"), Times.once(), null).thenRespond(response("response_one")))));
    }

    @Test
    public void shouldAddExpectation() {
        // given
        Expectation expectationOne = new Expectation(request("request_one")).thenRespond(response("response_one"));
        MockHttpServletRequest request = buildHttpServletRequest("PUT", "/mockserver/expectation", expectationSerializer.serialize(expectationOne));
        // when
        mockServerServlet.service(request, response);
        // then
        assertResponse(response, 201, "");
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(request("request_one")), Is.is(expectationOne));
    }

    @Test
    public void shouldRetrieveActiveExpectations() {
        // given
        Expectation expectationOne = new Expectation(request("request_one")).thenRespond(response("response_one"));
        httpStateHandler.add(expectationOne);
        MockHttpServletRequest expectationRetrieveExpectationsRequest = buildHttpServletRequest("PUT", "/mockserver/retrieve", httpRequestSerializer.serialize(request("request_one")));
        expectationRetrieveExpectationsRequest.setQueryString(("type=" + (ACTIVE_EXPECTATIONS.name())));
        // when
        mockServerServlet.service(expectationRetrieveExpectationsRequest, response);
        // then
        assertResponse(response, 200, expectationSerializer.serialize(Collections.singletonList(expectationOne)));
    }

    @Test
    public void shouldRetrieveLogMessages() {
        // given
        Expectation expectationOne = new Expectation(request("request_one")).thenRespond(response("response_one"));
        httpStateHandler.add(expectationOne);
        MockHttpServletRequest retrieveLogRequest = buildHttpServletRequest("PUT", "/mockserver/retrieve", httpRequestSerializer.serialize(request("request_one")));
        retrieveLogRequest.setQueryString(("type=" + (LOGS.name())));
        // when
        mockServerServlet.service(retrieveLogRequest, response);
        // then
        MatcherAssert.assertThat(response.getStatus(), Is.is(200));
        String[] splitBody = new String(response.getContentAsByteArray(), Charsets.UTF_8).split((((NEW_LINE) + "------------------------------------") + (NEW_LINE)));
        MatcherAssert.assertThat(splitBody.length, Is.is(2));
        MatcherAssert.assertThat(splitBody[0], Is.is(CoreMatchers.endsWith(((((((((((((((((((((((((((((((((((("creating expectation:" + (NEW_LINE)) + "") + (NEW_LINE)) + "\t{") + (NEW_LINE)) + "\t  \"httpRequest\" : {") + (NEW_LINE)) + "\t    \"path\" : \"request_one\"") + (NEW_LINE)) + "\t  },") + (NEW_LINE)) + "\t  \"times\" : {") + (NEW_LINE)) + "\t    \"unlimited\" : true") + (NEW_LINE)) + "\t  },") + (NEW_LINE)) + "\t  \"timeToLive\" : {") + (NEW_LINE)) + "\t    \"unlimited\" : true") + (NEW_LINE)) + "\t  },") + (NEW_LINE)) + "\t  \"httpResponse\" : {") + (NEW_LINE)) + "\t    \"statusCode\" : 200,") + (NEW_LINE)) + "\t    \"reasonPhrase\" : \"OK\",") + (NEW_LINE)) + "\t    \"body\" : \"response_one\"") + (NEW_LINE)) + "\t  }") + (NEW_LINE)) + "\t}") + (NEW_LINE)))));
        MatcherAssert.assertThat(splitBody[1], Is.is(CoreMatchers.endsWith(((((((((("retrieving logs that match:" + (NEW_LINE)) + (NEW_LINE)) + "\t{") + (NEW_LINE)) + "\t  \"path\" : \"request_one\"") + (NEW_LINE)) + "\t}") + (NEW_LINE)) + (NEW_LINE)))));
    }

    @Test
    public void shouldUseActionHandlerToHandleNonAPIRequestsOnDefaultPort() {
        // given
        MockHttpServletRequest request = buildHttpServletRequest("GET", "request_one", "");
        request.setLocalAddr("local_address");
        request.setLocalPort(80);
        // when
        mockServerServlet.service(request, response);
        // then
        Mockito.verify(mockActionHandler).processAction(ArgumentMatchers.eq(request("request_one").withMethod("GET").withKeepAlive(true).withSecure(false)), ArgumentMatchers.any(ServletResponseWriter.class), ArgumentMatchers.isNull(ChannelHandlerContext.class), ArgumentMatchers.eq(ImmutableSet.of("local_address", "localhost", "127.0.0.1")), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true));
    }

    @Test
    public void shouldUseActionHandlerToHandleNonAPIRequestsOnNonDefaultPort() {
        // given
        MockHttpServletRequest request = buildHttpServletRequest("GET", "request_one", "");
        request.setLocalAddr("local_address");
        request.setLocalPort(666);
        // when
        mockServerServlet.service(request, response);
        // then
        Mockito.verify(mockActionHandler).processAction(ArgumentMatchers.eq(request("request_one").withMethod("GET").withKeepAlive(true).withSecure(false)), ArgumentMatchers.any(ServletResponseWriter.class), ArgumentMatchers.isNull(ChannelHandlerContext.class), ArgumentMatchers.eq(ImmutableSet.of("local_address:666", "localhost:666", "127.0.0.1:666")), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true));
    }

    @Test
    public void shouldUseActionHandlerToHandleNonAPISecureRequestsOnDefaultPort() {
        // given
        MockHttpServletRequest request = buildHttpServletRequest("GET", "request_one", "");
        request.setSecure(true);
        request.setLocalAddr("local_address");
        request.setLocalPort(443);
        // when
        mockServerServlet.service(request, response);
        // then
        Mockito.verify(mockActionHandler).processAction(ArgumentMatchers.eq(request("request_one").withMethod("GET").withKeepAlive(true).withSecure(true)), ArgumentMatchers.any(ServletResponseWriter.class), ArgumentMatchers.isNull(ChannelHandlerContext.class), ArgumentMatchers.eq(ImmutableSet.of("local_address", "localhost", "127.0.0.1")), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true));
    }

    @Test
    public void shouldUseActionHandlerToHandleNonAPISecureRequestsOnNonDefaultPort() {
        // given
        MockHttpServletRequest request = buildHttpServletRequest("GET", "request_one", "");
        request.setSecure(true);
        request.setLocalAddr("local_address");
        request.setLocalPort(666);
        // when
        mockServerServlet.service(request, response);
        // then
        Mockito.verify(mockActionHandler).processAction(ArgumentMatchers.eq(request("request_one").withMethod("GET").withKeepAlive(true).withSecure(true)), ArgumentMatchers.any(ServletResponseWriter.class), ArgumentMatchers.isNull(ChannelHandlerContext.class), ArgumentMatchers.eq(ImmutableSet.of("local_address:666", "localhost:666", "127.0.0.1:666")), ArgumentMatchers.eq(false), ArgumentMatchers.eq(true));
    }
}

