/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.controller.tasks;


import ConnectableType.FUNNEL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.nifi.connectable.Connection;
import org.apache.nifi.connectable.Funnel;
import org.apache.nifi.controller.ProcessorNode;
import org.apache.nifi.controller.queue.FlowFileQueue;
import org.apache.nifi.processor.Processor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestConnectableTask {
    @Test
    public void testIsWorkToDo() {
        final ProcessorNode procNode = Mockito.mock(ProcessorNode.class);
        Mockito.when(procNode.hasIncomingConnection()).thenReturn(false);
        final Processor processor = Mockito.mock(Processor.class);
        Mockito.when(procNode.getIdentifier()).thenReturn("123");
        Mockito.when(procNode.getRunnableComponent()).thenReturn(processor);
        // There is work to do because there are no incoming connections.
        final ConnectableTask task = createTask(procNode);
        Assert.assertFalse(task.invoke().isYield());
        // Test with only a single connection that is self-looping and empty
        final Connection selfLoopingConnection = Mockito.mock(Connection.class);
        Mockito.when(selfLoopingConnection.getSource()).thenReturn(procNode);
        Mockito.when(selfLoopingConnection.getDestination()).thenReturn(procNode);
        Mockito.when(procNode.hasIncomingConnection()).thenReturn(true);
        Mockito.when(procNode.getIncomingConnections()).thenReturn(Collections.singletonList(selfLoopingConnection));
        Assert.assertFalse(task.invoke().isYield());
        // Test with only a single connection that is self-looping and empty
        final FlowFileQueue flowFileQueue = Mockito.mock(FlowFileQueue.class);
        Mockito.when(flowFileQueue.isActiveQueueEmpty()).thenReturn(true);
        final FlowFileQueue nonEmptyQueue = Mockito.mock(FlowFileQueue.class);
        Mockito.when(nonEmptyQueue.isActiveQueueEmpty()).thenReturn(false);
        Mockito.when(selfLoopingConnection.getFlowFileQueue()).thenReturn(nonEmptyQueue);
        Assert.assertFalse(task.invoke().isYield());
        // Test with only a non-looping Connection that has no FlowFiles
        final Connection emptyConnection = Mockito.mock(Connection.class);
        Mockito.when(emptyConnection.getSource()).thenReturn(Mockito.mock(ProcessorNode.class));
        Mockito.when(emptyConnection.getDestination()).thenReturn(procNode);
        Mockito.when(emptyConnection.getFlowFileQueue()).thenReturn(flowFileQueue);
        Mockito.when(procNode.getIncomingConnections()).thenReturn(Collections.singletonList(emptyConnection));
        Assert.assertTrue(task.invoke().isYield());
        // test when the queue has data
        final Connection nonEmptyConnection = Mockito.mock(Connection.class);
        Mockito.when(nonEmptyConnection.getSource()).thenReturn(Mockito.mock(ProcessorNode.class));
        Mockito.when(nonEmptyConnection.getDestination()).thenReturn(procNode);
        Mockito.when(nonEmptyConnection.getFlowFileQueue()).thenReturn(nonEmptyQueue);
        Mockito.when(procNode.getIncomingConnections()).thenReturn(Collections.singletonList(nonEmptyConnection));
        Assert.assertFalse(task.invoke().isYield());
    }

    @Test
    public void testIsWorkToDoFunnels() {
        final Funnel funnel = Mockito.mock(Funnel.class);
        Mockito.when(funnel.hasIncomingConnection()).thenReturn(false);
        Mockito.when(funnel.getRunnableComponent()).thenReturn(funnel);
        Mockito.when(funnel.getConnectableType()).thenReturn(FUNNEL);
        Mockito.when(funnel.getIdentifier()).thenReturn("funnel-1");
        final ConnectableTask task = createTask(funnel);
        Assert.assertTrue("If there is no incoming connection, it should be yielded.", task.invoke().isYield());
        // Test with only a single connection that is self-looping and empty.
        // Actually, this self-loop input can not be created for Funnels using NiFi API because an outer layer check condition does not allow it.
        // But test it anyways.
        final Connection selfLoopingConnection = Mockito.mock(Connection.class);
        Mockito.when(selfLoopingConnection.getSource()).thenReturn(funnel);
        Mockito.when(selfLoopingConnection.getDestination()).thenReturn(funnel);
        Mockito.when(funnel.hasIncomingConnection()).thenReturn(true);
        Mockito.when(funnel.getIncomingConnections()).thenReturn(Collections.singletonList(selfLoopingConnection));
        final FlowFileQueue emptyQueue = Mockito.mock(FlowFileQueue.class);
        Mockito.when(emptyQueue.isActiveQueueEmpty()).thenReturn(true);
        Mockito.when(selfLoopingConnection.getFlowFileQueue()).thenReturn(emptyQueue);
        final Set<Connection> outgoingConnections = new HashSet<>();
        outgoingConnections.add(selfLoopingConnection);
        Mockito.when(funnel.getConnections()).thenReturn(outgoingConnections);
        Assert.assertTrue("If there is no incoming connection from other components, it should be yielded.", task.invoke().isYield());
        // Add an incoming connection from another component.
        final ProcessorNode inputProcessor = Mockito.mock(ProcessorNode.class);
        final Connection incomingFromAnotherComponent = Mockito.mock(Connection.class);
        Mockito.when(incomingFromAnotherComponent.getSource()).thenReturn(inputProcessor);
        Mockito.when(incomingFromAnotherComponent.getDestination()).thenReturn(funnel);
        Mockito.when(incomingFromAnotherComponent.getFlowFileQueue()).thenReturn(emptyQueue);
        Mockito.when(funnel.hasIncomingConnection()).thenReturn(true);
        Mockito.when(funnel.getIncomingConnections()).thenReturn(Arrays.asList(selfLoopingConnection, incomingFromAnotherComponent));
        Assert.assertTrue(("Even if there is an incoming connection from another component," + " it should be yielded because there's no outgoing connections."), task.invoke().isYield());
        // Add an outgoing connection to another component.
        final ProcessorNode outputProcessor = Mockito.mock(ProcessorNode.class);
        final Connection outgoingToAnotherComponent = Mockito.mock(Connection.class);
        Mockito.when(outgoingToAnotherComponent.getSource()).thenReturn(funnel);
        Mockito.when(outgoingToAnotherComponent.getDestination()).thenReturn(outputProcessor);
        outgoingConnections.add(outgoingToAnotherComponent);
        Assert.assertTrue(("Even if there is an incoming connection from another component and an outgoing connection as well," + " it should be yielded because there's no incoming FlowFiles to process."), task.invoke().isYield());
        // Adding input FlowFiles.
        final FlowFileQueue nonEmptyQueue = Mockito.mock(FlowFileQueue.class);
        Mockito.when(nonEmptyQueue.isActiveQueueEmpty()).thenReturn(false);
        Mockito.when(incomingFromAnotherComponent.getFlowFileQueue()).thenReturn(nonEmptyQueue);
        Assert.assertFalse("When a Funnel has both incoming and outgoing connections and FlowFiles to process, then it should be executed.", task.invoke().isYield());
    }
}

