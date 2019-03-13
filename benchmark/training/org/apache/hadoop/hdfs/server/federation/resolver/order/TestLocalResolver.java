/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.federation.resolver.order;


import DestinationOrder.LOCAL;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.server.federation.resolver.MultipleDestinationMountTableResolver;
import org.apache.hadoop.hdfs.server.federation.resolver.PathLocation;
import org.apache.hadoop.hdfs.server.federation.router.Router;
import org.apache.hadoop.hdfs.server.federation.store.MembershipStore;
import org.apache.hadoop.hdfs.server.federation.store.StateStoreService;
import org.apache.hadoop.hdfs.server.federation.store.protocol.GetNamenodeRegistrationsRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.GetNamenodeRegistrationsResponse;
import org.apache.hadoop.hdfs.server.federation.store.records.MembershipState;
import org.apache.hadoop.hdfs.server.federation.store.records.MountTable;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Test the {@link LocalResolver}.
 */
public class TestLocalResolver {
    @Test
    @SuppressWarnings("unchecked")
    public void testLocalResolver() throws IOException {
        // Mock the subcluster mapping
        Configuration conf = new Configuration();
        Router router = Mockito.mock(Router.class);
        StateStoreService stateStore = Mockito.mock(StateStoreService.class);
        MembershipStore membership = Mockito.mock(MembershipStore.class);
        Mockito.when(router.getStateStore()).thenReturn(stateStore);
        Mockito.when(stateStore.getRegisteredRecordStore(ArgumentMatchers.any(Class.class))).thenReturn(membership);
        GetNamenodeRegistrationsResponse response = GetNamenodeRegistrationsResponse.newInstance();
        // Set the mapping for each client
        List<MembershipState> records = new LinkedList<>();
        records.add(newMembershipState("client0", "subcluster0"));
        records.add(newMembershipState("client1", "subcluster1"));
        records.add(newMembershipState("client2", "subcluster2"));
        response.setNamenodeMemberships(records);
        Mockito.when(membership.getNamenodeRegistrations(ArgumentMatchers.any(GetNamenodeRegistrationsRequest.class))).thenReturn(response);
        // Mock the client resolution: it will be anything in sb
        StringBuilder sb = new StringBuilder("clientX");
        LocalResolver localResolver = new LocalResolver(conf, router);
        LocalResolver spyLocalResolver = Mockito.spy(localResolver);
        Mockito.doAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return sb.toString();
            }
        }).when(spyLocalResolver).getClientAddr();
        // Add the mocks to the resolver
        MultipleDestinationMountTableResolver resolver = new MultipleDestinationMountTableResolver(conf, router);
        resolver.addResolver(LOCAL, spyLocalResolver);
        // We point /local to subclusters 0, 1, 2 with the local order
        Map<String, String> mapLocal = new HashMap<>();
        mapLocal.put("subcluster0", "/local");
        mapLocal.put("subcluster1", "/local");
        mapLocal.put("subcluster2", "/local");
        MountTable localEntry = MountTable.newInstance("/local", mapLocal);
        localEntry.setDestOrder(LOCAL);
        resolver.addEntry(localEntry);
        // Test first with the default destination
        PathLocation dest = resolver.getDestinationForPath("/local/file0.txt");
        assertDestination("subcluster0", dest);
        // We change the client location and verify
        TestLocalResolver.setClient(sb, "client2");
        dest = resolver.getDestinationForPath("/local/file0.txt");
        assertDestination("subcluster2", dest);
        TestLocalResolver.setClient(sb, "client1");
        dest = resolver.getDestinationForPath("/local/file0.txt");
        assertDestination("subcluster1", dest);
        TestLocalResolver.setClient(sb, "client0");
        dest = resolver.getDestinationForPath("/local/file0.txt");
        assertDestination("subcluster0", dest);
    }
}

