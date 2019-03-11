/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.collections;


import java.util.Comparator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SortComparator;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::collections-unidirectional-sorted-set-custom-comparator-example[]
public class UnidirectionalComparatorSortedSetTest extends BaseEntityManagerFunctionalTestCase {
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
            Set<org.hibernate.userguide.collections.Phone> phones = person.getPhones();
            Assert.assertEquals(2, phones.size());
            phones.stream().forEach(( phone) -> log.infov("Phone number %s", phone.getNumber()));
            phones.remove(phones.iterator().next());
            Assert.assertEquals(1, phones.size());
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = entityManager.find(.class, 1L);
            Set<org.hibernate.userguide.collections.Phone> phones = person.getPhones();
            Assert.assertEquals(1, phones.size());
        });
    }

    // tag::collections-unidirectional-sorted-set-custom-comparator-example[]
    // tag::collections-unidirectional-sorted-set-custom-comparator-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        @OneToMany(cascade = CascadeType.ALL)
        @SortComparator(UnidirectionalComparatorSortedSetTest.ReverseComparator.class)
        private SortedSet<UnidirectionalComparatorSortedSetTest.Phone> phones = new TreeSet<>();

        // Getters and setters are omitted for brevity
        // end::collections-unidirectional-sorted-set-custom-comparator-example[]
        public Person() {
        }

        public Person(Long id) {
            this.id = id;
        }

        public java.util.Set<UnidirectionalComparatorSortedSetTest.Phone> getPhones() {
            return phones;
        }
    }

    public static class ReverseComparator implements Comparator<UnidirectionalComparatorSortedSetTest.Phone> {
        @Override
        public int compare(UnidirectionalComparatorSortedSetTest.Phone o1, UnidirectionalComparatorSortedSetTest.Phone o2) {
            return o2.compareTo(o1);
        }
    }

    @Entity(name = "Phone")
    public static class Phone implements Comparable<UnidirectionalComparatorSortedSetTest.Phone> {
        @Id
        private Long id;

        private String type;

        @NaturalId
        @Column(name = "`number`")
        private String number;

        // Getters and setters are omitted for brevity
        // end::collections-unidirectional-sorted-set-custom-comparator-example[]
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

        // tag::collections-unidirectional-sorted-set-custom-comparator-example[]
        @Override
        public int compareTo(UnidirectionalComparatorSortedSetTest.Phone o) {
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
            UnidirectionalComparatorSortedSetTest.Phone phone = ((UnidirectionalComparatorSortedSetTest.Phone) (o));
            return Objects.equals(number, phone.number);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number);
        }
    }
}

