/**
 * Copyright 2002-2018 the original author or authors.
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


import MediaType.APPLICATION_OCTET_STREAM;
import MimeTypeUtils.ALL;
import MimeTypeUtils.TEXT_HTML;
import MimeTypeUtils.TEXT_XML;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.codec.Encoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.test.MockServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link EncoderHttpMessageWriter}.
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 */
public class EncoderHttpMessageWriterTests {
    private static final Map<String, Object> NO_HINTS = Collections.emptyMap();

    private static final MediaType TEXT_PLAIN_UTF_8 = new MediaType("text", "plain", StandardCharsets.UTF_8);

    @Mock
    private Encoder<String> encoder;

    private ArgumentCaptor<MediaType> mediaTypeCaptor;

    private MockServerHttpResponse response;

    @Test
    public void getWritableMediaTypes() {
        HttpMessageWriter<?> writer = getWriter(TEXT_HTML, TEXT_XML);
        Assert.assertEquals(Arrays.asList(TEXT_HTML, TEXT_XML), writer.getWritableMediaTypes());
    }

    @Test
    public void canWrite() {
        HttpMessageWriter<?> writer = getWriter(TEXT_HTML);
        Mockito.when(this.encoder.canEncode(forClass(String.class), TEXT_HTML)).thenReturn(true);
        Assert.assertTrue(writer.canWrite(forClass(String.class), TEXT_HTML));
        Assert.assertFalse(writer.canWrite(forClass(String.class), TEXT_XML));
    }

    @Test
    public void useNegotiatedMediaType() {
        HttpMessageWriter<String> writer = getWriter(ALL);
        writer.write(Mono.just("body"), forClass(String.class), TEXT_PLAIN, this.response, EncoderHttpMessageWriterTests.NO_HINTS);
        Assert.assertEquals(TEXT_PLAIN, getHeaders().getContentType());
        Assert.assertEquals(TEXT_PLAIN, this.mediaTypeCaptor.getValue());
    }

    @Test
    public void useDefaultMediaType() {
        testDefaultMediaType(null);
        testDefaultMediaType(new MediaType("text", "*"));
        testDefaultMediaType(new MediaType("*", "*"));
        testDefaultMediaType(APPLICATION_OCTET_STREAM);
    }

    @Test
    public void useDefaultMediaTypeCharset() {
        HttpMessageWriter<String> writer = getWriter(EncoderHttpMessageWriterTests.TEXT_PLAIN_UTF_8, TEXT_HTML);
        writer.write(Mono.just("body"), forClass(String.class), TEXT_HTML, response, EncoderHttpMessageWriterTests.NO_HINTS);
        Assert.assertEquals(new MediaType("text", "html", StandardCharsets.UTF_8), getHeaders().getContentType());
        Assert.assertEquals(new MediaType("text", "html", StandardCharsets.UTF_8), this.mediaTypeCaptor.getValue());
    }

    @Test
    public void useNegotiatedMediaTypeCharset() {
        MediaType negotiatedMediaType = new MediaType("text", "html", StandardCharsets.ISO_8859_1);
        HttpMessageWriter<String> writer = getWriter(EncoderHttpMessageWriterTests.TEXT_PLAIN_UTF_8, TEXT_HTML);
        writer.write(Mono.just("body"), forClass(String.class), negotiatedMediaType, this.response, EncoderHttpMessageWriterTests.NO_HINTS);
        Assert.assertEquals(negotiatedMediaType, getHeaders().getContentType());
        Assert.assertEquals(negotiatedMediaType, this.mediaTypeCaptor.getValue());
    }

    @Test
    public void useHttpOutputMessageMediaType() {
        MediaType outputMessageMediaType = MediaType.TEXT_HTML;
        getHeaders().setContentType(outputMessageMediaType);
        HttpMessageWriter<String> writer = getWriter(EncoderHttpMessageWriterTests.TEXT_PLAIN_UTF_8, TEXT_HTML);
        writer.write(Mono.just("body"), forClass(String.class), TEXT_PLAIN, this.response, EncoderHttpMessageWriterTests.NO_HINTS);
        Assert.assertEquals(outputMessageMediaType, getHeaders().getContentType());
        Assert.assertEquals(outputMessageMediaType, this.mediaTypeCaptor.getValue());
    }

    @Test
    public void setContentLengthForMonoBody() {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DataBuffer buffer = factory.wrap("body".getBytes(StandardCharsets.UTF_8));
        HttpMessageWriter<String> writer = getWriter(Flux.just(buffer), MimeTypeUtils.TEXT_PLAIN);
        writer.write(Mono.just("body"), forClass(String.class), TEXT_PLAIN, this.response, EncoderHttpMessageWriterTests.NO_HINTS).block();
        Assert.assertEquals(4, getHeaders().getContentLength());
    }

    // SPR-17220
    @Test
    public void emptyBodyWritten() {
        HttpMessageWriter<String> writer = getWriter(MimeTypeUtils.TEXT_PLAIN);
        writer.write(Mono.empty(), forClass(String.class), TEXT_PLAIN, this.response, EncoderHttpMessageWriterTests.NO_HINTS).block();
        StepVerifier.create(this.response.getBody()).expectComplete();
        Assert.assertEquals(0, getHeaders().getContentLength());
    }
}

