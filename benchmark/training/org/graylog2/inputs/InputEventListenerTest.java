/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.inputs;


import IOState.Type.RUNNING;
import IOState.Type.STOPPED;
import org.graylog2.database.NotFoundException;
import org.graylog2.plugin.IOState;
import org.graylog2.plugin.inputs.MessageInput;
import org.graylog2.plugin.system.NodeId;
import org.graylog2.rest.models.system.inputs.responses.InputCreated;
import org.graylog2.rest.models.system.inputs.responses.InputDeleted;
import org.graylog2.rest.models.system.inputs.responses.InputUpdated;
import org.graylog2.shared.inputs.InputLauncher;
import org.graylog2.shared.inputs.InputRegistry;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class InputEventListenerTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private InputLauncher inputLauncher;

    @Mock
    private InputRegistry inputRegistry;

    @Mock
    private InputService inputService;

    @Mock
    private NodeId nodeId;

    private InputEventListener listener;

    @Test
    public void inputCreatedDoesNothingIfInputDoesNotExist() throws Exception {
        final String inputId = "input-id";
        Mockito.when(inputService.find(inputId)).thenThrow(NotFoundException.class);
        listener.inputCreated(InputCreated.create(inputId));
        Mockito.verifyZeroInteractions(inputLauncher, inputRegistry, nodeId);
    }

    @Test
    public void inputCreatedStopsInputIfItIsRunning() throws Exception {
        final String inputId = "input-id";
        final Input input = Mockito.mock(Input.class);
        @SuppressWarnings("unchecked")
        final IOState<MessageInput> inputState = Mockito.mock(IOState.class);
        Mockito.when(inputService.find(inputId)).thenReturn(input);
        Mockito.when(inputRegistry.getInputState(inputId)).thenReturn(inputState);
        listener.inputCreated(InputCreated.create(inputId));
        Mockito.verify(inputRegistry, Mockito.times(1)).remove(inputState);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void inputCreatedDoesNotStopInputIfItIsNotRunning() throws Exception {
        final String inputId = "input-id";
        final Input input = Mockito.mock(Input.class);
        Mockito.when(inputService.find(inputId)).thenReturn(input);
        Mockito.when(inputRegistry.getInputState(inputId)).thenReturn(null);
        listener.inputCreated(InputCreated.create(inputId));
        Mockito.verify(inputRegistry, Mockito.never()).remove(ArgumentMatchers.any(IOState.class));
    }

    @Test
    public void inputCreatedStartsGlobalInputOnOtherNode() throws Exception {
        final String inputId = "input-id";
        final Input input = Mockito.mock(Input.class);
        Mockito.when(inputService.find(inputId)).thenReturn(input);
        Mockito.when(nodeId.toString()).thenReturn("node-id");
        Mockito.when(input.getNodeId()).thenReturn("other-node-id");
        Mockito.when(input.isGlobal()).thenReturn(true);
        final MessageInput messageInput = Mockito.mock(MessageInput.class);
        Mockito.when(inputService.getMessageInput(input)).thenReturn(messageInput);
        listener.inputCreated(InputCreated.create(inputId));
        Mockito.verify(inputLauncher, Mockito.times(1)).launch(messageInput);
    }

    @Test
    public void inputCreatedDoesNotStartLocalInputOnAnyNode() throws Exception {
        final String inputId = "input-id";
        final Input input = Mockito.mock(Input.class);
        Mockito.when(inputService.find(inputId)).thenReturn(input);
        Mockito.when(nodeId.toString()).thenReturn("node-id");
        Mockito.when(input.getNodeId()).thenReturn("other-node-id");
        Mockito.when(input.isGlobal()).thenReturn(false);
        final MessageInput messageInput = Mockito.mock(MessageInput.class);
        Mockito.when(inputService.getMessageInput(input)).thenReturn(messageInput);
        listener.inputCreated(InputCreated.create(inputId));
        Mockito.verify(inputLauncher, Mockito.never()).launch(messageInput);
    }

    @Test
    public void inputCreatedStartsLocalInputOnLocalNode() throws Exception {
        final String inputId = "input-id";
        final Input input = Mockito.mock(Input.class);
        Mockito.when(inputService.find(inputId)).thenReturn(input);
        Mockito.when(nodeId.toString()).thenReturn("node-id");
        Mockito.when(input.getNodeId()).thenReturn("node-id");
        Mockito.when(input.isGlobal()).thenReturn(false);
        final MessageInput messageInput = Mockito.mock(MessageInput.class);
        Mockito.when(inputService.getMessageInput(input)).thenReturn(messageInput);
        listener.inputCreated(InputCreated.create(inputId));
        Mockito.verify(inputLauncher, Mockito.times(1)).launch(messageInput);
    }

    @Test
    public void inputUpdatedDoesNothingIfInputDoesNotExist() throws Exception {
        final String inputId = "input-id";
        Mockito.when(inputService.find(inputId)).thenThrow(NotFoundException.class);
        listener.inputUpdated(InputUpdated.create(inputId));
        Mockito.verifyZeroInteractions(inputLauncher, inputRegistry, nodeId);
    }

    @Test
    public void inputUpdatedStopsInputIfItIsRunning() throws Exception {
        final String inputId = "input-id";
        final Input input = Mockito.mock(Input.class);
        @SuppressWarnings("unchecked")
        final IOState<MessageInput> inputState = Mockito.mock(IOState.class);
        Mockito.when(inputService.find(inputId)).thenReturn(input);
        Mockito.when(inputRegistry.getInputState(inputId)).thenReturn(inputState);
        listener.inputUpdated(InputUpdated.create(inputId));
        Mockito.verify(inputRegistry, Mockito.times(1)).remove(inputState);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void inputUpdatedDoesNotStopInputIfItIsNotRunning() throws Exception {
        final String inputId = "input-id";
        final Input input = Mockito.mock(Input.class);
        Mockito.when(inputService.find(inputId)).thenReturn(input);
        Mockito.when(inputRegistry.getInputState(inputId)).thenReturn(null);
        listener.inputUpdated(InputUpdated.create(inputId));
        Mockito.verify(inputRegistry, Mockito.never()).remove(ArgumentMatchers.any(IOState.class));
    }

    @Test
    public void inputUpdatedRestartsGlobalInputOnAnyNode() throws Exception {
        final String inputId = "input-id";
        final Input input = Mockito.mock(Input.class);
        @SuppressWarnings("unchecked")
        final IOState<MessageInput> inputState = Mockito.mock(IOState.class);
        Mockito.when(inputState.getState()).thenReturn(RUNNING);
        Mockito.when(inputService.find(inputId)).thenReturn(input);
        Mockito.when(inputRegistry.getInputState(inputId)).thenReturn(inputState);
        Mockito.when(nodeId.toString()).thenReturn("node-id");
        Mockito.when(input.getNodeId()).thenReturn("other-node-id");
        Mockito.when(input.isGlobal()).thenReturn(true);
        final MessageInput messageInput = Mockito.mock(MessageInput.class);
        Mockito.when(inputService.getMessageInput(input)).thenReturn(messageInput);
        listener.inputUpdated(InputUpdated.create(inputId));
        Mockito.verify(inputLauncher, Mockito.times(1)).launch(messageInput);
    }

    @Test
    public void inputUpdatedDoesNotStartLocalInputOnOtherNode() throws Exception {
        final String inputId = "input-id";
        final Input input = Mockito.mock(Input.class);
        @SuppressWarnings("unchecked")
        final IOState<MessageInput> inputState = Mockito.mock(IOState.class);
        Mockito.when(inputState.getState()).thenReturn(RUNNING);
        Mockito.when(inputService.find(inputId)).thenReturn(input);
        Mockito.when(nodeId.toString()).thenReturn("node-id");
        Mockito.when(input.getNodeId()).thenReturn("other-node-id");
        Mockito.when(input.isGlobal()).thenReturn(false);
        final MessageInput messageInput = Mockito.mock(MessageInput.class);
        Mockito.when(inputService.getMessageInput(input)).thenReturn(messageInput);
        listener.inputUpdated(InputUpdated.create(inputId));
        Mockito.verify(inputLauncher, Mockito.never()).launch(messageInput);
    }

    @Test
    public void inputUpdatedRestartsLocalInputOnLocalNode() throws Exception {
        final String inputId = "input-id";
        final Input input = Mockito.mock(Input.class);
        @SuppressWarnings("unchecked")
        final IOState<MessageInput> inputState = Mockito.mock(IOState.class);
        Mockito.when(inputState.getState()).thenReturn(RUNNING);
        Mockito.when(inputService.find(inputId)).thenReturn(input);
        Mockito.when(inputRegistry.getInputState(inputId)).thenReturn(inputState);
        Mockito.when(nodeId.toString()).thenReturn("node-id");
        Mockito.when(input.getNodeId()).thenReturn("node-id");
        Mockito.when(input.isGlobal()).thenReturn(false);
        final MessageInput messageInput = Mockito.mock(MessageInput.class);
        Mockito.when(inputService.getMessageInput(input)).thenReturn(messageInput);
        listener.inputUpdated(InputUpdated.create(inputId));
        Mockito.verify(inputLauncher, Mockito.times(1)).launch(messageInput);
    }

    @Test
    public void inputUpdatedDoesNotStartLocalInputOnLocalNodeIfItWasNotRunning() throws Exception {
        final String inputId = "input-id";
        final Input input = Mockito.mock(Input.class);
        @SuppressWarnings("unchecked")
        final IOState<MessageInput> inputState = Mockito.mock(IOState.class);
        Mockito.when(inputState.getState()).thenReturn(STOPPED);
        Mockito.when(inputService.find(inputId)).thenReturn(input);
        Mockito.when(inputRegistry.getInputState(inputId)).thenReturn(inputState);
        Mockito.when(nodeId.toString()).thenReturn("node-id");
        Mockito.when(input.getNodeId()).thenReturn("node-id");
        Mockito.when(input.isGlobal()).thenReturn(false);
        final MessageInput messageInput = Mockito.mock(MessageInput.class);
        Mockito.when(inputService.getMessageInput(input)).thenReturn(messageInput);
        listener.inputUpdated(InputUpdated.create(inputId));
        Mockito.verify(inputLauncher, Mockito.never()).launch(messageInput);
    }

    @Test
    public void inputDeletedStopsInputIfItIsRunning() throws Exception {
        final String inputId = "input-id";
        @SuppressWarnings("unchecked")
        final IOState<MessageInput> inputState = Mockito.mock(IOState.class);
        Mockito.when(inputState.getState()).thenReturn(RUNNING);
        Mockito.when(inputRegistry.getInputState(inputId)).thenReturn(inputState);
        listener.inputDeleted(InputDeleted.create(inputId));
        Mockito.verify(inputRegistry, Mockito.never()).remove(ArgumentMatchers.any(MessageInput.class));
    }

    @Test
    public void inputDeletedDoesNothingIfInputIsNotRunning() throws Exception {
        final String inputId = "input-id";
        @SuppressWarnings("unchecked")
        final IOState<MessageInput> inputState = Mockito.mock(IOState.class);
        Mockito.when(inputState.getState()).thenReturn(null);
        Mockito.when(inputRegistry.getInputState(inputId)).thenReturn(inputState);
        listener.inputDeleted(InputDeleted.create(inputId));
        Mockito.verify(inputRegistry, Mockito.never()).remove(ArgumentMatchers.any(MessageInput.class));
    }
}

