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


import com.spotify.helios.common.Clock;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class FlapControllerTest {
    @Mock
    private Clock clock;

    @Test
    public void testRecoveryFromFlappingWhileRunning() throws Exception {
        final FlapController controller = FlapController.newBuilder().setClock(clock).setRestartCount(2).setTimeRangeMillis(1000).build();
        Assert.assertFalse(controller.isFlapping());
        Mockito.when(clock.now()).thenReturn(new Instant(0));
        // get controller into flapping state
        controller.started();
        controller.exited();
        Assert.assertFalse(controller.isFlapping());// not failed enough *yet*

        controller.started();
        controller.exited();
        Assert.assertTrue(controller.isFlapping());// now we failed enough.

        // // See that the state maintains the flapping state.
        controller.started();
        controller.exited();
        Assert.assertTrue(controller.isFlapping());
        controller.started();
        controller.exited();
        Assert.assertTrue(controller.isFlapping());
        // // Now test that the state will update while the future is running
        controller.started();
        Mockito.when(clock.now()).thenReturn(new Instant(2000));
        Assert.assertFalse(controller.isFlapping());
    }

    @Test
    public void testEnterAndExitFlapping() throws Exception {
        final FlapController controller = FlapController.newBuilder().setClock(clock).setRestartCount(2).setTimeRangeMillis(1000).build();
        Assert.assertFalse(controller.isFlapping());
        Mockito.when(clock.now()).thenReturn(new Instant(0));
        controller.started();
        Mockito.when(clock.now()).thenReturn(new Instant(1));
        controller.exited();// 1 second of runtime T=1

        Assert.assertFalse(controller.isFlapping());
        controller.started();
        Mockito.when(clock.now()).thenReturn(new Instant(2));
        controller.exited();// total of 2ms of runtime T=2

        Assert.assertTrue(controller.isFlapping());// next time job would start would be at t=7 seconds

        controller.started();
        Mockito.when(clock.now()).thenReturn(new Instant(8));
        controller.exited();// total of 3ms of runtime T=8 (5 of that is throttle)

        Assert.assertTrue(controller.isFlapping());// next time job would start would be at t=13

        controller.started();
        Mockito.when(clock.now()).thenReturn(new Instant(1034));
        controller.exited();// ran 1021ms additionally here, so should disengage flapping T=1034

        Assert.assertFalse(controller.isFlapping());
    }
}

