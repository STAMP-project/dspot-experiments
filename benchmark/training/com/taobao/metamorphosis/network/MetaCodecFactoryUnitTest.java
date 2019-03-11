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
package com.taobao.metamorphosis.network;


import com.taobao.gecko.core.buffer.IoBuffer;
import com.taobao.gecko.core.core.CodecFactory.Decoder;
import com.taobao.metamorphosis.exception.MetaCodecException;
import com.taobao.metamorphosis.network.MetamorphosisWireFormatType.MetaCodecFactory;
import com.taobao.metamorphosis.transaction.LocalTransactionId;
import com.taobao.metamorphosis.transaction.TransactionId;
import com.taobao.metamorphosis.transaction.TransactionInfo;
import org.junit.Assert;
import org.junit.Test;

import static HttpStatus.NotFound;


public class MetaCodecFactoryUnitTest {
    private final MetaCodecFactory codecFactory = new MetaCodecFactory();

    final Decoder decoder = this.codecFactory.getDecoder();

    @Test
    public void testDecodeEmptyBuffer() {
        Assert.assertNull(this.decoder.decode(null, null));
        Assert.assertNull(this.decoder.decode(IoBuffer.allocate(0), null));
    }

    @Test
    public void testDecodeOldPutCommand() {
        final PutCommand putCommand = new PutCommand("test", 1, "hello".getBytes(), null, 0, 0);
        final PutCommand decodedCmd = ((PutCommand) (this.decoder.decode(IoBuffer.wrap("put test 1 5 0 0\r\nhello".getBytes()), null)));
        Assert.assertNotNull(decodedCmd);
        Assert.assertEquals(putCommand, decodedCmd);
    }

    @Test
    public void testDecodeNewPutCommand() {
        final PutCommand putCommand = new PutCommand("test", 1, "hello".getBytes(), 0, 100, null, 0);
        final PutCommand decodedCmd = ((PutCommand) (this.decoder.decode(IoBuffer.wrap("put test 1 5 0 100 0\r\nhello".getBytes()), null)));
        Assert.assertNotNull(decodedCmd);
        Assert.assertEquals(putCommand, decodedCmd);
    }

    @Test
    public void testDecodeOldPutCommand_HasTransactionId() {
        final TransactionId xid = new LocalTransactionId("test", 100);
        final PutCommand putCommand = new PutCommand("test", 1, "hello".getBytes(), xid, 0, 0);
        final PutCommand decodedCmd = ((PutCommand) (this.decoder.decode(IoBuffer.wrap("put test 1 5 0 TX:test:100 0\r\nhello".getBytes()), null)));
        Assert.assertNotNull(decodedCmd);
        Assert.assertNotNull(decodedCmd.getTransactionId());
        Assert.assertEquals(putCommand, decodedCmd);
        Assert.assertEquals(putCommand.getTransactionId(), decodedCmd.getTransactionId());
    }

    @Test
    public void testDecodeNewPutCommand_HasTransactionId() {
        final TransactionId xid = new LocalTransactionId("test", 100);
        final PutCommand putCommand = new PutCommand("test", 1, "hello".getBytes(), 0, 9999, xid, 0);
        final PutCommand decodedCmd = ((PutCommand) (this.decoder.decode(IoBuffer.wrap("put test 1 5 0 9999 TX:test:100 0\r\nhello".getBytes()), null)));
        Assert.assertNotNull(decodedCmd);
        Assert.assertNotNull(decodedCmd.getTransactionId());
        Assert.assertEquals(putCommand, decodedCmd);
        Assert.assertEquals(putCommand.getTransactionId(), decodedCmd.getTransactionId());
    }

    @Test
    public void testDecodeSyncCommand() {
        final SyncCommand syncCmd = new SyncCommand("test", 1, "hello".getBytes(), 0, 9999L, 0, (-1));
        final IoBuffer buf = syncCmd.encode();
        final SyncCommand decodedCmd = ((SyncCommand) (this.decoder.decode(buf, null)));
        Assert.assertNotNull(decodedCmd);
        Assert.assertEquals(9999L, decodedCmd.getMsgId());
        // assertNotNull(decodedCmd.getTransactionId());
        Assert.assertEquals(syncCmd, decodedCmd);
        Assert.assertEquals(syncCmd.getMsgId(), decodedCmd.getMsgId());
        Assert.assertFalse(buf.hasRemaining());
    }

    @Test
    public void testDecodeGetCommand() {
        final GetCommand cmd = new GetCommand("test", "boyan", 1, 1000L, (1024 * 1024), (-3));
        final IoBuffer buf = cmd.encode();
        final GetCommand decodedCmd = ((GetCommand) (this.decoder.decode(buf, null)));
        Assert.assertNotNull(decodedCmd);
        Assert.assertEquals(cmd, decodedCmd);
        Assert.assertFalse(buf.hasRemaining());
    }

    @Test
    public void testDecodeDataCommand() {
        final IoBuffer buf = IoBuffer.allocate(100);
        buf.put("value 5 99\r\nhello".getBytes());
        buf.flip();
        final DataCommand decodedCmd = ((DataCommand) (this.decoder.decode(buf, null)));
        Assert.assertNotNull(decodedCmd);
        Assert.assertEquals(((Integer) (99)), decodedCmd.getOpaque());
        Assert.assertEquals("hello", new String(decodedCmd.getData()));
        Assert.assertFalse(buf.hasRemaining());
    }

    @Test
    public void testDecodeBooleanCommand() {
        final BooleanCommand cmd = new BooleanCommand(NotFound, "not found", 99);
        final IoBuffer buf = cmd.encode();
        final BooleanCommand decodedCmd = ((BooleanCommand) (this.decoder.decode(buf, null)));
        Assert.assertNotNull(decodedCmd);
        Assert.assertEquals(cmd, decodedCmd);
        Assert.assertFalse(buf.hasRemaining());
    }

    @Test
    public void testDecodeVersion() {
        IoBuffer buf = IoBuffer.wrap("version\r\n".getBytes());
        VersionCommand versionCommand = ((VersionCommand) (this.decoder.decode(buf, null)));
        Assert.assertNotNull(versionCommand);
        Assert.assertEquals(Integer.MAX_VALUE, ((int) (versionCommand.getOpaque())));
        buf = IoBuffer.wrap("version -1\r\n".getBytes());
        versionCommand = ((VersionCommand) (this.decoder.decode(buf, null)));
        Assert.assertNotNull(versionCommand);
        Assert.assertEquals((-1), ((int) (versionCommand.getOpaque())));
    }

    @Test
    public void testDecodeOffsetCommand() {
        final OffsetCommand cmd = new OffsetCommand("test", "boyan", 1, 1000L, (-1));
        final IoBuffer buf = cmd.encode();
        final OffsetCommand decodedCmd = ((OffsetCommand) (this.decoder.decode(buf, null)));
        Assert.assertNotNull(decodedCmd);
        Assert.assertEquals(cmd, decodedCmd);
        Assert.assertFalse(buf.hasRemaining());
    }

    @Test(expected = MetaCodecException.class)
    public void testDecodeUnknowCommand() {
        final IoBuffer buf = IoBuffer.wrap("just for test\r\n".getBytes());
        this.decoder.decode(buf, null);
    }

    @Test
    public void testDecodeNotALine() {
        final IoBuffer buf = IoBuffer.wrap("just for test".getBytes());
        Assert.assertNull(this.decoder.decode(buf, null));
    }

    @Test
    public void decodePutCommandUnCompleteData() {
        IoBuffer buf = IoBuffer.wrap("put test 1 5 0 10\r\nhel".getBytes());
        PutCommand decodedCmd = ((PutCommand) (this.decoder.decode(buf, null)));
        Assert.assertNull(decodedCmd);
        Assert.assertEquals(0, buf.position());
        Assert.assertEquals(buf.capacity(), buf.remaining());
        buf = IoBuffer.wrap("put test 1 5 0 10\r\nhello".getBytes());
        decodedCmd = ((PutCommand) (this.decoder.decode(buf, null)));
        Assert.assertNotNull(decodedCmd);
        Assert.assertEquals("test", decodedCmd.getTopic());
        Assert.assertEquals(1, decodedCmd.getPartition());
        Assert.assertEquals(0, decodedCmd.getFlag());
        Assert.assertEquals(10, ((int) (decodedCmd.getOpaque())));
        Assert.assertEquals("hello", new String(decodedCmd.getData()));
        Assert.assertFalse(buf.hasRemaining());
    }

    @Test
    public void testDecodeTransactionCommand() {
        final IoBuffer buf = IoBuffer.wrap("transaction TX:sessionId:99 sessionId COMMIT_ONE_PHASE 100\r\n".getBytes());
        final TransactionCommand cmd = ((TransactionCommand) (this.decoder.decode(buf, null)));
        Assert.assertNotNull(cmd);
        Assert.assertEquals(100, ((int) (cmd.getOpaque())));
        Assert.assertEquals(0, cmd.getTransactionInfo().getTimeout());
        final TransactionInfo info = cmd.getTransactionInfo();
        Assert.assertNotNull(info);
        Assert.assertNotNull(info.getTransactionId());
        Assert.assertTrue(info.getTransactionId().isLocalTransaction());
        final LocalTransactionId id = ((LocalTransactionId) (info.getTransactionId()));
        Assert.assertEquals("sessionId", id.getSessionId());
        Assert.assertEquals(99, id.getValue());
    }

    @Test
    public void testDecodeTransactionCommandWithTimeout() {
        final IoBuffer buf = IoBuffer.wrap("transaction TX:sessionId:99 sessionId COMMIT_ONE_PHASE 3 100\r\n".getBytes());
        final TransactionCommand cmd = ((TransactionCommand) (this.decoder.decode(buf, null)));
        Assert.assertNotNull(cmd);
        Assert.assertEquals(100, ((int) (cmd.getOpaque())));
        Assert.assertEquals(3, cmd.getTransactionInfo().getTimeout());
        final TransactionInfo info = cmd.getTransactionInfo();
        Assert.assertNotNull(info);
        Assert.assertNotNull(info.getTransactionId());
        Assert.assertTrue(info.getTransactionId().isLocalTransaction());
        final LocalTransactionId id = ((LocalTransactionId) (info.getTransactionId()));
        Assert.assertEquals("sessionId", id.getSessionId());
        Assert.assertEquals(99, id.getValue());
    }

    @Test
    public void testDecodeTransactionCommandWithUniqueQualifier() {
        final IoBuffer buf = IoBuffer.wrap("transaction TX:sessionId:99 sessionId COMMIT_ONE_PHASE unique-qualifier 100\r\n".getBytes());
        final TransactionCommand cmd = ((TransactionCommand) (this.decoder.decode(buf, null)));
        Assert.assertNotNull(cmd);
        Assert.assertEquals(100, ((int) (cmd.getOpaque())));
        Assert.assertEquals(0, cmd.getTransactionInfo().getTimeout());
        Assert.assertEquals("unique-qualifier", cmd.getTransactionInfo().getUniqueQualifier());
        final TransactionInfo info = cmd.getTransactionInfo();
        Assert.assertNotNull(info);
        Assert.assertNotNull(info.getTransactionId());
        Assert.assertTrue(info.getTransactionId().isLocalTransaction());
        final LocalTransactionId id = ((LocalTransactionId) (info.getTransactionId()));
        Assert.assertEquals("sessionId", id.getSessionId());
        Assert.assertEquals(99, id.getValue());
    }

    @Test
    public void testDecodeTransactionCommandWithTimeoutAndUniqueQualifier() {
        final IoBuffer buf = IoBuffer.wrap("transaction TX:sessionId:99 sessionId COMMIT_ONE_PHASE 3 unique-qualifier 100\r\n".getBytes());
        final TransactionCommand cmd = ((TransactionCommand) (this.decoder.decode(buf, null)));
        Assert.assertNotNull(cmd);
        Assert.assertEquals(100, ((int) (cmd.getOpaque())));
        Assert.assertEquals(3, cmd.getTransactionInfo().getTimeout());
        Assert.assertEquals("unique-qualifier", cmd.getTransactionInfo().getUniqueQualifier());
        final TransactionInfo info = cmd.getTransactionInfo();
        Assert.assertNotNull(info);
        Assert.assertNotNull(info.getTransactionId());
        Assert.assertTrue(info.getTransactionId().isLocalTransaction());
        final LocalTransactionId id = ((LocalTransactionId) (info.getTransactionId()));
        Assert.assertEquals("sessionId", id.getSessionId());
        Assert.assertEquals(99, id.getValue());
    }
}

