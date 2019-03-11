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
package io.netty.handler.codec.protobuf;


import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class ProtobufVarint32LengthFieldPrependerTest {
    private EmbeddedChannel ch;

    @Test
    public void testSize1Varint() {
        final int size = 1;
        final int num = 10;
        Assert.assertThat(ProtobufVarint32LengthFieldPrepender.computeRawVarint32Size(num), Is.is(size));
        final byte[] buf = new byte[size + num];
        // 0000 1010
        buf[0] = 10;
        for (int i = size; i < (num + size); ++i) {
            buf[i] = 1;
        }
        Assert.assertTrue(ch.writeOutbound(wrappedBuffer(buf, size, ((buf.length) - size))));
        ByteBuf expected = wrappedBuffer(buf);
        ByteBuf actual = ch.readOutbound();
        Assert.assertThat(expected, Is.is(actual));
        Assert.assertFalse(ch.finish());
        expected.release();
        actual.release();
    }

    @Test
    public void testSize2Varint() {
        final int size = 2;
        final int num = 266;
        Assert.assertThat(ProtobufVarint32LengthFieldPrepender.computeRawVarint32Size(num), Is.is(size));
        final byte[] buf = new byte[size + num];
        /**
         * 8    A    0    2
         * 1000 1010 0000 0010
         * 0000 1010 0000 0010
         * 0000 0010 0000 1010
         *  000 0010  000 1010
         *
         *  0000 0001 0000 1010
         *  0    1    0    A
         * 266
         */
        buf[0] = ((byte) (138 & 255));
        buf[1] = 2;
        for (int i = size; i < (num + size); ++i) {
            buf[i] = 1;
        }
        Assert.assertTrue(ch.writeOutbound(wrappedBuffer(buf, size, ((buf.length) - size))));
        ByteBuf expected = wrappedBuffer(buf);
        ByteBuf actual = ch.readOutbound();
        Assert.assertThat(actual, Is.is(expected));
        Assert.assertFalse(ch.finish());
        expected.release();
        actual.release();
    }

    @Test
    public void testSize3Varint() {
        final int size = 3;
        final int num = 16384;
        Assert.assertThat(ProtobufVarint32LengthFieldPrepender.computeRawVarint32Size(num), Is.is(size));
        final byte[] buf = new byte[size + num];
        /**
         * 8    0    8    0    0    1
         * 1000 0000 1000 0000 0000 0001
         * 0000 0000 0000 0000 0000 0001
         * 0000 0001 0000 0000 0000 0000
         *  000 0001  000 0000  000 0000
         *
         *    0 0000 0100 0000 0000 0000
         *    0    0    4    0    0    0
         */
        buf[0] = ((byte) (128 & 255));
        buf[1] = ((byte) (128 & 255));
        buf[2] = 1;
        for (int i = size; i < (num + size); ++i) {
            buf[i] = 1;
        }
        Assert.assertTrue(ch.writeOutbound(wrappedBuffer(buf, size, ((buf.length) - size))));
        ByteBuf expected = wrappedBuffer(buf);
        ByteBuf actual = ch.readOutbound();
        Assert.assertThat(expected, Is.is(actual));
        Assert.assertFalse(ch.finish());
        expected.release();
        actual.release();
    }

    @Test
    public void testSize4Varint() {
        final int size = 4;
        final int num = 2097152;
        Assert.assertThat(ProtobufVarint32LengthFieldPrepender.computeRawVarint32Size(num), Is.is(size));
        final byte[] buf = new byte[size + num];
        /**
         * 8    0    8    0    8    0    0    1
         * 1000 0000 1000 0000 1000 0000 0000 0001
         * 0000 0000 0000 0000 0000 0000 0000 0001
         * 0000 0001 0000 0000 0000 0000 0000 0000
         *  000 0001  000 0000  000 0000  000 0000
         *
         *    0000 0010 0000 0000 0000 0000 0000
         *    0    2    0    0    0    0    0
         */
        buf[0] = ((byte) (128 & 255));
        buf[1] = ((byte) (128 & 255));
        buf[2] = ((byte) (128 & 255));
        buf[3] = 1;
        for (int i = size; i < (num + size); ++i) {
            buf[i] = 1;
        }
        Assert.assertTrue(ch.writeOutbound(wrappedBuffer(buf, size, ((buf.length) - size))));
        ByteBuf expected = wrappedBuffer(buf);
        ByteBuf actual = ch.readOutbound();
        Assert.assertThat(actual, Is.is(expected));
        Assert.assertFalse(ch.finish());
        expected.release();
        actual.release();
    }

    @Test
    public void testTinyEncode() {
        byte[] b = new byte[]{ 4, 1, 1, 1, 1 };
        Assert.assertTrue(ch.writeOutbound(wrappedBuffer(b, 1, ((b.length) - 1))));
        ByteBuf expected = wrappedBuffer(b);
        ByteBuf actual = ch.readOutbound();
        Assert.assertThat(actual, Is.is(expected));
        Assert.assertFalse(ch.finish());
        expected.release();
        actual.release();
    }

    @Test
    public void testRegularDecode() {
        byte[] b = new byte[2048];
        for (int i = 2; i < 2048; i++) {
            b[i] = 1;
        }
        b[0] = -2;
        b[1] = 15;
        Assert.assertTrue(ch.writeOutbound(wrappedBuffer(b, 2, ((b.length) - 2))));
        ByteBuf expected = wrappedBuffer(b);
        ByteBuf actual = ch.readOutbound();
        Assert.assertThat(actual, Is.is(expected));
        Assert.assertFalse(ch.finish());
        expected.release();
        actual.release();
    }
}

