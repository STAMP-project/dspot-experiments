/**
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okio;


import Segment.SIZE;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Test;

import static Segment.SIZE;


/**
 * Tests solely for the behavior of RealBufferedSink's implementation. For generic
 * BufferedSink behavior use BufferedSinkTest.
 */
public final class RealBufferedSinkTest {
    @Test
    public void inputStreamCloses() throws Exception {
        RealBufferedSink sink = new RealBufferedSink(new Buffer());
        OutputStream out = sink.outputStream();
        out.close();
        try {
            sink.writeUtf8("Hi!");
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertEquals("closed", e.getMessage());
        }
    }

    @Test
    public void bufferedSinkEmitsTailWhenItIsComplete() throws IOException {
        Buffer sink = new Buffer();
        BufferedSink bufferedSink = new RealBufferedSink(sink);
        bufferedSink.writeUtf8(TestUtil.repeat('a', ((SIZE) - 1)));
        Assert.assertEquals(0, sink.size());
        bufferedSink.writeByte(0);
        Assert.assertEquals(SIZE, sink.size());
        Assert.assertEquals(0, bufferedSink.buffer().size());
    }

    @Test
    public void bufferedSinkEmitMultipleSegments() throws IOException {
        Buffer sink = new Buffer();
        BufferedSink bufferedSink = new RealBufferedSink(sink);
        bufferedSink.writeUtf8(TestUtil.repeat('a', (((SIZE) * 4) - 1)));
        Assert.assertEquals(((SIZE) * 3), sink.size());
        Assert.assertEquals(((SIZE) - 1), bufferedSink.buffer().size());
    }

    @Test
    public void bufferedSinkFlush() throws IOException {
        Buffer sink = new Buffer();
        BufferedSink bufferedSink = new RealBufferedSink(sink);
        bufferedSink.writeByte('a');
        Assert.assertEquals(0, sink.size());
        bufferedSink.flush();
        Assert.assertEquals(0, bufferedSink.buffer().size());
        Assert.assertEquals(1, sink.size());
    }

    @Test
    public void bytesEmittedToSinkWithFlush() throws Exception {
        Buffer sink = new Buffer();
        BufferedSink bufferedSink = new RealBufferedSink(sink);
        bufferedSink.writeUtf8("abc");
        bufferedSink.flush();
        Assert.assertEquals(3, sink.size());
    }

    @Test
    public void bytesNotEmittedToSinkWithoutFlush() throws Exception {
        Buffer sink = new Buffer();
        BufferedSink bufferedSink = new RealBufferedSink(sink);
        bufferedSink.writeUtf8("abc");
        Assert.assertEquals(0, sink.size());
    }

    @Test
    public void bytesEmittedToSinkWithEmit() throws Exception {
        Buffer sink = new Buffer();
        BufferedSink bufferedSink = new RealBufferedSink(sink);
        bufferedSink.writeUtf8("abc");
        bufferedSink.emit();
        Assert.assertEquals(3, sink.size());
    }

    @Test
    public void completeSegmentsEmitted() throws Exception {
        Buffer sink = new Buffer();
        BufferedSink bufferedSink = new RealBufferedSink(sink);
        bufferedSink.writeUtf8(TestUtil.repeat('a', ((SIZE) * 3)));
        Assert.assertEquals(((SIZE) * 3), sink.size());
    }

    @Test
    public void incompleteSegmentsNotEmitted() throws Exception {
        Buffer sink = new Buffer();
        BufferedSink bufferedSink = new RealBufferedSink(sink);
        bufferedSink.writeUtf8(TestUtil.repeat('a', (((SIZE) * 3) - 1)));
        Assert.assertEquals(((SIZE) * 2), sink.size());
    }

    @Test
    public void closeWithExceptionWhenWriting() throws IOException {
        MockSink mockSink = new MockSink();
        mockSink.scheduleThrow(0, new IOException());
        BufferedSink bufferedSink = new RealBufferedSink(mockSink);
        bufferedSink.writeByte('a');
        try {
            bufferedSink.close();
            Assert.fail();
        } catch (IOException expected) {
        }
        mockSink.assertLog("write(Buffer[size=1 data=61], 1)", "close()");
    }

    @Test
    public void closeWithExceptionWhenClosing() throws IOException {
        MockSink mockSink = new MockSink();
        mockSink.scheduleThrow(1, new IOException());
        BufferedSink bufferedSink = new RealBufferedSink(mockSink);
        bufferedSink.writeByte('a');
        try {
            bufferedSink.close();
            Assert.fail();
        } catch (IOException expected) {
        }
        mockSink.assertLog("write(Buffer[size=1 data=61], 1)", "close()");
    }

    @Test
    public void closeWithExceptionWhenWritingAndClosing() throws IOException {
        MockSink mockSink = new MockSink();
        mockSink.scheduleThrow(0, new IOException("first"));
        mockSink.scheduleThrow(1, new IOException("second"));
        BufferedSink bufferedSink = new RealBufferedSink(mockSink);
        bufferedSink.writeByte('a');
        try {
            bufferedSink.close();
            Assert.fail();
        } catch (IOException expected) {
            Assert.assertEquals("first", expected.getMessage());
        }
        mockSink.assertLog("write(Buffer[size=1 data=61], 1)", "close()");
    }

    @Test
    public void operationsAfterClose() throws IOException {
        MockSink mockSink = new MockSink();
        BufferedSink bufferedSink = new RealBufferedSink(mockSink);
        bufferedSink.writeByte('a');
        bufferedSink.close();
        // Test a sample set of methods.
        try {
            bufferedSink.writeByte('a');
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            bufferedSink.write(new byte[10]);
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            bufferedSink.emitCompleteSegments();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            bufferedSink.emit();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
        try {
            bufferedSink.flush();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
        // Test a sample set of methods on the OutputStream.
        OutputStream os = bufferedSink.outputStream();
        try {
            os.write('a');
            Assert.fail();
        } catch (IOException expected) {
        }
        try {
            os.write(new byte[10]);
            Assert.fail();
        } catch (IOException expected) {
        }
        // Permitted
        os.flush();
    }

    @Test
    public void writeAll() throws IOException {
        MockSink mockSink = new MockSink();
        BufferedSink bufferedSink = Okio.buffer(mockSink);
        bufferedSink.buffer().writeUtf8("abc");
        Assert.assertEquals(3, bufferedSink.writeAll(new Buffer().writeUtf8("def")));
        Assert.assertEquals(6, bufferedSink.buffer().size());
        Assert.assertEquals("abcdef", bufferedSink.buffer().readUtf8(6));
        mockSink.assertLog();// No writes.

    }

    @Test
    public void writeAllExhausted() throws IOException {
        MockSink mockSink = new MockSink();
        BufferedSink bufferedSink = Okio.buffer(mockSink);
        Assert.assertEquals(0, bufferedSink.writeAll(new Buffer()));
        Assert.assertEquals(0, bufferedSink.buffer().size());
        mockSink.assertLog();// No writes.

    }

    @Test
    public void writeAllWritesOneSegmentAtATime() throws IOException {
        Buffer write1 = new Buffer().writeUtf8(TestUtil.repeat('a', SIZE));
        Buffer write2 = new Buffer().writeUtf8(TestUtil.repeat('b', SIZE));
        Buffer write3 = new Buffer().writeUtf8(TestUtil.repeat('c', SIZE));
        Buffer source = new Buffer().writeUtf8(((("" + (TestUtil.repeat('a', SIZE))) + (TestUtil.repeat('b', SIZE))) + (TestUtil.repeat('c', SIZE))));
        MockSink mockSink = new MockSink();
        BufferedSink bufferedSink = Okio.buffer(mockSink);
        Assert.assertEquals(((SIZE) * 3), bufferedSink.writeAll(source));
        mockSink.assertLog((((("write(" + write1) + ", ") + (write1.size())) + ")"), (((("write(" + write2) + ", ") + (write2.size())) + ")"), (((("write(" + write3) + ", ") + (write3.size())) + ")"));
    }
}

