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
package org.ehcache.clustered.server.internal.messages;


import EhcacheMessageType.CHAIN_REPLICATION_OP;
import EhcacheMessageType.CLEAR_INVALIDATION_COMPLETE;
import EhcacheMessageType.INVALIDATION_COMPLETE;
import java.nio.ByteBuffer;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.common.internal.store.Chain;
import org.ehcache.clustered.server.internal.messages.PassiveReplicationMessage.ChainReplicationMessage;
import org.ehcache.clustered.server.internal.messages.PassiveReplicationMessage.ClearInvalidationCompleteMessage;
import org.ehcache.clustered.server.internal.messages.PassiveReplicationMessage.InvalidationCompleteMessage;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static org.ehcache.clustered.Matchers.matchesChain;


public class PassiveReplicationMessageCodecTest {
    private PassiveReplicationMessageCodec codec = new PassiveReplicationMessageCodec();

    @Test
    public void testChainReplicationMessageCodec() {
        Chain chain = ChainUtils.chainOf(ChainUtils.createPayload(2L), ChainUtils.createPayload(20L));
        ChainReplicationMessage chainReplicationMessage = new ChainReplicationMessage(2L, chain, 200L, 100L, 1L);
        byte[] encoded = codec.encode(chainReplicationMessage);
        ChainReplicationMessage decodedMsg = ((ChainReplicationMessage) (codec.decode(CHAIN_REPLICATION_OP, ByteBuffer.wrap(encoded))));
        Assert.assertThat(decodedMsg.getClientId(), Matchers.is(chainReplicationMessage.getClientId()));
        Assert.assertThat(decodedMsg.getTransactionId(), Matchers.is(chainReplicationMessage.getTransactionId()));
        Assert.assertThat(decodedMsg.getOldestTransactionId(), Matchers.is(chainReplicationMessage.getOldestTransactionId()));
        Assert.assertThat(decodedMsg.getKey(), Matchers.is(chainReplicationMessage.getKey()));
        Assert.assertThat(decodedMsg.getChain(), matchesChain(chainReplicationMessage.getChain()));
    }

    @Test
    public void testClearInvalidationCompleteMessage() {
        ClearInvalidationCompleteMessage clearInvalidationCompleteMessage = new ClearInvalidationCompleteMessage();
        byte[] encoded = codec.encode(clearInvalidationCompleteMessage);
        ClearInvalidationCompleteMessage decoded = ((ClearInvalidationCompleteMessage) (codec.decode(CLEAR_INVALIDATION_COMPLETE, ByteBuffer.wrap(encoded))));
        Assert.assertThat(decoded.getMessageType(), Matchers.is(CLEAR_INVALIDATION_COMPLETE));
    }

    @Test
    public void testInvalidationCompleteMessage() {
        InvalidationCompleteMessage invalidationCompleteMessage = new InvalidationCompleteMessage(20L);
        byte[] encoded = codec.encode(invalidationCompleteMessage);
        InvalidationCompleteMessage decoded = ((InvalidationCompleteMessage) (codec.decode(INVALIDATION_COMPLETE, ByteBuffer.wrap(encoded))));
        Assert.assertThat(decoded.getMessageType(), Matchers.is(INVALIDATION_COMPLETE));
        Assert.assertThat(decoded.getKey(), Matchers.equalTo(invalidationCompleteMessage.getKey()));
    }
}

