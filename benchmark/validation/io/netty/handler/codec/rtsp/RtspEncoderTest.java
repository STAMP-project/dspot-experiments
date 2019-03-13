/**
 * Copyright 2015 The Netty Project
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
package io.netty.handler.codec.rtsp;


import CharsetUtil.UTF_8;
import RtspHeaderNames.CONTENT_LENGTH;
import RtspHeaderNames.CONTENT_TYPE;
import RtspHeaderNames.CSEQ;
import RtspHeaderNames.SERVER;
import RtspHeaderNames.SESSION;
import RtspHeaderNames.TRANSPORT;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.junit.Assert;
import org.junit.Test;

import static RtspMethods.GET_PARAMETER;
import static RtspMethods.SETUP;
import static RtspResponseStatuses.OK;
import static RtspVersions.RTSP_1_0;


/**
 * Test cases for RTSP encoder.
 */
public class RtspEncoderTest {
    /**
     * Test of a SETUP request, with no body.
     */
    @Test
    public void testSendSetupRequest() {
        String expected = "SETUP rtsp://172.10.20.30:554/d3abaaa7-65f2-42b4-" + ((((("8d6b-379f492fcf0f RTSP/1.0\r\n" + "transport: MP2T/DVBC/UDP;unicast;client=01234567;") + "source=172.10.20.30;") + "destination=1.1.1.1;client_port=6922\r\n") + "cseq: 1\r\n") + "\r\n");
        HttpRequest request = new io.netty.handler.codec.http.DefaultHttpRequest(RTSP_1_0, SETUP, "rtsp://172.10.20.30:554/d3abaaa7-65f2-42b4-8d6b-379f492fcf0f");
        request.headers().add(TRANSPORT, ("MP2T/DVBC/UDP;unicast;client=01234567;source=172.10.20.30;" + "destination=1.1.1.1;client_port=6922"));
        request.headers().add(CSEQ, "1");
        EmbeddedChannel ch = new EmbeddedChannel(new RtspEncoder());
        ch.writeOutbound(request);
        ByteBuf buf = ch.readOutbound();
        String actual = buf.toString(UTF_8);
        buf.release();
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test of a GET_PARAMETER request, with body.
     */
    @Test
    public void testSendGetParameterRequest() {
        String expected = "GET_PARAMETER rtsp://172.10.20.30:554 RTSP/1.0\r\n" + ((((((("session: 2547019973447939919\r\n" + "cseq: 3\r\n") + "content-length: 31\r\n") + "content-type: text/parameters\r\n") + "\r\n") + "stream_state\r\n") + "position\r\n") + "scale\r\n");
        byte[] content = ("stream_state\r\n" + ("position\r\n" + "scale\r\n")).getBytes(UTF_8);
        FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(RTSP_1_0, GET_PARAMETER, "rtsp://172.10.20.30:554");
        request.headers().add(SESSION, "2547019973447939919");
        request.headers().add(CSEQ, "3");
        request.headers().add(CONTENT_LENGTH, ("" + (content.length)));
        request.headers().add(CONTENT_TYPE, "text/parameters");
        request.content().writeBytes(content);
        EmbeddedChannel ch = new EmbeddedChannel(new RtspEncoder());
        ch.writeOutbound(request);
        ByteBuf buf = ch.readOutbound();
        String actual = buf.toString(UTF_8);
        buf.release();
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test of a 200 OK response, without body.
     */
    @Test
    public void testSend200OkResponseWithoutBody() {
        String expected = "RTSP/1.0 200 OK\r\n" + ((("server: Testserver\r\n" + "cseq: 1\r\n") + "session: 2547019973447939919\r\n") + "\r\n");
        HttpResponse response = new io.netty.handler.codec.http.DefaultHttpResponse(RTSP_1_0, OK);
        response.headers().add(SERVER, "Testserver");
        response.headers().add(CSEQ, "1");
        response.headers().add(SESSION, "2547019973447939919");
        EmbeddedChannel ch = new EmbeddedChannel(new RtspEncoder());
        ch.writeOutbound(response);
        ByteBuf buf = ch.readOutbound();
        String actual = buf.toString(UTF_8);
        buf.release();
        Assert.assertEquals(expected, actual);
    }

    /**
     * Test of a 200 OK response, with body.
     */
    @Test
    public void testSend200OkResponseWithBody() {
        String expected = "RTSP/1.0 200 OK\r\n" + (((((((("server: Testserver\r\n" + "session: 2547019973447939919\r\n") + "content-type: text/parameters\r\n") + "content-length: 50\r\n") + "cseq: 3\r\n") + "\r\n") + "position: 24\r\n") + "stream_state: playing\r\n") + "scale: 1.00\r\n");
        byte[] content = ("position: 24\r\n" + ("stream_state: playing\r\n" + "scale: 1.00\r\n")).getBytes(UTF_8);
        FullHttpResponse response = new io.netty.handler.codec.http.DefaultFullHttpResponse(RTSP_1_0, OK);
        response.headers().add(SERVER, "Testserver");
        response.headers().add(SESSION, "2547019973447939919");
        response.headers().add(CONTENT_TYPE, "text/parameters");
        response.headers().add(CONTENT_LENGTH, ("" + (content.length)));
        response.headers().add(CSEQ, "3");
        response.content().writeBytes(content);
        EmbeddedChannel ch = new EmbeddedChannel(new RtspEncoder());
        ch.writeOutbound(response);
        ByteBuf buf = ch.readOutbound();
        String actual = buf.toString(UTF_8);
        buf.release();
        Assert.assertEquals(expected, actual);
    }
}

