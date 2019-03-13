/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ops.genericApi;


import LockMode.PESSIMISTIC_WRITE;
import LockOptions.UPGRADE;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class BasicGetLoadAccessTest extends BaseNonConfigCoreFunctionalTestCase {
    @Entity(name = "User")
    @Table(name = "my_user")
    public static class User {
        private Integer id;

        private String name;

        public User() {
        }

        public User(String name) {
            this.name = name;
        }

        @Id
        @GeneratedValue(generator = "increment")
        @GenericGenerator(name = "increment", strategy = "increment")
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

    @Test
    public void testIt() {
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // create a row
        Session s = openSession();
        s.beginTransaction();
        s.save(new BasicGetLoadAccessTest.User("steve"));
        s.getTransaction().commit();
        s.close();
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // test `get` access
        s = openSession();
        s.beginTransaction();
        BasicGetLoadAccessTest.User user = s.get(BasicGetLoadAccessTest.User.class, 1);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.get(BasicGetLoadAccessTest.User.class, 1, PESSIMISTIC_WRITE);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.get(BasicGetLoadAccessTest.User.class, 1, UPGRADE);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.byId(BasicGetLoadAccessTest.User.class).load(1);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.byId(BasicGetLoadAccessTest.User.class).with(UPGRADE).load(1);
        s.getTransaction().commit();
        s.close();
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // test `load` access
        s = openSession();
        s.beginTransaction();
        user = s.load(BasicGetLoadAccessTest.User.class, 1);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.load(BasicGetLoadAccessTest.User.class, 1, PESSIMISTIC_WRITE);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.load(BasicGetLoadAccessTest.User.class, 1, UPGRADE);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.byId(BasicGetLoadAccessTest.User.class).getReference(1);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.byId(BasicGetLoadAccessTest.User.class).with(UPGRADE).getReference(1);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testNullLoadResult() {
        Session s = openSession();
        s.beginTransaction();
        Assert.assertNull(s.byId(BasicGetLoadAccessTest.User.class).load((-1)));
        Optional<BasicGetLoadAccessTest.User> user = s.byId(BasicGetLoadAccessTest.User.class).loadOptional((-1));
        Assert.assertNotNull(user);
        Assert.assertFalse(user.isPresent());
        try {
            user.get();
            Assert.fail("Expecting call to Optional#get to throw NoSuchElementException");
        } catch (NoSuchElementException expected) {
            // the expected result...
        }
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testNullQueryResult() {
        Session s = openSession();
        s.beginTransaction();
        Assert.assertNull(s.createQuery("select u from User u where u.id = -1").uniqueResult());
        Optional<BasicGetLoadAccessTest.User> user = s.createQuery("select u from User u where u.id = -1").uniqueResultOptional();
        Assert.assertNotNull(user);
        Assert.assertFalse(user.isPresent());
        try {
            user.get();
            Assert.fail("Expecting call to Optional#get to throw NoSuchElementException");
        } catch (NoSuchElementException expected) {
            // the expected result...
        }
        s.getTransaction().commit();
        s.close();
    }
}

