package dev.morphia.mapping.lazy;


import dev.morphia.TestBase;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import dev.morphia.mapping.MappingException;
import dev.morphia.mapping.lazy.proxy.LazyReferenceFetchingException;
import dev.morphia.query.FindOptions;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


public class TestLazyWithMissingReferences extends TestBase {
    @Test(expected = MappingException.class)
    public void testMissingRef() {
        final TestLazyWithMissingReferences.Source source = new TestLazyWithMissingReferences.Source();
        source.setTarget(new TestLazyWithMissingReferences.Target());
        getDs().save(source);// does not fail due to pre-initialized Ids

        TestBase.toList(getDs().find(TestLazyWithMissingReferences.Source.class).find());
    }

    @Test(expected = LazyReferenceFetchingException.class)
    public void testMissingRefLazy() {
        final TestLazyWithMissingReferences.Source e = new TestLazyWithMissingReferences.Source();
        e.setLazy(new TestLazyWithMissingReferences.Target());
        getDs().save(e);// does not fail due to pre-initialized Ids

        Assert.assertNull(getDs().find(TestLazyWithMissingReferences.Source.class).find(new FindOptions().limit(1)).tryNext().getLazy());
    }

    @Test
    public void testMissingRefLazyIgnoreMissing() {
        final TestLazyWithMissingReferences.Source e = new TestLazyWithMissingReferences.Source();
        e.setIgnoreMissing(new TestLazyWithMissingReferences.Target());
        getDs().save(e);// does not fail due to pre-initialized Ids

        try {
            getDs().find(TestLazyWithMissingReferences.Source.class).find(new FindOptions().limit(1)).tryNext().getIgnoreMissing().foo();
        } catch (RuntimeException re) {
            Assert.assertEquals("Cannot dispatch method foo", re.getMessage());
        }
    }

    static class Source {
        @Id
        private ObjectId id = new ObjectId();

        @Reference
        private TestLazyWithMissingReferences.Target target;

        @Reference(lazy = true)
        private TestLazyWithMissingReferences.Target lazy;

        @Reference(lazy = true, ignoreMissing = true)
        private TestLazyWithMissingReferences.Target ignoreMissing;

        public TestLazyWithMissingReferences.Target getTarget() {
            return target;
        }

        public void setTarget(final TestLazyWithMissingReferences.Target target) {
            this.target = target;
        }

        public TestLazyWithMissingReferences.Target getLazy() {
            return lazy;
        }

        public void setLazy(final TestLazyWithMissingReferences.Target lazy) {
            this.lazy = lazy;
        }

        public TestLazyWithMissingReferences.Target getIgnoreMissing() {
            return ignoreMissing;
        }

        public void setIgnoreMissing(final TestLazyWithMissingReferences.Target ignoreMissing) {
            this.ignoreMissing = ignoreMissing;
        }
    }

    static class Target {
        @Id
        private ObjectId id = new ObjectId();

        private String foo = "bar";

        void foo() {
        }
    }
}

