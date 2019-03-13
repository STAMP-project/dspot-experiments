/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.cluster.multiple.internal;


import NewEnv.Type;
import StringPool.BLANK;
import com.liferay.portal.cluster.multiple.configuration.ClusterExecutorConfiguration;
import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.kernel.cluster.ClusterEvent;
import com.liferay.portal.kernel.cluster.ClusterEventListener;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.cluster.ClusterNodeResponse;
import com.liferay.portal.kernel.cluster.ClusterNodeResponses;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.cluster.FutureClusterResponses;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.ObjectValuePair;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Tina Tian
 */
@NewEnv(type = Type.CLASSLOADER)
public class ClusterExecutorImplTest extends BaseClusterTestCase {
    @Test
    public void testClusterEventListener() {
        // Test 1, add cluster event listener
        ClusterExecutorImpl clusterExecutorImpl = getClusterExecutorImpl(true);
        List<ClusterEventListener> clusterEventListeners = clusterExecutorImpl.getClusterEventListeners();
        Assert.assertEquals(clusterEventListeners.toString(), 0, clusterEventListeners.size());
        ClusterEventListener clusterEventListener = new ClusterEventListener() {
            @Override
            public void processClusterEvent(ClusterEvent clusterEvent) {
            }
        };
        clusterExecutorImpl.addClusterEventListener(clusterEventListener);
        clusterEventListeners = clusterExecutorImpl.getClusterEventListeners();
        Assert.assertEquals(clusterEventListeners.toString(), 1, clusterEventListeners.size());
        // Test 2, remove cluster event listener
        clusterExecutorImpl.removeClusterEventListener(clusterEventListener);
        clusterEventListeners = clusterExecutorImpl.getClusterEventListeners();
        Assert.assertEquals(clusterEventListeners.toString(), 0, clusterEventListeners.size());
        // Test 3, set cluster event listener
        clusterEventListeners = new ArrayList();
        clusterEventListeners.add(clusterEventListener);
        clusterExecutorImpl.setClusterEventListeners(clusterEventListeners);
        clusterEventListeners = clusterExecutorImpl.getClusterEventListeners();
        Assert.assertEquals(clusterEventListeners.toString(), 1, clusterEventListeners.size());
    }

    @Test
    public void testDeactivate() {
        ClusterExecutorImpl clusterExecutorImpl = getClusterExecutorImpl(true);
        List<TestClusterChannel> clusterChannels = TestClusterChannel.getClusterChannels();
        Assert.assertEquals(clusterChannels.toString(), 1, clusterChannels.size());
        TestClusterChannel clusterChannel = clusterChannels.get(0);
        ExecutorService executorService = clusterExecutorImpl.getExecutorService();
        Assert.assertFalse(executorService.isShutdown());
        Assert.assertFalse(clusterChannel.isClosed());
        clusterExecutorImpl.deactivate();
        Assert.assertTrue(clusterChannel.isClosed());
        Assert.assertTrue(executorService.isShutdown());
    }

    @Test
    public void testDebugClusterEventListener() {
        ClusterExecutorImpl clusterExecutorImpl = getClusterExecutorImpl(true);
        clusterExecutorImpl.clusterExecutorConfiguration = new ClusterExecutorConfiguration() {
            @Override
            public long clusterNodeAddressTimeout() {
                return 100;
            }

            @Override
            public boolean debugEnabled() {
                return true;
            }

            @Override
            public String[] excludedPropertyKeys() {
                return new String[]{ "access_key", "connection_password", "connection_username", "secret_access_key" };
            }
        };
        clusterExecutorImpl.manageDebugClusterEventListener();
        List<ClusterEventListener> clusterEventListeners = clusterExecutorImpl.getClusterEventListeners();
        Assert.assertEquals(clusterEventListeners.toString(), 1, clusterEventListeners.size());
        ClusterEventListener clusterEventListener = clusterEventListeners.get(0);
        Class<?> clusterEventListenerClass = clusterEventListener.getClass();
        Assert.assertEquals(DebuggingClusterEventListenerImpl.class.getName(), clusterEventListenerClass.getName());
    }

    @Test
    public void testDisabledClusterLink() {
        // Test 1, initialize
        ClusterExecutorImpl clusterExecutorImpl = getClusterExecutorImpl(false);
        List<TestClusterChannel> clusterChannels = TestClusterChannel.getClusterChannels();
        Assert.assertTrue(clusterChannels.toString(), clusterChannels.isEmpty());
        Assert.assertNull(clusterExecutorImpl.getExecutorService());
        // Test 2, send unitcast message
        List<Serializable> multicastMessages = TestClusterChannel.getMulticastMessages();
        List<ObjectValuePair<Serializable, Address>> unicastMessages = TestClusterChannel.getUnicastMessages();
        clusterExecutorImpl.execute(ClusterRequest.createUnicastRequest(BLANK, BLANK));
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.isEmpty());
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
        // Test 3, send multicast message
        clusterExecutorImpl.execute(ClusterRequest.createMulticastRequest(BLANK));
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.isEmpty());
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
        // Test 4, destroy
        clusterExecutorImpl.deactivate();
    }

    @Test
    public void testExecute() throws Exception {
        // Test 1, execute multicast request and not skip local
        ClusterExecutorImpl clusterExecutorImpl = getClusterExecutorImpl(true);
        TestClusterChannel.clearAllMessages();
        List<Serializable> multicastMessages = TestClusterChannel.getMulticastMessages();
        List<ObjectValuePair<Serializable, Address>> unicastMessages = TestClusterChannel.getUnicastMessages();
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.isEmpty());
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
        ClusterRequest clusterRequest = ClusterRequest.createMulticastRequest(BLANK);
        FutureClusterResponses futureClusterResponses = clusterExecutorImpl.execute(clusterRequest);
        Assert.assertEquals(multicastMessages.toString(), 1, multicastMessages.size());
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.contains(clusterRequest));
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
        ClusterNodeResponses clusterNodeResponses = futureClusterResponses.get();
        Assert.assertEquals(1, clusterNodeResponses.size());
        // Test 2, execute multicast request and skip local
        TestClusterChannel.clearAllMessages();
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.isEmpty());
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
        clusterRequest = ClusterRequest.createMulticastRequest(BLANK, true);
        futureClusterResponses = clusterExecutorImpl.execute(clusterRequest);
        Assert.assertEquals(multicastMessages.toString(), 1, multicastMessages.size());
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.contains(clusterRequest));
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
        clusterNodeResponses = futureClusterResponses.get();
        Assert.assertEquals(0, clusterNodeResponses.size());
        // Test 3, execute unicast request to local address
        TestClusterChannel.clearAllMessages();
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.isEmpty());
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
        ClusterNode localClusterNode = clusterExecutorImpl.getLocalClusterNode();
        clusterRequest = ClusterRequest.createUnicastRequest(clusterRequest, localClusterNode.getClusterNodeId());
        futureClusterResponses = clusterExecutorImpl.execute(clusterRequest);
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.isEmpty());
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
        clusterNodeResponses = futureClusterResponses.get();
        Assert.assertEquals(1, clusterNodeResponses.size());
        // Test 4, execute unicast request to other address
        TestClusterChannel.clearAllMessages();
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.isEmpty());
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
        ClusterExecutorImpl newClusterExecutorImpl = getClusterExecutorImpl(true);
        Assert.assertEquals(multicastMessages.toString(), 1, multicastMessages.size());
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
        Serializable serializable = multicastMessages.get(0);
        clusterExecutorImpl.handleReceivedClusterRequest(((ClusterRequest) (serializable)));
        TestClusterChannel.clearAllMessages();
        ClusterNode newClusterNode = newClusterExecutorImpl.getLocalClusterNode();
        clusterRequest = ClusterRequest.createUnicastRequest(BLANK, newClusterNode.getClusterNodeId());
        clusterExecutorImpl.execute(clusterRequest);
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.isEmpty());
        Assert.assertEquals(unicastMessages.toString(), 1, unicastMessages.size());
        ObjectValuePair<Serializable, Address> receivedMessage = unicastMessages.get(0);
        Assert.assertEquals(clusterRequest, receivedMessage.getKey());
    }

    @Test
    public void testExecuteClusterRequest() throws Exception {
        ClusterExecutorImpl clusterExecutorImpl = getClusterExecutorImpl(true);
        // Test 1, payload is not method handler
        ClusterNodeResponse clusterNodeResponse = clusterExecutorImpl.executeClusterRequest(ClusterRequest.createMulticastRequest(BLANK));
        Exception exception = clusterNodeResponse.getException();
        Assert.assertEquals(("Payload is not of type " + (MethodHandler.class.getName())), exception.getMessage());
        // Test 2, invoke with exception
        String timestamp = String.valueOf(System.currentTimeMillis());
        clusterNodeResponse = clusterExecutorImpl.executeClusterRequest(ClusterRequest.createMulticastRequest(new MethodHandler(new MethodKey(TestBean.class, "testMethod3", String.class), timestamp)));
        try {
            clusterNodeResponse.getResult();
            Assert.fail();
        } catch (Exception e) {
            Throwable throwable = e.getCause();
            Assert.assertEquals(timestamp, throwable.getMessage());
        }
        // Test 3, invoke without exception
        timestamp = String.valueOf(System.currentTimeMillis());
        clusterNodeResponse = clusterExecutorImpl.executeClusterRequest(ClusterRequest.createMulticastRequest(new MethodHandler(new MethodKey(TestBean.class, "testMethod1", String.class), timestamp)));
        Assert.assertEquals(timestamp, clusterNodeResponse.getResult());
        // Test 4, thread local
        Assert.assertTrue(ClusterInvokeThreadLocal.isEnabled());
        clusterNodeResponse = clusterExecutorImpl.executeClusterRequest(ClusterRequest.createMulticastRequest(new MethodHandler(new MethodKey(TestBean.class, "testMethod5"))));
        Assert.assertFalse(((Boolean) (clusterNodeResponse.getResult())));
        Assert.assertTrue(ClusterInvokeThreadLocal.isEnabled());
    }

    @Rule
    public final NewEnvTestRule newEnvTestRule = NewEnvTestRule.INSTANCE;
}

