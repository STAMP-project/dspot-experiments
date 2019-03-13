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
package io.reactivex.internal.operators.flowable;


import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.internal.util.NotificationLite;
import org.junit.Assert;
import org.junit.Test;


// TODO this test is no longer relevant as nulls are not allowed and value maps to itself
// @Test
// public void testValueKind() {
// assertTrue(NotificationLite.isNull(NotificationLite.next(null)));
// assertFalse(NotificationLite.isNull(NotificationLite.next(1)));
// assertFalse(NotificationLite.isNull(NotificationLite.error(new TestException())));
// assertFalse(NotificationLite.isNull(NotificationLite.completed()));
// assertFalse(NotificationLite.isNull(null));
// 
// assertTrue(NotificationLite.isNext(NotificationLite.next(null)));
// assertTrue(NotificationLite.isNext(NotificationLite.next(1)));
// assertFalse(NotificationLite.isNext(NotificationLite.completed()));
// assertFalse(NotificationLite.isNext(null));
// assertFalse(NotificationLite.isNext(NotificationLite.error(new TestException())));
// }
public class NotificationLiteTest {
    @Test
    public void testComplete() {
        Object n = NotificationLite.next("Hello");
        Object c = NotificationLite.complete();
        Assert.assertTrue(NotificationLite.isComplete(c));
        Assert.assertFalse(NotificationLite.isComplete(n));
        Assert.assertEquals("Hello", NotificationLite.getValue(n));
    }

    @Test
    public void testValueKind() {
        Assert.assertSame(1, NotificationLite.next(1));
    }

    @Test
    public void soloEnum() {
        TestHelper.checkEnum(NotificationLite.class);
    }

    @Test
    public void errorNotification() {
        Object o = NotificationLite.error(new TestException());
        Assert.assertEquals("NotificationLite.Error[io.reactivex.exceptions.TestException]", o.toString());
        Assert.assertTrue(NotificationLite.isError(o));
        Assert.assertFalse(NotificationLite.isComplete(o));
        Assert.assertFalse(NotificationLite.isDisposable(o));
        Assert.assertFalse(NotificationLite.isSubscription(o));
        Assert.assertTrue(((NotificationLite.getError(o)) instanceof TestException));
    }

    @Test
    public void completeNotification() {
        Object o = NotificationLite.complete();
        Object o2 = NotificationLite.complete();
        Assert.assertSame(o, o2);
        Assert.assertFalse(NotificationLite.isError(o));
        Assert.assertTrue(NotificationLite.isComplete(o));
        Assert.assertFalse(NotificationLite.isDisposable(o));
        Assert.assertFalse(NotificationLite.isSubscription(o));
        Assert.assertEquals("NotificationLite.Complete", o.toString());
        Assert.assertTrue(NotificationLite.isComplete(o));
    }

    @Test
    public void disposableNotification() {
        Object o = NotificationLite.disposable(Disposables.empty());
        Assert.assertEquals("NotificationLite.Disposable[RunnableDisposable(disposed=false, EmptyRunnable)]", o.toString());
        Assert.assertFalse(NotificationLite.isError(o));
        Assert.assertFalse(NotificationLite.isComplete(o));
        Assert.assertTrue(NotificationLite.isDisposable(o));
        Assert.assertFalse(NotificationLite.isSubscription(o));
        Assert.assertNotNull(NotificationLite.getDisposable(o));
    }

    @Test
    public void subscriptionNotification() {
        Object o = NotificationLite.subscription(new BooleanSubscription());
        Assert.assertEquals("NotificationLite.Subscription[BooleanSubscription(cancelled=false)]", o.toString());
        Assert.assertFalse(NotificationLite.isError(o));
        Assert.assertFalse(NotificationLite.isComplete(o));
        Assert.assertFalse(NotificationLite.isDisposable(o));
        Assert.assertTrue(NotificationLite.isSubscription(o));
        Assert.assertNotNull(NotificationLite.getSubscription(o));
    }
}

