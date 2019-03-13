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
import MediaType.JSON_SEQ;
import MediaType.JSON_UTF_8;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.testing.internal.AnticipatedException;
import io.netty.util.AsciiString;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


public class JacksonResponseConverterFunctionTest {
    private static final ResponseConverterFunction function = new JacksonResponseConverterFunction();

    private static final ServiceRequestContext ctx = Mockito.mock(ServiceRequestContext.class);

    // Copied from JsonTextSequences class.
    private static final byte RECORD_SEPARATOR = 30;

    private static final byte LINE_FEED = 10;

    private static final HttpHeaders JSON_HEADERS = HttpHeaders.of(OK).contentType(JSON_UTF_8);

    private static final HttpHeaders JSON_SEQ_HEADERS = HttpHeaders.of(OK).contentType(JSON_SEQ);

    private static final HttpHeaders DEFAULT_TRAILING_HEADERS = HttpHeaders.EMPTY_HEADERS;

    /**
     * Strings which are used for a publisher.
     */
    private static final String[] TEST_STRINGS = new String[]{ "foo", "bar", "baz", "qux" };

    /**
     * {@link HttpData} instances which are expected to be produced by the
     * {@link JacksonResponseConverterFunction} when the function gets the
     * {@link #TEST_STRINGS} as input.
     */
    private static final HttpData[] EXPECTED_CONTENTS = new HttpData[]{ HttpData.of(new byte[]{ JacksonResponseConverterFunctionTest.RECORD_SEPARATOR, '\"', 'f', 'o', 'o', '\"', JacksonResponseConverterFunctionTest.LINE_FEED }), HttpData.of(new byte[]{ JacksonResponseConverterFunctionTest.RECORD_SEPARATOR, '\"', 'b', 'a', 'r', '\"', JacksonResponseConverterFunctionTest.LINE_FEED }), HttpData.of(new byte[]{ JacksonResponseConverterFunctionTest.RECORD_SEPARATOR, '\"', 'b', 'a', 'z', '\"', JacksonResponseConverterFunctionTest.LINE_FEED }), HttpData.of(new byte[]{ JacksonResponseConverterFunctionTest.RECORD_SEPARATOR, '\"', 'q', 'u', 'x', '\"', JacksonResponseConverterFunctionTest.LINE_FEED }) };

    @Test
    public void aggregatedJson() throws Exception {
        expectAggregatedJson(Stream.of(JacksonResponseConverterFunctionTest.TEST_STRINGS)).expectComplete().verify();
        expectAggregatedJson(Flux.fromArray(JacksonResponseConverterFunctionTest.TEST_STRINGS)).expectComplete().verify();
        // Iterable with trailing headers.
        final HttpHeaders trailer = HttpHeaders.of(AsciiString.of("x-trailer"), "value");
        expectAggregatedJson(Arrays.asList(JacksonResponseConverterFunctionTest.TEST_STRINGS), JacksonResponseConverterFunctionTest.JSON_HEADERS, trailer).expectNext(trailer).expectComplete().verify();
    }

    @Test
    public void aggregatedJson_streamError() throws Exception {
        final Stream<String> stream = Stream.of(JacksonResponseConverterFunctionTest.TEST_STRINGS).map(( s) -> {
            throw new AnticipatedException();
        });
        final HttpResponse response = JacksonResponseConverterFunctionTest.function.convertResponse(JacksonResponseConverterFunctionTest.ctx, JacksonResponseConverterFunctionTest.JSON_HEADERS, stream, JacksonResponseConverterFunctionTest.DEFAULT_TRAILING_HEADERS);
        StepVerifier.create(response).expectError(AnticipatedException.class).verify();
    }

    @Test
    public void aggregatedJson_otherTypes() throws Exception {
        StepVerifier.create(JacksonResponseConverterFunctionTest.function.convertResponse(JacksonResponseConverterFunctionTest.ctx, JacksonResponseConverterFunctionTest.JSON_HEADERS, "abc", JacksonResponseConverterFunctionTest.DEFAULT_TRAILING_HEADERS)).expectNext(JacksonResponseConverterFunctionTest.JSON_HEADERS).expectNext(HttpData.ofUtf8("\"abc\"")).expectComplete().verify();
        StepVerifier.create(JacksonResponseConverterFunctionTest.function.convertResponse(JacksonResponseConverterFunctionTest.ctx, JacksonResponseConverterFunctionTest.JSON_HEADERS, 123, JacksonResponseConverterFunctionTest.DEFAULT_TRAILING_HEADERS)).expectNext(JacksonResponseConverterFunctionTest.JSON_HEADERS).expectNext(HttpData.ofUtf8("123")).expectComplete().verify();
    }

    @Test
    public void jsonTextSequences_stream() throws Exception {
        expectJsonSeqContents(Stream.of(JacksonResponseConverterFunctionTest.TEST_STRINGS)).expectComplete().verify();
        // Parallel stream.
        expectJsonSeqContents(Stream.of(JacksonResponseConverterFunctionTest.TEST_STRINGS).parallel()).expectComplete().verify();
    }

    @Test
    public void jsonTextSequences_streamError() throws Exception {
        final Stream<String> stream = Stream.of(JacksonResponseConverterFunctionTest.TEST_STRINGS).map(( s) -> {
            throw new AnticipatedException();
        });
        final HttpResponse response = JacksonResponseConverterFunctionTest.function.convertResponse(JacksonResponseConverterFunctionTest.ctx, JacksonResponseConverterFunctionTest.JSON_SEQ_HEADERS, stream, JacksonResponseConverterFunctionTest.DEFAULT_TRAILING_HEADERS);
        StepVerifier.create(response).expectError(AnticipatedException.class).verify();
    }

    @Test
    public void jsonTextSequences_publisher() throws Exception {
        expectJsonSeqContents(Flux.fromArray(JacksonResponseConverterFunctionTest.TEST_STRINGS)).expectComplete().verify();
        // With trailing headers.
        final HttpHeaders trailer = HttpHeaders.of(AsciiString.of("x-trailer"), "value");
        expectJsonSeqContents(Flux.fromArray(JacksonResponseConverterFunctionTest.TEST_STRINGS), JacksonResponseConverterFunctionTest.JSON_SEQ_HEADERS, trailer).expectNext(trailer).expectComplete().verify();
    }

    @Test
    public void jsonTextSequences_publisherError() throws Exception {
        StepVerifier.create(Mono.error(new AnticipatedException())).expectError(AnticipatedException.class).verify();
        final Flux<String> publisher = Flux.concat(Flux.fromArray(JacksonResponseConverterFunctionTest.TEST_STRINGS), Mono.error(new AnticipatedException()));
        expectJsonSeqContents(publisher).expectError(AnticipatedException.class).verify();
    }

    @Test
    public void jsonTextSequences_otherTypes() throws Exception {
        StepVerifier.create(JacksonResponseConverterFunctionTest.function.convertResponse(JacksonResponseConverterFunctionTest.ctx, JacksonResponseConverterFunctionTest.JSON_SEQ_HEADERS, "abc", JacksonResponseConverterFunctionTest.DEFAULT_TRAILING_HEADERS)).expectNext(JacksonResponseConverterFunctionTest.JSON_SEQ_HEADERS).expectNext(HttpData.of(new byte[]{ JacksonResponseConverterFunctionTest.RECORD_SEPARATOR, '\"', 'a', 'b', 'c', '\"', JacksonResponseConverterFunctionTest.LINE_FEED })).expectComplete().verify();
        StepVerifier.create(JacksonResponseConverterFunctionTest.function.convertResponse(JacksonResponseConverterFunctionTest.ctx, JacksonResponseConverterFunctionTest.JSON_SEQ_HEADERS, 123, JacksonResponseConverterFunctionTest.DEFAULT_TRAILING_HEADERS)).expectNext(JacksonResponseConverterFunctionTest.JSON_SEQ_HEADERS).expectNext(HttpData.of(new byte[]{ JacksonResponseConverterFunctionTest.RECORD_SEPARATOR, '1', '2', '3', JacksonResponseConverterFunctionTest.LINE_FEED })).expectComplete().verify();
    }
}

