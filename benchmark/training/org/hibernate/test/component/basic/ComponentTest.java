/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.component.basic;


import java.util.Date;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class ComponentTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testUpdateFalse() {
        sessionFactory().getStatistics().clear();
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User u = new User("gavin", "secret", new Person("Gavin King", new Date(), "Karbarook Ave"));
        s.persist(u);
        s.flush();
        u.getPerson().setName("XXXXYYYYY");
        t.commit();
        s.close();
        Assert.assertEquals(1, sessionFactory().getStatistics().getEntityInsertCount());
        Assert.assertEquals(0, sessionFactory().getStatistics().getEntityUpdateCount());
        s = openSession();
        t = s.beginTransaction();
        u = ((User) (s.get(User.class, "gavin")));
        Assert.assertEquals(u.getPerson().getName(), "Gavin King");
        s.delete(u);
        t.commit();
        s.close();
        Assert.assertEquals(1, sessionFactory().getStatistics().getEntityDeleteCount());
    }

    @Test
    public void testComponent() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User u = new User("gavin", "secret", new Person("Gavin King", new Date(), "Karbarook Ave"));
        s.persist(u);
        s.flush();
        u.getPerson().changeAddress("Phipps Place");
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        u = ((User) (s.get(User.class, "gavin")));
        Assert.assertEquals(u.getPerson().getAddress(), "Phipps Place");
        Assert.assertEquals(u.getPerson().getPreviousAddress(), "Karbarook Ave");
        Assert.assertEquals(u.getPerson().getYob(), ((u.getPerson().getDob().getYear()) + 1900));
        u.setPassword("$ecret");
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        u = ((User) (s.get(User.class, "gavin")));
        Assert.assertEquals(u.getPerson().getAddress(), "Phipps Place");
        Assert.assertEquals(u.getPerson().getPreviousAddress(), "Karbarook Ave");
        Assert.assertEquals(u.getPassword(), "$ecret");
        s.delete(u);
        t.commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-2366")
    public void testComponentStateChangeAndDirtiness() {
        Session s = openSession();
        s.beginTransaction();
        User u = new User("steve", "hibernater", new Person("Steve Ebersole", new Date(), "Main St"));
        s.persist(u);
        s.flush();
        long intialUpdateCount = sessionFactory().getStatistics().getEntityUpdateCount();
        u.getPerson().setAddress("Austin");
        s.flush();
        Assert.assertEquals((intialUpdateCount + 1), sessionFactory().getStatistics().getEntityUpdateCount());
        intialUpdateCount = sessionFactory().getStatistics().getEntityUpdateCount();
        u.getPerson().setAddress("Cedar Park");
        s.flush();
        Assert.assertEquals((intialUpdateCount + 1), sessionFactory().getStatistics().getEntityUpdateCount());
        s.delete(u);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testComponentQueries() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Employee emp = new Employee();
        emp.setHireDate(new Date());
        emp.setPerson(new Person());
        emp.getPerson().setName("steve");
        emp.getPerson().setDob(new Date());
        s.save(emp);
        s.createQuery("from Employee e where e.person = :p and 1 = 1 and 2=2").setParameter("p", emp.getPerson()).list();
        s.createQuery("from Employee e where :p = e.person").setParameter("p", emp.getPerson()).list();
        // The following fails on Sybase due to HHH-3510. When HHH-3510
        // is fixed, the check for SybaseASE15Dialect should be removed.
        if (!((getDialect()) instanceof SybaseASE15Dialect)) {
            s.createQuery("from Employee e where e.person = ('steve', current_timestamp)").list();
        }
        s.delete(emp);
        t.commit();
        s.close();
    }

    @Test
    @RequiresDialect(SybaseASE15Dialect.class)
    @FailureExpected(jiraKey = "HHH-3150")
    public void testComponentQueryMethodNoParensFailureExpected() {
        // Sybase should translate "current_timestamp" in HQL to "getdate()";
        // This fails currently due to HHH-3510. The following test should be
        // deleted and testComponentQueries() should be updated (as noted
        // in that test case) when HHH-3510 is fixed.
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Employee emp = new Employee();
        emp.setHireDate(new Date());
        emp.setPerson(new Person());
        emp.getPerson().setName("steve");
        emp.getPerson().setDob(new Date());
        s.save(emp);
        s.createQuery("from Employee e where e.person = ('steve', current_timestamp)").list();
        s.delete(emp);
        t.commit();
        s.close();
    }

    @Test
    public void testComponentFormulaQuery() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.createQuery("from User u where u.person.yob = 1999").list();
        s.createCriteria(User.class).add(Property.forName("person.yob").between(new Integer(1999), new Integer(2002))).list();
        if (getDialect().supportsRowValueConstructorSyntax()) {
            s.createQuery("from User u where u.person = ('gavin', :dob, 'Peachtree Rd', 'Karbarook Ave', 1974, 34, 'Peachtree Rd')").setDate("dob", new Date("March 25, 1974")).list();
            s.createQuery("from User where person = ('gavin', :dob, 'Peachtree Rd', 'Karbarook Ave', 1974, 34, 'Peachtree Rd')").setDate("dob", new Date("March 25, 1974")).list();
        }
        t.commit();
        s.close();
    }

    @Test
    public void testCustomColumnReadAndWrite() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        User u = new User("steve", "hibernater", new Person("Steve Ebersole", new Date(), "Main St"));
        final double HEIGHT_INCHES = 73;
        final double HEIGHT_CENTIMETERS = HEIGHT_INCHES * 2.54;
        u.getPerson().setHeightInches(HEIGHT_INCHES);
        s.persist(u);
        s.flush();
        // Test value conversion during insert
        // Value returned by Oracle native query is a Types.NUMERIC, which is mapped to a BigDecimalType;
        // Cast returned value to Number then call Number.doubleValue() so it works on all dialects.
        Double heightViaSql = ((Number) (s.createSQLQuery("select height_centimeters from T_USER where T_USER.username='steve'").uniqueResult())).doubleValue();
        Assert.assertEquals(HEIGHT_CENTIMETERS, heightViaSql, 0.01);
        // Test projection
        Double heightViaHql = ((Double) (s.createQuery("select u.person.heightInches from User u where u.id = 'steve'").uniqueResult()));
        Assert.assertEquals(HEIGHT_INCHES, heightViaHql, 0.01);
        // Test restriction and entity load via criteria
        u = ((User) (s.createCriteria(User.class).add(Restrictions.between("person.heightInches", (HEIGHT_INCHES - 0.01), (HEIGHT_INCHES + 0.01))).uniqueResult()));
        Assert.assertEquals(HEIGHT_INCHES, u.getPerson().getHeightInches(), 0.01);
        // Test predicate and entity load via HQL
        u = ((User) (s.createQuery("from User u where u.person.heightInches between ?1 and ?2").setDouble(1, (HEIGHT_INCHES - 0.01)).setDouble(2, (HEIGHT_INCHES + 0.01)).uniqueResult()));
        Assert.assertEquals(HEIGHT_INCHES, u.getPerson().getHeightInches(), 0.01);
        // Test update
        u.getPerson().setHeightInches(1);
        s.flush();
        heightViaSql = ((Number) (s.createSQLQuery("select height_centimeters from T_USER where T_USER.username='steve'").uniqueResult())).doubleValue();
        Assert.assertEquals(2.54, heightViaSql, 0.01);
        s.delete(u);
        t.commit();
        s.close();
    }

    @Test
    public void testNamedQuery() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.getNamedQuery("userNameIn").setParameterList("nameList", new Object[]{ "1ovthafew", "turin", "xam" }).list();
        t.commit();
        s.close();
    }

    @Test
    public void testMergeComponent() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Employee emp = new Employee();
        emp.setHireDate(new Date());
        emp.setPerson(new Person());
        emp.getPerson().setName("steve");
        emp.getPerson().setDob(new Date());
        s.persist(emp);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        emp = ((Employee) (s.get(Employee.class, emp.getId())));
        t.commit();
        s.close();
        Assert.assertNull(emp.getOptionalComponent());
        emp.setOptionalComponent(new OptionalComponent());
        emp.getOptionalComponent().setValue1("emp-value1");
        emp.getOptionalComponent().setValue2("emp-value2");
        s = openSession();
        t = s.beginTransaction();
        emp = ((Employee) (s.merge(emp)));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        emp = ((Employee) (s.get(Employee.class, emp.getId())));
        t.commit();
        s.close();
        Assert.assertEquals("emp-value1", emp.getOptionalComponent().getValue1());
        Assert.assertEquals("emp-value2", emp.getOptionalComponent().getValue2());
        emp.getOptionalComponent().setValue1(null);
        emp.getOptionalComponent().setValue2(null);
        s = openSession();
        t = s.beginTransaction();
        emp = ((Employee) (s.merge(emp)));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        emp = ((Employee) (s.get(Employee.class, emp.getId())));
        Hibernate.initialize(emp.getDirectReports());
        t.commit();
        s.close();
        Assert.assertNull(emp.getOptionalComponent());
        Employee emp1 = new Employee();
        emp1.setHireDate(new Date());
        emp1.setPerson(new Person());
        emp1.getPerson().setName("bozo");
        emp1.getPerson().setDob(new Date());
        emp.getDirectReports().add(emp1);
        s = openSession();
        t = s.beginTransaction();
        emp = ((Employee) (s.merge(emp)));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        emp = ((Employee) (s.get(Employee.class, emp.getId())));
        Hibernate.initialize(emp.getDirectReports());
        t.commit();
        s.close();
        Assert.assertEquals(1, emp.getDirectReports().size());
        emp1 = ((Employee) (emp.getDirectReports().iterator().next()));
        Assert.assertNull(emp1.getOptionalComponent());
        emp1.setOptionalComponent(new OptionalComponent());
        emp1.getOptionalComponent().setValue1("emp1-value1");
        emp1.getOptionalComponent().setValue2("emp1-value2");
        s = openSession();
        t = s.beginTransaction();
        emp = ((Employee) (s.merge(emp)));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        emp = ((Employee) (s.get(Employee.class, emp.getId())));
        Hibernate.initialize(emp.getDirectReports());
        t.commit();
        s.close();
        Assert.assertEquals(1, emp.getDirectReports().size());
        emp1 = ((Employee) (emp.getDirectReports().iterator().next()));
        Assert.assertEquals("emp1-value1", emp1.getOptionalComponent().getValue1());
        Assert.assertEquals("emp1-value2", emp1.getOptionalComponent().getValue2());
        emp1.getOptionalComponent().setValue1(null);
        emp1.getOptionalComponent().setValue2(null);
        s = openSession();
        t = s.beginTransaction();
        emp = ((Employee) (s.merge(emp)));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        emp = ((Employee) (s.get(Employee.class, emp.getId())));
        Hibernate.initialize(emp.getDirectReports());
        t.commit();
        s.close();
        Assert.assertEquals(1, emp.getDirectReports().size());
        emp1 = ((Employee) (emp.getDirectReports().iterator().next()));
        Assert.assertNull(emp1.getOptionalComponent());
        s = openSession();
        t = s.beginTransaction();
        s.delete(emp);
        t.commit();
        s.close();
    }
}

