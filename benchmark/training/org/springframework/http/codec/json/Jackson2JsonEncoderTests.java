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
package org.springframework.http.codec.json;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractEncoderTestCase;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.Pojo;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;


/**
 *
 *
 * @author Sebastien Deleuze
 */
public class Jackson2JsonEncoderTests extends AbstractEncoderTestCase<Jackson2JsonEncoder> {
    public Jackson2JsonEncoderTests() {
        super(new Jackson2JsonEncoder());
    }

    @Override
    @Test
    public void canEncode() {
        ResolvableType pojoType = ResolvableType.forClass(Pojo.class);
        Assert.assertTrue(this.encoder.canEncode(pojoType, MediaType.APPLICATION_JSON));
        Assert.assertTrue(this.encoder.canEncode(pojoType, MediaType.APPLICATION_JSON_UTF8));
        Assert.assertTrue(this.encoder.canEncode(pojoType, MediaType.APPLICATION_STREAM_JSON));
        Assert.assertTrue(this.encoder.canEncode(pojoType, null));
        // SPR-15464
        Assert.assertTrue(this.encoder.canEncode(ResolvableType.NONE, null));
        // SPR-15910
        Assert.assertFalse(this.encoder.canEncode(ResolvableType.forClass(Object.class), MediaType.APPLICATION_OCTET_STREAM));
    }

    // SPR-15866
    @Test
    public void canEncodeWithCustomMimeType() {
        MimeType textJavascript = new MimeType("text", "javascript", StandardCharsets.UTF_8);
        Jackson2JsonEncoder encoder = new Jackson2JsonEncoder(new ObjectMapper(), textJavascript);
        Assert.assertEquals(Collections.singletonList(textJavascript), encoder.getEncodableMimeTypes());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void encodableMimeTypesIsImmutable() {
        MimeType textJavascript = new MimeType("text", "javascript", StandardCharsets.UTF_8);
        Jackson2JsonEncoder encoder = new Jackson2JsonEncoder(new ObjectMapper(), textJavascript);
        encoder.getMimeTypes().add(new MimeType("text", "ecmascript"));
    }

    @Test
    public void canNotEncode() {
        Assert.assertFalse(this.encoder.canEncode(ResolvableType.forClass(String.class), null));
        Assert.assertFalse(this.encoder.canEncode(ResolvableType.forClass(Pojo.class), MediaType.APPLICATION_XML));
        ResolvableType sseType = ResolvableType.forClass(ServerSentEvent.class);
        Assert.assertFalse(this.encoder.canEncode(sseType, MediaType.APPLICATION_JSON));
    }

    @Test
    public void encodeNonStream() {
        Flux<Pojo> input = Flux.just(new Pojo("foo", "bar"), new Pojo("foofoo", "barbar"), new Pojo("foofoofoo", "barbarbar"));
        testEncode(input, Pojo.class, ( step) -> step.consumeNextWith(expectString(("[" + (("{\"foo\":\"foo\",\"bar\":\"bar\"}," + "{\"foo\":\"foofoo\",\"bar\":\"barbar\"},") + "{\"foo\":\"foofoofoo\",\"bar\":\"barbarbar\"}]"))).andThen(DataBufferUtils::release)).verifyComplete());
    }

    @Test
    public void encodeWithType() {
        Flux<Jackson2JsonEncoderTests.ParentClass> input = Flux.just(new Jackson2JsonEncoderTests.Foo(), new Jackson2JsonEncoderTests.Bar());
        testEncode(input, Jackson2JsonEncoderTests.ParentClass.class, ( step) -> step.consumeNextWith(expectString("[{\"type\":\"foo\"},{\"type\":\"bar\"}]").andThen(DataBufferUtils::release)).verifyComplete());
    }

    // SPR-15727
    @Test
    public void encodeAsStreamWithCustomStreamingType() {
        MediaType fooMediaType = new MediaType("application", "foo");
        MediaType barMediaType = new MediaType("application", "bar");
        this.encoder.setStreamingMediaTypes(Arrays.asList(fooMediaType, barMediaType));
        Flux<Pojo> input = Flux.just(new Pojo("foo", "bar"), new Pojo("foofoo", "barbar"), new Pojo("foofoofoo", "barbarbar"));
        testEncode(input, ResolvableType.forClass(Pojo.class), ( step) -> step.consumeNextWith(expectString("{\"foo\":\"foo\",\"bar\":\"bar\"}\n").andThen(DataBufferUtils::release)).consumeNextWith(expectString("{\"foo\":\"foofoo\",\"bar\":\"barbar\"}\n").andThen(DataBufferUtils::release)).consumeNextWith(expectString("{\"foo\":\"foofoofoo\",\"bar\":\"barbarbar\"}\n").andThen(DataBufferUtils::release)).verifyComplete(), barMediaType, null);
    }

    @Test
    public void fieldLevelJsonView() {
        JacksonViewBean bean = new JacksonViewBean();
        bean.setWithView1("with");
        bean.setWithView2("with");
        bean.setWithoutView("without");
        Mono<JacksonViewBean> input = Mono.just(bean);
        ResolvableType type = ResolvableType.forClass(JacksonViewBean.class);
        Map<String, Object> hints = Collections.singletonMap(Jackson2JsonEncoder.JSON_VIEW_HINT, JacksonViewBean.MyJacksonView1.class);
        testEncode(input, type, ( step) -> step.consumeNextWith(expectString("{\"withView1\":\"with\"}").andThen(DataBufferUtils::release)).verifyComplete(), null, hints);
    }

    @Test
    public void classLevelJsonView() {
        JacksonViewBean bean = new JacksonViewBean();
        bean.setWithView1("with");
        bean.setWithView2("with");
        bean.setWithoutView("without");
        Mono<JacksonViewBean> input = Mono.just(bean);
        ResolvableType type = ResolvableType.forClass(JacksonViewBean.class);
        Map<String, Object> hints = Collections.singletonMap(Jackson2JsonEncoder.JSON_VIEW_HINT, JacksonViewBean.MyJacksonView3.class);
        testEncode(input, type, ( step) -> step.consumeNextWith(expectString("{\"withoutView\":\"without\"}").andThen(DataBufferUtils::release)).verifyComplete(), null, hints);
    }

    @JsonTypeInfo(use = NAME, property = "type")
    private static class ParentClass {}

    @JsonTypeName("foo")
    private static class Foo extends Jackson2JsonEncoderTests.ParentClass {}

    @JsonTypeName("bar")
    private static class Bar extends Jackson2JsonEncoderTests.ParentClass {}
}

