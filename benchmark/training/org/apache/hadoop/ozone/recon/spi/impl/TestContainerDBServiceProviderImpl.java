/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.recon.spi.impl;


import MetadataStore.KeyValue;
import com.google.inject.Injector;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.ozone.recon.api.types.ContainerKeyPrefix;
import org.apache.hadoop.ozone.recon.spi.ContainerDBServiceProvider;
import org.apache.hadoop.utils.MetaStoreIterator;
import org.apache.hadoop.utils.MetadataStore;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit Tests for ContainerDBServiceProviderImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestContainerDBServiceProviderImpl {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private MetadataStore containerDBStore;

    private ContainerDBServiceProvider containerDbServiceProvider = new ContainerDBServiceProviderImpl();

    private Injector injector;

    @Test
    public void testStoreContainerKeyMapping() throws Exception {
        long containerId = System.currentTimeMillis();
        Map<String, Integer> prefixCounts = new HashMap<>();
        prefixCounts.put("V1/B1/K1", 1);
        prefixCounts.put("V1/B1/K2", 2);
        prefixCounts.put("V1/B2/K3", 3);
        for (String prefix : prefixCounts.keySet()) {
            ContainerKeyPrefix containerKeyPrefix = new ContainerKeyPrefix(containerId, prefix);
            containerDbServiceProvider.storeContainerKeyMapping(containerKeyPrefix, prefixCounts.get(prefix));
        }
        int count = 0;
        MetaStoreIterator<MetadataStore.KeyValue> iterator = containerDBStore.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        } 
        Assert.assertTrue((count == 3));
    }

    @Test
    public void testGetCountForForContainerKeyPrefix() throws Exception {
        long containerId = System.currentTimeMillis();
        containerDbServiceProvider.storeContainerKeyMapping(new ContainerKeyPrefix(containerId, "V1/B1/K1"), 2);
        Integer count = containerDbServiceProvider.getCountForForContainerKeyPrefix(new ContainerKeyPrefix(containerId, "V1/B1/K1"));
        Assert.assertTrue((count == 2));
    }

    @Test
    public void testGetKeyPrefixesForContainer() throws Exception {
        long containerId = System.currentTimeMillis();
        containerDbServiceProvider.storeContainerKeyMapping(new ContainerKeyPrefix(containerId, "V1/B1/K1"), 1);
        containerDbServiceProvider.storeContainerKeyMapping(new ContainerKeyPrefix(containerId, "V1/B1/K2"), 2);
        long nextContainerId = System.currentTimeMillis();
        containerDbServiceProvider.storeContainerKeyMapping(new ContainerKeyPrefix(nextContainerId, "V1/B2/K1"), 3);
        Map<String, Integer> keyPrefixMap = containerDbServiceProvider.getKeyPrefixesForContainer(containerId);
        Assert.assertTrue(((keyPrefixMap.size()) == 2));
        Assert.assertTrue(((keyPrefixMap.get("V1/B1/K1")) == 1));
        Assert.assertTrue(((keyPrefixMap.get("V1/B1/K2")) == 2));
        keyPrefixMap = containerDbServiceProvider.getKeyPrefixesForContainer(nextContainerId);
        Assert.assertTrue(((keyPrefixMap.size()) == 1));
        Assert.assertTrue(((keyPrefixMap.get("V1/B2/K1")) == 3));
    }
}

