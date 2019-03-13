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


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.codec.ByteArrayDecoder;
import org.springframework.core.codec.ByteArrayEncoder;
import org.springframework.core.codec.ByteBufferDecoder;
import org.springframework.core.codec.ByteBufferEncoder;
import org.springframework.core.codec.DataBufferDecoder;
import org.springframework.core.codec.DataBufferEncoder;
import org.springframework.core.codec.ResourceDecoder;
import org.springframework.http.codec.FormHttpMessageReader;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ResourceHttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.ServerSentEventHttpMessageWriter;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.codec.json.Jackson2SmileDecoder;
import org.springframework.http.codec.json.Jackson2SmileEncoder;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.http.codec.protobuf.ProtobufDecoder;
import org.springframework.http.codec.protobuf.ProtobufHttpMessageWriter;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;


/**
 * Unit tests for {@link ServerCodecConfigurer}.
 *
 * @author Rossen Stoyanchev
 */
public class ServerCodecConfigurerTests {
    private final ServerCodecConfigurer configurer = new DefaultServerCodecConfigurer();

    private final AtomicInteger index = new AtomicInteger(0);

    @Test
    public void defaultReaders() {
        List<HttpMessageReader<?>> readers = this.configurer.getReaders();
        Assert.assertEquals(13, readers.size());
        Assert.assertEquals(ByteArrayDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(ByteBufferDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(DataBufferDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(ResourceDecoder.class, getNextDecoder(readers).getClass());
        assertStringDecoder(getNextDecoder(readers), true);
        Assert.assertEquals(ProtobufDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(FormHttpMessageReader.class, readers.get(this.index.getAndIncrement()).getClass());
        Assert.assertEquals(SynchronossPartHttpMessageReader.class, readers.get(this.index.getAndIncrement()).getClass());
        Assert.assertEquals(MultipartHttpMessageReader.class, readers.get(this.index.getAndIncrement()).getClass());
        Assert.assertEquals(Jackson2JsonDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(Jackson2SmileDecoder.class, getNextDecoder(readers).getClass());
        Assert.assertEquals(Jaxb2XmlDecoder.class, getNextDecoder(readers).getClass());
        assertStringDecoder(getNextDecoder(readers), false);
    }

    @Test
    public void defaultWriters() {
        List<HttpMessageWriter<?>> writers = this.configurer.getWriters();
        Assert.assertEquals(11, writers.size());
        Assert.assertEquals(ByteArrayEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(ByteBufferEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(DataBufferEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(ResourceHttpMessageWriter.class, writers.get(index.getAndIncrement()).getClass());
        assertStringEncoder(getNextEncoder(writers), true);
        Assert.assertEquals(ProtobufHttpMessageWriter.class, writers.get(index.getAndIncrement()).getClass());
        Assert.assertEquals(Jackson2JsonEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(Jackson2SmileEncoder.class, getNextEncoder(writers).getClass());
        Assert.assertEquals(Jaxb2XmlEncoder.class, getNextEncoder(writers).getClass());
        assertSseWriter(writers);
        assertStringEncoder(getNextEncoder(writers), false);
    }

    @Test
    public void jackson2EncoderOverride() {
        Jackson2JsonEncoder encoder = new Jackson2JsonEncoder();
        this.configurer.defaultCodecs().jackson2JsonEncoder(encoder);
        Assert.assertSame(encoder, this.configurer.getWriters().stream().filter(( writer) -> .class.equals(writer.getClass())).map(( writer) -> ((ServerSentEventHttpMessageWriter) (writer))).findFirst().map(ServerSentEventHttpMessageWriter::getEncoder).filter(( e) -> e == encoder).orElse(null));
    }
}

