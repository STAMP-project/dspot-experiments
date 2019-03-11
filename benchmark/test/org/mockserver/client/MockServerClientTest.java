package org.mockserver.client;


import ClearType.LOG;
import Format.JSON;
import HttpForward.Scheme.HTTPS;
import HttpStatusCode.OK_200;
import RetrieveType.ACTIVE_EXPECTATIONS;
import RetrieveType.RECORDED_EXPECTATIONS;
import RetrieveType.REQUESTS;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockserver.Version;
import org.mockserver.matchers.Times;
import org.mockserver.mock.Expectation;
import org.mockserver.serialization.ExpectationSerializer;
import org.mockserver.serialization.HttpRequestSerializer;
import org.mockserver.serialization.VerificationSequenceSerializer;
import org.mockserver.serialization.VerificationSerializer;
import org.mockserver.verify.Verification;
import org.mockserver.verify.VerificationSequence;
import org.mockserver.verify.VerificationTimes;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerClientTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private NettyHttpClient mockHttpClient;

    @Mock
    private ExpectationSerializer mockExpectationSerializer;

    @Mock
    private HttpRequestSerializer mockHttpRequestSerializer;

    @Mock
    private VerificationSerializer mockVerificationSerializer;

    @Mock
    private VerificationSequenceSerializer mockVerificationSequenceSerializer;

    @InjectMocks
    private MockServerClient mockServerClient;

    @Test
    public void shouldHandleNullHostnameExceptions() {
        // then
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Matchers.containsString("Host can not be null or empty"));
        // when
        new MockServerClient(null, 1080);
    }

    @Test
    public void shouldHandleNullContextPathExceptions() {
        // then
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Matchers.containsString("ContextPath can not be null"));
        // when
        new MockServerClient("localhost", 1080, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldHandleInvalidExpectationException() {
        // then
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Matchers.containsString("error_body"));
        // given
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withStatusCode(HttpResponseStatus.BAD_REQUEST.code()).withBody("error_body"));
        // when
        ForwardChainExpectation forwardChainExpectation = mockServerClient.when(request());
        forwardChainExpectation.respond(response());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldHandleUnmatchingServerVersion() {
        try {
            System.setProperty("MOCKSERVER_VERSION", "some_version");
            // then
            exception.expect(ClientException.class);
            exception.expectMessage(Matchers.containsString((("Client version \"" + (Version.getVersion())) + "\" does not match server version \"another_non_matching_version\"")));
            // given
            Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withHeader("version", "another_non_matching_version").withStatusCode(HttpResponseStatus.CREATED.code()));
            // when
            ForwardChainExpectation forwardChainExpectation = mockServerClient.when(request());
            forwardChainExpectation.respond(response());
        } finally {
            System.clearProperty("MOCKSERVER_VERSION");
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldHandleMatchingServerVersion() {
        try {
            // given
            System.setProperty("MOCKSERVER_VERSION", "same_version");
            Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withHeader("version", Version.getVersion()).withStatusCode(HttpResponseStatus.CREATED.code()));
            // when
            ForwardChainExpectation forwardChainExpectation = mockServerClient.when(request());
            forwardChainExpectation.respond(response());
        } catch (Throwable t) {
            // then - no exception should be thrown
            Assert.fail();
        } finally {
            System.clearProperty("MOCKSERVER_VERSION");
        }
    }

    @Test
    public void shouldSetupExpectationWithResponse() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        HttpResponse httpResponse = new HttpResponse().withBody("some_response_body").withHeaders(new Header("responseName", "responseValue"));
        // when
        ForwardChainExpectation forwardChainExpectation = mockServerClient.when(httpRequest);
        forwardChainExpectation.respond(httpResponse);
        // then
        Expectation expectation = forwardChainExpectation.getExpectation();
        Assert.assertTrue(expectation.isActive());
        Assert.assertSame(httpResponse, expectation.getHttpResponse());
        Assert.assertEquals(Times.unlimited(), expectation.getTimes());
    }

    @Test
    public void shouldSetupExpectationWithResponseTemplate() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        HttpTemplate template = withTemplate("some_template");
        // when
        ForwardChainExpectation forwardChainExpectation = mockServerClient.when(httpRequest);
        forwardChainExpectation.respond(template);
        // then
        Expectation expectation = forwardChainExpectation.getExpectation();
        Assert.assertTrue(expectation.isActive());
        Assert.assertSame(template, expectation.getHttpResponseTemplate());
        Assert.assertEquals(Times.unlimited(), expectation.getTimes());
    }

    @Test
    public void shouldSetupExpectationWithResponseClassCallback() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        HttpClassCallback httpClassCallback = new HttpClassCallback().withCallbackClass("some_class");
        // when
        ForwardChainExpectation forwardChainExpectation = mockServerClient.when(httpRequest);
        forwardChainExpectation.respond(httpClassCallback);
        // then
        Expectation expectation = forwardChainExpectation.getExpectation();
        Assert.assertTrue(expectation.isActive());
        Assert.assertSame(httpClassCallback, expectation.getHttpResponseClassCallback());
        Assert.assertEquals(Times.unlimited(), expectation.getTimes());
    }

    @Test
    public void shouldSetupExpectationWithForward() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        HttpForward httpForward = new HttpForward().withHost("some_host").withPort(9090).withScheme(HTTPS);
        // when
        ForwardChainExpectation forwardChainExpectation = mockServerClient.when(httpRequest);
        forwardChainExpectation.forward(httpForward);
        // then
        Expectation expectation = forwardChainExpectation.getExpectation();
        Assert.assertTrue(expectation.isActive());
        Assert.assertSame(httpForward, expectation.getHttpForward());
        Assert.assertEquals(Times.unlimited(), expectation.getTimes());
    }

    @Test
    public void shouldSetupExpectationWithForwardTemplate() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        HttpTemplate template = withTemplate("some_template");
        // when
        ForwardChainExpectation forwardChainExpectation = mockServerClient.when(httpRequest);
        forwardChainExpectation.forward(template);
        // then
        Expectation expectation = forwardChainExpectation.getExpectation();
        Assert.assertTrue(expectation.isActive());
        Assert.assertSame(template, expectation.getHttpForwardTemplate());
        Assert.assertEquals(Times.unlimited(), expectation.getTimes());
    }

    @Test
    public void shouldSetupExpectationWithForwardClassCallback() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        HttpClassCallback httpClassCallback = new HttpClassCallback().withCallbackClass("some_class");
        // when
        ForwardChainExpectation forwardChainExpectation = mockServerClient.when(httpRequest);
        forwardChainExpectation.forward(httpClassCallback);
        // then
        Expectation expectation = forwardChainExpectation.getExpectation();
        Assert.assertTrue(expectation.isActive());
        Assert.assertSame(httpClassCallback, expectation.getHttpForwardClassCallback());
        Assert.assertEquals(Times.unlimited(), expectation.getTimes());
    }

    @Test
    public void shouldSetupExpectationWithOverrideForwardedRequest() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        // when
        ForwardChainExpectation forwardChainExpectation = mockServerClient.when(httpRequest);
        forwardChainExpectation.forward(forwardOverriddenRequest(request().withBody("some_overridden_body")));
        // then
        Expectation expectation = forwardChainExpectation.getExpectation();
        Assert.assertTrue(expectation.isActive());
        Assert.assertThat(expectation.getHttpForwardTemplate(), CoreMatchers.nullValue());
        Assert.assertThat(expectation.getHttpOverrideForwardedRequest(), CoreMatchers.is(new HttpOverrideForwardedRequest().withHttpRequest(request().withBody("some_overridden_body"))));
        Assert.assertEquals(Times.unlimited(), expectation.getTimes());
    }

    @Test
    public void shouldSetupExpectationWithError() {
        // given
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        HttpError httpError = new HttpError().withDropConnection(true).withResponseBytes("silly_bytes".getBytes(StandardCharsets.UTF_8));
        // when
        ForwardChainExpectation forwardChainExpectation = mockServerClient.when(httpRequest);
        forwardChainExpectation.error(httpError);
        // then
        Expectation expectation = forwardChainExpectation.getExpectation();
        Assert.assertTrue(expectation.isActive());
        Assert.assertSame(httpError, expectation.getHttpError());
        Assert.assertEquals(Times.unlimited(), expectation.getTimes());
    }

    @Test
    public void shouldSendExpectationWithRequest() {
        // when
        mockServerClient.when(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")), Times.exactly(3)).respond(new HttpResponse().withBody("some_response_body").withHeaders(new Header("responseName", "responseValue")));
        // then
        Mockito.verify(mockExpectationSerializer).serialize(new ExpectationDTO().setHttpRequest(new HttpRequestDTO(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")))).setHttpResponse(new HttpResponseDTO(new HttpResponse().withBody("some_response_body").withHeaders(new Header("responseName", "responseValue")))).setTimes(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))).buildObject());
    }

    @Test
    public void shouldSendExpectationWithRequestTemplate() {
        // when
        mockServerClient.when(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")), Times.exactly(3)).respond(new HttpResponse().withBody("some_response_body").withHeaders(new Header("responseName", "responseValue")));
        // then
        Mockito.verify(mockExpectationSerializer).serialize(new ExpectationDTO().setHttpRequest(new HttpRequestDTO(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")))).setHttpResponse(new HttpResponseDTO(new HttpResponse().withBody("some_response_body").withHeaders(new Header("responseName", "responseValue")))).setTimes(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))).buildObject());
    }

    @Test
    public void shouldSendExpectationWithRequestClassCallback() {
        // when
        mockServerClient.when(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")), Times.exactly(3)).respond(new HttpClassCallback().withCallbackClass("some_class"));
        // then
        Mockito.verify(mockExpectationSerializer).serialize(new ExpectationDTO().setHttpRequest(new HttpRequestDTO(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")))).setHttpResponseClassCallback(new HttpClassCallbackDTO(new HttpClassCallback().withCallbackClass("some_class"))).setTimes(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))).buildObject());
    }

    @Test
    public void shouldSendExpectationWithForward() {
        // when
        mockServerClient.when(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")), Times.exactly(3)).forward(new HttpForward().withHost("some_host").withPort(9090).withScheme(HTTPS));
        // then
        Mockito.verify(mockExpectationSerializer).serialize(new ExpectationDTO().setHttpRequest(new HttpRequestDTO(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")))).setHttpForward(new HttpForwardDTO(new HttpForward().withHost("some_host").withPort(9090).withScheme(HTTPS))).setTimes(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))).buildObject());
    }

    @Test
    public void shouldSendExpectationWithForwardTemplate() {
        // when
        mockServerClient.when(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")), Times.exactly(3)).forward(new HttpForward().withHost("some_host").withPort(9090).withScheme(HTTPS));
        // then
        Mockito.verify(mockExpectationSerializer).serialize(new ExpectationDTO().setHttpRequest(new HttpRequestDTO(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")))).setHttpForward(new HttpForwardDTO(new HttpForward().withHost("some_host").withPort(9090).withScheme(HTTPS))).setTimes(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))).buildObject());
    }

    @Test
    public void shouldSendExpectationWithForwardClassCallback() {
        // when
        mockServerClient.when(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")), Times.exactly(3)).forward(new HttpClassCallback().withCallbackClass("some_class"));
        // then
        Mockito.verify(mockExpectationSerializer).serialize(new ExpectationDTO().setHttpRequest(new HttpRequestDTO(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")))).setHttpForwardClassCallback(new HttpClassCallbackDTO(new HttpClassCallback().withCallbackClass("some_class"))).setTimes(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))).buildObject());
    }

    @Test
    public void shouldSendExpectationWithOverrideForwardedRequest() {
        // when
        ForwardChainExpectation forwardChainExpectation = mockServerClient.when(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")), Times.exactly(3));
        forwardChainExpectation.forward(forwardOverriddenRequest(request().withBody("some_replaced_body")));
        // then
        Mockito.verify(mockExpectationSerializer).serialize(new ExpectationDTO().setHttpRequest(new HttpRequestDTO(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")))).setHttpOverrideForwardedRequest(new HttpOverrideForwardedRequestDTO(new HttpOverrideForwardedRequest().withHttpRequest(request().withBody("some_replaced_body")))).setTimes(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))).buildObject());
    }

    @Test
    public void shouldSendExpectationWithError() {
        // when
        mockServerClient.when(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")), Times.exactly(3)).error(new HttpError().withDelay(TimeUnit.MILLISECONDS, 100).withResponseBytes("random_bytes".getBytes(StandardCharsets.UTF_8)));
        // then
        Mockito.verify(mockExpectationSerializer).serialize(new ExpectationDTO().setHttpRequest(new HttpRequestDTO(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")))).setHttpError(new HttpErrorDTO(new HttpError().withDelay(TimeUnit.MILLISECONDS, 100).withResponseBytes("random_bytes".getBytes(StandardCharsets.UTF_8)))).setTimes(new org.mockserver.serialization.model.TimesDTO(Times.exactly(3))).buildObject());
    }

    @Test
    public void shouldSendExpectationRequestWithDefaultTimes() {
        // when
        mockServerClient.when(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"))).respond(new HttpResponse().withBody("some_response_body").withHeaders(new Header("responseName", "responseValue")));
        // then
        Mockito.verify(mockExpectationSerializer).serialize(new ExpectationDTO().setHttpRequest(new HttpRequestDTO(new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body")))).setHttpResponse(new HttpResponseDTO(new HttpResponse().withBody("some_response_body").withHeaders(new Header("responseName", "responseValue")))).setTimes(new org.mockserver.serialization.model.TimesDTO(Times.unlimited())).buildObject());
    }

    @Test
    public void shouldSendStopRequest() {
        // when
        mockServerClient.stop();
        // then
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/stop"), 20000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldBeCloseable() {
        // when
        mockServerClient.close();
        // then
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/stop"), 20000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldQueryRunningStatus() {
        // given
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withStatusCode(OK_200.code()));
        // when
        boolean running = mockServerClient.isRunning();
        // then
        Assert.assertTrue(running);
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/status"), 20000, TimeUnit.MILLISECONDS);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldQueryRunningStatusWhenSocketConnectionException() {
        // given
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenThrow(SocketConnectionException.class);
        // when
        boolean running = mockServerClient.isRunning();
        // then
        Assert.assertFalse(running);
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/status"), 20000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldSendResetRequest() {
        // when
        mockServerClient.reset();
        // then
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/reset"), 20000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldSendClearRequest() {
        // given
        HttpRequest someRequestMatcher = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        Mockito.when(mockHttpRequestSerializer.serialize(someRequestMatcher)).thenReturn(someRequestMatcher.toString());
        // when
        mockServerClient.clear(someRequestMatcher);
        // then
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/clear").withBody(someRequestMatcher.toString(), StandardCharsets.UTF_8), 20000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldSendClearRequestWithType() {
        // given
        HttpRequest someRequestMatcher = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        Mockito.when(mockHttpRequestSerializer.serialize(someRequestMatcher)).thenReturn(someRequestMatcher.toString());
        // when
        mockServerClient.clear(someRequestMatcher, LOG);
        // then
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/clear").withQueryStringParameter("type", "log").withBody(someRequestMatcher.toString(), StandardCharsets.UTF_8), 20000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldSendClearRequestForNullRequest() {
        // when
        mockServerClient.clear(null);
        // then
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/clear").withBody("", StandardCharsets.UTF_8), 20000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldRetrieveRequests() {
        // given - a request
        HttpRequest someRequestMatcher = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        Mockito.when(mockHttpRequestSerializer.serialize(someRequestMatcher)).thenReturn(someRequestMatcher.toString());
        // and - a client
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withBody("body"));
        // and - a response
        HttpRequest[] httpRequests = new HttpRequest[]{  };
        Mockito.when(mockHttpRequestSerializer.deserializeArray("body")).thenReturn(httpRequests);
        // when
        Assert.assertSame(httpRequests, mockServerClient.retrieveRecordedRequests(someRequestMatcher));
        // then
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", REQUESTS.name()).withQueryStringParameter("format", JSON.name()).withBody(someRequestMatcher.toString(), StandardCharsets.UTF_8), 20000, TimeUnit.MILLISECONDS);
        Mockito.verify(mockHttpRequestSerializer).deserializeArray("body");
    }

    @Test
    public void shouldRetrieveRequestsWithNullRequest() {
        // given
        HttpRequest[] httpRequests = new HttpRequest[]{  };
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withBody("body"));
        Mockito.when(mockHttpRequestSerializer.deserializeArray("body")).thenReturn(httpRequests);
        // when
        Assert.assertSame(httpRequests, mockServerClient.retrieveRecordedRequests(null));
        // then
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", REQUESTS.name()).withQueryStringParameter("format", JSON.name()).withBody("", StandardCharsets.UTF_8), 20000, TimeUnit.MILLISECONDS);
        Mockito.verify(mockHttpRequestSerializer).deserializeArray("body");
    }

    @Test
    public void shouldRetrieveActiveExpectations() {
        // given - a request
        HttpRequest someRequestMatcher = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        Mockito.when(mockHttpRequestSerializer.serialize(someRequestMatcher)).thenReturn(someRequestMatcher.toString());
        // and - a client
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withBody("body"));
        // and - an expectation
        Expectation[] expectations = new Expectation[]{  };
        Mockito.when(mockExpectationSerializer.deserializeArray("body")).thenReturn(expectations);
        // when
        Assert.assertSame(expectations, mockServerClient.retrieveActiveExpectations(someRequestMatcher));
        // then
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", ACTIVE_EXPECTATIONS.name()).withQueryStringParameter("format", JSON.name()).withBody(someRequestMatcher.toString(), StandardCharsets.UTF_8), 20000, TimeUnit.MILLISECONDS);
        Mockito.verify(mockExpectationSerializer).deserializeArray("body");
    }

    @Test
    public void shouldRetrieveActiveExpectationsWithNullRequest() {
        // given
        Expectation[] expectations = new Expectation[]{  };
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withBody("body"));
        Mockito.when(mockExpectationSerializer.deserializeArray("body")).thenReturn(expectations);
        // when
        Assert.assertSame(expectations, mockServerClient.retrieveActiveExpectations(null));
        // then
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", ACTIVE_EXPECTATIONS.name()).withQueryStringParameter("format", JSON.name()).withBody("", StandardCharsets.UTF_8), 20000, TimeUnit.MILLISECONDS);
        Mockito.verify(mockExpectationSerializer).deserializeArray("body");
    }

    @Test
    public void shouldRetrieveRecordedExpectations() {
        // given - a request
        HttpRequest someRequestMatcher = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        Mockito.when(mockHttpRequestSerializer.serialize(someRequestMatcher)).thenReturn(someRequestMatcher.toString());
        // and - a client
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withBody("body"));
        // and - an expectation
        Expectation[] expectations = new Expectation[]{  };
        Mockito.when(mockExpectationSerializer.deserializeArray("body")).thenReturn(expectations);
        // when
        Assert.assertSame(expectations, mockServerClient.retrieveRecordedExpectations(someRequestMatcher));
        // then
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", RECORDED_EXPECTATIONS.name()).withQueryStringParameter("format", JSON.name()).withBody(someRequestMatcher.toString(), StandardCharsets.UTF_8), 20000, TimeUnit.MILLISECONDS);
        Mockito.verify(mockExpectationSerializer).deserializeArray("body");
    }

    @Test
    public void shouldRetrieveExpectationsWithNullRequest() {
        // given
        Expectation[] expectations = new Expectation[]{  };
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withBody("body"));
        Mockito.when(mockExpectationSerializer.deserializeArray("body")).thenReturn(expectations);
        // when
        Assert.assertSame(expectations, mockServerClient.retrieveRecordedExpectations(null));
        // then
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/retrieve").withQueryStringParameter("type", RECORDED_EXPECTATIONS.name()).withQueryStringParameter("format", JSON.name()).withBody("", StandardCharsets.UTF_8), 20000, TimeUnit.MILLISECONDS);
        Mockito.verify(mockExpectationSerializer).deserializeArray("body");
    }

    @Test
    public void shouldVerifyDoesNotMatchSingleRequestNoVerificationTimes() {
        // given
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withBody("Request not found at least once expected:<foo> but was:<bar>"));
        Mockito.when(mockVerificationSequenceSerializer.serialize(ArgumentMatchers.any(VerificationSequence.class))).thenReturn("verification_json");
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        try {
            mockServerClient.verify(httpRequest);
            // then
            Assert.fail();
        } catch (AssertionError ae) {
            Mockito.verify(mockVerificationSequenceSerializer).serialize(new VerificationSequence().withRequests(httpRequest));
            Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/verifySequence").withBody("verification_json", StandardCharsets.UTF_8), 20000, TimeUnit.MILLISECONDS);
            Assert.assertThat(ae.getMessage(), CoreMatchers.is("Request not found at least once expected:<foo> but was:<bar>"));
        }
    }

    @Test
    public void shouldVerifyDoesNotMatchMultipleRequestsNoVerificationTimes() {
        // given
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withBody("Request not found at least once expected:<foo> but was:<bar>"));
        Mockito.when(mockVerificationSequenceSerializer.serialize(ArgumentMatchers.any(VerificationSequence.class))).thenReturn("verification_json");
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        try {
            mockServerClient.verify(httpRequest, httpRequest);
            // then
            Assert.fail();
        } catch (AssertionError ae) {
            Mockito.verify(mockVerificationSequenceSerializer).serialize(new VerificationSequence().withRequests(httpRequest, httpRequest));
            Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/verifySequence").withBody("verification_json", StandardCharsets.UTF_8), 20000, TimeUnit.MILLISECONDS);
            Assert.assertThat(ae.getMessage(), CoreMatchers.is("Request not found at least once expected:<foo> but was:<bar>"));
        }
    }

    @Test
    public void shouldVerifyDoesMatchSingleRequestNoVerificationTimes() {
        // given
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withBody(""));
        Mockito.when(mockVerificationSequenceSerializer.serialize(ArgumentMatchers.any(VerificationSequence.class))).thenReturn("verification_json");
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        try {
            mockServerClient.verify(httpRequest);
            // then
        } catch (AssertionError ae) {
            Assert.fail();
        }
        // then
        Mockito.verify(mockVerificationSequenceSerializer).serialize(new VerificationSequence().withRequests(httpRequest));
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/verifySequence").withBody("verification_json", StandardCharsets.UTF_8), 20000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldVerifyDoesMatchSingleRequestOnce() {
        // given
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withBody(""));
        Mockito.when(mockVerificationSerializer.serialize(ArgumentMatchers.any(Verification.class))).thenReturn("verification_json");
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        try {
            mockServerClient.verify(httpRequest, once());
            // then
        } catch (AssertionError ae) {
            Assert.fail();
        }
        // then
        Mockito.verify(mockVerificationSerializer).serialize(verification().withRequest(httpRequest).withTimes(once()));
        Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/verify").withBody("verification_json", StandardCharsets.UTF_8), 20000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void shouldVerifyDoesNotMatchSingleRequest() {
        // given
        Mockito.when(mockHttpClient.sendRequest(ArgumentMatchers.any(HttpRequest.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(response().withBody("Request not found at least once expected:<foo> but was:<bar>"));
        Mockito.when(mockVerificationSerializer.serialize(ArgumentMatchers.any(Verification.class))).thenReturn("verification_json");
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        try {
            mockServerClient.verify(httpRequest, atLeast(1));
            // then
            Assert.fail();
        } catch (AssertionError ae) {
            Mockito.verify(mockVerificationSerializer).serialize(verification().withRequest(httpRequest).withTimes(atLeast(1)));
            Mockito.verify(mockHttpClient).sendRequest(request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 1080)).withMethod("PUT").withPath("/mockserver/verify").withBody("verification_json", StandardCharsets.UTF_8), 20000, TimeUnit.MILLISECONDS);
            Assert.assertThat(ae.getMessage(), CoreMatchers.is("Request not found at least once expected:<foo> but was:<bar>"));
        }
    }

    @Test
    public void shouldHandleNullHttpRequest() {
        // then
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Matchers.containsString("verify(HttpRequest, VerificationTimes) requires a non null HttpRequest object"));
        // when
        mockServerClient.verify(null, VerificationTimes.exactly(2));
    }

    @Test
    public void shouldHandleNullVerificationTimes() {
        // then
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Matchers.containsString("verify(HttpRequest, VerificationTimes) requires a non null VerificationTimes object"));
        // when
        mockServerClient.verify(request(), null);
    }

    @Test
    public void shouldHandleNullHttpRequestSequence() {
        // then
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Matchers.containsString("verify(HttpRequest...) requires a non null non empty array of HttpRequest objects"));
        // when
        mockServerClient.verify(((HttpRequest) (null)));
    }

    @Test
    public void shouldHandleEmptyHttpRequestSequence() {
        // then
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(Matchers.containsString("verify(HttpRequest...) requires a non null non empty array of HttpRequest objects"));
        // when
        mockServerClient.verify();
    }

    @Test
    public void shouldHandleExplicitUnsecuredConnectionsToMockServer() {
        // given
        mockServerClient.withSecure(false);
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        HttpResponse httpResponse = new HttpResponse().withBody("some_response_body").withHeaders(new Header("responseName", "responseValue"));
        // when
        ForwardChainExpectation forwardChainExpectation = mockServerClient.when(httpRequest);
        forwardChainExpectation.respond(httpResponse);
        // then
        Expectation expectation = forwardChainExpectation.getExpectation();
        Assert.assertTrue(expectation.isActive());
        Assert.assertSame(httpResponse, expectation.getHttpResponse());
        Assert.assertEquals(Times.unlimited(), expectation.getTimes());
        ArgumentCaptor<HttpRequest> configRequestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        Mockito.verify(mockHttpClient).sendRequest(configRequestCaptor.capture(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(TimeUnit.class));
        Assert.assertFalse(configRequestCaptor.getValue().isSecure());
    }

    @Test
    public void shouldHandleExplicitSecuredConnectionsToMockServer() {
        // given
        mockServerClient.withSecure(true);
        HttpRequest httpRequest = new HttpRequest().withPath("/some_path").withBody(new StringBody("some_request_body"));
        HttpResponse httpResponse = new HttpResponse().withBody("some_response_body").withHeaders(new Header("responseName", "responseValue"));
        // when
        ForwardChainExpectation forwardChainExpectation = mockServerClient.when(httpRequest);
        forwardChainExpectation.respond(httpResponse);
        // then
        Expectation expectation = forwardChainExpectation.getExpectation();
        Assert.assertTrue(expectation.isActive());
        Assert.assertSame(httpResponse, expectation.getHttpResponse());
        Assert.assertEquals(Times.unlimited(), expectation.getTimes());
        ArgumentCaptor<HttpRequest> configRequestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        Mockito.verify(mockHttpClient).sendRequest(configRequestCaptor.capture(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(TimeUnit.class));
        Assert.assertTrue(configRequestCaptor.getValue().isSecure());
    }
}

