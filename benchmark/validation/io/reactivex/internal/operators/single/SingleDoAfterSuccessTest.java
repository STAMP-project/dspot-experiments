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
package io.reactivex.internal.operators.single;


import io.reactivex.Consumer;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SingleDoAfterSuccessTest {
    final List<Integer> values = new ArrayList<Integer>();

    final Consumer<Integer> afterSuccess = new Consumer<Integer>() {
        @Override
        public void accept(Integer e) throws Exception {
            values.add((-e));
        }
    };

    final TestObserver<Integer> to = new TestObserver<Integer>() {
        @Override
        public void onNext(Integer t) {
            super.onNext(t);
            SingleDoAfterSuccessTest.this.values.add(t);
        }
    };

    @Test
    public void just() {
        Single.just(1).doAfterSuccess(afterSuccess).subscribeWith(to).assertResult(1);
        Assert.assertEquals(Arrays.asList(1, (-1)), values);
    }

    @Test
    public void error() {
        Single.<Integer>error(new TestException()).doAfterSuccess(afterSuccess).subscribeWith(to).assertFailure(TestException.class);
        Assert.assertTrue(values.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void consumerNull() {
        Single.just(1).doAfterSuccess(null);
    }

    @Test
    public void justConditional() {
        Single.just(1).doAfterSuccess(afterSuccess).filter(Functions.alwaysTrue()).subscribeWith(to).assertResult(1);
        Assert.assertEquals(Arrays.asList(1, (-1)), values);
    }

    @Test
    public void errorConditional() {
        Single.<Integer>error(new TestException()).doAfterSuccess(afterSuccess).filter(Functions.alwaysTrue()).subscribeWith(to).assertFailure(TestException.class);
        Assert.assertTrue(values.isEmpty());
    }

    @Test
    public void consumerThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Single.just(1).doAfterSuccess(new Consumer<Integer>() {
                @Override
                public void accept(Integer e) throws Exception {
                    throw new TestException();
                }
            }).test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishSubject.<Integer>create().singleOrError().doAfterSuccess(afterSuccess));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeSingle(new Function<Single<Integer>, SingleSource<Integer>>() {
            @Override
            public io.reactivex.SingleSource<Integer> apply(Single<Integer> m) throws Exception {
                return m.doAfterSuccess(afterSuccess);
            }
        });
    }
}

