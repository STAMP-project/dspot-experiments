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


import MaybeCreate.Emitter;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class MaybeCreateTest {
    @Test
    public void callbackThrows() {
        Maybe.create(new MaybeOnSubscribe<Object>() {
            @Override
            public void subscribe(MaybeEmitter<Object> e) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void onSuccessNull() {
        Maybe.create(new MaybeOnSubscribe<Object>() {
            @Override
            public void subscribe(MaybeEmitter<Object> e) throws Exception {
                e.onSuccess(null);
            }
        }).test().assertFailure(NullPointerException.class);
    }

    @Test
    public void onErrorNull() {
        Maybe.create(new MaybeOnSubscribe<Object>() {
            @Override
            public void subscribe(MaybeEmitter<Object> e) throws Exception {
                e.onError(null);
            }
        }).test().assertFailure(NullPointerException.class);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Maybe.create(new MaybeOnSubscribe<Object>() {
            @Override
            public void subscribe(MaybeEmitter<Object> e) throws Exception {
                e.onSuccess(1);
            }
        }));
    }

    @Test
    public void onSuccessThrows() {
        Maybe.create(new MaybeOnSubscribe<Object>() {
            @Override
            public void subscribe(MaybeEmitter<Object> e) throws Exception {
                Disposable d = Disposables.empty();
                e.setDisposable(d);
                try {
                    e.onSuccess(1);
                    fail("Should have thrown");
                } catch (TestException ex) {
                    // expected
                }
                assertTrue(d.isDisposed());
                assertTrue(e.isDisposed());
            }
        }).subscribe(new MaybeObserver<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(Object value) {
                throw new TestException();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Test
    public void onErrorThrows() {
        Maybe.create(new MaybeOnSubscribe<Object>() {
            @Override
            public void subscribe(MaybeEmitter<Object> e) throws Exception {
                Disposable d = Disposables.empty();
                e.setDisposable(d);
                try {
                    e.onError(new java.io.IOException());
                    fail("Should have thrown");
                } catch (TestException ex) {
                    // expected
                }
                assertTrue(d.isDisposed());
                assertTrue(e.isDisposed());
            }
        }).subscribe(new MaybeObserver<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(Object value) {
            }

            @Override
            public void onError(Throwable e) {
                throw new TestException();
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Test
    public void onCompleteThrows() {
        Maybe.create(new MaybeOnSubscribe<Object>() {
            @Override
            public void subscribe(MaybeEmitter<Object> e) throws Exception {
                Disposable d = Disposables.empty();
                e.setDisposable(d);
                try {
                    e.onComplete();
                    fail("Should have thrown");
                } catch (TestException ex) {
                    // expected
                }
                assertTrue(d.isDisposed());
                assertTrue(e.isDisposed());
            }
        }).subscribe(new MaybeObserver<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(Object value) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                throw new TestException();
            }
        });
    }

    @Test
    public void onSuccessThrows2() {
        Maybe.create(new MaybeOnSubscribe<Object>() {
            @Override
            public void subscribe(MaybeEmitter<Object> e) throws Exception {
                try {
                    e.onSuccess(1);
                    fail("Should have thrown");
                } catch (TestException ex) {
                    // expected
                }
                assertTrue(e.isDisposed());
            }
        }).subscribe(new MaybeObserver<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(Object value) {
                throw new TestException();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Test
    public void onErrorThrows2() {
        Maybe.create(new MaybeOnSubscribe<Object>() {
            @Override
            public void subscribe(MaybeEmitter<Object> e) throws Exception {
                try {
                    e.onError(new java.io.IOException());
                    fail("Should have thrown");
                } catch (TestException ex) {
                    // expected
                }
                assertTrue(e.isDisposed());
            }
        }).subscribe(new MaybeObserver<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(Object value) {
            }

            @Override
            public void onError(Throwable e) {
                throw new TestException();
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Test
    public void onCompleteThrows2() {
        Maybe.create(new MaybeOnSubscribe<Object>() {
            @Override
            public void subscribe(MaybeEmitter<Object> e) throws Exception {
                try {
                    e.onComplete();
                    fail("Should have thrown");
                } catch (TestException ex) {
                    // expected
                }
                assertTrue(e.isDisposed());
            }
        }).subscribe(new MaybeObserver<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(Object value) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                throw new TestException();
            }
        });
    }

    @Test
    public void tryOnError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Boolean[] response = new Boolean[]{ null };
            Maybe.create(new MaybeOnSubscribe<Object>() {
                @Override
                public void subscribe(MaybeEmitter<Object> e) throws Exception {
                    e.onSuccess(1);
                    response[0] = e.tryOnError(new TestException());
                }
            }).test().assertResult(1);
            Assert.assertFalse(response[0]);
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void emitterHasToString() {
        Maybe.create(new MaybeOnSubscribe<Object>() {
            @Override
            public void subscribe(MaybeEmitter<Object> emitter) throws Exception {
                assertTrue(emitter.toString().contains(Emitter.class.getSimpleName()));
            }
        }).test().assertEmpty();
    }
}

