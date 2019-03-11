/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.associations;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::associations-one-to-one-unidirectional-example[]
public class OneToOneUnidirectionalTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.associations.Phone phone = new org.hibernate.userguide.associations.Phone("123-456-7890");
            org.hibernate.userguide.associations.PhoneDetails details = new org.hibernate.userguide.associations.PhoneDetails("T-Mobile", "GSM");
            phone.setDetails(details);
            entityManager.persist(phone);
            entityManager.persist(details);
        });
    }

    // tag::associations-one-to-one-unidirectional-example[]
    // tag::associations-one-to-one-unidirectional-example[]
    @Entity(name = "Phone")
    public static class Phone {
        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "`number`")
        private String number;

        @OneToOne
        @JoinColumn(name = "details_id")
        private OneToOneUnidirectionalTest.PhoneDetails details;

        // Getters and setters are omitted for brevity
        // end::associations-one-to-one-unidirectional-example[]
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

        public OneToOneUnidirectionalTest.PhoneDetails getDetails() {
            return details;
        }

        public void setDetails(OneToOneUnidirectionalTest.PhoneDetails details) {
            this.details = details;
        }
    }

    // tag::associations-one-to-one-unidirectional-example[]
    @Entity(name = "PhoneDetails")
    public static class PhoneDetails {
        @Id
        @GeneratedValue
        private Long id;

        private String provider;

        private String technology;

        // Getters and setters are omitted for brevity
        // end::associations-one-to-one-unidirectional-example[]
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
    }
}

