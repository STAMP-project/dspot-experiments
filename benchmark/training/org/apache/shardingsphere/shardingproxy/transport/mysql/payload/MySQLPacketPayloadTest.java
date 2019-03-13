/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.shardingproxy.transport.mysql.payload;


import io.netty.buffer.ByteBuf;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class MySQLPacketPayloadTest {
    @Mock
    private ByteBuf byteBuf;

    @Test
    public void assertReadInt1() {
        Mockito.when(byteBuf.readByte()).thenReturn(((byte) (1)));
        Assert.assertThat(readInt1(), CoreMatchers.is(1));
    }

    @Test
    public void assertWriteInt1() {
        writeInt1(1);
        Mockito.verify(byteBuf).writeByte(1);
    }

    @Test
    public void assertReadInt2() {
        Mockito.when(byteBuf.readShortLE()).thenReturn(((short) (1)));
        Assert.assertThat(readInt2(), CoreMatchers.is(1));
    }

    @Test
    public void assertWriteInt2() {
        writeInt2(1);
        Mockito.verify(byteBuf).writeShortLE(1);
    }

    @Test
    public void assertReadInt3() {
        Mockito.when(byteBuf.readMediumLE()).thenReturn(1);
        Assert.assertThat(readInt3(), CoreMatchers.is(1));
    }

    @Test
    public void assertWriteInt3() {
        writeInt3(1);
        Mockito.verify(byteBuf).writeMediumLE(1);
    }

    @Test
    public void assertReadInt4() {
        Mockito.when(byteBuf.readIntLE()).thenReturn(1);
        Assert.assertThat(readInt4(), CoreMatchers.is(1));
    }

    @Test
    public void assertWriteInt4() {
        writeInt4(1);
        Mockito.verify(byteBuf).writeIntLE(1);
    }

    @Test
    public void assertReadInt6() {
        Assert.assertThat(readInt6(), CoreMatchers.is(0));
    }

    @Test
    public void assertWriteInt6() {
        writeInt6(1);
    }

    @Test
    public void assertReadInt8() {
        Mockito.when(byteBuf.readLongLE()).thenReturn(1L);
        Assert.assertThat(readInt8(), CoreMatchers.is(1L));
    }

    @Test
    public void assertWriteInt8() {
        writeInt8(1L);
        Mockito.verify(byteBuf).writeLongLE(1L);
    }

    @Test
    public void assertReadIntLenencWithOneByte() {
        Mockito.when(byteBuf.readByte()).thenReturn(((byte) (1)));
        Assert.assertThat(readIntLenenc(), CoreMatchers.is(1L));
    }

    @Test
    public void assertReadIntLenencWithZero() {
        Mockito.when(byteBuf.readByte()).thenReturn(((byte) (251)));
        Assert.assertThat(readIntLenenc(), CoreMatchers.is(0L));
    }

    @Test
    public void assertReadIntLenencWithTwoBytes() {
        Mockito.when(byteBuf.readByte()).thenReturn(((byte) (252)));
        Mockito.when(byteBuf.readShortLE()).thenReturn(((short) (100)));
        Assert.assertThat(readIntLenenc(), CoreMatchers.is(100L));
    }

    @Test
    public void assertReadIntLenencWithThreeBytes() {
        Mockito.when(byteBuf.readByte()).thenReturn(((byte) (253)));
        Mockito.when(byteBuf.readMediumLE()).thenReturn(99999);
        Assert.assertThat(readIntLenenc(), CoreMatchers.is(99999L));
    }

    @Test
    public void assertReadIntLenencWithFourBytes() {
        Mockito.when(byteBuf.readByte()).thenReturn(((byte) (255)));
        Mockito.when(byteBuf.readLongLE()).thenReturn(Long.MAX_VALUE);
        Assert.assertThat(readIntLenenc(), CoreMatchers.is(Long.MAX_VALUE));
    }

    @Test
    public void assertWriteIntLenencWithOneByte() {
        writeIntLenenc(1L);
        Mockito.verify(byteBuf).writeByte(1);
    }

    @Test
    public void assertWriteIntLenencWithTwoBytes() {
        writeIntLenenc(((new Double(Math.pow(2, 16)).longValue()) - 1));
        Mockito.verify(byteBuf).writeByte(252);
        Mockito.verify(byteBuf).writeShortLE(((new Double(Math.pow(2, 16)).intValue()) - 1));
    }

    @Test
    public void assertWriteIntLenencWithThreeBytes() {
        writeIntLenenc(((new Double(Math.pow(2, 24)).longValue()) - 1));
        Mockito.verify(byteBuf).writeByte(253);
        Mockito.verify(byteBuf).writeMediumLE(((new Double(Math.pow(2, 24)).intValue()) - 1));
    }

    @Test
    public void assertWriteIntLenencWithFourBytes() {
        writeIntLenenc(((new Double(Math.pow(2, 25)).longValue()) - 1));
        Mockito.verify(byteBuf).writeByte(254);
        Mockito.verify(byteBuf).writeLongLE(((new Double(Math.pow(2, 25)).intValue()) - 1));
    }

    @Test
    public void assertReadStringLenenc() {
        Mockito.when(byteBuf.readByte()).thenReturn(((byte) (0)));
        Assert.assertThat(readStringLenenc(), CoreMatchers.is(""));
    }

    @Test
    public void assertReadStringLenencByBytes() {
        Mockito.when(byteBuf.readByte()).thenReturn(((byte) (0)));
        Assert.assertThat(readStringLenencByBytes(), CoreMatchers.is(new byte[]{  }));
    }

    @Test
    public void assertWriteStringLenencWithEmpty() {
        writeStringLenenc("");
        Mockito.verify(byteBuf).writeByte(0);
    }

    @Test
    public void assertWriteBytesLenenc() {
        writeBytesLenenc("value".getBytes());
        Mockito.verify(byteBuf).writeByte(5);
        writeBytes("value".getBytes());
    }

    @Test
    public void assertWriteBytesLenencWithEmpty() {
        writeBytesLenenc("".getBytes());
        Mockito.verify(byteBuf).writeByte(0);
    }

    @Test
    public void assertWriteStringLenenc() {
        writeStringLenenc("value");
        Mockito.verify(byteBuf).writeByte(5);
        writeBytes("value".getBytes());
    }

    @Test
    public void assertReadStringFix() {
        Assert.assertThat(readStringFix(0), CoreMatchers.is(""));
    }

    @Test
    public void assertReadStringFixByBytes() {
        Assert.assertThat(readStringFixByBytes(0), CoreMatchers.is(new byte[]{  }));
    }

    @Test
    public void assertWriteStringFix() {
        writeStringFix("value");
        writeBytes("value".getBytes());
    }

    @Test
    public void assertWriteBytes() {
        writeBytes("value".getBytes());
        writeBytes("value".getBytes());
    }

    @Test
    public void assertReadStringVar() {
        Assert.assertThat(readStringVar(), CoreMatchers.is(""));
    }

    @Test
    public void assertWriteStringVar() {
        writeStringVar("");
    }

    @Test
    public void assertReadStringNul() {
        Mockito.when(byteBuf.bytesBefore(((byte) (0)))).thenReturn(0);
        Assert.assertThat(readStringNul(), CoreMatchers.is(""));
        Mockito.verify(byteBuf).skipBytes(1);
    }

    @Test
    public void assertReadStringNulByBytes() {
        Mockito.when(byteBuf.bytesBefore(((byte) (0)))).thenReturn(0);
        Assert.assertThat(readStringNulByBytes(), CoreMatchers.is(new byte[]{  }));
        Mockito.verify(byteBuf).skipBytes(1);
    }

    @Test
    public void assertWriteStringNul() {
        writeStringNul("value");
        writeBytes("value".getBytes());
        Mockito.verify(byteBuf).writeByte(0);
    }

    @Test
    public void assertReadStringEOF() {
        Mockito.when(byteBuf.readableBytes()).thenReturn(0);
        Assert.assertThat(readStringEOF(), CoreMatchers.is(""));
    }

    @Test
    public void assertWriteStringEOF() {
        writeStringEOF("value");
        writeBytes("value".getBytes());
    }

    @Test
    public void assertSkipReserved() {
        skipReserved(10);
        Mockito.verify(byteBuf).skipBytes(10);
    }

    @Test
    public void assertWriteReserved() {
        writeReserved(10);
        Mockito.verify(byteBuf, Mockito.times(10)).writeByte(0);
    }

    @Test
    public void assertClose() {
        close();
        Mockito.verify(byteBuf).release();
    }
}

