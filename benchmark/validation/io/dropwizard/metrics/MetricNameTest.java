/**
 * Copyright 2010-2013 Coda Hale and Yammer, Inc., 2014-2017 Dropwizard Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
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
package io.dropwizard.metrics;


import MetricName.EMPTY;
import MetricName.EMPTY_TAGS;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


public class MetricNameTest {
    @Test
    public void testEmpty() throws Exception {
        assertThat(EMPTY.getTags()).isEqualTo(EMPTY_TAGS);
        assertThat(EMPTY.getKey()).isEqualTo(null);
        assertThat(new MetricName().getTags()).isEqualTo(EMPTY_TAGS);
        assertThat(EMPTY).isEqualTo(new MetricName());
        assertThat(MetricName.build()).isEqualTo(EMPTY);
        assertThat(EMPTY.resolve(null)).isEqualTo(EMPTY);
    }

    @Test
    public void testEmptyResolve() throws Exception {
        final MetricName name = new MetricName();
        assertThat(name.resolve("foo")).isEqualTo(new MetricName("foo"));
    }

    @Test
    public void testResolveToEmpty() throws Exception {
        final MetricName name = new MetricName("foo");
        assertThat(name.resolve(null)).isEqualTo(new MetricName("foo"));
    }

    @Test
    public void testResolve() throws Exception {
        final MetricName name = new MetricName("foo");
        assertThat(name.resolve("bar")).isEqualTo(new MetricName("foo.bar"));
    }

    @Test
    public void testResolveBothEmpty() throws Exception {
        final MetricName name = new MetricName(null);
        assertThat(name.resolve(null)).isEqualTo(new MetricName());
    }

    @Test
    public void testAddTagsVarious() {
        final Map<String, String> refTags = new HashMap<>();
        refTags.put("foo", "bar");
        final MetricName test = EMPTY.tagged("foo", "bar");
        final MetricName test2 = EMPTY.tagged(refTags);
        assertThat(test).isEqualTo(new MetricName(null, refTags));
        assertThat(test.getTags()).isEqualTo(refTags);
        assertThat(test2).isEqualTo(new MetricName(null, refTags));
        assertThat(test2.getTags()).isEqualTo(refTags);
    }

    @Test
    public void testTaggedMoreArguments() {
        final Map<String, String> refTags = new HashMap<>();
        refTags.put("foo", "bar");
        refTags.put("baz", "biz");
        assertThat(EMPTY.tagged("foo", "bar", "baz", "biz").getTags()).isEqualTo(refTags);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTaggedNotPairs() {
        EMPTY.tagged("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTaggedNotPairs2() {
        EMPTY.tagged("foo", "bar", "baz");
    }

    @Test
    public void testCompareTo() {
        final MetricName a = EMPTY.tagged("foo", "bar");
        final MetricName b = EMPTY.tagged("foo", "baz");
        assertThat(a.compareTo(b)).isLessThan(0);
        assertThat(b.compareTo(a)).isGreaterThan(0);
        assertThat(b.resolve("key").compareTo(b)).isLessThan(0);
        assertThat(b.compareTo(b.resolve("key"))).isGreaterThan(0);
    }
}

