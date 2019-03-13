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
import io.reactivex.subjects.MaybeSubject;
import org.junit.Test;


public class MaybeMaterializeTest {
    @Test
    @SuppressWarnings("unchecked")
    public void success() {
        Maybe.just(1).materialize().test().assertResult(Notification.createOnNext(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void error() {
        TestException ex = new TestException();
        Maybe.error(ex).materialize().test().assertResult(Notification.createOnError(ex));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void empty() {
        Maybe.empty().materialize().test().assertResult(Notification.createOnComplete());
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeMaybeToSingle(new io.reactivex.functions.Function<Maybe<Object>, SingleSource<Notification<Object>>>() {
            @Override
            public io.reactivex.SingleSource<Notification<Object>> apply(Maybe<Object> v) throws Exception {
                return v.materialize();
            }
        });
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(MaybeSubject.create().materialize());
    }
}

