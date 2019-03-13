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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class TestBinaryEncoderFidelity {
    static byte[] legacydata;

    static byte[] complexdata;

    EncoderFactory factory = EncoderFactory.get();

    @Test
    public void testBinaryEncoder() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryEncoder e = factory.binaryEncoder(baos, null);
        TestBinaryEncoderFidelity.generateData(e, true);
        byte[] result = baos.toByteArray();
        Assert.assertEquals(TestBinaryEncoderFidelity.legacydata.length, result.length);
        Assert.assertArrayEquals(TestBinaryEncoderFidelity.legacydata, result);
        baos.reset();
        TestBinaryEncoderFidelity.generateComplexData(e);
        byte[] result2 = baos.toByteArray();
        Assert.assertEquals(TestBinaryEncoderFidelity.complexdata.length, result2.length);
        Assert.assertArrayEquals(TestBinaryEncoderFidelity.complexdata, result2);
    }

    @Test
    public void testDirectBinaryEncoder() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryEncoder e = factory.directBinaryEncoder(baos, null);
        TestBinaryEncoderFidelity.generateData(e, true);
        byte[] result = baos.toByteArray();
        Assert.assertEquals(TestBinaryEncoderFidelity.legacydata.length, result.length);
        Assert.assertArrayEquals(TestBinaryEncoderFidelity.legacydata, result);
        baos.reset();
        TestBinaryEncoderFidelity.generateComplexData(e);
        byte[] result2 = baos.toByteArray();
        Assert.assertEquals(TestBinaryEncoderFidelity.complexdata.length, result2.length);
        Assert.assertArrayEquals(TestBinaryEncoderFidelity.complexdata, result2);
    }

    @Test
    public void testBlockingBinaryEncoder() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryEncoder e = factory.blockingBinaryEncoder(baos, null);
        TestBinaryEncoderFidelity.generateData(e, true);
        byte[] result = baos.toByteArray();
        Assert.assertEquals(TestBinaryEncoderFidelity.legacydata.length, result.length);
        Assert.assertArrayEquals(TestBinaryEncoderFidelity.legacydata, result);
        baos.reset();
        TestBinaryEncoderFidelity.generateComplexData(e);
        byte[] result2 = baos.toByteArray();
        // blocking will cause different length, should be two bytes larger
        Assert.assertEquals(((TestBinaryEncoderFidelity.complexdata.length) + 2), result2.length);
        // the first byte is the array start, with the count of items negative
        Assert.assertEquals(((TestBinaryEncoderFidelity.complexdata[0]) >>> 1), result2[0]);
    }
}

