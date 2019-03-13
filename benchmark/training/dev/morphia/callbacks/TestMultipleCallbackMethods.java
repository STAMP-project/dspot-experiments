package dev.morphia.callbacks;


import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import dev.morphia.annotations.PostPersist;
import dev.morphia.annotations.PreLoad;
import dev.morphia.annotations.PrePersist;
import dev.morphia.query.FindOptions;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestMultipleCallbackMethods extends TestBase {
    private static int loading;

    @Test
    public void testMultipleCallbackAnnotation() {
        final TestMultipleCallbackMethods.SomeEntity entity = new TestMultipleCallbackMethods.SomeEntity();
        getDs().save(entity);
        Assert.assertEquals(4, entity.getFoo());
        Assert.assertEquals(0, TestMultipleCallbackMethods.loading);
        final TestMultipleCallbackMethods.SomeEntity someEntity = getDs().find(TestMultipleCallbackMethods.SomeEntity.class).filter("_id", entity.getId()).find(new FindOptions().limit(1)).next();
        Assert.assertEquals(4, entity.getFoo());
        Assert.assertEquals((-1), someEntity.getFoo());
        Assert.assertEquals(2, TestMultipleCallbackMethods.loading);
    }

    abstract static class CallbackAbstractEntity {
        @Id
        private final ObjectId id = new ObjectId();

        private int foo;

        public ObjectId getId() {
            return id;
        }

        int getFoo() {
            return foo;
        }

        void setFoo(final int foo) {
            this.foo = foo;
        }

        @PrePersist
        void prePersist1() {
            (foo)++;
        }

        @PrePersist
        void prePersist2() {
            (foo)++;
        }

        @PostPersist
        void postPersist1() {
            (foo)++;
        }

        @PostPersist
        void postPersist2() {
            (foo)++;
        }

        @PreLoad
        void preLoad1() {
            (TestMultipleCallbackMethods.loading)++;
        }

        @PreLoad
        void preLoad2() {
            (TestMultipleCallbackMethods.loading)++;
        }

        @PostLoad
        void postLoad1() {
            (foo)--;
        }

        @PostLoad
        void postLoad2() {
            (foo)--;
        }

        @PostLoad
        void postLoad3() {
            (foo)--;
        }
    }

    static class SomeEntity extends TestMultipleCallbackMethods.CallbackAbstractEntity {}
}

