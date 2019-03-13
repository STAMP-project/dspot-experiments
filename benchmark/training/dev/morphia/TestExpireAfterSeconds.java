package dev.morphia;


import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Indexes;
import java.util.Date;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestExpireAfterSeconds extends TestBase {
    @Test
    public void testClassAnnotation() {
        getMorphia().map(TestExpireAfterSeconds.ClassAnnotation.class);
        getDs().ensureIndexes();
        getDs().save(new TestExpireAfterSeconds.ClassAnnotation());
        final DB db = getDs().getDB();
        final DBCollection dbCollection = db.getCollection("ClassAnnotation");
        final List<DBObject> indexes = dbCollection.getIndexInfo();
        Assert.assertNotNull(indexes);
        Assert.assertEquals(2, indexes.size());
        DBObject index = null;
        for (final DBObject candidateIndex : indexes) {
            if (candidateIndex.containsField("expireAfterSeconds")) {
                index = candidateIndex;
            }
        }
        Assert.assertNotNull(index);
        Assert.assertTrue(index.containsField("expireAfterSeconds"));
        Assert.assertEquals(5, ((Number) (index.get("expireAfterSeconds"))).intValue());
    }

    @Test
    public void testIndexedField() {
        getMorphia().map(TestExpireAfterSeconds.HasExpiryField.class);
        getDs().ensureIndexes();
        getDs().save(new TestExpireAfterSeconds.HasExpiryField());
        final DB db = getDs().getDB();
        final DBCollection dbCollection = db.getCollection("HasExpiryField");
        final List<DBObject> indexes = dbCollection.getIndexInfo();
        Assert.assertNotNull(indexes);
        Assert.assertEquals(2, indexes.size());
        DBObject index = null;
        for (final DBObject candidateIndex : indexes) {
            if (candidateIndex.containsField("expireAfterSeconds")) {
                index = candidateIndex;
            }
        }
        Assert.assertNotNull(index);
        Assert.assertEquals(5, ((Number) (index.get("expireAfterSeconds"))).intValue());
    }

    @Entity
    public static class HasExpiryField {
        @Indexed(options = @IndexOptions(expireAfterSeconds = 5))
        private final Date offerExpiresAt = new Date();

        @Id
        private ObjectId id;
    }

    @Entity
    @Indexes(@Index(fields = @Field("offerExpiresAt"), options = @IndexOptions(expireAfterSeconds = 5)))
    public static class ClassAnnotation {
        private final Date offerExpiresAt = new Date();

        @Id
        private ObjectId id;
    }
}

