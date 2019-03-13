/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
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
 * -/-/-
 */
package com.spotify.helios.master.reaper;


import DeploymentGroup.RollingUpdateReason;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.RolloutOptions;
import com.spotify.helios.master.MasterModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.Mockito;


public class OldJobReaperTest {
    @Rule
    public TestName name = new TestName();

    private static final long RETENTION_DAYS = 1;

    private static final Job DUMMY_JOB = Job.newBuilder().build();

    /**
     * A job not deployed, with history, and last used too long ago should BE reaped.
     */
    @Test
    public void jobNotDeployedWithHistoryLastUsedTooLongAgoReaped() throws Exception {
        final MasterModel masterModel = Mockito.mock(MasterModel.class);
        testReap(name.getMethodName(), Collections.emptyMap(), events(ImmutableList.of(TimeUnit.HOURS.toMillis(20), TimeUnit.HOURS.toMillis(22))), null, masterModel, true);
    }

    /**
     * A job not deployed, with history, and last used recently should NOT BE reaped.
     */
    @Test
    public void jobNotDeployedWithHistoryLastUsedRecentlyNotReaped() throws Exception {
        final MasterModel masterModel = Mockito.mock(MasterModel.class);
        testReap(name.getMethodName(), Collections.emptyMap(), events(ImmutableList.of(TimeUnit.HOURS.toMillis(20), TimeUnit.HOURS.toMillis(40))), null, masterModel, false);
    }

    /**
     * A job not deployed, without history, and without a creation date should BE reaped.
     */
    @Test
    public void jobNotDeployedWithoutHistoryWithCreateDateReaped() throws Exception {
        final MasterModel masterModel = Mockito.mock(MasterModel.class);
        testReap(name.getMethodName(), Collections.emptyMap(), Collections.emptyList(), null, masterModel, true);
    }

    /**
     * A job not deployed, without history, and created before retention time should BE reaped.
     */
    @Test
    public void jobNotDeployedWithoutHistoryCreateBeforeRetentionReaped() throws Exception {
        final MasterModel masterModel = Mockito.mock(MasterModel.class);
        testReap(name.getMethodName(), Collections.emptyMap(), Collections.emptyList(), TimeUnit.HOURS.toMillis(23), masterModel, true);
    }

    /**
     * A job not deployed, without history, created after retention time should NOT BE reaped.
     */
    @Test
    public void jobNotDeployedWithoutHistoryCreateAfterRetentionNotReaped() throws Exception {
        final MasterModel masterModel = Mockito.mock(MasterModel.class);
        testReap(name.getMethodName(), Collections.emptyMap(), Collections.emptyList(), TimeUnit.HOURS.toMillis(25), masterModel, false);
    }

    /**
     * A job deployed and without history should NOT BE reaped.
     */
    @Test
    public void jobDeployedWithoutHistoryNotReaped() throws Exception {
        final MasterModel masterModel = Mockito.mock(MasterModel.class);
        testReap(name.getMethodName(), deployments(JobId.fromString(name.getMethodName()), 2), Collections.emptyList(), null, masterModel, false);
    }

    /**
     * A job deployed, with history, and last used too long ago should NOT BE reaped.
     */
    @Test
    public void jobDeployedWithHistoryLastUsedTooLongAgoNotReaped() throws Exception {
        final MasterModel masterModel = Mockito.mock(MasterModel.class);
        testReap(name.getMethodName(), deployments(JobId.fromString(name.getMethodName()), 3), events(ImmutableList.of(TimeUnit.HOURS.toMillis(20), TimeUnit.HOURS.toMillis(22))), null, masterModel, false);
    }

    /**
     * A job deployed, with history, and last used recently should NOT BE reaped.
     */
    @Test
    public void jobDeployedWithHistoryLastUsedRecentlyNotReaped() throws Exception {
        final MasterModel masterModel = Mockito.mock(MasterModel.class);
        testReap(name.getMethodName(), deployments(JobId.fromString(name.getMethodName()), 3), events(ImmutableList.of(TimeUnit.HOURS.toMillis(20), TimeUnit.HOURS.toMillis(40))), null, masterModel, false);
    }

    /**
     * A job not deployed, with history, last used too long ago and part of a deployment group
     * should NOT BE reaped.
     */
    @Test
    public void jobDeployedWithHistoryLastUsedTooLongAgoInDgNotReaped() throws Exception {
        final MasterModel masterModel = Mockito.mock(MasterModel.class);
        Mockito.when(masterModel.getDeploymentGroups()).thenReturn(ImmutableMap.of("testdg", new com.spotify.helios.common.descriptors.DeploymentGroup("name", new ArrayList(), JobId.fromString(name.getMethodName()), RolloutOptions.getDefault(), RollingUpdateReason.MANUAL)));
        testReap(name.getMethodName(), Collections.emptyMap(), events(ImmutableList.of(TimeUnit.HOURS.toMillis(2), TimeUnit.HOURS.toMillis(3))), null, masterModel, false);
    }

    /**
     * A job not deployed, with history, last used too long ago and part of a deployment group
     * should BE reaped.
     */
    @Test
    public void jobDeployedWithHistoryLastUsedTooLongAgoNotInDgReaped() throws Exception {
        final MasterModel masterModel = Mockito.mock(MasterModel.class);
        Mockito.when(masterModel.getDeploymentGroups()).thenReturn(ImmutableMap.of("testdg", new com.spotify.helios.common.descriptors.DeploymentGroup("name", new ArrayList(), JobId.fromString("framazama"), RolloutOptions.getDefault(), RollingUpdateReason.MANUAL)));
        testReap(name.getMethodName(), Collections.emptyMap(), events(ImmutableList.of(TimeUnit.HOURS.toMillis(2), TimeUnit.HOURS.toMillis(3))), null, masterModel, true);
    }
}

