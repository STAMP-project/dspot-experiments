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
package io.reactivex.internal.operators.observable;


import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.ObserverFusion;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.UnicastSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ObservableDoAfterNextTest {
    final List<Integer> values = new ArrayList<Integer>();

    final Consumer<Integer> afterNext = new Consumer<Integer>() {
        @Override
        public void accept(Integer e) throws Exception {
            values.add((-e));
        }
    };

    final TestObserver<Integer> to = new TestObserver<Integer>() {
        @Override
        public void onNext(Integer t) {
            super.onNext(t);
            ObservableDoAfterNextTest.this.values.add(t);
        }
    };

    @Test
    public void just() {
        java.util.Observable.just(1).doAfterNext(afterNext).subscribeWith(to).assertResult(1);
        Assert.assertEquals(Arrays.asList(1, (-1)), values);
    }

    @Test
    public void justHidden() {
        java.util.Observable.just(1).hide().doAfterNext(afterNext).subscribeWith(to).assertResult(1);
        Assert.assertEquals(Arrays.asList(1, (-1)), values);
    }

    @Test
    public void range() {
        java.util.Observable.range(1, 5).doAfterNext(afterNext).subscribeWith(to).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList(1, (-1), 2, (-2), 3, (-3), 4, (-4), 5, (-5)), values);
    }

    @Test
    public void error() {
        java.util.Observable.<Integer>error(new TestException()).doAfterNext(afterNext).subscribeWith(to).assertFailure(TestException.class);
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void empty() {
        java.util.Observable.<Integer>empty().doAfterNext(afterNext).subscribeWith(to).assertResult();
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void syncFused() {
        TestObserver<Integer> to0 = ObserverFusion.newTest(SYNC);
        java.util.Observable.range(1, 5).doAfterNext(afterNext).subscribe(to0);
        ObserverFusion.assertFusion(to0, SYNC).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList((-1), (-2), (-3), (-4), (-5)), values);
    }

    @Test
    public void asyncFusedRejected() {
        TestObserver<Integer> to0 = ObserverFusion.newTest(ASYNC);
        java.util.Observable.range(1, 5).doAfterNext(afterNext).subscribe(to0);
        ObserverFusion.assertFusion(to0, NONE).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList((-1), (-2), (-3), (-4), (-5)), values);
    }

    @Test
    public void asyncFused() {
        TestObserver<Integer> to0 = ObserverFusion.newTest(ASYNC);
        UnicastSubject<Integer> up = UnicastSubject.create();
        TestHelper.emit(up, 1, 2, 3, 4, 5);
        up.doAfterNext(afterNext).subscribe(to0);
        ObserverFusion.assertFusion(to0, ASYNC).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList((-1), (-2), (-3), (-4), (-5)), values);
    }

    @Test(expected = NullPointerException.class)
    public void consumerNull() {
        java.util.Observable.just(1).doAfterNext(null);
    }

    @Test
    public void justConditional() {
        java.util.Observable.just(1).doAfterNext(afterNext).filter(Functions.alwaysTrue()).subscribeWith(to).assertResult(1);
        Assert.assertEquals(Arrays.asList(1, (-1)), values);
    }

    @Test
    public void rangeConditional() {
        java.util.Observable.range(1, 5).doAfterNext(afterNext).filter(Functions.alwaysTrue()).subscribeWith(to).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList(1, (-1), 2, (-2), 3, (-3), 4, (-4), 5, (-5)), values);
    }

    @Test
    public void errorConditional() {
        java.util.Observable.<Integer>error(new TestException()).doAfterNext(afterNext).filter(Functions.alwaysTrue()).subscribeWith(to).assertFailure(TestException.class);
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void emptyConditional() {
        java.util.Observable.<Integer>empty().doAfterNext(afterNext).filter(Functions.alwaysTrue()).subscribeWith(to).assertResult();
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void syncFusedConditional() {
        TestObserver<Integer> to0 = ObserverFusion.newTest(SYNC);
        java.util.Observable.range(1, 5).doAfterNext(afterNext).filter(Functions.alwaysTrue()).subscribe(to0);
        ObserverFusion.assertFusion(to0, SYNC).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList((-1), (-2), (-3), (-4), (-5)), values);
    }

    @Test
    public void asyncFusedRejectedConditional() {
        TestObserver<Integer> to0 = ObserverFusion.newTest(ASYNC);
        java.util.Observable.range(1, 5).doAfterNext(afterNext).filter(Functions.alwaysTrue()).subscribe(to0);
        ObserverFusion.assertFusion(to0, NONE).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList((-1), (-2), (-3), (-4), (-5)), values);
    }

    @Test
    public void asyncFusedConditional() {
        TestObserver<Integer> to0 = ObserverFusion.newTest(ASYNC);
        UnicastSubject<Integer> up = UnicastSubject.create();
        TestHelper.emit(up, 1, 2, 3, 4, 5);
        up.doAfterNext(afterNext).filter(Functions.alwaysTrue()).subscribe(to0);
        ObserverFusion.assertFusion(to0, ASYNC).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(Arrays.asList((-1), (-2), (-3), (-4), (-5)), values);
    }

    @Test
    public void consumerThrows() {
        java.util.Observable.just(1, 2).doAfterNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer e) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class, 1);
    }

    @Test
    public void consumerThrowsConditional() {
        java.util.Observable.just(1, 2).doAfterNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer e) throws Exception {
                throw new TestException();
            }
        }).filter(Functions.alwaysTrue()).test().assertFailure(TestException.class, 1);
    }

    @Test
    public void consumerThrowsConditional2() {
        java.util.Observable.just(1, 2).hide().doAfterNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer e) throws Exception {
                throw new TestException();
            }
        }).filter(Functions.alwaysTrue()).test().assertFailure(TestException.class, 1);
    }
}

