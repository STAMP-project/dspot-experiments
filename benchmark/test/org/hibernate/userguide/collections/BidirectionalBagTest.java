/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.collections;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
// end::collections-bidirectional-bag-example[]
public class BidirectionalBagTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = new org.hibernate.userguide.collections.Person(1L);
            entityManager.persist(person);
            // tag::collections-bidirectional-bag-lifecycle-example[]
            person.addPhone(new org.hibernate.userguide.collections.Phone(1L, "landline", "028-234-9876"));
            person.addPhone(new org.hibernate.userguide.collections.Phone(2L, "mobile", "072-122-9876"));
            entityManager.flush();
            person.removePhone(person.getPhones().get(0));
            // end::collections-bidirectional-bag-lifecycle-example[]
        });
    }

    // tag::collections-bidirectional-bag-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
        private List<BidirectionalBagTest.Phone> phones = new ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::collections-bidirectional-bag-example[]
        public Person() {
        }

        public Person(Long id) {
            this.id = id;
        }

        public List<BidirectionalBagTest.Phone> getPhones() {
            return phones;
        }

        // tag::collections-bidirectional-bag-example[]
        public void addPhone(BidirectionalBagTest.Phone phone) {
            phones.add(phone);
            phone.setPerson(this);
        }

        public void removePhone(BidirectionalBagTest.Phone phone) {
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
        private BidirectionalBagTest.Person person;

        // Getters and setters are omitted for brevity
        // end::collections-bidirectional-bag-example[]
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

        public BidirectionalBagTest.Person getPerson() {
            return person;
        }

        public void setPerson(BidirectionalBagTest.Person person) {
            this.person = person;
        }

        // tag::collections-bidirectional-bag-example[]
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            BidirectionalBagTest.Phone phone = ((BidirectionalBagTest.Phone) (o));
            return Objects.equals(number, phone.number);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number);
        }
    }
}

