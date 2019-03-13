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
import Priority.LEVEL1;
import Priority.LEVEL10;
import Priority.LEVEL2;
import Priority.LEVEL3;
import Priority.LEVEL4;
import Priority.LEVEL5;
import Priority.LEVEL6;
import Priority.LEVEL7;
import Priority.LEVEL8;
import Priority.LEVEL9;
import com.liferay.portal.kernel.cluster.Address;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.test.rule.NewEnvTestRule;
import com.liferay.portal.kernel.util.ObjectValuePair;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static ClusterLinkImpl.MAX_CHANNEL_COUNT;


/**
 *
 *
 * @author Tina Tian
 * @author Shuyang Zhou
 */
@NewEnv(type = Type.CLASSLOADER)
public class ClusterLinkImplTest extends BaseClusterTestCase {
    @Test
    public void testDeactivate() {
        ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl(true, 1);
        List<TestClusterChannel> clusterChannels = TestClusterChannel.getClusterChannels();
        Assert.assertEquals(clusterChannels.toString(), 1, clusterChannels.size());
        TestClusterChannel clusterChannel = clusterChannels.get(0);
        ExecutorService executorService = clusterLinkImpl.getExecutorService();
        Assert.assertFalse(clusterChannel.isClosed());
        Assert.assertFalse(executorService.isShutdown());
        clusterLinkImpl.deactivate();
        Assert.assertTrue(clusterChannel.isClosed());
        Assert.assertTrue(executorService.isShutdown());
    }

    @Test
    public void testDisabledClusterLink() {
        // Test 1, initialize
        ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl(false, 1);
        List<TestClusterChannel> clusterChannels = TestClusterChannel.getClusterChannels();
        Assert.assertTrue(clusterChannels.toString(), clusterChannels.isEmpty());
        Assert.assertNull(clusterLinkImpl.getExecutorService());
        // Test 2, send unicast message
        List<Serializable> multicastMessages = TestClusterChannel.getMulticastMessages();
        List<ObjectValuePair<Serializable, Address>> unicastMessages = TestClusterChannel.getUnicastMessages();
        Message message = new Message();
        Address address = new TestAddress((-1));
        clusterLinkImpl.sendUnicastMessage(address, message, LEVEL1);
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.isEmpty());
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
        // Test 3, send multicast message
        clusterLinkImpl.sendMulticastMessage(message, LEVEL1);
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.isEmpty());
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
        // Test 4, destroy
        clusterLinkImpl.deactivate();
    }

    @Test
    public void testGetChannel() {
        ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl(true, 2);
        ClusterChannel clusterChannel1 = clusterLinkImpl.getChannel(LEVEL1);
        Assert.assertSame(clusterChannel1, clusterLinkImpl.getChannel(LEVEL2));
        Assert.assertSame(clusterChannel1, clusterLinkImpl.getChannel(LEVEL3));
        Assert.assertSame(clusterChannel1, clusterLinkImpl.getChannel(LEVEL4));
        Assert.assertSame(clusterChannel1, clusterLinkImpl.getChannel(LEVEL5));
        ClusterChannel clusterChannel2 = clusterLinkImpl.getChannel(LEVEL6);
        Assert.assertSame(clusterChannel2, clusterLinkImpl.getChannel(LEVEL7));
        Assert.assertSame(clusterChannel2, clusterLinkImpl.getChannel(LEVEL8));
        Assert.assertSame(clusterChannel2, clusterLinkImpl.getChannel(LEVEL9));
        Assert.assertSame(clusterChannel2, clusterLinkImpl.getChannel(LEVEL10));
        List<TestClusterChannel> clusterChannels = TestClusterChannel.getClusterChannels();
        Assert.assertEquals(clusterChannels.toString(), 2, clusterChannels.size());
        Assert.assertNotEquals(clusterChannel1, clusterChannel2);
        Assert.assertTrue(clusterChannels.toString(), clusterChannels.contains(clusterChannel1));
        Assert.assertTrue(clusterChannels.toString(), clusterChannels.contains(clusterChannel2));
    }

    @Test
    public void testInitChannels() {
        try (CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(ClusterLinkImpl.class.getName(), Level.OFF)) {
            // Test 1, create ClusterLinkImpl#MAX_CHANNEL_COUNT channels
            List<LogRecord> logRecords = captureHandler.getLogRecords();
            try {
                getClusterLinkImpl(true, ((MAX_CHANNEL_COUNT) + 1));
                Assert.fail();
            } catch (IllegalStateException ise) {
                Assert.assertEquals(logRecords.toString(), 0, logRecords.size());
                Assert.assertEquals((("java.lang.IllegalArgumentException: Channel count must " + "be between 1 and ") + (MAX_CHANNEL_COUNT)), ise.getMessage());
            }
            // Test 2, create 0 channels
            logRecords = captureHandler.resetLogLevel(Level.SEVERE);
            try {
                getClusterLinkImpl(true, 0);
                Assert.fail();
            } catch (IllegalStateException ise) {
                Assert.assertEquals(logRecords.toString(), 1, logRecords.size());
                LogRecord logRecord = logRecords.get(0);
                Assert.assertEquals("Unable to initialize channels", logRecord.getMessage());
                Assert.assertEquals((("java.lang.IllegalArgumentException: Channel count must " + "be between 1 and ") + (MAX_CHANNEL_COUNT)), ise.getMessage());
            }
        }
    }

    @Test
    public void testInitialize() {
        ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl(true, 2);
        Assert.assertNotNull(clusterLinkImpl.getExecutorService());
        List<TestClusterChannel> clusterChannels = TestClusterChannel.getClusterChannels();
        Assert.assertEquals(clusterChannels.toString(), 2, clusterChannels.size());
        for (TestClusterChannel clusterChannel : clusterChannels) {
            Assert.assertFalse(clusterChannel.isClosed());
            ClusterReceiver clusterReceiver = clusterChannel.getClusterReceiver();
            CountDownLatch countDownLatch = ReflectionTestUtil.getFieldValue(clusterReceiver, "_countDownLatch");
            Assert.assertEquals(0, countDownLatch.getCount());
        }
    }

    @Test
    public void testSendMulticastMessage() {
        ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl(true, 1);
        List<Serializable> multicastMessages = TestClusterChannel.getMulticastMessages();
        List<ObjectValuePair<Serializable, Address>> unicastMessages = TestClusterChannel.getUnicastMessages();
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.isEmpty());
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
        Message message = new Message();
        clusterLinkImpl.sendMulticastMessage(message, LEVEL1);
        Assert.assertEquals(multicastMessages.toString(), 1, multicastMessages.size());
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.contains(message));
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
    }

    @Test
    public void testSendUnicastMessage() {
        ClusterLinkImpl clusterLinkImpl = getClusterLinkImpl(true, 1);
        List<Serializable> multicastMessages = TestClusterChannel.getMulticastMessages();
        List<ObjectValuePair<Serializable, Address>> unicastMessages = TestClusterChannel.getUnicastMessages();
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.isEmpty());
        Assert.assertTrue(unicastMessages.toString(), unicastMessages.isEmpty());
        Message message = new Message();
        Address address = new TestAddress((-1));
        clusterLinkImpl.sendUnicastMessage(address, message, LEVEL1);
        Assert.assertTrue(multicastMessages.toString(), multicastMessages.isEmpty());
        Assert.assertEquals(unicastMessages.toString(), 1, unicastMessages.size());
        ObjectValuePair<Serializable, Address> unicastMessage = unicastMessages.get(0);
        Assert.assertSame(message, unicastMessage.getKey());
        Assert.assertSame(address, unicastMessage.getValue());
    }

    @Rule
    public final NewEnvTestRule newEnvTestRule = NewEnvTestRule.INSTANCE;
}

