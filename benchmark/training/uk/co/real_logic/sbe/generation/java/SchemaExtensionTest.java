/**
 * Copyright 2013-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.sbe.generation.java;


import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.generation.StringWriterOutputManager;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import uk.co.real_logic.sbe.ir.Ir;


public class SchemaExtensionTest {
    private static final Class<?> BUFFER_CLASS = MutableDirectBuffer.class;

    private static final String BUFFER_NAME = SchemaExtensionTest.BUFFER_CLASS.getName();

    private static final Class<DirectBuffer> READ_ONLY_BUFFER_CLASS = DirectBuffer.class;

    private static final String READ_ONLY_BUFFER_NAME = SchemaExtensionTest.READ_ONLY_BUFFER_CLASS.getName();

    private final StringWriterOutputManager outputManager = new StringWriterOutputManager();

    private Ir ir;

    @SuppressWarnings("MethodLength")
    @Test
    public void testMessage1() throws Exception {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[4096]);
        {
            // Encode
            final Object encoder = SchemaExtensionTest.wrap(buffer, compile("TestMessage1Encoder").getConstructor().newInstance());
            ReflectionUtil.set(encoder, "tag1", int.class, 100);
            ReflectionUtil.set(encoder, "tag2", int.class, 200);
            final Object compositeEncoder = encoder.getClass().getMethod("tag3").invoke(encoder);
            ReflectionUtil.set(compositeEncoder, "value", int.class, 300);
            final Object enumConstant = getAEnumConstant(encoder, "AEnum", 1);
            ReflectionUtil.set(encoder, "tag4", enumConstant.getClass(), enumConstant);
            final Object setEncoder = encoder.getClass().getMethod("tag5").invoke(encoder);
            ReflectionUtil.set(setEncoder, "firstChoice", boolean.class, false);
            ReflectionUtil.set(setEncoder, "secondChoice", boolean.class, true);
            ReflectionUtil.set(encoder, "tag6", String.class, "This is some variable length data");
        }
        {
            // Decode version 0
            final Object decoderVersion0 = getMessage1Decoder(buffer, 4, 0);
            Assert.assertEquals(100, ReflectionUtil.get(decoderVersion0, "tag1"));
            Assert.assertEquals(Integer.MIN_VALUE, ReflectionUtil.get(decoderVersion0, "tag2"));
            Assert.assertNull(ReflectionUtil.get(decoderVersion0, "tag3"));
            Assert.assertThat(ReflectionUtil.get(decoderVersion0, "tag4").toString(), CoreMatchers.is("NULL_VAL"));
            Assert.assertNull(ReflectionUtil.get(decoderVersion0, "tag5"));
            final StringBuilder tag6Value = new StringBuilder();
            ReflectionUtil.get(decoderVersion0, "tag6", tag6Value);
            Assert.assertThat(tag6Value.length(), CoreMatchers.is(0));
            Assert.assertEquals(0, decoderVersion0.getClass().getMethod("tag1SinceVersion").invoke(null));
            Assert.assertEquals(1, decoderVersion0.getClass().getMethod("tag2SinceVersion").invoke(null));
            Assert.assertEquals(2, decoderVersion0.getClass().getMethod("tag3SinceVersion").invoke(null));
            Assert.assertEquals(3, decoderVersion0.getClass().getMethod("tag4SinceVersion").invoke(null));
            Assert.assertEquals(4, decoderVersion0.getClass().getMethod("tag5SinceVersion").invoke(null));
            Assert.assertEquals(5, decoderVersion0.getClass().getMethod("tag6SinceVersion").invoke(null));
        }
        {
            // Decode version 1
            final Object decoderVersion1 = getMessage1Decoder(buffer, 8, 1);
            Assert.assertEquals(100, ReflectionUtil.get(decoderVersion1, "tag1"));
            Assert.assertEquals(200, ReflectionUtil.get(decoderVersion1, "tag2"));
            Assert.assertNull(ReflectionUtil.get(decoderVersion1, "tag3"));
            Assert.assertThat(ReflectionUtil.get(decoderVersion1, "tag4").toString(), CoreMatchers.is("NULL_VAL"));
            Assert.assertNull(ReflectionUtil.get(decoderVersion1, "tag5"));
            final StringBuilder tag6Value = new StringBuilder();
            ReflectionUtil.get(decoderVersion1, "tag6", tag6Value);
            Assert.assertThat(tag6Value.length(), CoreMatchers.is(0));
        }
        {
            // Decode version 2
            final Object decoderVersion2 = getMessage1Decoder(buffer, 8, 2);
            Assert.assertEquals(100, ReflectionUtil.get(decoderVersion2, "tag1"));
            Assert.assertEquals(200, ReflectionUtil.get(decoderVersion2, "tag2"));
            final Object compositeDecoder2 = ReflectionUtil.get(decoderVersion2, "tag3");
            Assert.assertNotNull(compositeDecoder2);
            Assert.assertEquals(300, ReflectionUtil.get(compositeDecoder2, "value"));
            Assert.assertThat(ReflectionUtil.get(decoderVersion2, "tag4").toString(), CoreMatchers.is("NULL_VAL"));
            Assert.assertNull(ReflectionUtil.get(decoderVersion2, "tag5"));
            final StringBuilder tag6Value = new StringBuilder();
            ReflectionUtil.get(decoderVersion2, "tag6", tag6Value);
            Assert.assertThat(tag6Value.length(), CoreMatchers.is(0));
        }
        {
            // Decode version 3
            final Object decoderVersion3 = getMessage1Decoder(buffer, 12, 3);
            Assert.assertEquals(100, ReflectionUtil.get(decoderVersion3, "tag1"));
            Assert.assertEquals(200, ReflectionUtil.get(decoderVersion3, "tag2"));
            final Object compositeDecoder3 = ReflectionUtil.get(decoderVersion3, "tag3");
            Assert.assertNotNull(compositeDecoder3);
            Assert.assertEquals(300, ReflectionUtil.get(compositeDecoder3, "value"));
            final Object enumConstant = getAEnumConstant(decoderVersion3, "AEnum", 1);
            Assert.assertEquals(enumConstant, ReflectionUtil.get(decoderVersion3, "tag4"));
            Assert.assertNull(ReflectionUtil.get(decoderVersion3, "tag5"));
            final StringBuilder tag6Value = new StringBuilder();
            ReflectionUtil.get(decoderVersion3, "tag6", tag6Value);
            Assert.assertThat(tag6Value.length(), CoreMatchers.is(0));
        }
        {
            // Decode version 4
            final Object decoderVersion4 = getMessage1Decoder(buffer, 12, 4);
            Assert.assertEquals(100, ReflectionUtil.get(decoderVersion4, "tag1"));
            Assert.assertEquals(200, ReflectionUtil.get(decoderVersion4, "tag2"));
            final Object compositeDecoder4 = ReflectionUtil.get(decoderVersion4, "tag3");
            Assert.assertNotNull(compositeDecoder4);
            Assert.assertEquals(300, ReflectionUtil.get(compositeDecoder4, "value"));
            final Object enumConstant = getAEnumConstant(decoderVersion4, "AEnum", 1);
            Assert.assertEquals(enumConstant, ReflectionUtil.get(decoderVersion4, "tag4"));
            final Object setDecoder = ReflectionUtil.get(decoderVersion4, "tag5");
            Assert.assertNotNull(setDecoder);
            Assert.assertEquals(false, ReflectionUtil.get(setDecoder, "firstChoice"));
            Assert.assertEquals(true, ReflectionUtil.get(setDecoder, "secondChoice"));
            final StringBuilder tag6Value = new StringBuilder();
            ReflectionUtil.get(decoderVersion4, "tag6", tag6Value);
            Assert.assertThat(tag6Value.length(), CoreMatchers.is(0));
        }
        {
            // Decode version 5
            final Object decoderVersion5 = getMessage1Decoder(buffer, 14, 5);
            Assert.assertEquals(100, ReflectionUtil.get(decoderVersion5, "tag1"));
            Assert.assertEquals(200, ReflectionUtil.get(decoderVersion5, "tag2"));
            final Object compositeDecoder4 = ReflectionUtil.get(decoderVersion5, "tag3");
            Assert.assertNotNull(compositeDecoder4);
            Assert.assertEquals(300, ReflectionUtil.get(compositeDecoder4, "value"));
            final Object enumConstant = getAEnumConstant(decoderVersion5, "AEnum", 1);
            Assert.assertEquals(enumConstant, ReflectionUtil.get(decoderVersion5, "tag4"));
            final Object setDecoder = ReflectionUtil.get(decoderVersion5, "tag5");
            Assert.assertNotNull(setDecoder);
            Assert.assertEquals(false, ReflectionUtil.get(setDecoder, "firstChoice"));
            Assert.assertEquals(true, ReflectionUtil.get(setDecoder, "secondChoice"));
            final StringBuilder tag6Value = new StringBuilder();
            ReflectionUtil.get(decoderVersion5, "tag6", tag6Value);
            Assert.assertThat(tag6Value.toString(), CoreMatchers.is("This is some variable length data"));
        }
    }

    @Test
    public void testMessage2() throws Exception {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[4096]);
        {
            // Encode
            final Object encoder = SchemaExtensionTest.wrap(buffer, compile("TestMessage2Encoder").getConstructor().newInstance());
            ReflectionUtil.set(encoder, "tag1", int.class, 100);
            ReflectionUtil.set(encoder, "tag2", int.class, 200);
            final Object compositeEncoder = encoder.getClass().getMethod("tag3").invoke(encoder);
            ReflectionUtil.set(compositeEncoder, "value", int.class, 300);
            final Object enumConstant = getAEnumConstant(encoder, "AEnum", 1);
            ReflectionUtil.set(encoder, "tag4", enumConstant.getClass(), enumConstant);
            final Object setEncoder = encoder.getClass().getMethod("tag5").invoke(encoder);
            ReflectionUtil.set(setEncoder, "firstChoice", boolean.class, false);
            ReflectionUtil.set(setEncoder, "secondChoice", boolean.class, true);
        }
        {
            // Decode version 0
            final Object decoderVersion0 = getMessage2Decoder(buffer, 4, 0);
            Assert.assertEquals(100, ReflectionUtil.get(decoderVersion0, "tag1"));
            Assert.assertEquals(Integer.MIN_VALUE, ReflectionUtil.get(decoderVersion0, "tag2"));
            Assert.assertNull(ReflectionUtil.get(decoderVersion0, "tag3"));
            Assert.assertThat(ReflectionUtil.get(decoderVersion0, "tag4").toString(), CoreMatchers.is("NULL_VAL"));
            Assert.assertNull(ReflectionUtil.get(decoderVersion0, "tag5"));
            Assert.assertEquals(0, decoderVersion0.getClass().getMethod("tag1SinceVersion").invoke(null));
            Assert.assertEquals(2, decoderVersion0.getClass().getMethod("tag2SinceVersion").invoke(null));
            Assert.assertEquals(1, decoderVersion0.getClass().getMethod("tag3SinceVersion").invoke(null));
            Assert.assertEquals(4, decoderVersion0.getClass().getMethod("tag4SinceVersion").invoke(null));
            Assert.assertEquals(3, decoderVersion0.getClass().getMethod("tag5SinceVersion").invoke(null));
        }
        {
            // Decode version 1
            final Object decoderVersion1 = getMessage2Decoder(buffer, 8, 1);
            Assert.assertEquals(100, ReflectionUtil.get(decoderVersion1, "tag1"));
            Assert.assertEquals(Integer.MIN_VALUE, ReflectionUtil.get(decoderVersion1, "tag2"));
            final Object compositeDecoder2 = ReflectionUtil.get(decoderVersion1, "tag3");
            Assert.assertNotNull(compositeDecoder2);
            Assert.assertEquals(300, ReflectionUtil.get(compositeDecoder2, "value"));
            Assert.assertThat(ReflectionUtil.get(decoderVersion1, "tag4").toString(), CoreMatchers.is("NULL_VAL"));
            Assert.assertNull(ReflectionUtil.get(decoderVersion1, "tag5"));
        }
        {
            // Decode version 2
            final Object decoderVersion2 = getMessage2Decoder(buffer, 8, 2);
            Assert.assertEquals(100, ReflectionUtil.get(decoderVersion2, "tag1"));
            Assert.assertEquals(200, ReflectionUtil.get(decoderVersion2, "tag2"));
            final Object compositeDecoder2 = ReflectionUtil.get(decoderVersion2, "tag3");
            Assert.assertNotNull(compositeDecoder2);
            Assert.assertEquals(300, ReflectionUtil.get(compositeDecoder2, "value"));
            Assert.assertThat(ReflectionUtil.get(decoderVersion2, "tag4").toString(), CoreMatchers.is("NULL_VAL"));
            Assert.assertNull(ReflectionUtil.get(decoderVersion2, "tag5"));
        }
        {
            // Decode version 3
            final Object decoderVersion3 = getMessage2Decoder(buffer, 12, 3);
            Assert.assertEquals(100, ReflectionUtil.get(decoderVersion3, "tag1"));
            Assert.assertEquals(200, ReflectionUtil.get(decoderVersion3, "tag2"));
            final Object compositeDecoder3 = ReflectionUtil.get(decoderVersion3, "tag3");
            Assert.assertNotNull(compositeDecoder3);
            Assert.assertEquals(300, ReflectionUtil.get(compositeDecoder3, "value"));
            Assert.assertThat(ReflectionUtil.get(decoderVersion3, "tag4").toString(), CoreMatchers.is("NULL_VAL"));
            final Object setDecoder = ReflectionUtil.get(decoderVersion3, "tag5");
            Assert.assertNotNull(setDecoder);
            Assert.assertEquals(false, ReflectionUtil.get(setDecoder, "firstChoice"));
            Assert.assertEquals(true, ReflectionUtil.get(setDecoder, "secondChoice"));
        }
        {
            // Decode version 4
            final Object decoderVersion4 = getMessage2Decoder(buffer, 12, 4);
            Assert.assertEquals(100, ReflectionUtil.get(decoderVersion4, "tag1"));
            Assert.assertEquals(200, ReflectionUtil.get(decoderVersion4, "tag2"));
            final Object compositeDecoder4 = ReflectionUtil.get(decoderVersion4, "tag3");
            Assert.assertNotNull(compositeDecoder4);
            Assert.assertEquals(300, ReflectionUtil.get(compositeDecoder4, "value"));
            final Object enumConstant = getAEnumConstant(decoderVersion4, "AEnum", 1);
            Assert.assertEquals(enumConstant, ReflectionUtil.get(decoderVersion4, "tag4"));
            final Object setDecoder = ReflectionUtil.get(decoderVersion4, "tag5");
            Assert.assertNotNull(setDecoder);
            Assert.assertEquals(false, ReflectionUtil.get(setDecoder, "firstChoice"));
            Assert.assertEquals(true, ReflectionUtil.get(setDecoder, "secondChoice"));
        }
    }
}

