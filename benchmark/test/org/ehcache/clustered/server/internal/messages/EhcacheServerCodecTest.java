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


import EhcacheCodec.OP_CODE_DECODER;
import EhcacheMessageType.LIFECYCLE_MESSAGES;
import EhcacheMessageType.PASSIVE_REPLICATION_MESSAGES;
import EhcacheMessageType.STATE_REPO_OPERATION_MESSAGES;
import EhcacheMessageType.STORE_OPERATION_MESSAGES;
import java.nio.ByteBuffer;
import org.ehcache.clustered.common.internal.messages.EhcacheCodec;
import org.ehcache.clustered.common.internal.messages.EhcacheEntityMessage;
import org.ehcache.clustered.common.internal.messages.EhcacheMessageType;
import org.ehcache.clustered.common.internal.messages.LifecycleMessage;
import org.ehcache.clustered.server.internal.messages.PassiveReplicationMessage.InvalidationCompleteMessage;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * EhcacheServerCodecTest
 */
public class EhcacheServerCodecTest {
    @Mock
    private EhcacheCodec clientCodec;

    @Mock
    private PassiveReplicationMessageCodec replicationCodec;

    private EhcacheServerCodec serverCodec;

    @Test
    public void testDelegatesToEhcacheCodeForEncoding() throws Exception {
        LifecycleMessage lifecycleMessage = new LifecycleMessage() {
            private static final long serialVersionUID = 1L;

            @Override
            public EhcacheMessageType getMessageType() {
                return EhcacheMessageType.APPEND;
            }
        };
        serverCodec.encodeMessage(lifecycleMessage);
        Mockito.verify(clientCodec).encodeMessage(ArgumentMatchers.any(EhcacheEntityMessage.class));
        Mockito.verifyZeroInteractions(replicationCodec);
    }

    @Test
    public void testDelegatesToPassiveReplicationCodeForEncoding() throws Exception {
        InvalidationCompleteMessage message = new InvalidationCompleteMessage(1000L);
        serverCodec.encodeMessage(message);
        Mockito.verify(replicationCodec).encode(message);
        Mockito.verifyZeroInteractions(clientCodec);
    }

    @Test
    public void decodeLifeCycleMessages() throws Exception {
        for (EhcacheMessageType messageType : EhcacheMessageType.LIFECYCLE_MESSAGES) {
            ByteBuffer encodedBuffer = OP_CODE_DECODER.encoder().enm("opCode", messageType).encode();
            serverCodec.decodeMessage(encodedBuffer.array());
        }
        Mockito.verify(clientCodec, Mockito.times(LIFECYCLE_MESSAGES.size())).decodeMessage(ArgumentMatchers.any(ByteBuffer.class), ArgumentMatchers.any(EhcacheMessageType.class));
        Mockito.verifyZeroInteractions(replicationCodec);
    }

    @Test
    public void decodeServerStoreMessages() throws Exception {
        for (EhcacheMessageType messageType : EhcacheMessageType.STORE_OPERATION_MESSAGES) {
            ByteBuffer encodedBuffer = OP_CODE_DECODER.encoder().enm("opCode", messageType).encode();
            serverCodec.decodeMessage(encodedBuffer.array());
        }
        Mockito.verify(clientCodec, Mockito.times(STORE_OPERATION_MESSAGES.size())).decodeMessage(ArgumentMatchers.any(ByteBuffer.class), ArgumentMatchers.any(EhcacheMessageType.class));
        Mockito.verifyZeroInteractions(replicationCodec);
    }

    @Test
    public void decodeStateRepoMessages() throws Exception {
        for (EhcacheMessageType messageType : EhcacheMessageType.STATE_REPO_OPERATION_MESSAGES) {
            ByteBuffer encodedBuffer = OP_CODE_DECODER.encoder().enm("opCode", messageType).encode();
            serverCodec.decodeMessage(encodedBuffer.array());
        }
        Mockito.verify(clientCodec, Mockito.times(STATE_REPO_OPERATION_MESSAGES.size())).decodeMessage(ArgumentMatchers.any(ByteBuffer.class), ArgumentMatchers.any(EhcacheMessageType.class));
        Mockito.verifyZeroInteractions(replicationCodec);
    }

    @Test
    public void decodeClientIDTrackerMessages() throws Exception {
        for (EhcacheMessageType messageType : EhcacheMessageType.PASSIVE_REPLICATION_MESSAGES) {
            ByteBuffer encodedBuffer = OP_CODE_DECODER.encoder().enm("opCode", messageType).encode();
            serverCodec.decodeMessage(encodedBuffer.array());
        }
        Mockito.verify(replicationCodec, Mockito.times(PASSIVE_REPLICATION_MESSAGES.size())).decode(ArgumentMatchers.any(EhcacheMessageType.class), ArgumentMatchers.any(ByteBuffer.class));
        Mockito.verifyZeroInteractions(clientCodec);
    }
}

