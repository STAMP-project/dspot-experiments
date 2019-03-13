/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package io.reactivex.internal.schedulers;


import Functions.EMPTY_RUNNABLE;
import ImmediateThinScheduler.INSTANCE;
import io.reactivex.Scheduler.Worker;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class ImmediateThinSchedulerTest {
    @Test
    public void scheduleDirect() {
        final int[] count = new int[]{ 0 };
        INSTANCE.scheduleDirect(new Runnable() {
            @Override
            public void run() {
                (count[0])++;
            }
        });
        Assert.assertEquals(1, count[0]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void scheduleDirectTimed() {
        INSTANCE.scheduleDirect(EMPTY_RUNNABLE, 1, TimeUnit.SECONDS);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void scheduleDirectPeriodic() {
        INSTANCE.schedulePeriodicallyDirect(EMPTY_RUNNABLE, 1, 1, TimeUnit.SECONDS);
    }

    @Test
    public void schedule() {
        final int[] count = new int[]{ 0 };
        Worker w = INSTANCE.createWorker();
        Assert.assertFalse(w.isDisposed());
        w.schedule(new Runnable() {
            @Override
            public void run() {
                (count[0])++;
            }
        });
        Assert.assertEquals(1, count[0]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void scheduleTimed() {
        INSTANCE.createWorker().schedule(EMPTY_RUNNABLE, 1, TimeUnit.SECONDS);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void schedulePeriodic() {
        INSTANCE.createWorker().schedulePeriodically(EMPTY_RUNNABLE, 1, 1, TimeUnit.SECONDS);
    }
}

