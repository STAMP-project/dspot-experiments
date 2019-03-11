/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.client.zookeeper;


import com.google.common.collect.ImmutableSet;
import com.linecorp.armeria.client.Endpoint;
import java.util.Set;
import javax.annotation.Nullable;
import org.junit.Test;
import zookeeperjunit.CloseableZooKeeper;


public class ZooKeeperEndpointGroupTest extends ZooKeeperTestBase {
    @Nullable
    private ZooKeeperEndpointGroup endpointGroup;

    @Test
    public void testGetEndpointGroup() {
        await().untilAsserted(() -> assertThat(endpointGroup.endpoints()).hasSameElementsAs(ZooKeeperTestBase.sampleEndpoints));
    }

    @Test
    public void testUpdateEndpointGroup() throws Throwable {
        Set<Endpoint> expected = ImmutableSet.of(Endpoint.of("127.0.0.1", 8001).withWeight(2), Endpoint.of("127.0.0.1", 8002).withWeight(3));
        // Add two more nodes.
        setNodeChild(expected);
        // Construct the final expected node list.
        final ImmutableSet.Builder<Endpoint> builder = ImmutableSet.builder();
        builder.addAll(ZooKeeperTestBase.sampleEndpoints).addAll(expected);
        expected = builder.build();
        try (CloseableZooKeeper zk = connection()) {
            zk.sync(ZooKeeperTestBase.zNode, ( rc, path, ctx) -> {
            }, null);
        }
        final Set<Endpoint> finalExpected = expected;
        await().untilAsserted(() -> assertThat(endpointGroup.endpoints()).hasSameElementsAs(finalExpected));
    }
}

