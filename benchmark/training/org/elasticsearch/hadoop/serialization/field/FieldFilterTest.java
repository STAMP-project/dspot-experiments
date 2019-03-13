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
package org.elasticsearch.hadoop.serialization.field;


import java.util.ArrayList;
import java.util.Arrays;
import org.elasticsearch.hadoop.EsHadoopIllegalArgumentException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class FieldFilterTest {
    @Test
    public void testFilterNoIncludes() {
        Assert.assertTrue(FieldFilterTest.filter("foo.bar", null, null));
    }

    @Test
    public void testFilterOnlyIncludesNotMatching() {
        Assert.assertFalse(FieldFilterTest.filter("foo.bar", Arrays.asList("bar"), null));
    }

    @Test
    public void testFilterOnlyIncludesPartialMatching() {
        Assert.assertFalse(FieldFilterTest.filter("fo", Arrays.asList("foo.bar.baz"), null));
        Assert.assertTrue(FieldFilterTest.filter("foo", Arrays.asList("foo.bar.baz"), null));
        Assert.assertFalse(FieldFilterTest.filter("foo.ba", Arrays.asList("foo.bar.baz"), null));
        Assert.assertTrue(FieldFilterTest.filter("foo.bar", Arrays.asList("foo.bar.baz"), null));
        Assert.assertFalse(FieldFilterTest.filter("foo.bar.ba", Arrays.asList("foo.bar.baz"), null));
        Assert.assertTrue(FieldFilterTest.filter("foo.bar.baz", Arrays.asList("foo.bar.baz"), null));
        Assert.assertFalse(FieldFilterTest.filter("foo.bar.baz.qux", Arrays.asList("foo.bar.baz"), null));
    }

    @Test
    public void testFilterOnlyIncludesExactMatch() {
        Assert.assertTrue(FieldFilterTest.filter("foo.bar", Arrays.asList("foo.bar"), null));
    }

    @Test
    public void testFilterOnlyIncludesTopLevelMatchWithoutPattern() {
        Assert.assertFalse(FieldFilterTest.filter("foo.bar", Arrays.asList("foo"), null));
    }

    @Test
    public void testFilterOnlyIncludesTopLevelMatchWithPattern() {
        Assert.assertTrue(FieldFilterTest.filter("foo.bar", Arrays.asList("foo.*"), null));
    }

    @Test
    public void testFilterOnlyIncludesNestedMatch() {
        Assert.assertTrue(FieldFilterTest.filter("foo.bar", Arrays.asList("*.bar"), null));
    }

    @Test
    public void testFilterOnlyIncludesNestedPattern() {
        Assert.assertTrue(FieldFilterTest.filter("foo.bar.taz", Arrays.asList("foo.*ar.taz"), null));
    }

    @Test
    public void testFilterOnlyIncludesNestedPatternNotMatching() {
        Assert.assertFalse(FieldFilterTest.filter("foo.bar.taz", Arrays.asList("foo.br*.taz"), null));
    }

    @Test
    public void testFilterOnlyExcludesPartialMatch() {
        Assert.assertTrue(FieldFilterTest.filter("foo.bar", null, Arrays.asList("foo")));
    }

    @Test
    public void testFilterOnlyExcludesWithExactMatch() {
        Assert.assertFalse(FieldFilterTest.filter("foo.bar", null, Arrays.asList("foo.bar")));
    }

    @Test
    public void testFilterOnlyExcludesWithTopPatternMatch() {
        Assert.assertFalse(FieldFilterTest.filter("foo.bar", null, Arrays.asList("foo*")));
    }

    @Test
    public void testFilterOnlyExcludesWithNestedPatternMatch() {
        Assert.assertFalse(FieldFilterTest.filter("foo.bar", null, Arrays.asList("*.bar")));
    }

    @Test
    public void testFilterOnlyExcludesWithNestedMiddlePatternMatch() {
        Assert.assertFalse(FieldFilterTest.filter("foo.bar.taz", null, Arrays.asList("foo.*.taz")));
    }

    @Test
    public void testFilterIncludeAndExcludeExactMatch() {
        Assert.assertFalse(FieldFilterTest.filter("foo.bar", Arrays.asList("foo", "foo.bar"), Arrays.asList("foo.bar")));
    }

    @Test
    public void testFilterIncludeTopMatchWithExcludeNestedExactMatch() {
        Assert.assertFalse(FieldFilterTest.filter("foo.bar.taz", Arrays.asList("foo.bar.*"), Arrays.asList("foo.*.taz")));
    }

    @Test
    public void testFilterIncludeExactMatchWithExcludePattern() {
        Assert.assertFalse(FieldFilterTest.filter("foo.bar", Arrays.asList("foo.bar"), Arrays.asList("foo.*")));
    }

    @Test
    public void testFilterMatchNonExisting() {
        Assert.assertFalse(FieldFilterTest.filter("nested.what", Arrays.asList("nested.bar"), null));
    }

    @Test(expected = EsHadoopIllegalArgumentException.class)
    public void testCreateMalformedFilter() {
        FieldFilter.toNumberedFilter(Arrays.asList("a:broken"));
    }

    @Test
    public void testCreateSimpleFilter() {
        Assert.assertThat(new ArrayList<FieldFilter.NumberedInclude>(FieldFilter.toNumberedFilter(Arrays.asList("a"))), Matchers.contains(new FieldFilter.NumberedInclude("a", 1)));
    }

    @Test
    public void testCreateMultipleSimpleFilters() {
        Assert.assertThat(new ArrayList<FieldFilter.NumberedInclude>(FieldFilter.toNumberedFilter(Arrays.asList("a", "b"))), Matchers.contains(new FieldFilter.NumberedInclude("a", 1), new FieldFilter.NumberedInclude("b", 1)));
    }

    @Test
    public void testCreateSimpleNumberedFilter() {
        Assert.assertThat(new ArrayList<FieldFilter.NumberedInclude>(FieldFilter.toNumberedFilter(Arrays.asList("a:2"))), Matchers.contains(new FieldFilter.NumberedInclude("a", 2)));
    }

    @Test
    public void testCreateMultipleNumberedFilters() {
        Assert.assertThat(new ArrayList<FieldFilter.NumberedInclude>(FieldFilter.toNumberedFilter(Arrays.asList("a:2", "b:4"))), Matchers.contains(new FieldFilter.NumberedInclude("a", 2), new FieldFilter.NumberedInclude("b", 4)));
    }

    @Test
    public void testCreateMultipleMixedFilters() {
        Assert.assertThat(new ArrayList<FieldFilter.NumberedInclude>(FieldFilter.toNumberedFilter(Arrays.asList("a:2", "b:4", "c"))), Matchers.contains(new FieldFilter.NumberedInclude("a", 2), new FieldFilter.NumberedInclude("b", 4), new FieldFilter.NumberedInclude("c", 1)));
    }
}

