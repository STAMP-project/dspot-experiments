/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.flush;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class FlushOrderTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testOrder() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.createNativeQuery("delete from Person").executeUpdate();
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.flush.Person person = new org.hibernate.userguide.flush.Person("John Doe");
            person.id = 1L;
            entityManager.persist(person);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            log.info("testFlushSQL");
            // tag::flushing-order-example[]
            org.hibernate.userguide.flush.Person person = entityManager.find(.class, 1L);
            entityManager.remove(person);
            org.hibernate.userguide.flush.Person newPerson = new org.hibernate.userguide.flush.Person();
            newPerson.setId(2L);
            newPerson.setName("John Doe");
            entityManager.persist(newPerson);
            // end::flushing-order-example[]
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
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

