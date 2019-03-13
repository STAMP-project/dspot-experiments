/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agrona.concurrent;


import DynamicCompositeAgent.Status.CLOSED;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DynamicCompositeAgentTest {
    private static final String ROLE_NAME = "roleName";

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowAddAfterClose() {
        final DynamicCompositeAgent compositeAgent = new DynamicCompositeAgent(DynamicCompositeAgentTest.ROLE_NAME);
        final AgentInvoker invoker = new AgentInvoker(Throwable::printStackTrace, null, compositeAgent);
        invoker.close();
        compositeAgent.tryAdd(Mockito.mock(Agent.class));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowRemoveAfterClose() {
        final DynamicCompositeAgent compositeAgent = new DynamicCompositeAgent(DynamicCompositeAgentTest.ROLE_NAME);
        final AgentInvoker invoker = new AgentInvoker(Throwable::printStackTrace, null, compositeAgent);
        invoker.close();
        compositeAgent.tryRemove(Mockito.mock(Agent.class));
    }

    @Test
    public void shouldAddAgent() throws Exception {
        final Agent mockAgentOne = Mockito.mock(Agent.class);
        final DynamicCompositeAgent compositeAgent = new DynamicCompositeAgent(DynamicCompositeAgentTest.ROLE_NAME, mockAgentOne);
        final AgentInvoker invoker = new AgentInvoker(Throwable::printStackTrace, null, compositeAgent);
        Assert.assertThat(compositeAgent.roleName(), CoreMatchers.is(DynamicCompositeAgentTest.ROLE_NAME));
        invoker.start();
        Mockito.verify(mockAgentOne, Mockito.times(1)).onStart();
        invoker.invoke();
        Mockito.verify(mockAgentOne, Mockito.times(1)).onStart();
        Mockito.verify(mockAgentOne, Mockito.times(1)).doWork();
        final Agent mockAgentTwo = Mockito.mock(Agent.class);
        Assert.assertTrue(compositeAgent.tryAdd(mockAgentTwo));
        Assert.assertFalse(compositeAgent.hasAddAgentCompleted());
        invoker.invoke();
        Assert.assertTrue(compositeAgent.hasAddAgentCompleted());
        Mockito.verify(mockAgentOne, Mockito.times(1)).onStart();
        Mockito.verify(mockAgentOne, Mockito.times(2)).doWork();
        Mockito.verify(mockAgentTwo, Mockito.times(1)).onStart();
        Mockito.verify(mockAgentTwo, Mockito.times(1)).doWork();
    }

    @Test
    public void shouldRemoveAgent() throws Exception {
        final Agent mockAgentOne = Mockito.mock(Agent.class);
        final Agent mockAgentTwo = Mockito.mock(Agent.class);
        final DynamicCompositeAgent compositeAgent = new DynamicCompositeAgent(DynamicCompositeAgentTest.ROLE_NAME, mockAgentOne, mockAgentTwo);
        final AgentInvoker invoker = new AgentInvoker(Throwable::printStackTrace, null, compositeAgent);
        invoker.start();
        Mockito.verify(mockAgentOne, Mockito.times(1)).onStart();
        Mockito.verify(mockAgentTwo, Mockito.times(1)).onStart();
        invoker.invoke();
        Mockito.verify(mockAgentOne, Mockito.times(1)).doWork();
        Mockito.verify(mockAgentTwo, Mockito.times(1)).doWork();
        Assert.assertTrue(compositeAgent.tryRemove(mockAgentTwo));
        Assert.assertFalse(compositeAgent.hasRemoveAgentCompleted());
        invoker.invoke();
        Assert.assertTrue(compositeAgent.hasRemoveAgentCompleted());
        Mockito.verify(mockAgentOne, Mockito.times(2)).doWork();
        Mockito.verify(mockAgentTwo, Mockito.times(1)).doWork();
        Mockito.verify(mockAgentOne, Mockito.times(1)).onStart();
        Mockito.verify(mockAgentTwo, Mockito.times(1)).onStart();
        Mockito.verify(mockAgentTwo, Mockito.times(1)).onClose();
    }

    @Test
    public void shouldCloseAgents() throws Exception {
        final Agent mockAgentOne = Mockito.mock(Agent.class);
        final Agent mockAgentTwo = Mockito.mock(Agent.class);
        final DynamicCompositeAgent compositeAgent = new DynamicCompositeAgent(DynamicCompositeAgentTest.ROLE_NAME, mockAgentOne, mockAgentTwo);
        compositeAgent.onClose();
        Mockito.verify(mockAgentOne, Mockito.never()).doWork();
        Mockito.verify(mockAgentTwo, Mockito.never()).doWork();
        Mockito.verify(mockAgentOne, Mockito.never()).onStart();
        Mockito.verify(mockAgentTwo, Mockito.never()).onStart();
        Mockito.verify(mockAgentOne, Mockito.times(1)).onClose();
        Mockito.verify(mockAgentTwo, Mockito.times(1)).onClose();
        Assert.assertEquals(CLOSED, compositeAgent.status());
    }

    @Test
    public void shouldDetectConcurrentAdd() {
        final Agent mockAgentOne = Mockito.mock(Agent.class);
        final Agent mockAgentTwo = Mockito.mock(Agent.class);
        final DynamicCompositeAgent compositeAgent = new DynamicCompositeAgent(DynamicCompositeAgentTest.ROLE_NAME, mockAgentOne, mockAgentTwo);
        final AgentInvoker invoker = new AgentInvoker(Throwable::printStackTrace, null, compositeAgent);
        invoker.start();
        Assert.assertTrue(compositeAgent.tryAdd(mockAgentOne));
        Assert.assertFalse(compositeAgent.tryAdd(mockAgentTwo));
        invoker.invoke();
        Assert.assertTrue(compositeAgent.tryAdd(mockAgentTwo));
    }

    @Test
    public void shouldDetectConcurrentRemove() {
        final Agent mockAgentOne = Mockito.mock(Agent.class);
        final Agent mockAgentTwo = Mockito.mock(Agent.class);
        final DynamicCompositeAgent compositeAgent = new DynamicCompositeAgent(DynamicCompositeAgentTest.ROLE_NAME, mockAgentOne, mockAgentTwo);
        final AgentInvoker invoker = new AgentInvoker(Throwable::printStackTrace, null, compositeAgent);
        invoker.start();
        Assert.assertTrue(compositeAgent.tryAdd(mockAgentOne));
        invoker.invoke();
        Assert.assertTrue(compositeAgent.tryAdd(mockAgentTwo));
        invoker.invoke();
        Assert.assertTrue(compositeAgent.tryRemove(mockAgentOne));
        Assert.assertFalse(compositeAgent.tryRemove(mockAgentTwo));
        invoker.invoke();
        Assert.assertTrue(compositeAgent.tryRemove(mockAgentTwo));
    }
}

