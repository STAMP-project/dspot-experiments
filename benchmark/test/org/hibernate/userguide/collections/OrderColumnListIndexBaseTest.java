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
import javax.persistence.OrderColumn;
import org.hibernate.annotations.ListIndexBase;
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class OrderColumnListIndexBaseTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::collections-customizing-ordered-list-ordinal-persist-example[]
            org.hibernate.userguide.collections.Person person = new org.hibernate.userguide.collections.Person(1L);
            entityManager.persist(person);
            person.addPhone(new org.hibernate.userguide.collections.Phone(1L, "landline", "028-234-9876"));
            person.addPhone(new org.hibernate.userguide.collections.Phone(2L, "mobile", "072-122-9876"));
            // end::collections-customizing-ordered-list-ordinal-persist-example[]
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        // tag::collections-customizing-ordered-list-ordinal-mapping-example[]
        @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
        @OrderColumn(name = "order_id")
        @ListIndexBase(100)
        private List<OrderColumnListIndexBaseTest.Phone> phones = new ArrayList<>();

        // end::collections-customizing-ordered-list-ordinal-mapping-example[]
        public Person() {
        }

        public Person(Long id) {
            this.id = id;
        }

        public List<OrderColumnListIndexBaseTest.Phone> getPhones() {
            return phones;
        }

        public void addPhone(OrderColumnListIndexBaseTest.Phone phone) {
            phones.add(phone);
            phone.setPerson(this);
        }

        public void removePhone(OrderColumnListIndexBaseTest.Phone phone) {
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
        private OrderColumnListIndexBaseTest.Person person;

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

        public OrderColumnListIndexBaseTest.Person getPerson() {
            return person;
        }

        public void setPerson(OrderColumnListIndexBaseTest.Person person) {
            this.person = person;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            OrderColumnListIndexBaseTest.Phone phone = ((OrderColumnListIndexBaseTest.Phone) (o));
            return Objects.equals(number, phone.number);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number);
        }
    }
}

