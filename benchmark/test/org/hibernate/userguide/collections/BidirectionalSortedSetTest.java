/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.collections;


import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SortNatural;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class BidirectionalSortedSetTest extends BaseEntityManagerFunctionalTestCase {
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
            phones.stream().forEach(( phone) -> log.infov("Phone number %s", phone.getNumber()));
            person.removePhone(phones.iterator().next());
            Assert.assertEquals(1, phones.size());
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = entityManager.find(.class, 1L);
            Set<org.hibernate.userguide.collections.Phone> phones = person.getPhones();
            Assert.assertEquals(1, phones.size());
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        // tag::collections-bidirectional-sorted-set-example[]
        @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
        @SortNatural
        private SortedSet<BidirectionalSortedSetTest.Phone> phones = new TreeSet<>();

        // end::collections-bidirectional-sorted-set-example[]
        public Person() {
        }

        public Person(Long id) {
            this.id = id;
        }

        public java.util.Set<BidirectionalSortedSetTest.Phone> getPhones() {
            return phones;
        }

        public void addPhone(BidirectionalSortedSetTest.Phone phone) {
            phones.add(phone);
            phone.setPerson(this);
        }

        public void removePhone(BidirectionalSortedSetTest.Phone phone) {
            phones.remove(phone);
            phone.setPerson(null);
        }
    }

    @Entity(name = "Phone")
    public static class Phone implements Comparable<BidirectionalSortedSetTest.Phone> {
        @Id
        private Long id;

        private String type;

        @Column(name = "`number`", unique = true)
        @NaturalId
        private String number;

        @ManyToOne
        private BidirectionalSortedSetTest.Person person;

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

        public BidirectionalSortedSetTest.Person getPerson() {
            return person;
        }

        public void setPerson(BidirectionalSortedSetTest.Person person) {
            this.person = person;
        }

        @Override
        public int compareTo(BidirectionalSortedSetTest.Phone o) {
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
            BidirectionalSortedSetTest.Phone phone = ((BidirectionalSortedSetTest.Phone) (o));
            return Objects.equals(number, phone.number);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number);
        }
    }
}

