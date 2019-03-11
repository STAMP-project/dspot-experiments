/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.naturalid.immutable;


import DialectChecks.SupportsIdentityColumns;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Alex Burgel
 */
@RequiresDialectFeature(value = SupportsIdentityColumns.class, jiraKey = "HHH-11330")
public class IdentifierGeneratorWithNaturalIdCacheTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10659")
    public void testNaturalIdCacheEntry() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.naturalid.immutable.Person person = new org.hibernate.test.naturalid.immutable.Person();
            person.setName("John Doe");
            session.persist(person);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            assertEquals(0, sessionFactory().getStatistics().getSecondLevelCacheHitCount());
            assertEquals(0, sessionFactory().getStatistics().getNaturalIdCacheHitCount());
            org.hibernate.test.naturalid.immutable.Person person = session.bySimpleNaturalId(.class).load("John Doe");
            assertEquals(0, sessionFactory().getStatistics().getSecondLevelCacheHitCount());
            assertEquals(1, sessionFactory().getStatistics().getNaturalIdCacheHitCount());
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.naturalid.immutable.Person person = session.bySimpleNaturalId(.class).load("John Doe");
            assertEquals(1, sessionFactory().getStatistics().getSecondLevelCacheHitCount());
            assertEquals(2, sessionFactory().getStatistics().getNaturalIdCacheHitCount());
        });
    }

    @Entity(name = "Person")
    @NaturalIdCache
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    public static class Person {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NaturalId
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

