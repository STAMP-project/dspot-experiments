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


import SingleInternalHelper.NoSuchElementCallable;
import SingleInternalHelper.ToFlowable;
import SingleInternalHelper.ToObservable;
import io.reactivex.TestHelper;
import java.util.Collections;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class SingleInternalHelperTest {
    @Test
    public void utilityClass() {
        TestHelper.checkUtilityClass(SingleInternalHelper.class);
    }

    @Test
    public void noSuchElementCallableEnum() {
        Assert.assertEquals(1, NoSuchElementCallable.values().length);
        Assert.assertNotNull(NoSuchElementCallable.valueOf("INSTANCE"));
    }

    @Test
    public void toFlowableEnum() {
        Assert.assertEquals(1, ToFlowable.values().length);
        Assert.assertNotNull(ToFlowable.valueOf("INSTANCE"));
    }

    @Test
    public void toObservableEnum() {
        Assert.assertEquals(1, ToObservable.values().length);
        Assert.assertNotNull(ToObservable.valueOf("INSTANCE"));
    }

    @Test
    public void singleIterableToFlowableIterable() {
        Iterable<? extends Flowable<Integer>> it = SingleInternalHelper.iterableToFlowable(Collections.singletonList(Single.just(1)));
        Iterator<? extends Flowable<Integer>> iter = it.iterator();
        if (iter.hasNext()) {
            test().assertResult(1);
            if (iter.hasNext()) {
                Assert.fail("Iterator reports an additional element");
            }
        } else {
            Assert.fail("Iterator was empty");
        }
    }
}

