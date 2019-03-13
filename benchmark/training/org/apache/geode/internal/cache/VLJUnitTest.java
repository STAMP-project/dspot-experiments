/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.geode.internal.InternalDataSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for the new variable length format
 *
 * TODO these tests need some work. I don't think they really represent edge cases for this variable
 * length value.
 */
public class VLJUnitTest {
    private ByteArrayOutputStream baos;

    private DataOutputStream dos;

    @Test
    public void testZero() throws IOException {
        InternalDataSerializer.writeUnsignedVL(0, createDOS());
        Assert.assertEquals(0, InternalDataSerializer.readUnsignedVL(createDIS()));
    }

    @Test
    public void testOne() throws IOException {
        InternalDataSerializer.writeUnsignedVL(1, createDOS());
        Assert.assertEquals(1, InternalDataSerializer.readUnsignedVL(createDIS()));
    }

    @Test
    public void testMinusOne() throws IOException {
        InternalDataSerializer.writeUnsignedVL((-1), createDOS());
        Assert.assertEquals((-1), InternalDataSerializer.readUnsignedVL(createDIS()));
    }

    @Test
    public void testMaxByte() throws IOException {
        InternalDataSerializer.writeUnsignedVL(127, createDOS());
        Assert.assertEquals(127, InternalDataSerializer.readUnsignedVL(createDIS()));
    }

    @Test
    public void testMaxNegativeByte() throws IOException {
        InternalDataSerializer.writeUnsignedVL((-127), createDOS());
        Assert.assertEquals((-127), InternalDataSerializer.readUnsignedVL(createDIS()));
    }

    @Test
    public void testMinShort() throws IOException {
        InternalDataSerializer.writeUnsignedVL(255, createDOS());
        Assert.assertEquals(255, InternalDataSerializer.readUnsignedVL(createDIS()));
    }

    @Test
    public void testMinNegativeShort() throws IOException {
        InternalDataSerializer.writeUnsignedVL((-255), createDOS());
        Assert.assertEquals((-255), InternalDataSerializer.readUnsignedVL(createDIS()));
    }

    @Test
    public void testMaxShort() throws IOException {
        InternalDataSerializer.writeUnsignedVL(32767, createDOS());
        Assert.assertEquals(32767, InternalDataSerializer.readUnsignedVL(createDIS()));
    }

    @Test
    public void testMaxNegativeShort() throws IOException {
        InternalDataSerializer.writeUnsignedVL((-32767), createDOS());
        Assert.assertEquals((-32767), InternalDataSerializer.readUnsignedVL(createDIS()));
    }

    @Test
    public void testMin3Byte() throws IOException {
        InternalDataSerializer.writeUnsignedVL(65535, createDOS());
        Assert.assertEquals(65535, InternalDataSerializer.readUnsignedVL(createDIS()));
    }

    @Test
    public void testMin3Negative() throws IOException {
        InternalDataSerializer.writeUnsignedVL((-65535), createDOS());
        Assert.assertEquals((-65535), InternalDataSerializer.readUnsignedVL(createDIS()));
    }

    @Test
    public void testMaxInt() throws IOException {
        InternalDataSerializer.writeUnsignedVL(2147483647, createDOS());
        Assert.assertEquals(2147483647, InternalDataSerializer.readUnsignedVL(createDIS()));
    }

    @Test
    public void testMinLong() throws IOException {
        InternalDataSerializer.writeUnsignedVL((2147483647L + 1), createDOS());
        Assert.assertEquals((2147483647L + 1), InternalDataSerializer.readUnsignedVL(createDIS()));
    }

    @Test
    public void testMaxLong() throws IOException {
        InternalDataSerializer.writeUnsignedVL(Long.MAX_VALUE, createDOS());
        Assert.assertEquals(Long.MAX_VALUE, InternalDataSerializer.readUnsignedVL(createDIS()));
    }
}

