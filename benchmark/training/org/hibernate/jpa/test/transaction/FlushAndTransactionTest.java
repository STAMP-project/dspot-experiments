/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.transaction;


import LockModeType.READ;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TransactionRequiredException;
import org.hibernate.Session;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.stat.Statistics;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class FlushAndTransactionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testAlwaysTransactionalOperations() throws Exception {
        Book book = new Book();
        book.name = "Le petit prince";
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(book);
        em.getTransaction().commit();
        try {
            em.flush();
            Assert.fail("flush has to be inside a Tx");
        } catch (TransactionRequiredException e) {
            // success
        }
        try {
            em.lock(book, READ);
            Assert.fail("lock has to be inside a Tx");
        } catch (TransactionRequiredException e) {
            // success
        }
        em.getTransaction().begin();
        em.remove(em.find(Book.class, book.id));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testTransactionalOperationsWhenExtended() throws Exception {
        Book book = new Book();
        book.name = "Le petit prince";
        EntityManager em = getOrCreateEntityManager();
        Statistics stats = getSessionFactory().getStatistics();
        stats.clear();
        stats.setStatisticsEnabled(true);
        em.persist(book);
        Assert.assertEquals(0, stats.getEntityInsertCount());
        em.getTransaction().begin();
        em.flush();
        em.getTransaction().commit();
        Assert.assertEquals(1, stats.getEntityInsertCount());
        em.clear();
        book.name = "Le prince";
        book = em.merge(book);
        em.refresh(book);
        Assert.assertEquals(0, stats.getEntityUpdateCount());
        em.getTransaction().begin();
        em.flush();
        em.getTransaction().commit();
        Assert.assertEquals(0, stats.getEntityUpdateCount());
        book.name = "Le prince";
        em.getTransaction().begin();
        em.find(Book.class, book.id);
        em.getTransaction().commit();
        Assert.assertEquals(1, stats.getEntityUpdateCount());
        em.remove(book);
        Assert.assertEquals(0, stats.getEntityDeleteCount());
        em.getTransaction().begin();
        em.flush();
        em.getTransaction().commit();
        Assert.assertEquals(1, stats.getEntityDeleteCount());
        em.close();
        stats.setStatisticsEnabled(false);
    }

    @Test
    public void testMergeWhenExtended() throws Exception {
        Book book = new Book();
        book.name = "Le petit prince";
        EntityManager em = getOrCreateEntityManager();
        Statistics stats = getSessionFactory().getStatistics();
        em.getTransaction().begin();
        em.persist(book);
        Assert.assertEquals(0, stats.getEntityInsertCount());
        em.getTransaction().commit();
        em.clear();// persist and clear

        stats.clear();
        stats.setStatisticsEnabled(true);
        Book bookReloaded = em.find(Book.class, book.id);
        book.name = "Le prince";
        Assert.assertEquals("Merge should use the available entiies in the PC", em.merge(book), bookReloaded);
        Assert.assertEquals(book.name, bookReloaded.name);
        Assert.assertEquals(0, stats.getEntityDeleteCount());
        Assert.assertEquals(0, stats.getEntityInsertCount());
        Assert.assertEquals("Updates should have been queued", 0, stats.getEntityUpdateCount());
        em.getTransaction().begin();
        Book bookReReloaded = em.find(Book.class, bookReloaded.id);
        Assert.assertEquals("reload should return the object in PC", bookReReloaded, bookReloaded);
        Assert.assertEquals(bookReReloaded.name, bookReloaded.name);
        em.getTransaction().commit();
        Assert.assertEquals(0, stats.getEntityDeleteCount());
        Assert.assertEquals(0, stats.getEntityInsertCount());
        Assert.assertEquals("Work on Tx should flush", 1, stats.getEntityUpdateCount());
        em.getTransaction().begin();
        em.remove(bookReReloaded);
        em.getTransaction().commit();
        em.close();
        stats.setStatisticsEnabled(false);
    }

    @Test
    public void testCloseAndTransaction() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Book book = new Book();
        book.name = "Java for Dummies";
        em.close();
        Assert.assertFalse(em.isOpen());
        try {
            em.flush();
            Assert.fail("direct action on a closed em should fail");
        } catch (IllegalStateException e) {
            // success
        }
    }

    @Test
    public void testTransactionCommitDoesNotFlush() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Book book = new Book();
        book.name = "Java for Dummies";
        em.persist(book);
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        List result = em.createQuery("select book from Book book where book.name = :title").setParameter("title", book.name).getResultList();
        Assert.assertEquals("EntityManager.commit() should trigger a flush()", 1, result.size());
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testTransactionAndContains() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Book book = new Book();
        book.name = "Java for Dummies";
        em.persist(book);
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        List result = em.createQuery("select book from Book book where book.name = :title").setParameter("title", book.name).getResultList();
        Assert.assertEquals("EntityManager.commit() should trigger a flush()", 1, result.size());
        Assert.assertTrue(em.contains(result.get(0)));
        em.getTransaction().commit();
        Assert.assertTrue(em.contains(result.get(0)));
        em.close();
    }

    @Test
    public void testRollbackOnlyOnPersistenceException() throws Exception {
        Book book = new Book();
        book.name = "Stolen keys";
        book.id = null;// new Integer( 50 );

        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(book);
            em.flush();
            em.clear();
            book.setName("kitty kid");
            em.merge(book);
            em.flush();
            em.clear();
            book.setName("kitty kid2");// non updated version

            em.merge(book);
            em.flush();
            Assert.fail("optimistic locking exception");
        } catch (PersistenceException e) {
            // success
        }
        try {
            em.getTransaction().commit();
            Assert.fail("Commit should be rollbacked");
        } catch (RollbackException e) {
            // success
        } finally {
            em.close();
        }
    }

    @Test
    public void testRollbackExceptionOnOptimisticLockException() throws Exception {
        Book book = new Book();
        book.name = "Stolen keys";
        book.id = null;// new Integer( 50 );

        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(book);
        em.flush();
        em.clear();
        book.setName("kitty kid");
        em.merge(book);
        em.flush();
        em.clear();
        book.setName("kitty kid2");// non updated version

        em.unwrap(Session.class).update(book);
        try {
            em.getTransaction().commit();
            Assert.fail("Commit should be rollbacked");
        } catch (RollbackException e) {
            Assert.assertTrue("During flush a StateStateException is wrapped into a OptimisticLockException", ((e.getCause()) instanceof OptimisticLockException));
        } finally {
            em.close();
        }
    }

    @Test
    public void testRollbackClearPC() throws Exception {
        Book book = new Book();
        book.name = "Stolen keys";
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(book);
        em.getTransaction().commit();
        em.getTransaction().begin();
        book.name = "Recovered keys";
        em.merge(book);
        em.getTransaction().rollback();
        em.getTransaction().begin();
        Assert.assertEquals("Stolen keys", em.find(Book.class, book.id).name);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testSetRollbackOnlyAndFlush() throws Exception {
        Book book = new Book();
        book.name = "The jungle book";
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.getTransaction().setRollbackOnly();
        em.persist(book);
        em.flush();
        em.getTransaction().rollback();
        em.getTransaction().begin();
        Query query = em.createQuery("SELECT b FROM Book b WHERE b.name = :name");
        query.setParameter("name", book.name);
        Assert.assertEquals(0, query.getResultList().size());
        em.getTransaction().commit();
        em.close();
    }
}

