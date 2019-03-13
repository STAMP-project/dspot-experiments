/**
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.netty.handler.codec.marshalling;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.handler.codec.embedder.CodecEmbedderException;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractCompatibleMarshallingDecoderTest {
    @SuppressWarnings("RedundantStringConstructorCall")
    private final String testObject = new String("test");

    @Test
    public void testSimpleUnmarshalling() throws IOException {
        MarshallerFactory marshallerFactory = createMarshallerFactory();
        MarshallingConfiguration configuration = createMarshallingConfig();
        DecoderEmbedder<Object> decoder = new DecoderEmbedder<Object>(createDecoder(Integer.MAX_VALUE));
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
        marshaller.start(Marshalling.createByteOutput(bout));
        marshaller.writeObject(testObject);
        marshaller.finish();
        marshaller.close();
        byte[] testBytes = bout.toByteArray();
        decoder.offer(input(testBytes));
        Assert.assertTrue(decoder.finish());
        String unmarshalled = ((String) (decoder.poll()));
        Assert.assertEquals(testObject, unmarshalled);
        Assert.assertNull(decoder.poll());
    }

    @Test
    public void testFragmentedUnmarshalling() throws IOException {
        MarshallerFactory marshallerFactory = createMarshallerFactory();
        MarshallingConfiguration configuration = createMarshallingConfig();
        DecoderEmbedder<Object> decoder = new DecoderEmbedder<Object>(createDecoder(Integer.MAX_VALUE));
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
        marshaller.start(Marshalling.createByteOutput(bout));
        marshaller.writeObject(testObject);
        marshaller.finish();
        marshaller.close();
        byte[] testBytes = bout.toByteArray();
        ChannelBuffer buffer = input(testBytes);
        ChannelBuffer slice = buffer.readSlice(2);
        decoder.offer(slice);
        decoder.offer(buffer);
        Assert.assertTrue(decoder.finish());
        String unmarshalled = ((String) (decoder.poll()));
        Assert.assertEquals(testObject, unmarshalled);
        Assert.assertNull(decoder.poll());
    }

    @Test
    public void testTooBigObject() throws IOException {
        MarshallerFactory marshallerFactory = createMarshallerFactory();
        MarshallingConfiguration configuration = createMarshallingConfig();
        ChannelUpstreamHandler mDecoder = createDecoder(4);
        DecoderEmbedder<Object> decoder = new DecoderEmbedder<Object>(mDecoder);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
        marshaller.start(Marshalling.createByteOutput(bout));
        marshaller.writeObject(testObject);
        marshaller.finish();
        marshaller.close();
        byte[] testBytes = bout.toByteArray();
        try {
            decoder.offer(input(testBytes));
            Assert.fail();
        } catch (CodecEmbedderException e) {
            Assert.assertEquals(TooLongFrameException.class, e.getCause().getClass());
        }
    }
}

