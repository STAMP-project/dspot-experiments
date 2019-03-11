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
package com.linecorp.armeria.common.logging;


import HttpMethod.UNKNOWN;
import SerializationFormat.NONE;
import SessionProtocol.H2C;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.common.RpcResponse;
import com.linecorp.armeria.testing.internal.AnticipatedException;
import io.netty.channel.Channel;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class DefaultRequestLogTest {
    @Rule
    public MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    private RequestContext ctx;

    @Mock
    private Channel channel;

    private DefaultRequestLog log;

    @Test
    public void endRequestSuccess() {
        Mockito.when(ctx.sessionProtocol()).thenReturn(H2C);
        log.endRequest();
        assertThat(log.requestDurationNanos()).isZero();
        assertThat(log.requestCause()).isNull();
    }

    @Test
    public void endRequestWithoutHeaders() {
        Mockito.when(ctx.sessionProtocol()).thenReturn(H2C);
        log.endRequest();
        final HttpHeaders headers = log.requestHeaders();
        assertThat(headers.scheme()).isEqualTo("http");
        assertThat(headers.authority()).isEqualTo("?");
        assertThat(headers.method()).isSameAs(UNKNOWN);
        assertThat(headers.path()).isEqualTo("?");
    }

    @Test
    public void endResponseSuccess() {
        Mockito.when(ctx.sessionProtocol()).thenReturn(H2C);
        log.endResponse();
        assertThat(log.responseDurationNanos()).isZero();
        assertThat(log.responseCause()).isNull();
    }

    @Test
    public void endResponseFailure() {
        final Throwable error = new Throwable("response failed");
        log.endResponse(error);
        assertThat(log.responseDurationNanos()).isZero();
        assertThat(log.responseCause()).isSameAs(error);
    }

    @Test
    public void endResponseWithoutHeaders() {
        log.endResponse();
        assertThat(log.responseHeaders().status()).isEqualTo(HttpStatus.valueOf(0));
    }

    @Test
    public void rpcFailure_endResponseWithoutCause() {
        final Throwable error = new Throwable("response failed");
        log.responseContent(RpcResponse.ofFailure(error), null);
        // If user code doesn't call endResponse, the framework automatically does with no cause.
        log.endResponse();
        assertThat(log.responseDurationNanos()).isZero();
        assertThat(log.responseCause()).isSameAs(error);
    }

    @Test
    public void rpcFailure_endResponseDifferentCause() {
        final Throwable error = new Throwable("response failed one way");
        final Throwable error2 = new Throwable("response failed a different way?");
        log.responseContent(RpcResponse.ofFailure(error), null);
        log.endResponse(error2);
        assertThat(log.responseDurationNanos()).isZero();
        assertThat(log.responseCause()).isSameAs(error);
    }

    @Test
    public void addChild() {
        final DefaultRequestLog child = new DefaultRequestLog(ctx);
        log.addChild(child);
        child.startRequest(channel, H2C);
        assertThat(log.requestStartTimeMicros()).isEqualTo(child.requestStartTimeMicros());
        assertThat(log.channel()).isSameAs(channel);
        assertThat(log.sessionProtocol()).isSameAs(H2C);
        child.serializationFormat(NONE);
        assertThat(log.serializationFormat()).isSameAs(NONE);
        child.requestFirstBytesTransferred();
        assertThat(log.requestFirstBytesTransferredTimeNanos()).isEqualTo(child.requestFirstBytesTransferredTimeNanos());
        final HttpHeaders foo = HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("foo"), "foo");
        child.requestHeaders(foo);
        assertThat(log.requestHeaders()).isSameAs(foo);
        final String requestContent = "baz";
        final String rawRequestContent = "qux";
        child.requestContent(requestContent, rawRequestContent);
        assertThat(log.requestContent()).isSameAs(requestContent);
        assertThat(log.rawRequestContent()).isSameAs(rawRequestContent);
        child.endRequest();
        assertThat(log.requestDurationNanos()).isEqualTo(child.requestDurationNanos());
        // response-side log are propagated when RequestLogBuilder.endResponseWithLastChild() is invoked
        child.startResponse();
        assertThatThrownBy(() -> log.responseStartTimeMicros()).isExactlyInstanceOf(RequestLogAvailabilityException.class);
        child.responseFirstBytesTransferred();
        assertThatThrownBy(() -> log.responseFirstBytesTransferredTimeNanos()).isExactlyInstanceOf(RequestLogAvailabilityException.class);
        final HttpHeaders bar = HttpHeaders.of(com.linecorp.armeria.common.HttpHeaderNames.of("bar"), "bar");
        child.responseHeaders(bar);
        assertThatThrownBy(() -> log.responseHeaders()).isExactlyInstanceOf(RequestLogAvailabilityException.class);
        log.endResponseWithLastChild();
        assertThat(log.responseStartTimeMicros()).isEqualTo(child.responseStartTimeMicros());
        assertThat(log.responseFirstBytesTransferredTimeNanos()).isEqualTo(child.responseFirstBytesTransferredTimeNanos());
        assertThat(log.responseHeaders()).isSameAs(bar);
        final String responseContent = "baz1";
        final String rawResponseContent = "qux1";
        child.responseContent(responseContent, rawResponseContent);
        assertThat(log.responseContent()).isSameAs(responseContent);
        assertThat(log.rawResponseContent()).isSameAs(rawResponseContent);
        child.endResponse(new AnticipatedException("Oops!"));
        assertThat(log.responseDurationNanos()).isEqualTo(child.responseDurationNanos());
        assertThat(log.totalDurationNanos()).isEqualTo(child.totalDurationNanos());
    }
}

