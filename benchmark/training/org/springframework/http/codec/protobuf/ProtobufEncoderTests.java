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
package org.springframework.http.codec.protobuf;


import com.google.protobuf.Message;
import java.io.UncheckedIOException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.codec.AbstractEncoderTestCase;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.protobuf.Msg;
import org.springframework.protobuf.SecondMsg;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.core.ResolvableType.forClass;


/**
 * Unit tests for {@link ProtobufEncoder}.
 *
 * @author Sebastien Deleuze
 */
public class ProtobufEncoderTests extends AbstractEncoderTestCase<ProtobufEncoder> {
    private static final MimeType PROTOBUF_MIME_TYPE = new MimeType("application", "x-protobuf");

    private Msg msg1 = Msg.newBuilder().setFoo("Foo").setBlah(SecondMsg.newBuilder().setBlah(123).build()).build();

    private Msg msg2 = Msg.newBuilder().setFoo("Bar").setBlah(SecondMsg.newBuilder().setBlah(456).build()).build();

    public ProtobufEncoderTests() {
        super(new ProtobufEncoder());
    }

    @Override
    @Test
    public void canEncode() {
        Assert.assertTrue(this.encoder.canEncode(forClass(Msg.class), null));
        Assert.assertTrue(this.encoder.canEncode(forClass(Msg.class), ProtobufEncoderTests.PROTOBUF_MIME_TYPE));
        Assert.assertTrue(this.encoder.canEncode(forClass(Msg.class), MediaType.APPLICATION_OCTET_STREAM));
        Assert.assertFalse(this.encoder.canEncode(forClass(Msg.class), MediaType.APPLICATION_JSON));
        Assert.assertFalse(this.encoder.canEncode(forClass(Object.class), ProtobufEncoderTests.PROTOBUF_MIME_TYPE));
    }

    @Override
    @Test
    public void encode() {
        Mono<Message> input = Mono.just(this.msg1);
        testEncodeAll(input, Msg.class, ( step) -> step.consumeNextWith(( dataBuffer) -> {
            try {
                assertEquals(this.msg1, Msg.parseFrom(dataBuffer.asInputStream()));
            } catch ( ex) {
                throw new <ex>UncheckedIOException();
            } finally {
                DataBufferUtils.release(dataBuffer);
            }
        }).verifyComplete());
    }

    @Test
    public void encodeStream() {
        Flux<Message> input = Flux.just(this.msg1, this.msg2);
        testEncodeAll(input, Msg.class, ( step) -> step.consumeNextWith(expect(this.msg1)).consumeNextWith(expect(this.msg2)).verifyComplete());
    }
}

