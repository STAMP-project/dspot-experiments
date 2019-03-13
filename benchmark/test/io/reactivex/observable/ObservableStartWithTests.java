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
package io.reactivex.observable;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ObservableStartWithTests {
    @Test
    public void startWith1() {
        List<String> values = java.util.Observable.just("one", "two").startWithArray("zero").toList().blockingGet();
        Assert.assertEquals("zero", values.get(0));
        Assert.assertEquals("two", values.get(2));
    }

    @Test
    public void startWithIterable() {
        List<String> li = new ArrayList<String>();
        li.add("alpha");
        li.add("beta");
        List<String> values = java.util.Observable.just("one", "two").startWith(li).toList().blockingGet();
        Assert.assertEquals("alpha", values.get(0));
        Assert.assertEquals("beta", values.get(1));
        Assert.assertEquals("one", values.get(2));
        Assert.assertEquals("two", values.get(3));
    }

    @Test
    public void startWithObservable() {
        List<String> li = new ArrayList<String>();
        li.add("alpha");
        li.add("beta");
        List<String> values = java.util.Observable.just("one", "two").startWith(fromIterable(li)).toList().blockingGet();
        Assert.assertEquals("alpha", values.get(0));
        Assert.assertEquals("beta", values.get(1));
        Assert.assertEquals("one", values.get(2));
        Assert.assertEquals("two", values.get(3));
    }

    @Test
    public void startWithEmpty() {
        just(1).startWithArray().test().assertResult(1);
    }
}

