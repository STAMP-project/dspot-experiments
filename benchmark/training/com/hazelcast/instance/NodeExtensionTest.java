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
package com.hazelcast.instance;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NodeExtensionTest extends HazelcastTestSupport {
    private HazelcastInstanceImpl hazelcastInstance;

    @Test
    public void verifyMethods() throws Exception {
        TestNodeContext nodeContext = new TestNodeContext();
        NodeExtension nodeExtension = nodeContext.getNodeExtension();
        hazelcastInstance = new HazelcastInstanceImpl(HazelcastTestSupport.randomName(), getConfig(), nodeContext);
        InOrder inOrder = Mockito.inOrder(nodeExtension);
        inOrder.verify(nodeExtension, Mockito.times(1)).printNodeInfo();
        inOrder.verify(nodeExtension, Mockito.times(1)).beforeStart();
        inOrder.verify(nodeExtension, Mockito.times(1)).createSerializationService();
        inOrder.verify(nodeExtension, Mockito.times(1)).createExtensionServices();
        inOrder.verify(nodeExtension, Mockito.times(1)).beforeJoin();
        inOrder.verify(nodeExtension, Mockito.times(1)).afterStart();
        hazelcastInstance.shutdown();
        inOrder.verify(nodeExtension, Mockito.times(1)).beforeShutdown();
        inOrder.verify(nodeExtension, Mockito.times(1)).shutdown();
    }
}

