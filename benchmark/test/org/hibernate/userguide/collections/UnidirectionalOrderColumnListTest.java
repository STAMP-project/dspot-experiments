/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.collections;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class UnidirectionalOrderColumnListTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = new org.hibernate.userguide.collections.Person(1L);
            person.getPhones().add(new org.hibernate.userguide.collections.Phone(1L, "landline", "028-234-9876"));
            person.getPhones().add(new org.hibernate.userguide.collections.Phone(2L, "mobile", "072-122-9876"));
            entityManager.persist(person);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = entityManager.find(.class, 1L);
            person.getPhones().remove(0);
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        // tag::collections-unidirectional-ordered-list-order-column-example[]
        @OneToMany(cascade = CascadeType.ALL)
        @OrderColumn(name = "order_id")
        private List<UnidirectionalOrderColumnListTest.Phone> phones = new ArrayList<>();

        // end::collections-unidirectional-ordered-list-order-column-example[]
        public Person() {
        }

        public Person(Long id) {
            this.id = id;
        }

        public List<UnidirectionalOrderColumnListTest.Phone> getPhones() {
            return phones;
        }
    }

    @Entity(name = "Phone")
    public static class Phone {
        @Id
        private Long id;

        private String type;

        @Column(name = "`number`")
        private String number;

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
    }
}

