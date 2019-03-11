/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.component.empty;


import java.io.Serializable;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class EmptyCompositeManyToOneKeyCachedTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testGetEntityWithNullManyToOne() {
        sessionFactory().getCache().evictAllRegions();
        sessionFactory().getStatistics().clear();
        int id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity();
            session.persist(anEntity);
            return anEntity.id;
        });
        Assert.assertEquals(1, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getPutCount());
        sessionFactory().getStatistics().clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.find(.class, id);
            assertNotNull(anEntity);
            assertNull(anEntity.otherEntity);
        });
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getPutCount());
        Assert.assertEquals(1, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getMissCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getMissCount());
    }

    @Test
    public void testQueryEntityWithNullManyToOne() {
        sessionFactory().getCache().evictAllRegions();
        sessionFactory().getStatistics().clear();
        int id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity();
            session.persist(anEntity);
            return anEntity.id;
        });
        Assert.assertEquals(1, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getPutCount());
        sessionFactory().getStatistics().clear();
        final String queryString = "from AnEntity where id = " + id;
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.createQuery(queryString, .class).setCacheable(true).uniqueResult();
            assertNull(anEntity.otherEntity);
        });
        Assert.assertEquals(0, getQueryStatistics(queryString).getCacheHitCount());
        Assert.assertEquals(1, getQueryStatistics(queryString).getCacheMissCount());
        Assert.assertEquals(1, getQueryStatistics(queryString).getCachePutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getMissCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getMissCount());
        sessionFactory().getStatistics().clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.createQuery(queryString, .class).setCacheable(true).uniqueResult();
            assertNull(anEntity.otherEntity);
        });
        Assert.assertEquals(1, getQueryStatistics(queryString).getCacheHitCount());
        Assert.assertEquals(0, getQueryStatistics(queryString).getCacheMissCount());
        Assert.assertEquals(0, getQueryStatistics(queryString).getCachePutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getPutCount());
        Assert.assertEquals(1, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getMissCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getMissCount());
    }

    @Test
    public void testQueryEntityJoinFetchNullManyToOne() {
        sessionFactory().getCache().evictAllRegions();
        sessionFactory().getStatistics().clear();
        int id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity();
            session.persist(anEntity);
            return anEntity.id;
        });
        Assert.assertEquals(1, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getPutCount());
        sessionFactory().getStatistics().clear();
        final String queryString = "from AnEntity e join fetch e.otherEntity where e.id = " + id;
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.createQuery(queryString, .class).setCacheable(true).uniqueResult();
            assertNull(anEntity);
        });
        Assert.assertEquals(0, getQueryStatistics(queryString).getCacheHitCount());
        Assert.assertEquals(1, getQueryStatistics(queryString).getCacheMissCount());
        Assert.assertEquals(1, getQueryStatistics(queryString).getCachePutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getMissCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getMissCount());
        sessionFactory().getStatistics().clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.createQuery(queryString, .class).setCacheable(true).uniqueResult();
            assertNull(anEntity);
        });
        Assert.assertEquals(1, getQueryStatistics(queryString).getCacheHitCount());
        Assert.assertEquals(0, getQueryStatistics(queryString).getCacheMissCount());
        Assert.assertEquals(0, getQueryStatistics(queryString).getCachePutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getMissCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getMissCount());
    }

    @Test
    public void testQueryEntityLeftJoinFetchNullManyToOne() {
        sessionFactory().getCache().evictAllRegions();
        sessionFactory().getStatistics().clear();
        int id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity();
            session.persist(anEntity);
            return anEntity.id;
        });
        Assert.assertEquals(1, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getPutCount());
        sessionFactory().getStatistics().clear();
        final String queryString = "from AnEntity e left join fetch e.otherEntity where e.id = " + id;
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.createQuery(queryString, .class).setCacheable(true).uniqueResult();
            assertNull(anEntity.otherEntity);
        });
        Assert.assertEquals(0, getQueryStatistics(queryString).getCacheHitCount());
        Assert.assertEquals(1, getQueryStatistics(queryString).getCacheMissCount());
        Assert.assertEquals(1, getQueryStatistics(queryString).getCachePutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getMissCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getMissCount());
        sessionFactory().getStatistics().clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = session.createQuery(queryString, .class).setCacheable(true).uniqueResult();
            assertNull(anEntity.otherEntity);
        });
        Assert.assertEquals(1, getQueryStatistics(queryString).getCacheHitCount());
        Assert.assertEquals(0, getQueryStatistics(queryString).getCacheMissCount());
        Assert.assertEquals(0, getQueryStatistics(queryString).getCachePutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getPutCount());
        Assert.assertEquals(1, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getMissCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getMissCount());
    }

    @Test
    public void testQueryEntityAndNullManyToOne() {
        sessionFactory().getCache().evictAllRegions();
        sessionFactory().getStatistics().clear();
        int id = TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.component.empty.AnEntity anEntity = new org.hibernate.test.component.empty.AnEntity();
            session.persist(anEntity);
            return anEntity.id;
        });
        Assert.assertEquals(1, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getPutCount());
        sessionFactory().getStatistics().clear();
        final String queryString = "select e, e.otherEntity from AnEntity e left join e.otherEntity where e.id = " + id;
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final Object[] result = session.createQuery(queryString, .class).setCacheable(true).uniqueResult();
            assertEquals(2, result.length);
            assertTrue(.class.isInstance(result[0]));
            assertNull(result[1]);
        });
        Assert.assertEquals(0, getQueryStatistics(queryString).getCacheHitCount());
        Assert.assertEquals(1, getQueryStatistics(queryString).getCacheMissCount());
        Assert.assertEquals(1, getQueryStatistics(queryString).getCachePutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getMissCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getMissCount());
        sessionFactory().getStatistics().clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final Object[] result = session.createQuery(queryString, .class).setCacheable(true).uniqueResult();
            assertEquals(2, result.length);
            assertTrue(.class.isInstance(result[0]));
            assertNull(result[1]);
        });
        Assert.assertEquals(1, getQueryStatistics(queryString).getCacheHitCount());
        Assert.assertEquals(0, getQueryStatistics(queryString).getCacheMissCount());
        Assert.assertEquals(0, getQueryStatistics(queryString).getCachePutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getPutCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getPutCount());
        Assert.assertEquals(1, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getHitCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.AnEntity.class).getMissCount());
        Assert.assertEquals(0, getEntity2LCStatistics(EmptyCompositeManyToOneKeyCachedTest.OtherEntity.class).getMissCount());
    }

    @Entity(name = "AnEntity")
    @Cacheable
    public static class AnEntity {
        @Id
        private int id;

        @ManyToOne
        private EmptyCompositeManyToOneKeyCachedTest.OtherEntity otherEntity;
    }

    @Entity(name = "OtherEntity")
    @Cacheable
    public static class OtherEntity implements Serializable {
        @Id
        private String firstName;

        @Id
        private String lastName;

        private String description;

        @Override
        public String toString() {
            return ((((((((("OtherEntity{" + "firstName='") + (firstName)) + '\'') + ", lastName='") + (lastName)) + '\'') + ", description='") + (description)) + '\'') + '}';
        }
    }
}

