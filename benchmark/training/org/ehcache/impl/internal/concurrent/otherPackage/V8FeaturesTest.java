/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.impl.internal.concurrent.otherPackage;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ludovic Orban
 */
public class V8FeaturesTest {
    @Test
    public void testCompute() throws Exception {
        ConcurrentHashMap<String, Integer> chm = new ConcurrentHashMap<>();
        chm.put("one", 1);
        chm.put("two", 2);
        chm.put("three", 3);
        Integer result = chm.compute("three", ( s, i) -> -i);
        Assert.assertThat(result, CoreMatchers.equalTo((-3)));
    }

    @Test
    public void testComputeIfAbsent() throws Exception {
        ConcurrentHashMap<String, AtomicInteger> chm = new ConcurrentHashMap<>();
        Assert.assertThat(chm.get("four"), Matchers.is(Matchers.nullValue()));
        chm.computeIfAbsent("four", ( s) -> new AtomicInteger(0)).incrementAndGet();
        Assert.assertThat(chm.get("four").get(), CoreMatchers.equalTo(1));
        chm.computeIfAbsent("four", ( s) -> new AtomicInteger(0)).incrementAndGet();
        Assert.assertThat(chm.get("four").get(), CoreMatchers.equalTo(2));
        chm.computeIfAbsent("four", ( s) -> new AtomicInteger(0)).incrementAndGet();
        Assert.assertThat(chm.get("four").get(), CoreMatchers.equalTo(3));
    }

    @Test
    public void testComputeIfPresent() throws Exception {
        ConcurrentHashMap<String, Integer> chm = new ConcurrentHashMap<>();
        chm.put("four", 0);
        Assert.assertThat(chm.get("four"), CoreMatchers.equalTo(0));
        chm.computeIfPresent("four", ( s, i) -> i + 1);
        Assert.assertThat(chm.get("four"), CoreMatchers.equalTo(1));
        chm.computeIfPresent("four", ( s, i) -> i + 1);
        Assert.assertThat(chm.get("four"), CoreMatchers.equalTo(2));
        chm.computeIfPresent("four", ( s, i) -> i + 1);
        Assert.assertThat(chm.get("four"), CoreMatchers.equalTo(3));
    }

    @Test
    public void testMerge() throws Exception {
        ConcurrentHashMap<Integer, String> chm = new ConcurrentHashMap<>();
        chm.put(1, "one");
        chm.put(2, "two");
        chm.put(3, "three");
        String result = chm.merge(1, "un", ( s, s2) -> (s + "#") + s2);
        Assert.assertThat(result, CoreMatchers.equalTo("one#un"));
        Assert.assertThat(chm.get(1), CoreMatchers.equalTo("one#un"));
    }

    @SuppressWarnings("serial")
    @Test
    public void testReplaceAll() throws Exception {
        ConcurrentHashMap<Integer, String> chm = new ConcurrentHashMap<>();
        chm.put(1, "one");
        chm.put(2, "two");
        chm.put(3, "three");
        chm.replaceAll(( i, s) -> {
            if (i == 1)
                return "un";

            if (i == 2)
                return "deux";

            if (i == 3)
                return "trois";

            throw new AssertionError(((("did not expect this pair : " + i) + ":") + s));
        });
        Assert.assertEquals(new HashMap<Integer, String>() {
            {
                put(1, "un");
                put(2, "deux");
                put(3, "trois");
            }
        }, chm);
    }

    @Test
    public void testForEach() throws Exception {
        ConcurrentHashMap<String, Integer> chm = new ConcurrentHashMap<>();
        chm.put("one", 1);
        chm.put("two", 2);
        chm.put("three", 3);
        chm.put("four", 4);
        chm.put("five", 5);
        chm.put("six", 6);
        chm.put("seven", 7);
        chm.put("eight", 8);
        final Map<String, Integer> collector = Collections.synchronizedMap(new HashMap<String, Integer>());
        chm.forEach(collector::put);
        Assert.assertThat(chm, CoreMatchers.equalTo(collector));
    }

    @Test
    public void testParallelForEach() throws Exception {
        ConcurrentHashMap<String, Integer> chm = new ConcurrentHashMap<>();
        chm.put("one", 1);
        chm.put("two", 2);
        chm.put("three", 3);
        chm.put("four", 4);
        chm.put("five", 5);
        chm.put("six", 6);
        chm.put("seven", 7);
        chm.put("eight", 8);
        final Map<String, Integer> collector = Collections.synchronizedMap(new HashMap<String, Integer>());
        chm.forEach(4, ( s, i) -> {
            System.out.println(((s + " ") + i));
            collector.put(s, i);
        });
        Assert.assertThat(chm, CoreMatchers.equalTo(collector));
    }

    @Test
    public void testReduce() throws Exception {
        ConcurrentHashMap<String, V8FeaturesTest.Entry> chm = new ConcurrentHashMap<>();
        chm.put("SF", new V8FeaturesTest.Entry("CA", "San Francisco", 20));
        chm.put("PX", new V8FeaturesTest.Entry("AZ", "Phoenix", 2000));
        chm.put("NY", new V8FeaturesTest.Entry("NY", "New York City", 1));
        chm.put("LA", new V8FeaturesTest.Entry("CA", "Los Angeles", 40));
        chm.put("SD", new V8FeaturesTest.Entry("CA", "San Diego", 50));
        chm.put("SC", new V8FeaturesTest.Entry("CA", "Sacramento", 30));
        Integer result = chm.reduce(4, ( s, entry) -> {
            if (entry.state.equals("CA")) {
                return entry.temperature;
            }
            return null;
        }, ( temp1, temp2) -> (temp1 + temp2) / 2);
        Assert.assertThat(result, Matchers.is(35));
    }

    static class Entry {
        String state;

        String city;

        int temperature;

        Entry(String state, String city, int temperature) {
            this.state = state;
            this.city = city;
            this.temperature = temperature;
        }
    }
}

