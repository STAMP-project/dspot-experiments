/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;


import Fuseable.NONE;
import Fuseable.SYNC;
import MonoSubscriber.FUSED_CONSUMED;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Operators.MonoSubscriber;
import reactor.core.scheduler.Schedulers;
import reactor.test.subscriber.AssertSubscriber;


public class MonoSubscriberTest {
    @Test
    public void queueSubscriptionSyncRejected() {
        MonoSubscriber<Integer, Integer> ds = new MonoSubscriber(new AssertSubscriber());
        Assert.assertEquals(NONE, ds.requestFusion(SYNC));
    }

    @Test
    public void clear() {
        MonoSubscriber<Integer, Integer> ds = new MonoSubscriber(new AssertSubscriber());
        ds.value = 1;
        ds.clear();
        Assert.assertEquals(FUSED_CONSUMED, ds.state);
        Assert.assertNull(ds.value);
    }

    @Test
    public void completeCancelRace() {
        for (int i = 0; i < 500; i++) {
            final MonoSubscriber<Integer, Integer> ds = new MonoSubscriber(new AssertSubscriber());
            Runnable r1 = () -> ds.complete(1);
            Runnable r2 = ds::cancel;
            MonoSubscriberTest.race(r1, r2, Schedulers.single());
        }
    }

    @Test
    public void requestClearRace() {
        for (int i = 0; i < 5000; i++) {
            AssertSubscriber<Integer> ts = new AssertSubscriber<Integer>(0L);
            final MonoSubscriber<Integer, Integer> ds = new MonoSubscriber(ts);
            ts.onSubscribe(ds);
            ds.complete(1);
            Runnable r1 = () -> ds.request(1);
            Runnable r2 = () -> ds.value = null;
            MonoSubscriberTest.race(r1, r2, Schedulers.single());
            if ((ts.values().size()) >= 1) {
                ts.assertValues(1);
            }
        }
    }

    @Test
    public void requestCancelRace() {
        for (int i = 0; i < 5000; i++) {
            AssertSubscriber<Integer> ts = new AssertSubscriber<>(0L);
            final MonoSubscriber<Integer, Integer> ds = new MonoSubscriber(ts);
            ts.onSubscribe(ds);
            ds.complete(1);
            Runnable r1 = () -> ds.request(1);
            Runnable r2 = ds::cancel;
            MonoSubscriberTest.race(r1, r2, Schedulers.single());
            if ((ts.values().size()) >= 1) {
                ts.assertValues(1);
            }
        }
    }
}

