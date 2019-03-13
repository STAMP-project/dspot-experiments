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
package com.hazelcast.cluster;


import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import example.serialization.TestDeserialized;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Tests if deserialization blacklisting works for MulticastService.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class MulticastDeserializationTest {
    private static final int MULTICAST_PORT = 53535;

    private static final String MULTICAST_GROUP = "224.0.0.219";

    // TTL = 0 : restricted to the same host, won't be output by any interface
    private static final int MULTICAST_TTL = 0;

    /**
     * Given: Multicast is configured.
     * When: DatagramPacket with a correct Packet comes. The Packet references
     * Java serializer and the serialized object is not a Join message.
     * Then: The object from the Packet is not deserialized.
     */
    @Test
    public void test() throws Exception {
        Config config = getConfig();
        Hazelcast.newHazelcastInstance(config);
        sendJoinDatagram(new TestDeserialized());
        Thread.sleep(500L);
        Assert.assertFalse("Untrusted deserialization is possible", TestDeserialized.isDeserialized);
    }
}

