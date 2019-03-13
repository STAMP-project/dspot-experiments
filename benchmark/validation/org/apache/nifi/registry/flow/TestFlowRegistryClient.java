/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.registry.flow;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestFlowRegistryClient {
    private FlowRegistryClient flowRegistryClient;

    @Test
    public void testParamWithTrailingSlash() {
        flowRegistryClient.addFlowRegistry("1", "Registry 1", "http://localhost:1111", "NA");
        flowRegistryClient.addFlowRegistry("2", "Registry 2", "http://localhost:2222", "NA");
        flowRegistryClient.addFlowRegistry("3", "Registry 3", "http://localhost:3333", "NA");
        final String flowRegistryId = flowRegistryClient.getFlowRegistryId("http://localhost:1111/");
        Assert.assertNotNull(flowRegistryId);
        Assert.assertEquals("1", flowRegistryId);
    }

    @Test
    public void testClientWithTrailingSlash() {
        flowRegistryClient.addFlowRegistry("1", "Registry 1", "http://localhost:1111", "NA");
        flowRegistryClient.addFlowRegistry("2", "Registry 2", "http://localhost:2222/", "NA");
        flowRegistryClient.addFlowRegistry("3", "Registry 3", "http://localhost:3333", "NA");
        final String flowRegistryId = flowRegistryClient.getFlowRegistryId("http://localhost:2222");
        Assert.assertNotNull(flowRegistryId);
        Assert.assertEquals("2", flowRegistryId);
    }

    @Test
    public void testNoTrailingSlash() {
        flowRegistryClient.addFlowRegistry("1", "Registry 1", "http://localhost:1111", "NA");
        flowRegistryClient.addFlowRegistry("2", "Registry 2", "http://localhost:2222", "NA");
        flowRegistryClient.addFlowRegistry("3", "Registry 3", "http://localhost:3333", "NA");
        final String flowRegistryId = flowRegistryClient.getFlowRegistryId("http://localhost:3333");
        Assert.assertNotNull(flowRegistryId);
        Assert.assertEquals("3", flowRegistryId);
    }

    private static class MockFlowRegistryClient implements FlowRegistryClient {
        private Map<String, FlowRegistry> registryMap = new HashMap<>();

        @Override
        public FlowRegistry getFlowRegistry(String registryId) {
            return registryMap.get(registryId);
        }

        @Override
        public Set<String> getRegistryIdentifiers() {
            return registryMap.keySet();
        }

        @Override
        public void addFlowRegistry(FlowRegistry registry) {
            registryMap.put(registry.getIdentifier(), registry);
        }

        @Override
        public FlowRegistry addFlowRegistry(String registryId, String registryName, String registryUrl, String description) {
            final FlowRegistry flowRegistry = Mockito.mock(FlowRegistry.class);
            Mockito.when(flowRegistry.getIdentifier()).thenReturn(registryId);
            Mockito.when(flowRegistry.getName()).thenReturn(registryName);
            Mockito.when(flowRegistry.getURL()).thenReturn(registryUrl);
            Mockito.when(flowRegistry.getDescription()).thenReturn(description);
            registryMap.put(flowRegistry.getIdentifier(), flowRegistry);
            return flowRegistry;
        }

        @Override
        public FlowRegistry removeFlowRegistry(String registryId) {
            return registryMap.remove(registryId);
        }
    }
}

