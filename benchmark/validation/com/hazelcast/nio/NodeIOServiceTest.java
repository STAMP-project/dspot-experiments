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
package com.hazelcast.nio;


import com.hazelcast.config.NetworkConfig;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NodeIOServiceTest extends HazelcastTestSupport {
    private NetworkConfig networkConfig;

    private NodeIOService ioService;

    @Test
    public void testGetOutboundPorts_zeroTakesPrecedenceInRange() {
        networkConfig.addOutboundPortDefinition("0-100");
        Collection<Integer> outboundPorts = ioService.getOutboundPorts(EndpointQualifier.MEMBER);
        Assert.assertEquals(0, outboundPorts.size());
    }

    @Test
    public void testGetOutboundPorts_zeroTakesPrecedenceInCSV() {
        networkConfig.addOutboundPortDefinition("5701, 0, 63");
        Collection<Integer> outboundPorts = ioService.getOutboundPorts(EndpointQualifier.MEMBER);
        Assert.assertEquals(0, outboundPorts.size());
    }

    @Test
    public void testGetOutboundPorts_acceptsZero() {
        networkConfig.addOutboundPortDefinition("0");
        Collection<Integer> outboundPorts = ioService.getOutboundPorts(EndpointQualifier.MEMBER);
        Assert.assertEquals(0, outboundPorts.size());
    }

    @Test
    public void testGetOutboundPorts_acceptsWildcard() {
        networkConfig.addOutboundPortDefinition("*");
        Collection<Integer> outboundPorts = ioService.getOutboundPorts(EndpointQualifier.MEMBER);
        Assert.assertEquals(0, outboundPorts.size());
    }

    @Test
    public void testGetOutboundPorts_returnsEmptyCollectionByDefault() {
        Collection<Integer> outboundPorts = ioService.getOutboundPorts(EndpointQualifier.MEMBER);
        Assert.assertEquals(0, outboundPorts.size());
    }

    @Test
    public void testGetOutboundPorts_acceptsRange() {
        networkConfig.addOutboundPortDefinition("29000-29001");
        Collection<Integer> outboundPorts = ioService.getOutboundPorts(EndpointQualifier.MEMBER);
        Assert.assertThat(outboundPorts, Matchers.hasSize(2));
        Assert.assertThat(outboundPorts, Matchers.containsInAnyOrder(29000, 29001));
    }

    @Test
    public void testGetOutboundPorts_acceptsSpaceAfterComma() {
        networkConfig.addOutboundPortDefinition("29000, 29001");
        Collection<Integer> outboundPorts = ioService.getOutboundPorts(EndpointQualifier.MEMBER);
        Assert.assertThat(outboundPorts, Matchers.hasSize(2));
        Assert.assertThat(outboundPorts, Matchers.containsInAnyOrder(29000, 29001));
    }

    @Test
    public void testGetOutboundPorts_acceptsSpaceAsASeparator() {
        networkConfig.addOutboundPortDefinition("29000 29001");
        Collection<Integer> outboundPorts = ioService.getOutboundPorts(EndpointQualifier.MEMBER);
        Assert.assertThat(outboundPorts, Matchers.hasSize(2));
        Assert.assertThat(outboundPorts, Matchers.containsInAnyOrder(29000, 29001));
    }

    @Test
    public void testGetOutboundPorts_acceptsSemicolonAsASeparator() {
        networkConfig.addOutboundPortDefinition("29000;29001");
        Collection<Integer> outboundPorts = ioService.getOutboundPorts(EndpointQualifier.MEMBER);
        Assert.assertThat(outboundPorts, Matchers.hasSize(2));
        Assert.assertThat(outboundPorts, Matchers.containsInAnyOrder(29000, 29001));
    }
}

