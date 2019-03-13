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
package com.hazelcast.internal.ascii;


import EndpointQualifier.MEMBER;
import EndpointQualifier.REST;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * This test is intentionally not in the {@link com.hazelcast.test.annotation.ParallelTest} category,
 * since it starts real HazelcastInstances which have REST enabled.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(SlowTest.class)
public class RestMultiendpointTest extends RestTest {
    @Test
    public void assertAdvancedNetworkInUse() {
        setup();
        int numberOfEndpointsInConfig = config.getAdvancedNetworkConfig().getEndpointConfigs().size();
        MemberImpl local = HazelcastTestSupport.getNode(instance).getClusterService().getLocalMember();
        Assert.assertTrue(((local.getAddressMap().size()) == numberOfEndpointsInConfig));
        Assert.assertFalse(local.getSocketAddress(REST).equals(local.getSocketAddress(MEMBER)));
    }
}

