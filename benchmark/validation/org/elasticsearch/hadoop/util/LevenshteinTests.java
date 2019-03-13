/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.util;


import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class LevenshteinTests {
    Map<String, String> props;

    Set<String> orig = new LinkedHashSet<String>();

    public LevenshteinTests() {
        orig.add("foo");
        orig.add("foo.bar123");
        orig.add("foo.bar123.abcdefghijklmn");
        orig.add("foo.bar123.abcdefghijklmn.xyz890");
        props = unroll(orig);
    }

    @Test
    public void testDistance() {
        Assert.assertThat(levenshteinDistance("bar", "bor", 1), is(1));
        Assert.assertThat(levenshteinDistance("bar", "bara", 1), is(1));
        Assert.assertThat(levenshteinDistance("bar", "abar", 1), is(1));
        Assert.assertThat(levenshteinDistance("bar", "abara", 3), is(2));
        Assert.assertThat(levenshteinDistance("bar", "abora", 5), is(3));
        Assert.assertThat(levenshteinDistance("bar", "arb", 3), is(2));
        Assert.assertThat(levenshteinDistance("bar", "aarb", 2), is(2));
        Assert.assertThat(levenshteinDistance("bar", "aarbx", 3), is(3));
        Assert.assertThat(levenshteinDistance("bar", "aarbx", 2), is((-1)));
    }

    @Test
    public void testFindTypos() {
        Set<String> keySet = props.keySet();
        Assert.assertThat(findSimiliar("foo", keySet), contains("foo"));
        Assert.assertThat(findSimiliar("ofo", keySet), contains("foo"));
        Assert.assertThat(findSimiliar("oof", keySet), contains("foo"));
        Assert.assertThat(findSimiliar("ar123", keySet), contains("bar123"));
        Assert.assertThat(findSimiliar("ba123", keySet), contains("bar123"));
        Assert.assertThat(findSimiliar("abr123", keySet), contains("bar123"));
        Assert.assertThat(findSimiliar("xyz890", keySet), contains("xyz890"));
        Assert.assertThat(findSimiliar("xyz89", keySet), contains("xyz890"));
        Assert.assertThat(findSimiliar("yzx890", keySet), contains("xyz890"));
        Assert.assertThat(findSimiliar("yz890", keySet), contains("xyz890"));
    }
}

