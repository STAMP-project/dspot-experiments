/**
 * Copyright 2014 The Netty Project
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
package io.netty.handler.codec.http.websocketx;


import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;


/**
 * Tests the WebSocket08FrameEncoder and Decoder implementation.<br>
 * Checks whether the combination of encoding and decoding yields the original data.<br>
 * Thereby also the masking behavior is checked.
 */
public class WebSocket08EncoderDecoderTest {
    private ByteBuf binTestData;

    private String strTestData;

    private static final int MAX_TESTDATA_LENGTH = 100 * 1024;

    @Test
    public void testWebSocketEncodingAndDecoding() {
        initTestData();
        // Test without masking
        EmbeddedChannel outChannel = new EmbeddedChannel(new WebSocket08FrameEncoder(false));
        EmbeddedChannel inChannel = new EmbeddedChannel(new WebSocket08FrameDecoder(false, false, (1024 * 1024), false));
        executeTests(outChannel, inChannel);
        // Test with activated masking
        outChannel = new EmbeddedChannel(new WebSocket08FrameEncoder(true));
        inChannel = new EmbeddedChannel(new WebSocket08FrameDecoder(true, false, (1024 * 1024), false));
        executeTests(outChannel, inChannel);
        // Test with activated masking and an unmasked expecting but forgiving decoder
        outChannel = new EmbeddedChannel(new WebSocket08FrameEncoder(true));
        inChannel = new EmbeddedChannel(new WebSocket08FrameDecoder(false, false, (1024 * 1024), true));
        executeTests(outChannel, inChannel);
        // Release test data
        binTestData.release();
    }
}

