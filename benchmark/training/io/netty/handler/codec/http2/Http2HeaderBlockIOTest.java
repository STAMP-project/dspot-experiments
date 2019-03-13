/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.handler.codec.http2;


import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for encoding/decoding HTTP2 header blocks.
 */
public class Http2HeaderBlockIOTest {
    private DefaultHttp2HeadersDecoder decoder;

    private DefaultHttp2HeadersEncoder encoder;

    private ByteBuf buffer;

    @Test
    public void roundtripShouldBeSuccessful() throws Http2Exception {
        Http2Headers in = Http2HeaderBlockIOTest.headers();
        assertRoundtripSuccessful(in);
    }

    @Test
    public void successiveCallsShouldSucceed() throws Http2Exception {
        Http2Headers in = new DefaultHttp2Headers().method(new AsciiString("GET")).scheme(new AsciiString("https")).authority(new AsciiString("example.org")).path(new AsciiString("/some/path")).add(new AsciiString("accept"), new AsciiString("*/*"));
        assertRoundtripSuccessful(in);
        in = new DefaultHttp2Headers().method(new AsciiString("GET")).scheme(new AsciiString("https")).authority(new AsciiString("example.org")).path(new AsciiString("/some/path/resource1")).add(new AsciiString("accept"), new AsciiString("image/jpeg")).add(new AsciiString("cache-control"), new AsciiString("no-cache"));
        assertRoundtripSuccessful(in);
        in = new DefaultHttp2Headers().method(new AsciiString("GET")).scheme(new AsciiString("https")).authority(new AsciiString("example.org")).path(new AsciiString("/some/path/resource2")).add(new AsciiString("accept"), new AsciiString("image/png")).add(new AsciiString("cache-control"), new AsciiString("no-cache"));
        assertRoundtripSuccessful(in);
    }

    @Test
    public void setMaxHeaderSizeShouldBeSuccessful() throws Http2Exception {
        encoder.maxHeaderTableSize(10);
        Http2Headers in = Http2HeaderBlockIOTest.headers();
        assertRoundtripSuccessful(in);
        Assert.assertEquals(10, decoder.maxHeaderTableSize());
    }
}

