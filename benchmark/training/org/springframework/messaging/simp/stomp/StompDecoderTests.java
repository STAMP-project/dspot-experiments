/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.messaging.simp.stomp;


import SimpMessageType.HEARTBEAT;
import StompCommand.CONNECT;
import StompCommand.DISCONNECT;
import StompCommand.SEND;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.util.InvalidMimeTypeException;


/**
 * Test fixture for {@link StompDecoder}.
 *
 * @author Andy Wilkinson
 * @author Stephane Maldini
 */
public class StompDecoderTests {
    private final StompDecoder decoder = new StompDecoder();

    @Test
    public void decodeFrameWithCrLfEols() {
        Message<byte[]> frame = decode("DISCONNECT\r\n\r\n\u0000");
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(frame);
        Assert.assertEquals(DISCONNECT, headers.getCommand());
        Assert.assertEquals(0, headers.toNativeHeaderMap().size());
        Assert.assertEquals(0, frame.getPayload().length);
    }

    @Test
    public void decodeFrameWithNoHeadersAndNoBody() {
        Message<byte[]> frame = decode("DISCONNECT\n\n\u0000");
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(frame);
        Assert.assertEquals(DISCONNECT, headers.getCommand());
        Assert.assertEquals(0, headers.toNativeHeaderMap().size());
        Assert.assertEquals(0, frame.getPayload().length);
    }

    @Test
    public void decodeFrameWithNoBody() {
        String accept = "accept-version:1.1\n";
        String host = "host:github.org\n";
        Message<byte[]> frame = decode(((("CONNECT\n" + accept) + host) + "\n\u0000"));
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(frame);
        Assert.assertEquals(CONNECT, headers.getCommand());
        Assert.assertEquals(2, headers.toNativeHeaderMap().size());
        Assert.assertEquals("1.1", headers.getFirstNativeHeader("accept-version"));
        Assert.assertEquals("github.org", headers.getHost());
        Assert.assertEquals(0, frame.getPayload().length);
    }

    @Test
    public void decodeFrame() throws UnsupportedEncodingException {
        Message<byte[]> frame = decode("SEND\ndestination:test\n\nThe body of the message\u0000");
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(frame);
        Assert.assertEquals(SEND, headers.getCommand());
        Assert.assertEquals(headers.toNativeHeaderMap().toString(), 1, headers.toNativeHeaderMap().size());
        Assert.assertEquals("test", headers.getDestination());
        String bodyText = new String(frame.getPayload());
        Assert.assertEquals("The body of the message", bodyText);
    }

    @Test
    public void decodeFrameWithContentLength() {
        Message<byte[]> message = decode("SEND\ncontent-length:23\n\nThe body of the message\u0000");
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
        Assert.assertEquals(SEND, headers.getCommand());
        Assert.assertEquals(1, headers.toNativeHeaderMap().size());
        Assert.assertEquals(Integer.valueOf(23), headers.getContentLength());
        String bodyText = new String(message.getPayload());
        Assert.assertEquals("The body of the message", bodyText);
    }

    // SPR-11528
    @Test
    public void decodeFrameWithInvalidContentLength() {
        Message<byte[]> message = decode("SEND\ncontent-length:-1\n\nThe body of the message\u0000");
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
        Assert.assertEquals(SEND, headers.getCommand());
        Assert.assertEquals(1, headers.toNativeHeaderMap().size());
        Assert.assertEquals(Integer.valueOf((-1)), headers.getContentLength());
        String bodyText = new String(message.getPayload());
        Assert.assertEquals("The body of the message", bodyText);
    }

    @Test
    public void decodeFrameWithContentLengthZero() {
        Message<byte[]> frame = decode("SEND\ncontent-length:0\n\n\u0000");
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(frame);
        Assert.assertEquals(SEND, headers.getCommand());
        Assert.assertEquals(1, headers.toNativeHeaderMap().size());
        Assert.assertEquals(Integer.valueOf(0), headers.getContentLength());
        String bodyText = new String(frame.getPayload());
        Assert.assertEquals("", bodyText);
    }

    @Test
    public void decodeFrameWithNullOctectsInTheBody() {
        Message<byte[]> frame = decode("SEND\ncontent-length:23\n\nThe b\u0000dy \u0000f the message\u0000");
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(frame);
        Assert.assertEquals(SEND, headers.getCommand());
        Assert.assertEquals(1, headers.toNativeHeaderMap().size());
        Assert.assertEquals(Integer.valueOf(23), headers.getContentLength());
        String bodyText = new String(frame.getPayload());
        Assert.assertEquals("The b\u0000dy \u0000f the message", bodyText);
    }

    @Test
    public void decodeFrameWithEscapedHeaders() {
        Message<byte[]> frame = decode("DISCONNECT\na\\c\\r\\n\\\\b:alpha\\cbravo\\r\\n\\\\\n\n\u0000");
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(frame);
        Assert.assertEquals(DISCONNECT, headers.getCommand());
        Assert.assertEquals(1, headers.toNativeHeaderMap().size());
        Assert.assertEquals("alpha:bravo\r\n\\", headers.getFirstNativeHeader("a:\r\n\\b"));
    }

    @Test(expected = StompConversionException.class)
    public void decodeFrameBodyNotAllowed() {
        decode("CONNECT\naccept-version:1.2\n\nThe body of the message\u0000");
    }

    @Test
    public void decodeMultipleFramesFromSameBuffer() {
        String frame1 = "SEND\ndestination:test\n\nThe body of the message\u0000";
        String frame2 = "DISCONNECT\n\n\u0000";
        ByteBuffer buffer = ByteBuffer.wrap((frame1 + frame2).getBytes());
        final List<Message<byte[]>> messages = decoder.decode(buffer);
        Assert.assertEquals(2, messages.size());
        Assert.assertEquals(SEND, StompHeaderAccessor.wrap(messages.get(0)).getCommand());
        Assert.assertEquals(DISCONNECT, StompHeaderAccessor.wrap(messages.get(1)).getCommand());
    }

    // SPR-13111
    @Test
    public void decodeFrameWithHeaderWithEmptyValue() {
        String accept = "accept-version:1.1\n";
        String valuelessKey = "key:\n";
        Message<byte[]> frame = decode(((("CONNECT\n" + accept) + valuelessKey) + "\n\u0000"));
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(frame);
        Assert.assertEquals(CONNECT, headers.getCommand());
        Assert.assertEquals(2, headers.toNativeHeaderMap().size());
        Assert.assertEquals("1.1", headers.getFirstNativeHeader("accept-version"));
        Assert.assertEquals("", headers.getFirstNativeHeader("key"));
        Assert.assertEquals(0, frame.getPayload().length);
    }

    @Test
    public void decodeFrameWithIncompleteCommand() {
        assertIncompleteDecode("MESSAG");
    }

    @Test
    public void decodeFrameWithIncompleteHeader() {
        assertIncompleteDecode("SEND\ndestination");
        assertIncompleteDecode("SEND\ndestination:");
        assertIncompleteDecode("SEND\ndestination:test");
    }

    @Test
    public void decodeFrameWithoutNullOctetTerminator() {
        assertIncompleteDecode("SEND\ndestination:test\n");
        assertIncompleteDecode("SEND\ndestination:test\n\n");
        assertIncompleteDecode("SEND\ndestination:test\n\nThe body");
    }

    @Test
    public void decodeFrameWithInsufficientContent() {
        assertIncompleteDecode("SEND\ncontent-length:23\n\nThe body of the mess");
    }

    @Test
    public void decodeFrameWithIncompleteContentType() {
        assertIncompleteDecode("SEND\ncontent-type:text/plain;charset=U");
    }

    @Test(expected = InvalidMimeTypeException.class)
    public void decodeFrameWithInvalidContentType() {
        assertIncompleteDecode("SEND\ncontent-type:text/plain;charset=U\n\nThe body\u0000");
    }

    @Test(expected = StompConversionException.class)
    public void decodeFrameWithIncorrectTerminator() {
        decode("SEND\ncontent-length:23\n\nThe body of the message*");
    }

    @Test
    public void decodeHeartbeat() {
        String frame = "\n";
        ByteBuffer buffer = ByteBuffer.wrap(frame.getBytes());
        final List<Message<byte[]>> messages = decoder.decode(buffer);
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals(HEARTBEAT, StompHeaderAccessor.wrap(messages.get(0)).getMessageType());
    }
}

