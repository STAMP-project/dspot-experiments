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
package io.reactivex.internal.subscribers;


import io.reactivex.TestHelper;
import io.reactivex.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;


public class BasicFuseableSubscriberTest {
    @Test
    public void offerThrows() {
        BasicFuseableSubscriber<Integer, Integer> fcs = new BasicFuseableSubscriber<Integer, Integer>(new io.reactivex.subscribers.TestSubscriber<Integer>(0L)) {
            @Override
            public void onNext(Integer t) {
            }

            @Override
            public int requestFusion(int mode) {
                return 0;
            }

            @Nullable
            @Override
            public Integer poll() throws Exception {
                return null;
            }
        };
        fcs.onSubscribe(new io.reactivex.internal.subscriptions.ScalarSubscription<Integer>(fcs, 1));
        TestHelper.assertNoOffer(fcs);
        Assert.assertFalse(fcs.isEmpty());
        fcs.clear();
        Assert.assertTrue(fcs.isEmpty());
    }
}

