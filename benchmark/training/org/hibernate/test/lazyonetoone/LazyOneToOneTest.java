/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.lazyonetoone;


import java.util.Date;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.testing.Skip;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
@Skip(condition = LazyOneToOneTest.DomainClassesInstrumentedMatcher.class, message = "Test domain classes were not instrumented")
public class LazyOneToOneTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testLazy() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Person p = new Person("Gavin");
        Person p2 = new Person("Emmanuel");
        Employee e = new Employee(p);
        new Employment(e, "JBoss");
        Employment old = new Employment(e, "IFA");
        old.setEndDate(new Date());
        s.persist(p);
        s.persist(p2);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        p = ((Person) (s.createQuery("from Person where name='Gavin'").uniqueResult()));
        // assertFalse( Hibernate.isPropertyInitialized(p, "employee") );
        Assert.assertSame(p.getEmployee().getPerson(), p);
        Assert.assertTrue(Hibernate.isInitialized(p.getEmployee().getEmployments()));
        Assert.assertEquals(p.getEmployee().getEmployments().size(), 1);
        p2 = ((Person) (s.createQuery("from Person where name='Emmanuel'").uniqueResult()));
        Assert.assertNull(p2.getEmployee());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        p = ((Person) (s.get(Person.class, "Gavin")));
        // assertFalse( Hibernate.isPropertyInitialized(p, "employee") );
        Assert.assertSame(p.getEmployee().getPerson(), p);
        Assert.assertTrue(Hibernate.isInitialized(p.getEmployee().getEmployments()));
        Assert.assertEquals(p.getEmployee().getEmployments().size(), 1);
        p2 = ((Person) (s.get(Person.class, "Emmanuel")));
        Assert.assertNull(p2.getEmployee());
        s.delete(p2);
        s.delete(old);
        s.delete(p);
        t.commit();
        s.close();
    }

    public static class DomainClassesInstrumentedMatcher implements Skip.Matcher {
        @Override
        public boolean isMatch() {
            // we match (to skip) when the classes are *not* instrumented...
            return !(PersistentAttributeInterceptable.class.isAssignableFrom(Person.class));
        }
    }
}

