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
package com.hazelcast.query.impl;


import Warning.NONFINAL_FIELDS;
import Warning.STRICT_INHERITANCE;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import java.util.Set;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class FalsePredicateTest extends HazelcastTestSupport {
    private FalsePredicate falsePredicate;

    private SerializationService serializationService;

    @Test
    public void apply() {
        Map.Entry entry = Mockito.mock(Map.Entry.class);
        boolean result = falsePredicate.apply(entry);
        Assert.assertFalse(result);
    }

    @Test
    public void isIndexed() {
        QueryContext queryContext = Mockito.mock(QueryContext.class);
        Assert.assertTrue(falsePredicate.isIndexed(queryContext));
    }

    @Test
    public void filter() {
        QueryContext queryContext = Mockito.mock(QueryContext.class);
        Set<QueryableEntry> result = falsePredicate.filter(queryContext);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void serialize() {
        Data data = serializationService.toData(falsePredicate);
        Object result = serializationService.toObject(data);
        HazelcastTestSupport.assertInstanceOf(FalsePredicate.class, result);
    }

    @Test
    public void testToString() {
        String result = falsePredicate.toString();
        Assert.assertEquals("FalsePredicate{}", result);
    }

    @Test
    public void testEqualsAndHashCode() {
        EqualsVerifier.forClass(FalsePredicate.class).suppress(NONFINAL_FIELDS, STRICT_INHERITANCE).allFieldsShouldBeUsed().verify();
    }
}

