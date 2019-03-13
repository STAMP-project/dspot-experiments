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
import javax.persistence.ManyToMany;
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::associations-many-to-many-bidirectional-example[]
public class ManyToManyBidirectionalTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::associations-many-to-many-bidirectional-lifecycle-example[]
            org.hibernate.userguide.associations.Person person1 = new org.hibernate.userguide.associations.Person("ABC-123");
            org.hibernate.userguide.associations.Person person2 = new org.hibernate.userguide.associations.Person("DEF-456");
            org.hibernate.userguide.associations.Address address1 = new org.hibernate.userguide.associations.Address("12th Avenue", "12A", "4005A");
            org.hibernate.userguide.associations.Address address2 = new org.hibernate.userguide.associations.Address("18th Avenue", "18B", "4007B");
            person1.addAddress(address1);
            person1.addAddress(address2);
            person2.addAddress(address1);
            entityManager.persist(person1);
            entityManager.persist(person2);
            entityManager.flush();
            person1.removeAddress(address1);
            // end::associations-many-to-many-bidirectional-lifecycle-example[]
        });
    }

    // tag::associations-many-to-many-bidirectional-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        @NaturalId
        private String registrationNumber;

        @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
        private List<ManyToManyBidirectionalTest.Address> addresses = new ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::associations-many-to-many-bidirectional-example[]
        public Person() {
        }

        public Person(String registrationNumber) {
            this.registrationNumber = registrationNumber;
        }

        public List<ManyToManyBidirectionalTest.Address> getAddresses() {
            return addresses;
        }

        // tag::associations-many-to-many-bidirectional-example[]
        public void addAddress(ManyToManyBidirectionalTest.Address address) {
            addresses.add(address);
            address.getOwners().add(this);
        }

        public void removeAddress(ManyToManyBidirectionalTest.Address address) {
            addresses.remove(address);
            address.getOwners().remove(this);
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ManyToManyBidirectionalTest.Person person = ((ManyToManyBidirectionalTest.Person) (o));
            return Objects.equals(registrationNumber, person.registrationNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(registrationNumber);
        }
    }

    @Entity(name = "Address")
    public static class Address {
        @Id
        @GeneratedValue
        private Long id;

        private String street;

        @Column(name = "`number`")
        private String number;

        private String postalCode;

        @ManyToMany(mappedBy = "addresses")
        private List<ManyToManyBidirectionalTest.Person> owners = new ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::associations-many-to-many-bidirectional-example[]
        public Address() {
        }

        public Address(String street, String number, String postalCode) {
            this.street = street;
            this.number = number;
            this.postalCode = postalCode;
        }

        public Long getId() {
            return id;
        }

        public String getStreet() {
            return street;
        }

        public String getNumber() {
            return number;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public List<ManyToManyBidirectionalTest.Person> getOwners() {
            return owners;
        }

        // tag::associations-many-to-many-bidirectional-example[]
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ManyToManyBidirectionalTest.Address address = ((ManyToManyBidirectionalTest.Address) (o));
            return ((Objects.equals(street, address.street)) && (Objects.equals(number, address.number))) && (Objects.equals(postalCode, address.postalCode));
        }

        @Override
        public int hashCode() {
            return Objects.hash(street, number, postalCode);
        }
    }
}

