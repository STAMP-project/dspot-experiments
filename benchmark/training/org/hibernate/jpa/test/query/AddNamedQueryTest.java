/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.query;


import CacheMode.IGNORE;
import FlushMode.MANUAL;
import FlushModeType.AUTO;
import FlushModeType.COMMIT;
import LockMode.PESSIMISTIC_WRITE;
import LockModeType.NONE;
import LockModeType.OPTIMISTIC;
import QueryHints.HINT_TIMEOUT;
import QueryHints.SPEC_HINT_TIMEOUT;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.org.hibernate.query.Query;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link javax.persistence.EntityManagerFactory#addNamedQuery} handling.
 *
 * @author Steve Ebersole
 */
public class AddNamedQueryTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void basicTest() {
        // just making sure we can add one and that it is usable when we get it back
        EntityManager em = getOrCreateEntityManager();
        Query query = em.createQuery("from Item");
        final String name = "myBasicItemQuery";
        em.getEntityManagerFactory().addNamedQuery(name, query);
        Query query2 = em.createNamedQuery(name);
        query2.getResultList();
        em.close();
    }

    @Test
    public void testLockModeHandling() {
        final String name = "lock-mode-handling";
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Query q = em.createQuery("from Item");
        Assert.assertEquals(NONE, q.getLockMode());
        q.setLockMode(OPTIMISTIC);
        Assert.assertEquals(OPTIMISTIC, q.getLockMode());
        em.getEntityManagerFactory().addNamedQuery(name, q);
        // first, lets check the underlying stored query def
        SessionFactoryImplementor sfi = entityManagerFactory().unwrap(SessionFactoryImplementor.class);
        NamedQueryDefinition def = sfi.getNamedQueryRepository().getNamedQueryDefinition(name);
        Assert.assertEquals(LockMode.OPTIMISTIC, def.getLockOptions().getLockMode());
        // then lets create a query by name and check its setting
        q = em.createNamedQuery(name);
        Assert.assertEquals(LockMode.OPTIMISTIC, q.unwrap(Query.class).getLockOptions().getLockMode());
        Assert.assertEquals(OPTIMISTIC, q.getLockMode());
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testFlushModeHandling() {
        final String name = "flush-mode-handling";
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Query q = em.createQuery("from Item");
        Assert.assertEquals(AUTO, q.getFlushMode());
        q.setFlushMode(COMMIT);
        Assert.assertEquals(COMMIT, q.getFlushMode());
        em.getEntityManagerFactory().addNamedQuery(name, q);
        // first, lets check the underlying stored query def
        SessionFactoryImplementor sfi = entityManagerFactory().unwrap(SessionFactoryImplementor.class);
        NamedQueryDefinition def = sfi.getNamedQueryRepository().getNamedQueryDefinition(name);
        Assert.assertEquals(FlushMode.COMMIT, def.getFlushMode());
        // then lets create a query by name and check its setting
        q = em.createNamedQuery(name);
        Assert.assertEquals(FlushMode.COMMIT, q.unwrap(Query.class).getHibernateFlushMode());
        Assert.assertEquals(COMMIT, q.getFlushMode());
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testConfigValueHandling() {
        final String name = "itemJpaQueryWithLockModeAndHints";
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Query query = em.createNamedQuery(name);
        org.hibernate.query.Query hibernateQuery = ((org.hibernate.query.Query) (query));
        // assert the state of the query config settings based on the initial named query
        // 
        // NOTE: here we check "query options" via the Hibernate contract (allowing nullness checking); see below for access via the JPA contract
        Assert.assertNull(hibernateQuery.getQueryOptions().getFirstRow());
        Assert.assertNull(hibernateQuery.getQueryOptions().getMaxRows());
        Assert.assertEquals(MANUAL, hibernateQuery.getHibernateFlushMode());
        Assert.assertEquals(COMMIT, hibernateQuery.getFlushMode());
        Assert.assertEquals(IGNORE, hibernateQuery.getCacheMode());
        Assert.assertEquals(PESSIMISTIC_WRITE, hibernateQuery.getLockOptions().getLockMode());
        // jpa timeout is in milliseconds, whereas Hibernate's is in seconds
        Assert.assertEquals(((Integer) (3)), hibernateQuery.getTimeout());
        query.setHint(HINT_TIMEOUT, 10);
        em.getEntityManagerFactory().addNamedQuery(name, query);
        query = em.createNamedQuery(name);
        hibernateQuery = ((org.hibernate.query.Query) (query));
        // assert the state of the query config settings based on the initial named query
        // 
        // NOTE: here we check "query options" via the JPA contract
        Assert.assertEquals(0, hibernateQuery.getFirstResult());
        Assert.assertEquals(Integer.MAX_VALUE, hibernateQuery.getMaxResults());
        Assert.assertEquals(MANUAL, hibernateQuery.getHibernateFlushMode());
        Assert.assertEquals(COMMIT, hibernateQuery.getFlushMode());
        Assert.assertEquals(IGNORE, hibernateQuery.getCacheMode());
        Assert.assertEquals(PESSIMISTIC_WRITE, hibernateQuery.getLockOptions().getLockMode());
        Assert.assertEquals(((Integer) (10)), hibernateQuery.getTimeout());
        query.setHint(SPEC_HINT_TIMEOUT, 10000);
        em.getEntityManagerFactory().addNamedQuery(name, query);
        query = em.createNamedQuery(name);
        hibernateQuery = ((org.hibernate.query.Query) (query));
        // assert the state of the query config settings based on the initial named query
        Assert.assertEquals(0, hibernateQuery.getFirstResult());
        Assert.assertEquals(Integer.MAX_VALUE, hibernateQuery.getMaxResults());
        Assert.assertEquals(MANUAL, hibernateQuery.getHibernateFlushMode());
        Assert.assertEquals(COMMIT, hibernateQuery.getFlushMode());
        Assert.assertEquals(IGNORE, hibernateQuery.getCacheMode());
        Assert.assertEquals(PESSIMISTIC_WRITE, hibernateQuery.getLockOptions().getLockMode());
        Assert.assertEquals(((Integer) (10)), hibernateQuery.getTimeout());
        query.setFirstResult(51);
        em.getEntityManagerFactory().addNamedQuery(name, query);
        query = em.createNamedQuery(name);
        hibernateQuery = ((org.hibernate.query.Query) (query));
        // assert the state of the query config settings based on the initial named query
        Assert.assertEquals(51, hibernateQuery.getFirstResult());
        Assert.assertEquals(Integer.MAX_VALUE, hibernateQuery.getMaxResults());
        Assert.assertEquals(MANUAL, hibernateQuery.getHibernateFlushMode());
        Assert.assertEquals(COMMIT, hibernateQuery.getFlushMode());
        Assert.assertEquals(IGNORE, hibernateQuery.getCacheMode());
        Assert.assertEquals(PESSIMISTIC_WRITE, hibernateQuery.getLockOptions().getLockMode());
        Assert.assertEquals(((Integer) (10)), hibernateQuery.getTimeout());
        em.getTransaction().commit();
        em.close();
    }
}

