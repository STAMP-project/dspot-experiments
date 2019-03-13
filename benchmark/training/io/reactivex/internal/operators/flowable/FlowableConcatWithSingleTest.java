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


import io.reactivex.exceptions.TestException;
import io.reactivex.subjects.SingleSubject;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Assert;
import org.junit.Test;


public class FlowableConcatWithSingleTest {
    @Test
    public void normal() {
        final TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.range(1, 5).concatWith(Single.just(100)).subscribe(ts);
        ts.assertResult(1, 2, 3, 4, 5, 100);
    }

    @Test
    public void backpressure() {
        Flowable.range(1, 5).concatWith(Single.just(100)).test(0).assertEmpty().requestMore(3).assertValues(1, 2, 3).requestMore(2).assertValues(1, 2, 3, 4, 5).requestMore(1).assertResult(1, 2, 3, 4, 5, 100);
    }

    @Test
    public void mainError() {
        final TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.<Integer>error(new TestException()).concatWith(Single.just(100)).subscribe(ts);
        ts.assertFailure(TestException.class);
    }

    @Test
    public void otherError() {
        final TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.range(1, 5).concatWith(Single.<Integer>error(new TestException())).subscribe(ts);
        ts.assertFailure(TestException.class, 1, 2, 3, 4, 5);
    }

    @Test
    public void takeMain() {
        final TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.range(1, 5).concatWith(Single.just(100)).take(3).subscribe(ts);
        ts.assertResult(1, 2, 3);
    }

    @Test
    public void cancelOther() {
        SingleSubject<Object> other = SingleSubject.create();
        TestSubscriber<Object> ts = Flowable.empty().concatWith(other).test();
        Assert.assertTrue(other.hasObservers());
        ts.cancel();
        Assert.assertFalse(other.hasObservers());
    }
}

