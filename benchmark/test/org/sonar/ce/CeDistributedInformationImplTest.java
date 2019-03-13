/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.ce;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.ce.taskprocessor.CeWorker;
import org.sonar.ce.taskprocessor.CeWorkerFactory;
import org.sonar.process.cluster.hz.HazelcastMember;


public class CeDistributedInformationImplTest {
    private String clientUUID1 = "1";

    private String clientUUID2 = "2";

    private String clientUUID3 = "3";

    private Map workerMap = ImmutableMap.of(clientUUID1, ImmutableSet.of("1", "2"), clientUUID2, ImmutableSet.of("3"), clientUUID3, ImmutableSet.of("4", "5", "6"));

    private HazelcastMember hzClientWrapper = Mockito.mock(HazelcastMember.class);

    @Test
    public void getWorkerUUIDs_returns_union_of_workers_uuids_of_local_and_cluster_worker_uuids() {
        Mockito.when(hzClientWrapper.getUuid()).thenReturn(clientUUID1);
        Mockito.when(hzClientWrapper.getMemberUuids()).thenReturn(ImmutableSet.of(clientUUID1, clientUUID2, clientUUID3));
        Mockito.when(hzClientWrapper.getReplicatedMap(WORKER_UUIDS)).thenReturn(workerMap);
        CeDistributedInformation ceDistributedInformation = new CeDistributedInformationImpl(hzClientWrapper, Mockito.mock(CeWorkerFactory.class));
        assertThat(ceDistributedInformation.getWorkerUUIDs()).containsExactly("1", "2", "3", "4", "5", "6");
    }

    @Test
    public void getWorkerUUIDs_must_filter_absent_client() {
        Mockito.when(hzClientWrapper.getUuid()).thenReturn(clientUUID1);
        Mockito.when(hzClientWrapper.getMemberUuids()).thenReturn(ImmutableSet.of(clientUUID1, clientUUID2));
        Mockito.when(hzClientWrapper.getReplicatedMap(WORKER_UUIDS)).thenReturn(workerMap);
        CeDistributedInformation ceDistributedInformation = new CeDistributedInformationImpl(hzClientWrapper, Mockito.mock(CeWorkerFactory.class));
        assertThat(ceDistributedInformation.getWorkerUUIDs()).containsExactly("1", "2", "3");
    }

    @Test
    public void broadcastWorkerUUIDs_adds_local_workerUUIDs_to_shared_map_under_key_of_localendpoint_uuid() {
        Set<String> connectedClients = new HashSet<>();
        Map modifiableWorkerMap = new HashMap<>();
        connectedClients.add(clientUUID1);
        connectedClients.add(clientUUID2);
        Mockito.when(hzClientWrapper.getUuid()).thenReturn(clientUUID1);
        Mockito.when(hzClientWrapper.getMemberUuids()).thenReturn(connectedClients);
        Mockito.when(hzClientWrapper.getReplicatedMap(WORKER_UUIDS)).thenReturn(modifiableWorkerMap);
        CeWorkerFactory ceWorkerFactory = Mockito.mock(CeWorkerFactory.class);
        Set<CeWorker> ceWorkers = Stream.of("a10", "a11").map(( uuid) -> {
            CeWorker res = Mockito.mock(CeWorker.class);
            Mockito.when(res.getUUID()).thenReturn(uuid);
            return res;
        }).collect(Collectors.toSet());
        Mockito.when(ceWorkerFactory.getWorkers()).thenReturn(ceWorkers);
        CeDistributedInformationImpl ceDistributedInformation = new CeDistributedInformationImpl(hzClientWrapper, ceWorkerFactory);
        try {
            ceDistributedInformation.broadcastWorkerUUIDs();
            assertThat(modifiableWorkerMap).containsExactly(entry(clientUUID1, ImmutableSet.of("a10", "a11")));
        } finally {
            ceDistributedInformation.stop();
        }
    }

    @Test
    public void stop_must_remove_local_workerUUIDs() {
        Set<String> connectedClients = new HashSet<>();
        connectedClients.add(clientUUID1);
        connectedClients.add(clientUUID2);
        connectedClients.add(clientUUID3);
        Map modifiableWorkerMap = new HashMap();
        modifiableWorkerMap.putAll(workerMap);
        Mockito.when(hzClientWrapper.getUuid()).thenReturn(clientUUID1);
        Mockito.when(hzClientWrapper.getMemberUuids()).thenReturn(connectedClients);
        Mockito.when(hzClientWrapper.getReplicatedMap(WORKER_UUIDS)).thenReturn(modifiableWorkerMap);
        CeDistributedInformationImpl ceDistributedInformation = new CeDistributedInformationImpl(hzClientWrapper, Mockito.mock(CeWorkerFactory.class));
        ceDistributedInformation.stop();
        assertThat(modifiableWorkerMap).containsExactly(entry(clientUUID2, ImmutableSet.of("3")), entry(clientUUID3, ImmutableSet.of("4", "5", "6")));
    }
}

