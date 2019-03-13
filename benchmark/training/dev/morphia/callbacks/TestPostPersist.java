package dev.morphia.callbacks;


import dev.morphia.TestBase;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostPersist;
import java.util.Arrays;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestPostPersist extends TestBase {
    @Test
    public void testBulkLifecycleEvents() {
        TestPostPersist.TestObject to1 = new TestPostPersist.TestObject("post value 1");
        TestPostPersist.TestObject to2 = new TestPostPersist.TestObject("post value 2");
        getAds().insert(Arrays.asList(to1, to2));
        Assert.assertNotNull(to1.id);
        Assert.assertNotNull(to1.one);
        Assert.assertNotNull(to2.id);
        Assert.assertNotNull(to2.one);
    }

    @Test
    public void testCallback() throws Exception {
        final TestPostPersist.ProblematicPostPersistEntity p = new TestPostPersist.ProblematicPostPersistEntity();
        getDs().save(p);
        Assert.assertTrue(p.called);
        Assert.assertTrue(p.i.innerCalled);
    }

    public static class ProblematicPostPersistEntity {
        private final TestPostPersist.ProblematicPostPersistEntity.Inner i = new TestPostPersist.ProblematicPostPersistEntity.Inner();

        @Id
        private ObjectId id;

        private boolean called;

        static class Inner {
            private boolean innerCalled;

            @PostPersist
            void m2() {
                innerCalled = true;
            }
        }

        @PostPersist
        void m1() {
            called = true;
        }
    }

    @Entity
    public static class TestObject {
        private final String value;

        @Id
        private ObjectId id;

        private String one;

        public TestObject(final String value) {
            this.value = value;
        }

        @PostPersist
        public void doIt() {
            if ((one) != null) {
                throw new RuntimeException("@PostPersist methods should only be called once");
            }
            one = value;
        }
    }
}

