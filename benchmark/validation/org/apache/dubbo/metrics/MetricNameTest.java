/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.metrics;


import MetricLevel.CRITICAL;
import MetricName.EMPTY;
import MetricName.EMPTY_TAGS;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class MetricNameTest {
    @Test
    public void testEmpty() {
        Assertions.assertEquals(EMPTY.getTags(), EMPTY_TAGS);
        Assertions.assertNull(EMPTY.getKey());
        Assertions.assertEquals(new MetricName().getTags(), EMPTY_TAGS);
        Assertions.assertEquals(EMPTY, new MetricName());
        Assertions.assertEquals(MetricName.build(), EMPTY);
        Assertions.assertEquals(EMPTY.resolve(null), EMPTY);
    }

    @Test
    public void testEmptyResolve() {
        final MetricName name = new MetricName();
        Assertions.assertEquals(name.resolve("foo"), new MetricName("foo"));
    }

    @Test
    public void testResolveToEmpty() {
        final MetricName name = new MetricName("foo");
        Assertions.assertEquals(name.resolve(null), new MetricName("foo"));
    }

    @Test
    public void testResolve() {
        final MetricName name = new MetricName("foo");
        Assertions.assertEquals(name.resolve("bar"), new MetricName("foo.bar"));
    }

    @Test
    public void testResolveWithTags() {
        final MetricName name = new MetricName("foo").tag("key", "value");
        Assertions.assertEquals(name.resolve("bar"), new MetricName("foo.bar").tag("key", "value"));
    }

    @Test
    public void testResolveWithoutTags() {
        final MetricName name = new MetricName("foo").tag("key", "value");
        Assertions.assertEquals(name.resolve("bar", false), new MetricName("foo.bar"));
    }

    @Test
    public void testResolveBothEmpty() {
        final MetricName name = new MetricName(null);
        Assertions.assertEquals(name.resolve(null), new MetricName());
    }

    @Test
    public void testAddTagsVarious() {
        final Map<String, String> refTags = new HashMap<String, String>();
        refTags.put("foo", "bar");
        final MetricName test = EMPTY.tag("foo", "bar");
        final MetricName test2 = EMPTY.tag(refTags);
        Assertions.assertEquals(test, new MetricName(null, refTags));
        Assertions.assertEquals(test.getTags(), refTags);
        Assertions.assertEquals(test2, new MetricName(null, refTags));
        Assertions.assertEquals(test2.getTags(), refTags);
    }

    @Test
    public void testTaggedMoreArguments() {
        final Map<String, String> refTags = new HashMap<String, String>();
        refTags.put("foo", "bar");
        refTags.put("baz", "biz");
        Assertions.assertEquals(EMPTY.tag("foo", "bar", "baz", "biz").getTags(), refTags);
    }

    @Test
    public void testTaggedNotPairs() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> EMPTY.tag("foo"));
    }

    @Test
    public void testTaggedNotPairs2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> EMPTY.tag("foo", "bar", "baz"));
    }

    @Test
    public void testCompareTo() {
        final MetricName a = EMPTY.tag("foo", "bar");
        final MetricName b = EMPTY.tag("foo", "baz");
        Assertions.assertTrue(((a.compareTo(b)) < 0));
        Assertions.assertTrue(((b.compareTo(a)) > 0));
        Assertions.assertTrue(((b.compareTo(b)) == 0));
        Assertions.assertTrue(((b.resolve("key").compareTo(b)) < 0));
        Assertions.assertTrue(((b.compareTo(b.resolve("key"))) > 0));
    }

    @Test
    public void testTaggedWithLevel() {
        MetricName name = MetricName.build("test").level(CRITICAL);
        MetricName tagged = name.tag("foo", "bar");
        Assertions.assertEquals(tagged.getMetricLevel(), CRITICAL);
    }

    @Test
    public void testJoinWithLevel() {
        MetricName name = MetricName.build("test").level(CRITICAL);
        MetricName tagged = MetricName.join(name, MetricName.build("abc"));
        Assertions.assertEquals(tagged.getMetricLevel(), CRITICAL);
    }

    @Test
    public void testResolveWithLevel() {
        final MetricName name = new MetricName("foo").level(CRITICAL).tag("key", "value");
        Assertions.assertEquals(name.resolve("bar"), new MetricName("foo.bar").tag("key", "value").level(CRITICAL));
    }
}

