package org.hibernate.test.cache;


import LockMode.READ;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Guenther Demetz
 * @author Gail Badner
 */
public class LockModeTest extends BaseCoreFunctionalTestCase {
    /**
     *
     */
    @TestForIssue(jiraKey = "HHH-9764")
    @Test
    public void testDefaultLockModeOnCollectionInitialization() {
        Session s1 = openSession();
        s1.beginTransaction();
        Company company1 = s1.get(Company.class, 1);
        User user1 = s1.get(User.class, 1);// into persistent context

        /**
         * ****************************************
         */
        Session s2 = openSession();
        s2.beginTransaction();
        User user = s2.get(User.class, 1);
        user.setName("TestUser");
        s2.getTransaction().commit();
        s2.close();
        /**
         * ****************************************
         */
        // init cache of collection
        Assert.assertEquals(1, company1.getUsers().size());// raises org.hibernate.StaleObjectStateException if 2LCache is enabled

        s1.getTransaction().commit();
        s1.close();
    }

    @TestForIssue(jiraKey = "HHH-9764")
    @Test
    public void testDefaultLockModeOnEntityLoad() {
        // first evict user
        sessionFactory().getCache().evictEntity(User.class.getName(), 1);
        Session s1 = openSession();
        s1.beginTransaction();
        Company company1 = s1.get(Company.class, 1);
        /**
         * ****************************************
         */
        Session s2 = openSession();
        s2.beginTransaction();
        Company company = s2.get(Company.class, 1);
        company.setName("TestCompany");
        s2.getTransaction().commit();
        s2.close();
        /**
         * ****************************************
         */
        User user1 = s1.get(User.class, 1);// into persistent context

        // init cache of collection
        Assert.assertNull(user1.getCompany().getName());// raises org.hibernate.StaleObjectStateException if 2LCache is enabled

        s1.getTransaction().commit();
        s1.close();
    }

    @TestForIssue(jiraKey = "HHH-9764")
    @Test
    public void testReadLockModeOnEntityLoad() {
        // first evict user
        sessionFactory().getCache().evictEntity(User.class.getName(), 1);
        Session s1 = openSession();
        s1.beginTransaction();
        Company company1 = s1.get(Company.class, 1);
        /**
         * ****************************************
         */
        Session s2 = openSession();
        s2.beginTransaction();
        Company company = s2.get(Company.class, 1);
        company.setName("TestCompany");
        s2.getTransaction().commit();
        s2.close();
        /**
         * ****************************************
         */
        User user1 = s1.get(User.class, 1, READ);// into persistent context

        // init cache of collection
        Assert.assertNull(user1.getCompany().getName());// raises org.hibernate.StaleObjectStateException if 2LCache is enabled

        s1.getTransaction().commit();
        s1.close();
    }
}

