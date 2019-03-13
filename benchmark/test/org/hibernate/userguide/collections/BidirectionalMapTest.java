/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.collections;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::collections-map-bidirectional-example[]
public class BidirectionalMapTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = new org.hibernate.userguide.collections.Person(1L);
            LocalDateTime now = LocalDateTime.now();
            person.addPhone(new org.hibernate.userguide.collections.Phone(PhoneType.LAND_LINE, "028-234-9876", Timestamp.valueOf(now)));
            person.addPhone(new org.hibernate.userguide.collections.Phone(PhoneType.MOBILE, "072-122-9876", Timestamp.valueOf(now.minusDays(1))));
            entityManager.persist(person);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.collections.Person person = entityManager.find(.class, 1L);
            Map<org.hibernate.userguide.collections.PhoneType, org.hibernate.userguide.collections.Phone> phones = person.getPhoneRegister();
            Assert.assertEquals(2, phones.size());
        });
    }

    public enum PhoneType {

        LAND_LINE,
        MOBILE;}

    // tag::collections-map-bidirectional-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
        @MapKey(name = "type")
        @MapKeyEnumerated
        private java.util.Map<BidirectionalMapTest.PhoneType, BidirectionalMapTest.Phone> phoneRegister = new HashMap<>();

        // Getters and setters are omitted for brevity
        // end::collections-map-bidirectional-example[]
        public Person() {
        }

        public Person(Long id) {
            this.id = id;
        }

        public java.util.Map<BidirectionalMapTest.PhoneType, BidirectionalMapTest.Phone> getPhoneRegister() {
            return phoneRegister;
        }

        // tag::collections-map-bidirectional-example[]
        public void addPhone(BidirectionalMapTest.Phone phone) {
            phone.setPerson(this);
            phoneRegister.put(phone.getType(), phone);
        }
    }

    // tag::collections-map-bidirectional-example[]
    @Entity(name = "Phone")
    public static class Phone {
        @Id
        @GeneratedValue
        private Long id;

        private BidirectionalMapTest.PhoneType type;

        @Column(name = "`number`")
        private String number;

        private Date since;

        @ManyToOne
        private BidirectionalMapTest.Person person;

        // Getters and setters are omitted for brevity
        // end::collections-map-bidirectional-example[]
        public Phone() {
        }

        public Phone(BidirectionalMapTest.PhoneType type, String number, Date since) {
            this.type = type;
            this.number = number;
            this.since = since;
        }

        public BidirectionalMapTest.PhoneType getType() {
            return type;
        }

        public String getNumber() {
            return number;
        }

        public Date getSince() {
            return since;
        }

        public BidirectionalMapTest.Person getPerson() {
            return person;
        }

        public void setPerson(BidirectionalMapTest.Person person) {
            this.person = person;
        }
    }
}

