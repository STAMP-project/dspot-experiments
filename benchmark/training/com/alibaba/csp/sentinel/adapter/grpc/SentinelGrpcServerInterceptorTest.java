/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.adapter.grpc;


import EntryType.IN;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link SentinelGrpcServerInterceptor}.
 *
 * @author Eric Zhao
 */
public class SentinelGrpcServerInterceptorTest {
    private final String resourceName = "com.alibaba.sentinel.examples.FooService/anotherHello";

    private final int threshold = 4;

    private final GrpcTestServer server = new GrpcTestServer();

    private FooServiceClient client;

    @Test
    public void testGrpcServerInterceptor() throws Exception {
        final int port = 19329;
        client = new FooServiceClient("localhost", port);
        configureFlowRule(threshold);
        server.start(port, true);
        Assert.assertTrue(sendRequest());
        ClusterNode clusterNode = ClusterBuilderSlot.getClusterNode(resourceName, IN);
        Assert.assertNotNull(clusterNode);
        Assert.assertEquals(1, ((clusterNode.totalRequest()) - (clusterNode.blockRequest())));
        // Not allowed to pass.
        configureFlowRule(0);
        // The second request will be blocked.
        Assert.assertFalse(sendRequest());
        Assert.assertEquals(1, clusterNode.blockRequest());
        server.stop();
    }
}

