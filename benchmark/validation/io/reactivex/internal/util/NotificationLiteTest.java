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
package io.reactivex.internal.util;


import io.reactivex.exceptions.TestException;
import io.reactivex.observers.TestObserver;
import org.junit.Assert;
import org.junit.Test;


public class NotificationLiteTest {
    @Test
    public void acceptFullObserver() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Disposable d = Disposables.empty();
        Assert.assertFalse(NotificationLite.acceptFull(NotificationLite.disposable(d), to));
        to.assertSubscribed();
        to.cancel();
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void errorNotificationCompare() {
        TestException ex = new TestException();
        Object n1 = NotificationLite.error(ex);
        Assert.assertEquals(ex.hashCode(), n1.hashCode());
        Assert.assertFalse(n1.equals(NotificationLite.complete()));
    }
}

