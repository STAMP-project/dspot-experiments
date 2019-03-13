/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ops.genericApi;


import LockMode.PESSIMISTIC_WRITE;
import LockOptions.UPGRADE;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Proxy;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ProxiedGetLoadAccessTest extends BaseNonConfigCoreFunctionalTestCase {
    public static interface User {
        public Integer getId();

        public String getName();

        public void setName(String name);
    }

    @Entity(name = "User")
    @Table(name = "my_user")
    @Proxy(proxyClass = ProxiedGetLoadAccessTest.User.class)
    public static class UserImpl implements ProxiedGetLoadAccessTest.User {
        private Integer id;

        private String name;

        public UserImpl() {
        }

        public UserImpl(String name) {
            this.name = name;
        }

        @Id
        @GeneratedValue(generator = "increment")
        @GenericGenerator(name = "increment", strategy = "increment")
        @Override
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
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
        s.save(new ProxiedGetLoadAccessTest.UserImpl("steve"));
        s.getTransaction().commit();
        s.close();
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // test `get` access
        s = openSession();
        s.beginTransaction();
        // THis technically works
        ProxiedGetLoadAccessTest.User user = s.get(ProxiedGetLoadAccessTest.UserImpl.class, 1);
        user = s.get(ProxiedGetLoadAccessTest.User.class, 1);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.get(ProxiedGetLoadAccessTest.UserImpl.class, 1, PESSIMISTIC_WRITE);
        user = s.get(ProxiedGetLoadAccessTest.User.class, 1, PESSIMISTIC_WRITE);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.get(ProxiedGetLoadAccessTest.UserImpl.class, 1, UPGRADE);
        user = s.get(ProxiedGetLoadAccessTest.User.class, 1, UPGRADE);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.byId(ProxiedGetLoadAccessTest.UserImpl.class).load(1);
        user = s.byId(ProxiedGetLoadAccessTest.User.class).load(1);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.byId(ProxiedGetLoadAccessTest.UserImpl.class).with(UPGRADE).load(1);
        user = s.byId(ProxiedGetLoadAccessTest.User.class).with(UPGRADE).load(1);
        s.getTransaction().commit();
        s.close();
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // test `load` access
        s = openSession();
        s.beginTransaction();
        user = s.load(ProxiedGetLoadAccessTest.UserImpl.class, 1);
        user = s.load(ProxiedGetLoadAccessTest.User.class, 1);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.load(ProxiedGetLoadAccessTest.UserImpl.class, 1, PESSIMISTIC_WRITE);
        user = s.load(ProxiedGetLoadAccessTest.User.class, 1, PESSIMISTIC_WRITE);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.load(ProxiedGetLoadAccessTest.UserImpl.class, 1, UPGRADE);
        user = s.load(ProxiedGetLoadAccessTest.User.class, 1, UPGRADE);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.byId(ProxiedGetLoadAccessTest.UserImpl.class).getReference(1);
        user = s.byId(ProxiedGetLoadAccessTest.User.class).getReference(1);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        user = s.byId(ProxiedGetLoadAccessTest.UserImpl.class).with(UPGRADE).getReference(1);
        user = s.byId(ProxiedGetLoadAccessTest.User.class).with(UPGRADE).getReference(1);
        s.getTransaction().commit();
        s.close();
    }
}

