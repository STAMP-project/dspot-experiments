package dev.morphia.indexes;


import com.mongodb.DBObject;
import com.mongodb.MongoCommandException;
import dev.morphia.TestBase;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Text;
import dev.morphia.utils.IndexType;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestTextIndexing extends TestBase {
    @Test(expected = MongoCommandException.class)
    public void shouldNotAllowMultipleTextIndexes() {
        Class<TestTextIndexing.MultipleTextIndexes> clazz = TestTextIndexing.MultipleTextIndexes.class;
        getMorphia().map(clazz);
        getDs().getCollection(clazz).drop();
        getDs().ensureIndexes();
    }

    @Test
    public void testIndexAll() {
        getMorphia().map(TestTextIndexing.TextIndexAll.class);
        getDs().ensureIndexes();
        List<DBObject> indexInfo = getDs().getCollection(TestTextIndexing.TextIndexAll.class).getIndexInfo();
        Assert.assertEquals(2, indexInfo.size());
        for (DBObject dbObject : indexInfo) {
            if (!(dbObject.get("name").equals("_id_"))) {
                Assert.assertEquals(1, get("$**"));
                Assert.assertEquals("english", dbObject.get("default_language"));
                Assert.assertEquals("language", dbObject.get("language_override"));
            }
        }
    }

    @Test
    public void testSingleAnnotation() {
        getMorphia().map(TestTextIndexing.CompoundTextIndex.class);
        getDs().getCollection(TestTextIndexing.CompoundTextIndex.class).drop();
        getDs().ensureIndexes();
        List<DBObject> indexInfo = getDs().getCollection(TestTextIndexing.CompoundTextIndex.class).getIndexInfo();
        Assert.assertEquals(2, indexInfo.size());
        boolean found = false;
        for (DBObject dbObject : indexInfo) {
            if (dbObject.get("name").equals("indexing_test")) {
                found = true;
                Assert.assertEquals(dbObject.toString(), "russian", dbObject.get("default_language"));
                Assert.assertEquals(dbObject.toString(), "nativeTongue", dbObject.get("language_override"));
                Assert.assertEquals(dbObject.toString(), 1, get("name"));
                Assert.assertEquals(dbObject.toString(), 10, get("nick"));
                Assert.assertEquals(dbObject.toString(), 1, get("age"));
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void testTextAnnotation() {
        Class<TestTextIndexing.SingleFieldTextIndex> clazz = TestTextIndexing.SingleFieldTextIndex.class;
        getMorphia().map(clazz);
        getDs().getCollection(clazz).drop();
        getDs().ensureIndexes();
        List<DBObject> indexInfo = getDs().getCollection(clazz).getIndexInfo();
        Assert.assertEquals(indexInfo.toString(), 2, indexInfo.size());
        boolean found = false;
        for (DBObject dbObject : indexInfo) {
            if (dbObject.get("name").equals("single_annotation")) {
                found = true;
                Assert.assertEquals(dbObject.toString(), "english", dbObject.get("default_language"));
                Assert.assertEquals(dbObject.toString(), "nativeTongue", dbObject.get("language_override"));
                Assert.assertEquals(dbObject.toString(), 10, get("nickName"));
            }
        }
        Assert.assertTrue(indexInfo.toString(), found);
    }

    @Test
    public void testTextIndexOnNamedCollection() {
        getMorphia().map(TestTextIndexing.TextIndexAll.class);
        getAds().ensureIndexes("randomCollection", TestTextIndexing.TextIndexAll.class);
        List<DBObject> indexInfo = getDb().getCollection("randomCollection").getIndexInfo();
        Assert.assertEquals(2, indexInfo.size());
        for (DBObject dbObject : indexInfo) {
            if (!(dbObject.get("name").equals("_id_"))) {
                Assert.assertEquals(1, get("$**"));
                Assert.assertEquals("english", dbObject.get("default_language"));
                Assert.assertEquals("language", dbObject.get("language_override"));
            }
        }
    }

    @Entity
    @Indexes(@Index(fields = @Field(value = "$**", type = IndexType.TEXT)))
    private static class TextIndexAll {
        @Id
        private ObjectId id;

        private String name;

        private String nickName;
    }

    @Entity
    @Indexes(@Index(fields = { @Field(value = "name", type = IndexType.TEXT), @Field(value = "nick", type = IndexType.TEXT, weight = 10), @Field("age") }, options = @IndexOptions(name = "indexing_test", language = "russian", languageOverride = "nativeTongue")))
    private static class CompoundTextIndex {
        @Id
        private ObjectId id;

        private String name;

        private Integer age;

        @Property("nick")
        private String nickName;

        private String nativeTongue;
    }

    @Entity
    private static class SingleFieldTextIndex {
        @Id
        private ObjectId id;

        private String name;

        @Text(value = 10, options = @IndexOptions(name = "single_annotation", languageOverride = "nativeTongue"))
        private String nickName;
    }

    @Entity
    @Indexes({ @Index(fields = @Field(value = "name", type = IndexType.TEXT)), @Index(fields = @Field(value = "nickName", type = IndexType.TEXT)) })
    private static class MultipleTextIndexes {
        @Id
        private ObjectId id;

        private String name;

        private String nickName;
    }
}

