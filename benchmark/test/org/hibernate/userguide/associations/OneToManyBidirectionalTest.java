/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.associations;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::associations-one-to-many-bidirectional-example[]
public class OneToManyBidirectionalTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::associations-one-to-many-bidirectional-lifecycle-example[]
            org.hibernate.userguide.associations.Person person = new org.hibernate.userguide.associations.Person();
            org.hibernate.userguide.associations.Phone phone1 = new org.hibernate.userguide.associations.Phone("123-456-7890");
            org.hibernate.userguide.associations.Phone phone2 = new org.hibernate.userguide.associations.Phone("321-654-0987");
            person.addPhone(phone1);
            person.addPhone(phone2);
            entityManager.persist(person);
            entityManager.flush();
            person.removePhone(phone1);
            // end::associations-one-to-many-bidirectional-lifecycle-example[]
        });
    }

    // tag::associations-one-to-many-bidirectional-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<OneToManyBidirectionalTest.Phone> phones = new ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::associations-one-to-many-bidirectional-example[]
        public Person() {
        }

        public Person(Long id) {
            this.id = id;
        }

        public List<OneToManyBidirectionalTest.Phone> getPhones() {
            return phones;
        }

        // tag::associations-one-to-many-bidirectional-example[]
        public void addPhone(OneToManyBidirectionalTest.Phone phone) {
            phones.add(phone);
            phone.setPerson(this);
        }

        public void removePhone(OneToManyBidirectionalTest.Phone phone) {
            phones.remove(phone);
            phone.setPerson(null);
        }
    }

    @Entity(name = "Phone")
    public static class Phone {
        @Id
        @GeneratedValue
        private Long id;

        @NaturalId
        @Column(name = "`number`", unique = true)
        private String number;

        @ManyToOne
        private OneToManyBidirectionalTest.Person person;

        // Getters and setters are omitted for brevity
        // end::associations-one-to-many-bidirectional-example[]
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

        public OneToManyBidirectionalTest.Person getPerson() {
            return person;
        }

        public void setPerson(OneToManyBidirectionalTest.Person person) {
            this.person = person;
        }

        // tag::associations-one-to-many-bidirectional-example[]
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            OneToManyBidirectionalTest.Phone phone = ((OneToManyBidirectionalTest.Phone) (o));
            return Objects.equals(number, phone.number);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number);
        }
    }
}

