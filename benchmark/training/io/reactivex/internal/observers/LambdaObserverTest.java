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
package io.reactivex.internal.observers;


import io.reactivex.TestHelper;
import io.reactivex.disposables.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import org.junit.Assert;
import org.junit.Test;


public class LambdaObserverTest {
    @Test
    public void onSubscribeThrows() {
        final List<Object> received = new ArrayList<Object>();
        LambdaObserver<Object> o = new LambdaObserver<Object>(new Consumer<Object>() {
            @Override
            public void accept(Object v) throws Exception {
                received.add(v);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                received.add(e);
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                received.add(100);
            }
        }, new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) throws Exception {
                throw new TestException();
            }
        });
        Assert.assertFalse(o.isDisposed());
        just(1).subscribe(o);
        Assert.assertTrue(received.toString(), ((received.get(0)) instanceof TestException));
        Assert.assertEquals(received.toString(), 1, received.size());
        Assert.assertTrue(o.isDisposed());
    }

    @Test
    public void onNextThrows() {
        final List<Object> received = new ArrayList<Object>();
        LambdaObserver<Object> o = new LambdaObserver<Object>(new Consumer<Object>() {
            @Override
            public void accept(Object v) throws Exception {
                throw new TestException();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                received.add(e);
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                received.add(100);
            }
        }, new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) throws Exception {
            }
        });
        Assert.assertFalse(o.isDisposed());
        just(1).subscribe(o);
        Assert.assertTrue(received.toString(), ((received.get(0)) instanceof TestException));
        Assert.assertEquals(received.toString(), 1, received.size());
        Assert.assertTrue(o.isDisposed());
    }

    @Test
    public void onErrorThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final List<Object> received = new ArrayList<Object>();
            LambdaObserver<Object> o = new LambdaObserver<Object>(new Consumer<Object>() {
                @Override
                public void accept(Object v) throws Exception {
                    received.add(v);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable e) throws Exception {
                    throw new TestException("Inner");
                }
            }, new Action() {
                @Override
                public void run() throws Exception {
                    received.add(100);
                }
            }, new Consumer<Disposable>() {
                @Override
                public void accept(Disposable d) throws Exception {
                }
            });
            Assert.assertFalse(o.isDisposed());
            <Integer>error(new TestException("Outer")).subscribe(o);
            Assert.assertTrue(received.toString(), received.isEmpty());
            Assert.assertTrue(o.isDisposed());
            TestHelper.assertError(errors, 0, CompositeException.class);
            List<Throwable> ce = TestHelper.compositeList(errors.get(0));
            TestHelper.assertError(ce, 0, TestException.class, "Outer");
            TestHelper.assertError(ce, 1, TestException.class, "Inner");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final List<Object> received = new ArrayList<Object>();
            LambdaObserver<Object> o = new LambdaObserver<Object>(new Consumer<Object>() {
                @Override
                public void accept(Object v) throws Exception {
                    received.add(v);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable e) throws Exception {
                    received.add(e);
                }
            }, new Action() {
                @Override
                public void run() throws Exception {
                    throw new TestException();
                }
            }, new Consumer<Disposable>() {
                @Override
                public void accept(Disposable d) throws Exception {
                }
            });
            Assert.assertFalse(o.isDisposed());
            <Integer>empty().subscribe(o);
            Assert.assertTrue(received.toString(), received.isEmpty());
            Assert.assertTrue(o.isDisposed());
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void badSourceOnSubscribe() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Observable<Integer> source = new Observable<Integer>() {
                @Override
                public void subscribeActual(Observer<? extends Integer> observer) {
                    Disposable d1 = Disposables.empty();
                    observer.onSubscribe(d1);
                    Disposable d2 = Disposables.empty();
                    observer.onSubscribe(d2);
                    assertFalse(d1.isDisposed());
                    assertTrue(d2.isDisposed());
                    observer.onNext(1);
                    observer.onComplete();
                }
            };
            final List<Object> received = new ArrayList<Object>();
            LambdaObserver<Object> o = new LambdaObserver<Object>(new Consumer<Object>() {
                @Override
                public void accept(Object v) throws Exception {
                    received.add(v);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable e) throws Exception {
                    received.add(e);
                }
            }, new Action() {
                @Override
                public void run() throws Exception {
                    received.add(100);
                }
            }, new Consumer<Disposable>() {
                @Override
                public void accept(Disposable d) throws Exception {
                }
            });
            source.subscribe(o);
            Assert.assertEquals(Arrays.asList(1, 100), received);
            TestHelper.assertError(errors, 0, ProtocolViolationException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void badSourceEmitAfterDone() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Observable<Integer> source = new Observable<Integer>() {
                @Override
                public void subscribeActual(Observer<? extends Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onNext(1);
                    observer.onComplete();
                    observer.onNext(2);
                    observer.onError(new TestException());
                    observer.onComplete();
                }
            };
            final List<Object> received = new ArrayList<Object>();
            LambdaObserver<Object> o = new LambdaObserver<Object>(new Consumer<Object>() {
                @Override
                public void accept(Object v) throws Exception {
                    received.add(v);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable e) throws Exception {
                    received.add(e);
                }
            }, new Action() {
                @Override
                public void run() throws Exception {
                    received.add(100);
                }
            }, new Consumer<Disposable>() {
                @Override
                public void accept(Disposable d) throws Exception {
                }
            });
            source.subscribe(o);
            Assert.assertEquals(Arrays.asList(1, 100), received);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onNextThrowsCancelsUpstream() {
        PublishSubject<Integer> ps = PublishSubject.create();
        final List<Throwable> errors = new ArrayList<Throwable>();
        ps.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                throw new TestException();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                errors.add(e);
            }
        });
        Assert.assertTrue("No observers?!", ps.hasObservers());
        Assert.assertTrue("Has errors already?!", errors.isEmpty());
        ps.onNext(1);
        Assert.assertFalse("Has observers?!", ps.hasObservers());
        Assert.assertFalse("No errors?!", errors.isEmpty());
        Assert.assertTrue(errors.toString(), ((errors.get(0)) instanceof TestException));
    }

    @Test
    public void onSubscribeThrowsCancelsUpstream() {
        PublishSubject<Integer> ps = PublishSubject.create();
        final List<Throwable> errors = new ArrayList<Throwable>();
        ps.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                errors.add(e);
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
            }
        }, new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) throws Exception {
                throw new TestException();
            }
        });
        Assert.assertFalse("Has observers?!", ps.hasObservers());
        Assert.assertFalse("No errors?!", errors.isEmpty());
        Assert.assertTrue(errors.toString(), ((errors.get(0)) instanceof TestException));
    }

    @Test
    public void onErrorMissingShouldReportNoCustomOnError() {
        LambdaObserver<Integer> o = new LambdaObserver<Integer>(Functions.<Integer>emptyConsumer(), Functions.ON_ERROR_MISSING, Functions.EMPTY_ACTION, Functions.<Disposable>emptyConsumer());
        Assert.assertFalse(o.hasCustomOnError());
    }

    @Test
    public void customOnErrorShouldReportCustomOnError() {
        LambdaObserver<Integer> o = new LambdaObserver<Integer>(Functions.<Integer>emptyConsumer(), Functions.<Throwable>emptyConsumer(), Functions.EMPTY_ACTION, Functions.<Disposable>emptyConsumer());
        Assert.assertTrue(o.hasCustomOnError());
    }

    @Test
    public void disposedObserverShouldReportErrorOnGlobalErrorHandler() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final List<Throwable> observerErrors = Collections.synchronizedList(new ArrayList<Throwable>());
            LambdaObserver<Integer> o = new LambdaObserver<Integer>(Functions.<Integer>emptyConsumer(), new Consumer<Throwable>() {
                @Override
                public void accept(Throwable t) {
                    observerErrors.add(t);
                }
            }, Functions.EMPTY_ACTION, Functions.<Disposable>emptyConsumer());
            o.dispose();
            o.onError(new IOException());
            o.onError(new IOException());
            Assert.assertTrue(observerErrors.isEmpty());
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
            TestHelper.assertUndeliverable(errors, 1, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

