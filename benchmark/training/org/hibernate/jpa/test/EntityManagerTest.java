/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id$
 */
package org.hibernate.jpa.test;


import AvailableSettings.FLUSH_MODE;
import FlushMode.ALWAYS;
import FlushModeType.AUTO;
import FlushModeType.COMMIT;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Map;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class EntityManagerTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testEntityManager() {
        Item item = new Item("Mouse", "Micro$oft mouse");
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(item);
        Assert.assertTrue(em.contains(item));
        em.getTransaction().commit();
        Assert.assertTrue(em.contains(item));
        em.getTransaction().begin();
        Item item1 = ((Item) (em.createQuery("select i from Item i where descr like 'M%'").getSingleResult()));
        Assert.assertNotNull(item1);
        Assert.assertSame(item, item1);
        item.setDescr("Micro$oft wireless mouse");
        Assert.assertTrue(em.contains(item));
        em.getTransaction().commit();
        Assert.assertTrue(em.contains(item));
        em.getTransaction().begin();
        item1 = em.find(Item.class, "Mouse");
        Assert.assertSame(item, item1);
        em.getTransaction().commit();
        Assert.assertTrue(em.contains(item));
        item1 = em.find(Item.class, "Mouse");
        Assert.assertSame(item, item1);
        Assert.assertTrue(em.contains(item));
        item1 = ((Item) (em.createQuery("select i from Item i where descr like 'M%'").getSingleResult()));
        Assert.assertNotNull(item1);
        Assert.assertSame(item, item1);
        Assert.assertTrue(em.contains(item));
        em.getTransaction().begin();
        Assert.assertTrue(em.contains(item));
        em.remove(item);
        em.remove(item);// Second should be no-op

        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testConfiguration() throws Exception {
        Item item = new Item("Mouse", "Micro$oft mouse");
        Distributor res = new Distributor();
        res.setName("Bruce");
        item.setDistributors(new HashSet<Distributor>());
        item.getDistributors().add(res);
        Statistics stats = getSessionFactory().getStatistics();
        stats.clear();
        stats.setStatisticsEnabled(true);
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(res);
        em.persist(item);
        Assert.assertTrue(em.contains(item));
        em.getTransaction().commit();
        em.close();
        Assert.assertEquals(1, stats.getSecondLevelCachePutCount());
        Assert.assertEquals(0, stats.getSecondLevelCacheHitCount());
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Item second = em.find(Item.class, item.getName());
        Assert.assertEquals(1, second.getDistributors().size());
        Assert.assertEquals(1, stats.getSecondLevelCacheHitCount());
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        second = em.find(Item.class, item.getName());
        Assert.assertEquals(1, second.getDistributors().size());
        Assert.assertEquals(3, stats.getSecondLevelCacheHitCount());
        em.remove(second);
        em.remove(second.getDistributors().iterator().next());
        em.getTransaction().commit();
        em.close();
        stats.clear();
        stats.setStatisticsEnabled(false);
    }

    @Test
    public void testContains() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Integer nonManagedObject = Integer.valueOf(4);
        try {
            em.contains(nonManagedObject);
            Assert.fail("Should have raised an exception");
        } catch (IllegalArgumentException iae) {
            // success
            if ((em.getTransaction()) != null) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Item item = new Item();
        item.setDescr("Mine");
        item.setName("Juggy");
        em.persist(item);
        em.getTransaction().commit();
        em.getTransaction().begin();
        item = em.getReference(Item.class, item.getName());
        Assert.assertTrue(em.contains(item));
        em.remove(item);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testClear() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Wallet w = new Wallet();
        w.setBrand("Lacoste");
        w.setModel("Minimic");
        w.setSerial("0100202002");
        em.persist(w);
        em.flush();
        em.clear();
        Assert.assertFalse(em.contains(w));
        em.getTransaction().rollback();
        em.close();
    }

    @Test
    public void testFlushMode() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.setFlushMode(COMMIT);
        Assert.assertEquals(COMMIT, em.getFlushMode());
        ((Session) (em)).setFlushMode(ALWAYS);
        Assert.assertEquals(em.getFlushMode(), AUTO);
        em.close();
    }

    @Test
    public void testPersistNoneGenerator() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Wallet w = new Wallet();
        w.setBrand("Lacoste");
        w.setModel("Minimic");
        w.setSerial("0100202002");
        em.persist(w);
        em.getTransaction().commit();
        em.getTransaction().begin();
        Wallet wallet = em.find(Wallet.class, w.getSerial());
        Assert.assertEquals(w.getBrand(), wallet.getBrand());
        em.remove(wallet);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testSerializableException() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        try {
            Query query = em.createQuery("SELECT p FETCH JOIN p.distributors FROM Item p");
            query.getSingleResult();
        } catch (IllegalArgumentException e) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(stream);
            out.writeObject(e);
            out.close();
            byte[] serialized = stream.toByteArray();
            stream.close();
            ByteArrayInputStream byteIn = new ByteArrayInputStream(serialized);
            ObjectInputStream in = new ObjectInputStream(byteIn);
            IllegalArgumentException deserializedException = ((IllegalArgumentException) (in.readObject()));
            in.close();
            byteIn.close();
            Assert.assertNull(deserializedException.getCause().getCause());
            Assert.assertNull(e.getCause().getCause());
        }
        em.getTransaction().rollback();
        em.close();
        Exception e = new HibernateException("Exception", new NullPointerException("NPE"));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(stream);
        out.writeObject(e);
        out.close();
        byte[] serialized = stream.toByteArray();
        stream.close();
        ByteArrayInputStream byteIn = new ByteArrayInputStream(serialized);
        ObjectInputStream in = new ObjectInputStream(byteIn);
        HibernateException deserializedException = ((HibernateException) (in.readObject()));
        in.close();
        byteIn.close();
        Assert.assertNotNull("Arbitrary exceptions nullified", deserializedException.getCause());
        Assert.assertNotNull(e.getCause());
    }

    @Test
    public void testIsOpen() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        Assert.assertTrue(em.isOpen());
        em.getTransaction().begin();
        Assert.assertTrue(em.isOpen());
        em.getTransaction().rollback();
        em.close();
        Assert.assertFalse(em.isOpen());
    }

    @Test
    @TestForIssue(jiraKey = "EJB-9")
    public void testGet() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Item item = em.getReference(Item.class, "nonexistentone");
        try {
            item.getDescr();
            em.getTransaction().commit();
            Assert.fail("Object with wrong id should have failed");
        } catch (EntityNotFoundException e) {
            // success
            if ((em.getTransaction()) != null) {
                em.getTransaction().rollback();
            }
        } finally {
            em.close();
        }
    }

    @Test
    public void testGetProperties() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        Map<String, Object> properties = em.getProperties();
        Assert.assertNotNull(properties);
        try {
            properties.put("foo", "bar");
            Assert.fail();
        } catch (UnsupportedOperationException e) {
            // success
        }
        Assert.assertTrue(properties.containsKey(FLUSH_MODE));
    }

    @Test
    public void testSetProperty() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Wallet wallet = new Wallet();
        wallet.setSerial("000");
        em.persist(wallet);
        em.getTransaction().commit();
        em.clear();
        Assert.assertEquals(em.getProperties().get(FLUSH_MODE), "AUTO");
        Assert.assertNotNull("With default settings the entity should be persisted on commit.", em.find(Wallet.class, wallet.getSerial()));
        em.getTransaction().begin();
        wallet = em.merge(wallet);
        em.remove(wallet);
        em.getTransaction().commit();
        em.clear();
        Assert.assertNull("The entity should have been removed.", em.find(Wallet.class, wallet.getSerial()));
        em.setProperty("org.hibernate.flushMode", ("MANUAL" + ""));
        em.getTransaction().begin();
        wallet = new Wallet();
        wallet.setSerial("000");
        em.persist(wallet);
        em.getTransaction().commit();
        em.clear();
        Assert.assertNull("With a flush mode of manual the entity should not have been persisted.", em.find(Wallet.class, wallet.getSerial()));
        Assert.assertEquals("MANUAL", em.getProperties().get(FLUSH_MODE));
        em.close();
    }

    @Test
    public void testSetAndGetUnserializableProperty() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        try {
            EntityManagerTest.MyObject object = new EntityManagerTest.MyObject();
            object.value = 5;
            em.setProperty("MyObject", object);
            Assert.assertFalse(em.getProperties().keySet().contains("MyObject"));
        } finally {
            em.close();
        }
    }

    @Test
    public void testSetAndGetSerializedProperty() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        try {
            em.setProperty("MyObject", "Test123");
            Assert.assertTrue(em.getProperties().keySet().contains("MyObject"));
            Assert.assertEquals("Test123", em.getProperties().get("MyObject"));
        } finally {
            em.close();
        }
    }

    @Test
    public void testPersistExisting() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Wallet w = new Wallet();
        w.setBrand("Lacoste");
        w.setModel("Minimic");
        w.setSerial("0100202002");
        em.persist(w);
        w = new Wallet();
        w.setBrand("Lacoste");
        w.setModel("Minimic");
        w.setSerial("0100202002");
        try {
            em.persist(w);
        } catch (EntityExistsException eee) {
            // success
            if ((em.getTransaction()) != null) {
                em.getTransaction().rollback();
            }
            em.close();
            return;
        }
        try {
            em.getTransaction().commit();
            Assert.fail("Should have raised an exception");
        } catch (PersistenceException pe) {
        } finally {
            em.close();
        }
    }

    @Test
    public void testFactoryClosed() throws Exception {
        EntityManager em = createIsolatedEntityManager();
        Assert.assertTrue(em.isOpen());
        Assert.assertTrue(em.getEntityManagerFactory().isOpen());
        em.getEntityManagerFactory().close();// closing the entity manager factory should close the EM

        Assert.assertFalse(em.isOpen());
        try {
            em.close();
            Assert.fail("closing entity manager that uses a closed session factory, must throw IllegalStateException");
        } catch (IllegalStateException expected) {
            // success
        }
    }

    @Test
    public void testEntityNotFoundException() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Wallet w = new Wallet();
        w.setBrand("Lacoste");
        w.setModel("Minimic");
        w.setSerial("0324");
        em.persist(w);
        Wallet wallet = em.find(Wallet.class, w.getSerial());
        em.createNativeQuery("delete from Wallet").executeUpdate();
        try {
            em.refresh(wallet);
        } catch (EntityNotFoundException enfe) {
            // success
            if ((em.getTransaction()) != null) {
                em.getTransaction().rollback();
            }
            em.close();
            return;
        }
        try {
            em.getTransaction().commit();
            Assert.fail("Should have raised an EntityNotFoundException");
        } catch (PersistenceException pe) {
        } finally {
            em.close();
        }
    }

    private static class MyObject {
        public int value;
    }
}

