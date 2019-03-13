/**
 * Copyright (c) 2008-2016 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.morphia;


import WriteConcern.ACKNOWLEDGED;
import com.mongodb.client.MongoCollection;
import dev.morphia.testutil.TestEntity;
import java.util.Arrays;
import java.util.Collections;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;


public class TestAdvancedDatastore extends TestBase {
    @Test
    public void testInsert() {
        String name = "some_collection";
        MongoCollection<Document> collection = getMongoClient().getDatabase(TestBase.TEST_DB_NAME).getCollection(name);
        this.getAds().insert(name, new TestEntity());
        Assert.assertEquals(1, collection.count());
        this.getAds().insert(name, new TestEntity(), new InsertOptions().writeConcern(ACKNOWLEDGED));
        Assert.assertEquals(2, collection.count());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testBulkInsertOld() {
        this.getAds().insert(Arrays.asList(new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity()), new InsertOptions().writeConcern(ACKNOWLEDGED));
        Assert.assertEquals(5, getDs().getCollection(TestEntity.class).count());
        String name = "some_collection";
        MongoCollection<Document> collection = getMongoClient().getDatabase(TestBase.TEST_DB_NAME).getCollection(name);
        this.getAds().insert(name, Arrays.asList(new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity()), new InsertOptions().writeConcern(ACKNOWLEDGED));
        Assert.assertEquals(5, collection.count());
        collection.drop();
        this.getAds().insert(name, Arrays.asList(new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity()), ACKNOWLEDGED);
        Assert.assertEquals(5, collection.count());
    }

    @Test
    public void testBulkInsert() {
        this.getAds().insert(Arrays.asList(new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity()), new InsertOptions().writeConcern(ACKNOWLEDGED));
        Assert.assertEquals(5, getDs().getCollection(TestEntity.class).count());
        String name = "some_collection";
        MongoCollection<Document> collection = getMongoClient().getDatabase(TestBase.TEST_DB_NAME).getCollection(name);
        this.getAds().insert(name, Arrays.asList(new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity()), new InsertOptions().writeConcern(ACKNOWLEDGED));
        Assert.assertEquals(5, collection.count());
        collection.drop();
        this.getAds().insert(name, Arrays.asList(new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity()), new InsertOptions().writeConcern(ACKNOWLEDGED));
        Assert.assertEquals(5, collection.count());
    }

    @Test
    public void testBulkInsertWithNullWC() {
        this.getAds().insert(Arrays.asList(new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity()), new InsertOptions());
        Assert.assertEquals(5, getDs().getCollection(TestEntity.class).count());
        String name = "some_collection";
        this.getAds().insert(name, Arrays.asList(new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity(), new TestEntity()), new InsertOptions());
        Assert.assertEquals(5, getMongoClient().getDatabase(TestBase.TEST_DB_NAME).getCollection(name).count());
    }

    @Test
    public void testInsertEmpty() {
        this.getAds().insert(Collections.emptyList());
        this.getAds().insert(Collections.emptyList(), new InsertOptions().writeConcern(ACKNOWLEDGED));
        this.getAds().insert("some_collection", Collections.emptyList(), new InsertOptions().writeConcern(ACKNOWLEDGED));
    }
}

