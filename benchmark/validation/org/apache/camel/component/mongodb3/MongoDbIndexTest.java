/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.mongodb3;


import MongoDbConstants.COLLECTION;
import MongoDbConstants.COLLECTION_INDEX;
import MongoDbConstants.DATABASE;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;


public class MongoDbIndexTest extends AbstractMongoDbTest {
    @Test
    public void testInsertDynamicityEnabledDBAndCollectionAndIndex() {
        assertEquals(0, AbstractMongoDbTest.testCollection.count());
        AbstractMongoDbTest.mongo.getDatabase("otherDB").drop();
        AbstractMongoDbTest.db.getCollection("otherCollection").drop();
        assertFalse("The otherDB database should not exist", StreamSupport.stream(AbstractMongoDbTest.mongo.listDatabaseNames().spliterator(), false).anyMatch("otherDB"::equals));
        String body = "{\"_id\": \"testInsertDynamicityEnabledDBAndCollection\", \"a\" : 1, \"b\" : 2}";
        Map<String, Object> headers = new HashMap<>();
        headers.put(DATABASE, "otherDB");
        headers.put(COLLECTION, "otherCollection");
        List<Document> objIndex = new ArrayList<>();
        Document index1 = new Document();
        index1.put("a", 1);
        Document index2 = new Document();
        index2.put("b", (-1));
        objIndex.add(index1);
        objIndex.add(index2);
        headers.put(COLLECTION_INDEX, objIndex);
        Object result = template.requestBodyAndHeaders("direct:dynamicityEnabled", body, headers);
        assertEquals("Response isn't of type WriteResult", Document.class, result.getClass());
        MongoCollection<Document> localDynamicCollection = AbstractMongoDbTest.mongo.getDatabase("otherDB").getCollection("otherCollection", Document.class);
        ListIndexesIterable<Document> indexInfos = localDynamicCollection.listIndexes(Document.class);
        MongoCursor<Document> iterator = indexInfos.iterator();
        iterator.next();
        Document key1 = iterator.next().get("key", Document.class);
        Document key2 = iterator.next().get("key", Document.class);
        assertTrue("No index on the field a", ((key1.containsKey("a")) && (1 == (key1.getInteger("a")))));
        assertTrue("No index on the field b", ((key2.containsKey("b")) && ((-1) == (key2.getInteger("b")))));
        Document b = localDynamicCollection.find(new Document(MongoDbConstants.MONGO_ID, "testInsertDynamicityEnabledDBAndCollection")).first();
        assertNotNull("No record with 'testInsertDynamicityEnabledDBAndCollection' _id", b);
        b = AbstractMongoDbTest.testCollection.find(new Document(MongoDbConstants.MONGO_ID, "testInsertDynamicityEnabledDBOnly")).first();
        assertNull("There is a record with 'testInsertDynamicityEnabledDBAndCollection' _id in the test collection", b);
        assertTrue("The otherDB database should exist", StreamSupport.stream(AbstractMongoDbTest.mongo.listDatabaseNames().spliterator(), false).anyMatch("otherDB"::equals));
    }

    @Test
    public void testInsertDynamicityEnabledCollectionAndIndex() {
        assertEquals(0, AbstractMongoDbTest.testCollection.count());
        AbstractMongoDbTest.mongo.getDatabase("otherDB").drop();
        AbstractMongoDbTest.db.getCollection("otherCollection").drop();
        assertFalse("The otherDB database should not exist", StreamSupport.stream(AbstractMongoDbTest.mongo.listDatabaseNames().spliterator(), false).anyMatch("otherDB"::equals));
        String body = "{\"_id\": \"testInsertDynamicityEnabledCollectionAndIndex\", \"a\" : 1, \"b\" : 2}";
        Map<String, Object> headers = new HashMap<>();
        headers.put(COLLECTION, "otherCollection");
        List<Bson> objIndex = Arrays.asList(ascending("a"), descending("b"));
        headers.put(COLLECTION_INDEX, objIndex);
        Object result = template.requestBodyAndHeaders("direct:dynamicityEnabled", body, headers);
        assertEquals("Response isn't of type WriteResult", Document.class, result.getClass());
        MongoCollection<Document> localDynamicCollection = AbstractMongoDbTest.db.getCollection("otherCollection", Document.class);
        MongoCursor<Document> indexInfos = localDynamicCollection.listIndexes(Document.class).iterator();
        indexInfos.next();
        Document key1 = indexInfos.next().get("key", Document.class);
        Document key2 = indexInfos.next().get("key", Document.class);
        assertTrue("No index on the field a", ((key1.containsKey("a")) && (1 == (key1.getInteger("a")))));
        assertTrue("No index on the field b", ((key2.containsKey("b")) && ((-1) == (key2.getInteger("b")))));
        Document b = localDynamicCollection.find(eq(MongoDbConstants.MONGO_ID, "testInsertDynamicityEnabledCollectionAndIndex")).first();
        assertNotNull("No record with 'testInsertDynamicityEnabledCollectionAndIndex' _id", b);
        b = AbstractMongoDbTest.testCollection.find(eq(MongoDbConstants.MONGO_ID, "testInsertDynamicityEnabledDBOnly")).first();
        assertNull("There is a record with 'testInsertDynamicityEnabledDBAndCollection' _id in the test collection", b);
        assertFalse("The otherDB database should not exist", AbstractMongoDbTest.mongo.getUsedDatabases().contains("otherDB"));
    }

    @Test
    public void testInsertDynamicityEnabledCollectionOnlyAndURIIndex() {
        assertEquals(0, AbstractMongoDbTest.testCollection.count());
        AbstractMongoDbTest.mongo.getDatabase("otherDB").drop();
        AbstractMongoDbTest.db.getCollection("otherCollection").drop();
        assertFalse("The otherDB database should not exist", StreamSupport.stream(AbstractMongoDbTest.mongo.listDatabaseNames().spliterator(), false).anyMatch("otherDB"::equals));
        String body = "{\"_id\": \"testInsertDynamicityEnabledCollectionOnlyAndURIIndex\", \"a\" : 1, \"b\" : 2}";
        Map<String, Object> headers = new HashMap<>();
        headers.put(COLLECTION, "otherCollection");
        Object result = template.requestBodyAndHeaders("direct:dynamicityEnabledWithIndexUri", body, headers);
        assertEquals("Response isn't of type WriteResult", Document.class, result.getClass());
        MongoCollection<Document> localDynamicCollection = AbstractMongoDbTest.db.getCollection("otherCollection", Document.class);
        MongoCursor<Document> indexInfos = localDynamicCollection.listIndexes().iterator();
        Document key1 = indexInfos.next().get("key", Document.class);
        assertFalse("No index on the field a", ((key1.containsKey("a")) && ("-1".equals(key1.getString("a")))));
        Document b = localDynamicCollection.find(eq(MongoDbConstants.MONGO_ID, "testInsertDynamicityEnabledCollectionOnlyAndURIIndex")).first();
        assertNotNull("No record with 'testInsertDynamicityEnabledCollectionOnlyAndURIIndex' _id", b);
        b = AbstractMongoDbTest.testCollection.find(eq(MongoDbConstants.MONGO_ID, "testInsertDynamicityEnabledCollectionOnlyAndURIIndex")).first();
        assertNull("There is a record with 'testInsertDynamicityEnabledCollectionOnlyAndURIIndex' _id in the test collection", b);
        assertFalse("The otherDB database should not exist", StreamSupport.stream(AbstractMongoDbTest.mongo.listDatabaseNames().spliterator(), false).anyMatch("otherDB"::equals));
    }
}

