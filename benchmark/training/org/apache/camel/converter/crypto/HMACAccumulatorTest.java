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
package org.apache.camel.converter.crypto;


import java.security.Key;
import org.apache.camel.converter.crypto.HMACAccumulator.CircularBuffer;
import org.junit.Assert;
import org.junit.Test;


public class HMACAccumulatorTest {
    private byte[] payload = new byte[]{ 0, 0, 17, 17, 34, 34, 51, 51, 68, 68, 85, 85, 102, 102, 119, 119, ((byte) (136)), ((byte) (136)), ((byte) (153)), ((byte) (153)) };

    private byte[] expected;

    private Key key;

    @Test
    public void testEncryptionPhaseCalculation() throws Exception {
        int buffersize = 256;
        byte[] buffer = new byte[buffersize];
        System.arraycopy(payload, 0, buffer, 0, payload.length);
        HMACAccumulator builder = new HMACAccumulator(key, "HmacSHA1", null, buffersize);
        builder.encryptUpdate(buffer, 20);
        assertMacs(expected, builder.getCalculatedMac());
    }

    @Test
    public void testDecryptionWhereBufferSizeIsGreaterThanDataSize() throws Exception {
        int buffersize = 256;
        byte[] buffer = initializeBuffer(buffersize);
        HMACAccumulator builder = new HMACAccumulator(key, "HmacSHA1", null, buffersize);
        builder.decryptUpdate(buffer, 40);
        validate(builder);
    }

    @Test
    public void testDecryptionWhereMacOverlaps() throws Exception {
        int buffersize = 32;
        byte[] buffer = new byte[buffersize];
        int overlap = buffersize - (payload.length);
        System.arraycopy(payload, 0, buffer, 0, payload.length);
        System.arraycopy(expected, 0, buffer, payload.length, overlap);
        HMACAccumulator builder = new HMACAccumulator(key, "HmacSHA1", null, buffersize);
        builder.decryptUpdate(buffer, buffersize);
        System.arraycopy(expected, overlap, buffer, 0, (20 - overlap));
        builder.decryptUpdate(buffer, (20 - overlap));
        validate(builder);
    }

    @Test
    public void testDecryptionWhereDataIsMultipleOfBufferLength() throws Exception {
        int buffersize = 20;
        byte[] buffer = new byte[buffersize];
        System.arraycopy(payload, 0, buffer, 0, payload.length);
        HMACAccumulator builder = new HMACAccumulator(key, "HmacSHA1", null, buffersize);
        builder.decryptUpdate(buffer, buffersize);
        System.arraycopy(expected, 0, buffer, 0, expected.length);
        builder.decryptUpdate(buffer, 20);
        validate(builder);
    }

    @Test
    public void testDecryptionWhereThereIsNoPayloadData() throws Exception {
        int buffersize = 20;
        byte[] buffer = new byte[buffersize];
        payload = new byte[0];
        createExpectedMac();
        HMACAccumulator builder = new HMACAccumulator(key, "HmacSHA1", null, buffersize);
        System.arraycopy(expected, 0, buffer, 0, expected.length);
        builder.decryptUpdate(buffer, 20);
        validate(builder);
    }

    @Test
    public void testDecryptionMultipleReadsSmallerThanBufferSize() throws Exception {
        int buffersize = 256;
        byte[] buffer = new byte[buffersize];
        HMACAccumulator builder = new HMACAccumulator(key, "HmacSHA1", null, buffersize);
        int read = (payload.length) / 2;
        System.arraycopy(payload, 0, buffer, 0, read);
        builder.decryptUpdate(buffer, read);
        System.arraycopy(payload, read, buffer, 0, read);
        builder.decryptUpdate(buffer, read);
        System.arraycopy(expected, 0, buffer, 0, expected.length);
        builder.decryptUpdate(buffer, 20);
        validate(builder);
    }

    @Test
    public void testBufferAdd() throws Exception {
        CircularBuffer buffer = new CircularBuffer(((payload.length) * 2));
        buffer.write(payload, 0, payload.length);
        Assert.assertEquals(payload.length, buffer.availableForWrite());
        buffer.write(payload, 0, payload.length);
        Assert.assertEquals(0, buffer.availableForWrite());
        buffer.write(payload, 0, payload.length);
        Assert.assertEquals(0, buffer.availableForWrite());
    }

    @Test
    public void testBufferDrain() throws Exception {
        CircularBuffer buffer = new CircularBuffer(((payload.length) * 2));
        buffer.write(payload, 0, payload.length);
        byte[] data = new byte[(payload.length) >> 1];
        Assert.assertEquals(data.length, buffer.read(data, 0, data.length));
        Assert.assertEquals(data.length, buffer.read(data, 0, data.length));
        Assert.assertEquals(0, buffer.read(data, 0, data.length));
    }

    @Test
    public void testBufferCompare() throws Exception {
        CircularBuffer buffer = new CircularBuffer(((payload.length) * 2));
        buffer.write(new byte[(payload.length) >> 1], 0, ((payload.length) >> 1));
        buffer.write(payload, 0, payload.length);
        buffer.compareTo(payload, 0, payload.length);
    }
}

