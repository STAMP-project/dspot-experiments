/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.jpa.compliance.tck2_2.caching;


import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hibernate.Hibernate;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class InheritedCacheableTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testMapping() {
        MatcherAssert.assertThat(sessionFactory().getMetamodel().entityPersister(InheritedCacheableTest.Person.class).hasCache(), CoreMatchers.is(true));
        MatcherAssert.assertThat(sessionFactory().getMetamodel().entityPersister(InheritedCacheableTest.Employee.class).hasCache(), CoreMatchers.is(true));
        MatcherAssert.assertThat(sessionFactory().getMetamodel().entityPersister(InheritedCacheableTest.Customer.class).hasCache(), CoreMatchers.is(false));
    }

    @Test
    public void testOnlySubclassIsCached() {
        final StatisticsImplementor statistics = sessionFactory().getStatistics();
        inTransaction(( s) -> {
            s.persist(new org.hibernate.test.jpa.compliance.tck2_2.caching.Employee("1", "John Doe", "987", "engineering"));
            s.persist(new org.hibernate.test.jpa.compliance.tck2_2.caching.Customer("2", "Acme Corp", "123"));
        });
        Assert.assertTrue(sessionFactory().getCache().contains(InheritedCacheableTest.Employee.class, "1"));
        Assert.assertTrue(sessionFactory().getCache().contains(InheritedCacheableTest.Person.class, "1"));
        Assert.assertFalse(sessionFactory().getCache().contains(InheritedCacheableTest.Customer.class, "2"));
        Assert.assertFalse(sessionFactory().getCache().contains(InheritedCacheableTest.Person.class, "2"));
        inTransaction(( s) -> {
            statistics.clear();
            final org.hibernate.test.jpa.compliance.tck2_2.caching.Customer customer = s.get(.class, "2");
            assertTrue(Hibernate.isInitialized(customer));
            assertThat(statistics.getSecondLevelCacheHitCount(), CoreMatchers.is(0L));
            statistics.clear();
            final org.hibernate.test.jpa.compliance.tck2_2.caching.Employee emp = s.get(.class, "1");
            assertTrue(Hibernate.isInitialized(emp));
            assertThat(statistics.getSecondLevelCacheHitCount(), CoreMatchers.is(1L));
        });
    }

    @Entity(name = "Person")
    @Table(name = "persons")
    @Cacheable
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    public static class Person {
        @Id
        public String id;

        public String name;

        public Person() {
        }

        public Person(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Entity(name = "Employee")
    public static class Employee extends InheritedCacheableTest.Person {
        public String employeeCode;

        public String costCenter;

        public Employee() {
        }

        public Employee(String id, String name, String employeeCode, String costCenter) {
            super(id, name);
            this.employeeCode = employeeCode;
            this.costCenter = costCenter;
        }
    }

    @Entity(name = "Customer")
    @Cacheable(false)
    public static class Customer extends InheritedCacheableTest.Person {
        public String erpCode;

        public Customer() {
        }

        public Customer(String id, String name, String erpCode) {
            super(id, name);
            this.erpCode = erpCode;
        }
    }
}

