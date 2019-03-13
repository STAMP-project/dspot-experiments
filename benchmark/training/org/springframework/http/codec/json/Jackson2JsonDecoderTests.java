/**
 * Copyright 2002-2019 the original author or authors.
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


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoderTestCase;
import org.springframework.core.codec.CodecException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.Pojo;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link Jackson2JsonDecoder}.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 */
public class Jackson2JsonDecoderTests extends AbstractDecoderTestCase<Jackson2JsonDecoder.Jackson2JsonDecoder> {
    private Pojo pojo1 = new Pojo("f1", "b1");

    private Pojo pojo2 = new Pojo("f2", "b2");

    public Jackson2JsonDecoderTests() {
        super(new Jackson2JsonDecoder.Jackson2JsonDecoder());
    }

    @Override
    @Test
    public void canDecode() {
        Assert.assertTrue(decoder.canDecode(forClass(Pojo.class), APPLICATION_JSON));
        Assert.assertTrue(decoder.canDecode(forClass(Pojo.class), APPLICATION_JSON_UTF8));
        Assert.assertTrue(decoder.canDecode(forClass(Pojo.class), APPLICATION_STREAM_JSON));
        Assert.assertTrue(decoder.canDecode(forClass(Pojo.class), null));
        Assert.assertFalse(decoder.canDecode(forClass(String.class), null));
        Assert.assertFalse(decoder.canDecode(forClass(Pojo.class), APPLICATION_XML));
    }

    // SPR-15866
    @Test
    public void canDecodeWithProvidedMimeType() {
        MimeType textJavascript = new MimeType("text", "javascript", StandardCharsets.UTF_8);
        Jackson2JsonDecoder.Jackson2JsonDecoder decoder = new Jackson2JsonDecoder.Jackson2JsonDecoder(new ObjectMapper(), textJavascript);
        Assert.assertEquals(Collections.singletonList(textJavascript), decoder.getDecodableMimeTypes());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void decodableMimeTypesIsImmutable() {
        MimeType textJavascript = new MimeType("text", "javascript", StandardCharsets.UTF_8);
        Jackson2JsonDecoder.Jackson2JsonDecoder decoder = new Jackson2JsonDecoder.Jackson2JsonDecoder(new ObjectMapper(), textJavascript);
        decoder.getMimeTypes().add(new MimeType("text", "ecmascript"));
    }

    @Override
    @Test
    public void decode() {
        Flux<DataBuffer> input = Flux.concat(stringBuffer("[{\"bar\":\"b1\",\"foo\":\"f1\"},"), stringBuffer("{\"bar\":\"b2\",\"foo\":\"f2\"}]"));
        testDecodeAll(input, Pojo.class, ( step) -> step.expectNext(pojo1).expectNext(pojo2).verifyComplete());
    }

    @Test
    public void decodeEmptyArrayToFlux() {
        Flux<DataBuffer> input = Flux.from(stringBuffer("[]"));
        testDecode(input, Pojo.class, ( step) -> step.verifyComplete());
    }

    @Test
    public void fieldLevelJsonView() {
        Flux<DataBuffer> input = Flux.from(stringBuffer("{\"withView1\" : \"with\", \"withView2\" : \"with\", \"withoutView\" : \"without\"}"));
        ResolvableType elementType = forClass(JacksonViewBean.class);
        Map<String, Object> hints = Collections.singletonMap(JSON_VIEW_HINT, JacksonViewBean.MyJacksonView1.class);
        testDecode(input, elementType, ( step) -> step.consumeNextWith(( o) -> {
            JacksonViewBean.JacksonViewBean b = ((JacksonViewBean.JacksonViewBean) (o));
            assertEquals("with", b.getWithView1());
            assertNull(b.getWithView2());
            assertNull(b.getWithoutView());
        }), null, hints);
    }

    @Test
    public void classLevelJsonView() {
        Flux<DataBuffer> input = Flux.from(stringBuffer("{\"withView1\" : \"with\", \"withView2\" : \"with\", \"withoutView\" : \"without\"}"));
        ResolvableType elementType = forClass(JacksonViewBean.class);
        Map<String, Object> hints = Collections.singletonMap(JSON_VIEW_HINT, JacksonViewBean.MyJacksonView3.class);
        testDecode(input, elementType, ( step) -> step.consumeNextWith(( o) -> {
            JacksonViewBean.JacksonViewBean b = ((JacksonViewBean.JacksonViewBean) (o));
            assertEquals("without", b.getWithoutView());
            assertNull(b.getWithView1());
            assertNull(b.getWithView2());
        }).verifyComplete(), null, hints);
    }

    @Test
    public void invalidData() {
        Flux<DataBuffer> input = Flux.from(stringBuffer("{\"foofoo\": \"foofoo\", \"barbar\": \"barbar\""));
        testDecode(input, Pojo.class, ( step) -> step.verifyError(.class));
    }

    // gh-22042
    @Test
    public void decodeWithNullLiteral() {
        Flux<Object> result = this.decoder.decode(Flux.concat(stringBuffer("null")), ResolvableType.forType(Pojo.class), MediaType.APPLICATION_JSON, Collections.emptyMap());
        StepVerifier.create(result).expectComplete().verify();
    }

    @Test
    public void noDefaultConstructor() {
        Flux<DataBuffer> input = Flux.from(stringBuffer("{\"property1\":\"foo\",\"property2\":\"bar\"}"));
        ResolvableType elementType = forClass(Jackson2JsonDecoderTests.BeanWithNoDefaultConstructor.class);
        Flux<Object> flux = new Jackson2JsonDecoder.Jackson2JsonDecoder().decode(input, elementType, null, Collections.emptyMap());
        StepVerifier.create(flux).verifyError(CodecException.class);
    }

    // SPR-15975
    @Test
    public void customDeserializer() {
        Mono<DataBuffer> input = stringBuffer("{\"test\": 1}");
        testDecode(input, Jackson2JsonDecoderTests.TestObject.class, ( step) -> step.consumeNextWith(( o) -> assertEquals(1, o.getTest())).verifyComplete());
    }

    private static class BeanWithNoDefaultConstructor {
        private final String property1;

        private final String property2;

        public BeanWithNoDefaultConstructor(String property1, String property2) {
            this.property1 = property1;
            this.property2 = property2;
        }

        public String getProperty1() {
            return this.property1;
        }

        public String getProperty2() {
            return this.property2;
        }
    }

    @JsonDeserialize(using = Jackson2JsonDecoderTests.Deserializer.class)
    public static class TestObject {
        private int test;

        public int getTest() {
            return this.test;
        }

        public void setTest(int test) {
            this.test = test;
        }
    }

    public static class Deserializer extends StdDeserializer<Jackson2JsonDecoderTests.TestObject> {
        private static final long serialVersionUID = 1L;

        protected Deserializer() {
            super(Jackson2JsonDecoderTests.TestObject.class);
        }

        @Override
        public Jackson2JsonDecoderTests.TestObject deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            Jackson2JsonDecoderTests.TestObject result = new Jackson2JsonDecoderTests.TestObject();
            result.setTest(node.get("test").asInt());
            return result;
        }
    }
}

