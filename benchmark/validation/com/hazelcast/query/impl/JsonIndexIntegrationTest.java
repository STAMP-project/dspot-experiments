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


import com.hazelcast.config.CacheDeserializedValues;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MetadataPolicy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.core.IMap;
import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.json.HazelcastJson;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(QuickTest.class)
public class JsonIndexIntegrationTest extends HazelcastTestSupport {
    private static final String MAP_NAME = "map";

    @Parameterized.Parameter(0)
    public InMemoryFormat inMemoryFormat;

    @Parameterized.Parameter(1)
    public CacheDeserializedValues cacheDeserializedValues;

    @Parameterized.Parameter(2)
    public MetadataPolicy metadataPolicy;

    @Test
    public void testViaAccessingInternalIndexes() {
        HazelcastInstance instance = createHazelcastInstance();
        IMap<Integer, HazelcastJsonValue> map = instance.getMap(JsonIndexIntegrationTest.MAP_NAME);
        map.addIndex("age", false);
        map.addIndex("active", false);
        map.addIndex("name", false);
        for (int i = 0; i < 1000; i++) {
            String jsonString = ((("{\"age\" : " + i) + "  , \"name\" : \"sancar\" , \"active\" :  ") + ((i % 2) == 0)) + " } ";
            map.put(i, HazelcastJson.fromString(jsonString));
        }
        Set<QueryableEntry> records = getRecords(instance, JsonIndexIntegrationTest.MAP_NAME, "age", 40);
        Assert.assertEquals(1, records.size());
        records = getRecords(instance, JsonIndexIntegrationTest.MAP_NAME, "active", true);
        Assert.assertEquals(500, records.size());
        records = getRecords(instance, JsonIndexIntegrationTest.MAP_NAME, "name", "sancar");
        Assert.assertEquals(1000, records.size());
    }

    @Test
    public void testIndex_viaQueries() {
        HazelcastInstance instance = createHazelcastInstance();
        IMap<Integer, HazelcastJsonValue> map = instance.getMap(JsonIndexIntegrationTest.MAP_NAME);
        map.addIndex("age", false);
        map.addIndex("active", false);
        map.addIndex("name", false);
        for (int i = 0; i < 1000; i++) {
            String jsonString = ((("{\"age\" : " + i) + "  , \"name\" : \"sancar\" , \"active\" :  ") + ((i % 2) == 0)) + " } ";
            map.put(i, HazelcastJson.fromString(jsonString));
        }
        Assert.assertEquals(500, map.values(Predicates.and(Predicates.equal("name", "sancar"), Predicates.equal("active", "true"))).size());
        Assert.assertEquals(299, map.values(Predicates.and(Predicates.greaterThan("age", 400), Predicates.equal("active", true))).size());
        Assert.assertEquals(1000, map.values(new SqlPredicate("name == sancar")).size());
    }

    @Test
    public void testEntryProcessorChanges_viaQueries() {
        HazelcastInstance instance = createHazelcastInstance();
        IMap<Integer, HazelcastJsonValue> map = instance.getMap(JsonIndexIntegrationTest.MAP_NAME);
        map.addIndex("age", false);
        map.addIndex("active", false);
        map.addIndex("name", false);
        for (int i = 0; i < 1000; i++) {
            String jsonString = ((("{\"age\" : " + i) + "  , \"name\" : \"sancar\" , \"active\" :  ") + ((i % 2) == 0)) + " } ";
            map.put(i, HazelcastJson.fromString(jsonString));
        }
        Assert.assertEquals(500, map.values(Predicates.and(Predicates.equal("name", "sancar"), Predicates.equal("active", "true"))).size());
        Assert.assertEquals(299, map.values(Predicates.and(Predicates.greaterThan("age", 400), Predicates.equal("active", true))).size());
        Assert.assertEquals(1000, map.values(new SqlPredicate("name == sancar")).size());
        map.executeOnEntries(new JsonIndexIntegrationTest.JsonEntryProcessor());
        Assert.assertEquals(1000, map.values(Predicates.and(Predicates.equal("name", "sancar"), Predicates.equal("active", false))).size());
        Assert.assertEquals(0, map.values(Predicates.and(Predicates.greaterThan("age", 400), Predicates.equal("active", false))).size());
        Assert.assertEquals(1000, map.values(new SqlPredicate("name == sancar")).size());
    }

    @Test
    public void testEntryProcessorChanges_viaQueries_withoutIndex() {
        HazelcastInstance instance = createHazelcastInstance();
        IMap<Integer, HazelcastJsonValue> map = instance.getMap(JsonIndexIntegrationTest.MAP_NAME);
        for (int i = 0; i < 1000; i++) {
            String jsonString = ((("{\"age\" : " + i) + "  , \"name\" : \"sancar\" , \"active\" :  ") + ((i % 2) == 0)) + " } ";
            map.put(i, HazelcastJson.fromString(jsonString));
        }
        Assert.assertEquals(500, map.values(Predicates.and(Predicates.equal("name", "sancar"), Predicates.equal("active", "true"))).size());
        Assert.assertEquals(299, map.values(Predicates.and(Predicates.greaterThan("age", 400), Predicates.equal("active", true))).size());
        Assert.assertEquals(1000, map.values(new SqlPredicate("name == sancar")).size());
        map.executeOnEntries(new JsonIndexIntegrationTest.JsonEntryProcessor());
        Assert.assertEquals(1000, map.values(Predicates.and(Predicates.equal("name", "sancar"), Predicates.equal("active", false))).size());
        Assert.assertEquals(0, map.values(Predicates.and(Predicates.greaterThan("age", 400), Predicates.equal("active", false))).size());
        Assert.assertEquals(1000, map.values(new SqlPredicate("name == sancar")).size());
    }

    private static class JsonEntryProcessor implements EntryProcessor<Integer, HazelcastJsonValue> {
        @Override
        public Object process(Map.Entry<Integer, HazelcastJsonValue> entry) {
            JsonObject jsonObject = Json.parse(entry.getValue().toJsonString()).asObject();
            jsonObject.set("age", 0);
            jsonObject.set("active", false);
            entry.setValue(HazelcastJson.fromString(jsonObject.toString()));
            return "anyResult";
        }

        @Override
        public EntryBackupProcessor<Integer, HazelcastJsonValue> getBackupProcessor() {
            return new EntryBackupProcessor<Integer, HazelcastJsonValue>() {
                @Override
                public void processBackup(Map.Entry<Integer, HazelcastJsonValue> entry) {
                    process(entry);
                }
            };
        }
    }
}

