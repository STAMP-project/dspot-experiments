/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.exceptions;


import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class ExceptionsTest {
    @Test
    public void testOnErrorNotImplementedIsThrown() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        Observable.just(1, 2, 3).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer t1) {
                throw new RuntimeException("hello");
            }
        });
        TestHelper.assertError(errors, 0, RuntimeException.class);
        Assert.assertTrue(errors.get(0).toString(), errors.get(0).getMessage().contains("hello"));
        RxJavaPlugins.reset();
    }

    @Test
    public void testStackOverflowWouldOccur() {
        final PublishSubject<Integer> a = PublishSubject.create();
        final PublishSubject<Integer> b = PublishSubject.create();
        final int MAX_STACK_DEPTH = 800;
        final AtomicInteger depth = new AtomicInteger();
        a.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Integer n) {
                b.onNext((n + 1));
            }
        });
        b.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Integer n) {
                if ((depth.get()) < MAX_STACK_DEPTH) {
                    depth.set(Thread.currentThread().getStackTrace().length);
                    a.onNext((n + 1));
                }
            }
        });
        a.onNext(1);
        Assert.assertTrue(((depth.get()) >= MAX_STACK_DEPTH));
    }

    @Test(expected = StackOverflowError.class)
    public void testStackOverflowErrorIsThrown() {
        Observable.just(1).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Integer t) {
                throw new StackOverflowError();
            }
        });
    }

    @Test(expected = ThreadDeath.class)
    public void testThreadDeathIsThrown() {
        Observable.just(1).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Integer t) {
                throw new ThreadDeath();
            }
        });
    }

    private class OnErrorFailedSubscriber implements Observer<Integer> {
        @Override
        public void onSubscribe(Disposable d) {
        }

        @Override
        public void onComplete() {
        }

        @Override
        public void onError(Throwable e) {
            throw new RuntimeException();
        }

        @Override
        public void onNext(Integer value) {
        }
    }

    @Test
    public void utilityClass() {
        TestHelper.checkUtilityClass(Exceptions.class);
    }

    @Test
    public void manualThrowIfFatal() {
        try {
            Exceptions.throwIfFatal(new ThreadDeath());
            Assert.fail("Didn't throw fatal exception");
        } catch (ThreadDeath ex) {
            // expected
        }
        try {
            Exceptions.throwIfFatal(new LinkageError());
            Assert.fail("Didn't throw fatal error");
        } catch (LinkageError ex) {
            // expected
        }
        try {
            ExceptionHelper.wrapOrThrow(new LinkageError());
            Assert.fail("Didn't propagate Error");
        } catch (LinkageError ex) {
            // expected
        }
    }

    @Test
    public void manualPropagate() {
        try {
            Exceptions.propagate(new InternalError());
            Assert.fail("Didn't throw exception");
        } catch (InternalError ex) {
            // expected
        }
        try {
            throw Exceptions.propagate(new IllegalArgumentException());
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            throw ExceptionHelper.wrapOrThrow(new IOException());
        } catch (RuntimeException ex) {
            if (!((ex.getCause()) instanceof IOException)) {
                Assert.fail(((ex.toString()) + ": should have thrown RuntimeException(IOException)"));
            }
        }
    }

    @Test
    public void errorNotImplementedNull1() {
        OnErrorNotImplementedException ex = new OnErrorNotImplementedException(null);
        Assert.assertTrue(("" + (ex.getCause())), ((ex.getCause()) instanceof NullPointerException));
    }

    @Test
    public void errorNotImplementedNull2() {
        OnErrorNotImplementedException ex = new OnErrorNotImplementedException("Message", null);
        Assert.assertTrue(("" + (ex.getCause())), ((ex.getCause()) instanceof NullPointerException));
    }

    @Test
    public void errorNotImplementedWithCause() {
        OnErrorNotImplementedException ex = new OnErrorNotImplementedException("Message", new TestException("Forced failure"));
        Assert.assertTrue(("" + (ex.getCause())), ((ex.getCause()) instanceof TestException));
        Assert.assertEquals(("" + (ex.getCause())), "Forced failure", ex.getCause().getMessage());
    }
}

