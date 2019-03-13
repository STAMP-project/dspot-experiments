/**
 * -
 * -\-\-
 * Helios Testing Library
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
package com.spotify.helios.testing;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.spotify.helios.client.HeliosClient;
import com.spotify.helios.common.descriptors.HostStatus;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class DefaultDeployerTest {
    private static final String HOSTB = "hostb";

    private static final String HOSTA = "hosta";

    private static final List<TemporaryJob> EMPTY_JOBS_LIST = Lists.newArrayList();

    private static final ListenableFuture<HostStatus> DOWN_STATUS = Futures.immediateFuture(DefaultDeployerTest.makeDummyStatusBuilder().setStatus(DOWN).build());

    private static final ListenableFuture<HostStatus> UP_STATUS = Futures.immediateFuture(DefaultDeployerTest.makeDummyStatusBuilder().setStatus(UP).build());

    private static final List<String> HOSTS = ImmutableList.of(DefaultDeployerTest.HOSTA, DefaultDeployerTest.HOSTB);

    private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(5);

    // Pick the first host in the list
    private static final HostPickingStrategy PICK_FIRST = new HostPickingStrategy() {
        @Override
        public String pickHost(final List<String> hosts) {
            return hosts.get(0);
        }
    };

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final HeliosClient client = Mockito.mock(HeliosClient.class);

    private final DefaultDeployer deployer = new DefaultDeployer(client, DefaultDeployerTest.EMPTY_JOBS_LIST, DefaultDeployerTest.PICK_FIRST, "", DefaultDeployerTest.TIMEOUT);

    @Test
    public void testTryAgainOnHostDown() throws Exception {
        // hosta is down, hostb is up.
        Mockito.when(client.hostStatus(DefaultDeployerTest.HOSTA)).thenReturn(DefaultDeployerTest.DOWN_STATUS);
        Mockito.when(client.hostStatus(DefaultDeployerTest.HOSTB)).thenReturn(DefaultDeployerTest.UP_STATUS);
        Assert.assertEquals(DefaultDeployerTest.HOSTB, deployer.pickHost(DefaultDeployerTest.HOSTS));
    }

    @Test
    public void testFailsOnAllDown() throws Exception {
        final DefaultDeployer sut = new DefaultDeployer(client, DefaultDeployerTest.EMPTY_JOBS_LIST, DefaultDeployerTest.PICK_FIRST, "", DefaultDeployerTest.TIMEOUT);
        // hosta is down, hostb is down too.
        Mockito.when(client.hostStatus(DefaultDeployerTest.HOSTA)).thenReturn(DefaultDeployerTest.DOWN_STATUS);
        Mockito.when(client.hostStatus(DefaultDeployerTest.HOSTB)).thenReturn(DefaultDeployerTest.DOWN_STATUS);
        exception.expect(AssertionError.class);
        sut.pickHost(DefaultDeployerTest.HOSTS);
    }

    @Test
    public void testHostStatusIsNull() throws Exception {
        Mockito.when(client.hostStatus(DefaultDeployerTest.HOSTA)).thenReturn(Futures.<HostStatus>immediateFuture(null));
        Mockito.when(client.hostStatus(DefaultDeployerTest.HOSTB)).thenReturn(DefaultDeployerTest.UP_STATUS);
        Assert.assertEquals(DefaultDeployerTest.HOSTB, deployer.pickHost(DefaultDeployerTest.HOSTS));
    }
}

