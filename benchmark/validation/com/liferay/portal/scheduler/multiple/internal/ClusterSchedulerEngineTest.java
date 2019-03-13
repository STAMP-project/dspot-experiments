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
package com.liferay.portal.scheduler.multiple.internal;


import DestinationNames.SCHEDULER_DISPATCH;
import NewEnv.Type;
import SchedulerEngine.JOB_STATE;
import StorageType.MEMORY_CLUSTERED;
import StorageType.PERSISTED;
import StringPool.BLANK;
import TriggerState.NORMAL;
import TriggerState.PAUSED;
import TriggerState.UNSCHEDULED;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cluster.ClusterEventListener;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterInvokeAcceptor;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.cluster.ClusterMasterTokenTransitionListener;
import com.liferay.portal.kernel.cluster.ClusterNode;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.cluster.FutureClusterResponses;
import com.liferay.portal.kernel.concurrent.DefaultNoticeableFuture;
import com.liferay.portal.kernel.concurrent.NoticeableFuture;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelperUtil;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.scheduler.TriggerState;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.servlet.PluginContextLifecycleThreadLocal;
import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.test.rule.AdviseWith;
import com.liferay.portal.test.rule.AspectJNewEnvTestRule;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Tina Tian
 */
@NewEnv(type = Type.CLASSLOADER)
public class ClusterSchedulerEngineTest {
    @AdviseWith(adviceClasses = ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.class)
    @Test
    public void testDeleteOnMaster() throws SchedulerException {
        // Test 1, MEMORY_CLUSTERED job by jobName and groupName
        _mockClusterMasterExecutor.reset(true, 0, 0);
        _mockSchedulerEngine.resetJobs(4, 4);
        _clusterSchedulerEngine.start();
        List<SchedulerResponse> schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
        Assert.assertEquals(schedulerResponses.toString(), 4, schedulerResponses.size());
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        _clusterSchedulerEngine.delete(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
        Assert.assertEquals(schedulerResponses.toString(), 3, schedulerResponses.size());
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 2, MEMORY_CLUSTERED jobs by groupName
        _clusterSchedulerEngine.delete(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 3, PERSISTED job by jobName and groupName
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(PERSISTED);
        Assert.assertEquals(schedulerResponses.toString(), 4, schedulerResponses.size());
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        _clusterSchedulerEngine.delete(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(PERSISTED);
        Assert.assertEquals(schedulerResponses.toString(), 3, schedulerResponses.size());
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 4, PERSISTED jobs by groupName
        _clusterSchedulerEngine.delete(ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(PERSISTED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
    }

    @AdviseWith(adviceClasses = ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.class)
    @Test
    public void testDeleteOnSlave() throws SchedulerException {
        // Test 1, MEMORY_CLUSTERED job by jobName and groupName
        _mockClusterMasterExecutor.reset(false, 4, 0);
        _mockSchedulerEngine.resetJobs(0, 4);
        _clusterSchedulerEngine.start();
        List<SchedulerResponse> schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        Assert.assertEquals(_memoryClusteredJobs.toString(), 4, _memoryClusteredJobs.size());
        _clusterSchedulerEngine.delete(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        Assert.assertEquals(_memoryClusteredJobs.toString(), 3, _memoryClusteredJobs.size());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 2, not existed group
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME, MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        schedulerResponses = getMemoryClusteredJobs(ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        _clusterSchedulerEngine.delete(ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME, MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        schedulerResponses = getMemoryClusteredJobs(ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 3, MEMORY_CLUSTERED jobs by groupName
        _clusterSchedulerEngine.delete(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
    }

    @Test
    public void testGetSchedulerJobs() throws SchedulerException {
        _mockClusterMasterExecutor.reset(true, 0, 0);
        _mockSchedulerEngine.resetJobs(4, 2);
        _clusterSchedulerEngine.start();
        List<SchedulerResponse> schedulerResponses = _clusterSchedulerEngine.getScheduledJobs();
        Assert.assertEquals(schedulerResponses.toString(), 6, schedulerResponses.size());
    }

    @Test
    public void testMasterToSlave() throws SchedulerException {
        // Test 1, with log disabled
        _mockClusterMasterExecutor.reset(true, 0, 0);
        _mockSchedulerEngine.resetJobs(4, 2);
        _clusterSchedulerEngine.start();
        Assert.assertTrue(_mockClusterMasterExecutor.isMaster());
        List<SchedulerResponse> schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
        Assert.assertEquals(schedulerResponses.toString(), 4, schedulerResponses.size());
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        try (CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(ClusterSchedulerEngine.class.getName(), Level.OFF)) {
            _mockClusterMasterExecutor.reset(false, 4, 2);
            ClusterMasterTokenTransitionListener clusterMasterTokenTransitionListener = _mockClusterMasterExecutor.getClusterMasterTokenTransitionListener();
            clusterMasterTokenTransitionListener.masterTokenReleased();
            List<LogRecord> logRecords = captureHandler.getLogRecords();
            Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());
            Assert.assertFalse(_mockClusterMasterExecutor.isMaster());
            schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
            Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
            Assert.assertEquals(_memoryClusteredJobs.toString(), 4, _memoryClusteredJobs.size());
            // Test 2, with log enabled
            _mockClusterMasterExecutor.reset(true, 0, 0);
            _mockSchedulerEngine.resetJobs(4, 2);
            _clusterSchedulerEngine.start();
            _memoryClusteredJobs.clear();
            Assert.assertTrue(_mockClusterMasterExecutor.isMaster());
            schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
            Assert.assertEquals(schedulerResponses.toString(), 4, schedulerResponses.size());
            Assert.assertTrue(_memoryClusteredJobs.isEmpty());
            logRecords = captureHandler.resetLogLevel(Level.ALL);
            _mockClusterMasterExecutor.reset(false, 4, 2);
            clusterMasterTokenTransitionListener = _mockClusterMasterExecutor.getClusterMasterTokenTransitionListener();
            clusterMasterTokenTransitionListener.masterTokenReleased();
            Assert.assertEquals(logRecords.toString(), 3, logRecords.size());
            LogRecord logRecord = logRecords.get(0);
            Assert.assertEquals("Load 4 memory clustered jobs from master", logRecord.getMessage());
            logRecord = logRecords.get(1);
            Assert.assertEquals("4 MEMORY_CLUSTERED jobs stopped running on this node", logRecord.getMessage());
            logRecord = logRecords.get(2);
            Assert.assertEquals("Unable to notify slave", logRecord.getMessage());
            Assert.assertFalse(_mockClusterMasterExecutor.isMaster());
            schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
            Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
            Assert.assertEquals(_memoryClusteredJobs.toString(), 4, _memoryClusteredJobs.size());
        }
    }

    @AdviseWith(adviceClasses = ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.class)
    @Test
    public void testPauseAndResumeOnMaster() throws SchedulerException {
        // Test 1, MEMORY_CLUSTERED job by jobName and groupName
        _mockClusterMasterExecutor.reset(true, 0, 0);
        _mockSchedulerEngine.resetJobs(4, 4);
        _clusterSchedulerEngine.start();
        SchedulerResponse schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        assertTriggerState(schedulerResponse, NORMAL);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        _clusterSchedulerEngine.pause(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        assertTriggerState(schedulerResponse, PAUSED);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        _clusterSchedulerEngine.resume(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        assertTriggerState(schedulerResponse, NORMAL);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 2, MEMORY_CLUSTERED jobs by groupName
        List<SchedulerResponse> schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        for (SchedulerResponse curSchedulerResponse : schedulerResponses) {
            assertTriggerState(curSchedulerResponse, NORMAL);
        }
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        _clusterSchedulerEngine.pause(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        for (SchedulerResponse curSchedulerResponse : schedulerResponses) {
            assertTriggerState(curSchedulerResponse, PAUSED);
        }
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        _clusterSchedulerEngine.resume(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        for (SchedulerResponse curSchedulerResponse : schedulerResponses) {
            assertTriggerState(curSchedulerResponse, NORMAL);
        }
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 3, PERSISTED job by jobName and groupName
        schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        assertTriggerState(schedulerResponse, NORMAL);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        _clusterSchedulerEngine.pause(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        assertTriggerState(schedulerResponse, PAUSED);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        _clusterSchedulerEngine.resume(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        assertTriggerState(schedulerResponse, NORMAL);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 4, PERSISTED jobs by groupName
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        for (SchedulerResponse curSchedulerResponse : schedulerResponses) {
            assertTriggerState(curSchedulerResponse, NORMAL);
        }
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        _clusterSchedulerEngine.pause(ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        for (SchedulerResponse curSchedulerResponse : schedulerResponses) {
            assertTriggerState(curSchedulerResponse, PAUSED);
        }
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        _clusterSchedulerEngine.resume(ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        for (SchedulerResponse curSchedulerResponse : schedulerResponses) {
            assertTriggerState(curSchedulerResponse, NORMAL);
        }
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
    }

    @AdviseWith(adviceClasses = ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.class)
    @Test
    public void testPauseAndResumeOnSlave() throws SchedulerException {
        // Test 1, MEMORY_CLUSTERED job by jobName and groupName
        _mockClusterMasterExecutor.reset(false, 4, 0);
        _mockSchedulerEngine.resetJobs(0, 4);
        _clusterSchedulerEngine.start();
        Assert.assertNull(_clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED));
        SchedulerResponse schedulerResponse = getMemoryClusteredJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME);
        assertTriggerState(schedulerResponse, NORMAL);
        _clusterSchedulerEngine.pause(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        Assert.assertNull(_clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED));
        schedulerResponse = getMemoryClusteredJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME);
        assertTriggerState(schedulerResponse, PAUSED);
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        _clusterSchedulerEngine.resume(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        Assert.assertNull(_clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED));
        schedulerResponse = getMemoryClusteredJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME);
        assertTriggerState(schedulerResponse, NORMAL);
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 2, MEMORY_CLUSTERED jobs by groupName
        List<SchedulerResponse> schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        schedulerResponses = getMemoryClusteredJobs(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME);
        for (SchedulerResponse curSchedulerResponse : schedulerResponses) {
            assertTriggerState(curSchedulerResponse, NORMAL);
        }
        _clusterSchedulerEngine.pause(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        schedulerResponses = getMemoryClusteredJobs(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME);
        for (SchedulerResponse curSchedulerResponse : schedulerResponses) {
            assertTriggerState(curSchedulerResponse, PAUSED);
        }
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        _clusterSchedulerEngine.resume(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        schedulerResponses = getMemoryClusteredJobs(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME);
        for (SchedulerResponse curSchedulerResponse : schedulerResponses) {
            assertTriggerState(curSchedulerResponse, NORMAL);
        }
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 3, not existed job
        Assert.assertNull(_clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME, MEMORY_CLUSTERED));
        Assert.assertNull(getMemoryClusteredJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME));
        _clusterSchedulerEngine.pause(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME, MEMORY_CLUSTERED);
        Assert.assertNull(_clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME, MEMORY_CLUSTERED));
        Assert.assertNull(getMemoryClusteredJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME));
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 4, not existed group
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME, MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        schedulerResponses = getMemoryClusteredJobs(ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        _clusterSchedulerEngine.pause(ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME, MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        schedulerResponses = getMemoryClusteredJobs(ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
    }

    @AdviseWith(adviceClasses = ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.class)
    @Test
    public void testScheduleOnMaster() throws SchedulerException {
        // Test 1, MEMORY_CLUSTERED job
        _mockClusterMasterExecutor.reset(true, 0, 0);
        _mockSchedulerEngine.resetJobs(1, 1);
        ClusterSchedulerEngineTest.MockClusterExecutor mockClusterExecutor = new ClusterSchedulerEngineTest.MockClusterExecutor();
        _clusterSchedulerEngine.setClusterExecutor(mockClusterExecutor);
        _clusterSchedulerEngine.start();
        List<SchedulerResponse> schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
        Assert.assertEquals(schedulerResponses.toString(), 1, schedulerResponses.size());
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterRequest clusterRequest = mockClusterExecutor.getClusterRequest();
        Assert.assertNull(clusterRequest);
        Trigger trigger = ClusterSchedulerEngineTest.getTrigger(((ClusterSchedulerEngineTest._TEST_JOB_NAME_PREFIX) + "new"), ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, ClusterSchedulerEngineTest._DEFAULT_INTERVAL);
        _clusterSchedulerEngine.schedule(trigger, BLANK, BLANK, new Message(), MEMORY_CLUSTERED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
        Assert.assertEquals(schedulerResponses.toString(), 2, schedulerResponses.size());
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        clusterRequest = mockClusterExecutor.getClusterRequest();
        Assert.assertNotNull(clusterRequest);
        Assert.assertTrue(clusterRequest.isMulticast());
        Assert.assertTrue(clusterRequest.isSkipLocal());
        MethodHandler methodHandler = ((MethodHandler) (clusterRequest.getPayload()));
        Assert.assertEquals(new MethodKey(ClusterSchedulerEngine.class, "_addMemoryClusteredJob", SchedulerResponse.class, String.class), methodHandler.getMethodKey());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 2, PERSISTED job
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(PERSISTED);
        Assert.assertEquals(schedulerResponses.toString(), 1, schedulerResponses.size());
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        trigger = ClusterSchedulerEngineTest.getTrigger(((ClusterSchedulerEngineTest._TEST_JOB_NAME_PREFIX) + "new"), ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, ClusterSchedulerEngineTest._DEFAULT_INTERVAL);
        _clusterSchedulerEngine.schedule(trigger, BLANK, BLANK, new Message(), PERSISTED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(PERSISTED);
        Assert.assertEquals(schedulerResponses.toString(), 2, schedulerResponses.size());
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
    }

    @AdviseWith(adviceClasses = ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.class)
    @Test
    public void testScheduleOnSlave() throws SchedulerException {
        _mockClusterMasterExecutor.reset(false, 1, 0);
        _mockSchedulerEngine.resetJobs(0, 0);
        _clusterSchedulerEngine.start();
        List<SchedulerResponse> schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        Assert.assertEquals(_memoryClusteredJobs.toString(), 1, _memoryClusteredJobs.size());
        _mockClusterMasterExecutor.reset(false, 2, 0);
        Trigger trigger = ClusterSchedulerEngineTest.getTrigger(((ClusterSchedulerEngineTest._TEST_JOB_NAME_PREFIX) + "1"), ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, ClusterSchedulerEngineTest._DEFAULT_INTERVAL);
        _clusterSchedulerEngine.schedule(trigger, BLANK, BLANK, new Message(), MEMORY_CLUSTERED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        Assert.assertEquals(_memoryClusteredJobs.toString(), 2, _memoryClusteredJobs.size());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
    }

    @Test
    public void testShutdown() throws SchedulerException {
        _clusterSchedulerEngine.start();
        Assert.assertNotNull(_mockClusterMasterExecutor.getClusterMasterTokenTransitionListener());
        _clusterSchedulerEngine.shutdown();
        Assert.assertNull(_mockClusterMasterExecutor.getClusterMasterTokenTransitionListener());
    }

    @Test
    public void testSlaveToMaster() throws SchedulerException {
        // Test 1, with log disabled
        _mockClusterMasterExecutor.reset(false, 4, 0);
        _mockSchedulerEngine.resetJobs(0, 0);
        _clusterSchedulerEngine.start();
        Assert.assertFalse(_mockClusterMasterExecutor.isMaster());
        List<SchedulerResponse> schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        Assert.assertEquals(_memoryClusteredJobs.toString(), 4, _memoryClusteredJobs.size());
        _clusterSchedulerEngine.pause(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        try (CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(ClusterSchedulerEngine.class.getName(), Level.OFF)) {
            _mockClusterMasterExecutor.reset(true, 0, 0);
            ClusterMasterTokenTransitionListener clusterMasterTokenTransitionListener = _mockClusterMasterExecutor.getClusterMasterTokenTransitionListener();
            clusterMasterTokenTransitionListener.masterTokenAcquired();
            List<LogRecord> logRecords = captureHandler.getLogRecords();
            Assert.assertTrue(logRecords.toString(), logRecords.isEmpty());
            Assert.assertTrue(_mockClusterMasterExecutor.isMaster());
            schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
            Assert.assertEquals(schedulerResponses.toString(), 4, schedulerResponses.size());
            SchedulerResponse schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
            assertTriggerState(schedulerResponse, PAUSED);
            Assert.assertTrue(_memoryClusteredJobs.isEmpty());
            // Test 2, with log enabled
            _mockClusterMasterExecutor.reset(false, 4, 0);
            _mockSchedulerEngine.resetJobs(0, 0);
            _clusterSchedulerEngine.start();
            Assert.assertFalse(_mockClusterMasterExecutor.isMaster());
            schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
            Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
            Assert.assertEquals(_memoryClusteredJobs.toString(), 4, _memoryClusteredJobs.size());
            logRecords = captureHandler.resetLogLevel(Level.ALL);
            _mockClusterMasterExecutor.reset(true, 0, 0);
            clusterMasterTokenTransitionListener = _mockClusterMasterExecutor.getClusterMasterTokenTransitionListener();
            clusterMasterTokenTransitionListener.masterTokenAcquired();
            Assert.assertEquals(logRecords.toString(), 2, logRecords.size());
            LogRecord logRecord = logRecords.get(0);
            Assert.assertEquals("4 MEMORY_CLUSTERED jobs started running on this node", logRecord.getMessage());
            logRecord = logRecords.get(1);
            Assert.assertEquals("Unable to notify slave", logRecord.getMessage());
            Assert.assertTrue(_mockClusterMasterExecutor.isMaster());
            schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(MEMORY_CLUSTERED);
            Assert.assertEquals(schedulerResponses.toString(), 4, schedulerResponses.size());
            Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        }
    }

    @AdviseWith(adviceClasses = ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.class)
    @Test
    public void testSuppressErrorOnMaster() throws SchedulerException {
        // Test 1, MEMORY_CLUSTERED job by jobName and groupName
        _mockClusterMasterExecutor.reset(true, 0, 0);
        _mockSchedulerEngine.resetJobs(1, 1);
        _clusterSchedulerEngine.start();
        SchedulerResponse schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        assertSuppressErrorValue(schedulerResponse, null);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        _clusterSchedulerEngine.suppressError(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        assertSuppressErrorValue(schedulerResponse, Boolean.TRUE);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 2, PERSISTED job by jobName and groupName
        schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        assertSuppressErrorValue(schedulerResponse, null);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        _clusterSchedulerEngine.suppressError(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        assertSuppressErrorValue(schedulerResponse, Boolean.TRUE);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
    }

    @AdviseWith(adviceClasses = ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.class)
    @Test
    public void testSuppressErrorOnSlave() throws SchedulerException {
        _mockClusterMasterExecutor.reset(false, 1, 0);
        _mockSchedulerEngine.resetJobs(0, 0);
        _clusterSchedulerEngine.start();
        Assert.assertNull(_clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED));
        SchedulerResponse schedulerResponse = getMemoryClusteredJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME);
        assertSuppressErrorValue(schedulerResponse, null);
        _clusterSchedulerEngine.suppressError(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        Assert.assertNull(_clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED));
        schedulerResponse = getMemoryClusteredJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME);
        assertSuppressErrorValue(schedulerResponse, null);
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
    }

    @AdviseWith(adviceClasses = ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.class)
    @Test
    public void testThreadLocal() throws SchedulerException {
        // Test 1, PERSISTED when portal is starting
        ClusterInvokeThreadLocal.setEnabled(false);
        PluginContextLifecycleThreadLocal.setInitializing(false);
        PluginContextLifecycleThreadLocal.setDestroying(false);
        _clusterSchedulerEngine.setClusterableThreadLocal(PERSISTED);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 2, MEMORY_CLUSTERED when portal is starting
        ClusterInvokeThreadLocal.setEnabled(false);
        PluginContextLifecycleThreadLocal.setInitializing(false);
        PluginContextLifecycleThreadLocal.setDestroying(false);
        _clusterSchedulerEngine.setClusterableThreadLocal(MEMORY_CLUSTERED);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        _clusterSchedulerEngine.start();
        // Test 3, PERSISTED when portal is started
        ClusterInvokeThreadLocal.setEnabled(false);
        PluginContextLifecycleThreadLocal.setInitializing(false);
        PluginContextLifecycleThreadLocal.setDestroying(false);
        _clusterSchedulerEngine.setClusterableThreadLocal(PERSISTED);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 4, MEMORY_CLUSTERED when portal is started
        ClusterInvokeThreadLocal.setEnabled(false);
        PluginContextLifecycleThreadLocal.setInitializing(false);
        PluginContextLifecycleThreadLocal.setDestroying(false);
        _clusterSchedulerEngine.setClusterableThreadLocal(MEMORY_CLUSTERED);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 5, PERSISTED when plugin is starting
        ClusterInvokeThreadLocal.setEnabled(false);
        PluginContextLifecycleThreadLocal.setInitializing(true);
        PluginContextLifecycleThreadLocal.setDestroying(false);
        _clusterSchedulerEngine.setClusterableThreadLocal(PERSISTED);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 6, MEMORY_CLUSTERED when plugin is starting
        ClusterInvokeThreadLocal.setEnabled(false);
        PluginContextLifecycleThreadLocal.setInitializing(true);
        PluginContextLifecycleThreadLocal.setDestroying(false);
        _clusterSchedulerEngine.setClusterableThreadLocal(MEMORY_CLUSTERED);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 7, PERSISTED when plugin is destroying
        ClusterInvokeThreadLocal.setEnabled(false);
        PluginContextLifecycleThreadLocal.setInitializing(false);
        PluginContextLifecycleThreadLocal.setDestroying(true);
        _clusterSchedulerEngine.setClusterableThreadLocal(PERSISTED);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 8, MEMORY_CLUSTERED when plugin is destroying
        ClusterInvokeThreadLocal.setEnabled(false);
        PluginContextLifecycleThreadLocal.setInitializing(false);
        PluginContextLifecycleThreadLocal.setDestroying(true);
        _clusterSchedulerEngine.setClusterableThreadLocal(MEMORY_CLUSTERED);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 9, PERSISTED when cluster invoke is disabled
        ClusterInvokeThreadLocal.setEnabled(false);
        PluginContextLifecycleThreadLocal.setInitializing(false);
        PluginContextLifecycleThreadLocal.setDestroying(false);
        _clusterSchedulerEngine.setClusterableThreadLocal(PERSISTED);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 10, PERSISTED when cluster invoke is enabled
        ClusterInvokeThreadLocal.setEnabled(true);
        PluginContextLifecycleThreadLocal.setInitializing(false);
        PluginContextLifecycleThreadLocal.setDestroying(false);
        _clusterSchedulerEngine.setClusterableThreadLocal(PERSISTED);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 11, MEMORY_CLUSTERED when cluster invoke is disabled
        ClusterInvokeThreadLocal.setEnabled(false);
        PluginContextLifecycleThreadLocal.setInitializing(false);
        PluginContextLifecycleThreadLocal.setDestroying(false);
        _clusterSchedulerEngine.setClusterableThreadLocal(MEMORY_CLUSTERED);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 12, MEMORY_CLUSTERED when cluster invoke is enabled
        ClusterInvokeThreadLocal.setEnabled(true);
        PluginContextLifecycleThreadLocal.setInitializing(false);
        PluginContextLifecycleThreadLocal.setDestroying(false);
        _clusterSchedulerEngine.setClusterableThreadLocal(MEMORY_CLUSTERED);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
    }

    @AdviseWith(adviceClasses = ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.class)
    @Test
    public void testUnscheduleOnMaster() throws SchedulerException {
        // Test 1, MEMORY_CLUSTERED job by jobName and groupName
        _mockClusterMasterExecutor.reset(true, 0, 0);
        _mockSchedulerEngine.resetJobs(4, 4);
        _clusterSchedulerEngine.start();
        SchedulerResponse schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        assertTriggerState(schedulerResponse, NORMAL);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        _clusterSchedulerEngine.unschedule(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        assertTriggerState(schedulerResponse, UNSCHEDULED);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 2, MEMORY_CLUSTERED jobs by groupName
        List<SchedulerResponse> schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        for (SchedulerResponse curSchedulerResponse : schedulerResponses) {
            if (Objects.equals(curSchedulerResponse.getJobName(), ClusterSchedulerEngineTest._TEST_JOB_NAME_0)) {
                assertTriggerState(curSchedulerResponse, UNSCHEDULED);
            } else {
                assertTriggerState(curSchedulerResponse, NORMAL);
            }
        }
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        _clusterSchedulerEngine.unschedule(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        for (SchedulerResponse curSchedulerResponse : schedulerResponses) {
            assertTriggerState(curSchedulerResponse, UNSCHEDULED);
        }
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 3, PERSISTED job by jobName and groupName
        schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        assertTriggerState(schedulerResponse, NORMAL);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        _clusterSchedulerEngine.unschedule(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        assertTriggerState(schedulerResponse, UNSCHEDULED);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 4, PERSISTED jobs by groupName
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        for (SchedulerResponse curSchedulerResponse : schedulerResponses) {
            if (Objects.equals(curSchedulerResponse.getJobName(), ClusterSchedulerEngineTest._TEST_JOB_NAME_0)) {
                assertTriggerState(curSchedulerResponse, UNSCHEDULED);
            } else {
                assertTriggerState(curSchedulerResponse, NORMAL);
            }
        }
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        _clusterSchedulerEngine.unschedule(ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        for (SchedulerResponse curSchedulerResponse : schedulerResponses) {
            assertTriggerState(curSchedulerResponse, UNSCHEDULED);
        }
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
    }

    @AdviseWith(adviceClasses = ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.class)
    @Test
    public void testUnscheduleOnSlave() throws SchedulerException {
        // Test 1, MEMORY_CLUSTERED job by jobName and groupName
        _mockClusterMasterExecutor.reset(false, 4, 0);
        _mockSchedulerEngine.resetJobs(0, 4);
        _clusterSchedulerEngine.start();
        Assert.assertNull(_clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED));
        SchedulerResponse schedulerResponse = getMemoryClusteredJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME);
        assertTriggerState(schedulerResponse, NORMAL);
        _clusterSchedulerEngine.unschedule(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        Assert.assertNull(_clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED));
        Assert.assertNull(getMemoryClusteredJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME));
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 2, MEMORY_CLUSTERED jobs by groupName
        List<SchedulerResponse> schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        schedulerResponses = getMemoryClusteredJobs(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME);
        for (SchedulerResponse curSchedulerResponse : schedulerResponses) {
            assertTriggerState(curSchedulerResponse, NORMAL);
        }
        _clusterSchedulerEngine.unschedule(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        schedulerResponses = _clusterSchedulerEngine.getScheduledJobs(ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
    }

    @AdviseWith(adviceClasses = ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.class)
    @Test
    public void testUpdateOnMaster() throws SchedulerException {
        // Test 1, MEMORY_CLUSTERED job by jobName and groupName
        _mockClusterMasterExecutor.reset(true, 0, 0);
        _mockSchedulerEngine.resetJobs(1, 1);
        _clusterSchedulerEngine.start();
        SchedulerResponse schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        assertTriggerContent(schedulerResponse, ClusterSchedulerEngineTest._DEFAULT_INTERVAL);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        Trigger trigger = ClusterSchedulerEngineTest.getTrigger(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, ((ClusterSchedulerEngineTest._DEFAULT_INTERVAL) * 2));
        _clusterSchedulerEngine.update(trigger, MEMORY_CLUSTERED);
        schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED);
        assertTriggerContent(schedulerResponse, ((ClusterSchedulerEngineTest._DEFAULT_INTERVAL) * 2));
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertTrue(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
        // Test 2, PERSISTED job by jobName and groupName
        schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        assertTriggerContent(schedulerResponse, ClusterSchedulerEngineTest._DEFAULT_INTERVAL);
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        trigger = ClusterSchedulerEngineTest.getTrigger(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, ((ClusterSchedulerEngineTest._DEFAULT_INTERVAL) * 2));
        _clusterSchedulerEngine.update(trigger, PERSISTED);
        schedulerResponse = _clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED);
        assertTriggerContent(schedulerResponse, ((ClusterSchedulerEngineTest._DEFAULT_INTERVAL) * 2));
        Assert.assertTrue(_memoryClusteredJobs.isEmpty());
        ClusterInvokeThreadLocal.setEnabled(false);
        Assert.assertFalse(_clusterInvokeAcceptor.accept(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.getAndClearThreadLocals()));
    }

    @AdviseWith(adviceClasses = ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice.class)
    @Test
    public void testUpdateOnSlave() throws SchedulerException {
        // Test 1, without exception
        _mockClusterMasterExecutor.reset(false, 1, 0);
        _mockSchedulerEngine.resetJobs(0, 0);
        _clusterSchedulerEngine.start();
        Assert.assertNull(_clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED));
        SchedulerResponse schedulerResponse = getMemoryClusteredJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME);
        assertTriggerContent(schedulerResponse, ClusterSchedulerEngineTest._DEFAULT_INTERVAL);
        Trigger trigger = ClusterSchedulerEngineTest.getTrigger(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, ((ClusterSchedulerEngineTest._DEFAULT_INTERVAL) * 2));
        _clusterSchedulerEngine.update(trigger, MEMORY_CLUSTERED);
        Assert.assertNull(_clusterSchedulerEngine.getScheduledJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED));
        schedulerResponse = getMemoryClusteredJob(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME);
        assertTriggerContent(schedulerResponse, ((ClusterSchedulerEngineTest._DEFAULT_INTERVAL) * 2));
        // Test 2, with not existed group name
        trigger = ClusterSchedulerEngineTest.getTrigger(ClusterSchedulerEngineTest._TEST_JOB_NAME_0, ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME, ((ClusterSchedulerEngineTest._DEFAULT_INTERVAL) * 2));
        try {
            _clusterSchedulerEngine.update(trigger, MEMORY_CLUSTERED);
            Assert.fail();
        } catch (SchedulerException se) {
            Assert.assertEquals("Unable to update trigger for memory clustered job", se.getMessage());
        }
        // Test 3, with not existed job name
        trigger = ClusterSchedulerEngineTest.getTrigger(ClusterSchedulerEngineTest._TEST_JOB_NAME_PREFIX, ClusterSchedulerEngineTest._NOT_EXISTED_GROUP_NAME, ((ClusterSchedulerEngineTest._DEFAULT_INTERVAL) * 2));
        try {
            _clusterSchedulerEngine.update(trigger, MEMORY_CLUSTERED);
            Assert.fail();
        } catch (SchedulerException se) {
            Assert.assertEquals("Unable to update trigger for memory clustered job", se.getMessage());
        }
    }

    @Rule
    public final AspectJNewEnvTestRule aspectJNewEnvTestRule = AspectJNewEnvTestRule.INSTANCE;

    @Aspect
    public static class ClusterableContextThreadLocalAdvice {
        public static Map<String, Serializable> getAndClearThreadLocals() {
            Map<String, Serializable> threadLocal = new HashMap<>(ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice._threadLocals);
            ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice._threadLocals.clear();
            return threadLocal;
        }

        @Around("execution(void com.liferay.portal.kernel.cluster." + (("ClusterableContextThreadLocal.putThreadLocalContext(" + "java.lang.String, java.io.Serializable)) && args(key, ") + "value)"))
        public void loadIndexesFromCluster(String key, Serializable value) {
            ClusterSchedulerEngineTest.ClusterableContextThreadLocalAdvice._threadLocals.put(key, value);
        }

        private static final Map<String, Serializable> _threadLocals = new HashMap<>();
    }

    private static final int _DEFAULT_INTERVAL = 20;

    private static final String _MEMORY_CLUSTER_TEST_GROUP_NAME = "memory.cluster.test.group";

    private static final String _NOT_EXISTED_GROUP_NAME = "not.existed.test.group";

    private static final String _PERSISTENT_TEST_GROUP_NAME = "persistent.test.group";

    private static final String _SUPPRESS_ERROR = "suppressError";

    private static final String _TEST_JOB_NAME_0 = "test.job.0";

    private static final String _TEST_JOB_NAME_PREFIX = "test.job.";

    private static final MethodKey _getScheduledJobMethodKey = new MethodKey(SchedulerEngineHelperUtil.class, "getScheduledJob", String.class, String.class, StorageType.class);

    private static final MethodKey _getScheduledJobsMethodKey = new MethodKey(SchedulerEngineHelperUtil.class, "getScheduledJobs", StorageType.class);

    private ClusterInvokeAcceptor _clusterInvokeAcceptor;

    private ClusterSchedulerEngine _clusterSchedulerEngine;

    private Map<String, ObjectValuePair<SchedulerResponse, TriggerState>> _memoryClusteredJobs;

    private final ClusterSchedulerEngineTest.MockClusterMasterExecutor _mockClusterMasterExecutor = new ClusterSchedulerEngineTest.MockClusterMasterExecutor();

    private ClusterSchedulerEngineTest.MockSchedulerEngine _mockSchedulerEngine;

    private Props _props;

    private static class MockClusterExecutor implements ClusterExecutor {
        @Override
        public void addClusterEventListener(ClusterEventListener clusterEventListener) {
        }

        @Override
        public FutureClusterResponses execute(ClusterRequest clusterRequest) {
            _clusterRequest = clusterRequest;
            return null;
        }

        @Override
        public InetAddress getBindInetAddress() {
            return null;
        }

        @Override
        public NetworkInterface getBindNetworkInterface() {
            return null;
        }

        @Override
        public List<ClusterEventListener> getClusterEventListeners() {
            return null;
        }

        @Override
        public List<ClusterNode> getClusterNodes() {
            return null;
        }

        public ClusterRequest getClusterRequest() {
            return _clusterRequest;
        }

        @Override
        public ClusterNode getLocalClusterNode() {
            return null;
        }

        @Override
        public boolean isClusterNodeAlive(String clusterNodeId) {
            return false;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void removeClusterEventListener(ClusterEventListener clusterEventListener) {
        }

        private ClusterRequest _clusterRequest;
    }

    private static class MockClusterMasterExecutor implements ClusterMasterExecutor {
        @Override
        public void addClusterMasterTokenTransitionListener(ClusterMasterTokenTransitionListener clusterMasterTokenAcquisitionListener) {
            _clusterMasterTokenTransitionListener = clusterMasterTokenAcquisitionListener;
        }

        @Override
        public <T> NoticeableFuture<T> executeOnMaster(MethodHandler methodHandler) {
            if (_exception) {
                throw new SystemException();
            }
            T result = null;
            MethodKey methodKey = methodHandler.getMethodKey();
            if (methodKey.equals(ClusterSchedulerEngineTest._getScheduledJobsMethodKey)) {
                StorageType storageType = ((StorageType) (methodHandler.getArguments()[0]));
                result = ((T) (_mockSchedulerEngine.getScheduledJobs(storageType)));
            } else
                if (methodKey.equals(ClusterSchedulerEngineTest._getScheduledJobMethodKey)) {
                    String jobName = ((String) (methodHandler.getArguments()[0]));
                    String groupName = ((String) (methodHandler.getArguments()[1]));
                    StorageType storageType = ((StorageType) (methodHandler.getArguments()[2]));
                    result = ((T) (_mockSchedulerEngine.getScheduledJob(jobName, groupName, storageType)));
                }

            DefaultNoticeableFuture<T> defaultNoticeableFuture = new DefaultNoticeableFuture();
            defaultNoticeableFuture.set(result);
            return defaultNoticeableFuture;
        }

        public ClusterMasterTokenTransitionListener getClusterMasterTokenTransitionListener() {
            return _clusterMasterTokenTransitionListener;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public boolean isMaster() {
            return _master;
        }

        @Override
        public void removeClusterMasterTokenTransitionListener(ClusterMasterTokenTransitionListener clusterMasterTokenAcquisitionListener) {
            if ((_clusterMasterTokenTransitionListener) == clusterMasterTokenAcquisitionListener) {
                _clusterMasterTokenTransitionListener = null;
            }
        }

        public void reset(boolean master, int memoryClusterJobs, int persistentJobs) {
            _master = master;
            _mockSchedulerEngine.resetJobs(memoryClusterJobs, persistentJobs);
        }

        public void setException(boolean exception) {
            _exception = exception;
        }

        private ClusterMasterTokenTransitionListener _clusterMasterTokenTransitionListener;

        private boolean _exception;

        private boolean _master;

        private final ClusterSchedulerEngineTest.MockSchedulerEngine _mockSchedulerEngine = new ClusterSchedulerEngineTest.MockSchedulerEngine();
    }

    private static class MockSchedulerEngine implements SchedulerEngine {
        @Override
        public void delete(String groupName, StorageType storageType) {
            Set<String> keySet = _defaultJobs.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if ((key.contains(groupName)) && (key.contains(storageType.toString()))) {
                    iterator.remove();
                }
            } 
        }

        @Override
        public void delete(String jobName, String groupName, StorageType storageType) {
            _defaultJobs.remove(_getFullName(jobName, groupName, storageType));
        }

        @Override
        public SchedulerResponse getScheduledJob(String jobName, String groupName, StorageType storageType) {
            return _defaultJobs.get(_getFullName(jobName, groupName, storageType));
        }

        @Override
        public List<SchedulerResponse> getScheduledJobs() {
            return new ArrayList(_defaultJobs.values());
        }

        @Override
        public List<SchedulerResponse> getScheduledJobs(StorageType storageType) {
            List<SchedulerResponse> schedulerResponses = new ArrayList<>();
            for (SchedulerResponse schedulerResponse : _defaultJobs.values()) {
                if (storageType == (schedulerResponse.getStorageType())) {
                    schedulerResponses.add(schedulerResponse);
                }
            }
            return schedulerResponses;
        }

        @Override
        public List<SchedulerResponse> getScheduledJobs(String groupName, StorageType storageType) {
            List<SchedulerResponse> schedulerResponses = new ArrayList<>();
            for (Map.Entry<String, SchedulerResponse> entry : _defaultJobs.entrySet()) {
                String key = entry.getKey();
                if ((key.contains(groupName)) && (key.contains(storageType.toString()))) {
                    schedulerResponses.add(entry.getValue());
                }
            }
            return schedulerResponses;
        }

        @Override
        public void pause(String groupName, StorageType storageType) {
            for (SchedulerResponse schedulerResponse : getScheduledJobs(groupName, storageType)) {
                Message message = schedulerResponse.getMessage();
                message.put(JOB_STATE, new com.liferay.portal.kernel.scheduler.JobState(TriggerState.PAUSED));
            }
        }

        @Override
        public void pause(String jobName, String groupName, StorageType storageType) {
            SchedulerResponse schedulerResponse = getScheduledJob(jobName, groupName, storageType);
            Message message = schedulerResponse.getMessage();
            message.put(JOB_STATE, new com.liferay.portal.kernel.scheduler.JobState(TriggerState.PAUSED));
        }

        public void resetJobs(int memoryClusterJobs, int persistentJobs) {
            _defaultJobs.clear();
            for (int i = 0; i < memoryClusterJobs; i++) {
                _addJobs(ClusterSchedulerEngineTest._TEST_JOB_NAME_PREFIX.concat(String.valueOf(i)), ClusterSchedulerEngineTest._MEMORY_CLUSTER_TEST_GROUP_NAME, MEMORY_CLUSTERED, null, null);
            }
            for (int i = 0; i < persistentJobs; i++) {
                _addJobs(ClusterSchedulerEngineTest._TEST_JOB_NAME_PREFIX.concat(String.valueOf(i)), ClusterSchedulerEngineTest._PERSISTENT_TEST_GROUP_NAME, PERSISTED, null, null);
            }
        }

        @Override
        public void resume(String groupName, StorageType storageType) {
            for (SchedulerResponse schedulerResponse : getScheduledJobs(groupName, storageType)) {
                Message message = schedulerResponse.getMessage();
                message.put(JOB_STATE, new com.liferay.portal.kernel.scheduler.JobState(TriggerState.NORMAL));
            }
        }

        @Override
        public void resume(String jobName, String groupName, StorageType storageType) {
            SchedulerResponse schedulerResponse = getScheduledJob(jobName, groupName, storageType);
            Message message = schedulerResponse.getMessage();
            message.put(JOB_STATE, new com.liferay.portal.kernel.scheduler.JobState(TriggerState.NORMAL));
        }

        @Override
        public void schedule(Trigger trigger, String description, String destinationName, Message message, StorageType storageType) throws SchedulerException {
            String jobName = trigger.getJobName();
            if (!(jobName.startsWith(ClusterSchedulerEngineTest._TEST_JOB_NAME_PREFIX))) {
                throw new SchedulerException(("Invalid job name " + jobName));
            }
            _addJobs(trigger.getJobName(), trigger.getGroupName(), storageType, trigger, message);
        }

        @Override
        public void shutdown() {
            _defaultJobs.clear();
        }

        @Override
        public void start() {
        }

        @Override
        public void suppressError(String jobName, String groupName, StorageType storageType) {
            SchedulerResponse schedulerResponse = getScheduledJob(jobName, groupName, storageType);
            Message message = schedulerResponse.getMessage();
            message.put(ClusterSchedulerEngineTest._SUPPRESS_ERROR, Boolean.TRUE);
        }

        @Override
        public void unschedule(String groupName, StorageType storageType) {
            for (SchedulerResponse schedulerResponse : getScheduledJobs(groupName, storageType)) {
                Message message = schedulerResponse.getMessage();
                message.put(JOB_STATE, new com.liferay.portal.kernel.scheduler.JobState(TriggerState.UNSCHEDULED));
            }
        }

        @Override
        public void unschedule(String jobName, String groupName, StorageType storageType) {
            SchedulerResponse schedulerResponse = getScheduledJob(jobName, groupName, storageType);
            Message message = schedulerResponse.getMessage();
            message.put(JOB_STATE, new com.liferay.portal.kernel.scheduler.JobState(TriggerState.UNSCHEDULED));
        }

        @Override
        public void update(Trigger trigger, StorageType storageType) {
            SchedulerResponse schedulerResponse = getScheduledJob(trigger.getJobName(), trigger.getGroupName(), storageType);
            schedulerResponse.setTrigger(trigger);
        }

        @Override
        public void validateTrigger(Trigger trigger, StorageType storageType) {
        }

        private SchedulerResponse _addJobs(String jobName, String groupName, StorageType storageType, Trigger trigger, Message message) {
            SchedulerResponse schedulerResponse = new SchedulerResponse();
            schedulerResponse.setDestinationName(SCHEDULER_DISPATCH);
            schedulerResponse.setGroupName(groupName);
            schedulerResponse.setJobName(jobName);
            if (message == null) {
                message = new Message();
            }
            message.put(JOB_STATE, new com.liferay.portal.kernel.scheduler.JobState(TriggerState.NORMAL));
            schedulerResponse.setMessage(message);
            schedulerResponse.setStorageType(storageType);
            if (trigger == null) {
                trigger = ClusterSchedulerEngineTest.getTrigger(jobName, groupName, ClusterSchedulerEngineTest._DEFAULT_INTERVAL);
            }
            schedulerResponse.setTrigger(trigger);
            _defaultJobs.put(_getFullName(jobName, groupName, storageType), schedulerResponse);
            return schedulerResponse;
        }

        private String _getFullName(String jobName, String groupName, StorageType storageType) {
            return ((groupName + (StringPool.PERIOD)) + jobName) + storageType;
        }

        private final Map<String, SchedulerResponse> _defaultJobs = new HashMap<>();
    }

    private static class MockTrigger implements Trigger {
        public MockTrigger(String jobName, String groupName, Date startDate, Date endDate, int interval, TimeUnit timeUnit) {
            _jobName = jobName;
            _groupName = groupName;
            if (startDate != null) {
                _startDate = startDate;
            } else {
                _startDate = new Date();
            }
            _endDate = endDate;
            _interval = interval;
            _timeUnit = timeUnit;
        }

        @Override
        public Date getEndDate() {
            return _endDate;
        }

        @Override
        public Date getFireDateAfter(Date date) {
            if ((_timeUnit) != (TimeUnit.SECOND)) {
                return null;
            }
            long nextFirTime = _startDate.getTime();
            do {
                nextFirTime += (_interval) * 1000;
            } while (nextFirTime <= (date.getTime()) );
            return new Date(nextFirTime);
        }

        @Override
        public String getGroupName() {
            return _groupName;
        }

        public int getInterval() {
            return _interval;
        }

        @Override
        public String getJobName() {
            return _jobName;
        }

        @Override
        public Date getStartDate() {
            return _startDate;
        }

        public TimeUnit getTimeUnit() {
            return _timeUnit;
        }

        @Override
        public Serializable getWrappedTrigger() {
            return null;
        }

        private final Date _endDate;

        private final String _groupName;

        private final int _interval;

        private final String _jobName;

        private final Date _startDate;

        private final TimeUnit _timeUnit;
    }

    private static class MockTriggerFactory implements TriggerFactory {
        @Override
        public Trigger createTrigger(String jobName, String groupName, Date startDate, Date endDate, int interval, TimeUnit timeUnit) {
            return null;
        }

        @Override
        public Trigger createTrigger(String jobName, String groupName, Date startDate, Date endDate, String cronExpression) {
            return null;
        }

        @Override
        public Trigger createTrigger(Trigger trigger, Date startDate, Date endDate) {
            ClusterSchedulerEngineTest.MockTrigger mockTrigger = ((ClusterSchedulerEngineTest.MockTrigger) (trigger));
            return new ClusterSchedulerEngineTest.MockTrigger(mockTrigger.getJobName(), mockTrigger.getGroupName(), startDate, endDate, mockTrigger.getInterval(), mockTrigger.getTimeUnit());
        }
    }
}

