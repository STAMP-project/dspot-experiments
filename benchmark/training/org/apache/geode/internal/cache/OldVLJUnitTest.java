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


public class OldVLJUnitTest {
    private ByteArrayOutputStream baos;

    private DataOutputStream dos;

    @Test
    public void testMinByte() throws IOException {
        InternalDataSerializer.writeVLOld(1, createDOS());
        Assert.assertEquals(1, InternalDataSerializer.readVLOld(createDIS()));
    }

    @Test
    public void testMaxByte() throws IOException {
        InternalDataSerializer.writeVLOld(125, createDOS());
        Assert.assertEquals(125, InternalDataSerializer.readVLOld(createDIS()));
    }

    @Test
    public void testMinShort() throws IOException {
        InternalDataSerializer.writeVLOld(126, createDOS());
        Assert.assertEquals(126, InternalDataSerializer.readVLOld(createDIS()));
    }

    @Test
    public void testMaxShort() throws IOException {
        InternalDataSerializer.writeVLOld(32767, createDOS());
        Assert.assertEquals(32767, InternalDataSerializer.readVLOld(createDIS()));
    }

    @Test
    public void testMinInt() throws IOException {
        InternalDataSerializer.writeVLOld((32767 + 1), createDOS());
        Assert.assertEquals((32767 + 1), InternalDataSerializer.readVLOld(createDIS()));
    }

    @Test
    public void testMaxInt() throws IOException {
        InternalDataSerializer.writeVLOld(2147483647, createDOS());
        Assert.assertEquals(2147483647, InternalDataSerializer.readVLOld(createDIS()));
    }

    @Test
    public void testMinLong() throws IOException {
        InternalDataSerializer.writeVLOld((2147483647L + 1), createDOS());
        Assert.assertEquals((2147483647L + 1), InternalDataSerializer.readVLOld(createDIS()));
    }

    @Test
    public void testMaxLong() throws IOException {
        InternalDataSerializer.writeVLOld(Long.MAX_VALUE, createDOS());
        Assert.assertEquals(Long.MAX_VALUE, InternalDataSerializer.readVLOld(createDIS()));
    }
}

