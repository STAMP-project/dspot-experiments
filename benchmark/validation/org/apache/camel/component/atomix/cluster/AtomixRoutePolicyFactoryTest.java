/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.atomix.cluster;


import io.atomix.catalyst.transport.Address;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.camel.test.AvailablePortFinder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class AtomixRoutePolicyFactoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AtomixRoutePolicyFactoryTest.class);

    private final List<Address> addresses = Arrays.asList(new Address("127.0.0.1", AvailablePortFinder.getNextAvailable()), new Address("127.0.0.1", AvailablePortFinder.getNextAvailable()), new Address("127.0.0.1", AvailablePortFinder.getNextAvailable()));

    private final Set<Address> results = new HashSet<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(addresses.size());

    private final CountDownLatch latch = new CountDownLatch(addresses.size());

    // ************************************
    // Test
    // ************************************
    @Test
    public void test() throws Exception {
        for (Address address : addresses) {
            scheduler.submit(() -> run(address));
        }
        latch.await(1, TimeUnit.MINUTES);
        scheduler.shutdownNow();
        Assert.assertEquals(addresses.size(), results.size());
        Assert.assertTrue(results.containsAll(addresses));
    }
}

