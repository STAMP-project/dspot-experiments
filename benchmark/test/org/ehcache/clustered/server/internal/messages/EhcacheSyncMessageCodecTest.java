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


import java.util.HashMap;
import java.util.Map;
import org.ehcache.clustered.ChainUtils;
import org.ehcache.clustered.Matchers;
import org.ehcache.clustered.common.internal.messages.EhcacheEntityResponse;
import org.ehcache.clustered.common.internal.messages.ResponseCodec;
import org.ehcache.clustered.common.internal.store.Chain;
import org.ehcache.clustered.server.TestClientSourceId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class EhcacheSyncMessageCodecTest {
    private ResponseCodec responseCodec = Mockito.mock(ResponseCodec.class);

    private EhcacheSyncMessageCodec codec = new EhcacheSyncMessageCodec(responseCodec);

    @Test
    public void testDataSyncMessageEncodeDecode() throws Exception {
        Map<Long, Chain> chainMap = new HashMap<>();
        Chain chain = ChainUtils.sequencedChainOf(ChainUtils.createPayload(10L), ChainUtils.createPayload(100L), ChainUtils.createPayload(1000L));
        chainMap.put(1L, chain);
        chainMap.put(2L, chain);
        chainMap.put(3L, chain);
        EhcacheDataSyncMessage message = new EhcacheDataSyncMessage(chainMap);
        byte[] encodedMessage = codec.encode(0, message);
        EhcacheDataSyncMessage decoded = ((EhcacheDataSyncMessage) (codec.decode(0, encodedMessage)));
        Map<Long, Chain> decodedChainMap = decoded.getChainMap();
        assertThat(decodedChainMap).hasSize(3);
        Assert.assertThat(decodedChainMap.get(1L), Matchers.matchesChain(chain));
        Assert.assertThat(decodedChainMap.get(2L), Matchers.matchesChain(chain));
        Assert.assertThat(decodedChainMap.get(3L), Matchers.matchesChain(chain));
    }

    @Test
    public void testMessageTrackerSyncEncodeDecode_emptyMessage() throws Exception {
        EhcacheMessageTrackerMessage message = new EhcacheMessageTrackerMessage(1, new HashMap());
        byte[] encodedMessage = codec.encode(0, message);
        EhcacheMessageTrackerMessage decoded = ((EhcacheMessageTrackerMessage) (codec.decode(0, encodedMessage)));
        assertThat(decoded.getTrackedMessages()).isEmpty();
    }

    @Test
    public void testMessageTrackerSyncEncodeDecode_clientWithoutMessage() throws Exception {
        HashMap<Long, Map<Long, EhcacheEntityResponse>> trackerMap = new HashMap<>();
        trackerMap.put(1L, new HashMap());
        EhcacheMessageTrackerMessage message = new EhcacheMessageTrackerMessage(1, trackerMap);
        byte[] encodedMessage = codec.encode(0, message);
        EhcacheMessageTrackerMessage decoded = ((EhcacheMessageTrackerMessage) (codec.decode(0, encodedMessage)));
        assertThat(decoded.getTrackedMessages()).isEmpty();
    }

    @Test
    public void testMessageTrackerSyncEncodeDecode_messages() throws Exception {
        TestClientSourceId id1 = new TestClientSourceId(1);
        TestClientSourceId id2 = new TestClientSourceId(2);
        EhcacheEntityResponse r3 = new EhcacheMessageTrackerMessageTest.NullResponse();
        EhcacheEntityResponse r4 = new EhcacheMessageTrackerMessageTest.NullResponse();
        EhcacheEntityResponse r5 = new EhcacheMessageTrackerMessageTest.NullResponse();
        Mockito.when(responseCodec.encode(r3)).thenReturn(new byte[3]);
        Mockito.when(responseCodec.encode(r4)).thenReturn(new byte[4]);
        Mockito.when(responseCodec.encode(r5)).thenReturn(new byte[5]);
        Mockito.when(responseCodec.decode(ArgumentMatchers.argThat(( a) -> (a != null) && (a.length == 3)))).thenReturn(r3);
        Mockito.when(responseCodec.decode(ArgumentMatchers.argThat(( a) -> (a != null) && (a.length == 4)))).thenReturn(r4);
        Mockito.when(responseCodec.decode(ArgumentMatchers.argThat(( a) -> (a != null) && (a.length == 5)))).thenReturn(r5);
        HashMap<Long, Map<Long, EhcacheEntityResponse>> trackerMap = new HashMap<>();
        Map<Long, EhcacheEntityResponse> responses1 = new HashMap<>();
        responses1.put(3L, r3);
        responses1.put(4L, r4);
        trackerMap.put(1L, responses1);
        Map<Long, EhcacheEntityResponse> responses2 = new HashMap<>();
        responses2.put(5L, r5);
        trackerMap.put(2L, responses2);
        EhcacheMessageTrackerMessage message = new EhcacheMessageTrackerMessage(1, trackerMap);
        byte[] encodedMessage = codec.encode(0, message);
        EhcacheMessageTrackerMessage decoded = ((EhcacheMessageTrackerMessage) (codec.decode(0, encodedMessage)));
        Map<Long, Map<Long, EhcacheEntityResponse>> trackedMessages = decoded.getTrackedMessages();
        assertThat(trackedMessages).containsKeys(id1.toLong(), id2.toLong());
        assertThat(trackedMessages.get(id1.toLong())).containsEntry(3L, r3);
        assertThat(trackedMessages.get(id1.toLong())).containsEntry(4L, r4);
        assertThat(trackedMessages.get(id2.toLong())).containsEntry(5L, r5);
    }
}

