package org.mockserver.mock.action;


import com.google.common.util.concurrent.SettableFuture;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import java.net.InetSocketAddress;
import java.util.HashSet;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockserver.client.NettyHttpClient;
import org.mockserver.configuration.ConfigurationProperties;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.matchers.TimeToLive;
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;
import org.mockserver.mock.HttpStateHandler;
import org.mockserver.model.Delay;
import org.mockserver.responsewriter.ResponseWriter;
import org.mockserver.scheduler.Scheduler;
import org.mockserver.serialization.curl.HttpRequestToCurlSerializer;

import static org.mockserver.model.HttpClassCallback.callback;
import static org.mockserver.model.HttpError.error;
import static org.mockserver.model.HttpForward.forward;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.HttpTemplate.template;


/**
 *
 *
 * @author jamesdbloom
 */
public class ActionHandlerTest {
    private static Scheduler scheduler;

    @Mock
    private HttpResponseActionHandler mockHttpResponseActionHandler;

    @Mock
    private HttpResponseTemplateActionHandler mockHttpResponseTemplateActionHandler;

    @Mock
    private HttpResponseClassCallbackActionHandler mockHttpResponseClassCallbackActionHandler;

    @Mock
    private HttpResponseObjectCallbackActionHandler mockHttpResponseObjectCallbackActionHandler;

    @Mock
    private HttpForwardActionHandler mockHttpForwardActionHandler;

    @Mock
    private HttpForwardTemplateActionHandler mockHttpForwardTemplateActionHandler;

    @Mock
    private HttpForwardClassCallbackActionHandler mockHttpForwardClassCallbackActionHandler;

    @Mock
    private HttpForwardObjectCallbackActionHandler mockHttpForwardObjectCallbackActionHandler;

    @Mock
    private HttpOverrideForwardedRequestActionHandler mockHttpOverrideForwardedRequestActionHandler;

    @Mock
    private HttpErrorActionHandler mockHttpErrorActionHandler;

    @Mock
    private ResponseWriter mockResponseWriter;

    @Mock
    private MockServerLogger mockLogFormatter;

    @Spy
    private HttpRequestToCurlSerializer httpRequestToCurlSerializer = new HttpRequestToCurlSerializer();

    @Mock
    private NettyHttpClient mockNettyHttpClient;

    private HttpStateHandler mockHttpStateHandler;

    private HttpRequest request;

    private HttpResponse response;

    private SettableFuture<HttpResponse> responseFuture;

    private HttpRequest forwardedHttpRequest;

    private HttpForwardActionResult httpForwardActionResult;

    private Expectation expectation;

    @InjectMocks
    private ActionHandler actionHandler;

    @Test
    public void shouldProcessResponseAction() {
        // given
        HttpResponse response = response("some_template").withDelay(Delay.milliseconds(1));
        expectation = new Expectation(request, Times.unlimited(), TimeToLive.unlimited()).thenRespond(response);
        Mockito.when(mockHttpStateHandler.firstMatchingExpectation(request)).thenReturn(expectation);
        // when
        actionHandler.processAction(request, mockResponseWriter, null, new HashSet<String>(), false, true);
        // then
        Mockito.verify(mockHttpResponseActionHandler).handle(response);
        Mockito.verify(mockResponseWriter).writeResponse(request, this.response, false);
        Mockito.verify(mockHttpStateHandler, VerificationModeFactory.times(1)).log(new org.mockserver.log.model.ExpectationMatchLogEntry(request, expectation));
        Mockito.verify(mockLogFormatter).info(EXPECTATION_RESPONSE, request, "returning response:{}for request:{}for action:{}", this.response, request, response);
        Mockito.verify(ActionHandlerTest.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(true), ArgumentMatchers.eq(Delay.milliseconds(1)), ArgumentMatchers.eq(Delay.milliseconds(0)));
    }

    @Test
    public void shouldProcessResponseTemplateAction() {
        // given
        HttpTemplate template = template(HttpTemplate.TemplateType.JAVASCRIPT, "some_template").withDelay(Delay.milliseconds(1));
        expectation = new Expectation(request, Times.unlimited(), TimeToLive.unlimited()).thenRespond(template);
        Mockito.when(mockHttpStateHandler.firstMatchingExpectation(request)).thenReturn(expectation);
        // when
        actionHandler.processAction(request, mockResponseWriter, null, new HashSet<String>(), false, true);
        // then
        Mockito.verify(mockHttpResponseTemplateActionHandler).handle(template, request);
        Mockito.verify(mockResponseWriter).writeResponse(request, response, false);
        Mockito.verify(mockHttpStateHandler, VerificationModeFactory.times(1)).log(new org.mockserver.log.model.ExpectationMatchLogEntry(request, expectation));
        Mockito.verify(mockLogFormatter).info(EXPECTATION_RESPONSE, request, "returning response:{}for request:{}for action:{}", response, request, template);
        Mockito.verify(ActionHandlerTest.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(true), ArgumentMatchers.eq(Delay.milliseconds(1)), ArgumentMatchers.eq(Delay.milliseconds(0)));
    }

    @Test
    public void shouldProcessResponseClassCallbackAction() {
        // given
        HttpClassCallback callback = callback("some_class").withDelay(Delay.milliseconds(1));
        expectation = new Expectation(request, Times.unlimited(), TimeToLive.unlimited()).thenRespond(callback);
        Mockito.when(mockHttpStateHandler.firstMatchingExpectation(request)).thenReturn(expectation);
        // when
        actionHandler.processAction(request, mockResponseWriter, null, new HashSet<String>(), false, true);
        // then
        Mockito.verify(mockHttpResponseClassCallbackActionHandler).handle(callback, request);
        Mockito.verify(mockResponseWriter).writeResponse(request, response, false);
        Mockito.verify(mockHttpStateHandler, VerificationModeFactory.times(1)).log(new org.mockserver.log.model.ExpectationMatchLogEntry(request, expectation));
        Mockito.verify(mockLogFormatter).info(EXPECTATION_RESPONSE, request, "returning response:{}for request:{}for action:{}", response, request, callback);
        Mockito.verify(ActionHandlerTest.scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(true), ArgumentMatchers.eq(Delay.milliseconds(1)), ArgumentMatchers.eq(Delay.milliseconds(0)));
    }

    @Test
    public void shouldProcessResponseObjectCallbackAction() {
        // given
        HttpObjectCallback callback = new HttpObjectCallback().withClientId("some_request_client_id").withDelay(Delay.milliseconds(1));
        expectation = new Expectation(request, Times.unlimited(), TimeToLive.unlimited()).thenRespond(callback);
        Mockito.when(mockHttpStateHandler.firstMatchingExpectation(request)).thenReturn(expectation);
        ResponseWriter mockResponseWriter = Mockito.mock(ResponseWriter.class);
        // when
        actionHandler.processAction(request, mockResponseWriter, null, new HashSet<String>(), false, true);
        // then
        Mockito.verify(mockHttpStateHandler, VerificationModeFactory.times(1)).log(new org.mockserver.log.model.ExpectationMatchLogEntry(request, expectation));
        Mockito.verify(mockHttpResponseObjectCallbackActionHandler).handle(ArgumentMatchers.any(ActionHandler.class), ArgumentMatchers.same(callback), ArgumentMatchers.same(request), ArgumentMatchers.same(mockResponseWriter), ArgumentMatchers.eq(true));
    }

    @Test
    public void shouldProcessForwardAction() {
        // given
        HttpForward forward = forward().withHost("localhost").withPort(1080);
        expectation = new Expectation(request, Times.unlimited(), TimeToLive.unlimited()).thenForward(forward);
        Mockito.when(mockHttpStateHandler.firstMatchingExpectation(request)).thenReturn(expectation);
        // when
        actionHandler.processAction(request, mockResponseWriter, null, new HashSet<String>(), false, true);
        // then
        Mockito.verify(mockHttpForwardActionHandler).handle(forward, request);
        Mockito.verify(mockResponseWriter).writeResponse(request, response, false);
        Mockito.verify(mockHttpStateHandler, VerificationModeFactory.times(1)).log(new org.mockserver.log.model.ExpectationMatchLogEntry(request, expectation));
        Mockito.verify(mockLogFormatter).info(ArgumentMatchers.same(FORWARDED_REQUEST), ArgumentMatchers.same(request), ArgumentMatchers.eq("returning response:{}for forwarded request\n\n in json:{}\n\n in curl:{}for action:{}"), ArgumentMatchers.same(response), ArgumentMatchers.same(forwardedHttpRequest), ArgumentMatchers.any(String.class), ArgumentMatchers.eq(expectation.getAction()));
        Mockito.verify(httpRequestToCurlSerializer).toCurl(forwardedHttpRequest);
    }

    @Test
    public void shouldProcessForwardTemplateAction() {
        // given
        HttpTemplate template = org.mockserver.model.HttpTemplate.template(HttpTemplate.TemplateType.JAVASCRIPT, "some_template");
        expectation = new Expectation(request, Times.unlimited(), TimeToLive.unlimited()).thenForward(template);
        Mockito.when(mockHttpStateHandler.firstMatchingExpectation(request)).thenReturn(expectation);
        // when
        actionHandler.processAction(request, mockResponseWriter, null, new HashSet<String>(), false, true);
        // then
        Mockito.verify(mockHttpForwardTemplateActionHandler).handle(template, request);
        Mockito.verify(mockResponseWriter).writeResponse(request, response, false);
        Mockito.verify(mockHttpStateHandler, VerificationModeFactory.times(1)).log(new org.mockserver.log.model.ExpectationMatchLogEntry(request, expectation));
        Mockito.verify(mockLogFormatter).info(ArgumentMatchers.same(FORWARDED_REQUEST), ArgumentMatchers.same(request), ArgumentMatchers.eq("returning response:{}for forwarded request\n\n in json:{}\n\n in curl:{}for action:{}"), ArgumentMatchers.same(response), ArgumentMatchers.same(forwardedHttpRequest), ArgumentMatchers.any(String.class), ArgumentMatchers.eq(expectation.getAction()));
        Mockito.verify(httpRequestToCurlSerializer).toCurl(forwardedHttpRequest);
    }

    @Test
    public void shouldProcessForwardClassCallbackAction() {
        // given
        HttpClassCallback callback = org.mockserver.model.HttpClassCallback.callback("some_class");
        expectation = new Expectation(request, Times.unlimited(), TimeToLive.unlimited()).thenForward(callback);
        Mockito.when(mockHttpStateHandler.firstMatchingExpectation(request)).thenReturn(expectation);
        // when
        actionHandler.processAction(request, mockResponseWriter, null, new HashSet<String>(), false, true);
        // then
        Mockito.verify(mockHttpForwardClassCallbackActionHandler).handle(callback, request);
        Mockito.verify(mockResponseWriter).writeResponse(request, response, false);
        Mockito.verify(mockHttpStateHandler, VerificationModeFactory.times(1)).log(new org.mockserver.log.model.ExpectationMatchLogEntry(request, expectation));
        Mockito.verify(mockLogFormatter).info(ArgumentMatchers.same(FORWARDED_REQUEST), ArgumentMatchers.same(request), ArgumentMatchers.eq("returning response:{}for forwarded request\n\n in json:{}\n\n in curl:{}for action:{}"), ArgumentMatchers.same(response), ArgumentMatchers.same(forwardedHttpRequest), ArgumentMatchers.any(String.class), ArgumentMatchers.eq(expectation.getAction()));
        Mockito.verify(httpRequestToCurlSerializer).toCurl(forwardedHttpRequest);
    }

    @Test
    public void shouldProcessForwardObjectCallbackAction() {
        // given
        HttpObjectCallback callback = new HttpObjectCallback().withClientId("some_forward_client_id");
        expectation = new Expectation(request, Times.unlimited(), TimeToLive.unlimited()).thenForward(callback);
        Mockito.when(mockHttpStateHandler.firstMatchingExpectation(request)).thenReturn(expectation);
        ResponseWriter mockResponseWriter = Mockito.mock(ResponseWriter.class);
        // when
        actionHandler.processAction(request, mockResponseWriter, null, new HashSet<String>(), false, true);
        // then
        Mockito.verify(mockHttpStateHandler, VerificationModeFactory.times(1)).log(new org.mockserver.log.model.ExpectationMatchLogEntry(request, expectation));
        Mockito.verify(mockHttpForwardObjectCallbackActionHandler).handle(ArgumentMatchers.any(ActionHandler.class), ArgumentMatchers.same(callback), ArgumentMatchers.same(request), ArgumentMatchers.same(mockResponseWriter), ArgumentMatchers.eq(true));
    }

    @Test
    public void shouldProcessOverrideForwardedRequest() {
        // given
        HttpOverrideForwardedRequest httpOverrideForwardedRequest = new HttpOverrideForwardedRequest().withHttpRequest(request("some_overridden_path"));
        expectation = new Expectation(request, Times.unlimited(), TimeToLive.unlimited()).thenForward(httpOverrideForwardedRequest);
        Mockito.when(mockHttpStateHandler.firstMatchingExpectation(request)).thenReturn(expectation);
        ResponseWriter mockResponseWriter = Mockito.mock(ResponseWriter.class);
        // when
        actionHandler.processAction(request, mockResponseWriter, null, new HashSet<String>(), false, true);
        // then
        Mockito.verify(mockHttpStateHandler, VerificationModeFactory.times(1)).log(new org.mockserver.log.model.ExpectationMatchLogEntry(request, expectation));
        Mockito.verify(mockHttpOverrideForwardedRequestActionHandler).handle(httpOverrideForwardedRequest, request);
    }

    @Test
    public void shouldProcessErrorAction() {
        // given
        HttpError error = error();
        expectation = new Expectation(request, Times.unlimited(), TimeToLive.unlimited()).thenError(error);
        Mockito.when(mockHttpStateHandler.firstMatchingExpectation(request)).thenReturn(expectation);
        ResponseWriter mockResponseWriter = Mockito.mock(ResponseWriter.class);
        ChannelHandlerContext mockChannelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        // when
        actionHandler.processAction(request, mockResponseWriter, mockChannelHandlerContext, new HashSet<String>(), false, true);
        // then
        Mockito.verify(mockHttpStateHandler, VerificationModeFactory.times(1)).log(new org.mockserver.log.model.ExpectationMatchLogEntry(request, expectation));
        Mockito.verify(mockHttpErrorActionHandler).handle(error, mockChannelHandlerContext);
        Mockito.verify(mockLogFormatter).info(EXPECTATION_RESPONSE, request, "returning error:{}for request:{}for action:{}", error, request, error);
    }

    @Test
    public void shouldProxyRequestsWithRemoteSocketAttribute() {
        // given
        HttpRequest request = org.mockserver.model.HttpRequest.request("request_one");
        // and - remote socket attribute
        ChannelHandlerContext mockChannelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        Channel mockChannel = Mockito.mock(Channel.class);
        Mockito.when(mockChannelHandlerContext.channel()).thenReturn(mockChannel);
        InetSocketAddress remoteAddress = new InetSocketAddress(1080);
        Attribute<InetSocketAddress> inetSocketAddressAttribute = Mockito.mock(Attribute.class);
        Mockito.when(inetSocketAddressAttribute.get()).thenReturn(remoteAddress);
        Mockito.when(mockChannel.attr(ActionHandler.REMOTE_SOCKET)).thenReturn(inetSocketAddressAttribute);
        // and - netty http client
        Mockito.when(mockNettyHttpClient.sendRequest(request("request_one"), remoteAddress, ConfigurationProperties.socketConnectionTimeout())).thenReturn(responseFuture);
        // when
        actionHandler.processAction(request("request_one"), mockResponseWriter, mockChannelHandlerContext, new HashSet<String>(), true, true);
        // then
        Mockito.verify(mockHttpStateHandler).log(new org.mockserver.log.model.RequestResponseLogEntry(request, response));
        Mockito.verify(mockNettyHttpClient).sendRequest(request("request_one"), remoteAddress, ConfigurationProperties.socketConnectionTimeout());
        Mockito.verify(mockLogFormatter).info(FORWARDED_REQUEST, request, (((((("returning response:{}for forwarded request" + (NEW_LINE)) + (NEW_LINE)) + " in json:{}") + (NEW_LINE)) + (NEW_LINE)) + " in curl:{}"), response, request, httpRequestToCurlSerializer.toCurl(request, remoteAddress));
    }
}

