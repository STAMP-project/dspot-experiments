/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.collections;


import java.util.HashSet;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::collections-bidirectional-set-example[]
public class BidirectionalSetTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = new org.hibernate.userguide.collections.Person(1L);
            entityManager.persist(person);
            person.addPhone(new org.hibernate.userguide.collections.Phone(1L, "landline", "028-234-9876"));
            person.addPhone(new org.hibernate.userguide.collections.Phone(2L, "mobile", "072-122-9876"));
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = entityManager.find(.class, 1L);
            Set<org.hibernate.userguide.collections.Phone> phones = person.getPhones();
            Assert.assertEquals(2, phones.size());
            person.removePhone(phones.iterator().next());
            Assert.assertEquals(1, phones.size());
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = entityManager.find(.class, 1L);
            Set<org.hibernate.userguide.collections.Phone> phones = person.getPhones();
            Assert.assertEquals(1, phones.size());
        });
    }

    // tag::collections-bidirectional-set-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
        private java.util.Set<BidirectionalSetTest.Phone> phones = new HashSet<>();

        // Getters and setters are omitted for brevity
        // end::collections-bidirectional-set-example[]
        public Person() {
        }

        public Person(Long id) {
            this.id = id;
        }

        public java.util.Set<BidirectionalSetTest.Phone> getPhones() {
            return phones;
        }

        // tag::collections-bidirectional-set-example[]
        public void addPhone(BidirectionalSetTest.Phone phone) {
            phones.add(phone);
            phone.setPerson(this);
        }

        public void removePhone(BidirectionalSetTest.Phone phone) {
            phones.remove(phone);
            phone.setPerson(null);
        }
    }

    @Entity(name = "Phone")
    public static class Phone {
        @Id
        private Long id;

        private String type;

        @Column(name = "`number`", unique = true)
        @NaturalId
        private String number;

        @ManyToOne
        private BidirectionalSetTest.Person person;

        // Getters and setters are omitted for brevity
        // end::collections-bidirectional-set-example[]
        public Phone() {
        }

        public Phone(Long id, String type, String number) {
            this.id = id;
            this.type = type;
            this.number = number;
        }

        public Long getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getNumber() {
            return number;
        }

        public BidirectionalSetTest.Person getPerson() {
            return person;
        }

        public void setPerson(BidirectionalSetTest.Person person) {
            this.person = person;
        }

        // tag::collections-bidirectional-set-example[]
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            BidirectionalSetTest.Phone phone = ((BidirectionalSetTest.Phone) (o));
            return Objects.equals(number, phone.number);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number);
        }
    }
}

