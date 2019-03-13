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
package com.spotify.helios.master.reaper;


import HostStatus.Status;
import com.google.common.collect.Lists;
import com.spotify.helios.common.Clock;
import com.spotify.helios.common.descriptors.AgentInfo;
import com.spotify.helios.common.descriptors.HostStatus;
import com.spotify.helios.master.MasterModel;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.joda.time.Instant;
import org.junit.Test;
import org.mockito.Mockito;


public class DeadAgentReaperTest {
    private static final long TIMEOUT_HOURS = 1000;

    private static class Datapoint {
        private final String host;

        private final long startTime;

        private final long uptime;

        private final Status status;

        private final boolean expectReap;

        private Datapoint(final String host, final long startTimeHours, final long uptimeHours, final HostStatus.Status status, final boolean expectReap) {
            this.host = host;
            this.startTime = TimeUnit.HOURS.toMillis(startTimeHours);
            this.uptime = TimeUnit.HOURS.toMillis(uptimeHours);
            this.status = status;
            this.expectReap = expectReap;
        }
    }

    @Test
    public void testDeadAgentReaper() throws Exception {
        final MasterModel masterModel = Mockito.mock(MasterModel.class);
        final Clock clock = Mockito.mock(Clock.class);
        Mockito.when(clock.now()).thenReturn(new Instant(TimeUnit.HOURS.toMillis(2000)));
        final List<DeadAgentReaperTest.Datapoint> datapoints = // Agents started in the future should not be reaped, even if they are reported as down
        // Agents that are UP should not be reaped even if the start and uptime indicate that
        // they should
        Lists.newArrayList(new DeadAgentReaperTest.Datapoint("host1", 0, ((DeadAgentReaperTest.TIMEOUT_HOURS) - 1), Status.DOWN, true), new DeadAgentReaperTest.Datapoint("host2", 0, ((DeadAgentReaperTest.TIMEOUT_HOURS) + 1), Status.DOWN, false), new DeadAgentReaperTest.Datapoint("host3", 1000, 1000, Status.UP, false), new DeadAgentReaperTest.Datapoint("host4", 500, 300, Status.DOWN, true), new DeadAgentReaperTest.Datapoint("host5", 5000, 0, Status.DOWN, false), new DeadAgentReaperTest.Datapoint("host6", 0, 0, Status.UP, false));
        Mockito.when(masterModel.listHosts()).thenReturn(Lists.newArrayList(datapoints.stream().map(( input) -> input.host).collect(Collectors.toList())));
        for (final DeadAgentReaperTest.Datapoint datapoint : datapoints) {
            Mockito.when(masterModel.isHostUp(datapoint.host)).thenReturn(((Status.UP) == (datapoint.status)));
            Mockito.when(masterModel.getAgentInfo(datapoint.host)).thenReturn(AgentInfo.newBuilder().setStartTime(datapoint.startTime).setUptime(datapoint.uptime).build());
        }
        final DeadAgentReaper reaper = new DeadAgentReaper(masterModel, DeadAgentReaperTest.TIMEOUT_HOURS, clock, 100, 0);
        reaper.startAsync().awaitRunning();
        for (final DeadAgentReaperTest.Datapoint datapoint : datapoints) {
            if (datapoint.expectReap) {
                Mockito.verify(masterModel, Mockito.timeout(500)).deregisterHost(datapoint.host);
            } else {
                Mockito.verify(masterModel, Mockito.never()).deregisterHost(datapoint.host);
            }
        }
    }
}

