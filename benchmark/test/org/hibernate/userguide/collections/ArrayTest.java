/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.collections;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::collections-array-binary-example[]
public class ArrayTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = new org.hibernate.userguide.collections.Person(1L);
            String[] phones = new String[2];
            phones[0] = "028-234-9876";
            phones[1] = "072-122-9876";
            person.setPhones(phones);
            entityManager.persist(person);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = entityManager.find(.class, 1L);
            String[] phones = new String[1];
            phones[0] = "072-122-9876";
            person.setPhones(phones);
        });
    }

    // tag::collections-array-binary-example[]
    // tag::collections-array-binary-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String[] phones;

        // Getters and setters are omitted for brevity
        // end::collections-array-binary-example[]
        public Person() {
        }

        public Person(Long id) {
            this.id = id;
        }

        public String[] getPhones() {
            return phones;
        }

        public void setPhones(String[] phones) {
            this.phones = phones;
        }
    }
}

