/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.metrics2.filter;


import java.util.Arrays;
import org.apache.commons.configuration2.SubsetConfiguration;
import org.apache.hadoop.metrics2.impl.ConfigBuilder;
import org.junit.Test;


public class TestPatternFilter {
    /**
     * Filters should default to accept
     */
    @Test
    public void emptyConfigShouldAccept() {
        SubsetConfiguration empty = new ConfigBuilder().subset("");
        TestPatternFilter.shouldAccept(empty, "anything");
        TestPatternFilter.shouldAccept(empty, Arrays.asList(tag("key", "desc", "value")));
        TestPatternFilter.shouldAccept(empty, TestPatternFilter.mockMetricsRecord("anything", Arrays.asList(tag("key", "desc", "value"))));
    }

    /**
     * Filters should handle white-listing correctly
     */
    @Test
    public void includeOnlyShouldOnlyIncludeMatched() {
        SubsetConfiguration wl = new ConfigBuilder().add("p.include", "foo").add("p.include.tags", "foo:f").subset("p");
        TestPatternFilter.shouldAccept(wl, "foo");
        TestPatternFilter.shouldAccept(wl, Arrays.asList(tag("bar", "", ""), tag("foo", "", "f")), new boolean[]{ false, true });
        TestPatternFilter.shouldAccept(wl, TestPatternFilter.mockMetricsRecord("foo", Arrays.asList(tag("bar", "", ""), tag("foo", "", "f"))));
        TestPatternFilter.shouldReject(wl, "bar");
        TestPatternFilter.shouldReject(wl, Arrays.asList(tag("bar", "", "")));
        TestPatternFilter.shouldReject(wl, Arrays.asList(tag("foo", "", "boo")));
        TestPatternFilter.shouldReject(wl, TestPatternFilter.mockMetricsRecord("bar", Arrays.asList(tag("foo", "", "f"))));
        TestPatternFilter.shouldReject(wl, TestPatternFilter.mockMetricsRecord("foo", Arrays.asList(tag("bar", "", ""))));
    }

    /**
     * Filters should handle black-listing correctly
     */
    @Test
    public void excludeOnlyShouldOnlyExcludeMatched() {
        SubsetConfiguration bl = new ConfigBuilder().add("p.exclude", "foo").add("p.exclude.tags", "foo:f").subset("p");
        TestPatternFilter.shouldAccept(bl, "bar");
        TestPatternFilter.shouldAccept(bl, Arrays.asList(tag("bar", "", "")));
        TestPatternFilter.shouldAccept(bl, TestPatternFilter.mockMetricsRecord("bar", Arrays.asList(tag("bar", "", ""))));
        TestPatternFilter.shouldReject(bl, "foo");
        TestPatternFilter.shouldReject(bl, Arrays.asList(tag("bar", "", ""), tag("foo", "", "f")), new boolean[]{ true, false });
        TestPatternFilter.shouldReject(bl, TestPatternFilter.mockMetricsRecord("foo", Arrays.asList(tag("bar", "", ""))));
        TestPatternFilter.shouldReject(bl, TestPatternFilter.mockMetricsRecord("bar", Arrays.asList(tag("bar", "", ""), tag("foo", "", "f"))));
    }

    /**
     * Filters should accepts unmatched item when both include and
     * exclude patterns are present.
     */
    @Test
    public void shouldAcceptUnmatchedWhenBothAreConfigured() {
        SubsetConfiguration c = new ConfigBuilder().add("p.include", "foo").add("p.include.tags", "foo:f").add("p.exclude", "bar").add("p.exclude.tags", "bar:b").subset("p");
        TestPatternFilter.shouldAccept(c, "foo");
        TestPatternFilter.shouldAccept(c, Arrays.asList(tag("foo", "", "f")));
        TestPatternFilter.shouldAccept(c, TestPatternFilter.mockMetricsRecord("foo", Arrays.asList(tag("foo", "", "f"))));
        TestPatternFilter.shouldReject(c, "bar");
        TestPatternFilter.shouldReject(c, Arrays.asList(tag("bar", "", "b")));
        TestPatternFilter.shouldReject(c, TestPatternFilter.mockMetricsRecord("bar", Arrays.asList(tag("foo", "", "f"))));
        TestPatternFilter.shouldReject(c, TestPatternFilter.mockMetricsRecord("foo", Arrays.asList(tag("bar", "", "b"))));
        TestPatternFilter.shouldAccept(c, "foobar");
        TestPatternFilter.shouldAccept(c, Arrays.asList(tag("foobar", "", "")));
        TestPatternFilter.shouldAccept(c, TestPatternFilter.mockMetricsRecord("foobar", Arrays.asList(tag("foobar", "", ""))));
    }

    /**
     * Include patterns should take precedence over exclude patterns
     */
    @Test
    public void includeShouldOverrideExclude() {
        SubsetConfiguration c = new ConfigBuilder().add("p.include", "foo").add("p.include.tags", "foo:f").add("p.exclude", "foo").add("p.exclude.tags", "foo:f").subset("p");
        TestPatternFilter.shouldAccept(c, "foo");
        TestPatternFilter.shouldAccept(c, Arrays.asList(tag("foo", "", "f")));
        TestPatternFilter.shouldAccept(c, TestPatternFilter.mockMetricsRecord("foo", Arrays.asList(tag("foo", "", "f"))));
    }
}

