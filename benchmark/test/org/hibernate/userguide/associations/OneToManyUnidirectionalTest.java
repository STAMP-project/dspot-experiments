/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.associations;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::associations-one-to-many-unidirectional-example[]
public class OneToManyUnidirectionalTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::associations-one-to-many-unidirectional-lifecycle-example[]
            org.hibernate.userguide.associations.Person person = new org.hibernate.userguide.associations.Person();
            org.hibernate.userguide.associations.Phone phone1 = new org.hibernate.userguide.associations.Phone("123-456-7890");
            org.hibernate.userguide.associations.Phone phone2 = new org.hibernate.userguide.associations.Phone("321-654-0987");
            person.getPhones().add(phone1);
            person.getPhones().add(phone2);
            entityManager.persist(person);
            entityManager.flush();
            person.getPhones().remove(phone1);
            // end::associations-one-to-many-unidirectional-lifecycle-example[]
        });
    }

    // tag::associations-one-to-many-unidirectional-example[]
    // tag::associations-one-to-many-unidirectional-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
        private List<OneToManyUnidirectionalTest.Phone> phones = new ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::associations-one-to-many-unidirectional-example[]
        public Person() {
        }

        public List<OneToManyUnidirectionalTest.Phone> getPhones() {
            return phones;
        }
    }

    // tag::associations-one-to-many-unidirectional-example[]
    @Entity(name = "Phone")
    public static class Phone {
        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "`number`")
        private String number;

        // Getters and setters are omitted for brevity
        // end::associations-one-to-many-unidirectional-example[]
        public Phone() {
        }

        public Phone(String number) {
            this.number = number;
        }

        public Long getId() {
            return id;
        }

        public String getNumber() {
            return number;
        }
    }
}

