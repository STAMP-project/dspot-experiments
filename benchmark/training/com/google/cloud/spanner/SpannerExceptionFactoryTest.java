/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.spanner;


import Status.Code.ABORTED;
import Status.Code.INTERNAL;
import Status.Code.RESOURCE_EXHAUSTED;
import com.google.protobuf.Duration;
import com.google.rpc.RetryInfo;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link SpannerExceptionFactory}.
 */
@RunWith(JUnit4.class)
public class SpannerExceptionFactoryTest {
    @Test
    public void http2InternalErrorIsRetryable() {
        Status status = Status.fromCodeValue(INTERNAL.value()).withDescription("HTTP/2 error code: INTERNAL_ERROR");
        SpannerException e = SpannerExceptionFactory.newSpannerException(new io.grpc.StatusRuntimeException(status));
        assertThat(e.isRetryable()).isTrue();
    }

    @Test
    public void connectionClosedIsRetryable() {
        Status status = Status.fromCodeValue(INTERNAL.value()).withDescription("Connection closed with unknown cause");
        SpannerException e = SpannerExceptionFactory.newSpannerException(new io.grpc.StatusRuntimeException(status));
        assertThat(e.isRetryable()).isTrue();
    }

    @Test
    public void resourceExhausted() {
        Status status = Status.fromCodeValue(RESOURCE_EXHAUSTED.value()).withDescription("Memory pushback");
        SpannerException e = SpannerExceptionFactory.newSpannerException(new io.grpc.StatusRuntimeException(status));
        assertThat(e.isRetryable()).isFalse();
    }

    @Test
    public void resourceExhaustedWithBackoff() {
        Status status = Status.fromCodeValue(RESOURCE_EXHAUSTED.value()).withDescription("Memory pushback");
        Metadata trailers = new Metadata();
        Metadata.Key<RetryInfo> key = ProtoUtils.keyForProto(RetryInfo.getDefaultInstance());
        RetryInfo retryInfo = RetryInfo.newBuilder().setRetryDelay(Duration.newBuilder().setNanos(1000000).setSeconds(1L)).build();
        trailers.put(key, retryInfo);
        SpannerException e = SpannerExceptionFactory.newSpannerException(new io.grpc.StatusRuntimeException(status, trailers));
        assertThat(e.isRetryable()).isTrue();
        assertThat(e.getRetryDelayInMillis()).isEqualTo(1001);
    }

    @Test
    public void abortWithRetryInfo() {
        Metadata.Key<RetryInfo> key = ProtoUtils.keyForProto(RetryInfo.getDefaultInstance());
        Status status = Status.fromCodeValue(ABORTED.value());
        Metadata trailers = new Metadata();
        RetryInfo retryInfo = RetryInfo.newBuilder().setRetryDelay(Duration.newBuilder().setNanos(1000000).setSeconds(1L)).build();
        trailers.put(key, retryInfo);
        SpannerException e = SpannerExceptionFactory.newSpannerException(new io.grpc.StatusRuntimeException(status, trailers));
        assertThat(e).isInstanceOf(AbortedException.class);
        assertThat(getRetryDelayInMillis()).isEqualTo(1001L);
    }

    @Test
    public void abortWithoutRetryInfo() {
        Status status = Status.fromCodeValue(ABORTED.value());
        SpannerException e = SpannerExceptionFactory.newSpannerException(new io.grpc.StatusRuntimeException(status));
        assertThat(e).isInstanceOf(AbortedException.class);
        assertThat(getRetryDelayInMillis()).isEqualTo((-1L));
    }

    @Test
    public void abortWithoutDuration() {
        Metadata.Key<RetryInfo> key = ProtoUtils.keyForProto(RetryInfo.getDefaultInstance());
        Status status = Status.fromCodeValue(ABORTED.value());
        Metadata trailers = new Metadata();
        trailers.put(key, RetryInfo.getDefaultInstance());
        SpannerException e = SpannerExceptionFactory.newSpannerException(new io.grpc.StatusRuntimeException(status, trailers));
        assertThat(e).isInstanceOf(AbortedException.class);
        assertThat(getRetryDelayInMillis()).isEqualTo((-1L));
    }
}

