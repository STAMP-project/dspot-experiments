/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.callbacks;


import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(BytecodeEnhancerRunner.class)
public class PrivateConstructorEnhancerTest extends BaseNonConfigCoreFunctionalTestCase {
    private PrivateConstructorEnhancerTest.Country country = new PrivateConstructorEnhancerTest.Country("Romania");

    private PrivateConstructorEnhancerTest.Person person = new PrivateConstructorEnhancerTest.Person("Vlad Mihalcea", country);

    @Test
    public void testFindEntity() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            try {
                org.hibernate.jpa.test.callbacks.Country country = session.find(.class, this.country.id);
                assertNotNull("Romania", country.getName());
                fail("Should have thrown exception");
            } catch ( expected) {
                assertTrue(expected.getMessage().contains("No default constructor for entity"));
            }
        });
    }

    @Test
    public void testGetReferenceEntity() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            try {
                org.hibernate.jpa.test.callbacks.Country country = session.getReference(.class, this.country.id);
                assertNotNull("Romania", country.getName());
                fail("Should have thrown exception");
            } catch ( expected) {
                assertTrue(expected.getMessage().contains("No default constructor for entity"));
            }
        });
    }

    @Test
    public void testLoadProxyAssociation() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            try {
                org.hibernate.jpa.test.callbacks.Person person = session.find(.class, this.person.id);
                assertNotNull("Romania", person.getCountry().getName());
                fail("Should have thrown exception");
            } catch ( expected) {
                assertTrue(expected.getMessage().contains("No default constructor for entity"));
            }
        });
    }

    @Test
    public void testListEntity() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            try {
                List<org.hibernate.jpa.test.callbacks.Person> persons = session.createQuery("select p from Person p").getResultList();
                assertTrue(persons.stream().anyMatch(( p) -> p.getCountry().getName().equals("Romania")));
                fail("Should have thrown exception");
            } catch ( expected) {
                assertTrue(expected.getMessage().contains("No default constructor for entity"));
            }
        });
    }

    @Test
    public void testListJoinFetchEntity() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            try {
                List<org.hibernate.jpa.test.callbacks.Person> persons = session.createQuery("select p from Person p join fetch p.country").getResultList();
                assertTrue(persons.stream().anyMatch(( p) -> p.getCountry().getName().equals("Romania")));
                fail("Should have thrown exception");
            } catch ( expected) {
                assertTrue(expected.getMessage().contains("No default constructor for entity"));
            }
        });
    }

    @Entity(name = "Person")
    private static class Person {
        @Id
        @GeneratedValue
        private int id;

        private String name;

        @ManyToOne(fetch = FetchType.LAZY)
        private PrivateConstructorEnhancerTest.Country country;

        public Person() {
        }

        private Person(String name, PrivateConstructorEnhancerTest.Country country) {
            this.name = name;
            this.country = country;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public PrivateConstructorEnhancerTest.Country getCountry() {
            return country;
        }
    }

    @Entity(name = "Country")
    private static class Country {
        @Id
        @GeneratedValue
        private int id;

        @Basic(fetch = FetchType.LAZY)
        private String name;

        private Country(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}

