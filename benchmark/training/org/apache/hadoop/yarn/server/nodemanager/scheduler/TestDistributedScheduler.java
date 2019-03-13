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
package org.apache.hadoop.yarn.server.nodemanager.scheduler;


import ExecutionType.GUARANTEED;
import ExecutionType.OPPORTUNISTIC;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.DistributedSchedulingAllocateRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.DistributedSchedulingAllocateResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RemoteNode;
import org.apache.hadoop.yarn.server.nodemanager.amrmproxy.RequestInterceptor;
import org.apache.hadoop.yarn.util.Records;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Test cases for {@link DistributedScheduler}.
 */
public class TestDistributedScheduler {
    @Test
    public void testDistributedScheduler() throws Exception {
        Configuration conf = new Configuration();
        DistributedScheduler distributedScheduler = new DistributedScheduler();
        RequestInterceptor finalReqIntcptr = setup(conf, distributedScheduler);
        registerAM(distributedScheduler, finalReqIntcptr, Arrays.asList(RemoteNode.newInstance(NodeId.newInstance("a", 1), "http://a:1"), RemoteNode.newInstance(NodeId.newInstance("b", 2), "http://b:2"), RemoteNode.newInstance(NodeId.newInstance("c", 3), "http://c:3"), RemoteNode.newInstance(NodeId.newInstance("d", 4), "http://d:4")));
        final AtomicBoolean flipFlag = new AtomicBoolean(true);
        Mockito.when(finalReqIntcptr.allocateForDistributedScheduling(Mockito.any(DistributedSchedulingAllocateRequest.class))).thenAnswer(new Answer<DistributedSchedulingAllocateResponse>() {
            @Override
            public DistributedSchedulingAllocateResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                flipFlag.set((!(flipFlag.get())));
                if (flipFlag.get()) {
                    return createAllocateResponse(Arrays.asList(RemoteNode.newInstance(NodeId.newInstance("c", 3), "http://c:3"), RemoteNode.newInstance(NodeId.newInstance("d", 4), "http://d:4"), RemoteNode.newInstance(NodeId.newInstance("e", 5), "http://e:5"), RemoteNode.newInstance(NodeId.newInstance("f", 6), "http://f:6")));
                } else {
                    return createAllocateResponse(Arrays.asList(RemoteNode.newInstance(NodeId.newInstance("f", 6), "http://f:6"), RemoteNode.newInstance(NodeId.newInstance("e", 5), "http://e:5"), RemoteNode.newInstance(NodeId.newInstance("d", 4), "http://d:4"), RemoteNode.newInstance(NodeId.newInstance("c", 3), "http://c:3")));
                }
            }
        });
        AllocateRequest allocateRequest = Records.newRecord(AllocateRequest.class);
        ResourceRequest guaranteedReq = createResourceRequest(GUARANTEED, 5, "*");
        ResourceRequest opportunisticReq = createResourceRequest(OPPORTUNISTIC, 4, "*");
        allocateRequest.setAskList(Arrays.asList(guaranteedReq, opportunisticReq));
        // Verify 4 containers were allocated
        AllocateResponse allocateResponse = distributedScheduler.allocate(allocateRequest);
        Assert.assertEquals(4, allocateResponse.getAllocatedContainers().size());
        // Verify equal distribution on hosts a, b, c and d, and none on e / f
        // NOTE: No more than 1 container will be allocated on a node in the
        // top k list per allocate call.
        Map<NodeId, List<ContainerId>> allocs = mapAllocs(allocateResponse, 4);
        Assert.assertEquals(1, allocs.get(NodeId.newInstance("a", 1)).size());
        Assert.assertEquals(1, allocs.get(NodeId.newInstance("b", 2)).size());
        Assert.assertEquals(1, allocs.get(NodeId.newInstance("c", 3)).size());
        Assert.assertEquals(1, allocs.get(NodeId.newInstance("d", 4)).size());
        Assert.assertNull(allocs.get(NodeId.newInstance("e", 5)));
        Assert.assertNull(allocs.get(NodeId.newInstance("f", 6)));
        // New Allocate request
        allocateRequest = Records.newRecord(AllocateRequest.class);
        opportunisticReq = createResourceRequest(OPPORTUNISTIC, 4, "*");
        allocateRequest.setAskList(Arrays.asList(guaranteedReq, opportunisticReq));
        // Verify 4 containers were allocated
        allocateResponse = distributedScheduler.allocate(allocateRequest);
        Assert.assertEquals(4, allocateResponse.getAllocatedContainers().size());
        // Verify new containers are equally distribution on hosts c and d,
        // and none on a or b
        allocs = mapAllocs(allocateResponse, 4);
        Assert.assertEquals(1, allocs.get(NodeId.newInstance("c", 3)).size());
        Assert.assertEquals(1, allocs.get(NodeId.newInstance("d", 4)).size());
        Assert.assertEquals(1, allocs.get(NodeId.newInstance("e", 5)).size());
        Assert.assertEquals(1, allocs.get(NodeId.newInstance("f", 6)).size());
        Assert.assertNull(allocs.get(NodeId.newInstance("a", 1)));
        Assert.assertNull(allocs.get(NodeId.newInstance("b", 2)));
        // Ensure the DistributedScheduler respects the list order..
        // The first request should be allocated to "c" since it is ranked higher
        // The second request should be allocated to "f" since the ranking is
        // flipped on every allocate response.
        allocateRequest = Records.newRecord(AllocateRequest.class);
        opportunisticReq = createResourceRequest(OPPORTUNISTIC, 1, "*");
        allocateRequest.setAskList(Arrays.asList(guaranteedReq, opportunisticReq));
        allocateResponse = distributedScheduler.allocate(allocateRequest);
        allocs = mapAllocs(allocateResponse, 1);
        Assert.assertEquals(1, allocs.get(NodeId.newInstance("c", 3)).size());
        allocateRequest = Records.newRecord(AllocateRequest.class);
        opportunisticReq = createResourceRequest(OPPORTUNISTIC, 1, "*");
        allocateRequest.setAskList(Arrays.asList(guaranteedReq, opportunisticReq));
        allocateResponse = distributedScheduler.allocate(allocateRequest);
        allocs = mapAllocs(allocateResponse, 1);
        Assert.assertEquals(1, allocs.get(NodeId.newInstance("f", 6)).size());
        allocateRequest = Records.newRecord(AllocateRequest.class);
        opportunisticReq = createResourceRequest(OPPORTUNISTIC, 1, "*");
        allocateRequest.setAskList(Arrays.asList(guaranteedReq, opportunisticReq));
        allocateResponse = distributedScheduler.allocate(allocateRequest);
        allocs = mapAllocs(allocateResponse, 1);
        Assert.assertEquals(1, allocs.get(NodeId.newInstance("c", 3)).size());
    }
}

