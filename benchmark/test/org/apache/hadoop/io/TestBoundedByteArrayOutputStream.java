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
package org.apache.hadoop.io;


import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for BoundedByteArrayOutputStream
 */
public class TestBoundedByteArrayOutputStream {
    private static final int SIZE = 1024;

    private static final byte[] INPUT = new byte[TestBoundedByteArrayOutputStream.SIZE];

    static {
        new Random().nextBytes(TestBoundedByteArrayOutputStream.INPUT);
    }

    @Test
    public void testBoundedStream() throws IOException {
        BoundedByteArrayOutputStream stream = new BoundedByteArrayOutputStream(TestBoundedByteArrayOutputStream.SIZE);
        // Write to the stream, get the data back and check for contents
        stream.write(TestBoundedByteArrayOutputStream.INPUT, 0, TestBoundedByteArrayOutputStream.SIZE);
        Assert.assertTrue("Array Contents Mismatch", Arrays.equals(TestBoundedByteArrayOutputStream.INPUT, stream.getBuffer()));
        // Try writing beyond end of buffer. Should throw an exception
        boolean caughtException = false;
        try {
            stream.write(TestBoundedByteArrayOutputStream.INPUT[0]);
        } catch (Exception e) {
            caughtException = true;
        }
        Assert.assertTrue("Writing beyond limit did not throw an exception", caughtException);
        // Reset the stream and try, should succeed
        stream.reset();
        Assert.assertTrue("Limit did not get reset correctly", ((stream.getLimit()) == (TestBoundedByteArrayOutputStream.SIZE)));
        stream.write(TestBoundedByteArrayOutputStream.INPUT, 0, TestBoundedByteArrayOutputStream.SIZE);
        Assert.assertTrue("Array Contents Mismatch", Arrays.equals(TestBoundedByteArrayOutputStream.INPUT, stream.getBuffer()));
        // Try writing one more byte, should fail
        caughtException = false;
        try {
            stream.write(TestBoundedByteArrayOutputStream.INPUT[0]);
        } catch (Exception e) {
            caughtException = true;
        }
        // Reset the stream, but set a lower limit. Writing beyond
        // the limit should throw an exception
        stream.reset(((TestBoundedByteArrayOutputStream.SIZE) - 1));
        Assert.assertTrue("Limit did not get reset correctly", ((stream.getLimit()) == ((TestBoundedByteArrayOutputStream.SIZE) - 1)));
        caughtException = false;
        try {
            stream.write(TestBoundedByteArrayOutputStream.INPUT, 0, TestBoundedByteArrayOutputStream.SIZE);
        } catch (Exception e) {
            caughtException = true;
        }
        Assert.assertTrue("Writing beyond limit did not throw an exception", caughtException);
    }

    static class ResettableBoundedByteArrayOutputStream extends BoundedByteArrayOutputStream {
        public ResettableBoundedByteArrayOutputStream(int capacity) {
            super(capacity);
        }

        public void resetBuffer(byte[] buf, int offset, int length) {
            super.resetBuffer(buf, offset, length);
        }
    }

    @Test
    public void testResetBuffer() throws IOException {
        TestBoundedByteArrayOutputStream.ResettableBoundedByteArrayOutputStream stream = new TestBoundedByteArrayOutputStream.ResettableBoundedByteArrayOutputStream(TestBoundedByteArrayOutputStream.SIZE);
        // Write to the stream, get the data back and check for contents
        stream.write(TestBoundedByteArrayOutputStream.INPUT, 0, TestBoundedByteArrayOutputStream.SIZE);
        Assert.assertTrue("Array Contents Mismatch", Arrays.equals(TestBoundedByteArrayOutputStream.INPUT, getBuffer()));
        // Try writing beyond end of buffer. Should throw an exception
        boolean caughtException = false;
        try {
            write(TestBoundedByteArrayOutputStream.INPUT[0]);
        } catch (Exception e) {
            caughtException = true;
        }
        Assert.assertTrue("Writing beyond limit did not throw an exception", caughtException);
        // Reset the stream and try, should succeed
        byte[] newBuf = new byte[TestBoundedByteArrayOutputStream.SIZE];
        stream.resetBuffer(newBuf, 0, newBuf.length);
        Assert.assertTrue("Limit did not get reset correctly", ((getLimit()) == (TestBoundedByteArrayOutputStream.SIZE)));
        stream.write(TestBoundedByteArrayOutputStream.INPUT, 0, TestBoundedByteArrayOutputStream.SIZE);
        Assert.assertTrue("Array Contents Mismatch", Arrays.equals(TestBoundedByteArrayOutputStream.INPUT, getBuffer()));
        // Try writing one more byte, should fail
        caughtException = false;
        try {
            write(TestBoundedByteArrayOutputStream.INPUT[0]);
        } catch (Exception e) {
            caughtException = true;
        }
        Assert.assertTrue("Writing beyond limit did not throw an exception", caughtException);
    }
}

