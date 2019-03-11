/**
 * Copyright 2017 The Netty Project
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


import org.junit.Test;


public abstract class ByteBufAllocatorTest {
    @Test
    public void testBuffer() {
        testBuffer(true);
        testBuffer(false);
    }

    @Test
    public void testBufferWithCapacity() {
        testBufferWithCapacity(true, 8);
        testBufferWithCapacity(false, 8);
    }

    @Test
    public void testHeapBuffer() {
        testHeapBuffer(true);
        testHeapBuffer(false);
    }

    @Test
    public void testHeapBufferMaxCapacity() {
        testHeapBuffer(true, 8);
        testHeapBuffer(false, 8);
    }

    @Test
    public void testDirectBuffer() {
        testDirectBuffer(true);
        testDirectBuffer(false);
    }

    @Test
    public void testDirectBufferMaxCapacity() {
        testDirectBuffer(true, 8);
        testDirectBuffer(false, 8);
    }

    @Test
    public void testCompositeBuffer() {
        testCompositeBuffer(true);
        testCompositeBuffer(false);
    }

    @Test
    public void testCompositeBufferWithCapacity() {
        testCompositeHeapBufferWithCapacity(true, 8);
        testCompositeHeapBufferWithCapacity(false, 8);
    }

    @Test
    public void testCompositeHeapBuffer() {
        testCompositeHeapBuffer(true);
        testCompositeHeapBuffer(false);
    }

    @Test
    public void testCompositeHeapBufferWithCapacity() {
        testCompositeHeapBufferWithCapacity(true, 8);
        testCompositeHeapBufferWithCapacity(false, 8);
    }

    @Test
    public void testCompositeDirectBuffer() {
        testCompositeDirectBuffer(true);
        testCompositeDirectBuffer(false);
    }

    @Test
    public void testCompositeDirectBufferWithCapacity() {
        testCompositeDirectBufferWithCapacity(true, 8);
        testCompositeDirectBufferWithCapacity(false, 8);
    }
}

