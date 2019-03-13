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
package io.reactivex.subscribers;


import io.reactivex.exceptions.TestException;
import org.junit.Test;
import org.reactivestreams.Subscription;


public class SafeSubscriberWithPluginTest {
    private final class SubscriptionCancelThrows implements Subscription {
        @Override
        public void cancel() {
            throw new RuntimeException();
        }

        @Override
        public void request(long n) {
        }
    }

    @Test
    public void testOnCompletedThrows2() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onComplete() {
                throw new RuntimeException(new TestException());
            }
        };
        SafeSubscriber<Integer> safe = new SafeSubscriber<Integer>(ts);
        try {
            safe.onComplete();
        } catch (RuntimeException ex) {
            // expected
        }
        // FIXME no longer assertable
        // assertTrue(safe.isUnsubscribed());
    }
}

