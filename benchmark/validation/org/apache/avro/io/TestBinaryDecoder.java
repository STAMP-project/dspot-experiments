/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.io;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.util.ByteBufferOutputStream;
import org.apache.avro.util.Utf8;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestBinaryDecoder {
    // prime number buffer size so that looping tests hit the buffer edge
    // at different points in the loop.
    DecoderFactory factory = new DecoderFactory().configureDecoderBufferSize(521);

    private boolean useDirect = false;

    static EncoderFactory e_factory = EncoderFactory.get();

    public TestBinaryDecoder(boolean useDirect) {
        this.useDirect = useDirect;
    }

    /**
     * Verify EOFException throw at EOF
     */
    @Test(expected = EOFException.class)
    public void testEOFBoolean() throws IOException {
        newDecoderWithNoData().readBoolean();
    }

    @Test(expected = EOFException.class)
    public void testEOFInt() throws IOException {
        newDecoderWithNoData().readInt();
    }

    @Test(expected = EOFException.class)
    public void testEOFLong() throws IOException {
        newDecoderWithNoData().readLong();
    }

    @Test(expected = EOFException.class)
    public void testEOFFloat() throws IOException {
        newDecoderWithNoData().readFloat();
    }

    @Test(expected = EOFException.class)
    public void testEOFDouble() throws IOException {
        newDecoderWithNoData().readDouble();
    }

    @Test(expected = EOFException.class)
    public void testEOFBytes() throws IOException {
        newDecoderWithNoData().readBytes(null);
    }

    @Test(expected = EOFException.class)
    public void testEOFString() throws IOException {
        newDecoderWithNoData().readString(new Utf8("a"));
    }

    @Test(expected = EOFException.class)
    public void testEOFFixed() throws IOException {
        newDecoderWithNoData().readFixed(new byte[1]);
    }

    @Test(expected = EOFException.class)
    public void testEOFEnum() throws IOException {
        newDecoderWithNoData().readEnum();
    }

    @Test
    public void testReuse() throws IOException {
        ByteBufferOutputStream bbo1 = new ByteBufferOutputStream();
        ByteBufferOutputStream bbo2 = new ByteBufferOutputStream();
        byte[] b1 = new byte[]{ 1, 2 };
        BinaryEncoder e1 = TestBinaryDecoder.e_factory.binaryEncoder(bbo1, null);
        e1.writeBytes(b1);
        e1.flush();
        BinaryEncoder e2 = TestBinaryDecoder.e_factory.binaryEncoder(bbo2, null);
        e2.writeBytes(b1);
        e2.flush();
        DirectBinaryDecoder d = new DirectBinaryDecoder(new org.apache.avro.util.ByteBufferInputStream(bbo1.getBufferList()));
        ByteBuffer bb1 = d.readBytes(null);
        Assert.assertEquals(b1.length, ((bb1.limit()) - (bb1.position())));
        d.configure(new org.apache.avro.util.ByteBufferInputStream(bbo2.getBufferList()));
        ByteBuffer bb2 = d.readBytes(null);
        Assert.assertEquals(b1.length, ((bb2.limit()) - (bb2.position())));
    }

    private static byte[] data = null;

    private static int seed = -1;

    private static Schema schema = null;

    private static int count = 200;

    private static ArrayList<Object> records = new ArrayList<>(TestBinaryDecoder.count);

    @Test
    public void testDecodeFromSources() throws IOException {
        GenericDatumReader<Object> reader = new GenericDatumReader();
        reader.setSchema(TestBinaryDecoder.schema);
        ByteArrayInputStream is = new ByteArrayInputStream(TestBinaryDecoder.data);
        ByteArrayInputStream is2 = new ByteArrayInputStream(TestBinaryDecoder.data);
        ByteArrayInputStream is3 = new ByteArrayInputStream(TestBinaryDecoder.data);
        Decoder fromInputStream = newDecoder(is);
        Decoder fromArray = newDecoder(TestBinaryDecoder.data);
        byte[] data2 = new byte[(TestBinaryDecoder.data.length) + 30];
        Arrays.fill(data2, ((byte) (255)));
        System.arraycopy(TestBinaryDecoder.data, 0, data2, 15, TestBinaryDecoder.data.length);
        Decoder fromOffsetArray = newDecoder(data2, 15, TestBinaryDecoder.data.length);
        BinaryDecoder initOnInputStream = factory.binaryDecoder(new byte[50], 0, 30, null);
        initOnInputStream = factory.binaryDecoder(is2, initOnInputStream);
        BinaryDecoder initOnArray = factory.binaryDecoder(is3, null);
        initOnArray = factory.binaryDecoder(TestBinaryDecoder.data, 0, TestBinaryDecoder.data.length, initOnArray);
        for (Object datum : TestBinaryDecoder.records) {
            Assert.assertEquals("InputStream based BinaryDecoder result does not match", datum, reader.read(null, fromInputStream));
            Assert.assertEquals("Array based BinaryDecoder result does not match", datum, reader.read(null, fromArray));
            Assert.assertEquals("offset Array based BinaryDecoder result does not match", datum, reader.read(null, fromOffsetArray));
            Assert.assertEquals("InputStream initialized BinaryDecoder result does not match", datum, reader.read(null, initOnInputStream));
            Assert.assertEquals("Array initialized BinaryDecoder result does not match", datum, reader.read(null, initOnArray));
        }
    }

    @Test
    public void testInputStreamProxy() throws IOException {
        Decoder d = newDecoder(TestBinaryDecoder.data);
        if (d instanceof BinaryDecoder) {
            BinaryDecoder bd = ((BinaryDecoder) (d));
            InputStream test = bd.inputStream();
            InputStream check = new ByteArrayInputStream(TestBinaryDecoder.data);
            validateInputStreamReads(test, check);
            bd = factory.binaryDecoder(TestBinaryDecoder.data, bd);
            test = bd.inputStream();
            check = new ByteArrayInputStream(TestBinaryDecoder.data);
            validateInputStreamSkips(test, check);
            // with input stream sources
            bd = factory.binaryDecoder(new ByteArrayInputStream(TestBinaryDecoder.data), bd);
            test = bd.inputStream();
            check = new ByteArrayInputStream(TestBinaryDecoder.data);
            validateInputStreamReads(test, check);
            bd = factory.binaryDecoder(new ByteArrayInputStream(TestBinaryDecoder.data), bd);
            test = bd.inputStream();
            check = new ByteArrayInputStream(TestBinaryDecoder.data);
            validateInputStreamSkips(test, check);
        }
    }

    @Test
    public void testInputStreamProxyDetached() throws IOException {
        Decoder d = newDecoder(TestBinaryDecoder.data);
        if (d instanceof BinaryDecoder) {
            BinaryDecoder bd = ((BinaryDecoder) (d));
            InputStream test = bd.inputStream();
            InputStream check = new ByteArrayInputStream(TestBinaryDecoder.data);
            // detach input stream and decoder from old source
            factory.binaryDecoder(new byte[56], null);
            InputStream bad = bd.inputStream();
            InputStream check2 = new ByteArrayInputStream(TestBinaryDecoder.data);
            validateInputStreamReads(test, check);
            Assert.assertFalse(((bad.read()) == (check2.read())));
        }
    }

    @Test
    public void testInputStreamPartiallyUsed() throws IOException {
        BinaryDecoder bd = factory.binaryDecoder(new ByteArrayInputStream(TestBinaryDecoder.data), null);
        InputStream test = bd.inputStream();
        InputStream check = new ByteArrayInputStream(TestBinaryDecoder.data);
        // triggers buffer fill if unused and tests isEnd()
        try {
            Assert.assertFalse(bd.isEnd());
        } catch (UnsupportedOperationException e) {
            // this is ok if its a DirectBinaryDecoder.
            if ((bd.getClass()) != (DirectBinaryDecoder.class)) {
                throw e;
            }
        }
        bd.readFloat();// use data, and otherwise trigger buffer fill

        check.skip(4);// skip the same # of bytes here

        validateInputStreamReads(test, check);
    }

    @Test
    public void testBadIntEncoding() throws IOException {
        byte[] badint = new byte[5];
        Arrays.fill(badint, ((byte) (255)));
        Decoder bd = factory.binaryDecoder(badint, null);
        String message = "";
        try {
            bd.readInt();
        } catch (IOException ioe) {
            message = ioe.getMessage();
        }
        Assert.assertEquals("Invalid int encoding", message);
    }

    @Test
    public void testBadLongEncoding() throws IOException {
        byte[] badint = new byte[10];
        Arrays.fill(badint, ((byte) (255)));
        Decoder bd = factory.binaryDecoder(badint, null);
        String message = "";
        try {
            bd.readLong();
        } catch (IOException ioe) {
            message = ioe.getMessage();
        }
        Assert.assertEquals("Invalid long encoding", message);
    }

    @Test
    public void testNegativeLengthEncoding() throws IOException {
        byte[] bad = new byte[]{ ((byte) (1)) };
        Decoder bd = factory.binaryDecoder(bad, null);
        String message = "";
        try {
            bd.readString();
        } catch (AvroRuntimeException e) {
            message = e.getMessage();
        }
        Assert.assertEquals("Malformed data. Length is negative: -1", message);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLongLengthEncoding() throws IOException {
        // Size equivalent to Integer.MAX_VALUE + 1
        byte[] bad = new byte[]{ ((byte) (-128)), ((byte) (-128)), ((byte) (-128)), ((byte) (-128)), ((byte) (16)) };
        Decoder bd = factory.binaryDecoder(bad, null);
        bd.readString();
    }

    @Test(expected = EOFException.class)
    public void testIntTooShort() throws IOException {
        byte[] badint = new byte[4];
        Arrays.fill(badint, ((byte) (255)));
        newDecoder(badint).readInt();
    }

    @Test(expected = EOFException.class)
    public void testLongTooShort() throws IOException {
        byte[] badint = new byte[9];
        Arrays.fill(badint, ((byte) (255)));
        newDecoder(badint).readLong();
    }

    @Test(expected = EOFException.class)
    public void testFloatTooShort() throws IOException {
        byte[] badint = new byte[3];
        Arrays.fill(badint, ((byte) (255)));
        newDecoder(badint).readInt();
    }

    @Test(expected = EOFException.class)
    public void testDoubleTooShort() throws IOException {
        byte[] badint = new byte[7];
        Arrays.fill(badint, ((byte) (255)));
        newDecoder(badint).readLong();
    }

    @Test
    public void testSkipping() throws IOException {
        Decoder d = newDecoder(TestBinaryDecoder.data);
        skipGenerated(d);
        if (d instanceof BinaryDecoder) {
            BinaryDecoder bd = ((BinaryDecoder) (d));
            try {
                Assert.assertTrue(bd.isEnd());
            } catch (UnsupportedOperationException e) {
                // this is ok if its a DirectBinaryDecoder.
                if ((bd.getClass()) != (DirectBinaryDecoder.class)) {
                    throw e;
                }
            }
            bd = factory.binaryDecoder(new ByteArrayInputStream(TestBinaryDecoder.data), bd);
            skipGenerated(bd);
            try {
                Assert.assertTrue(bd.isEnd());
            } catch (UnsupportedOperationException e) {
                // this is ok if its a DirectBinaryDecoder.
                if ((bd.getClass()) != (DirectBinaryDecoder.class)) {
                    throw e;
                }
            }
        }
    }

    @Test(expected = EOFException.class)
    public void testEOF() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Encoder e = EncoderFactory.get().binaryEncoder(baos, null);
        e.writeLong(4503599627370496L);
        e.flush();
        Decoder d = newDecoder(new ByteArrayInputStream(baos.toByteArray()));
        Assert.assertEquals(4503599627370496L, d.readLong());
        d.readInt();
    }
}

