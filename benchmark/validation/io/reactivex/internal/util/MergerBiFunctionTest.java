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
package io.reactivex.internal.util;


import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class MergerBiFunctionTest {
    @Test
    public void firstEmpty() throws Exception {
        MergerBiFunction<Integer> merger = new MergerBiFunction<Integer>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        List<Integer> list = merger.apply(Collections.<Integer>emptyList(), Arrays.asList(3, 5));
        Assert.assertEquals(Arrays.asList(3, 5), list);
    }

    @Test
    public void bothEmpty() throws Exception {
        MergerBiFunction<Integer> merger = new MergerBiFunction<Integer>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        List<Integer> list = merger.apply(Collections.<Integer>emptyList(), Collections.<Integer>emptyList());
        Assert.assertEquals(Collections.<Integer>emptyList(), list);
    }

    @Test
    public void secondEmpty() throws Exception {
        MergerBiFunction<Integer> merger = new MergerBiFunction<Integer>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        List<Integer> list = merger.apply(Arrays.asList(2, 4), Collections.<Integer>emptyList());
        Assert.assertEquals(Arrays.asList(2, 4), list);
    }

    @Test
    public void sameSize() throws Exception {
        MergerBiFunction<Integer> merger = new MergerBiFunction<Integer>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        List<Integer> list = merger.apply(Arrays.asList(2, 4), Arrays.asList(3, 5));
        Assert.assertEquals(Arrays.asList(2, 3, 4, 5), list);
    }

    @Test
    public void sameSizeReverse() throws Exception {
        MergerBiFunction<Integer> merger = new MergerBiFunction<Integer>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });
        List<Integer> list = merger.apply(Arrays.asList(3, 5), Arrays.asList(2, 4));
        Assert.assertEquals(Arrays.asList(2, 3, 4, 5), list);
    }
}

