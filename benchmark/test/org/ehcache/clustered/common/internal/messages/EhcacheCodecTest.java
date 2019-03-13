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


import EhcacheCodec.OP_CODE_DECODER;
import EhcacheMessageType.LIFECYCLE_MESSAGES;
import EhcacheMessageType.STATE_REPO_OPERATION_MESSAGES;
import EhcacheMessageType.STORE_OPERATION_MESSAGES;
import LifecycleMessage.ValidateServerStore;
import ServerStoreOpMessage.ClearMessage;
import StateRepositoryOpMessage.EntrySetMessage;
import java.nio.ByteBuffer;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static EhcacheMessageType.LIFECYCLE_MESSAGES;
import static EhcacheMessageType.STATE_REPO_OPERATION_MESSAGES;
import static EhcacheMessageType.STORE_OPERATION_MESSAGES;


public class EhcacheCodecTest {
    @Mock
    private ServerStoreOpCodec serverStoreOpCodec;

    @Mock
    private LifeCycleMessageCodec lifeCycleMessageCodec;

    @Mock
    private StateRepositoryOpCodec stateRepositoryOpCodec;

    private EhcacheCodec codec;

    @Test
    public void encodeMessage() throws Exception {
        LifecycleMessage.ValidateServerStore lifecycleMessage = new LifecycleMessage.ValidateServerStore("foo", null);
        codec.encodeMessage(lifecycleMessage);
        Mockito.verify(lifeCycleMessageCodec, Mockito.only()).encode(ArgumentMatchers.any(LifecycleMessage.class));
        Mockito.verify(serverStoreOpCodec, Mockito.never()).encode(ArgumentMatchers.any(ServerStoreOpMessage.class));
        Mockito.verify(stateRepositoryOpCodec, Mockito.never()).encode(ArgumentMatchers.any(StateRepositoryOpMessage.class));
        ServerStoreOpMessage.ClearMessage serverStoreOpMessage = new ServerStoreOpMessage.ClearMessage();
        codec.encodeMessage(serverStoreOpMessage);
        Mockito.verify(lifeCycleMessageCodec, Mockito.only()).encode(ArgumentMatchers.any(LifecycleMessage.class));
        Mockito.verify(serverStoreOpCodec, Mockito.only()).encode(ArgumentMatchers.any(ServerStoreOpMessage.class));
        Mockito.verify(stateRepositoryOpCodec, Mockito.never()).encode(ArgumentMatchers.any(StateRepositoryOpMessage.class));
        StateRepositoryOpMessage.EntrySetMessage stateRepositoryOpMessage = new StateRepositoryOpMessage.EntrySetMessage("foo", "bar");
        codec.encodeMessage(stateRepositoryOpMessage);
        Mockito.verify(lifeCycleMessageCodec, Mockito.only()).encode(ArgumentMatchers.any(LifecycleMessage.class));
        Mockito.verify(serverStoreOpCodec, Mockito.only()).encode(ArgumentMatchers.any(ServerStoreOpMessage.class));
        Mockito.verify(stateRepositoryOpCodec, Mockito.only()).encode(ArgumentMatchers.any(StateRepositoryOpMessage.class));
    }

    @Test
    public void decodeLifeCycleMessages() throws Exception {
        for (EhcacheMessageType messageType : LIFECYCLE_MESSAGES) {
            ByteBuffer encodedBuffer = OP_CODE_DECODER.encoder().enm("opCode", messageType).encode();
            codec.decodeMessage(encodedBuffer.array());
        }
        Mockito.verify(lifeCycleMessageCodec, Mockito.times(LIFECYCLE_MESSAGES.size())).decode(ArgumentMatchers.any(EhcacheMessageType.class), ArgumentMatchers.any(ByteBuffer.class));
        Mockito.verifyZeroInteractions(serverStoreOpCodec, stateRepositoryOpCodec);
    }

    @Test
    public void decodeServerStoreMessages() throws Exception {
        for (EhcacheMessageType messageType : STORE_OPERATION_MESSAGES) {
            ByteBuffer encodedBuffer = OP_CODE_DECODER.encoder().enm("opCode", messageType).encode();
            codec.decodeMessage(encodedBuffer.array());
        }
        Mockito.verify(serverStoreOpCodec, Mockito.times(STORE_OPERATION_MESSAGES.size())).decode(ArgumentMatchers.any(EhcacheMessageType.class), ArgumentMatchers.any(ByteBuffer.class));
        Mockito.verifyZeroInteractions(lifeCycleMessageCodec, stateRepositoryOpCodec);
    }

    @Test
    public void decodeStateRepoMessages() throws Exception {
        for (EhcacheMessageType messageType : STATE_REPO_OPERATION_MESSAGES) {
            ByteBuffer encodedBuffer = OP_CODE_DECODER.encoder().enm("opCode", messageType).encode();
            codec.decodeMessage(encodedBuffer.array());
        }
        Mockito.verify(stateRepositoryOpCodec, Mockito.times(STATE_REPO_OPERATION_MESSAGES.size())).decode(ArgumentMatchers.any(EhcacheMessageType.class), ArgumentMatchers.any(ByteBuffer.class));
        Mockito.verifyZeroInteractions(lifeCycleMessageCodec, serverStoreOpCodec);
    }
}

