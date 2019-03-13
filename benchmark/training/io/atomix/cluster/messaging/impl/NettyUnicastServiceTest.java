/**
 * Copyright 2018-present Open Networking Foundation
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
package io.atomix.cluster.messaging.impl;


import io.atomix.cluster.messaging.ManagedUnicastService;
import io.atomix.utils.net.Address;
import net.jodah.concurrentunit.ConcurrentTestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Netty unicast service test.
 */
public class NettyUnicastServiceTest extends ConcurrentTestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyBroadcastServiceTest.class);

    ManagedUnicastService service1;

    ManagedUnicastService service2;

    Address address1;

    Address address2;

    @Test
    public void testUnicast() throws Exception {
        service1.addListener("test", ( address, payload) -> {
            assertEquals(address2, address);
            assertArrayEquals("Hello world!".getBytes(), payload);
            resume();
        });
        service2.unicast(address1, "test", "Hello world!".getBytes());
        await(5000);
    }
}

