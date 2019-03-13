/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.client.spi.impl.discovery;


import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class HazelcastCloudProviderTest {
    private Map<Address, Address> expectedAddresses = new ConcurrentHashMap<Address, Address>();

    private HazelcastCloudDiscovery hazelcastCloudDiscovery;

    private HazelcastCloudAddressProvider provider;

    @Test
    public void testLoadAddresses() {
        Collection<Address> addresses = provider.loadAddresses().primary();
        Assert.assertEquals(3, addresses.size());
        for (Address address : expectedAddresses.keySet()) {
            addresses.remove(address);
        }
        Assert.assertTrue(addresses.isEmpty());
    }

    @Test
    public void testLoadAddresses_whenExceptionIsThrown() {
        Mockito.when(hazelcastCloudDiscovery.discoverNodes()).thenThrow(new IllegalStateException("Expected exception"));
        Collection<Address> addresses = provider.loadAddresses().primary();
        Assert.assertEquals("Expected that no addresses are loaded", 0, addresses.size());
    }
}

