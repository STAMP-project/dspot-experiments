/**
 * Copyright 2016 KairosDB Authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.core.aggregator;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kairosdb.core.datastore.DataPointGroup;
import org.kairosdb.testing.ListDataPointGroup;


public class PercentileAggregatorTest {
    private PercentileAggregator aggregator;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test(expected = NullPointerException.class)
    public void test_nullSet_invalid() {
        aggregator.aggregate(null);
    }

    @Test
    public void test_longValues() {
        test_percentileValue_long(0.75, 10);
        test_percentileValue_long(0.9, 10);
        test_percentileValue_long(0.95, 10);
        test_percentileValue_long(0.98, 10);
        test_percentileValue_long(0.999, 10);
        test_percentileValue_long(0.75, 100);
        test_percentileValue_long(0.9, 100);
        test_percentileValue_long(0.95, 100);
        test_percentileValue_long(0.98, 100);
        test_percentileValue_long(0.999, 100);
        test_percentileValue_long(0.75, 10000);
        test_percentileValue_long(0.9, 10000);
        test_percentileValue_long(0.95, 10000);
        test_percentileValue_long(0.98, 10000);
        test_percentileValue_long(0.999, 10000);
    }

    @Test
    public void test_doubleValues() {
        test_percentileValue_double(0.75, 10);
        test_percentileValue_double(0.9, 10);
        test_percentileValue_double(0.95, 10);
        test_percentileValue_double(0.98, 10);
        test_percentileValue_double(0.999, 10);
        test_percentileValue_double(0.75, 100);
        test_percentileValue_double(0.9, 100);
        test_percentileValue_double(0.95, 100);
        test_percentileValue_double(0.98, 100);
        test_percentileValue_double(0.999, 100);
        test_percentileValue_double(0.75, 10000);
        test_percentileValue_double(0.9, 10000);
        test_percentileValue_double(0.95, 10000);
        test_percentileValue_double(0.98, 10000);
        test_percentileValue_double(0.999, 10000);
    }

    @Test
    public void test_mixedTypeValues() {
        test_percentileValue_mixedTypeValues(0.75, 10);
        test_percentileValue_mixedTypeValues(0.9, 10);
        test_percentileValue_mixedTypeValues(0.95, 10);
        test_percentileValue_mixedTypeValues(0.98, 10);
        test_percentileValue_mixedTypeValues(0.999, 10);
        test_percentileValue_mixedTypeValues(0.75, 100);
        test_percentileValue_mixedTypeValues(0.9, 100);
        test_percentileValue_mixedTypeValues(0.95, 100);
        test_percentileValue_mixedTypeValues(0.98, 100);
        test_percentileValue_mixedTypeValues(0.999, 100);
        test_percentileValue_mixedTypeValues(0.75, 10000);
        test_percentileValue_mixedTypeValues(0.9, 10000);
        test_percentileValue_mixedTypeValues(0.95, 10000);
        test_percentileValue_mixedTypeValues(0.98, 10000);
        test_percentileValue_mixedTypeValues(0.999, 10000);
    }

    @Test
    public void test_noValues() {
        ListDataPointGroup group = new ListDataPointGroup("group");
        DataPointGroup results = aggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_invalidPercentiles() {
        exception.expect(IllegalArgumentException.class);
        test_percentileValue_long(5, 10);
        exception.expect(IllegalArgumentException.class);
        test_percentileValue_double(1.2, 10);
        exception.expect(IllegalArgumentException.class);
        test_percentileValue_mixedTypeValues((-2), 10);
        exception.expect(IllegalArgumentException.class);
        test_percentileValue_long(1.00001, 10);
    }
}

