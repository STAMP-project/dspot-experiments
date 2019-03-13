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


import io.reactivex.exceptions.TestException;
import org.junit.Assert;
import org.junit.Test;


public class BlockingFirstObserverTest {
    @Test
    public void firstValueOnly() {
        BlockingFirstObserver<Integer> bf = new BlockingFirstObserver<Integer>();
        Disposable d = Disposables.empty();
        bf.onSubscribe(d);
        bf.onNext(1);
        Assert.assertTrue(d.isDisposed());
        Assert.assertEquals(1, bf.value.intValue());
        Assert.assertEquals(0, bf.getCount());
        bf.onNext(2);
        Assert.assertEquals(1, bf.value.intValue());
        Assert.assertEquals(0, bf.getCount());
        bf.onError(new TestException());
        Assert.assertEquals(1, bf.value.intValue());
        Assert.assertNull(bf.error);
        Assert.assertEquals(0, bf.getCount());
    }
}

