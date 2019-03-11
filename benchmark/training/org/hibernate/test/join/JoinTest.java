/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.join;


import java.util.Iterator;
import java.util.List;
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
public class JoinTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSequentialSelects() {
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
        Assert.assertEquals(s.createQuery("from Person p where p.class is null").list().size(), 1);
        Assert.assertEquals(s.createQuery("from Person p where p.class = Customer").list().size(), 1);
        Assert.assertTrue(((s.createQuery("from Customer c").list().size()) == 1));
        s.clear();
        List customers = s.createQuery("from Customer c left join fetch c.salesperson").list();
        for (Iterator iter = customers.iterator(); iter.hasNext();) {
            Customer c = ((Customer) (iter.next()));
            Assert.assertTrue(Hibernate.isInitialized(c.getSalesperson()));
            Assert.assertEquals(c.getSalesperson().getName(), "Mark");
        }
        Assert.assertEquals(customers.size(), 1);
        s.clear();
        customers = s.createQuery("from Customer").list();
        for (Iterator iter = customers.iterator(); iter.hasNext();) {
            Customer c = ((Customer) (iter.next()));
            Assert.assertFalse(Hibernate.isInitialized(c.getSalesperson()));
            Assert.assertEquals(c.getSalesperson().getName(), "Mark");
        }
        Assert.assertEquals(customers.size(), 1);
        s.clear();
        mark = ((Employee) (s.get(Employee.class, new Long(mark.getId()))));
        joe = ((Customer) (s.get(Customer.class, new Long(joe.getId()))));
        mark.setZip("30306");
        Assert.assertEquals(s.createQuery("from Person p where p.zip = '30306'").list().size(), 1);
        s.delete(mark);
        s.delete(joe);
        s.delete(yomomma);
        Assert.assertTrue(s.createQuery("from Person").list().isEmpty());
        t.commit();
        s.close();
    }

    @Test
    public void testSequentialSelectsOptionalData() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User jesus = new User();
        jesus.setName("Jesus Olvera y Martinez");
        jesus.setSex('M');
        s.save(jesus);
        Assert.assertEquals(s.createQuery("from java.io.Serializable").list().size(), 0);
        Assert.assertEquals(s.createQuery("from Person").list().size(), 1);
        Assert.assertEquals(s.createQuery("from Person p where p.class is null").list().size(), 0);
        Assert.assertEquals(s.createQuery("from Person p where p.class = User").list().size(), 1);
        Assert.assertTrue(((s.createQuery("from User u").list().size()) == 1));
        s.clear();
        // Remove the optional row from the join table and requery the User obj
        doWork(s);
        s.clear();
        jesus = ((User) (s.get(Person.class, new Long(jesus.getId()))));
        s.clear();
        // Cleanup the test data
        s.delete(jesus);
        Assert.assertTrue(s.createQuery("from Person").list().isEmpty());
        t.commit();
        s.close();
    }

    @Test
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
        User u = new User();
        u.setName("Steve");
        u.setSex('M');
        u.setPasswordExpiryDays(PASSWORD_EXPIRY_DAYS);
        s.persist(u);
        s.flush();
        // Test value conversion during insert
        // Oracle returns BigDecimaal while other dialects return Double;
        // casting to Number so it works on all dialects
        Number heightViaSql = ((Number) (s.createSQLQuery("select height_centimeters from person where name='Emmanuel'").uniqueResult()));
        Assert.assertEquals(HEIGHT_CENTIMETERS, heightViaSql.doubleValue(), 0.01);
        Number expiryViaSql = ((Number) (s.createSQLQuery("select pwd_expiry_weeks from t_user where person_id=?").setLong(0, u.getId()).uniqueResult()));
        Assert.assertEquals(PASSWORD_EXPIRY_WEEKS, expiryViaSql.doubleValue(), 0.01);
        // Test projection
        Double heightViaHql = ((Double) (s.createQuery("select p.heightInches from Person p where p.name = 'Emmanuel'").uniqueResult()));
        Assert.assertEquals(HEIGHT_INCHES, heightViaHql, 0.01);
        Double expiryViaHql = ((Double) (s.createQuery("select u.passwordExpiryDays from User u where u.name = 'Steve'").uniqueResult()));
        Assert.assertEquals(PASSWORD_EXPIRY_DAYS, expiryViaHql, 0.01);
        // Test restriction and entity load via criteria
        p = ((Person) (s.createCriteria(Person.class).add(Restrictions.between("heightInches", (HEIGHT_INCHES - 0.01), (HEIGHT_INCHES + 0.01))).uniqueResult()));
        Assert.assertEquals(HEIGHT_INCHES, p.getHeightInches(), 0.01);
        u = ((User) (s.createCriteria(User.class).add(Restrictions.between("passwordExpiryDays", (PASSWORD_EXPIRY_DAYS - 0.01), (PASSWORD_EXPIRY_DAYS + 0.01))).uniqueResult()));
        Assert.assertEquals(PASSWORD_EXPIRY_DAYS, u.getPasswordExpiryDays(), 0.01);
        // Test predicate and entity load via HQL
        p = ((Person) (s.createQuery("from Person p where p.heightInches between ?1 and ?2").setDouble(1, (HEIGHT_INCHES - 0.01)).setDouble(2, (HEIGHT_INCHES + 0.01)).uniqueResult()));
        Assert.assertNotNull(p);
        Assert.assertEquals(HEIGHT_INCHES, p.getHeightInches(), 0.01);
        u = ((User) (s.createQuery("from User u where u.passwordExpiryDays between ?1 and ?2").setDouble(1, (PASSWORD_EXPIRY_DAYS - 0.01)).setDouble(2, (PASSWORD_EXPIRY_DAYS + 0.01)).uniqueResult()));
        Assert.assertEquals(PASSWORD_EXPIRY_DAYS, u.getPasswordExpiryDays(), 0.01);
        // Test update
        p.setHeightInches(1);
        u.setPasswordExpiryDays(7.0);
        s.flush();
        heightViaSql = ((Number) (s.createSQLQuery("select height_centimeters from person where name='Emmanuel'").uniqueResult()));
        Assert.assertEquals(2.54, heightViaSql.doubleValue(), 0.01);
        expiryViaSql = ((Number) (s.createSQLQuery("select pwd_expiry_weeks from t_user where person_id=?").setLong(0, u.getId()).uniqueResult()));
        Assert.assertEquals(1.0, expiryViaSql.doubleValue(), 0.01);
        s.delete(p);
        s.delete(u);
        Assert.assertTrue(s.createQuery("from Person").list().isEmpty());
        t.commit();
        s.close();
    }
}

