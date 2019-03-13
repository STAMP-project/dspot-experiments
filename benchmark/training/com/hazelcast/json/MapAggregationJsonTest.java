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
package com.hazelcast.json;


import com.hazelcast.aggregation.Aggregators;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MetadataPolicy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.json.Json;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapAggregationJsonTest extends HazelcastTestSupport {
    public static final int OBJECT_COUNT = 1000;

    private static final String STRING_PREFIX = "s";

    TestHazelcastInstanceFactory factory;

    HazelcastInstance instance;

    @Parameterized.Parameter(0)
    public InMemoryFormat inMemoryFormat;

    @Parameterized.Parameter(1)
    public MetadataPolicy metadataPolicy;

    @Test
    public void testLongField() {
        IMap<Integer, HazelcastJsonValue> map = getPreloadedMap();
        long maxLongValue = map.aggregate(Aggregators.<Map.Entry<Integer, HazelcastJsonValue>>longMax("longValue"));
        Assert.assertEquals(((MapAggregationJsonTest.OBJECT_COUNT) - 1), maxLongValue);
    }

    @Test
    public void testDoubleField() {
        IMap<Integer, HazelcastJsonValue> map = getPreloadedMap();
        double maxDoubleValue = map.aggregate(Aggregators.<Map.Entry<Integer, HazelcastJsonValue>>doubleMax("doubleValue"));
        Assert.assertEquals(((MapAggregationJsonTest.OBJECT_COUNT) - 0.5), maxDoubleValue, 1.0E-5);
    }

    @Test
    public void testStringField() {
        IMap<Integer, HazelcastJsonValue> map = getPreloadedMap();
        String maxStringValue = map.aggregate(Aggregators.<Map.Entry<Integer, HazelcastJsonValue>, String>comparableMax("stringValue"));
        Assert.assertEquals(((MapAggregationJsonTest.STRING_PREFIX) + "999"), maxStringValue);
    }

    @Test
    public void testNestedField() {
        IMap<Integer, HazelcastJsonValue> map = getPreloadedMap();
        long maxLongValue = map.aggregate(Aggregators.<Map.Entry<Integer, HazelcastJsonValue>>longMax("nestedObject.nestedLongValue"));
        Assert.assertEquals((((MapAggregationJsonTest.OBJECT_COUNT) - 1) * 10), maxLongValue);
    }

    @Test
    public void testValueIsOmitted_whenObjectIsEmpty() {
        IMap<Integer, HazelcastJsonValue> map = getPreloadedMap();
        map.put(MapAggregationJsonTest.OBJECT_COUNT, HazelcastJson.fromString(Json.object().toString()));
        long maxLongValue = map.aggregate(Aggregators.<Map.Entry<Integer, HazelcastJsonValue>>longMax("longValue"));
        Assert.assertEquals(((MapAggregationJsonTest.OBJECT_COUNT) - 1), maxLongValue);
    }

    @Test
    public void testValueIsOmitted_whenAttributePathDoesNotExist() {
        IMap<Integer, HazelcastJsonValue> map = getPreloadedMap();
        map.put(MapAggregationJsonTest.OBJECT_COUNT, HazelcastJson.fromString(Json.object().add("someField", "someValue").toString()));
        long maxLongValue = map.aggregate(Aggregators.<Map.Entry<Integer, HazelcastJsonValue>>longMax("longValue"));
        Assert.assertEquals(((MapAggregationJsonTest.OBJECT_COUNT) - 1), maxLongValue);
    }

    @Test
    public void testValueIsOmitted_whenValueIsNotAnObject() {
        IMap<Integer, HazelcastJsonValue> map = getPreloadedMap();
        map.put(MapAggregationJsonTest.OBJECT_COUNT, HazelcastJson.fromString(Json.value(5).toString()));
        long maxLongValue = map.aggregate(Aggregators.<Map.Entry<Integer, HazelcastJsonValue>>longMax("longValue"));
        Assert.assertEquals(((MapAggregationJsonTest.OBJECT_COUNT) - 1), maxLongValue);
    }

    @Test
    public void testValueIsOmitted_whenAttributePathIsNotTerminal() {
        IMap<Integer, HazelcastJsonValue> map = getPreloadedMap();
        map.put(MapAggregationJsonTest.OBJECT_COUNT, HazelcastJson.fromString(Json.object().add("longValue", Json.object()).toString()));
        long count = map.aggregate(Aggregators.<Map.Entry<Integer, HazelcastJsonValue>>longMax("longValue"));
        Assert.assertEquals(((MapAggregationJsonTest.OBJECT_COUNT) - 1), count);
    }

    @Test
    public void testValueIsOmitted_whenAttributePathIsNotTerminal_count() {
        IMap<Integer, HazelcastJsonValue> map = getPreloadedMap();
        map.put(MapAggregationJsonTest.OBJECT_COUNT, HazelcastJson.fromString(Json.object().add("longValue", Json.object()).toString()));
        long count = map.aggregate(Aggregators.<Map.Entry<Integer, HazelcastJsonValue>>count("longValue"));
        Assert.assertEquals(MapAggregationJsonTest.OBJECT_COUNT, count);
    }

    @Test
    public void testValueIsOmitted_whenAttributePathIsNotTerminal_distinct() {
        IMap<Integer, HazelcastJsonValue> map = getPreloadedMap();
        map.put(MapAggregationJsonTest.OBJECT_COUNT, HazelcastJson.fromString(Json.object().add("longValue", Json.object()).toString()));
        Collection<Object> distinctLongValues = map.aggregate(Aggregators.<Map.Entry<Integer, HazelcastJsonValue>, Object>distinct("longValue"));
        Assert.assertEquals(MapAggregationJsonTest.OBJECT_COUNT, distinctLongValues.size());
    }

    @Test
    public void testAny() {
        IMap<Integer, HazelcastJsonValue> map = getPreloadedMap();
        Collection<Object> distinctStrings = map.aggregate(Aggregators.<Map.Entry<Integer, HazelcastJsonValue>, Object>distinct("stringValueArray[any]"));
        Assert.assertEquals(((MapAggregationJsonTest.OBJECT_COUNT) * 2), distinctStrings.size());
        for (int i = 0; i < (MapAggregationJsonTest.OBJECT_COUNT); i++) {
            HazelcastTestSupport.assertContains(distinctStrings, (("nested0 " + (MapAggregationJsonTest.STRING_PREFIX)) + i));
            HazelcastTestSupport.assertContains(distinctStrings, (("nested1 " + (MapAggregationJsonTest.STRING_PREFIX)) + i));
        }
    }
}

