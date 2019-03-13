/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.agent;


import com.spotify.helios.common.Clock;
import com.spotify.helios.servicescommon.coordination.Paths;
import com.spotify.helios.servicescommon.coordination.ZooKeeperClient;
import com.spotify.helios.servicescommon.coordination.ZooKeeperOperation;
import com.spotify.helios.servicescommon.coordination.ZooKeeperOperations;
import java.util.List;
import org.apache.zookeeper.data.Stat;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class AgentZooKeeperRegistrarTest {
    private final ZooKeeperClient client = Mockito.mock(ZooKeeperClient.class);

    private final String agentName = "agent";

    private final String hostId = "1234";

    private final int registrationTtl = 2;

    // a clock that always returns the same time
    private final Instant now = Instant.now();

    private final Clock clock = () -> now;

    private final AgentZooKeeperRegistrar registrar = new AgentZooKeeperRegistrar(agentName, hostId, registrationTtl, clock);

    private final String hostPath = Paths.statusHostInfo(agentName);

    private final String upPath = Paths.statusHostUp(agentName);

    private final String idPath = Paths.configHostId(agentName);

    @Test
    public void newRegistration() throws Exception {
        // stat = null means path does not exist
        Mockito.when(client.exists(idPath)).thenReturn(null);
        Mockito.when(client.stat(hostPath)).thenReturn(null);
        final boolean success = registrar.tryToRegister(client);
        Assert.assertTrue(success);
        // verify the id was claimed in zookeeper
        Mockito.verify(client).createAndSetData(idPath, hostId.getBytes());
    }

    @Test
    public void alreadyRegistered_SameHostId() throws Exception {
        final boolean success = registrar.tryToRegister(client);
        Assert.assertTrue(success);
        // no need to re-write the id path
        Mockito.verify(client, Mockito.never()).createAndSetData(idPath, hostId.getBytes());
    }

    @Test
    public void recentRegistrationExists_DifferentHostId() throws Exception {
        // hostInfo has been updated for this host name very recently...
        final Stat hostInfo = new Stat();
        hostInfo.setMtime(clock.now().getMillis());
        Mockito.when(client.stat(hostPath)).thenReturn(hostInfo);
        // ... and the name is claimed by a different ID
        Mockito.when(client.getData(idPath)).thenReturn("a different host".getBytes());
        final boolean success = registrar.tryToRegister(client);
        Assert.assertFalse(success);
        Mockito.verify(client, Mockito.never()).createAndSetData(idPath, hostId.getBytes());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void oldRegistrationExists_DifferentHostId() throws Exception {
        // the hostname is claimed by a different ID...
        Mockito.when(client.getData(idPath)).thenReturn("a different host".getBytes());
        // ... but the hostInfo was last updated more than TTL minutes ago
        final Stat hostInfo = new Stat();
        hostInfo.setMtime(clock.now().minus(Duration.standardMinutes(((registrationTtl) * 2))).getMillis());
        Mockito.when(client.stat(hostPath)).thenReturn(hostInfo);
        // expect the old host to be deregistered and this registration to succeed
        final boolean success = registrar.tryToRegister(client);
        Assert.assertTrue(success);
        // expect a transaction containing a delete of the idpath followed by a create
        // // TODO (mbrown): this should really be in a test of ZooKeeperRegistrarUtil, and
        // AgentZooKeeperRegistrar should not call a static method to do this
        final ArgumentCaptor<List> opsCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(client).transaction(opsCaptor.capture());
        // note that we are not testing full equality of the list, just that it contains
        // a few notable items
        final List<ZooKeeperOperation> actual = opsCaptor.getValue();
        Assert.assertThat(actual, Matchers.hasItems(ZooKeeperOperations.delete(idPath), ZooKeeperOperations.create(idPath, hostId.getBytes())));
    }
}

