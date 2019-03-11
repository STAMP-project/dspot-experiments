/**
 * Copyright 2011 Google Inc.
 * Copyright 2018 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitcoinj.core;


import org.junit.Assert;
import org.junit.Test;


public class VarIntTest {
    @Test
    public void testBytes() throws Exception {
        VarInt a = new VarInt(10);// with widening conversion

        Assert.assertEquals(1, a.getSizeInBytes());
        Assert.assertEquals(1, a.encode().length);
        Assert.assertEquals(10, new VarInt(a.encode(), 0).value);
    }

    @Test
    public void testShorts() throws Exception {
        VarInt a = new VarInt(64000);// with widening conversion

        Assert.assertEquals(3, a.getSizeInBytes());
        Assert.assertEquals(3, a.encode().length);
        Assert.assertEquals(64000, new VarInt(a.encode(), 0).value);
    }

    @Test
    public void testShortFFFF() throws Exception {
        VarInt a = new VarInt(65535L);
        Assert.assertEquals(3, a.getSizeInBytes());
        Assert.assertEquals(3, a.encode().length);
        Assert.assertEquals(65535L, new VarInt(a.encode(), 0).value);
    }

    @Test
    public void testInts() throws Exception {
        VarInt a = new VarInt(2864434397L);
        Assert.assertEquals(5, a.getSizeInBytes());
        Assert.assertEquals(5, a.encode().length);
        byte[] bytes = a.encode();
        Assert.assertEquals(2864434397L, (4294967295L & (new VarInt(bytes, 0).value)));
    }

    @Test
    public void testIntFFFFFFFF() throws Exception {
        VarInt a = new VarInt(4294967295L);
        Assert.assertEquals(5, a.getSizeInBytes());
        Assert.assertEquals(5, a.encode().length);
        byte[] bytes = a.encode();
        Assert.assertEquals(4294967295L, (4294967295L & (new VarInt(bytes, 0).value)));
    }

    @Test
    public void testLong() throws Exception {
        VarInt a = new VarInt(-3819410105021120785L);
        Assert.assertEquals(9, a.getSizeInBytes());
        Assert.assertEquals(9, a.encode().length);
        byte[] bytes = a.encode();
        Assert.assertEquals(-3819410105021120785L, new VarInt(bytes, 0).value);
    }

    @Test
    public void testSizeOfNegativeInt() throws Exception {
        // shouldn't normally be passed, but at least stay consistent (bug regression test)
        Assert.assertEquals(VarInt.sizeOf((-1)), new VarInt((-1)).encode().length);
    }
}

