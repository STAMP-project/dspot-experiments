/**
 * Copyright 2012 The Netty Project
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
package org.jboss.netty.buffer;


import ChannelBufferIndexFinder.CR;
import ChannelBufferIndexFinder.CRLF;
import ChannelBufferIndexFinder.LF;
import ChannelBufferIndexFinder.LINEAR_WHITESPACE;
import ChannelBufferIndexFinder.NOT_CR;
import ChannelBufferIndexFinder.NOT_CRLF;
import ChannelBufferIndexFinder.NOT_LF;
import ChannelBufferIndexFinder.NOT_LINEAR_WHITESPACE;
import ChannelBufferIndexFinder.NOT_NUL;
import ChannelBufferIndexFinder.NUL;
import CharsetUtil.ISO_8859_1;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the index-finding capabilities of channel buffers
 */
public class ChannelBufferIndexFinderTest {
    @Test
    public void testForward() {
        ChannelBuffer buf = ChannelBuffers.copiedBuffer("abc\r\n\ndef\r\rghi\n\njkl\u0000\u0000mno  \t\tx", ISO_8859_1);
        Assert.assertEquals(3, buf.indexOf(Integer.MIN_VALUE, buf.capacity(), CRLF));
        Assert.assertEquals(6, buf.indexOf(3, buf.capacity(), NOT_CRLF));
        Assert.assertEquals(9, buf.indexOf(6, buf.capacity(), CR));
        Assert.assertEquals(11, buf.indexOf(9, buf.capacity(), NOT_CR));
        Assert.assertEquals(14, buf.indexOf(11, buf.capacity(), LF));
        Assert.assertEquals(16, buf.indexOf(14, buf.capacity(), NOT_LF));
        Assert.assertEquals(19, buf.indexOf(16, buf.capacity(), NUL));
        Assert.assertEquals(21, buf.indexOf(19, buf.capacity(), NOT_NUL));
        Assert.assertEquals(24, buf.indexOf(21, buf.capacity(), LINEAR_WHITESPACE));
        Assert.assertEquals(28, buf.indexOf(24, buf.capacity(), NOT_LINEAR_WHITESPACE));
        Assert.assertEquals((-1), buf.indexOf(28, buf.capacity(), LINEAR_WHITESPACE));
    }

    @Test
    public void testBackward() {
        ChannelBuffer buf = ChannelBuffers.copiedBuffer("abc\r\n\ndef\r\rghi\n\njkl\u0000\u0000mno  \t\tx", ISO_8859_1);
        Assert.assertEquals(27, buf.indexOf(Integer.MAX_VALUE, 0, LINEAR_WHITESPACE));
        Assert.assertEquals(23, buf.indexOf(28, 0, NOT_LINEAR_WHITESPACE));
        Assert.assertEquals(20, buf.indexOf(24, 0, NUL));
        Assert.assertEquals(18, buf.indexOf(21, 0, NOT_NUL));
        Assert.assertEquals(15, buf.indexOf(19, 0, LF));
        Assert.assertEquals(13, buf.indexOf(16, 0, NOT_LF));
        Assert.assertEquals(10, buf.indexOf(14, 0, CR));
        Assert.assertEquals(8, buf.indexOf(11, 0, NOT_CR));
        Assert.assertEquals(5, buf.indexOf(9, 0, CRLF));
        Assert.assertEquals(2, buf.indexOf(6, 0, NOT_CRLF));
        Assert.assertEquals((-1), buf.indexOf(3, 0, CRLF));
    }
}

