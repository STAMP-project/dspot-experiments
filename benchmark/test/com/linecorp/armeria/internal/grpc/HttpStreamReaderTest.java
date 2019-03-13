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
package com.linecorp.armeria.internal.grpc;


import GrpcHeaderNames.GRPC_STATUS;
import HttpData.EMPTY_DATA;
import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import Status.INTERNAL;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import io.grpc.Status;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.reactivestreams.Subscription;


public class HttpStreamReaderTest {
    private static final HttpHeaders HEADERS = HttpHeaders.of(OK);

    private static final HttpHeaders TRAILERS = HttpHeaders.of().set(GRPC_STATUS, "2");

    private static final HttpData DATA = HttpData.ofUtf8("foobarbaz");

    @Rule
    public MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    private TransportStatusListener transportStatusListener;

    @Mock
    private ArmeriaMessageDeframer deframer;

    @Mock
    private Subscription subscription;

    private HttpStreamReader reader;

    @Test
    public void subscribe_noServerRequests() {
        reader.onSubscribe(subscription);
        Mockito.verifyZeroInteractions(subscription);
    }

    @Test
    public void subscribe_hasServerRequests_subscribeFirst() {
        Mockito.when(deframer.isStalled()).thenReturn(true);
        reader.onSubscribe(subscription);
        Mockito.verifyZeroInteractions(subscription);
        reader.request(1);
        Mockito.verify(subscription).request(1);
        Mockito.verifyNoMoreInteractions(subscription);
    }

    @Test
    public void subscribe_hasServerRequests_requestFirst() {
        Mockito.when(deframer.isStalled()).thenReturn(true);
        reader.request(1);
        reader.onSubscribe(subscription);
        Mockito.verify(subscription).request(1);
        Mockito.verifyNoMoreInteractions(subscription);
    }

    @Test
    public void onHeaders() {
        reader.onSubscribe(subscription);
        Mockito.verifyZeroInteractions(subscription);
        Mockito.when(deframer.isStalled()).thenReturn(true);
        reader.onNext(HttpStreamReaderTest.HEADERS);
        Mockito.verify(subscription).request(1);
    }

    @Test
    public void onTrailers() {
        reader.onSubscribe(subscription);
        Mockito.when(deframer.isStalled()).thenReturn(true);
        reader.onNext(HttpStreamReaderTest.TRAILERS);
        Mockito.verifyZeroInteractions(subscription);
    }

    @Test
    public void onMessage_noServerRequests() {
        reader.onSubscribe(subscription);
        reader.onNext(HttpStreamReaderTest.DATA);
        Mockito.verify(deframer).deframe(HttpStreamReaderTest.DATA, false);
        Mockito.verifyZeroInteractions(subscription);
    }

    @Test
    public void onMessage_hasServerRequests() {
        reader.onSubscribe(subscription);
        Mockito.when(deframer.isStalled()).thenReturn(true);
        reader.onNext(HttpStreamReaderTest.DATA);
        Mockito.verify(deframer).deframe(HttpStreamReaderTest.DATA, false);
        Mockito.verify(subscription).request(1);
    }

    @Test
    public void onMessage_deframeError() {
        Mockito.doThrow(INTERNAL.asRuntimeException()).when(deframer).deframe(ArgumentMatchers.isA(HttpData.class), ArgumentMatchers.anyBoolean());
        reader.onSubscribe(subscription);
        reader.onNext(HttpStreamReaderTest.DATA);
        Mockito.verify(deframer).deframe(HttpStreamReaderTest.DATA, false);
        Mockito.verify(transportStatusListener).transportReportStatus(INTERNAL);
        Mockito.verify(deframer).close();
    }

    @Test
    public void onMessage_deframeError_errorListenerThrows() {
        Mockito.doThrow(INTERNAL.asRuntimeException()).when(deframer).deframe(ArgumentMatchers.isA(HttpData.class), ArgumentMatchers.anyBoolean());
        Mockito.doThrow(new IllegalStateException()).when(transportStatusListener).transportReportStatus(ArgumentMatchers.isA(Status.class));
        reader.onSubscribe(subscription);
        assertThatThrownBy(() -> reader.onNext(DATA)).isInstanceOf(IllegalStateException.class);
        Mockito.verify(deframer).close();
    }

    @Test
    public void clientDone() {
        reader.apply(null, null);
        Mockito.verify(deframer).deframe(EMPTY_DATA, true);
        Mockito.verify(deframer).closeWhenComplete();
    }

    @Test
    public void serverCancelled() {
        reader.onSubscribe(subscription);
        reader.cancel();
        Mockito.verify(subscription).cancel();
    }

    @Test
    public void httpNotOk() {
        reader.onSubscribe(subscription);
        Mockito.verifyZeroInteractions(subscription);
        reader.onNext(HttpHeaders.of(UNAUTHORIZED));
        Mockito.verifyZeroInteractions(subscription);
        Mockito.verifyZeroInteractions(deframer);
        Mockito.verify(transportStatusListener).transportReportStatus(ArgumentMatchers.argThat(( s) -> (s.getCode()) == (Status.UNAUTHENTICATED.getCode())));
    }
}

