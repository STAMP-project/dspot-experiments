/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.associations;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
// end::associations-many-to-many-bidirectional-with-link-entity-example[]
public class ManyToManyBidirectionalWithLinkEntityTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::associations-many-to-many-bidirectional-with-link-entity-lifecycle-example[]
            org.hibernate.userguide.associations.Person person1 = new org.hibernate.userguide.associations.Person("ABC-123");
            org.hibernate.userguide.associations.Person person2 = new org.hibernate.userguide.associations.Person("DEF-456");
            org.hibernate.userguide.associations.Address address1 = new org.hibernate.userguide.associations.Address("12th Avenue", "12A", "4005A");
            org.hibernate.userguide.associations.Address address2 = new org.hibernate.userguide.associations.Address("18th Avenue", "18B", "4007B");
            entityManager.persist(person1);
            entityManager.persist(person2);
            entityManager.persist(address1);
            entityManager.persist(address2);
            person1.addAddress(address1);
            person1.addAddress(address2);
            person2.addAddress(address1);
            entityManager.flush();
            log.info("Removing address");
            person1.removeAddress(address1);
            // end::associations-many-to-many-bidirectional-with-link-entity-lifecycle-example[]
        });
    }

    // tag::associations-many-to-many-bidirectional-with-link-entity-example[]
    @Entity(name = "Person")
    public static class Person implements Serializable {
        @Id
        @GeneratedValue
        private Long id;

        @NaturalId
        private String registrationNumber;

        @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<ManyToManyBidirectionalWithLinkEntityTest.PersonAddress> addresses = new ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::associations-many-to-many-bidirectional-with-link-entity-example[]
        public Person() {
        }

        public Person(String registrationNumber) {
            this.registrationNumber = registrationNumber;
        }

        public Long getId() {
            return id;
        }

        public List<ManyToManyBidirectionalWithLinkEntityTest.PersonAddress> getAddresses() {
            return addresses;
        }

        // tag::associations-many-to-many-bidirectional-with-link-entity-example[]
        public void addAddress(ManyToManyBidirectionalWithLinkEntityTest.Address address) {
            ManyToManyBidirectionalWithLinkEntityTest.PersonAddress personAddress = new ManyToManyBidirectionalWithLinkEntityTest.PersonAddress(this, address);
            addresses.add(personAddress);
            address.getOwners().add(personAddress);
        }

        public void removeAddress(ManyToManyBidirectionalWithLinkEntityTest.Address address) {
            ManyToManyBidirectionalWithLinkEntityTest.PersonAddress personAddress = new ManyToManyBidirectionalWithLinkEntityTest.PersonAddress(this, address);
            address.getOwners().remove(personAddress);
            addresses.remove(personAddress);
            personAddress.setPerson(null);
            personAddress.setAddress(null);
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ManyToManyBidirectionalWithLinkEntityTest.Person person = ((ManyToManyBidirectionalWithLinkEntityTest.Person) (o));
            return Objects.equals(registrationNumber, person.registrationNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(registrationNumber);
        }
    }

    @Entity(name = "PersonAddress")
    public static class PersonAddress implements Serializable {
        @Id
        @ManyToOne
        private ManyToManyBidirectionalWithLinkEntityTest.Person person;

        @Id
        @ManyToOne
        private ManyToManyBidirectionalWithLinkEntityTest.Address address;

        // Getters and setters are omitted for brevity
        // end::associations-many-to-many-bidirectional-with-link-entity-example[]
        public PersonAddress() {
        }

        public PersonAddress(ManyToManyBidirectionalWithLinkEntityTest.Person person, ManyToManyBidirectionalWithLinkEntityTest.Address address) {
            this.person = person;
            this.address = address;
        }

        public ManyToManyBidirectionalWithLinkEntityTest.Person getPerson() {
            return person;
        }

        public void setPerson(ManyToManyBidirectionalWithLinkEntityTest.Person person) {
            this.person = person;
        }

        public ManyToManyBidirectionalWithLinkEntityTest.Address getAddress() {
            return address;
        }

        public void setAddress(ManyToManyBidirectionalWithLinkEntityTest.Address address) {
            this.address = address;
        }

        // tag::associations-many-to-many-bidirectional-with-link-entity-example[]
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ManyToManyBidirectionalWithLinkEntityTest.PersonAddress that = ((ManyToManyBidirectionalWithLinkEntityTest.PersonAddress) (o));
            return (Objects.equals(person, that.person)) && (Objects.equals(address, that.address));
        }

        @Override
        public int hashCode() {
            return Objects.hash(person, address);
        }
    }

    @Entity(name = "Address")
    public static class Address implements Serializable {
        @Id
        @GeneratedValue
        private Long id;

        private String street;

        @Column(name = "`number`")
        private String number;

        private String postalCode;

        @OneToMany(mappedBy = "address", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<ManyToManyBidirectionalWithLinkEntityTest.PersonAddress> owners = new ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::associations-many-to-many-bidirectional-with-link-entity-example[]
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

        public List<ManyToManyBidirectionalWithLinkEntityTest.PersonAddress> getOwners() {
            return owners;
        }

        // tag::associations-many-to-many-bidirectional-with-link-entity-example[]
        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ManyToManyBidirectionalWithLinkEntityTest.Address address = ((ManyToManyBidirectionalWithLinkEntityTest.Address) (o));
            return ((Objects.equals(street, address.street)) && (Objects.equals(number, address.number))) && (Objects.equals(postalCode, address.postalCode));
        }

        @Override
        public int hashCode() {
            return Objects.hash(street, number, postalCode);
        }
    }
}

