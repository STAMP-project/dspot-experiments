/**
 * Copyright 2002-2017 the original author or authors.
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


import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.messaging.Message;


/**
 * Unit tests for {@link BufferingStompDecoder}.
 *
 * @author Rossen Stoyanchev
 * @since 4.0.3
 */
public class BufferingStompDecoderTests {
    private final StompDecoder STOMP_DECODER = new StompDecoder();

    @Test
    public void basic() throws InterruptedException {
        BufferingStompDecoder stompDecoder = new BufferingStompDecoder(STOMP_DECODER, 128);
        String chunk = "SEND\na:alpha\n\nMessage body\u0000";
        List<Message<byte[]>> messages = stompDecoder.decode(toByteBuffer(chunk));
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals("Message body", new String(messages.get(0).getPayload()));
        Assert.assertEquals(0, stompDecoder.getBufferSize());
        Assert.assertNull(stompDecoder.getExpectedContentLength());
    }

    @Test
    public void oneMessageInTwoChunks() throws InterruptedException {
        BufferingStompDecoder stompDecoder = new BufferingStompDecoder(STOMP_DECODER, 128);
        String chunk1 = "SEND\na:alpha\n\nMessage";
        String chunk2 = " body\u0000";
        List<Message<byte[]>> messages = stompDecoder.decode(toByteBuffer(chunk1));
        Assert.assertEquals(Collections.<Message<byte[]>>emptyList(), messages);
        messages = stompDecoder.decode(toByteBuffer(chunk2));
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals("Message body", new String(messages.get(0).getPayload()));
        Assert.assertEquals(0, stompDecoder.getBufferSize());
        Assert.assertNull(stompDecoder.getExpectedContentLength());
    }

    @Test
    public void twoMessagesInOneChunk() throws InterruptedException {
        BufferingStompDecoder stompDecoder = new BufferingStompDecoder(STOMP_DECODER, 128);
        String chunk = "SEND\na:alpha\n\nPayload1\u0000" + "SEND\na:alpha\n\nPayload2\u0000";
        List<Message<byte[]>> messages = stompDecoder.decode(toByteBuffer(chunk));
        Assert.assertEquals(2, messages.size());
        Assert.assertEquals("Payload1", new String(messages.get(0).getPayload()));
        Assert.assertEquals("Payload2", new String(messages.get(1).getPayload()));
        Assert.assertEquals(0, stompDecoder.getBufferSize());
        Assert.assertNull(stompDecoder.getExpectedContentLength());
    }

    @Test
    public void oneFullAndOneSplitMessageContentLength() throws InterruptedException {
        BufferingStompDecoder stompDecoder = new BufferingStompDecoder(STOMP_DECODER, 128);
        int contentLength = "Payload2a-Payload2b".getBytes().length;
        String chunk1 = ("SEND\na:alpha\n\nPayload1\u0000SEND\ncontent-length:" + contentLength) + "\n";
        List<Message<byte[]>> messages = stompDecoder.decode(toByteBuffer(chunk1));
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals("Payload1", new String(messages.get(0).getPayload()));
        Assert.assertEquals(23, stompDecoder.getBufferSize());
        Assert.assertEquals(contentLength, ((int) (stompDecoder.getExpectedContentLength())));
        String chunk2 = "\nPayload2a";
        messages = stompDecoder.decode(toByteBuffer(chunk2));
        Assert.assertEquals(0, messages.size());
        Assert.assertEquals(33, stompDecoder.getBufferSize());
        Assert.assertEquals(contentLength, ((int) (stompDecoder.getExpectedContentLength())));
        String chunk3 = "-Payload2b\u0000";
        messages = stompDecoder.decode(toByteBuffer(chunk3));
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals("Payload2a-Payload2b", new String(messages.get(0).getPayload()));
        Assert.assertEquals(0, stompDecoder.getBufferSize());
        Assert.assertNull(stompDecoder.getExpectedContentLength());
    }

    @Test
    public void oneFullAndOneSplitMessageNoContentLength() throws InterruptedException {
        BufferingStompDecoder stompDecoder = new BufferingStompDecoder(STOMP_DECODER, 128);
        String chunk1 = "SEND\na:alpha\n\nPayload1\u0000SEND\na:alpha\n";
        List<Message<byte[]>> messages = stompDecoder.decode(toByteBuffer(chunk1));
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals("Payload1", new String(messages.get(0).getPayload()));
        Assert.assertEquals(13, stompDecoder.getBufferSize());
        Assert.assertNull(stompDecoder.getExpectedContentLength());
        String chunk2 = "\nPayload2a";
        messages = stompDecoder.decode(toByteBuffer(chunk2));
        Assert.assertEquals(0, messages.size());
        Assert.assertEquals(23, stompDecoder.getBufferSize());
        Assert.assertNull(stompDecoder.getExpectedContentLength());
        String chunk3 = "-Payload2b\u0000";
        messages = stompDecoder.decode(toByteBuffer(chunk3));
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals("Payload2a-Payload2b", new String(messages.get(0).getPayload()));
        Assert.assertEquals(0, stompDecoder.getBufferSize());
        Assert.assertNull(stompDecoder.getExpectedContentLength());
    }

    @Test
    public void oneFullAndOneSplitWithContentLengthExceedingBufferSize() throws InterruptedException {
        BufferingStompDecoder stompDecoder = new BufferingStompDecoder(STOMP_DECODER, 128);
        String chunk1 = "SEND\na:alpha\n\nPayload1\u0000SEND\ncontent-length:129\n";
        List<Message<byte[]>> messages = stompDecoder.decode(toByteBuffer(chunk1));
        Assert.assertEquals("We should have gotten the 1st message", 1, messages.size());
        Assert.assertEquals("Payload1", new String(messages.get(0).getPayload()));
        Assert.assertEquals(24, stompDecoder.getBufferSize());
        Assert.assertEquals(129, ((int) (stompDecoder.getExpectedContentLength())));
        try {
            String chunk2 = "\nPayload2a";
            stompDecoder.decode(toByteBuffer(chunk2));
            Assert.fail("Expected exception");
        } catch (StompConversionException ex) {
            // expected
        }
    }

    @Test(expected = StompConversionException.class)
    public void bufferSizeLimit() {
        BufferingStompDecoder stompDecoder = new BufferingStompDecoder(STOMP_DECODER, 10);
        String payload = "SEND\na:alpha\n\nMessage body";
        stompDecoder.decode(toByteBuffer(payload));
    }

    @Test
    public void incompleteCommand() {
        BufferingStompDecoder stompDecoder = new BufferingStompDecoder(STOMP_DECODER, 128);
        String chunk = "MESSAG";
        List<Message<byte[]>> messages = stompDecoder.decode(toByteBuffer(chunk));
        Assert.assertEquals(0, messages.size());
    }

    // SPR-13416
    @Test
    public void incompleteHeaderWithPartialEscapeSequence() throws Exception {
        BufferingStompDecoder stompDecoder = new BufferingStompDecoder(STOMP_DECODER, 128);
        String chunk = "SEND\na:long\\";
        List<Message<byte[]>> messages = stompDecoder.decode(toByteBuffer(chunk));
        Assert.assertEquals(0, messages.size());
    }

    @Test(expected = StompConversionException.class)
    public void invalidEscapeSequence() {
        BufferingStompDecoder stompDecoder = new BufferingStompDecoder(STOMP_DECODER, 128);
        String payload = "SEND\na:alpha\\x\\n\nMessage body\u0000";
        stompDecoder.decode(toByteBuffer(payload));
    }

    @Test(expected = StompConversionException.class)
    public void invalidEscapeSequenceWithSingleSlashAtEndOfHeaderValue() {
        BufferingStompDecoder stompDecoder = new BufferingStompDecoder(STOMP_DECODER, 128);
        String payload = "SEND\na:alpha\\\n\nMessage body\u0000";
        stompDecoder.decode(toByteBuffer(payload));
    }
}

