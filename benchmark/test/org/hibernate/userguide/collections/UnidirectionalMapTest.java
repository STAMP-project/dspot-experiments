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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKey;
import javax.persistence.MapKeyTemporal;
import javax.persistence.OneToMany;
import javax.persistence.TemporalType;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::collections-map-unidirectional-example[]
public class UnidirectionalMapTest extends BaseEntityManagerFunctionalTestCase {
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
            Map<Date, org.hibernate.userguide.collections.Phone> phones = person.getPhoneRegister();
            Assert.assertEquals(2, phones.size());
        });
    }

    // tag::collections-map-unidirectional-example[]
    public enum PhoneType {

        LAND_LINE,
        MOBILE;}

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
        @JoinTable(name = "phone_register", joinColumns = @JoinColumn(name = "phone_id"), inverseJoinColumns = @JoinColumn(name = "person_id"))
        @MapKey(name = "since")
        @MapKeyTemporal(TemporalType.TIMESTAMP)
        private java.util.Map<Date, UnidirectionalMapTest.Phone> phoneRegister = new HashMap<>();

        // Getters and setters are omitted for brevity
        // end::collections-map-unidirectional-example[]
        public Person() {
        }

        public Person(Long id) {
            this.id = id;
        }

        public java.util.Map<Date, UnidirectionalMapTest.Phone> getPhoneRegister() {
            return phoneRegister;
        }

        // tag::collections-map-unidirectional-example[]
        public void addPhone(UnidirectionalMapTest.Phone phone) {
            phoneRegister.put(phone.getSince(), phone);
        }
    }

    // tag::collections-map-unidirectional-example[]
    @Entity(name = "Phone")
    public static class Phone {
        @Id
        @GeneratedValue
        private Long id;

        private UnidirectionalMapTest.PhoneType type;

        @Column(name = "`number`")
        private String number;

        private Date since;

        // Getters and setters are omitted for brevity
        // end::collections-map-unidirectional-example[]
        public Phone() {
        }

        public Phone(UnidirectionalMapTest.PhoneType type, String number, Date since) {
            this.type = type;
            this.number = number;
            this.since = since;
        }

        public UnidirectionalMapTest.PhoneType getType() {
            return type;
        }

        public String getNumber() {
            return number;
        }

        public Date getSince() {
            return since;
        }
    }
}

