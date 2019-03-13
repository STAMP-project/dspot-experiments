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


import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import org.junit.Assert;
import org.junit.Test;


public class ObservableForEachTest {
    @Test
    public void forEachWile() {
        final List<Object> list = new ArrayList<Object>();
        range(1, 5).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                list.add(v);
            }
        }).forEachWhile(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                return v < 3;
            }
        });
        Assert.assertEquals(Arrays.asList(1, 2, 3), list);
    }

    @Test
    public void forEachWileWithError() {
        final List<Object> list = new ArrayList<Object>();
        range(1, 5).concatWith(<Integer>error(new TestException())).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                list.add(v);
            }
        }).forEachWhile(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                return true;
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                list.add(100);
            }
        });
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 100), list);
    }

    @Test
    public void badSource() {
        TestHelper.checkBadSourceObservable(new Function<Observable<Integer>, Object>() {
            @Override
            public Object apply(Observable<Integer> f) throws Exception {
                return f.forEachWhile(Functions.alwaysTrue());
            }
        }, false, 1, 1, ((Object[]) (null)));
    }

    @Test
    public void dispose() {
        PublishSubject<Integer> ps = PublishSubject.create();
        Disposable d = ps.forEachWhile(Functions.alwaysTrue());
        Assert.assertFalse(d.isDisposed());
        d.dispose();
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void whilePredicateThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            just(1).forEachWhile(new Predicate<Integer>() {
                @Override
                public boolean test(Integer v) throws Exception {
                    throw new TestException();
                }
            });
            TestHelper.assertError(errors, 0, OnErrorNotImplementedException.class);
            Throwable c = errors.get(0).getCause();
            Assert.assertTrue(("" + c), (c instanceof TestException));
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void whileErrorThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            <Integer>error(new TestException("Outer")).forEachWhile(Functions.alwaysTrue(), new Consumer<Throwable>() {
                @Override
                public void accept(Throwable v) throws Exception {
                    throw new TestException("Inner");
                }
            });
            TestHelper.assertError(errors, 0, CompositeException.class);
            List<Throwable> ce = TestHelper.compositeList(errors.get(0));
            TestHelper.assertError(ce, 0, TestException.class, "Outer");
            TestHelper.assertError(ce, 1, TestException.class, "Inner");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void whileCompleteThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            just(1).forEachWhile(Functions.alwaysTrue(), Functions.emptyConsumer(), new Action() {
                @Override
                public void run() throws Exception {
                    throw new TestException();
                }
            });
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

