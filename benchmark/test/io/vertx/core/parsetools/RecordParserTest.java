/**
 * Copyright (c) 2014 Red Hat, Inc. and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.parsetools;


import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.test.core.TestUtils;
import io.vertx.test.fakestream.FakeStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 * @author <a href="mailto:larsdtimm@gmail.com">Lars Timm</a>
 */
public class RecordParserTest {
    @Test
    public void testIllegalArguments() {
        TestUtils.assertNullPointerException(() -> RecordParser.newDelimited(((Buffer) (null)), ( handler) -> {
        }));
        TestUtils.assertNullPointerException(() -> RecordParser.newDelimited(((String) (null)), ( handler) -> {
        }));
        RecordParser parser = RecordParser.newDelimited("", ( handler) -> {
        });
        TestUtils.assertNullPointerException(() -> parser.setOutput(null));
        TestUtils.assertNullPointerException(() -> parser.delimitedMode(((Buffer) (null))));
        TestUtils.assertNullPointerException(() -> parser.delimitedMode(((String) (null))));
        TestUtils.assertIllegalArgumentException(() -> parser.maxRecordSize((-1)));
    }

    /* Test parsing with delimiters */
    @Test
    public void testDelimited() {
        delimited(Buffer.buffer().appendByte(((byte) ('\n'))));
        delimited(Buffer.buffer().appendByte(((byte) ('\r'))).appendByte(((byte) ('\n'))));
        delimited(Buffer.buffer(new byte[]{ 0, 3, 2, 5, 6, 4, 6 }));
    }

    /* Test parsing with fixed size records */
    @Test
    public void testFixed() {
        int lines = 50;
        Buffer[] expected = new Buffer[lines];
        // We create lines of length zero to <lines> and shuffle them
        List<Buffer> lineList = generateLines(lines, false, ((byte) (0)));
        expected = lineList.toArray(expected);
        int totLength = (lines * (lines - 1)) / 2;// The sum of 0...(lines - 1)

        Buffer inp = Buffer.buffer(totLength);
        for (int i = 0; i < lines; i++) {
            inp.appendBuffer(expected[i]);
        }
        // We then try every combination of chunk size up to twice the input string length
        for (int i = 1; i < ((inp.length()) * 2); i++) {
            doTestFixed(inp, new Integer[]{ i }, expected);
        }
        // Then we try a sequence of random chunk sizes
        List<Integer> chunkSizes = generateChunkSizes(lines);
        // Repeat a few times
        for (int i = 0; i < 10; i++) {
            Collections.shuffle(chunkSizes);
            doTestFixed(inp, chunkSizes.toArray(new Integer[]{  }), expected);
        }
    }

    /* Test mixture of fixed and delimited */
    @Test
    public void testMixed() {
        final int lines = 8;
        final List<Object> types = new ArrayList<Object>();
        class MyHandler implements Handler<Buffer> {
            RecordParser parser = RecordParser.newFixed(10, this);

            int pos;

            public void handle(Buffer buff) {
                if ((pos) < lines) {
                    Object type = types.get(pos);
                    if (type instanceof byte[]) {
                        byte[] bytes = ((byte[]) (type));
                        parser.delimitedMode(Buffer.buffer(bytes));
                    } else {
                        int length = ((Integer) (type));
                        parser.fixedSizeMode(length);
                    }
                }
            }
        }
        MyHandler out = new MyHandler();
        Buffer[] expected = new Buffer[lines];
        Buffer input = Buffer.buffer(100);
        expected[0] = TestUtils.randomBuffer(10);
        input.appendBuffer(expected[0]);
        types.add(expected[0].length());
        expected[1] = TestUtils.randomBuffer(100);
        input.appendBuffer(expected[1]);
        types.add(expected[1].length());
        byte[] delim = new byte[]{ 23, -120, 100, 3 };
        expected[2] = TestUtils.randomBuffer(50, true, delim[0]);
        input.appendBuffer(expected[2]);
        types.add(delim);
        input.appendBuffer(Buffer.buffer(delim));
        expected[3] = TestUtils.randomBuffer(1000);
        input.appendBuffer(expected[3]);
        types.add(expected[3].length());
        expected[4] = TestUtils.randomBuffer(230, true, delim[0]);
        input.appendBuffer(expected[4]);
        types.add(delim);
        input.appendBuffer(Buffer.buffer(delim));
        delim = new byte[]{ 17 };
        expected[5] = TestUtils.randomBuffer(341, true, delim[0]);
        input.appendBuffer(expected[5]);
        types.add(delim);
        input.appendBuffer(Buffer.buffer(delim));
        delim = new byte[]{ 54, -32, 0 };
        expected[6] = TestUtils.randomBuffer(1234, true, delim[0]);
        input.appendBuffer(expected[6]);
        types.add(delim);
        input.appendBuffer(Buffer.buffer(delim));
        expected[7] = TestUtils.randomBuffer(100);
        input.appendBuffer(expected[7]);
        types.add(expected[7].length());
        feedChunks(input, out.parser, new Integer[]{ 50, 10, 3 });
    }

    /* test issue-209 */
    @Test
    public void testSpreadDelimiter() {
        doTestDelimited(Buffer.buffer("start-a-b-c-dddabc"), Buffer.buffer("abc"), new Integer[]{ 18 }, Buffer.buffer("start-a-b-c-ddd"));
        doTestDelimited(Buffer.buffer("start-abc-dddabc"), Buffer.buffer("abc"), new Integer[]{ 18 }, Buffer.buffer("start-"), Buffer.buffer("-ddd"));
        doTestDelimited(Buffer.buffer("start-ab-c-dddabc"), Buffer.buffer("abc"), new Integer[]{ 18 }, Buffer.buffer("start-ab-c-ddd"));
    }

    @Test
    public void testDelimitedMaxRecordSize() {
        doTestDelimitedMaxRecordSize(Buffer.buffer("ABCD\nEFGH\n"), Buffer.buffer("\n"), new Integer[]{ 2 }, 4, null, Buffer.buffer("ABCD"), Buffer.buffer("EFGH"));
        doTestDelimitedMaxRecordSize(Buffer.buffer("A\nBC10\nDEFGHIJKLM\n"), Buffer.buffer("\n"), new Integer[]{ 2 }, 10, null, Buffer.buffer("A"), Buffer.buffer("BC10"), Buffer.buffer("DEFGHIJKLM"));
        doTestDelimitedMaxRecordSize(Buffer.buffer("AB\nC\n\nDEFG\n\n"), Buffer.buffer("\n\n"), new Integer[]{ 2 }, 4, null, Buffer.buffer("AB\nC"), Buffer.buffer("DEFG"));
        doTestDelimitedMaxRecordSize(Buffer.buffer("AB--C---D-"), Buffer.buffer("-"), new Integer[]{ 3 }, 2, null, Buffer.buffer("AB"), Buffer.buffer(""), Buffer.buffer("C"), Buffer.buffer(""), Buffer.buffer(""), Buffer.buffer("D"));
        try {
            doTestDelimitedMaxRecordSize(Buffer.buffer("ABCD--"), Buffer.buffer("--"), new Integer[]{ 2 }, 3, null, Buffer.buffer());
            Assert.fail("should throw exception");
        } catch (IllegalStateException ex) {
            /* OK */
        }
        AtomicBoolean handled = new AtomicBoolean();
        Handler<Throwable> exHandler = ( throwable) -> handled.set(true);
        doTestDelimitedMaxRecordSize(Buffer.buffer("ABCD--"), Buffer.buffer("--"), new Integer[]{ 2 }, 3, exHandler, Buffer.buffer("ABCD"));
        Assert.assertTrue(handled.get());
    }

    @Test
    public void testWrapReadStream() {
        FakeStream<Buffer> stream = new FakeStream<>();
        RecordParser parser = RecordParser.newDelimited("\r\n", stream);
        AtomicInteger ends = new AtomicInteger();
        parser.endHandler(( v) -> ends.incrementAndGet());
        Deque<String> records = new ArrayDeque<>();
        parser.handler(( record) -> records.add(record.toString()));
        Assert.assertFalse(stream.isPaused());
        parser.pause();
        parser.resume();
        stream.emit(Buffer.buffer("first\r\nsecond\r\nthird"));
        Assert.assertEquals("first", records.poll());
        Assert.assertEquals("second", records.poll());
        Assert.assertNull(records.poll());
        stream.emit(Buffer.buffer("\r\n"));
        Assert.assertEquals("third", records.poll());
        Assert.assertNull(records.poll());
        Assert.assertEquals(0, ends.get());
        Throwable cause = new Throwable();
        stream.fail(cause);
        List<Throwable> failures = new ArrayList<>();
        parser.exceptionHandler(failures::add);
        stream.fail(cause);
        Assert.assertEquals(Collections.singletonList(cause), failures);
        Assert.assertFalse(stream.isPaused());
        parser.pause();
        Assert.assertFalse(stream.isPaused());
        int count = 0;
        do {
            stream.emit(Buffer.buffer((("item-" + (count++)) + "\r\n")));
        } while (!(stream.isPaused()) );
        Assert.assertNull(records.poll());
        parser.resume();
        for (int i = 0; i < count; i++) {
            Assert.assertEquals(("item-" + i), records.poll());
        }
        Assert.assertNull(records.poll());
        Assert.assertFalse(stream.isPaused());
        stream.end();
        Assert.assertEquals(1, ends.get());
    }

    @Test
    public void testPausedStreamShouldNotPauseOnIncompleteMatch() {
        FakeStream<Buffer> stream = new FakeStream<>();
        RecordParser parser = RecordParser.newDelimited("\r\n", stream);
        parser.handler(( event) -> {
        });
        parser.pause().fetch(1);
        stream.emit(Buffer.buffer("abc"));
        Assert.assertFalse(stream.isPaused());
    }

    @Test
    public void testSuspendParsing() {
        FakeStream<Buffer> stream = new FakeStream<>();
        RecordParser parser = RecordParser.newDelimited("\r\n", stream);
        List<Buffer> emitted = new ArrayList<>();
        parser.handler(emitted::add);
        parser.pause().fetch(1);
        stream.emit(Buffer.buffer("abc\r\ndef\r\n"));
        parser.fetch(1);
        Assert.assertEquals(Arrays.asList(Buffer.buffer("abc"), Buffer.buffer("def")), emitted);
    }

    @Test
    public void testParseEmptyChunkOnFetch() {
        FakeStream<Buffer> stream = new FakeStream<>();
        RecordParser parser = RecordParser.newDelimited("\r\n", stream);
        List<Buffer> emitted = new ArrayList<>();
        parser.handler(emitted::add);
        parser.pause();
        stream.emit(Buffer.buffer("abc\r\n\r\n"));
        parser.fetch(1);
        Assert.assertEquals(Collections.singletonList(Buffer.buffer("abc")), emitted);
        parser.fetch(1);
        Assert.assertEquals(Arrays.asList(Buffer.buffer("abc"), Buffer.buffer()), emitted);
    }

    @Test
    public void testSwitchModeResetsState() {
        FakeStream<Buffer> stream = new FakeStream<>();
        RecordParser parser = RecordParser.newDelimited("\r\n", stream);
        List<Buffer> emitted = new ArrayList<>();
        parser.handler(emitted::add);
        parser.pause();
        stream.emit(Buffer.buffer("3\r\nabc\r\n"));
        parser.fetch(1);
        Assert.assertEquals(Collections.singletonList(Buffer.buffer("3")), emitted);
        parser.fixedSizeMode(5);
        parser.fetch(1);
        Assert.assertEquals(Arrays.asList(Buffer.buffer("3"), Buffer.buffer("abc\r\n")), emitted);
    }
}

