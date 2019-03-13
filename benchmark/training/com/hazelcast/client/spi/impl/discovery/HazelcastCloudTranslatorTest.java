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


import com.hazelcast.logging.LoggingService;
import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class HazelcastCloudTranslatorTest {
    private final Map<Address, Address> lookup = new HashMap<Address, Address>();

    private Address privateAddress;

    private Address publicAddress;

    private LoggingService loggingService;

    private HazelcastCloudAddressTranslator translator;

    @Test
    public void testTranslate_whenAddressIsNull_thenReturnNull() {
        Address actual = translator.translate(null);
        Assert.assertNull(actual);
    }

    @Test
    public void testTranslate() {
        Address actual = translator.translate(privateAddress);
        Assert.assertEquals(publicAddress.getHost(), actual.getHost());
        Assert.assertEquals(privateAddress.getPort(), actual.getPort());
    }

    @Test
    public void testRefreshAndTranslate() {
        translator.refresh();
        Address actual = translator.translate(privateAddress);
        Assert.assertEquals(publicAddress.getHost(), actual.getHost());
        Assert.assertEquals(privateAddress.getPort(), actual.getPort());
    }

    @Test
    public void testTranslate_whenNotFound_thenReturnNull() throws UnknownHostException {
        Address notAvailableAddress = new Address("127.0.0.3", 5701);
        Address actual = translator.translate(notAvailableAddress);
        Assert.assertNull(actual);
    }

    @Test
    public void testRefresh_whenException_thenLogWarning() {
        HazelcastCloudDiscovery cloudDiscovery = Mockito.mock(HazelcastCloudDiscovery.class);
        Mockito.when(cloudDiscovery.discoverNodes()).thenReturn(lookup);
        translator = new HazelcastCloudAddressTranslator(cloudDiscovery, loggingService);
        translator.refresh();
    }
}

