/**
 * Copyright 2018 The Netty Project
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
package io.netty.handler.codec.stomp;


import StompHeaders.HOST;
import io.netty.util.AsciiString;
import org.junit.Assert;
import org.junit.Test;

import static StompCommand.CONNECT;


public class DefaultStompFrameTest {
    @Test
    public void testStompFrameCopy() {
        StompFrame sourceFrame = new DefaultStompFrame(CONNECT);
        Assert.assertTrue(sourceFrame.headers().isEmpty());
        sourceFrame.headers().set(HOST, "localhost");
        StompFrame copyFrame = sourceFrame.copy();
        Assert.assertEquals(sourceFrame.headers(), copyFrame.headers());
        Assert.assertEquals(sourceFrame.content(), copyFrame.content());
        AsciiString copyHeaderName = new AsciiString("foo");
        AsciiString copyHeaderValue = new AsciiString("bar");
        copyFrame.headers().set(copyHeaderName, copyHeaderValue);
        Assert.assertFalse(sourceFrame.headers().contains(copyHeaderName, copyHeaderValue));
        Assert.assertTrue(copyFrame.headers().contains(copyHeaderName, copyHeaderValue));
        Assert.assertEquals(1, sourceFrame.headers().size());
        Assert.assertEquals(2, copyFrame.headers().size());
        Assert.assertNotEquals(sourceFrame.headers(), copyFrame.headers());
        Assert.assertTrue(sourceFrame.release());
        Assert.assertTrue(copyFrame.release());
    }
}

