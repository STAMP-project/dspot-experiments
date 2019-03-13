/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.server.hash;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.zk.gen.OperationsNodeInfo;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;


public class ConsistentHashResolverTest {
    @Test(expected = RuntimeException.class)
    public void getNodeForNullUserIdTest() {
        ConsistentHashResolver consistentHashResolver = new ConsistentHashResolver(new ArrayList<OperationsNodeInfo>(), 5);
        consistentHashResolver.getNode(null);
    }

    @Test
    public void getNodeForEmptyCircleTest() {
        ConsistentHashResolver consistentHashResolver = new ConsistentHashResolver(new ArrayList<OperationsNodeInfo>(), 5);
        OperationsNodeInfo info = consistentHashResolver.getNode("userId");
        Assert.assertNull(info);
    }

    @Test
    public void getNodeForOneItemCircleTest() {
        List<OperationsNodeInfo> nodes = createNodeListWithOneNode();
        ConsistentHashResolver consistentHashResolver = new ConsistentHashResolver(nodes, 1);
        OperationsNodeInfo returnedNode = consistentHashResolver.getNode("userId");
        Assert.assertEquals(nodes.get(0), returnedNode);
    }

    @Test
    public void getNodeForMultipleItemsTest() {
        List<OperationsNodeInfo> nodes = createNodeListWithThreeItems();
        ConsistentHashResolver consistentHashResolver = new ConsistentHashResolver(nodes, 2);
        OperationsNodeInfo returnedNode = consistentHashResolver.getNode("aa");
        // operations node info 1 should be returned
        Assert.assertEquals(nodes.get(0), returnedNode);
    }

    @Test
    public void getNodeForMultipleItemsEmptyTailMap() {
        List<OperationsNodeInfo> nodes = createNodeListWithThreeItems();
        ConsistentHashResolver consistentHashResolver = new ConsistentHashResolver(nodes, 2);
        OperationsNodeInfo returnedNode = consistentHashResolver.getNode("aaaa");
        // operations node info 3 should be returned
        Assert.assertEquals(nodes.get(2), returnedNode);
    }

    @Test
    public void onNodeRemovedTest() throws NoSuchFieldException {
        int t = 10;
        List<OperationsNodeInfo> nodes = createNodeListWithOneNode();
        ConsistentHashResolver consistentHashResolver = new ConsistentHashResolver(nodes, t);
        SortedMap circle = Mockito.mock(SortedMap.class);
        ReflectionTestUtils.setField(consistentHashResolver, "circle", circle);
        consistentHashResolver.onNodeUpdated(nodes.get(0));
        // verify that remove was called t times
        Mockito.verify(circle, Mockito.times(t)).remove(ArgumentMatchers.any());
    }
}

