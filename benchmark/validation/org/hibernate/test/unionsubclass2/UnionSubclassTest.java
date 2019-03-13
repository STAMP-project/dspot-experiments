/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.unionsubclass2;


import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.TeradataDialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class UnionSubclassTest extends BaseCoreFunctionalTestCase {
    @Test
    @SkipForDialect(value = TeradataDialect.class, jiraKey = "HHH-8190", comment = "SQL uses Teradata reserved word: title")
    public void testUnionSubclass() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Employee mark = new Employee();
        mark.setName("Mark");
        mark.setTitle("internal sales");
        mark.setSex('M');
        mark.setAddress("buckhead");
        mark.setZip("30305");
        mark.setCountry("USA");
        Customer joe = new Customer();
        joe.setName("Joe");
        joe.setAddress("San Francisco");
        joe.setZip("XXXXX");
        joe.setCountry("USA");
        joe.setComments("Very demanding");
        joe.setSex('M');
        joe.setSalesperson(mark);
        Person yomomma = new Person();
        yomomma.setName("mum");
        yomomma.setSex('F');
        s.save(yomomma);
        s.save(mark);
        s.save(joe);
        Assert.assertEquals(s.createQuery("from java.io.Serializable").list().size(), 0);
        Assert.assertEquals(s.createQuery("from Person").list().size(), 3);
        Assert.assertEquals(s.createQuery("from Person p where p.class = Customer").list().size(), 1);
        Assert.assertEquals(s.createQuery("from Person p where p.class = Person").list().size(), 1);
        Assert.assertEquals(s.createQuery("from Person p where type(p) in :who").setParameter("who", Customer.class).list().size(), 1);
        Assert.assertEquals(s.createQuery("from Person p where type(p) in :who").setParameterList("who", new Class[]{ Customer.class, Person.class }).list().size(), 2);
        s.clear();
        List customers = s.createQuery("from Customer c left join fetch c.salesperson").list();
        for (Object customer : customers) {
            Customer c = ((Customer) (customer));
            Assert.assertTrue(Hibernate.isInitialized(c.getSalesperson()));
            Assert.assertEquals(c.getSalesperson().getName(), "Mark");
        }
        Assert.assertEquals(customers.size(), 1);
        s.clear();
        customers = s.createQuery("from Customer").list();
        for (Object customer : customers) {
            Customer c = ((Customer) (customer));
            Assert.assertFalse(Hibernate.isInitialized(c.getSalesperson()));
            Assert.assertEquals(c.getSalesperson().getName(), "Mark");
        }
        Assert.assertEquals(customers.size(), 1);
        s.clear();
        mark = ((Employee) (s.get(Employee.class, Long.valueOf(mark.getId()))));
        joe = ((Customer) (s.get(Customer.class, Long.valueOf(joe.getId()))));
        mark.setZip("30306");
        Assert.assertEquals(s.createQuery("from Person p where p.address.zip = '30306'").list().size(), 1);
        s.createCriteria(Person.class).add(Restrictions.in("address", new Address[]{ mark.getAddress(), joe.getAddress() })).list();
        s.delete(mark);
        s.delete(joe);
        s.delete(yomomma);
        Assert.assertTrue(s.createQuery("from Person").list().isEmpty());
        t.commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = TeradataDialect.class, jiraKey = "HHH-8190", comment = "SQL uses Teradata reserved word: title")
    public void testQuerySubclassAttribute() {
        if ((getDialect()) instanceof HSQLDialect) {
            return;// TODO : why??

        }
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Person p = new Person();
        p.setName("Emmanuel");
        p.setSex('M');
        s.persist(p);
        Employee q = new Employee();
        q.setName("Steve");
        q.setSex('M');
        q.setTitle("Mr");
        q.setSalary(new BigDecimal(1000));
        s.persist(q);
        List result = s.createQuery("from Person where salary > 100").list();
        Assert.assertEquals(result.size(), 1);
        Assert.assertSame(result.get(0), q);
        result = s.createQuery("from Person where salary > 100 or name like 'E%'").list();
        Assert.assertEquals(result.size(), 2);
        result = s.createCriteria(Person.class).add(Property.forName("salary").gt(new BigDecimal(100))).list();
        Assert.assertEquals(result.size(), 1);
        Assert.assertSame(result.get(0), q);
        result = s.createQuery("select salary from Person where salary > 100").list();
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(((BigDecimal) (result.get(0))).intValue(), 1000);
        s.delete(p);
        s.delete(q);
        t.commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = TeradataDialect.class, jiraKey = "HHH-8190", comment = "SQL uses Teradata reserved word: title")
    public void testCustomColumnReadAndWrite() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        final double HEIGHT_INCHES = 73;
        final double HEIGHT_CENTIMETERS = HEIGHT_INCHES * 2.54;
        Person p = new Person();
        p.setName("Emmanuel");
        p.setSex('M');
        p.setHeightInches(HEIGHT_INCHES);
        s.persist(p);
        final double PASSWORD_EXPIRY_WEEKS = 4;
        final double PASSWORD_EXPIRY_DAYS = PASSWORD_EXPIRY_WEEKS * 7.0;
        Employee e = new Employee();
        e.setName("Steve");
        e.setSex('M');
        e.setTitle("Mr");
        e.setPasswordExpiryDays(PASSWORD_EXPIRY_DAYS);
        s.persist(e);
        s.flush();
        // Test value conversion during insert
        // Value returned by Oracle native query is a Types.NUMERIC, which is mapped to a BigDecimalType;
        // Cast returned value to Number then call Number.doubleValue() so it works on all dialects.
        Double heightViaSql = ((Number) (s.createSQLQuery("select height_centimeters from UPerson where name='Emmanuel'").uniqueResult())).doubleValue();
        Assert.assertEquals(HEIGHT_CENTIMETERS, heightViaSql, 0.01);
        Double expiryViaSql = ((Number) (s.createSQLQuery("select pwd_expiry_weeks from UEmployee where person_id=?").setLong(1, e.getId()).uniqueResult())).doubleValue();
        Assert.assertEquals(PASSWORD_EXPIRY_WEEKS, expiryViaSql, 0.01);
        // Test projection
        Double heightViaHql = ((Double) (s.createQuery("select p.heightInches from Person p where p.name = 'Emmanuel'").uniqueResult()));
        Assert.assertEquals(HEIGHT_INCHES, heightViaHql, 0.01);
        Double expiryViaHql = ((Double) (s.createQuery("select e.passwordExpiryDays from Employee e where e.name = 'Steve'").uniqueResult()));
        Assert.assertEquals(PASSWORD_EXPIRY_DAYS, expiryViaHql, 0.01);
        // Test restriction and entity load via criteria
        p = ((Person) (s.createCriteria(Person.class).add(Restrictions.between("heightInches", (HEIGHT_INCHES - 0.01), (HEIGHT_INCHES + 0.01))).uniqueResult()));
        Assert.assertEquals(HEIGHT_INCHES, p.getHeightInches(), 0.01);
        e = ((Employee) (s.createCriteria(Employee.class).add(Restrictions.between("passwordExpiryDays", (PASSWORD_EXPIRY_DAYS - 0.01), (PASSWORD_EXPIRY_DAYS + 0.01))).uniqueResult()));
        Assert.assertEquals(PASSWORD_EXPIRY_DAYS, e.getPasswordExpiryDays(), 0.01);
        // Test predicate and entity load via HQL
        p = ((Person) (s.createQuery("from Person p where p.heightInches between ?1 and ?2").setDouble(1, (HEIGHT_INCHES - 0.01)).setDouble(2, (HEIGHT_INCHES + 0.01)).uniqueResult()));
        Assert.assertEquals(HEIGHT_INCHES, p.getHeightInches(), 0.01);
        e = ((Employee) (s.createQuery("from Employee e where e.passwordExpiryDays between ?1 and ?2").setDouble(1, (PASSWORD_EXPIRY_DAYS - 0.01)).setDouble(2, (PASSWORD_EXPIRY_DAYS + 0.01)).uniqueResult()));
        Assert.assertEquals(PASSWORD_EXPIRY_DAYS, e.getPasswordExpiryDays(), 0.01);
        // Test update
        p.setHeightInches(1);
        e.setPasswordExpiryDays(7);
        s.flush();
        heightViaSql = ((Number) (s.createSQLQuery("select height_centimeters from UPerson where name='Emmanuel'").uniqueResult())).doubleValue();
        Assert.assertEquals(2.54, heightViaSql, 0.01);
        expiryViaSql = ((Number) (s.createSQLQuery("select pwd_expiry_weeks from UEmployee where person_id=?").setLong(1, e.getId()).uniqueResult())).doubleValue();
        Assert.assertEquals(1.0, expiryViaSql, 0.01);
        s.delete(p);
        s.delete(e);
        t.commit();
        s.close();
    }
}

