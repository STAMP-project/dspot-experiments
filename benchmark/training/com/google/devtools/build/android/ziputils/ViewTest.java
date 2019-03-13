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
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link View}.
 */
@RunWith(JUnit4.class)
public class ViewTest {
    private static final FakeFileSystem fileSystem = new FakeFileSystem();

    @Test
    public void testView() {
        // View takes ownership of constructor argument!
        // Subclasses are responsible for slicing, when needed.
        ByteBuffer buffer = ByteBuffer.allocate(100);
        ViewTest.TestView instance = new ViewTest.TestView(buffer);
        buffer.putInt(12345678);
        int fromBuf = buffer.getInt(0);
        int fromView = instance.getInt(0);
        assertWithMessage("must assume buffer ownership").that(fromView).isEqualTo(fromBuf);
        int posBuf = buffer.position();
        int posView = instance.buffer.position();
        assertWithMessage("must assume buffer ownership").that(posView).isEqualTo(posBuf);
    }

    @Test
    public void testAt() {
        long fileOffset = 0L;
        ByteBuffer buffer = ByteBuffer.allocate(100);
        ViewTest.TestView instance = new ViewTest.TestView(buffer);
        View<ViewTest.TestView> result = at(fileOffset);
        assertWithMessage("didn't return this").that(result).isSameAs(instance);
        long resultValue = fileOffset();
        assertWithMessage("didn't return set value").that(resultValue).isEqualTo(fileOffset);
    }

    @Test
    public void testFileOffset() {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        ViewTest.TestView instance = new ViewTest.TestView(buffer);
        long expResult = -1L;
        long result = fileOffset();
        assertWithMessage("default file offset should be -1").that(result).isEqualTo(expResult);
    }

    @Test
    public void testFinish() {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        ViewTest.TestView instance = new ViewTest.TestView(buffer);
        int limit = instance.buffer.limit();
        int pos = instance.buffer.position();
        assertWithMessage("initial limit").that(limit).isEqualTo(100);
        assertWithMessage("initial position").that(pos).isEqualTo(0);
        instance.putInt(1234);
        limit = instance.buffer.limit();
        pos = instance.buffer.position();
        assertWithMessage("limit unchanged").that(limit).isEqualTo(100);
        assertWithMessage("position advanced").that(pos).isEqualTo(4);
        instance.buffer.flip();
        int finishedLimit = instance.buffer.limit();
        int finishedPos = instance.buffer.position();
        assertWithMessage("must set limit to position").that(finishedLimit).isEqualTo(pos);
        assertWithMessage("must set position to 0").that(finishedPos).isEqualTo(0);
    }

    @Test
    public void testWriteTo() throws Exception {
        FileChannel file = ViewTest.fileSystem.getOutputChannel("hello", false);
        byte[] bytes = "hello world".getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ViewTest.TestView instance = new ViewTest.TestView(buffer);
        int expResult = bytes.length;
        instance.buffer.rewind();
        int result = file.write(instance.buffer);
        file.close();
        assertWithMessage("incorrect number of bytes written").that(result).isEqualTo(expResult);
        byte[] bytesWritten = ViewTest.fileSystem.toByteArray("hello");
        assertWithMessage("incorrect bytes written").that(bytesWritten).isEqualTo(bytes);
    }

    @Test
    public void testGetBytes() {
        int off = 3;
        int len = 5;
        byte[] bytes = "hello world".getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ViewTest.TestView instance = new ViewTest.TestView(buffer);
        byte[] expResult = "lo wo".getBytes(StandardCharsets.UTF_8);
        byte[] result = getBytes(off, len);
        assertWithMessage("incorrect bytes returned").that(result).isEqualTo(expResult);
        try {
            instance.getBytes((((bytes.length) - len) + 1), len);
            Assert.fail("expected Exception");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        try {
            instance.getBytes((-1), len);
            Assert.fail("expected Exception");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
    }

    @Test
    public void testGetString() {
        int off = 6;
        int len = 5;
        byte[] bytes = "hello world".getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ViewTest.TestView instance = new ViewTest.TestView(buffer);
        String expResult = "world";
        String result = getString(off, len);
        assertWithMessage("didn't return this").that(result).isEqualTo(expResult);
        try {
            instance.getString((off + 1), len);
            Assert.fail("expected Exception");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
        try {
            instance.getString((-1), len);
            Assert.fail("expected Exception");
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
    }

    @Test
    public void testByteOrder() {
        byte[] bytes = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        ViewTest.TestView instance = new ViewTest.TestView(ByteBuffer.wrap(bytes));
        int expValue = 134678021;
        int value = instance.getInt(4);
        assertWithMessage("Byte order incorrect").that(value).isEqualTo(expValue);
    }

    static class TestView extends View<ViewTest.TestView> {
        TestView(ByteBuffer buffer) {
            super(buffer);
        }

        // Will advance buffer position
        public void putInt(int value) {
            buffer.putInt(value);
        }

        // Will advance buffer position
        public int getInt() {
            return buffer.getInt();
        }

        // will not advance buffer position
        public void putInt(int index, int value) {
            buffer.putInt(index, value);
        }

        // will not advance buffer position
        public int getInt(int index) {
            return buffer.getInt(index);
        }
    }
}

