package org.baeldung.persistence.service;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.baeldung.persistence.deletion.config.PersistenceJPAConfigDeletion;
import org.baeldung.persistence.deletion.model.Bar;
import org.baeldung.persistence.deletion.model.Baz;
import org.baeldung.persistence.deletion.model.Foo;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceJPAConfigDeletion.class }, loader = AnnotationConfigContextLoader.class)
@DirtiesContext
public class DeletionIntegrationTest {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Test
    @Transactional
    public final void givenEntityIsRemoved_thenItIsNotInDB() {
        Foo foo = new Foo("foo");
        entityManager.persist(foo);
        flushAndClear();
        foo = entityManager.find(Foo.class, foo.getId());
        Assert.assertThat(foo, Matchers.notNullValue());
        entityManager.remove(foo);
        flushAndClear();
        Assert.assertThat(entityManager.find(Foo.class, foo.getId()), Matchers.nullValue());
    }

    @Test
    @Transactional
    public final void givenEntityIsRemovedAndReferencedByAnotherEntity_thenItIsNotRemoved() {
        Bar bar = new Bar("bar");
        Foo foo = new Foo("foo");
        foo.setBar(bar);
        entityManager.persist(foo);
        flushAndClear();
        foo = entityManager.find(Foo.class, foo.getId());
        bar = entityManager.find(Bar.class, bar.getId());
        entityManager.remove(bar);
        flushAndClear();
        bar = entityManager.find(Bar.class, bar.getId());
        Assert.assertThat(bar, Matchers.notNullValue());
        foo = entityManager.find(Foo.class, foo.getId());
        foo.setBar(null);
        entityManager.remove(bar);
        flushAndClear();
        Assert.assertThat(entityManager.find(Bar.class, bar.getId()), Matchers.nullValue());
    }

    @Test
    @Transactional
    public final void givenEntityIsRemoved_thenRemovalIsCascaded() {
        Bar bar = new Bar("bar");
        Foo foo = new Foo("foo");
        foo.setBar(bar);
        entityManager.persist(foo);
        flushAndClear();
        foo = entityManager.find(Foo.class, foo.getId());
        entityManager.remove(foo);
        flushAndClear();
        Assert.assertThat(entityManager.find(Foo.class, foo.getId()), Matchers.nullValue());
        Assert.assertThat(entityManager.find(Bar.class, bar.getId()), Matchers.nullValue());
    }

    @Test
    @Transactional
    public final void givenEntityIsDisassociated_thenOrphanRemovalIsApplied() {
        Bar bar = new Bar("bar");
        Baz baz = new Baz("baz");
        bar.getBazList().add(baz);
        entityManager.persist(bar);
        flushAndClear();
        bar = entityManager.find(Bar.class, bar.getId());
        baz = bar.getBazList().get(0);
        bar.getBazList().remove(baz);
        flushAndClear();
        Assert.assertThat(entityManager.find(Baz.class, baz.getId()), Matchers.nullValue());
    }

    @Test
    @Transactional
    public final void givenEntityIsDeletedWithJpaBulkDeleteStatement_thenItIsNotInDB() {
        Foo foo = new Foo("foo");
        entityManager.persist(foo);
        flushAndClear();
        entityManager.createQuery("delete from Foo where id = :id").setParameter("id", foo.getId()).executeUpdate();
        Assert.assertThat(entityManager.find(Foo.class, foo.getId()), Matchers.nullValue());
    }

    @Test
    @Transactional
    public final void givenEntityIsDeletedWithNativeQuery_thenItIsNotInDB() {
        Foo foo = new Foo("foo");
        entityManager.persist(foo);
        flushAndClear();
        entityManager.createNativeQuery("delete from FOO where ID = :id").setParameter("id", foo.getId()).executeUpdate();
        Assert.assertThat(entityManager.find(Foo.class, foo.getId()), Matchers.nullValue());
    }

    @Test
    @Transactional
    public final void givenEntityIsSoftDeleted_thenItIsNotReturnedFromQueries() {
        Foo foo = new Foo("foo");
        entityManager.persist(foo);
        flushAndClear();
        foo = entityManager.find(Foo.class, foo.getId());
        foo.setDeleted();
        flushAndClear();
        Assert.assertThat(entityManager.find(Foo.class, foo.getId()), Matchers.nullValue());
    }
}

