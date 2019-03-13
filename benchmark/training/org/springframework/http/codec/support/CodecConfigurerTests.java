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
package org.springframework.http.codec.support;


import com.google.protobuf.ExtensionRegistry;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.ByteArrayDecoder;
import org.springframework.core.codec.ByteArrayEncoder;
import org.springframework.core.codec.ByteBufferDecoder;
import org.springframework.core.codec.ByteBufferEncoder;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.DataBufferDecoder;
import org.springframework.core.codec.DataBufferEncoder;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.core.codec.ResourceDecoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.FormHttpMessageReader;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ResourceHttpMessageWriter;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.codec.json.Jackson2SmileDecoder;
import org.springframework.http.codec.json.Jackson2SmileEncoder;
import org.springframework.http.codec.protobuf.ProtobufDecoder;
import org.springframework.http.codec.protobuf.ProtobufEncoder;
import org.springframework.http.codec.protobuf.ProtobufHttpMessageWriter;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;


/**
 * Unit tests for {@link BaseDefaultCodecs}.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 */
public class CodecConfigurerTests {
    private final CodecConfigurer configurer = new CodecConfigurerTests.TestCodecConfigurer();

    private final AtomicInteger index = new AtomicInteger(0);

    @Test
    public void defaultReaders() {
        List<HttpMessageReader<?>> readers = this.configurer.getReaders();
        Assert.assertEquals(11, readers.size());
        Assert.assertEquals(ByteArrayDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(ByteBufferDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(DataBufferDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(ResourceDecoder.class, getNextDecoder(readers).getClass());
        assertStringDecoder(getNextDecoder(readers), true);
        Assert.assertEquals(ProtobufDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(FormHttpMessageReader.class, readers.get(this.index.getAndIncrement()).getClass());
        Assert.assertEquals(Jackson2JsonDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(Jackson2SmileDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(Jaxb2XmlDecoder.class, getNextDecoder(readers).getClass());
        assertStringDecoder(getNextDecoder(readers), false);
    }

    @Test
    public void defaultWriters() {
        List<HttpMessageWriter<?>> writers = this.configurer.getWriters();
        Assert.assertEquals(10, writers.size());
        Assert.assertEquals(ByteArrayEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(ByteBufferEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(DataBufferEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(ResourceHttpMessageWriter.class, writers.get(index.getAndIncrement()).getClass());
        assertStringEncoder(getNextEncoder(writers), true);
        Assert.assertEquals(ProtobufHttpMessageWriter.class, writers.get(index.getAndIncrement()).getClass());
        Assert.assertEquals(Jackson2JsonEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(Jackson2SmileEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(Jaxb2XmlEncoder.class, getNextEncoder(writers).getClass());
        assertStringEncoder(getNextEncoder(writers), false);
    }

    @Test
    public void defaultAndCustomReaders() {
        Decoder<?> customDecoder1 = Mockito.mock(Decoder.class);
        Decoder<?> customDecoder2 = Mockito.mock(Decoder.class);
        Mockito.when(customDecoder1.canDecode(ResolvableType.forClass(Object.class), null)).thenReturn(false);
        Mockito.when(customDecoder2.canDecode(ResolvableType.forClass(Object.class), null)).thenReturn(true);
        HttpMessageReader<?> customReader1 = Mockito.mock(HttpMessageReader.class);
        HttpMessageReader<?> customReader2 = Mockito.mock(HttpMessageReader.class);
        Mockito.when(customReader1.canRead(ResolvableType.forClass(Object.class), null)).thenReturn(false);
        Mockito.when(customReader2.canRead(ResolvableType.forClass(Object.class), null)).thenReturn(true);
        this.configurer.customCodecs().decoder(customDecoder1);
        this.configurer.customCodecs().decoder(customDecoder2);
        this.configurer.customCodecs().reader(customReader1);
        this.configurer.customCodecs().reader(customReader2);
        List<HttpMessageReader<?>> readers = this.configurer.getReaders();
        Assert.assertEquals(15, readers.size());
        Assert.assertEquals(ByteArrayDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(ByteBufferDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(DataBufferDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(ResourceDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(StringDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(ProtobufDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(FormHttpMessageReader.class, readers.get(this.index.getAndIncrement()).getClass());
        Assert.assertSame(customDecoder1, getNextDecoder(readers));
        Assert.assertSame(customReader1, readers.get(this.index.getAndIncrement()));
        Assert.assertEquals(Jackson2JsonDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(Jackson2SmileDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(Jaxb2XmlDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertSame(customDecoder2, getNextDecoder(readers));
        Assert.assertSame(customReader2, readers.get(this.index.getAndIncrement()));
        Assert.assertEquals(StringDecoder.class, getNextDecoder(readers).getClass());
    }

    @Test
    public void defaultAndCustomWriters() {
        Encoder<?> customEncoder1 = Mockito.mock(Encoder.class);
        Encoder<?> customEncoder2 = Mockito.mock(Encoder.class);
        Mockito.when(customEncoder1.canEncode(ResolvableType.forClass(Object.class), null)).thenReturn(false);
        Mockito.when(customEncoder2.canEncode(ResolvableType.forClass(Object.class), null)).thenReturn(true);
        HttpMessageWriter<?> customWriter1 = Mockito.mock(HttpMessageWriter.class);
        HttpMessageWriter<?> customWriter2 = Mockito.mock(HttpMessageWriter.class);
        Mockito.when(customWriter1.canWrite(ResolvableType.forClass(Object.class), null)).thenReturn(false);
        Mockito.when(customWriter2.canWrite(ResolvableType.forClass(Object.class), null)).thenReturn(true);
        this.configurer.customCodecs().encoder(customEncoder1);
        this.configurer.customCodecs().encoder(customEncoder2);
        this.configurer.customCodecs().writer(customWriter1);
        this.configurer.customCodecs().writer(customWriter2);
        List<HttpMessageWriter<?>> writers = this.configurer.getWriters();
        Assert.assertEquals(14, writers.size());
        Assert.assertEquals(ByteArrayEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(ByteBufferEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(DataBufferEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(ResourceHttpMessageWriter.class, writers.get(index.getAndIncrement()).getClass());
        Assert.assertEquals(CharSequenceEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(ProtobufHttpMessageWriter.class, writers.get(index.getAndIncrement()).getClass());
        Assert.assertSame(customEncoder1, getNextEncoder(writers));
        Assert.assertSame(customWriter1, writers.get(this.index.getAndIncrement()));
        Assert.assertEquals(Jackson2JsonEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(Jackson2SmileEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(Jaxb2XmlEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertSame(customEncoder2, getNextEncoder(writers));
        Assert.assertSame(customWriter2, writers.get(this.index.getAndIncrement()));
        Assert.assertEquals(CharSequenceEncoder.class, getNextEncoder(writers).getClass());
    }

    @Test
    public void defaultsOffCustomReaders() {
        Decoder<?> customDecoder1 = Mockito.mock(Decoder.class);
        Decoder<?> customDecoder2 = Mockito.mock(Decoder.class);
        Mockito.when(customDecoder1.canDecode(ResolvableType.forClass(Object.class), null)).thenReturn(false);
        Mockito.when(customDecoder2.canDecode(ResolvableType.forClass(Object.class), null)).thenReturn(true);
        HttpMessageReader<?> customReader1 = Mockito.mock(HttpMessageReader.class);
        HttpMessageReader<?> customReader2 = Mockito.mock(HttpMessageReader.class);
        Mockito.when(customReader1.canRead(ResolvableType.forClass(Object.class), null)).thenReturn(false);
        Mockito.when(customReader2.canRead(ResolvableType.forClass(Object.class), null)).thenReturn(true);
        this.configurer.customCodecs().decoder(customDecoder1);
        this.configurer.customCodecs().decoder(customDecoder2);
        this.configurer.customCodecs().reader(customReader1);
        this.configurer.customCodecs().reader(customReader2);
        this.configurer.registerDefaults(false);
        List<HttpMessageReader<?>> readers = this.configurer.getReaders();
        Assert.assertEquals(4, readers.size());
        Assert.assertSame(customDecoder1, getNextDecoder(readers));
        Assert.assertSame(customReader1, readers.get(this.index.getAndIncrement()));
        Assert.assertSame(customDecoder2, getNextDecoder(readers));
        Assert.assertSame(customReader2, readers.get(this.index.getAndIncrement()));
    }

    @Test
    public void defaultsOffWithCustomWriters() {
        Encoder<?> customEncoder1 = Mockito.mock(Encoder.class);
        Encoder<?> customEncoder2 = Mockito.mock(Encoder.class);
        Mockito.when(customEncoder1.canEncode(ResolvableType.forClass(Object.class), null)).thenReturn(false);
        Mockito.when(customEncoder2.canEncode(ResolvableType.forClass(Object.class), null)).thenReturn(true);
        HttpMessageWriter<?> customWriter1 = Mockito.mock(HttpMessageWriter.class);
        HttpMessageWriter<?> customWriter2 = Mockito.mock(HttpMessageWriter.class);
        Mockito.when(customWriter1.canWrite(ResolvableType.forClass(Object.class), null)).thenReturn(false);
        Mockito.when(customWriter2.canWrite(ResolvableType.forClass(Object.class), null)).thenReturn(true);
        this.configurer.customCodecs().encoder(customEncoder1);
        this.configurer.customCodecs().encoder(customEncoder2);
        this.configurer.customCodecs().writer(customWriter1);
        this.configurer.customCodecs().writer(customWriter2);
        this.configurer.registerDefaults(false);
        List<HttpMessageWriter<?>> writers = this.configurer.getWriters();
        Assert.assertEquals(4, writers.size());
        Assert.assertSame(customEncoder1, getNextEncoder(writers));
        Assert.assertSame(customWriter1, writers.get(this.index.getAndIncrement()));
        Assert.assertSame(customEncoder2, getNextEncoder(writers));
        Assert.assertSame(customWriter2, writers.get(this.index.getAndIncrement()));
    }

    @Test
    public void encoderDecoderOverrides() {
        Jackson2JsonDecoder jacksonDecoder = new Jackson2JsonDecoder();
        Jackson2JsonEncoder jacksonEncoder = new Jackson2JsonEncoder();
        ProtobufDecoder protobufDecoder = new ProtobufDecoder(ExtensionRegistry.newInstance());
        ProtobufEncoder protobufEncoder = new ProtobufEncoder();
        Jaxb2XmlEncoder jaxb2Encoder = new Jaxb2XmlEncoder();
        Jaxb2XmlDecoder jaxb2Decoder = new Jaxb2XmlDecoder();
        this.configurer.defaultCodecs().jackson2JsonDecoder(jacksonDecoder);
        this.configurer.defaultCodecs().jackson2JsonEncoder(jacksonEncoder);
        this.configurer.defaultCodecs().protobufDecoder(protobufDecoder);
        this.configurer.defaultCodecs().protobufEncoder(protobufEncoder);
        this.configurer.defaultCodecs().jaxb2Decoder(jaxb2Decoder);
        this.configurer.defaultCodecs().jaxb2Encoder(jaxb2Encoder);
        assertDecoderInstance(jacksonDecoder);
        assertDecoderInstance(protobufDecoder);
        assertDecoderInstance(jaxb2Decoder);
        assertEncoderInstance(jacksonEncoder);
        assertEncoderInstance(protobufEncoder);
        assertEncoderInstance(jaxb2Encoder);
    }

    private static class TestCodecConfigurer extends BaseCodecConfigurer {
        TestCodecConfigurer() {
            super(new CodecConfigurerTests.TestCodecConfigurer.TestDefaultCodecs());
        }

        private static class TestDefaultCodecs extends BaseDefaultCodecs {}
    }
}

