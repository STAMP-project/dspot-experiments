/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.cluster.gracefulstop;


import io.crate.action.sql.SQLOperations;
import io.crate.execution.engine.collect.stats.JobsLogs;
import io.crate.test.integration.CrateDummyClusterServiceUnitTest;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;
import org.elasticsearch.action.admin.cluster.health.TransportClusterHealthAction;
import org.elasticsearch.action.admin.cluster.settings.TransportClusterUpdateSettingsAction;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DecommissioningServiceTest extends CrateDummyClusterServiceUnitTest {
    private JobsLogs jobsLogs;

    private DecommissioningServiceTest.TestableDecommissioningService decommissioningService;

    private ScheduledExecutorService executorService;

    private SQLOperations sqlOperations;

    private AtomicBoolean exited = new AtomicBoolean(false);

    @Test
    public void testExitIfNoActiveRequests() throws Exception {
        decommissioningService.exitIfNoActiveRequests(0);
        assertThat(exited.get(), Is.is(true));
        assertThat(decommissioningService.forceStopOrAbortCalled, Is.is(false));
    }

    @Test
    public void testNoExitIfRequestAreActive() throws Exception {
        jobsLogs.logExecutionEnd(UUID.randomUUID(), null);
        exitIfNoActiveRequests(System.nanoTime());
        assertThat(exited.get(), Is.is(false));
        assertThat(decommissioningService.forceStopOrAbortCalled, Is.is(false));
        Mockito.verify(executorService, Mockito.times(1)).schedule(Mockito.any(Runnable.class), Mockito.anyLong(), Mockito.any(TimeUnit.class));
    }

    @Test
    public void testAbortOrForceStopIsCalledOnTimeout() throws Exception {
        jobsLogs.logExecutionEnd(UUID.randomUUID(), null);
        decommissioningService.exitIfNoActiveRequests(((System.nanoTime()) - (TimeValue.timeValueHours(3).nanos())));
        assertThat(decommissioningService.forceStopOrAbortCalled, Is.is(true));
        Mockito.verify(sqlOperations, Mockito.times(1)).enable();
    }

    private static class TestableDecommissioningService extends DecommissioningService {
        private boolean forceStopOrAbortCalled = false;

        TestableDecommissioningService(Settings settings, ClusterService clusterService, JobsLogs jobsLogs, ScheduledExecutorService executorService, SQLOperations sqlOperations, Runnable safeExitAction, TransportClusterHealthAction healthAction, TransportClusterUpdateSettingsAction updateSettingsAction) {
            super(settings, clusterService, jobsLogs, executorService, sqlOperations, () -> 0, safeExitAction, healthAction, updateSettingsAction);
        }

        @Override
        void forceStopOrAbort(@Nullable
        Throwable e) {
            forceStopOrAbortCalled = true;
            super.forceStopOrAbort(e);
        }

        @Override
        protected void removeDecommissioningSetting() {
        }
    }
}

