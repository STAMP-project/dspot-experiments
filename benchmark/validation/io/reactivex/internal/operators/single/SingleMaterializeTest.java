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
import io.reactivex.subjects.SingleSubject;
import org.junit.Test;


public class SingleMaterializeTest {
    @Test
    @SuppressWarnings("unchecked")
    public void success() {
        Single.just(1).materialize().test().assertResult(Notification.createOnNext(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void error() {
        TestException ex = new TestException();
        Maybe.error(ex).materialize().test().assertResult(Notification.createOnError(ex));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeSingle(new io.reactivex.functions.Function<Single<Object>, SingleSource<Notification<Object>>>() {
            @Override
            public io.reactivex.SingleSource<Notification<Object>> apply(Single<Object> v) throws Exception {
                return v.materialize();
            }
        });
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(SingleSubject.create().materialize());
    }
}

