/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.web.socket.sockjs.frame;


import SockJsFrameType.CLOSE;
import SockJsFrameType.HEARTBEAT;
import SockJsFrameType.MESSAGE;
import SockJsFrameType.OPEN;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link org.springframework.web.socket.sockjs.frame.SockJsFrame}.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class SockJsFrameTests {
    @Test
    public void openFrame() {
        SockJsFrame frame = SockJsFrame.openFrame();
        Assert.assertEquals("o", frame.getContent());
        Assert.assertEquals(OPEN, frame.getType());
        Assert.assertNull(frame.getFrameData());
    }

    @Test
    public void heartbeatFrame() {
        SockJsFrame frame = SockJsFrame.heartbeatFrame();
        Assert.assertEquals("h", frame.getContent());
        Assert.assertEquals(HEARTBEAT, frame.getType());
        Assert.assertNull(frame.getFrameData());
    }

    @Test
    public void messageArrayFrame() {
        SockJsFrame frame = SockJsFrame.messageFrame(new Jackson2SockJsMessageCodec(), "m1", "m2");
        Assert.assertEquals("a[\"m1\",\"m2\"]", frame.getContent());
        Assert.assertEquals(MESSAGE, frame.getType());
        Assert.assertEquals("[\"m1\",\"m2\"]", frame.getFrameData());
    }

    @Test
    public void messageArrayFrameEmpty() {
        SockJsFrame frame = new SockJsFrame("a");
        Assert.assertEquals("a[]", frame.getContent());
        Assert.assertEquals(MESSAGE, frame.getType());
        Assert.assertEquals("[]", frame.getFrameData());
        frame = new SockJsFrame("a[]");
        Assert.assertEquals("a[]", frame.getContent());
        Assert.assertEquals(MESSAGE, frame.getType());
        Assert.assertEquals("[]", frame.getFrameData());
    }

    @Test
    public void closeFrame() {
        SockJsFrame frame = SockJsFrame.closeFrame(3000, "Go Away!");
        Assert.assertEquals("c[3000,\"Go Away!\"]", frame.getContent());
        Assert.assertEquals(CLOSE, frame.getType());
        Assert.assertEquals("[3000,\"Go Away!\"]", frame.getFrameData());
    }

    @Test
    public void closeFrameEmpty() {
        SockJsFrame frame = new SockJsFrame("c");
        Assert.assertEquals("c[]", frame.getContent());
        Assert.assertEquals(CLOSE, frame.getType());
        Assert.assertEquals("[]", frame.getFrameData());
        frame = new SockJsFrame("c[]");
        Assert.assertEquals("c[]", frame.getContent());
        Assert.assertEquals(CLOSE, frame.getType());
        Assert.assertEquals("[]", frame.getFrameData());
    }
}

