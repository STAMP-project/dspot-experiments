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


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class CompositeAgentTest {
    static class AgentException extends RuntimeException {
        final int index;

        AgentException(final int index) {
            this.index = index;
        }
    }

    private final Agent[] agents = new Agent[]{ Mockito.mock(Agent.class), Mockito.mock(Agent.class), Mockito.mock(Agent.class) };

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptEmptyList() {
        final CompositeAgent ignore = new CompositeAgent();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullAgents() {
        final CompositeAgent ignore = new CompositeAgent(agents[0], null, agents[1]);
    }

    @Test
    public void shouldApplyLifecycleToAll() throws Exception {
        final CompositeAgent compositeAgent = new CompositeAgent(agents[0], agents[1], agents[2]);
        compositeAgent.onStart();
        for (final Agent agent : agents) {
            Mockito.verify(agent).onStart();
        }
        compositeAgent.doWork();
        for (final Agent agent : agents) {
            Mockito.verify(agent).doWork();
        }
        compositeAgent.onClose();
        for (final Agent agent : agents) {
            Mockito.verify(agent).onClose();
        }
    }

    @Test
    public void shouldApplyLifecycleToAllDespiteExceptions() throws Exception {
        final CompositeAgent compositeAgent = new CompositeAgent(agents[0], agents[1], agents[2]);
        for (int i = 0; i < (agents.length); i++) {
            final int index = i;
            final Agent agent = agents[index];
            Mockito.doThrow(new CompositeAgentTest.AgentException(index)).when(agent).onStart();
        }
        try {
            compositeAgent.onStart();
        } catch (final Exception e) {
            for (final Throwable suppressed : e.getSuppressed()) {
                Assert.assertTrue((suppressed instanceof CompositeAgentTest.AgentException));
            }
        }
        for (final Agent agent : agents) {
            Mockito.verify(agent).onStart();
        }
        for (int i = 0; i < (agents.length); i++) {
            final int index = i;
            final Agent agent = agents[index];
            Mockito.doThrow(new CompositeAgentTest.AgentException(index)).when(agent).doWork();
        }
        for (int i = 0; i < (agents.length); i++) {
            try {
                compositeAgent.doWork();
            } catch (final CompositeAgentTest.AgentException e) {
                Assert.assertEquals(i, e.index);
            }
        }
        for (final Agent agent : agents) {
            Mockito.verify(agent).doWork();
        }
        for (int i = 0; i < (agents.length); i++) {
            final int index = i;
            final Agent agent = agents[index];
            Mockito.doThrow(new CompositeAgentTest.AgentException(index)).when(agent).onClose();
        }
        try {
            compositeAgent.onClose();
        } catch (final Exception e) {
            for (final Throwable suppressed : e.getSuppressed()) {
                Assert.assertTrue((suppressed instanceof CompositeAgentTest.AgentException));
            }
        }
        for (final Agent agent : agents) {
            Mockito.verify(agent).onClose();
        }
    }
}

