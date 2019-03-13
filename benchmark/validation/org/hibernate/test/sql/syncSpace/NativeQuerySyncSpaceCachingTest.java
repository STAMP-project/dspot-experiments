/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.sql.syncSpace;


import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests of how sync-spaces for a native query affect caching
 *
 * @author Samuel Fung
 * @author Steve Ebersole
 */
public class NativeQuerySyncSpaceCachingTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testSelectAnotherEntityWithNoSyncSpaces() {
        Assert.assertTrue(sessionFactory().getCache().containsEntity(NativeQuerySyncSpaceCachingTest.Customer.class, 1));
        Session session = openSession();
        session.createSQLQuery("select * from Address").list();
        session.close();
        Assert.assertTrue(sessionFactory().getCache().containsEntity(NativeQuerySyncSpaceCachingTest.Customer.class, 1));
    }

    @Test
    public void testUpdateAnotherEntityWithNoSyncSpaces() {
        Assert.assertTrue(sessionFactory().getCache().containsEntity(NativeQuerySyncSpaceCachingTest.Customer.class, 1));
        Session session = openSession();
        session.beginTransaction();
        session.createSQLQuery("update Address set id = id").executeUpdate();
        session.getTransaction().commit();
        session.close();
        // NOTE false here because executeUpdate is different than selects
        Assert.assertFalse(sessionFactory().getCache().containsEntity(NativeQuerySyncSpaceCachingTest.Customer.class, 1));
    }

    @Test
    public void testUpdateAnotherEntityWithSyncSpaces() {
        Assert.assertTrue(sessionFactory().getCache().containsEntity(NativeQuerySyncSpaceCachingTest.Customer.class, 1));
        Session session = openSession();
        session.beginTransaction();
        session.createSQLQuery("update Address set id = id").addSynchronizedEntityClass(NativeQuerySyncSpaceCachingTest.Address.class).executeUpdate();
        session.getTransaction().commit();
        session.close();
        Assert.assertTrue(sessionFactory().getCache().containsEntity(NativeQuerySyncSpaceCachingTest.Customer.class, 1));
    }

    @Test
    public void testSelectCachedEntityWithNoSyncSpaces() {
        Assert.assertTrue(sessionFactory().getCache().containsEntity(NativeQuerySyncSpaceCachingTest.Customer.class, 1));
        Session session = openSession();
        session.createSQLQuery("select * from Customer").list();
        session.close();
        Assert.assertTrue(sessionFactory().getCache().containsEntity(NativeQuerySyncSpaceCachingTest.Customer.class, 1));
    }

    @Test
    public void testUpdateCachedEntityWithNoSyncSpaces() {
        Assert.assertTrue(sessionFactory().getCache().containsEntity(NativeQuerySyncSpaceCachingTest.Customer.class, 1));
        Session session = openSession();
        session.beginTransaction();
        session.createSQLQuery("update Customer set id = id").executeUpdate();
        session.getTransaction().commit();
        session.close();
        // NOTE false here because executeUpdate is different than selects
        Assert.assertFalse(sessionFactory().getCache().containsEntity(NativeQuerySyncSpaceCachingTest.Customer.class, 1));
    }

    @Test
    public void testUpdateCachedEntityWithSyncSpaces() {
        Assert.assertTrue(sessionFactory().getCache().containsEntity(NativeQuerySyncSpaceCachingTest.Customer.class, 1));
        Session session = openSession();
        session.beginTransaction();
        session.createSQLQuery("update Customer set id = id").addSynchronizedEntityClass(NativeQuerySyncSpaceCachingTest.Customer.class).executeUpdate();
        session.getTransaction().commit();
        session.close();
        Assert.assertFalse(sessionFactory().getCache().containsEntity(NativeQuerySyncSpaceCachingTest.Customer.class, 1));
    }

    @Entity(name = "Customer")
    @Table(name = "Customer")
    @Cacheable
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    public static class Customer {
        @Id
        private int id;

        private String name;

        public Customer() {
        }

        public Customer(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Entity(name = "Address")
    @Table(name = "Address")
    public static class Address {
        @Id
        private int id;

        private String text;

        public Address() {
        }

        public Address(int id, String text) {
            this.id = id;
            this.text = text;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}

