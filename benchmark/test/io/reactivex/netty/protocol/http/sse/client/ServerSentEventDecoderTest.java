/**
 * Copyright 2015 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.netty.protocol.http.sse.client;


import Type.Data;
import io.netty.channel.ChannelHandlerContext;
import io.reactivex.netty.protocol.http.sse.ServerSentEvent;
import io.reactivex.netty.protocol.http.sse.SseTestUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ServerSentEventDecoderTest {
    private final ServerSentEventDecoder decoder = new ServerSentEventDecoder();

    private final ChannelHandlerContext ch = pipeline().firstContext();

    @Test(timeout = 60000)
    public void testOneDataLineDecode() throws Exception {
        String eventType = "add";
        String eventId = "1";
        String data = "data line";
        ServerSentEvent expected = SseTestUtil.newServerSentEvent(eventType, eventId, data);
        doTest(SseTestUtil.newSseProtocolString(eventType, eventId, data), expected);
    }

    @Test(timeout = 60000)
    public void testMultipleDataLineDecode() throws Exception {
        String eventType = "add";
        String eventId = "1";
        String data1 = "data line";
        String data2 = "data line";
        ServerSentEvent expected1 = SseTestUtil.newServerSentEvent(eventType, eventId, data1);
        ServerSentEvent expected2 = SseTestUtil.newServerSentEvent(eventType, eventId, data2);
        doTest(SseTestUtil.newSseProtocolString(eventType, eventId, data1, data2), expected1, expected2);
    }

    @Test(timeout = 60000)
    public void testEventWithNoIdDecode() throws Exception {
        String eventType = "add";
        String data = "data line";
        ServerSentEvent expected = SseTestUtil.newServerSentEvent(eventType, null, data);
        doTest(SseTestUtil.newSseProtocolString(eventType, null, data), expected);
    }

    @Test(timeout = 60000)
    public void testEventWithNoEventTypeDecode() throws Exception {
        String eventId = "1";
        String data = "data line";
        ServerSentEvent expected = SseTestUtil.newServerSentEvent(null, eventId, data);
        doTest(SseTestUtil.newSseProtocolString(null, eventId, data), expected);
    }

    @Test(timeout = 60000)
    public void testEventWithDataOnlyDecode() throws Exception {
        String data = "data line";
        ServerSentEvent expected = SseTestUtil.newServerSentEvent(null, null, data);
        doTest(SseTestUtil.newSseProtocolString(null, null, data), expected);
    }

    @Test(timeout = 60000)
    public void testResetEventType() throws Exception {
        String eventType = "add";
        String eventId = "1";
        String data1 = "data line";
        String data2 = "data line";
        ServerSentEvent expected1 = SseTestUtil.newServerSentEvent(eventType, eventId, data1);
        ServerSentEvent expected2 = SseTestUtil.newServerSentEvent(null, eventId, data2);
        doTest(((SseTestUtil.newSseProtocolString(eventType, eventId, data1)) + (SseTestUtil.newSseProtocolString("", null, data2))), expected1, expected2);
    }

    @Test(timeout = 60000)
    public void testResetEventId() throws Exception {
        String eventType = "add";
        String eventId = "1";
        String data1 = "data line";
        String data2 = "data line";
        ServerSentEvent expected1 = SseTestUtil.newServerSentEvent(eventType, eventId, data1);
        ServerSentEvent expected2 = SseTestUtil.newServerSentEvent(eventType, null, data2);
        doTest(((SseTestUtil.newSseProtocolString(eventType, eventId, data1)) + (SseTestUtil.newSseProtocolString(null, "", data2))), expected1, expected2);
    }

    @Test(timeout = 60000)
    public void testIncompleteEventId() throws Exception {
        List<Object> out = new ArrayList<>();
        decoder.decode(ch, SseTestUtil.toHttpContent("id: 111"), out);
        Assert.assertEquals("Unexpected number of decoded messages.", 0, out.size());
        ServerSentEvent expected = SseTestUtil.newServerSentEvent(null, "1111", "data line");
        doTest("1\ndata: data line\n", expected);
    }

    @Test(timeout = 60000)
    public void testIncompleteEventType() throws Exception {
        List<Object> out = new ArrayList<>();
        decoder.decode(ch, SseTestUtil.toHttpContent("event: ad"), out);
        Assert.assertEquals("Unexpected number of decoded messages.", 0, out.size());
        ServerSentEvent expected = SseTestUtil.newServerSentEvent("add", null, "data line");
        doTest("d\ndata: data line\n", expected);
    }

    @Test(timeout = 60000)
    public void testIncompleteEventData() throws Exception {
        ServerSentEvent expected = SseTestUtil.newServerSentEvent("add", null, "data line");
        List<Object> out = new ArrayList<>();
        decoder.decode(ch, SseTestUtil.toHttpContent("event: add\n"), out);
        Assert.assertEquals("Unexpected number of decoded messages.", 0, out.size());
        decoder.decode(ch, SseTestUtil.toHttpContent("data: d"), out);
        Assert.assertEquals("Unexpected number of decoded messages.", 0, out.size());
        doTest("ata line\n", expected);
    }

    @Test(timeout = 60000)
    public void testIncompleteFieldName() throws Exception {
        ServerSentEvent expected = SseTestUtil.newServerSentEvent("add", null, "data line");
        List<Object> out = new ArrayList<>();
        decoder.decode(ch, SseTestUtil.toHttpContent("ev"), out);
        Assert.assertEquals("Unexpected number of decoded messages.", 0, out.size());
        decoder.decode(ch, SseTestUtil.toHttpContent("ent: add\n d"), out);
        Assert.assertEquals("Unexpected number of decoded messages.", 0, out.size());
        doTest("ata: data line\n", expected);
    }

    @Test(timeout = 60000)
    public void testInvalidFieldNameAndNextEvent() throws Exception {
        ArrayList<Object> out = new ArrayList<>();
        decoder.decode(ch, SseTestUtil.toHttpContent("event: event type\n"), out);
        Assert.assertTrue("Output list not empty.", out.isEmpty());
        decoder.decode(ch, SseTestUtil.toHttpContent("data: dumb \n"), out);
        Assert.assertFalse("Event not emitted after invalid field name.", out.isEmpty());
        Assert.assertEquals("Unexpected event count after invalid field name.", 1, out.size());
    }

    @Test(timeout = 60000)
    public void testInvalidFieldName() throws Throwable {
        ArrayList<Object> out = new ArrayList<>();
        decoder.decode(ch, SseTestUtil.toHttpContent("event: dumb \n"), out);
        Assert.assertTrue("Event emitted for invalid field name.", out.isEmpty());
    }

    @Test(timeout = 60000)
    public void testFieldNameWithSpace() throws Throwable {
        ArrayList<Object> out = new ArrayList<>();
        decoder.decode(ch, SseTestUtil.toHttpContent("eve nt: dumb \n"), new ArrayList());
        Assert.assertTrue("Event emitted for invalid field name.", out.isEmpty());
    }

    @Test(timeout = 60000)
    public void testDataInMultipleChunks() throws Exception {
        ServerSentEvent expected = SseTestUtil.newServerSentEvent(null, null, "data line");
        List<Object> out = new ArrayList<>();
        decoder.decode(ch, SseTestUtil.toHttpContent("da"), out);
        Assert.assertEquals("Unexpected number of decoded messages.", 0, out.size());
        decoder.decode(ch, SseTestUtil.toHttpContent("ta: d"), out);
        Assert.assertEquals("Unexpected number of decoded messages.", 0, out.size());
        decoder.decode(ch, SseTestUtil.toHttpContent("ata"), out);
        Assert.assertEquals("Unexpected number of decoded messages.", 0, out.size());
        decoder.decode(ch, SseTestUtil.toHttpContent(" "), out);
        Assert.assertEquals("Unexpected number of decoded messages.", 0, out.size());
        decoder.decode(ch, SseTestUtil.toHttpContent("li"), out);
        Assert.assertEquals("Unexpected number of decoded messages.", 0, out.size());
        decoder.decode(ch, SseTestUtil.toHttpContent("ne"), out);
        Assert.assertEquals("Unexpected number of decoded messages.", 0, out.size());
        doTest("\n", expected);
    }

    @Test(timeout = 10000)
    public void testLeadingNewLineInFieldName() throws Exception {
        List<Object> out = new ArrayList<>();
        decoder.decode(ch, SseTestUtil.toHttpContent("\n data: ad\n"), out);
        Assert.assertEquals("Unexpected number of decoded messages.", 1, out.size());
    }

    @Test(timeout = 10000)
    public void testLeadingSpaceInFieldName() throws Exception {
        List<Object> out = new ArrayList<>();
        decoder.decode(ch, SseTestUtil.toHttpContent("              data: ad\n"), out);
        Assert.assertEquals("Unexpected number of decoded messages.", 1, out.size());
        ServerSentEvent event = ((ServerSentEvent) (out.get(0)));
        Assert.assertEquals("Unexpected event type.", Data, event.getType());
        Assert.assertEquals("Unexpected event type.", "ad", event.contentAsString());
    }
}

