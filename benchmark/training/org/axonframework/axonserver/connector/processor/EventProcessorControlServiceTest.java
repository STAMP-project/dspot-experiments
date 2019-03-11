/**
 * Copyright (c) 2010-2019. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.axonserver.connector.processor;


import java.util.function.Consumer;
import org.axonframework.axonserver.connector.AxonServerConnectionManager;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class EventProcessorControlServiceTest {
    private final AxonServerConnectionManager axonServerConnectionManager = Mockito.mock(AxonServerConnectionManager.class);

    private final EventProcessorController eventProcessorController = Mockito.mock(EventProcessorController.class);

    private final EventProcessorControlService testSubject = new EventProcessorControlService(axonServerConnectionManager, eventProcessorController);

    @SuppressWarnings("unchecked")
    @Test
    public void testStartAddOutboundInstructionToTheAxonServerConnectionManager() {
        testSubject.start();
        Mockito.verify(axonServerConnectionManager).onOutboundInstruction(ArgumentMatchers.eq(PAUSE_EVENT_PROCESSOR), ArgumentMatchers.any(Consumer.class));
        Mockito.verify(axonServerConnectionManager).onOutboundInstruction(ArgumentMatchers.eq(START_EVENT_PROCESSOR), ArgumentMatchers.any(Consumer.class));
        Mockito.verify(axonServerConnectionManager).onOutboundInstruction(ArgumentMatchers.eq(RELEASE_SEGMENT), ArgumentMatchers.any(Consumer.class));
        Mockito.verify(axonServerConnectionManager).onOutboundInstruction(ArgumentMatchers.eq(REQUEST_EVENT_PROCESSOR_INFO), ArgumentMatchers.any(Consumer.class));
        Mockito.verify(axonServerConnectionManager).onOutboundInstruction(ArgumentMatchers.eq(SPLIT_EVENT_PROCESSOR_SEGMENT), ArgumentMatchers.any(Consumer.class));
        Mockito.verify(axonServerConnectionManager).onOutboundInstruction(ArgumentMatchers.eq(MERGE_EVENT_PROCESSOR_SEGMENT), ArgumentMatchers.any(Consumer.class));
        Mockito.verifyNoMoreInteractions(axonServerConnectionManager);
    }
}

