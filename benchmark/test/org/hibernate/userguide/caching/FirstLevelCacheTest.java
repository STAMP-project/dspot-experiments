/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.caching;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class FirstLevelCacheTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testCache() {
        FirstLevelCacheTest.Person aPerson = doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(new org.hibernate.userguide.caching.Person());
            entityManager.persist(new org.hibernate.userguide.caching.Person());
            org.hibernate.userguide.caching.Person person = new org.hibernate.userguide.caching.Person();
            entityManager.persist(person);
            return person;
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Object> dtos = new ArrayList<>();
            // tag::caching-management-jpa-detach-example[]
            for (org.hibernate.userguide.caching.Person person : entityManager.createQuery("select p from Person p", .class).getResultList()) {
                dtos.add(toDTO(person));
                entityManager.detach(person);
            }
            // end::caching-management-jpa-detach-example[]
            // tag::caching-management-clear-example[]
            entityManager.clear();
            // end::caching-management-clear-example[]
            org.hibernate.userguide.caching.Person person = aPerson;
            // tag::caching-management-contains-example[]
            entityManager.contains(person);
            // end::caching-management-contains-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Object> dtos = new ArrayList<>();
            // tag::caching-management-native-evict-example[]
            Session session = entityManager.unwrap(.class);
            for (org.hibernate.userguide.caching.Person person : ((List<org.hibernate.userguide.caching.Person>) (session.createQuery("select p from Person p").list()))) {
                dtos.add(toDTO(person));
                session.evict(person);
            }
            // end::caching-management-native-evict-example[]
            // tag::caching-management-clear-example[]
            session.clear();
            // end::caching-management-clear-example[]
            org.hibernate.userguide.caching.Person person = aPerson;
            // tag::caching-management-contains-example[]
            session.contains(person);
            // end::caching-management-contains-example[]
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private String name;

        public Person() {
        }

        public Person(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

