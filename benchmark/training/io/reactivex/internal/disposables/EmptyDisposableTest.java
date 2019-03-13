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
package io.reactivex.internal.disposables;


import EmptyDisposable.INSTANCE;
import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.TestHelper;
import org.junit.Assert;
import org.junit.Test;


public class EmptyDisposableTest {
    @Test
    public void noOffer() {
        TestHelper.assertNoOffer(INSTANCE);
    }

    @Test
    public void asyncFusion() {
        Assert.assertEquals(NONE, INSTANCE.requestFusion(SYNC));
        Assert.assertEquals(ASYNC, INSTANCE.requestFusion(ASYNC));
    }

    @Test
    public void checkEnum() {
        Assert.assertEquals(2, EmptyDisposable.values().length);
        Assert.assertNotNull(EmptyDisposable.valueOf("INSTANCE"));
        Assert.assertNotNull(EmptyDisposable.valueOf("NEVER"));
    }
}

