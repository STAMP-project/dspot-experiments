package dev.morphia;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Property;
import dev.morphia.utils.IndexType;
import org.bson.types.ObjectId;
import org.junit.Test;


public class TestIndexCollections extends TestBase {
    @Test
    public void testEmbedded() {
        AdvancedDatastore ads = getAds();
        DB db = getDb();
        getMorphia().map(TestIndexCollections.HasEmbeddedIndex.class);
        ads.ensureIndexes();
        ads.ensureIndexes("b_2", TestIndexCollections.HasEmbeddedIndex.class);
        BasicDBObject[] indexes = new BasicDBObject[]{ new BasicDBObject("name", 1), new BasicDBObject("embeddedIndex.color", (-1)), new BasicDBObject("embeddedIndex.name", 1) };
        testIndex(db.getCollection("b_2").getIndexInfo(), indexes);
        testIndex(ads.getCollection(TestIndexCollections.HasEmbeddedIndex.class).getIndexInfo(), indexes);
    }

    @Test
    public void testOldStyleIndexing() {
        getMorphia().map(TestIndexCollections.OldStyleIndexing.class);
        getDb().dropDatabase();
        getAds().ensureIndexes();
        testIndex(getAds().getCollection(TestIndexCollections.OldStyleIndexing.class).getIndexInfo(), new BasicDBObject("field", 1), new BasicDBObject("field2", (-1)), new BasicDBObject("f3", 1));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testSingleFieldIndex() {
        AdvancedDatastore ads = getAds();
        DB db = getDb();
        ads.ensureIndexes("a_1", TestIndexCollections.SingleFieldIndex.class);
        testIndex(db.getCollection("a_1").getIndexInfo(), new BasicDBObject("field", 1), new BasicDBObject("field2", (-1)), new BasicDBObject("f3", 1));
        ads.ensureIndex("a_2", TestIndexCollections.SingleFieldIndex.class, "-field2");
        ads.ensureIndexes("a_2", TestIndexCollections.SingleFieldIndex.class);
        testIndex(db.getCollection("a_2").getIndexInfo(), new BasicDBObject("field", 1), new BasicDBObject("field2", 1), new BasicDBObject("field2", (-1)), new BasicDBObject("f3", 1));
        ads.ensureIndex("a_3", TestIndexCollections.SingleFieldIndex.class, "field, field2");
        testIndex(db.getCollection("a_3").getIndexInfo(), new BasicDBObject("field", 1).append("field2", 1));
    }

    @Entity
    @Indexes({ @Index(fields = @Field(value = "field2", type = IndexType.DESC)), @Index(fields = @Field("field3")) })
    private static class SingleFieldIndex {
        @Id
        private ObjectId id;

        @Indexed
        private String field;

        @Property
        private String field2;

        @Property("f3")
        private String field3;
    }

    @Entity
    @Indexes({ @Index(fields = @Field(value = "field2", type = IndexType.DESC)), @Index(fields = @Field("field3")) })
    private static class OldStyleIndexing {
        @Id
        private ObjectId id;

        @Indexed
        private String field;

        @Property
        private String field2;

        @Property("f3")
        private String field3;
    }

    @Entity
    private static class HasEmbeddedIndex {
        @Id
        private ObjectId id;

        @Indexed
        private String name;

        @Embedded
        private TestIndexCollections.EmbeddedIndex embeddedIndex;
    }

    @Embedded
    @Indexes(@Index(fields = @Field(value = "color", type = IndexType.DESC)))
    private static class EmbeddedIndex {
        @Indexed
        private String name;

        private String color;
    }
}

