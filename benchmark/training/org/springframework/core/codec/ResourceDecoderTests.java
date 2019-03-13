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
package org.springframework.core.codec;


import MimeTypeUtils.APPLICATION_JSON;
import MimeTypeUtils.TEXT_PLAIN;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class ResourceDecoderTests extends AbstractDecoderTestCase<ResourceDecoder> {
    private final byte[] fooBytes = "foo".getBytes(StandardCharsets.UTF_8);

    private final byte[] barBytes = "bar".getBytes(StandardCharsets.UTF_8);

    public ResourceDecoderTests() {
        super(new ResourceDecoder());
    }

    @Override
    @Test
    public void canDecode() {
        Assert.assertTrue(this.decoder.canDecode(ResolvableType.forClass(InputStreamResource.class), TEXT_PLAIN));
        Assert.assertTrue(this.decoder.canDecode(ResolvableType.forClass(ByteArrayResource.class), TEXT_PLAIN));
        Assert.assertTrue(this.decoder.canDecode(ResolvableType.forClass(Resource.class), TEXT_PLAIN));
        Assert.assertTrue(this.decoder.canDecode(ResolvableType.forClass(InputStreamResource.class), APPLICATION_JSON));
        Assert.assertFalse(this.decoder.canDecode(ResolvableType.forClass(Object.class), APPLICATION_JSON));
    }

    @Override
    @Test
    public void decode() {
        Flux<DataBuffer> input = Flux.concat(dataBuffer(this.fooBytes), dataBuffer(this.barBytes));
        testDecodeAll(input, Resource.class, ( step) -> step.consumeNextWith(( resource) -> {
            try {
                byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
                assertEquals("foobar", new String(bytes));
            } catch ( e) {
                fail(e.getMessage());
            }
        }).expectComplete().verify());
    }
}

