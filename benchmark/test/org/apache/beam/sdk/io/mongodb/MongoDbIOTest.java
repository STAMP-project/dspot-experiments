/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.mongodb;


import MongoDbIO.BoundedMongoDbSource;
import MongoDbIO.Read;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import java.util.ArrayList;
import java.util.List;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.Filter;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.PCollection;
import org.bson.Document;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test on the MongoDbIO.
 */
public class MongoDbIOTest {
    private static final Logger LOG = LoggerFactory.getLogger(MongoDbIOTest.class);

    @ClassRule
    public static final TemporaryFolder MONGODB_LOCATION = new TemporaryFolder();

    private static final String DATABASE = "beam";

    private static final String COLLECTION = "test";

    private static final MongodStarter mongodStarter = MongodStarter.getDefaultInstance();

    private static MongodExecutable mongodExecutable;

    private static MongodProcess mongodProcess;

    private static MongoClient client;

    private static int port;

    @Rule
    public final TestPipeline pipeline = TestPipeline.create();

    @Test
    public void testSplitIntoFilters() {
        ArrayList<Document> documents = new ArrayList<>();
        documents.add(new Document("_id", 56));
        documents.add(new Document("_id", 109));
        documents.add(new Document("_id", 256));
        List<String> filters = BoundedMongoDbSource.splitKeysToFilters(documents, null);
        Assert.assertEquals(4, filters.size());
        Assert.assertEquals("{ $and: [ {\"_id\":{$lte:ObjectId(\"56\")}} ]}", filters.get(0));
        Assert.assertEquals("{ $and: [ {\"_id\":{$gt:ObjectId(\"56\"),$lte:ObjectId(\"109\")}} ]}", filters.get(1));
        Assert.assertEquals("{ $and: [ {\"_id\":{$gt:ObjectId(\"109\"),$lte:ObjectId(\"256\")}} ]}", filters.get(2));
        Assert.assertEquals("{ $and: [ {\"_id\":{$gt:ObjectId(\"256\")}} ]}", filters.get(3));
    }

    @Test
    public void testFullRead() {
        PCollection<Document> output = pipeline.apply(MongoDbIO.read().withUri(("mongodb://localhost:" + (MongoDbIOTest.port))).withDatabase(MongoDbIOTest.DATABASE).withCollection(MongoDbIOTest.COLLECTION));
        PAssert.thatSingleton(output.apply("Count All", Count.globally())).isEqualTo(1000L);
        PAssert.that(output.apply("Map Scientist", MapElements.via(new MongoDbIOTest.DocumentToKVFn())).apply("Count Scientist", Count.perKey())).satisfies(( input) -> {
            for (KV<String, Long> element : input) {
                assertEquals(100L, element.getValue().longValue());
            }
            return null;
        });
        pipeline.run();
    }

    @Test
    public void testReadWithCustomConnectionOptions() {
        MongoDbIO.Read read = MongoDbIO.read().withUri(("mongodb://localhost:" + (MongoDbIOTest.port))).withKeepAlive(false).withMaxConnectionIdleTime(10).withDatabase(MongoDbIOTest.DATABASE).withCollection(MongoDbIOTest.COLLECTION);
        Assert.assertFalse(read.keepAlive());
        Assert.assertEquals(10, read.maxConnectionIdleTime());
        PCollection<Document> documents = pipeline.apply(read);
        PAssert.thatSingleton(documents.apply("Count All", Count.globally())).isEqualTo(1000L);
        PAssert.that(documents.apply("Map Scientist", MapElements.via(new MongoDbIOTest.DocumentToKVFn())).apply("Count Scientist", Count.perKey())).satisfies(( input) -> {
            for (KV<String, Long> element : input) {
                assertEquals(100L, element.getValue().longValue());
            }
            return null;
        });
        pipeline.run();
    }

    @Test
    public void testReadWithFilter() {
        PCollection<Document> output = pipeline.apply(MongoDbIO.read().withUri(("mongodb://localhost:" + (MongoDbIOTest.port))).withDatabase(MongoDbIOTest.DATABASE).withCollection(MongoDbIOTest.COLLECTION).withFilter("{\"scientist\":\"Einstein\"}"));
        PAssert.thatSingleton(output.apply("Count", Count.globally())).isEqualTo(100L);
        pipeline.run();
    }

    @Test
    public void testReadWithFilterAndProjection() {
        PCollection<Document> output = pipeline.apply(MongoDbIO.read().withUri(("mongodb://localhost:" + (MongoDbIOTest.port))).withDatabase(MongoDbIOTest.DATABASE).withCollection(MongoDbIOTest.COLLECTION).withFilter("{\"scientist\":\"Einstein\"}").withProjection("country", "scientist"));
        PAssert.thatSingleton(output.apply("Map Scientist", Filter.by((Document doc) -> ((doc.get("country")) != null) && ((doc.get("scientist")) != null))).apply("Count", Count.globally())).isEqualTo(100L);
        pipeline.run();
    }

    @Test
    public void testReadWithProjection() {
        PCollection<Document> output = pipeline.apply(MongoDbIO.read().withUri(("mongodb://localhost:" + (MongoDbIOTest.port))).withDatabase(MongoDbIOTest.DATABASE).withCollection(MongoDbIOTest.COLLECTION).withProjection("country"));
        PAssert.thatSingleton(output.apply("Map scientist", Filter.by((Document doc) -> ((doc.get("country")) != null) && ((doc.get("scientist")) == null))).apply("Count", Count.globally())).isEqualTo(1000L);
        pipeline.run();
    }

    @Test
    public void testWrite() {
        final String collectionName = "testWrite";
        final int numElements = 1000;
        pipeline.apply(Create.of(MongoDbIOTest.createDocuments(numElements))).apply(MongoDbIO.write().withUri(("mongodb://localhost:" + (MongoDbIOTest.port))).withDatabase(MongoDbIOTest.DATABASE).withCollection(collectionName));
        pipeline.run();
        Assert.assertEquals(numElements, MongoDbIOTest.countElements(collectionName));
    }

    @Test
    public void testWriteUnordered() {
        final String collectionName = "testWriteUnordered";
        Document doc = Document.parse("{\"_id\":\"521df3a4300466f1f2b5ae82\",\"scientist\":\"Test %s\"}");
        pipeline.apply(Create.of(doc, doc)).apply(MongoDbIO.write().withUri(("mongodb://localhost:" + (MongoDbIOTest.port))).withDatabase(MongoDbIOTest.DATABASE).withOrdered(false).withCollection(collectionName));
        pipeline.run();
        Assert.assertEquals(1, MongoDbIOTest.countElements(collectionName));
    }

    static class DocumentToKVFn extends SimpleFunction<Document, org.apache.beam.sdk.values.KV<String, Void>> {
        @Override
        public org.apache.beam.sdk.values.KV<String, Void> apply(Document input) {
            return org.apache.beam.sdk.values.KV.of(input.getString("scientist"), null);
        }
    }
}

