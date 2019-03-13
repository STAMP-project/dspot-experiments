/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.transport.amqp.message;


import EncodingCodes.STR32;
import EncodingCodes.STR8;
import ReadableBuffer.ByteBufferReader;
import java.util.UUID;
import org.apache.qpid.proton.codec.AMQPDefinedTypes;
import org.apache.qpid.proton.codec.DecoderImpl;
import org.apache.qpid.proton.codec.EncoderImpl;
import org.apache.qpid.proton.codec.PrimitiveTypeEncoding;
import org.apache.qpid.proton.codec.ReadableBuffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the UTF8Buffer type encoder
 */
public class UTF8BufferTypeTest {
    private final UTF8BufferType utf8BufferEncoding;

    private final DecoderImpl decoder = new DecoderImpl();

    private final EncoderImpl encoder = new EncoderImpl(decoder);

    {
        AMQPDefinedTypes.registerAllTypes(decoder, encoder);
        utf8BufferEncoding = new UTF8BufferType(encoder, decoder);
        encoder.register(utf8BufferEncoding);
    }

    private String smallString = UUID.randomUUID().toString();

    private String largeString = (((((((UUID.randomUUID().toString()) + (UUID.randomUUID().toString())) + (UUID.randomUUID().toString())) + (UUID.randomUUID().toString())) + (UUID.randomUUID().toString())) + (UUID.randomUUID().toString())) + (UUID.randomUUID().toString())) + (UUID.randomUUID().toString());

    private UTF8Buffer smallBuffer;

    private UTF8Buffer largeBuffer;

    @Test
    public void testGetAllEncodings() {
        Assert.assertEquals(2, utf8BufferEncoding.getAllEncodings().size());
    }

    @Test
    public void testGetTypeClass() {
        Assert.assertEquals(UTF8Buffer.class, utf8BufferEncoding.getTypeClass());
    }

    @Test
    public void testGetCanonicalEncoding() {
        Assert.assertNotNull(utf8BufferEncoding.getCanonicalEncoding());
    }

    @Test
    public void testGetEncodingForSmallUTF8Buffer() {
        PrimitiveTypeEncoding<UTF8Buffer> encoding = utf8BufferEncoding.getEncoding(smallBuffer);
        Assert.assertTrue((encoding instanceof UTF8BufferType.SmallUTF8BufferEncoding));
        Assert.assertEquals(1, encoding.getConstructorSize());
        Assert.assertEquals(((smallBuffer.getLength()) + (Byte.BYTES)), encoding.getValueSize(smallBuffer));
        Assert.assertEquals(STR8, encoding.getEncodingCode());
        Assert.assertFalse(encoding.encodesJavaPrimitive());
        Assert.assertEquals(utf8BufferEncoding, encoding.getType());
    }

    @Test
    public void testGetEncodingForLargeUTF8Buffer() {
        PrimitiveTypeEncoding<UTF8Buffer> encoding = utf8BufferEncoding.getEncoding(largeBuffer);
        Assert.assertTrue((encoding instanceof UTF8BufferType.LargeUTF8BufferEncoding));
        Assert.assertEquals(1, encoding.getConstructorSize());
        Assert.assertEquals(((largeBuffer.getLength()) + (Integer.BYTES)), encoding.getValueSize(largeBuffer));
        Assert.assertEquals(STR32, encoding.getEncodingCode());
        Assert.assertFalse(encoding.encodesJavaPrimitive());
        Assert.assertEquals(utf8BufferEncoding, encoding.getType());
    }

    @Test
    public void testEncodeDecodeEmptyStringBuffer() {
        final AmqpWritableBuffer buffer = new AmqpWritableBuffer();
        encoder.setByteBuffer(buffer);
        encoder.writeObject(new UTF8Buffer(""));
        byte[] copy = new byte[buffer.getArrayLength()];
        System.arraycopy(buffer.getArray(), 0, copy, 0, buffer.getArrayLength());
        ReadableBuffer encoded = ByteBufferReader.wrap(copy);
        decoder.setBuffer(encoded);
        Object valueRead = decoder.readObject();
        Assert.assertTrue((valueRead instanceof String));
        String decodedString = ((String) (valueRead));
        Assert.assertEquals("", decodedString);
    }

    @Test
    public void testEncodeDecodeSmallBuffer() {
        final AmqpWritableBuffer buffer = new AmqpWritableBuffer();
        encoder.setByteBuffer(buffer);
        encoder.writeObject(smallBuffer);
        byte[] copy = new byte[buffer.getArrayLength()];
        System.arraycopy(buffer.getArray(), 0, copy, 0, buffer.getArrayLength());
        ReadableBuffer encoded = ByteBufferReader.wrap(copy);
        decoder.setBuffer(encoded);
        Object valueRead = decoder.readObject();
        Assert.assertTrue((valueRead instanceof String));
        String decodedString = ((String) (valueRead));
        Assert.assertEquals(smallString, decodedString);
    }

    @Test
    public void testEncodeDecodeLargeBuffer() {
        final AmqpWritableBuffer buffer = new AmqpWritableBuffer();
        encoder.setByteBuffer(buffer);
        encoder.writeObject(largeBuffer);
        byte[] copy = new byte[buffer.getArrayLength()];
        System.arraycopy(buffer.getArray(), 0, copy, 0, buffer.getArrayLength());
        ReadableBuffer encoded = ByteBufferReader.wrap(copy);
        decoder.setBuffer(encoded);
        Object valueRead = decoder.readObject();
        Assert.assertTrue((valueRead instanceof String));
        String decodedString = ((String) (valueRead));
        Assert.assertEquals(largeString, decodedString);
    }
}

