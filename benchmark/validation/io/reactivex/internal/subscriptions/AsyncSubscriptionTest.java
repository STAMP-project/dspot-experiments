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


import io.reactivex.disposables.Disposable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscription;


public class AsyncSubscriptionTest {
    @Test
    public void testNoResource() {
        AsyncSubscription as = new AsyncSubscription();
        Subscription s = Mockito.mock(Subscription.class);
        as.setSubscription(s);
        as.request(1);
        as.cancel();
        Mockito.verify(s).request(1);
        Mockito.verify(s).cancel();
    }

    @Test
    public void testRequestBeforeSet() {
        AsyncSubscription as = new AsyncSubscription();
        Subscription s = Mockito.mock(Subscription.class);
        as.request(1);
        as.setSubscription(s);
        as.cancel();
        Mockito.verify(s).request(1);
        Mockito.verify(s).cancel();
    }

    @Test
    public void testCancelBeforeSet() {
        AsyncSubscription as = new AsyncSubscription();
        Subscription s = Mockito.mock(Subscription.class);
        as.request(1);
        as.cancel();
        as.setSubscription(s);
        Mockito.verify(s, Mockito.never()).request(1);
        Mockito.verify(s).cancel();
    }

    @Test
    public void testSingleSet() {
        AsyncSubscription as = new AsyncSubscription();
        Subscription s = Mockito.mock(Subscription.class);
        as.setSubscription(s);
        Subscription s1 = Mockito.mock(Subscription.class);
        as.setSubscription(s1);
        Assert.assertSame(as.actual.get(), s);
        Mockito.verify(s1).cancel();
    }

    @Test
    public void testInitialResource() {
        Disposable r = Mockito.mock(Disposable.class);
        AsyncSubscription as = new AsyncSubscription(r);
        as.cancel();
        Mockito.verify(r).dispose();
    }

    @Test
    public void testSetResource() {
        AsyncSubscription as = new AsyncSubscription();
        Disposable r = Mockito.mock(Disposable.class);
        Assert.assertTrue(as.setResource(r));
        as.cancel();
        Mockito.verify(r).dispose();
    }

    @Test
    public void testReplaceResource() {
        AsyncSubscription as = new AsyncSubscription();
        Disposable r = Mockito.mock(Disposable.class);
        Assert.assertTrue(as.replaceResource(r));
        as.cancel();
        Mockito.verify(r).dispose();
    }

    @Test
    public void testSetResource2() {
        AsyncSubscription as = new AsyncSubscription();
        Disposable r = Mockito.mock(Disposable.class);
        Assert.assertTrue(as.setResource(r));
        Disposable r2 = Mockito.mock(Disposable.class);
        Assert.assertTrue(as.setResource(r2));
        as.cancel();
        Mockito.verify(r).dispose();
        Mockito.verify(r2).dispose();
    }

    @Test
    public void testReplaceResource2() {
        AsyncSubscription as = new AsyncSubscription();
        Disposable r = Mockito.mock(Disposable.class);
        Assert.assertTrue(as.replaceResource(r));
        Disposable r2 = Mockito.mock(Disposable.class);
        as.replaceResource(r2);
        as.cancel();
        Mockito.verify(r, Mockito.never()).dispose();
        Mockito.verify(r2).dispose();
    }

    @Test
    public void testSetResourceAfterCancel() {
        AsyncSubscription as = new AsyncSubscription();
        as.cancel();
        Disposable r = Mockito.mock(Disposable.class);
        as.setResource(r);
        Mockito.verify(r).dispose();
    }

    @Test
    public void testReplaceResourceAfterCancel() {
        AsyncSubscription as = new AsyncSubscription();
        as.cancel();
        Disposable r = Mockito.mock(Disposable.class);
        as.replaceResource(r);
        Mockito.verify(r).dispose();
    }

    @Test
    public void testCancelOnce() {
        Disposable r = Mockito.mock(Disposable.class);
        AsyncSubscription as = new AsyncSubscription(r);
        Subscription s = Mockito.mock(Subscription.class);
        as.setSubscription(s);
        as.cancel();
        as.cancel();
        as.cancel();
        Mockito.verify(s, Mockito.never()).request(ArgumentMatchers.anyLong());
        Mockito.verify(s).cancel();
        Mockito.verify(r).dispose();
    }

    @Test
    public void disposed() {
        AsyncSubscription as = new AsyncSubscription();
        Assert.assertFalse(as.isDisposed());
        as.dispose();
        Assert.assertTrue(as.isDisposed());
    }
}

