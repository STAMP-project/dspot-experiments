/**
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.http.codec;


import HttpHeaders.ACCEPT_RANGES;
import HttpHeaders.CONTENT_RANGE;
import HttpHeaders.RANGE;
import HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
import MimeTypeUtils.ALL;
import MimeTypeUtils.APPLICATION_OCTET_STREAM;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.test.MockServerHttpResponse;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link ResourceHttpMessageWriter}.
 *
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 */
public class ResourceHttpMessageWriterTests {
    private static final Map<String, Object> HINTS = Collections.emptyMap();

    private final ResourceHttpMessageWriter writer = new ResourceHttpMessageWriter();

    private final MockServerHttpResponse response = new MockServerHttpResponse();

    private final Mono<Resource> input = Mono.just(new ByteArrayResource("Spring Framework test resource content.".getBytes(StandardCharsets.UTF_8)));

    @Test
    public void getWritableMediaTypes() throws Exception {
        Assert.assertThat(this.writer.getWritableMediaTypes(), Matchers.containsInAnyOrder(APPLICATION_OCTET_STREAM, ALL));
    }

    @Test
    public void writeResource() throws Exception {
        testWrite(MockServerHttpRequest.get("/").build());
        Assert.assertThat(getHeaders().getContentType(), Matchers.is(MediaType.TEXT_PLAIN));
        Assert.assertThat(getHeaders().getContentLength(), Matchers.is(39L));
        Assert.assertThat(getHeaders().getFirst(ACCEPT_RANGES), Matchers.is("bytes"));
        String content = "Spring Framework test resource content.";
        StepVerifier.create(this.response.getBodyAsString()).expectNext(content).expectComplete().verify();
    }

    @Test
    public void writeSingleRegion() throws Exception {
        testWrite(MockServerHttpRequest.get("/").range(ResourceHttpMessageWriterTests.of(0, 5)).build());
        Assert.assertThat(getHeaders().getContentType(), Matchers.is(MediaType.TEXT_PLAIN));
        Assert.assertThat(getHeaders().getFirst(CONTENT_RANGE), Matchers.is("bytes 0-5/39"));
        Assert.assertThat(getHeaders().getContentLength(), Matchers.is(6L));
        StepVerifier.create(this.response.getBodyAsString()).expectNext("Spring").expectComplete().verify();
    }

    @Test
    public void writeMultipleRegions() throws Exception {
        testWrite(MockServerHttpRequest.get("/").range(ResourceHttpMessageWriterTests.of(0, 5), ResourceHttpMessageWriterTests.of(7, 15), ResourceHttpMessageWriterTests.of(17, 20), ResourceHttpMessageWriterTests.of(22, 38)).build());
        org.springframework.http.HttpHeaders headers = this.response.getHeaders();
        String contentType = headers.getContentType().toString();
        String boundary = contentType.substring(30);
        Assert.assertThat(contentType, Matchers.startsWith("multipart/byteranges;boundary="));
        StepVerifier.create(this.response.getBodyAsString()).consumeNextWith(( content) -> {
            String[] actualRanges = StringUtils.tokenizeToStringArray(content, "\r\n", false, true);
            String[] expected = new String[]{ "--" + boundary, "Content-Type: text/plain", "Content-Range: bytes 0-5/39", "Spring", "--" + boundary, "Content-Type: text/plain", "Content-Range: bytes 7-15/39", "Framework", "--" + boundary, "Content-Type: text/plain", "Content-Range: bytes 17-20/39", "test", "--" + boundary, "Content-Type: text/plain", "Content-Range: bytes 22-38/39", "resource content.", ("--" + boundary) + "--" };
            assertArrayEquals(expected, actualRanges);
        }).expectComplete().verify();
    }

    @Test
    public void invalidRange() throws Exception {
        testWrite(MockServerHttpRequest.get("/").header(RANGE, "invalid").build());
        Assert.assertThat(getHeaders().getFirst(ACCEPT_RANGES), Matchers.is("bytes"));
        Assert.assertThat(getStatusCode(), Matchers.is(REQUESTED_RANGE_NOT_SATISFIABLE));
    }
}

