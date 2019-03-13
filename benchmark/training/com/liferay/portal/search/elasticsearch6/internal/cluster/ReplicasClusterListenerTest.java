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
package com.liferay.portal.search.elasticsearch6.internal.cluster;


import com.liferay.portal.kernel.test.CaptureHandler;
import com.liferay.portal.kernel.test.JDKLoggerTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Andr? de Oliveira
 */
@Ignore
public class ReplicasClusterListenerTest {
    @Test
    public void testAHappyDay() {
        processClusterEvent();
        assertReplicasChanged();
    }

    @Test
    public void testLiferayClusterReportsEmpty() {
        Mockito.when(_replicasClusterContext.getClusterSize()).thenReturn(0);
        processClusterEvent();
        Mockito.verify(_replicasManager).updateNumberOfReplicas(0, ReplicasClusterListenerTest._INDICES);
    }

    @Test
    public void testMasterTokenAcquired() {
        masterTokenAcquired();
        assertReplicasChanged();
    }

    @Test
    public void testMasterTokenReleased() {
        masterTokenReleased();
        assertReplicasUnchanged();
    }

    @Test
    public void testNonmasterLiferayNodeDoesNothing() {
        setMasterExecutor(false);
        processClusterEvent();
        assertReplicasUnchanged();
    }

    @Test
    public void testRemoteElasticsearchClusterIsLeftAlone() {
        setEmbeddedCluster(false);
        processClusterEvent();
        assertReplicasUnchanged();
    }

    @Test
    public void testResilientToUpdateFailures() {
        Throwable throwable = new RuntimeException();
        Mockito.doThrow(throwable).when(_replicasManager).updateNumberOfReplicas(Mockito.anyInt(), ((String[]) (Mockito.anyVararg())));
        try (CaptureHandler captureHandler = JDKLoggerTestUtil.configureJDKLogger(ReplicasClusterListener.class.getName(), Level.WARNING)) {
            masterTokenAcquired();
            List<LogRecord> logRecords = captureHandler.getLogRecords();
            Assert.assertEquals(logRecords.toString(), 1, logRecords.size());
            LogRecord logRecord = logRecords.get(0);
            Assert.assertEquals("Unable to update number of replicas", logRecord.getMessage());
            Assert.assertSame(throwable, logRecord.getThrown());
        }
    }

    private static final String[] _INDICES = new String[]{ RandomTestUtil.randomString(), RandomTestUtil.randomString() };

    private static final int _REPLICAS = (RandomTestUtil.randomInt()) - 1;

    @Mock
    private ReplicasClusterContext _replicasClusterContext;

    private ReplicasClusterListener _replicasClusterListener;

    @Mock
    private ReplicasManager _replicasManager;
}

