/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.associations;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::associations-one-to-one-bidirectional-example[]
public class OneToOneBidirectionalTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.associations.Phone phone = new org.hibernate.userguide.associations.Phone("123-456-7890");
            org.hibernate.userguide.associations.PhoneDetails details = new org.hibernate.userguide.associations.PhoneDetails("T-Mobile", "GSM");
            phone.addDetails(details);
            entityManager.persist(phone);
        });
    }

    @Test
    public void testConstraint() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::associations-one-to-one-bidirectional-lifecycle-example[]
            org.hibernate.userguide.associations.Phone phone = new org.hibernate.userguide.associations.Phone("123-456-7890");
            org.hibernate.userguide.associations.PhoneDetails details = new org.hibernate.userguide.associations.PhoneDetails("T-Mobile", "GSM");
            phone.addDetails(details);
            entityManager.persist(phone);
            // end::associations-one-to-one-bidirectional-lifecycle-example[]
        });
        try {
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                org.hibernate.userguide.associations.Phone phone = entityManager.find(.class, 1L);
                // tag::associations-one-to-one-bidirectional-constraint-example[]
                org.hibernate.userguide.associations.PhoneDetails otherDetails = new org.hibernate.userguide.associations.PhoneDetails("T-Mobile", "CDMA");
                otherDetails.setPhone(phone);
                entityManager.persist(otherDetails);
                entityManager.flush();
                entityManager.clear();
                // throws javax.persistence.PersistenceException: org.hibernate.HibernateException: More than one row with the given identifier was found: 1
                phone = entityManager.find(.class, phone.getId());
                // end::associations-one-to-one-bidirectional-constraint-example[]
                phone.getDetails().getProvider();
            });
            Assert.fail("Expected: HHH000327: Error performing load command : org.hibernate.HibernateException: More than one row with the given identifier was found: 1");
        } catch (Exception expected) {
            log.error("Expected", expected);
        }
    }

    // tag::associations-one-to-one-bidirectional-example[]
    @Entity(name = "Phone")
    public static class Phone {
        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "`number`")
        private String number;

        @OneToOne(mappedBy = "phone", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        private OneToOneBidirectionalTest.PhoneDetails details;

        // Getters and setters are omitted for brevity
        // end::associations-one-to-one-bidirectional-example[]
        public Phone() {
        }

        public Phone(String number) {
            this.number = number;
        }

        public Long getId() {
            return id;
        }

        public String getNumber() {
            return number;
        }

        public OneToOneBidirectionalTest.PhoneDetails getDetails() {
            return details;
        }

        // tag::associations-one-to-one-bidirectional-example[]
        public void addDetails(OneToOneBidirectionalTest.PhoneDetails details) {
            details.setPhone(this);
            this.details = details;
        }

        public void removeDetails() {
            if ((details) != null) {
                details.setPhone(null);
                this.details = null;
            }
        }
    }

    // tag::associations-one-to-one-bidirectional-example[]
    @Entity(name = "PhoneDetails")
    public static class PhoneDetails {
        @Id
        @GeneratedValue
        private Long id;

        private String provider;

        private String technology;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "phone_id")
        private OneToOneBidirectionalTest.Phone phone;

        // Getters and setters are omitted for brevity
        // end::associations-one-to-one-bidirectional-example[]
        public PhoneDetails() {
        }

        public PhoneDetails(String provider, String technology) {
            this.provider = provider;
            this.technology = technology;
        }

        public String getProvider() {
            return provider;
        }

        public String getTechnology() {
            return technology;
        }

        public void setTechnology(String technology) {
            this.technology = technology;
        }

        public OneToOneBidirectionalTest.Phone getPhone() {
            return phone;
        }

        public void setPhone(OneToOneBidirectionalTest.Phone phone) {
            this.phone = phone;
        }
    }
}

