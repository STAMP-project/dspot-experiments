/**
 * (C) 2007-2012 Alibaba Group Holding Limited.
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
 * Authors:
 *   wuhua <wq163@163.com> , boyan <killme2008@gmail.com>
 */
package com.taobao.metamorphosis.server.store;


import com.taobao.gecko.core.buffer.IoBuffer;
import com.taobao.gecko.service.Connection;
import com.taobao.metamorphosis.network.GetCommand;
import com.taobao.metamorphosis.network.PutCommand;
import com.taobao.metamorphosis.utils.MessageUtils;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class FileMessageSetUnitTest {
    private FileMessageSet fileMessageSet;

    private File file;

    @Test
    public void testMakeHead() throws Exception {
        final IoBuffer head = this.fileMessageSet.makeHead((-1999), 100);
        Assert.assertEquals(0, head.position());
        Assert.assertTrue(head.hasRemaining());
        Assert.assertEquals("value 100 -1999\r\n", new String(head.array()));
    }

    @Test
    public void testSlice() throws Exception {
        final long limit = 100;
        this.fileMessageSet.channel().position(limit);
        final ByteBuffer buf = ByteBuffer.allocate(1);
        buf.put(((byte) (1)));
        buf.flip();
        this.fileMessageSet.append(buf);
        this.fileMessageSet.setSizeInBytes(limit);
        this.fileMessageSet.flush();
        Assert.assertEquals(limit, this.fileMessageSet.highWaterMark());
        FileMessageSet subSet = ((FileMessageSet) (this.fileMessageSet.slice(1, 10)));
        Assert.assertNotNull(subSet);
        Assert.assertEquals(9, subSet.getSizeInBytes());
        Assert.assertEquals(1, subSet.getOffset());
        subSet = ((FileMessageSet) (this.fileMessageSet.slice(10, 210)));
        Assert.assertNotNull(subSet);
        Assert.assertEquals(((limit + 1) - 10), subSet.getSizeInBytes());
        Assert.assertEquals(10, subSet.getOffset());
    }

    @Test
    public void testAppendAppendCloseRecover() throws Exception {
        PutCommand message = new PutCommand("test", 0, "hello".getBytes(), null, 0, 0);
        final ByteBuffer buf1 = MessageUtils.makeMessageBuffer(1, message);
        Assert.assertEquals(0, this.fileMessageSet.append(buf1));
        message = new PutCommand("test", 0, "world".getBytes(), null, 0, 0);
        final ByteBuffer buf2 = MessageUtils.makeMessageBuffer(2, message);
        Assert.assertEquals(buf1.capacity(), this.fileMessageSet.append(buf2));
        this.fileMessageSet.flush();
        this.fileMessageSet.close();
        final FileChannel channel = new RandomAccessFile(this.file, "rw").getChannel();
        this.fileMessageSet = new FileMessageSet(channel);
        Assert.assertEquals((2 * (buf2.capacity())), this.fileMessageSet.getSizeInBytes());
        Assert.assertEquals((2 * (buf2.capacity())), this.fileMessageSet.channel().position());
    }

    @Test
    public void testAppendAppendCloseRecoverTruncate() throws Exception {
        PutCommand message = new PutCommand("test", 0, "hello".getBytes(), null, 0, 0);
        ByteBuffer buf = MessageUtils.makeMessageBuffer(1, message);
        this.fileMessageSet.append(buf);
        message = new PutCommand("test", 0, "world".getBytes(), null, 0, 0);
        buf = MessageUtils.makeMessageBuffer(2, message);
        this.fileMessageSet.append(buf);
        this.fileMessageSet.append(ByteBuffer.wrap("temp".getBytes()));
        this.fileMessageSet.flush();
        this.fileMessageSet.close();
        final FileChannel channel = new RandomAccessFile(this.file, "rw").getChannel();
        this.fileMessageSet = new FileMessageSet(channel);
        Assert.assertEquals((2 * (buf.capacity())), this.fileMessageSet.getSizeInBytes());
        Assert.assertEquals((2 * (buf.capacity())), this.fileMessageSet.channel().position());
    }

    @Test
    public void testAppendAppendFlushSliceWrite() throws IOException {
        final String str = "hello world";
        final ByteBuffer buf = ByteBuffer.wrap(str.getBytes());
        Assert.assertEquals(0, this.fileMessageSet.append(buf));
        buf.rewind();
        Assert.assertEquals(buf.capacity(), this.fileMessageSet.append(buf));
        Assert.assertEquals(0L, this.fileMessageSet.highWaterMark());
        this.fileMessageSet.flush();
        Assert.assertEquals((2L * (str.length())), this.fileMessageSet.highWaterMark());
        final FileMessageSet subSet = ((FileMessageSet) (this.fileMessageSet.slice(0, 100)));
        Assert.assertEquals((2L * (str.length())), subSet.highWaterMark());
        final Connection conn = EasyMock.createMock(Connection.class);
        EasyMock.expect(conn.getRemoteSocketAddress()).andReturn(new InetSocketAddress(8181)).anyTimes();
        final int opaque = 99;
        final IoBuffer head = IoBuffer.wrap((((("value " + (2 * (str.length()))) + " ") + opaque) + "\r\n").getBytes());
        conn.transferFrom(head, null, this.fileMessageSet.channel(), 0, (2 * (str.length())));
        EasyMock.expectLastCall();
        EasyMock.replay(conn);
        subSet.write(new GetCommand("test", "boyan-test", (-1), 0, (1024 * 1024), opaque), new com.taobao.metamorphosis.server.network.SessionContextImpl(null, conn));
        EasyMock.verify(conn);
    }
}

