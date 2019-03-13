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


import io.reactivex.TestHelper;
import org.junit.Assert;
import org.junit.Test;


public class ArrayCompositeDisposableTest {
    @Test
    public void normal() {
        ArrayCompositeDisposable acd = new ArrayCompositeDisposable(2);
        Disposable d1 = Disposables.empty();
        Disposable d2 = Disposables.empty();
        Assert.assertTrue(acd.setResource(0, d1));
        Assert.assertTrue(acd.setResource(1, d2));
        Disposable d3 = Disposables.empty();
        Disposable d4 = Disposables.empty();
        acd.replaceResource(0, d3);
        acd.replaceResource(1, d4);
        Assert.assertFalse(d1.isDisposed());
        Assert.assertFalse(d2.isDisposed());
        acd.setResource(0, d1);
        acd.setResource(1, d2);
        Assert.assertTrue(d3.isDisposed());
        Assert.assertTrue(d4.isDisposed());
        Assert.assertFalse(acd.isDisposed());
        acd.dispose();
        acd.dispose();
        Assert.assertTrue(acd.isDisposed());
        Assert.assertTrue(d1.isDisposed());
        Assert.assertTrue(d2.isDisposed());
        Disposable d5 = Disposables.empty();
        Disposable d6 = Disposables.empty();
        Assert.assertFalse(acd.setResource(0, d5));
        acd.replaceResource(1, d6);
        Assert.assertTrue(d5.isDisposed());
        Assert.assertTrue(d6.isDisposed());
    }

    @Test
    public void disposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final ArrayCompositeDisposable acd = new ArrayCompositeDisposable(2);
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    acd.dispose();
                }
            };
            TestHelper.race(r, r);
        }
    }

    @Test
    public void replaceRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final ArrayCompositeDisposable acd = new ArrayCompositeDisposable(2);
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    acd.replaceResource(0, Disposables.empty());
                }
            };
            TestHelper.race(r, r);
        }
    }

    @Test
    public void setRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final ArrayCompositeDisposable acd = new ArrayCompositeDisposable(2);
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    acd.setResource(0, Disposables.empty());
                }
            };
            TestHelper.race(r, r);
        }
    }
}

