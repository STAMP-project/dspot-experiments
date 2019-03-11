/**
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.collection;


import androidx.arch.core.internal.FastSafeIterableMap;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class FastSafeIterableMapTest {
    @Test
    public void testCeil() {
        FastSafeIterableMap<Integer, Boolean> map = new FastSafeIterableMap();
        Assert.assertThat(map.ceil(1), CoreMatchers.nullValue());
        map.putIfAbsent(1, false);
        Assert.assertThat(map.ceil(1), CoreMatchers.nullValue());
        map.putIfAbsent(2, false);
        Assert.assertThat(map.ceil(2).getKey(), CoreMatchers.is(1));
        map.remove(1);
        Assert.assertThat(map.ceil(2), CoreMatchers.nullValue());
    }

    @Test
    public void testPut() {
        FastSafeIterableMap<Integer, Integer> map = new FastSafeIterableMap();
        map.putIfAbsent(10, 20);
        map.putIfAbsent(20, 40);
        map.putIfAbsent(30, 60);
        Assert.assertThat(map.putIfAbsent(5, 10), CoreMatchers.is(((Integer) (null))));
        Assert.assertThat(map.putIfAbsent(10, 30), CoreMatchers.is(20));
    }

    @Test
    public void testContains() {
        FastSafeIterableMap<Integer, Integer> map = new FastSafeIterableMap();
        map.putIfAbsent(10, 20);
        map.putIfAbsent(20, 40);
        map.putIfAbsent(30, 60);
        Assert.assertThat(map.contains(10), CoreMatchers.is(true));
        Assert.assertThat(map.contains(11), CoreMatchers.is(false));
        Assert.assertThat(new FastSafeIterableMap<Integer, Integer>().contains(0), CoreMatchers.is(false));
    }

    @Test
    public void testRemove() {
        FastSafeIterableMap<Integer, Integer> map = new FastSafeIterableMap();
        map.putIfAbsent(10, 20);
        map.putIfAbsent(20, 40);
        Assert.assertThat(map.contains(10), CoreMatchers.is(true));
        Assert.assertThat(map.contains(20), CoreMatchers.is(true));
        Assert.assertThat(map.remove(10), CoreMatchers.is(20));
        Assert.assertThat(map.contains(10), CoreMatchers.is(false));
        Assert.assertThat(map.putIfAbsent(10, 30), CoreMatchers.nullValue());
        Assert.assertThat(map.putIfAbsent(10, 40), CoreMatchers.is(30));
    }
}

