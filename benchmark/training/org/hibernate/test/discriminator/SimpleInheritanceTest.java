/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.discriminator;


import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class SimpleInheritanceTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testDiscriminatorSubclass() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Employee mark = new Employee();
        mark.setId(1);
        mark.setName("Mark");
        mark.setTitle("internal sales");
        mark.setSex('M');
        Customer joe = new Customer();
        joe.setId(2);
        joe.setName("Joe");
        joe.setComments("Very demanding");
        joe.setSex('M');
        Person yomomma = new Person();
        yomomma.setId(3);
        yomomma.setName("mum");
        yomomma.setSex('F');
        s.save(yomomma);
        s.save(mark);
        s.save(joe);
        Assert.assertEquals(s.createQuery("from java.io.Serializable").list().size(), 0);
        Assert.assertEquals(s.createQuery("from org.hibernate.test.discriminator.Person").list().size(), 3);
        Assert.assertEquals(s.createQuery("from org.hibernate.test.discriminator.Person p where p.class = org.hibernate.test.discriminator.Person").list().size(), 1);
        Assert.assertEquals(s.createQuery("from org.hibernate.test.discriminator.Person p where p.class = org.hibernate.test.discriminator.Customer").list().size(), 1);
        Assert.assertEquals(s.createQuery("from org.hibernate.test.discriminator.Person p where type(p) = :who").setParameter("who", Person.class).list().size(), 1);
        Assert.assertEquals(s.createQuery("from org.hibernate.test.discriminator.Person p where type(p) in :who").setParameterList("who", new Class[]{ Customer.class, Person.class }).list().size(), 2);
        s.clear();
        List customers = s.createQuery("from org.hibernate.test.discriminator.Customer").list();
        for (Object customer : customers) {
            Customer c = ((Customer) (customer));
            Assert.assertEquals("Very demanding", c.getComments());
        }
        Assert.assertEquals(customers.size(), 1);
        s.clear();
        mark = ((Employee) (s.get(Employee.class, mark.getId())));
        joe = ((Customer) (s.get(Customer.class, joe.getId())));
        s.delete(mark);
        s.delete(joe);
        s.delete(yomomma);
        Assert.assertTrue(s.createQuery("from org.hibernate.test.discriminator.Person").list().isEmpty());
        t.commit();
        s.close();
    }

    @Test
    public void testAccessAsIncorrectSubclass() {
        Session s = openSession();
        s.beginTransaction();
        Employee e = new Employee();
        e.setId(4);
        e.setName("Steve");
        e.setSex('M');
        e.setTitle("grand poobah");
        s.save(e);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Customer c = ((Customer) (s.get(Customer.class, e.getId())));
        s.getTransaction().commit();
        s.close();
        Assert.assertNull(c);
        s = openSession();
        s.beginTransaction();
        e = ((Employee) (s.get(Employee.class, e.getId())));
        c = ((Customer) (s.get(Customer.class, e.getId())));
        s.getTransaction().commit();
        s.close();
        Assert.assertNotNull(e);
        Assert.assertNull(c);
        s = openSession();
        s.beginTransaction();
        s.delete(e);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testQuerySubclassAttribute() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Person p = new Person();
        p.setId(5);
        p.setName("Emmanuel");
        p.setSex('M');
        s.save(p);
        Employee q = new Employee();
        q.setId(6);
        q.setName("Steve");
        q.setSex('M');
        q.setTitle("Mr");
        q.setSalary(new BigDecimal(1000));
        s.save(q);
        List result = s.createQuery("from org.hibernate.test.discriminator.Person where salary > 100").list();
        Assert.assertEquals(result.size(), 1);
        Assert.assertSame(result.get(0), q);
        result = s.createQuery("from org.hibernate.test.discriminator.Person where salary > 100 or name like 'E%'").list();
        Assert.assertEquals(result.size(), 2);
        result = s.createCriteria(Person.class).add(Property.forName("salary").gt(new BigDecimal(100))).list();
        Assert.assertEquals(result.size(), 1);
        Assert.assertSame(result.get(0), q);
        // TODO: make this work:
        /* result = s.createQuery("select salary from Person where salary > 100").list();
        assertEquals( result.size(), 1 );
        assertEquals( result.get(0), new BigDecimal(1000) );
         */
        s.delete(p);
        s.delete(q);
        t.commit();
        s.close();
    }

    @Test
    public void testLoadSuperclassProxyPolymorphicAccess() {
        Session s = openSession();
        s.beginTransaction();
        Employee e = new Employee();
        e.setId(7);
        e.setName("Steve");
        e.setSex('M');
        e.setTitle("grand poobah");
        s.save(e);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        // load the superclass proxy.
        Person pLoad = ((Person) (s.load(Person.class, new Long(e.getId()))));
        Assert.assertTrue((pLoad instanceof HibernateProxy));
        Person pGet = ((Person) (s.get(Person.class, e.getId())));
        Person pQuery = ((Person) (s.createQuery("from org.hibernate.test.discriminator.Person where id = :id").setLong("id", e.getId()).uniqueResult()));
        Person pCriteria = ((Person) (s.createCriteria(Person.class).add(Restrictions.idEq(e.getId())).uniqueResult()));
        // assert that executing the queries polymorphically returns the same proxy
        Assert.assertSame(pLoad, pGet);
        Assert.assertSame(pLoad, pQuery);
        Assert.assertSame(pLoad, pCriteria);
        // assert that the proxy is not an instance of Employee
        Assert.assertFalse((pLoad instanceof Employee));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.delete(e);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testLoadSuperclassProxyEvictPolymorphicAccess() {
        Session s = openSession();
        s.beginTransaction();
        Employee e = new Employee();
        e.setId(8);
        e.setName("Steve");
        e.setSex('M');
        e.setTitle("grand poobah");
        s.save(e);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        // load the superclass proxy.
        Person pLoad = ((Person) (s.load(Person.class, new Long(e.getId()))));
        Assert.assertTrue((pLoad instanceof HibernateProxy));
        // evict the proxy
        s.evict(pLoad);
        Employee pGet = ((Employee) (s.get(Person.class, e.getId())));
        Employee pQuery = ((Employee) (s.createQuery("from org.hibernate.test.discriminator.Person where id = :id").setLong("id", e.getId()).uniqueResult()));
        Employee pCriteria = ((Employee) (s.createCriteria(Person.class).add(Restrictions.idEq(e.getId())).uniqueResult()));
        // assert that executing the queries polymorphically returns the same Employee instance
        Assert.assertSame(pGet, pQuery);
        Assert.assertSame(pGet, pCriteria);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.delete(e);
        s.getTransaction().commit();
        s.close();
    }
}

