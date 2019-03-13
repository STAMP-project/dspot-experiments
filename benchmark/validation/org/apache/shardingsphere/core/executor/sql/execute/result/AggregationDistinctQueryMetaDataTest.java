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
package org.apache.shardingsphere.core.executor.sql.execute.result;


import AggregationType.AVG;
import java.util.Arrays;
import java.util.Collection;
import org.apache.shardingsphere.core.constant.AggregationType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class AggregationDistinctQueryMetaDataTest {
    private AggregationDistinctQueryMetaData distinctQueryMetaData;

    @Test
    public void assertGetAggregationDistinctColumnIndexes() {
        Collection<Integer> actual = distinctQueryMetaData.getAggregationDistinctColumnIndexes();
        Collection<Integer> expected = Arrays.asList(1, 2);
        Assert.assertThat(actual.size(), CoreMatchers.is(2));
        Assert.assertThat(actual.iterator().next(), CoreMatchers.is(expected.iterator().next()));
    }

    @Test
    public void assertGetAggregationDistinctColumnLabels() {
        Collection<String> actual = distinctQueryMetaData.getAggregationDistinctColumnLabels();
        Collection<String> expected = Arrays.asList("c", "a");
        Assert.assertThat(actual.size(), CoreMatchers.is(2));
        Assert.assertThat(actual.iterator().next(), CoreMatchers.is(expected.iterator().next()));
    }

    @Test
    public void assertGetAggregationType() {
        AggregationType actual = distinctQueryMetaData.getAggregationType(2);
        Assert.assertThat(actual, CoreMatchers.is(AVG));
    }

    @Test
    public void assertGetDerivedCountColumnIndexes() {
        Collection<Integer> actual = distinctQueryMetaData.getDerivedCountColumnIndexes();
        Assert.assertThat(actual.size(), CoreMatchers.is(1));
        Assert.assertThat(actual.iterator().next(), CoreMatchers.is(3));
    }

    @Test
    public void assertGetDerivedSumColumnIndexes() {
        Collection<Integer> actual = distinctQueryMetaData.getDerivedSumColumnIndexes();
        Assert.assertThat(actual.size(), CoreMatchers.is(1));
        Assert.assertThat(actual.iterator().next(), CoreMatchers.is(4));
    }

    @Test
    public void assertGetAggregationDistinctColumnIndexByColumnLabel() {
        int actual = distinctQueryMetaData.getAggregationDistinctColumnIndex("a");
        Assert.assertThat(actual, CoreMatchers.is(2));
    }

    @Test
    public void assertGetAggregationDistinctColumnIndexBySumIndex() {
        int actual = distinctQueryMetaData.getAggregationDistinctColumnIndex(4);
        Assert.assertThat(actual, CoreMatchers.is(2));
    }

    @Test
    public void assertGetAggregationDistinctColumnLabel() {
        String actual = distinctQueryMetaData.getAggregationDistinctColumnLabel(1);
        Assert.assertThat(actual, CoreMatchers.is("c"));
    }
}

