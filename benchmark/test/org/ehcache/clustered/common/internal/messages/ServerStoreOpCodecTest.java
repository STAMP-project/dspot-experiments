/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.clustered.common.internal.messages;


import EhcacheMessageType.APPEND;
import EhcacheMessageType.CLEAR;
import EhcacheMessageType.CLIENT_INVALIDATION_ACK;
import EhcacheMessageType.GET_AND_APPEND;
import EhcacheMessageType.GET_STORE;
import EhcacheMessageType.ITERATOR_ADVANCE;
import EhcacheMessageType.ITERATOR_CLOSE;
import EhcacheMessageType.ITERATOR_OPEN;
import EhcacheMessageType.LOCK;
import EhcacheMessageType.REPLACE;
import EhcacheMessageType.UNLOCK;
import ServerStoreOpMessage.AppendMessage;
import ServerStoreOpMessage.ClientInvalidationAck;
import ServerStoreOpMessage.GetAndAppendMessage;
import ServerStoreOpMessage.GetMessage;
import ServerStoreOpMessage.IteratorAdvanceMessage;
import ServerStoreOpMessage.IteratorCloseMessage;
import ServerStoreOpMessage.IteratorOpenMessage;
import ServerStoreOpMessage.LockMessage;
import ServerStoreOpMessage.ReplaceAtHeadMessage;
import ServerStoreOpMessage.UnlockMessage;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.ehcache.clustered.ChainUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static org.ehcache.clustered.Matchers.hasPayloads;


public class ServerStoreOpCodecTest {
    private static final ServerStoreOpCodec STORE_OP_CODEC = new ServerStoreOpCodec();

    @Test
    public void testAppendMessageCodec() {
        ServerStoreOpMessage.AppendMessage appendMessage = new ServerStoreOpMessage.AppendMessage(1L, ChainUtils.createPayload(1L));
        byte[] encoded = ServerStoreOpCodecTest.STORE_OP_CODEC.encode(appendMessage);
        EhcacheEntityMessage decodedMsg = ServerStoreOpCodecTest.STORE_OP_CODEC.decode(appendMessage.getMessageType(), ByteBuffer.wrap(encoded));
        ServerStoreOpMessage.AppendMessage decodedAppendMessage = ((ServerStoreOpMessage.AppendMessage) (decodedMsg));
        Assert.assertThat(decodedAppendMessage.getKey(), Matchers.is(1L));
        Assert.assertThat(ChainUtils.readPayload(decodedAppendMessage.getPayload()), Matchers.is(1L));
        Assert.assertThat(decodedAppendMessage.getMessageType(), Matchers.is(APPEND));
    }

    @Test
    public void testGetMessageCodec() {
        ServerStoreOpMessage getMessage = new ServerStoreOpMessage.GetMessage(2L);
        byte[] encoded = ServerStoreOpCodecTest.STORE_OP_CODEC.encode(getMessage);
        EhcacheEntityMessage decodedMsg = ServerStoreOpCodecTest.STORE_OP_CODEC.decode(getMessage.getMessageType(), ByteBuffer.wrap(encoded));
        ServerStoreOpMessage.GetMessage decodedGetMessage = ((ServerStoreOpMessage.GetMessage) (decodedMsg));
        Assert.assertThat(decodedGetMessage.getKey(), Matchers.is(2L));
        Assert.assertThat(decodedGetMessage.getMessageType(), Matchers.is(GET_STORE));
    }

    @Test
    public void testGetAndAppendMessageCodec() {
        ServerStoreOpMessage getAndAppendMessage = new ServerStoreOpMessage.GetAndAppendMessage(10L, ChainUtils.createPayload(10L));
        byte[] encoded = ServerStoreOpCodecTest.STORE_OP_CODEC.encode(getAndAppendMessage);
        EhcacheEntityMessage decodedMsg = ServerStoreOpCodecTest.STORE_OP_CODEC.decode(getAndAppendMessage.getMessageType(), ByteBuffer.wrap(encoded));
        ServerStoreOpMessage.GetAndAppendMessage decodedGetAndAppendMessage = ((ServerStoreOpMessage.GetAndAppendMessage) (decodedMsg));
        Assert.assertThat(decodedGetAndAppendMessage.getKey(), Matchers.is(10L));
        Assert.assertThat(ChainUtils.readPayload(decodedGetAndAppendMessage.getPayload()), Matchers.is(10L));
        Assert.assertThat(decodedGetAndAppendMessage.getMessageType(), Matchers.is(GET_AND_APPEND));
    }

    @Test
    public void testReplaceAtHeadMessageCodec() {
        ServerStoreOpMessage replaceAtHeadMessage = new ServerStoreOpMessage.ReplaceAtHeadMessage(10L, ChainUtils.sequencedChainOf(ChainUtils.createPayload(10L), ChainUtils.createPayload(100L), ChainUtils.createPayload(1000L)), ChainUtils.chainOf(ChainUtils.createPayload(2000L)));
        byte[] encoded = ServerStoreOpCodecTest.STORE_OP_CODEC.encode(replaceAtHeadMessage);
        EhcacheEntityMessage decodedMsg = ServerStoreOpCodecTest.STORE_OP_CODEC.decode(replaceAtHeadMessage.getMessageType(), ByteBuffer.wrap(encoded));
        ServerStoreOpMessage.ReplaceAtHeadMessage decodedReplaceAtHeadMessage = ((ServerStoreOpMessage.ReplaceAtHeadMessage) (decodedMsg));
        Assert.assertThat(decodedReplaceAtHeadMessage.getKey(), Matchers.is(10L));
        Assert.assertThat(decodedReplaceAtHeadMessage.getExpect(), org.ehcache.clustered.Matchers.hasPayloads(10L, 100L, 1000L));
        Assert.assertThat(decodedReplaceAtHeadMessage.getUpdate(), hasPayloads(2000L));
        Assert.assertThat(decodedReplaceAtHeadMessage.getMessageType(), Matchers.is(REPLACE));
    }

    @Test
    public void testClearMessageCodec() throws Exception {
        ServerStoreOpMessage clearMessage = new ServerStoreOpMessage.ClearMessage();
        byte[] encoded = ServerStoreOpCodecTest.STORE_OP_CODEC.encode(clearMessage);
        ServerStoreOpMessage decodedMsg = ((ServerStoreOpMessage) (ServerStoreOpCodecTest.STORE_OP_CODEC.decode(clearMessage.getMessageType(), ByteBuffer.wrap(encoded))));
        Assert.assertThat(decodedMsg.getMessageType(), Matchers.is(CLEAR));
    }

    @Test
    public void testClientInvalidationAckMessageCodec() throws Exception {
        ServerStoreOpMessage invalidationAckMessage = new ServerStoreOpMessage.ClientInvalidationAck(42L, 123);
        byte[] encoded = ServerStoreOpCodecTest.STORE_OP_CODEC.encode(invalidationAckMessage);
        EhcacheEntityMessage decodedMsg = ServerStoreOpCodecTest.STORE_OP_CODEC.decode(invalidationAckMessage.getMessageType(), ByteBuffer.wrap(encoded));
        ServerStoreOpMessage.ClientInvalidationAck decodedInvalidationAckMessage = ((ServerStoreOpMessage.ClientInvalidationAck) (decodedMsg));
        Assert.assertThat(decodedInvalidationAckMessage.getKey(), Matchers.is(42L));
        Assert.assertThat(decodedInvalidationAckMessage.getInvalidationId(), Matchers.is(123));
        Assert.assertThat(decodedInvalidationAckMessage.getMessageType(), Matchers.is(CLIENT_INVALIDATION_ACK));
    }

    @Test
    public void testLockMessage() throws Exception {
        ServerStoreOpMessage lockMessage = new ServerStoreOpMessage.LockMessage(2L);
        byte[] encoded = ServerStoreOpCodecTest.STORE_OP_CODEC.encode(lockMessage);
        EhcacheEntityMessage decoded = ServerStoreOpCodecTest.STORE_OP_CODEC.decode(lockMessage.getMessageType(), ByteBuffer.wrap(encoded));
        ServerStoreOpMessage.LockMessage decodedLockMessage = ((ServerStoreOpMessage.LockMessage) (decoded));
        Assert.assertThat(decodedLockMessage.getHash(), Matchers.is(2L));
        Assert.assertThat(decodedLockMessage.getMessageType(), Matchers.is(LOCK));
    }

    @Test
    public void testUnlockMessage() throws Exception {
        ServerStoreOpMessage unlockMessage = new ServerStoreOpMessage.UnlockMessage(2L);
        byte[] encoded = ServerStoreOpCodecTest.STORE_OP_CODEC.encode(unlockMessage);
        EhcacheEntityMessage decoded = ServerStoreOpCodecTest.STORE_OP_CODEC.decode(unlockMessage.getMessageType(), ByteBuffer.wrap(encoded));
        ServerStoreOpMessage.UnlockMessage decodedLockMessage = ((ServerStoreOpMessage.UnlockMessage) (decoded));
        Assert.assertThat(decodedLockMessage.getHash(), Matchers.is(2L));
        Assert.assertThat(decodedLockMessage.getMessageType(), Matchers.is(UNLOCK));
    }

    @Test
    public void testIteratorOpenMessage() {
        ServerStoreOpMessage iteratorOpenMessage = new ServerStoreOpMessage.IteratorOpenMessage(42);
        byte[] encoded = ServerStoreOpCodecTest.STORE_OP_CODEC.encode(iteratorOpenMessage);
        ServerStoreOpMessage.IteratorOpenMessage decoded = ((ServerStoreOpMessage.IteratorOpenMessage) (ServerStoreOpCodecTest.STORE_OP_CODEC.decode(iteratorOpenMessage.getMessageType(), ByteBuffer.wrap(encoded))));
        Assert.assertThat(decoded.getMessageType(), Matchers.is(ITERATOR_OPEN));
        Assert.assertThat(decoded.getBatchSize(), Matchers.is(42));
    }

    @Test
    public void testIteratorCloseMessage() {
        UUID uuid = UUID.randomUUID();
        ServerStoreOpMessage iteratorCloseMessage = new ServerStoreOpMessage.IteratorCloseMessage(uuid);
        byte[] encoded = ServerStoreOpCodecTest.STORE_OP_CODEC.encode(iteratorCloseMessage);
        ServerStoreOpMessage.IteratorCloseMessage decoded = ((ServerStoreOpMessage.IteratorCloseMessage) (ServerStoreOpCodecTest.STORE_OP_CODEC.decode(iteratorCloseMessage.getMessageType(), ByteBuffer.wrap(encoded))));
        Assert.assertThat(decoded.getMessageType(), Matchers.is(ITERATOR_CLOSE));
        Assert.assertThat(decoded.getIdentity(), Matchers.is(uuid));
    }

    @Test
    public void testIteratorAdvanceMessage() {
        UUID uuid = UUID.randomUUID();
        ServerStoreOpMessage iteratorAdvanceMessage = new ServerStoreOpMessage.IteratorAdvanceMessage(uuid, 42);
        byte[] encoded = ServerStoreOpCodecTest.STORE_OP_CODEC.encode(iteratorAdvanceMessage);
        ServerStoreOpMessage.IteratorAdvanceMessage decoded = ((ServerStoreOpMessage.IteratorAdvanceMessage) (ServerStoreOpCodecTest.STORE_OP_CODEC.decode(iteratorAdvanceMessage.getMessageType(), ByteBuffer.wrap(encoded))));
        Assert.assertThat(decoded.getMessageType(), Matchers.is(ITERATOR_ADVANCE));
        Assert.assertThat(decoded.getIdentity(), Matchers.is(uuid));
        Assert.assertThat(decoded.getBatchSize(), Matchers.is(42));
    }
}

