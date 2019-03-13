/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.lock;


import DialectChecks.DoesNotSupportFollowOnLocking;
import LockMode.NONE;
import LockMode.OPTIMISTIC;
import LockModeType.OPTIMISTIC_FORCE_INCREMENT;
import LockModeType.PESSIMISTIC_FORCE_INCREMENT;
import LockModeType.PESSIMISTIC_READ;
import LockModeType.PESSIMISTIC_WRITE;
import LockModeType.READ;
import QueryHints.HINT_NATIVE_LOCKMODE;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.org.hibernate.query.Query;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.internal.SessionImpl;
import org.hibernate.jpa.AvailableSettings;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.query.NativeQuery;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class QueryLockingTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testOverallLockMode() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        org.hibernate.query.Query query = em.createQuery("from Lockable l").unwrap(Query.class);
        Assert.assertEquals(NONE, query.getLockOptions().getLockMode());
        Assert.assertNull(query.getLockOptions().getAliasSpecificLockMode("l"));
        Assert.assertEquals(NONE, query.getLockOptions().getEffectiveLockMode("l"));
        // NOTE : LockModeType.READ should map to LockMode.OPTIMISTIC
        query.setLockMode(READ);
        Assert.assertEquals(OPTIMISTIC, query.getLockOptions().getLockMode());
        Assert.assertNull(query.getLockOptions().getAliasSpecificLockMode("l"));
        Assert.assertEquals(OPTIMISTIC, query.getLockOptions().getEffectiveLockMode("l"));
        query.setHint(((AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE) + ".l"), PESSIMISTIC_WRITE);
        Assert.assertEquals(OPTIMISTIC, query.getLockOptions().getLockMode());
        Assert.assertEquals(LockMode.PESSIMISTIC_WRITE, query.getLockOptions().getAliasSpecificLockMode("l"));
        Assert.assertEquals(LockMode.PESSIMISTIC_WRITE, query.getLockOptions().getEffectiveLockMode("l"));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8756")
    public void testNoneLockModeForNonSelectQueryAllowed() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        org.hibernate.query.Query query = em.createQuery("delete from Lockable l").unwrap(Query.class);
        Assert.assertEquals(NONE, query.getLockOptions().getLockMode());
        query.setLockMode(LockModeType.NONE);
        em.getTransaction().commit();
        em.clear();
        // ensure other modes still throw the exception
        em.getTransaction().begin();
        query = em.createQuery("delete from Lockable l").unwrap(Query.class);
        Assert.assertEquals(NONE, query.getLockOptions().getLockMode());
        try {
            // Throws IllegalStateException
            query.setLockMode(PESSIMISTIC_WRITE);
            Assert.fail("IllegalStateException should have been thrown.");
        } catch (IllegalStateException e) {
            // expected
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }

    @Test
    public void testNativeSql() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        NativeQuery query = em.createNativeQuery("select * from lockable l").unwrap(NativeQuery.class);
        // the spec disallows calling setLockMode in a native SQL query
        try {
            query.setLockMode(READ);
            Assert.fail("Should have failed");
        } catch (IllegalStateException expected) {
        }
        // however, we should be able to set it using hints
        query.setHint(HINT_NATIVE_LOCKMODE, READ);
        // NOTE : LockModeType.READ should map to LockMode.OPTIMISTIC
        Assert.assertEquals(OPTIMISTIC, query.getLockOptions().getLockMode());
        Assert.assertNull(query.getLockOptions().getAliasSpecificLockMode("l"));
        Assert.assertEquals(OPTIMISTIC, query.getLockOptions().getEffectiveLockMode("l"));
        query.setHint(((AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE) + ".l"), PESSIMISTIC_WRITE);
        Assert.assertEquals(OPTIMISTIC, query.getLockOptions().getLockMode());
        Assert.assertEquals(LockMode.PESSIMISTIC_WRITE, query.getLockOptions().getAliasSpecificLockMode("l"));
        Assert.assertEquals(LockMode.PESSIMISTIC_WRITE, query.getLockOptions().getEffectiveLockMode("l"));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testPessimisticForcedIncrementOverall() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Lockable lock = new Lockable("name");
        em.persist(lock);
        em.getTransaction().commit();
        em.close();
        Integer initial = lock.getVersion();
        Assert.assertNotNull(initial);
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Lockable reread = em.createQuery("from Lockable", Lockable.class).setLockMode(PESSIMISTIC_FORCE_INCREMENT).getSingleResult();
        Assert.assertFalse(reread.getVersion().equals(initial));
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.remove(em.getReference(Lockable.class, reread.getId()));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testPessimisticForcedIncrementSpecific() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Lockable lock = new Lockable("name");
        em.persist(lock);
        em.getTransaction().commit();
        em.close();
        Integer initial = lock.getVersion();
        Assert.assertNotNull(initial);
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Lockable reread = em.createQuery("from Lockable l", Lockable.class).setHint(((AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE) + ".l"), PESSIMISTIC_FORCE_INCREMENT).getSingleResult();
        Assert.assertFalse(reread.getVersion().equals(initial));
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.remove(em.getReference(Lockable.class, reread.getId()));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testOptimisticForcedIncrementOverall() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Lockable lock = new Lockable("name");
        em.persist(lock);
        em.getTransaction().commit();
        em.close();
        Integer initial = lock.getVersion();
        Assert.assertNotNull(initial);
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Lockable reread = em.createQuery("from Lockable", Lockable.class).setLockMode(OPTIMISTIC_FORCE_INCREMENT).getSingleResult();
        Assert.assertEquals(initial, reread.getVersion());
        em.getTransaction().commit();
        em.close();
        Assert.assertFalse(reread.getVersion().equals(initial));
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.remove(em.getReference(Lockable.class, reread.getId()));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testOptimisticForcedIncrementSpecific() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Lockable lock = new Lockable("name");
        em.persist(lock);
        em.getTransaction().commit();
        em.close();
        Integer initial = lock.getVersion();
        Assert.assertNotNull(initial);
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Lockable reread = em.createQuery("from Lockable l", Lockable.class).setHint(((AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE) + ".l"), OPTIMISTIC_FORCE_INCREMENT).getSingleResult();
        Assert.assertEquals(initial, reread.getVersion());
        em.getTransaction().commit();
        em.close();
        Assert.assertFalse(reread.getVersion().equals(initial));
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.remove(em.getReference(Lockable.class, reread.getId()));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testOptimisticOverall() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Lockable lock = new Lockable("name");
        em.persist(lock);
        em.getTransaction().commit();
        em.close();
        Integer initial = lock.getVersion();
        Assert.assertNotNull(initial);
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Lockable reread = em.createQuery("from Lockable", Lockable.class).setLockMode(LockModeType.OPTIMISTIC).getSingleResult();
        Assert.assertEquals(initial, reread.getVersion());
        Assert.assertTrue(em.unwrap(SessionImpl.class).getActionQueue().hasBeforeTransactionActions());
        em.getTransaction().commit();
        em.close();
        Assert.assertEquals(initial, reread.getVersion());
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.remove(em.getReference(Lockable.class, reread.getId()));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9419")
    public void testNoVersionCheckAfterRemove() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Lockable lock = new Lockable("name");
        em.persist(lock);
        em.getTransaction().commit();
        em.close();
        Integer initial = lock.getVersion();
        Assert.assertNotNull(initial);
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Lockable reread = em.createQuery("from Lockable", Lockable.class).setLockMode(LockModeType.OPTIMISTIC).getSingleResult();
        Assert.assertEquals(initial, reread.getVersion());
        Assert.assertTrue(em.unwrap(SessionImpl.class).getActionQueue().hasBeforeTransactionActions());
        em.remove(reread);
        em.getTransaction().commit();
        em.close();
        Assert.assertEquals(initial, reread.getVersion());
    }

    @Test
    public void testOptimisticSpecific() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Lockable lock = new Lockable("name");
        em.persist(lock);
        em.getTransaction().commit();
        em.close();
        Integer initial = lock.getVersion();
        Assert.assertNotNull(initial);
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Lockable reread = em.createQuery("from Lockable l", Lockable.class).setHint(((AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE) + ".l"), LockModeType.OPTIMISTIC).getSingleResult();
        Assert.assertEquals(initial, reread.getVersion());
        Assert.assertTrue(em.unwrap(SessionImpl.class).getActionQueue().hasBeforeTransactionActions());
        em.getTransaction().commit();
        em.close();
        Assert.assertEquals(initial, reread.getVersion());
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.remove(em.getReference(Lockable.class, reread.getId()));
        em.getTransaction().commit();
        em.close();
    }

    /**
     * lock some entities via a query and check the resulting lock mode type via EntityManager
     */
    @Test
    @RequiresDialectFeature(DoesNotSupportFollowOnLocking.class)
    public void testEntityLockModeStateAfterQueryLocking() {
        // Create some test data
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(new QueryLockingTest.LocalEntity(1, "test"));
        em.getTransaction().commit();
        // em.close();
        // issue the query with locking
        // em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Query query = em.createQuery("select l from LocalEntity l");
        Assert.assertEquals(LockModeType.NONE, query.getLockMode());
        query.setLockMode(PESSIMISTIC_READ);
        Assert.assertEquals(PESSIMISTIC_READ, query.getLockMode());
        List<QueryLockingTest.LocalEntity> results = query.getResultList();
        // and check the lock mode for each result
        for (QueryLockingTest.LocalEntity e : results) {
            Assert.assertEquals(PESSIMISTIC_READ, em.getLockMode(e));
        }
        em.getTransaction().commit();
        em.close();
        // clean up test data
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.createQuery("delete from LocalEntity").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11376")
    @RequiresDialect(SQLServerDialect.class)
    public void testCriteriaWithPessimisticLock() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Person> criteria = builder.createQuery(.class);
            Root<Person> personRoot = criteria.from(.class);
            ParameterExpression<Long> personIdParameter = builder.parameter(.class);
            // Eagerly fetch the parent
            personRoot.fetch("parent", JoinType.LEFT);
            criteria.select(personRoot).where(builder.equal(personRoot.get("id"), personIdParameter));
            final List<Person> resultList = entityManager.createQuery(criteria).setParameter(personIdParameter, 1L).setLockMode(LockModeType.PESSIMISTIC_WRITE).getResultList();
            resultList.isEmpty();
        });
    }

    @Entity(name = "LocalEntity")
    @Table(name = "LocalEntity")
    public static class LocalEntity {
        private Integer id;

        private String name;

        public LocalEntity() {
        }

        public LocalEntity(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

