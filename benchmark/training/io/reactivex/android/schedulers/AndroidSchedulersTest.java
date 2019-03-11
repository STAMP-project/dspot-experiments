/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.android.schedulers;


import Build.VERSION;
import android.os.Looper;
import android.os.Message;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.android.testutil.EmptyScheduler;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowMessageQueue;
import org.robolectric.util.ReflectionHelpers;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public final class AndroidSchedulersTest {
    @Test
    public void mainThreadCallsThroughToHook() {
        final AtomicInteger called = new AtomicInteger();
        final Scheduler newScheduler = new EmptyScheduler();
        RxAndroidPlugins.setMainThreadSchedulerHandler(new io.reactivex.functions.Function<Scheduler, Scheduler>() {
            @Override
            public Scheduler apply(Scheduler scheduler) {
                called.getAndIncrement();
                return newScheduler;
            }
        });
        Assert.assertSame(newScheduler, AndroidSchedulers.mainThread());
        Assert.assertEquals(1, called.get());
        Assert.assertSame(newScheduler, AndroidSchedulers.mainThread());
        Assert.assertEquals(2, called.get());
    }

    @Test
    public void fromNullThrows() {
        try {
            AndroidSchedulers.from(null);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("looper == null", e.getMessage());
        }
    }

    @Test
    public void fromNullThrowsTwoArg() {
        try {
            AndroidSchedulers.from(null, false);
            Assert.fail();
        } catch (NullPointerException e) {
            Assert.assertEquals("looper == null", e.getMessage());
        }
    }

    @Test
    public void fromReturnsUsableScheduler() {
        Assert.assertNotNull(AndroidSchedulers.from(Looper.getMainLooper()));
    }

    @Test
    public void asyncIgnoredPre16() {
        ReflectionHelpers.setStaticField(VERSION.class, "SDK_INT", 14);
        ShadowLooper mainLooper = ShadowLooper.getShadowMainLooper();
        mainLooper.pause();
        ShadowMessageQueue mainMessageQueue = shadowOf(Looper.getMainLooper().getQueue());
        Scheduler main = AndroidSchedulers.from(Looper.getMainLooper(), true);
        main.scheduleDirect(new Runnable() {
            @Override
            public void run() {
            }
        });
        Message message = mainMessageQueue.getHead();
        Assert.assertFalse(message.isAsynchronous());
    }
}

