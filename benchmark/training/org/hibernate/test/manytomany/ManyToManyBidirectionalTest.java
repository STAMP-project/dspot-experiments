/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.manytomany;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.NaturalId;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class ManyToManyBidirectionalTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testRemoveOwnerSide() {
        ManyToManyBidirectionalTest.Person _person1 = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.manytomany.Person person1 = new org.hibernate.test.manytomany.Person("ABC-123");
            org.hibernate.test.manytomany.Person person2 = new org.hibernate.test.manytomany.Person("DEF-456");
            org.hibernate.test.manytomany.Address address1 = new org.hibernate.test.manytomany.Address("12th Avenue", "12A", "4005A");
            org.hibernate.test.manytomany.Address address2 = new org.hibernate.test.manytomany.Address("18th Avenue", "18B", "4007B");
            person1.addAddress(address1);
            person1.addAddress(address2);
            person2.addAddress(address1);
            entityManager.persist(person1);
            entityManager.persist(person2);
            return person1;
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.manytomany.Person person1 = entityManager.find(.class, _person1.id);
            entityManager.remove(person1);
        });
    }

    @Test
    @FailureExpected(jiraKey = "HHH-12239")
    public void testRemoveMappedBySide() {
        ManyToManyBidirectionalTest.Address _address1 = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.manytomany.Person person1 = new org.hibernate.test.manytomany.Person("ABC-123");
            org.hibernate.test.manytomany.Person person2 = new org.hibernate.test.manytomany.Person("DEF-456");
            org.hibernate.test.manytomany.Address address1 = new org.hibernate.test.manytomany.Address("12th Avenue", "12A", "4005A");
            org.hibernate.test.manytomany.Address address2 = new org.hibernate.test.manytomany.Address("18th Avenue", "18B", "4007B");
            person1.addAddress(address1);
            person1.addAddress(address2);
            person2.addAddress(address1);
            entityManager.persist(person1);
            entityManager.persist(person2);
            return address1;
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.manytomany.Address address1 = entityManager.find(.class, _address1.id);
            entityManager.remove(address1);
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        @NaturalId
        private String registrationNumber;

        @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
        private List<ManyToManyBidirectionalTest.Address> addresses = new ArrayList<>();

        public Person() {
        }

        public Person(String registrationNumber) {
            this.registrationNumber = registrationNumber;
        }

        public void addAddress(ManyToManyBidirectionalTest.Address address) {
            addresses.add(address);
            address.owners.add(this);
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

        public Address() {
        }

        public Address(String street, String number, String postalCode) {
            this.street = street;
            this.number = number;
            this.postalCode = postalCode;
        }
    }
}

