package dev.morphia.callbacks;


import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import dev.morphia.annotations.PostPersist;
import dev.morphia.annotations.Transient;
import dev.morphia.query.FindOptions;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestMultipleCallbacksPerMethod extends TestBase {
    @Test
    public void testMultipleCallbackAnnotation() {
        final TestMultipleCallbacksPerMethod.SomeEntity entity = new TestMultipleCallbacksPerMethod.SomeEntity();
        Assert.assertFalse(entity.isPersistent());
        getDs().save(entity);
        Assert.assertTrue(entity.isPersistent());
        final TestMultipleCallbacksPerMethod.SomeEntity reloaded = getDs().find(TestMultipleCallbacksPerMethod.SomeEntity.class).filter("id", entity.getId()).find(new FindOptions().limit(1)).tryNext();
        Assert.assertTrue(reloaded.isPersistent());
    }

    abstract static class CallbackAbstractEntity {
        @Id
        private final String id = new ObjectId().toHexString();

        @Transient
        private boolean persistentMarker;

        public String getId() {
            return id;
        }

        public boolean isPersistent() {
            return persistentMarker;
        }

        @PostPersist
        @PostLoad
        void markPersistent() {
            persistentMarker = true;
        }
    }

    static class SomeEntity extends TestMultipleCallbacksPerMethod.CallbackAbstractEntity {}
}

