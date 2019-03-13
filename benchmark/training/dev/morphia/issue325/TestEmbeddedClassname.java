package dev.morphia.issue325;


import com.mongodb.DBObject;
import dev.morphia.Datastore;
import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PreLoad;
import dev.morphia.annotations.Transient;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TestEmbeddedClassname extends TestBase {
    @Test
    public final void testEmbeddedClassname() {
        Datastore ds = getDs();
        TestEmbeddedClassname.Root r = new TestEmbeddedClassname.Root();
        r.singleA = new TestEmbeddedClassname.A();
        ds.save(r);
        ds.update(ds.find(TestEmbeddedClassname.Root.class), ds.createUpdateOperations(TestEmbeddedClassname.Root.class).addToSet("aList", new TestEmbeddedClassname.A()));
        r = ds.get(TestEmbeddedClassname.Root.class, "id");
        DBObject aRaw = r.singleA.raw;
        // Test that singleA does not contain the class name
        final String discriminatorField = getMorphia().getMapper().getOptions().getDiscriminatorField();
        Assert.assertFalse(aRaw.containsField(discriminatorField));
        // Test that aList does not contain the class name
        aRaw = r.aList.get(0).raw;
        Assert.assertFalse(aRaw.containsField(discriminatorField));
        // Test that bList does not contain the class name of the subclass
        ds.update(ds.find(TestEmbeddedClassname.Root.class), ds.createUpdateOperations(TestEmbeddedClassname.Root.class).addToSet("bList", new TestEmbeddedClassname.B()));
        r = ds.get(TestEmbeddedClassname.Root.class, "id");
        aRaw = r.aList.get(0).raw;
        Assert.assertFalse(aRaw.containsField(discriminatorField));
        DBObject bRaw = r.bList.get(0).getRaw();
        Assert.assertFalse(bRaw.containsField(discriminatorField));
        ds.delete(ds.find(TestEmbeddedClassname.Root.class));
        // test saving an B in aList, and it should have the classname.
        TestEmbeddedClassname.Root entity = new TestEmbeddedClassname.Root();
        entity.singleA = new TestEmbeddedClassname.B();
        ds.save(entity);
        ds.update(ds.find(TestEmbeddedClassname.Root.class), ds.createUpdateOperations(TestEmbeddedClassname.Root.class).addToSet("aList", new TestEmbeddedClassname.B()));
        r = ds.get(TestEmbeddedClassname.Root.class, "id");
        // test that singleA.raw *does* contain the classname because we stored a subclass there
        aRaw = r.singleA.raw;
        Assert.assertTrue(aRaw.containsField(discriminatorField));
        DBObject bRaw2 = r.aList.get(0).raw;
        Assert.assertTrue(bRaw2.containsField(discriminatorField));
    }

    @Entity(noClassnameStored = true)
    private static class Root {
        @Embedded
        private final List<TestEmbeddedClassname.A> aList = new ArrayList<TestEmbeddedClassname.A>();

        @Embedded
        private final List<TestEmbeddedClassname.B> bList = new ArrayList<TestEmbeddedClassname.B>();

        @Id
        private String id = "id";

        @Embedded
        private TestEmbeddedClassname.A singleA;
    }

    @Embedded
    private static class A {
        private String name = "some name";

        @Transient
        private DBObject raw;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public DBObject getRaw() {
            return raw;
        }

        public void setRaw(final DBObject raw) {
            this.raw = raw;
        }

        @PreLoad
        void preLoad(final DBObject dbObj) {
            raw = dbObj;
        }
    }

    private static class B extends TestEmbeddedClassname.A {
        private String description = "<description here>";
    }
}

