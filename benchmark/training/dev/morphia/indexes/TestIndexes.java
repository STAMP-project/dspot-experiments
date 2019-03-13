/**
 * Copyright (c) 2008-2015 MongoDB, Inc.
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
package dev.morphia.indexes;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CollationAlternate;
import com.mongodb.client.model.CollationCaseFirst;
import com.mongodb.client.model.CollationMaxVariable;
import com.mongodb.client.model.CollationStrength;
import dev.morphia.Datastore;
import dev.morphia.TestBase;
import dev.morphia.annotations.Collation;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.mapping.MapperOptions.Builder;
import dev.morphia.utils.IndexType;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestIndexes extends TestBase {
    @Test
    public void testIndexes() {
        final Datastore datastore = getDs();
        datastore.delete(datastore.find(TestIndexes.TestWithIndexOption.class));
        final DBCollection indexOptionColl = getDs().getCollection(TestIndexes.TestWithIndexOption.class);
        indexOptionColl.drop();
        Assert.assertEquals(0, indexOptionColl.getIndexInfo().size());
        final DBCollection depIndexColl = getDs().getCollection(TestIndexes.TestWithDeprecatedIndex.class);
        depIndexColl.drop();
        Assert.assertEquals(0, depIndexColl.getIndexInfo().size());
        final DBCollection hashIndexColl = getDs().getCollection(TestIndexes.TestWithHashedIndex.class);
        hashIndexColl.drop();
        Assert.assertEquals(0, hashIndexColl.getIndexInfo().size());
        if (serverIsAtLeastVersion(3.4)) {
            datastore.ensureIndexes(TestIndexes.TestWithIndexOption.class, true);
            Assert.assertEquals(2, indexOptionColl.getIndexInfo().size());
            List<DBObject> indexInfo = indexOptionColl.getIndexInfo();
            assertBackground(indexInfo);
            for (DBObject dbObject : indexInfo) {
                if (dbObject.get("name").equals("collated")) {
                    Assert.assertEquals(BasicDBObject.parse("{ name : { $exists : true } }"), dbObject.get("partialFilterExpression"));
                    BasicDBObject collation = ((BasicDBObject) (dbObject.get("collation")));
                    Assert.assertEquals("en_US", collation.get("locale"));
                    Assert.assertEquals("upper", collation.get("caseFirst"));
                    Assert.assertEquals("shifted", collation.get("alternate"));
                    Assert.assertTrue(collation.getBoolean("backwards"));
                    Assert.assertEquals("upper", collation.get("caseFirst"));
                    Assert.assertTrue(collation.getBoolean("caseLevel"));
                    Assert.assertEquals("space", collation.get("maxVariable"));
                    Assert.assertTrue(collation.getBoolean("normalization"));
                    Assert.assertTrue(collation.getBoolean("numericOrdering"));
                    Assert.assertEquals(5, collation.get("strength"));
                }
            }
        }
        datastore.ensureIndexes(TestIndexes.TestWithDeprecatedIndex.class, true);
        Assert.assertEquals(2, depIndexColl.getIndexInfo().size());
        assertBackground(depIndexColl.getIndexInfo());
        datastore.ensureIndexes(TestIndexes.TestWithHashedIndex.class);
        Assert.assertEquals(2, hashIndexColl.getIndexInfo().size());
        assertHashed(hashIndexColl.getIndexInfo());
    }

    @Test
    public void embeddedIndexPartialFilters() {
        getMorphia().map(TestIndexes.FeedEvent.class, TestIndexes.InboxEvent.class);
        getDs().ensureIndexes();
        final MongoCollection<Document> inboxEvent = getDatabase().getCollection("InboxEvent");
        for (final Document index : inboxEvent.listIndexes()) {
            if (!("_id_".equals(index.get("name")))) {
                for (String name : index.get("key", Document.class).keySet()) {
                    Assert.assertTrue(("Key names should start with the field name: " + name), name.startsWith("feedEvent."));
                }
            }
        }
        // check the logging is disabled
        inboxEvent.drop();
        final Builder builder = MapperOptions.builder(getMorphia().getMapper().getOptions()).disableEmbeddedIndexes(true);
        getMorphia().getMapper().setOptions(builder.build());
        getDs().ensureIndexes();
        Assert.assertNull("No indexes should be generated for InboxEvent", inboxEvent.listIndexes().iterator().tryNext());
    }

    @Entity(noClassnameStored = true)
    @Indexes({ @Index(options = @IndexOptions(name = "collated", partialFilter = "{ name : { $exists : true } }", collation = @Collation(locale = "en_US", alternate = CollationAlternate.SHIFTED, backwards = true, caseFirst = CollationCaseFirst.UPPER, caseLevel = true, maxVariable = CollationMaxVariable.SPACE, normalization = true, numericOrdering = true, strength = CollationStrength.IDENTICAL)), fields = { @Field("name") }) })
    public static class TestWithIndexOption {
        private String name;
    }

    @Entity(noClassnameStored = true)
    @Indexes({ @Index("name") })
    public static class TestWithDeprecatedIndex {
        private String name;
    }

    @Entity(noClassnameStored = true)
    @Indexes({ @Index(options = @IndexOptions, fields = { @Field(value = "hashedValue", type = IndexType.HASHED) }) })
    public static class TestWithHashedIndex {
        private String hashedValue;
    }

    @Entity
    @Indexes(@Index(fields = { @Field("actor.actorObject.userId"), @Field(value = "actor.actorType", type = IndexType.DESC) }, options = @IndexOptions(disableValidation = true, partialFilter = "{ 'actor.actorObject.userId': { $exists: true }, 'actor.actorType': { $exists: true } }")))
    public static class FeedEvent {
        @Id
        private ObjectId id;
    }

    @Entity
    public static class InboxEvent {
        @Id
        private ObjectId id;

        @Embedded
        private TestIndexes.FeedEvent feedEvent;
    }
}

