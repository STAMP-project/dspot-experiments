/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.android.ziputils;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.ZipInputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static DataDescriptor.SIGNATURE;


/**
 * Unit tests for {@link DataDescriptor}.
 */
@RunWith(JUnit4.class)
public class DataDescriptorTest {
    /**
     * Test of viewOf method, of class DataDescriptor.
     */
    @Test
    public void testViewOf() {
        int[] markers = new int[]{ 12345678, SIGNATURE, 0 };
        for (int marker : markers) {
            ByteBuffer buffer = ByteBuffer.allocate(50).order(ByteOrder.LITTLE_ENDIAN);
            for (int i = 0; i < 50; i++) {
                buffer.put(((byte) (i)));
            }
            int offset = 20;
            buffer.putInt(offset, marker);
            buffer.position(offset);
            DataDescriptor view = DataDescriptor.viewOf(buffer);
            int expMark = (marker == (SIGNATURE)) ? ((int) (ZipInputStream.EXTSIG)) : -1;
            int expSize = (marker == (SIGNATURE)) ? ZipInputStream.EXTHDR : (ZipInputStream.EXTHDR) - 4;
            int expPos = 0;
            assertWithMessage((("not based at current position[" + marker) + "]")).that(view.get(DataDescriptor.EXTSIG)).isEqualTo(expMark);
            assertWithMessage((("Not slice with position 0[" + marker) + "]")).that(view.buffer.position()).isEqualTo(expPos);
            assertWithMessage((("Not sized with comment[" + marker) + "]")).that(view.getSize()).isEqualTo(expSize);
            assertWithMessage((("Not limited to size[" + marker) + "]")).that(view.buffer.limit()).isEqualTo(expSize);
        }
    }

    /**
     * Test of view method, of class DataDescriptor.
     */
    @Test
    public void testView_0args() {
        DataDescriptor view = DataDescriptor.allocate();
        int expSize = ZipInputStream.EXTHDR;
        int expPos = 0;
        int expMarker = ((int) (ZipInputStream.EXTSIG));
        assertWithMessage("no marker").that(view.hasMarker()).isTrue();
        assertWithMessage("No marker").that(view.get(DataDescriptor.EXTSIG)).isEqualTo(expMarker);
        assertWithMessage("Not at position 0").that(view.buffer.position()).isEqualTo(expPos);
        assertWithMessage("Not sized correctly").that(view.getSize()).isEqualTo(expSize);
        assertWithMessage("Not limited to size").that(view.buffer.limit()).isEqualTo(expSize);
    }

    /**
     * Test of view method, of class DataDescriptor.
     */
    @Test
    public void testView_ByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(100).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < 100; i++) {
            buffer.put(((byte) (i)));
        }
        buffer.position(50);
        DataDescriptor view = DataDescriptor.view(buffer);
        int expMark = ((int) (ZipInputStream.EXTSIG));
        int expSize = ZipInputStream.EXTHDR;
        int expPos = 0;
        assertWithMessage("not based at current position").that(view.get(DataDescriptor.EXTSIG)).isEqualTo(expMark);
        assertWithMessage("Not slice with position 0").that(view.buffer.position()).isEqualTo(expPos);
        assertWithMessage("Not sized with comment").that(view.getSize()).isEqualTo(expSize);
        assertWithMessage("Not limited to size").that(view.buffer.limit()).isEqualTo(expSize);
    }

    /**
     * Test of copy method, of class DataDescriptor.
     */
    @Test
    public void testCopy() {
        ByteBuffer buffer = ByteBuffer.allocate(100).order(ByteOrder.LITTLE_ENDIAN);
        DataDescriptor view = DataDescriptor.allocate();
        view.copy(buffer);
        int expSize = view.getSize();
        assertWithMessage("buffer not advanced as expected").that(buffer.position()).isEqualTo(expSize);
        buffer.position(0);
        DataDescriptor clone = DataDescriptor.viewOf(buffer);
        assertWithMessage("Fail to copy mark").that(clone.get(DataDescriptor.EXTSIG)).isEqualTo(view.get(DataDescriptor.EXTSIG));
    }

    /**
     * Test of with and get methods.
     */
    @Test
    public void testWithAndGetMethods() {
        int crc = 305419896;
        int compressed = 56095189;
        int uncompressed = 1954623833;
        DataDescriptor view = DataDescriptor.allocate().set(DataDescriptor.EXTCRC, crc).set(DataDescriptor.EXTSIZ, compressed).set(DataDescriptor.EXTLEN, uncompressed);
        assertWithMessage("CRC").that(view.get(DataDescriptor.EXTCRC)).isEqualTo(crc);
        assertWithMessage("Compressed size").that(view.get(DataDescriptor.EXTSIZ)).isEqualTo(compressed);
        assertWithMessage("Uncompressed size").that(view.get(DataDescriptor.EXTLEN)).isEqualTo(uncompressed);
    }
}

