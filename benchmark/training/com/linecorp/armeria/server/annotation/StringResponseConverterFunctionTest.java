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


import HttpHeaderNames.CONTENT_LENGTH;
import HttpStatus.OK;
import MediaType.PLAIN_TEXT_UTF_8;
import com.google.common.collect.ImmutableList;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.internal.FallthroughException;
import com.linecorp.armeria.server.ServiceRequestContext;
import java.nio.charset.Charset;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;


public class StringResponseConverterFunctionTest {
    private static final ResponseConverterFunction function = new StringResponseConverterFunction();

    private static final ServiceRequestContext ctx = Mockito.mock(ServiceRequestContext.class);

    private static final HttpHeaders PLAIN_TEXT_HEADERS = HttpHeaders.of(OK).contentType(PLAIN_TEXT_UTF_8).addInt(CONTENT_LENGTH, 6);

    private static final HttpHeaders DEFAULT_TRAILING_HEADERS = HttpHeaders.EMPTY_HEADERS;

    @Test
    public void aggregatedText() throws Exception {
        final List<String> contents = ImmutableList.of("foo", ",", "bar", ",", "baz");
        for (final Object result : // publisher
        // stream
        ImmutableList.of(Flux.fromIterable(contents), contents.stream(), contents)) {
            // iterable
            StepVerifier.create(StringResponseConverterFunctionTest.from(result)).expectNext(StringResponseConverterFunctionTest.PLAIN_TEXT_HEADERS).expectNext(HttpData.of("foo,bar,baz".getBytes())).expectComplete().verify();
        }
    }

    @Test
    public void withoutContentType() throws Exception {
        StepVerifier.create(StringResponseConverterFunctionTest.function.convertResponse(StringResponseConverterFunctionTest.ctx, HttpHeaders.of(OK), "foo", StringResponseConverterFunctionTest.DEFAULT_TRAILING_HEADERS)).expectNext(HttpHeaders.of(OK).contentType(PLAIN_TEXT_UTF_8).addInt(CONTENT_LENGTH, 3)).expectNext(HttpData.ofUtf8("foo")).expectComplete().verify();
        assertThatThrownBy(() -> function.convertResponse(ctx, HttpHeaders.of(HttpStatus.OK), ImmutableList.of(), DEFAULT_TRAILING_HEADERS)).isInstanceOf(FallthroughException.class);
    }

    @Test
    public void charset() throws Exception {
        final HttpHeaders headers = HttpHeaders.of(OK).contentType(com.linecorp.armeria.common.MediaType.parse("text/plain; charset=euc-kr"));
        StepVerifier.create(StringResponseConverterFunctionTest.function.convertResponse(StringResponseConverterFunctionTest.ctx, headers, "??", StringResponseConverterFunctionTest.DEFAULT_TRAILING_HEADERS)).expectNext(headers).expectNext(HttpData.of(Charset.forName("euc-kr"), "??")).expectComplete().verify();
    }
}

