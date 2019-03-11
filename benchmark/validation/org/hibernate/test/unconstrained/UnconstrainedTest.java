/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.unconstrained;


import FetchMode.JOIN;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class UnconstrainedTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testUnconstrainedNoCache() {
        Session session = openSession();
        Transaction tx = session.beginTransaction();
        Person p = new Person("gavin");
        p.setEmployeeId("123456");
        session.persist(p);
        tx.commit();
        session.close();
        sessionFactory().getCache().evictEntityRegion(Person.class);
        session = openSession();
        tx = session.beginTransaction();
        p = ((Person) (session.get(Person.class, "gavin")));
        Assert.assertNull(p.getEmployee());
        p.setEmployee(new Employee("123456"));
        tx.commit();
        session.close();
        sessionFactory().getCache().evictEntityRegion(Person.class);
        session = openSession();
        tx = session.beginTransaction();
        p = ((Person) (session.get(Person.class, "gavin")));
        Assert.assertTrue(Hibernate.isInitialized(p.getEmployee()));
        Assert.assertNotNull(p.getEmployee());
        session.delete(p);
        tx.commit();
        session.close();
    }

    @Test
    public void testUnconstrainedOuterJoinFetch() {
        Session session = openSession();
        Transaction tx = session.beginTransaction();
        Person p = new Person("gavin");
        p.setEmployeeId("123456");
        session.persist(p);
        tx.commit();
        session.close();
        sessionFactory().getCache().evictEntityRegion(Person.class);
        session = openSession();
        tx = session.beginTransaction();
        p = ((Person) (session.createCriteria(Person.class).setFetchMode("employee", JOIN).add(Restrictions.idEq("gavin")).uniqueResult()));
        Assert.assertNull(p.getEmployee());
        p.setEmployee(new Employee("123456"));
        tx.commit();
        session.close();
        sessionFactory().getCache().evictEntityRegion(Person.class);
        session = openSession();
        tx = session.beginTransaction();
        p = ((Person) (session.createCriteria(Person.class).setFetchMode("employee", JOIN).add(Restrictions.idEq("gavin")).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(p.getEmployee()));
        Assert.assertNotNull(p.getEmployee());
        session.delete(p);
        tx.commit();
        session.close();
    }

    @Test
    public void testUnconstrained() {
        Session session = openSession();
        Transaction tx = session.beginTransaction();
        Person p = new Person("gavin");
        p.setEmployeeId("123456");
        session.persist(p);
        tx.commit();
        session.close();
        session = openSession();
        tx = session.beginTransaction();
        p = ((Person) (session.get(Person.class, "gavin")));
        Assert.assertNull(p.getEmployee());
        p.setEmployee(new Employee("123456"));
        tx.commit();
        session.close();
        session = openSession();
        tx = session.beginTransaction();
        p = ((Person) (session.get(Person.class, "gavin")));
        Assert.assertTrue(Hibernate.isInitialized(p.getEmployee()));
        Assert.assertNotNull(p.getEmployee());
        session.delete(p);
        tx.commit();
        session.close();
    }
}

