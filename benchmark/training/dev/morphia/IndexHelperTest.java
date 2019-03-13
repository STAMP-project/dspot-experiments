/**
 * Copyright 2016 MongoDB, Inc.
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


import IndexType.DESC;
import IndexType.TEXT;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CollationAlternate;
import com.mongodb.client.model.CollationCaseFirst;
import com.mongodb.client.model.CollationMaxVariable;
import com.mongodb.client.model.CollationStrength;
import com.mongodb.client.model.IndexOptions;
import dev.morphia.annotations.Collation;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Text;
import dev.morphia.mapping.MappedClass;
import dev.morphia.mapping.Mapper;
import dev.morphia.mapping.MappingException;
import dev.morphia.utils.IndexType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class IndexHelperTest extends TestBase {
    private final IndexHelper indexHelper = new IndexHelper(getMorphia().getMapper(), getDatabase());

    @Test
    public void calculateBadKeys() {
        MappedClass mappedClass = getMorphia().getMapper().getMappedClass(IndexHelperTest.IndexedClass.class);
        IndexBuilder index = new IndexBuilder().fields(new FieldBuilder().value("texting").type(TEXT).weight(1), new FieldBuilder().value("nest").type(DESC));
        try {
            indexHelper.calculateKeys(mappedClass, index);
            Assert.fail("Validation should have failed on the bad key");
        } catch (MappingException e) {
            // all good
        }
        index.options(new IndexOptionsBuilder().disableValidation(true));
        indexHelper.calculateKeys(mappedClass, index);
    }

    @Test
    public void calculateKeys() {
        MappedClass mappedClass = getMorphia().getMapper().getMappedClass(IndexHelperTest.IndexedClass.class);
        BsonDocument keys = indexHelper.calculateKeys(mappedClass, new IndexBuilder().fields(new FieldBuilder().value("text").type(TEXT).weight(1), new FieldBuilder().value("nest").type(DESC)));
        Assert.assertEquals(new BsonDocument().append("text", new BsonString("text")).append("nest", new BsonInt32((-1))), keys);
    }

    @Test
    public void createIndex() {
        checkMinServerVersion(3.4);
        String collectionName = getDs().getCollection(IndexHelperTest.IndexedClass.class).getName();
        MongoCollection<Document> collection = getDatabase().getCollection(collectionName);
        Mapper mapper = getMorphia().getMapper();
        indexHelper.createIndex(collection, mapper.getMappedClass(IndexHelperTest.IndexedClass.class), false);
        List<DBObject> indexInfo = getDs().getCollection(IndexHelperTest.IndexedClass.class).getIndexInfo();
        Assert.assertEquals("Should have 6 indexes", 6, indexInfo.size());
        for (DBObject dbObject : indexInfo) {
            String name = dbObject.get("name").toString();
            if (name.equals("latitude_1")) {
                Assert.assertEquals(BasicDBObject.parse("{ 'latitude' : 1 }"), dbObject.get("key"));
            } else
                if (name.equals("behind_interface")) {
                    Assert.assertEquals(BasicDBObject.parse("{ 'nest.name' : -1} "), dbObject.get("key"));
                    Assert.assertEquals(BasicDBObject.parse(("{ 'locale' : 'en' , 'caseLevel' : false , 'caseFirst' : 'off' , 'strength' : 2 , 'numericOrdering' :" + (" false , 'alternate' : 'non-ignorable' , 'maxVariable' : 'punct' , 'normalization' : false , " + "'backwards' : false , 'version' : '57.1'}"))), dbObject.get("collation"));
                } else
                    if (name.equals("nest.name_1")) {
                        Assert.assertEquals(BasicDBObject.parse("{ 'nest.name' : 1} "), dbObject.get("key"));
                    } else
                        if (name.equals("searchme")) {
                            Assert.assertEquals(BasicDBObject.parse("{ 'text' : 10 }"), dbObject.get("weights"));
                        } else
                            if (name.equals("indexName_1")) {
                                Assert.assertEquals(BasicDBObject.parse("{'indexName': 1 }"), dbObject.get("key"));
                            } else {
                                if (!("_id_".equals(dbObject.get("name")))) {
                                    throw new MappingException(("Found an index I wasn't expecting:  " + dbObject));
                                }
                            }




        }
        collection = getDatabase().getCollection(getDs().getCollection(IndexHelperTest.AbstractParent.class).getName());
        indexHelper.createIndex(collection, mapper.getMappedClass(IndexHelperTest.AbstractParent.class), false);
        indexInfo = getDs().getCollection(IndexHelperTest.AbstractParent.class).getIndexInfo();
        Assert.assertTrue(("Shouldn't find any indexes: " + indexInfo), indexInfo.isEmpty());
    }

    @Test
    public void findField() {
        MappedClass mappedClass = getMorphia().getMapper().getMappedClass(IndexHelperTest.IndexedClass.class);
        Assert.assertEquals("indexName", indexHelper.findField(mappedClass, new IndexOptionsBuilder(), Collections.singletonList("indexName")));
        Assert.assertEquals("nest.name", indexHelper.findField(mappedClass, new IndexOptionsBuilder(), Arrays.asList("nested", "name")));
        Assert.assertEquals("nest.name", indexHelper.findField(mappedClass, new IndexOptionsBuilder(), Arrays.asList("nest", "name")));
        try {
            Assert.assertEquals("nest.whatsit", indexHelper.findField(mappedClass, new IndexOptionsBuilder(), Arrays.asList("nest", "whatsit")));
            Assert.fail("Should have failed on the bad index path");
        } catch (MappingException e) {
            // alles ist gut
        }
        Assert.assertEquals("nest.whatsit.nested.more.deeply.than.the.object.model", indexHelper.findField(mappedClass, new IndexOptionsBuilder().disableValidation(true), Arrays.asList("nest", "whatsit", "nested", "more", "deeply", "than", "the", "object", "model")));
    }

    @Test
    public void index() {
        checkMinServerVersion(3.4);
        MongoCollection<Document> indexes = getDatabase().getCollection("indexes");
        MappedClass mappedClass = getMorphia().getMapper().getMappedClass(IndexHelperTest.IndexedClass.class);
        indexes.drop();
        Index index = new IndexBuilder().fields(new FieldBuilder().value("indexName"), new FieldBuilder().value("text").type(DESC)).options(indexOptions());
        indexHelper.createIndex(indexes, mappedClass, index, false);
        List<DBObject> indexInfo = getDs().getCollection(IndexHelperTest.IndexedClass.class).getIndexInfo();
        for (DBObject dbObject : indexInfo) {
            if (dbObject.get("name").equals("indexName")) {
                checkIndex(dbObject);
                Assert.assertEquals("en", dbObject.get("default_language"));
                Assert.assertEquals("de", dbObject.get("language_override"));
                Assert.assertEquals(new BasicDBObject().append("locale", "en").append("caseLevel", true).append("caseFirst", "upper").append("strength", 5).append("numericOrdering", true).append("alternate", "shifted").append("maxVariable", "space").append("backwards", true).append("normalization", true).append("version", "57.1"), dbObject.get("collation"));
            }
        }
    }

    @Test
    public void indexCollationConversion() {
        Collation collation = collation();
        com.mongodb.client.model.Collation driver = indexHelper.convert(collation);
        Assert.assertEquals("en", driver.getLocale());
        Assert.assertTrue(driver.getCaseLevel());
        Assert.assertEquals(CollationCaseFirst.UPPER, driver.getCaseFirst());
        Assert.assertEquals(CollationStrength.IDENTICAL, driver.getStrength());
        Assert.assertTrue(driver.getNumericOrdering());
        Assert.assertEquals(CollationAlternate.SHIFTED, driver.getAlternate());
        Assert.assertEquals(CollationMaxVariable.SPACE, driver.getMaxVariable());
        Assert.assertTrue(driver.getNormalization());
        Assert.assertTrue(driver.getBackwards());
    }

    @Test
    public void indexOptionsConversion() {
        IndexOptionsBuilder indexOptions = indexOptions();
        IndexOptions options = indexHelper.convert(indexOptions, false);
        Assert.assertEquals("index_name", options.getName());
        Assert.assertTrue(options.isBackground());
        Assert.assertTrue(options.isUnique());
        Assert.assertTrue(options.isSparse());
        Assert.assertEquals(Long.valueOf(42), options.getExpireAfter(TimeUnit.SECONDS));
        Assert.assertEquals("en", options.getDefaultLanguage());
        Assert.assertEquals("de", options.getLanguageOverride());
        Assert.assertEquals(indexHelper.convert(indexOptions.collation()), options.getCollation());
        Assert.assertTrue(indexHelper.convert(indexOptions, true).isBackground());
        Assert.assertTrue(indexHelper.convert(indexOptions.background(false), true).isBackground());
        Assert.assertTrue(indexHelper.convert(indexOptions.background(true), true).isBackground());
        Assert.assertTrue(indexHelper.convert(indexOptions.background(true), false).isBackground());
        Assert.assertFalse(indexHelper.convert(indexOptions.background(false), false).isBackground());
    }

    @Test
    public void oldIndexForm() {
        MongoCollection<Document> indexes = getDatabase().getCollection("indexes");
        MappedClass mappedClass = getMorphia().getMapper().getMappedClass(IndexHelperTest.IndexedClass.class);
        indexes.drop();
        Index index = new IndexBuilder().name("index_name").background(true).disableValidation(true).expireAfterSeconds(42).sparse(true).unique(true).value("indexName, -text");
        indexHelper.createIndex(indexes, mappedClass, index, false);
        List<DBObject> indexInfo = getDs().getCollection(IndexHelperTest.IndexedClass.class).getIndexInfo();
        for (DBObject dbObject : indexInfo) {
            if (dbObject.get("name").equals("index_indexName")) {
                checkIndex(dbObject);
            }
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void oldIndexedForm() {
        Indexed indexed = new IndexedBuilder().name("index_name").background(true).dropDups(true).expireAfterSeconds(42).sparse(true).unique(true).value(IndexDirection.DESC);
        Assert.assertEquals(indexed.options().name(), "");
        Index converted = indexHelper.convert(indexed, "oldstyle");
        Assert.assertEquals(converted.options().name(), "index_name");
        Assert.assertTrue(converted.options().background());
        Assert.assertTrue(converted.options().dropDups());
        Assert.assertTrue(converted.options().sparse());
        Assert.assertTrue(converted.options().unique());
        Assert.assertEquals(new FieldBuilder().value("oldstyle").type(DESC), converted.fields()[0]);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void convertTextIndex() {
        TextBuilder text = new TextBuilder().value(4).options(new IndexOptionsBuilder().name("index_name").background(true).dropDups(true).expireAfterSeconds(42).sparse(true).unique(true));
        Index index = indexHelper.convert(text, "search_field");
        Assert.assertEquals(index.options().name(), "index_name");
        Assert.assertTrue(index.options().background());
        Assert.assertTrue(index.options().dropDups());
        Assert.assertTrue(index.options().sparse());
        Assert.assertTrue(index.options().unique());
        Assert.assertEquals(new FieldBuilder().value("search_field").type(TEXT).weight(4), index.fields()[0]);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void normalizeIndexed() {
        Indexed indexed = new IndexedBuilder().value(IndexDirection.DESC).options(new IndexOptionsBuilder().name("index_name").background(true).dropDups(true).expireAfterSeconds(42).sparse(true).unique(true));
        Index converted = indexHelper.convert(indexed, "oldstyle");
        Assert.assertEquals(converted.options().name(), "index_name");
        Assert.assertTrue(converted.options().background());
        Assert.assertTrue(converted.options().dropDups());
        Assert.assertTrue(converted.options().sparse());
        Assert.assertTrue(converted.options().unique());
        Assert.assertEquals(new FieldBuilder().value("oldstyle").type(DESC), converted.fields()[0]);
    }

    @Test
    public void wildcardTextIndex() {
        MongoCollection<Document> indexes = getDatabase().getCollection("indexes");
        MappedClass mappedClass = getMorphia().getMapper().getMappedClass(IndexHelperTest.IndexedClass.class);
        IndexBuilder index = new IndexBuilder().fields(new FieldBuilder().value("$**").type(TEXT));
        indexHelper.createIndex(indexes, mappedClass, index, false);
        List<DBObject> wildcard = getDb().getCollection("indexes").getIndexInfo();
        boolean found = false;
        for (DBObject dbObject : wildcard) {
            found |= dbObject.get("name").equals("$**_text");
        }
        Assert.assertTrue("Should have found the wildcard index", found);
    }

    @Test(expected = MappingException.class)
    public void weightsOnNonTextIndex() {
        MongoCollection<Document> indexes = getDatabase().getCollection("indexes");
        MappedClass mappedClass = getMorphia().getMapper().getMappedClass(IndexHelperTest.IndexedClass.class);
        IndexBuilder index = new IndexBuilder().fields(new FieldBuilder().value("name").weight(10));
        indexHelper.createIndex(indexes, mappedClass, index, false);
    }

    @Test
    public void indexPartialFilters() {
        MongoCollection<Document> collection = getDatabase().getCollection("indexes");
        MappedClass mappedClass = getMorphia().getMapper().getMappedClass(IndexHelperTest.IndexedClass.class);
        Index index = new IndexBuilder().fields(new FieldBuilder().value("text")).options(new IndexOptionsBuilder().partialFilter("{ name : { $gt : 13 } }"));
        indexHelper.createIndex(collection, mappedClass, index, false);
        findPartialIndex(BasicDBObject.parse(index.options().partialFilter()));
    }

    @Test
    public void indexedPartialFilters() {
        MongoCollection<Document> collection = getDatabase().getCollection("indexes");
        MappedClass mappedClass = getMorphia().getMapper().getMappedClass(IndexHelperTest.IndexedClass.class);
        Indexed indexed = new IndexedBuilder().options(new IndexOptionsBuilder().partialFilter("{ name : { $gt : 13 } }"));
        indexHelper.createIndex(collection, mappedClass, indexHelper.convert(indexed, "text"), false);
        findPartialIndex(BasicDBObject.parse(indexed.options().partialFilter()));
    }

    @Test
    public void textPartialFilters() {
        MongoCollection<Document> collection = getDatabase().getCollection("indexes");
        MappedClass mappedClass = getMorphia().getMapper().getMappedClass(IndexHelperTest.IndexedClass.class);
        Text text = new TextBuilder().value(4).options(new IndexOptionsBuilder().partialFilter("{ name : { $gt : 13 } }"));
        indexHelper.createIndex(collection, mappedClass, indexHelper.convert(text, "text"), false);
        findPartialIndex(BasicDBObject.parse(text.options().partialFilter()));
    }

    @Embedded
    private interface NestedClass {}

    @Entity("indexes")
    @Indexes(@Index(fields = @Field("latitude")))
    private static class IndexedClass extends IndexHelperTest.AbstractParent {
        @Text(value = 10, options = @dev.morphia.annotations.IndexOptions(name = "searchme"))
        private String text;

        private double latitude;

        @Embedded("nest")
        private IndexHelperTest.NestedClass nested;
    }

    @Indexes(@Index(fields = @Field(value = "name", type = IndexType.DESC), options = @dev.morphia.annotations.IndexOptions(name = "behind_interface", collation = @Collation(locale = "en", strength = CollationStrength.SECONDARY))))
    private static class NestedClassImpl implements IndexHelperTest.NestedClass {
        @Indexed
        private String name;
    }

    @Indexes(@Index(fields = @Field("indexName")))
    private abstract static class AbstractParent {
        @Id
        private ObjectId id;

        private double indexName;
    }
}

