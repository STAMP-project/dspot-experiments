package dev.morphia.mapping.lazy;


import dev.morphia.TestBase;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.testutil.TestEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author josephpachod
 */
public class LazyInEmbeddedTest extends TestBase {
    @Test
    public void testLoadingOfRefInField() {
        // TODO us: exclusion does not work properly with maven + junit4
        if (!(LazyFeatureDependencies.testDependencyFullFilled())) {
            return;
        }
        getMorphia().map(LazyInEmbeddedTest.ContainerWithRefInField.class);
        getMorphia().map(LazyInEmbeddedTest.OtherEntity.class);
        LazyInEmbeddedTest.OtherEntity otherEntity = new LazyInEmbeddedTest.OtherEntity();
        LazyInEmbeddedTest.ContainerWithRefInField containerWithRefInField = new LazyInEmbeddedTest.ContainerWithRefInField();
        getDs().save(Arrays.asList(otherEntity, containerWithRefInField));
        otherEntity = getDs().get(otherEntity);
        containerWithRefInField = getDs().get(containerWithRefInField);
        Assert.assertNotNull(otherEntity);
        Assert.assertNotNull(containerWithRefInField);
        final LazyInEmbeddedTest.EmbedWithRef embedWithRef = new LazyInEmbeddedTest.EmbedWithRef();
        embedWithRef.otherEntity = otherEntity;
        containerWithRefInField.embedWithRef = embedWithRef;
        getDs().save(containerWithRefInField);
        containerWithRefInField = getDs().get(containerWithRefInField);
        Assert.assertNotNull(containerWithRefInField);
    }

    @Test
    public void testLoadingOfRefInList() {
        // TODO us: exclusion does not work properly with maven + junit4
        if (!(LazyFeatureDependencies.testDependencyFullFilled())) {
            return;
        }
        getMorphia().map(LazyInEmbeddedTest.ContainerWithRefList.class);
        getMorphia().map(LazyInEmbeddedTest.OtherEntity.class);
        LazyInEmbeddedTest.OtherEntity otherEntity = new LazyInEmbeddedTest.OtherEntity();
        LazyInEmbeddedTest.ContainerWithRefList containerWithRefInList = new LazyInEmbeddedTest.ContainerWithRefList();
        getDs().save(Arrays.asList(otherEntity, containerWithRefInList));
        otherEntity = getDs().get(otherEntity);
        containerWithRefInList = getDs().get(containerWithRefInList);
        Assert.assertNotNull(otherEntity);
        Assert.assertNotNull(containerWithRefInList);
        final LazyInEmbeddedTest.EmbedWithRef embedWithRef = new LazyInEmbeddedTest.EmbedWithRef();
        embedWithRef.otherEntity = otherEntity;
        containerWithRefInList.embedWithRef.add(embedWithRef);
        getDs().save(Arrays.asList(otherEntity, containerWithRefInList));
        containerWithRefInList = getDs().get(containerWithRefInList);
        Assert.assertNotNull(containerWithRefInList);
        final Query<LazyInEmbeddedTest.ContainerWithRefList> createQuery = getDs().find(LazyInEmbeddedTest.ContainerWithRefList.class);
        containerWithRefInList = createQuery.find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(containerWithRefInList);
    }

    @Test
    public void testLoadingOfRefThroughInheritanceInField() {
        // TODO us: exclusion does not work properly with maven + junit4
        if (!(LazyFeatureDependencies.testDependencyFullFilled())) {
            return;
        }
        getMorphia().map(LazyInEmbeddedTest.ContainerWithRefInField.class);
        getMorphia().map(LazyInEmbeddedTest.OtherEntityChild.class);
        LazyInEmbeddedTest.OtherEntityChild otherEntity = new LazyInEmbeddedTest.OtherEntityChild();
        LazyInEmbeddedTest.ContainerWithRefInField containerWithRefInField = new LazyInEmbeddedTest.ContainerWithRefInField();
        getDs().save(Arrays.asList(otherEntity, containerWithRefInField));
        otherEntity = getDs().get(otherEntity);
        final LazyInEmbeddedTest.ContainerWithRefInField reload = getDs().get(containerWithRefInField);
        Assert.assertNotNull(otherEntity);
        Assert.assertNotNull(reload);
        final LazyInEmbeddedTest.EmbedWithRef embedWithRef = new LazyInEmbeddedTest.EmbedWithRef();
        embedWithRef.otherEntity = otherEntity;
        reload.embedWithRef = embedWithRef;
        getDs().save(reload);
        getDs().get(reload);
        containerWithRefInField = getDs().get(containerWithRefInField);
        Assert.assertNotNull(containerWithRefInField);
    }

    @Test
    public void testLoadingOfRefThroughInheritanceInList() {
        // TODO us: exclusion does not work properly with maven + junit4
        if (!(LazyFeatureDependencies.testDependencyFullFilled())) {
            return;
        }
        getMorphia().map(LazyInEmbeddedTest.ContainerWithRefList.class);
        getMorphia().map(LazyInEmbeddedTest.OtherEntityChild.class);
        LazyInEmbeddedTest.OtherEntityChild otherEntity = new LazyInEmbeddedTest.OtherEntityChild();
        LazyInEmbeddedTest.ContainerWithRefList containerWithRefInList = new LazyInEmbeddedTest.ContainerWithRefList();
        getDs().save(Arrays.asList(otherEntity, containerWithRefInList));
        otherEntity = getDs().get(otherEntity);
        final LazyInEmbeddedTest.ContainerWithRefList reload = getDs().get(containerWithRefInList);
        Assert.assertNotNull(otherEntity);
        Assert.assertNotNull(reload);
        final LazyInEmbeddedTest.EmbedWithRef embedWithRef = new LazyInEmbeddedTest.EmbedWithRef();
        embedWithRef.otherEntity = otherEntity;
        reload.embedWithRef.add(embedWithRef);
        getDs().save(Arrays.asList(otherEntity, reload));
        getDs().get(reload);
        containerWithRefInList = getDs().get(reload);
        Assert.assertNotNull(containerWithRefInList);
        final Query<LazyInEmbeddedTest.ContainerWithRefList> createQuery = getDs().find(LazyInEmbeddedTest.ContainerWithRefList.class);
        containerWithRefInList = createQuery.find(new FindOptions().limit(1)).tryNext();
        Assert.assertNotNull(containerWithRefInList);
    }

    public enum SomeEnum {

        B,
        A;}

    @Entity
    public static class ContainerWithRefInField extends TestEntity {
        @Embedded
        private LazyInEmbeddedTest.EmbedWithRef embedWithRef;
    }

    @Entity
    public static class ContainerWithRefList extends TestEntity {
        @Embedded
        private final List<LazyInEmbeddedTest.EmbedWithRef> embedWithRef = new ArrayList<LazyInEmbeddedTest.EmbedWithRef>();
    }

    @Entity
    public static class OtherEntity extends TestEntity {
        @Property("some")
        private LazyInEmbeddedTest.SomeEnum someEnum;

        protected OtherEntity() {
        }

        public OtherEntity(final LazyInEmbeddedTest.SomeEnum someEnum) {
            this.someEnum = someEnum;
        }
    }

    @Entity
    public static class OtherEntityChild extends LazyInEmbeddedTest.OtherEntity {
        @Property
        private String name;

        public OtherEntityChild() {
            super(LazyInEmbeddedTest.SomeEnum.A);
        }
    }

    public static class EmbedWithRef implements Serializable {
        @Reference(lazy = true)
        private LazyInEmbeddedTest.OtherEntity otherEntity;
    }
}

