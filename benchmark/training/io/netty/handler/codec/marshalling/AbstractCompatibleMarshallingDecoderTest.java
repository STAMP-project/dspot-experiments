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
package io.netty.handler.codec.marshalling;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractCompatibleMarshallingDecoderTest extends AbstractMarshallingTest {
    @SuppressWarnings("RedundantStringConstructorCall")
    private final String testObject = new String("test");

    @Test
    public void testSimpleUnmarshalling() throws IOException {
        MarshallerFactory marshallerFactory = createMarshallerFactory();
        MarshallingConfiguration configuration = createMarshallingConfig();
        EmbeddedChannel ch = new EmbeddedChannel(createDecoder(Integer.MAX_VALUE));
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
        marshaller.start(Marshalling.createByteOutput(bout));
        marshaller.writeObject(testObject);
        marshaller.finish();
        marshaller.close();
        byte[] testBytes = bout.toByteArray();
        ch.writeInbound(input(testBytes));
        Assert.assertTrue(ch.finish());
        String unmarshalled = ch.readInbound();
        Assert.assertEquals(testObject, unmarshalled);
        Assert.assertNull(ch.readInbound());
    }

    @Test
    public void testFragmentedUnmarshalling() throws IOException {
        MarshallerFactory marshallerFactory = createMarshallerFactory();
        MarshallingConfiguration configuration = createMarshallingConfig();
        EmbeddedChannel ch = new EmbeddedChannel(createDecoder(Integer.MAX_VALUE));
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
        marshaller.start(Marshalling.createByteOutput(bout));
        marshaller.writeObject(testObject);
        marshaller.finish();
        marshaller.close();
        byte[] testBytes = bout.toByteArray();
        ByteBuf buffer = input(testBytes);
        ByteBuf slice = buffer.readRetainedSlice(2);
        ch.writeInbound(slice);
        ch.writeInbound(buffer);
        Assert.assertTrue(ch.finish());
        String unmarshalled = ch.readInbound();
        Assert.assertEquals(testObject, unmarshalled);
        Assert.assertNull(ch.readInbound());
    }

    @Test
    public void testTooBigObject() throws IOException {
        MarshallerFactory marshallerFactory = createMarshallerFactory();
        MarshallingConfiguration configuration = createMarshallingConfig();
        ChannelHandler mDecoder = createDecoder(4);
        EmbeddedChannel ch = new EmbeddedChannel(mDecoder);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
        marshaller.start(Marshalling.createByteOutput(bout));
        marshaller.writeObject(testObject);
        marshaller.finish();
        marshaller.close();
        byte[] testBytes = bout.toByteArray();
        onTooBigFrame(ch, input(testBytes));
    }
}

