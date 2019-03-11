/**
 * Copyright 2016 The Netty Project
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
package io.netty.buffer;


import java.util.ArrayDeque;
import java.util.Queue;
import org.junit.Assert;
import org.junit.Test;


public class SimpleLeakAwareByteBufTest extends BigEndianHeapByteBufTest {
    private final Class<? extends ByteBuf> clazz = leakClass();

    private final Queue<NoopResourceLeakTracker<ByteBuf>> trackers = new ArrayDeque<NoopResourceLeakTracker<ByteBuf>>();

    @Test
    public void testWrapSlice() {
        assertWrapped(newBuffer(8).slice());
    }

    @Test
    public void testWrapSlice2() {
        assertWrapped(newBuffer(8).slice(0, 1));
    }

    @Test
    public void testWrapReadSlice() {
        ByteBuf buffer = newBuffer(8);
        if (buffer.isReadable()) {
            assertWrapped(buffer.readSlice(1));
        } else {
            Assert.assertTrue(buffer.release());
        }
    }

    @Test
    public void testWrapRetainedSlice() {
        ByteBuf buffer = newBuffer(8);
        assertWrapped(buffer.retainedSlice());
        Assert.assertTrue(buffer.release());
    }

    @Test
    public void testWrapRetainedSlice2() {
        ByteBuf buffer = newBuffer(8);
        if (buffer.isReadable()) {
            assertWrapped(buffer.retainedSlice(0, 1));
        }
        Assert.assertTrue(buffer.release());
    }

    @Test
    public void testWrapReadRetainedSlice() {
        ByteBuf buffer = newBuffer(8);
        if (buffer.isReadable()) {
            assertWrapped(buffer.readRetainedSlice(1));
        }
        Assert.assertTrue(buffer.release());
    }

    @Test
    public void testWrapDuplicate() {
        assertWrapped(newBuffer(8).duplicate());
    }

    @Test
    public void testWrapRetainedDuplicate() {
        ByteBuf buffer = newBuffer(8);
        assertWrapped(buffer.retainedDuplicate());
        Assert.assertTrue(buffer.release());
    }

    @Test
    public void testWrapReadOnly() {
        assertWrapped(newBuffer(8).asReadOnly());
    }
}

