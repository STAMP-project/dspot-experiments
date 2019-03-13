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
package io.reactivex.flowable;


import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class FlowableWindowTests {
    @Test
    public void testWindow() {
        final ArrayList<List<Integer>> lists = new ArrayList<List<Integer>>();
        Flowable.concat(Flowable.just(1, 2, 3, 4, 5, 6).window(3).map(new Function<Flowable<Integer>, Flowable<List<Integer>>>() {
            @Override
            public Flowable<List<Integer>> apply(Flowable<Integer> xs) {
                return xs.toList().toFlowable();
            }
        })).blockingForEach(new Consumer<List<Integer>>() {
            @Override
            public void accept(List<Integer> xs) {
                lists.add(xs);
            }
        });
        Assert.assertArrayEquals(lists.get(0).toArray(new Integer[3]), new Integer[]{ 1, 2, 3 });
        Assert.assertArrayEquals(lists.get(1).toArray(new Integer[3]), new Integer[]{ 4, 5, 6 });
        Assert.assertEquals(2, lists.size());
    }
}

