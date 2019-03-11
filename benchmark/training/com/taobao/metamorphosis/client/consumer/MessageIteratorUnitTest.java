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
package com.taobao.metamorphosis.client.consumer;


import MessageUtils.HEADER_LEN;
import com.taobao.metamorphosis.Message;
import com.taobao.metamorphosis.MessageAccessor;
import com.taobao.metamorphosis.consumer.MessageIterator;
import com.taobao.metamorphosis.exception.InvalidMessageException;
import com.taobao.metamorphosis.utils.CheckSum;
import com.taobao.metamorphosis.utils.MessageUtils;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class MessageIteratorUnitTest {
    private MessageIterator it;

    @Test
    public void testHasNext_NullData() {
        this.it = new MessageIterator("test", null);
        Assert.assertFalse(this.it.hasNext());
    }

    @Test
    public void testHasNext_EmptyData() {
        this.it = new MessageIterator("test", new byte[0]);
        Assert.assertFalse(this.it.hasNext());
    }

    @Test
    public void testHasNext_End() {
        this.it = new MessageIterator("test", new byte[16]);
        this.it.setOffset(16);
        Assert.assertFalse(this.it.hasNext());
    }

    @Test
    public void testHasNext_Over() {
        this.it = new MessageIterator("test", new byte[16]);
        this.it.setOffset(17);
        Assert.assertFalse(this.it.hasNext());
    }

    @Test
    public void testIteratorAsKey() {
        this.it = new MessageIterator("test", new byte[16]);
        final Map<MessageIterator, Integer> map = new HashMap<MessageIterator, Integer>();
        Assert.assertNull(map.get(this.it));
        map.put(this.it, 100);
        Assert.assertEquals(((Integer) (100)), map.get(this.it));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void tsetRemove() {
        this.it = new MessageIterator("test", new byte[16]);
        this.it.remove();
    }

    @Test
    public void testHasNext_HeaderNotComplete() {
        this.it = new MessageIterator("test", new byte[16]);
        Assert.assertFalse(this.it.hasNext());
    }

    @Test
    public void testHasNext_NotEnoughPayload() {
        final ByteBuffer buf = ByteBuffer.allocate(HEADER_LEN);
        buf.putInt(20);// msg length

        this.it = new MessageIterator("test", buf.array());
        Assert.assertFalse(this.it.hasNext());
    }

    @Test
    public void testHasNext_true() {
        final ByteBuffer buf = ByteBuffer.allocate(((MessageUtils.HEADER_LEN) + 4));
        buf.putInt(4);// msg length

        buf.position(20);
        buf.putInt(99);// payload

        this.it = new MessageIterator("test", buf.array());
        Assert.assertTrue(this.it.hasNext());
    }

    @Test(expected = InvalidMessageException.class)
    public void testNext_InvalidMessage() throws Exception {
        final ByteBuffer buf = ByteBuffer.allocate(((MessageUtils.HEADER_LEN) + 5));
        buf.putInt(5);// msg length

        buf.putInt(CheckSum.crc32("hello".getBytes()));// checksum

        buf.position(HEADER_LEN);
        buf.put("world".getBytes());
        this.it = new MessageIterator("test", buf.array());
        Assert.assertTrue(this.it.hasNext());
        this.it.next();
    }

    @Test
    public void testNext_NoAttribute() throws Exception {
        final ByteBuffer buf = ByteBuffer.allocate(((MessageUtils.HEADER_LEN) + 5));
        buf.putInt(5);// msg length

        buf.putInt(CheckSum.crc32("hello".getBytes()));// checksum

        buf.putLong(9999);// id

        buf.putInt(0);// flag

        buf.position(HEADER_LEN);
        buf.put("hello".getBytes());
        this.it = new MessageIterator("test", buf.array());
        Assert.assertTrue(this.it.hasNext());
        final Message msg = this.it.next();
        Assert.assertNotNull(msg);
        Assert.assertEquals(9999L, msg.getId());
        Assert.assertEquals("test", msg.getTopic());
        Assert.assertFalse(msg.hasAttribute());
        Assert.assertEquals(0, MessageAccessor.getFlag(msg));
        Assert.assertEquals("hello", new String(msg.getData()));
    }

    @Test
    public void testNext_HasAttribute() throws Exception {
        final ByteBuffer dataBuf = ByteBuffer.allocate(18);
        dataBuf.putInt(9);
        dataBuf.put("attribute".getBytes());
        dataBuf.put("hello".getBytes());
        dataBuf.flip();
        final ByteBuffer buf = ByteBuffer.allocate(((((MessageUtils.HEADER_LEN) + 4) + 9) + 5));
        buf.putInt(18);// msg length

        buf.putInt(CheckSum.crc32(dataBuf.array()));// checksum

        buf.putLong(9999);// id

        buf.putInt(1);// flag

        buf.position(HEADER_LEN);
        buf.put(dataBuf);
        this.it = new MessageIterator("test", buf.array());
        Assert.assertTrue(this.it.hasNext());
        final Message msg = this.it.next();
        Assert.assertNotNull(msg);
        Assert.assertEquals(9999L, msg.getId());
        Assert.assertEquals("test", msg.getTopic());
        Assert.assertTrue(msg.hasAttribute());
        Assert.assertEquals("attribute", msg.getAttribute());
        Assert.assertEquals(1, MessageAccessor.getFlag(msg));
        Assert.assertEquals("hello", new String(msg.getData()));
        Assert.assertFalse(this.it.hasNext());
        Assert.assertEquals(38, this.it.getOffset());
    }
}

