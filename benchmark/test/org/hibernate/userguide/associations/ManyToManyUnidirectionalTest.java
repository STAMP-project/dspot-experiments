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
// end::associations-many-to-many-unidirectional-example[]
public class ManyToManyUnidirectionalTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::associations-many-to-many-unidirectional-lifecycle-example[]
            org.hibernate.userguide.associations.Person person1 = new org.hibernate.userguide.associations.Person();
            org.hibernate.userguide.associations.Person person2 = new org.hibernate.userguide.associations.Person();
            org.hibernate.userguide.associations.Address address1 = new org.hibernate.userguide.associations.Address("12th Avenue", "12A");
            org.hibernate.userguide.associations.Address address2 = new org.hibernate.userguide.associations.Address("18th Avenue", "18B");
            person1.getAddresses().add(address1);
            person1.getAddresses().add(address2);
            person2.getAddresses().add(address1);
            entityManager.persist(person1);
            entityManager.persist(person2);
            entityManager.flush();
            person1.getAddresses().remove(address1);
            // end::associations-many-to-many-unidirectional-lifecycle-example[]
        });
    }

    @Test
    public void testRemove() {
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
            log.info("Remove");
            // tag::associations-many-to-many-unidirectional-remove-example[]
            org.hibernate.userguide.associations.Person person1 = entityManager.find(.class, personId);
            entityManager.remove(person1);
            // end::associations-many-to-many-unidirectional-remove-example[]
        });
    }

    // tag::associations-many-to-many-unidirectional-example[]
    // tag::associations-many-to-many-unidirectional-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
        private List<ManyToManyUnidirectionalTest.Address> addresses = new ArrayList<>();

        // Getters and setters are omitted for brevity
        // end::associations-many-to-many-unidirectional-example[]
        public Person() {
        }

        public List<ManyToManyUnidirectionalTest.Address> getAddresses() {
            return addresses;
        }
    }

    // tag::associations-many-to-many-unidirectional-example[]
    @Entity(name = "Address")
    public static class Address {
        @Id
        @GeneratedValue
        private Long id;

        private String street;

        @Column(name = "`number`")
        private String number;

        // Getters and setters are omitted for brevity
        // end::associations-many-to-many-unidirectional-example[]
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

