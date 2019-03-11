/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.collections;


import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.CollectionType;
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::collections-custom-collection-mapping-example[]
public class QueueTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = new org.hibernate.userguide.collections.Person(1L);
            person.getPhones().add(new org.hibernate.userguide.collections.Phone(1L, "landline", "028-234-9876"));
            person.getPhones().add(new org.hibernate.userguide.collections.Phone(2L, "mobile", "072-122-9876"));
            entityManager.persist(person);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::collections-custom-collection-example[]
            org.hibernate.userguide.collections.Person person = entityManager.find(.class, 1L);
            Queue<org.hibernate.userguide.collections.Phone> phones = person.getPhones();
            org.hibernate.userguide.collections.Phone head = phones.peek();
            assertSame(head, phones.poll());
            assertEquals(1, phones.size());
            // end::collections-custom-collection-example[]
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = entityManager.find(.class, 1L);
            person.getPhones().clear();
        });
    }

    // tag::collections-custom-collection-mapping-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        @OneToMany(cascade = CascadeType.ALL)
        @CollectionType(type = "org.hibernate.userguide.collections.type.QueueType")
        private Collection<QueueTest.Phone> phones = new LinkedList<>();

        // Constructors are omitted for brevity
        // end::collections-custom-collection-mapping-example[]
        public Person() {
        }

        public Person(Long id) {
            this.id = id;
        }

        // tag::collections-custom-collection-mapping-example[]
        public java.util.Queue<QueueTest.Phone> getPhones() {
            return ((java.util.Queue<QueueTest.Phone>) (phones));
        }
    }

    @Entity(name = "Phone")
    public static class Phone implements Comparable<QueueTest.Phone> {
        @Id
        private Long id;

        private String type;

        @NaturalId
        @Column(name = "`number`")
        private String number;

        // Getters and setters are omitted for brevity
        // end::collections-custom-collection-mapping-example[]
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

        // tag::collections-custom-collection-mapping-example[]
        @Override
        public int compareTo(QueueTest.Phone o) {
            return number.compareTo(o.getNumber());
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            QueueTest.Phone phone = ((QueueTest.Phone) (o));
            return Objects.equals(number, phone.number);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number);
        }
    }
}

