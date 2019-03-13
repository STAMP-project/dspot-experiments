package org.baeldung.persistence.service;


import CacheManager.ALL_CACHE_MANAGERS;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.RandomStringUtils;
import org.baeldung.config.PersistenceJPAConfigL2Cache;
import org.baeldung.persistence.model.Bar;
import org.baeldung.persistence.model.Foo;
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


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceJPAConfigL2Cache.class }, loader = AnnotationConfigContextLoader.class)
@DirtiesContext
public class SecondLevelCacheIntegrationTest {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private FooService fooService;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Test
    public final void givenEntityIsLoaded_thenItIsCached() {
        final Foo foo = new Foo(RandomStringUtils.randomAlphabetic(6));
        fooService.create(foo);
        fooService.findOne(foo.getId());
        final int size = ALL_CACHE_MANAGERS.get(0).getCache("org.baeldung.persistence.model.Foo").getSize();
        Assert.assertThat(size, Matchers.greaterThan(0));
    }

    @Test
    public final void givenBarIsUpdatedInNativeQuery_thenFoosAreNotEvicted() {
        final Foo foo = new Foo(RandomStringUtils.randomAlphabetic(6));
        fooService.create(foo);
        fooService.findOne(foo.getId());
        execute(( status) -> {
            final Bar bar = new Bar(randomAlphabetic(6));
            entityManager.persist(bar);
            final Query nativeQuery = entityManager.createNativeQuery("update BAR set NAME = :updatedName where ID = :id");
            nativeQuery.setParameter("updatedName", "newName");
            nativeQuery.setParameter("id", bar.getId());
            nativeQuery.unwrap(.class).addSynchronizedEntityClass(.class);
            return nativeQuery.executeUpdate();
        });
        final int size = ALL_CACHE_MANAGERS.get(0).getCache("org.baeldung.persistence.model.Foo").getSize();
        Assert.assertThat(size, Matchers.greaterThan(0));
    }

    @Test
    public final void givenCacheableQueryIsExecuted_thenItIsCached() {
        execute(( status) -> {
            return entityManager.createQuery("select f from Foo f").setHint("org.hibernate.cacheable", true).getResultList();
        });
        final int size = ALL_CACHE_MANAGERS.get(0).getCache("org.hibernate.cache.internal.StandardQueryCache").getSize();
        Assert.assertThat(size, Matchers.greaterThan(0));
    }
}

