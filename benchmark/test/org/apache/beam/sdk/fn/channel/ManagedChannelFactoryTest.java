/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.fn.channel;


import Endpoints.ApiServiceDescriptor;
import org.apache.beam.model.pipeline.v1.Endpoints;
import org.apache.beam.vendor.grpc.v1p13p1.io.grpc.ManagedChannel;
import org.apache.beam.vendor.grpc.v1p13p1.io.netty.channel.epoll.Epoll;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ManagedChannelFactory}.
 */
@RunWith(JUnit4.class)
public class ManagedChannelFactoryTest {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testDefaultChannel() {
        Endpoints.ApiServiceDescriptor apiServiceDescriptor = ApiServiceDescriptor.newBuilder().setUrl("localhost:123").build();
        ManagedChannel channel = ManagedChannelFactory.createDefault().forDescriptor(apiServiceDescriptor);
        Assert.assertEquals("localhost:123", channel.authority());
        channel.shutdownNow();
    }

    @Test
    public void testEpollHostPortChannel() {
        Assume.assumeTrue(Epoll.isAvailable());
        Endpoints.ApiServiceDescriptor apiServiceDescriptor = ApiServiceDescriptor.newBuilder().setUrl("localhost:123").build();
        ManagedChannel channel = ManagedChannelFactory.createEpoll().forDescriptor(apiServiceDescriptor);
        Assert.assertEquals("localhost:123", channel.authority());
        channel.shutdownNow();
    }

    @Test
    public void testEpollDomainSocketChannel() throws Exception {
        Assume.assumeTrue(Epoll.isAvailable());
        Endpoints.ApiServiceDescriptor apiServiceDescriptor = ApiServiceDescriptor.newBuilder().setUrl(("unix://" + (tmpFolder.newFile().getAbsolutePath()))).build();
        ManagedChannel channel = ManagedChannelFactory.createEpoll().forDescriptor(apiServiceDescriptor);
        Assert.assertEquals(apiServiceDescriptor.getUrl().substring("unix://".length()), channel.authority());
        channel.shutdownNow();
    }
}

