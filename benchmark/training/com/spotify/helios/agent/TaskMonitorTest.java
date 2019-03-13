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
package com.spotify.helios.agent;


import com.google.common.util.concurrent.SettableFuture;
import com.spotify.docker.client.exceptions.ImageNotFoundException;
import com.spotify.docker.client.exceptions.ImagePullFailedException;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.ThrottleState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TaskMonitorTest {
    @Mock
    FlapController flapController;

    @Mock
    StatusUpdater statusUpdater;

    static final JobId JOB_ID = JobId.fromString("foo:bar:deadbeef");

    TaskMonitor sut;

    @Test
    public void verifyMonitorPropagatesState() throws Exception {
        sut.pulling();
        Mockito.verify(statusUpdater, Mockito.never()).setThrottleState(ArgumentMatchers.any(ThrottleState.class));
        Mockito.verify(statusUpdater).setState(PULLING_IMAGE);
        Mockito.verify(statusUpdater).update();
        Mockito.reset(statusUpdater);
        sut.creating();
        Mockito.verify(statusUpdater).setState(CREATING);
        Mockito.verify(statusUpdater, Mockito.never()).setThrottleState(ArgumentMatchers.any(ThrottleState.class));
        Mockito.verify(statusUpdater).update();
        Mockito.reset(statusUpdater);
        sut.starting();
        Mockito.verify(statusUpdater).setState(STARTING);
        Mockito.verify(statusUpdater, Mockito.never()).setThrottleState(ArgumentMatchers.any(ThrottleState.class));
        Mockito.verify(statusUpdater).update();
        Mockito.reset(statusUpdater);
        sut.running();
        Mockito.verify(statusUpdater).setState(RUNNING);
        Mockito.verify(statusUpdater, Mockito.never()).setThrottleState(ArgumentMatchers.any(ThrottleState.class));
        Mockito.verify(statusUpdater).update();
        Mockito.reset(statusUpdater);
        sut.exited(4711);
        Mockito.verify(statusUpdater).setState(EXITED);
        Mockito.verify(statusUpdater, Mockito.never()).setThrottleState(ArgumentMatchers.any(ThrottleState.class));
        Mockito.verify(statusUpdater).update();
        Mockito.reset(statusUpdater);
        sut.failed(new Exception(), "Error herping derps.");
        Mockito.verify(statusUpdater).setState(FAILED);
        Mockito.verify(statusUpdater).setContainerError("Error herping derps.");
        Mockito.verify(statusUpdater).update();
        Mockito.verify(statusUpdater, Mockito.never()).setThrottleState(ArgumentMatchers.any(ThrottleState.class));
        Mockito.reset(statusUpdater);
    }

    @Test
    public void verifyMonitorPropagatesImagePullFailed() throws Exception {
        sut.failed(new ImagePullFailedException("foobar", "failure"), "container error");
        Mockito.verify(statusUpdater).setThrottleState(IMAGE_PULL_FAILED);
        Mockito.verify(statusUpdater).setState(FAILED);
        Mockito.verify(statusUpdater).setContainerError("container error");
        Mockito.verify(statusUpdater).update();
    }

    @Test
    public void verifyMonitorPropagatesImageMissing() throws Exception {
        sut.failed(new ImageNotFoundException("foobar", "not found"), "container error");
        Mockito.verify(statusUpdater).setThrottleState(IMAGE_MISSING);
        Mockito.verify(statusUpdater).setState(FAILED);
        Mockito.verify(statusUpdater).setContainerError("container error");
        Mockito.verify(statusUpdater).update();
    }

    @Test
    public void verifyMonitorUsesFlapController() {
        sut.running();
        Mockito.verify(statusUpdater, Mockito.never()).setThrottleState(FLAPPING);
        Mockito.verify(flapController).started();
        sut.exited(17);
        Mockito.verify(flapController).exited();
    }

    @Test
    public void verifyImagePullFailureTrumpsFlappingState() throws Exception {
        Mockito.when(flapController.isFlapping()).thenReturn(true);
        sut.failed(new ImagePullFailedException("foobar", "failure"), "container error");
        Mockito.verify(statusUpdater).setThrottleState(IMAGE_PULL_FAILED);
        Mockito.verify(statusUpdater).setState(FAILED);
        Mockito.verify(statusUpdater).setContainerError("container error");
        Mockito.verify(statusUpdater).update();
    }

    @Test
    public void verifyImageMissingTrumpsFlappingState() throws Exception {
        Mockito.when(flapController.isFlapping()).thenReturn(true);
        sut.failed(new ImageNotFoundException("foobar", "not found"), "container error");
        Mockito.verify(statusUpdater).setThrottleState(IMAGE_MISSING);
        Mockito.verify(statusUpdater).setState(FAILED);
        Mockito.verify(statusUpdater).setContainerError("container error");
        Mockito.verify(statusUpdater).update();
    }

    @Test
    public void verifyMonitorRecoversFromFlappingState() {
        final SettableFuture<Boolean> flappingRecoveryFuture = SettableFuture.create();
        Mockito.when(flapController.isFlapping()).thenReturn(true).thenAnswer(futureAnswer(flappingRecoveryFuture));
        Mockito.when(flapController.millisLeftToUnflap()).thenReturn(10L);
        sut.exited(17);
        Mockito.verify(flapController).exited();
        Mockito.verify(statusUpdater).setThrottleState(FLAPPING);
        Mockito.verify(flapController, Mockito.timeout(30000).times(2)).isFlapping();
        flappingRecoveryFuture.set(false);
        Mockito.verify(statusUpdater, Mockito.timeout(30000)).setThrottleState(NO);
    }
}

