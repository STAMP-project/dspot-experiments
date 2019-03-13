package dev.morphia.mapping;


import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Reference;
import dev.morphia.testutil.TestEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author josephpachod
 */
public class ReferencesInEmbeddedTest extends TestBase {
    @Test
    public void testLazyReferencesInEmbedded() throws Exception {
        final ReferencesInEmbeddedTest.Container container = new ReferencesInEmbeddedTest.Container();
        container.name = "lazy";
        getDs().save(container);
        final ReferencesInEmbeddedTest.ReferencedEntity referencedEntity = new ReferencesInEmbeddedTest.ReferencedEntity();
        getDs().save(referencedEntity);
        container.embed = new ReferencesInEmbeddedTest.EmbedContainingReference();
        container.embed.lazyRef = referencedEntity;
        getDs().save(container);
        final ReferencesInEmbeddedTest.Container reloadedContainer = getDs().get(container);
        Assert.assertNotNull(reloadedContainer);
    }

    @Test
    public void testMapping() throws Exception {
        getMorphia().map(ReferencesInEmbeddedTest.Container.class);
        getMorphia().map(ReferencesInEmbeddedTest.ReferencedEntity.class);
    }

    @Test
    public void testNonLazyReferencesInEmbedded() throws Exception {
        final ReferencesInEmbeddedTest.Container container = new ReferencesInEmbeddedTest.Container();
        container.name = "nonLazy";
        getDs().save(container);
        final ReferencesInEmbeddedTest.ReferencedEntity referencedEntity = new ReferencesInEmbeddedTest.ReferencedEntity();
        getDs().save(referencedEntity);
        container.embed = new ReferencesInEmbeddedTest.EmbedContainingReference();
        container.embed.ref = referencedEntity;
        getDs().save(container);
        final ReferencesInEmbeddedTest.Container reloadedContainer = getDs().get(container);
        Assert.assertNotNull(reloadedContainer);
    }

    @Entity
    private static class Container extends TestEntity {
        private String name;

        @Embedded
        private ReferencesInEmbeddedTest.EmbedContainingReference embed;
    }

    private static class EmbedContainingReference {
        private String name;

        @Reference
        private ReferencesInEmbeddedTest.ReferencedEntity ref;

        @Reference(lazy = true)
        private ReferencesInEmbeddedTest.ReferencedEntity lazyRef;
    }

    @Entity
    public static class ReferencedEntity extends TestEntity {
        private String foo;
    }
}

