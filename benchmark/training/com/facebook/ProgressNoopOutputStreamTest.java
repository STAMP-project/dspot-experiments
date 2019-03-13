/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook;


import org.junit.Assert;
import org.junit.Test;


public class ProgressNoopOutputStreamTest extends FacebookTestCase {
    private ProgressNoopOutputStream stream;

    @Test
    public void testSetup() {
        Assert.assertEquals(0, stream.getMaxProgress());
        Assert.assertTrue(stream.getProgressMap().isEmpty());
    }

    @Test
    public void testWriting() {
        Assert.assertEquals(0, stream.getMaxProgress());
        stream.write(0);
        Assert.assertEquals(1, stream.getMaxProgress());
        final byte[] buf = new byte[8];
        stream.write(buf);
        Assert.assertEquals(9, stream.getMaxProgress());
        stream.write(buf, 2, 2);
        Assert.assertEquals(11, stream.getMaxProgress());
        stream.addProgress(16);
        Assert.assertEquals(27, stream.getMaxProgress());
    }
}

