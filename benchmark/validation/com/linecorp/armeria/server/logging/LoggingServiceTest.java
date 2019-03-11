/**
 * Copyright 2017 LINE Corporation
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
package com.linecorp.armeria.server.logging;


import HttpHeaderNames.CONTENT_MD5;
import HttpHeaderNames.CONTENT_TYPE;
import HttpHeaderNames.COOKIE;
import HttpHeaderNames.SET_COOKIE;
import HttpMethod.GET;
import LogLevel.INFO;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.logging.RequestLog;
import com.linecorp.armeria.server.Service;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.testing.internal.AnticipatedException;
import java.util.function.Function;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;


public class LoggingServiceTest {
    private static final HttpRequest REQUEST = HttpRequest.of(GET, "/foo");

    private static final HttpHeaders REQUEST_HEADERS = HttpHeaders.of(COOKIE, "armeria");

    private static final Object REQUEST_CONTENT = "request with pii";

    private static final HttpHeaders REQUEST_TRAILERS = HttpHeaders.of(CONTENT_MD5, "barmeria");

    private static final HttpHeaders RESPONSE_HEADERS = HttpHeaders.of(SET_COOKIE, "carmeria");

    private static final Object RESPONSE_CONTENT = "response with pii";

    private static final HttpHeaders RESPONSE_TRAILERS = HttpHeaders.of(CONTENT_MD5, "darmeria");

    private static final String REQUEST_FORMAT = "Request: {}";

    private static final String RESPONSE_FORMAT = "Response: {}";

    @Rule
    public MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    private ServiceRequestContext ctx;

    @Mock
    private Logger logger;

    @Mock
    private RequestLog log;

    @Mock
    private Service<HttpRequest, HttpResponse> delegate;

    @Test
    public void defaults_success() throws Exception {
        final LoggingService<HttpRequest, HttpResponse> service = new LoggingServiceBuilder().<HttpRequest, HttpResponse>newDecorator().apply(delegate);
        service.serve(ctx, LoggingServiceTest.REQUEST);
        Mockito.verify(logger, Mockito.never()).info(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(Object.class));
    }

    @Test
    public void defaults_error() throws Exception {
        final LoggingService<HttpRequest, HttpResponse> service = new LoggingServiceBuilder().<HttpRequest, HttpResponse>newDecorator().apply(delegate);
        final IllegalStateException cause = new IllegalStateException("Failed");
        Mockito.when(log.responseCause()).thenReturn(cause);
        service.serve(ctx, LoggingServiceTest.REQUEST);
        Mockito.verify(logger).warn(LoggingServiceTest.REQUEST_FORMAT, ((((("headers: " + (LoggingServiceTest.REQUEST_HEADERS)) + ", content: ") + (LoggingServiceTest.REQUEST_CONTENT)) + ", trailers: ") + (LoggingServiceTest.REQUEST_TRAILERS)));
        Mockito.verify(logger).warn(LoggingServiceTest.RESPONSE_FORMAT, ((((("headers: " + (LoggingServiceTest.RESPONSE_HEADERS)) + ", content: ") + (LoggingServiceTest.RESPONSE_CONTENT)) + ", trailers: ") + (LoggingServiceTest.RESPONSE_TRAILERS)), cause);
    }

    @Test
    public void infoLevel() throws Exception {
        final LoggingService<HttpRequest, HttpResponse> service = new LoggingServiceBuilder().requestLogLevel(INFO).successfulResponseLogLevel(INFO).<HttpRequest, HttpResponse>newDecorator().apply(delegate);
        service.serve(ctx, LoggingServiceTest.REQUEST);
        Mockito.verify(logger).info(LoggingServiceTest.REQUEST_FORMAT, ((((("headers: " + (LoggingServiceTest.REQUEST_HEADERS)) + ", content: ") + (LoggingServiceTest.REQUEST_CONTENT)) + ", trailers: ") + (LoggingServiceTest.REQUEST_TRAILERS)));
        Mockito.verify(logger).info(LoggingServiceTest.RESPONSE_FORMAT, ((((("headers: " + (LoggingServiceTest.RESPONSE_HEADERS)) + ", content: ") + (LoggingServiceTest.RESPONSE_CONTENT)) + ", trailers: ") + (LoggingServiceTest.RESPONSE_TRAILERS)));
    }

    @Test
    public void sanitize() throws Exception {
        final HttpHeaders sanitizedRequestHeaders = HttpHeaders.of(CONTENT_TYPE, "no cookies, too bad");
        final Function<HttpHeaders, HttpHeaders> requestHeadersSanitizer = ( headers) -> {
            assertThat(headers).isEqualTo(LoggingServiceTest.REQUEST_HEADERS);
            return sanitizedRequestHeaders;
        };
        final Function<Object, Object> requestContentSanitizer = ( content) -> {
            assertThat(content).isEqualTo(LoggingServiceTest.REQUEST_CONTENT);
            return "clean request";
        };
        final HttpHeaders sanitizedRequestTrailers = HttpHeaders.of(CONTENT_MD5, "it's the secret");
        final Function<HttpHeaders, HttpHeaders> requestTrailersSanitizer = ( headers) -> {
            assertThat(headers).isEqualTo(LoggingServiceTest.REQUEST_TRAILERS);
            return sanitizedRequestTrailers;
        };
        final HttpHeaders sanitizedResponseHeaders = HttpHeaders.of(CONTENT_TYPE, "where are the cookies?");
        final Function<HttpHeaders, HttpHeaders> responseHeadersSanitizer = ( headers) -> {
            assertThat(headers).isEqualTo(LoggingServiceTest.RESPONSE_HEADERS);
            return sanitizedResponseHeaders;
        };
        final Function<Object, Object> responseContentSanitizer = ( content) -> {
            assertThat(content).isEqualTo(LoggingServiceTest.RESPONSE_CONTENT);
            return "clean response";
        };
        final HttpHeaders sanitizedResponseTrailers = HttpHeaders.of(CONTENT_MD5, "it's a secret");
        final Function<HttpHeaders, HttpHeaders> responseTrailersSanitizer = ( headers) -> {
            assertThat(headers).isEqualTo(LoggingServiceTest.RESPONSE_TRAILERS);
            return sanitizedResponseTrailers;
        };
        final LoggingService<HttpRequest, HttpResponse> service = new LoggingServiceBuilder().requestLogLevel(INFO).successfulResponseLogLevel(INFO).requestHeadersSanitizer(requestHeadersSanitizer).requestContentSanitizer(requestContentSanitizer).requestTrailersSanitizer(requestTrailersSanitizer).requestTrailersSanitizer(requestTrailersSanitizer).responseHeadersSanitizer(responseHeadersSanitizer).responseContentSanitizer(responseContentSanitizer).responseTrailersSanitizer(responseTrailersSanitizer).<HttpRequest, HttpResponse>newDecorator().apply(delegate);
        service.serve(ctx, LoggingServiceTest.REQUEST);
        Mockito.verify(logger).info(LoggingServiceTest.REQUEST_FORMAT, (((("headers: " + sanitizedRequestHeaders) + ", content: clean request") + ", trailers: ") + sanitizedRequestTrailers));
        Mockito.verify(logger).info(LoggingServiceTest.RESPONSE_FORMAT, (((("headers: " + sanitizedResponseHeaders) + ", content: clean response") + ", trailers: ") + sanitizedResponseTrailers));
    }

    @Test
    public void sanitize_error() throws Exception {
        final IllegalStateException dirtyCause = new IllegalStateException("dirty");
        final AnticipatedException cleanCause = new AnticipatedException("clean");
        final Function<Throwable, Throwable> responseCauseSanitizer = ( cause) -> {
            assertThat(cause).isSameAs(dirtyCause);
            return cleanCause;
        };
        final LoggingService<HttpRequest, HttpResponse> service = new LoggingServiceBuilder().requestLogLevel(INFO).successfulResponseLogLevel(INFO).responseCauseSanitizer(responseCauseSanitizer).<HttpRequest, HttpResponse>newDecorator().apply(delegate);
        Mockito.when(log.responseCause()).thenReturn(dirtyCause);
        service.serve(ctx, LoggingServiceTest.REQUEST);
        Mockito.verify(logger).info(LoggingServiceTest.REQUEST_FORMAT, ((((("headers: " + (LoggingServiceTest.REQUEST_HEADERS)) + ", content: ") + (LoggingServiceTest.REQUEST_CONTENT)) + ", trailers: ") + (LoggingServiceTest.REQUEST_TRAILERS)));
        Mockito.verify(logger).warn(LoggingServiceTest.RESPONSE_FORMAT, ((((("headers: " + (LoggingServiceTest.RESPONSE_HEADERS)) + ", content: ") + (LoggingServiceTest.RESPONSE_CONTENT)) + ", trailers: ") + (LoggingServiceTest.RESPONSE_TRAILERS)), cleanCause);
    }

    @Test
    public void sanitize_error_silenced() throws Exception {
        final IllegalStateException dirtyCause = new IllegalStateException("dirty");
        final Function<Throwable, Throwable> responseCauseSanitizer = ( cause) -> {
            assertThat(cause).isSameAs(dirtyCause);
            return null;
        };
        final LoggingService<HttpRequest, HttpResponse> service = new LoggingServiceBuilder().requestLogLevel(INFO).successfulResponseLogLevel(INFO).responseCauseSanitizer(responseCauseSanitizer).<HttpRequest, HttpResponse>newDecorator().apply(delegate);
        Mockito.when(log.responseCause()).thenReturn(dirtyCause);
        service.serve(ctx, LoggingServiceTest.REQUEST);
        Mockito.verify(logger).info(LoggingServiceTest.REQUEST_FORMAT, ((((("headers: " + (LoggingServiceTest.REQUEST_HEADERS)) + ", content: ") + (LoggingServiceTest.REQUEST_CONTENT)) + ", trailers: ") + (LoggingServiceTest.REQUEST_TRAILERS)));
        Mockito.verify(logger).warn(LoggingServiceTest.RESPONSE_FORMAT, ((((("headers: " + (LoggingServiceTest.RESPONSE_HEADERS)) + ", content: ") + (LoggingServiceTest.RESPONSE_CONTENT)) + ", trailers: ") + (LoggingServiceTest.RESPONSE_TRAILERS)));
    }

    @Test
    public void sample() throws Exception {
        final LoggingService<HttpRequest, HttpResponse> service = new LoggingServiceBuilder().requestLogLevel(INFO).successfulResponseLogLevel(INFO).samplingRate(0.0F).<HttpRequest, HttpResponse>newDecorator().apply(delegate);
        service.serve(ctx, LoggingServiceTest.REQUEST);
        Mockito.verifyZeroInteractions(logger);
    }
}

