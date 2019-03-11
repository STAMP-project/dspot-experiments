/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.server.domain;


import AgentStatus.Disabled;
import AgentStatus.Pending;
import SystemEnvironment.MAX_PENDING_AGENTS_ALLOWED;
import com.thoughtworks.go.config.AgentConfig;
import com.thoughtworks.go.config.Agents;
import com.thoughtworks.go.domain.AgentInstance;
import com.thoughtworks.go.domain.NullAgentInstance;
import com.thoughtworks.go.domain.exception.MaxPendingAgentsLimitReachedException;
import com.thoughtworks.go.helper.AgentInstanceMother;
import com.thoughtworks.go.listener.AgentStatusChangeListener;
import com.thoughtworks.go.server.service.AgentRuntimeInfo;
import com.thoughtworks.go.util.SystemEnvironment;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AgentInstancesTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AgentInstance idle;

    private AgentInstance building;

    private AgentInstance pending;

    private AgentInstance disabled;

    private AgentInstance local;

    @Mock
    private SystemEnvironment systemEnvironment;

    private AgentStatusChangeListener agentStatusChangeListener;

    @Test
    public void shouldUnderstandFilteringAgentListBasedOnUuid() {
        AgentInstances instances = new AgentInstances(Mockito.mock(AgentStatusChangeListener.class));
        AgentRuntimeInfo agent1 = AgentRuntimeInfo.fromServer(new AgentConfig("uuid-1", "host-1", "192.168.1.2"), true, "/foo/bar", 100L, "linux", false);
        AgentRuntimeInfo agent2 = AgentRuntimeInfo.fromServer(new AgentConfig("uuid-2", "host-2", "192.168.1.3"), true, "/bar/baz", 200L, "linux", false);
        AgentRuntimeInfo agent3 = AgentRuntimeInfo.fromServer(new AgentConfig("uuid-3", "host-3", "192.168.1.4"), true, "/baz/quux", 300L, "linux", false);
        AgentInstance instance1 = AgentInstance.createFromLiveAgent(agent1, systemEnvironment, Mockito.mock(AgentStatusChangeListener.class));
        instances.add(instance1);
        instances.add(AgentInstance.createFromLiveAgent(agent2, systemEnvironment, Mockito.mock(AgentStatusChangeListener.class)));
        AgentInstance instance3 = AgentInstance.createFromLiveAgent(agent3, systemEnvironment, Mockito.mock(AgentStatusChangeListener.class));
        instances.add(instance3);
        List<AgentInstance> agents = instances.filter(Arrays.asList("uuid-1", "uuid-3"));
        Assert.assertThat(agents, JUnitMatchers.hasItems(instance1, instance3));
        Assert.assertThat(agents.size(), Matchers.is(2));
    }

    @Test
    public void shouldFindEnabledAgents() {
        AgentInstances agentInstances = sample();
        AgentInstances enabledAgents = agentInstances.findEnabledAgents();
        Assert.assertThat(enabledAgents.size(), Matchers.is(2));
        Assert.assertThat(enabledAgents.findAgentAndRefreshStatus("uuid2"), Matchers.is(idle));
        Assert.assertThat(enabledAgents.findAgentAndRefreshStatus("uuid3"), Matchers.is(building));
    }

    @Test
    public void shouldFindRegisteredAgents() {
        AgentInstances agentInstances = sample();
        AgentInstances agents = agentInstances.findRegisteredAgents();
        Assert.assertThat(agents.size(), Matchers.is(3));
        Assert.assertThat(agents.findAgentAndRefreshStatus("uuid2"), Matchers.is(idle));
        Assert.assertThat(agents.findAgentAndRefreshStatus("uuid3"), Matchers.is(building));
        Assert.assertThat(agents.findAgentAndRefreshStatus("uuid5"), Matchers.is(disabled));
    }

    @Test
    public void shouldFindAgentsByItHostName() throws Exception {
        AgentInstance idle = AgentInstanceMother.idle(new Date(), "ghost-name");
        AgentInstances agentInstances = new AgentInstances(systemEnvironment, agentStatusChangeListener, idle, AgentInstanceMother.building());
        AgentInstance byHostname = agentInstances.findFirstByHostname("ghost-name");
        Assert.assertThat(byHostname, Matchers.is(idle));
    }

    @Test
    public void shouldReturnNullAgentsWhenHostNameIsNotFound() throws Exception {
        AgentInstances agentInstances = new AgentInstances(systemEnvironment, agentStatusChangeListener, AgentInstanceMother.building());
        agentInstances.add(idle);
        agentInstances.add(building);
        AgentInstance byHostname = agentInstances.findFirstByHostname("not-exist");
        Assert.assertThat(byHostname, Matchers.is(Matchers.instanceOf(NullAgentInstance.class)));
    }

    @Test
    public void shouldReturnFirstMatchedAgentsWhenHostNameHasMoreThanOneMatch() throws Exception {
        AgentInstance agent = AgentInstance.createFromConfig(new AgentConfig("uuid20", "CCeDev01", "10.18.5.20"), systemEnvironment, null);
        AgentInstance duplicatedAgent = AgentInstance.createFromConfig(new AgentConfig("uuid21", "CCeDev01", "10.18.5.20"), systemEnvironment, null);
        AgentInstances agentInstances = new AgentInstances(systemEnvironment, agentStatusChangeListener, agent, duplicatedAgent);
        AgentInstance byHostname = agentInstances.findFirstByHostname("CCeDev01");
        Assert.assertThat(byHostname, Matchers.is(agent));
    }

    @Test
    public void shouldAddAgentIntoMemoryAfterAgentIsManuallyAddedInConfigFile() throws Exception {
        AgentInstances agentInstances = new AgentInstances(Mockito.mock(AgentStatusChangeListener.class));
        AgentConfig agentConfig = new AgentConfig("uuid20", "CCeDev01", "10.18.5.20");
        agentInstances.sync(new Agents(agentConfig));
        Assert.assertThat(agentInstances.size(), Matchers.is(1));
        Assert.assertThat(agentInstances.findAgentAndRefreshStatus("uuid20").agentConfig(), Matchers.is(agentConfig));
    }

    @Test
    public void shouldRemoveAgentWhenAgentIsRemovedFromConfigFile() throws Exception {
        AgentInstances agentInstances = new AgentInstances(systemEnvironment, agentStatusChangeListener, idle, building);
        Agents oneAgentIsRemoved = new Agents(new AgentConfig("uuid2", "CCeDev01", "10.18.5.1"));
        agentInstances.sync(oneAgentIsRemoved);
        Assert.assertThat(agentInstances.size(), Matchers.is(1));
        Assert.assertThat(agentInstances.findAgentAndRefreshStatus("uuid2"), Matchers.is(idle));
        Assert.assertThat(agentInstances.findAgentAndRefreshStatus("uuid1"), Matchers.is(new NullAgentInstance("uuid1")));
    }

    @Test
    public void shouldSyncAgent() throws Exception {
        AgentInstances agentInstances = new AgentInstances(systemEnvironment, agentStatusChangeListener, AgentInstanceMother.building(), idle);
        AgentConfig agentConfig = new AgentConfig("uuid2", "CCeDev01", "10.18.5.1");
        agentConfig.setDisabled(true);
        Agents oneAgentIsRemoved = new Agents(agentConfig);
        agentInstances.sync(oneAgentIsRemoved);
        Assert.assertThat(agentInstances.findAgentAndRefreshStatus("uuid2").getStatus(), Matchers.is(Disabled));
    }

    @Test
    public void shouldNotRemovePendingAgentDuringSync() throws Exception {
        AgentInstances agentInstances = new AgentInstances(systemEnvironment, agentStatusChangeListener, AgentInstanceMother.building());
        agentInstances.add(pending);
        Agents agents = new Agents();
        agentInstances.sync(agents);
        Assert.assertThat(agentInstances.size(), Matchers.is(1));
        Assert.assertThat(agentInstances.findAgentAndRefreshStatus("uuid4").getStatus(), Matchers.is(Pending));
    }

    @Test
    public void agentHostnameShouldBeUnique() {
        AgentConfig agentConfig = new AgentConfig("uuid2", "CCeDev01", "10.18.5.1");
        AgentInstances agentInstances = new AgentInstances(Mockito.mock(AgentStatusChangeListener.class));
        agentInstances.register(AgentRuntimeInfo.fromServer(agentConfig, false, "/var/lib", 0L, "linux", false));
        agentInstances.register(AgentRuntimeInfo.fromServer(agentConfig, false, "/var/lib", 0L, "linux", false));
    }

    @Test(expected = MaxPendingAgentsLimitReachedException.class)
    public void registerShouldErrorOutIfMaxPendingAgentsLimitIsReached() {
        AgentConfig agentConfig = new AgentConfig("uuid2", "CCeDev01", "10.18.5.1");
        AgentInstances agentInstances = new AgentInstances(systemEnvironment, agentStatusChangeListener, AgentInstanceMother.pending());
        Mockito.when(systemEnvironment.get(MAX_PENDING_AGENTS_ALLOWED)).thenReturn(1);
        agentInstances.register(AgentRuntimeInfo.fromServer(agentConfig, false, "/var/lib", 0L, "linux", false));
    }

    @Test
    public void shouldRemovePendingAgentThatIsTimedOut() {
        Mockito.when(systemEnvironment.getAgentConnectionTimeout()).thenReturn((-1));
        AgentInstances agentInstances = new AgentInstances(systemEnvironment, agentStatusChangeListener, pending, building, disabled);
        agentInstances.refresh();
        Assert.assertThat(agentInstances.findAgentAndRefreshStatus("uuid4"), Matchers.is(Matchers.instanceOf(NullAgentInstance.class)));
    }

    @Test
    public void shouldSupportConcurrentOperations() throws Exception {
        final AgentInstances agentInstances = new AgentInstances(Mockito.mock(AgentStatusChangeListener.class));
        // register 100 agents
        for (int i = 0; i < 100; i++) {
            AgentConfig agentConfig = new AgentConfig(("uuid" + i), ("CCeDev_" + i), ("10.18.5." + i));
            agentInstances.register(AgentRuntimeInfo.fromServer(agentConfig, false, "/var/lib", Long.MAX_VALUE, "linux", false));
        }
        thrown.expect(MaxPendingAgentsLimitReachedException.class);
        thrown.expectMessage("Max pending agents allowed 100, limit reached");
        AgentConfig agentConfig = new AgentConfig(("uuid" + 200), ("CCeDev_" + 200), ("10.18.5." + 200));
        agentInstances.register(AgentRuntimeInfo.fromServer(agentConfig, false, "/var/lib", Long.MAX_VALUE, "linux", false));
    }

    private static class AgentAdder implements Runnable {
        private final AgentInstances agentInstances;

        private boolean stop;

        public static AgentInstancesTest.AgentAdder startAdding(AgentInstances agentInstances) {
            AgentInstancesTest.AgentAdder agentAdder = new AgentInstancesTest.AgentAdder(agentInstances);
            Thread thread = new Thread(agentAdder);
            thread.setDaemon(true);
            thread.start();
            return agentAdder;
        }

        private AgentAdder(AgentInstances agentInstances) {
            this.agentInstances = agentInstances;
        }

        public void run() {
            int count = 0;
            while (!(stop)) {
                AgentConfig agentConfig = new AgentConfig(("uuid" + count), ("CCeDev_" + count), ("10.18.5." + count));
                agentInstances.register(AgentRuntimeInfo.fromServer(agentConfig, false, "/var/lib", Long.MAX_VALUE, "linux", false));
                count++;
            } 
        }

        public void stop() {
            this.stop = true;
        }
    }
}

