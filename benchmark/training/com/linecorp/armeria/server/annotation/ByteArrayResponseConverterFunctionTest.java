/**
 * Copyright 2019 LINE Corporation
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
package com.linecorp.armeria.server.annotation;


import HttpStatus.OK;
import MediaType.OCTET_STREAM;
import com.google.common.collect.ImmutableList;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.internal.FallthroughException;
import com.linecorp.armeria.server.ServiceRequestContext;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class ByteArrayResponseConverterFunctionTest {
    private static final ResponseConverterFunction function = new ByteArrayResponseConverterFunction();

    private static final ServiceRequestContext ctx = Mockito.mock(ServiceRequestContext.class);

    private static final HttpHeaders OCTET_STREAM_HEADERS = HttpHeaders.of(OK).contentType(OCTET_STREAM);

    private static final HttpHeaders DEFAULT_TRAILING_HEADERS = HttpHeaders.EMPTY_HEADERS;

    @Test
    public void streaming_HttpData() throws Exception {
        final List<HttpData> contents = ImmutableList.of(HttpData.ofUtf8("foo"), HttpData.ofUtf8("bar"), HttpData.ofUtf8("baz"));
        for (final Object result : ImmutableList.of(Flux.fromIterable(contents), contents.stream())) {
            StepVerifier.create(ByteArrayResponseConverterFunctionTest.from(result)).expectNext(ByteArrayResponseConverterFunctionTest.OCTET_STREAM_HEADERS).expectNext(contents.get(0)).expectNext(contents.get(1)).expectNext(contents.get(2)).expectComplete().verify();
        }
        StepVerifier.create(ByteArrayResponseConverterFunctionTest.from(contents.get(0))).expectNext(ByteArrayResponseConverterFunctionTest.OCTET_STREAM_HEADERS).expectNext(contents.get(0)).expectComplete().verify();
    }

    @Test
    public void streaming_byteArray() throws Exception {
        final List<byte[]> contents = ImmutableList.of("foo".getBytes(), "bar".getBytes(), "baz".getBytes());
        for (final Object result : ImmutableList.of(Flux.fromIterable(contents), contents.stream())) {
            StepVerifier.create(ByteArrayResponseConverterFunctionTest.from(result)).expectNext(ByteArrayResponseConverterFunctionTest.OCTET_STREAM_HEADERS).expectNext(HttpData.of(contents.get(0))).expectNext(HttpData.of(contents.get(1))).expectNext(HttpData.of(contents.get(2))).expectComplete().verify();
        }
        StepVerifier.create(ByteArrayResponseConverterFunctionTest.from(contents.get(0))).expectNext(ByteArrayResponseConverterFunctionTest.OCTET_STREAM_HEADERS).expectNext(HttpData.of(contents.get(0))).expectComplete().verify();
    }

    @Test
    public void streaming_unsupportedType() throws Exception {
        final String unsupported = "Unsupported type.";
        StepVerifier.create(ByteArrayResponseConverterFunctionTest.from(Mono.just(unsupported))).expectError(IllegalStateException.class).verify();
        StepVerifier.create(ByteArrayResponseConverterFunctionTest.from(Stream.of(unsupported))).expectError(IllegalStateException.class).verify();
        assertThatThrownBy(() -> from(unsupported)).isInstanceOf(FallthroughException.class);
    }

    @Test
    public void withoutContentType() throws Exception {
        // 'application/octet-stream' should be added.
        StepVerifier.create(ByteArrayResponseConverterFunctionTest.function.convertResponse(ByteArrayResponseConverterFunctionTest.ctx, HttpHeaders.of(OK), HttpData.ofUtf8("foo"), ByteArrayResponseConverterFunctionTest.DEFAULT_TRAILING_HEADERS)).expectNext(ByteArrayResponseConverterFunctionTest.OCTET_STREAM_HEADERS).expectNext(HttpData.ofUtf8("foo")).expectComplete().verify();
        StepVerifier.create(ByteArrayResponseConverterFunctionTest.function.convertResponse(ByteArrayResponseConverterFunctionTest.ctx, HttpHeaders.of(OK), "foo".getBytes(), ByteArrayResponseConverterFunctionTest.DEFAULT_TRAILING_HEADERS)).expectNext(ByteArrayResponseConverterFunctionTest.OCTET_STREAM_HEADERS).expectNext(HttpData.of("foo".getBytes())).expectComplete().verify();
        assertThatThrownBy(() -> from("Unsupported type.")).isInstanceOf(FallthroughException.class);
    }
}

