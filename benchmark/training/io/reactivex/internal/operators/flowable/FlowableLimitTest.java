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
import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class FlowableLimitTest implements Action , LongConsumer {
    final List<Long> requests = new ArrayList<Long>();

    static final Long CANCELLED = -100L;

    @Test
    public void shorterSequence() {
        limit(6).test().assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(6, requests.get(0).intValue());
    }

    @Test
    public void exactSequence() {
        limit(5).test().assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(2, requests.size());
        Assert.assertEquals(5, requests.get(0).intValue());
        Assert.assertEquals(FlowableLimitTest.CANCELLED, requests.get(1));
    }

    @Test
    public void longerSequence() {
        limit(5).test().assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(5, requests.get(0).intValue());
    }

    @Test
    public void error() {
        limit(5).test().assertFailure(TestException.class);
    }

    @Test
    public void limitZero() {
        limit(0).test().assertResult();
        Assert.assertEquals(1, requests.size());
        Assert.assertEquals(FlowableLimitTest.CANCELLED, requests.get(0));
    }

    @Test
    public void limitStep() {
        TestSubscriber<Integer> ts = limit(5).test(0L);
        Assert.assertEquals(0, requests.size());
        ts.request(1);
        ts.assertValue(1);
        ts.request(2);
        ts.assertValues(1, 2, 3);
        ts.request(3);
        ts.assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList(1L, 2L, 2L), requests);
    }

    @Test
    public void limitAndTake() {
        limit(6).take(5).test().assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList(6L, FlowableLimitTest.CANCELLED), requests);
    }

    @Test
    public void noOverrequest() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = limit(5).test(0L);
        ts.request(5);
        ts.request(10);
        Assert.assertTrue(pp.offer(1));
        pp.onComplete();
        ts.assertResult(1);
    }

    @Test
    public void cancelIgnored() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            limit(0).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
            TestHelper.assertError(errors, 1, NullPointerException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void badRequest() {
        TestHelper.assertBadRequestReported(limit(3));
    }

    @Test
    public void requestRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final TestSubscriber<Integer> ts = limit(5).test(0L);
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    ts.request(3);
                }
            };
            TestHelper.race(r, r);
            ts.assertResult(1, 2, 3, 4, 5);
        }
    }

    @Test
    public void errorAfterLimitReached() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            limit(0).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

