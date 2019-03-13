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
package com.hazelcast.json.internal;


import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapLoader;
import com.hazelcast.internal.json.Json;
import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.test.HazelcastParallelParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParallelParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class JsonMetadataCreationTest extends HazelcastTestSupport {
    private static final int ENTRY_COUNT = 1000;

    private static final int NODE_COUNT = 3;

    private static final int PARTITION_COUNT = 10;

    protected TestHazelcastInstanceFactory factory;

    protected HazelcastInstance[] instances;

    private IMap map;

    private IMap mapWithMapStore;

    private IMap mapWithoutMetadata;

    @Parameterized.Parameter
    public InMemoryFormat inMemoryFormat;

    @Test
    public void testPutCreatesMetadataForJson() {
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.put(JsonMetadataCreationTest.createJsonValue("key", i), JsonMetadataCreationTest.createJsonValue("value", i));
        }
        assertMetadataCreated(map.getName());
    }

    @Test
    public void testMetadataIsntCreatedWhenKeyAndValueAreNotJson() {
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.put(i, i);
        }
        for (int i = 0; i < (JsonMetadataCreationTest.NODE_COUNT); i++) {
            for (int j = 0; j < (JsonMetadataCreationTest.ENTRY_COUNT); j++) {
                Assert.assertNull(getMetadata(map.getName(), j, i));
            }
        }
    }

    @Test
    public void testMetadataIsRemoved_whenValueBecomesNonJson() {
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.put(i, JsonMetadataCreationTest.createJsonValue("value", i));
        }
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.put(i, i);
        }
        for (int i = 0; i < (JsonMetadataCreationTest.NODE_COUNT); i++) {
            for (int j = 0; j < (JsonMetadataCreationTest.ENTRY_COUNT); j++) {
                Assert.assertNull(getMetadata(map.getName(), j, i));
            }
        }
    }

    @Test
    public void testPutDoesNotCreateMetadata_whenMetadataPolicyIsOff() {
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            mapWithoutMetadata.put(JsonMetadataCreationTest.createJsonValue("key", i), JsonMetadataCreationTest.createJsonValue("value", i));
        }
        assertMetadataNotCreated(mapWithoutMetadata.getName());
    }

    @Test
    public void testPutCreatesMetadataForJson_whenReplacingExisting() {
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.put(JsonMetadataCreationTest.createJsonValue("key", i), "somevalue");
        }
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.put(JsonMetadataCreationTest.createJsonValue("key", i), JsonMetadataCreationTest.createJsonValue("value", i));
        }
        assertMetadataCreated(map.getName());
    }

    @Test
    public void testPutAsyncCreatesMetadataForJson() throws InterruptedException, ExecutionException {
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.putAsync(JsonMetadataCreationTest.createJsonValue("key", i), JsonMetadataCreationTest.createJsonValue("value", i)).get();
        }
        assertMetadataCreated(map.getName());
    }

    @Test
    public void testTryPutCreatesMetadataForJson() {
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.tryPut(JsonMetadataCreationTest.createJsonValue("key", i), JsonMetadataCreationTest.createJsonValue("value", i), 1, TimeUnit.HOURS);
        }
        assertMetadataCreated(map.getName());
    }

    @Test
    public void testPutTransientCreatesMetadataForJson() {
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.putTransient(JsonMetadataCreationTest.createJsonValue("key", i), JsonMetadataCreationTest.createJsonValue("value", i), 1, TimeUnit.HOURS);
        }
        assertMetadataCreated(map.getName());
    }

    @Test
    public void testPutIfAbsentCreatesMetadataForJson() {
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.putIfAbsent(JsonMetadataCreationTest.createJsonValue("key", i), JsonMetadataCreationTest.createJsonValue("value", i));
        }
        assertMetadataCreated(map.getName());
    }

    @Test
    public void testEntryProcessorCreatesMetadataForJson() {
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.put(JsonMetadataCreationTest.createJsonValue("key", i), "not-json-value");
        }
        map.executeOnEntries(new JsonMetadataCreationTest.ModifyingEntryProcessor());
        assertMetadataCreatedEventually(map.getName());
    }

    @Test
    public void testReplaceCreatesMetadataForJson() {
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.put(JsonMetadataCreationTest.createJsonValue("key", i), "not-json-value");
        }
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.replace(JsonMetadataCreationTest.createJsonValue("key", i), JsonMetadataCreationTest.createJsonValue("value", i));
        }
        assertMetadataCreated(map.getName());
    }

    @Test
    public void testReplaceIfSameCreatesMetadataForJson() {
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.put(JsonMetadataCreationTest.createJsonValue("key", i), "not-json-value");
        }
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.replace(JsonMetadataCreationTest.createJsonValue("key", i), "not-json-value", JsonMetadataCreationTest.createJsonValue("value", i));
        }
        assertMetadataCreated(map.getName());
    }

    @Test
    public void testLoadAllCreatesMetadataForJson() {
        mapWithMapStore.loadAll(false);
        assertMetadataCreatedEventually(mapWithMapStore.getName());
    }

    @Test
    public void testLoadCreatesMetadataForJson() {
        mapWithMapStore.loadAll(false);
        mapWithMapStore.evictAll();
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            mapWithMapStore.get(JsonMetadataCreationTest.createJsonValue("key", i));
        }
        assertMetadataCreated(mapWithMapStore.getName(), 1);
    }

    @Test
    public void testPutAllCreatesMetadataForJson() {
        Map<HazelcastJsonValue, HazelcastJsonValue> localMap = new HashMap<HazelcastJsonValue, HazelcastJsonValue>();
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            localMap.put(JsonMetadataCreationTest.createJsonValue("key", i), JsonMetadataCreationTest.createJsonValue("value", i));
        }
        map.putAll(localMap);
        assertMetadataCreated(map.getName());
    }

    @Test
    public void testSetCreatesMetadataForJson() {
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.put(JsonMetadataCreationTest.createJsonValue("key", i), "not-json-value");
        }
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.set(JsonMetadataCreationTest.createJsonValue("key", i), JsonMetadataCreationTest.createJsonValue("value", i));
        }
        assertMetadataCreated(map.getName());
    }

    @Test
    public void testSetAsyncCreatesMetadataForJson() throws InterruptedException, ExecutionException {
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.put(JsonMetadataCreationTest.createJsonValue("key", i), "not-json-value");
        }
        for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
            map.setAsync(JsonMetadataCreationTest.createJsonValue("key", i), JsonMetadataCreationTest.createJsonValue("value", i)).get();
        }
        assertMetadataCreated(map.getName());
    }

    static class JsonMapLoader implements MapLoader<HazelcastJsonValue, HazelcastJsonValue> {
        @Override
        public HazelcastJsonValue load(HazelcastJsonValue key) {
            int value = Json.parse(key.toJsonString()).asObject().get("value").asInt();
            return JsonMetadataCreationTest.createJsonValue("value", value);
        }

        @Override
        public Map<HazelcastJsonValue, HazelcastJsonValue> loadAll(Collection<HazelcastJsonValue> keys) {
            Map<HazelcastJsonValue, HazelcastJsonValue> localMap = new HashMap<HazelcastJsonValue, HazelcastJsonValue>();
            for (HazelcastJsonValue key : keys) {
                int value = Json.parse(key.toJsonString()).asObject().get("value").asInt();
                localMap.put(key, JsonMetadataCreationTest.createJsonValue("value", value));
            }
            return localMap;
        }

        @Override
        public Iterable<HazelcastJsonValue> loadAllKeys() {
            Collection<HazelcastJsonValue> localKeys = new ArrayList<HazelcastJsonValue>();
            for (int i = 0; i < (JsonMetadataCreationTest.ENTRY_COUNT); i++) {
                localKeys.add(JsonMetadataCreationTest.createJsonValue("key", i));
            }
            return localKeys;
        }
    }

    static class ModifyingEntryProcessor extends AbstractEntryProcessor<HazelcastJsonValue, Object> {
        @Override
        public Object process(Map.Entry<HazelcastJsonValue, Object> entry) {
            HazelcastJsonValue key = entry.getKey();
            int value = Json.parse(key.toJsonString()).asObject().get("value").asInt();
            entry.setValue(JsonMetadataCreationTest.createJsonValue("value", value));
            return null;
        }
    }
}

