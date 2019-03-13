package dev.morphia.query;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import dev.morphia.Datastore;
import dev.morphia.TestBase;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexes;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestMaxMin extends TestBase {
    @SuppressWarnings("deprecation")
    @Test(expected = MongoException.class)
    public void testExceptionForIndexMismatchOld() {
        getDs().find(TestMaxMin.IndexedEntity.class).lowerIndexBound(new BasicDBObject("doesNotExist", 1)).get();
    }

    @Test(expected = MongoException.class)
    public void testExceptionForIndexMismatch() {
        getDs().find(TestMaxMin.IndexedEntity.class).find(new FindOptions().limit(1).modifier("$min", new BasicDBObject("doesNotExist", 1))).next();
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testMax() {
        final TestMaxMin.IndexedEntity a = new TestMaxMin.IndexedEntity("a");
        final TestMaxMin.IndexedEntity b = new TestMaxMin.IndexedEntity("b");
        final TestMaxMin.IndexedEntity c = new TestMaxMin.IndexedEntity("c");
        Datastore ds = getDs();
        ds.save(a);
        ds.save(b);
        ds.save(c);
        Assert.assertEquals("last", b.id, ds.find(TestMaxMin.IndexedEntity.class).order("-id").upperIndexBound(new BasicDBObject("testField", "c")).get().id);
        Assert.assertEquals("last", b.id, ds.find(TestMaxMin.IndexedEntity.class).order("-id").get(new FindOptions().modifier("$max", new BasicDBObject("testField", "c"))).id);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testMaxCompoundIndex() {
        final TestMaxMin.IndexedEntity a1 = new TestMaxMin.IndexedEntity("a");
        final TestMaxMin.IndexedEntity a2 = new TestMaxMin.IndexedEntity("a");
        final TestMaxMin.IndexedEntity b1 = new TestMaxMin.IndexedEntity("b");
        final TestMaxMin.IndexedEntity b2 = new TestMaxMin.IndexedEntity("b");
        final TestMaxMin.IndexedEntity c1 = new TestMaxMin.IndexedEntity("c");
        final TestMaxMin.IndexedEntity c2 = new TestMaxMin.IndexedEntity("c");
        Datastore ds = getDs();
        ds.save(a1);
        ds.save(a2);
        ds.save(b1);
        ds.save(b2);
        ds.save(c1);
        ds.save(c2);
        List<TestMaxMin.IndexedEntity> l = ds.find(TestMaxMin.IndexedEntity.class).order("testField, id").upperIndexBound(new BasicDBObject("testField", "b").append("_id", b2.id)).asList();
        Assert.assertEquals("size", 3, l.size());
        Assert.assertEquals("item", b1.id, l.get(2).id);
        l = ds.find(TestMaxMin.IndexedEntity.class).order("testField, id").asList(new FindOptions().modifier("$max", new BasicDBObject("testField", "b").append("_id", b2.id)));
        Assert.assertEquals("size", 3, l.size());
        Assert.assertEquals("item", b1.id, l.get(2).id);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testMin() {
        final TestMaxMin.IndexedEntity a = new TestMaxMin.IndexedEntity("a");
        final TestMaxMin.IndexedEntity b = new TestMaxMin.IndexedEntity("b");
        final TestMaxMin.IndexedEntity c = new TestMaxMin.IndexedEntity("c");
        Datastore ds = getDs();
        ds.save(a);
        ds.save(b);
        ds.save(c);
        Assert.assertEquals("last", b.id, ds.find(TestMaxMin.IndexedEntity.class).order("id").lowerIndexBound(new BasicDBObject("testField", "b")).get().id);
        Assert.assertEquals("last", b.id, ds.find(TestMaxMin.IndexedEntity.class).order("id").get(new FindOptions().modifier("$min", new BasicDBObject("testField", "b"))).id);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testMinCompoundIndex() {
        final TestMaxMin.IndexedEntity a1 = new TestMaxMin.IndexedEntity("a");
        final TestMaxMin.IndexedEntity a2 = new TestMaxMin.IndexedEntity("a");
        final TestMaxMin.IndexedEntity b1 = new TestMaxMin.IndexedEntity("b");
        final TestMaxMin.IndexedEntity b2 = new TestMaxMin.IndexedEntity("b");
        final TestMaxMin.IndexedEntity c1 = new TestMaxMin.IndexedEntity("c");
        final TestMaxMin.IndexedEntity c2 = new TestMaxMin.IndexedEntity("c");
        Datastore ds = getDs();
        ds.save(a1);
        ds.save(a2);
        ds.save(b1);
        ds.save(b2);
        ds.save(c1);
        ds.save(c2);
        List<TestMaxMin.IndexedEntity> l = ds.find(TestMaxMin.IndexedEntity.class).order("testField, id").lowerIndexBound(new BasicDBObject("testField", "b").append("_id", b1.id)).asList();
        Assert.assertEquals("size", 4, l.size());
        Assert.assertEquals("item", b1.id, l.get(0).id);
        l = ds.find(TestMaxMin.IndexedEntity.class).order("testField, id").asList(new FindOptions().modifier("$min", new BasicDBObject("testField", "b").append("_id", b1.id)));
        Assert.assertEquals("size", 4, l.size());
        Assert.assertEquals("item", b1.id, l.get(0).id);
    }

    @Entity("IndexedEntity")
    @Indexes({ @Index(fields = @Field("testField")), @Index(fields = { @Field("testField"), @Field("_id") }) })
    private static final class IndexedEntity {
        @Id
        private ObjectId id;

        private String testField;

        private IndexedEntity(final String testField) {
            this.testField = testField;
        }

        private IndexedEntity() {
        }
    }
}

