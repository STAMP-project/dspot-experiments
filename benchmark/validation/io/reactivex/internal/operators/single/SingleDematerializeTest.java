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


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.subjects.SingleSubject;
import org.junit.Test;


public class SingleDematerializeTest {
    @Test
    public void success() {
        Single.just(Notification.createOnNext(1)).dematerialize(Functions.<Notification<Integer>>identity()).test().assertResult(1);
    }

    @Test
    public void empty() {
        Single.just(Notification.<Integer>createOnComplete()).dematerialize(Functions.<Notification<Integer>>identity()).test().assertResult();
    }

    @Test
    public void error() {
        Single.<Notification<Integer>>error(new TestException()).dematerialize(Functions.<Notification<Integer>>identity()).test().assertFailure(TestException.class);
    }

    @Test
    public void errorNotification() {
        Single.just(Notification.<Integer>createOnError(new TestException())).dematerialize(Functions.<Notification<Integer>>identity()).test().assertFailure(TestException.class);
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeSingleToMaybe(new io.reactivex.functions.Function<Single<Object>, MaybeSource<Object>>() {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public io.reactivex.MaybeSource<Object> apply(Single<Object> v) throws Exception {
                return v.dematerialize(((io.reactivex.functions.Function) (Functions.identity())));
            }
        });
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(SingleSubject.<Notification<Integer>>create().dematerialize(Functions.<Notification<Integer>>identity()));
    }

    @Test
    public void selectorCrash() {
        Single.just(Notification.createOnNext(1)).dematerialize(new io.reactivex.functions.Function<Notification<Integer>, Notification<Integer>>() {
            @Override
            public io.reactivex.Notification<Integer> apply(Notification<Integer> v) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void selectorNull() {
        Single.just(Notification.createOnNext(1)).dematerialize(Functions.justFunction(((Notification<Integer>) (null)))).test().assertFailure(NullPointerException.class);
    }

    @Test
    public void selectorDifferentType() {
        Single.just(Notification.createOnNext(1)).dematerialize(new io.reactivex.functions.Function<Notification<Integer>, Notification<String>>() {
            @Override
            public io.reactivex.Notification<String> apply(Notification<Integer> v) throws Exception {
                return Notification.createOnNext(("Value-" + 1));
            }
        }).test().assertResult("Value-1");
    }
}

