package dev.morphia.mapping.lazy;


import dev.morphia.Key;
import dev.morphia.annotations.Reference;
import dev.morphia.query.FindOptions;
import dev.morphia.testutil.TestEntity;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class TestLazyCircularReference extends ProxyTestBase {
    @Test
    public void testCircularReferences() {
        TestLazyCircularReference.RootEntity root = new TestLazyCircularReference.RootEntity();
        TestLazyCircularReference.ReferencedEntity first = new TestLazyCircularReference.ReferencedEntity();
        TestLazyCircularReference.ReferencedEntity second = new TestLazyCircularReference.ReferencedEntity();
        getDs().save(Arrays.asList(root, first, second));
        root.r = first;
        root.secondReference = second;
        first.parent = root;
        second.parent = root;
        getDs().save(Arrays.asList(root, first, second));
        TestLazyCircularReference.RootEntity rootEntity = getDs().find(TestLazyCircularReference.RootEntity.class).find(new FindOptions().limit(1)).tryNext();
        Assert.assertEquals(first.getId(), rootEntity.getR().getId());
        Assert.assertEquals(second.getId(), rootEntity.getSecondReference().getId());
        Assert.assertEquals(root.getId(), rootEntity.getR().getParent().getId());
    }

    @Test
    public final void testGetKeyWithoutFetching() {
        // TODO us: exclusion does not work properly with maven + junit4
        if (!(LazyFeatureDependencies.testDependencyFullFilled())) {
            return;
        }
        TestLazyCircularReference.RootEntity root = new TestLazyCircularReference.RootEntity();
        final TestLazyCircularReference.ReferencedEntity reference = new TestLazyCircularReference.ReferencedEntity();
        reference.parent = root;
        root.r = reference;
        reference.setFoo("bar");
        final Key<TestLazyCircularReference.ReferencedEntity> k = getDs().save(reference);
        final String keyAsString = k.getId().toString();
        getDs().save(root);
        root = getDs().get(root);
        final TestLazyCircularReference.ReferencedEntity p = root.r;
        assertIsProxy(p);
        assertNotFetched(p);
        Assert.assertEquals(keyAsString, getDs().getKey(p).getId().toString());
        // still not fetched?
        assertNotFetched(p);
        p.getFoo();
        // should be fetched now.
        assertFetched(p);
    }

    public static class RootEntity extends TestEntity {
        @Reference(lazy = true)
        private TestLazyCircularReference.ReferencedEntity r;

        @Reference(lazy = true)
        private TestLazyCircularReference.ReferencedEntity secondReference;

        public TestLazyCircularReference.ReferencedEntity getR() {
            return r;
        }

        public void setR(final TestLazyCircularReference.ReferencedEntity r) {
            this.r = r;
        }

        public TestLazyCircularReference.ReferencedEntity getSecondReference() {
            return secondReference;
        }

        public void setSecondReference(final TestLazyCircularReference.ReferencedEntity secondReference) {
            this.secondReference = secondReference;
        }
    }

    public static class ReferencedEntity extends TestEntity {
        private String foo;

        @Reference(lazy = true)
        private TestLazyCircularReference.RootEntity parent;

        public String getFoo() {
            return foo;
        }

        public void setFoo(final String string) {
            foo = string;
        }

        public TestLazyCircularReference.RootEntity getParent() {
            return parent;
        }

        public void setParent(final TestLazyCircularReference.RootEntity parent) {
            this.parent = parent;
        }
    }
}

