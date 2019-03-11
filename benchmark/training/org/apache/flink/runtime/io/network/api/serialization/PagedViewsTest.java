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
package org.apache.flink.runtime.io.network.api.serialization;


import SerializationTestTypeFactory.INT;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.flink.core.memory.MemorySegment;
import org.apache.flink.core.memory.MemorySegmentFactory;
import org.apache.flink.runtime.memory.AbstractPagedInputView;
import org.apache.flink.runtime.memory.AbstractPagedOutputView;
import org.apache.flink.testutils.serialization.types.Util;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link AbstractPagedInputView} and {@link AbstractPagedOutputView}.
 */
public class PagedViewsTest {
    @Test
    public void testSequenceOfIntegersWithAlignedBuffers() {
        try {
            final int numInts = 1000000;
            PagedViewsTest.testSequenceOfTypes(Util.randomRecords(numInts, INT), 2048);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Test encountered an unexpected exception.");
        }
    }

    @Test
    public void testSequenceOfIntegersWithUnalignedBuffers() {
        try {
            final int numInts = 1000000;
            PagedViewsTest.testSequenceOfTypes(Util.randomRecords(numInts, INT), 2047);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Test encountered an unexpected exception.");
        }
    }

    @Test
    public void testRandomTypes() {
        try {
            final int numTypes = 100000;
            // test with an odd buffer size to force many unaligned cases
            PagedViewsTest.testSequenceOfTypes(Util.randomRecords(numTypes), 57);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Test encountered an unexpected exception.");
        }
    }

    @Test
    public void testReadFully() {
        int bufferSize = 100;
        byte[] expected = new byte[bufferSize];
        new Random().nextBytes(expected);
        PagedViewsTest.TestOutputView outputView = new PagedViewsTest.TestOutputView(bufferSize);
        try {
            write(expected);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Could not write to TestOutputView.");
        }
        outputView.close();
        PagedViewsTest.TestInputView inputView = new PagedViewsTest.TestInputView(outputView.segments);
        byte[] buffer = new byte[bufferSize];
        try {
            readFully(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Could not read TestInputView.");
        }
        Assert.assertEquals(getCurrentPositionInSegment(), bufferSize);
        Assert.assertArrayEquals(expected, buffer);
    }

    @Test
    public void testReadFullyAcrossSegments() {
        int bufferSize = 100;
        int segmentSize = 30;
        byte[] expected = new byte[bufferSize];
        new Random().nextBytes(expected);
        PagedViewsTest.TestOutputView outputView = new PagedViewsTest.TestOutputView(segmentSize);
        try {
            write(expected);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Could not write to TestOutputView.");
        }
        outputView.close();
        PagedViewsTest.TestInputView inputView = new PagedViewsTest.TestInputView(outputView.segments);
        byte[] buffer = new byte[bufferSize];
        try {
            readFully(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Could not read TestInputView.");
        }
        Assert.assertEquals(getCurrentPositionInSegment(), (bufferSize % segmentSize));
        Assert.assertArrayEquals(expected, buffer);
    }

    @Test
    public void testReadAcrossSegments() {
        int bufferSize = 100;
        int bytes2Write = 75;
        int segmentSize = 30;
        byte[] expected = new byte[bytes2Write];
        new Random().nextBytes(expected);
        PagedViewsTest.TestOutputView outputView = new PagedViewsTest.TestOutputView(segmentSize);
        try {
            write(expected);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Could not write to TestOutputView.");
        }
        outputView.close();
        PagedViewsTest.TestInputView inputView = new PagedViewsTest.TestInputView(outputView.segments);
        byte[] buffer = new byte[bufferSize];
        int bytesRead = 0;
        try {
            bytesRead = read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Could not read TestInputView.");
        }
        Assert.assertEquals(bytes2Write, bytesRead);
        Assert.assertEquals(getCurrentPositionInSegment(), (bytes2Write % segmentSize));
        byte[] tempBuffer = new byte[bytesRead];
        System.arraycopy(buffer, 0, tempBuffer, 0, bytesRead);
        Assert.assertArrayEquals(expected, tempBuffer);
    }

    @Test
    public void testEmptyingInputView() {
        int bufferSize = 100;
        int bytes2Write = 75;
        int segmentSize = 30;
        byte[] expected = new byte[bytes2Write];
        new Random().nextBytes(expected);
        PagedViewsTest.TestOutputView outputView = new PagedViewsTest.TestOutputView(segmentSize);
        try {
            write(expected);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Could not write to TestOutputView.");
        }
        outputView.close();
        PagedViewsTest.TestInputView inputView = new PagedViewsTest.TestInputView(outputView.segments);
        byte[] buffer = new byte[bufferSize];
        int bytesRead = 0;
        try {
            bytesRead = read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Could not read TestInputView.");
        }
        Assert.assertEquals(bytes2Write, bytesRead);
        byte[] tempBuffer = new byte[bytesRead];
        System.arraycopy(buffer, 0, tempBuffer, 0, bytesRead);
        Assert.assertArrayEquals(expected, tempBuffer);
        try {
            bytesRead = read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Input view should be empty and thus return -1.");
        }
        Assert.assertEquals((-1), bytesRead);
        Assert.assertEquals(getCurrentPositionInSegment(), (bytes2Write % segmentSize));
    }

    @Test
    public void testReadFullyWithNotEnoughData() {
        int bufferSize = 100;
        int bytes2Write = 99;
        int segmentSize = 30;
        byte[] expected = new byte[bytes2Write];
        new Random().nextBytes(expected);
        PagedViewsTest.TestOutputView outputView = new PagedViewsTest.TestOutputView(segmentSize);
        try {
            write(expected);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Could not write to TestOutputView.");
        }
        outputView.close();
        PagedViewsTest.TestInputView inputView = new PagedViewsTest.TestInputView(outputView.segments);
        byte[] buffer = new byte[bufferSize];
        boolean eofException = false;
        try {
            readFully(buffer);
        } catch (EOFException e) {
            // Expected exception
            eofException = true;
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Could not read TestInputView.");
        }
        Assert.assertTrue("EOFException should have occurred.", eofException);
        int bytesRead = 0;
        try {
            bytesRead = read(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Could not read TestInputView.");
        }
        Assert.assertEquals((-1), bytesRead);
    }

    @Test
    public void testReadFullyWithOffset() {
        int bufferSize = 100;
        int segmentSize = 30;
        byte[] expected = new byte[bufferSize];
        new Random().nextBytes(expected);
        PagedViewsTest.TestOutputView outputView = new PagedViewsTest.TestOutputView(segmentSize);
        try {
            write(expected);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Could not write to TestOutputView.");
        }
        outputView.close();
        PagedViewsTest.TestInputView inputView = new PagedViewsTest.TestInputView(outputView.segments);
        byte[] buffer = new byte[2 * bufferSize];
        try {
            inputView.readFully(buffer, bufferSize, bufferSize);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Could not read TestInputView.");
        }
        Assert.assertEquals(getCurrentPositionInSegment(), (bufferSize % segmentSize));
        byte[] tempBuffer = new byte[bufferSize];
        System.arraycopy(buffer, bufferSize, tempBuffer, 0, bufferSize);
        Assert.assertArrayEquals(expected, tempBuffer);
    }

    @Test
    public void testReadFullyEmptyView() {
        int segmentSize = 30;
        PagedViewsTest.TestOutputView outputView = new PagedViewsTest.TestOutputView(segmentSize);
        outputView.close();
        PagedViewsTest.TestInputView inputView = new PagedViewsTest.TestInputView(outputView.segments);
        byte[] buffer = new byte[segmentSize];
        boolean eofException = false;
        try {
            readFully(buffer);
        } catch (EOFException e) {
            // expected Exception
            eofException = true;
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception: Could not read TestInputView.");
        }
        Assert.assertTrue("EOFException expected.", eofException);
    }

    // ============================================================================================
    private static final class SegmentWithPosition {
        private final MemorySegment segment;

        private final int position;

        public SegmentWithPosition(MemorySegment segment, int position) {
            this.segment = segment;
            this.position = position;
        }
    }

    private static final class TestOutputView extends AbstractPagedOutputView {
        private final List<PagedViewsTest.SegmentWithPosition> segments = new ArrayList<>();

        private final int segmentSize;

        private TestOutputView(int segmentSize) {
            super(MemorySegmentFactory.allocateUnpooledSegment(segmentSize), segmentSize, 0);
            this.segmentSize = segmentSize;
        }

        @Override
        protected MemorySegment nextSegment(MemorySegment current, int positionInCurrent) throws IOException {
            segments.add(new PagedViewsTest.SegmentWithPosition(current, positionInCurrent));
            return MemorySegmentFactory.allocateUnpooledSegment(segmentSize);
        }

        public void close() {
            segments.add(new PagedViewsTest.SegmentWithPosition(getCurrentSegment(), getCurrentPositionInSegment()));
        }
    }

    private static final class TestInputView extends AbstractPagedInputView {
        private final List<PagedViewsTest.SegmentWithPosition> segments;

        private int num;

        private TestInputView(List<PagedViewsTest.SegmentWithPosition> segments) {
            super(segments.get(0).segment, segments.get(0).position, 0);
            this.segments = segments;
            this.num = 0;
        }

        @Override
        protected MemorySegment nextSegment(MemorySegment current) throws IOException {
            (num)++;
            if ((num) < (segments.size())) {
                return segments.get(num).segment;
            } else {
                throw new EOFException();
            }
        }

        @Override
        protected int getLimitForSegment(MemorySegment segment) {
            return segments.get(num).position;
        }
    }
}

