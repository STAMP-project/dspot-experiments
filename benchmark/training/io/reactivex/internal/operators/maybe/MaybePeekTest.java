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
package io.reactivex.internal.operators.maybe;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class MaybePeekTest {
    @Test
    public void disposed() {
        TestHelper.checkDisposed(PublishProcessor.create().singleElement().doOnSuccess(Functions.emptyConsumer()));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeMaybe(new Function<Maybe<Object>, MaybeSource<Object>>() {
            @Override
            public io.reactivex.MaybeSource<Object> apply(Maybe<Object> m) throws Exception {
                return m.doOnSuccess(Functions.emptyConsumer());
            }
        });
    }

    @Test
    public void doubleError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        final Throwable[] err = new Throwable[]{ null };
        try {
            TestObserver<Integer> to = doOnError(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable e) throws Exception {
                    err[0] = e;
                }
            }).test();
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
            Assert.assertTrue(("" + err), ((err[0]) instanceof TestException));
            Assert.assertEquals("First", err[0].getMessage());
            to.assertFailureAndMessage(TestException.class, "First");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void doubleComplete() {
        final int[] compl = new int[]{ 0 };
        TestObserver<Integer> to = doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                (compl[0])++;
            }
        }).test();
        Assert.assertEquals(1, compl[0]);
        to.assertResult();
    }

    @Test
    public void doOnErrorThrows() {
        TestObserver<Object> to = Maybe.error(new TestException("Main")).doOnError(new Consumer<Object>() {
            @Override
            public void accept(Object t) throws Exception {
                throw new TestException("Inner");
            }
        }).test();
        to.assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(to.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "Main");
        TestHelper.assertError(errors, 1, TestException.class, "Inner");
    }

    @Test
    public void afterTerminateThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Maybe.just(1).doAfterTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    throw new TestException();
                }
            }).test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

