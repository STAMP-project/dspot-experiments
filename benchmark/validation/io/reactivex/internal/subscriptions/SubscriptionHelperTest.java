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
package io.reactivex.internal.subscriptions;


import SubscriptionHelper.CANCELLED;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.ProtocolViolationException;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscription;

import static org.mockito.ArgumentMatchers.anyLong;


public class SubscriptionHelperTest {
    @Test
    public void checkEnum() {
        TestHelper.checkEnum(SubscriptionHelper.class);
    }

    @Test
    public void validateNullThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            SubscriptionHelper.validate(null, null);
            TestHelper.assertError(errors, 0, NullPointerException.class, "next is null");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void cancelNoOp() {
        CANCELLED.cancel();
    }

    @Test
    public void set() {
        AtomicReference<Subscription> atomicSubscription = new AtomicReference<Subscription>();
        BooleanSubscription bs1 = new BooleanSubscription();
        Assert.assertTrue(SubscriptionHelper.set(atomicSubscription, bs1));
        BooleanSubscription bs2 = new BooleanSubscription();
        Assert.assertTrue(SubscriptionHelper.set(atomicSubscription, bs2));
        Assert.assertTrue(bs1.isCancelled());
        Assert.assertFalse(bs2.isCancelled());
    }

    @Test
    public void replace() {
        AtomicReference<Subscription> atomicSubscription = new AtomicReference<Subscription>();
        BooleanSubscription bs1 = new BooleanSubscription();
        Assert.assertTrue(SubscriptionHelper.replace(atomicSubscription, bs1));
        BooleanSubscription bs2 = new BooleanSubscription();
        Assert.assertTrue(SubscriptionHelper.replace(atomicSubscription, bs2));
        Assert.assertFalse(bs1.isCancelled());
        Assert.assertFalse(bs2.isCancelled());
    }

    @Test
    public void cancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final AtomicReference<Subscription> atomicSubscription = new AtomicReference<Subscription>();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    SubscriptionHelper.cancel(atomicSubscription);
                }
            };
            TestHelper.race(r, r);
        }
    }

    @Test
    public void setRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final AtomicReference<Subscription> atomicSubscription = new AtomicReference<Subscription>();
            final BooleanSubscription bs1 = new BooleanSubscription();
            final BooleanSubscription bs2 = new BooleanSubscription();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    SubscriptionHelper.set(atomicSubscription, bs1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    SubscriptionHelper.set(atomicSubscription, bs2);
                }
            };
            TestHelper.race(r1, r2);
            Assert.assertTrue(((bs1.isCancelled()) ^ (bs2.isCancelled())));
        }
    }

    @Test
    public void replaceRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final AtomicReference<Subscription> atomicSubscription = new AtomicReference<Subscription>();
            final BooleanSubscription bs1 = new BooleanSubscription();
            final BooleanSubscription bs2 = new BooleanSubscription();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    SubscriptionHelper.replace(atomicSubscription, bs1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    SubscriptionHelper.replace(atomicSubscription, bs2);
                }
            };
            TestHelper.race(r1, r2);
            Assert.assertFalse(bs1.isCancelled());
            Assert.assertFalse(bs2.isCancelled());
        }
    }

    @Test
    public void cancelAndChange() {
        AtomicReference<Subscription> atomicSubscription = new AtomicReference<Subscription>();
        SubscriptionHelper.cancel(atomicSubscription);
        BooleanSubscription bs1 = new BooleanSubscription();
        Assert.assertFalse(SubscriptionHelper.set(atomicSubscription, bs1));
        Assert.assertTrue(bs1.isCancelled());
        Assert.assertFalse(SubscriptionHelper.set(atomicSubscription, null));
        BooleanSubscription bs2 = new BooleanSubscription();
        Assert.assertFalse(SubscriptionHelper.replace(atomicSubscription, bs2));
        Assert.assertTrue(bs2.isCancelled());
        Assert.assertFalse(SubscriptionHelper.replace(atomicSubscription, null));
    }

    @Test
    public void invalidDeferredRequest() {
        AtomicReference<Subscription> atomicSubscription = new AtomicReference<Subscription>();
        AtomicLong r = new AtomicLong();
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            SubscriptionHelper.deferredRequest(atomicSubscription, r, (-99));
            TestHelper.assertError(errors, 0, IllegalArgumentException.class, "n > 0 required but it was -99");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void deferredRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final AtomicReference<Subscription> atomicSubscription = new AtomicReference<Subscription>();
            final AtomicLong r = new AtomicLong();
            final AtomicLong q = new AtomicLong();
            final Subscription a = new Subscription() {
                @Override
                public void request(long n) {
                    q.addAndGet(n);
                }

                @Override
                public void cancel() {
                }
            };
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    SubscriptionHelper.deferredSetOnce(atomicSubscription, r, a);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    SubscriptionHelper.deferredRequest(atomicSubscription, r, 1);
                }
            };
            TestHelper.race(r1, r2);
            Assert.assertSame(a, atomicSubscription.get());
            Assert.assertEquals(1, q.get());
            Assert.assertEquals(0, r.get());
        }
    }

    @Test
    public void setOnceAndRequest() {
        AtomicReference<Subscription> ref = new AtomicReference<Subscription>();
        Subscription sub = Mockito.mock(Subscription.class);
        Assert.assertTrue(SubscriptionHelper.setOnce(ref, sub, 1));
        Mockito.verify(sub).request(1);
        Mockito.verify(sub, Mockito.never()).cancel();
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            sub = Mockito.mock(Subscription.class);
            Assert.assertFalse(SubscriptionHelper.setOnce(ref, sub, 1));
            Mockito.verify(sub, Mockito.never()).request(anyLong());
            Mockito.verify(sub).cancel();
            TestHelper.assertError(errors, 0, ProtocolViolationException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

