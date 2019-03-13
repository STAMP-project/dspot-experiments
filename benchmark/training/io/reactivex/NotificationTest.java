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
package io.reactivex;


import io.reactivex.exceptions.TestException;
import org.junit.Assert;
import org.junit.Test;


public class NotificationTest {
    @Test
    public void valueOfOnErrorIsNull() {
        Notification<Integer> notification = Notification.createOnError(new TestException());
        Assert.assertNull(notification.getValue());
        Assert.assertTrue(notification.getError().toString(), ((notification.getError()) instanceof TestException));
    }

    @Test
    public void valueOfOnCompleteIsNull() {
        Notification<Integer> notification = Notification.createOnComplete();
        Assert.assertNull(notification.getValue());
        Assert.assertNull(notification.getError());
        Assert.assertTrue(notification.isOnComplete());
    }

    @Test
    public void notEqualsToObject() {
        Notification<Integer> n1 = Notification.createOnNext(0);
        Assert.assertFalse(n1.equals(0));
        Notification<Integer> n2 = Notification.createOnError(new TestException());
        Assert.assertFalse(n2.equals(0));
        Notification<Integer> n3 = Notification.createOnComplete();
        Assert.assertFalse(n3.equals(0));
    }

    @Test
    public void hashCodeIsTheInner() {
        Notification<Integer> n1 = Notification.createOnNext(1337);
        Assert.assertEquals(Integer.valueOf(1337).hashCode(), n1.hashCode());
        Assert.assertEquals(0, Notification.createOnComplete().hashCode());
    }

    @Test
    public void toStringPattern() {
        Assert.assertEquals("OnNextNotification[1]", Notification.createOnNext(1).toString());
        Assert.assertEquals("OnErrorNotification[io.reactivex.exceptions.TestException]", Notification.createOnError(new TestException()).toString());
        Assert.assertEquals("OnCompleteNotification", Notification.createOnComplete().toString());
    }
}

