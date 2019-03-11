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
package org.graylog2.plugin.inputs;


import IOState.Type;
import com.google.common.eventbus.EventBus;
import org.graylog2.plugin.IOState;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class IOStateTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testNotEqualIfDifferentInput() throws Exception {
        EventBus eventBus = Mockito.mock(EventBus.class);
        MessageInput messageInput1 = Mockito.mock(MessageInput.class);
        MessageInput messageInput2 = Mockito.mock(MessageInput.class);
        IOState<MessageInput> inputState1 = new IOState(eventBus, messageInput1);
        IOState<MessageInput> inputState2 = new IOState(eventBus, messageInput2);
        Assert.assertFalse(inputState1.equals(inputState2));
        Assert.assertFalse(inputState2.equals(inputState1));
    }

    @Test
    public void testEqualsSameState() throws Exception {
        EventBus eventBus = Mockito.mock(EventBus.class);
        MessageInput messageInput = Mockito.mock(MessageInput.class);
        IOState<MessageInput> inputState1 = new IOState(eventBus, messageInput, Type.RUNNING);
        IOState<MessageInput> inputState2 = new IOState(eventBus, messageInput, Type.RUNNING);
        Assert.assertTrue(inputState1.equals(inputState2));
        Assert.assertTrue(inputState2.equals(inputState1));
    }

    @Test
    public void testNotEqualIfDifferentState() throws Exception {
        EventBus eventBus = Mockito.mock(EventBus.class);
        MessageInput messageInput = Mockito.mock(MessageInput.class);
        IOState<MessageInput> inputState1 = new IOState(eventBus, messageInput, Type.RUNNING);
        IOState<MessageInput> inputState2 = new IOState(eventBus, messageInput, Type.STOPPED);
        Assert.assertTrue(inputState1.equals(inputState2));
        Assert.assertTrue(inputState2.equals(inputState1));
    }
}

