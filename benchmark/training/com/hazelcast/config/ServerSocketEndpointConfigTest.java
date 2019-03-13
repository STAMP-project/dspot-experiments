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
package com.hazelcast.config;


import ProtocolType.MEMBER;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ServerSocketEndpointConfigTest {
    private ServerSocketEndpointConfig endpointConfig;

    private String endpointName = HazelcastTestSupport.randomName();

    @Test
    public void testEndpointConfig_defaultConstructor() {
        endpointConfig = new ServerSocketEndpointConfig();
        endpointConfig.setName(endpointName);
        Assert.assertEquals(endpointName, endpointConfig.getName());
        // assertNull(endpointConfig.getMemberAddressProviderConfig());
        Assert.assertNull(endpointConfig.getProtocolType());
    }

    @Test
    public void testEndpointConfig_fullyConfigured() {
        endpointConfig = new ServerSocketEndpointConfig();
        // .setMemberAddressProviderConfig(new MemberAddressProviderConfig()
        // .setEnabled(true)
        // .setClassName("com.hazelcast.MemberAddressProviderImpl"))
        endpointConfig.setName("anotherName").setProtocolType(MEMBER).setPort(19000).setPublicAddress("192.168.2.1");
        Assert.assertEquals(endpointConfig.getProtocolType(), MEMBER);
        Assert.assertEquals("anotherName", endpointConfig.getName());
        Assert.assertEquals(19000, endpointConfig.getPort());
        Assert.assertEquals("192.168.2.1", endpointConfig.getPublicAddress());
        // assertTrue(endpointConfig.getMemberAddressProviderConfig().isEnabled());
        // assertEquals("com.hazelcast.MemberAddressProviderImpl", endpointConfig.getMemberAddressProviderConfig().getClassName());
    }
}

