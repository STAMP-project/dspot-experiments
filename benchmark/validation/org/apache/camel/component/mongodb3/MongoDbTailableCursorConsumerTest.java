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


import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CreateCollectionOptions;
import org.apache.camel.component.mock.MockEndpoint;
import org.bson.Document;
import org.junit.Test;


public class MongoDbTailableCursorConsumerTest extends AbstractMongoDbTest {
    private MongoCollection<Document> cappedTestCollection;

    private String cappedTestCollectionName;

    @Test
    public void testThousandRecordsWithoutReadPreference() throws Exception {
        testThousandRecordsWithRouteId("tailableCursorConsumer1");
    }

    @Test
    public void testThousandRecordsWithReadPreference() throws Exception {
        testThousandRecordsWithRouteId("tailableCursorConsumer1.readPreference");
    }

    @Test
    public void testNoRecords() throws Exception {
        assertEquals(0, cappedTestCollection.count());
        MockEndpoint mock = getMockEndpoint("mock:test");
        mock.expectedMessageCount(0);
        // DocumentBuilder.start().add("capped", true).add("size",
        // 1000000000).add("max", 1000).get()
        // create a capped collection with max = 1000
        CreateCollectionOptions collectionOptions = new CreateCollectionOptions().capped(true).sizeInBytes(1000000000).maxDocuments(1000);
        AbstractMongoDbTest.db.createCollection(cappedTestCollectionName, collectionOptions);
        cappedTestCollection = AbstractMongoDbTest.db.getCollection(cappedTestCollectionName, Document.class);
        assertEquals(0, cappedTestCollection.count());
        addTestRoutes();
        context.getRouteController().startRoute("tailableCursorConsumer1");
        Thread.sleep(1000);
        mock.assertIsSatisfied();
        context.getRouteController().stopRoute("tailableCursorConsumer1");
    }

    @Test
    public void testMultipleBursts() throws Exception {
        assertEquals(0, cappedTestCollection.count());
        MockEndpoint mock = getMockEndpoint("mock:test");
        mock.expectedMessageCount(5000);
        // DocumentBuilder.start().add("capped", true).add("size",
        // 1000000000).add("max", 1000).get()
        // create a capped collection with max = 1000
        CreateCollectionOptions createCollectionOptions = new CreateCollectionOptions().capped(true).sizeInBytes(1000000000).maxDocuments(1000);
        AbstractMongoDbTest.db.createCollection(cappedTestCollectionName, createCollectionOptions);
        cappedTestCollection = AbstractMongoDbTest.db.getCollection(cappedTestCollectionName, Document.class);
        addTestRoutes();
        context.getRouteController().startRoute("tailableCursorConsumer1");
        // pump 5 bursts of 1000 records each with 500ms pause between burst and
        // burst
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5000; i++) {
                    if ((i % 1000) == 0) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    cappedTestCollection.insertOne(new Document("increasing", i).append("string", ("value" + i)));
                }
            }
        });
        // start the data pumping
        t.start();
        // before we assert, wait for the data pumping to end
        t.join();
        mock.assertIsSatisfied();
        context.getRouteController().stopRoute("tailableCursorConsumer1");
    }

    @Test
    public void testHundredThousandRecords() throws Exception {
        assertEquals(0, cappedTestCollection.count());
        final MockEndpoint mock = getMockEndpoint("mock:test");
        mock.expectedMessageCount(1000);
        // create a capped collection with max = 1000
        // DocumentBuilder.start().add("capped", true).add("size",
        // 1000000000).add("max", 1000).get())
        AbstractMongoDbTest.db.createCollection(cappedTestCollectionName, new CreateCollectionOptions().capped(true).sizeInBytes(1000000000).maxDocuments(1000));
        cappedTestCollection = AbstractMongoDbTest.db.getCollection(cappedTestCollectionName, Document.class);
        addTestRoutes();
        context.getRouteController().startRoute("tailableCursorConsumer1");
        // continuous pump of 100000 records, asserting incrementally to reduce
        // overhead on the mock endpoint
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= 100000; i++) {
                    cappedTestCollection.insertOne(new Document("increasing", i).append("string", ("value" + i)));
                    // incrementally assert, as the mock endpoint stores all
                    // messages and otherwise the test would be sluggish
                    if ((i % 1000) == 0) {
                        try {
                            MongoDbTailableCursorConsumerTest.this.assertAndResetMockEndpoint(mock);
                        } catch (Exception e) {
                            return;
                        }
                    }
                }
            }
        });
        // start the data pumping
        t.start();
        // before we stop the route, wait for the data pumping to end
        t.join();
        context.getRouteController().stopRoute("tailableCursorConsumer1");
    }
}

