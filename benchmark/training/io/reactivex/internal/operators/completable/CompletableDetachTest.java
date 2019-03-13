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
package io.reactivex.internal.operators.completable;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.TestObserver;
import io.reactivex.processors.PublishProcessor;
import java.lang.ref.WeakReference;
import org.junit.Assert;
import org.junit.Test;


public class CompletableDetachTest {
    @Test
    public void doubleSubscribe() {
        TestHelper.checkDoubleOnSubscribeCompletable(new io.reactivex.functions.Function<Completable, CompletableSource>() {
            @Override
            public io.reactivex.CompletableSource apply(Completable m) throws Exception {
                return m.onTerminateDetach();
            }
        });
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(onTerminateDetach());
    }

    @Test
    public void onError() {
        onTerminateDetach().test().assertFailure(TestException.class);
    }

    @Test
    public void onComplete() {
        onTerminateDetach().test().assertResult();
    }

    @Test
    public void cancelDetaches() throws Exception {
        Disposable d = Disposables.empty();
        final WeakReference<Disposable> wr = new WeakReference<Disposable>(d);
        TestObserver<Void> to = onTerminateDetach().test();
        d = null;
        to.cancel();
        System.gc();
        Thread.sleep(200);
        to.assertEmpty();
        Assert.assertNull(wr.get());
    }

    @Test
    public void completeDetaches() throws Exception {
        Disposable d = Disposables.empty();
        final WeakReference<Disposable> wr = new WeakReference<Disposable>(d);
        TestObserver<Void> to = onTerminateDetach().test();
        d = null;
        System.gc();
        Thread.sleep(200);
        to.assertResult();
        Assert.assertNull(wr.get());
    }

    @Test
    public void errorDetaches() throws Exception {
        Disposable d = Disposables.empty();
        final WeakReference<Disposable> wr = new WeakReference<Disposable>(d);
        TestObserver<Void> to = onTerminateDetach().test();
        d = null;
        System.gc();
        Thread.sleep(200);
        to.assertFailure(TestException.class);
        Assert.assertNull(wr.get());
    }
}

