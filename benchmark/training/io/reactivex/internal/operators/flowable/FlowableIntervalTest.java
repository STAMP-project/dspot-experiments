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
import io.reactivex.internal.operators.flowable.FlowableInterval.IntervalSubscriber;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


public class FlowableIntervalTest {
    @Test(timeout = 2000)
    public void cancel() {
        Flowable.interval(1, TimeUnit.MILLISECONDS, Schedulers.trampoline()).take(10).test().assertResult(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
    }

    @Test
    public void badRequest() {
        TestHelper.assertBadRequestReported(Flowable.interval(1, TimeUnit.MILLISECONDS, Schedulers.trampoline()));
    }

    @Test
    public void cancelledOnRun() {
        TestSubscriber<Long> ts = new TestSubscriber<Long>();
        IntervalSubscriber is = new IntervalSubscriber(ts);
        ts.onSubscribe(is);
        is.cancel();
        is.run();
        ts.assertEmpty();
    }
}

