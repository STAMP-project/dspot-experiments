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


import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CompositeValueTest {
    @Test
    public void testConstruction() {
        MatcherAssert.assertThat(new CompositeValue(new Comparable[]{ 1, 1.1, "1.1" }).getComponents(), Matchers.equalTo(new Object[]{ 1, 1.1, "1.1" }));
        MatcherAssert.assertThat(new CompositeValue(1, "prefix", null).getComponents(), Matchers.equalTo(new Object[]{ "prefix" }));
        MatcherAssert.assertThat(new CompositeValue(2, "prefix", "filler").getComponents(), Matchers.equalTo(new Object[]{ "prefix", "filler" }));
        MatcherAssert.assertThat(new CompositeValue(5, "prefix", "filler").getComponents(), Matchers.equalTo(new Object[]{ "prefix", "filler", "filler", "filler", "filler" }));
    }

    @Test
    public void testEqualsAndHashCode() {
        CompositeValue a = CompositeValueTest.value("a", 'a');
        MatcherAssert.assertThat(a, Matchers.equalTo(a));
        MatcherAssert.assertThat(a, Matchers.not(Matchers.equalTo(null)));
        MatcherAssert.assertThat(CompositeValueTest.value("a", 'a'), Matchers.equalTo(CompositeValueTest.value("a", 'a')));
        MatcherAssert.assertThat(CompositeValueTest.value("a", 'a').hashCode(), Matchers.equalTo(CompositeValueTest.value("a", 'a').hashCode()));
        MatcherAssert.assertThat(CompositeValueTest.value("a", 'a'), Matchers.not(Matchers.equalTo(CompositeValueTest.value('a', "a"))));
        MatcherAssert.assertThat(CompositeValueTest.value("a", 'a'), Matchers.not(Matchers.equalTo(CompositeValueTest.value("b", 'b'))));
        MatcherAssert.assertThat(CompositeValueTest.value("a", 'a'), Matchers.not(Matchers.equalTo(CompositeValueTest.value('b', "b"))));
        MatcherAssert.assertThat(CompositeValueTest.value("a", 'a'), Matchers.not(Matchers.equalTo(CompositeValueTest.value(null, 'a'))));
        MatcherAssert.assertThat(CompositeValueTest.value("a", 'a'), Matchers.not(Matchers.equalTo(CompositeValueTest.value("a", AbstractIndex.NULL))));
        MatcherAssert.assertThat(CompositeValueTest.value("a", 'a'), Matchers.not(Matchers.equalTo(CompositeValueTest.value("a", CompositeValue.NEGATIVE_INFINITY))));
        MatcherAssert.assertThat(CompositeValueTest.value("a", 'a'), Matchers.not(Matchers.equalTo(CompositeValueTest.value("a", CompositeValue.POSITIVE_INFINITY))));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSpecialValuesOrdering() {
        MatcherAssert.assertThat(AbstractIndex.NULL, Matchers.comparesEqualTo(AbstractIndex.NULL));
        MatcherAssert.assertThat(AbstractIndex.NULL, Matchers.equalTo(AbstractIndex.NULL));
        MatcherAssert.assertThat(AbstractIndex.NULL, Matchers.greaterThan(CompositeValue.NEGATIVE_INFINITY));
        MatcherAssert.assertThat(AbstractIndex.NULL, Matchers.lessThan(CompositeValue.POSITIVE_INFINITY));
        MatcherAssert.assertThat(CompositeValue.NEGATIVE_INFINITY, Matchers.lessThan(AbstractIndex.NULL));
        MatcherAssert.assertThat(CompositeValue.NEGATIVE_INFINITY, Matchers.lessThan(CompositeValue.POSITIVE_INFINITY));
        MatcherAssert.assertThat(CompositeValue.POSITIVE_INFINITY, Matchers.greaterThan(AbstractIndex.NULL));
        MatcherAssert.assertThat(CompositeValue.POSITIVE_INFINITY, Matchers.greaterThan(CompositeValue.NEGATIVE_INFINITY));
        MatcherAssert.assertThat(AbstractIndex.NULL, Matchers.lessThan(((Comparable) ("a"))));
        MatcherAssert.assertThat(CompositeValue.NEGATIVE_INFINITY, Matchers.lessThan(((Comparable) ("a"))));
        MatcherAssert.assertThat(CompositeValue.POSITIVE_INFINITY, Matchers.greaterThan(((Comparable) ("a"))));
    }

    @Test
    public void testComparable() {
        CompositeValue a = CompositeValueTest.value("a", 'a');
        MatcherAssert.assertThat(a, Matchers.comparesEqualTo(a));
        MatcherAssert.assertThat(CompositeValueTest.value("a", 'a'), Matchers.comparesEqualTo(CompositeValueTest.value("a", 'a')));
        MatcherAssert.assertThat(CompositeValueTest.value("a", 'a'), Matchers.not(Matchers.comparesEqualTo(CompositeValueTest.value("b", 'b'))));
        MatcherAssert.assertThat(CompositeValueTest.value("a", 'a'), Matchers.not(Matchers.comparesEqualTo(CompositeValueTest.value("a", AbstractIndex.NULL))));
        MatcherAssert.assertThat(CompositeValueTest.value("a", 'a'), Matchers.not(Matchers.comparesEqualTo(CompositeValueTest.value("a", CompositeValue.NEGATIVE_INFINITY))));
        MatcherAssert.assertThat(CompositeValueTest.value("a", 'a'), Matchers.not(Matchers.comparesEqualTo(CompositeValueTest.value("a", CompositeValue.POSITIVE_INFINITY))));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.lessThan(CompositeValueTest.value("b", 'c')));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.greaterThan(CompositeValueTest.value("b", 'a')));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.lessThan(CompositeValueTest.value("c", 'b')));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.greaterThan(CompositeValueTest.value("a", 'b')));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.lessThan(CompositeValueTest.value("c", 'c')));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.greaterThan(CompositeValueTest.value("a", 'a')));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.greaterThan(CompositeValueTest.value(AbstractIndex.NULL, AbstractIndex.NULL)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", AbstractIndex.NULL), Matchers.greaterThan(CompositeValueTest.value(AbstractIndex.NULL, AbstractIndex.NULL)));
        MatcherAssert.assertThat(CompositeValueTest.value(AbstractIndex.NULL, 'b'), Matchers.greaterThan(CompositeValueTest.value(AbstractIndex.NULL, AbstractIndex.NULL)));
        MatcherAssert.assertThat(CompositeValueTest.value(AbstractIndex.NULL, AbstractIndex.NULL), Matchers.comparesEqualTo(CompositeValueTest.value(AbstractIndex.NULL, AbstractIndex.NULL)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.greaterThan(CompositeValueTest.value("b", AbstractIndex.NULL)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.greaterThan(CompositeValueTest.value(AbstractIndex.NULL, 'b')));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.greaterThan(CompositeValueTest.value(CompositeValue.NEGATIVE_INFINITY, CompositeValue.NEGATIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", AbstractIndex.NULL), Matchers.greaterThan(CompositeValueTest.value(CompositeValue.NEGATIVE_INFINITY, CompositeValue.NEGATIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value(AbstractIndex.NULL, 'b'), Matchers.greaterThan(CompositeValueTest.value(CompositeValue.NEGATIVE_INFINITY, CompositeValue.NEGATIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value(AbstractIndex.NULL, AbstractIndex.NULL), Matchers.greaterThan(CompositeValueTest.value(CompositeValue.NEGATIVE_INFINITY, CompositeValue.NEGATIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.lessThan(CompositeValueTest.value(CompositeValue.POSITIVE_INFINITY, CompositeValue.NEGATIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", AbstractIndex.NULL), Matchers.lessThan(CompositeValueTest.value(CompositeValue.POSITIVE_INFINITY, CompositeValue.NEGATIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value(AbstractIndex.NULL, 'b'), Matchers.lessThan(CompositeValueTest.value(CompositeValue.POSITIVE_INFINITY, CompositeValue.NEGATIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value(AbstractIndex.NULL, AbstractIndex.NULL), Matchers.lessThan(CompositeValueTest.value(CompositeValue.POSITIVE_INFINITY, CompositeValue.NEGATIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.greaterThan(CompositeValueTest.value(CompositeValue.NEGATIVE_INFINITY, CompositeValue.POSITIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", AbstractIndex.NULL), Matchers.greaterThan(CompositeValueTest.value(CompositeValue.NEGATIVE_INFINITY, CompositeValue.POSITIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value(AbstractIndex.NULL, 'b'), Matchers.greaterThan(CompositeValueTest.value(CompositeValue.NEGATIVE_INFINITY, CompositeValue.POSITIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value(AbstractIndex.NULL, AbstractIndex.NULL), Matchers.greaterThan(CompositeValueTest.value(CompositeValue.NEGATIVE_INFINITY, CompositeValue.POSITIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.lessThan(CompositeValueTest.value(CompositeValue.POSITIVE_INFINITY, CompositeValue.POSITIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", AbstractIndex.NULL), Matchers.lessThan(CompositeValueTest.value(CompositeValue.POSITIVE_INFINITY, CompositeValue.POSITIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value(AbstractIndex.NULL, 'b'), Matchers.lessThan(CompositeValueTest.value(CompositeValue.POSITIVE_INFINITY, CompositeValue.POSITIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value(AbstractIndex.NULL, AbstractIndex.NULL), Matchers.lessThan(CompositeValueTest.value(CompositeValue.POSITIVE_INFINITY, CompositeValue.POSITIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.greaterThan(CompositeValueTest.value("b", CompositeValue.NEGATIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.lessThan(CompositeValueTest.value("b", CompositeValue.POSITIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.greaterThan(CompositeValueTest.value("a", CompositeValue.NEGATIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.greaterThan(CompositeValueTest.value("a", CompositeValue.POSITIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.lessThan(CompositeValueTest.value("c", CompositeValue.NEGATIVE_INFINITY)));
        MatcherAssert.assertThat(CompositeValueTest.value("b", 'b'), Matchers.lessThan(CompositeValueTest.value("c", CompositeValue.POSITIVE_INFINITY)));
    }

    @Test
    public void testSerialization() {
        InternalSerializationService serializationService = new DefaultSerializationServiceBuilder().build();
        Data data = serializationService.toData(CompositeValueTest.value(1.0, "a", AbstractIndex.NULL, CompositeValue.POSITIVE_INFINITY, CompositeValue.NEGATIVE_INFINITY));
        MatcherAssert.assertThat(CompositeValueTest.value(1.0, "a", AbstractIndex.NULL, CompositeValue.POSITIVE_INFINITY, CompositeValue.NEGATIVE_INFINITY), Matchers.equalTo(serializationService.toObject(data)));
    }
}

