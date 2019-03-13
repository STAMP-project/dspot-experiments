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
package org.apache.beam.sdk.util;


import Charsets.UTF_8;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link BufferedElementCountingOutputStream}.
 */
@RunWith(JUnit4.class)
public class BufferedElementCountingOutputStreamTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static final int BUFFER_SIZE = 8;

    @Test
    public void testEmptyValues() throws Exception {
        testValues(Collections.emptyList());
    }

    @Test
    public void testSingleValue() throws Exception {
        testValues(toBytes("abc"));
    }

    @Test
    public void testSingleValueGreaterThanBuffer() throws Exception {
        testValues(toBytes("abcdefghijklmnopqrstuvwxyz"));
    }

    @Test
    public void testMultipleValuesLessThanBuffer() throws Exception {
        testValues(toBytes("a", "b", "c"));
    }

    @Test
    public void testMultipleValuesThatBecomeGreaterThanBuffer() throws Exception {
        testValues(toBytes("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));
    }

    @Test
    public void testMultipleRandomSizedValues() throws Exception {
        Random r = new Random(234589023580234890L);
        byte[] randomData = new byte[r.nextInt(18)];
        for (int i = 0; i < 1000; ++i) {
            List<byte[]> bytes = new ArrayList<>();
            for (int j = 0; j < 100; ++j) {
                r.nextBytes(randomData);
                bytes.add(randomData);
            }
            testValues(bytes);
        }
    }

    @Test
    public void testFlushInMiddleOfElement() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedElementCountingOutputStream os = new BufferedElementCountingOutputStream(bos);
        os.markElementStart();
        os.write(1);
        os.flush();
        os.write(2);
        os.close();
        Assert.assertArrayEquals(new byte[]{ 1, 1, 2, 0 }, bos.toByteArray());
    }

    @Test
    public void testFlushInMiddleOfElementUsingByteArrays() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedElementCountingOutputStream os = new BufferedElementCountingOutputStream(bos);
        os.markElementStart();
        os.write(new byte[]{ 1 });
        os.flush();
        os.write(new byte[]{ 2 });
        os.close();
        Assert.assertArrayEquals(new byte[]{ 1, 1, 2, 0 }, bos.toByteArray());
    }

    @Test
    public void testFlushingWhenFinishedIsNoOp() throws Exception {
        BufferedElementCountingOutputStream os = testValues(toBytes("a"));
        os.flush();
        os.flush();
        os.flush();
    }

    @Test
    public void testFinishingWhenFinishedIsNoOp() throws Exception {
        BufferedElementCountingOutputStream os = testValues(toBytes("a"));
        os.finish();
        os.finish();
        os.finish();
    }

    @Test
    public void testClosingFinishesTheStream() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedElementCountingOutputStream os = createAndWriteValues(toBytes("abcdefghij"), baos);
        os.close();
        verifyValues(toBytes("abcdefghij"), new ByteArrayInputStream(baos.toByteArray()));
    }

    @Test
    public void testAddingElementWhenFinishedThrows() throws Exception {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("Stream has been finished.");
        testValues(toBytes("a")).markElementStart();
    }

    @Test
    public void testWritingByteWhenFinishedThrows() throws Exception {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("Stream has been finished.");
        testValues(toBytes("a")).write(1);
    }

    @Test
    public void testWritingBytesWhenFinishedThrows() throws Exception {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("Stream has been finished.");
        testValues(toBytes("a")).write("b".getBytes(UTF_8));
    }

    @Test
    public void testBuffersAreTakenAndReturned() throws Exception {
        BufferedElementCountingOutputStream.BUFFER_POOL.clear();
        BufferedElementCountingOutputStream.BUFFER_POOL.offer(ByteBuffer.allocate(256));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedElementCountingOutputStream os = createAndWriteValues(toBytes("abcdefghij"), baos);
        Assert.assertEquals(0, BufferedElementCountingOutputStream.BUFFER_POOL.size());
        os.finish();
        Assert.assertEquals(1, BufferedElementCountingOutputStream.BUFFER_POOL.size());
    }

    @Test
    public void testBehaviorWhenBufferPoolFull() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((BufferedElementCountingOutputStream.BUFFER_POOL.remainingCapacity()) > 0) {
            BufferedElementCountingOutputStream.BUFFER_POOL.offer(ByteBuffer.allocate(256));
        } 
        BufferedElementCountingOutputStream os = createAndWriteValues(toBytes("abcdefghij"), baos);
        os.finish();
        Assert.assertEquals(0, BufferedElementCountingOutputStream.BUFFER_POOL.remainingCapacity());
    }

    @Test
    public void testBehaviorWhenBufferPoolEmpty() throws Exception {
        BufferedElementCountingOutputStream.BUFFER_POOL.clear();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedElementCountingOutputStream os = createAndWriteValues(toBytes("abcdefghij"), baos);
        Assert.assertEquals(0, BufferedElementCountingOutputStream.BUFFER_POOL.size());
        os.finish();
        Assert.assertEquals(1, BufferedElementCountingOutputStream.BUFFER_POOL.size());
    }
}

