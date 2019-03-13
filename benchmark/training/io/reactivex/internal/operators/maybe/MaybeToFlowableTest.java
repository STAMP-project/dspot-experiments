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
import io.reactivex.internal.fuseable.HasUpstreamMaybeSource;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;


public class MaybeToFlowableTest {
    @Test
    public void source() {
        Maybe<Integer> m = Maybe.just(1);
        Assert.assertSame(m, ((HasUpstreamMaybeSource<?>) (m.toFlowable())).source());
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeMaybeToFlowable(new io.reactivex.functions.Function<Maybe<Object>, Publisher<Object>>() {
            @Override
            public Publisher<Object> apply(Maybe<Object> m) throws Exception {
                return m.toFlowable();
            }
        });
    }
}

