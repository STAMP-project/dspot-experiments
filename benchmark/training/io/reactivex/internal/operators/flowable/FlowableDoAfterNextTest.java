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


import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.TestHelper;
import io.reactivex.TestSubscriber;
import io.reactivex.exceptions.TestException;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.processors.UnicastProcessor;
import io.reactivex.subscribers.SubscriberFusion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class FlowableDoAfterNextTest {
    final List<Integer> values = new ArrayList<Integer>();

    final Consumer<Integer> afterNext = new Consumer<Integer>() {
        @Override
        public void accept(Integer e) throws Exception {
            values.add((-e));
        }
    };

    final TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
        @Override
        public void onNext(Integer t) {
            super.onNext(t);
            FlowableDoAfterNextTest.this.values.add(t);
        }
    };

    @Test
    public void just() {
        Flowable.just(1).doAfterNext(afterNext).subscribeWith(ts).assertResult(1);
        Assert.assertEquals(Arrays.asList(1, (-1)), values);
    }

    @Test
    public void range() {
        Flowable.range(1, 5).doAfterNext(afterNext).subscribeWith(ts).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList(1, (-1), 2, (-2), 3, (-3), 4, (-4), 5, (-5)), values);
    }

    @Test
    public void error() {
        Flowable.<Integer>error(new TestException()).doAfterNext(afterNext).subscribeWith(ts).assertFailure(TestException.class);
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void empty() {
        Flowable.<Integer>empty().doAfterNext(afterNext).subscribeWith(ts).assertResult();
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void syncFused() {
        TestSubscriber<Integer> ts0 = SubscriberFusion.newTest(SYNC);
        Flowable.range(1, 5).doAfterNext(afterNext).subscribe(ts0);
        SubscriberFusion.assertFusion(ts0, SYNC).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList((-1), (-2), (-3), (-4), (-5)), values);
    }

    @Test
    public void asyncFusedRejected() {
        TestSubscriber<Integer> ts0 = SubscriberFusion.newTest(ASYNC);
        Flowable.range(1, 5).doAfterNext(afterNext).subscribe(ts0);
        SubscriberFusion.assertFusion(ts0, NONE).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList((-1), (-2), (-3), (-4), (-5)), values);
    }

    @Test
    public void asyncFused() {
        TestSubscriber<Integer> ts0 = SubscriberFusion.newTest(ASYNC);
        UnicastProcessor<Integer> up = UnicastProcessor.create();
        TestHelper.emit(up, 1, 2, 3, 4, 5);
        up.doAfterNext(afterNext).subscribe(ts0);
        SubscriberFusion.assertFusion(ts0, ASYNC).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList((-1), (-2), (-3), (-4), (-5)), values);
    }

    @Test(expected = NullPointerException.class)
    public void consumerNull() {
        Flowable.just(1).doAfterNext(null);
    }

    @Test
    public void justConditional() {
        Flowable.just(1).doAfterNext(afterNext).filter(Functions.alwaysTrue()).subscribeWith(ts).assertResult(1);
        Assert.assertEquals(Arrays.asList(1, (-1)), values);
    }

    @Test
    public void rangeConditional() {
        Flowable.range(1, 5).doAfterNext(afterNext).filter(Functions.alwaysTrue()).subscribeWith(ts).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList(1, (-1), 2, (-2), 3, (-3), 4, (-4), 5, (-5)), values);
    }

    @Test
    public void errorConditional() {
        Flowable.<Integer>error(new TestException()).doAfterNext(afterNext).filter(Functions.alwaysTrue()).subscribeWith(ts).assertFailure(TestException.class);
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void emptyConditional() {
        Flowable.<Integer>empty().doAfterNext(afterNext).filter(Functions.alwaysTrue()).subscribeWith(ts).assertResult();
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void syncFusedConditional() {
        TestSubscriber<Integer> ts0 = SubscriberFusion.newTest(SYNC);
        Flowable.range(1, 5).doAfterNext(afterNext).filter(Functions.alwaysTrue()).subscribe(ts0);
        SubscriberFusion.assertFusion(ts0, SYNC).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList((-1), (-2), (-3), (-4), (-5)), values);
    }

    @Test
    public void asyncFusedRejectedConditional() {
        TestSubscriber<Integer> ts0 = SubscriberFusion.newTest(ASYNC);
        Flowable.range(1, 5).doAfterNext(afterNext).filter(Functions.alwaysTrue()).subscribe(ts0);
        SubscriberFusion.assertFusion(ts0, NONE).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList((-1), (-2), (-3), (-4), (-5)), values);
    }

    @Test
    public void asyncFusedConditional() {
        TestSubscriber<Integer> ts0 = SubscriberFusion.newTest(ASYNC);
        UnicastProcessor<Integer> up = UnicastProcessor.create();
        TestHelper.emit(up, 1, 2, 3, 4, 5);
        up.doAfterNext(afterNext).filter(Functions.alwaysTrue()).subscribe(ts0);
        SubscriberFusion.assertFusion(ts0, ASYNC).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList((-1), (-2), (-3), (-4), (-5)), values);
    }

    @Test
    public void consumerThrows() {
        Flowable.just(1, 2).doAfterNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer e) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class, 1);
    }

    @Test
    public void consumerThrowsConditional() {
        Flowable.just(1, 2).doAfterNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer e) throws Exception {
                throw new TestException();
            }
        }).filter(Functions.alwaysTrue()).test().assertFailure(TestException.class, 1);
    }

    @Test
    public void consumerThrowsConditional2() {
        Flowable.just(1, 2).hide().doAfterNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer e) throws Exception {
                throw new TestException();
            }
        }).filter(Functions.alwaysTrue()).test().assertFailure(TestException.class, 1);
    }
}

