/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.envers;


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::envers-tracking-properties-changes-mapping-example[]
public class ModifiedFlagsAuditTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.envers.Customer customer = new org.hibernate.userguide.envers.Customer();
            customer.setId(1L);
            customer.setFirstName("John");
            customer.setLastName("Doe");
            entityManager.persist(customer);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::envers-tracking-properties-changes-example[]
            org.hibernate.userguide.envers.Customer customer = entityManager.find(.class, 1L);
            customer.setLastName("Doe Jr.");
            // end::envers-tracking-properties-changes-example[]
        });
    }

    // tag::envers-tracking-properties-changes-mapping-example[]
    // tag::envers-tracking-properties-changes-mapping-example[]
    @Audited(withModifiedFlag = true)
    @Entity(name = "Customer")
    public static class Customer {
        @Id
        private Long id;

        private String firstName;

        private String lastName;

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "created_on")
        @CreationTimestamp
        private Date createdOn;

        // Getters and setters are omitted for brevity
        // end::envers-tracking-properties-changes-mapping-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Date getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(Date createdOn) {
            this.createdOn = createdOn;
        }
    }
}

