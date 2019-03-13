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


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoderTestCase;
import org.springframework.http.MediaType;
import org.springframework.http.codec.Pojo;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.MimeType;


/**
 * Unit tests for {@link Jackson2SmileDecoder}.
 *
 * @author Sebastien Deleuze
 */
public class Jackson2SmileDecoderTests extends AbstractDecoderTestCase<Jackson2SmileDecoder> {
    private static final MimeType SMILE_MIME_TYPE = new MimeType("application", "x-jackson-smile");

    private static final MimeType STREAM_SMILE_MIME_TYPE = new MimeType("application", "stream+x-jackson-smile");

    private Pojo pojo1 = new Pojo("f1", "b1");

    private Pojo pojo2 = new Pojo("f2", "b2");

    private ObjectMapper mapper = Jackson2ObjectMapperBuilder.smile().build();

    public Jackson2SmileDecoderTests() {
        super(new Jackson2SmileDecoder());
    }

    @Override
    @Test
    public void canDecode() {
        Assert.assertTrue(decoder.canDecode(ResolvableType.forClass(Pojo.class), Jackson2SmileDecoderTests.SMILE_MIME_TYPE));
        Assert.assertTrue(decoder.canDecode(ResolvableType.forClass(Pojo.class), Jackson2SmileDecoderTests.STREAM_SMILE_MIME_TYPE));
        Assert.assertTrue(decoder.canDecode(ResolvableType.forClass(Pojo.class), null));
        Assert.assertFalse(decoder.canDecode(ResolvableType.forClass(String.class), null));
        Assert.assertFalse(decoder.canDecode(ResolvableType.forClass(Pojo.class), MediaType.APPLICATION_JSON));
    }
}

