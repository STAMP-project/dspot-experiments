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
package org.springframework.http.codec.multipart;


import MediaType.APPLICATION_FORM_URLENCODED;
import MediaType.APPLICATION_JSON_UTF8;
import MediaType.IMAGE_JPEG;
import MediaType.MULTIPART_FORM_DATA;
import MediaType.TEXT_PLAIN;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.AbstractLeakCheckingTestCase;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.mock.http.server.reactive.test.MockServerHttpResponse;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;


/**
 *
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 */
public class MultipartHttpMessageWriterTests extends AbstractLeakCheckingTestCase {
    private final MultipartHttpMessageWriter writer = new MultipartHttpMessageWriter(ClientCodecConfigurer.create().getWriters());

    private MockServerHttpResponse response;

    @Test
    public void canWrite() {
        Assert.assertTrue(this.writer.canWrite(ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, Object.class), MULTIPART_FORM_DATA));
        Assert.assertTrue(this.writer.canWrite(ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, String.class), MULTIPART_FORM_DATA));
        Assert.assertFalse(this.writer.canWrite(ResolvableType.forClassWithGenerics(Map.class, String.class, Object.class), MULTIPART_FORM_DATA));
        Assert.assertTrue(this.writer.canWrite(ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, Object.class), APPLICATION_FORM_URLENCODED));
    }

    @Test
    public void writeMultipart() throws Exception {
        Resource logo = new ClassPathResource("/org/springframework/http/converter/logo.jpg");
        Resource utf8 = new ClassPathResource("/org/springframework/http/converter/logo.jpg") {
            @Override
            public String getFilename() {
                // SPR-12108
                return "Hall\u00f6le.jpg";
            }
        };
        Publisher<String> publisher = Flux.just("foo", "bar", "baz");
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("name 1", "value 1");
        bodyBuilder.part("name 2", "value 2+1");
        bodyBuilder.part("name 2", "value 2+2");
        bodyBuilder.part("logo", logo);
        bodyBuilder.part("utf8", utf8);
        bodyBuilder.part("json", new MultipartHttpMessageWriterTests.Foo("bar"), APPLICATION_JSON_UTF8);
        bodyBuilder.asyncPart("publisher", publisher, String.class);
        Mono<MultiValueMap<String, HttpEntity<?>>> result = Mono.just(bodyBuilder.build());
        Map<String, Object> hints = Collections.emptyMap();
        this.writer.write(result, null, MULTIPART_FORM_DATA, this.response, hints).block(Duration.ofSeconds(5));
        MultiValueMap<String, Part> requestParts = parse(hints);
        Assert.assertEquals(6, requestParts.size());
        Part part = requestParts.getFirst("name 1");
        Assert.assertTrue((part instanceof FormFieldPart));
        Assert.assertEquals("name 1", part.name());
        Assert.assertEquals("value 1", value());
        List<Part> parts2 = requestParts.get("name 2");
        Assert.assertEquals(2, parts2.size());
        part = parts2.get(0);
        Assert.assertTrue((part instanceof FormFieldPart));
        Assert.assertEquals("name 2", part.name());
        Assert.assertEquals("value 2+1", value());
        part = parts2.get(1);
        Assert.assertTrue((part instanceof FormFieldPart));
        Assert.assertEquals("name 2", part.name());
        Assert.assertEquals("value 2+2", value());
        part = requestParts.getFirst("logo");
        Assert.assertTrue((part instanceof FilePart));
        Assert.assertEquals("logo", part.name());
        Assert.assertEquals("logo.jpg", filename());
        Assert.assertEquals(IMAGE_JPEG, part.headers().getContentType());
        Assert.assertEquals(logo.getFile().length(), part.headers().getContentLength());
        part = requestParts.getFirst("utf8");
        Assert.assertTrue((part instanceof FilePart));
        Assert.assertEquals("utf8", part.name());
        Assert.assertEquals("Hall\u00f6le.jpg", filename());
        Assert.assertEquals(IMAGE_JPEG, part.headers().getContentType());
        Assert.assertEquals(utf8.getFile().length(), part.headers().getContentLength());
        part = requestParts.getFirst("json");
        Assert.assertEquals("json", part.name());
        Assert.assertEquals(APPLICATION_JSON_UTF8, part.headers().getContentType());
        String value = StringDecoder.textPlainOnly(false).decodeToMono(part.content(), ResolvableType.forClass(String.class), TEXT_PLAIN, Collections.emptyMap()).block(Duration.ZERO);
        Assert.assertEquals("{\"bar\":\"bar\"}", value);
        part = requestParts.getFirst("publisher");
        Assert.assertEquals("publisher", part.name());
        value = StringDecoder.textPlainOnly(false).decodeToMono(part.content(), ResolvableType.forClass(String.class), TEXT_PLAIN, Collections.emptyMap()).block(Duration.ZERO);
        Assert.assertEquals("foobarbaz", value);
    }

    // SPR-16402
    @Test
    public void singleSubscriberWithResource() throws IOException {
        UnicastProcessor<Resource> processor = UnicastProcessor.create();
        Resource logo = new ClassPathResource("/org/springframework/http/converter/logo.jpg");
        Mono.just(logo).subscribe(processor);
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.asyncPart("logo", processor, Resource.class);
        Mono<MultiValueMap<String, HttpEntity<?>>> result = Mono.just(bodyBuilder.build());
        Map<String, Object> hints = Collections.emptyMap();
        this.writer.write(result, null, MULTIPART_FORM_DATA, this.response, hints).block();
        MultiValueMap<String, Part> requestParts = parse(hints);
        Assert.assertEquals(1, requestParts.size());
        Part part = requestParts.getFirst("logo");
        Assert.assertEquals("logo", part.name());
        Assert.assertTrue((part instanceof FilePart));
        Assert.assertEquals("logo.jpg", filename());
        Assert.assertEquals(IMAGE_JPEG, part.headers().getContentType());
        Assert.assertEquals(logo.getFile().length(), part.headers().getContentLength());
    }

    // SPR-16402
    @Test
    public void singleSubscriberWithStrings() {
        UnicastProcessor<String> processor = UnicastProcessor.create();
        Flux.just("foo", "bar", "baz").subscribe(processor);
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.asyncPart("name", processor, String.class);
        Mono<MultiValueMap<String, HttpEntity<?>>> result = Mono.just(bodyBuilder.build());
        Map<String, Object> hints = Collections.emptyMap();
        this.writer.write(result, null, MULTIPART_FORM_DATA, this.response, hints).block();
    }

    // SPR-16376
    @Test
    public void customContentDisposition() throws IOException {
        Resource logo = new ClassPathResource("/org/springframework/http/converter/logo.jpg");
        Flux<DataBuffer> buffers = DataBufferUtils.read(logo, new DefaultDataBufferFactory(), 1024);
        long contentLength = logo.contentLength();
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("resource", logo).headers(( h) -> h.setContentDispositionFormData("resource", "spring.jpg"));
        bodyBuilder.asyncPart("buffers", buffers, DataBuffer.class).headers(( h) -> {
            h.setContentDispositionFormData("buffers", "buffers.jpg");
            h.setContentType(MediaType.IMAGE_JPEG);
            h.setContentLength(contentLength);
        });
        MultiValueMap<String, HttpEntity<?>> multipartData = bodyBuilder.build();
        Map<String, Object> hints = Collections.emptyMap();
        this.writer.write(Mono.just(multipartData), null, MULTIPART_FORM_DATA, this.response, hints).block();
        MultiValueMap<String, Part> requestParts = parse(hints);
        Assert.assertEquals(2, requestParts.size());
        Part part = requestParts.getFirst("resource");
        Assert.assertTrue((part instanceof FilePart));
        Assert.assertEquals("spring.jpg", filename());
        Assert.assertEquals(logo.getFile().length(), part.headers().getContentLength());
        part = requestParts.getFirst("buffers");
        Assert.assertTrue((part instanceof FilePart));
        Assert.assertEquals("buffers.jpg", filename());
        Assert.assertEquals(logo.getFile().length(), part.headers().getContentLength());
    }

    private class Foo {
        private String bar;

        public Foo() {
        }

        public Foo(String bar) {
            this.bar = bar;
        }

        public String getBar() {
            return this.bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }
    }
}

