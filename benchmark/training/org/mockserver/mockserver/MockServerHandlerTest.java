package org.mockserver.mockserver;


import RetrieveType.ACTIVE_EXPECTATIONS;
import RetrieveType.LOGS;
import RetrieveType.RECORDED_EXPECTATIONS;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.MediaType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockserver.lifecycle.LifeCycle;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;
import org.mockserver.mock.HttpStateHandler;
import org.mockserver.mock.action.ActionHandler;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.responsewriter.NettyResponseWriter;
import org.mockserver.serialization.ExpectationSerializer;
import org.mockserver.serialization.HttpRequestSerializer;
import org.mockserver.serialization.PortBindingSerializer;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerHandlerTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private HttpStateHandler httpStateHandler;

    protected LifeCycle server;

    private ActionHandler mockActionHandler;

    private EmbeddedChannel embeddedChannel;

    @InjectMocks
    private MockServerHandler mockServerHandler;

    private HttpRequestSerializer httpRequestSerializer = new HttpRequestSerializer(new MockServerLogger());

    private ExpectationSerializer expectationSerializer = new ExpectationSerializer(new MockServerLogger());

    private PortBindingSerializer portBindingSerializer = new PortBindingSerializer(new MockServerLogger());

    @Test
    public void shouldRetrieveRequests() {
        // given
        httpStateHandler.log(new org.mockserver.log.model.RequestLogEntry(request("request_one")));
        HttpRequest expectationRetrieveRequestsRequest = request("/mockserver/retrieve").withMethod("PUT").withBody(httpRequestSerializer.serialize(request("request_one")));
        // when
        embeddedChannel.writeInbound(expectationRetrieveRequestsRequest);
        // then
        assertResponse(200, httpRequestSerializer.serialize(Collections.singletonList(request("request_one"))));
    }

    @Test
    public void shouldClear() {
        // given
        httpStateHandler.add(new Expectation(request("request_one")).thenRespond(response("response_one")));
        httpStateHandler.log(new org.mockserver.log.model.RequestLogEntry(request("request_one")));
        HttpRequest clearRequest = request("/mockserver/clear").withMethod("PUT").withBody(httpRequestSerializer.serialize(request("request_one")));
        // when
        embeddedChannel.writeInbound(clearRequest);
        // then
        assertResponse(200, "");
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(request("request_one")), Is.is(IsNull.nullValue()));
        MatcherAssert.assertThat(httpStateHandler.retrieve(request("/mockserver/retrieve").withMethod("PUT").withBody(httpRequestSerializer.serialize(request("request_one")))), Is.is(response().withBody("[]", MediaType.JSON_UTF_8).withStatusCode(200)));
    }

    @Test
    public void shouldReturnStatus() {
        // given
        Mockito.when(server.getLocalPorts()).thenReturn(Arrays.asList(1080, 1090));
        HttpRequest statusRequest = request("/mockserver/status").withMethod("PUT");
        // when
        embeddedChannel.writeInbound(statusRequest);
        // then
        assertResponse(200, portBindingSerializer.serialize(portBinding(1080, 1090)));
    }

    @Test
    public void shouldBindNewPorts() {
        // given
        Mockito.when(server.bindServerPorts(ArgumentMatchers.anyListOf(Integer.class))).thenReturn(Arrays.asList(1080, 1090));
        HttpRequest statusRequest = request("/mockserver/bind").withMethod("PUT").withBody(portBindingSerializer.serialize(portBinding(1080, 1090)));
        // when
        embeddedChannel.writeInbound(statusRequest);
        // then
        Mockito.verify(server).bindServerPorts(Arrays.asList(1080, 1090));
        assertResponse(200, portBindingSerializer.serialize(portBinding(1080, 1090)));
    }

    @Test
    public void shouldStop() throws InterruptedException {
        // given
        HttpRequest statusRequest = request("/mockserver/stop").withMethod("PUT");
        // when
        embeddedChannel.writeInbound(statusRequest);
        // then
        assertResponse(200, null);
        TimeUnit.SECONDS.sleep(1);// ensure stop thread has run

        Mockito.verify(server).stop();
    }

    @Test
    public void shouldRetrieveRecordedExpectations() {
        // given
        httpStateHandler.log(new org.mockserver.log.model.RequestResponseLogEntry(request("request_one"), response("response_one")));
        HttpRequest expectationRetrieveExpectationsRequest = request("/mockserver/retrieve").withMethod("PUT").withQueryStringParameter("type", RECORDED_EXPECTATIONS.name()).withBody(httpRequestSerializer.serialize(request("request_one")));
        // when
        embeddedChannel.writeInbound(expectationRetrieveExpectationsRequest);
        // then
        assertResponse(200, expectationSerializer.serialize(Collections.singletonList(new Expectation(request("request_one"), Times.once(), null).thenRespond(response("response_one")))));
    }

    @Test
    public void shouldRetrieveLogMessages() throws InterruptedException {
        // given
        Expectation expectationOne = new Expectation(request("request_one")).thenRespond(response("response_one"));
        httpStateHandler.add(expectationOne);
        HttpRequest retrieveLogRequest = request("/mockserver/retrieve").withMethod("PUT").withQueryStringParameter("type", LOGS.name()).withBody(httpRequestSerializer.serialize(request("request_one")));
        // when
        embeddedChannel.writeInbound(retrieveLogRequest);
        // then
        HttpResponse response = embeddedChannel.readOutbound();
        MatcherAssert.assertThat(response.getStatusCode(), Is.is(200));
        String[] splitBody = response.getBodyAsString().split((((NEW_LINE) + "------------------------------------") + (NEW_LINE)));
        MatcherAssert.assertThat(splitBody.length, Is.is(2));
        MatcherAssert.assertThat(splitBody[0], Is.is(CoreMatchers.endsWith(((((((((((((((((((((((((((((((((((("creating expectation:" + (NEW_LINE)) + "") + (NEW_LINE)) + "\t{") + (NEW_LINE)) + "\t  \"httpRequest\" : {") + (NEW_LINE)) + "\t    \"path\" : \"request_one\"") + (NEW_LINE)) + "\t  },") + (NEW_LINE)) + "\t  \"times\" : {") + (NEW_LINE)) + "\t    \"unlimited\" : true") + (NEW_LINE)) + "\t  },") + (NEW_LINE)) + "\t  \"timeToLive\" : {") + (NEW_LINE)) + "\t    \"unlimited\" : true") + (NEW_LINE)) + "\t  },") + (NEW_LINE)) + "\t  \"httpResponse\" : {") + (NEW_LINE)) + "\t    \"statusCode\" : 200,") + (NEW_LINE)) + "\t    \"reasonPhrase\" : \"OK\",") + (NEW_LINE)) + "\t    \"body\" : \"response_one\"") + (NEW_LINE)) + "\t  }") + (NEW_LINE)) + "\t}") + (NEW_LINE)))));
        MatcherAssert.assertThat(splitBody[1], Is.is(CoreMatchers.endsWith(((((((((("retrieving logs that match:" + (NEW_LINE)) + (NEW_LINE)) + "\t{") + (NEW_LINE)) + "\t  \"path\" : \"request_one\"") + (NEW_LINE)) + "\t}") + (NEW_LINE)) + (NEW_LINE)))));
    }

    @Test
    public void shouldAddExpectation() {
        // given
        Expectation expectationOne = new Expectation(request("request_one")).thenRespond(response("response_one"));
        HttpRequest request = request("/mockserver/expectation").withMethod("PUT").withBody(expectationSerializer.serialize(expectationOne));
        // when
        embeddedChannel.writeInbound(request);
        // then
        assertResponse(201, "");
        MatcherAssert.assertThat(httpStateHandler.firstMatchingExpectation(request("request_one")), Is.is(expectationOne));
    }

    @Test
    public void shouldRetrieveActiveExpectations() {
        // given
        Expectation expectationOne = new Expectation(request("request_one")).thenRespond(response("response_one"));
        httpStateHandler.add(expectationOne);
        HttpRequest expectationRetrieveExpectationsRequest = request("/mockserver/retrieve").withMethod("PUT").withQueryStringParameter("type", ACTIVE_EXPECTATIONS.name()).withBody(httpRequestSerializer.serialize(request("request_one")));
        // when
        embeddedChannel.writeInbound(expectationRetrieveExpectationsRequest);
        // then
        assertResponse(200, expectationSerializer.serialize(Collections.singletonList(expectationOne)));
    }

    @Test
    public void shouldProxyRequestsWhenProxying() {
        // given
        HttpRequest request = request("request_one");
        InetSocketAddress remoteAddress = new InetSocketAddress(1080);
        embeddedChannel.attr(MockServerHandler.LOCAL_HOST_HEADERS).set(ImmutableSet.of("local_address:666", "localhost:666", "127.0.0.1:666"));
        embeddedChannel.attr(MockServerHandler.PROXYING).set(true);
        embeddedChannel.attr(ActionHandler.REMOTE_SOCKET).set(remoteAddress);
        // when
        embeddedChannel.writeInbound(request);
        // then
        Mockito.verify(mockActionHandler).processAction(ArgumentMatchers.eq(request), ArgumentMatchers.any(NettyResponseWriter.class), ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(ImmutableSet.of("local_address:666", "localhost:666", "127.0.0.1:666")), ArgumentMatchers.eq(true), ArgumentMatchers.eq(false));
    }

    @Test
    public void shouldProxyRequestsWhenNotProxying() {
        // given
        HttpRequest request = request("request_one");
        InetSocketAddress remoteAddress = new InetSocketAddress(1080);
        embeddedChannel.attr(MockServerHandler.LOCAL_HOST_HEADERS).set(ImmutableSet.of("local_address:666", "localhost:666", "127.0.0.1:666"));
        embeddedChannel.attr(MockServerHandler.PROXYING).set(false);
        embeddedChannel.attr(ActionHandler.REMOTE_SOCKET).set(remoteAddress);
        // when
        embeddedChannel.writeInbound(request);
        // then
        Mockito.verify(mockActionHandler).processAction(ArgumentMatchers.eq(request), ArgumentMatchers.any(NettyResponseWriter.class), ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(ImmutableSet.of("local_address:666", "localhost:666", "127.0.0.1:666")), ArgumentMatchers.eq(false), ArgumentMatchers.eq(false));
    }
}

