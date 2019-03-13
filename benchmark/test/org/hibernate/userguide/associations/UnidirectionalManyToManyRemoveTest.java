/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.associations;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class UnidirectionalManyToManyRemoveTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testRemove() {
        try {
            final Long personId = doInJPA(this::entityManagerFactory, ( entityManager) -> {
                org.hibernate.userguide.associations.Person person1 = new org.hibernate.userguide.associations.Person();
                org.hibernate.userguide.associations.Person person2 = new org.hibernate.userguide.associations.Person();
                org.hibernate.userguide.associations.Address address1 = new org.hibernate.userguide.associations.Address("12th Avenue", "12A");
                org.hibernate.userguide.associations.Address address2 = new org.hibernate.userguide.associations.Address("18th Avenue", "18B");
                person1.getAddresses().add(address1);
                person1.getAddresses().add(address2);
                person2.getAddresses().add(address1);
                entityManager.persist(person1);
                entityManager.persist(person2);
                return person1.id;
            });
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                org.hibernate.userguide.associations.Person person1 = entityManager.find(.class, personId);
                entityManager.remove(person1);
            });
        } catch (Exception expected) {
            log.error("Expected", expected);
        }
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToMany(cascade = { CascadeType.ALL })
        private List<UnidirectionalManyToManyRemoveTest.Address> addresses = new ArrayList<>();

        public Person() {
        }

        public List<UnidirectionalManyToManyRemoveTest.Address> getAddresses() {
            return addresses;
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

        public Address() {
        }

        public Address(String street, String number) {
            this.street = street;
            this.number = number;
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
    }
}

