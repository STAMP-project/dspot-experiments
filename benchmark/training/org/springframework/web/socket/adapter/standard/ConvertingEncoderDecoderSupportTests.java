/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.web.socket.adapter.standard;


import java.nio.ByteBuffer;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.ContextLoaderTestUtils;


/**
 * Test for {@link org.springframework.web.socket.adapter.standard.ConvertingEncoderDecoderSupport}.
 *
 * @author Phillip Webb
 */
public class ConvertingEncoderDecoderSupportTests {
    private static final String CONVERTED_TEXT = "_test";

    private static final ByteBuffer CONVERTED_BYTES = ByteBuffer.wrap("~test".getBytes());

    @Rule
    public ExpectedException thown = ExpectedException.none();

    private WebApplicationContext applicationContext;

    private ConvertingEncoderDecoderSupportTests.MyType myType = new ConvertingEncoderDecoderSupportTests.MyType("test");

    @Test
    public void encodeToText() throws Exception {
        Assert.assertThat(encode(myType), equalTo(ConvertingEncoderDecoderSupportTests.CONVERTED_TEXT));
    }

    @Test
    public void encodeToTextCannotConvert() throws Exception {
        setup(ConvertingEncoderDecoderSupportTests.NoConvertersConfig.class);
        thown.expect(EncodeException.class);
        thown.expectCause(isA(ConverterNotFoundException.class));
        encode(myType);
    }

    @Test
    public void encodeToBinary() throws Exception {
        Assert.assertThat(encode(myType).array(), equalTo(ConvertingEncoderDecoderSupportTests.CONVERTED_BYTES.array()));
    }

    @Test
    public void encodeToBinaryCannotConvert() throws Exception {
        setup(ConvertingEncoderDecoderSupportTests.NoConvertersConfig.class);
        thown.expect(EncodeException.class);
        thown.expectCause(isA(ConverterNotFoundException.class));
        encode(myType);
    }

    @Test
    public void decodeFromText() throws Exception {
        Decoder.Text<ConvertingEncoderDecoderSupportTests.MyType> decoder = new ConvertingEncoderDecoderSupportTests.MyTextDecoder();
        Assert.assertThat(decoder.willDecode(ConvertingEncoderDecoderSupportTests.CONVERTED_TEXT), is(true));
        Assert.assertThat(decoder.decode(ConvertingEncoderDecoderSupportTests.CONVERTED_TEXT), equalTo(myType));
    }

    @Test
    public void decodeFromTextCannotConvert() throws Exception {
        setup(ConvertingEncoderDecoderSupportTests.NoConvertersConfig.class);
        Decoder.Text<ConvertingEncoderDecoderSupportTests.MyType> decoder = new ConvertingEncoderDecoderSupportTests.MyTextDecoder();
        Assert.assertThat(decoder.willDecode(ConvertingEncoderDecoderSupportTests.CONVERTED_TEXT), is(false));
        thown.expect(DecodeException.class);
        thown.expectCause(isA(ConverterNotFoundException.class));
        decoder.decode(ConvertingEncoderDecoderSupportTests.CONVERTED_TEXT);
    }

    @Test
    public void decodeFromBinary() throws Exception {
        Decoder.Binary<ConvertingEncoderDecoderSupportTests.MyType> decoder = new ConvertingEncoderDecoderSupportTests.MyBinaryDecoder();
        Assert.assertThat(decoder.willDecode(ConvertingEncoderDecoderSupportTests.CONVERTED_BYTES), is(true));
        Assert.assertThat(decoder.decode(ConvertingEncoderDecoderSupportTests.CONVERTED_BYTES), equalTo(myType));
    }

    @Test
    public void decodeFromBinaryCannotConvert() throws Exception {
        setup(ConvertingEncoderDecoderSupportTests.NoConvertersConfig.class);
        Decoder.Binary<ConvertingEncoderDecoderSupportTests.MyType> decoder = new ConvertingEncoderDecoderSupportTests.MyBinaryDecoder();
        Assert.assertThat(decoder.willDecode(ConvertingEncoderDecoderSupportTests.CONVERTED_BYTES), is(false));
        thown.expect(DecodeException.class);
        thown.expectCause(isA(ConverterNotFoundException.class));
        decoder.decode(ConvertingEncoderDecoderSupportTests.CONVERTED_BYTES);
    }

    @Test
    public void encodeAndDecodeText() throws Exception {
        ConvertingEncoderDecoderSupportTests.MyTextEncoderDecoder encoderDecoder = new ConvertingEncoderDecoderSupportTests.MyTextEncoderDecoder();
        String encoded = encoderDecoder.encode(myType);
        Assert.assertThat(encoderDecoder.decode(encoded), equalTo(myType));
    }

    @Test
    public void encodeAndDecodeBytes() throws Exception {
        ConvertingEncoderDecoderSupportTests.MyBinaryEncoderDecoder encoderDecoder = new ConvertingEncoderDecoderSupportTests.MyBinaryEncoderDecoder();
        ByteBuffer encoded = encoderDecoder.encode(myType);
        Assert.assertThat(decode(encoded), equalTo(myType));
    }

    @Test
    public void autowiresIntoEncoder() throws Exception {
        ConvertingEncoderDecoderSupportTests.WithAutowire withAutowire = new ConvertingEncoderDecoderSupportTests.WithAutowire();
        init(null);
        Assert.assertThat(withAutowire.config, equalTo(applicationContext.getBean(ConvertingEncoderDecoderSupportTests.Config.class)));
    }

    @Test
    public void cannotFindApplicationContext() throws Exception {
        ContextLoaderTestUtils.setCurrentWebApplicationContext(null);
        ConvertingEncoderDecoderSupportTests.WithAutowire encoder = new ConvertingEncoderDecoderSupportTests.WithAutowire();
        init(null);
        thown.expect(IllegalStateException.class);
        thown.expectMessage("Unable to locate the Spring ApplicationContext");
        encode(myType);
    }

    @Test
    public void cannotFindConversionService() throws Exception {
        setup(ConvertingEncoderDecoderSupportTests.NoConfig.class);
        ConvertingEncoderDecoderSupportTests.MyBinaryEncoder encoder = new ConvertingEncoderDecoderSupportTests.MyBinaryEncoder();
        init(null);
        thown.expect(IllegalStateException.class);
        thown.expectMessage("Unable to find ConversionService");
        encode(myType);
    }

    @Configuration
    public static class Config {
        @Bean
        public ConversionService webSocketConversionService() {
            GenericConversionService conversionService = new DefaultConversionService();
            conversionService.addConverter(new ConvertingEncoderDecoderSupportTests.MyTypeToStringConverter());
            conversionService.addConverter(new ConvertingEncoderDecoderSupportTests.MyTypeToBytesConverter());
            conversionService.addConverter(new ConvertingEncoderDecoderSupportTests.StringToMyTypeConverter());
            conversionService.addConverter(new ConvertingEncoderDecoderSupportTests.BytesToMyTypeConverter());
            return conversionService;
        }
    }

    @Configuration
    public static class NoConvertersConfig {
        @Bean
        public ConversionService webSocketConversionService() {
            return new GenericConversionService();
        }
    }

    @Configuration
    public static class NoConfig {}

    public static class MyType {
        private String value;

        public MyType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ConvertingEncoderDecoderSupportTests.MyType) {
                return ((ConvertingEncoderDecoderSupportTests.MyType) (obj)).value.equals(value);
            }
            return false;
        }
    }

    private static class MyTypeToStringConverter implements Converter<ConvertingEncoderDecoderSupportTests.MyType, String> {
        @Override
        public String convert(ConvertingEncoderDecoderSupportTests.MyType source) {
            return "_" + (source.toString());
        }
    }

    private static class MyTypeToBytesConverter implements Converter<ConvertingEncoderDecoderSupportTests.MyType, byte[]> {
        @Override
        public byte[] convert(ConvertingEncoderDecoderSupportTests.MyType source) {
            return ("~" + (source.toString())).getBytes();
        }
    }

    private static class StringToMyTypeConverter implements Converter<String, ConvertingEncoderDecoderSupportTests.MyType> {
        @Override
        public ConvertingEncoderDecoderSupportTests.MyType convert(String source) {
            return new ConvertingEncoderDecoderSupportTests.MyType(source.substring(1));
        }
    }

    private static class BytesToMyTypeConverter implements Converter<byte[], ConvertingEncoderDecoderSupportTests.MyType> {
        @Override
        public ConvertingEncoderDecoderSupportTests.MyType convert(byte[] source) {
            return new ConvertingEncoderDecoderSupportTests.MyType(new String(source).substring(1));
        }
    }

    public static class MyTextEncoder extends ConvertingEncoderDecoderSupport.TextEncoder<ConvertingEncoderDecoderSupportTests.MyType> {}

    public static class MyBinaryEncoder extends ConvertingEncoderDecoderSupport.BinaryEncoder<ConvertingEncoderDecoderSupportTests.MyType> {}

    public static class MyTextDecoder extends ConvertingEncoderDecoderSupport.TextDecoder<ConvertingEncoderDecoderSupportTests.MyType> {}

    public static class MyBinaryDecoder extends ConvertingEncoderDecoderSupport.BinaryDecoder<ConvertingEncoderDecoderSupportTests.MyType> {}

    public static class MyTextEncoderDecoder extends ConvertingEncoderDecoderSupport<ConvertingEncoderDecoderSupportTests.MyType, String> implements Decoder.Text<ConvertingEncoderDecoderSupportTests.MyType> , Encoder.Text<ConvertingEncoderDecoderSupportTests.MyType> {}

    public static class MyBinaryEncoderDecoder extends ConvertingEncoderDecoderSupport<ConvertingEncoderDecoderSupportTests.MyType, ByteBuffer> implements Decoder.Binary<ConvertingEncoderDecoderSupportTests.MyType> , Encoder.Binary<ConvertingEncoderDecoderSupportTests.MyType> {}

    public static class WithAutowire extends ConvertingEncoderDecoderSupport.TextDecoder<ConvertingEncoderDecoderSupportTests.MyType> {
        @Autowired
        private ConvertingEncoderDecoderSupportTests.Config config;
    }
}

