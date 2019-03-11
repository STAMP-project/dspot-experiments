/**
 * Copyright 2013 The Netty Project
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


import ByteProcessor.FIND_ASCII_SPACE;
import ByteProcessor.FIND_CR;
import ByteProcessor.FIND_CRLF;
import ByteProcessor.FIND_LF;
import ByteProcessor.FIND_LINEAR_WHITESPACE;
import ByteProcessor.FIND_NON_CR;
import ByteProcessor.FIND_NON_CRLF;
import ByteProcessor.FIND_NON_LF;
import ByteProcessor.FIND_NON_LINEAR_WHITESPACE;
import ByteProcessor.FIND_NON_NUL;
import ByteProcessor.FIND_NUL;
import CharsetUtil.ISO_8859_1;
import org.junit.Assert;
import org.junit.Test;


public class ByteProcessorTest {
    @Test
    public void testForward() {
        final ByteBuf buf = Unpooled.copiedBuffer("abc\r\n\ndef\r\rghi\n\njkl\u0000\u0000mno  \t\tx", ISO_8859_1);
        final int length = buf.readableBytes();
        Assert.assertEquals(3, buf.forEachByte(0, length, FIND_CRLF));
        Assert.assertEquals(6, buf.forEachByte(3, (length - 3), FIND_NON_CRLF));
        Assert.assertEquals(9, buf.forEachByte(6, (length - 6), FIND_CR));
        Assert.assertEquals(11, buf.forEachByte(9, (length - 9), FIND_NON_CR));
        Assert.assertEquals(14, buf.forEachByte(11, (length - 11), FIND_LF));
        Assert.assertEquals(16, buf.forEachByte(14, (length - 14), FIND_NON_LF));
        Assert.assertEquals(19, buf.forEachByte(16, (length - 16), FIND_NUL));
        Assert.assertEquals(21, buf.forEachByte(19, (length - 19), FIND_NON_NUL));
        Assert.assertEquals(24, buf.forEachByte(19, (length - 19), FIND_ASCII_SPACE));
        Assert.assertEquals(24, buf.forEachByte(21, (length - 21), FIND_LINEAR_WHITESPACE));
        Assert.assertEquals(28, buf.forEachByte(24, (length - 24), FIND_NON_LINEAR_WHITESPACE));
        Assert.assertEquals((-1), buf.forEachByte(28, (length - 28), FIND_LINEAR_WHITESPACE));
        buf.release();
    }

    @Test
    public void testBackward() {
        final ByteBuf buf = Unpooled.copiedBuffer("abc\r\n\ndef\r\rghi\n\njkl\u0000\u0000mno  \t\tx", ISO_8859_1);
        final int length = buf.readableBytes();
        Assert.assertEquals(27, buf.forEachByteDesc(0, length, FIND_LINEAR_WHITESPACE));
        Assert.assertEquals(25, buf.forEachByteDesc(0, length, FIND_ASCII_SPACE));
        Assert.assertEquals(23, buf.forEachByteDesc(0, 28, FIND_NON_LINEAR_WHITESPACE));
        Assert.assertEquals(20, buf.forEachByteDesc(0, 24, FIND_NUL));
        Assert.assertEquals(18, buf.forEachByteDesc(0, 21, FIND_NON_NUL));
        Assert.assertEquals(15, buf.forEachByteDesc(0, 19, FIND_LF));
        Assert.assertEquals(13, buf.forEachByteDesc(0, 16, FIND_NON_LF));
        Assert.assertEquals(10, buf.forEachByteDesc(0, 14, FIND_CR));
        Assert.assertEquals(8, buf.forEachByteDesc(0, 11, FIND_NON_CR));
        Assert.assertEquals(5, buf.forEachByteDesc(0, 9, FIND_CRLF));
        Assert.assertEquals(2, buf.forEachByteDesc(0, 6, FIND_NON_CRLF));
        Assert.assertEquals((-1), buf.forEachByteDesc(0, 3, FIND_CRLF));
        buf.release();
    }
}

