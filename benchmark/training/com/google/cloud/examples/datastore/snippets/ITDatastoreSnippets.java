/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.examples.datastore.snippets;


import com.google.cloud.datastore.Batch;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;


public class ITDatastoreSnippets {
    private static Datastore datastore;

    private static DatastoreSnippets datastoreSnippets;

    private final List<Key> registeredKeys = new ArrayList<>();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Test
    public void testRunInTransaction() {
        String testString = "Test String";
        String result = ITDatastoreSnippets.datastoreSnippets.runInTransaction(testString);
        Assert.assertEquals(testString, result);
    }

    @Test
    public void testNewBatch() {
        String testKey1 = registerKey("new_batch_key1");
        String testKey2 = registerKey("new_batch_key2");
        Batch batch = ITDatastoreSnippets.datastoreSnippets.newBatch(testKey1, testKey2);
        Assert.assertNotNull(batch);
    }

    @Test
    public void testAllocateIdSingle() {
        Key key = ITDatastoreSnippets.datastoreSnippets.allocateIdSingle();
        Assert.assertNotNull(key);
    }

    @Test
    public void testAllocateIdMultiple() {
        List<Key> keys = ITDatastoreSnippets.datastoreSnippets.batchAllocateId();
        Assert.assertEquals(2, keys.size());
    }

    @Test
    public void testEntityAddGet() {
        String key = registerKey("my_single_key_add");
        ITDatastoreSnippets.datastoreSnippets.addSingleEntity(key);
        Entity entity = ITDatastoreSnippets.datastoreSnippets.getEntityWithKey(key);
        Assert.assertEquals("value", entity.getString("propertyName"));
    }

    @Test
    public void testEntityPutGet() {
        String key = registerKey("my_single_key_put");
        ITDatastoreSnippets.datastoreSnippets.putSingleEntity(key);
        Entity entity = ITDatastoreSnippets.datastoreSnippets.getEntityWithKey(key);
        Assert.assertEquals("value", entity.getString("propertyName"));
    }

    @Test
    public void testBatchEntityCrud() {
        String key1 = registerKey("batch_key1");
        String key2 = registerKey("batch_key2");
        ITDatastoreSnippets.datastoreSnippets.batchAddEntities(key1, key2);
        Assert.assertNotNull(ITDatastoreSnippets.datastoreSnippets.getEntityWithKey(key1));
        Assert.assertNotNull(ITDatastoreSnippets.datastoreSnippets.getEntityWithKey(key2));
        ITDatastoreSnippets.datastoreSnippets.batchDeleteEntities(key1, key2);
        Assert.assertNull(ITDatastoreSnippets.datastoreSnippets.getEntityWithKey(key1));
        Assert.assertNull(ITDatastoreSnippets.datastoreSnippets.getEntityWithKey(key2));
        ITDatastoreSnippets.datastoreSnippets.batchPutEntities(key1, key2);
        Assert.assertNotNull(ITDatastoreSnippets.datastoreSnippets.getEntityWithKey(key1));
        Assert.assertNotNull(ITDatastoreSnippets.datastoreSnippets.getEntityWithKey(key2));
        List<Entity> entities = Lists.newArrayList(ITDatastoreSnippets.datastoreSnippets.getEntitiesWithKeys(key1, key2));
        Assert.assertEquals(2, entities.size());
        Map<String, Entity> entityMap = createEntityMap(entities);
        Assert.assertEquals("value1", entityMap.get(key1).getString("propertyName"));
        Assert.assertEquals("value2", entityMap.get(key2).getString("propertyName"));
        ITDatastoreSnippets.datastoreSnippets.batchUpdateEntities(key1, key2);
        List<Entity> fetchedEntities = ITDatastoreSnippets.datastoreSnippets.fetchEntitiesWithKeys(key1, key2);
        Assert.assertEquals(2, fetchedEntities.size());
        Map<String, Entity> fetchedEntityMap = createEntityMap(fetchedEntities);
        Assert.assertEquals("updatedValue1", fetchedEntityMap.get(key1).getString("propertyName"));
        Assert.assertEquals("updatedValue2", fetchedEntityMap.get(key2).getString("propertyName"));
        ITDatastoreSnippets.datastoreSnippets.batchDeleteEntities(key1, key2);
        Assert.assertNull(ITDatastoreSnippets.datastoreSnippets.getEntityWithKey(key1));
        Assert.assertNull(ITDatastoreSnippets.datastoreSnippets.getEntityWithKey(key2));
        List<Entity> fetchedEntities2 = ITDatastoreSnippets.datastoreSnippets.fetchEntitiesWithKeys(key1, key2);
        Assert.assertEquals(2, fetchedEntities2.size());
        Assert.assertNull(fetchedEntities2.get(0));
        Assert.assertNull(fetchedEntities2.get(1));
    }

    @Test
    public void testCreateKeyFactory() {
        KeyFactory keyFactory = ITDatastoreSnippets.datastoreSnippets.createKeyFactory();
        Assert.assertNotNull(keyFactory);
    }

    @Test
    public void testRunQuery() {
        String kindToFind = "ClassToFind";
        String kindToMiss = "OtherClass";
        String keyNameToFind = registerKey("my_key_name_to_find", kindToFind);
        String otherKeyNameToFind = registerKey("other_key_name_to_find", kindToFind);
        String keyNameToMiss = registerKey("my_key_name_to_miss", kindToMiss);
        String property = "my_property_name";
        String valueToFind = "my_value_to_find";
        String valueToMiss = "my_value_to_miss";
        addEntity(keyNameToFind, kindToFind, property, valueToFind);
        addEntity(otherKeyNameToFind, kindToFind, property, valueToMiss);
        addEntity(keyNameToMiss, kindToMiss, property, valueToFind);
        List<Entity> queryResults = ITDatastoreSnippets.datastoreSnippets.runQuery(kindToFind);
        Assert.assertNotNull(queryResults);
        Assert.assertEquals(2, queryResults.size());
        queryResults = ITDatastoreSnippets.datastoreSnippets.runQueryOnProperty(kindToFind, property, valueToFind);
        Assert.assertNotNull(queryResults);
        Assert.assertEquals(1, queryResults.size());
    }
}

