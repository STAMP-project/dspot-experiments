/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map.impl.query;


import com.hazelcast.query.Predicates;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(QuickTest.class)
public class QueryNullIndexingTest extends HazelcastTestSupport {
    @Parameterized.Parameter(0)
    public IndexCopyBehavior copyBehavior;

    @Test
    public void testIndexedNullValueOnUnorderedIndexStoreWithLessPredicate() {
        List<Long> dates = queryIndexedDateFieldAsNullValue(false, Predicates.lessThan("date", 5000000L));
        Assert.assertEquals(2, dates.size());
        HazelcastTestSupport.assertContainsAll(dates, Arrays.asList(2000000L, 4000000L));
    }

    @Test
    public void testIndexedNullValueOnUnorderedIndexStoreWithLessEqualPredicate() {
        List<Long> dates = queryIndexedDateFieldAsNullValue(false, Predicates.lessEqual("date", 5000000L));
        Assert.assertEquals(2, dates.size());
        HazelcastTestSupport.assertContainsAll(dates, Arrays.asList(2000000L, 4000000L));
    }

    @Test
    public void testIndexedNullValueOnUnorderedIndexStoreWithGreaterPredicate() {
        List<Long> dates = queryIndexedDateFieldAsNullValue(false, Predicates.greaterThan("date", 5000000L));
        Assert.assertEquals(3, dates.size());
        HazelcastTestSupport.assertContainsAll(dates, Arrays.asList(6000000L, 8000000L, 10000000L));
    }

    @Test
    public void testIndexedNullValueOnUnorderedIndexStoreWithGreaterEqualPredicate() {
        List<Long> dates = queryIndexedDateFieldAsNullValue(false, Predicates.greaterEqual("date", 6000000L));
        Assert.assertEquals(3, dates.size());
        HazelcastTestSupport.assertContainsAll(dates, Arrays.asList(6000000L, 8000000L, 10000000L));
    }

    @Test
    public void testIndexedNullValueOnUnorderedIndexStoreWithNotEqualPredicate() {
        List<Long> dates = queryIndexedDateFieldAsNullValue(false, Predicates.notEqual("date", 2000000L));
        Assert.assertEquals(9, dates.size());
        HazelcastTestSupport.assertContainsAll(dates, Arrays.asList(4000000L, 6000000L, 8000000L, 10000000L, null));
    }

    @Test
    public void testIndexedNullValueOnOrderedIndexStoreWithLessPredicate() {
        List<Long> dates = queryIndexedDateFieldAsNullValue(true, Predicates.lessThan("date", 5000000L));
        Assert.assertEquals(2, dates.size());
        HazelcastTestSupport.assertContainsAll(dates, Arrays.asList(2000000L, 4000000L));
    }

    @Test
    public void testIndexedNullValueOnOrderedIndexStoreWithLessEqualPredicate() {
        List<Long> dates = queryIndexedDateFieldAsNullValue(true, Predicates.lessEqual("date", 5000000L));
        Assert.assertEquals(2, dates.size());
        HazelcastTestSupport.assertContainsAll(dates, Arrays.asList(2000000L, 4000000L));
    }

    @Test
    public void testIndexedNullValueOnOrderedIndexStoreWithGreaterPredicate() {
        List<Long> dates = queryIndexedDateFieldAsNullValue(true, Predicates.greaterThan("date", 5000000L));
        Assert.assertEquals(3, dates.size());
        HazelcastTestSupport.assertContainsAll(dates, Arrays.asList(6000000L, 8000000L, 10000000L));
    }

    @Test
    public void testIndexedNullValueOnOrderedIndexStoreWithGreaterEqualPredicate() {
        List<Long> dates = queryIndexedDateFieldAsNullValue(true, Predicates.greaterEqual("date", 6000000L));
        Assert.assertEquals(3, dates.size());
        HazelcastTestSupport.assertContainsAll(dates, Arrays.asList(6000000L, 8000000L, 10000000L));
    }

    @Test
    public void testIndexedNullValueOnOrderedIndexStoreWithNotEqualPredicate() {
        List<Long> dates = queryIndexedDateFieldAsNullValue(true, Predicates.notEqual("date", 2000000L));
        Assert.assertEquals(9, dates.size());
        HazelcastTestSupport.assertContainsAll(dates, Arrays.asList(4000000L, 6000000L, 8000000L, 10000000L, null));
    }
}

