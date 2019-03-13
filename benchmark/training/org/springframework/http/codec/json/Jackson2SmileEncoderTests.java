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


import ResolvableType.NONE;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractEncoderTestCase;
import org.springframework.http.MediaType;
import org.springframework.http.codec.Pojo;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link Jackson2SmileEncoder}.
 *
 * @author Sebastien Deleuze
 */
public class Jackson2SmileEncoderTests extends AbstractEncoderTestCase<Jackson2SmileEncoder> {
    private static final MimeType SMILE_MIME_TYPE = new MimeType("application", "x-jackson-smile");

    private static final MimeType STREAM_SMILE_MIME_TYPE = new MimeType("application", "stream+x-jackson-smile");

    private final Jackson2SmileEncoder encoder = new Jackson2SmileEncoder();

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.smile().build();

    public Jackson2SmileEncoderTests() {
        super(new Jackson2SmileEncoder());
    }

    @Override
    @Test
    public void canEncode() {
        ResolvableType pojoType = ResolvableType.forClass(Pojo.class);
        Assert.assertTrue(this.encoder.canEncode(pojoType, Jackson2SmileEncoderTests.SMILE_MIME_TYPE));
        Assert.assertTrue(this.encoder.canEncode(pojoType, Jackson2SmileEncoderTests.STREAM_SMILE_MIME_TYPE));
        Assert.assertTrue(this.encoder.canEncode(pojoType, null));
        // SPR-15464
        Assert.assertTrue(this.encoder.canEncode(NONE, null));
    }

    @Test
    public void canNotEncode() {
        Assert.assertFalse(this.encoder.canEncode(ResolvableType.forClass(String.class), null));
        Assert.assertFalse(this.encoder.canEncode(ResolvableType.forClass(Pojo.class), MediaType.APPLICATION_XML));
        ResolvableType sseType = ResolvableType.forClass(ServerSentEvent.class);
        Assert.assertFalse(this.encoder.canEncode(sseType, Jackson2SmileEncoderTests.SMILE_MIME_TYPE));
    }

    @Override
    @Test
    public void encode() {
        List<Pojo> list = Arrays.asList(new Pojo("foo", "bar"), new Pojo("foofoo", "barbar"), new Pojo("foofoofoo", "barbarbar"));
        Flux<Pojo> input = Flux.fromIterable(list);
        testEncode(input, Pojo.class, ( step) -> step.consumeNextWith(expect(list, .class)));
    }

    @Test
    public void encodeError() throws Exception {
        Mono<Pojo> input = Mono.error(new InputException());
        testEncode(input, Pojo.class, ( step) -> step.expectError(.class).verify());
    }

    @Test
    public void encodeAsStream() throws Exception {
        Pojo pojo1 = new Pojo("foo", "bar");
        Pojo pojo2 = new Pojo("foofoo", "barbar");
        Pojo pojo3 = new Pojo("foofoofoo", "barbarbar");
        Flux<Pojo> input = Flux.just(pojo1, pojo2, pojo3);
        ResolvableType type = ResolvableType.forClass(Pojo.class);
        testEncodeAll(input, type, ( step) -> step.consumeNextWith(expect(pojo1, .class)).consumeNextWith(expect(pojo2, .class)).consumeNextWith(expect(pojo3, .class)).verifyComplete(), Jackson2SmileEncoderTests.STREAM_SMILE_MIME_TYPE, null);
    }
}

