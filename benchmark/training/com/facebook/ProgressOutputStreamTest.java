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


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ProgressOutputStreamTest extends FacebookTestCase {
    private static final int MAX_PROGRESS = 10;

    private GraphRequest r1;

    private GraphRequest r2;

    private Map<GraphRequest, RequestProgress> progressMap;

    private GraphRequestBatch requests;

    private ProgressOutputStream stream;

    @Test
    public void testSetup() {
        Assert.assertEquals(0, stream.getBatchProgress());
        Assert.assertEquals(ProgressOutputStreamTest.MAX_PROGRESS, stream.getMaxProgress());
        for (RequestProgress p : progressMap.values()) {
            Assert.assertEquals(0, p.getProgress());
            Assert.assertEquals(5, p.getMaxProgress());
        }
    }

    @Test
    public void testWriting() {
        try {
            Assert.assertEquals(0, stream.getBatchProgress());
            stream.setCurrentRequest(r1);
            stream.write(0);
            Assert.assertEquals(1, stream.getBatchProgress());
            final byte[] buf = new byte[4];
            stream.write(buf);
            Assert.assertEquals(5, stream.getBatchProgress());
            stream.setCurrentRequest(r2);
            stream.write(buf, 2, 2);
            stream.write(buf, 1, 3);
            Assert.assertEquals(ProgressOutputStreamTest.MAX_PROGRESS, stream.getBatchProgress());
            Assert.assertEquals(stream.getMaxProgress(), stream.getBatchProgress());
            Assert.assertEquals(progressMap.get(r1).getMaxProgress(), progressMap.get(r1).getProgress());
            Assert.assertEquals(progressMap.get(r2).getMaxProgress(), progressMap.get(r2).getProgress());
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }
}

