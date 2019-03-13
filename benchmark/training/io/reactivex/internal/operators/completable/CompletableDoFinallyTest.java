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
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class CompletableDoFinallyTest implements Action {
    int calls;

    @Test
    public void normalEmpty() {
        Completable.complete().doFinally(this).test().assertResult();
        Assert.assertEquals(1, calls);
    }

    @Test
    public void normalError() {
        Completable.error(new TestException()).doFinally(this).test().assertFailure(TestException.class);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeCompletable(new Function<Completable, Completable>() {
            @Override
            public io.reactivex.Completable apply(Completable f) throws Exception {
                return f.doFinally(CompletableDoFinallyTest.this);
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void nullAction() {
        Completable.complete().doFinally(null);
    }

    @Test
    public void actionThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Completable.complete().doFinally(new Action() {
                @Override
                public void run() throws Exception {
                    throw new TestException();
                }
            }).test().assertResult().cancel();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(PublishSubject.create().ignoreElements().doFinally(this));
    }
}

