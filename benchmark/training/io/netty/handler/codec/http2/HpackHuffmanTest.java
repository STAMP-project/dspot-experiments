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
/**
 * Copyright 2014 Twitter, Inc.
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
package io.netty.handler.codec.http2;


import java.util.Random;
import org.junit.Test;


public class HpackHuffmanTest {
    @Test
    public void testHuffman() throws Http2Exception {
        String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < (s.length()); i++) {
            HpackHuffmanTest.roundTrip(s.substring(0, i));
        }
        Random random = new Random(123456789L);
        byte[] buf = new byte[4096];
        random.nextBytes(buf);
        HpackHuffmanTest.roundTrip(buf);
    }

    @Test(expected = Http2Exception.class)
    public void testDecodeEOS() throws Http2Exception {
        byte[] buf = new byte[4];
        for (int i = 0; i < 4; i++) {
            buf[i] = ((byte) (255));
        }
        HpackHuffmanTest.decode(HpackHuffmanTest.newHuffmanDecoder(), buf);
    }

    @Test(expected = Http2Exception.class)
    public void testDecodeIllegalPadding() throws Http2Exception {
        byte[] buf = new byte[1];
        buf[0] = 0;// '0', invalid padding

        HpackHuffmanTest.decode(HpackHuffmanTest.newHuffmanDecoder(), buf);
    }

    @Test(expected = Http2Exception.class)
    public void testDecodeExtraPadding() throws Http2Exception {
        byte[] buf = HpackHuffmanTest.makeBuf(15, 255);// '1', 'EOS'

        HpackHuffmanTest.decode(HpackHuffmanTest.newHuffmanDecoder(), buf);
    }

    @Test(expected = Http2Exception.class)
    public void testDecodeExtraPadding1byte() throws Http2Exception {
        byte[] buf = HpackHuffmanTest.makeBuf(255);
        HpackHuffmanTest.decode(HpackHuffmanTest.newHuffmanDecoder(), buf);
    }

    @Test(expected = Http2Exception.class)
    public void testDecodeExtraPadding2byte() throws Http2Exception {
        byte[] buf = HpackHuffmanTest.makeBuf(31, 255);// 'a'

        HpackHuffmanTest.decode(HpackHuffmanTest.newHuffmanDecoder(), buf);
    }

    @Test(expected = Http2Exception.class)
    public void testDecodeExtraPadding3byte() throws Http2Exception {
        byte[] buf = HpackHuffmanTest.makeBuf(31, 255, 255);// 'a'

        HpackHuffmanTest.decode(HpackHuffmanTest.newHuffmanDecoder(), buf);
    }

    @Test(expected = Http2Exception.class)
    public void testDecodeExtraPadding4byte() throws Http2Exception {
        byte[] buf = HpackHuffmanTest.makeBuf(31, 255, 255, 255);// 'a'

        HpackHuffmanTest.decode(HpackHuffmanTest.newHuffmanDecoder(), buf);
    }

    @Test(expected = Http2Exception.class)
    public void testDecodeExtraPadding29bit() throws Http2Exception {
        byte[] buf = HpackHuffmanTest.makeBuf(255, 159, 255, 255, 255);// '|'

        HpackHuffmanTest.decode(HpackHuffmanTest.newHuffmanDecoder(), buf);
    }

    @Test(expected = Http2Exception.class)
    public void testDecodePartialSymbol() throws Http2Exception {
        byte[] buf = HpackHuffmanTest.makeBuf(82, 188, 48, 255, 255, 255, 255);// " pFA\x00", 31 bits of padding, a.k.a. EOS

        HpackHuffmanTest.decode(HpackHuffmanTest.newHuffmanDecoder(), buf);
    }
}

