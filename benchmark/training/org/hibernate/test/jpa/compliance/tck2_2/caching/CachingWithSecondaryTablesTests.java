/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.jpa.compliance.tck2_2.caching;


import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hamcrest.CoreMatchers;
import org.hibernate.Hibernate;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.transaction.TransactionUtil2;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class CachingWithSecondaryTablesTests extends BaseUnitTestCase {
    private SessionFactoryImplementor sessionFactory;

    @Test
    public void testUnstrictUnversioned() {
        sessionFactory = buildSessionFactory(CachingWithSecondaryTablesTests.Person.class, false);
        final StatisticsImplementor statistics = sessionFactory.getStatistics();
        TransactionUtil2.inTransaction(sessionFactory, ( s) -> s.persist(new org.hibernate.test.jpa.compliance.tck2_2.caching.Person("1", "John Doe", true)));
        // it should not be in the cache because it should be invalidated instead
        Assert.assertEquals(statistics.getSecondLevelCachePutCount(), 0);
        Assert.assertFalse(sessionFactory.getCache().contains(CachingWithSecondaryTablesTests.Person.class, "1"));
        TransactionUtil2.inTransaction(sessionFactory, ( s) -> {
            statistics.clear();
            final org.hibernate.test.jpa.compliance.tck2_2.caching.Person person = s.get(.class, "1");
            assertTrue(Hibernate.isInitialized(person));
            assertThat(statistics.getSecondLevelCacheHitCount(), CoreMatchers.is(0L));
            statistics.clear();
        });
    }

    @Test
    public void testStrictUnversioned() {
        sessionFactory = buildSessionFactory(CachingWithSecondaryTablesTests.Person.class, true);
        final StatisticsImplementor statistics = sessionFactory.getStatistics();
        TransactionUtil2.inTransaction(sessionFactory, ( s) -> s.persist(new org.hibernate.test.jpa.compliance.tck2_2.caching.Person("1", "John Doe", true)));
        // this time it should be iun the cache because we enabled JPA compliance
        Assert.assertEquals(statistics.getSecondLevelCachePutCount(), 1);
        Assert.assertTrue(sessionFactory.getCache().contains(CachingWithSecondaryTablesTests.Person.class, "1"));
        TransactionUtil2.inTransaction(sessionFactory, ( s) -> {
            statistics.clear();
            final org.hibernate.test.jpa.compliance.tck2_2.caching.Person person = s.get(.class, "1");
            assertTrue(Hibernate.isInitialized(person));
            assertThat(statistics.getSecondLevelCacheHitCount(), CoreMatchers.is(1L));
            statistics.clear();
        });
    }

    @Test
    public void testVersioned() {
        sessionFactory = buildSessionFactory(CachingWithSecondaryTablesTests.VersionedPerson.class, false);
        final StatisticsImplementor statistics = sessionFactory.getStatistics();
        TransactionUtil2.inTransaction(sessionFactory, ( s) -> s.persist(new org.hibernate.test.jpa.compliance.tck2_2.caching.VersionedPerson("1", "John Doe", true)));
        // versioned data should be cacheable regardless
        Assert.assertEquals(statistics.getSecondLevelCachePutCount(), 1);
        Assert.assertTrue(sessionFactory.getCache().contains(CachingWithSecondaryTablesTests.VersionedPerson.class, "1"));
        TransactionUtil2.inTransaction(sessionFactory, ( s) -> {
            statistics.clear();
            final org.hibernate.test.jpa.compliance.tck2_2.caching.VersionedPerson person = s.get(.class, "1");
            assertTrue(Hibernate.isInitialized(person));
            assertThat(statistics.getSecondLevelCacheHitCount(), CoreMatchers.is(1L));
            statistics.clear();
        });
    }

    @Entity(name = "Person")
    @Table(name = "persons")
    @Cacheable
    @SecondaryTable(name = "crm_persons")
    public static class Person {
        @Id
        public String id;

        public String name;

        @Column(table = "crm_persons")
        public boolean crmMarketingSchpele;

        public Person() {
        }

        public Person(String id, String name, boolean crmMarketingSchpele) {
            this.id = id;
            this.name = name;
        }
    }

    @Entity(name = "VersionedPerson")
    @Table(name = "versioned_persons")
    @Cacheable
    @SecondaryTable(name = "crm_persons2")
    public static class VersionedPerson {
        @Id
        public String id;

        public String name;

        @Version
        public int version;

        @Column(table = "crm_persons2")
        public boolean crmMarketingSchpele;

        public VersionedPerson() {
        }

        public VersionedPerson(String id, String name, boolean crmMarketingSchpele) {
            this.id = id;
            this.name = name;
        }
    }
}

